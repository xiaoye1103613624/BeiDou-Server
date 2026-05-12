package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Family;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 频道服务器入站封包处理器「FamilyPreceptsHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
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
