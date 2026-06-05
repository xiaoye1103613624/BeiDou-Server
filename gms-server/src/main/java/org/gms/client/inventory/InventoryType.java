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
package org.gms.client.inventory;

import lombok.Getter;
import org.gms.util.I18nUtil;

/**
 * 【类型】InventoryType（enum），包 `org.gms.client.inventory`。背包类型枚举：定义物品背包分类（装备、消耗、设置、其他、现金）及对应的WZ目录名映射与位域编码。
 *
 * @author Matze
 */
@Getter
public enum InventoryType {
    UNDEFINED(0, I18nUtil.getMessage("InventoryType.UNDEFINED")),
    EQUIP(1, I18nUtil.getMessage("InventoryType.EQUIP")),
    USE(2, I18nUtil.getMessage("InventoryType.USE")),
    SETUP(3, I18nUtil.getMessage("InventoryType.SETUP")),
    ETC(4, I18nUtil.getMessage("InventoryType.ETC")),
    CASH(5, I18nUtil.getMessage("InventoryType.CASH")),
    CANHOLD(6, I18nUtil.getMessage("InventoryType.CANHOLD")),   //Proof-guard for inserting after removal checks
    EQUIPPED(-1, I18nUtil.getMessage("InventoryType.EQUIPPED")); //Seems nexon screwed something when removing an item T_T

    private final byte type;
    private final String name;

    InventoryType(int type, String name) {
        this.type = (byte) type;
        this.name = name;
    }

    public short getBitfieldEncoding() {
        return (short) (2 << type);
    }

    public static InventoryType getByType(byte type) {
        for (InventoryType l : InventoryType.values()) {
            if (l.getType() == type) {
                return l;
            }
        }
        return UNDEFINED;
    }

    /**
     * 根据WZ文件中的名称获取对应的背包类型
     * @param name WZ中的背包类型名称
     * @return 对应的背包类型枚举
     */
    public static InventoryType getByWZName(String name) {
        return switch (name) {
            // 装饰/装置类物品
            case "Install" -> SETUP;
            // 消耗类物品
            case "Consume" -> USE;
            // 其他杂项物品
            case "Etc" -> ETC;
            // 现金类物品
            case "Cash" -> CASH;
            // 宠物类物品（归类为现金类）
            case "Pet" -> CASH;
            // 未知类型
            default -> UNDEFINED;
        };
    }

    public boolean canChangeSlotMax() {
        // 如果需要支持更改现金的最大堆叠，可以修改这里
        return this == USE || this == ETC;
    }

    public boolean isEquip() {
        return this == EQUIP || this == EQUIPPED;
    }
}