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
 * 世界
 * 管理一个世界内的所有频道、在线角色、组队、信使和好友关系等状态
 */
public class World {
    private static final Logger log = LoggerFactory.getLogger(World.class);

    /** 世界ID */
    @Getter
    private final int id;
    /** 世界标志 */
    @Getter
    private int flag;
    /** 经验倍率 */
    @Getter
    private float expRate;
    /** 掉落倍率 */
    @Getter
    private float dropRate;
    /** BOSS掉落倍率 */
    // boss rate concept thanks to Lapeiro
    @Setter
    @Getter
    private float bossDropRate;
    /** 金币倍率 */
    @Getter
    private float mesoRate;
    /** 任务经验倍率 */
    @Setter
    @Getter
    private float questRate;
    /** 旅行时间倍率 */
    @Setter
    @Getter
    private float travelRate;
    /** 钓鱼倍率 */
    @Setter
    @Getter
    private float fishingRate;
    /** 活动消息 */
    private String eventmsg;
    /** 频道列表 */
    private final List<Channel> channels = new ArrayList<>();
    /** 玩家NPC步骤映射 */
    private final Map<Integer, Byte> pnpcStep = new HashMap<>();
    /** 玩家NPC podium位置映射 */
    private final Map<Integer, Short> pnpcPodium = new HashMap<>();
    /** 信使映射 */
    private final Map<Integer, Messenger> messengers = new HashMap<>();
    /** 信使ID生成器 */
    private final AtomicInteger runningMessengerId = new AtomicInteger();
    /** 家族映射 */
    private final Map<Integer, Family> families = new LinkedHashMap<>();
    /** 玩家关系映射（玩家ID -> 关系ID） */
    private final Map<Integer, Integer> relationships = new HashMap<>();
    /** 关系ID -> (新郎ID, 新娘ID)映射 */
    private final Map<Integer, Pair<Integer, Integer>> relationshipCouples = new HashMap<>();
    /** 公会摘要存储 */
    private final Map<Integer, GuildSummary> gsStore = new HashMap<>();
    /** 玩家存储 */
    private PlayerStorage players = new PlayerStorage();
    /** 世界服务管理器 */
    private final ServicesManager services = new ServicesManager(WorldServices.SAVE_CHARACTER);
    /** 匹配检查协调器 */
    private final MatchCheckerCoordinator matchChecker = new MatchCheckerCoordinator();
    /** 组队搜索协调器 */
    private final PartySearchCoordinator partySearch = new PartySearchCoordinator();

    /** 频道读锁 */
    private final Lock chnRLock;
    /** 频道写锁 */
    private final Lock chnWLock;

    /** 账号角色视图映射（账号ID -> 角色排序映射） */
    private final Map<Integer, SortedMap<Integer, Character>> accountChars = new HashMap<>();
    /** 账号仓库映射 */
    private final Map<Integer, Storage> accountStorages = new HashMap<>();
    /** 账号角色锁 */
    private final Lock accountCharsLock = new ReentrantLock(true);

    /** 排队中的公会 */
    private final Set<Integer> queuedGuilds = new HashSet<>();
    /** 排队中的婚礼映射 */
    private final Map<Integer, Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>>> queuedMarriages = new HashMap<>();
    /** 婚礼嘉宾映射 */
    private final Map<Integer, Set<Integer>> marriageGuests = new ConcurrentHashMap<>();

    /** 角色队伍映射（角色ID -> 队伍ID） */
    private final Map<Integer, Integer> partyChars = new HashMap<>();
    /** 队伍映射 */
    private final Map<Integer, Party> parties = new HashMap<>();
    /** 队伍ID生成器 */
    private final AtomicInteger runningPartyId = new AtomicInteger();
    /** 队伍操作锁 */
    private final Lock partyLock = new ReentrantLock(true);

    /** 猫头鹰搜索记录 */
    private final Map<Integer, Integer> owlSearched = new LinkedHashMap<>();
    /** 现金物品购买记录（9个分类） */
    private final List<Map<Integer, Integer>> cashItemBought = new ArrayList<>(9);

    /** 推荐系统读锁 */
    private final Lock suggestRLock;
    /** 推荐系统写锁 */
    private final Lock suggestWLock;

    /** 禁用服务器消息的玩家映射 */
    // reuse owl lock
    private final Map<Integer, Integer> disabledServerMessages = new HashMap<>();
    /** 服务器消息锁 */
    private final Lock srvMessagesLock = new ReentrantLock();
    /** 服务器消息定时任务 */
    private ScheduledFuture<?> srvMessagesSchedule;

    /** 活跃宠物锁 */
    private final Lock activePetsLock = new ReentrantLock(true);
    /** 活跃宠物映射 */
    private final Map<Integer, Integer> activePets = new LinkedHashMap<>();
    /** 宠物定时任务 */
    private ScheduledFuture<?> petsSchedule;
    /** 宠物更新时间 */
    private long petUpdate;

    /** 活跃坐骑锁 */
    private final Lock activeMountsLock = new ReentrantLock(true);
    /** 活跃坐骑映射 */
    private final Map<Integer, Integer> activeMounts = new LinkedHashMap<>();
    /** 坐骑定时任务 */
    private ScheduledFuture<?> mountsSchedule;
    /** 坐骑更新时间 */
    private long mountUpdate;

