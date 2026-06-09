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
package org.gms.net.server;

import lombok.Getter;
import lombok.Setter;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkillFactory;
import org.gms.client.command.CommandsExecutor;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.config.GameConfig;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.PlayernpcsFieldDO;
import org.gms.model.dto.ServerShutdownDTO;
import org.gms.property.ServiceProperty;
import org.gms.util.*;
import org.gms.model.pojo.NewYearCardRecord;
import org.gms.client.processor.npc.FredrickProcessor;
import org.gms.constants.game.GameConstants;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.net.OpcodeConstants;
import org.gms.constants.net.ServerConstants;
import org.gms.dao.entity.NxcouponsDO;
import org.gms.manager.ServerManager;
import org.gms.net.ChannelDependencies;
import org.gms.net.PacketProcessor;
import org.gms.net.netty.LoginServer;
import org.gms.net.packet.Packet;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.session.IpAddresses;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.net.server.guild.Alliance;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.guild.GuildCharacter;
import org.gms.net.server.task.*;
import org.gms.net.server.world.World;
import org.gms.server.CashShop.CashItemFactory;
import org.gms.server.SkillbookInformationProvider;
import org.gms.server.ThreadManager;
import org.gms.server.TimerManager;
import org.gms.server.expeditions.ExpeditionBossLog;
import org.gms.server.life.PlayerNPC;
import org.gms.server.quest.Quest;
import org.gms.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.*;

/**
 * 游戏服务器核心类（单例）
 * 管理登录服务器、频道服务器、在线玩家、活动状态等全局状态
 * 提供世界列表、频道列表、玩家查找等核心功能
 */
public class Server {
    static {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false"); // Mute GraalVM warning: "The polyglot context is using an implementation that does not support runtime compilation."
    }

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    /** 服务器单例实例 */
    private static Server instance = null;

    /**
     * 获取服务器单例实例
     * @return 服务器单例
     */
    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    /** 可飞行账号ID集合 */
    private static final Set<Integer> activeFly = new HashSet<>();
    /** 优惠券倍率映射，key为优惠券ID，value为倍率值 */
    private static final Map<Integer, Integer> couponRates = new HashMap<>(30);
    /** 当前激活的优惠券ID列表 */
    private static final List<Integer> activeCoupons = new LinkedList<>();
    /** 频道依赖服务 */
    private ChannelDependencies channelDependencies;

    /** 登录服务器实例 */
    private LoginServer loginServer;
    /** 各世界的频道IP信息，外层索引为世界ID，内层Map的key为频道ID，value为IP地址 */
    private final List<Map<Integer, String>> channels = new LinkedList<>();
    /** 世界列表 */
    private final List<World> worlds = new ArrayList<>();
    @Getter
    /** 子网配置属性 */
    private final Properties subnetInfo = new Properties();
    /** 账号角色映射，key为账号ID，value为该账号下的角色ID集合 */
    private final Map<Integer, Set<Integer>> accountChars = new HashMap<>();
    /** 账号角色数量映射，key为账号ID，value为角色总数 */
    private final Map<Integer, Short> accountCharacterCount = new HashMap<>();
    /** 角色所属世界映射，key为角色ID，value为世界ID */
    private final Map<Integer, Integer> worldChars = new HashMap<>();
    /** 正在转频道中的角色映射，key为远程IP地址，value为角色ID */
    private final Map<String, Integer> transitioningChars = new HashMap<>();
    /** 世界推荐列表，每个元素为世界ID和推荐消息的键值对 */
    private final List<Pair<Integer, String>> worldRecommendedList = new LinkedList<>();
    /** 公会映射，key为公会ID，value为公会对象 */
    private final Map<Integer, Guild> guilds = new HashMap<>(100);
    /** 登录状态中的客户端映射，key为客户端对象，value为超时时间戳 */
    private final Map<Client, Long> inLoginState = new HashMap<>(100);

    /** 玩家Buff存储 */
    private final PlayerBuffStorage buffStorage = new PlayerBuffStorage();
    /** 联盟映射，key为联盟ID，value为联盟对象 */
    private final Map<Integer, Alliance> alliances = new HashMap<>(100);
    /** 新年贺卡记录映射，key为贺卡ID，value为贺卡记录 */
    private final Map<Integer, NewYearCardRecord> newyears = new HashMap<>();
    /** 待处理疾病通知的玩家客户端列表 */
    private final List<Client> processDiseaseAnnouncePlayers = new LinkedList<>();
    /** 已注册疾病通知的玩家客户端列表 */
    private final List<Client> registeredDiseaseAnnouncePlayers = new LinkedList<>();

    /** 玩家排行榜，每个世界一个列表，列表中元素为角色名和等级的键值对 */
    private final List<List<Pair<String, Integer>>> playerRanking = new LinkedList<>();

    /** 服务器全局锁 */
    private final Lock srvLock = new ReentrantLock();
    /** 疾病通知锁 */
    private final Lock disLock = new ReentrantLock();

    /** 世界读锁 */
    private final Lock wldRLock;
    /** 世界写锁 */
    private final Lock wldWLock;

    /** 登录读锁 */
    private final Lock lgnRLock;
    /** 登录写锁 */
    private final Lock lgnWLock;

    /** 原子时间计数器，用于跟踪服务器运行时间 */
    private final AtomicLong currentTime = new AtomicLong(0);
    /** 缓存的服务端当前时间，用于减少系统调用 */
    private long serverCurrentTime = 0;

    /** 开发者房间是否可用 */
    private volatile boolean availableDeveloperRoom = false;
    @Getter
    @Setter
    /** 服务器是否在线运行 */
    private boolean online = false;
    /** 服务器启动时间戳 */
    public static long uptime = System.currentTimeMillis();
    /** 下一次随机事件触发时间戳 */
    private long nextTime;

    /** NPC服务 */
    private static final NpcService npcService = ServerManager.getApplicationContext().getBean(NpcService.class);
    /** 优惠券服务 */
    private static final NxCouponService nxCouponService = ServerManager.getApplicationContext().getBean(NxCouponService.class);
    /** 角色服务 */
    private static final CharacterService characterService = ServerManager.getApplicationContext().getBean(CharacterService.class);
    /** 账号服务 */
    private static final AccountService accountService = ServerManager.getApplicationContext().getBean(AccountService.class);
    /** 兑换码服务 */
    private static final NxCodeService nxCodeService = ServerManager.getApplicationContext().getBean(NxCodeService.class);
    /** 新年贺卡服务 */
    private static final NewYearCardService newYearCardService = ServerManager.getApplicationContext().getBean(NewYearCardService.class);
    /** 改名服务 */
    private static final NameChangeService nameChangeService = ServerManager.getApplicationContext().getBean(NameChangeService.class);
    /** 转区服务 */
    private static final WorldTransferService worldTransferService = ServerManager.getApplicationContext().getBean(WorldTransferService.class);
    /** 家族服务 */
    private static final FamilyService familyService = ServerManager.getApplicationContext().getBean(FamilyService.class);
    /** 信件服务 */
    private static final NoteService noteService = ServerManager.getApplicationContext().getBean(NoteService.class);
    /** HP/MP报警服务 */
    private static final HpMpAlertService hpMpAlertService = ServerManager.getApplicationContext().getBean(HpMpAlertService.class);
    /** 服务配置属性 */
    private static final ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
    /** 自动封禁配置服务 */
    private static final AutobanConfigService autobanConfigService = ServerManager.getApplicationContext().getBean(AutobanConfigService.class);

    /**
     * 私有构造函数，初始化读写锁
     */
    private Server() {
        ReadWriteLock worldLock = new ReentrantReadWriteLock(true);
        this.wldRLock = worldLock.readLock();
        this.wldWLock = worldLock.writeLock();

        ReadWriteLock loginLock = new ReentrantReadWriteLock(true);
        this.lgnRLock = loginLock.readLock();
        this.lgnWLock = loginLock.writeLock();
    }

