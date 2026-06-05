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
 * 【类型】MapMonitor（class），包 `org.gms.server.maps`。
 * 
 * <p>地图监控器类，用于监控地图状态并在地图上没有玩家时执行清理操作。
 * 当地图上没有玩家时，监控器会自动清理怪物、掉落物，并重置反应器状态。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>监控地图上的玩家数量</li>
 *   <li>在地图无人时清理怪物和掉落物</li>
 *   <li>重置地图反应器状态</li>
 *   <li>管理指定传送门的状态</li>
 * </ul>
 */
public class MapMonitor {
    /** 监控任务调度器 */
    private ScheduledFuture<?> monitorSchedule;
    /** 被监控的地图 */
    private MapleMap map;
    /** 被管理的传送门 */
    private Portal portal;

    /**
     * 构造函数：创建地图监控器实例
     * 
     * <p>创建一个地图监控器，每5秒检查一次地图上是否有玩家。
     * 如果地图上没有玩家，则执行清理操作。</p>
     * 
     * @param map 要监控的地图
     * @param portalName 要管理的传送门名称
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
     * 执行取消操作
     * 
     * <p>当地图上没有玩家时执行此方法，执行以下清理操作：</p>
     * <ul>
     *   <li>取消监控任务调度器</li>
     *   <li>清除地图上所有怪物</li>
     *   <li>清除地图上所有掉落物</li>
     *   <li>打开指定传送门（如果存在）</li>
     *   <li>重置地图反应器状态</li>
     *   <li>释放地图和传送门引用</li>
     * </ul>
     * 
     * <p>注意：此方法由监控任务自动调用，无需手动调用。</p>
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