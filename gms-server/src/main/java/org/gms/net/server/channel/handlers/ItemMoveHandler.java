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
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#ITEM_MOVE} 封包。
 * 负责处理客户端的移动物品操作。
 */
public final class ItemMoveHandler extends AbstractPacketHandler {
    @Override
    public final void handlePacket(InPacket p, Client c) {  //使用装备、物品、道具
        p.skip(4); // 跳过包头
        // 操作频率检测（300ms内禁止连续操作）
        if (c.getPlayer().getAutoBanManager().getLastSpam(6) + 300 > currentServerTime()) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        InventoryType type = InventoryType.getByType(p.readByte()); // 物品栏类型
        short src = p.readShort();     // 源位置（装备栏为负值）
        short action = p.readShort();  // 目标位置（负值表示穿上装备）
        short quantity = p.readShort(); // 数量（丢弃时使用）

        // 根据src和action的正负判断操作类型
        if (src < 0 && action > 0) {
            InventoryManipulator.unequip(c, src, action);   //脱下装备
        } else if (action < 0) {
            InventoryManipulator.equip(c, src, action);     //穿上装备
        } else if (action == 0) {
            InventoryManipulator.drop(c, type, src, quantity); // 丢弃物品
        } else {
            InventoryManipulator.move(c, type, src, action);   // 移动物品
        }

        c.getPlayer().getAutoBanManager().spam(6);
    }
}