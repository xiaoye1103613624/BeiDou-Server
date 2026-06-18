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
package org.gms.client.inventory.manipulator;

import org.gms.client.inventory.Item;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.ItemConstants;

/**
 * 业力（绑定）操作工具类
 * 处理物品的绑定/解绑标记，防止物品在非绑定状态下交易
 * 支持装备和消耗品的业力标记设置
 *
 * @author RonanLana
 */
public class KarmaManipulator {
    private static short getKarmaFlag(Item item) {
        return item.getItemType() == 1 ? ItemConstants.KARMA_EQP : ItemConstants.KARMA_USE;
    }

    public static boolean hasKarmaFlag(Item item) {
        short karmaFlag = getKarmaFlag(item);
        return (item.getFlag() & karmaFlag) == karmaFlag;
    }

    public static void toggleKarmaFlagToUntradeable(Item item) {
        // 怪物卡片(2380000~2389999)允许自由交换，不转为不可交易
        if (ItemId.isMonsterCard(item.getItemId())) {
            return;
        }
        short karmaFlag = getKarmaFlag(item);
        short flag = item.getFlag();

        if ((flag & karmaFlag) == karmaFlag) {
            flag ^= karmaFlag;
            flag |= ItemConstants.UNTRADEABLE;

            item.setFlag(flag);
        }
    }

    public static void setKarmaFlag(Item item) {
        short karmaFlag = getKarmaFlag(item);
        short flag = item.getFlag();

        flag |= karmaFlag;
        flag &= (0xFFFFFFFF ^ ItemConstants.UNTRADEABLE);
        item.setFlag(flag);
    }
}