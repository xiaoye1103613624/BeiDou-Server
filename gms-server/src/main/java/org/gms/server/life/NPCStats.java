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
package org.gms.server.life;

/**
 * NPC属性
 * 存储NPC的基础属性信息，如名称等
 *
 * @author Matze
 */
public class NPCStats {
    /** NPC名称 */
    private String name;

    /**
     * 构造NPC属性对象
     *
     * @param name NPC名称
     */
    public NPCStats(String name) {
        this.name = name;
    }

    /**
     * 获取NPC名称
     *
     * @return NPC名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置NPC名称
     *
     * @param name NPC名称
     */
    public void setName(String name) {
        this.name = name;
    }
}