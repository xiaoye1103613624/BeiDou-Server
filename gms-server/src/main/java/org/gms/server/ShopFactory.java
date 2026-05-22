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
 * 【类型】ShopFactory（class），包 `org.gms.server`。
 *
 * 商店工厂（单例），负责从数据库加载商店数据并缓存。
 * 维护两个索引：按商店 ID（{@code shops}）和按 NPC ID（{@code npcShops}），
 * 支持通过商店 ID 或 NPC ID 查找到对应的 {@link Shop} 实例。
 * 懒加载策略：首次查询时从数据库加载并缓存，后续直接命中缓存。
 *
 * @author Matze
 */
public class ShopFactory {
    private static final ShopFactory instance = new ShopFactory();

    public static ShopFactory getInstance() {
        return instance;
    }

    /** 商店 ID → 商店实例（一个商店可被多个 NPC 共享） */
    private final Map<Integer, Shop> shops = new HashMap<>();
    /** NPC ID → 商店实例（一个 NPC 关联一个商店） */
    private final Map<Integer, Shop> npcShops = new HashMap<>();

    /** 从数据库加载商店数据并双索引缓存 */
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

    /** 按商店 ID 获取商店（缓存未命中时从 DB 加载） */
    public Shop getShop(int shopId) {
        if (shops.containsKey(shopId)) {
            return shops.get(shopId);
        }
        return loadShop(shopId, true);
    }

    /** 按 NPC ID 获取关联商店（缓存未命中时从 DB 加载） */
    public Shop getShopForNPC(int npcId) {
        if (npcShops.containsKey(npcId)) {
            return npcShops.get(npcId);
        }
        return loadShop(npcId, false);
    }

    /** 清空所有缓存（用于重载商店数据） */
    public void reloadShops() {
        shops.clear();
        npcShops.clear();
    }
}
