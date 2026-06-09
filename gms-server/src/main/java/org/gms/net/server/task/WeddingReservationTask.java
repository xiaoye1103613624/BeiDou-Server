/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2017 RonanLana

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
import org.gms.util.Pair;

import java.util.Set;

/**
 * 婚礼预约定时任务
 * 定期检查并启动婚礼预约，分别处理教堂和小礼拜堂的婚礼
 *
 * @author Ronan
 */
public class WeddingReservationTask extends BaseTask implements Runnable {

    @Override
    public void run() {
        for (Channel ch : wserv.getChannels()) {
            Pair<Boolean, Pair<Integer, Set<Integer>>> wedding;

            wedding = ch.getNextWeddingReservation(true);   // start cathedral
            if (wedding != null) {
                ch.setOngoingWedding(true, wedding.getLeft(), wedding.getRight().getLeft(), wedding.getRight().getRight());
            } else {
                ch.setOngoingWedding(true, null, null, null);
            }

            wedding = ch.getNextWeddingReservation(false);  // start chapel
            if (wedding != null) {
                ch.setOngoingWedding(false, wedding.getLeft(), wedding.getRight().getLeft(), wedding.getRight().getRight());
            } else {
                ch.setOngoingWedding(false, null, null, null);
            }
        }
    }

    /**
     * 构造婚礼预约定时任务
     *
     * @param world 关联的世界实例
     */
    public WeddingReservationTask(World world) {
        super(world);
    }
}