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
 * 【技能常量】Page（class），包 `org.gms.constants.skills`。
 *
 * 定义准骑士职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Page {
    /** 剑精通：准骑士的被动技能，提升剑攻击力 */
    public static final int SWORD_MASTERY = 1200000;
    /** 钝器精通：准骑士的被动技能，提升钝器攻击力 */
    public static final int BW_MASTERY = 1200001;
    /** 最终攻击-剑：准骑士的被动技能，有一定几率进行额外攻击 */
    public static final int FINAL_ATTACK_SWORD = 1200002;
    /** 最终攻击-钝器：准骑士的被动技能，有一定几率进行额外攻击 */
    public static final int FINAL_ATTACK_BW = 1200003;
    /** 剑加速：准骑士的被动技能，提升攻击速度 */
    public static final int SWORD_BOOSTER = 1201004;
    /** 钝器加速：准骑士的被动技能，提升攻击速度 */
    public static final int BW_BOOSTER = 1201005;
    /** 威胁：准骑士的技能，降低怪物的攻击力和防御力 */
    public static final int THREATEN = 1201006;
    /** 力量守护：准骑士的技能，将部分伤害转换为MP */
    public static final int POWER_GUARD = 1201007;
}