    /** 活跃玩家商店锁 */
    private final Lock activePlayerShopsLock = new ReentrantLock(true);
    /** 活跃玩家商店映射 */
    private final Map<Integer, PlayerShop> activePlayerShops = new LinkedHashMap<>();

    /** 活跃雇佣商人锁 */
    private final Lock activeMerchantsLock = new ReentrantLock(true);
    /** 活跃雇佣商人映射 */
    private final Map<Integer, Pair<HiredMerchant, Integer>> activeMerchants = new LinkedHashMap<>();
    /** 雇佣商人定时任务 */
    private ScheduledFuture<?> merchantSchedule;
    /** 雇佣商人更新时间 */
    private long merchantUpdate;

    /** 注册的定时地图对象映射 */
    private final Map<Runnable, Long> registeredTimedMapObjects = new LinkedHashMap<>();
    /** 定时地图对象任务 */
    private ScheduledFuture<?> timedMapObjectsSchedule;
    /** 定时地图对象锁 */
    private final Lock timedMapObjectLock = new ReentrantLock(true);

    /** 钓鱼尝试者映射 */
    private final Map<Character, Integer> fishingAttempters = Collections.synchronizedMap(new WeakHashMap<>());
    /** 玩家HP减少映射 */
    private final Map<Character, Integer> playerHpDec = Collections.synchronizedMap(new WeakHashMap<>());

    /** 角色自动保存任务 */
    private ScheduledFuture<?> charactersSchedule;
    /** 婚礼预约任务 */
    private ScheduledFuture<?> marriagesSchedule;
    /** 地图占用检查任务 */
    private ScheduledFuture<?> mapOwnershipSchedule;
    /** 钓鱼定时任务 */
    private ScheduledFuture<?> fishingSchedule;
    /** 组队搜索定时任务 */
    private ScheduledFuture<?> partySearchSchedule;
    /** 超时检查任务 */
    private ScheduledFuture<?> timeoutSchedule;
    /** HP减少定时任务 */
    private ScheduledFuture<?> hpDecSchedule;

    /**
     * 构造世界
     *
     * @param world         世界ID
     * @param flag          标志
     * @param eventmsg      活动消息
     * @param expRate       经验倍率
     * @param dropRate      掉落倍率
     * @param bossDropRate  BOSS掉落倍率
     * @param mesoRate      金币倍率
     * @param questRate     任务经验倍率
     * @param travelRate    旅行时间倍率
     * @param fishingRate   钓鱼倍率
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
        // partyid must not clash with charid to solve update item looting issues, found thanks to Vcoc
        runningPartyId.set(1000000001);
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
     * 获取频道数量
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
     * 获取频道列表
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
     * 获取指定频道
     *
     * @param channel 频道号
     * @return 频道对象，不存在返回null
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
     * 添加频道
     *
     * @param channel 频道对象
     * @return 是否成功添加
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
     * 移除最后一个频道
     *
     * @return 移除的频道ID，-1表示无频道可移除
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
     * 是否可以卸载世界
     *
     * @return 是否可卸载
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
     * 设置标志
     *
     * @param b 标志值
     */
    public void setFlag(byte b) {
        this.flag = b;
    }

    /**
     * 获取活动消息
     *
     * @return 活动消息
     */
    public String getEventMessage() {
        return eventmsg;
    }

    /**
     * 设置活动消息
     *
     * @param eventMessage 活动消息
     */
    public void setEventMessage(String eventMessage) {
        this.eventmsg = eventMessage;
    }

    /**
     * 设置经验倍率
     *
     * @param exp 经验倍率
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
     * 设置掉落倍率
     *
     * @param drop 掉落倍率
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
     * 设置金币倍率
     *
     * @param meso 金币倍率
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
     * 获取交通工具实际旅行时间
     *
     * @param travelTime 基础旅行时间
     * @return 实际旅行时间
     */
    // 交通工具、旅行时间倍率，由于支持小数，所以需要改为相乘
    public int getTransportationTime(int travelTime) {
        return NumberTool.floatToInt(travelTime * travelRate);
    }

    /**
     * 加载账号角色视图
     *
     * @param accountId 账号ID
     * @param chars     角色列表
     */
    public void loadAccountCharactersView(Integer accountId, List<Character> chars) {
        SortedMap<Integer, Character> charsMap = new TreeMap<>();
        for (Character chr : chars) {
            charsMap.put(chr.getId(), chr);
        }

        // accountCharsLock should be used after server's lgnWLock for compliance
        accountCharsLock.lock();
        try {
            accountChars.put(accountId, charsMap);
        } finally {
            accountCharsLock.unlock();
        }
    }

    /**
     * 注册账号角色视图
     *
     * @param accountId 账号ID
     * @param chr       角色
     */
    public void registerAccountCharacterView(Integer accountId, Character chr) {
        accountCharsLock.lock();
        try {
            accountChars.get(accountId).put(chr.getId(), chr);
        } finally {
            accountCharsLock.unlock();
        }
    }

    /**
     * 取消注册账号角色视图
     *
     * @param accountId 账号ID
     * @param chrId     角色ID
     */
    public void unregisterAccountCharacterView(Integer accountId, Integer chrId) {
        accountCharsLock.lock();
        try {
            accountChars.get(accountId).remove(chrId);
        } finally {
            accountCharsLock.unlock();
        }
    }

