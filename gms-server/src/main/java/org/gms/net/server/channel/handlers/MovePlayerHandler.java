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
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;
import org.gms.exception.EmptyMovementException;

/**
 * 玩家移动处理器
 * 处理玩家角色的移动操作
 */
public final class MovePlayerHandler extends AbstractMovementPacketHandler {

    /**
     * 处理玩家移动包，解析玩家的移动路径，更新位置并广播给地图中的其他玩家
     *
     * @param p 输入数据包，包含玩家的移动数据
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        p.skip(9);
        try {   // thanks Sa for noticing empty movement sequences crashing players
            int movementDataStart = p.getPosition();
            updatePosition(p, c.getPlayer(), 0);
            long movementDataLength = p.getPosition() - movementDataStart; //how many bytes were read by updatePosition
            p.seek(movementDataStart);

            c.getPlayer().getMap().movePlayer(c.getPlayer(), c.getPlayer().getPosition());
            if (c.getPlayer().isHidden()) {
                c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), PacketCreator.movePlayer(c.getPlayer().getId(), p, movementDataLength), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.movePlayer(c.getPlayer().getId(), p, movementDataLength), false);
            }
        } catch (EmptyMovementException e) {
        }
    }
}
