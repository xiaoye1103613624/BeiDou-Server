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
 * 传送门工厂
 * 从WZ数据中加载地图传送门，支持多种传送门类型
 */
public class PortalFactory {
    /** 下一个神秘之门传送门ID，从0x80开始分配 */
    private int nextDoorPortal;

    public PortalFactory() {
        nextDoorPortal = 0x80;
    }

    /**
     * 根据类型创建传送门并从WZ数据加载属性
     *
     * @param type   传送门类型
     * @param portal WZ数据节点
     * @return 创建的传送门
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
     * 从WZ数据中加载传送门属性
     * 包括名称、目标、坐标、脚本名称等，DOOR_PORTAL类型使用自增ID
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