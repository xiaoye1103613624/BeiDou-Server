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
 * 召唤兽移动类型枚举
 * 定义召唤兽跟随玩家的移动方式
 */
public enum SummonMovementType {
    /** 静止不动 */
    STATIONARY(0),
    /** 跟随玩家 */
    FOLLOW(1),
    /** 环绕玩家 */
    CIRCLE_FOLLOW(3);

    /** 移动类型编号 */
    private final int val;

    SummonMovementType(int val) {
        this.val = val;
    }

    /**
     * 获取移动类型编号
     *
     * @return 编号
     */
    public int getValue() {
        return val;
    }
}