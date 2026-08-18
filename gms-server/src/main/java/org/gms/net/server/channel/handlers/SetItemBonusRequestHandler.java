package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.setitem.SetItemManager;
import org.gms.util.PacketCreator;

public final class SetItemBonusRequestHandler extends AbstractPacketHandler {
    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        int setId = p.readInt();
        boolean enabled = SetItemManager.isSetEnabled(setId);
        String text = enabled ? SetItemManager.buildSetBonusText(chr, setId) : "";

        c.sendPacket(PacketCreator.setItemFinalDamageBonus(
                chr.getSetFinalDamage(), chr.getSetDamageSkin(), chr.getCombatStatProfile()));
        c.sendPacket(PacketCreator.setItemSkillBonusSingle(setId, enabled, text));
    }
}
