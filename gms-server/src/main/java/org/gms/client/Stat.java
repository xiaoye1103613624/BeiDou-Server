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
package org.gms.client;

/**
 * 角色属性枚举
 * <p>
 * 定义了游戏中角色的各种属性及其对应的位掩码值，用于高效地标识和传输角色状态变更信息。
 * 每个枚举值代表一种可变的游戏属性，如力量、敏捷、智力、HP、MP等。
 * </p>
 *
 * @author OdinMS Team
 * @version 1.0
 */
public enum Stat {
    /** 皮肤外观属性，位掩码值为 0x1 */
    SKIN(0x1),
    /** 面部外观属性，位掩码值为 0x2 */
    FACE(0x2),
    /** 头发外观属性，位掩码值为 0x4 */
    HAIR(0x4),
    /** 等级属性，位掩码值为 0x10 */
    LEVEL(0x10),
    /** 职业属性，位掩码值为 0x20 */
    JOB(0x20),
    /** 力量属性，位掩码值为 0x40 */
    STR(0x40),
    /** 敏捷属性，位掩码值为 0x80 */
    DEX(0x80),
    /** 智力属性，位掩码值为 0x100 */
    INT(0x100),
    /** 运气属性，位掩码值为 0x200 */
    LUK(0x200),
    /** 当前生命值属性，位掩码值为 0x400 */
    HP(0x400),
    /** 最大生命值属性，位掩码值为 0x800 */
    MAXHP(0x800),
    /** 当前魔法值属性，位掩码值为 0x1000 */
    MP(0x1000),
    /** 最大魔法值属性，位掩码值为 0x2000 */
    MAXMP(0x2000),
    /** 可分配属性点数，位掩码值为 0x4000 */
    AVAILABLEAP(0x4000),
    /** 可分配技能点数，位掩码值为 0x8000 */
    AVAILABLESP(0x8000),
    /** 经验值属性，位掩码值为 0x10000 */
    EXP(0x10000),
    /** 人气值属性，位掩码值为 0x20000 */
    FAME(0x20000),
    /** 游戏货币（金币）属性，位掩码值为 0x40000 */
    MESO(0x40000),
    /** 宠物信息属性，位掩码值为 0x180008 */
    PET(0x180008),
    /** 幸运抽奖经验加成属性，位掩码值为 0x200000 */
    GACHAEXP(0x200000);
    
    /** 属性对应的位掩码值 */
    private final int i;

    /**
     * 构造函数
     * <p>初始化枚举项的位掩码值</p>
     *
     * @param i 属性对应的位掩码值
     */
    Stat(int i) {
        this.i = i;
    }

    /**
     * 获取属性的位掩码值
     *
     * @return 位掩码值
     */
    public int getValue() {
        return i;
    }

    /**
     * 根据位掩码值获取对应的属性枚举项
     *
     * @param value 位掩码值
     * @return 对应的Stat枚举项，如果未找到则返回null
     */
    public static Stat getByValue(int value) {
        for (Stat stat : Stat.values()) {
            if (stat.getValue() == value) {
                return stat;
            }
        }
        return null;
    }

    /**
     * 根据5字节编码获取对应的属性枚举项
     * <p>主要用于解析特定格式的属性数据</p>
     *
     * @param encoded 5字节编码值
     * @return 对应的Stat枚举项，如果未找到则返回null
     */
    public static Stat getBy5ByteEncoding(int encoded) {
        switch (encoded) {
            case 64:
                return STR;
            case 128:
                return DEX;
            case 256:
                return INT;
            case 512:
                return LUK;
        }
        return null;
    }

    /**
     * 根据字符串名称获取对应的属性枚举项
     *
     * @param type 属性名称字符串
     * @return 对应的Stat枚举项，如果未找到则返回null
     */
    public static Stat getByString(String type) {
    	for (Stat stat : Stat.values()) {
            if (stat.name().equals(type)) {
                return stat;
            }
        }
        return null;
    }
}