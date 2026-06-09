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

import org.gms.net.server.Server;

/**
 * 角色异常状态广播定时任务
 * 定期广播地图内玩家的异常状态（如中毒、虚弱等），确保新进入地图的玩家能看到其他玩家的状态效果
 *
 * @author Ronan
 */
public class CharacterDiseaseTask implements Runnable {
    @Override
    public void run() {
        Server serv = Server.getInstance();

        serv.updateCurrentTime();
        serv.runAnnouncePlayerDiseasesSchedule();
    }
}