    /**
     * 清除账号角色视图
     *
     * @param accountId 账号ID
     */
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

    /**
     * 加载账号仓库
     *
     * @param accountId 账号ID
     */
    public void loadAccountStorage(Integer accountId) {
        if (getAccountStorage(accountId) == null) {
            registerAccountStorage(accountId);
        }
    }

    /**
     * 注册账号仓库
     *
     * @param accountId 账号ID
     */
    private void registerAccountStorage(Integer accountId) {
        Storage storage = Storage.loadOrCreateFromDB(accountId, this.id);
        accountCharsLock.lock();
        try {
            accountStorages.put(accountId, storage);
        } finally {
            accountCharsLock.unlock();
        }
    }

    /**
     * 取消注册账号仓库
     *
     * @param accountId 账号ID
     */
    public void unregisterAccountStorage(Integer accountId) {
        accountCharsLock.lock();
        try {
            accountStorages.remove(accountId);
        } finally {
            accountCharsLock.unlock();
        }
    }

    /**
     * 获取账号仓库
     *
     * @param accountId 账号ID
     * @return 仓库对象
     */
    public Storage getAccountStorage(Integer accountId) {
        return accountStorages.get(accountId);
    }

    /**
     * 获取按账号ID排序的角色视图列表
     *
     * @param map 账号角色视图映射
     * @return 排序后的列表
     */
    private static List<Entry<Integer, SortedMap<Integer, Character>>> getSortedAccountCharacterView(Map<Integer, SortedMap<Integer, Character>> map) {
        List<Entry<Integer, SortedMap<Integer, Character>>> list = new ArrayList<>(map.size());
        list.addAll(map.entrySet());

        list.sort((o1, o2) -> o1.getKey() - o2.getKey());

        return list;
    }

    /**
     * 加载并获取所有角色视图
     *
     * @return 角色列表
     */
    public List<Character> loadAndGetAllCharactersView() {
        Server.getInstance().loadAllAccountsCharactersView();
        return getAllCharactersView();
    }

    /**
     * 获取所有角色视图（按账号ID和角色ID排序）
     *
     * @return 角色列表
     */
    // sorting by accountid, charid
    public List<Character> getAllCharactersView() {
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
     * 获取账号角色视图
     *
     * @param accountId 账号ID
     * @return 角色列表
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
     * 获取玩家存储
     *
     * @return 玩家存储
     */
    public PlayerStorage getPlayerStorage() {
        return players;
    }

    /**
     * 获取匹配检查协调器
     *
     * @return 匹配检查协调器
     */
    public MatchCheckerCoordinator getMatchCheckerCoordinator() {
        return matchChecker;
    }

    /**
     * 获取组队搜索协调器
     *
     * @return 组队搜索协调器
     */
    public PartySearchCoordinator getPartySearchCoordinator() {
        return partySearch;
    }

    /**
     * 添加玩家
     *
     * @param chr 角色对象
     */
    public void addPlayer(Character chr) {
        players.addPlayer(chr);
    }

    /**
     * 移除玩家
     *
     * @param chr 角色对象
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
     * 添加家族
     *
     * @param id 家族ID
     * @param f  家族对象
     */
    public void addFamily(int id, Family f) {
        synchronized (families) {
            if (!families.containsKey(id)) {
                families.put(id, f);
            }
        }
    }

    /**
     * 移除家族
     *
     * @param id 家族ID
     */
    public void removeFamily(int id) {
        synchronized (families) {
            families.remove(id);
        }
    }

    /**
     * 获取家族
     *
     * @param id 家族ID
     * @return 家族对象，不存在返回null
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
     * 获取所有家族
     *
     * @return 不可修改的家族集合
     */
    public Collection<Family> getFamilies() {
        synchronized (families) {
            return Collections.unmodifiableCollection(families.values());
        }
    }

    /**
     * 获取公会
     *
     * @param mgc 公会角色
     * @return 公会对象
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
     * 世界容量是否已满
     *
     * @return 是否已满
     */
    public boolean isWorldCapacityFull() {
        return getWorldCapacityStatus() == 2;
    }

    /**
     * 获取世界容量状态
     *
     * @return 0正常，1超过80%，2已满
     */
    public int getWorldCapacityStatus() {
        int worldCap = getChannelsSize() * GameConfig.getServerInt("channel_capacity");
        int num = players.getSize();

        int status;
        if (num >= worldCap) {
            status = 2;
        // More than 80 percent o___o
        } else if (num >= worldCap * .8) {
            status = 1;
        } else {
            status = 0;
        }

        return status;
    }

    /**
     * 获取公会摘要
     *
     * @param gid 公会ID
     * @param wid 世界ID
     * @return 公会摘要
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
     * 更新公会摘要
     *
     * @param gid 公会ID
     * @param mgs 公会摘要
     */
    public void updateGuildSummary(int gid, GuildSummary mgs) {
        gsStore.put(gid, mgs);
    }

    /**
     * 重新加载公会摘要
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
     * 批量设置公会和等级
     *
     * @param cids      角色ID列表
     * @param guildid   公会ID
     * @param rank      等级
     * @param exception 排除的角色ID
     */
    public void setGuildAndRank(List<Integer> cids, int guildid, int rank, int exception) {
        for (int cid : cids) {
            if (cid != exception) {
                setGuildAndRank(cid, guildid, rank);
            }
        }
    }

    /**
     * 设置离线角色公会状态
     *
     * @param guildid   公会ID
     * @param guildrank 公会等级
     * @param cid       角色ID
     */
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

    /**
     * 设置角色公会和等级
     *
     * @param cid     角色ID
     * @param guildid 公会ID
     * @param rank    等级
     */
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
     * 变更徽章
     *
     * @param gid             公会ID
     * @param affectedPlayers 受影响的玩家ID列表
     * @param mgs             公会摘要
     */
    public void changeEmblem(int gid, List<Integer> affectedPlayers, GuildSummary mgs) {
        updateGuildSummary(gid, mgs);
        sendPacket(affectedPlayers, GuildPackets.guildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor()), -1);
        // respawn player
        setGuildAndRank(affectedPlayers, -1, -1, -1);
    }

