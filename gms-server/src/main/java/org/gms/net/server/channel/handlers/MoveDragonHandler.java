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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.packet.InPacket;
import org.gms.server.maps.Dragon;
import org.gms.util.PacketCreator;
import org.gms.exception.EmptyMovementException;

import java.awt.*;


/**
 * 龙移动处理器
 * 处理玩家控制龙移动的操作
 */
public class MoveDragonHandler extends AbstractMovementPacketHandler {

    /**
     * 处理龙移动包，解析龙的起始位置，更新龙的移动路径并广播给地图中的其他玩家
     *
     * @param p 输入数据包，包含龙的移动数据
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public void handlePacket(InPacket p, Client c) {
        final Character chr = c.getPlayer();
        final Point startPos = new Point(p.readShort(), p.readShort());
        final Dragon dragon = chr.getDragon();
        if (dragon != null) {
            try {
                int movementDataStart = p.getPosition();
                updatePosition(p, dragon, 0);
                long movementDataLength = p.getPosition() - movementDataStart; //how many bytes were read by updatePosition
                p.seek(movementDataStart);

                if (chr.isHidden()) {
                    chr.getMap().broadcastGMPacket(chr, PacketCreator.moveDragon(dragon, startPos, p, movementDataLength));
                } else {
                    chr.getMap().broadcastMessage(chr, PacketCreator.moveDragon(dragon, startPos, p, movementDataLength), dragon.getPosition());
                }
            } catch (EmptyMovementException e) {
            }
        }
    }
}