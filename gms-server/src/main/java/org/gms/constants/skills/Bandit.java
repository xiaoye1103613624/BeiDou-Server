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
 * 【技能常量】Bandit（class），包 `org.gms.constants.skills`。
 *
 * 定义侠客职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Bandit {
    /** 匕首精通：侠客的被动技能，提升匕首攻击力 */
    public static final int DAGGER_MASTERY = 4200000;
    /** 忍耐：侠客的被动技能，提升物理防御力 */
    public static final int ENDURE = 4200001;
    /** 匕首加速：侠客的被动技能，提升攻击速度 */
    public static final int DAGGER_BOOSTER = 4201002;
    /** 急速：侠客的增益技能，提升移动速度 */
    public static final int HASTE = 4201003;
    /** 盗取：侠客的技能，有一定几率盗取怪物身上的物品 */
    public static final int STEAL = 4201004;
    /** 残暴打击：侠客的攻击技能 */
    public static final int SAVAGE_BLOW = 4201005;
}