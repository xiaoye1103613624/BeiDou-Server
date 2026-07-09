package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.dailycheckin.DailyCheckinRewards;
import org.gms.util.PacketCreator;

/**
 * @签到 / @daily — 打开每日签到窗口。
 */
public class CheckinCommand extends Command {
    {
        setDescription("打开每日签到窗口。");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (player.getLevel() < DailyCheckinRewards.MIN_LEVEL) {
            player.dropMessage(6, "每日签到在 " + DailyCheckinRewards.MIN_LEVEL + " 级解锁。");
            return;
        }
        int claimable = player.refreshCheckin();
        int viewDay = claimable >= 1 ? claimable : player.getCheckinDay();
        c.sendPacket(PacketCreator.dailyCheckinSnapshot(viewDay, player.getCheckinClaimed(), 0));
        if (claimable < 1) {
            long secs = player.getCheckinCooldownSeconds();
            long h = secs / 3600;
            long m = (secs % 3600) / 60;
            player.dropMessage(6, "每日签到：下次奖励将在 " + h + " 小时 " + m + " 分钟后解锁。");
        }
    }
}
