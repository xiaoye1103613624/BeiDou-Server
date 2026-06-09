package org.gms.net.server.coordinator.session;

import org.gms.net.server.Server;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主机硬件ID缓存
 * 缓存远程主机与硬件ID的映射关系，定期清理过期条目
 */
class HostHwidCache {
    /** 主机硬件ID缓存表，key为远程主机地址 */
    private final ConcurrentHashMap<String, HostHwid> hostHwidCache = new ConcurrentHashMap<>();

    void clearExpired() {
        SessionDAO.deleteExpiredHwidAccounts();

        Instant now = Instant.ofEpochMilli(Server.getInstance().getCurrentTime());
        List<String> remoteHostsToRemove = new ArrayList<>();
        for (Map.Entry<String, HostHwid> entry : hostHwidCache.entrySet()) {
            if (now.isAfter(entry.getValue().expiry())) {
                remoteHostsToRemove.add(entry.getKey());
            }
        }

        for (String remoteHost : remoteHostsToRemove) {
            hostHwidCache.remove(remoteHost);
        }
    }

    void addEntry(String remoteHost, Hwid hwid) {
        hostHwidCache.put(remoteHost, HostHwid.createWithDefaultExpiry(hwid));
    }

    HostHwid getEntry(String remoteHost) {
        return hostHwidCache.get(remoteHost);
    }

    Hwid removeEntryAndGetItsHwid(String remoteHost) {
        HostHwid hostHwid = hostHwidCache.remove(remoteHost);
        return hostHwid == null ? null : hostHwid.hwid();
    }

    Hwid getEntryHwid(String remoteHost) {
        HostHwid hostHwid = hostHwidCache.get(remoteHost);
        return hostHwid == null ? null : hostHwid.hwid();
    }

}