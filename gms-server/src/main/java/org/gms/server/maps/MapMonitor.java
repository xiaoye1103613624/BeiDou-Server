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

import org.gms.server.TimerManager;

import java.util.concurrent.ScheduledFuture;

/**
 * 地图监控器
 * 监控地图是否有玩家，若超过5秒无玩家则自动清理怪物、掉落物和反应器，并重新开启传送门
 */
public class MapMonitor {
    /** 监控调度任务 */
    private ScheduledFuture<?> monitorSchedule;
    /** 被监控的地图 */
    private MapleMap map;
    /** 监控完成后重新开启的传送门 */
    private Portal portal;

    /**
     * 构造方法，启动5秒定时器检查地图是否有玩家
     *
     * @param map    被监控的地图
     * @param portal 监控完成后重新开启的传送门名称
     */
    public MapMonitor(final MapleMap map, String portal) {
        this.map = map;
        this.portal = map.getPortal(portal);
        this.monitorSchedule = TimerManager.getInstance().register(() -> {
            if (map.getCharacters().size() < 1) {
                cancelAction();
            }
        }, 5000);
    }

    /**
     * 取消监控，清理地图并恢复传送门状态
     * 清理包括：杀死所有怪物、清除掉落物、重置反应器、开启传送门
     */
    private void cancelAction() {
        if (monitorSchedule != null) {  // thanks Thora for pointing a NPE occurring here
            monitorSchedule.cancel(false);
            monitorSchedule = null;
        }

        map.killAllMonsters();
        map.clearDrops();
        if (portal != null) {
            portal.setPortalStatus(Portal.OPEN);
        }
        map.resetReactors();

        map = null;
        portal = null;
    }
}