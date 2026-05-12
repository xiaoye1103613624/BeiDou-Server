/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.autoban.AutobanFactory;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ChatLogger;
import org.gms.util.PacketCreator;

/**
 * 频道服务器入站封包处理器「PetChatHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class PetChatHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(PetChatHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        int petId = p.readInt();
        p.readInt();
        p.readByte();
        int act = p.readByte();
        byte pet = c.getPlayer().getPetIndex(petId);
        if ((pet < 0 || pet > 3) || (act < 0 || act > 9)) {
            return;
        }
        String text = p.readString();
        if (text.length() > Byte.MAX_VALUE) {
            AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with pets.");
            log.warn("Chr {} tried to send text with length of {}", c.getPlayer().getName(), text.length());
            c.disconnect(true, false);
            return;
        }
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.petChat(c.getPlayer().getId(), pet, act, text), true);
        ChatLogger.log(c, "Pet", text);
    }
}
