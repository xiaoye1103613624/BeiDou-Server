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
 * 皮肤颜色枚举
 * 定义角色的8种肤色选项，使用整数ID标识
 */
public enum SkinColor {
    /** 普通 */
    NORMAL(0),
    /** 暗色 */
    DARK(1),
    /** 黑色 */
    BLACK(2),
    /** 苍白 */
    PALE(3),
    /** 蓝色 */
    BLUE(4),
    /** 绿色 */
    GREEN(5),
    /** 白色 */
    WHITE(9),
    /** 粉色 */
    PINK(10);

    final int id;

    SkinColor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SkinColor getById(int id) {
        for (SkinColor l : SkinColor.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }
}