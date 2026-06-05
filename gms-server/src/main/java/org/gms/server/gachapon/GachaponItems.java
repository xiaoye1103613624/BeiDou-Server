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

package org.gms.server.gachapon;

/**
 * 【类型】GachaponItems（abstract class），包 `org.gms.server.gachapon`。
 *
 * 扭蛋道具池抽象基类，定义扭蛋机各城镇专属道具池的三级稀有度物品列表契约，子类实现 getCommonItems/getUncommonItems/getRareItems。
 *
 * @author 萧曵
 */
public abstract class GachaponItems {

    public abstract int[] getCommonItems();
    public abstract int[] getUncommonItems();
    public abstract int[] getRareItems();

    private final int[] commonItems;
    private final int[] uncommonItems;
    private final int[] rareItems;

    public GachaponItems() {
        this.commonItems = getCommonItems();
        this.uncommonItems = getUncommonItems();
        this.rareItems = getRareItems();
    }

    /**
     * 根据稀有度等级获取对应的物品列表
     * @param tier 稀有度等级（0=普通，1=稀有，2=极稀有）
     * @return 对应稀有度的物品数组
     */
    public final int[] getItems(int tier) {
        // 根据稀有度等级返回对应的物品数组
        switch (tier) {
        // 0: 普通物品
        case 0:
            return commonItems;
        // 1: 稀有物品
        case 1:
            return uncommonItems;
        // 2: 极稀有物品
        case 2:
            return rareItems;
        // 默认: 无效等级返回null
        default:
            return null;
        }
    }
}