    /**
     * 获取当前时间戳（相对于服务器启动时间的偏移量，秒级）
     * @return 当前时间戳
     */
    public int getCurrentTimestamp() {
        return (int) (Server.getInstance().getCurrentTime() - Server.uptime);
    }

    /**
     * 获取服务器当前时间（微延迟值，在UPDATE_INTERVAL频率以下）
     * @return 服务器当前时间
     */
    public long getCurrentTime() {
        return serverCurrentTime;
    }

    /**
     * 按配置的时间间隔更新服务器当前时间
     */
    public void updateCurrentTime() {
        serverCurrentTime = currentTime.addAndGet(GameConfig.getServerLong("update_interval"));
    }

    /**
     * 强制更新服务器当前时间为系统实际时间
     * @return 更新后的时间值
     */
    public long forceUpdateCurrentTime() {
        long timeNow = System.currentTimeMillis();
        serverCurrentTime = timeNow;
        currentTime.set(timeNow);

        return timeNow;
    }

    /**
     * 获取世界推荐列表
     * @return 世界推荐列表
     */
    public List<Pair<Integer, String>> worldRecommendedList() {
        return worldRecommendedList;
    }

    /**
     * 设置新年贺卡记录
     * @param nyc 新年贺卡记录
     */
    public void setNewYearCard(NewYearCardRecord nyc) {
        newyears.put(nyc.getId(), nyc);
    }

    /**
     * 根据贺卡ID获取新年贺卡记录
     * @param cardid 贺卡ID
     * @return 新年贺卡记录，不存在则返回null
     */
    public NewYearCardRecord getNewYearCard(int cardid) {
        return newyears.get(cardid);
    }

    /**
     * 移除新年贺卡记录
     * @param cardid 贺卡ID
     * @return 被移除的贺卡记录，不存在则返回null
     */
    public NewYearCardRecord removeNewYearCard(int cardid) {
        return newyears.remove(cardid);
    }

    /**
     * 设置开发者房间为可用状态
     */
    public void setAvailableDeveloperRoom() {
        availableDeveloperRoom = true;
    }

    /**
     * 检查是否可以进入开发者房间
     * @return 是否可以进入
     */
    public boolean canEnterDeveloperRoom() {
        return availableDeveloperRoom;
    }

    /**
     * 从数据库加载玩家NPC地图步骤数据
     */
    private void loadPlayerNpcMapStepFromDb() {
        List<PlayernpcsFieldDO> playernpcsFieldDOList = npcService.getPlayerNpcFields(new PlayernpcsFieldDO());
        playernpcsFieldDOList.forEach(playernpcsFieldDO -> {
            World world = getWorld(playernpcsFieldDO.getWorld());
            if (world != null) world.setPlayerNpcMapData(playernpcsFieldDO.getMap(), playernpcsFieldDO.getStep(), playernpcsFieldDO.getPodium());
        });
    }

