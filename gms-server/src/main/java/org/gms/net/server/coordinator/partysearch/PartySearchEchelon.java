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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 队伍搜索梯队
 * 按等级区间划分的队伍搜索分组，管理该梯队内的搜索角色
 */
public class PartySearchEchelon {
    /** 读锁 */
    private final Lock psRLock;
    /** 写锁 */
    private final Lock psWLock;

    /** 梯队中的玩家，key为角色ID，使用弱引用避免阻止GC */
    private final Map<Integer, WeakReference<Character>> echelon = new HashMap<>(20);

    public PartySearchEchelon() {
        ReadWriteLock partySearchLock = new ReentrantReadWriteLock(true);
        this.psRLock = partySearchLock.readLock();
        this.psWLock = partySearchLock.writeLock();
    }

    /**
     * 导出梯队中所有在线玩家并清空梯队
     * 使用写锁（reverse locking策略）以允许精度换性能的折中
     *
     * @return 在线玩家列表
     */
    public List<Character> exportEchelon() {
        psWLock.lock();     // reversing read/write actually could provide a lax yet sure performance/precision trade-off here
        try {
            List<Character> players = new ArrayList<>(echelon.size());

            for (WeakReference<Character> chrRef : echelon.values()) {
                Character chr = chrRef.get();
                if (chr != null) {
                    players.add(chr);
                }
            }

            echelon.clear();
            return players;
        } finally {
            psWLock.unlock();
        }
    }

    /**
     * 将玩家添加到梯队中
     *
     * @param chr 玩家角色
     */
    public void attachPlayer(Character chr) {
        psRLock.lock();
        try {
            echelon.put(chr.getId(), new WeakReference<>(chr));
        } finally {
            psRLock.unlock();
        }
    }

    /**
     * 从梯队中移除玩家
     *
     * @param chr 玩家角色
     * @return 是否成功移除
     */
    public boolean detachPlayer(Character chr) {
        psRLock.lock();
        try {
            return echelon.remove(chr.getId()) != null;
        } finally {
            psRLock.unlock();
        }
    }

}