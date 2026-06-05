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
 * 【技能常量】Priest（class），包 `org.gms.constants.skills`。
 *
 * 定义祭司职业的技能ID常量。
 *
 * @author 萧曵
 */
public class Priest {
    /** 元素抵抗：祭司的被动技能，提升对异常状态的抗性 */
    public static final int ELEMENTAL_RESISTANCE = 2310000;
    /** 驱散：祭司的技能，移除队伍成员的异常状态 */
    public static final int DISPEL = 2311001;
    /** 神秘之门：祭司的技能，在地图上设置传送门 */
    public static final int MYSTIC_DOOR = 2311002;
    /** 神圣象征：祭司的增益技能，提升队伍的HP恢复速度 */
    public static final int HOLY_SYMBOL = 2311003;
    /** 闪光射线：祭司的攻击技能，发射光线攻击怪物 */
    public static final int SHINING_RAY = 2311004;
    /** 末日：祭司的技能，降低怪物的攻击力和防御力 */
    public static final int DOOM = 2311005;
    /** 召唤龙：祭司的召唤技能，召唤龙协助战斗 */
    public static final int SUMMON_DRAGON = 2311006;
}