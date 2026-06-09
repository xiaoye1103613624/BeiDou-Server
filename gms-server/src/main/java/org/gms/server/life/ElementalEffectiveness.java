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
 * 元素抗性效果枚举
 * 定义怪物对元素攻击的抵抗程度，与{@link Element}配合使用
 * 从WZ文件的elemAttr属性中解析，每两个字符为一组（元素+抗性等级）
 *
 * @author OdinMS Team
 */
public enum ElementalEffectiveness {
    /** 正常效果 */
    NORMAL,
    /** 免疫 */
    IMMUNE,
    /** 强抵抗 */
    STRONG,
    /** 弱抵抗 */
    WEAK,
    /** 中立 */
    NEUTRAL;

    /**
     * 根据编号获取对应的抗性效果
     *
     * @param num 抗性编号（1=免疫, 2=强抵抗, 3=弱抵抗, 4=中立）
     * @return 对应的抗性效果
     * @throws IllegalArgumentException 当编号无效时
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