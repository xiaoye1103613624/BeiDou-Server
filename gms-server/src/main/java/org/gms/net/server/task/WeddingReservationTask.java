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
 * @author Ronan
 */
public class WeddingReservationTask extends BaseTask implements Runnable {

    /**
     * 遍历当前世界的所有频道，依次处理大教堂和小教堂的婚礼预约。
     * <p>
     * 对每个频道分别获取下一个大教堂和小教堂的婚礼预约信息，
     * 若存在有效预约则启动对应的婚礼流程，否则清除该频道的婚礼状态。
     * </p>
     */
    @Override
    public void run() {
        for (Channel ch : wserv.getChannels()) {
            Pair<Boolean, Pair<Integer, Set<Integer>>> wedding;

            // 获取并启动大教堂（Cathedral）的下一场婚礼预约
            wedding = ch.getNextWeddingReservation(true);
            if (wedding != null) {
                ch.setOngoingWedding(true, wedding.getLeft(), wedding.getRight().getLeft(), wedding.getRight().getRight());
            } else {
                ch.setOngoingWedding(true, null, null, null);
            }

            // 获取并启动小教堂（Chapel）的下一场婚礼预约
            wedding = ch.getNextWeddingReservation(false);
            if (wedding != null) {
                ch.setOngoingWedding(false, wedding.getLeft(), wedding.getRight().getLeft(), wedding.getRight().getRight());
            } else {
                ch.setOngoingWedding(false, null, null, null);
            }
        }
    }

    public WeddingReservationTask(World world) {
        super(world);
    }
}
