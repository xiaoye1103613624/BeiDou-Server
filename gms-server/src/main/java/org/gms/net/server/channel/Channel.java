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
package org.gms.net.server.channel;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.constants.id.MapId;
import org.gms.manager.ServerManager;
import org.gms.net.netty.ChannelServer;
import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.services.BaseService;
import org.gms.net.server.services.ServicesManager;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.World;
import org.gms.property.ServiceProperty;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventScriptManager;
import org.gms.server.TimerManager;
import org.gms.server.events.gm.Event;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionType;
import org.gms.server.maps.HiredMerchant;
import org.gms.server.maps.MapManager;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MiniDungeon;
import org.gms.server.maps.MiniDungeonInfo;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 频道
 * 管理频道内的玩家、地图、服务器消息和在线总时长等状态
 */
public final class Channel {
    private static final Logger log = LoggerFactory.getLogger(Channel.class);

    /** 基础端口号 */
    private static final int BASE_PORT = 7575;

    /** 端口号 */
    private final int port;
    /** IP地址 */
    private final String ip;
    /** 所属世界 */
    private final int world;
    /** 频道号 */
    private final int channel;

    /** 玩家存储 */
    private PlayerStorage players = new PlayerStorage();
    /** 频道Netty服务器 */
    private ChannelServer channelServer;
    /** 服务器消息 */
    private String serverMessage;
    /** 地图管理器 */
    private MapManager mapManager;
    /** 事件脚本管理器 */
    private EventScriptManager eventSM;
    /** 服务管理器 */
    private ServicesManager services;
    /** 雇佣商人映射 */
    private final Map<Integer, HiredMerchant> hiredMerchants = new HashMap<>();
    /** 存储变量映射 */
    private final Map<Integer, Integer> storedVars = new HashMap<>();
    /** 离开频道的玩家集合（CS或MTS中） */
    private final Set<Integer> playersAway = new HashSet<>();
    /** 远征队映射 */
    private final Map<ExpeditionType, Expedition> expeditions = new HashMap<>();
    /** 副本映射 */
    private final Map<Integer, MiniDungeon> dungeons = new HashMap<>();
    /** 远征队类型列表 */
    private final List<ExpeditionType> expedType = new ArrayList<>();
    /** 被占用的地图集合 */
    private final Set<MapleMap> ownedMaps = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    /** GM活动 */
    private Event event;
    /** 是否已完成关闭 */
    private boolean finishedShutdown = false;
    /** 已使用的怪物嘉年华房间 */
    private final Set<Integer> usedMC = new HashSet<>();

    /** 已使用的道场槽位标志 */
    private int usedDojo = 0;
    /** 道场当前阶段 */
    private int[] dojoStage;
    /** 道场完成时间 */
    private long[] dojoFinishTime;
    /** 道场定时任务 */
    private ScheduledFuture<?>[] dojoTask;
    /** 道场队伍映射 */
    private final Map<Integer, Integer> dojoParty = new HashMap<>();

    /** 小教堂预约队列 */
    private final List<Integer> chapelReservationQueue = new LinkedList<>();
    /** 大教堂预约队列 */
    private final List<Integer> cathedralReservationQueue = new LinkedList<>();
    /** 小教堂预约定时任务 */
    private ScheduledFuture<?> chapelReservationTask;
    /** 大教堂预约定时任务 */
    private ScheduledFuture<?> cathedralReservationTask;

    /** 正在进行的教堂婚礼ID */
    private Integer ongoingChapel = null;
    /** 正在进行的教堂婚礼类型 */
    private Boolean ongoingChapelType = null;
    /** 正在进行的教堂婚礼嘉宾 */
    private Set<Integer> ongoingChapelGuests = null;
    /** 正在进行的大教堂婚礼ID */
    private Integer ongoingCathedral = null;
    /** 正在进行的大教堂婚礼类型 */
    private Boolean ongoingCathedralType = null;
    /** 正在进行的大教堂婚礼嘉宾 */
    private Set<Integer> ongoingCathedralGuests = null;
    /** 婚礼开始时间 */
    private long ongoingStartTime;

