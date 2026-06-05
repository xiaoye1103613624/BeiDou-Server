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
 * 【技能常量】DawnWarrior（class），包 `org.gms.constants.skills`。
 *
 * 定义魂骑士（皇家骑士团战士）职业的技能ID常量。
 *
 * @author 萧曵
 */
public class DawnWarrior {
    // 1st job
    /** 最大HP提升：魂骑士1转前的被动技能，提升最大HP值 */
    public static final int MAX_HP_INCREASE = 11000000;
    /** 钢铁之身：魂骑士1转前的被动技能，提升物理防御力 */
    public static final int IRON_BODY = 11001001;
    /** 力量攻击：魂骑士1转前的攻击技能 */
    public static final int POWER_STRIKE = 11001002;
    /** 斩裂冲击：魂骑士1转前的攻击技能 */
    public static final int SLASH_BLAST = 11001003;
    /** 灵气：魂骑士1转前的被动技能，提升攻击力 */
    public static final int SOUL = 11001004;
    // 2nd job
    /** 剑精通：魂骑士1转后的被动技能，提升剑攻击力 */
    public static final int SWORD_MASTERY = 11100000;
    /** 剑加速：魂骑士1转后的被动技能，提升攻击速度 */
    public static final int SWORD_BOOSTER = 11101001;
    /** 最终攻击：魂骑士1转后的被动技能，有一定几率进行额外攻击 */
    public static final int FINAL_ATTACK = 11101002;
    /** 愤怒：魂骑士1转后的增益技能，提升攻击力和防御力 */
    public static final int RAGE = 11101003;
    /** 灵魂之刃：魂骑士1转后的攻击技能 */
    public static final int SOUL_BLADE = 11101004;
    /** 灵魂冲撞：魂骑士1转后的攻击技能 */
    public static final int SOUL_RUSH = 11101005;
    // 3rd job
    /** MP恢复提升：魂骑士2转后的被动技能，提升MP自然恢复速度 */
    public static final int INCREASED_MP_RECOVERY = 11110000;
    /** 连击：魂骑士2转后的被动技能，通过攻击累积连击点数 */
    public static final int COMBO = 11111001;
    /** 恐慌：魂骑士2转后的攻击技能，降低怪物的攻击力 */
    public static final int PANIC = 11111002;
    /** 昏迷：魂骑士2转后的攻击技能，有一定几率使怪物昏迷 */
    public static final int COMA = 11111003;
    /** 挥斩：魂骑士2转后的范围攻击技能 */
    public static final int BRANDISH = 11111004;
    /** 高级连击：魂骑士2转后的被动技能，提升连击系统效果 */
    public static final int ADVANCED_COMBO = 11110005;
    /** 灵魂驱动：魂骑士2转后的攻击技能 */
    public static final int SOUL_DRIVER = 11111006;
    /** 灵魂充能：魂骑士2转后的增益技能，提升攻击力 */
    public static final int SOUL_CHARGE = 11111007;
}