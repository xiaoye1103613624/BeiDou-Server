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
 * 【技能常量】Crusader（class），包 `org.gms.constants.skills`。
 *
 * 定义十字军职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Crusader {
    /** 改善HP/MP恢复：十字军的被动技能，提升HP/MP自然恢复速度 */
    public static final int IMPROVING_MPREC = 1110000;
    /** 盾牌精通：十字军的被动技能，提升盾牌格挡率 */
    public static final int SHIELD_MASTERY = 1110001;
    /** 连击：十字军的被动技能，通过攻击累积连击点数 */
    public static final int COMBO = 1111002;
    /** 剑恐慌：十字军的攻击技能，降低怪物的攻击力 */
    public static final int SWORD_PANIC = 1111003;
    /** 斧恐慌：十字军的攻击技能，降低怪物的攻击力 */
    public static final int AXE_PANIC = 1111004;
    /** 剑昏迷：十字军的攻击技能，有一定几率使怪物昏迷 */
    public static final int SWORD_COMA = 1111005;
    /** 斧昏迷：十字军的攻击技能，有一定几率使怪物昏迷 */
    public static final int AXE_COMA = 1111006;
    /** 护甲破坏：十字军的攻击技能，降低怪物的防御力 */
    public static final int ARMOR_CRASH = 1111007;
    /** 吼叫：十字军的技能，提升周围队友的攻击力 */
    public static final int SHOUT = 1111008;
}