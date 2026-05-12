package org.gms.net.server.coordinator.session;

import org.gms.net.server.coordinator.session.SessionCoordinator.AntiMulticlientResult;

/**
 * 会话与并发协调「InitializationResult」。
 * 在多开检测、登录绕过、匹配、事件召回等场景下集中管理跨连接状态。
 */
enum InitializationResult {
    SUCCESS(AntiMulticlientResult.SUCCESS),
    ALREADY_INITIALIZED(AntiMulticlientResult.REMOTE_PROCESSING),
    TIMED_OUT(AntiMulticlientResult.COORDINATOR_ERROR),
    ERROR(AntiMulticlientResult.COORDINATOR_ERROR);

    private final AntiMulticlientResult antiMulticlientResult;

    InitializationResult(AntiMulticlientResult antiMulticlientResult) {
        this.antiMulticlientResult = antiMulticlientResult;
    }

    public AntiMulticlientResult getAntiMulticlientResult() {
        return antiMulticlientResult;
    }
}
