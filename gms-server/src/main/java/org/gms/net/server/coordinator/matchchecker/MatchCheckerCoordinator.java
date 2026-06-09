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
package org.gms.net.server.coordinator.matchchecker;

import lombok.Getter;
import org.gms.client.Character;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import org.gms.net.server.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 匹配检查协调器
 * 管理玩家匹配检查的生命周期，用于CPQ挑战和公会创建等需要双人确认的场景
 */
public class MatchCheckerCoordinator {

    /** 匹配条目表，key为匹配ID */
    private final Map<Integer, MatchCheckingElement> matchEntries = new HashMap<>();

    /** 已池化的角色ID集合 */
    private final Set<Integer> pooledCids = new HashSet<>();
    /** 信号量池，限制并发匹配数 */
    private final Semaphore semaphorePool = new Semaphore(7);

    /**
     * 匹配检查条目
     */
    private class MatchCheckingEntry {
        private boolean accepted;
        private final int cid;

        private MatchCheckingEntry(int cid) {
            this.cid = cid;
            this.accepted = false;
        }

        private boolean setAccept() {
            if (!this.accepted) {
                this.accepted = true;
                return true;
            } else {
                return false;
            }
        }

        private boolean getAccept() {
            return this.accepted;
        }
    }

    /**
     * 匹配检查元素
     * 封装一次匹配操作的所有信息
     */
    private class MatchCheckingElement {
        private final int leaderCid;
        private final int world;

        private final MatchCheckerType matchType;
        private final AbstractMatchCheckerListener listener;

        private final Map<Integer, MatchCheckingEntry> confirmingMembers = new HashMap<>();
        private int confirmCount;
        private boolean active = true;

        private final String message;
        @Getter
        private final long beginTime = System.currentTimeMillis();

        private MatchCheckingElement(MatchCheckerType matchType, int leaderCid, int world, AbstractMatchCheckerListener leaderListener, Set<Integer> matchPlayers, String message) {
            this.leaderCid = leaderCid;
            this.world = world;
            this.listener = leaderListener;
            this.confirmCount = 0;
            this.message = message;
            this.matchType = matchType;

            for (Integer cid : matchPlayers) {
                MatchCheckingEntry mmcEntry = new MatchCheckingEntry(cid);
                confirmingMembers.put(cid, mmcEntry);
            }
        }

        private boolean acceptEntry(int cid) {
            MatchCheckingEntry mmcEntry = confirmingMembers.get(cid);
            if (mmcEntry != null) {
                if (mmcEntry.setAccept()) {
                    this.confirmCount++;

                    return this.confirmCount == this.confirmingMembers.size();
                }
            }

            return false;
        }

        private boolean isMatchActive() {
            return active;
        }

        private void setMatchActive(boolean a) {
            active = a;
        }

        private Set<Integer> getMatchPlayers() {
            return confirmingMembers.keySet();
        }

        private Set<Integer> getAcceptedMatchPlayers() {
            Set<Integer> s = new HashSet<>();

            for (Entry<Integer, MatchCheckingEntry> e : confirmingMembers.entrySet()) {
                if (e.getValue().getAccept()) {
                    s.add(e.getKey());
                }
            }

            return s;
        }

        private Set<Character> getMatchCharacters() {
            Set<Character> players = new HashSet<>();

            World wserv = Server.getInstance().getWorld(world);
            if (wserv != null) {
                PlayerStorage ps = wserv.getPlayerStorage();

                for (Integer cid : getMatchPlayers()) {
                    Character chr = ps.getCharacterById(cid);
                    if (chr != null) {
                        players.add(chr);
                    }
                }
            }

            return players;
        }

        private void dispatchMatchCreated() {
            Set<Character> nonLeaderMatchPlayers = getMatchCharacters();
            Character leader = null;

            for (Character chr : nonLeaderMatchPlayers) {
                if (chr.getId() == leaderCid) {
                    leader = chr;
                    break;
                }
            }

            nonLeaderMatchPlayers.remove(leader);
            listener.onMatchCreated(leader, nonLeaderMatchPlayers, message);
        }

