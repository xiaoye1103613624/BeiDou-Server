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
 * 【技能常量】WhiteKnight（class），包 `org.gms.constants.skills`。
 *
 * 定义骑士职业的技能ID常量。
 *
 * @author 萧曵
 */
public class WhiteKnight {
    /** 改善MP恢复：骑士的被动技能，提升MP自然恢复速度 */
    public static final int IMPROVING_MP_RECOVERY = 1210000;
    /** 盾牌精通：骑士的被动技能，提升盾牌格挡率 */
    public static final int SHIELD_MASTERY = 1210001;
    /** 充能打击：骑士的攻击技能，附带元素属性的强力攻击 */
    public static final int CHARGE_BLOW = 1211002;
    /** 剑火属性攻击：骑士的攻击技能，使用剑时附加火属性 */
    public static final int SWORD_FIRE_CHARGE = 1211003;
    /** 钝器火属性攻击：骑士的攻击技能，使用钝器时附加火属性 */
    public static final int BW_FIRE_CHARGE = 1211004;
    /** 剑冰属性攻击：骑士的攻击技能，使用剑时附加冰属性 */
    public static final int SWORD_ICE_CHARGE = 1211005;
    /** 钝器冰属性攻击：骑士的攻击技能，使用钝器时附加冰属性 */
    public static final int BW_ICE_CHARGE = 1211006;
    /** 剑雷属性攻击：骑士的攻击技能，使用剑时附加雷属性 */
    public static final int SWORD_LIT_CHARGE = 1211007;
    /** 钝器雷属性攻击：骑士的攻击技能，使用钝器时附加雷属性 */
    public static final int BW_LIT_CHARGE = 1211008;
    /** 魔法冲击：骑士的技能，有一定几率使怪物进入异常状态 */
    public static final int MAGIC_CRASH = 1211009;
}