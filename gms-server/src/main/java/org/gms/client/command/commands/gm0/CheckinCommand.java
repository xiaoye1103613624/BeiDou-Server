package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.dailycheckin.DailyCheckinRewards;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;

/**
 * @签到 / @daily — 打开每日签到窗口。
 */
public class CheckinCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("DailyCheckin.command.desc"));
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (player.getLevel() < DailyCheckinRewards.MIN_LEVEL) {
            player.dropMessage(6, I18nUtil.getMessage("DailyCheckin.command.locked", DailyCheckinRewards.MIN_LEVEL));
            return;
        }
        int claimable = player.refreshCheckin();
        int viewDay = claimable >= 1 ? claimable : player.getCheckinDay();
        c.sendPacket(PacketCreator.dailyCheckinSnapshot(viewDay, player.getCheckinClaimed(), 0));
        if (claimable < 1) {
            long secs = player.getCheckinCooldownSeconds();
            long h = secs / 3600;
            long m = (secs % 3600) / 60;
            player.dropMessage(6, I18nUtil.getMessage("DailyCheckin.command.cooldown", h, m));
        }
    }
}
