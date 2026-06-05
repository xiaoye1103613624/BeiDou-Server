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
 * 【技能常量】BlazeWizard（class），包 `org.gms.constants.skills`。
 *
 * 定义炎术士（皇家骑士团魔法师）职业的技能ID常量。
 *
 * @author 萧曵
 */
public class BlazeWizard {
    // 1st job
    /** 提升最大MP：炎术士1转前的被动技能，提升最大MP值 */
    public static final int INCREASING_MAX_MP = 12000000;
    /** 魔法护盾：炎术士1转前的被动技能，用MP抵消部分伤害 */
    public static final int MAGIC_GUARD = 12001001;
    /** 魔法装甲：炎术士1转前的被动技能，提升魔法防御力 */
    public static final int MAGIC_ARMOR = 12001002;
    /** 魔法爪：炎术士1转前的攻击技能 */
    public static final int MAGIC_CLAW = 12001003;
    /** 火焰：炎术士1转前的攻击技能 */
    public static final int FLAME = 12001004;
    // 2nd job
    /** 冥想：炎术士1转后的被动技能，提升魔法攻击力 */
    public static final int MEDITATION = 12101000;
    /** 缓速：炎术士1转后的控制技能，降低怪物速度 */
    public static final int SLOW = 12101001;
    /** 火箭：炎术士1转后的攻击技能 */
    public static final int FIRE_ARROW = 12101002;
    /** 传送：炎术士1转后的移动技能 */
    public static final int TELEPORT = 12101003;
    /** 魔法加速：炎术士1转后的增益技能，提升施法速度 */
    public static final int SPELL_BOOSTER = 12101004;
    /** 元素重置：炎术士1转后的技能，重置元素属性 */
    public static final int ELEMENTAL_RESET = 12101005;
    /** 火柱：炎术士1转后的范围攻击技能 */
    public static final int FIRE_PILLAR = 12101006;
    // 3rd job
    /** 元素抵抗：炎术士2转后的被动技能，提升对异常状态的抗性 */
    public static final int ELEMENTAL_RESISTANCE = 12110000;
    /** 元素增幅：炎术士2转后的被动技能，提升元素技能伤害 */
    public static final int ELEMENT_AMPLIFICATION = 12110001;
    /** 封印：炎术士2转后的控制技能，封印怪物行动 */
    public static final int SEAL = 12111002;
    /** 流星雨：炎术士2转后的范围攻击技能 */
    public static final int METEOR_SHOWER = 12111003;
    /** 伊弗利特：炎术士2转后的召唤技能，召唤火精灵伊弗利特 */
    public static final int IFRIT = 12111004;
    /** 焰齿轮：炎术士2转后的攻击技能 */
    public static final int FLAME_GEAR = 12111005;
    /** 火焰打击：炎术士2转后的攻击技能 */
    public static final int FIRE_STRIKE = 12111006;
}