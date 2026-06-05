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
 * 【技能常量】Crossbowman（class），包 `org.gms.constants.skills`。
 *
 * 定义弩弓手职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Crossbowman {
    /** 弩精通：弩弓手的被动技能，提升弩攻击力 */
    public static final int CROSSBOW_MASTERY = 3200000;
    /** 最终攻击：弩弓手的被动技能，有一定几率进行额外攻击 */
    public static final int FINAL_ATTACK = 3200001;
    /** 弩加速：弩弓手的被动技能，提升攻击速度 */
    public static final int CROSSBOW_BOOSTER = 3201002;
    /** 力量击退：弩弓手的攻击技能，击退怪物 */
    public static final int POWER_KNOCKBACK = 3201003;
    /** 灵魂箭：弩弓手的技能，无需消耗箭矢 */
    public static final int SOUL_ARROW = 3201004;
    /** 铁箭：弩弓手的攻击技能 */
    public static final int IRON_ARROW = 3201005;
}