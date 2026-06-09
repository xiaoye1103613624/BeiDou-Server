/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.server.loot;

import org.gms.client.Character;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;

import java.util.HashMap;
import java.util.Map;


/**
 * 掉落库存
 * 快照玩家背包中的物品数量，用于掉落分配时判断是否需要某物品
 *
 * @author Ronan
 */
public class LootInventory {
    /** 物品ID -> 数量映射 */
    Map<Integer, Integer> items = new HashMap<>(50);

    /**
     * 从玩家背包构造快照
     *
     * @param from 玩家
     */
    public LootInventory(Character from) {
        for (InventoryType values : InventoryType.values()) {

            for (Item it : from.getInventory(values).list()) {
                Integer itemQty = items.get(it.getItemId());

                if (itemQty == null) {
                    items.put(it.getItemId(), (int) it.getQuantity());
                } else {
                    items.put(it.getItemId(), itemQty + it.getQuantity());
                }
            }
        }
    }

    /**
     * 检查是否拥有物品
     *
     * @param itemid   物品ID
     * @param quantity 需求量
     * @return 0不拥有，1拥有但不足，2满足需求
     */
    public int hasItem(int itemid, int quantity) {
        Integer itemQty = items.get(itemid);
        return itemQty == null ? 0 : itemQty >= quantity ? 2 : itemQty > 0 ? 1 : 0;
    }

}