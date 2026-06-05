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
package org.gms.net.server.world;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.Getter;
import lombok.Setter;
import org.gms.client.BuddyList;
import org.gms.client.BuddyList.BuddyAddResult;
import org.gms.client.BuddyList.BuddyOperation;
import org.gms.client.BuddylistEntry;
import org.gms.client.Character;
import org.gms.client.Family;
import org.gms.config.GameConfig;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.dao.entity.PlayernpcsFieldDO;
import org.gms.dao.mapper.PlayernpcsFieldMapper;
import org.gms.manager.ServerManager;
import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.channel.CharacterIdChannelPair;
import org.gms.net.server.coordinator.matchchecker.MatchCheckerCoordinator;
import org.gms.net.server.coordinator.partysearch.PartySearchCoordinator;
import org.gms.net.server.coordinator.world.InviteCoordinator;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteResultType;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteType;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.guild.GuildCharacter;
import org.gms.net.server.guild.GuildPackets;
import org.gms.net.server.guild.GuildSummary;
import org.gms.net.server.services.BaseService;
import org.gms.net.server.services.ServicesManager;
import org.gms.net.server.services.type.WorldServices;
import org.gms.net.server.task.CharacterAutosaverTask;
import org.gms.net.server.task.CharacterHpDecreaseTask;
import org.gms.net.server.task.FamilyDailyResetTask;
import org.gms.net.server.task.FishingTask;
import org.gms.net.server.task.HiredMerchantTask;
import org.gms.net.server.task.MapOwnershipTask;
import org.gms.net.server.task.MountTirednessTask;
import org.gms.net.server.task.PartySearchTask;
import org.gms.net.server.task.PetFullnessTask;
import org.gms.net.server.task.ServerMessageTask;
import org.gms.net.server.task.TimedMapObjectTask;
import org.gms.net.server.task.TimeoutTask;
import org.gms.net.server.task.WeddingReservationTask;
import org.gms.util.*;
import org.gms.util.packets.Fishing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.server.Storage;
import org.gms.server.TimerManager;
import org.gms.server.maps.AbstractMapObject;
import org.gms.server.maps.HiredMerchant;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MiniDungeon;
import org.gms.server.maps.MiniDungeonInfo;
import org.gms.server.maps.PlayerShop;
import org.gms.server.maps.PlayerShopItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.gms.dao.entity.table.PlayernpcsFieldDOTableDef.PLAYERNPCS_FIELD_D_O;

/**
 * 【类型】World（class），包 {@code org.gms.net.server.world}。游戏世界/大区运行时实体：管理所属频道列表、在线玩家容器、
 * 公会、家族、婚姻、组队、雇佣商店等业务模块，并通过 TimerManager 注册各类定时任务（宠物饱食度、坐骑疲劳、钓鱼、角色自动存档等）。
 * 每个 World 代表一个独立的游戏大区（如 Scania、Bera）。
 *
 * @author kevintjuh93
 * @author Ronan - thread-oriented (world schedules + guild queue + marriages + party chars)
 */
public class World {
    private static final Logger log = LoggerFactory.getLogger(World.class);

    @Getter
    private final int id;
    @Getter
    private int flag;
    @Getter
    private float expRate;
    @Getter
    private float dropRate;
    // boss rate concept thanks to Lapeiro
    @Setter
    @Getter
    private float bossDropRate;
    @Getter
    private float mesoRate;
    @Setter
    @Getter
    private float questRate;
    @Setter
    @Getter
    private float travelRate;
    @Setter
    @Getter
    private float fishingRate;
    private String eventmsg;
    private final List<Channel> channels = new ArrayList<>();
    private final Map<Integer, Byte> pnpcStep = new HashMap<>();
    private final Map<Integer, Short> pnpcPodium = new HashMap<>();
    private final Map<Integer, Messenger> messengers = new HashMap<>();
    private final AtomicInteger runningMessengerId = new AtomicInteger();
    private final Map<Integer, Family> families = new LinkedHashMap<>();
    private final Map<Integer, Integer> relationships = new HashMap<>();
    private final Map<Integer, Pair<Integer, Integer>> relationshipCouples = new HashMap<>();
    private final Map<Integer, GuildSummary> gsStore = new HashMap<>();
    private PlayerStorage players = new PlayerStorage();
    private final ServicesManager services = new ServicesManager(WorldServices.SAVE_CHARACTER);
    private final MatchCheckerCoordinator matchChecker = new MatchCheckerCoordinator();
    private final PartySearchCoordinator partySearch = new PartySearchCoordinator();

    private final Lock chnRLock;
    private final Lock chnWLock;

    private final Map<Integer, SortedMap<Integer, Character>> accountChars = new HashMap<>();
    private final Map<Integer, Storage> accountStorages = new HashMap<>();
    private final Lock accountCharsLock = new ReentrantLock(true);

    private final Set<Integer> queuedGuilds = new HashSet<>();
    private final Map<Integer, Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>>> queuedMarriages = new HashMap<>();
    private final Map<Integer, Set<Integer>> marriageGuests = new ConcurrentHashMap<>();

    private final Map<Integer, Integer> partyChars = new HashMap<>();
    private final Map<Integer, Party> parties = new HashMap<>();
    private final AtomicInteger runningPartyId = new AtomicInteger();
    private final Lock partyLock = new ReentrantLock(true);

    private final Map<Integer, Integer> owlSearched = new LinkedHashMap<>();
    private final List<Map<Integer, Integer>> cashItemBought = new ArrayList<>(9);

    private final Lock suggestRLock;
    private final Lock suggestWLock;

    private final Map<Integer, Integer> disabledServerMessages = new HashMap<>();    // reuse owl lock
    private final Lock srvMessagesLock = new ReentrantLock();
    private ScheduledFuture<?> srvMessagesSchedule;

    private final Lock activePetsLock = new ReentrantLock(true);
    private final Map<Integer, Integer> activePets = new LinkedHashMap<>();
    private ScheduledFuture<?> petsSchedule;
    private long petUpdate;

    private final Lock activeMountsLock = new ReentrantLock(true);
    private final Map<Integer, Integer> activeMounts = new LinkedHashMap<>();
    private ScheduledFuture<?> mountsSchedule;
    private long mountUpdate;

    private final Lock activePlayerShopsLock = new ReentrantLock(true);
    private final Map<Integer, PlayerShop> activePlayerShops = new LinkedHashMap<>();

    private final Lock activeMerchantsLock = new ReentrantLock(true);
    private final Map<Integer, Pair<HiredMerchant, Integer>> activeMerchants = new LinkedHashMap<>();
    private ScheduledFuture<?> merchantSchedule;
    private long merchantUpdate;

    private final Map<Runnable, Long> registeredTimedMapObjects = new LinkedHashMap<>();
    private ScheduledFuture<?> timedMapObjectsSchedule;
    private final Lock timedMapObjectLock = new ReentrantLock(true);

    private final Map<Character, Integer> fishingAttempters = Collections.synchronizedMap(new WeakHashMap<>());
    private final Map<Character, Integer> playerHpDec = Collections.synchronizedMap(new WeakHashMap<>());

    private ScheduledFuture<?> charactersSchedule;
    private ScheduledFuture<?> marriagesSchedule;
    private ScheduledFuture<?> mapOwnershipSchedule;
    private ScheduledFuture<?> fishingSchedule;
    private ScheduledFuture<?> partySearchSchedule;
    private ScheduledFuture<?> timeoutSchedule;
    private ScheduledFuture<?> hpDecSchedule;

    /**
     * World 构造函数，初始化游戏世界/大区。
     *
     * @param world 世界ID
     * @param flag 世界状态标志
     * @param eventmsg 活动公告消息
     * @param expRate 经验倍率
     * @param dropRate 掉落倍率
     * @param bossDropRate BOSS掉落倍率
     * @param mesoRate 金币倍率
     * @param questRate 任务倍率
     * @param travelRate 旅行时间倍率
     * @param fishingRate 钓鱼倍率
     */
    public World(int world, int flag, String eventmsg, float expRate, float dropRate, float bossDropRate, float mesoRate,
                 float questRate, float travelRate, float fishingRate) {
        this.id = world;
        this.flag = flag;
        this.eventmsg = eventmsg;
        this.expRate = expRate;
        this.dropRate = dropRate;
        this.bossDropRate = bossDropRate;
        this.mesoRate = mesoRate;
        this.questRate = questRate;
        this.travelRate = travelRate;
        this.fishingRate = fishingRate;
        runningPartyId.set(1000000001); // partyid must not clash with charid to solve update item looting issues, found thanks to Vcoc
        runningMessengerId.set(1);

        ReadWriteLock channelLock = new ReentrantReadWriteLock(true);
        this.chnRLock = channelLock.readLock();
        this.chnWLock = channelLock.writeLock();

        ReadWriteLock suggestLock = new ReentrantReadWriteLock(true);
        this.suggestRLock = suggestLock.readLock();
        this.suggestWLock = suggestLock.writeLock();

        petUpdate = Server.getInstance().getCurrentTime();
        mountUpdate = petUpdate;

        for (int i = 0; i < 9; i++) {
            cashItemBought.add(new LinkedHashMap<>());
        }

        TimerManager tman = TimerManager.getInstance();
        petsSchedule = tman.register(new PetFullnessTask(this), MINUTES.toMillis(1), MINUTES.toMillis(1));
        srvMessagesSchedule = tman.register(new ServerMessageTask(this), SECONDS.toMillis(10), SECONDS.toMillis(10));
        mountsSchedule = tman.register(new MountTirednessTask(this), MINUTES.toMillis(1), MINUTES.toMillis(1));
        merchantSchedule = tman.register(new HiredMerchantTask(this), 10 * MINUTES.toMillis(1), 10 * MINUTES.toMillis(1));
        timedMapObjectsSchedule = tman.register(new TimedMapObjectTask(this), MINUTES.toMillis(1), MINUTES.toMillis(1));
        charactersSchedule = tman.register(new CharacterAutosaverTask(this), HOURS.toMillis(1), HOURS.toMillis(1));
        marriagesSchedule = tman.register(new WeddingReservationTask(this), MINUTES.toMillis(GameConfig.getServerLong("wedding_reservation_interval")), MINUTES.toMillis(GameConfig.getServerLong("wedding_reservation_interval")));
        mapOwnershipSchedule = tman.register(new MapOwnershipTask(this), SECONDS.toMillis(20), SECONDS.toMillis(20));
        fishingSchedule = tman.register(new FishingTask(this), SECONDS.toMillis(10), SECONDS.toMillis(10));
        partySearchSchedule = tman.register(new PartySearchTask(this), SECONDS.toMillis(10), SECONDS.toMillis(10));
        timeoutSchedule = tman.register(new TimeoutTask(this), SECONDS.toMillis(10), SECONDS.toMillis(10));
        hpDecSchedule = tman.register(new CharacterHpDecreaseTask(this), GameConfig.getServerLong("map_damage_overtime_interval"), GameConfig.getServerLong("map_damage_overtime_interval"));

        if (GameConfig.getServerBoolean("use_family_system")) {
            long timeLeft = Server.getTimeLeftForNextDay();
            FamilyDailyResetTask.resetEntitlementUsage(this);
            tman.register(new FamilyDailyResetTask(this), DAYS.toMillis(1), timeLeft);
        }
    }

