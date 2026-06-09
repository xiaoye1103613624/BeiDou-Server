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
 * 使用位掩码标识角色各属性，如等级、力量、敏捷、HP等
 * 用于网络包中标识哪些属性发生了变更
 */
public enum Stat {
    /** 皮肤 */
    SKIN(0x1),
    /** 脸型 */
    FACE(0x2),
    /** 发型 */
    HAIR(0x4),
    /** 等级 */
    LEVEL(0x10),
    /** 职业 */
    JOB(0x20),
    /** 力量 */
    STR(0x40),
    /** 敏捷 */
    DEX(0x80),
    /** 智力 */
    INT(0x100),
    /** 运气 */
    LUK(0x200),
    /** 当前HP */
    HP(0x400),
    /** 最大HP */
    MAXHP(0x800),
    /** 当前MP */
    MP(0x1000),
    /** 最大MP */
    MAXMP(0x2000),
    /** 可用属性点 */
    AVAILABLEAP(0x4000),
    /** 可用技能点 */
    AVAILABLESP(0x8000),
    /** 经验值 */
    EXP(0x10000),
    /** 人气 */
    FAME(0x20000),
    /** 金币 */
    MESO(0x40000),
    /** 宠物 */
    PET(0x180008),
    /** 扭蛋经验 */
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

    public static Stat getByString(String type) {
    	for (Stat stat : Stat.values()) {
            if (stat.name().equals(type)) {
                return stat;
            }
        }
        return null;
    }
}