    /**
     * 根据世界ID获取世界对象
     * @param id 世界ID
     * @return 世界对象，不存在则返回null
     */
    public World getWorld(int id) {
        wldRLock.lock();
        try {
            try {
                return worlds.get(id);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        } finally {
            wldRLock.unlock();
        }
    }

    /**
     * 获取所有世界的只读列表
     * @return 世界列表
     */
    public List<World> getWorlds() {
        wldRLock.lock();
        try {
            return Collections.unmodifiableList(worlds);
        } finally {
            wldRLock.unlock();
        }
    }

    /**
     * 获取世界数量
     * @return 世界数量
     */
    public int getWorldsSize() {
        wldRLock.lock();
        try {
            return worlds.size();
        } finally {
            wldRLock.unlock();
        }
    }

    /**
     * 根据世界ID和频道ID获取频道对象
     * @param world 世界ID
     * @param channel 频道ID
     * @return 频道对象，不存在则返回null
     */
    public Channel getChannel(int world, int channel) {
        try {
            return this.getWorld(world).getChannel(channel);
        } catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * 获取指定世界的所有频道
     * @param world 世界ID
     * @return 频道列表
     */
    public List<Channel> getChannelsFromWorld(int world) {
        try {
            return this.getWorld(world).getChannels();
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    /**
     * 获取所有世界的全部频道
     * @return 所有频道列表
     */
    public List<Channel> getAllChannels() {
        try {
            List<Channel> channelz = new ArrayList<>();
            for (World world : this.getWorlds()) {
                channelz.addAll(world.getChannels());
            }
            return channelz;
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    /**
     * 获取指定世界已开启的频道ID集合
     * @param world 世界ID
     * @return 频道ID集合
     */
    public Set<Integer> getOpenChannels(int world) {
        wldRLock.lock();
        try {
            return new HashSet<>(channels.get(world).keySet());
        } finally {
            wldRLock.unlock();
        }
    }

    /**
     * 获取指定世界和频道的IP地址字符串
     * @param world 世界ID
     * @param channel 频道ID
     * @return IP地址字符串（格式：ip:port）
     */
    private String getIP(int world, int channel) {
        wldRLock.lock();
        try {
            return channels.get(world).get(channel);
        } finally {
            wldRLock.unlock();
        }
    }

    /**
     * 根据客户端IP类型获取合适的服务器地址
     * 本地地址使用localhost，局域网地址使用LAN IP，外网地址使用公网IP
     * @param client 客户端连接
     * @param world 世界ID
     * @param channel 频道ID
     * @return 包含IP和端口的字符串数组，[0]=IP, [1]=端口
     */
    public String[] getInetSocket(Client client, int world, int channel) {
        String remoteIp = client.getRemoteAddress();

        String[] hostAddress = getIP(world, channel).split(":");
        if (IpAddresses.isLocalAddress(remoteIp)) {
            hostAddress[0] = serviceProperty.getLocalhost();
        } else if (IpAddresses.isLanAddress(remoteIp)) {
            hostAddress[0] = serviceProperty.getLanHost();
        }

        try {
            return hostAddress;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 在指定世界添加一个新频道
     * @param worldid 世界ID
     * @return 新频道ID，-2表示已达最大频道数，-3表示世界不存在
     */
    public int addChannel(int worldid) {
        World world;
        Map<Integer, String> channelInfo;
        int channelid;

        wldRLock.lock();
        try {
            if (worldid >= worlds.size()) {
                return -3;
            }

            channelInfo = channels.get(worldid);
            if (channelInfo == null) {
                return -3;
            }

            channelid = channelInfo.size();
            if (channelid >= GameConfig.getServerInt("max_channel_size")) {
                return -2;
            }

            channelid++;
            world = this.getWorld(worldid);
        } finally {
            wldRLock.unlock();
        }

        Channel channel = new Channel(worldid, channelid, getCurrentTime());
        channel.setServerMessage(GameConfig.getWorldString(worldid, "recommend_message"));

        if (world.addChannel(channel)) {
            wldWLock.lock();
            try {
                channelInfo.put(channelid, channel.getIP());
            } finally {
                wldWLock.unlock();
            }
        }

        return channelid;
    }

    /**
     * 添加一个新世界
     * @return 新世界ID，-1表示已达最大世界数
     */
    public int addWorld() {
        int newWorld = initWorld();
        if (newWorld > -1) {
            reloadWorldsPlayerRanking();

            Set<Integer> accounts;
            lgnRLock.lock();
            try {
                accounts = new HashSet<>(accountChars.keySet());
            } finally {
                lgnRLock.unlock();
            }

            for (Integer accId : accounts) {
                loadAccountCharactersView(accId, 0, newWorld);
            }
        }

        return newWorld;
    }

    /**
     * 初始化一个世界，从配置读取倍率等参数并创建频道
     * @return 世界ID，-1表示已达最大世界数，-2表示部署失败
     */
    private int initWorld() {
        int i;

        wldRLock.lock();
        try {
            i = worlds.size();

            if (i >= GameConfig.getServerInt("max_world_size")) {
                return -1;
            }
        } finally {
            wldRLock.unlock();
        }

        log.info(I18nUtil.getLogMessage("Server.initWorld.info1"), i);

        float expRate = GameConfig.getWorldFloat(i, "exp_rate");
        float mesoRate = GameConfig.getWorldFloat(i, "meso_rate");
        float dropRate = GameConfig.getWorldFloat(i, "drop_rate");
        float bossDropRate = GameConfig.getWorldFloat(i, "boss_drop_rate");
        float questRate = GameConfig.getWorldFloat(i, "quest_rate");
        float travelRate = GameConfig.getWorldFloat(i, "travel_rate");
        float fishingRate = GameConfig.getWorldFloat(i, "fishing_rate");

        int flag = GameConfig.getWorldInt(i, "flag");
        String event_message = GameConfig.getWorldString(i, "event_message");
        String recommend_message = GameConfig.getWorldString(i, "recommend_message");

        World world = new World(i, flag, event_message, expRate, dropRate, bossDropRate, mesoRate, questRate,
                travelRate, fishingRate);

        Map<Integer, String> channelInfo = new HashMap<>();
        long bootTime = getCurrentTime();
        for (int j = 1; j <= GameConfig.getWorldInt(i, "channel_size"); j++) {
            Channel channel = new Channel(i, j, bootTime);

            world.addChannel(channel);
            channelInfo.put(j, channel.getIP());
        }

        boolean canDeploy;

        // thanks Ashen for noticing a deadlock issue when trying to deploy a channel
        wldWLock.lock();
        try {
            canDeploy = world.getId() == worlds.size();
            if (canDeploy) {
                worldRecommendedList.add(new Pair<>(i, recommend_message));
                worlds.add(world);
                channels.add(i, channelInfo);
            }
        } finally {
            wldWLock.unlock();
        }

        if (canDeploy) {
            world.setServerMessage(GameConfig.getWorldString(i, "server_message"));

            log.info(I18nUtil.getLogMessage("Server.initWorld.info2"), i);
            return i;
        } else {
            log.error(I18nUtil.getLogMessage("Server.initWorld.error1"), i);
            world.shutdown();
            return -2;
        }
    }

    /**
     * 从指定世界移除一个频道
     * @param worldid 世界ID
     * @return 是否成功移除
     */
    //lol don't!
    public boolean removeChannel(int worldid) {
        World world;

        wldRLock.lock();
        try {
            if (worldid >= worlds.size()) {
                return false;
            }
            world = worlds.get(worldid);
        } finally {
            wldRLock.unlock();
        }

        if (world != null) {
            int channel = world.removeChannel();
            wldWLock.lock();
            try {
                Map<Integer, String> m = channels.get(worldid);
                if (m != null) {
                    m.remove(channel);
                }
            } finally {
                wldWLock.unlock();
            }

            return channel > -1;
        }

        return false;
    }

    /**
     * 移除最后一个世界
     * @return 是否成功移除
     */
    //lol don't!
    public boolean removeWorld() {
        World w;
        int worldid;

        wldRLock.lock();
        try {
            worldid = worlds.size() - 1;
            if (worldid < 0) {
                return false;
            }

            w = worlds.get(worldid);
        } finally {
            wldRLock.unlock();
        }

        if (w == null || !w.canUninstall()) {
            return false;
        }

        w.shutdown();

        wldWLock.lock();
        try {
            if (worldid == worlds.size() - 1) {
                worlds.remove(worldid);
                channels.remove(worldid);
                worldRecommendedList.remove(worldid);
            }
            reloadWorldsPlayerRanking();
        } finally {
            wldWLock.unlock();
        }

        return true;
    }

    /**
     * 重置服务器世界数据（清空世界列表、频道列表和推荐列表）
     */
    // thanks maple006 for noticing proprietary lists assigned to null
    private void resetServerWorlds() {
        wldWLock.lock();
        try {
            worlds.clear();
            channels.clear();
            worldRecommendedList.clear();
        } finally {
            wldWLock.unlock();
        }
    }

    /**
     * 获取距离下一个整点的剩余时间（毫秒）
     * @return 剩余毫秒数
     */
    private static long getTimeLeftForNextHour() {
        Calendar nextHour = Calendar.getInstance();
        nextHour.add(Calendar.HOUR, 1);
        nextHour.set(Calendar.MINUTE, 0);
        nextHour.set(Calendar.SECOND, 0);

        return Math.max(0, nextHour.getTimeInMillis() - System.currentTimeMillis());
    }

    /**
     * 获取距离下一天的剩余时间（毫秒）
     * @return 剩余毫秒数
     */
    public static long getTimeLeftForNextDay() {
        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DAY_OF_MONTH, 1);
        nextDay.set(Calendar.HOUR_OF_DAY, 0);
        nextDay.set(Calendar.MINUTE, 0);
        nextDay.set(Calendar.SECOND, 0);

        return Math.max(0, nextDay.getTimeInMillis() - System.currentTimeMillis());
    }

    /**
     * 获取优惠券倍率映射表
     * @return 优惠券倍率Map
     */
    public Map<Integer, Integer> getCouponRates() {
        return couponRates;
    }

    /**
     * 获取当前激活的优惠券ID列表
     * @return 激活优惠券列表
     */
    public List<Integer> getActiveCoupons() {
        synchronized (activeCoupons) {
            return activeCoupons;
        }
    }

    /**
     * 向所有在线玩家提交激活的优惠券倍率更新
     */
    public void commitActiveCoupons() {
        for (World world : getWorlds()) {
            for (Character chr : world.getPlayerStorage().getAllCharacters()) {
                if (!chr.isLoggedIn()) {
                    continue;
                }

                chr.updateCouponRates();
            }
        }
    }

    /**
     * 切换优惠券的激活状态
     * @param couponId 优惠券ID
     */
    public void toggleCoupon(Integer couponId) {
        if (ItemConstants.isRateCoupon(couponId)) {
            synchronized (activeCoupons) {
                if (activeCoupons.contains(couponId)) {
                    activeCoupons.remove(couponId);
                } else {
                    activeCoupons.add(couponId);
                }

                commitActiveCoupons();
            }
        }
    }

    /**
     * 从数据库更新当前时间段的激活优惠券列表
     */
    public void updateActiveCoupons() {
        synchronized (activeCoupons) {
            activeCoupons.clear();
            Calendar c = Calendar.getInstance();
            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            int hourDay = c.get(Calendar.HOUR_OF_DAY);
            int weekdayMask = (1 << weekDay);
            activeCoupons.addAll(nxCouponService.selectActiveCouponIds(weekdayMask, hourDay));
        }
    }

    /**
     * 运行玩家疾病通知调度
     * 先处理待通知列表中的玩家，然后将已注册玩家移至待处理列表
     */
    public void runAnnouncePlayerDiseasesSchedule() {
        List<Client> processDiseaseAnnounceClients;
        disLock.lock();
        try {
            processDiseaseAnnounceClients = new LinkedList<>(processDiseaseAnnouncePlayers);
            processDiseaseAnnouncePlayers.clear();
        } finally {
            disLock.unlock();
        }

        while (!processDiseaseAnnounceClients.isEmpty()) {
            Client c = processDiseaseAnnounceClients.remove(0);
            Character player = c.getPlayer();
            if (player != null && player.isLoggedInWorld()) {
                player.announceDiseases();
                player.collectDiseases();
            }
        }

        disLock.lock();
        try {
            // this is to force the system to wait for at least one complete tick before releasing disease info for the registered clients
            while (!registeredDiseaseAnnouncePlayers.isEmpty()) {
                Client c = registeredDiseaseAnnouncePlayers.remove(0);
                processDiseaseAnnouncePlayers.add(c);
            }
        } finally {
            disLock.unlock();
        }
    }

    /**
     * 注册客户端以接收疾病通知
     * @param c 客户端
     */
    public void registerAnnouncePlayerDiseases(Client c) {
        disLock.lock();
        try {
            registeredDiseaseAnnouncePlayers.add(c);
        } finally {
            disLock.unlock();
        }
    }

    /**
     * 获取指定世界的玩家排行榜
     * @param worldid 世界ID
     * @return 排行榜列表（角色名和等级的键值对）
     */
    public List<Pair<String, Integer>> getWorldPlayerRanking(int worldid) {
        wldRLock.lock();
        try {
            return new ArrayList<>(playerRanking.get(!GameConfig.getServerBoolean("use_whole_server_ranking") ? worldid : 0));
        } finally {
            wldRLock.unlock();
        }
    }

    /**
     * 重新加载所有世界的玩家排行榜
     */
    public void reloadWorldsPlayerRanking() {
        List<List<CharactersDO>> rankPlayers = characterService.getWorldsRankPlayers(getWorldsSize());
        if (rankPlayers.isEmpty()) {
            return;
        }
        wldWLock.lock();
        try {
            playerRanking.clear();
            rankPlayers.forEach(rankPlayer -> playerRanking.add(rankPlayer.stream().map(c -> new Pair<>(c.getName(), c.getLevel())).collect(Collectors.toList())));
        } finally {
            wldWLock.unlock();
        }
    }

    /**
     * 游戏服务器初始化
     * 加载技能、现金道具、任务、技能书数据，初始化各个世界并启动登录服务器
     */
    //游戏启动
    public void init() {
        Instant beforeInit = Instant.now();
        log.info(I18nUtil.getLogMessage("Server.init.info1"), ServerConstants.VERSION);

        // 发送信件
        registerChannelDependencies();

        // 利用虚拟线程，减少开销
        log.info(I18nUtil.getLogMessage("Server.init.info2"));
        try (ExecutorService initExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 加载wz
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(initExecutor.submit(SkillFactory::loadAllSkills));
            futures.add(initExecutor.submit(CashItemFactory::loadAllCashItems));
            futures.add(initExecutor.submit(Quest::loadAllQuests));
            futures.add(initExecutor.submit(SkillbookInformationProvider::loadAllSkillbookInformation));
            // Wait on all async tasks to complete
            for (Future<?> future : futures) {
                future.get();
            }
            initExecutor.shutdown();
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("Server.init.error1"), e);
            throw new IllegalStateException(e);
        }
        log.info(I18nUtil.getLogMessage("Server.init.info3"));

        TimeZone.setDefault(TimeZone.getTimeZone(GameConfig.getServerString("timezone")));

        log.info(I18nUtil.getLogMessage("Server.init.info4"));
        final int worldCount = Math.min(GameConstants.WORLD_NAMES.length, GameConfig.getConfig().getJSONObject("world").size());

        // 重置登录状态和雇佣商店状态
        accountService.resetAllLoggedIn();
        characterService.resetMerchant();

        // 清空失效的现金物品
        nxCodeService.clearExpirations();

        // 重载倍率卡
        List<NxcouponsDO> nxcouponsDOList = nxCouponService.getNxCoupons(new NxcouponsDO());
        couponRates.clear();
        nxcouponsDOList.forEach(nxcouponsDO -> couponRates.put(nxcouponsDO.getCouponid(), nxcouponsDO.getRate()));
        updateActiveCoupons();
        newYearCardService.startPendingNewYearCardRequests();
        CashIdGenerator.loadExistentCashIdsFromDb();

        // 接受未完成的改名
        nameChangeService.applyAllNameChange();

        // 接受转区
        worldTransferService.applyAllWorldTransfer();

        // 加载玩家排名
        PlayerNPC.loadRunningRankData(worldCount);

        // 加载自动封禁配置
        autobanConfigService.loadConfigs();

        // 主动清理每日零点需要清理的数据
        new BossLogTask().run();
        new ExtendValueTask().run();
        log.info(I18nUtil.getLogMessage("Server.init.info5"));

        ThreadManager.getInstance().start();
        // aggregated method for timely tasks thanks to lxconan
        initializeTimelyTasks();

        try {
            for (int i = 0; i < worldCount; i++) {
                initWorld();
            }
            // world初始化后需要加载的
            reloadWorldsPlayerRanking();
            loadPlayerNpcMapStepFromDb();
            if (GameConfig.getServerBoolean("use_family_system")) {
                familyService.loadAllFamilies();
            }
        } catch (Exception e) {
            //For those who get errors
            log.error(I18nUtil.getLogMessage("Server.init.error3"), e);
            System.exit(0);
        }

        loginServer = initLoginServer(serviceProperty.getLoginPort());
        log.info(I18nUtil.getLogMessage("Server.init.info6"), serviceProperty.getLoginPort());

        OpcodeConstants.generateOpcodeNames();
        CommandsExecutor.getInstance().loadCommandsExecutor();

        log.info(I18nUtil.getLogMessage("Server.init.info7"));
        for (Channel ch : this.getAllChannels()) {
            ch.reloadEventScriptManager();
        }
        log.info(I18nUtil.getLogMessage("Server.init.info8"));
        online = true;
        Duration initDuration = Duration.between(beforeInit, Instant.now());
        log.info(I18nUtil.getLogMessage("Server.init.info9"), initDuration.toMillis() / 1000.0);
    }

    /**
     * 注册频道依赖服务
     * 创建信件处理器和频道依赖关系
     */
    private void registerChannelDependencies() {
        FredrickProcessor fredrickProcessor = new FredrickProcessor(noteService);
        ChannelDependencies channelDependencies = new ChannelDependencies(noteService, fredrickProcessor);
        PacketProcessor.registerGameHandlerDependencies(channelDependencies);
        this.channelDependencies = channelDependencies;
    }

    /**
     * 初始化并启动登录服务器
     * @param port 登录服务器端口
     * @return 登录服务器实例
     */
    private LoginServer initLoginServer(int port) {
        LoginServer loginServer = new LoginServer(port);
        loginServer.start();
        return loginServer;
    }

    /**
     * 初始化定时任务
     * 注册疾病检测、优惠券更新、排行榜刷新、Boss日志等周期性任务
     */
    private void initializeTimelyTasks() {
        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        //Purging ftw...
        tMan.register(tMan.purge(), MINUTES.toMillis(5));
        disconnectIdlesOnLoginTask();

        long timeLeft = getTimeLeftForNextHour();
        tMan.register(new CharacterDiseaseTask(), GameConfig.getServerLong("update_interval"), GameConfig.getServerLong("update_interval"));
        tMan.register(new CouponTask(), HOURS.toMillis(1), timeLeft);
        tMan.register(new RankingCommandTask(), MINUTES.toMillis(5), MINUTES.toMillis(5));
        tMan.register(new RankingLoginTask(), HOURS.toMillis(1), timeLeft);
        tMan.register(new LoginCoordinatorTask(), HOURS.toMillis(1), timeLeft);
        tMan.register(new EventRecallCoordinatorTask(), HOURS.toMillis(1), timeLeft);
        tMan.register(new LoginStorageTask(), MINUTES.toMillis(2), MINUTES.toMillis(2));
        tMan.register(new DueyFredrickTask(channelDependencies.fredrickProcessor()), HOURS.toMillis(1), timeLeft);
        tMan.register(new InvitationTask(), SECONDS.toMillis(30), SECONDS.toMillis(30));
        tMan.register(new RespawnTask(), GameConfig.getServerLong("respawn_interval"), GameConfig.getServerLong("respawn_interval"));
        tMan.register(new OnlineTimeTask(), 5000, 5000);

        timeLeft = getTimeLeftForNextDay();
        ExpeditionBossLog.resetBossLogTable();
        tMan.register(new BossLogTask(), DAYS.toMillis(1), timeLeft);
        tMan.register(new ExtendValueTask(), DAYS.toMillis(1), timeLeft);
    }

    /**
     * 根据ID获取联盟
     * @param id 联盟ID
     * @return 联盟对象，不存在则返回null
     */
    public Alliance getAlliance(int id) {
        synchronized (alliances) {
            if (alliances.containsKey(id)) {
                return alliances.get(id);
            }
            return null;
        }
    }

    /**
     * 添加联盟
     * @param id 联盟ID
     * @param alliance 联盟对象
     */
    public void addAlliance(int id, Alliance alliance) {
        synchronized (alliances) {
            if (!alliances.containsKey(id)) {
                alliances.put(id, alliance);
            }
        }
    }

    /**
     * 解散联盟
     * @param id 联盟ID
     */
    public void disbandAlliance(int id) {
        synchronized (alliances) {
            Alliance alliance = alliances.get(id);
            if (alliance != null) {
                for (Integer gid : alliance.getGuilds()) {
                    guilds.get(gid).setAllianceId(0);
                }
                alliances.remove(id);
            }
        }
    }

    /**
     * 向联盟内所有公会（排除指定公会）发送消息
     * @param id 联盟ID
     * @param packet 数据包
     * @param exception 排除的角色ID
     * @param guildex 排除的公会ID
     */
    public void allianceMessage(int id, Packet packet, int exception, int guildex) {
        Alliance alliance = alliances.get(id);
        if (alliance != null) {
            for (Integer gid : alliance.getGuilds()) {
                if (guildex == gid) {
                    continue;
                }
                Guild guild = guilds.get(gid);
                if (guild != null) {
                    guild.broadcast(packet, exception);
                }
            }
        }
    }

    /**
     * 将公会添加到联盟
     * @param aId 联盟ID
     * @param guildId 公会ID
     * @return 是否成功添加
     */
    public boolean addGuildtoAlliance(int aId, int guildId) {
        Alliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.addGuild(guildId);
            guilds.get(guildId).setAllianceId(aId);
            return true;
        }
        return false;
    }

    /**
     * 从联盟中移除公会
     * @param aId 联盟ID
     * @param guildId 公会ID
     * @return 是否成功移除
     */
    public boolean removeGuildFromAlliance(int aId, int guildId) {
        Alliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.removeGuild(guildId);
            guilds.get(guildId).setAllianceId(0);
            return true;
        }
        return false;
    }

    /**
     * 设置联盟职位名称
     * @param aId 联盟ID
     * @param ranks 职位名称数组
     * @return 是否成功设置
     */
    public boolean setAllianceRanks(int aId, String[] ranks) {
        Alliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.setRankTitle(ranks);
            return true;
        }
        return false;
    }

    /**
     * 设置联盟公告
     * @param aId 联盟ID
     * @param notice 公告内容
     * @return 是否成功设置
     */
    public boolean setAllianceNotice(int aId, String notice) {
        Alliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.setNotice(notice);
            return true;
        }
        return false;
    }

    /**
     * 增加联盟容量
     * @param aId 联盟ID
     * @param inc 增加的数量
     * @return 是否成功增加
     */
    public boolean increaseAllianceCapacity(int aId, int inc) {
        Alliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.increaseCapacity(inc);
            return true;
        }
        return false;
    }

    /**
     * 创建公会
     * @param leaderId 会长角色ID
     * @param name 公会名称
     * @return 新公会的ID
     */
    public int createGuild(int leaderId, String name) {
        return Guild.createGuild(leaderId, name);
    }

    /**
     * 根据名称查找公会（忽略大小写）
     * @param name 公会名称
     * @return 公会对象，不存在则返回null
     */
    public Guild getGuildByName(String name) {
        synchronized (guilds) {
            for (Guild mg : guilds.values()) {
                if (mg.getName().equalsIgnoreCase(name)) {
                    return mg;
                }
            }

            return null;
        }
    }

    /**
     * 根据ID获取公会（仅从内存查找）
     * @param id 公会ID
     * @return 公会对象，不存在则返回null
     */
    public Guild getGuild(int id) {
        synchronized (guilds) {
            if (guilds.get(id) != null) {
                return guilds.get(id);
            }

            return null;
        }
    }

    /**
     * 根据ID和世界获取公会
     * @param id 公会ID
     * @param world 世界ID
     * @return 公会对象，不存在则返回null
     */
    public Guild getGuild(int id, int world) {
        return getGuild(id, world, null);
    }

    /**
     * 根据ID、世界和角色获取公会（内存不存在则从数据库加载）
     * @param id 公会ID
     * @param world 世界ID
     * @param mc 角色对象
     * @return 公会对象，不存在则返回null
     */
    public Guild getGuild(int id, int world, Character mc) {
        synchronized (guilds) {
            Guild g = guilds.get(id);
            if (g != null) {
                return g;
            }
            // character表默认的guildId为0，导致会生成一个没有名字的guid，进而影响showGuildInfo的发包
            if (id == 0) {
                return null;
            }

            g = new Guild(id, world);
            if (g.getId() == -1) {
                return null;
            }

            if (mc != null) {
                GuildCharacter mgc = g.getMGC(mc.getId());
                if (mgc != null) {
                    mc.setMGC(mgc);
                    mgc.setCharacter(mc);
                } else {
                    log.error("Could not find chr {} when loading guild {}", mc.getName(), id);
                }

                g.setOnline(mc.getId(), true, mc.getClient().getChannel());
            }

            guilds.put(id, g);
            return g;
        }
    }

    /**
     * 设置公会成员的在线状态
     * @param mc 角色
     * @param bOnline 是否在线
     * @param channel 所在频道
     */
    public void setGuildMemberOnline(Character mc, boolean bOnline, int channel) {
        Guild g = getGuild(mc.getGuildId(), mc.getWorld(), mc);
        g.setOnline(mc.getId(), bOnline, channel);
    }

    /**
     * 添加公会成员
     * @param mgc 公会角色信息
     * @param chr 角色对象
     * @return 操作结果码
     */
    public int addGuildMember(GuildCharacter mgc, Character chr) {
        Guild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            return g.addGuildMember(mgc, chr);
        }
        return 0;
    }

