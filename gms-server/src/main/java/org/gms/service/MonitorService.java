package org.gms.service;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.constants.net.ServerConstants;
import org.gms.exception.BizException;
import org.gms.exception.BizExceptionEnum;
import org.gms.model.dto.MysqlMonitorRtnDTO;
import org.gms.model.dto.ServerMonitorRtnDTO;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台只读监控采集：JVM/OS/游戏在线与 MySQL/Druid。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MonitorService {

    private final DataSource dataSource;

    public ServerMonitorRtnDTO getServerInfo() {
        return ServerMonitorRtnDTO.builder()
                .sampledAt(System.currentTimeMillis())
                .jvm(collectJvm())
                .os(collectOs())
                .game(collectGame())
                .build();
    }

    public MysqlMonitorRtnDTO getMysqlInfo() {
        MysqlMonitorRtnDTO.MysqlMonitorRtnDTOBuilder builder = MysqlMonitorRtnDTO.builder()
                .sampledAt(System.currentTimeMillis())
                .pool(collectPool());

        try (Connection connection = dataSource.getConnection()) {
            String databaseName = connection.getCatalog();
            builder.databaseName(databaseName);
            builder.version(querySingleString(connection, "SELECT VERSION()"));

            Map<String, Long> status = queryStatusMap(connection,
                    "SHOW GLOBAL STATUS WHERE Variable_name IN ("
                            + "'Uptime','Threads_connected','Threads_running','Questions','Slow_queries')");
            Map<String, Long> variables = queryStatusMap(connection,
                    "SHOW GLOBAL VARIABLES WHERE Variable_name IN ('max_connections')");

            builder.uptimeSeconds(status.getOrDefault("Uptime", 0L));
            builder.threadsConnected(status.getOrDefault("Threads_connected", 0L));
            builder.threadsRunning(status.getOrDefault("Threads_running", 0L));
            builder.questions(status.getOrDefault("Questions", 0L));
            builder.slowQueries(status.getOrDefault("Slow_queries", 0L));
            builder.maxConnections(variables.getOrDefault("max_connections", 0L));
            builder.databaseSizeBytes(queryDatabaseSize(connection, databaseName));
        } catch (SQLException e) {
            log.error(I18nUtil.getLogMessage("MonitorService.mysql.error"), e.toString());
            throw new BizException(BizExceptionEnum.INTERNAL_SERVER_ERROR.getResultCode(),
                    I18nUtil.getExceptionMessage("MonitorService.mysql.queryFailed"));
        }

        return builder.build();
    }

    private ServerMonitorRtnDTO.JvmInfo collectJvm() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        List<ServerMonitorRtnDTO.GcInfo> gcList = new ArrayList<>();
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcList.add(ServerMonitorRtnDTO.GcInfo.builder()
                    .name(gcBean.getName())
                    .collectionCount(Math.max(gcBean.getCollectionCount(), 0L))
                    .collectionTimeMs(Math.max(gcBean.getCollectionTime(), 0L))
                    .build());
        }

        return ServerMonitorRtnDTO.JvmInfo.builder()
                .heapUsed(heap.getUsed())
                .heapMax(heap.getMax() > 0 ? heap.getMax() : heap.getCommitted())
                .nonHeapUsed(nonHeap.getUsed())
                .nonHeapMax(nonHeap.getMax() > 0 ? nonHeap.getMax() : nonHeap.getCommitted())
                .threadCount(threadMXBean.getThreadCount())
                .daemonThreadCount(threadMXBean.getDaemonThreadCount())
                .uptimeMs(runtimeMXBean.getUptime())
                .gc(gcList)
                .build();
    }

    private ServerMonitorRtnDTO.OsInfo collectOs() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        ServerMonitorRtnDTO.OsInfo.OsInfoBuilder builder = ServerMonitorRtnDTO.OsInfo.builder()
                .name(osBean.getName())
                .arch(osBean.getArch())
                .availableProcessors(osBean.getAvailableProcessors())
                .systemCpuLoad(-1D)
                .processCpuLoad(-1D)
                .totalPhysicalMemory(0L)
                .freePhysicalMemory(0L);

        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            builder.systemCpuLoad(normalizeCpuLoad(sunOs.getCpuLoad()));
            builder.processCpuLoad(normalizeCpuLoad(sunOs.getProcessCpuLoad()));
            builder.totalPhysicalMemory(Math.max(sunOs.getTotalMemorySize(), 0L));
            builder.freePhysicalMemory(Math.max(sunOs.getFreeMemorySize(), 0L));
        }
        return builder.build();
    }

    private double normalizeCpuLoad(double load) {
        if (load < 0) {
            return -1D;
        }
        return Math.min(load, 1D);
    }

    private ServerMonitorRtnDTO.GameInfo collectGame() {
        Server server = Server.getInstance();
        boolean online = server != null && server.isOnline();
        List<ServerMonitorRtnDTO.WorldOnlineInfo> worlds = new ArrayList<>();

        if (online) {
            List<World> worldList = server.getWorlds();
            if (worldList != null) {
                for (World world : worldList) {
                    List<ServerMonitorRtnDTO.ChannelOnlineInfo> channels = new ArrayList<>();
                    int worldPlayers = 0;
                    List<Channel> channelList = world.getChannels();
                    if (channelList != null) {
                        for (Channel channel : channelList) {
                            int count = channel.getPlayerStorage() == null
                                    ? 0
                                    : channel.getPlayerStorage().getSize();
                            worldPlayers += count;
                            channels.add(ServerMonitorRtnDTO.ChannelOnlineInfo.builder()
                                    .id(channel.getId())
                                    .playerCount(count)
                                    .build());
                        }
                    }
                    worlds.add(ServerMonitorRtnDTO.WorldOnlineInfo.builder()
                            .id(world.getId())
                            .playerCount(worldPlayers)
                            .channels(channels)
                            .build());
                }
            }
        }

        return ServerMonitorRtnDTO.GameInfo.builder()
                .online(online)
                .version(ServerConstants.BEI_DOU_VERSION)
                .worlds(worlds)
                .build();
    }

    private MysqlMonitorRtnDTO.PoolInfo collectPool() {
        if (dataSource instanceof DruidDataSource druid) {
            return MysqlMonitorRtnDTO.PoolInfo.builder()
                    .available(true)
                    .activeCount(druid.getActiveCount())
                    .poolingCount(druid.getPoolingCount())
                    .waitThreadCount(druid.getWaitThreadCount())
                    .maxActive(druid.getMaxActive())
                    .connectCount(druid.getConnectCount())
                    .connectErrorCount(druid.getConnectErrorCount())
                    .createCount(druid.getCreateCount())
                    .destroyCount(druid.getDestroyCount())
                    .build();
        }
        log.warn(I18nUtil.getLogMessage("MonitorService.pool.warn1"),
                dataSource == null ? "null" : dataSource.getClass().getName());
        return MysqlMonitorRtnDTO.PoolInfo.builder()
                .available(false)
                .activeCount(0)
                .poolingCount(0)
                .waitThreadCount(0)
                .maxActive(0)
                .connectCount(0L)
                .connectErrorCount(0L)
                .createCount(0L)
                .destroyCount(0L)
                .build();
    }

    private String querySingleString(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return "";
    }

    private Map<String, Long> queryStatusMap(Connection connection, String sql) throws SQLException {
        Map<String, Long> map = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                map.put(resultSet.getString(1), parseLongSafe(resultSet.getString(2)));
            }
        }
        return map;
    }

    private long queryDatabaseSize(Connection connection, String databaseName) throws SQLException {
        if (databaseName == null || databaseName.isBlank()) {
            return 0L;
        }
        String sql = "SELECT COALESCE(SUM(DATA_LENGTH + INDEX_LENGTH), 0) "
                + "FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        }
        return 0L;
    }

    private long parseLongSafe(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
