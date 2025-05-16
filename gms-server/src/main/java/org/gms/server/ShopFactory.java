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

import java.util.HashMap;
import java.util.Map;

/**
 * 商店工厂
 *
 * @author Matze
 */
public class ShopFactory {
    /**
     * 商店工厂实例 饿汉式
     */
    private static final ShopFactory INSTANCE = new ShopFactory();

    public static ShopFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 商店id 与商店对象映射
     */
    private final Map<Integer, Shop> shops = new HashMap<>();
    /**
     * NPCid 与商店对象映射
     */
    private final Map<Integer, Shop> npcShops = new HashMap<>();

    /**
     * 根据商店ID或NPC ID加载商店信息
     *
     * @param id 商店ID或NPC ID
     * @param isShopId 是否为商店ID（true为商店ID，false为NPC ID）
     * @return 返回加载的商店信息，如果未找到则返回null
     */
    private Shop loadShop(int id, boolean isShopId) {
        // 从数据库加载商店信息
        Shop ret = Shop.createFromDB(id, isShopId);
        if (ret != null) {
            shops.put(ret.getId(), ret);
            npcShops.put(ret.getNpcId(), ret);
        } else if (isShopId) {
            shops.put(id, null);
        } else {
            npcShops.put(id, null);
        }
        return ret;
    }


    public Shop getShop(int shopId) {
        if (shops.containsKey(shopId)) {
            return shops.get(shopId);
        }
        return loadShop(shopId, true);
    }

    public Shop getShopForNPC(int npcId) {
        if (npcShops.containsKey(npcId)) {
            return npcShops.get(npcId);
        }
        return loadShop(npcId, false);
    }

    public void reloadShops() {
        shops.clear();
        npcShops.clear();
    }
}
