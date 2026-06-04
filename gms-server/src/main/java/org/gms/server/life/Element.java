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
package org.gms.server.life;

/**
 * 【枚举】Element：定义元素类型常量。
 * <p>支持的元素包括：无属性、物理、火、冰、雷、毒、神圣、黑暗</p>
 */
public enum Element {
    NEUTRAL(0),      // 无属性
    PHYSICAL(1),     // 物理攻击
    FIRE(2, true),   // 火属性
    ICE(3, true),    // 冰属性
    LIGHTING(4),     // 雷属性
    POISON(5),       // 毒属性
    HOLY(6, true),   // 神圣属性
    DARKNESS(7);     // 黑暗属性

    /** 元素值 */
    private final int value;
    /** 是否为特殊元素（火/冰/神圣） */
    private boolean special = false;

    Element(int v) {
        this.value = v;
    }

    Element(int v, boolean special) {
        this.value = v;
        this.special = special;
    }

    /**
     * 判断是否为特殊元素
     * @return true=特殊元素, false=普通元素
     */
    public boolean isSpecial() {
        return special;
    }

    /**
     * 根据字符获取元素类型
     * @param c 元素字符标识（F=火, I=冰, L=雷, S=毒, H=神圣, D=黑暗, P=无属性）
     * @return 对应的元素枚举
     * @throws IllegalArgumentException 未知字符时抛出
     */
    public static Element getFromChar(char c) {
        switch (Character.toUpperCase(c)) {
            case 'F':
                return FIRE;
            case 'I':
                return ICE;
            case 'L':
                return LIGHTING;
            case 'S':
                return POISON;
            case 'H':
                return HOLY;
            case 'D':
                return DARKNESS;
            case 'P':
                return NEUTRAL;
        }
        throw new IllegalArgumentException("unknown elemnt char " + c);
    }

    /**
     * 获取元素值
     * @return 元素的数值标识
     */
    public int getValue() {
        return value;
    }
}