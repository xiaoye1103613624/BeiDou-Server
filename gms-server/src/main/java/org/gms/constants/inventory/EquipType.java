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
package org.gms.constants.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * 【枚举】EquipType，包 {@code org.gms.constants.inventory}。
 * 装备类型枚举。
 *
 * <p>定义游戏中各类装备的类型标识及其与物品ID的映射关系。</p>
 *
 * <p>装备类型包括：</p>
 * <ul>
 *   <li>配件类(ACCESSORY)</li>
 *   <li>防具类：帽子(CAP)、披风(CAPE)、上衣(COAT)、手套(GLOVES)、长袍(LONGCOAT)、裤子(PANTS)、鞋子(SHOES)、盾牌(SHIELD)</li>
 *   <li>脸饰/发型：脸饰(FACE)、发型(HAIR)</li>
 *   <li>宠物装备类：宠物装备(PET_EQUIP)、宠物装备-场地(PET_EQUIP_FIELD)、宠物装备-标签(PET_EQUIP_LABEL)、宠物装备-对话(PET_EQUIP_QUOTE)</li>
 *   <li>骑宠类：坐骑(TAMING)、马鞍(TAMING_SADDLE)</li>
 *   <li>武器类：单手剑/斧/锤/匕首/魔杖/法杖、双武器双手武器等</li>
 * </ul>
 *
 * <p>通过物品ID计算对应的装备类型，用于装备验证和分类。</p>
 *
 * @author 萧曵
 */
public enum EquipType {
    /** 未定义类型 - 用于无法识别的装备ID */
    UNDEFINED(-1),
    /** 配件类装备 */
    ACCESSORY(0),
    /** 帽子 */
    CAP(100),
    /** 披风 */
    CAPE(110),
    /** 上衣 */
    COAT(104),
    /** 脸饰 */
    FACE(2),
    /** 手套 */
    GLOVES(108),
    /** 发型 */
    HAIR(3),
    /** 长袍/套装 */
    LONGCOAT(105),
    /** 裤子 */
    PANTS(106),
    /** 宠物装备 */
    PET_EQUIP(180),
    /** 宠物装备-场地 */
    PET_EQUIP_FIELD(181),
    /** 宠物装备-标签 */
    PET_EQUIP_LABEL(182),
    /** 宠物装备-对话 */
    PET_EQUIP_QUOTE(183),
    /** 戒指 */
    RING(111),
    /** 盾牌 */
    SHIELD(109),
    /** 鞋子 */
    SHOES(107),
    /** 骑宠/坐骑 */
    TAMING(190),
    /** 坐骑马鞍 */
    TAMING_SADDLE(191),
    /** 单手剑 */
    SWORD(1302),
    /** 单手斧 */
    AXE(1312),
    /** 单手锤 */
    MACE(1322),
    /** 匕首 */
    DAGGER(1332),
    /** 魔杖 */
    WAND(1372),
    /** 法杖 */
    STAFF(1382),
    /** 双手剑 */
    SWORD_2H(1402),
    /** 双手斧 */
    AXE_2H(1412),
    /** 双手锤 */
    MACE_2H(1422),
    /** 枪 */
    SPEAR(1432),
    /** 长柄武器/戟 */
    POLEARM(1442),
    /** 弓 */
    BOW(1452),
    /** 弩 */
    CROSSBOW(1462),
    /** 拳套/爪 */
    CLAW(1472),
    /** 指节 */
    KNUCKLER(1482),
    /** 手枪 */
    PISTOL(1492);

    /** 装备类型值 */
    private final int i;

    /** 装备类型值到枚举的映射表 */
    private static final Map<Integer, EquipType> map = new HashMap(34);

    /**
     * 构造函数
     * @param val 装备类型值
     */
    EquipType(int val) {
        this.i = val;
    }

    /**
     * 获取装备类型值
     * @return 装备类型对应的数值
     */
    public int getValue() {
        return i;
    }

    /**
     * 静态初始化块：构建装备类型值到枚举的映射表
     */
    static {
        for (EquipType eqEnum : EquipType.values()) {
            map.put(eqEnum.i, eqEnum);
        }
    }

    /**
     * 根据物品ID获取装备类型
     * <p>通过物品ID计算对应的装备类型：
     * 对于武器类(13x, 14x)，使用物品ID/1000作为键；
     * 对于其他类型，使用物品ID/10000作为键。</p>
     * @param itemid 物品ID
     * @return 对应的装备类型，若未找到则返回UNDEFINED
     */
    public static EquipType getEquipTypeById(int itemid) {
        EquipType ret;
        int val = itemid / 100000;

        if (val == 13 || val == 14) {
            ret = map.get(itemid / 1000);
        } else {
            ret = map.get(itemid / 10000);
        }

        return (ret != null) ? ret : EquipType.UNDEFINED;
    }
}