    /**
     * 设置公会的联盟ID
     * @param gId 公会ID
     * @param aId 联盟ID
     * @return 是否成功设置
     */
    public boolean setGuildAllianceId(int gId, int aId) {
        Guild guild = guilds.get(gId);
        if (guild != null) {
            guild.setAllianceId(aId);
            return true;
        }
        return false;
    }

    /**
     * 重置联盟内公会成员的排名
     * @param gId 公会ID
     */
    public void resetAllianceGuildPlayersRank(int gId) {
        guilds.get(gId).resetAllianceGuildPlayersRank();
    }

    /**
     * 退出公会
     * @param mgc 公会角色信息
     */
    public void leaveGuild(GuildCharacter mgc) {
        Guild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            g.leaveGuild(mgc);
        }
    }

    /**
     * 公会聊天
     * @param gid 公会ID
     * @param name 发言人名称
     * @param cid 发言人角色ID
     * @param msg 消息内容
     */
    public void guildChat(int gid, String name, int cid, String msg) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.guildChat(name, cid, msg);
        }
    }

    /**
     * 修改公会成员职位
     * @param gid 公会ID
     * @param cid 角色ID
     * @param newRank 新职位等级
     */
    public void changeRank(int gid, int cid, int newRank) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.changeRank(cid, newRank);
        }
    }

    /**
     * 开除公会成员
     * @param initiator 发起操作的公会角色
     * @param name 被开除角色名
     * @param cid 被开除角色ID
     */
    public void expelMember(GuildCharacter initiator, String name, int cid) {
        Guild g = guilds.get(initiator.getGuildId());
        if (g != null) {
            g.expelMember(initiator, name, cid, channelDependencies.noteService());
        }
    }

    /**
     * 设置公会公告
     * @param gid 公会ID
     * @param notice 公告内容
     */
    public void setGuildNotice(int gid, String notice) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.setGuildNotice(notice);
        }
    }

    /**
     * 更新公会成员的等级和职业信息
     * @param mgc 公会角色信息
     */
    public void memberLevelJobUpdate(GuildCharacter mgc) {
        Guild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            g.memberLevelJobUpdate(mgc);
        }
    }

    /**
     * 修改公会职位名称
     * @param gid 公会ID
     * @param ranks 职位名称数组
     */
    public void changeRankTitle(int gid, String[] ranks) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.changeRankTitle(ranks);
        }
    }

    /**
     * 设置公会徽章
     * @param gid 公会ID
     * @param bg 背景图案ID
     * @param bgcolor 背景颜色
     * @param logo 徽标图案ID
     * @param logocolor 徽标颜色
     */
    public void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.setGuildEmblem(bg, bgcolor, logo, logocolor);
        }
    }

    /**
     * 解散公会
     * @param gid 公会ID
     */
    public void disbandGuild(int gid) {
        synchronized (guilds) {
            Guild g = guilds.get(gid);
            g.disbandGuild();
            guilds.remove(gid);
        }
    }

    /**
     * 增加公会容量
     * @param gid 公会ID
     * @return 是否成功增加
     */
    public boolean increaseGuildCapacity(int gid) {
        Guild g = guilds.get(gid);
        if (g != null) {
            return g.increaseCapacity();
        }
        return false;
    }

    /**
     * 增加公会GP点数
     * @param gid 公会ID
     * @param amount 增加量
     */
    public void gainGP(int gid, int amount) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.gainGP(amount);
        }
    }

    /**
     * 向公会所有成员广播消息
     * @param gid 公会ID
     * @param packet 数据包
     */
    public void guildMessage(int gid, Packet packet) {
        guildMessage(gid, packet, -1);
    }

    /**
     * 向公会所有成员广播消息（排除指定角色）
     * @param gid 公会ID
     * @param packet 数据包
     * @param exception 排除的角色ID，-1表示不排除
     */
    public void guildMessage(int gid, Packet packet, int exception) {
        Guild g = guilds.get(gid);
        if (g != null) {
            g.broadcast(packet, exception);
        }
    }

    /**
     * 获取玩家Buff存储
     * @return PlayerBuffStorage实例
     */
    public PlayerBuffStorage getPlayerBuffStorage() {
        return buffStorage;
    }

    /**
     * 删除公会角色（根据Character对象）
     * 普通成员退出公会，会长则解散公会
     * @param mc 角色对象
     */
    public void deleteGuildCharacter(Character mc) {
        setGuildMemberOnline(mc, false, (byte) -1);
        if (mc.getMGC().getGuildRank() > 1) {
            leaveGuild(mc.getMGC());
        } else {
            disbandGuild(mc.getMGC().getGuildId());
        }
    }

    /**
     * 删除公会角色（根据GuildCharacter对象）
     * 普通成员退出公会，会长则解散公会
     * @param mgc 公会角色信息
     */
    public void deleteGuildCharacter(GuildCharacter mgc) {
        if (mgc.getCharacter() != null) {
            setGuildMemberOnline(mgc.getCharacter(), false, (byte) -1);
        }
        if (mgc.getGuildRank() > 1) {
            leaveGuild(mgc);
        } else {
            disbandGuild(mgc.getGuildId());
        }
    }

    /**
     * 重新加载指定世界中所有在线角色的公会信息
     * @param world 世界ID
     */
    public void reloadGuildCharacters(int world) {
        World worlda = getWorld(world);
        for (Character mc : worlda.getPlayerStorage().getAllCharacters()) {
            if (mc.getGuildId() > 0) {
                setGuildMemberOnline(mc, true, worlda.getId());
                memberLevelJobUpdate(mc.getMGC());
            }
        }
        worlda.reloadGuildSummary();
    }

    /**
     * 向指定世界的所有频道广播消息
     * @param world 世界ID
     * @param packet 数据包
     */
    public void broadcastMessage(int world, Packet packet) {
        for (Channel ch : getChannelsFromWorld(world)) {
            ch.broadcastPacket(packet);
        }
    }

    /**
     * 向指定世界的所有频道广播GM消息
     * @param world 世界ID
     * @param packet 数据包
     */
    public void broadcastGMMessage(int world, Packet packet) {
        for (Channel ch : getChannelsFromWorld(world)) {
            ch.broadcastGMPacket(packet);
        }
    }

    /**
     * 检查指定世界是否有GM在线
     * @param world 世界ID
     * @return 是否有GM在线
     */
    public boolean isGmOnline(int world) {
        for (Channel ch : getChannelsFromWorld(world)) {
            for (Character player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改账号的飞行权限
     * @param accountid 账号ID
     * @param canFly 是否可以飞行
     */
    public void changeFly(Integer accountid, boolean canFly) {
        if (canFly) {
            activeFly.add(accountid);
        } else {
            activeFly.remove(accountid);
        }
    }

    /**
     * 检查账号是否可以飞行
     * @param accountid 账号ID
     * @return 是否可以飞行
     */
    public boolean canFly(Integer accountid) {
        return activeFly.contains(accountid);
    }

    /**
     * 获取角色所在的世界ID
     * @param chrid 角色ID
     * @return 世界ID，不存在则返回-1
     */
    public int getCharacterWorld(Integer chrid) {
        lgnRLock.lock();
        try {
            Integer worldid = worldChars.get(chrid);
            return worldid != null ? worldid : -1;
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 检查账号下是否包含指定角色
     * @param accountid 账号ID
     * @param chrid 角色ID
     * @return 是否包含
     */
    public boolean haveCharacterEntry(Integer accountid, Integer chrid) {
        lgnRLock.lock();
        try {
            Set<Integer> accChars = accountChars.get(accountid);
            return accChars.contains(chrid);
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 获取账号的角色总数
     * @param accountid 账号ID
     * @return 角色总数
     */
    public short getAccountCharacterCount(Integer accountid) {
        lgnRLock.lock();
        try {
            return accountCharacterCount.get(accountid);
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 获取账号在指定世界的角色数量
     * @param accountid 账号ID
     * @param worldid 世界ID
     * @return 角色数量
     */
    public short getAccountWorldCharacterCount(Integer accountid, Integer worldid) {
        lgnRLock.lock();
        try {
            short count = 0;

            for (Integer chr : accountChars.get(accountid)) {
                if (worldChars.get(chr).equals(worldid)) {
                    count++;
                }
            }

            return count;
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 获取账号下的角色ID集合
     * @param accountid 账号ID
     * @return 角色ID集合
     */
    private Set<Integer> getAccountCharacterEntries(Integer accountid) {
        lgnRLock.lock();
        try {
            return new HashSet<>(accountChars.get(accountid));
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 更新角色在角色选择界面的展示信息
     * @param chr 角色对象
     */
    public void updateCharacterEntry(Character chr) {
        // 已经不在线，不再更新角色视图
        if (!chr.isLoggedIn() || chr.getAccountId() == 0) {
            return;
        }

        Character chrView = chr.generateCharacterEntry();

        lgnWLock.lock();
        try {
            World wserv = this.getWorld(chrView.getWorld());
            if (wserv != null) {
                wserv.registerAccountCharacterView(chrView.getAccountId(), chrView);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    /**
     * 创建角色条目（新角色注册到账号下）
     * @param chr 角色对象
     */
    public void createCharacterEntry(Character chr) {
        Integer accountid = chr.getAccountId(), chrid = chr.getId(), world = chr.getWorld();

        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountid, (short) (accountCharacterCount.get(accountid) + 1));

            Set<Integer> accChars = accountChars.get(accountid);
            accChars.add(chrid);

            worldChars.put(chrid, world);

            Character chrView = chr.generateCharacterEntry();

            World wserv = this.getWorld(chrView.getWorld());
            if (wserv != null) {
                wserv.registerAccountCharacterView(chrView.getAccountId(), chrView);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    /**
     * 删除角色条目
     * @param accountid 账号ID
     * @param chrid 角色ID
     */
    public void deleteCharacterEntry(Integer accountid, Integer chrid) {
        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountid, (short) (accountCharacterCount.get(accountid) - 1));

            Set<Integer> accChars = accountChars.get(accountid);
            accChars.remove(chrid);

            Integer world = worldChars.remove(chrid);
            if (world != null) {
                World wserv = this.getWorld(world);
                if (wserv != null) {
                    wserv.unregisterAccountCharacterView(accountid, chrid);
                }
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    /**
     * 转移角色的世界归属（在设置新世界ID之前调用）
     * @param chr 角色对象
     * @param toWorld 目标世界ID
     */
    // used before setting the new worldid on the character object
    public void transferWorldCharacterEntry(Character chr, Integer toWorld) {
        lgnWLock.lock();
        try {
            Integer chrid = chr.getId(), accountid = chr.getAccountId(), world = worldChars.get(chr.getId());
            if (world != null) {
                World wserv = this.getWorld(world);
                if (wserv != null) {
                    wserv.unregisterAccountCharacterView(accountid, chrid);
                }
            }

            worldChars.put(chrid, toWorld);

            Character chrView = chr.generateCharacterEntry();

            World wserv = this.getWorld(toWorld);
            if (wserv != null) {
                wserv.registerAccountCharacterView(chrView.getAccountId(), chrView);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    // is this even a thing?
    //public void deleteAccountEntry(Integer accountid) {
    //    lgnWLock.lock();
    //    try {
    //        accountCharacterCount.remove(accountid);
    //        accountChars.remove(accountid);
    //    } finally {
    //        lgnWLock.unlock();
    //    }
    //
    //    for (World wserv : this.getWorlds()) {
    //        wserv.clearAccountCharacterView(accountid);
    //        wserv.unregisterAccountStorage(accountid);
    //    }
    //}

    /**
     * 加载账号的角色列表（按世界分组）
     * @param accountId 账号ID
     * @param visibleWorlds 可见世界数量
     * @return 按世界ID排序的角色列表映射
     */
    public SortedMap<Integer, List<Character>> loadAccountCharlist(int accountId, int visibleWorlds) {
        List<World> worlds = this.getWorlds();
        if (worlds.size() > visibleWorlds) {
            worlds = worlds.subList(0, visibleWorlds);
        }

        SortedMap<Integer, List<Character>> worldChrs = new TreeMap<>();
        int chrTotal = 0;

        lgnRLock.lock();
        try {
            for (World world : worlds) {
                List<Character> chrs = world.getAccountCharactersView(accountId);
                if (chrs == null) {
                    if (!accountChars.containsKey(accountId)) {
                        accountCharacterCount.put(accountId, (short) 0);
                        // not advisable at all to write on the map on a read-protected environment
                        accountChars.put(accountId, new HashSet<>());
                        // yet it's known there's no problem since no other point in the source does
                        // this action.
                    }
                } else if (!chrs.isEmpty()) {
                    worldChrs.put(world.getId(), chrs);
                }
            }
        } finally {
            lgnRLock.unlock();
        }

        return worldChrs;
    }

    /**
     * 从数据库加载账号的角色视图数据
     * @param accId 账号ID
     * @param wlen 世界数量
     * @return 角色数量和各世界角色列表的键值对
     */
    private static Pair<Short, List<List<Character>>> loadAccountCharactersViewFromDb(int accId, int wlen) {
        short characterCount = 0;
        List<List<Character>> wchars = new ArrayList<>(wlen);
        for (int i = 0; i < wlen; i++) {
            wchars.add(i, new LinkedList<>());
        }

        List<Character> chars = new LinkedList<>();
        int curWorld = 0;
        try {
            List<Pair<Item, Integer>> accEquips = ItemFactory.loadEquippedItems(accId, true, true);
            Map<Integer, List<Item>> accPlayerEquips = new HashMap<>();

            for (Pair<Item, Integer> ae : accEquips) {
                List<Item> playerEquips = accPlayerEquips.get(ae.getRight());
                if (playerEquips == null) {
                    playerEquips = new LinkedList<>();
                    accPlayerEquips.put(ae.getRight(), playerEquips);
                }

                playerEquips.add(ae.getLeft());
            }


            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY world, id")) {
                ps.setInt(1, accId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        characterCount++;

                        int cworld = rs.getByte("world");
                        if (cworld >= wlen) {
                            continue;
                        }

                        if (cworld > curWorld) {
                            wchars.add(curWorld, chars);

                            curWorld = cworld;
                            chars = new LinkedList<>();
                        }

                        Integer cid = rs.getInt("id");
                        chars.add(Character.loadCharacterEntryFromDB(rs, accPlayerEquips.get(cid)));
                    }
                }
            }

            wchars.add(curWorld, chars);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return new Pair<>(characterCount, wchars);
    }

    /**
     * 加载所有账号的角色视图数据
     */
    public void loadAllAccountsCharactersView() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id FROM accounts");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int accountId = rs.getInt("id");
                if (isFirstAccountLogin(accountId)) {
                    loadAccountCharactersView(accountId, 0, 0);
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * 检查账号是否首次登录（内存中无角色数据）
     * @param accId 账号ID
     * @return 是否首次登录
     */
    private boolean isFirstAccountLogin(Integer accId) {
        lgnRLock.lock();
        try {
            return !accountChars.containsKey(accId);
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 加载账号的角色信息并设置GM等级
     * @param c 客户端连接
     */
    public void loadAccountCharacters(Client c) {
        Integer accId = c.getAccID();
        if (!isFirstAccountLogin(accId)) {
            Set<Integer> accWorlds = new HashSet<>();

            lgnRLock.lock();
            try {
                for (Integer chrid : getAccountCharacterEntries(accId)) {
                    accWorlds.add(worldChars.get(chrid));
                }
            } finally {
                lgnRLock.unlock();
            }

            int gmLevel = 0;
            for (Integer aw : accWorlds) {
                World wserv = this.getWorld(aw);

                if (wserv != null) {
                    for (Character chr : wserv.getAllCharactersView()) {
                        if (gmLevel < chr.gmLevel()) {
                            gmLevel = chr.gmLevel();
                        }
                    }
                }
            }

            c.setGMLevel(gmLevel);
            return;
        }

        int gmLevel = loadAccountCharactersView(c.getAccID(), 0, 0);
        c.setGMLevel(gmLevel);
    }

    /**
     * 从数据库加载账号角色视图，返回最高GM等级
     * @param accId 账号ID
     * @param gmLevel 初始GM等级
     * @param fromWorldid 起始世界ID
     * @return 账号下角色的最高GM等级
     */
    // returns the maximum gmLevel found
    private int loadAccountCharactersView(Integer accId, int gmLevel, int fromWorldid) {
        List<World> wlist = this.getWorlds();
        Pair<Short, List<List<Character>>> accCharacters = loadAccountCharactersViewFromDb(accId, wlist.size());

        lgnWLock.lock();
        try {
            List<List<Character>> accChars = accCharacters.getRight();
            accountCharacterCount.put(accId, accCharacters.getLeft());

            Set<Integer> chars = accountChars.get(accId);
            if (chars == null) {
                chars = new HashSet<>(5);
            }

            for (int wid = fromWorldid; wid < wlist.size(); wid++) {
                World w = wlist.get(wid);
                List<Character> wchars = accChars.get(wid);
                w.loadAccountCharactersView(accId, wchars);

                for (Character chr : wchars) {
                    int cid = chr.getId();
                    if (gmLevel < chr.gmLevel()) {
                        gmLevel = chr.gmLevel();
                    }

                    chars.add(cid);
                    worldChars.put(cid, wid);
                }
            }

            accountChars.put(accId, chars);
        } finally {
            lgnWLock.unlock();
        }

        return gmLevel;
    }

    /**
     * 加载账号在各世界的仓库数据
     * @param c 客户端连接
     */
    public void loadAccountStorages(Client c) {
        int accountId = c.getAccID();
        Set<Integer> accWorlds = new HashSet<>();
        lgnWLock.lock();
        try {
            Set<Integer> chars = accountChars.get(accountId);

            for (Integer cid : chars) {
                Integer worldid = worldChars.get(cid);
                if (worldid != null) {
                    accWorlds.add(worldid);
                }
            }
        } finally {
            lgnWLock.unlock();
        }

        List<World> worldList = this.getWorlds();
        for (Integer worldid : accWorlds) {
            if (worldid < worldList.size()) {
                World wserv = worldList.get(worldid);
                wserv.loadAccountStorage(accountId);
            }
        }
    }

    /**
     * 获取客户端的远程主机标识
     * @param client 客户端连接
     * @return 远程主机标识字符串
     */
    private static String getRemoteHost(Client client) {
        return SessionCoordinator.getSessionRemoteHost(client);
    }

    /**
     * 设置正在转频道中的角色ID
     * @param client 客户端连接
     * @param charId 角色ID
     */
    public void setCharacteridInTransition(Client client, int charId) {
        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            transitioningChars.put(remoteIp, charId);
        } finally {
            lgnWLock.unlock();
        }
    }

    /**
     * 验证转频道中的角色ID是否匹配
     * @param client 客户端连接
     * @param charId 角色ID
     * @return 是否验证通过
     */
    public boolean validateCharacteridInTransition(Client client, int charId) {
        if (!GameConfig.getServerBoolean("use_ip_validation")) {
            return true;
        }

        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            Integer cid = transitioningChars.remove(remoteIp);
            return cid != null && cid.equals(charId);
        } finally {
            lgnWLock.unlock();
        }
    }

    /**
     * 释放客户端的转频道状态
     * @param client 客户端连接
     * @return 被释放的角色ID，不存在则返回null
     */
    public Integer freeCharacteridInTransition(Client client) {
        if (!GameConfig.getServerBoolean("use_ip_validation")) {
            return null;
        }

        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            return transitioningChars.remove(remoteIp);
        } finally {
            lgnWLock.unlock();
        }
    }

    /**
     * 检查客户端是否存在正在转频道中的角色
     * @param client 客户端连接
     * @return 是否存在
     */
    public boolean hasCharacteridInTransition(Client client) {
        if (!GameConfig.getServerBoolean("use_ip_validation")) {
            return true;
        }

        String remoteIp = getRemoteHost(client);

        lgnRLock.lock();
        try {
            return transitioningChars.containsKey(remoteIp);
        } finally {
            lgnRLock.unlock();
        }
    }

    /**
     * 注册客户端的登录状态（设置10分钟超时）
     * @param c 客户端连接
     */
    public void registerLoginState(Client c) {
        srvLock.lock();
        try {
            inLoginState.put(c, System.currentTimeMillis() + 600000);
        } finally {
            srvLock.unlock();
        }
    }

    /**
     * 注销客户端的登录状态
     * @param c 客户端连接
     */
    public void unregisterLoginState(Client c) {
        srvLock.lock();
        try {
            inLoginState.remove(c);
        } finally {
            srvLock.unlock();
        }
    }

    /**
     * 断开所有登录超时的空闲客户端
     */
    private void disconnectIdlesOnLoginState() {
        List<Client> toDisconnect = new LinkedList<>();

        srvLock.lock();
        try {
            long timeNow = System.currentTimeMillis();

            for (Entry<Client, Long> mc : inLoginState.entrySet()) {
                if (timeNow > mc.getValue()) {
                    toDisconnect.add(mc.getKey());
                }
            }

            for (Client c : toDisconnect) {
                inLoginState.remove(c);
            }
        } finally {
            srvLock.unlock();
        }

        // thanks Lei for pointing a deadlock issue with srvLock
        for (Client c : toDisconnect) {
            if (c.isLoggedIn()) {
                c.disconnect(false, false);
            } else {
                SessionCoordinator.getInstance().closeSession(c, true);
            }
        }
    }

    /**
     * 注册登录空闲断开任务（每5分钟执行一次）
     */
    private void disconnectIdlesOnLoginTask() {
        TimerManager.getInstance().register(this::disconnectIdlesOnLoginState, 300000);
    }

    /**
     * 创建关服任务（返回Runnable）
     * @param restart 是否在关服后重启
     * @return 关服Runnable任务
     */
    //no player should be online when trying to shutdown!
    public final Runnable shutdown(final boolean restart) {
        return () -> shutdownInternal(restart);
    }

    /**
     * 执行服务器关闭的内部逻辑
     * 关闭所有世界、频道，停止定时器，可选重启服务器
     * @param restart 是否重启
     */
    public synchronized void shutdownInternal(boolean restart) {
        log.info(I18nUtil.getLogMessage("Server.shutdownInternal.info1"), restart ?
                I18nUtil.getLogMessage("Server.shutdownInternal.info2") : I18nUtil.getLogMessage("Server.shutdownInternal.info3"));
        if (getWorlds() == null) {
            //already shutdown
            return;
        }
        for (World w : getWorlds()) {
            w.shutdown();
        }

        hpMpAlertService.saveAll();
        hpMpAlertService.clear();

        for (Channel ch : getAllChannels()) {
            while (!ch.finishedShutdown()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    log.error(I18nUtil.getLogMessage("Server.shutdownInternal.error1"), ie);
                }
            }
        }

        resetServerWorlds();

        ThreadManager.getInstance().stop();
        TimerManager.getInstance().purge();
        TimerManager.getInstance().stop();
        loginServer.stop();
        online = false;
        log.info(I18nUtil.getLogMessage("Server.shutdownInternal.info4"));
        if (restart) {
            log.info(I18nUtil.getLogMessage("Server.shutdownInternal.info5"));
            instance = null;
            getInstance().init();
        }
    }

    /**
     * 检查是否到达下一次随机事件时间
     * 首次调用会随机设置1-4天后的时间点，后续到期后重新随机
     * @return 是否到达时间点
     */
    public boolean isNextTime() {
        if (nextTime == 0) {
            Random random = new Random();
            int base = 1;
            int ran = random.nextInt(4);
            nextTime = System.currentTimeMillis() + 86400000 * (base + ran);
            return false;
        }
        if (nextTime > System.currentTimeMillis()) {
            return false;
        }
        Random random = new Random();
        int base = 1;
        int ran = random.nextInt(4);
        nextTime = System.currentTimeMillis() + 86400000 * (base + ran);
        return true;
    }

    /**
     * 带消息提示的关服
     * 可设置倒计时时间、屏幕中央消息、滚动消息和聊天消息
     * @param serverShutdownDTO 关服配置DTO
     */
    public synchronized void shutdownWithMsgAndInternal(ServerShutdownDTO serverShutdownDTO) {

        int time = 60000;
        // 原来就支持立即停止，不能忽视本地用户
        if (serverShutdownDTO.getMinutes() >= 0) {
            time *= serverShutdownDTO.getMinutes();
        }

        if (time > 1) {
            int seconds = (time / (int) SECONDS.toMillis(1)) % 60;
            int minutes = (time / (int) MINUTES.toMillis(1)) % 60;
            int hours = (time / (int) HOURS.toMillis(1)) % 24;
            int days = (time / (int) DAYS.toMillis(1));

            String strTime = "";
            if (days > 0) {
                strTime += I18nUtil.getMessage("ShutdownCommand.message3", days);
            }
            if (hours > 0) {
                strTime += I18nUtil.getMessage("ShutdownCommand.message4", hours);
            }
            strTime += I18nUtil.getMessage("ShutdownCommand.message5", minutes);
            strTime += I18nUtil.getMessage("ShutdownCommand.message6", seconds);


            String shutDownMsg = I18nUtil.getMessage("ShutdownCommand.message7", strTime);

            if (serverShutdownDTO.getShutdownMsg() != null) {
                shutDownMsg = serverShutdownDTO.getShutdownMsg();
            }

            for (World w : Server.getInstance().getWorlds()) {
                for (Character chr : w.getPlayerStorage().getAllCharacters()) {
                    if (serverShutdownDTO.getShowCenterMsg()) {
                        // 屏幕中央提示消息 (火红玫瑰)
                        chr.startMapEffect(shutDownMsg, 5121009);
                    }
                }
                if (serverShutdownDTO.getShowServerMsg()) {
                    // 添加滚动消息到顶部，因为是固定时间停服，所以短暂的通知部分玩家可能看不到。
                    w.setServerMessage(shutDownMsg);
                }
                if (serverShutdownDTO.getShowChatMsg()) {
                    // 玩家聊天框蓝色GM消息
                    w.broadcastPacket(PacketCreator.serverNotice(6, shutDownMsg));
                }

            }
        }
        TimerManager.getInstance().schedule(Server.getInstance().shutdown(false), time);
    }


}