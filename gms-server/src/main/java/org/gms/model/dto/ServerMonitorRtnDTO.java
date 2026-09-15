package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 工作台服务器监控快照（JVM / OS / GC / 游戏在线）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerMonitorRtnDTO {
    private long sampledAt;
    private JvmInfo jvm;
    private OsInfo os;
    private GameInfo game;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JvmInfo {
        private long heapUsed;
        private long heapMax;
        private long nonHeapUsed;
        private long nonHeapMax;
        private int threadCount;
        private int daemonThreadCount;
        private long uptimeMs;
        private List<GcInfo> gc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GcInfo {
        private String name;
        private long collectionCount;
        private long collectionTimeMs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OsInfo {
        private String name;
        private String arch;
        private int availableProcessors;
        /** 0~1，不可用时为 -1 */
        private double systemCpuLoad;
        /** 0~1，不可用时为 -1 */
        private double processCpuLoad;
        private long totalPhysicalMemory;
        private long freePhysicalMemory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameInfo {
        private boolean online;
        private String version;
        private List<WorldOnlineInfo> worlds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorldOnlineInfo {
        private int id;
        private int playerCount;
        private List<ChannelOnlineInfo> channels;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelOnlineInfo {
        private int id;
        private int playerCount;
    }
}
