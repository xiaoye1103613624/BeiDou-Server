package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Family;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#CHANGE_FAMILY_MESSAGE} 封包。
 * 负责处理客户端的更改家族消息操作。
 */
public class FamilyPreceptsHandler extends AbstractPacketHandler {

    @Override
    public void handlePacket(InPacket p, Client c) {
        Family family = c.getPlayer().getFamily();
        if (family == null) {
            return;
        }
        if (family.getLeader().getChr() != c.getPlayer()) {
            return; //only the leader can set the precepts
        }
        String newPrecepts = p.readString();
        if (newPrecepts.length() > 200) {
            return;
        }
        family.setMessage(newPrecepts, true);
        //family.broadcastFamilyInfoUpdate(); //probably don't need to broadcast for this?
        c.sendPacket(PacketCreator.getFamilyInfo(c.getPlayer().getFamilyEntry()));
    }

}
