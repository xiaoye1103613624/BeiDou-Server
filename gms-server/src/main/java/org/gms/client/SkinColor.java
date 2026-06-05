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
 * 【枚举】SkinColor，包 {@code org.gms.client}。
 * 定义角色肤色类型常量。
 *
 * <p>支持多种肤色类型：普通、深色、黑色、苍白、蓝色、绿色、白色、粉色等。
 * 肤色ID从0开始编号，不同ID对应不同的肤色颜色。</p>
 *
 * @see Character#setSkinColor(int)
 */
public enum SkinColor {
    /** 普通肤色 - 标准的角色默认肤色 */
    NORMAL(0),
    /** 深色肤色 - 较深的棕色皮肤 */
    DARK(1),
    /** 黑色肤色 - 深黑色皮肤 */
    BLACK(2),
    /** 苍白色肤色 - 苍白的皮肤色调 */
    PALE(3),
    /** 蓝色肤色 - 蓝色系的特殊肤色 */
    BLUE(4),
    /** 绿色肤色 - 绿色系的特殊肤色 */
    GREEN(5),
    /** 白色肤色 - 白色系的特殊肤色 */
    WHITE(9),
    /** 粉色肤色 - 粉色系的特殊肤色 */
    PINK(10);

    /** 肤色ID，用于客户端和服务器端通信 */
    final int id;

    /**
     * 构造函数，初始化肤色ID
     * @param id 肤色编号
     */
    SkinColor(int id) {
        this.id = id;
    }

    /**
     * 获取肤色ID
     * @return 肤色ID值
     */
    public int getId() {
        return id;
    }

    /**
     * 根据ID获取对应的肤色枚举
     * @param id 肤色ID
     * @return 对应的肤色枚举，未找到返回null
     */
    public static SkinColor getById(int id) {
        // 遍历所有肤色枚举值，查找匹配的ID
        for (SkinColor l : SkinColor.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        // 未找到匹配的肤色，返回null
        return null;
    }
}