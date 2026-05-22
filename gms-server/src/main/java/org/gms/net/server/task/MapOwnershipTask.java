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

import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;

/**
 * 地图所有权检查任务。
 * <p>
 * 该任务定期遍历所有频道，执行地图所有权状态的检查与更新逻辑，
 * 确保地图资源的正确分配与管理。
 * </p>
 *
 * @author Ronan
 */
public class MapOwnershipTask extends BaseTask implements Runnable {

    @Override
    public void run() {
        for (Channel ch : wserv.getChannels()) {
            ch.runCheckOwnedMapsSchedule();
        }
    }

    public MapOwnershipTask(World world) {
        super(world);
    }
}
