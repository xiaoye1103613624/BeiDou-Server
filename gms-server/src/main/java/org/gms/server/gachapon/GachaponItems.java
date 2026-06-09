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
 * 扭蛋物品抽象基类
 * 定义不同城镇扭蛋机的奖品池，分为普通、稀有、罕见三个等级
 * 子类实现具体的奖品列表
 *
 * @author Alan (SharpAceX)
 */
public abstract class GachaponItems {

    /**
     * 获取普通品质物品
     *
     * @return 物品ID数组
     */
    public abstract int[] getCommonItems();

    /**
     * 获取稀有品质物品
     *
     * @return 物品ID数组
     */
    public abstract int[] getUncommonItems();

    /**
     * 获取罕见品质物品
     *
     * @return 物品ID数组
     */
    public abstract int[] getRareItems();

    /** 普通品质物品列表 */
    private final int[] commonItems;
    /** 稀有品质物品列表 */
    private final int[] uncommonItems;
    /** 罕见品质物品列表 */
    private final int[] rareItems;

    /**
     * 构造扭蛋物品管理器
     * 初始化时加载所有品质的物品列表
     */
    public GachaponItems() {
        this.commonItems = getCommonItems();
        this.uncommonItems = getUncommonItems();
        this.rareItems = getRareItems();
    }

    /**
     * 根据等级获取物品列表
     *
     * @param tier 等级（0=普通，1=稀有，2=罕见）
     * @return 物品ID数组
     */
    public final int[] getItems(int tier) {
        switch (tier) {
        case 0:
            return commonItems;
        case 1:
            return uncommonItems;
        case 2:
            return rareItems;
        default:
            return null;
        }
    }
}