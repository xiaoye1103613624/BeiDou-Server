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
package org.gms.net.server.task;

import org.gms.net.server.world.World;

/**
 * 组队搜索定时任务
 * 定期更新组队搜索列表，清理过期队伍信息
 *
 * @author Ronan
 */
public class PartySearchTask extends BaseTask implements Runnable {

    @Override
    public void run() {
        wserv.runPartySearchUpdateSchedule();
    }

    /**
     * 构造组队搜索定时任务
     *
     * @param world 关联的世界实例
     */
    public PartySearchTask(World world) {
        super(world);
    }
}