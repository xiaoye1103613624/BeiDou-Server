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
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 频道服务器入站封包处理器「FaceExpressionHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class FaceExpressionHandler extends AbstractPacketHandler {
    @Override
    public final void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        int emote = p.readInt();

        if (emote > 7) {
            int itemid = 5159992 + emote;   // thanks RajanGrewal (Darter) for reporting unchecked emote itemid
            if (!ItemId.isFaceExpression(itemid) || chr.getInventory(ItemConstants.getInventoryType(itemid)).findById(itemid) == null) {
                return;
            }
        } else if (emote < 1) {
            return;
        }

        if (c.tryacquireClient()) {
            try {   // expecting players never intends to wear the emote 0 (default face, that changes back after 5sec timeout)
                if (chr.isLoggedInWorld()) {
                    chr.changeFaceExpression(emote);
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}
