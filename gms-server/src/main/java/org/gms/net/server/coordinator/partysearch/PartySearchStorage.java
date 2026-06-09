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
import org.gms.util.IntervalBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 队伍搜索存储
 * 按等级梯队存储正在搜索队伍的玩家，提供查询和管理功能
 */
public class PartySearchStorage {

    /** 按等级排序的搜索角色列表 */
    private final List<PartySearchCharacter> storage = new ArrayList<>(20);
    /** 空区间记录，用于快速判断等级区间是否无匹配玩家 */
    private final IntervalBuilder emptyIntervals = new IntervalBuilder();

    /** 存储读锁 */
    private final Lock psRLock;
    /** 存储写锁 */
    private final Lock psWLock;

    public PartySearchStorage() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.psRLock = readWriteLock.readLock();
        this.psWLock = readWriteLock.writeLock();
    }

    /**
     * 获取存储列表的副本（线程安全）
     *
     * @return 存储列表副本
     */
    public List<PartySearchCharacter> getStorageList() {
        psRLock.lock();
        try {
            return new ArrayList<>(storage);
        } finally {
            psRLock.unlock();
        }
    }

    /**
     * 获取仍在排队中的玩家
     *
     * @return 角色ID到角色的映射
     */
    private Map<Integer, Character> fetchRemainingPlayers() {
        List<PartySearchCharacter> players = getStorageList();
        Map<Integer, Character> remainingPlayers = new HashMap<>(players.size());

        for (PartySearchCharacter psc : players) {
            if (psc.isQueued()) {
                Character chr = psc.getPlayer();
                if (chr != null) {
                    remainingPlayers.put(chr.getId(), chr);
                }
            }
        }

        return remainingPlayers;
    }

    /**
     * 更新存储
     * 将梯队中的玩家与当前排队玩家合并后重新排序
     *
     * @param echelon 新加入的玩家集合
     */
    public void updateStorage(Collection<Character> echelon) {
        Map<Integer, Character> newcomers = new HashMap<>();
        for (Character chr : echelon) {
            newcomers.put(chr.getId(), chr);
        }

        Map<Integer, Character> curStorage = fetchRemainingPlayers();
        curStorage.putAll(newcomers);

        List<PartySearchCharacter> pscList = new ArrayList<>(curStorage.size());
        for (Character chr : curStorage.values()) {
            pscList.add(new PartySearchCharacter(chr));
        }

        pscList.sort((c1, c2) -> {
            int levelP1 = c1.getLevel(), levelP2 = c2.getLevel();
            return levelP1 > levelP2 ? 1 : (levelP1 == levelP2 ? 0 : -1);
        });

        psWLock.lock();
        try {
            storage.clear();
            storage.addAll(pscList);
        } finally {
            psWLock.unlock();
        }

        emptyIntervals.clear();
    }

    /**
     * 二分查找，找到存储列表中等级小于等于给定值的最大索引
     *
     * @param storage 已排序的存储列表
     * @param level   目标等级
     * @return 等级小于等于目标值的最大索引
     */
    private static int bsearchStorage(List<PartySearchCharacter> storage, int level) {
        int st = 0, en = storage.size() - 1;

        int mid, idx;
        while (en >= st) {
            idx = (st + en) / 2;
            mid = storage.get(idx).getLevel();

            if (mid == level) {
                return idx;
            } else if (mid < level) {
                st = idx + 1;
            } else {
                en = idx - 1;
            }
        }

        return en;
    }

    /**
     * 查找并呼叫等级范围内的匹配玩家
     *
     * @param callerCid   呼叫的领袖角色ID
     * @param callerMapid 呼叫的地图ID
     * @param minLevel    最小等级
     * @param maxLevel    最大等级
     * @return 匹配的玩家，未找到则返回null
     */
    public Character callPlayer(int callerCid, int callerMapid, int minLevel, int maxLevel) {
        if (emptyIntervals.inInterval(minLevel, maxLevel)) {
            return null;
        }

        List<PartySearchCharacter> pscList = getStorageList();

        int idx = bsearchStorage(pscList, maxLevel);
        for (int i = idx; i >= 0; i--) {
            PartySearchCharacter psc = pscList.get(i);
            if (!psc.isQueued()) {
                continue;
            }

            if (psc.getLevel() < minLevel) {
                break;
            }

            Character chr = psc.callPlayer(callerCid, callerMapid);
            if (chr != null) {
                return chr;
            }
        }

        emptyIntervals.addInterval(minLevel, maxLevel);
        return null;
    }

    /**
     * 从存储中移除玩家
     *
     * @param chr 要移除的玩家
     */
    public void detachPlayer(Character chr) {
        PartySearchCharacter toRemove = null;
        for (PartySearchCharacter psc : getStorageList()) {
            Character player = psc.getPlayer();

            if (player != null && player.getId() == chr.getId()) {
                toRemove = psc;
                break;
            }
        }

        if (toRemove != null) {
            psWLock.lock();
            try {
                storage.remove(toRemove);
            } finally {
                psWLock.unlock();
            }
        }
    }

}