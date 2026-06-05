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
 * 【枚举】SavedLocationType：定义保存位置类型常量。
 * 
 * <p>用于标识玩家保存的各种传送位置类型。
 * 游戏系统使用这些类型来区分玩家在不同情况下保存的位置，
 * 如死亡后复活、使用特定传送服务等。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义各种保存位置的类型</li>
 *   <li>支持从字符串获取对应的枚举值</li>
 * </ul>
 */
public enum SavedLocationType {
    /** 自由市场 */
    FREE_MARKET,      
    /** 世界旅游 */
    WORLDTOUR,        
    /** 弗洛里娜海滩 */
    FLORINA,          
    /** 新手引导 */
    INTRO,            
    /** 周日市场 */
    SUNDAY_MARKET,    
    /** 镜子世界 */
    MIRROR,           
    /** 活动地图 */
    EVENT,            
    /** BOSS组队任务 */
    BOSSPQ,           
    /** 幸福村 */
    HAPPYVILLE,       
    /** 怪物嘉年华 */
    MONSTER_CARNIVAL, 
    /** 开发者区域 */
    DEVELOPER,        
    /** 监狱 */
    JAIL;             

    /**
     * 根据字符串获取保存位置类型
     * 
     * <p>根据提供的字符串名称获取对应的枚举值。
     * 字符串必须与枚举常量的名称完全匹配（大小写敏感）。</p>
     * 
     * @param str 类型名称字符串
     * @return 对应的枚举值
     */
    public static SavedLocationType fromString(String str) {
        return valueOf(str);
    }
}