    /**
     * 获取频道数量。
     *
     * @return 频道数量
     */
    public int getChannelsSize() {
        chnRLock.lock();
        try {
            return channels.size();
        } finally {
            chnRLock.unlock();
        }
    }

    /**
     * 获取所有频道列表。
     *
     * @return 频道列表副本
     */
    public List<Channel> getChannels() {
        chnRLock.lock();
        try {
            return new ArrayList<>(channels);
        } finally {
            chnRLock.unlock();
        }
    }

    /**
     * 根据频道号获取频道。
     *
     * @param channel 频道号（从1开始）
     * @return 对应的频道对象，如果不存在则返回null
     */
    public Channel getChannel(int channel) {
        chnRLock.lock();
        try {
            try {
                return channels.get(channel - 1);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        } finally {
            chnRLock.unlock();
        }
    }

    /**
     * 添加频道到世界。频道ID必须按顺序递增（只能添加当前最大ID+1的频道）。
     *
     * @param channel 要添加的频道
     * @return 添加成功返回true，否则返回false
     */
    public boolean addChannel(Channel channel) {
        chnWLock.lock();
        try {
            if (channel.getId() == channels.size() + 1) {
                channels.add(channel);
                return true;
            } else {
                return false;
            }
        } finally {
            chnWLock.unlock();
        }
    }

    /**
     * 移除最后一个频道。只有当频道可以卸载（无在线玩家）时才能移除。
     *
     * @return 被移除的频道ID，失败返回-1
     */
    public int removeChannel() {
        Channel ch;
        int chIdx;

        chnRLock.lock();
        try {
            chIdx = channels.size() - 1;
            if (chIdx < 0) {
                return -1;
            }

            ch = channels.get(chIdx);
        } finally {
            chnRLock.unlock();
        }

        if (ch == null || !ch.canUninstall()) {
            return -1;
        }

        chnWLock.lock();
        try {
            if (chIdx == channels.size() - 1) {
                channels.remove(chIdx);
            } else {
                return -1;
            }
        } finally {
            chnWLock.unlock();
        }

        ch.shutdown();
        return ch.getId();
    }

    /**
     * 检查世界是否可以卸载。只有当世界中没有在线玩家且所有频道都可以卸载时才返回true。
     *
     * @return 是否可以卸载
     */
    public boolean canUninstall() {
        if (players.getSize() > 0) {
            return false;
        }

        for (Channel ch : this.getChannels()) {
            if (!ch.canUninstall()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 设置世界状态标志。
     *
     * @param b 状态标志值
     */
    public void setFlag(byte b) {
        this.flag = b;
    }

    public String getEventMessage() {
        return eventmsg;
    }

    public void setEventMessage(String eventMessage) {
        this.eventmsg = eventMessage;
    }

    /**
     * 设置经验倍率，并更新所有在线角色的倍率设置。
     *
     * @param exp 新的经验倍率
     */
    public void setExpRate(float exp) {
        Collection<Character> list = getPlayerStorage().getAllCharacters();

        for (Character chr : list) {
            if (!chr.isLoggedIn()) {
                continue;
            }
            chr.revertWorldRates();
        }
        this.expRate = exp;
        for (Character chr : list) {
            if (!chr.isLoggedIn()) {
                continue;
            }
            chr.setWorldRates();
        }
    }

    /**
     * 设置掉落倍率，并更新所有在线角色的倍率设置。
     *
     * @param drop 新的掉落倍率
     */
    public void setDropRate(float drop) {
        Collection<Character> list = getPlayerStorage().getAllCharacters();

        for (Character chr : list) {
            if (!chr.isLoggedIn()) {
                continue;
            }
            chr.revertWorldRates();
        }
        this.dropRate = drop;
        for (Character chr : list) {
            if (!chr.isLoggedIn()) {
                continue;
            }
            chr.setWorldRates();
        }
    }

    /**
     * 设置金币倍率，并更新所有在线角色的倍率设置。
     *
     * @param meso 新的金币倍率
     */
    public void setMesoRate(float meso) {
        Collection<Character> list = getPlayerStorage().getAllCharacters();

        for (Character chr : list) {
            if (!chr.isLoggedIn()) {
                continue;
            }
            chr.revertWorldRates();
        }
        this.mesoRate = meso;
        for (Character chr : list) {
            if (!chr.isLoggedIn()) {
                continue;
            }
            chr.setWorldRates();
        }
    }

    /**
     * 根据旅行时间倍率计算实际旅行时间。
     *
     * @param travelTime 基础旅行时间
     * @return 实际旅行时间（已应用倍率）
     */
    public int getTransportationTime(int travelTime) {
        return NumberTool.floatToInt(travelTime * travelRate);//交通工具、旅行时间倍率，由于支持小数，所以需要改为相乘
    }

    /**
     * 加载账号角色视图，将账号下的所有角色注册到世界中。
     *
     * @param accountId 账号ID
     * @param chars 角色列表
     */
    public void loadAccountCharactersView(Integer accountId, List<Character> chars) {
        SortedMap<Integer, Character> charsMap = new TreeMap<>();
        for (Character chr : chars) {
            charsMap.put(chr.getId(), chr);
        }

        accountCharsLock.lock();    // accountCharsLock should be used after server's lgnWLock for compliance
        try {
            accountChars.put(accountId, charsMap);
        } finally {
            accountCharsLock.unlock();
        }
    }

    /**
     * 注册单个角色到账号角色视图。
     *
     * @param accountId 账号ID
     * @param chr 角色对象
     */
    public void registerAccountCharacterView(Integer accountId, Character chr) {
        accountCharsLock.lock();
        try {
            accountChars.get(accountId).put(chr.getId(), chr);
        } finally {
            accountCharsLock.unlock();
        }
    }

    public void unregisterAccountCharacterView(Integer accountId, Integer chrId) {
        accountCharsLock.lock();
        try {
            accountChars.get(accountId).remove(chrId);
        } finally {
            accountCharsLock.unlock();
        }
    }

    public void clearAccountCharacterView(Integer accountId) {
        accountCharsLock.lock();
        try {
            SortedMap<Integer, Character> accChars = accountChars.remove(accountId);
            if (accChars != null) {
                accChars.clear();
            }
        } finally {
            accountCharsLock.unlock();
        }
    }

    public void loadAccountStorage(Integer accountId) {
        if (getAccountStorage(accountId) == null) {
            registerAccountStorage(accountId);
        }
    }

    private void registerAccountStorage(Integer accountId) {
        Storage storage = Storage.loadOrCreateFromDB(accountId, this.id);
        accountCharsLock.lock();
        try {
            accountStorages.put(accountId, storage);
        } finally {
            accountCharsLock.unlock();
        }
    }

    public void unregisterAccountStorage(Integer accountId) {
        accountCharsLock.lock();
        try {
            accountStorages.remove(accountId);
        } finally {
            accountCharsLock.unlock();
        }
    }

    public Storage getAccountStorage(Integer accountId) {
        return accountStorages.get(accountId);
    }

    private static List<Entry<Integer, SortedMap<Integer, Character>>> getSortedAccountCharacterView(Map<Integer, SortedMap<Integer, Character>> map) {
        List<Entry<Integer, SortedMap<Integer, Character>>> list = new ArrayList<>(map.size());
        list.addAll(map.entrySet());

        list.sort((o1, o2) -> o1.getKey() - o2.getKey());

        return list;
    }

    /**
     * 加载并获取所有账号的角色视图。
     *
     * @return 所有角色列表（按账号ID和角色ID排序）
     */
    public List<Character> loadAndGetAllCharactersView() {
        Server.getInstance().loadAllAccountsCharactersView();
        return getAllCharactersView();
    }

    /**
     * 获取所有账号的角色视图。
     *
     * @return 所有角色列表（按账号ID和角色ID排序）
     */
    public List<Character> getAllCharactersView() {    // sorting by accountid, charid
        List<Character> chrList = new LinkedList<>();
        Map<Integer, SortedMap<Integer, Character>> accChars;

        accountCharsLock.lock();
        try {
            accChars = new HashMap<>(accountChars);
        } finally {
            accountCharsLock.unlock();
        }

        for (Entry<Integer, SortedMap<Integer, Character>> e : getSortedAccountCharacterView(accChars)) {
            chrList.addAll(e.getValue().values());
        }

        return chrList;
    }

    /**
     * 获取指定账号的角色视图。
     *
     * @param accountId 账号ID
     * @return 该账号下的角色列表，如果账号不存在则返回null
     */
    public List<Character> getAccountCharactersView(int accountId) {
        final List<Character> chrList;

        accountCharsLock.lock();
        try {
            SortedMap<Integer, Character> accChars = accountChars.get(accountId);

            if (accChars != null) {
                chrList = new LinkedList<>(accChars.values());
            } else {
                accountChars.put(accountId, new TreeMap<>());
                chrList = null;
            }
        } finally {
            accountCharsLock.unlock();
        }

        return chrList;
    }

    /**
     * 获取玩家存储管理器。
     *
     * @return 玩家存储对象
     */
    public PlayerStorage getPlayerStorage() {
        return players;
    }

    /**
     * 获取匹配检查协调器。
     *
     * @return 匹配检查协调器
     */
    public MatchCheckerCoordinator getMatchCheckerCoordinator() {
        return matchChecker;
    }

    /**
     * 获取组队搜索协调器。
     *
     * @return 组队搜索协调器
     */
    public PartySearchCoordinator getPartySearchCoordinator() {
        return partySearch;
    }

    /**
     * 添加玩家到世界的在线玩家存储。
     *
     * @param chr 要添加的玩家角色
     */
    public void addPlayer(Character chr) {
        players.addPlayer(chr);
    }

    /**
     * 从世界中移除玩家。先从所属频道移除，如果失败则遍历所有频道查找并移除。
     *
     * @param chr 要移除的玩家角色
     */
    public void removePlayer(Character chr) {
        Channel cserv = chr.getClient().getChannelServer();

        if (cserv != null) {
            if (!cserv.removePlayer(chr)) {
                // oy the player is not where they should be, find this mf

                for (Channel ch : getChannels()) {
                    if (ch.removePlayer(chr)) {
                        break;
                    }
                }
            }
        }

        players.removePlayer(chr.getId());
    }

    /**
     * 添加家族到世界。
     *
     * @param id 家族ID
     * @param f 家族对象
     */
    public void addFamily(int id, Family f) {
        synchronized (families) {
            if (!families.containsKey(id)) {
                families.put(id, f);
            }
        }
    }

    /**
     * 从世界中移除家族。
     *
     * @param id 家族ID
     */
    public void removeFamily(int id) {
        synchronized (families) {
            families.remove(id);
        }
    }

    /**
     * 获取指定ID的家族。
     *
     * @param id 家族ID
     * @return 家族对象，如果不存在则返回null
     */
    public Family getFamily(int id) {
        synchronized (families) {
            if (families.containsKey(id)) {
                return families.get(id);
            }
            return null;
        }
    }

    /**
     * 获取所有家族的集合视图。
     *
     * @return 不可修改的家族集合
     */
    public Collection<Family> getFamilies() {
        synchronized (families) {
            return Collections.unmodifiableCollection(families.values());
        }
    }

    /**
     * 根据公会角色获取公会对象，并缓存公会摘要。
     *
     * @param mgc 公会角色对象
     * @return 公会对象，如果不存在则返回null
     */
    public Guild getGuild(GuildCharacter mgc) {
        if (mgc == null) {
            return null;
        }

        int gid = mgc.getGuildId();
        Guild g = Server.getInstance().getGuild(gid, mgc.getWorld(), mgc.getCharacter());
        if (gsStore.get(gid) == null) {
            gsStore.put(gid, new GuildSummary(g));
        }
        return g;
    }

    /**
     * 检查世界是否已满员。
     *
     * @return 已满员返回true，否则返回false
     */
    public boolean isWorldCapacityFull() {
        return getWorldCapacityStatus() == 2;
    }

    /**
     * 获取世界容量状态。
     *
     * @return 0=正常, 1=繁忙(80%以上), 2=已满
     */
    public int getWorldCapacityStatus() {
        int worldCap = getChannelsSize() * GameConfig.getServerInt("channel_capacity");
        int num = players.getSize();

        int status;
        if (num >= worldCap) {
            status = 2;
        } else if (num >= worldCap * .8) { // More than 80 percent o___o
            status = 1;
        } else {
            status = 0;
        }

        return status;
    }

    /**
     * 获取公会摘要。如果缓存中不存在，则从服务器获取并缓存。
     *
     * @param gid 公会ID
     * @param wid 世界ID
     * @return 公会摘要对象，如果不存在则返回null
     */
    public GuildSummary getGuildSummary(int gid, int wid) {
        if (gsStore.containsKey(gid)) {
            return gsStore.get(gid);
        } else {
            Guild g = Server.getInstance().getGuild(gid, wid, null);
            if (g != null) {
                gsStore.put(gid, new GuildSummary(g));
            }
            return gsStore.get(gid);
        }
    }

    /**
     * 更新公会摘要缓存。
     *
     * @param gid 公会ID
     * @param mgs 公会摘要对象
     */
    public void updateGuildSummary(int gid, GuildSummary mgs) {
        gsStore.put(gid, mgs);
    }

    /**
     * 重新加载所有公会摘要缓存。
     */
    public void reloadGuildSummary() {
        Guild g;
        Server server = Server.getInstance();
        for (int i : gsStore.keySet()) {
            g = server.getGuild(i, getId(), null);
            if (g != null) {
                gsStore.put(i, new GuildSummary(g));
            } else {
                gsStore.remove(i);
            }
        }
    }

    /**
     * 批量设置角色的公会和职位。
     *
     * @param cids 角色ID列表
     * @param guildid 公会ID
     * @param rank 职位
     * @param exception 例外角色ID（跳过该角色）
     */
    public void setGuildAndRank(List<Integer> cids, int guildid, int rank, int exception) {
        for (int cid : cids) {
            if (cid != exception) {
                setGuildAndRank(cid, guildid, rank);
            }
        }
    }

    public void setOfflineGuildStatus(int guildid, int guildrank, int cid) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?")) {
            ps.setInt(1, guildid);
            ps.setInt(2, guildrank);
            ps.setInt(3, cid);
            ps.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void setGuildAndRank(int cid, int guildid, int rank) {
        Character mc = getPlayerStorage().getCharacterById(cid);
        if (mc == null) {
            return;
        }
        boolean bDifferentGuild;
        if (guildid == -1 && rank == -1) {
            bDifferentGuild = true;
        } else {
            bDifferentGuild = guildid != mc.getGuildId();
            mc.getMGC().setGuildId(guildid);
            mc.getMGC().setGuildRank(rank);

            if (bDifferentGuild) {
                mc.getMGC().setAllianceRank(5);
            }

            mc.saveGuildStatus();
        }
        if (bDifferentGuild) {
            if (mc.isLoggedInWorld()) {
                Guild guild = Server.getInstance().getGuild(guildid);
                if (guild != null) {
                    mc.getMap().broadcastPacket(mc, GuildPackets.guildNameChanged(cid, guild.getName()));
                    mc.getMap().broadcastPacket(mc, GuildPackets.guildMarkChanged(cid, guild));
                } else {
                    mc.getMap().broadcastPacket(mc, GuildPackets.guildNameChanged(cid, ""));
                }
            }
        }
    }

    /**
     * 修改公会徽章，并通知所有受影响的玩家。
     *
     * @param gid 公会ID
     * @param affectedPlayers 受影响的玩家ID列表
     * @param mgs 更新后的公会摘要
     */
    public void changeEmblem(int gid, List<Integer> affectedPlayers, GuildSummary mgs) {
        updateGuildSummary(gid, mgs);
        sendPacket(affectedPlayers, GuildPackets.guildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor()), -1);
        setGuildAndRank(affectedPlayers, -1, -1, -1);    //respawn player
    }

    /**
     * 向指定玩家列表发送数据包。
     *
     * @param targetIds 目标玩家ID列表
     * @param packet 要发送的数据包
     * @param exception 例外玩家ID（跳过该玩家）
     */
    public void sendPacket(List<Integer> targetIds, Packet packet, int exception) {
        Character chr;
        for (int i : targetIds) {
            if (i == exception) {
                continue;
            }
            chr = getPlayerStorage().getCharacterById(i);
            if (chr != null) {
                chr.sendPacket(packet);
            }
        }
    }

    /**
     * 检查公会是否在队列中。
     *
     * @param guildId 公会ID
     * @return 在队列中返回true，否则返回false
     */
    public boolean isGuildQueued(int guildId) {
        return queuedGuilds.contains(guildId);
    }

    /**
     * 将公会加入队列。
     *
     * @param guildId 公会ID
     */
    public void putGuildQueued(int guildId) {
        queuedGuilds.add(guildId);
    }

    /**
     * 将公会从队列中移除。
     *
     * @param guildId 公会ID
     */
    public void removeGuildQueued(int guildId) {
        queuedGuilds.remove(guildId);
    }

    /**
     * 检查婚礼是否在队列中。
     *
     * @param marriageId 婚礼ID
     * @return 在队列中返回true，否则返回false
     */
    public boolean isMarriageQueued(int marriageId) {
        return queuedMarriages.containsKey(marriageId);
    }

    public Pair<Boolean, Boolean> getMarriageQueuedLocation(int marriageId) {
        Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>> qm = queuedMarriages.get(marriageId);
        return (qm != null) ? qm.getLeft() : null;
    }

    public Pair<Integer, Integer> getMarriageQueuedCouple(int marriageId) {
        Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>> qm = queuedMarriages.get(marriageId);
        return (qm != null) ? qm.getRight() : null;
    }

    public void putMarriageQueued(int marriageId, boolean cathedral, boolean premium, int groomId, int brideId) {
        queuedMarriages.put(marriageId, new Pair<>(new Pair<>(cathedral, premium), new Pair<>(groomId, brideId)));
        marriageGuests.put(marriageId, new HashSet<>());
    }

    public Pair<Boolean, Set<Integer>> removeMarriageQueued(int marriageId) {
        Boolean type = queuedMarriages.remove(marriageId).getLeft().getRight();
        Set<Integer> guests = marriageGuests.remove(marriageId);

        return new Pair<>(type, guests);
    }

    public boolean addMarriageGuest(int marriageId, int playerId) {
        Set<Integer> guests = marriageGuests.get(marriageId);
        if (guests != null) {
            if (guests.contains(playerId)) {
                return false;
            }

            guests.add(playerId);
            return true;
        }

        return false;
    }

    public Pair<Integer, Integer> getWeddingCoupleForGuest(int guestId, Boolean cathedral) {
        for (Channel ch : getChannels()) {
            Pair<Integer, Integer> p = ch.getWeddingCoupleForGuest(guestId, cathedral);
            if (p != null) {
                return p;
            }
        }

        List<Integer> possibleWeddings = new LinkedList<>();
        for (Entry<Integer, Set<Integer>> mg : new HashSet<>(marriageGuests.entrySet())) {
            if (mg.getValue().contains(guestId)) {
                Pair<Boolean, Boolean> loc = getMarriageQueuedLocation(mg.getKey());
                if (loc != null && cathedral.equals(loc.getLeft())) {
                    possibleWeddings.add(mg.getKey());
                }
            }
        }

        int pwSize = possibleWeddings.size();
        if (pwSize == 0) {
            return null;
        } else if (pwSize > 1) {
            int selectedPw = -1;
            int selectedPos = Integer.MAX_VALUE;

            for (Integer pw : possibleWeddings) {
                for (Channel ch : getChannels()) {
                    int pos = ch.getWeddingReservationStatus(pw, cathedral);
                    if (pos != -1) {
                        if (pos < selectedPos) {
                            selectedPos = pos;
                            selectedPw = pw;
                            break;
                        }
                    }
                }
            }

            if (selectedPw == -1) {
                return null;
            }

            possibleWeddings.clear();
            possibleWeddings.add(selectedPw);
        }

        return getMarriageQueuedCouple(possibleWeddings.getFirst());
    }

    public void debugMarriageStatus() {
        log.debug("Queued marriages: {}", queuedMarriages);
        log.debug("Guest list: {}", marriageGuests);
    }

    private void registerCharacterParty(Integer chrid, Integer partyid) {
        partyLock.lock();
        try {
            partyChars.put(chrid, partyid);
        } finally {
            partyLock.unlock();
        }
    }

    private void unregisterCharacterPartyInternal(Integer chrid) {
        partyChars.remove(chrid);
    }

    private void unregisterCharacterParty(Integer chrid) {
        partyLock.lock();
        try {
            unregisterCharacterPartyInternal(chrid);
        } finally {
            partyLock.unlock();
        }
    }

    public Integer getCharacterPartyid(Integer chrid) {
        partyLock.lock();
        try {
            return partyChars.get(chrid);
        } finally {
            partyLock.unlock();
        }
    }

    /**
     * 创建新的队伍。
     *
     * @param chrfor 创建队伍的角色
     * @return 创建的队伍对象
     */
    public Party createParty(PartyCharacter chrfor) {
        int partyid = runningPartyId.getAndIncrement();
        Party party = new Party(partyid, chrfor);

        partyLock.lock();
        try {
            parties.put(party.getId(), party);
            registerCharacterParty(chrfor.getId(), partyid);
        } finally {
            partyLock.unlock();
        }

        party.addMember(chrfor);
        return party;
    }

    /**
     * 根据队伍ID获取队伍对象。
     *
     * @param partyid 队伍ID
     * @return 队伍对象，如果不存在则返回null
     */
    public Party getParty(int partyid) {
        partyLock.lock();
        try {
            return parties.get(partyid);
        } finally {
            partyLock.unlock();
        }
    }

    /**
     * 解散队伍。
     *
     * @param partyid 队伍ID
     * @return 被解散的队伍对象
     */
    private Party disbandParty(int partyid) {
        partyLock.lock();
        try {
            return parties.remove(partyid);
        } finally {
            partyLock.unlock();
        }
    }

    /**
     * 更新角色与队伍的关联关系。
     *
     * @param party 队伍对象
     * @param operation 操作类型
     * @param target 目标角色
     * @param partyMembers 队伍成员列表
     */
    private void updateCharacterParty(Party party, PartyOperation operation, PartyCharacter target, Collection<PartyCharacter> partyMembers) {
        // 根据队伍操作类型更新角色的队伍信息
        switch (operation) {
            // JOIN: 注册角色到队伍
            case JOIN:
                registerCharacterParty(target.getId(), party.getId());
                break;
            // LEAVE/EXPEL: 注销角色的队伍信息
            case LEAVE:
            case EXPEL:
                unregisterCharacterParty(target.getId());
                break;
            // DISBAND: 解散队伍，注销所有成员的队伍信息
            case DISBAND:
                partyLock.lock();
                try {
                    for (PartyCharacter partychar : partyMembers) {
                        unregisterCharacterPartyInternal(partychar.getId());
                    }
                } finally {
                    partyLock.unlock();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 更新队伍状态并通知所有成员。
     *
     * @param party 队伍对象
     * @param operation 操作类型
     * @param target 目标角色
     */
    private void updateParty(Party party, PartyOperation operation, PartyCharacter target) {
        Collection<PartyCharacter> partyMembers = party.getMembers();
        updateCharacterParty(party, operation, target, partyMembers);

        for (PartyCharacter partychar : partyMembers) {
            Character chr = getPlayerStorage().getCharacterById(partychar.getId());
            if (chr != null) {
                if (operation == PartyOperation.DISBAND) {
                    chr.setParty(null);
                    chr.setMPC(null);
                } else {
                    chr.setParty(party);
                    chr.setMPC(partychar);
                }
                chr.sendPacket(PacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
            }
        }
        // 通知离开/踢出的成员更新其本地队伍状态
        switch (operation) {
            // LEAVE/EXPEL: 通知目标角色离开队伍
            case LEAVE:
            case EXPEL:
                Character chr = getPlayerStorage().getCharacterById(target.getId());
                if (chr != null) {
                    chr.sendPacket(PacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
                    chr.setParty(null);
                    chr.setMPC(null);
                }
            default:
                break;
        }
    }

    /**
     * 更新队伍状态。
     *
     * @param partyid 队伍ID
     * @param operation 操作类型
     * @param target 目标角色
     * @throws IllegalArgumentException 如果队伍不存在
     */
    public void updateParty(int partyid, PartyOperation operation, PartyCharacter target) {
        Party party = getParty(partyid);
        if (party == null) {
            throw new IllegalArgumentException("no party with the specified partyid exists");
        }
        // 根据队伍操作类型更新队伍成员
        switch (operation) {
            // JOIN: 添加成员到队伍
            case JOIN:
                party.addMember(target);
                break;
            // EXPEL/LEAVE: 从队伍移除成员
            case EXPEL:
            case LEAVE:
                party.removeMember(target);
                break;
            // DISBAND: 解散队伍
            case DISBAND:
                disbandParty(partyid);
                break;
            // SILENT_UPDATE/LOG_ONOFF: 更新成员信息
            case SILENT_UPDATE:
            case LOG_ONOFF:
                party.updateMember(target);
                break;
            case CHANGE_LEADER:
                Character mc = party.getLeader().getPlayer();
                if (mc != null) {
                    EventInstanceManager eim = mc.getEventInstance();

                    if (eim != null && eim.isEventLeader(mc)) {
                        eim.changedLeader(target);
                    } else {
                        int oldLeaderMapid = mc.getMapId();

                        if (MiniDungeonInfo.isDungeonMap(oldLeaderMapid)) {
                            if (oldLeaderMapid != target.getMapId()) {
                                MiniDungeon mmd = mc.getClient().getChannelServer().getMiniDungeon(oldLeaderMapid);
                                if (mmd != null) {
                                    mmd.close();
                                }
                            }
                        }
                    }
                    party.setLeader(target);
                }
                break;
            default:
                log.warn("Unhandled updateParty operation: {}", operation.name());
        }
        updateParty(party, operation, target);
    }

    /**
     * 从所有成员所在地图中移除队伍标记。
     *
     * @param partyid 队伍ID
     */
    public void removeMapPartyMembers(int partyid) {
        Party party = getParty(partyid);
        if (party == null) {
            return;
        }

        for (PartyCharacter mpc : party.getMembers()) {
            Character mc = mpc.getPlayer();
            if (mc != null) {
                MapleMap map = mc.getMap();
                if (map != null) {
                    map.removeParty(partyid);
                }
            }
        }
    }

    /**
     * 根据角色名称查找角色所在频道。
     *
     * @param name 角色名称
     * @return 频道号，如果未找到返回-1
     */
    public int find(String name) {
        int channel = -1;
        Character chr = getPlayerStorage().getCharacterByName(name);
        if (chr != null) {
            channel = chr.getClient().getChannel();
        }
        return channel;
    }

    /**
     * 根据角色ID查找角色所在频道。
     *
     * @param id 角色ID
     * @return 频道号，如果未找到返回-1
     */
    public int find(int id) {
        int channel = -1;
        Character chr = getPlayerStorage().getCharacterById(id);
        if (chr != null) {
            channel = chr.getClient().getChannel();
        }
        return channel;
    }

    /**
     * 发送队伍聊天消息。
     *
     * @param party 队伍对象
     * @param chattext 聊天内容
     * @param namefrom 发送者名称
     */
    public void partyChat(Party party, String chattext, String namefrom) {
        for (PartyCharacter partychar : party.getMembers()) {
            if (!(partychar.getName().equals(namefrom))) {
                Character chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    chr.sendPacket(PacketCreator.multiChat(namefrom, chattext, 1));
                }
            }
        }
    }

    /**
     * 发送好友聊天消息。
     *
     * @param recipientCharacterIds 接收者角色ID数组
     * @param cidFrom 发送者角色ID
     * @param nameFrom 发送者名称
     * @param chattext 聊天内容
     */
    public void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
        PlayerStorage playerStorage = getPlayerStorage();
        for (int characterId : recipientCharacterIds) {
            Character chr = playerStorage.getCharacterById(characterId);
            if (chr != null) {
                if (chr.getBuddylist().containsVisible(cidFrom)) {
                    chr.sendPacket(PacketCreator.multiChat(nameFrom, chattext, 0));
                }
            }
        }
    }

    /**
     * 批量查找多个角色所在的频道。
     *
     * @param charIdFrom 查询者角色ID
     * @param characterIds 要查找的角色ID数组
     * @return 角色ID和频道的配对数组
     */
    public CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, int[] characterIds) {
        List<CharacterIdChannelPair> foundsChars = new ArrayList<>(characterIds.length);
        for (Channel ch : getChannels()) {
            for (int charid : ch.multiBuddyFind(charIdFrom, characterIds)) {
                foundsChars.add(new CharacterIdChannelPair(charid, ch.getId()));
            }
        }
        return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
    }

    /**
     * 根据信使ID获取信使对象。
     *
     * @param messengerid 信使ID
     * @return 信使对象，如果不存在则返回null
     */
    public Messenger getMessenger(int messengerid) {
        return messengers.get(messengerid);
    }

    /**
     * 离开信使会话。
     *
     * @param messengerid 信使ID
     * @param target 要离开的角色
     * @throws IllegalArgumentException 如果信使不存在
     */
    public void leaveMessenger(int messengerid, MessengerCharacter target) {
        Messenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        int position = messenger.getPositionByName(target.getName());
        messenger.removeMember(target);
        removeMessengerPlayer(messenger, position);
    }

    /**
     * 邀请玩家加入信使会话。
     *
     * @param sender 发送邀请者名称
     * @param messengerid 信使ID
     * @param target 被邀请者名称
     * @param fromchannel 发送者所在频道
     */
    public void messengerInvite(String sender, int messengerid, String target, int fromchannel) {
        if (isConnected(target)) {
            Character targetChr = getPlayerStorage().getCharacterByName(target);
            if (targetChr != null) {
                Messenger messenger = targetChr.getMessenger();
                if (messenger == null) {
                    Character from = getChannel(fromchannel).getPlayerStorage().getCharacterByName(sender);
                    if (from != null) {
                        if (InviteCoordinator.createInvite(InviteType.MESSENGER, from, messengerid, targetChr.getId())) {
                            targetChr.sendPacket(PacketCreator.messengerInvite(sender, messengerid));
                            from.sendPacket(PacketCreator.messengerNote(target, 4, 1));
                        } else {
                            from.sendPacket(PacketCreator.messengerChat(sender + " : " + target + " is already managing a Maple Messenger invitation"));
                        }
                    }
                } else {
                    Character from = getChannel(fromchannel).getPlayerStorage().getCharacterByName(sender);
                    from.sendPacket(PacketCreator.messengerChat(sender + " : " + target + " is already using Maple Messenger"));
                }
            }
        }
    }

    /**
     * 添加信使成员并通知其他成员。
     *
     * @param messenger 信使对象
     * @param namefrom 加入者名称
     * @param fromchannel 加入者所在频道
     * @param position 加入者位置
     */
    public void addMessengerPlayer(Messenger messenger, String namefrom, int fromchannel, int position) {
        for (MessengerCharacter messengerchar : messenger.getMembers()) {
            Character chr = getPlayerStorage().getCharacterByName(messengerchar.getName());
            if (chr == null) {
                continue;
            }
            if (!messengerchar.getName().equals(namefrom)) {
                Character from = getChannel(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
                chr.sendPacket(PacketCreator.addMessengerPlayer(namefrom, from, position, (byte) (fromchannel - 1)));
                from.sendPacket(PacketCreator.addMessengerPlayer(chr.getName(), chr, messengerchar.getPosition(), (byte) (messengerchar.getChannel() - 1)));
            } else {
                chr.sendPacket(PacketCreator.joinMessenger(messengerchar.getPosition()));
            }
        }
    }

    /**
     * 移除信使成员并通知其他成员。
     *
     * @param messenger 信使对象
     * @param position 被移除成员的位置
     */
    public void removeMessengerPlayer(Messenger messenger, int position) {
        for (MessengerCharacter messengerchar : messenger.getMembers()) {
            Character chr = getPlayerStorage().getCharacterByName(messengerchar.getName());
            if (chr != null) {
                chr.sendPacket(PacketCreator.removeMessengerPlayer(position));
            }
        }
    }

    /**
     * 发送信使聊天消息。
     *
     * @param messenger 信使对象
     * @param chattext 聊天内容
     * @param namefrom 发送者名称
     */
    public void messengerChat(Messenger messenger, String chattext, String namefrom) {
        String from = "";
        String to1 = "";
        String to2 = "";
        for (MessengerCharacter messengerchar : messenger.getMembers()) {
            if (!(messengerchar.getName().equals(namefrom))) {
                Character chr = getPlayerStorage().getCharacterByName(messengerchar.getName());
                if (chr != null) {
                    chr.sendPacket(PacketCreator.messengerChat(chattext));
                    if (to1.isEmpty()) {
                        to1 = messengerchar.getName();
                    } else if (to2.isEmpty()) {
                        to2 = messengerchar.getName();
                    }
                }
            } else {
                from = messengerchar.getName();
            }
        }
    }

    /**
     * 拒绝信使邀请。
     *
     * @param sender 邀请者名称
     * @param player 被邀请者角色
     */
    public void declineChat(String sender, Character player) {
        if (isConnected(sender)) {
            Character senderChr = getPlayerStorage().getCharacterByName(sender);
            if (senderChr != null && senderChr.getMessenger() != null) {
                if (InviteCoordinator.answerInvite(InviteType.MESSENGER, player.getId(), senderChr.getMessenger().getId(), false).result == InviteResultType.DENIED) {
                    senderChr.sendPacket(PacketCreator.messengerNote(player.getName(), 5, 0));
                }
            }
        }
    }

    /**
     * 更新信使成员信息。
     *
     * @param messengerid 信使ID
     * @param namefrom 更新者名称
     * @param fromchannel 更新者所在频道
     */
    public void updateMessenger(int messengerid, String namefrom, int fromchannel) {
        Messenger messenger = getMessenger(messengerid);
        int position = messenger.getPositionByName(namefrom);
        updateMessenger(messenger, namefrom, position, fromchannel);
    }

    /**
     * 更新信使成员信息（重载方法）。
     *
     * @param messenger 信使对象
     * @param namefrom 更新者名称
     * @param position 更新者位置
     * @param fromchannel 更新者所在频道
     */
    public void updateMessenger(Messenger messenger, String namefrom, int position, int fromchannel) {
        for (MessengerCharacter messengerchar : messenger.getMembers()) {
            Channel ch = getChannel(fromchannel);
            if (!(messengerchar.getName().equals(namefrom))) {
                Character chr = ch.getPlayerStorage().getCharacterByName(messengerchar.getName());
                if (chr != null) {
                    chr.sendPacket(PacketCreator.updateMessengerPlayer(namefrom, getChannel(fromchannel).getPlayerStorage().getCharacterByName(namefrom), position, (byte) (fromchannel - 1)));
                }
            }
        }
    }

    /**
     * 静默离开信使会话（不通知其他成员）。
     *
     * @param messengerid 信使ID
     * @param target 要离开的角色
     * @throws IllegalArgumentException 如果信使不存在
     */
    public void silentLeaveMessenger(int messengerid, MessengerCharacter target) {
        Messenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, target.getPosition());
    }

    /**
     * 加入信使会话。
     *
     * @param messengerid 信使ID
     * @param target 要加入的角色
     * @param from 邀请者名称
     * @param fromchannel 邀请者所在频道
     * @throws IllegalArgumentException 如果信使不存在
     */
    public void joinMessenger(int messengerid, MessengerCharacter target, String from, int fromchannel) {
        Messenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, target.getPosition());
        addMessengerPlayer(messenger, from, fromchannel, target.getPosition());
    }

    /**
     * 静默加入信使会话（不通知其他成员）。
     *
     * @param messengerid 信使ID
     * @param target 要加入的角色
     * @param position 位置
     * @throws IllegalArgumentException 如果信使不存在
     */
    public void silentJoinMessenger(int messengerid, MessengerCharacter target, int position) {
        Messenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, position);
    }

    /**
     * 创建新的信使会话。
     *
     * @param chrfor 创建信使的角色
     * @return 创建的信使对象
     */
    public Messenger createMessenger(MessengerCharacter chrfor) {
        int messengerid = runningMessengerId.getAndIncrement();
        Messenger messenger = new Messenger(messengerid, chrfor);
        messengers.put(messenger.getId(), messenger);
        return messenger;
    }

    /**
     * 检查角色是否在线。
     *
     * @param charName 角色名称
     * @return 在线返回true，否则返回false
     */
    public boolean isConnected(String charName) {
        return getPlayerStorage().getCharacterByName(charName) != null;
    }

    /**
     * 请求添加好友。
     *
     * @param addName 被添加者名称
     * @param channelFrom 添加者所在频道
     * @param cidFrom 添加者角色ID
     * @param nameFrom 添加者名称
     * @return 添加结果
     */
    public BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom) {
        Character addChar = getPlayerStorage().getCharacterByName(addName);
        if (addChar != null) {
            BuddyList buddylist = addChar.getBuddylist();
            // 检查好友列表是否已满
            if (buddylist.isFull()) {
                return BuddyAddResult.BUDDYLIST_FULL;
            }
            // 如果好友列表中不存在，则发送好友请求
            if (!buddylist.contains(cidFrom)) {
                buddylist.addBuddyRequest(addChar.getClient(), cidFrom, nameFrom, channelFrom);
            } else if (buddylist.containsVisible(cidFrom)) {
                // 已在好友列表中
                return BuddyAddResult.ALREADY_ON_LIST;
            }
        }
        return BuddyAddResult.OK;
    }

    /**
     * 好友关系变更处理。
     *
     * @param cid 目标角色ID
     * @param cidFrom 变更者角色ID
     * @param name 变更者名称
     * @param channel 变更者频道
     * @param operation 操作类型（添加/删除）
     */
    public void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyOperation operation) {
        Character addChar = getPlayerStorage().getCharacterById(cid);
        if (addChar != null) {
            BuddyList buddylist = addChar.getBuddylist();
            switch (operation) {
                case ADDED:
                    if (buddylist.contains(cidFrom)) {
                        buddylist.put(new BuddylistEntry(name, "Default Group", cidFrom, channel, true));
                        addChar.sendPacket(PacketCreator.updateBuddyChannel(cidFrom, (byte) (channel - 1)));
                    }
                    break;
                case DELETED:
                    if (buddylist.contains(cidFrom)) {
                        buddylist.put(new BuddylistEntry(name, "Default Group", cidFrom, (byte) -1, buddylist.get(cidFrom).isVisible()));
                        addChar.sendPacket(PacketCreator.updateBuddyChannel(cidFrom, (byte) -1));
                    }
                    break;
            }
        }
    }

    /**
     * 处理角色下线，更新好友列表状态。
     *
     * @param name 角色名称
     * @param characterId 角色ID
     * @param channel 所在频道
     * @param buddies 好友ID数组
     */
    public void loggedOff(String name, int characterId, int channel, int[] buddies) {
        updateBuddies(characterId, channel, buddies, true);
    }

    /**
     * 处理角色上线，更新好友列表状态。
     *
     * @param name 角色名称
     * @param characterId 角色ID
     * @param channel 所在频道
     * @param buddies 好友ID数组
     */
    public void loggedOn(String name, int characterId, int channel, int[] buddies) {
        updateBuddies(characterId, channel, buddies, false);
    }

    /**
     * 更新好友列表状态。
     *
     * @param characterId 角色ID
     * @param channel 所在频道
     * @param buddies 好友ID数组
     * @param offline 是否离线
     */
    private void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
        PlayerStorage playerStorage = getPlayerStorage();
        // 遍历所有好友，更新他们的好友列表
        for (int buddy : buddies) {
            Character chr = playerStorage.getCharacterById(buddy);
            if (chr != null) {
                BuddylistEntry ble = chr.getBuddylist().get(characterId);
                if (ble != null && ble.isVisible()) {
                    int mcChannel;
                    if (offline) {
                        // 离线：频道设为-1
                        ble.setChannel((byte) -1);
                        mcChannel = -1;
                    } else {
                        // 在线：设置当前频道
                        ble.setChannel(channel);
                        mcChannel = (byte) (channel - 1);
                    }
                    chr.getBuddylist().put(ble);
                    chr.sendPacket(PacketCreator.updateBuddyChannel(ble.getCharacterId(), mcChannel));
                }
            }
        }
    }

