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
 * 保存位置类型枚举
 * 定义玩家可保存的位置类型，用于回城、传送等场景
 * 每种类型对应一个特定的保存位置，供玩家在不同场景间切换
 */
public enum SavedLocationType {
    /** 自由市场 */
    FREE_MARKET,
    /** 世界旅行 */
    WORLDTOUR,
    /** 弗洛里纳海滩 */
    FLORINA,
    /** 教程入口 */
    INTRO,
    /** 周日市场 */
    SUNDAY_MARKET,
    /** 镜像 */
    MIRROR,
    /** 活动 */
    EVENT,
    /** BOSS组队 */
    BOSSPQ,
    /** 幸福村 */
    HAPPYVILLE,
    /** 怪物嘉年华 */
    MONSTER_CARNIVAL,
    /** 开发者 */
    DEVELOPER,
    /** 监狱 */
    JAIL;

    /**
     * 从字符串解析保存位置类型
     *
     * @param Str 类型字符串
     * @return 对应的枚举值
     */
    public static SavedLocationType fromString(String Str) {
        return valueOf(Str);
    }
}