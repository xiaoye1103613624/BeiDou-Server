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
 * 常见属性
 */
public enum Stat {
    /**
     * 皮肤
     */
    SKIN(0x1),
    /**
     * 面部
     */
    FACE(0x2),
    /**
     * 发型
     */
    HAIR(0x4),
    /**
     * 等级
     */
    LEVEL(0x10),
    /**
     * 职业 32
     */
    JOB(0x20),
    /**
     * 力量 64
     */
    STR(0x40),
    /**
     * 敏捷 128
     */
    DEX(0x80),
    /**
     * 智力 256
     */
    INT(0x100),
    /**
     * 运气 512
     */
    LUK(0x200),
    /**
     * 当前HP
     */
    HP(0x400),
    /**
     * 最大HP
     */
    MAXHP(0x800),
    /**
     * 当前MP
     */
    MP(0x1000),
    /**
     * 最大MP
     */
    MAXMP(0x2000),
    /**
     *
     */
    AVAILABLEAP(0x4000),
    /**
     *
     */
    AVAILABLESP(0x8000),
    /**
     * 经验
     */
    EXP(0x10000),
    /**
     * 人气
     */
    FAME(0x20000),
    /**
     * 金币
     */
    MESO(0x40000),
    /**
     * 宠物
     */
    PET(0x180008),
    /**
     * 金币抽奖经验值
     */
    GACHAEXP(0x200000);
    private final int i;

    Stat(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }

    public static Stat getByValue(int value) {
        for (Stat stat : Stat.values()) {
            if (stat.getValue() == value) {
                return stat;
            }
        }
        return null;
    }

    /**
     * 根据5字节编码值获取对应的Stat对象。
     *
     * @param encoded 5字节编码值
     * @return 对应编码值的Stat对象，如果不存在则返回null
     */
    public static Stat getBy5ByteEncoding(int encoded) {
        return switch (encoded) {
            case 64 -> STR;
            case 128 -> DEX;
            case 256 -> INT;
            case 512 -> LUK;
            default -> null;
        };
    }

    public static Stat getByString(String type) {
        for (Stat stat : Stat.values()) {
            if (stat.name().equals(type)) {
                return stat;
            }
        }
        return null;
    }
}
