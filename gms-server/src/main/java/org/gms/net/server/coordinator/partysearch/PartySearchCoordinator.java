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
import org.gms.client.Job;
import org.gms.config.GameConfig;
import org.gms.constants.id.MapId;
import org.gms.net.server.coordinator.world.InviteCoordinator;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteType;
import org.gms.net.server.world.Party;
import org.gms.provider.Data;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 队伍搜索协调器（单例）
 * 管理玩家队伍搜索功能，支持按等级范围查找并自动匹配队伍
 */
public class PartySearchCoordinator {

    /** 按职业分类的队伍搜索存储 */
    private final Map<Job, PartySearchStorage> storage = new HashMap<>();
    /** 按职业分类的新加入者梯队 */
    private final Map<Job, PartySearchEchelon> upcomers = new HashMap<>();

    /** 队伍领袖队列 */
    private final List<Character> leaderQueue = new LinkedList<>();
    /** 队伍领袖队列读锁 */
    private final Lock leaderQueueRLock;
    /** 队伍领袖队列写锁 */
    private final Lock leaderQueueWLock;

    /** 当前搜索中的领袖，key为角色ID */
    private final Map<Integer, Character> searchLeaders = new HashMap<>();
    /** 领袖搜索元数据，key为角色ID */
    private final Map<Integer, LeaderSearchMetadata> searchSettings = new HashMap<>();

    /** 超时等待队列中的领袖 */
    private final Map<Character, LeaderSearchMetadata> timeoutLeaders = new HashMap<>();

    /** 更新周期计数器 */
    private int updateCount = 0;

    /** 邻近地图映射表 */
    private static final Map<Integer, Set<Integer>> mapNeighbors = fetchNeighbouringMaps();
    /** 职业查找表，将位索引映射到职业 */
    private static final Map<Integer, Job> jobTable = instantiateJobTable();

    public PartySearchCoordinator() {
        for (Job job : jobTable.values()) {
            storage.put(job, new PartySearchStorage());
            upcomers.put(job, new PartySearchEchelon());
        }

        ReadWriteLock leaderQueueLock = new ReentrantReadWriteLock(true);
        this.leaderQueueRLock = leaderQueueLock.readLock();
        this.leaderQueueWLock = leaderQueueLock.writeLock();
    }

    /**
     * 从WZ数据中加载邻近地图关系表
     *
     * @return 地图ID到邻近地图ID集合的映射
     */
    private static Map<Integer, Set<Integer>> fetchNeighbouringMaps() {
        Map<Integer, Set<Integer>> mapLinks = new HashMap<>();

        Data data = DataProviderFactory.getDataProvider(WZFiles.ETC).getData("MapNeighbors.img");
        if (data != null) {
            for (Data mapdata : data.getChildren()) {
                int mapid = Integer.parseInt(mapdata.getName());

                Set<Integer> neighborMaps = new HashSet<>();
                mapLinks.put(mapid, neighborMaps);

                for (Data neighbordata : mapdata.getChildren()) {
                    int neighborid = DataTool.getInt(neighbordata, MapId.NONE);

                    if (neighborid != MapId.NONE) {
                        neighborMaps.add(neighborid);
                    }
                }
            }
        }

        return mapLinks;
    }

    /**
     * 判断两个地图是否在邻近范围内
     *
     * @param callerMapid 发起者地图ID
     * @param calleeMapid 被搜索者地图ID
     * @return 是否在邻近范围内
     */
    public static boolean isInVicinity(int callerMapid, int calleeMapid) {
        Set<Integer> vicinityMapids = mapNeighbors.get(calleeMapid);

        if (vicinityMapids != null) {
            return vicinityMapids.contains(calleeMapid);
        } else {
            int callerRange = callerMapid / 10000000;
            if (callerRange >= 90) {
                return callerRange == (calleeMapid / 1000000);
            } else {
                return callerRange == (calleeMapid / 10000000);
            }
        }
    }

