package org.gms.net.server.task;

import org.gms.client.Character;
import org.gms.constants.string.ExtendKey;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 在线时长统计定时任务
 * 每5分钟更新所有在线玩家的在线时长，跨天自动重置
 */
public class OnlineTimeTask implements Runnable {
    /** 上次更新日期 */
    private final AtomicReference<LocalDate> lastUpdated = new AtomicReference<>(LocalDate.now());
    /** 是否正在运行，防止重复执行 */
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void run() {
        if (!Server.getInstance().isOnline()) {
            return;
        }
        if (!running.compareAndSet(false, true)) {
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
                    // 避免异常导致running恒为true
                    onlineTime = getInitialOnlineTime(chr);
                } else {
                    onlineTime += 5;
                }
                if (isNextDay || onlineTime < 0) {
                    onlineTime = 0;
                }
                int oldOnlineTime = chr.getCurrentOnlineTime();
                chr.setCurrentOnlineTime(onlineTime);

                // 每日活跃-在线奖励：每满30分钟(1800秒)在线累计1次进度，跨天后onlineTime被重置为0，自然停止累计
                int oldTier = Math.max(oldOnlineTime, 0) / 1800;
                int newTier = Math.max(onlineTime, 0) / 1800;
                if (newTier > oldTier && !isNextDay) {
                    org.gms.config.DailyActiveManager.addProgress(chr.getId(), "online_reward", newTier - oldTier);
                }
            }
        }
        running.set(false);
        lastUpdated.set(now);
    }

    /**
     * 获取玩家初始在线时长
     * 从账户扩展值中读取历史在线时长，用于服务器重启后恢复
     *
     * @param chr 角色
     * @return 初始在线时长（秒）
     */
    private int getInitialOnlineTime(Character chr) {
        try {
            String timeStr = chr.getAbstractPlayerInteraction().getAccountExtendValue(ExtendKey.ONLINE_TIME.getKey(), true);
            return timeStr == null ? 0 : Integer.parseInt(timeStr);
        } catch (Exception e) {
            return 0;
        }
    }
}