package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作台 MySQL / Druid 连接池监控快照。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MysqlMonitorRtnDTO {
    private long sampledAt;
    private String version;
    private String databaseName;
    private long uptimeSeconds;
    private long threadsConnected;
    private long maxConnections;
    private long threadsRunning;
    private long questions;
    private long slowQueries;
    private long databaseSizeBytes;
    private PoolInfo pool;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoolInfo {
        private boolean available;
        private int activeCount;
        private int poolingCount;
        private int waitThreadCount;
        private int maxActive;
        private long connectCount;
        private long connectErrorCount;
        private long createCount;
        private long destroyCount;
    }
}