    /**
     * 生成宠物的唯一键值（假设最多3只宠物）。
     * 使用位运算：角色ID左移2位（乘以4）加上宠物槽位（0-3）。
     *
     * @param chr 角色对象
     * @param petSlot 宠物槽位
     * @return 宠物唯一键值
     */
    private static Integer getPetKey(Character chr, byte petSlot) {
        return (chr.getId() << 2) + petSlot;
    }

    /**
     * 记录猫头鹰搜索物品。
     *
     * @param itemid 物品ID
     */
    public void addOwlItemSearch(Integer itemid) {
        suggestWLock.lock();
        try {
            owlSearched.merge(itemid, 1, Integer::sum);
        } finally {
            suggestWLock.unlock();
        }
    }

    public List<Pair<Integer, Integer>> getOwlSearchedItems() {
        if (GameConfig.getServerBoolean("use_enforce_item_suggestion")) {
            return new ArrayList<>(0);
        }

        suggestRLock.lock();
        try {
            List<Pair<Integer, Integer>> searchCounts = new ArrayList<>(owlSearched.size());

            for (Entry<Integer, Integer> e : owlSearched.entrySet()) {
                searchCounts.add(new Pair<>(e.getKey(), e.getValue()));
            }

            return searchCounts;
        } finally {
            suggestRLock.unlock();
        }
    }

