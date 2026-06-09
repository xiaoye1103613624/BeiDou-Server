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
package org.gms.server;

/**
 * 商店物品
 * 表示NPC商店中出售的物品，包含购买限制、价格和展示位信息
 *
 * @author Matze
 */
public class ShopItem {
    /** 可购买数量（0表示无限） */
    private final short buyable;
    /** 物品ID */
    private final int itemId;
    /** 价格 */
    private final int price;
    /** 展示位（排序位置） */
    private final int pitch;

    public ShopItem(short buyable, int itemId, int price, int pitch) {
        this.buyable = buyable;
        this.itemId = itemId;
        this.price = price;
        this.pitch = pitch;
    }

    public short getBuyable() {
        return buyable;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    /**
     * 获取展示位（排序位置）
     *
     * @return 展示位
     */
    public int getPitch() {
        return pitch;
    }
}