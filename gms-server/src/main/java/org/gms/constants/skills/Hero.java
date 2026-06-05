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
package org.gms.constants.skills;

/**
 * 【技能常量】Hero（class），包 `org.gms.constants.skills`。
 *
 * 定义英雄职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Hero {
    /** 枫叶战士：英雄职业的终极技能，大幅提升各项属性 */
    public static final int MAPLE_WARRIOR = 1121000;
    /** 怪物磁铁：吸引周围怪物的技能 */
    public static final int MONSTER_MAGNET = 1121001;
    /** 稳如泰山：提升物理和魔法防御力的被动技能 */
    public static final int STANCE = 1121002;
    /** 高级连击：提升连击系统的被动技能 */
    public static final int ADVANCED_COMBO = 1120003;
    /** 阿基里斯：减少受到暴击伤害的被动技能 */
    public static final int ACHILLES = 1120004;
    /** 守护者：减少受到伤害的被动技能 */
    public static final int GUARDIAN = 1120005;
    /** 冲锋：向前冲锋并对怪物造成伤害的技能 */
    public static final int RUSH = 1121006;
    /** 激怒：提升攻击力和暴击率的主动技能 */
    public static final int ENRAGE = 1121010;
    /** 英雄意志：恢复HP/MP并移除异常状态的终极技能 */
    public static final int HEROS_WILL = 1121011;
    /** 挥斩：向前方大范围攻击的技能 */
    public static final int BRANDISH = 1121008;
}