    public void addCashItemBought(Integer snid) {
        suggestWLock.lock();
        try {
            Map<Integer, Integer> tabItemBought = cashItemBought.get(snid / 10000000);
            tabItemBought.merge(snid, 1, Integer::sum);
        } finally {
            suggestWLock.unlock();
        }
    }

    private List<List<Pair<Integer, Integer>>> getBoughtCashItems() {
        if (GameConfig.getServerBoolean("use_enforce_item_suggestion")) {
            List<List<Pair<Integer, Integer>>> boughtCounts = new ArrayList<>(9);

            // thanks GabrielSin for pointing out an issue here
            for (int i = 0; i < 9; i++) {
                List<Pair<Integer, Integer>> tabCounts = new ArrayList<>(0);
                boughtCounts.add(tabCounts);
            }

            return boughtCounts;
        }

        suggestRLock.lock();
        try {
            List<List<Pair<Integer, Integer>>> boughtCounts = new ArrayList<>(cashItemBought.size());

            for (Map<Integer, Integer> tab : cashItemBought) {
                List<Pair<Integer, Integer>> tabItems = new LinkedList<>();
                boughtCounts.add(tabItems);

                for (Entry<Integer, Integer> e : tab.entrySet()) {
                    tabItems.add(new Pair<>(e.getKey(), e.getValue()));
                }
            }

            return boughtCounts;
        } finally {
            suggestRLock.unlock();
        }
    }

