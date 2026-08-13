package org.gms.net.server.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.PacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

public class CustomPacketHandler implements PacketHandler {
    private static final byte DAMAGE_RANK_OPEN = 1;
    private static final byte DAMAGE_RANK_RESET = 2;
    private static final byte DAMAGE_RANK_CLOSE = 3;
    private static final byte WIDGET_SUBSCRIBE = 0x10;
    private static final byte WIDGET_BUFF_COUNTS = 0x11;
    private static final byte WIDGET_TRACKER_TOGGLE = 0x12;

    @Override
    public void handlePacket(InPacket p, Client c) {
        final Character chr = c.getPlayer();
        if (chr != null && p.available() > 0) {
            final byte subType = p.readByte();
            switch (subType) {
                case DAMAGE_RANK_OPEN:
                    chr.damageRankOpen();
                    return;
                case DAMAGE_RANK_RESET:
                    chr.damageRankReset();
                    return;
                case DAMAGE_RANK_CLOSE:
                    chr.damageRankClose();
                    return;
                case WIDGET_SUBSCRIBE:
                    handleWidgetSubscribe(c, chr);
                    return;
                case WIDGET_BUFF_COUNTS:
                    handleWidgetBuffCounts(p, chr);
                    return;
                case WIDGET_TRACKER_TOGGLE:
                    handleWidgetTrackerToggle(chr);
                    return;
                default:
                    break;
            }
        }

        if (p.available() > 0 && c.getGMLevel() >= 4) {
            c.sendPacket(PacketCreator.customPacket(p.readBytes(p.available())));
        }
    }

    private static void handleWidgetSubscribe(Client c, Character player) {
        c.sendPacket(PacketCreator.realHpMpWidget(player));

        for (Character member : player.getPartyMembersOnline()) {
            if (member.isLoggedInWorld()) {
                c.sendPacket(PacketCreator.partyBuffSnapshot(member));
                c.sendPacket(PacketCreator.partyHpPercent(member));
                if (member.getId() != player.getId()) {
                    byte[] countsPayload = member.getPartyBuffCountsPayload();
                    if (countsPayload != null && countsPayload.length > 0) {
                        c.sendPacket(PacketCreator.partyBuffCounts(
                                member.getId(), member.getPartyBuffCountsCount(), countsPayload));
                    }
                }
                if (player.isPartyTrackerVisible()) {
                    c.sendPacket(PacketCreator.partyTrackerUpdate(member));
                }
            }
        }

        if (player.getParty() == null) {
            c.sendPacket(PacketCreator.partyBuffSnapshot(player));
            c.sendPacket(PacketCreator.partyHpPercent(player));
            if (player.isPartyTrackerVisible()) {
                c.sendPacket(PacketCreator.partyTrackerUpdate(player));
            }
        }
    }

    private static void handleWidgetTrackerToggle(Character player) {
        if (player == null) {
            return;
        }
        player.setPartyTrackerVisible(!player.isPartyTrackerVisible());
    }

    private static void handleWidgetBuffCounts(InPacket p, Character player) {
        int count = p.readUnsignedByte();
        if (count > 0 && p.available() >= count * 9L) {
            byte[] payload = p.readBytes(count * 9);
            player.setPartyBuffCounts(count, payload);
            if (player.getParty() != null) {
                for (Character member : player.getPartyMembersOnline()) {
                    if (member.getId() != player.getId() && member.isLoggedInWorld()) {
                        member.sendPacket(PacketCreator.partyBuffCounts(player.getId(), count, payload));
                    }
                }
            }
        } else {
            player.setPartyBuffCounts(0, null);
        }
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}
