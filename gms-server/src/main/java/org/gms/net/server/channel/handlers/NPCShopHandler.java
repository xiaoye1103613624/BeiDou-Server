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
import org.gms.client.autoban.AutobanFactory;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.buyback.SoldItemStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matze
 */
public final class NPCShopHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(NPCShopHandler.class);

    private static final byte MODE_SHOW_BUYBACK = 4;
    private static final byte MODE_SHOW_SHOP = 5;

    @Override
    public void handlePacket(InPacket p, Client c) {
        byte bmode = p.readByte();
        switch (bmode) {
        case 0: { // mode 0 = buy :)
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
            if (isBuybackMode(c)) {
                SoldItemStorage.getInstance().buyBackFromShop(c, slot, itemId);
            } else {
                c.getPlayer().getShop().buy(c, slot, itemId, quantity);
            }
            break;
        }
        case 1: { // sell ;)
            short slot = p.readShort();
            int itemId = p.readInt();
            short quantity = p.readShort();
            c.getPlayer().getShop().sell(c, ItemConstants.getInventoryType(itemId), slot, quantity, itemId);
            SoldItemStorage.getInstance().refreshBuybackShop(c);
            SoldItemStorage.getInstance().refreshBuybackTab(c);
            break;
        }
        case 2: { // recharge ;)

            byte slot = (byte) p.readShort();
            c.getPlayer().getShop().recharge(c, slot);
            break;
        }
        case 3: // leaving :(
            c.getPlayer().setShop(null);
            break;
        case MODE_SHOW_BUYBACK:
            if (c.getPlayer() != null && c.getPlayer().getShop() != null) {
                SoldItemStorage.getInstance().sendBuybackShop(c);
            }
            break;
        case MODE_SHOW_SHOP:
            if (c.getPlayer() != null && c.getPlayer().getShop() != null) {
                SoldItemStorage.getInstance().sendNormalShop(c);
            }
            break;
        }

    }

    private static boolean isBuybackMode(Client c) {
        Character chr = c.getPlayer();
        return chr != null && chr.isShopBuybackMode();
    }
}