    /**
     * 获取某个分类下销量最高的物品（取前5名）。
     *
     * @param tabSellers 分类物品销量列表
     * @return 销量最高的物品ID列表
     */
    private List<Integer> getMostSellerOnTab(List<Pair<Integer, Integer>> tabSellers) {
        List<Integer> tabLeaderboards;

        // 按销量降序排序
        Comparator<Pair<Integer, Integer>> comparator = (p1, p2) -> p2.getRight().compareTo(p1.getRight());

        PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>(Math.max(1, tabSellers.size()), comparator);
        queue.addAll(tabSellers);

        tabLeaderboards = new LinkedList<>();
        // 取前5名
        for (int i = 0; i < Math.min(tabSellers.size(), 5); i++) {
            tabLeaderboards.add(queue.remove().getLeft());
        }

        return tabLeaderboards;
    }

    /**
     * 获取所有分类下销量最高的现金道具。
     *
     * @return 各分类销量前5的物品ID列表
     */
    public List<List<Integer>> getMostSellerCashItems() {
        List<List<Pair<Integer, Integer>>> mostSellers = this.getBoughtCashItems();
        List<List<Integer>> cashLeaderboards = new ArrayList<>(9);
        List<Integer> tabLeaderboards;
        List<Integer> allLeaderboards = null;

        for (List<Pair<Integer, Integer>> tabSellers : mostSellers) {
            if (tabSellers.size() < 5) {
                if (allLeaderboards == null) {
                    List<Pair<Integer, Integer>> allSellers = new LinkedList<>();
                    for (List<Pair<Integer, Integer>> tabItems : mostSellers) {
                        allSellers.addAll(tabItems);
                    }

                    allLeaderboards = getMostSellerOnTab(allSellers);
                }

                tabLeaderboards = new LinkedList<>();
                if (allLeaderboards.size() < 5) {
                    for (int i : GameConstants.CASH_DATA) {
                        tabLeaderboards.add(i);
                    }
                } else {
                    tabLeaderboards.addAll(allLeaderboards);
                }
            } else {
                tabLeaderboards = getMostSellerOnTab(tabSellers);
            }

            cashLeaderboards.add(tabLeaderboards);
        }

        return cashLeaderboards;
    }

