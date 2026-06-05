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
 * 【技能常量】Cleric（class），包 `org.gms.constants.skills`。
 *
 * 定义牧师职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Cleric {
    /** MP吸收：牧师1转前的技能，吸收怪物的MP */
    public static final int MP_EATER = 2300000;
    /** 治疗：牧师1转前的技能，恢复自身或队友的HP */
    public static final int HEAL = 2301002;
    /** 无敌：牧师1转前的被动技能，提升物理和魔法防御力 */
    public static final int INVINCIBLE = 2301003;
    /** 传送：牧师1转前的移动技能 */
    public static final int TELEPORT = 2301001;
    /** 祝福：牧师1转前的被动技能，提升魔法攻击力 */
    public static final int BLESS = 2301004;
    /** 神圣箭：牧师1转前的攻击技能 */
    public static final int HOLY_ARROW = 2301005;
}