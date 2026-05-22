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

    /**
     * 定时更新所有在线玩家的每日在线时长。
     * <p>
     * 任务每次执行时累加5分钟在线时间。若检测到跨天，则对所有玩家的每日在线时间进行重置。
     * 当玩家在线时长尚未初始化（值为-1）时，从账号扩展属性中读取历史值作为基准。
     * </p>
     */
    @Override
    public void run() {
        if (!Server.getInstance().isOnline()) {
            return;
        }

        // 判断是否跨天，跨天时重置所有玩家的每日在线时间
        LocalDate now = LocalDate.now();
        boolean isNextDay = now.isAfter(lastUpdated.get());

        // 遍历所有频道中的所有在线角色，更新其在线时长
        for (final Channel chan : Server.getInstance().getAllChannels()) {
            if (chan == null || chan.getPlayerStorage() == null) {
                continue;
            }
            for (final Character chr : chan.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }

                // 若在线时长未初始化，从账号扩展属性读取历史值；否则累加5分钟
                int onlineTime = chr.getCurrentOnlineTime();
                if (onlineTime == -1) {
                    String timeStr = chr.getAbstractPlayerInteraction().getAccountExtendValue("每日在线时间", true);
                    onlineTime = timeStr == null ? 0 : Integer.parseInt(timeStr);
                } else {
                    onlineTime += 5;
                }

                // 跨天或数值溢出时重置为0
                if (isNextDay || onlineTime < 0) {
                    onlineTime = 0;
                }
                chr.setCurrentOnlineTime(onlineTime);
            }
        }
        lastUpdated.set(now);
    }
}