    /**
     * 注册宠物饱食度追踪。
     * 如果GM设置了宠物不饿或全局宠物不饿，则不注册。
     *
     * @param chr 角色对象
     * @param petSlot 宠物槽位
     */
    public void registerPetHunger(Character chr, byte petSlot) {
        // GM宠物不饿或全局宠物不饿设置
        if (chr.isGM() && GameConfig.getServerBoolean("gm_pets_never_hungry") || GameConfig.getServerBoolean("pets_never_hungry")) {
            return;
        }

        Integer key = getPetKey(chr, petSlot);

        activePetsLock.lock();
        try {
            int initProc;
            // 根据上次更新时间决定初始值
            if (Server.getInstance().getCurrentTime() - petUpdate > 55000) {
                initProc = GameConfig.getServerInt("pet_exhaust_count") - 2;
            } else {
                initProc = GameConfig.getServerInt("pet_exhaust_count") - 1;
            }

            activePets.put(key, initProc);
        } finally {
            activePetsLock.unlock();
        }
    }

    /**
     * 取消注册宠物饱食度追踪。
     *
     * @param chr 角色对象
     * @param petSlot 宠物槽位
     */
    public void unregisterPetHunger(Character chr, byte petSlot) {
        Integer key = getPetKey(chr, petSlot);

        activePetsLock.lock();
        try {
            activePets.remove(key);
        } finally {
            activePetsLock.unlock();
        }
    }

    /**
     * 执行宠物饱食度定时任务。
     * 遍历所有活跃宠物，更新饱食度计数，达到阈值时触发饱食度恢复。
     */
    public void runPetSchedule() {
        Map<Integer, Integer> deployedPets;

        activePetsLock.lock();
        try {
            petUpdate = Server.getInstance().getCurrentTime();
            deployedPets = new HashMap<>(activePets);
        } finally {
            activePetsLock.unlock();
        }

        // 遍历所有活跃宠物
        for (Map.Entry<Integer, Integer> dp : deployedPets.entrySet()) {
            // 从键中提取角色ID（键格式：(角色ID << 2) + 宠物槽位）
            Character chr = this.getPlayerStorage().getCharacterById(dp.getKey() / 4);
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            // 饱食度计数+1
            int dpVal = dp.getValue() + 1;
            // 达到阈值时触发饱食度恢复
            if (dpVal == GameConfig.getServerInt("pet_exhaust_count")) {
                chr.runFullnessSchedule(dp.getKey() % 4);
                dpVal = 0;
            }

            activePetsLock.lock();
            try {
                activePets.put(dp.getKey(), dpVal);
            } finally {
                activePetsLock.unlock();
            }
        }
    }

