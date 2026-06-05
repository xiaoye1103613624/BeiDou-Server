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
 * 
 * <p>用于标识地图上各类对象的类型，便于统一管理和处理不同类型的地图对象。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>分类管理地图上的各种对象</li>
 *   <li>为不同类型对象提供类型标识</li>
 *   <li>支持对象类型的快速判断和处理</li>
 * </ul>
 * 
 * <p>包含以下对象类型：</p>
 * <ul>
 *   <li>NPC：非玩家角色</li>
 *   <li>MONSTER：怪物</li>
 *   <li>ITEM：掉落物品</li>
 *   <li>PLAYER：玩家角色</li>
 *   <li>DOOR：门</li>
 *   <li>SUMMON：召唤物</li>
 *   <li>SHOP：商店</li>
 *   <li>MINI_GAME：小游戏</li>
 *   <li>MIST：毒雾/迷雾</li>
 *   <li>REACTOR：反应器（可交互的地图元素）</li>
 *   <li>HIRED_MERCHANT：雇佣商人</li>
 *   <li>PLAYER_NPC：玩家NPC（玩家创建的NPC）</li>
 *   <li>DRAGON：龙（龙神职业的龙）</li>
 *   <li>KITE：风筝（弓箭手技能）</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public enum MapObjectType {
    /** 非玩家角色 */
    NPC,              
    /** 怪物 */
    MONSTER,          
    /** 掉落物品 */
    ITEM,             
    /** 玩家 */
    PLAYER,           
    /** 门 */
    DOOR,             
    /** 召唤物 */
    SUMMON,           
    /** 商店 */
    SHOP,             
    /** 小游戏 */
    MINI_GAME,        
    /** 毒雾/迷雾 */
    MIST,             
    /** 反应器（可交互的地图元素） */
    REACTOR,          
    /** 雇佣商人 */
    HIRED_MERCHANT,   
    /** 玩家NPC（玩家创建的NPC） */
    PLAYER_NPC,       
    /** 龙（龙神职业的龙） */
    DRAGON,           
    /** 风筝（弓箭手技能） */
    KITE              
}