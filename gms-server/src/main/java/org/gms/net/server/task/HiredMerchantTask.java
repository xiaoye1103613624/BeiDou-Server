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
 * 雇佣商人定时任务
 * 定期检查雇佣商人的到期和维护状态
 *
 * @author Ronan
 */
public class HiredMerchantTask extends BaseTask implements Runnable {

    @Override
    public void run() {
        wserv.runHiredMerchantSchedule();
    }

    /**
     * 构造雇佣商人定时任务
     *
     * @param world 关联的世界实例
     */
    public HiredMerchantTask(World world) {
        super(world);
    }
}