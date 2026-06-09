package org.gms.net.server.coordinator.session;

import org.gms.net.server.coordinator.session.SessionCoordinator.AntiMulticlientResult;

/**
 * 会话初始化结果枚举
 * 映射到防多客户端检测结果
 */
enum InitializationResult {
    /** 成功 */
    SUCCESS(AntiMulticlientResult.SUCCESS),
    /** 已初始化 */
    ALREADY_INITIALIZED(AntiMulticlientResult.REMOTE_PROCESSING),
    /** 超时 */
    TIMED_OUT(AntiMulticlientResult.COORDINATOR_ERROR),
    /** 错误 */
    ERROR(AntiMulticlientResult.COORDINATOR_ERROR);

    private final AntiMulticlientResult antiMulticlientResult;

    InitializationResult(AntiMulticlientResult antiMulticlientResult) {
        this.antiMulticlientResult = antiMulticlientResult;
    }

    public AntiMulticlientResult getAntiMulticlientResult() {
        return antiMulticlientResult;
    }
}