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

/*
 * MapleTVEffect
 * @author MrXotic (XoticStory)
 * @author Ronan - made MapleTV mechanics synchronous
 */
/**
 * 【类型】MapleTVEffect（class），包 `org.gms.server.maps`。
 * 
 * <p>枫叶电视效果类，用于在游戏中播放全服广播的特殊效果。
 * 枫叶TV是一种特殊的全服广播功能，可以在游戏中显示玩家信息和消息，
 * 通常用于庆祝特殊事件或展示玩家成就。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理枫叶TV的激活状态</li>
 *   <li>处理TV效果的播放和停止</li>
 *   <li>支持不同类型和持续时间的TV效果</li>
 * </ul>
 */
public class MapleTVEffect {

    /** 每个世界的TV激活状态数组 */
    private final static boolean[] ACTIVE = new boolean[Server.getInstance().getWorldsSize()];

    /**
     * 如果TV未激活则广播枫叶TV效果
     * 
     * <p>检查指定世界中的TV是否处于激活状态，如果未激活则播放TV效果。
     * 此方法用于确保同一时间只有一个TV效果在播放。</p>
     * 
     * @param player TV发起者
     * @param victim TV涉及的另一个玩家（如果有的话）
     * @param messages 要显示的消息列表
     * @param tvType TV类型（决定显示样式和持续时间）
     * @return 如果成功激活TV则返回true，否则返回false
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
     * 广播TV效果
     * 
     * <p>播放或停止TV效果，根据activity参数决定是启用还是禁用TV。</p>
     * 
     * @param activity true为启用TV，false为停止TV
     * @param userWorld 用户所在的世界
     * @param message 要显示的消息列表
     * @param user TV发起者
     * @param type TV类型
     * @param partner TV涉及的另一个玩家
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