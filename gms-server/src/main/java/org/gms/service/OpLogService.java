package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.dao.entity.OpLogDO;
import org.gms.dao.entity.OpLogTypeDO;
import org.gms.dao.mapper.OpLogMapper;
import org.gms.dao.mapper.OpLogTypeMapper;
import org.gms.log.OpLogType;
import org.gms.model.dto.OpLogSearchDTO;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.util.PacketCreator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 统一操作日志服务。
 * <p>脚本只需调用 {@link org.gms.log.OpLogManager#record} 上报一次；本服务负责：
 * 按操作类型绑定解析聊天样式并全服广播，同时将日志异步批量入库（高并发安全）。
 */
@Slf4j
@Service
public class OpLogService {

    private static final int QUEUE_CAPACITY = 100_000;
    private static final int BATCH_SIZE = 500;
    private static final int POLL_TIMEOUT_MS = 2000;

    private final OpLogMapper opLogMapper;
    private final OpLogTypeMapper opLogTypeMapper;

    private final LinkedBlockingQueue<OpLogDO> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final Thread flusher;
    private volatile boolean running = true;
    private volatile Map<Integer, OpLogTypeDO> typeCache = new ConcurrentHashMap<>();

    public OpLogService(OpLogMapper opLogMapper, OpLogTypeMapper opLogTypeMapper) {
        this.opLogMapper = opLogMapper;
        this.opLogTypeMapper = opLogTypeMapper;
        this.flusher = new Thread(this::flushLoop, "op-log-flusher");
        this.flusher.setDaemon(true);
    }

    @PostConstruct
    public void init() {
        reloadTypes();
        flusher.start();
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        flushRemaining();
    }

    // ==================== 记录入口 ====================

    /**
     * 记录一条操作日志并（按类型绑定）决定是否全服聊天广播。
     */
    public boolean record(int opType, Integer characterId, String characterName, Integer accountId,
                          String summary, String detail, String ip, String worldChannel) {
        OpLogTypeDO type = resolveType(opType);
        OpLogDO opLog = OpLogDO.builder()
                .opType(opType)
                .opTypeName(type.getName())
                .characterId(characterId)
                .characterName(characterName == null ? "" : characterName)
                .accountId(accountId)
                .summary(summary)
                .detail(detail)
                .chatType(type.getChatType() == null ? 0 : type.getChatType())
                .broadcast(Boolean.FALSE.equals(type.getBroadcast()) ? false : true)
                .ip(ip == null ? "" : ip)
                .worldChannel(worldChannel == null ? "" : worldChannel)
                .createTime(LocalDateTime.now())
                .build();

        try {
            if (Boolean.TRUE.equals(type.getEnabled())
                    && Boolean.TRUE.equals(type.getBroadcast())
                    && characterName != null && summary != null) {
                broadcast(type, characterName, summary);
            }
        } catch (Exception e) {
            log.warn("操作日志广播失败 opType={} err={}", opType, e.getMessage());
        }

        boolean ok = queue.offer(opLog);
        if (!ok) {
            log.warn("操作日志队列已满, 丢弃一条 opType={} char={}", opType, characterName);
        }
        return ok;
    }

    /**
     * 便捷重载：从角色对象采集账号/IP/世界频道。
     */
    public boolean record(Character chr, int opCode, String summary, String detail) {
        if (chr == null) {
            return false;
        }
        String ip = "";
        String worldChannel = "";
        try {
            if (chr.getClient() != null) {
                ip = chr.getClient().getRemoteAddress();
                worldChannel = chr.getWorld() + "-" + chr.getClient().getChannel();
            } else {
                worldChannel = String.valueOf(chr.getWorld());
            }
        } catch (Exception ignored) {
            worldChannel = String.valueOf(chr.getWorld());
        }
        return record(opCode, chr.getId(), chr.getName(), chr.getAccountId(), summary, detail, ip, worldChannel);
    }

    // ==================== 广播 ====================

    private void broadcast(OpLogTypeDO type, String playerName, String summary) {
        String tag = (type.getNoticeTag() == null || type.getNoticeTag().isEmpty())
                ? (type.getName() == null || type.getName().isEmpty() ? "系统" : type.getName())
                : type.getNoticeTag();
        String text = "【" + tag + "】" + playerName + " " + summary;
        int chatType = type.getChatType() == null ? 0 : type.getChatType();
        Server server;
        try {
            server = Server.getInstance();
        } catch (Exception e) {
            log.warn("Server 未就绪, 跳过广播: {}", text);
            return;
        }
        var packet = PacketCreator.serverNotice(chatType, text);
        for (World world : server.getWorlds()) {
            try {
                server.broadcastMessage(world.getId(), packet);
            } catch (Exception e) {
                log.warn("广播到世界{}失败: {}", world.getId(), e.getMessage());
            }
        }
    }

    // ==================== 异步批量入库 ====================

    private void flushLoop() {
        while (running) {
            try {
                OpLogDO first = queue.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (first == null) {
                    continue;
                }
                List<OpLogDO> batch = new ArrayList<>(BATCH_SIZE);
                batch.add(first);
                queue.drainTo(batch, BATCH_SIZE - 1);
                writeBatch(batch);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        flushRemaining();
    }

    private void writeBatch(List<OpLogDO> batch) {
        try {
            opLogMapper.insertBatch(batch);
        } catch (Exception e) {
            log.error("操作日志批量写入失败, 数量={}, 改逐条写入", batch.size(), e);
            for (OpLogDO opLog : batch) {
                try {
                    opLogMapper.insert(opLog);
                } catch (Exception e2) {
                    log.error("操作日志单条写入失败 char={}-{}", opLog.getCharacterId(), opLog.getCharacterName(), e2);
                }
            }
        }
    }

    private void flushRemaining() {
        List<OpLogDO> rest = new ArrayList<>(BATCH_SIZE);
        queue.drainTo(rest);
        if (!rest.isEmpty()) {
            writeBatch(rest);
        }
    }

    // ==================== 类型绑定解析 / CRUD ====================

    private OpLogTypeDO resolveType(int opCode) {
        OpLogTypeDO t = typeCache.get(opCode);
        if (t != null) {
            return t;
        }
        return typeCache.getOrDefault(OpLogType.OTHER, defaultType());
    }

    private OpLogTypeDO defaultType() {
        return OpLogTypeDO.builder()
                .opType(OpLogType.OTHER)
                .name(OpLogType.fallbackName(OpLogType.OTHER))
                .noticeTag("")
                .chatType(0)
                .broadcast(false)
                .enabled(true)
                .build();
    }

    public void reloadTypes() {
        List<OpLogTypeDO> list = opLogTypeMapper.selectListByQuery(QueryWrapper.create().where("enabled=1"));
        Map<Integer, OpLogTypeDO> map = new ConcurrentHashMap<>();
        for (OpLogTypeDO t : list) {
            if (t.getOpType() != null) {
                map.put(t.getOpType(), t);
            }
        }
        typeCache = map;
    }

    public List<OpLogTypeDO> listTypes() {
        return opLogTypeMapper.selectListByQuery(QueryWrapper.create().orderBy(OpLogTypeDO::getSortOrder, true));
    }

    public void saveType(OpLogTypeDO type) {
        if (type.getOpType() == null) {
            throw new IllegalArgumentException("操作类型码不能为空");
        }
        type.setUpdateTime(LocalDateTime.now());
        opLogTypeMapper.insertOrUpdate(type);
        reloadTypes();
    }

    public void deleteType(Integer id) {
        opLogTypeMapper.deleteById(id);
        reloadTypes();
    }

    // ==================== 日志查询 ====================

    public Page<OpLogDO> pageLogs(OpLogSearchDTO cond) {
        QueryWrapper qw = QueryWrapper.create().orderBy(OpLogDO::getCreateTime, false);
        if (cond.getOpType() != null) {
            qw.eq("op_type", cond.getOpType());
        }
        if (cond.getCharacterName() != null && !cond.getCharacterName().isBlank()) {
            qw.like("character_name", cond.getCharacterName());
        }
        if (cond.getAccountId() != null) {
            qw.eq("account_id", cond.getAccountId());
        }
        if (cond.getIp() != null && !cond.getIp().isBlank()) {
            qw.like("ip", cond.getIp());
        }
        if (cond.getStartTime() != null && !cond.getStartTime().isBlank()) {
            qw.ge("create_time", cond.getStartTime());
        }
        if (cond.getEndTime() != null && !cond.getEndTime().isBlank()) {
            qw.le("create_time", cond.getEndTime());
        }
        return opLogMapper.paginate(cond.getPageNo(), cond.getPageSize(), qw);
    }
}