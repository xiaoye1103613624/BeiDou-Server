package org.gms.net.server.task;

import org.gms.client.Character;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 服务器定时任务「OnlineTimeTask」。
 * 在 org.gms.net.server.task 下注册执行，用于重置、刷新或持久化与在线玩家相关的数据。
 */
public class OnlineTimeTask implements Runnable {
    private final AtomicReference<LocalDate> lastUpdated = new AtomicReference<>(LocalDate.now());

    @Override
    public void run() {
        if (!Server.getInstance().isOnline()) {
            return;
        }
        LocalDate now = LocalDate.now();
        boolean isNextDay = now.isAfter(lastUpdated.get());
        for (final Channel chan : Server.getInstance().getAllChannels()) {
            if (chan == null || chan.getPlayerStorage() == null) {
                continue;
            }
            for (final Character chr : chan.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                int onlineTime = chr.getCurrentOnlineTime();
                if (onlineTime == -1) {
                    String timeStr = chr.getAbstractPlayerInteraction().getAccountExtendValue("每日在线时间", true);
                    onlineTime = timeStr == null ? 0 : Integer.parseInt(timeStr);
                } else {
                    onlineTime += 5;
                }
                if (isNextDay || onlineTime < 0) {
                    onlineTime = 0;
                }
                chr.setCurrentOnlineTime(onlineTime);
            }
        }
        lastUpdated.set(now);
    }
}
