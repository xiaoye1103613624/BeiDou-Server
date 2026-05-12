package org.gms.net.server.coordinator.session;

import org.gms.net.server.Server;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 会话与并发协调「HostHwid」。
 * 在多开检测、登录绕过、匹配、事件召回等场景下集中管理跨连接状态。
 */
record HostHwid(Hwid hwid, Instant expiry) {
    static HostHwid createWithDefaultExpiry(Hwid hwid) {
        return new HostHwid(hwid, getDefaultExpiry());
    }

    private static Instant getDefaultExpiry() {
        return Instant.ofEpochMilli(Server.getInstance().getCurrentTime() + DAYS.toMillis(7));
    }
}
