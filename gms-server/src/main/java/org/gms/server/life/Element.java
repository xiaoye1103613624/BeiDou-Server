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
 * 元素枚举类，表示不同的属性类型。
 * @author beidou
 */

public enum Element {
    /**
     * 中性元素，表示没有特定属性。
     */
    NEUTRAL(0),
    /**
     * 物理属性，表示没有特定元素效果。
     */
    PHYSICAL(1),
    /**
     * 火属性
     */
    FIRE(2, true),
    /**
     * 冰属性
     */
    ICE(3, true),
    /**
     * 闪电属性
     */
    LIGHTING(4),
    /**
     * 毒属性
     */
    POISON(5),
    /**
     * 神圣属性
     */
    HOLY(6, true),
    /**
     * 暗属性
     */
    DARKNESS(7);

    private final int value;
    private boolean special = false;

    Element(int v) {
        this.value = v;
    }

    Element(int v, boolean special) {
        this.value = v;
        this.special = special;
    }

    public boolean isSpecial() {
        return special;
    }

    /**
     * 根据字符获取对应的元素。
     *
     * @param c 用于确定元素的字符。
     * @return 对应的元素。
     * @throws IllegalArgumentException 如果传入的字符无法对应到任何元素，则抛出此异常。
     */
    public static Element getFromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'F' -> FIRE;
            case 'I' -> ICE;
            case 'L' -> LIGHTING;
            case 'S' -> POISON;
            case 'H' -> HOLY;
            case 'D' -> DARKNESS;
            case 'P' -> NEUTRAL;
            default -> throw new IllegalArgumentException("未知元素属性 " + c);
        };
    }

    public int getValue() {
        return value;
    }
}
