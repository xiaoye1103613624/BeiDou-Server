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
 * 【技能常量】NightWalker（class），包 `org.gms.constants.skills`。
 *
 * 定义暗夜行者（皇家骑士团飞侠）职业的技能ID常量。
 *
 * @author 萧曵
 */
public class NightWalker {
    // 1st job
    /** 敏捷身躯：暗夜行者1转前的被动技能，提升敏捷和回避率 */
    public static final int NIMBLE_BODY = 14000000;
    /** 锐利之眼：暗夜行者1转前的被动技能，提升命中率 */
    public static final int KEEN_EYES = 14000001;
    /** 混乱：暗夜行者1转前的技能，降低怪物的攻击力和防御力 */
    public static final int DISORDER = 14000002;
    /** 暗影视觉：暗夜行者1转前的被动技能，隐身并提升移动速度 */
    public static final int DARK_SIGHT = 14001003;
    /** 幸运七：暗夜行者1转前的攻击技能 */
    public static final int LUCKY_SEVEN = 14001004;
    /** 黑暗：暗夜行者1转前的攻击技能 */
    public static final int DARKNESS = 14001005;
    // 2nd job
    /** 爪子精通：暗夜行者1转后的被动技能，提升爪子攻击力 */
    public static final int CLAW_MASTERY = 14100000;
    /** 暗器暴击：暗夜行者1转后的被动技能，提升暗器暴击率 */
    public static final int CRITICAL_THROW = 14100001;
    /** 爪子加速：暗夜行者1转后的被动技能，提升攻击速度 */
    public static final int CLAW_BOOSTER = 14101002;
    /** 急速：暗夜行者1转后的增益技能，提升移动速度 */
    public static final int HASTE = 14101003;
    /** 闪跃：暗夜行者1转后的移动技能，快速移动到指定位置 */
    public static final int FLASH_JUMP = 14101004;
    /** 消失：暗夜行者1转后的被动技能，提升闪避率 */
    public static final int VANISH = 14100005;
    /** 吸血鬼：暗夜行者1转后的技能，攻击时恢复HP */
    public static final int VAMPIRE = 14101006;
    // 3rd job
    /** 暗影伙伴：暗夜行者2转后的技能，召唤影子协助战斗 */
    public static final int SHADOW_PARTNER = 14111000;
    /** 暗影网：暗夜行者2转后的控制技能，限制怪物移动 */
    public static final int SHADOW_WEB = 14111001;
    /** 复仇者：暗夜行者2转后的被动技能，提升对异常状态怪物的伤害 */
    public static final int AVENGER = 14111002;
    /** 炼金术士：暗夜行者2转后的被动技能，提升技能效果 */
    public static final int ALCHEMIST = 14110003;
    /** 剧毒：暗夜行者2转后的被动技能，使武器带有毒性 */
    public static final int VENOM = 14110004;
    /** 三连镖：暗夜行者2转后的攻击技能 */
    public static final int TRIPLE_THROW = 14110005;
    /** 毒素炸弹：暗夜行者2转后的范围攻击技能 */
    public static final int POISON_BOMB = 14111006;
}