        private void dispatchMatchResult(boolean accept) {
            if (accept) {
                listener.onMatchAccepted(leaderCid, getMatchCharacters(), message);
            } else {
                listener.onMatchDeclined(leaderCid, getMatchCharacters(), message);
            }
        }

        private void dispatchMatchDismissed() {
            listener.onMatchDismissed(leaderCid, getMatchCharacters(), message);
        }
    }

    /**
     * 从匹配池中释放单个玩家
     */
    private void unpoolMatchPlayer(Integer cid) {
        unpoolMatchPlayers(Collections.singleton(cid));
    }

    private void unpoolMatchPlayers(Set<Integer> matchPlayers) {
        for (Integer cid : matchPlayers) {
            pooledCids.remove(cid);
        }
    }

    private boolean poolMatchPlayer(Integer cid) {
        return poolMatchPlayers(Collections.singleton(cid));
    }

    private boolean poolMatchPlayers(Set<Integer> matchPlayers) {
        Set<Integer> pooledPlayers = new HashSet<>();

        for (Integer cid : matchPlayers) {
            if (!pooledCids.add(cid)) {
                unpoolMatchPlayers(pooledPlayers);
                return false;
            } else {
                pooledPlayers.add(cid);
            }
        }

        return true;
    }

    private boolean isMatchingAvailable(Set<Integer> matchPlayers) {
        // 人物在嘉年华最多能呆13min，所以将冷却时间设置成13min，避免组队没解散，该人重进导致队友进度重置
        final long MATCHING_TIMEOUT = TimeUnit.MINUTES.toMillis(13);
        for (Integer cid : matchPlayers) {
            MatchCheckingElement element = matchEntries.get(cid);
            if (element != null && System.currentTimeMillis() - element.getBeginTime() > MATCHING_TIMEOUT) {
                return false;
            }
        }

        return true;
    }

    private void reenablePlayerMatching(Set<Integer> matchPlayers) {
        for (Integer cid : matchPlayers) {
            MatchCheckingElement mmce = matchEntries.get(cid);

            if (mmce != null) {
                synchronized (mmce) {
                    if (!mmce.isMatchActive()) {
                        matchEntries.remove(cid);
                    }
                }
            }
        }
    }

    /**
     * 获取匹配确认的领袖角色ID
     *
     * @param cid 查询的角色ID
     * @return 领袖角色ID，未找到则返回-1
     */
    public int getMatchConfirmationLeaderid(int cid) {
        MatchCheckingElement mmce = matchEntries.get(cid);
        if (mmce != null) {
            return mmce.leaderCid;
        } else {
            return -1;
        }
    }

    /**
     * 获取匹配确认的类型
     *
     * @param cid 查询的角色ID
     * @return 匹配类型，未找到则返回null
     */
    public MatchCheckerType getMatchConfirmationType(int cid) {
        MatchCheckingElement mmce = matchEntries.get(cid);
        if (mmce != null) {
            return mmce.matchType;
        } else {
            return null;
        }
    }

    /**
     * 判断匹配确认是否处于活跃状态
     *
     * @param cid 查询的角色ID
     * @return 是否活跃
     */
    public boolean isMatchConfirmationActive(int cid) {
        MatchCheckingElement mmce = matchEntries.get(cid);
        if (mmce != null) {
            return mmce.active;
        } else {
            return false;
        }
    }

    private MatchCheckingElement createMatchConfirmationInternal(MatchCheckerType matchType, int world, int leaderCid, AbstractMatchCheckerListener leaderListener, Set<Integer> players, String message) {
        MatchCheckingElement mmce = new MatchCheckingElement(matchType, leaderCid, world, leaderListener, players, message);

        for (Integer cid : players) {
            matchEntries.put(cid, mmce);
        }

        acceptMatchElement(mmce, leaderCid);
        return mmce;
    }

