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
 * 元素属性枚举
 * 定义冒险岛中的8种元素类型：无属性、物理、火、冰、雷、毒、圣、暗
 * 与{@link ElementalEffectiveness}配合使用，决定怪物对不同元素攻击的抵抗效果
 *
 * @author OdinMS Team
 */
public enum Element {
    /** 无属性 */
    NEUTRAL(0),
    /** 物理属性 */
    PHYSICAL(1),
    /** 火属性（特殊元素） */
    FIRE(2, true),
    /** 冰属性（特殊元素） */
    ICE(3, true),
    /** 雷属性 */
    LIGHTING(4),
    /** 毒属性 */
    POISON(5),
    /** 圣属性（特殊元素） */
    HOLY(6, true),
    /** 暗属性 */
    DARKNESS(7);

    /** 元素编号 */
    private final int value;
    /** 是否特殊元素（火/冰/圣有特殊效果） */
    private boolean special = false;

    Element(int v) {
        this.value = v;
    }

    Element(int v, boolean special) {
        this.value = v;
        this.special = special;
    }

    /**
     * 是否为特殊元素（火/冰/圣）
     *
     * @return true表示特殊元素
     */
    public boolean isSpecial() {
        return special;
    }

    /**
     * 根据字符查找对应元素类型
     *
     * @param c 元素字符（F=火, I=冰, L=雷, S=毒, H=圣, D=暗, P=物理）
     * @return 对应的Element枚举值
     * @throws IllegalArgumentException 当字符无效时
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
     * 获取元素编号
     *
     * @return 元素编号（0-7）
     */
    public int getValue() {
        return value;
    }
}