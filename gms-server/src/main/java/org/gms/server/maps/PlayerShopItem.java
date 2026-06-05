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
 * 【类型】PlayerShopItem，class，包 {@code org.gms.server.maps}。
 *
 * <p>玩家商店物品条目数据类，封装商店中单个商品的物品对象、捆绑数量和价格信息。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理商店中单个商品的信息</li>
 *   <li>跟踪商品的库存数量</li>
 *   <li>维护商品的价格</li>
 *   <li>标记商品是否存在</li>
 * </ul>
 *
 * @author Matze
 */
public class PlayerShopItem {
    /** 商品物品对象 */
    private final Item item;
    /** 捆绑数量 */
    private short bundles;
    /** 商品价格 */
    private final int price;
    /** 商品是否存在 */
    private boolean doesExist;

    /**
     * 构造函数：创建玩家商店商品实例
     * 
     * @param item 商品物品对象
     * @param bundles 捆绑数量
     * @param price 商品价格
     */
    public PlayerShopItem(Item item, short bundles, int price) {
        this.item = item;
        this.bundles = bundles;
        this.price = price;
        this.doesExist = true;
    }

    /**
     * 设置商品存在状态
     * 
     * @param tf 存在状态
     */
    public void setDoesExist(boolean tf) {
        this.doesExist = tf;
    }

    /**
     * 检查商品是否存在
     * 
     * @return 如果商品存在则返回true，否则返回false
     */
    public boolean isExist() {
        return doesExist;
    }

    /**
     * 获取商品物品对象
     * 
     * @return 商品物品对象
     */
    public Item getItem() {
        return item;
    }

    /**
     * 获取捆绑数量
     * 
     * @return 捆绑数量
     */
    public short getBundles() {
        return bundles;
    }

    /**
     * 获取商品价格
     * 
     * @return 商品价格
     */
    public int getPrice() {
        return price;
    }

    /**
     * 设置捆绑数量
     * 
     * @param bundles 新的捆绑数量
     */
    public void setBundles(short bundles) {
        this.bundles = bundles;
    }
}