package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.dailycheckin.DailyCheckinRewards;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每日签到 — 收包 0x11A，回复 0x17C 快照。
 */
public final class DailyCheckinHandler extends AbstractPacketHandler {

    private static final Logger log = LoggerFactory.getLogger(DailyCheckinHandler.class);
    private static final int REQ_OPEN = 0;
    private static final int REQ_CLAIM = 1;

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character player = c.getPlayer();
        if (player == null) {
            return;
        }
        int action = p.readByte();

        if (player.getLevel() < DailyCheckinRewards.MIN_LEVEL) {
            return;
        }

        int claimable = player.refreshCheckin();
        int justClaimed = 0;
        try {
            if (action == REQ_CLAIM) {
                int day = p.readByte();
                if (claimable >= 1 && day == claimable) {
                    if (DailyCheckinRewards.grantDay(c, day)) {
                        player.applyCheckinClaim(day);
                        player.saveCharToDB();
                        justClaimed = day;
                        claimable = 0;
                        log.info("[DailyCheckin] {} claimed day {}", player.getName(), day);
                    }
                } else {
                    log.info("[DailyCheckin] {} claim rejected day={} claimable={} cooldownSecs={}",
                            player.getName(), day, claimable, player.getCheckinCooldownSeconds());
                }
            }
        } catch (Exception e) {
            log.warn("[DailyCheckin] action={} threw", action, e);
        }

        int viewDay = claimable >= 1 ? claimable : player.getCheckinDay();
        c.sendPacket(PacketCreator.dailyCheckinSnapshot(viewDay, player.getCheckinClaimed(), justClaimed));
    }
}