    /**
     * 向目标玩家列表发送数据包
     *
     * @param targetIds 目标角色ID列表
     * @param packet    数据包
     * @param exception 排除的角色ID
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
     * 公会是否在排队中
     *
     * @param guildId 公会ID
     * @return 是否在排队
     */
    public boolean isGuildQueued(int guildId) {
        return queuedGuilds.contains(guildId);
    }

    /**
     * 添加公会排队
     *
     * @param guildId 公会ID
     */
    public void putGuildQueued(int guildId) {
        queuedGuilds.add(guildId);
    }

    /**
     * 移除公会排队
     *
     * @param guildId 公会ID
     */
    public void removeGuildQueued(int guildId) {
        queuedGuilds.remove(guildId);
    }

    /**
     * 婚礼是否在排队中
     *
     * @param marriageId 婚礼ID
     * @return 是否在排队
     */
    public boolean isMarriageQueued(int marriageId) {
        return queuedMarriages.containsKey(marriageId);
    }

    /**
     * 获取婚礼排队地点信息
     *
     * @param marriageId 婚礼ID
     * @return 地点信息（是否大教堂，是否高级）
     */
    public Pair<Boolean, Boolean> getMarriageQueuedLocation(int marriageId) {
        Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>> qm = queuedMarriages.get(marriageId);
        return (qm != null) ? qm.getLeft() : null;
    }

    /**
     * 获取婚礼排队新人ID
     *
     * @param marriageId 婚礼ID
     * @return 新人ID对（新郎ID, 新娘ID）
     */
    public Pair<Integer, Integer> getMarriageQueuedCouple(int marriageId) {
        Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>> qm = queuedMarriages.get(marriageId);
        return (qm != null) ? qm.getRight() : null;
    }

    /**
     * 添加婚礼排队
     *
     * @param marriageId 婚礼ID
     * @param cathedral  是否大教堂
     * @param premium    是否高级
     * @param groomId    新郎ID
     * @param brideId    新娘ID
     */
    public void putMarriageQueued(int marriageId, boolean cathedral, boolean premium, int groomId, int brideId) {
        queuedMarriages.put(marriageId, new Pair<>(new Pair<>(cathedral, premium), new Pair<>(groomId, brideId)));
        marriageGuests.put(marriageId, new HashSet<>());
    }

    /**
     * 移除婚礼排队
     *
     * @param marriageId 婚礼ID
     * @return 婚礼类型和嘉宾集合
     */
    public Pair<Boolean, Set<Integer>> removeMarriageQueued(int marriageId) {
        Boolean type = queuedMarriages.remove(marriageId).getLeft().getRight();
        Set<Integer> guests = marriageGuests.remove(marriageId);

        return new Pair<>(type, guests);
    }

    /**
     * 添加婚礼嘉宾
     *
     * @param marriageId 婚礼ID
     * @param playerId   玩家ID
     * @return 是否添加成功
     */
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

    /**
     * 获取嘉宾对应的婚礼新人ID
     *
     * @param guestId   嘉宾ID
     * @param cathedral 是否大教堂
     * @return 新人ID对
     */
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

    /**
     * 调试婚礼状态
     */
    public void debugMarriageStatus() {
        log.debug("Queued marriages: {}", queuedMarriages);
        log.debug("Guest list: {}", marriageGuests);
    }

    /**
     * 注册角色队伍
     *
     * @param chrid   角色ID
     * @param partyid 队伍ID
     */
    private void registerCharacterParty(Integer chrid, Integer partyid) {
        partyLock.lock();
        try {
            partyChars.put(chrid, partyid);
        } finally {
            partyLock.unlock();
        }
    }

    /**
     * 取消注册角色队伍（内部方法，不加锁）
     *
     * @param chrid 角色ID
     */
    private void unregisterCharacterPartyInternal(Integer chrid) {
        partyChars.remove(chrid);
    }

    /**
     * 取消注册角色队伍
     *
     * @param chrid 角色ID
     */
    private void unregisterCharacterParty(Integer chrid) {
        partyLock.lock();
        try {
            unregisterCharacterPartyInternal(chrid);
        } finally {
            partyLock.unlock();
        }
    }

    /**
     * 获取角色所属队伍ID
     *
     * @param chrid 角色ID
     * @return 队伍ID
     */
    public Integer getCharacterPartyid(Integer chrid) {
        partyLock.lock();
        try {
            return partyChars.get(chrid);
        } finally {
            partyLock.unlock();
        }
    }