    /**
     * 初始化职业查找表
     * 将位掩码索引映射到对应的游戏职业，用于客户端队伍搜索中的职业选择
     *
     * @return 位索引到职业的映射表
     */
    private static Map<Integer, Job> instantiateJobTable() {
        Map<Integer, Job> table = new HashMap<>();

        List<Pair<Integer, Integer>> jobSearchTypes = new LinkedList<Pair<Integer, Integer>>() {{
            add(new Pair<>(Job.MAPLELEAF_BRIGADIER.getId(), 0));
            add(new Pair<>(0, 0));
            add(new Pair<>(Job.ARAN1.getId(), 0));
            add(new Pair<>(100, 3));
            add(new Pair<>(Job.DAWNWARRIOR1.getId(), 0));
            add(new Pair<>(200, 3));
            add(new Pair<>(Job.BLAZEWIZARD1.getId(), 0));
            add(new Pair<>(500, 2));
            add(new Pair<>(Job.THUNDERBREAKER1.getId(), 0));
            add(new Pair<>(400, 2));
            add(new Pair<>(Job.NIGHTWALKER1.getId(), 0));
            add(new Pair<>(300, 2));
            add(new Pair<>(Job.WINDARCHER1.getId(), 0));
            add(new Pair<>(Job.EVAN1.getId(), 0));
        }};

        int i = 0;
        for (Pair<Integer, Integer> p : jobSearchTypes) {
            table.put(i, Job.getById(p.getLeft()));
            i++;

            for (int j = 1; j <= p.getRight(); j++) {
                table.put(i, Job.getById(p.getLeft() + 10 * j));
                i++;
            }
        }

        return table;
    }

    /**
     * 领袖搜索元数据
     * 封装队伍领袖的搜索参数和状态
     */
    private class LeaderSearchMetadata {
        /** 最小搜索等级 */
        private final int minLevel;
        /** 最大搜索等级 */
        private final int maxLevel;
        /** 搜索的职业列表 */
        private final List<Job> searchedJobs;

        /** 重新入队次数 */
        private int reentryCount;

        /**
         * 将客户端的职业位掩码解码为职业列表
         *
         * @param jobsSelected 位掩码表示的职业选择
         * @return 职业列表
         */
        private List<Job> decodeSearchedJobs(int jobsSelected) {
            List<Job> searchedJobs = new LinkedList<>();

            int topByte = (int) ((Math.log(jobsSelected) / Math.log(2)) + 1e-5);

            for (int i = 0; i <= topByte; i++) {
                if (jobsSelected % 2 == 1) {
                    Job job = jobTable.get(i);
                    if (job != null) {
                        searchedJobs.add(job);
                    }
                }

                jobsSelected = jobsSelected >> 1;
                if (jobsSelected == 0) {
                    break;
                }
            }

            return searchedJobs;
        }