    /**
     * 注册坐骑疲劳度追踪。
     *
     * @param chr 角色对象
     */
    public void registerMountHunger(Character chr) {
        if (chr.isGM() && GameConfig.getServerBoolean("gm_pets_never_hungry") || GameConfig.getServerBoolean("pets_never_hungry")) {
            return;
        }

        Integer key = chr.getId();
        activeMountsLock.lock();
        try {
            int initProc;
            if (Server.getInstance().getCurrentTime() - mountUpdate > 45000) {
                initProc = GameConfig.getServerInt("mount_exhaust_count") - 2;
            } else {
                initProc = GameConfig.getServerInt("mount_exhaust_count") - 1;
            }

            activeMounts.put(key, initProc);
        } finally {
            activeMountsLock.unlock();
        }
    }

    public void unregisterMountHunger(Character chr) {
        Integer key = chr.getId();

        activeMountsLock.lock();
        try {
            activeMounts.remove(key);
        } finally {
            activeMountsLock.unlock();
        }
    }

    /**
     * 执行坐骑疲劳度定时任务。
     * 遍历所有活跃坐骑，更新疲劳度计数，达到阈值时触发疲劳度恢复。
     */
    public void runMountSchedule() {
        Map<Integer, Integer> deployedMounts;
        activeMountsLock.lock();
        try {
            mountUpdate = Server.getInstance().getCurrentTime();
            deployedMounts = new HashMap<>(activeMounts);
        } finally {
            activeMountsLock.unlock();
        }

        // 遍历所有活跃坐骑
        for (Map.Entry<Integer, Integer> dp : deployedMounts.entrySet()) {
            Character chr = this.getPlayerStorage().getCharacterById(dp.getKey());
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            // 疲劳度计数+1
            int dpVal = dp.getValue() + 1;
            // 达到阈值时触发疲劳度恢复
            if (dpVal == GameConfig.getServerInt("mount_exhaust_count")) {
                if (!chr.runTirednessSchedule()) {
                    continue;
                }
                dpVal = 0;
            }

            activeMountsLock.lock();
            try {
                activeMounts.put(dp.getKey(), dpVal);
            } finally {
                activeMountsLock.unlock();
            }
        }
    }

    /**
     * 注册玩家商店。
     *
     * @param ps 玩家商店对象
     */
    public void registerPlayerShop(PlayerShop ps) {
        activePlayerShopsLock.lock();
        try {
            activePlayerShops.put(ps.getOwner().getId(), ps);
        } finally {
            activePlayerShopsLock.unlock();
        }
    }

    /**
     * 取消注册玩家商店。
     *
     * @param ps 玩家商店对象
     */
    public void unregisterPlayerShop(PlayerShop ps) {
        activePlayerShopsLock.lock();
        try {
            activePlayerShops.remove(ps.getOwner().getId());
        } finally {
            activePlayerShopsLock.unlock();
        }
    }

    /**
     * 获取所有活跃的玩家商店。
     *
     * @return 玩家商店列表
     */
    public List<PlayerShop> getActivePlayerShops() {
        activePlayerShopsLock.lock();
        try {
            return new ArrayList<>(activePlayerShops.values());
        } finally {
            activePlayerShopsLock.unlock();
        }
    }

    /**
     * 根据店主ID获取玩家商店。
     *
     * @param ownerid 店主ID
     * @return 玩家商店对象，如果不存在返回null
     */
    public PlayerShop getPlayerShop(int ownerid) {
        activePlayerShopsLock.lock();
        try {
            return activePlayerShops.get(ownerid);
        } finally {
            activePlayerShopsLock.unlock();
        }
    }

    /**
     * 注册雇佣商人。
     *
     * @param hm 雇佣商人对象
     */
    public void registerHiredMerchant(HiredMerchant hm) {
        activeMerchantsLock.lock();
        try {
            int initProc;
            if (Server.getInstance().getCurrentTime() - merchantUpdate > MINUTES.toMillis(5)) {
                initProc = 1;
            } else {
                initProc = 0;
            }

            activeMerchants.put(hm.getOwnerId(), new Pair<>(hm, initProc));
        } finally {
            activeMerchantsLock.unlock();
        }
    }

    /**
     * 取消注册雇佣商人。
     *
     * @param hm 雇佣商人对象
     */
    public void unregisterHiredMerchant(HiredMerchant hm) {
        activeMerchantsLock.lock();
        try {
            activeMerchants.remove(hm.getOwnerId());
        } finally {
            activeMerchantsLock.unlock();
        }
    }

    /**
     * 执行雇佣商人定时任务。
     * 检查雇佣商人运行时间，超过24小时（1440分钟）则强制关闭。
     */
    public void runHiredMerchantSchedule() {
        Map<Integer, Pair<HiredMerchant, Integer>> deployedMerchants;
        activeMerchantsLock.lock();
        try {
            merchantUpdate = Server.getInstance().getCurrentTime();
            deployedMerchants = new LinkedHashMap<>(activeMerchants);

            // 遍历所有雇佣商人
            for (Map.Entry<Integer, Pair<HiredMerchant, Integer>> dm : deployedMerchants.entrySet()) {
                int timeOn = dm.getValue().getRight();
                HiredMerchant hm = dm.getValue().getLeft();

                // 检查是否超过24小时（1440分钟）
                if (timeOn <= 144) {
                    activeMerchants.put(hm.getOwnerId(), new Pair<>(dm.getValue().getLeft(), timeOn + 1));
                } else {
                    // 超过时间限制，强制关闭
                    hm.forceClose();
                    this.getChannel(hm.getChannel()).removeHiredMerchant(hm.getOwnerId());
                    activeMerchants.remove(dm.getKey());
                }
            }
        } finally {
            activeMerchantsLock.unlock();
        }
    }

    /**
     * 获取所有活跃的雇佣商人。
     *
     * @return 雇佣商人列表
     */
    public List<HiredMerchant> getActiveMerchants() {
        List<HiredMerchant> hmList = new ArrayList<>();
        activeMerchantsLock.lock();
        try {
            for (Pair<HiredMerchant, Integer> hmp : activeMerchants.values()) {
                HiredMerchant hm = hmp.getLeft();
                if (hm.isOpen()) {
                    hmList.add(hm);
                }
            }

            return hmList;
        } finally {
            activeMerchantsLock.unlock();
        }
    }

    public HiredMerchant getHiredMerchant(int ownerid) {
        activeMerchantsLock.lock();
        try {
            if (activeMerchants.containsKey(ownerid)) {
                return activeMerchants.get(ownerid).getLeft();
            }

            return null;
        } finally {
            activeMerchantsLock.unlock();
        }
    }

    public void registerTimedMapObject(Runnable r, long duration) {
        timedMapObjectLock.lock();
        try {
            long expirationTime = Server.getInstance().getCurrentTime() + duration;
            registeredTimedMapObjects.put(r, expirationTime);
        } finally {
            timedMapObjectLock.unlock();
        }
    }

    public void runTimedMapObjectSchedule() {
        List<Runnable> toRemove = new LinkedList<>();

        timedMapObjectLock.lock();
        try {
            long timeNow = Server.getInstance().getCurrentTime();

            for (Entry<Runnable, Long> rtmo : registeredTimedMapObjects.entrySet()) {
                if (rtmo.getValue() <= timeNow) {
                    toRemove.add(rtmo.getKey());
                }
            }

            for (Runnable r : toRemove) {
                registeredTimedMapObjects.remove(r);
            }
        } finally {
            timedMapObjectLock.unlock();
        }

        for (Runnable r : toRemove) {
            r.run();
        }
    }

    public void addPlayerHpDecrease(Character chr) {
        playerHpDec.putIfAbsent(chr, 0);
    }

    public void removePlayerHpDecrease(Character chr) {
        playerHpDec.remove(chr);
    }

    public void runPlayerHpDecreaseSchedule() {
        Map<Character, Integer> m = new HashMap<>(playerHpDec);

        for (Entry<Character, Integer> e : m.entrySet()) {
            Character chr = e.getKey();

            if (!chr.isAwayFromWorld()) {
                int c = e.getValue();
                c = (c + 1) % GameConfig.getServerInt("map_damage_overtime_count");
                playerHpDec.replace(chr, c);

                if (c == 0) {
                    chr.doHurtHp();
                }
            }
        }
    }

    public void resetDisabledServerMessages() {
        srvMessagesLock.lock();
        try {
            disabledServerMessages.clear();
        } finally {
            srvMessagesLock.unlock();
        }
    }

    public boolean registerDisabledServerMessage(int chrid) {
        srvMessagesLock.lock();
        try {
            boolean alreadyDisabled = disabledServerMessages.containsKey(chrid);
            disabledServerMessages.put(chrid, 0);

            return alreadyDisabled;
        } finally {
            srvMessagesLock.unlock();
        }
    }

    public boolean unregisterDisabledServerMessage(int chrid) {
        srvMessagesLock.lock();
        try {
            return disabledServerMessages.remove(chrid) != null;
        } finally {
            srvMessagesLock.unlock();
        }
    }

    public void runDisabledServerMessagesSchedule() {
        List<Integer> toRemove = new LinkedList<>();

        srvMessagesLock.lock();
        try {
            for (Entry<Integer, Integer> dsm : disabledServerMessages.entrySet()) {
                int b = dsm.getValue();
                if (b >= 4) {   // ~35sec duration, 10sec update
                    toRemove.add(dsm.getKey());
                } else {
                    disabledServerMessages.put(dsm.getKey(), ++b);
                }
            }

            for (Integer chrid : toRemove) {
                disabledServerMessages.remove(chrid);
            }
        } finally {
            srvMessagesLock.unlock();
        }

        if (!toRemove.isEmpty()) {
            for (Integer chrid : toRemove) {
                Character chr = players.getCharacterById(chrid);

                if (chr != null && chr.isLoggedInWorld()) {
                    chr.sendPacket(PacketCreator.serverMessage(chr.getClient().getChannelServer().getServerMessage()));
                }
            }
        }
    }

    public void setPlayerNpcMapStep(int mapid, int step) {
        setPlayerNpcMapData(mapid, step, -1, false);
    }