    /**
     * 创建队伍
     *
     * @param chrfor 队长
     * @return 创建的队伍
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
     * 获取队伍
     *
     * @param partyid 队伍ID
     * @return 队伍对象
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
     * 解散队伍
     *
     * @param partyid 队伍ID
     * @return 被解散的队伍
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
     * 更新角色队伍关系
     *
     * @param party        队伍
     * @param operation    操作类型
     * @param target       目标角色
     * @param partyMembers 队伍成员列表
     */
    private void updateCharacterParty(Party party, PartyOperation operation, PartyCharacter target, Collection<PartyCharacter> partyMembers) {
        switch (operation) {
            case JOIN:
                registerCharacterParty(target.getId(), party.getId());
                break;

            case LEAVE:
            case EXPEL:
                unregisterCharacterParty(target.getId());
                break;

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
     * 更新队伍状态
     *
     * @param party      队伍
     * @param operation  操作类型
     * @param target     目标角色
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
        switch (operation) {
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
     * 更新队伍（公共接口）
     *
     * @param partyid   队伍ID
     * @param operation 操作类型
     * @param target    目标角色
     * @throws IllegalArgumentException 队伍不存在时抛出
     */
    public void updateParty(int partyid, PartyOperation operation, PartyCharacter target) {
        Party party = getParty(partyid);
        if (party == null) {
            throw new IllegalArgumentException("no party with the specified partyid exists");
        }
        switch (operation) {
            case JOIN:
                party.addMember(target);
                break;
            case EXPEL:
            case LEAVE:
                party.removeMember(target);
                break;
            case DISBAND:
                disbandParty(partyid);
                break;
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
     * 从地图中移除队伍成员
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
     * 按名称查找角色所在频道
     *
     * @param name 角色名称
     * @return 频道号，-1表示未找到
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
     * 按ID查找角色所在频道
     *
     * @param id 角色ID
     * @return 频道号，-1表示未找到
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
     * 队伍聊天
     *
     * @param party     队伍
     * @param chattext  聊天内容
     * @param namefrom  发送者名称
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
     * 好友聊天
     *
     * @param recipientCharacterIds 接收者角色ID数组
     * @param cidFrom               发送者角色ID
     * @param nameFrom              发送者名称
     * @param chattext              聊天内容
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
     * 多头像查找好友（跨频道）
     *
     * @param charIdFrom   发起查找的角色ID
     * @param characterIds 目标角色ID数组
     * @return 角色ID频道对数组
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
     * 获取信使
     *
     * @param messengerid 信使ID
     * @return 信使对象
     */
    public Messenger getMessenger(int messengerid) {
        return messengers.get(messengerid);
    }

    /**
     * 离开信使
     *
     * @param messengerid 信使ID
     * @param target      离开的角色
     * @throws IllegalArgumentException 信使不存在时抛出
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
     * 信使邀请
     *
     * @param sender       发送者名称
     * @param messengerid  信使ID
     * @param target       目标名称
     * @param fromchannel  来源频道
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
     * 添加信使玩家
     *
     * @param messenger   信使
     * @param namefrom    发起者名称
     * @param fromchannel 来源频道
     * @param position    位置
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
     * 移除信使玩家
     *
     * @param messenger 信使
     * @param position  移除位置
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
     * 信使聊天
     *
     * @param messenger 信使
     * @param chattext  聊天内容
     * @param namefrom  发送者名称
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
     * 拒绝聊天
     *
     * @param sender 发送者名称
     * @param player 拒绝的玩家
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
     * 更新信使
     *
     * @param messengerid 信使ID
     * @param namefrom    发起者名称
     * @param fromchannel 来源频道
     */
    public void updateMessenger(int messengerid, String namefrom, int fromchannel) {
        Messenger messenger = getMessenger(messengerid);
        int position = messenger.getPositionByName(namefrom);
        updateMessenger(messenger, namefrom, position, fromchannel);
    }

    /**
     * 更新信使
     *
     * @param messenger   信使
     * @param namefrom    发起者名称
     * @param position    位置
     * @param fromchannel 来源频道
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
     * 静默离开信使
     *
     * @param messengerid 信使ID
     * @param target      目标角色
     * @throws IllegalArgumentException 信使不存在时抛出
     */
    public void silentLeaveMessenger(int messengerid, MessengerCharacter target) {
        Messenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, target.getPosition());
    }

    /**
     * 加入信使
     *
     * @param messengerid 信使ID
     * @param target      目标角色
     * @param from        发起者名称
     * @param fromchannel 来源频道
     * @throws IllegalArgumentException 信使不存在时抛出
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
     * 静默加入信使
     *
     * @param messengerid 信使ID
     * @param target      目标角色
     * @param position    位置
     * @throws IllegalArgumentException 信使不存在时抛出
     */
    public void silentJoinMessenger(int messengerid, MessengerCharacter target, int position) {
        Messenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, position);
    }

    /**
     * 创建信使
     *
     * @param chrfor 创建者
     * @return 创建的信使
     */
    public Messenger createMessenger(MessengerCharacter chrfor) {
        int messengerid = runningMessengerId.getAndIncrement();
        Messenger messenger = new Messenger(messengerid, chrfor);
        messengers.put(messenger.getId(), messenger);
        return messenger;
    }

    /**
     * 角色是否连接
     *
     * @param charName 角色名称
     * @return 是否连接
     */
    public boolean isConnected(String charName) {
        return getPlayerStorage().getCharacterByName(charName) != null;
    }

    /**
     * 请求添加好友
     *
     * @param addName     目标名称
     * @param channelFrom 来源频道
     * @param cidFrom     来源角色ID
     * @param nameFrom    来源名称
     * @return 好友添加结果
     */
    public BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom) {
        Character addChar = getPlayerStorage().getCharacterByName(addName);
        if (addChar != null) {
            BuddyList buddylist = addChar.getBuddylist();
            if (buddylist.isFull()) {
                return BuddyAddResult.BUDDYLIST_FULL;
            }
            if (!buddylist.contains(cidFrom)) {
                buddylist.addBuddyRequest(addChar.getClient(), cidFrom, nameFrom, channelFrom);
            } else if (buddylist.containsVisible(cidFrom)) {
                return BuddyAddResult.ALREADY_ON_LIST;
            }
        }
        return BuddyAddResult.OK;
    }

    /**
     * 好友变更通知
     *
     * @param cid       角色ID
     * @param cidFrom   来源角色ID
     * @param name      名称
     * @param channel   频道
     * @param operation 操作类型
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
     * 玩家下线通知
     *
     * @param name        名称
     * @param characterId 角色ID
     * @param channel     频道
     * @param buddies     好友ID数组
     */
    public void loggedOff(String name, int characterId, int channel, int[] buddies) {
        updateBuddies(characterId, channel, buddies, true);
    }

    /**
     * 玩家上线通知
     *
     * @param name        名称
     * @param characterId 角色ID
     * @param channel     频道
     * @param buddies     好友ID数组
     */
    public void loggedOn(String name, int characterId, int channel, int[] buddies) {
        updateBuddies(characterId, channel, buddies, false);
    }

    /**
     * 更新好友状态
     *
     * @param characterId 角色ID
     * @param channel     频道
     * @param buddies     好友ID数组
     * @param offline     是否离线
     */
    private void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
        PlayerStorage playerStorage = getPlayerStorage();
        for (int buddy : buddies) {
            Character chr = playerStorage.getCharacterById(buddy);
            if (chr != null) {
                BuddylistEntry ble = chr.getBuddylist().get(characterId);
                if (ble != null && ble.isVisible()) {
                    int mcChannel;
                    if (offline) {
                        ble.setChannel((byte) -1);
                        mcChannel = -1;
                    } else {
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
     * 获取宠物键值
     *
     * @param chr     角色
     * @param petSlot 宠物槽位
     * @return 宠物键值
     */
    // assuming max 3 pets
    private static Integer getPetKey(Character chr, byte petSlot) {
        return (chr.getId() << 2) + petSlot;
    }

    /**
     * 添加猫头鹰物品搜索记录
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

    /**
     * 获取猫头鹰搜索物品列表
     *
     * @return 物品搜索计数列表
     */
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

    /**
     * 添加现金物品购买记录
     *
     * @param snid 物品SN ID
     */
    public void addCashItemBought(Integer snid) {
        suggestWLock.lock();
        try {
            Map<Integer, Integer> tabItemBought = cashItemBought.get(snid / 10000000);
            tabItemBought.merge(snid, 1, Integer::sum);
        } finally {
            suggestWLock.unlock();
        }
    }

    /**
     * 获取购买现金物品统计
     *
     * @return 分类物品购买统计列表
     */
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
     * 获取分类中最畅销物品
     *
     * @param tabSellers 分类销售列表
     * @return 畅销物品ID列表
     */
    private List<Integer> getMostSellerOnTab(List<Pair<Integer, Integer>> tabSellers) {
        List<Integer> tabLeaderboards;

        // descending order
        Comparator<Pair<Integer, Integer>> comparator = (p1, p2) -> p2.getRight().compareTo(p1.getRight());

        PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>(Math.max(1, tabSellers.size()), comparator);
        queue.addAll(tabSellers);

        tabLeaderboards = new LinkedList<>();
        for (int i = 0; i < Math.min(tabSellers.size(), 5); i++) {
            tabLeaderboards.add(queue.remove().getLeft());
        }

        return tabLeaderboards;
    }

    /**
     * 获取最畅销现金物品排行榜
     *
     * @return 分类畅销物品排行
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
     * 注册宠物饥饿度
     *
     * @param chr     角色
     * @param petSlot 宠物槽位
     */
    public void registerPetHunger(Character chr, byte petSlot) {
        if (chr.isGM() && GameConfig.getServerBoolean("gm_pets_never_hungry") || GameConfig.getServerBoolean("pets_never_hungry")) {
            return;
        }

        Integer key = getPetKey(chr, petSlot);

        activePetsLock.lock();
        try {
            int initProc;
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
     * 取消注册宠物饥饿度
     *
     * @param chr     角色
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
     * 运行宠物饥饿度定时任务
     */
    public void runPetSchedule() {
        Map<Integer, Integer> deployedPets;

        activePetsLock.lock();
        try {
            petUpdate = Server.getInstance().getCurrentTime();
            // exception here found thanks to MedicOP
            deployedPets = new HashMap<>(activePets);
        } finally {
            activePetsLock.unlock();
        }

        for (Map.Entry<Integer, Integer> dp : deployedPets.entrySet()) {
            Character chr = this.getPlayerStorage().getCharacterById(dp.getKey() / 4);
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            int dpVal = dp.getValue() + 1;
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
     * 注册坐骑疲劳度
     *
     * @param chr 角色
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

    /**
     * 取消注册坐骑疲劳度
     *
     * @param chr 角色
     */
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
     * 运行坐骑疲劳度定时任务
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

        for (Map.Entry<Integer, Integer> dp : deployedMounts.entrySet()) {
            Character chr = this.getPlayerStorage().getCharacterById(dp.getKey());
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            int dpVal = dp.getValue() + 1;
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
     * 注册玩家商店
     *
     * @param ps 玩家商店
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
     * 取消注册玩家商店
     *
     * @param ps 玩家商店
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
     * 获取活跃玩家商店列表
     *
     * @return 活跃玩家商店列表
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
     * 获取玩家商店
     *
     * @param ownerid 所有者ID
     * @return 玩家商店
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
     * 注册雇佣商人
     *
     * @param hm 雇佣商人
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
     * 取消注册雇佣商人
     *
     * @param hm 雇佣商人
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
     * 运行雇佣商人管理定时任务
     */
    public void runHiredMerchantSchedule() {
        Map<Integer, Pair<HiredMerchant, Integer>> deployedMerchants;
        activeMerchantsLock.lock();
        try {
            merchantUpdate = Server.getInstance().getCurrentTime();
            deployedMerchants = new LinkedHashMap<>(activeMerchants);

            for (Map.Entry<Integer, Pair<HiredMerchant, Integer>> dm : deployedMerchants.entrySet()) {
                int timeOn = dm.getValue().getRight();
                HiredMerchant hm = dm.getValue().getLeft();

                // 1440 minutes == 24hrs
                if (timeOn <= 144) {
                    activeMerchants.put(hm.getOwnerId(), new Pair<>(dm.getValue().getLeft(), timeOn + 1));
                } else {
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
     * 获取活跃雇佣商人列表
     *
     * @return 活跃雇佣商人列表
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

    /**
     * 获取雇佣商人
     *
     * @param ownerid 所有者ID
     * @return 雇佣商人，不存在返回null
     */
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

    /**
     * 注册定时地图对象
     *
     * @param r        可执行任务
     * @param duration 持续时间
     */
    public void registerTimedMapObject(Runnable r, long duration) {
        timedMapObjectLock.lock();
        try {
            long expirationTime = Server.getInstance().getCurrentTime() + duration;
            registeredTimedMapObjects.put(r, expirationTime);
        } finally {
            timedMapObjectLock.unlock();
        }
    }

    /**
     * 运行定时地图对象清理任务
     */
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

    /**
     * 添加玩家HP减少
     *
     * @param chr 角色
     */
    public void addPlayerHpDecrease(Character chr) {
        playerHpDec.putIfAbsent(chr, 0);
    }

    /**
     * 移除玩家HP减少
     *
     * @param chr 角色
     */
    public void removePlayerHpDecrease(Character chr) {
        playerHpDec.remove(chr);
    }

    /**
     * 运行玩家HP减少定时任务
     */
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

    /**
     * 重置禁用服务器消息
     */
    public void resetDisabledServerMessages() {
        srvMessagesLock.lock();
        try {
            disabledServerMessages.clear();
        } finally {
            srvMessagesLock.unlock();
        }
    }

    /**
     * 注册禁用服务器消息
     *
     * @param chrid 角色ID
     * @return 是否已经禁用
     */
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

    /**
     * 取消注册禁用服务器消息
     *
     * @param chrid 角色ID
     * @return 是否取消成功
     */
    public boolean unregisterDisabledServerMessage(int chrid) {
        srvMessagesLock.lock();
        try {
            return disabledServerMessages.remove(chrid) != null;
        } finally {
            srvMessagesLock.unlock();
        }
    }

    /**
     * 运行禁用服务器消息定时任务
     */
    // ~35sec duration, 10sec update
    public void runDisabledServerMessagesSchedule() {
        List<Integer> toRemove = new LinkedList<>();

        srvMessagesLock.lock();
        try {
            for (Entry<Integer, Integer> dsm : disabledServerMessages.entrySet()) {
                int b = dsm.getValue();
                if (b >= 4) {
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

    /**
     * 设置玩家NPC地图步骤
     *
     * @param mapid 地图ID
     * @param step  步骤
     */
    public void setPlayerNpcMapStep(int mapid, int step) {
        setPlayerNpcMapData(mapid, step, -1, false);
    }

    /**
     * 设置玩家NPC地图podium数据
     *
     * @param mapid  地图ID
     * @param podium podium值
     */
    public void setPlayerNpcMapPodiumData(int mapid, int podium) {
        setPlayerNpcMapData(mapid, -1, podium, false);
    }

    /**
     * 设置玩家NPC地图数据
     *
     * @param mapid 地图ID
     * @param step  步骤
     * @param podium podium值
     */
    public void setPlayerNpcMapData(int mapid, int step, int podium) {
        setPlayerNpcMapData(mapid, step, podium, true);
    }

    /**
     * 执行玩家NPC地图数据数据库更新
     *
     * @param isPodium      是否podium数据
     * @param pnpcData      缓存数据
     * @param value         值
     * @param worldId       世界ID
     * @param mapId         地图ID
     */
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

    /**
     * 设置玩家NPC地图数据
     *
     * @param mapId  地图ID
     * @param step   步骤
     * @param podium podium值
     * @param silent 是否静默（不写入数据库）
     */
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

    /**
     * 获取玩家NPC地图步骤
     *
     * @param mapid 地图ID
     * @return 步骤值
     */
    public int getPlayerNpcMapStep(int mapid) {
        try {
            return pnpcStep.get(mapid);
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    /**
     * 获取玩家NPC地图podium数据
     *
     * @param mapid 地图ID
     * @return podium值
     */
    public int getPlayerNpcMapPodiumData(int mapid) {
        try {
            return pnpcPodium.get(mapid);
        } catch (NullPointerException npe) {
            return 1;
        }
    }

    /**
     * 重置所有玩家NPC地图数据
     */
    public void resetPlayerNpcMapData() {
        pnpcStep.clear();
        pnpcPodium.clear();
    }

    /**
     * 设置服务器消息
     *
     * @param msg 消息内容
     */
    public void setServerMessage(String msg) {
        for (Channel ch : getChannels()) {
            ch.setServerMessage(msg);
        }
    }

    /**
     * 向全世界广播数据包
     *
     * @param packet 数据包
     */
    public void broadcastPacket(Packet packet) {
        for (Character chr : players.getAllCharacters()) {
            chr.sendPacket(packet);
        }
    }

    /**
     * 获取可用物品捆绑列表
     *
     * @param itemid 物品ID
     * @return 物品捆绑列表
     */
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

        // truncates the list to have up to 200 elements
        hmsAvailable.subList(0, Math.min(hmsAvailable.size(), 200));
        return hmsAvailable;
    }

    /**
     * 推送关系对
     *
     * @param couple 关系对（关系ID, (新郎ID, 新娘ID)）
     */
    private void pushRelationshipCouple(Pair<Integer, Pair<Integer, Integer>> couple) {
        int mid = couple.getLeft(), hid = couple.getRight().getLeft(), wid = couple.getRight().getRight();
        relationshipCouples.put(mid, couple.getRight());
        relationships.put(hid, mid);
        relationships.put(wid, mid);
    }

    /**
     * 获取关系新人ID对
     *
     * @param relationshipId 关系ID
     * @return 新人ID对
     */
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

    /**
     * 获取玩家关系ID
     *
     * @param playerId 玩家ID
     * @return 关系ID，-1表示无
     */
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

    /**
     * 从数据库获取关系新人信息
     *
     * @param id              依据ID
     * @param usingMarriageId 是否按婚姻ID查询
     * @return 关系新人对
     */
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

    /**
     * 创建关系（婚姻）
     *
     * @param groomId 新郎ID
     * @param brideId 新娘ID
     * @return 关系ID
     */
    public int createRelationship(int groomId, int brideId) {
        int ret = addRelationshipToDb(groomId, brideId);

        pushRelationshipCouple(new Pair<>(ret, new Pair<>(groomId, brideId)));
        return ret;
    }

    /**
     * 向数据库添加关系
     *
     * @param groomId 新郎ID
     * @param brideId 新娘ID
     * @return 关系ID
     */
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

    /**
     * 删除关系（婚姻）
     *
     * @param playerId  玩家ID
     * @param partnerId 伴侣ID
     */
    public void deleteRelationship(int playerId, int partnerId) {
        int relationshipId = relationships.get(playerId);
        deleteRelationshipFromDb(relationshipId);

        relationshipCouples.remove(relationshipId);
        relationships.remove(playerId);
        relationships.remove(partnerId);
    }

    /**
     * 从数据库删除关系
     *
     * @param playerId 关系ID
     */
    private static void deleteRelationshipFromDb(int playerId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM marriages WHERE marriageid = ?")) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * 向全世界广播消息
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void dropMessage(int type, String message) {
        for (Character player : getPlayerStorage().getAllCharacters()) {
            player.dropMessage(type, message);
        }
    }

    /**
     * 注册钓鱼玩家
     *
     * @param chr       角色
     * @param baitLevel 鱼饵等级
     * @return 是否注册成功
     */
    public boolean registerFisherPlayer(Character chr, int baitLevel) {
        synchronized (fishingAttempters) {
            if (fishingAttempters.containsKey(chr)) {
                return false;
            }

            fishingAttempters.put(chr, baitLevel);
            return true;
        }
    }

    /**
     * 取消注册钓鱼玩家
     *
     * @param chr 角色
     * @return 鱼饵等级
     */
    public int unregisterFisherPlayer(Character chr) {
        Integer baitLevel = fishingAttempters.remove(chr);
        if (baitLevel != null) {
            return baitLevel;
        } else {
            return 0;
        }
    }

    /**
     * 运行钓鱼检查定时任务
     */
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

    /**
     * 运行组队搜索定时任务
     */
    public void runPartySearchUpdateSchedule() {
        partySearch.updatePartySearchStorage();
        partySearch.runPartySearch();
    }

    /**
     * 获取世界服务
     *
     * @param sv 服务类型
     * @return 服务实例
     */
    public BaseService getServiceAccess(WorldServices sv) {
        return services.getAccess(sv).getService();
    }

    /**
     * 关闭世界服务
     */
    private void closeWorldServices() {
        services.shutdown();
    }

    /**
     * 清除世界数据
     */
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

    /**
     * 关闭世界
     */
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