    /**
     * 创建匹配确认
     * 使用信号量控制并发，确保匹配的原子性
     *
     * @param matchType  匹配类型
     * @param world      世界ID
     * @param leaderCid  领袖角色ID
     * @param players    参与玩家ID集合
     * @param message    匹配消息
     * @return 是否创建成功
     */
    public boolean createMatchConfirmation(MatchCheckerType matchType, int world, int leaderCid, Set<Integer> players, String message) {
        MatchCheckingElement mmce = null;
        try {
            semaphorePool.acquire();
            try {
                if (poolMatchPlayers(players)) {
                    try {
                        if (isMatchingAvailable(players)) {
                            AbstractMatchCheckerListener leaderListener = matchType.getListener();
                            mmce = createMatchConfirmationInternal(matchType, world, leaderCid, leaderListener, players, message);
                        } else {
                            reenablePlayerMatching(players);
                        }
                    } finally {
                        unpoolMatchPlayers(players);
                    }
                }
            } finally {
                semaphorePool.release();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        if (mmce != null) {
            mmce.dispatchMatchCreated();
            return true;
        } else {
            return false;
        }
    }

    private void disposeMatchElement(MatchCheckingElement mmce) {
        Set<Integer> matchPlayers = mmce.getMatchPlayers();     // thanks Ai for noticing players getting match-stuck on certain cases
        while (!poolMatchPlayers(matchPlayers)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
        }

        try {
            for (Integer cid : matchPlayers) {
                matchEntries.remove(cid);
            }
        } finally {
            unpoolMatchPlayers(matchPlayers);
        }
    }

    private boolean acceptMatchElement(MatchCheckingElement mmce, int cid) {
        if (mmce.acceptEntry(cid)) {
            unpoolMatchPlayer(cid);
            disposeMatchElement(mmce);

            return true;
        } else {
            return false;
        }
    }

    private void denyMatchElement(MatchCheckingElement mmce, int cid) {
        unpoolMatchPlayer(cid);
        disposeMatchElement(mmce);
    }

    private void dismissMatchElement(MatchCheckingElement mmce, int cid) {
        mmce.setMatchActive(false);

        unpoolMatchPlayer(cid);
        disposeMatchElement(mmce);
    }

    /**
     * 玩家对匹配确认的响应
     *
     * @param cid    角色ID
     * @param accept 是否接受
     * @return 始终返回false（由onMatchAccepted/onMatchDeclined回调处理结果）
     */
    public boolean answerMatchConfirmation(int cid, boolean accept) {
        MatchCheckingElement mmce = null;
        try {
            semaphorePool.acquire();
            try {
                while (matchEntries.containsKey(cid)) {
                    if (poolMatchPlayer(cid)) {
                        try {
                            mmce = matchEntries.get(cid);

                            if (mmce != null) {
                                synchronized (mmce) {
                                    if (!mmce.isMatchActive()) {    // thanks Alex (Alex-0000) for noticing that exploiters could stall on match checking
                                        matchEntries.remove(cid);
                                        mmce = null;
                                    } else {
                                        if (accept) {
                                            if (!acceptMatchElement(mmce, cid)) {
                                                mmce = null;
                                            }

                                            break;  // thanks Rohenn for noticing loop scenario here
                                        } else {
                                            denyMatchElement(mmce, cid);
                                            matchEntries.remove(cid);
                                        }
                                    }
                                }
                            }
                        } finally {
                            unpoolMatchPlayer(cid);
                        }
                    }
                }
            } finally {
                semaphorePool.release();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        if (mmce != null) {
            mmce.dispatchMatchResult(accept);
        }

        return false;
    }

    /**
     * 解散匹配确认
     * 由系统或玩家主动取消匹配时调用
     *
     * @param cid 角色ID
     * @return 是否成功解散
     */
    public boolean dismissMatchConfirmation(int cid) {
        MatchCheckingElement mmce = null;
        try {
            semaphorePool.acquire();
            try {
                while (matchEntries.containsKey(cid)) {
                    if (poolMatchPlayer(cid)) {
                        try {
                            mmce = matchEntries.get(cid);

                            if (mmce != null) {
                                synchronized (mmce) {
                                    if (!mmce.isMatchActive()) {
                                        mmce = null;
                                    } else {
                                        dismissMatchElement(mmce, cid);
                                    }
                                }
                            }
                        } finally {
                            unpoolMatchPlayer(cid);
                        }
                    }
                }
            } finally {
                semaphorePool.release();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        if (mmce != null) {
            mmce.dispatchMatchDismissed();
            return true;
        } else {
            return false;
        }
    }

}