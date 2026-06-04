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
 * 【枚举】MapObjectType：定义地图对象类型常量。
 * <p>用于标识地图上各类对象的类型，便于统一管理和处理</p>
 */
public enum MapObjectType {
    NPC,              // NPC
    MONSTER,          // 怪物
    ITEM,             // 掉落物品
    PLAYER,           // 玩家
    DOOR,             // 门
    SUMMON,           // 召唤物
    SHOP,             // 商店
    MINI_GAME,        // 小游戏
    MIST,             // 毒雾/迷雾
    REACTOR,          // 反应物（可交互的地图元素）
    HIRED_MERCHANT,   // 雇佣商人
    PLAYER_NPC,       // 玩家NPC（玩家创建的NPC）
    DRAGON,           // 龙（龙神职业的龙）
    KITE              // 风筝（弓箭手技能）
}