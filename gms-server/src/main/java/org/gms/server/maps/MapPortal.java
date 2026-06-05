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
 * 【类型】MapPortal（class），包 {@code org.gms.server.maps}。
 * 
 * <p>地图传送门类，继承自GenericPortal，专门用于处理同一地图内的传送点功能，允许玩家在地图内不同位置间传送。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>表示地图内的传送门节点</li>
 *   <li>处理地图内的位置跳转</li>
 *   <li>提供地图内导航功能</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * MapPortal portal = new MapPortal();
 * portal.setPosition(x, y);
 * portal.setTargetMapId(targetMapId);
 * portal.setTargetPortal(targetPortalName);
 * }</pre>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class MapPortal extends GenericPortal {

    /**
     * 构造函数：创建地图传送门实例
     * 
     * <p>初始化传送门类型为MAP_PORTAL，设置基本的传送门属性。</p>
     */
    public MapPortal() {
        super(Portal.MAP_PORTAL);
    }
}