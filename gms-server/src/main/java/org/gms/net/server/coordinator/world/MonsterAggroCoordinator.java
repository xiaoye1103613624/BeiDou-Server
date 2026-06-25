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
package org.gms.net.server.coordinator.world;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.gms.server.TimerManager;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 怪物仇恨协调器
 * 管理怪物仇恨列表，控制怪物对玩家的目标切换频率和优先级
 */
public class MonsterAggroCoordinator {
    /** 可重入锁 */
    private final Lock lock = new ReentrantLock();
    /** 空闲状态锁，公平锁 */
    private final Lock idleLock = new ReentrantLock(true);
    /** 上次停止时间 */
    private long lastStopTime = Server.getInstance().getCurrentTime();

    /** 仇恨监控定时器 */
    private ScheduledFuture<?> aggroMonitor = null;

    /** 怪物仇恨条目表，key为怪物，value为玩家ID到仇恨条目的映射 */
    private final Map<Monster, Map<Integer, PlayerAggroEntry>> mobAggroEntries = new HashMap<>();
    /** 怪物排序仇恨表，key为怪物，value为排序后的仇恨条目列表 */
    private final Map<Monster, List<PlayerAggroEntry>> mobSortedAggros = new HashMap<>();

    /** 傀儡地图条目集合 */
    private final Set<Integer> mapPuppetEntries = new HashSet<>();

    /**
     * 玩家仇恨条目
     */
    private class PlayerAggroEntry {
        /** 角色ID */
        protected int cid;
        /** 平均伤害 */
        protected int averageDamage = 0;
        /** 当前伤害实例数 */
        protected int currentDamageInstances = 0;
        /** 累计伤害 */
        protected long accumulatedDamage = 0;

        /** 过期计数 */
        protected int expireStreak = 0;
        /** 更新计数 */
        protected int updateStreak = 0;
        /** 到达下次更新的计数 */
        protected int toNextUpdate = 0;
        /** 排名 */
        protected int entryRank = -1;

        protected PlayerAggroEntry(int cid) {
            this.cid = cid;
        }
    }

    public void stopAggroCoordinator() {
        idleLock.lock();
        try {
            if (aggroMonitor == null) {
                return;
            }

            aggroMonitor.cancel(false);
            aggroMonitor = null;
        } finally {
            idleLock.unlock();
        }

        lastStopTime = Server.getInstance().getCurrentTime();
    }

    public void startAggroCoordinator() {
        idleLock.lock();
        try {
            if (aggroMonitor != null) {
                return;
            }

            aggroMonitor = TimerManager.getInstance().register(() -> {
                runAggroUpdate(1);
                runSortLeadingCharactersAggro();
            }, GameConfig.getServerLong("mob_status_aggro_interval"), GameConfig.getServerLong("mob_status_aggro_interval"));
        } finally {
            idleLock.unlock();
        }

        int timeDelta = (int) Math.ceil((Server.getInstance().getCurrentTime() - lastStopTime) / GameConfig.getServerDouble("mob_status_aggro_interval"));
        if (timeDelta > 0) {
            runAggroUpdate(timeDelta);
        }
    }

    private static void updateEntryExpiration(PlayerAggroEntry pae) {
        pae.toNextUpdate = (int) Math.ceil((120000L / GameConfig.getServerDouble("mob_status_aggro_interval")) / Math.pow(2, pae.expireStreak + pae.currentDamageInstances));
    }

    private static void insertEntryDamage(PlayerAggroEntry pae, long damage) {
        synchronized (pae) {
            long totalDamage = pae.averageDamage;
            totalDamage *= pae.currentDamageInstances;
            totalDamage += damage;

            pae.expireStreak = 0;
            pae.updateStreak = 0;
            updateEntryExpiration(pae);

            pae.currentDamageInstances += 1;
            pae.averageDamage = (int) (totalDamage / pae.currentDamageInstances);
            pae.accumulatedDamage = totalDamage;
        }
    }

    private static boolean expiredAfterUpdateEntryDamage(PlayerAggroEntry pae, int deltaTime) {
        synchronized (pae) {
            pae.updateStreak += 1;
            pae.toNextUpdate -= deltaTime;

            if (pae.toNextUpdate <= 0) {    // reached dmg instance expire time
                pae.expireStreak += 1;
                updateEntryExpiration(pae);

                pae.currentDamageInstances -= 1;
                if (pae.currentDamageInstances < 1) {   // expired aggro for this player
                    return true;
                }
                pae.accumulatedDamage = pae.averageDamage * pae.currentDamageInstances;
            }

            return false;
        }
    }

