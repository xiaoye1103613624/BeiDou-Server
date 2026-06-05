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
 * 【技能常量】Aran（class），包 `org.gms.constants.skills`。
 *
 * 定义战神职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Aran {
    // 1st job
    /** 连击能力：战神的基础连击技能 */
    public static final int COMBO_ABILITY = 21000000;
    /** 双重挥击：战神1转前的攻击技能 */
    public static final int DOUBLE_SWING = 21000002;
    /** 战斗步伐：战神1转前的移动技能 */
    public static final int COMBAT_STEP = 21001001;
    /** 长枪精通：战神1转前的被动技能，提升长枪攻击力 */
    public static final int POLEARM_BOOSTER = 21001003;
    // 2nd job
    /** 长枪精通：战神1转后的被动技能，提升长枪熟练度 */
    public static final int POLEARM_MASTERY = 21100000;
    /** 三重挥击：战神1转后的攻击技能 */
    public static final int TRIPLE_SWING = 21100001;
    /** 终极冲刺：战神1转后的攻击技能 */
    public static final int FINAL_CHARGE = 21100002;
    /** 连击吸收：战神1转后的被动技能，连击时吸收怪物HP */
    public static final int COMBO_DRAIN = 21100005;
    /** 连击碎击：战神1转后的攻击技能 */
    public static final int COMBO_SMASH = 21100004;
    /** 身体压迫：战神1转后的被动技能，提升防御力 */
    public static final int BODY_PRESSURE = 21101003;
    // 3rd job
    /** 全力挥击：战神2转后的攻击技能 */
    public static final int FULL_SWING = 21110002;
    /** 连击暴击：战神2转后的被动技能，提升暴击率 */
    public static final int COMBO_CRITICAL = 21110000;
    /** 终极投掷：战神2转后的攻击技能 */
    public static final int FINAL_TOSS = 21110003; // Final Toss seems to be missing from the handbook, sourced ID elsewhere
    /** 连击芬里尔：战神2转后的攻击技能 */
    public static final int COMBO_FENRIR = 21110004;
    /** 雪域冲锋：战神2转后的主动技能 */
    public static final int SNOW_CHARGE = 21111005;
    /** 智能击退：战神2转后的主动技能 */
    public static final int SMART_KNOCKBACK = 21111001;
    /** 滚动旋转：战神2转后的攻击技能 */
    public static final int ROLLING_SPIN = 21110006;
    /** 隐藏全力双重攻击：战神2转后的隐藏技能 */
    public static final int HIDDEN_FULL_DOUBLE = 21110007;
    /** 隐藏全力三重攻击：战神2转后的隐藏技能 */
    public static final int HIDDEN_FULL_TRIPLE = 21110008;
    // 4th job
    /** 枫叶战士：战神4转后的被动技能，全面提升属性 */
    public static final int MAPLE_WARRIOR = 21121000;
    /** 高级精通：战神4转后的被动技能，提升攻击力 */
    public static final int HIGH_MASTERY = 21120001;
    /** 超级挥击：战神4转后的攻击技能 */
    public static final int OVER_SWING = 21120002;
    /** 高级防御：战神4转后的被动技能，提升防御力 */
    public static final int HIGH_DEFENSE = 21120004;
    /** 终极一击：战神4转后的攻击技能 */
    public static final int FINAL_BLOW = 21120005;
    /** 连击风暴：战神4转后的攻击技能 */
    public static final int COMBO_TEMPEST = 21120006;
    /** 连击屏障：战神4转后的被动技能，减少受到的伤害 */
    public static final int COMBO_BARRIER = 21120007;
    /** 冰霜立场：战神4转后的被动技能，冰属性攻击 */
    public static final int FREEZE_STANDING = 21121003;
    /** 英雄意志：战神4转后的被动技能，清除异常状态 */
    public static final int HEROS_WILL = 21121008;
    /** 隐藏超级双重攻击：战神4转后的隐藏技能 */
    public static final int HIDDEN_OVER_DOUBLE = 21120009;
    /** 隐藏超级三重攻击：战神4转后的隐藏技能 */
    public static final int HIDDEN_OVER_TRIPLE = 21120010;
}