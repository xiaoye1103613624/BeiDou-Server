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
 * 【枚举】ElementalEffectiveness：定义元素克制关系的效果等级。
 * <p>用于表示怪物对特定元素攻击的抗性程度：</p>
 * <ul>
 *   <li>NORMAL: 普通（正常伤害）</li>
 *   <li>IMMUNE: 免疫（伤害为0）</li>
 *   <li>STRONG: 强效（造成额外伤害）</li>
 *   <li>WEAK: 弱点（受到额外伤害）</li>
 *   <li>NEUTRAL: 中立（无特殊效果）</li>
 * </ul>
 */
public enum ElementalEffectiveness {
    NORMAL,  // 普通
    IMMUNE,  // 免疫
    STRONG,  // 强效
    WEAK,    // 弱点
    NEUTRAL; // 中立

    /**
     * 根据数字编码获取元素效果等级
     * @param num 数字编码（1=IMMUNE, 2=STRONG, 3=WEAK, 4=NEUTRAL）
     * @return 对应的元素效果枚举
     * @throws IllegalArgumentException 未知编码时抛出
     */
    public static ElementalEffectiveness getByNumber(int num) {
        switch (num) {
            case 1:
                return IMMUNE;
            case 2:
                return STRONG;
            case 3:
                return WEAK;
            case 4:
                return NEUTRAL;
            default:
                throw new IllegalArgumentException("Unkown effectiveness: " + num);
        }
    }
}