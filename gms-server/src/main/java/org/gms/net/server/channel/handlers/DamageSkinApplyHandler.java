package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.DamageSkinInventory;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

public final class DamageSkinApplyHandler extends AbstractPacketHandler {
    private static final int OP_APPLY = 1;

    @Override
    public void handlePacket(InPacket p, Client c) {
        int skinId = p.readInt();
        Character chr = c.getPlayer();
        if (chr == null) return;
        DamageSkinInventory inv = chr.getDamageSkinInventory();
        if (!inv.ownsSkin(skinId)) {
            c.sendPacket(PacketCreator.damageSkinResult(OP_APPLY, false, skinId, chr.getMeso()));
            return;
        }
        chr.setActiveDamageSkin(skinId);
        c.sendPacket(PacketCreator.damageSkinResult(OP_APPLY, true, skinId, chr.getMeso()));
        if (chr.getMap() != null) {
            chr.getMap().broadcastMessage(PacketCreator.damageSkinBroadcast(chr.getId(), skinId));
        }
    }
}