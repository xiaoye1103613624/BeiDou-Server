/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.net.server.services.task.channel;

import org.gms.config.GameConfig;
import org.gms.net.server.services.BaseScheduler;
import org.gms.net.server.services.BaseService;

/**
 * 事件调度服务
 * 管理频道的游戏事件延迟任务调度，通过分片避免锁竞争
 *
 * @author Ronan
 */
public class EventService extends BaseService {

    /** 事件调度器数组，按频道分片 */
    private final EventScheduler[] eventSchedulers = new EventScheduler[GameConfig.getServerInt("channel_locks")];

    public EventService() {
        for (int i = 0; i < GameConfig.getServerInt("channel_locks"); i++) {
            eventSchedulers[i] = new EventScheduler();
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < GameConfig.getServerInt("channel_locks"); i++) {
            if (eventSchedulers[i] != null) {
                eventSchedulers[i].dispose();
                eventSchedulers[i] = null;
            }
        }
    }

    /**
     * 注册事件延迟操作
     *
     * @param mapid     地图ID
     * @param runAction 执行逻辑
     * @param delay     延迟时间
     */
    public void registerEventAction(int mapid, Runnable runAction, long delay) {
        eventSchedulers[getChannelSchedulerIndex(mapid)].registerDelayedAction(runAction, delay);
    }

    /**
     * 事件调度器
     * 负责事件延迟任务的注册
     */
    private class EventScheduler extends BaseScheduler {

        /**
         * 注册延迟操作
         *
         * @param runAction 执行逻辑
         * @param delay     延迟时间
         */
        public void registerDelayedAction(Runnable runAction, long delay) {
            registerEntry(runAction, runAction, delay);
        }

    }

}