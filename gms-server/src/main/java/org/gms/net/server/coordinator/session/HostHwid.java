package org.gms.net.server.coordinator.session;

import org.gms.net.server.Server;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 主机硬件ID记录
 * 绑定硬件ID与过期时间，用于缓存远程主机身份
 *
 * @param hwid   硬件ID
 * @param expiry 过期时间
 */
record HostHwid(Hwid hwid, Instant expiry) {
    static HostHwid createWithDefaultExpiry(Hwid hwid) {
        return new HostHwid(hwid, getDefaultExpiry());
    }

    private static Instant getDefaultExpiry() {
        return Instant.ofEpochMilli(Server.getInstance().getCurrentTime() + DAYS.toMillis(7));
    }
}