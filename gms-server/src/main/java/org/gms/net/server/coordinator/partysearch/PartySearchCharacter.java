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
package org.gms.net.server.coordinator.partysearch;

import org.gms.client.Character;

import java.lang.ref.WeakReference;

/**
 * 队伍搜索角色
 * 封装参与队伍搜索的玩家角色信息
 */
public class PartySearchCharacter {

    /** 弱引用持有的玩家角色，避免阻止GC回收 */
    private final WeakReference<Character> player;
    /** 玩家等级 */
    private final int level;
    /** 是否仍在排队中 */
    private boolean queued;

    public PartySearchCharacter(Character chr) {
        player = new WeakReference(chr);
        level = chr.getLevel();
        queued = true;
    }

    /**
     * 构造字符串描述
     *
     * @return 角色描述字符串
     */
    @Override
    public String toString() {
        Character chr = player.get();
        return chr == null ? "[empty]" : chr.toString();
    }

    /**
     * 尝试呼叫此玩家
     * 检查玩家是否在线、是否在地图附近、未被屏蔽且未加入队伍
     *
     * @param leaderid    领袖角色ID
     * @param callerMapid 呼叫的地图ID
     * @return 可用的角色，否则返回null
     */
    public Character callPlayer(int leaderid, int callerMapid) {
        Character chr = player.get();
        if (chr == null || !PartySearchCoordinator.isInVicinity(callerMapid, chr.getMapId())) {
            return null;
        }

        if (chr.getDisabledPartySearchInvites().contains(leaderid)) {
            return null;
        }

        queued = false;
        if (chr.isLoggedInWorld() && chr.getParty() == null) {
            return chr;
        } else {
            return null;
        }
    }

    public Character getPlayer() {
        return player.get();
    }

    public int getLevel() {
        return level;
    }

    public boolean isQueued() {
        return queued;
    }

}