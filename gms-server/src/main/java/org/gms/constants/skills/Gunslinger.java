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
 * 【技能常量】Gunslinger（class），包 `org.gms.constants.skills`。
 *
 * 定义枪手职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Gunslinger {
    /** 枪械精通：枪手的被动技能，提升枪械攻击力 */
    public static final int GUN_MASTERY = 5200000;
    /** 无形射击：枪手的攻击技能，无需瞄准直接射击 */
    public static final int INVISIBLE_SHOT = 5201001;
    /** 手榴弹：枪手的范围攻击技能 */
    public static final int GRENADE = 5201002;
    /** 枪械加速：枪手的被动技能，提升攻击速度 */
    public static final int GUN_BOOSTER = 5201003;
    /** 空包弹：枪手的技能，用于触发某些机制 */
    public static final int BLANK_SHOT = 5201004;
    /** 翼化：枪手的被动技能，提升移动速度和跳跃力 */
    public static final int WINGS = 5201005;
    /** 反冲射击：枪手的攻击技能，向后跳并射击 */
    public static final int RECOIL_SHOT = 5201006;
}