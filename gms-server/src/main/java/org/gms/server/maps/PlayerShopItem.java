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
package org.gms.server.maps;

import org.gms.client.inventory.Item;

/**
 * 玩家商店物品
 * 封装玩家商店中上架物品的基本信息
 *
 * @author Matze
 */
public class PlayerShopItem {
    /** 上架物品 */
    private final Item item;
    /** 物品数量 */
    private short bundles;
    /** 售价 */
    private final int price;
    /** 是否存在 */
    private boolean doesExist;

    /**
     * 构造玩家商店物品
     *
     * @param item    物品
     * @param bundles 数量
     * @param price   售价
     */
    public PlayerShopItem(Item item, short bundles, int price) {
        this.item = item;
        this.bundles = bundles;
        this.price = price;
        this.doesExist = true;
    }

    /**
     * 设置物品是否存在
     */
    public void setDoesExist(boolean tf) {
        this.doesExist = tf;
    }

    /**
     * 检查物品是否存在
     */
    public boolean isExist() {
        return doesExist;
    }

    /**
     * 获取物品
     */
    public Item getItem() {
        return item;
    }

    /**
     * 获取物品数量
     */
    public short getBundles() {
        return bundles;
    }

    /**
     * 获取售价
     */
    public int getPrice() {
        return price;
    }

    /**
     * 设置物品数量
     */
    public void setBundles(short bundles) {
        this.bundles = bundles;
    }
}