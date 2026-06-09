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

package org.gms.server.events.gm;

/**
 * GM活动基类
 * 管理活动地图和参与人数限制
 *
 * @author kevintjuh93
 */
public class Event {
    /** 活动地图ID */
    private final int mapid;
    /** 参与人数限制 */
    private int limit;

    /**
     * 构造GM活动
     *
     * @param mapid 活动地图ID
     * @param limit 参与人数限制
     */
    public Event(int mapid, int limit) {
        this.mapid = mapid;
        this.limit = limit;
    }

    /**
     * 获取活动地图ID
     *
     * @return 地图ID
     */
    public int getMapId() {
        return mapid;
    }

    /**
     * 获取参与人数限制
     *
     * @return 人数限制
     */
    public int getLimit() {
        return limit;
    }

    /**
     * 减少参与人数限制
     */
    public void minusLimit() {
        this.limit--;
    }

    /**
     * 增加参与人数限制
     */
    public void addLimit() {
        this.limit++;
    }
}