        /**
         * 构造函数，初始化搜索元数据
         *
         * @param minLevel 最小搜索等级
         * @param maxLevel 最大搜索等级
         * @param jobs     职业选择位掩码
         */
        private LeaderSearchMetadata(int minLevel, int maxLevel, int jobs) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.searchedJobs = decodeSearchedJobs(jobs);
            this.reentryCount = 0;
        }

    }

    /**
     * 将玩家添加到对应职业的搜索梯队中
     *
     * @param chr 要注册的玩家角色
     */
    public void attachPlayer(Character chr) {
        upcomers.get(getPartySearchJob(chr.getJob())).attachPlayer(chr);
    }

    /**
     * 从搜索梯队中移除玩家
     *
     * @param chr 要移除的玩家角色
     */
    public void detachPlayer(Character chr) {
        Job psJob = getPartySearchJob(chr.getJob());

        if (!upcomers.get(psJob).detachPlayer(chr)) {
            storage.get(psJob).detachPlayer(chr);
        }
    }

    /**
     * 更新队伍搜索存储
     * 将所有梯队中新增的玩家同步到搜索存储中
     */
    public void updatePartySearchStorage() {
        for (Entry<Job, PartySearchEchelon> psUpdate : upcomers.entrySet()) {
            storage.get(psUpdate.getKey()).updateStorage(psUpdate.getValue().exportEchelon());
        }
    }

    /**
     * 获取队伍搜索所用的泛化职业
     * 将具体职业映射到通用职业分类，以便跨子职业搜索
     *
     * @param job 具体职业
     * @return 搜索使用的泛化职业
     */
    private static Job getPartySearchJob(Job job) {
        if (job.getJobNiche() == 0) {
            return Job.BEGINNER;
        } else if (job.getId() < 600) { // 冒险家
            return Job.getById((job.getId() / 10) * 10);
        } else if (job.getId() >= 1000) { // 骑士团
            return Job.getById((job.getId() / 100) * 100);
        } else {
            return Job.MAPLELEAF_BRIGADIER;
        }
    }

    /**
     * 从搜索存储中获取匹配的玩家
     *
     * @param callerCid   发起搜索的领袖角色ID
     * @param callerMapid 发起搜索的地图ID
     * @param job         搜索职业
     * @param minLevel    最小等级
     * @param maxLevel    最大等级
     * @return 匹配的玩家，未找到则返回null
     */
    private Character fetchPlayer(int callerCid, int callerMapid, Job job, int minLevel, int maxLevel) {
        return storage.get(getPartySearchJob(job)).callPlayer(callerCid, callerMapid, minLevel, maxLevel);
    }

    /**
     * 将领袖加入处理队列
     */
    private void addQueueLeader(Character leader) {
        leaderQueueRLock.lock();
        try {
            leaderQueue.add(leader);
        } finally {
            leaderQueueRLock.unlock();
        }
    }

    /**
     * 从处理队列中移除领袖
     */
    private void removeQueueLeader(Character leader) {
        leaderQueueRLock.lock();
        try {
            leaderQueue.remove(leader);
        } finally {
            leaderQueueRLock.unlock();
        }
    }

    /**
     * 注册队伍领袖开始搜索
     *
     * @param leader   队伍领袖
     * @param minLevel 最小搜索等级
     * @param maxLevel 最大搜索等级
     * @param jobs     职业选择位掩码
     */
    public void registerPartyLeader(Character leader, int minLevel, int maxLevel, int jobs) {
        if (searchLeaders.containsKey(leader.getId())) {
            return;
        }

        searchSettings.put(leader.getId(), new LeaderSearchMetadata(minLevel, maxLevel, jobs));
        searchLeaders.put(leader.getId(), leader);
        addQueueLeader(leader);
    }

    /**
     * 使用已有元数据重新注册领袖
     */
    private void registerPartyLeader(Character leader, LeaderSearchMetadata settings) {
        if (searchLeaders.containsKey(leader.getId())) {
            return;
        }

        searchSettings.put(leader.getId(), settings);
        searchLeaders.put(leader.getId(), leader);
        addQueueLeader(leader);
    }

    /**
     * 注销队伍领袖，停止搜索
     *
     * @param leader 队伍领袖
     */
    public void unregisterPartyLeader(Character leader) {
        Character toRemove = searchLeaders.remove(leader.getId());
        if (toRemove != null) {
            removeQueueLeader(toRemove);
            searchSettings.remove(leader.getId());
        } else {
            unregisterLongTermPartyLeader(leader);
        }
    }

    /**
     * 为领袖搜索匹配的玩家
     *
     * @param leader 队伍领袖
     * @return 匹配的玩家，未找到则返回null
     */
    private Character searchPlayer(Character leader) {
        LeaderSearchMetadata settings = searchSettings.get(leader.getId());
        if (settings != null) {
            int minLevel = settings.minLevel, maxLevel = settings.maxLevel;
            Collections.shuffle(settings.searchedJobs);

            int leaderCid = leader.getId();
            int leaderMapid = leader.getMapId();
            for (Job searchJob : settings.searchedJobs) {
                Character chr = fetchPlayer(leaderCid, leaderMapid, searchJob, minLevel, maxLevel);
                if (chr != null) {
                    return chr;
                }
            }
        }

        return null;
    }

    /**
     * 发送队伍搜索邀请
     *
     * @param chr    被邀请的玩家
     * @param leader 邀请的领袖
     * @return 是否成功发送邀请
     */
    private boolean sendPartyInviteFromSearch(Character chr, Character leader) {
        if (chr == null) {
            return false;
        }

        int partyid = leader.getPartyId();
        if (partyid < 0) {
            return false;
        }

        if (InviteCoordinator.createInvite(InviteType.PARTY, leader, partyid, chr.getId())) {
            chr.getDisabledPartySearchInvites().add(leader.getId());
            chr.sendPacket(PacketCreator.partySearchInvite(leader));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取队列中的领袖并分割为当前批次和下一批次
     * 每次最多处理100个领袖
     *
     * @return 左为当前批次领袖列表，右为剩余领袖列表
     */
    private Pair<List<Character>, List<Character>> fetchQueuedLeaders() {
        List<Character> queuedLeaders, nextLeaders;

        leaderQueueWLock.lock();
        try {
            int splitIdx = Math.min(leaderQueue.size(), 100);

            queuedLeaders = new LinkedList<>(leaderQueue.subList(0, splitIdx));
            nextLeaders = new LinkedList<>(leaderQueue.subList(splitIdx, leaderQueue.size()));
        } finally {
            leaderQueueWLock.unlock();
        }

        return new Pair<>(queuedLeaders, nextLeaders);
    }

    /**
     * 将超时领袖加入长期等待队列
     *
     * @param recycledLeaders 回收的领袖与元数据配对列表
     */
    private void registerLongTermPartyLeaders(List<Pair<Character, LeaderSearchMetadata>> recycledLeaders) {
        leaderQueueRLock.lock();
        try {
            for (Pair<Character, LeaderSearchMetadata> p : recycledLeaders) {
                timeoutLeaders.put(p.getLeft(), p.getRight());
            }
        } finally {
            leaderQueueRLock.unlock();
        }
    }

    /**
     * 从长期等待队列中移除领袖
     */
    private void unregisterLongTermPartyLeader(Character leader) {
        leaderQueueRLock.lock();
        try {
            timeoutLeaders.remove(leader);
        } finally {
            leaderQueueRLock.unlock();
        }
    }

    /**
     * 将长期等待队列中的领袖重新加入处理队列
     * 每处理77轮执行一次，避免某些领袖长期得不到处理
     */
    private void reinstateLongTermPartyLeaders() {
        Map<Character, LeaderSearchMetadata> timeoutLeadersCopy;
        leaderQueueWLock.lock();
        try {
            timeoutLeadersCopy = new HashMap<>(timeoutLeaders);
            timeoutLeaders.clear();
        } finally {
            leaderQueueWLock.unlock();
        }

        for (Entry<Character, LeaderSearchMetadata> e : timeoutLeadersCopy.entrySet()) {
            registerPartyLeader(e.getKey(), e.getValue());
        }
    }

    /**
     * 执行一次队伍搜索
     * 处理当前批次的领袖，为每个领袖搜索匹配的玩家
     */
    public void runPartySearch() {
        Pair<List<Character>, List<Character>> queuedLeaders = fetchQueuedLeaders();

        List<Character> searchedLeaders = new LinkedList<>();
        List<Character> recalledLeaders = new LinkedList<>();
        List<Character> expiredLeaders = new LinkedList<>();

        for (Character leader : queuedLeaders.getLeft()) {
            Character chr = searchPlayer(leader);
            if (sendPartyInviteFromSearch(chr, leader)) {
                searchedLeaders.add(leader);
            } else {
                LeaderSearchMetadata settings = searchSettings.get(leader.getId());
                if (settings != null) {
                    if (settings.reentryCount < GameConfig.getServerInt("party_search_reentry_limit")) {
                        settings.reentryCount += 1;
                        recalledLeaders.add(leader);
                    } else {
                        expiredLeaders.add(leader);
                    }
                }
            }
        }

        leaderQueueRLock.lock();
        try {
            leaderQueue.clear();
            leaderQueue.addAll(queuedLeaders.getRight());

            try {
                leaderQueue.addAll(25, recalledLeaders);
            } catch (IndexOutOfBoundsException e) {
                leaderQueue.addAll(recalledLeaders);
            }
        } finally {
            leaderQueueRLock.unlock();
        }

        for (Character leader : searchedLeaders) {
            Party party = leader.getParty();
            if (party != null && party.getMembers().size() < 6) {
                addQueueLeader(leader);
            } else {
                if (leader.isLoggedInWorld()) {
                    leader.dropMessage(5, "Your Party Search token session has finished as your party reached full capacity.");
                }
                searchLeaders.remove(leader.getId());
                searchSettings.remove(leader.getId());
            }
        }

        List<Pair<Character, LeaderSearchMetadata>> recycledLeaders = new LinkedList<>();
        for (Character leader : expiredLeaders) {
            searchLeaders.remove(leader.getId());
            LeaderSearchMetadata settings = searchSettings.remove(leader.getId());

            if (leader.isLoggedInWorld()) {
                if (settings != null) {
                    recycledLeaders.add(new Pair<>(leader, settings));
                    if (GameConfig.getServerBoolean("use_debug") && leader.isGM()) {
                        leader.dropMessage(5, "Your Party Search token session is now on waiting queue for up to 7 minutes, to get it working right away please stop your Party Search and retry again later.");
                    }
                } else {
                    leader.dropMessage(5, "Your Party Search token session expired, please stop your Party Search and retry again later.");
                }
            }
        }

        if (!recycledLeaders.isEmpty()) {
            registerLongTermPartyLeaders(recycledLeaders);
        }

        updateCount++;
        if (updateCount % 77 == 0) {
            reinstateLongTermPartyLeaders();
        }
    }

}