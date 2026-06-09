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
 * 管理商店的加载和缓存，支持按商店ID和NPC ID查找
 * 使用单例模式，缓存商店数据避免重复加载
 *
 * @author Matze
 */
public class ShopFactory {
    private static final ShopFactory instance = new ShopFactory();

    public static ShopFactory getInstance() {
        return instance;
    }

    private final Map<Integer, Shop> shops = new HashMap<>();
    /** NPC ID -> 商店映射 */
    private final Map<Integer, Shop> npcShops = new HashMap<>();

    /**
     * 按ID加载商店并缓存
     *
     * @param id       商店ID或NPC ID
     * @param isShopId true=按商店ID查找, false=按NPC ID查找
     * @return 商店实例
     */
    private Shop loadShop(int id, boolean isShopId) {
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

    /**
     * 按NPC ID获取商店
     *
     * @param npcId NPC ID
     * @return 商店实例
     */
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