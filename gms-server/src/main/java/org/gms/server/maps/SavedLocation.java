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
 * 【类型】SavedLocation（class），包 `org.gms.server.maps`。
 * 
 * <p>保存位置数据类，记录玩家保存的传送位置（地图ID和传送门）。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>存储玩家保存的地图位置</li>
 *   <li>提供对地图ID和传送门的访问</li>
 *   <li>不可变数据结构</li>
 * </ul>
 */
public class SavedLocation {
    /** 地图ID */
    private final int mapId;
    /** 传送门索引 */
    private final int portal;

    /**
     * 构造函数：创建保存位置实例
     * 
     * @param mapId 地图ID
     * @param portal 传送门索引
     */
    public SavedLocation(int mapId, int portal) {
        this.mapId = mapId;
        this.portal = portal;
    }

    /**
     * 获取地图ID
     * 
     * @return 地图ID
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * 获取传送门索引
     * 
     * @return 传送门索引
     */
    public int getPortal() {
        return portal;
    }
}