    public void setPlayerNpcMapPodiumData(int mapid, int podium) {
        setPlayerNpcMapData(mapid, -1, podium, false);
    }

    public void setPlayerNpcMapData(int mapid, int step, int podium) {
        setPlayerNpcMapData(mapid, step, podium, true);
    }

    private static void executePlayerNpcMapDataUpdate(boolean isPodium, Map<Integer, ?> pnpcData, int value, int worldId, int mapId) {
        PlayernpcsFieldMapper playernpcsFieldMapper = ServerManager.getApplicationContext().getBean(PlayernpcsFieldMapper.class);
        PlayernpcsFieldDO playernpcsFieldDO = new PlayernpcsFieldDO();
        if (isPodium) {
            playernpcsFieldDO.setPodium(value);
        } else {
            playernpcsFieldDO.setStep(value);
            playernpcsFieldDO.setWorld(worldId);
            playernpcsFieldDO.setMap(mapId);
        }
        if (pnpcData.containsKey(mapId)) {
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .from(PLAYERNPCS_FIELD_D_O)
                    .where(PLAYERNPCS_FIELD_D_O.WORLD.eq(worldId))
                    .and(PLAYERNPCS_FIELD_D_O.MAP.eq(mapId));
            playernpcsFieldMapper.updateByQuery(playernpcsFieldDO, queryWrapper);
        } else {
            playernpcsFieldMapper.insert(playernpcsFieldDO);
        }
    }

    private void setPlayerNpcMapData(int mapId, int step, int podium, boolean silent) {
        if (!silent) {
            if (step != -1) {
                executePlayerNpcMapDataUpdate(false, pnpcStep, step, id, mapId);
            }
            if (podium != -1) {
                executePlayerNpcMapDataUpdate(true, pnpcPodium, podium, id, mapId);
            }
        }

        if (step != -1) {
            pnpcStep.put(mapId, (byte) step);
        }
        if (podium != -1) {
            pnpcPodium.put(mapId, (short) podium);
        }
    }

    public int getPlayerNpcMapStep(int mapid) {
        try {
            return pnpcStep.get(mapid);
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getPlayerNpcMapPodiumData(int mapid) {
        try {
            return pnpcPodium.get(mapid);
        } catch (NullPointerException npe) {
            return 1;
        }
    }

    public void resetPlayerNpcMapData() {
        pnpcStep.clear();
        pnpcPodium.clear();
    }

    public void setServerMessage(String msg) {
        for (Channel ch : getChannels()) {
            ch.setServerMessage(msg);
        }
    }

    public void broadcastPacket(Packet packet) {
        for (Character chr : players.getAllCharacters()) {
            chr.sendPacket(packet);
        }
    }

    public List<Pair<PlayerShopItem, AbstractMapObject>> getAvailableItemBundles(int itemid) {
        List<Pair<PlayerShopItem, AbstractMapObject>> hmsAvailable = new ArrayList<>();

        for (HiredMerchant hm : getActiveMerchants()) {
            List<PlayerShopItem> itemBundles = hm.sendAvailableBundles(itemid);

            for (PlayerShopItem mpsi : itemBundles) {
                hmsAvailable.add(new Pair<>(mpsi, hm));
            }
        }

        for (PlayerShop ps : getActivePlayerShops()) {
            List<PlayerShopItem> itemBundles = ps.sendAvailableBundles(itemid);

            for (PlayerShopItem mpsi : itemBundles) {
                hmsAvailable.add(new Pair<>(mpsi, ps));
            }
        }

        hmsAvailable.sort((p1, p2) -> p1.getLeft().getPrice() - p2.getLeft().getPrice());

        hmsAvailable.subList(0, Math.min(hmsAvailable.size(), 200));    //truncates the list to have up to 200 elements
        return hmsAvailable;
    }

    private void pushRelationshipCouple(Pair<Integer, Pair<Integer, Integer>> couple) {
        int mid = couple.getLeft(), hid = couple.getRight().getLeft(), wid = couple.getRight().getRight();
        relationshipCouples.put(mid, couple.getRight());
        relationships.put(hid, mid);
        relationships.put(wid, mid);
    }

    public Pair<Integer, Integer> getRelationshipCouple(int relationshipId) {
        Pair<Integer, Integer> rc = relationshipCouples.get(relationshipId);

        if (rc == null) {
            Pair<Integer, Pair<Integer, Integer>> couple = getRelationshipCoupleFromDb(relationshipId, true);
            if (couple == null) {
                return null;
            }

            pushRelationshipCouple(couple);
            rc = couple.getRight();
        }

        return rc;
    }

    public int getRelationshipId(int playerId) {
        Integer ret = relationships.get(playerId);

        if (ret == null) {
            Pair<Integer, Pair<Integer, Integer>> couple = getRelationshipCoupleFromDb(playerId, false);
            if (couple == null) {
                return -1;
            }

            pushRelationshipCouple(couple);
            ret = couple.getLeft();
        }

        return ret;
    }

    private static Pair<Integer, Pair<Integer, Integer>> getRelationshipCoupleFromDb(int id, boolean usingMarriageId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            Integer mid = null, hid = null, wid = null;

            PreparedStatement ps;
            if (usingMarriageId) {
                ps = con.prepareStatement("SELECT * FROM marriages WHERE marriageid = ?");
                ps.setInt(1, id);
            } else {
                ps = con.prepareStatement("SELECT * FROM marriages WHERE husbandid = ? OR wifeid = ?");
                ps.setInt(1, id);
                ps.setInt(2, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mid = rs.getInt("marriageid");
                    hid = rs.getInt("husbandid");
                    wid = rs.getInt("wifeid");
                }
            }

            ps.close();

            return (mid == null) ? null : new Pair<>(mid, new Pair<>(hid, wid));
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public int createRelationship(int groomId, int brideId) {
        int ret = addRelationshipToDb(groomId, brideId);

        pushRelationshipCouple(new Pair<>(ret, new Pair<>(groomId, brideId)));
        return ret;
    }

    private static int addRelationshipToDb(int groomId, int brideId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO marriages (husbandid, wifeid) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, groomId);
            ps.setInt(2, brideId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return -1;
        }
    }

    public void deleteRelationship(int playerId, int partnerId) {
        int relationshipId = relationships.get(playerId);
        deleteRelationshipFromDb(relationshipId);

        relationshipCouples.remove(relationshipId);
        relationships.remove(playerId);
        relationships.remove(partnerId);
    }

    private static void deleteRelationshipFromDb(int playerId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM marriages WHERE marriageid = ?")) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void dropMessage(int type, String message) {
        for (Character player : getPlayerStorage().getAllCharacters()) {
            player.dropMessage(type, message);
        }
    }

    public boolean registerFisherPlayer(Character chr, int baitLevel) {
        synchronized (fishingAttempters) {
            if (fishingAttempters.containsKey(chr)) {
                return false;
            }

            fishingAttempters.put(chr, baitLevel);
            return true;
        }
    }

    public int unregisterFisherPlayer(Character chr) {
        Integer baitLevel = fishingAttempters.remove(chr);
        if (baitLevel != null) {
            return baitLevel;
        } else {
            return 0;
        }
    }

    public void runCheckFishingSchedule() {
        double[] fishingLikelihoods = Fishing.fetchFishingLikelihood();
        double yearLikelihood = fishingLikelihoods[0], timeLikelihood = fishingLikelihoods[1];

        if (!fishingAttempters.isEmpty()) {
            List<Character> fishingAttemptersList;

            synchronized (fishingAttempters) {
                fishingAttemptersList = new ArrayList<>(fishingAttempters.keySet());
            }

            for (Character chr : fishingAttemptersList) {
                int baitLevel = unregisterFisherPlayer(chr);
                Fishing.doFishing(chr, baitLevel, yearLikelihood, timeLikelihood);
            }
        }
    }

    public void runPartySearchUpdateSchedule() {
        partySearch.updatePartySearchStorage();
        partySearch.runPartySearch();
    }

    public BaseService getServiceAccess(WorldServices sv) {
        return services.getAccess(sv).getService();
    }

    private void closeWorldServices() {
        services.shutdown();
    }

    private void clearWorldData() {
        List<Party> pList;
        partyLock.lock();
        try {
            pList = new ArrayList<>(parties.values());
        } finally {
            partyLock.unlock();
        }

        closeWorldServices();
    }

    public final void shutdown() {
        for (Channel ch : getChannels()) {
            ch.shutdown();
        }

        if (petsSchedule != null) {
            petsSchedule.cancel(false);
            petsSchedule = null;
        }

        if (srvMessagesSchedule != null) {
            srvMessagesSchedule.cancel(false);
            srvMessagesSchedule = null;
        }

        if (mountsSchedule != null) {
            mountsSchedule.cancel(false);
            mountsSchedule = null;
        }

        if (merchantSchedule != null) {
            merchantSchedule.cancel(false);
            merchantSchedule = null;
        }

        if (timedMapObjectsSchedule != null) {
            timedMapObjectsSchedule.cancel(false);
            timedMapObjectsSchedule = null;
        }

        if (charactersSchedule != null) {
            charactersSchedule.cancel(false);
            charactersSchedule = null;
        }

        if (marriagesSchedule != null) {
            marriagesSchedule.cancel(false);
            marriagesSchedule = null;
        }

        if (mapOwnershipSchedule != null) {
            mapOwnershipSchedule.cancel(false);
            mapOwnershipSchedule = null;
        }

        if (fishingSchedule != null) {
            fishingSchedule.cancel(false);
            fishingSchedule = null;
        }

        if (partySearchSchedule != null) {
            partySearchSchedule.cancel(false);
            partySearchSchedule = null;
        }

        if (timeoutSchedule != null) {
            timeoutSchedule.cancel(false);
            timeoutSchedule = null;
        }

        if (hpDecSchedule != null) {
            hpDecSchedule.cancel(false);
            hpDecSchedule = null;
        }

        players.disconnectAll();
        players = null;

        clearWorldData();
        log.info(I18nUtil.getLogMessage("World.shutdown.info1"), id);
    }
}