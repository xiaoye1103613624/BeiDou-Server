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
 * 【技能常量】Archer（class），包 `org.gms.constants.skills`。
 *
 * 定义弓箭手职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Archer {
    /** 亚马逊的祝福：弓箭手1转前的被动技能，提升力量和敏捷 */
    public static final int BLESSING_OF_AMAZON = 3000000;
    /** 亚马逊之眼：弓箭手1转前的被动技能，提升命中率 */
    public static final int EYE_OF_AMAZON = 3000002;
    /** 致命射击：弓箭手1转前的被动技能，提升暴击率 */
    public static final int CRITICAL_SHOT = 3000001;
    /** 专注：弓箭手1转前的被动技能，提升回避率和命中率 */
    public static final int FOCUS = 3001003;
    /** 二连射：弓箭手1转前的攻击技能 */
    public static final int DOUBLE_SHOT = 3001005;
    /** 箭矢冲击：弓箭手1转前的攻击技能 */
    public static final int ARROW_BLOW = 3001004;
}