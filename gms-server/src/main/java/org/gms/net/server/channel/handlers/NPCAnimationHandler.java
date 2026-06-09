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
import org.gms.net.AbstractPacketHandler;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.OutPacket;

/**
 * NPC动画处理器
 * 处理NPC的动画操作，包括NPC对话动作和NPC移动
 */
public final class NPCAnimationHandler extends AbstractPacketHandler {

    /**
     * 处理NPC动画包，根据数据长度区分NPC对话动作和NPC移动，并发送对应的响应包
     *
     * @param p 输入数据包，包含NPC动画数据
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        if (c.getPlayer().isChangingMaps()) {   // possible cause of error 38 in some map transition scenarios, thanks Arnah
            return;
        }

        OutPacket op = OutPacket.create(SendOpcode.NPC_ACTION);
        int length = p.available();
        if (length == 6) { // NPC Talk
            op.writeInt(p.readInt());
            op.writeByte(p.readByte());   // 2 bytes, thanks resinate
            op.writeByte(p.readByte());
        } else if (length > 6) { // NPC Move
            byte[] bytes = p.readBytes(length - 9);
            op.writeBytes(bytes);
        }
        c.sendPacket(op);
    }
}
