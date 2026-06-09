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

import org.gms.client.Character;
import org.gms.net.server.Server;
import org.gms.server.TimerManager;
import org.gms.util.PacketCreator;

import java.util.List;

/**
 * 冒险岛TV效果
 * 管理全服TV广播效果，包括婚礼、装备强化等消息，每个世界同时只能有一个TV在播放
 *
 * @author MrXotic (XoticStory)
 * @author Ronan - made MapleTV mechanics synchronous
 */
public class MapleTVEffect {

    /** 每个世界的TV激活状态，数组索引对应世界ID */
    private final static boolean[] ACTIVE = new boolean[Server.getInstance().getWorldsSize()];

    /**
     * 如果当前没有TV在播放则广播TV消息
     *
     * @param player   发送者
     * @param victim   接收者（如婚礼对象）
     * @param messages 消息列表
     * @param tvType   TV类型（1-3为普通，4为30秒，5为60秒）
     * @return 是否成功广播
     */
    public static synchronized boolean broadcastMapleTVIfNotActive(Character player, Character victim, List<String> messages, int tvType) {
        int w = player.getWorld();
        if (!ACTIVE[w]) {
            broadcastTV(true, w, messages, player, tvType, victim);
            return true;
        }

        return false;
    }

    /**
     * 广播TV消息
     * 根据类型设置不同的延迟时间后自动关闭TV
     */
    private static synchronized void broadcastTV(boolean activity, final int userWorld, List<String> message, Character user, int type, Character partner) {
        Server server = Server.getInstance();
        ACTIVE[userWorld] = activity;
        if (activity) {
            server.broadcastMessage(userWorld, PacketCreator.enableTV());
            server.broadcastMessage(userWorld, PacketCreator.sendTV(user, message, type <= 2 ? type : type - 3, partner));
            int delay = 15000;
            if (type == 4) {
                delay = 30000;
            } else if (type == 5) {
                delay = 60000;
            }
            TimerManager.getInstance().schedule(() -> broadcastTV(false, userWorld, null, null, -1, null), delay);
        } else {
            server.broadcastMessage(userWorld, PacketCreator.removeTV());
        }
    }
}