    /** 通用线程锁 */
    private final Lock lock = new ReentrantLock(true);;
    /** 商人读锁 */
    private final Lock merchRlock;
    /** 商人写锁 */
    private final Lock merchWlock;
    /** 服务属性 */
    private static final ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);

    /**
     * 构造频道
     *
     * @param world     世界ID
     * @param channel   频道号
     * @param startTime 启动时间
     */
    public Channel(final int world, final int channel, long startTime) {
        this.world = world;
        this.channel = channel;

        // rude approach to a world's last channel boot time, placeholder for the 1st wedding reservation ever
        this.ongoingStartTime = startTime + 10000;
        this.mapManager = new MapManager(null, world, channel);
        this.port = BASE_PORT + (this.channel - 1) + (world * 100);
        this.ip = serviceProperty.getWanHost() + ":" + port;

        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
        this.merchRlock = rwLock.readLock();
        this.merchWlock = rwLock.writeLock();

        try {
            this.channelServer = initServer(port, world, channel);
            expedType.addAll(Arrays.asList(ExpeditionType.values()));

            // postpone event loading to improve boot time... thanks Riizade, daronhudson for noticing slow startup times
            if (Server.getInstance().isOnline()) {
                eventSM = new EventScriptManager(this, getEvents());
                eventSM.init();
            } else {
                String[] ev = {"0_EXAMPLE"};
                eventSM = new EventScriptManager(this, ev);
            }

            dojoStage = new int[20];
            dojoFinishTime = new long[20];
            dojoTask = new ScheduledFuture<?>[20];
            for (int i = 0; i < 20; i++) {
                dojoStage[i] = 0;
                dojoFinishTime[i] = 0;
                dojoTask[i] = null;
            }

            services = new ServicesManager(ChannelServices.OVERALL);

            log.info(I18nUtil.getLogMessage("Channel.info1"), getId(), port);
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("Channel.error1"), e);
        }
    }

    /**
     * 初始化频道Netty服务器
     *
     * @param port    端口
     * @param world   世界ID
     * @param channel 频道号
     * @return 频道服务器实例
     */
    private ChannelServer initServer(int port, int world, int channel) {
        ChannelServer channelServer = new ChannelServer(port, world, channel);
        channelServer.start();
        return channelServer;
    }

    /**
     * 重新加载事件脚本管理器
     */
    public synchronized void reloadEventScriptManager() {
        if (finishedShutdown) {
            return;
        }

        eventSM.cancel();
        eventSM = null;
        eventSM = new EventScriptManager(this, getEvents());
    }

    /**
     * 关闭频道
     */
    public synchronized void shutdown() {
        try {
            if (finishedShutdown) {
                return;
            }

            log.info(I18nUtil.getLogMessage("Channel.shutdown.info1"), world, channel);

            closeAllMerchants();
            disconnectAwayPlayers();
            players.disconnectAll();

            eventSM.dispose();
            eventSM = null;

            mapManager.dispose();
            mapManager = null;

            closeChannelSchedules();
            players = null;

            channelServer.stop();

            finishedShutdown = true;
            log.info(I18nUtil.getLogMessage("Channel.shutdown.info2"), world, channel);
        } catch (Exception e) {
            log.info(I18nUtil.getLogMessage("Channel.shutdown.error1"), world, channel, e.getMessage(), e);
        }
    }

    /**
     * 关闭频道服务
     */
    private void closeChannelServices() {
        services.shutdown();
    }

    /**
     * 关闭频道定时任务
     */
    private void closeChannelSchedules() {
        lock.lock();
        try {
            for (int i = 0; i < dojoTask.length; i++) {
                if (dojoTask[i] != null) {
                    dojoTask[i].cancel(false);
                    dojoTask[i] = null;
                }
            }
        } finally {
            lock.unlock();
        }

        closeChannelServices();
    }

    /**
     * 关闭所有雇佣商人
     */
    private void closeAllMerchants() {
        try {
            List<HiredMerchant> merchs;

            merchWlock.lock();
            try {
                merchs = new ArrayList<>(hiredMerchants.values());
                hiredMerchants.clear();
            } finally {
                merchWlock.unlock();
            }

            for (HiredMerchant merch : merchs) {
                merch.forceClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取地图工厂
     *
     * @return 地图管理器
     */
    public MapManager getMapFactory() {
        return mapManager;
    }

    /**
     * 获取频道服务
     *
     * @param sv 服务类型
     * @return 服务实例
     */
    public BaseService getServiceAccess(ChannelServices sv) {
        return services.getAccess(sv).getService();
    }

    /**
     * 获取世界ID
     *
     * @return 世界ID
     */
    public int getWorld() {
        return world;
    }

    /**
     * 获取世界服务器
     *
     * @return 世界对象
     */
    public World getWorldServer() {
        return Server.getInstance().getWorld(world);
    }

    /**
     * 添加玩家到频道
     *
     * @param chr 角色对象
     */
    public void addPlayer(Character chr) {
        players.addPlayer(chr);
        chr.sendPacket(PacketCreator.serverMessage(serverMessage));
    }

    /**
     * 获取服务器消息
     *
     * @return 服务器消息
     */
    public String getServerMessage() {
        return serverMessage;
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
     * 移除玩家
     *
     * @param chr 角色对象
     * @return 是否移除成功
     */
    public boolean removePlayer(Character chr) {
        return players.removePlayer(chr.getId()) != null;
    }

    /**
     * 获取频道容量（百分比）
     *
     * @return 容量百分比
     */
    public int getChannelCapacity() {
        return (int) (Math.ceil(((float) players.getAllCharacters().size() / GameConfig.getServerInt("channel_capacity")) * 800));
    }

    /**
     * 广播数据包到频道内所有玩家
     *
     * @param packet 数据包
     */
    public void broadcastPacket(Packet packet) {
        for (Character chr : players.getAllCharacters()) {
            chr.sendPacket(packet);
        }
    }

    /**
     * 获取频道ID
     *
     * @return 频道号
     */
    public final int getId() {
        return channel;
    }

    /**
     * 获取IP地址
     *
     * @return IP地址
     */
    public String getIP() {
        return ip;
    }

    /**
     * 获取GM活动
     *
     * @return 活动对象
     */
    public Event getEvent() {
        return event;
    }

    /**
     * 设置GM活动
     *
     * @param event 活动对象
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * 获取事件脚本管理器
     *
     * @return 事件脚本管理器
     */
    public EventScriptManager getEventSM() {
        return eventSM;
    }

    /**
     * 广播GM数据包到频道内所有GM玩家
     *
     * @param packet 数据包
     */
    public void broadcastGMPacket(Packet packet) {
        for (Character chr : players.getAllCharacters()) {
            if (chr.isGM()) {
                chr.sendPacket(packet);
            }
        }
    }

    /**
     * 获取队伍中在本频道的成员
     *
     * @param party 队伍
     * @return 成员角色列表
     */
    public List<Character> getPartyMembers(Party party) {
        List<Character> partym = new ArrayList<>(8);
        for (PartyCharacter partychar : party.getMembers()) {
            if (partychar.getChannel() == getId()) {
                Character chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    partym.add(chr);
                }
            }
        }
        return partym;
    }

    /**
     * 将玩家标记为离开频道状态（CS或MTS中）
     *
     * @param chrId 角色ID
     */
    public void insertPlayerAway(int chrId) {
        playersAway.add(chrId);
    }

    /**
     * 移除玩家离开状态
     *
     * @param chrId 角色ID
     */
    public void removePlayerAway(int chrId) {
        playersAway.remove(chrId);
    }

    /**
     * 频道是否可以卸载
     *
     * @return 是否可卸载
     */
    public boolean canUninstall() {
        return players.getSize() == 0 && playersAway.isEmpty();
    }

    /**
     * 断开所有离开频道玩家的连接
     */
    private void disconnectAwayPlayers() {
        World wserv = getWorldServer();
        for (Integer cid : playersAway) {
            Character chr = wserv.getPlayerStorage().getCharacterById(cid);
            if (chr != null && chr.isLoggedIn()) {
                chr.getClient().forceDisconnect();
            }
        }
    }

    /**
     * 获取雇佣商人映射
     *
     * @return 不可修改的雇佣商人映射
     */
    public Map<Integer, HiredMerchant> getHiredMerchants() {
        merchRlock.lock();
        try {
            return Collections.unmodifiableMap(hiredMerchants);
        } finally {
            merchRlock.unlock();
        }
    }

    /**
     * 添加雇佣商人
     *
     * @param chrid 角色ID
     * @param hm    雇佣商人
     */
    public void addHiredMerchant(int chrid, HiredMerchant hm) {
        merchWlock.lock();
        try {
            hiredMerchants.put(chrid, hm);
        } finally {
            merchWlock.unlock();
        }
    }

    /**
     * 移除雇佣商人
     *
     * @param chrid 角色ID
     */
    public void removeHiredMerchant(int chrid) {
        merchWlock.lock();
        try {
            hiredMerchants.remove(chrid);
        } finally {
            merchWlock.unlock();
        }
    }

    /**
     * 多头像查找好友
     *
     * @param charIdFrom    发起查找的角色ID
     * @param characterIds  目标角色ID数组
     * @return 在线且可见的角色ID数组
     */
    public int[] multiBuddyFind(int charIdFrom, int[] characterIds) {
        List<Integer> ret = new ArrayList<>(characterIds.length);
        PlayerStorage playerStorage = getPlayerStorage();
        for (int characterId : characterIds) {
            Character chr = playerStorage.getCharacterById(characterId);
            if (chr != null) {
                if (chr.getBuddylist().containsVisible(charIdFrom)) {
                    ret.add(characterId);
                }
            }
        }
        int[] retArr = new int[ret.size()];
        int pos = 0;
        for (Integer i : ret) {
            retArr[pos++] = i;
        }
        return retArr;
    }

    /**
     * 添加远征队
     *
     * @param exped 远征队
     * @return 是否成功添加
     */
    public boolean addExpedition(Expedition exped) {
        synchronized (expeditions) {
            if (expeditions.containsKey(exped.getType())) {
                return false;
            }

            expeditions.put(exped.getType(), exped);
            // thanks Conrad for noticing leader still receiving packets on failure-to-register cases
            exped.beginRegistration();
            return true;
        }
    }

    /**
     * 移除远征队
     *
     * @param exped 远征队
     */
    public void removeExpedition(Expedition exped) {
        synchronized (expeditions) {
            expeditions.remove(exped.getType());
        }
    }

    /**
     * 获取探险队
     *
     * @param type 远征队类型
     * @return 远征队对象
     */
    public Expedition getExpedition(ExpeditionType type) {
        return expeditions.get(type);
    }

    /**
     * 获取所有远征队
     *
     * @return 远征队列表
     */
    public List<Expedition> getExpeditions() {
        synchronized (expeditions) {
            return new ArrayList<>(expeditions.values());
        }
    }

    /**
     * 角色是否在本频道连接
     *
     * @param name 角色名称
     * @return 是否连接
     */
    public boolean isConnected(String name) {
        return getPlayerStorage().getCharacterByName(name) != null;
    }

    /**
     * 频道是否活动
     *
     * @return 是否活动
     */
    public boolean isActive() {
        EventScriptManager esm = this.getEventSM();
        return esm != null && esm.isActive();
    }

    /**
     * 频道是否已关闭
     *
     * @return 是否已关闭
     */
    public boolean finishedShutdown() {
        return finishedShutdown;
    }

    /**
     * 设置服务器消息
     *
     * @param message 消息内容
     */
    public void setServerMessage(String message) {
        this.serverMessage = message;
        broadcastPacket(PacketCreator.serverMessage(message));
        getWorldServer().resetDisabledServerMessages();
    }

    /**
     * 获取事件脚本列表
     *
     * @return 事件脚本文件名数组
     */
    private static String[] getEvents() {
        // 优先取语言文件夹，没有则取scripts
        String scriptName = "scripts";
        String eventPath = "event";
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        String scriptLangName = scriptName + "-" + serviceProperty.getLanguage();

        Path scriptPath = Path.of(scriptName, eventPath);
        Path scriptLangPath = Path.of(scriptLangName, eventPath);
        Path actualPath = Files.exists(scriptLangPath) ? scriptLangPath : scriptPath;

        List<String> events = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(actualPath)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                events.add(fileName.substring(0, fileName.length() - 3));
            }
        } catch (IOException e) {
            log.warn("Unable to load events !");
            e.printStackTrace();
        }
        return events.toArray(new String[0]);
    }

    /**
     * 获取存储变量
     *
     * @param key 键
     * @return 值
     */
    public int getStoredVar(int key) {
        if (storedVars.containsKey(key)) {
            return storedVars.get(key);
        }

        return 0;
    }

    /**
     * 设置存储变量
     *
     * @param key 键
     * @param val 值
     */
    public void setStoredVar(int key, int val) {
        this.storedVars.put(key, val);
    }

    /**
     * 查找队伍道场
     *
     * @param party 队伍
     * @return 道场槽位，-1表示未找到
     */
    public int lookupPartyDojo(Party party) {
        if (party == null) {
            return -1;
        }

        Integer i = dojoParty.get(party.hashCode());
        return (i != null) ? i : -1;
    }

    /**
     * 进入道场
     *
     * @param isPartyDojo 是否队伍道场
     * @param fromStage   起始阶段
     * @return 槽位，-1表示已满
     */
    public int ingressDojo(boolean isPartyDojo, int fromStage) {
        return ingressDojo(isPartyDojo, null, fromStage);
    }

    /**
     * 进入道场
     *
     * @param isPartyDojo 是否队伍道场
     * @param party       队伍
     * @param fromStage   起始阶段
     * @return 槽位，-1表示已满，-2表示队伍已在道场中
     */
    public int ingressDojo(boolean isPartyDojo, Party party, int fromStage) {
        lock.lock();
        try {
            int dojoList = this.usedDojo;
            int range, slot = 0;

            if (!isPartyDojo) {
                dojoList = dojoList >> 5;
                range = 15;
            } else {
                range = 5;
            }

            while ((dojoList & 1) != 0) {
                dojoList = (dojoList >> 1);
                slot++;
            }

            if (slot < range) {
                int slotMapid = (isPartyDojo ? MapId.DOJO_PARTY_BASE : MapId.DOJO_SOLO_BASE) + (100 * (fromStage + 1)) + slot;
                int dojoSlot = getDojoSlot(slotMapid);

                if (party != null) {
                    if (dojoParty.containsKey(party.hashCode())) {
                        return -2;
                    }
                    dojoParty.put(party.hashCode(), dojoSlot);
                }

                this.usedDojo |= (1 << dojoSlot);

                this.resetDojo(slotMapid);
                this.startDojoSchedule(slotMapid);
                return slot;
            } else {
                return -1;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 释放道场槽位
     *
     * @param slot  槽位
     * @param party 队伍
     */
    private void freeDojoSlot(int slot, Party party) {
        int mask = 0b11111111111111111111;
        mask ^= (1 << slot);

        lock.lock();
        try {
            usedDojo &= mask;
        } finally {
            lock.unlock();
        }

        if (party != null) {
            if (dojoParty.remove(party.hashCode()) != null) {
                return;
            }
        }

        // strange case, no party there!
        if (dojoParty.containsValue(slot)) {
            Set<Entry<Integer, Integer>> es = new HashSet<>(dojoParty.entrySet());

            for (Entry<Integer, Integer> e : es) {
                if (e.getValue() == slot) {
                    dojoParty.remove(e.getKey());
                    break;
                }
            }
        }
    }

    /**
     * 获取道场槽位编号
     *
     * @param dojoMapId 道场地图ID
     * @return 槽位编号
     */
    private static int getDojoSlot(int dojoMapId) {
        return (dojoMapId % 100) + ((dojoMapId / 10000 == 92502) ? 5 : 0);
    }

    /**
     * 重置道场地图
     *
     * @param fromMapId 起始地图ID
     */
    public void resetDojoMap(int fromMapId) {
        for (int i = 0; i < (((fromMapId / 100) % 100 <= 36) ? 5 : 2); i++) {
            this.getMapFactory().getMap(fromMapId + (100 * i)).resetMapObjects();
        }
    }

    /**
     * 重置道场
     *
     * @param dojoMapId 道场地图ID
     */
    public void resetDojo(int dojoMapId) {
        resetDojo(dojoMapId, -1);
    }

    /**
     * 重置道场阶段
     *
     * @param dojoMapId 道场地图ID
     * @param thisStg   阶段
     */
    private void resetDojo(int dojoMapId, int thisStg) {
        int slot = getDojoSlot(dojoMapId);
        this.dojoStage[slot] = thisStg;
    }

    /**
     * 如果道场区域为空则释放
     *
     * @param dojoMapId 道场地图ID
     */
    public void freeDojoSectionIfEmpty(int dojoMapId) {
        final int slot = getDojoSlot(dojoMapId);
        final int delta = (dojoMapId) % 100;
        final int stage = (dojoMapId / 100) % 100;
        final int dojoBaseMap = (dojoMapId >= MapId.DOJO_PARTY_BASE) ? MapId.DOJO_PARTY_BASE : MapId.DOJO_SOLO_BASE;

        // only 32 stages, but 38 maps
        for (int i = 0; i < 5; i++) {
            if (stage + i > 38) {
                break;
            }
            MapleMap dojoMap = getMapFactory().getMap(dojoBaseMap + (100 * (stage + i)) + delta);
            if (!dojoMap.getAllPlayers().isEmpty()) {
                return;
            }
        }

        freeDojoSlot(slot, null);
    }

    /**
     * 启动道场定时任务
     *
     * @param dojoMapId 道场地图ID
     */
    private void startDojoSchedule(final int dojoMapId) {
        final int slot = getDojoSlot(dojoMapId);
        final int stage = (dojoMapId / 100) % 100;
        if (stage <= dojoStage[slot]) {
            return;
        }

        long clockTime = (stage > 36 ? 15 : (stage / 6) + 5) * 60000;

        lock.lock();
        try {
            if (this.dojoTask[slot] != null) {
                this.dojoTask[slot].cancel(false);
            }
            this.dojoTask[slot] = TimerManager.getInstance().schedule(() -> {
                final int delta = (dojoMapId) % 100;
                final int dojoBaseMap = (slot < 5) ? MapId.DOJO_PARTY_BASE : MapId.DOJO_SOLO_BASE;
                Party party = null;

                // only 32 stages, but 38 maps
                for (int i = 0; i < 5; i++) {
                    if (stage + i > 38) {
                        break;
                    }

                    MapleMap dojoExit = getMapFactory().getMap(MapId.DOJO_EXIT);
                    for (Character chr : getMapFactory().getMap(dojoBaseMap + (100 * (stage + i)) + delta).getAllPlayers()) {
                        if (MapId.isDojo(chr.getMap().getId())) {
                            chr.changeMap(dojoExit);
                        }
                        party = chr.getParty();
                    }
                }

                freeDojoSlot(slot, party);
            // let the TIMES UP display for 3 seconds, then warp
            }, clockTime + 3000);
        } finally {
            lock.unlock();
        }

        dojoFinishTime[slot] = Server.getInstance().getCurrentTime() + clockTime;
    }

    /**
     * 取消道场定时任务
     *
     * @param dojoMapId 道场地图ID
     * @param party     队伍
     */
    public void dismissDojoSchedule(int dojoMapId, Party party) {
        int slot = getDojoSlot(dojoMapId);
        int stage = (dojoMapId / 100) % 100;
        if (stage <= dojoStage[slot]) {
            return;
        }

        lock.lock();
        try {
            if (this.dojoTask[slot] != null) {
                this.dojoTask[slot].cancel(false);
                this.dojoTask[slot] = null;
            }
        } finally {
            lock.unlock();
        }

        freeDojoSlot(slot, party);
    }

    /**
     * 设置道场进度
     *
     * @param dojoMapId 道场地图ID
     * @return 是否推进
     */
    public boolean setDojoProgress(int dojoMapId) {
        int slot = getDojoSlot(dojoMapId);
        int dojoStg = (dojoMapId / 100) % 100;

        if (this.dojoStage[slot] < dojoStg) {
            this.dojoStage[slot] = dojoStg;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取道场完成时间
     *
     * @param dojoMapId 道场地图ID
     * @return 完成时间
     */
    public long getDojoFinishTime(int dojoMapId) {
        return dojoFinishTime[getDojoSlot(dojoMapId)];
    }

    /**
     * 添加迷你副本
     *
     * @param dungeonid 副本ID
     * @return 是否添加成功
     */
    public boolean addMiniDungeon(int dungeonid) {
        lock.lock();
        try {
            if (dungeons.containsKey(dungeonid)) {
                return false;
            }

            MiniDungeonInfo mmdi = MiniDungeonInfo.getDungeon(dungeonid);
            // thanks Conrad for noticing hardcoded time limit for minidungeons
            MiniDungeon mmd = new MiniDungeon(mmdi.getBase(), this.getMapFactory().getMap(mmdi.getDungeonId()).getTimeLimit());

            dungeons.put(dungeonid, mmd);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取迷你副本
     *
     * @param dungeonid 副本ID
     * @return 迷你副本
     */
    public MiniDungeon getMiniDungeon(int dungeonid) {
        lock.lock();
        try {
            return dungeons.get(dungeonid);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除迷你副本
     *
     * @param dungeonid 副本ID
     */
    public void removeMiniDungeon(int dungeonid) {
        lock.lock();
        try {
            dungeons.remove(dungeonid);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取下一个婚礼预约
     *
     * @param cathedral 是否大教堂
     * @return 婚礼信息（类型、婚礼ID、嘉宾集合），无则返回null
     */
    public Pair<Boolean, Pair<Integer, Set<Integer>>> getNextWeddingReservation(boolean cathedral) {
        Integer ret;

        lock.lock();
        try {
            List<Integer> weddingReservationQueue = (cathedral ? cathedralReservationQueue : chapelReservationQueue);
            if (weddingReservationQueue.isEmpty()) {
                return null;
            }

            ret = weddingReservationQueue.remove(0);
            if (ret == null) {
                return null;
            }
        } finally {
            lock.unlock();
        }

        World wserv = getWorldServer();

        Pair<Integer, Integer> coupleId = wserv.getMarriageQueuedCouple(ret);
        Pair<Boolean, Set<Integer>> typeGuests = wserv.removeMarriageQueued(ret);

        Pair<String, String> couple = new Pair<>(Character.getNameById(coupleId.getLeft()), Character.getNameById(coupleId.getRight()));
        wserv.dropMessage(6, couple.getLeft() + " and " + couple.getRight() + "'s wedding is going to be started at " + (cathedral ? "Cathedral" : "Chapel") + " on Channel " + channel + ".");

        return new Pair<>(typeGuests.getLeft(), new Pair<>(ret, typeGuests.getRight()));
    }

    /**
     * 婚礼是否已预约
     *
     * @param weddingId 婚礼ID
     * @return 是否已预约
     */
    public boolean isWeddingReserved(Integer weddingId) {
        World wserv = getWorldServer();

        lock.lock();
        try {
            return wserv.isMarriageQueued(weddingId) || weddingId.equals(ongoingCathedral) || weddingId.equals(ongoingChapel);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取婚礼预约状态
     *
     * @param weddingId 婚礼ID
     * @param cathedral 是否大教堂
     * @return 状态（0进行中，正数队列位置，-1未找到）
     */
    public int getWeddingReservationStatus(Integer weddingId, boolean cathedral) {
        if (weddingId == null) {
            return -1;
        }

        lock.lock();
        try {
            if (cathedral) {
                if (weddingId.equals(ongoingCathedral)) {
                    return 0;
                }

                for (int i = 0; i < cathedralReservationQueue.size(); i++) {
                    if (weddingId.equals(cathedralReservationQueue.get(i))) {
                        return i + 1;
                    }
                }
            } else {
                if (weddingId.equals(ongoingChapel)) {
                    return 0;
                }

                for (int i = 0; i < chapelReservationQueue.size(); i++) {
                    if (weddingId.equals(chapelReservationQueue.get(i))) {
                        return i + 1;
                    }
                }
            }

            return -1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加婚礼预约
     *
     * @param weddingId 婚礼ID
     * @param cathedral 是否大教堂
     * @param premium   是否高级
     * @param groomId   新郎ID
     * @param brideId   新娘ID
     * @return 队列位置，-1表示失败
     */
    public int pushWeddingReservation(Integer weddingId, boolean cathedral, boolean premium, Integer groomId, Integer brideId) {
        if (weddingId == null || isWeddingReserved(weddingId)) {
            return -1;
        }

        World wserv = getWorldServer();
        wserv.putMarriageQueued(weddingId, cathedral, premium, groomId, brideId);

        lock.lock();
        try {
            List<Integer> weddingReservationQueue = (cathedral ? cathedralReservationQueue : chapelReservationQueue);

            int delay = GameConfig.getServerInt("wedding_reservation_delay") - 1 - weddingReservationQueue.size();
            // push empty slots to fill the waiting time
            for (int i = 0; i < delay; i++) {
                weddingReservationQueue.add(null);
            }

            weddingReservationQueue.add(weddingId);
            return weddingReservationQueue.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 是否在正在进行的婚礼嘉宾列表中
     *
     * @param cathedral 是否大教堂
     * @param playerId  玩家ID
     * @return 是否嘉宾
     */
    public boolean isOngoingWeddingGuest(boolean cathedral, int playerId) {
        lock.lock();
        try {
            if (cathedral) {
                return ongoingCathedralGuests != null && ongoingCathedralGuests.contains(playerId);
            } else {
                return ongoingChapelGuests != null && ongoingChapelGuests.contains(playerId);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取正在进行的婚礼ID
     *
     * @param cathedral 是否大教堂
     * @return 婚礼ID
     */
    public Integer getOngoingWedding(boolean cathedral) {
        lock.lock();
        try {
            return cathedral ? ongoingCathedral : ongoingChapel;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取正在进行的婚礼类型
     *
     * @param cathedral 是否大教堂
     * @return 是否高级婚礼
     */
    public boolean getOngoingWeddingType(boolean cathedral) {
        lock.lock();
        try {
            return cathedral ? ongoingCathedralType : ongoingChapelType;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭正在进行的婚礼
     *
     * @param cathedral 是否大教堂
     */
    public void closeOngoingWedding(boolean cathedral) {
        lock.lock();
        try {
            if (cathedral) {
                ongoingCathedral = null;
                ongoingCathedralType = null;
                ongoingCathedralGuests = null;
            } else {
                ongoingChapel = null;
                ongoingChapelType = null;
                ongoingChapelGuests = null;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置正在进行的婚礼
     *
     * @param cathedral 是否大教堂
     * @param premium   是否高级
     * @param weddingId 婚礼ID
     * @param guests    嘉宾集合
     */
    public void setOngoingWedding(final boolean cathedral, Boolean premium, Integer weddingId, Set<Integer> guests) {
        lock.lock();
        try {
            if (cathedral) {
                ongoingCathedral = weddingId;
                ongoingCathedralType = premium;
                ongoingCathedralGuests = guests;
            } else {
                ongoingChapel = weddingId;
                ongoingChapelType = premium;
                ongoingChapelGuests = guests;
            }
        } finally {
            lock.unlock();
        }

        ongoingStartTime = System.currentTimeMillis();
        if (weddingId != null) {
            ScheduledFuture<?> weddingTask = TimerManager.getInstance().schedule(() -> closeOngoingWedding(cathedral), MINUTES.toMillis(GameConfig.getServerLong("wedding_reservation_timeout")));

            if (cathedral) {
                cathedralReservationTask = weddingTask;
            } else {
                chapelReservationTask = weddingTask;
            }
        }
    }

    /**
     * 接受正在进行的婚礼（新人确认到场）
     *
     * @param cathedral 是否大教堂
     * @return 是否接受成功
     */
    public synchronized boolean acceptOngoingWedding(final boolean cathedral) {
        if (cathedral) {
            if (cathedralReservationTask == null) {
                return false;
            }

            cathedralReservationTask.cancel(false);
            cathedralReservationTask = null;
        } else {
            if (chapelReservationTask == null) {
                return false;
            }

            chapelReservationTask.cancel(false);
            chapelReservationTask = null;
        }

        return true;
    }

    /**
     * 获取剩余时间字符串
     *
     * @param futureTime 未来时间点
     * @return 剩余时间字符串（如"2 hours, 30 minutes, 15 seconds"），null表示已过期
     */
    private static String getTimeLeft(long futureTime) {
        StringBuilder str = new StringBuilder();
        long leftTime = futureTime - System.currentTimeMillis();

        if (leftTime < 0) {
            return null;
        }

        byte mode = 0;
        // counts minutes
        if (leftTime / (MINUTES.toMillis(1)) > 0) {
            mode++;
            // counts hours
            if (leftTime / (HOURS.toMillis(1)) > 0) {
                mode++;
            }
        }

        switch (mode) {
            case 2:
                int hours = (int) ((leftTime / (HOURS.toMillis(1))));
                str.append(hours + " hours, ");

            case 1:
                int minutes = (int) ((leftTime / (MINUTES.toMillis(1))) % 60);
                str.append(minutes + " minutes, ");

            default:
                int seconds = (int) (leftTime / SECONDS.toMillis(1)) % 60;
                str.append(seconds + " seconds");
        }

        return str.toString();
    }

    /**
     * 获取婚礼票过期时间
     *
     * @param resSlot 预约槽位
     * @return 过期时间
     */
    public long getWeddingTicketExpireTime(int resSlot) {
        return ongoingStartTime + getRelativeWeddingTicketExpireTime(resSlot);
    }

    /**
     * 获取相对婚礼票过期时间
     *
     * @param resSlot 预约槽位
     * @return 相对过期时间（毫秒）
     */
    public static long getRelativeWeddingTicketExpireTime(int resSlot) {
        return MINUTES.toMillis((long) resSlot * GameConfig.getServerLong("wedding_reservation_interval"));
    }

    /**
     * 获取婚礼预约剩余时间描述
     *
     * @param weddingId 婚礼ID
     * @return 预约时间描述，null表示未找到
     */
    public String getWeddingReservationTimeLeft(Integer weddingId) {
        if (weddingId == null) {
            return null;
        }

        lock.lock();
        try {
            boolean cathedral = true;

            int resStatus;
            resStatus = getWeddingReservationStatus(weddingId, true);
            if (resStatus < 0) {
                cathedral = false;
                resStatus = getWeddingReservationStatus(weddingId, false);

                if (resStatus < 0) {
                    return null;
                }
            }

            String venue = (cathedral ? "Cathedral" : "Chapel");
            if (resStatus == 0) {
                return venue + " - RIGHT NOW";
            }

            return venue + " - " + getTimeLeft(ongoingStartTime + MINUTES.toMillis((long) resStatus * GameConfig.getServerLong("wedding_reservation_interval"))) + " from now";
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取嘉宾对应的婚礼夫妻ID
     *
     * @param guestId   嘉宾ID
     * @param cathedral 是否大教堂
     * @return 夫妻ID对（新郎ID, 新娘ID）
     */
    public Pair<Integer, Integer> getWeddingCoupleForGuest(int guestId, boolean cathedral) {
        lock.lock();
        try {
            return (isOngoingWeddingGuest(cathedral, guestId)) ? getWorldServer().getRelationshipCouple(getOngoingWedding(cathedral)) : null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 向频道内所有玩家发送消息
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
     * 注册被占用的地图
     *
     * @param map 地图
     */
    public void registerOwnedMap(MapleMap map) {
        ownedMaps.add(map);
    }

    /**
     * 取消注册被占用的地图
     *
     * @param map 地图
     */
    public void unregisterOwnedMap(MapleMap map) {
        ownedMaps.remove(map);
    }

    /**
     * 运行检查地图占用定时任务
     */
    public void runCheckOwnedMapsSchedule() {
        if (!ownedMaps.isEmpty()) {
            List<MapleMap> ownedMapsList;

            synchronized (ownedMaps) {
                ownedMapsList = new ArrayList<>(ownedMaps);
            }

            for (MapleMap map : ownedMapsList) {
                map.checkMapOwnerActivity();
            }
        }
    }

    /**
     * 获取怪物嘉年华房间编号
     *
     * @param cpq1  是否CPQ1
     * @param field 场次
     * @return 房间编号
     */
    private static int getMonsterCarnivalRoom(boolean cpq1, int field) {
        return (cpq1 ? 0 : 100) + field;
    }

    /**
     * 初始化怪物嘉年华房间
     *
     * @param cpq1  是否CPQ1
     * @param field 场次
     */
    public void initMonsterCarnival(boolean cpq1, int field) {
        usedMC.add(getMonsterCarnivalRoom(cpq1, field));
    }

    /**
     * 完成怪物嘉年华房间
     *
     * @param cpq1  是否CPQ1
     * @param field 场次
     */
    public void finishMonsterCarnival(boolean cpq1, int field) {
        usedMC.remove(getMonsterCarnivalRoom(cpq1, field));
    }

    /**
     * 是否可以初始化怪物嘉年华房间
     *
     * @param cpq1  是否CPQ1
     * @param field 场次
     * @return 是否可用
     */
    public boolean canInitMonsterCarnival(boolean cpq1, int field) {
        return !usedMC.contains(getMonsterCarnivalRoom(cpq1, field));
    }

    /**
     * 调试婚礼状态
     */
    public void debugMarriageStatus() {
        log.debug(" ----- WORLD DATA -----");
        getWorldServer().debugMarriageStatus();

        log.debug(" ----- CH. {} -----", channel);
        log.debug(" ----- CATHEDRAL -----");
        log.debug("Current Queue: {}", cathedralReservationQueue);
        log.debug("Cancel Task?: {}", cathedralReservationTask != null);
        log.debug("Ongoing wid: {}", ongoingCathedral);
        log.debug("Ongoing wid: {}, isPremium: {}", ongoingCathedral, ongoingCathedralType);
        log.debug("Guest list: {}", ongoingCathedralGuests);
        log.debug(" ----- CHAPEL -----");
        log.debug("Current Queue: {}", chapelReservationQueue);
        log.debug("Cancel Task?: {}", chapelReservationTask != null);
        log.debug("Ongoing wid: {}", ongoingChapel);
        log.debug("Ongoing wid: {}, isPremium: {}", ongoingChapel, ongoingChapelType);
        log.debug("Guest list: {}", ongoingChapelGuests);
        log.debug("Starttime: {}", ongoingStartTime);
    }
}