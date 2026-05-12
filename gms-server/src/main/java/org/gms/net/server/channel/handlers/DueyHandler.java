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
import org.gms.client.processor.npc.DueyProcessor;
import org.gms.config.GameConfig;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 频道服务器入站封包处理器「DueyHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class DueyHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        if (!GameConfig.getServerBoolean("use_duey")) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        byte operation = p.readByte();
        if (operation == DueyProcessor.Actions.TOSERVER_RECV_ITEM.getCode()) { // on click 'O' Button, thanks inhyuk
            DueyProcessor.dueySendTalk(c, false);
        } else if (operation == DueyProcessor.Actions.TOSERVER_SEND_ITEM.getCode()) {
            byte inventId = p.readByte();
            short itemPos = p.readShort();
            short amount = p.readShort();
            int mesos = p.readInt();
            String recipient = p.readString();
            boolean quick = p.readByte() != 0;
            String message = quick ? p.readString() : null;

            DueyProcessor.dueySendItem(c, inventId, itemPos, amount, mesos, message, recipient, quick);
        } else if (operation == DueyProcessor.Actions.TOSERVER_REMOVE_PACKAGE.getCode()) {
            int packageid = p.readInt();

            DueyProcessor.dueyRemovePackage(c, packageid, true);
        } else if (operation == DueyProcessor.Actions.TOSERVER_CLAIM_PACKAGE.getCode()) {
            int packageid = p.readInt();

            DueyProcessor.dueyClaimPackage(c, packageid);
        } else if (operation == DueyProcessor.Actions.TOSERVER_CLAIM_PACKAGE.getCode()) {
            DueyProcessor.dueySendTalk(c, false);
        }
    }
}
