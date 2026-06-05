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

import org.gms.provider.Data;
import org.gms.provider.DataTool;

import java.awt.*;

/**
 * 【工厂/提供者】PortalFactory：创建或提供 `maps` 相关运行时对象。
 * 
 * <p>传送门工厂类，负责从数据文件创建和初始化各种类型的传送门对象。
 * 工厂模式的实现，用于创建传送门实例并根据数据文件加载其属性。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>根据类型创建传送门实例</li>
 *   <li>从数据文件加载传送门属性</li>
 *   <li>管理传送门ID分配</li>
 * </ul>
 */
public class PortalFactory {
    /** 下一个门类传送门的ID */
    private int nextDoorPortal;

    /**
     * 构造函数：创建传送门工厂实例
     * 
     * <p>初始化传送门ID计数器为0x80（128）。</p>
     */
    public PortalFactory() {
        nextDoorPortal = 0x80;
    }

    /**
     * 创建传送门实例
     * 
     * <p>根据指定的类型创建相应的传送门实例，并从数据文件加载其属性。</p>
     * 
     * @param type 传送门类型
     * @param portal 传送门数据
     * @return 创建的传送门实例
     */
    public Portal makePortal(int type, Data portal) {
        GenericPortal ret = null;
        if (type == Portal.MAP_PORTAL) {
            ret = new MapPortal();
        } else {
            ret = new GenericPortal(type);
        }
        loadPortal(ret, portal);
        return ret;
    }

    /**
     * 从数据文件加载传送门属性
     * 
     * <p>根据数据文件中的信息设置传送门的各种属性，包括名称、目标、位置、脚本等。</p>
     * 
     * @param myPortal 要加载属性的传送门对象
     * @param portal 传送门数据
     */
    private void loadPortal(GenericPortal myPortal, Data portal) {
        myPortal.setName(DataTool.getString(portal.getChildByPath("pn")));
        myPortal.setTarget(DataTool.getString(portal.getChildByPath("tn")));
        myPortal.setTargetMapId(DataTool.getInt(portal.getChildByPath("tm")));
        int x = DataTool.getInt(portal.getChildByPath("x"));
        int y = DataTool.getInt(portal.getChildByPath("y"));
        myPortal.setPosition(new Point(x, y));
        String script = DataTool.getString("script", portal, null);
        if (script != null && script.equals("")) {
            script = null;
        }
        myPortal.setScriptName(script);
        if (myPortal.getType() == Portal.DOOR_PORTAL) {
            myPortal.setId(nextDoorPortal);
            nextDoorPortal++;
        } else {
            myPortal.setId(Integer.parseInt(portal.getName()));
        }
    }
}