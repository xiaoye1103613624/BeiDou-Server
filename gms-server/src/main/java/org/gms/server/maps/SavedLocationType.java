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
 * <p>用于标识玩家保存的各种传送位置类型</p>
 */
public enum SavedLocationType {
    FREE_MARKET,      // 自由市场
    WORLDTOUR,        // 世界旅游
    FLORINA,          // 弗洛里娜海滩
    INTRO,            // 新手引导
    SUNDAY_MARKET,    // 周日市场
    MIRROR,           // 镜子世界
    EVENT,            // 活动地图
    BOSSPQ,           // BOSS组队任务
    HAPPYVILLE,       // 幸福村
    MONSTER_CARNIVAL, // 怪物嘉年华
    DEVELOPER,        // 开发者区域
    JAIL;             // 监狱

    /**
     * 根据字符串获取保存位置类型
     * @param Str 类型名称字符串
     * @return 对应的枚举值
     */
    public static SavedLocationType fromString(String Str) {
        return valueOf(Str);
    }
}