    /**
     * 在指定时间内累计怪物伤害
     * 自动创建新的仇恨条目或激活已有条目
     *
     * @param mob    怪物
     * @param cid    角色ID
     * @param damage 本次造成的伤害
     */
    public void addAggroDamage(Monster mob, int cid, long damage) { // assumption: should not trigger after dispose()
        if (!mob.isAlive()) {
            return;
        }

        List<PlayerAggroEntry> sortedAggro = mobSortedAggros.get(mob);
        Map<Integer, PlayerAggroEntry> mobAggro = mobAggroEntries.get(mob);
        if (mobAggro == null) {
            if (lock.tryLock()) {   // can run unreliably, as fast as possible... try lock that is!
                try {
                    mobAggro = mobAggroEntries.get(mob);
                    if (mobAggro == null) {
                        mobAggro = new HashMap<>();
                        mobAggroEntries.put(mob, mobAggro);

                        sortedAggro = new LinkedList<>();
                        mobSortedAggros.put(mob, sortedAggro);
                    } else {
                        sortedAggro = mobSortedAggros.get(mob);
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                return;
            }
        }

        PlayerAggroEntry aggroEntry = mobAggro.get(cid);
        if (aggroEntry == null) {
            aggroEntry = new PlayerAggroEntry(cid);

            synchronized (mobAggro) {
                synchronized (sortedAggro) {
                    PlayerAggroEntry mappedEntry = mobAggro.get(cid);

                    if (mappedEntry == null) {
                        mobAggro.put(aggroEntry.cid, aggroEntry);
                        sortedAggro.add(aggroEntry);
                    } else {
                        aggroEntry = mappedEntry;
                    }
                }
            }
        } else if (damage < 1) {
            return;
        }

        insertEntryDamage(aggroEntry, damage);
    }

    /**
     * 执行一次仇恨更新
     * 更新所有怪物仇恨条目的过期状态
     *
     * @param deltaTime 时间增量
     */
    private void runAggroUpdate(int deltaTime) {
        List<Pair<Monster, Map<Integer, PlayerAggroEntry>>> aggroMobs = new LinkedList<>();
        lock.lock();
        try {
            for (Entry<Monster, Map<Integer, PlayerAggroEntry>> e : mobAggroEntries.entrySet()) {
                aggroMobs.add(new Pair<>(e.getKey(), e.getValue()));
            }
        } finally {
            lock.unlock();
        }

        for (Pair<Monster, Map<Integer, PlayerAggroEntry>> am : aggroMobs) {
            Map<Integer, PlayerAggroEntry> mobAggro = am.getRight();
            List<PlayerAggroEntry> sortedAggro = mobSortedAggros.get(am.getLeft());

            if (sortedAggro != null) {
                List<Integer> toRemove = new LinkedList<>();
                List<Integer> toRemoveIdx = new ArrayList<>(mobAggro.size());
                List<Integer> toRemoveByFetch = new LinkedList<>();

                synchronized (mobAggro) {
                    synchronized (sortedAggro) {
                        for (PlayerAggroEntry pae : mobAggro.values()) {
                            if (expiredAfterUpdateEntryDamage(pae, deltaTime)) {
                                toRemove.add(pae.cid);
                                if (pae.entryRank > -1) {
                                    toRemoveIdx.add(pae.entryRank);
                                } else {
                                    toRemoveByFetch.add(pae.cid);
                                }
                            }
                        }

                        if (!toRemove.isEmpty()) {
                            for (Integer cid : toRemove) {
                                mobAggro.remove(cid);
                            }

                            if (mobAggro.isEmpty()) {   // all aggro on this mob expired
                                if (!am.getLeft().isBoss()) {
                                    am.getLeft().aggroResetAggro();
                                }
                            }
                        }

                        if (!toRemoveIdx.isEmpty()) {
                            // last to first indexes
                            toRemoveIdx.sort((p1, p2) -> p1 < p2 ? 1 : p1.equals(p2) ? 0 : -1);

                            for (int idx : toRemoveIdx) {
                                sortedAggro.remove(idx);
                            }
                        }

                        if (!toRemoveByFetch.isEmpty()) {
                            for (Integer cid : toRemoveByFetch) {
                                for (int i = 0; i < sortedAggro.size(); i++) {
                                    if (cid.equals(sortedAggro.get(i).cid)) {
                                        sortedAggro.remove(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 使用插入排序按累计伤害对仇恨列表排序
     * 排序后为降序，伤害最高的在前面
     *
     * @param paeList 仇恨条目列表
     */
    private static void insertionSortAggroList(List<PlayerAggroEntry> paeList) {
        for (int i = 1; i < paeList.size(); i++) {
            PlayerAggroEntry pae = paeList.get(i);
            long curAccDmg = pae.accumulatedDamage;

            int j = i - 1;
            while (j >= 0 && curAccDmg > paeList.get(j).accumulatedDamage) {
                j -= 1;
            }

            j += 1;
            if (j != i) {
                paeList.remove(i);
                paeList.add(j, pae);
            }
        }

        int i = 0;
        for (PlayerAggroEntry pae : paeList) {
            pae.entryRank = i;
            i += 1;
        }
    }

    /**
     * 判断玩家是否是怪物的当前仇恨目标
     * 基于准排序的agro列表检查玩家是否可以被选为下一个仇恨领袖
     *
     * @param mob    怪物
     * @param player 玩家
     * @return 是否为首要仇恨目标
     */
    public boolean isLeadingCharacterAggro(Monster mob, Character player) {
        if (mob.isLeadingPuppetInVicinity()) {
            return false;
        } else if (mob.isCharacterPuppetInVicinity(player)) {
            return true;
        }

        // by assuming the quasi-sorted nature of "mobAggroList", this method
        // returns whether the player given as parameter can be elected as next aggro leader

        List<PlayerAggroEntry> mobAggroList = mobSortedAggros.get(mob);
        if (mobAggroList != null) {
            synchronized (mobAggroList) {
                mobAggroList = new ArrayList<>(mobAggroList.subList(0, Math.min(mobAggroList.size(), 5)));
            }

            MapleMap map = mob.getMap();
            for (PlayerAggroEntry pae : mobAggroList) {
                Character chr = map.getCharacterById(pae.cid);
                if (chr != null) {
                    if (player.getId() == pae.cid) {
                        return true;
                    } else if (pae.updateStreak < GameConfig.getServerInt("mob_status_aggro_persistence") && chr.isAlive()) {  // verifies currently leading players activity
                        return false;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 对所有怪物的仇恨列表执行排序
     */
    public void runSortLeadingCharactersAggro() {
        List<List<PlayerAggroEntry>> aggroList;
        lock.lock();
        try {
            aggroList = new ArrayList<>(mobSortedAggros.values());
        } finally {
            lock.unlock();
        }

        for (List<PlayerAggroEntry> mobAggroList : aggroList) {
            synchronized (mobAggroList) {
                insertionSortAggroList(mobAggroList);
            }
        }
    }

    /**
     * 移除怪物的所有仇恨记录
     *
     * @param mob 已死亡的怪物
     */
    public void removeAggroEntries(Monster mob) {
        lock.lock();
        try {
            mobAggroEntries.remove(mob);
            mobSortedAggros.remove(mob);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加傀儡仇恨记录
     *
     * @param player 召唤傀儡的玩家
     */
    public void addPuppetAggro(Character player) {
        synchronized (mapPuppetEntries) {
            mapPuppetEntries.add(player.getId());
        }
    }

    /**
     * 移除傀儡仇恨记录
     *
     * @param cid 角色ID
     */
    public void removePuppetAggro(Integer cid) {
        synchronized (mapPuppetEntries) {
            mapPuppetEntries.remove(cid);
        }
    }

    /**
     * 获取所有傀儡仇恨列表
     *
     * @return 傀儡仇恨角色ID列表的副本
     */
    public List<Integer> getPuppetAggroList() {
        synchronized (mapPuppetEntries) {
            return new ArrayList<>(mapPuppetEntries);
        }
    }

    /**
     * 销毁仇恨协调器，清理所有资源
     */
    public void dispose() {
        stopAggroCoordinator();

        lock.lock();
        try {
            mobAggroEntries.clear();
            mobSortedAggros.clear();
        } finally {
            lock.unlock();
        }
    }
}