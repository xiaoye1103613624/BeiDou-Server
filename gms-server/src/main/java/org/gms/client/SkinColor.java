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
package org.gms.client;

/**
 * 【枚举】SkinColor：定义角色肤色类型常量。
 * <p>支持多种肤色：普通、深色、黑色、苍白、蓝色、绿色、白色、粉色</p>
 */
public enum SkinColor {
    NORMAL(0),   // 普通肤色
    DARK(1),     // 深色肤色
    BLACK(2),    // 黑色肤色
    PALE(3),     // 苍白色肤色
    BLUE(4),     // 蓝色肤色
    GREEN(5),    // 绿色肤色
    WHITE(9),    // 白色肤色
    PINK(10);    // 粉色肤色

    /** 肤色ID */
    final int id;

    SkinColor(int id) {
        this.id = id;
    }

    /**
     * 获取肤色ID
     * @return 肤色ID
     */
    public int getId() {
        return id;
    }

    /**
     * 根据ID获取肤色枚举
     * @param id 肤色ID
     * @return 对应的肤色枚举，未找到返回null
     */
    public static SkinColor getById(int id) {
        for (SkinColor l : SkinColor.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }
}