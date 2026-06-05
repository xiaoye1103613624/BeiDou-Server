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
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#NPC_SHOP} 封包。
 * 负责处理客户端NPC商店购买、出售及充值操作。
 */
public final class NPCShopHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(NPCShopHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        byte bmode = p.readByte();
        // 根据商店操作类型执行相应操作
        switch (bmode) {
        // 0: 购买物品
        case 0: {
            short slot = p.readShort();// slot
            int itemId = p.readInt();
            short quantity = p.readShort();
            if (quantity < 1) {
                AutobanFactory.PACKET_EDIT.alert(c.getPlayer(),
                        c.getPlayer().getName() + " tried to packet edit a npc shop.");
                log.warn("Chr {} tried to buy quantity {} of itemid {}", c.getPlayer().getName(), quantity, itemId);
                c.disconnect(true, false);
                return;
            }
            c.getPlayer().getShop().buy(c, slot, itemId, quantity);
            break;
        }
        // 1: 出售物品
        case 1: {
            short slot = p.readShort();
            int itemId = p.readInt();
            short quantity = p.readShort();
            c.getPlayer().getShop().sell(c, ItemConstants.getInventoryType(itemId), slot, quantity);
            break;
        }
        // 2: 充值（为弹药/箭矢等补充）
        case 2: {
            byte slot = (byte) p.readShort();
            c.getPlayer().getShop().recharge(c, slot);
            break;
        }
        // 3: 离开商店
        case 3:
            c.getPlayer().setShop(null);
            break;
        }

    }
}