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
 * 【枚举】SummonMovementType：定义召唤物移动类型常量。
 * 
 * <p>用于控制召唤物的移动行为模式。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义召唤物的移动类型</li>
 *   <li>提供移动类型的数值标识</li>
 * </ul>
 */
public enum SummonMovementType {
    /** 静止不动 */
    STATIONARY(0),     
    /** 跟随玩家 */
    FOLLOW(1),         
    /** 环绕跟随 */
    CIRCLE_FOLLOW(3);  

    /** 移动类型数值 */
    private final int val;

    /**
     * 构造函数：创建召唤物移动类型实例
     * 
     * @param val 类型值
     */
    SummonMovementType(int val) {
        this.val = val;
    }

    /**
     * 获取移动类型数值
     * 
     * @return 数值标识
     */
    public int getValue() {
        return val;
    }
}