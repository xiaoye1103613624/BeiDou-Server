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
package org.gms.server.maps;

/**
 * 地图对象类型枚举
 * 定义地图上所有可交互对象的类型，用于区分NPC、怪物、物品、玩家等
 * 在进行地图对象查询和范围搜索时使用
 */
public enum MapObjectType {
    /** NPC */
    NPC,
    /** 怪物 */
    MONSTER,
    /** 掉落物品 */
    ITEM,
    /** 玩家 */
    PLAYER,
    /** 传送门 */
    DOOR,
    /** 召唤兽 */
    SUMMON,
    /** 商店 */
    SHOP,
    /** 小游戏 */
    MINI_GAME,
    /** 迷雾 */
    MIST,
    /** 反应器 */
    REACTOR,
    /** 雇佣商人 */
    HIRED_MERCHANT,
    /** 玩家NPC */
    PLAYER_NPC,
    /** 龙 */
    DRAGON,
    /** 风筝 */
    KITE
}