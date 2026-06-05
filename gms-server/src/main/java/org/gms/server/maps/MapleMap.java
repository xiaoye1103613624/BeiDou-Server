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
package org.gms.server.maps;

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.autoban.AutobanFactory;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.status.MonsterStatus;
import org.gms.client.status.MonsterStatusEffect;
import org.gms.config.BossConfigManager;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.MapId;
import org.gms.constants.id.MobId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.world.MonsterAggroCoordinator;
import org.gms.net.server.services.task.channel.MobMistService;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.World;
import org.gms.util.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.map.MapScriptManager;
import org.gms.server.ItemInformationProvider;
import org.gms.server.StatEffect;
import org.gms.server.TimerManager;
import org.gms.server.events.gm.Coconut;
import org.gms.server.events.gm.Fitness;
import org.gms.server.events.gm.Ola;
import org.gms.server.events.gm.OxQuiz;
import org.gms.server.events.gm.Snowball;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.LifeFactory.selfDestruction;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterDropEntry;
import org.gms.server.life.MonsterGlobalDropEntry;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.life.MonsterListener;
import org.gms.server.life.NPC;
import org.gms.server.life.PlayerNPC;
import org.gms.server.life.SpawnPoint;
import org.gms.server.partyquest.CarnivalFactory;
import org.gms.server.partyquest.CarnivalFactory.MCSkill;
import org.gms.server.partyquest.GuardianSpawnPoint;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;
import org.gms.util.Randomizer;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 【类型】MapleMap（class），包 {@code org.gms.server.maps}。
 * 
 * <p>游戏地图的核心类，每个实例代表一个具体的地图（如"射手村"、"魔法密林"等），
 * 负责管理地图内的所有对象（玩家、怪物、NPC、掉落物、反应堆、传送门等）及其生命周期，
 * 处理怪物刷新、掉落物、传送、战斗伤害、BOSS战、事件（雪球、椰子、OX问答等）。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li><b>生命周期管理</b>：地图加载/卸载、怪物刷新调度、物品过期清理</li>
 *   <li><b>玩家管理</b>：玩家进入/离开、广播消息、队伍追踪</li>
 *   <li><b>怪物系统</b>：怪物生成/死亡/伤害、BOSS血条广播、仇恨追踪</li>
 *   <li><b>掉落系统</b>：物品掉落定位、拾取校验、掉落物过期清理</li>
 *   <li><b>事件系统</b>：GM事件（雪球、椰子、OX）、PQ、地图特效</li>
 *   <li><b>安全机制</b>：读写锁保护玩家/对象集合、伤害/传送校验</li>
 * </ul>
 * 
 * <p>设计特点：</p>
 * <ul>
 *   <li>线程安全：使用读写锁保护关键数据结构</li>
 *   <li>事件驱动：响应玩家动作、怪物死亡、物品拾取等事件</li>
 *   <li>定时任务：处理物品过期、怪物刷新、状态更新等周期性操作</li>
 *   <li>对象管理：统一管理地图上的各种对象类型</li>
 * </ul>
 * 
 * <p>每个地图绑定到特定的world + channel，由{@link org.gms.server.maps.MapManager}的MapFactory创建和管理。</p>
 * 
 * @author Matze
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class MapleMap {
    private static final Logger log = LoggerFactory.getLogger(MapleMap.class);
    /** 需要按范围同步给玩家的地图对象类型（商店、物品、NPC、怪物、门、召唤兽、反应堆） */
    private static final List<MapObjectType> rangedMapobjectTypes = Arrays.asList(MapObjectType.SHOP, MapObjectType.ITEM, MapObjectType.NPC, MapObjectType.MONSTER, MapObjectType.DOOR, MapObjectType.SUMMON, MapObjectType.REACTOR);
    /** 掉落物有效范围缓存：key=怪物ID, value=<最小X偏移, 最大X偏移> */
    private static final Map<Integer, Pair<Integer, Integer>> dropBoundsCache = new HashMap<>(100);

    // ==================== 地图对象容器 ====================
    /** 地图上所有对象（怪物、NPC、玩家、掉落物等），key=OID */
    private final Map<Integer, MapObject> mapobjects = new LinkedHashMap<>();
    /** 带有自毁倒计时的对象 OID 集合 */
    private final Set<Integer> selfDestructives = new LinkedHashSet<>();
    /** 当前活跃的怪物刷新点列表（副本等场景可动态增删） */
    private final Collection<SpawnPoint> monsterSpawn = Collections.synchronizedList(new LinkedList<>());
    /** 地图的所有怪物刷新点（不会动态变化，用于 reload） */
    private final Collection<SpawnPoint> allMonsterSpawn = Collections.synchronizedList(new LinkedList<>());
    /** 当前地图已生成的怪物计数（原子操作） */
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    /** 当前地图掉落物计数（原子操作） */
    private final AtomicInteger droppedItemCount = new AtomicInteger(0);
    /** 当前地图的玩家集合 */
    private final Collection<Character> characters = new LinkedHashSet<>();
    /** 地图内队伍映射：<mapId, <partyId>>，用于追踪同一地图的多队伍 */
    private final Map<Integer, Set<Integer>> mapParty = new LinkedHashMap<>();
    /** 地图传送门（key=传送门名称或ID） */
    private final Map<Integer, Portal> portals = new HashMap<>();
    /** 地图背景类型 */
    private final Map<Integer, Integer> backgroundTypes = new HashMap<>();
    /** 地图环境变量（如天气效果、BGM） */
    private final Map<String, Integer> environment = new LinkedHashMap<>();
    /** 当前地图的掉落物：<掉落物Item, 掉落时间戳> */
    private final Map<MapItem, Long> droppedItems = new LinkedHashMap<>();
    /** 已注册到全局掉落追踪的弱引用对象列表 */
    private final LinkedList<WeakReference<MapObject>> registeredDrops = new LinkedList<>();
    /** 怪物掉落锁——防止同一怪物被多次拾取，<MobLootEntry, 处理时间戳> */
    private final Map<MobLootEntry, Long> mobLootEntries = new HashMap(20);
    /** 角色状态更新任务列表 */
    private final List<Runnable> statUpdateRunnables = new ArrayList(50);
    /** 地图上的区域定义（用于事件判断） */
    private final List<Rectangle> areas = new ArrayList<>();

    // ==================== 地形数据 ====================
    /** 地图的 foothold 四叉树（用于寻路和掉落定位） */
    private FootholdTree footholds = null;
    /** 地图 X 轴玩家可到达的边界 [minX, maxX] */
    private Pair<Integer, Integer> xLimits;
    /** 地图矩形区域 */
    private final Rectangle mapArea = new Rectangle();

    // ==================== 基础属性 ====================
    /** 地图 ID（WZ 中的地图编号） */
    private final int mapid;
    /** 地图对象 ID 自增器（OID 从 1000000001 开始） */
    private final AtomicInteger runningOid = new AtomicInteger(1000000001);
    /** 从本图离开时的返回地图 ID */
    private final int returnMapId;
    /** 所属频道号 */
    private final int channel;
    /** 所属大区号 */
    private final int world;
    /** 地图座位数 */
    private int seats;
    /** 怪物刷新倍率 */
    private byte monsterRate;
    /** 是否显示时钟 */
    private boolean clock;
    /** 是否为船类地图 */
    private boolean boat;
    /** 船是否已靠岸 */
    private boolean docked = false;
    /** 绑定的事件实例 */
    private EventInstanceManager event = null;
    /** 地图名称 */
    private String mapName;
    /** 地图所属街道名称 */
    private String streetName;
    /** 地图特效 */
    private MapEffect mapEffect = null;
    /** 是否永久地图（不随玩家离开而卸载） */
    private boolean everlast = false;
    /** 强制返回地图 ID */
    private int forcedReturnMap = MapId.NONE;
    /** 地图时间限制（秒） */
    private int timeLimit;
    /** 地图计时器起始时间 */
    private long mapTimer;
    /** 每秒扣减值 */
    private int decHP = 0;
    /** HP/MP 恢复倍率 */
    private float recovery = 1.0f;
    /** 保护物品 ID（持有后可免疫地图伤害） */
    private int protectItem = 0;
    /** 是否为城镇地图 */
    private boolean town;
    /** OX 问答事件对象 */
    private OxQuiz ox;
    /** 是否为 OX 问答地图 */
    private boolean isOxQuiz = false;
    /** 是否允许掉落 */
    private boolean dropsOn = true;
    /** 第一个玩家进入时执行的脚本 */
    private String onFirstUserEnter;
    /** 每个玩家进入时执行的脚本 */
    private String onUserEnter;
    /** 地图类型（如 0=普通, 1=副本等） */
    private int fieldType;
    /** 地图限制标志位（跳跃限制、传送限制等） */
    private int fieldLimit = 0;
    /** 怪物容量上限（-1 无限制） */
    private int mobCapacity = -1;

    // ==================== 调度/服务 ====================
    /** 怪物仇恨追踪器 */
    private MonsterAggroCoordinator aggroMonitor = null;
    /** 物品监控定时任务 */
    private ScheduledFuture<?> itemMonitor = null;
    /** 过期物品清理定时任务 */
    private ScheduledFuture<?> expireItemsTask = null;
    /** 怪物刷新定时任务 */
    private ScheduledFuture<?> mobSpawnLootTask = null;
    /** 角色状态更新定时任务 */
    private ScheduledFuture<?> characterStatUpdateTask = null;
    /** 物品监控超时时间 */
    private short itemMonitorTimeout;
    /** 限时怪物：<怪物ID, 提示消息> */
    private Pair<Integer, String> timeMob = null;
    /** 怪物刷新间隔（毫秒） */
    private short mobInterval = 5000;
    /** 是否允许召唤兽 */
    private boolean allowSummons = true;
    /** 轮回石碑NPC（当前地图上的轮回石碑） */
    private NPC samsaraStoneNpc = null;
    /** 轮回石碑所有者 */
    private Character samsaraOwner = null;
    /** 轮回石碑过期时间戳 */
    private long samsaraExpireTime = 0;
    /** 轮回石碑自动移除定时器 */
    private ScheduledFuture<?> samsaraRemoveTask = null;
    /** 地图所有者（副本专用） */
    private Character mapOwner = null;
    /** 地图所有者最后活动时间 */
    private long mapOwnerLastActivityTime = Long.MAX_VALUE;

    // ==================== 事件状态 ====================
    /** 事件是否已开始 */
    private boolean eventstarted = false;
    /** 是否全局禁言 */
    private boolean isMuted = false;
    /** 雪球事件：队伍0的雪球 */
    private Snowball snowball0 = null;
    /** 雪球事件：队伍1的雪球 */
    private Snowball snowball1 = null;
    /** 椰子事件 */
    private Coconut coconut;

    // ==================== 怪物嘉年华(CPQ) ====================
    /** CPQ 最大怪物数 */
    private int maxMobs;
    /** CPQ 最大反应堆数 */
    private int maxReactors;
    /** CPQ 死亡扣分 */
    private int deathCP;
    /** CPQ 默认时间 */
    private int timeDefault;
    /** CPQ 扩展时间 */
    private int timeExpand;

    // ==================== 线程安全锁 ====================
    /** 角色集合读锁 */
    private final Lock chrRLock;
    /** 角色集合写锁 */
    private final Lock chrWLock;
    /** 地图对象集合读锁 */
    private final Lock objectRLock;
    /** 地图对象集合写锁 */
    private final Lock objectWLock;
    /** 掉落物操作锁 */
    private final Lock lootLock = new ReentrantLock(true);
    /** 掉落范围缓存锁 */
    private static final Lock bndLock = new ReentrantLock(true);

    /**
     * 构造函数：创建新的地图实例
     * 
     * <p>初始化一个新的MapleMap对象，设置地图的基本属性和同步锁。
     * 地图是游戏世界的基本单位，包含玩家、怪物、NPC等游戏对象。</p>
     * 
     * <p>主要初始化内容包括：</p>
     * <ul>
     *   <li>地图ID、世界ID、频道ID等标识信息</li>
     *   <li>返回地图ID（用于死亡后传送）</li>
     *   <li>怪物生成率（经过向上取整处理，确保至少为1）</li>
     *   <li>用于线程安全的读写锁（玩家和对象访问锁）</li>
     *   <li>怪物仇恨协调器（用于管理怪物攻击目标）</li>
     * </ul>
     * 
     * @param mapid 地图ID（唯一标识地图）
     * @param world 世界ID（标识所属世界）
     * @param channel 频道ID（标识所属频道）
     * @param returnMapId 返回地图ID（死亡或特定情况下的返回目的地）
     * @param monsterRate 怪物生成率（影响怪物刷新频率，0表示无怪物）
     */
    /**
     * 构造函数：创建地图实例
     * 
     * <p>初始化地图对象的基本属性，包括地图ID、世界、频道、返回地图ID和怪物刷新率，
     * 并设置读写锁以保证多线程环境下的数据安全。</p>
     * 
     * <p>此构造函数会：</p>
     * <ul>
     *   <li>初始化地图基本信息（ID、世界、频道等）</li>
     *   <li>设置怪物刷新率（最低为1）</li>
     *   <li>创建玩家集合的读写锁</li>
     *   <li>创建地图对象的读写锁</li>
     *   <li>初始化怪物仇恨协调器</li>
     * </ul>
     * 
     * @param mapid 地图ID
     * @param world 世界编号
     * @param channel 频道编号
     * @param returnMapId 返回地图ID（当玩家掉出地图边界时传送至此）
     * @param monsterRate 怪物刷新率（影响怪物刷新频率）
     */
    public MapleMap(int mapid, int world, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = channel;
        this.world = world;
        this.returnMapId = returnMapId;
        this.monsterRate = (byte) Math.ceil(monsterRate);
        if (this.monsterRate == 0) {
            this.monsterRate = 1;
        }

        final ReadWriteLock chrLock = new ReentrantReadWriteLock(true);
        chrRLock = chrLock.readLock();
        chrWLock = chrLock.writeLock();

        final ReadWriteLock objectLock = new ReentrantReadWriteLock(true);
        objectRLock = objectLock.readLock();
        objectWLock = objectLock.writeLock();

        aggroMonitor = new MonsterAggroCoordinator();
    }

    /**
     * 设置事件实例管理器
     *
     * @param eim 事件实例管理器
     */
    public void setEventInstance(EventInstanceManager eim) {
        event = eim;
    }

    /**
     * 获取事件实例管理器
     *
     * @return 事件实例管理器
     */
    public EventInstanceManager getEventInstance() {
        return event;
    }

    public Rectangle getMapArea() {
        return mapArea;
    }

    /**
     * 获取所属大区号
     *
     * @return 大区号
     */
    public int getWorld() {
        return world;
    }

    /**
     * 向地图上除源玩家外的所有玩家广播数据包
     *
     * @param source 源玩家（不会收到广播）
     * @param packet 要广播的数据包
     */
    public void broadcastPacket(Character source, Packet packet) {
        broadcastPacket(packet, chr -> chr != null && chr.getClient() != null && chr != source);
    }

    /**
     * 向地图上除源玩家外的GM玩家广播数据包（仅GM等级>=源玩家的GM能收到）
     *
     * @param source 源GM玩家
     * @param packet 要广播的数据包
     */
    public void broadcastGMPacket(Character source, Packet packet) {
        broadcastPacket(packet, chr -> chr != null && chr.getClient() != null && chr != source && chr.gmLevel() >= source.gmLevel());
    }

    /**
     * 向地图上满足条件的玩家广播数据包
     *
     * @param packet    要广播的数据包
     * @param chrFilter 玩家过滤条件
     */
    private void broadcastPacket(Packet packet, Predicate<Character> chrFilter) {
        chrRLock.lock();
        try {
            characters.stream()
                    .filter(chrFilter)
                    .forEach(chr -> chr.sendPacket(packet));
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 切换掉落开关状态
     */
    public void toggleDrops() {
        this.dropsOn = !dropsOn;
    }

    /**
     * 获取地图对象同步的范围距离（平方值）
     *
     * @return 距离平方值
     */
    private static double getRangedDistance() {
        return GameConfig.getServerBoolean("use_max_range") ? Double.POSITIVE_INFINITY : 722500;
    }

    /**
     * 获取指定矩形区域内的地图对象
     *
     * @param box   矩形区域
     * @param types 对象类型列表
     * @return 符合条件的地图对象列表
     */
    public List<MapObject> getMapObjectsInRect(Rectangle box, List<MapObjectType> types) {
        objectRLock.lock();
        final List<MapObject> ret = new LinkedList<>();
        try {
            for (MapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return ret;
    }

    /**
     * 获取地图ID
     *
     * @return 地图ID
     */
    public int getId() {
        return mapid;
    }

    /**
     * 获取所属频道服务器
     *
     * @return 频道服务器
     */
    public Channel getChannelServer() {
        return Server.getInstance().getWorld(world).getChannel(channel);
    }

    /**
     * 获取所属世界服务器
     *
     * @return 世界服务器
     */
    public World getWorldServer() {
        return Server.getInstance().getWorld(world);
    }

    /**
     * 获取返回地图
     *
     * @return 返回地图实例
     */
    public MapleMap getReturnMap() {
        if (returnMapId == MapId.NONE) {
            return this;
        }
        return getChannelServer().getMapFactory().getMap(returnMapId);
    }

    /**
     * 获取返回地图ID
     *
     * @return 返回地图ID
     */
    public int getReturnMapId() {
        return returnMapId;
    }

    /**
     * 获取强制返回地图
     *
     * @return 强制返回地图实例
     */
    public MapleMap getForcedReturnMap() {
        return getChannelServer().getMapFactory().getMap(forcedReturnMap);
    }

    /**
     * 获取强制返回地图ID
     *
     * @return 强制返回地图ID
     */
    public int getForcedReturnId() {
        return forcedReturnMap;
    }

    /**
     * 设置强制返回地图
     *
     * @param map 地图ID
     */
    public void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    /**
     * 获取地图时间限制（秒）
     *
     * @return 时间限制
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * 获取剩余时间（秒）
     *
     * @return 剩余时间
     */
    public int getTimeLeft() {
        return (int) ((mapTimer - System.currentTimeMillis()) / 1000);
    }

    /**
     * 设置所有反应堆状态为激活状态
     */
    public void setReactorState() {
        for (MapObject o : getMapObjects()) {
            if (o.getType() == MapObjectType.REACTOR) {
                if (((Reactor) o).getState() < 1) {
                    Reactor mr = (Reactor) o;
                    mr.lockReactor();
                    try {
                        mr.resetReactorActions(1);
                        broadcastMessage(PacketCreator.triggerReactor((Reactor) o, 1));
                    } finally {
                        mr.unlockReactor();
                    }
                }
            }
        }
    }

    /**
     * 限制指定ID的反应堆数量
     *
     * @param rid 反应堆ID
     * @param num 最大数量
     */
    public final void limitReactor(final int rid, final int num) {
        List<Reactor> toDestroy = new ArrayList<>();
        Map<Integer, Integer> contained = new LinkedHashMap<>();

        for (MapObject obj : getReactors()) {
            Reactor mr = (Reactor) obj;
            if (contained.containsKey(mr.getId())) {
                if (contained.get(mr.getId()) >= num) {
                    toDestroy.add(mr);
                } else {
                    contained.put(mr.getId(), contained.get(mr.getId()) + 1);
                }
            } else {
                contained.put(mr.getId(), 1);
            }
        }

        for (Reactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    /**
     * 检查所有指定ID的反应堆是否都处于指定状态
     *
     * @param reactorId 反应堆ID
     * @param state     状态
     * @return 如果所有反应堆都处于指定状态返回true
     */
    public boolean isAllReactorState(final int reactorId, final int state) {
        for (MapObject mo : getReactors()) {
            Reactor r = (Reactor) mo;

            if (r.getId() == reactorId && r.getState() != state) {
                return false;
            }
        }
        return true;
    }

    public int getCurrentPartyId() {
        for (Character chr : this.getCharacters()) {
            if (chr.getPartyId() != -1) {
                return chr.getPartyId();
            }
        }
        return -1;
    }

    /**
     * 添加玩家NPC对象到地图
     * 
     * <p>将指定的玩家NPC对象添加到地图对象集合中，使其在地图上可见。
     * 此方法用于添加由玩家控制的NPC角色。</p>
     * 
     * @param pnpcobject 要添加的玩家NPC对象
     */
    public void addPlayerNPCMapObject(PlayerNPC pnpcobject) {
        objectWLock.lock();
        try {
            this.mapobjects.put(pnpcobject.getObjectId(), pnpcobject);
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 添加地图对象到地图
     * 
     * <p>为指定的地图对象分配唯一的对象ID，并将其添加到地图对象集合中。
     * 此方法用于添加各种类型的地图对象，如怪物、NPC、物品等。</p>
     * 
     * @param mapobject 要添加的地图对象
     */
    public void addMapObject(MapObject mapobject) {
        int curOID = getUsableOID();

        objectWLock.lock();
        try {
            mapobject.setObjectId(curOID);
            this.mapobjects.put(curOID, mapobject);
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 添加自毁怪物到自毁集合
     * 
     * <p>将具有自毁能力的怪物添加到自毁集合中进行跟踪。
     * 自毁怪物会在特定条件下自动销毁自身，通常用于某些特殊怪物。</p>
     * 
     * @param mob 要添加的怪物对象
     */
    public void addSelfDestructive(Monster mob) {
        if (mob.getStats().selfDestruction() != null) {
            this.selfDestructives.add(mob.getObjectId());
        }
    }

    /**
     * 从自毁集合中移除指定对象
     * 
     * <p>将指定对象从自毁集合中移除，停止对其自毁行为的跟踪。</p>
     * 
     * @param mapobjectid 要移除的对象ID
     * @return 如果集合中包含该元素则返回true，否则返回false
     */
    public boolean removeSelfDestructive(int mapobjectid) {
        return this.selfDestructives.remove(mapobjectid);
    }

    /**
     * 添加范围地图对象并通知范围内玩家
     * 
     * <p>将指定的地图对象添加到地图中，并向范围内符合条件的玩家发送生成数据包。
     * 此方法用于添加需要立即对周围玩家产生视觉效果的对象。</p>
     * 
     * @param mapobject 要添加的地图对象
     * @param packetbakery 数据包生成器，用于创建和发送对象生成数据包
     */
    private void spawnAndAddRangedMapObject(MapObject mapobject, DelayedPacketCreation packetbakery) {
        spawnAndAddRangedMapObject(mapobject, packetbakery, null);
    }

    /**
     * 添加范围地图对象并通知范围内玩家（带条件）
     * 
     * <p>将指定的地图对象添加到地图中，并向范围内符合条件的玩家发送生成数据包。
     * 此方法支持额外的生成条件检查，以确定是否对特定玩家显示对象。</p>
     * 
     * @param mapobject 要添加的地图对象
     * @param packetbakery 数据包生成器，用于创建和发送对象生成数据包
     * @param condition 生成条件，用于过滤哪些玩家可以看到对象，可以为null表示无条件
     */
    private void spawnAndAddRangedMapObject(MapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        List<Character> inRangeCharacters = new LinkedList<>();
        int curOID = getUsableOID();

        chrRLock.lock();
        objectWLock.lock();
        try {
            mapobject.setObjectId(curOID);
            this.mapobjects.put(curOID, mapobject);
            for (Character chr : characters) {
                if (condition == null || condition.canSpawn(chr)) {
                    if (chr.getPosition().distanceSq(mapobject.getPosition()) <= getRangedDistance()) {
                        inRangeCharacters.add(chr);
                        chr.addVisibleMapObject(mapobject);
                    }
                }
            }
        } finally {
            objectWLock.unlock();
            chrRLock.unlock();
        }

        for (Character chr : inRangeCharacters) {
            packetbakery.sendPackets(chr.getClient());
        }
    }

    private void spawnRangedMapObject(MapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        List<Character> inRangeCharacters = new LinkedList<>();

        chrRLock.lock();
        try {
            int curOID = getUsableOID();
            mapobject.setObjectId(curOID);
            for (Character chr : characters) {
                if (condition == null || condition.canSpawn(chr)) {
                    if (chr.getPosition().distanceSq(mapobject.getPosition()) <= getRangedDistance()) {
                        inRangeCharacters.add(chr);
                        chr.addVisibleMapObject(mapobject);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }

        for (Character chr : inRangeCharacters) {
            packetbakery.sendPackets(chr.getClient());
        }
    }

    private int getUsableOID() {
        objectRLock.lock();
        try {
            int curOid;

            // clashes with playernpc on curOid >= 2147000000, developernpc uses >= 2147483000
            do {
                if ((curOid = runningOid.incrementAndGet()) >= 2147000000) {
                    runningOid.set(curOid = 1000000001);
                }
            } while (mapobjects.containsKey(curOid));

            return curOid;
        } finally {
            objectRLock.unlock();
        }
    }

    public void removeMapObject(int num) {
        objectWLock.lock();
        try {
            this.mapobjects.remove(num);
        } finally {
            objectWLock.unlock();
        }
    }

    public void removeMapObject(final MapObject obj) {
        removeMapObject(obj.getObjectId());
    }

    private Point calcPointBelow(Point initial) {
        Foothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2)));
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) s5;
            } else {
                dropY = fh.getY1() + (int) s5;
            }
        }
        return new Point(initial.x, dropY);
    }

    public void generateMapDropRangeCache() {
        bndLock.lock();
        try {
            Pair<Integer, Integer> bounds = dropBoundsCache.get(mapid);

            if (bounds != null) {
                xLimits = bounds;
            } else {
                // assuming MINIMAP always have an equal-greater picture representation of the map area (players won't walk beyond the area known by the minimap).
                Point lp = new Point(mapArea.x, mapArea.y);
                Point rp = new Point(mapArea.x + mapArea.width, mapArea.y);
                Point fallback = new Point(mapArea.x + (mapArea.width / 2), mapArea.y);

                lp = bsearchDropPos(lp, fallback);  // approximated leftmost fh node position
                rp = bsearchDropPos(rp, fallback);  // approximated rightmost fh node position

                xLimits = new Pair<>(lp.x + 14, rp.x - 14);
                dropBoundsCache.put(mapid, xLimits);
            }
        } finally {
            bndLock.unlock();
        }
    }

    private Point bsearchDropPos(Point initial, Point fallback) {
        Point res, dropPos = null;

        int awayx = fallback.x;
        int homex = initial.x;

        int y = initial.y - 85;

        do {
            int distx = awayx - homex;
            int dx = distx / 2;

            int searchx = homex + dx;
            if ((res = calcPointBelow(new Point(searchx, y))) != null) {
                awayx = searchx;
                dropPos = res;
            } else {
                homex = searchx;
            }
        } while (Math.abs(homex - awayx) > 5);

        return (dropPos != null) ? dropPos : fallback;
    }

    public Point calcDropPos(Point initial, Point fallback) {
        if (initial.x < xLimits.left) {
            initial.x = xLimits.left;
        } else if (initial.x > xLimits.right) {
            initial.x = xLimits.right;
        }

        Point ret = calcPointBelow(new Point(initial.x, initial.y - 85));   // actual drop ranges: default - 120, explosive - 360
        if (ret == null) {
            ret = bsearchDropPos(initial, fallback);
        }

        if (!mapArea.contains(ret)) { // found drop pos outside the map :O
            return fallback;
        }

        return ret;
    }

    public boolean canDeployDoor(Point pos) {
        Point toStep = calcPointBelow(pos);
        return toStep != null && toStep.distance(pos) <= 42;
    }

    /**
     * Fetches angle relative between spawn and door points where 3 O'Clock is 0
     * and 12 O'Clock is 270 degrees
     *
     * @param spawnPoint
     * @param doorPoint
     * @return angle in degress from 0-360.
     */
    private static double getAngle(Point doorPoint, Point spawnPoint) {
        double dx = doorPoint.getX() - spawnPoint.getX();
        // Minus to correct for coord re-mapping
        double dy = -(doorPoint.getY() - spawnPoint.getY());

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
        if (inRads < 0) {
            inRads = Math.abs(inRads);
        } else {
            inRads = 2 * Math.PI - inRads;
        }

        return Math.toDegrees(inRads);
    }

    /**
     * Converts angle in degrees to rounded cardinal coordinate.
     *
     * @param angle
     * @return correspondent coordinate.
     */
    public static String getRoundedCoordinate(double angle) {
        String[] directions = {"E", "SE", "S", "SW", "W", "NW", "N", "NE", "E"};
        return directions[(int) Math.round(((angle % 360) / 45))];
    }

    public Pair<String, Integer> getDoorPositionStatus(Point pos) {
        Portal portal = findClosestPlayerSpawnpoint(pos);

        double angle = getAngle(portal.getPosition(), pos);
        double distn = pos.distanceSq(portal.getPosition());

        if (distn <= 777777.7) {
            return null;
        }

        distn = Math.sqrt(distn);
        return new Pair<>(getRoundedCoordinate(angle), (int) distn);
    }

    private static void sortDropEntries(List<MonsterDropEntry> from, List<MonsterDropEntry> item, List<MonsterDropEntry> visibleQuest, List<MonsterDropEntry> otherQuest, Character chr) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (MonsterDropEntry mde : from) {
            if (!ii.isQuestItem(mde.itemId)) {
                item.add(mde);
            } else {
                if (chr.needQuestItem(mde.questid, mde.itemId)) {
                    visibleQuest.add(mde);
                } else {
                    otherQuest.add(mde);
                }
            }
        }
    }

    private byte dropItemsFromMonsterOnMap(List<MonsterDropEntry> dropEntry, Point pos, byte d, float chRate, byte droptype, int mobpos, Character chr, Monster mob) {
        if (dropEntry.isEmpty()) {
            return d;
        }

        Collections.shuffle(dropEntry);

        Item idrop;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (final MonsterDropEntry de : dropEntry) {
            float cardRate = chr.getCardRate(de.itemId);
            int dropChance = (int) Math.min((float) de.chance * chRate * cardRate, Integer.MAX_VALUE);

            if (Randomizer.nextInt(999999) < dropChance) {
                if (droptype == 3) {
                    pos.x = mobpos + ((d % 2 == 0) ? (40 * ((d + 1) / 2)) : -(40 * (d / 2)));
                } else {
                    pos.x = mobpos + ((d % 2 == 0) ? (25 * ((d + 1) / 2)) : -(25 * (d / 2)));
                }
                if (de.itemId == 0) { // meso
                    int mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;

                    if (mesos > 0) {
                        if (chr.getBuffedValue(BuffStat.MESOUP) != null) {
                            mesos = NumberTool.doubleToInt(mesos * chr.getBuffedValue(BuffStat.MESOUP).doubleValue() / 100.0);
                        }
                        mesos = NumberTool.floatToInt(mesos * chr.getMesoRate());
                        if (mesos <= 0) {
                            mesos = Integer.MAX_VALUE;
                        }

                        spawnMesoDrop(mesos, calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
                    }
                } else {
                    if (ItemConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                }
                d++;
            }
        }

        return d;
    }

    private byte dropGlobalItemsFromMonsterOnMap(List<MonsterGlobalDropEntry> globalEntry, Point pos, byte d, byte droptype, int mobpos, Character chr, Monster mob) {
        Collections.shuffle(globalEntry);

        Item idrop;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (final MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) < de.chance) {
                if (droptype == 3) {
                    pos.x = mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2)));
                } else {
                    pos.x = mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2)));
                }
                if (de.itemId != 0) {
                    if (ItemConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                    d++;
                }
            }
        }

        return d;
    }

    private void dropFromMonster(final Character chr, final Monster mob, final boolean useBaseRate) {
        if (mob.dropsDisabled() || !dropsOn) {
            return;
        }

        final byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getPosition().x;
        float chRate = !mob.isBoss() ? chr.getDropRate() : chr.getBossDropRate();
        Point pos = new Point(0, mob.getPosition().y);

        MonsterStatusEffect stati = mob.getStati(MonsterStatus.SHOWDOWN);
        if (stati != null) {
            chRate *= (stati.getStati().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0 + 1.0);
        }

        if (chr.isFamilyBuff()) {
            chRate *= chr.getFamilyDrop();
        }

        if (useBaseRate) {
            chRate = 1;
        }

        final MonsterInformationProvider mi = MonsterInformationProvider.getInstance();
        final List<MonsterGlobalDropEntry> globalEntry = mi.getRelevantGlobalDrops(this.getId());

        final List<MonsterDropEntry> dropEntry = new ArrayList<>();
        final List<MonsterDropEntry> visibleQuestEntry = new ArrayList<>();
        final List<MonsterDropEntry> otherQuestEntry = new ArrayList<>();

        List<MonsterDropEntry> lootEntry = GameConfig.getServerBoolean("use_spawn_relevant_loot") ? mob.retrieveRelevantDrops() : mi.retrieveEffectiveDrop(mob.getId());
        sortDropEntries(lootEntry, dropEntry, visibleQuestEntry, otherQuestEntry, chr);     // thanks Articuno, Limit, Rohenn for noticing quest loots not showing up in only-quest item drops scenario

        if (lootEntry.isEmpty()) {   // thanks resinate
            return;
        }

        registerMobItemDrops(droptype, mobpos, chRate, pos, dropEntry, visibleQuestEntry, otherQuestEntry, globalEntry, chr, mob);
    }

    public void dropItemsFromMonster(List<MonsterDropEntry> list, final Character chr, final Monster mob) {
        if (mob.dropsDisabled() || !dropsOn) {
            return;
        }

        final byte droptype = (byte) (chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getPosition().x;
        int chRate = 1000000;   // guaranteed item drop
        byte d = 1;
        Point pos = new Point(0, mob.getPosition().y);

        dropItemsFromMonsterOnMap(list, pos, d, chRate, droptype, mobpos, chr, mob);
    }

    public void dropFromFriendlyMonster(final Character chr, final Monster mob) {
        dropFromMonster(chr, mob, true);
    }

    public void dropFromReactor(final Character chr, final Reactor reactor, Item drop, Point dropPos, short questid) {
        spawnDrop(drop, this.calcDropPos(dropPos, reactor.getPosition()), reactor, chr, (byte) (chr.getParty() != null ? 1 : 0), questid);
    }

    private void stopItemMonitor() {
        itemMonitor.cancel(false);
        itemMonitor = null;

        expireItemsTask.cancel(false);
        expireItemsTask = null;

        if (GameConfig.getServerBoolean("use_spawn_loot_on_animation")) {
            mobSpawnLootTask.cancel(false);
            mobSpawnLootTask = null;
        }

        characterStatUpdateTask.cancel(false);
        characterStatUpdateTask = null;
    }

    /**
     * 清理物品监控器
     * 
     * <p>清理已注册的掉落物列表中的空引用（null引用），
     * 释放不再有效的弱引用对象，避免内存泄漏。</p>
     */
    private void cleanItemMonitor() {
        objectWLock.lock();
        try {
            registeredDrops.removeAll(Collections.singleton(null));
        } finally {
            objectWLock.unlock();
        }
    }

    /**
     * 启动物品监控器
     * 
     * <p>启动一个定时任务，用于监控地图上的掉落物品。
     * 该任务会定期检查地图上是否有玩家，如果没有玩家且超时，
     * 则停止物品监控器和怪物仇恨协调器，以节省资源。</p>
     * 
     * <p>当地图上有玩家时，会重置超时计数器，保持监控器运行；
     * 当地图上没有玩家时，会递减超时计数器，达到零时停止监控器。</p>
     * 
     * <p>同时还会启动其他相关的定时任务，包括：
     * - 过期物品清理任务
     * - 怪物掉落物生成任务（如果启用）
     * - 角色状态更新任务</p>
     */
    private void startItemMonitor() {
        chrWLock.lock();
        try {
            if (itemMonitor != null) {
                return;
            }

            itemMonitor = TimerManager.getInstance().register(() -> {
                chrWLock.lock();
                try {
                    if (characters.isEmpty()) {
                        if (itemMonitorTimeout == 0) {
                            if (itemMonitor != null) {
                                stopItemMonitor();
                                aggroMonitor.stopAggroCoordinator();
                            }

                            return;
                        } else {
                            itemMonitorTimeout--;
                        }
                    } else {
                        itemMonitorTimeout = 1;
                    }
                } finally {
                    chrWLock.unlock();
                }

                boolean tryClean;
                objectRLock.lock();
                try {
                    tryClean = registeredDrops.size() > 70;
                } finally {
                    objectRLock.unlock();
                }

                if (tryClean) {
                    cleanItemMonitor();
                }
            }, GameConfig.getServerLong("item_monitor_time"), GameConfig.getServerLong("item_monitor_time"));

            // 注册过期物品清理任务，定期清理超过保存时间的掉落物
            expireItemsTask = TimerManager.getInstance().register(this::makeDisappearExpiredItemDrops, GameConfig.getServerLong("item_expire_check"), GameConfig.getServerLong("item_expire_check"));

            if (GameConfig.getServerBoolean("use_spawn_loot_on_animation")) {
                // 如果启用动画掉落功能，则清空怪物掉落条目并启动掉落生成任务
                lootLock.lock();
                try {
                    mobLootEntries.clear();
                } finally {
                    lootLock.unlock();
                }

                // 启动怪物掉落物生成任务，每200毫秒执行一次
                mobSpawnLootTask = TimerManager.getInstance().register(this::spawnMobItemDrops, 200, 200);
            }

            // 启动角色状态更新任务，每200毫秒执行一次
            characterStatUpdateTask = TimerManager.getInstance().register(this::runCharacterStatUpdate, 200, 200);

            itemMonitorTimeout = 1;
        } finally {
            chrWLock.unlock();
        }
    }

    /**
     * 检查是否已启动物品监控器
     * 
     * <p>检查当前地图是否已经启动了物品监控器，
     * 用于判断地图上是否正在进行掉落物监控。</p>
     * 
     * @return 如果已启动物品监控器则返回true，否则返回false
     */
    private boolean hasItemMonitor() {
        chrRLock.lock();
        try {
            return itemMonitor != null;
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取掉落物品计数
     * 
     * <p>返回当前地图上的掉落物品总数，
     * 该数值由原子整数维护，保证线程安全。</p>
     * 
     * @return 当前地图上的掉落物品总数
     */
    public int getDroppedItemCount() {
        return droppedItemCount.get();
    }

    private void instantiateItemDrop(MapItem mdrop) {
        if (droppedItemCount.get() >= GameConfig.getServerInt("item_limit_on_map")) {
            MapObject mapobj;

            do {
                mapobj = null;

                objectWLock.lock();
                try {
                    while (mapobj == null) {
                        if (registeredDrops.isEmpty()) {
                            break;
                        }
                        mapobj = registeredDrops.remove(0).get();
                    }
                } finally {
                    objectWLock.unlock();
                }
            } while (!makeDisappearItemFromMap(mapobj));
        }

        objectWLock.lock();
        try {
            registerItemDrop(mdrop);
            registeredDrops.add(new WeakReference<>(mdrop));
        } finally {
            objectWLock.unlock();
        }

        droppedItemCount.incrementAndGet();
    }

    private void registerItemDrop(MapItem mdrop) {
        droppedItems.put(mdrop, !everlast ? Server.getInstance().getCurrentTime() + GameConfig.getServerLong("item_expire_time") : Long.MAX_VALUE);
    }

    private void unregisterItemDrop(MapItem mdrop) {
        objectWLock.lock();
        try {
            droppedItems.remove(mdrop);
        } finally {
            objectWLock.unlock();
        }
    }

    private void makeDisappearExpiredItemDrops() {
        List<MapItem> toDisappear = new LinkedList<>();

        objectRLock.lock();
        try {
            long timeNow = Server.getInstance().getCurrentTime();

            for (Entry<MapItem, Long> it : droppedItems.entrySet()) {
                if (it.getValue() < timeNow) {
                    toDisappear.add(it.getKey());
                }
            }
        } finally {
            objectRLock.unlock();
        }

        for (MapItem mmi : toDisappear) {
            makeDisappearItemFromMap(mmi);
        }

        objectWLock.lock();
        try {
            for (MapItem mmi : toDisappear) {
                droppedItems.remove(mmi);
            }
        } finally {
            objectWLock.unlock();
        }
    }

    private void registerMobItemDrops(byte droptype, int mobpos, float chRate, Point pos, List<MonsterDropEntry> dropEntry, List<MonsterDropEntry> visibleQuestEntry, List<MonsterDropEntry> otherQuestEntry, List<MonsterGlobalDropEntry> globalEntry, Character chr, Monster mob) {
        MobLootEntry mle = new MobLootEntry(droptype, mobpos, chRate, pos, dropEntry, visibleQuestEntry, otherQuestEntry, globalEntry, chr, mob);

        if (GameConfig.getServerBoolean("use_spawn_loot_on_animation")) {
            int animationTime = mob.getAnimationTime("die1");

            lootLock.lock();
            try {
                long timeNow = Server.getInstance().getCurrentTime();
                mobLootEntries.put(mle, timeNow + ((long) (0.42 * animationTime)));
            } finally {
                lootLock.unlock();
            }
        } else {
            mle.run();
        }
    }

    private void spawnMobItemDrops() {
        Set<Entry<MobLootEntry, Long>> mleList;

        lootLock.lock();
        try {
            mleList = new HashSet<>(mobLootEntries.entrySet());
        } finally {
            lootLock.unlock();
        }

        long timeNow = Server.getInstance().getCurrentTime();
        List<MobLootEntry> toRemove = new LinkedList<>();
        for (Entry<MobLootEntry, Long> mlee : mleList) {
            if (mlee.getValue() < timeNow) {
                toRemove.add(mlee.getKey());
            }
        }

        if (!toRemove.isEmpty()) {
            List<MobLootEntry> toSpawnLoot = new LinkedList<>();

            lootLock.lock();
            try {
                for (MobLootEntry mle : toRemove) {
                    Long mler = mobLootEntries.remove(mle);
                    if (mler != null) {
                        toSpawnLoot.add(mle);
                    }
                }
            } finally {
                lootLock.unlock();
            }

            for (MobLootEntry mle : toSpawnLoot) {
                mle.run();
            }
        }
    }

    private List<MapItem> getDroppedItems() {
        objectRLock.lock();
        try {
            return new LinkedList<>(droppedItems.keySet());
        } finally {
            objectRLock.unlock();
        }
    }

    public int getDroppedItemsCountById(int itemid) {
        int count = 0;
        for (MapItem mmi : getDroppedItems()) {
            if (mmi.getItemId() == itemid) {
                count++;
            }
        }

        return count;
    }

    public void pickItemDrop(Packet pickupPacket, MapItem mdrop) { // mdrop must be already locked and not-pickedup checked at this point
        broadcastMessage(pickupPacket, mdrop.getPosition());

        droppedItemCount.decrementAndGet();
        this.removeMapObject(mdrop);
        mdrop.setPickedUp(true);
        unregisterItemDrop(mdrop);
    }

    public List<MapItem> updatePlayerItemDropsToParty(int partyid, int charid, List<Character> partyMembers, Character partyLeaver) {
        List<MapItem> partyDrops = new LinkedList<>();

        for (MapItem mdrop : getDroppedItems()) {
            if (mdrop.getOwnerId() == charid) {
                mdrop.lockItem();
                try {
                    if (mdrop.isPickedUp()) {
                        continue;
                    }

                    mdrop.setPartyOwnerId(partyid);

                    Packet removePacket = PacketCreator.silentRemoveItemFromMap(mdrop.getObjectId());
                    Packet updatePacket = PacketCreator.updateMapItemObject(mdrop, partyLeaver == null);

                    for (Character mc : partyMembers) {
                        if (this.equals(mc.getMap())) {
                            mc.sendPacket(removePacket);

                            if (mc.needQuestItem(mdrop.getQuest(), mdrop.getItemId())) {
                                mc.sendPacket(updatePacket);
                            }
                        }
                    }

                    if (partyLeaver != null) {
                        if (this.equals(partyLeaver.getMap())) {
                            partyLeaver.sendPacket(removePacket);

                            if (partyLeaver.needQuestItem(mdrop.getQuest(), mdrop.getItemId())) {
                                partyLeaver.sendPacket(PacketCreator.updateMapItemObject(mdrop, true));
                            }
                        }
                    }
                } finally {
                    mdrop.unlockItem();
                }
            } else if (partyid != -1 && mdrop.getPartyOwnerId() == partyid) {
                partyDrops.add(mdrop);
            }
        }

        return partyDrops;
    }

    public void updatePartyItemDropsToNewcomer(Character newcomer, List<MapItem> partyItems) {
        for (MapItem mdrop : partyItems) {
            mdrop.lockItem();
            try {
                if (mdrop.isPickedUp()) {
                    continue;
                }

                Packet removePacket = PacketCreator.silentRemoveItemFromMap(mdrop.getObjectId());
                Packet updatePacket = PacketCreator.updateMapItemObject(mdrop, true);

                if (newcomer != null) {
                    if (this.equals(newcomer.getMap())) {
                        newcomer.sendPacket(removePacket);

                        if (newcomer.needQuestItem(mdrop.getQuest(), mdrop.getItemId())) {
                            newcomer.sendPacket(updatePacket);
                        }
                    }
                }
            } finally {
                mdrop.unlockItem();
            }
        }
    }

    private void spawnDrop(final Item idrop, final Point dropPos, final MapObject dropper, final Character chr, final byte droptype, final short questid) {
        final MapItem mdrop = new MapItem(idrop, dropPos, dropper, chr, chr.getClient(), droptype, false, questid);
        mdrop.setDropTime(Server.getInstance().getCurrentTime());
        spawnAndAddRangedMapObject(mdrop, c -> {
            Character chr1 = c.getPlayer();

            if (chr1.needQuestItem(questid, idrop.getItemId())) {
                mdrop.lockItem();
                try {
                    c.sendPacket(PacketCreator.dropItemFromMapObject(chr1, mdrop, dropper.getPosition(), dropPos, (byte) 1));
                } finally {
                    mdrop.unlockItem();
                }
            }
        }, null);

        instantiateItemDrop(mdrop);
        activateItemReactors(mdrop, chr.getClient());
    }

    public final void spawnMesoDrop(final int meso, final Point position, final MapObject dropper, final Character owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapItem mdrop = new MapItem(meso, droppos, dropper, owner, owner.getClient(), droptype, playerDrop);
        mdrop.setDropTime(Server.getInstance().getCurrentTime());

        spawnAndAddRangedMapObject(mdrop, c -> {
            mdrop.lockItem();
            try {
                c.sendPacket(PacketCreator.dropItemFromMapObject(c.getPlayer(), mdrop, dropper.getPosition(), droppos, (byte) 1));
            } finally {
                mdrop.unlockItem();
            }
        }, null);

        instantiateItemDrop(mdrop);
    }

    public final void disappearingItemDrop(final MapObject dropper, final Character owner, final Item item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapItem mdrop = new MapItem(item, droppos, dropper, owner, owner.getClient(), (byte) 1, false);

        mdrop.lockItem();
        try {
            broadcastItemDropMessage(mdrop, dropper.getPosition(), droppos, (byte) 3, mdrop.getPosition());
        } finally {
            mdrop.unlockItem();
        }
    }

    public final void disappearingMesoDrop(final int meso, final MapObject dropper, final Character owner, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapItem mdrop = new MapItem(meso, droppos, dropper, owner, owner.getClient(), (byte) 1, false);

        mdrop.lockItem();
        try {
            broadcastItemDropMessage(mdrop, dropper.getPosition(), droppos, (byte) 3, mdrop.getPosition());
        } finally {
            mdrop.unlockItem();
        }
    }

    public Monster getMonsterById(int id) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.MONSTER) {
                    if (((Monster) obj).getId() == id) {
                        return (Monster) obj;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return null;
    }

    public int countMonster(int id) {
        return countMonster(id, id);
    }

    public int countMonster(int minid, int maxid) {
        int count = 0;
        for (MapObject m : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster mob = (Monster) m;
            if (mob.getId() >= minid && mob.getId() <= maxid) {
                count++;
            }
        }
        return count;
    }

    public int countMonsters() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER)).size();
    }

    public int countReactors() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.REACTOR)).size();
    }

    public final List<MapObject> getReactors() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.REACTOR));
    }

    public final List<MapObject> getMonsters() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER));
    }

    public final List<Reactor> getAllReactors() {
        List<Reactor> list = new LinkedList<>();
        for (MapObject mmo : getReactors()) {
            list.add((Reactor) mmo);
        }

        return list;
    }

    public final List<Monster> getAllMonsters() {
        List<Monster> list = new LinkedList<>();
        for (MapObject mmo : getMonsters()) {
            list.add((Monster) mmo);
        }

        return list;
    }

    public int countItems() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM)).size();
    }

    public final List<MapObject> getItems() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM));
    }

    public int countPlayers() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER)).size();
    }

    public List<MapObject> getPlayers() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER));
    }

    public List<Character> getAllPlayers() {
        List<Character> character;
        chrRLock.lock();
        try {
            character = new ArrayList<>(characters);
        } finally {
            chrRLock.unlock();
        }

        return character;
    }

    public Map<Integer, Character> getMapAllPlayers() {
        Map<Integer, Character> pchars = new HashMap<>();
        for (Character chr : this.getAllPlayers()) {
            pchars.put(chr.getId(), chr);
        }

        return pchars;
    }

    public List<Character> getPlayersInRange(Rectangle box) {
        List<Character> character = new LinkedList<>();
        chrRLock.lock();
        try {
            for (Character chr : characters) {
                if (box.contains(chr.getPosition())) {
                    character.add(chr);
                }
            }
        } finally {
            chrRLock.unlock();
        }

        return character;
    }

    public int countAlivePlayers() {
        int count = 0;

        for (Character mc : getAllPlayers()) {
            if (mc.isAlive()) {
                count++;
            }
        }

        return count;
    }

    public int countBosses() {
        int count = 0;

        for (Monster mob : getAllMonsters()) {
            if (mob.isBoss()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 对怪物造成伤害
     * 
     * <p>处理玩家对怪物的攻击，计算伤害并对怪物状态进行相应更新。
     * 特殊处理扎昆相关逻辑：如果攻击扎昆本体，会检查是否还有扎昆手臂存在。
     * 如果怪物设置了自爆机制且血量低于阈值，则触发自爆。</p>
     * 
     * <p>处理流程：</p>
     * <ol>
     *   <li>特殊怪物处理（如扎昆相关逻辑）</li>
     *   <li>对怪物造成伤害</li>
     *   <li>检查怪物是否触发自爆机制</li>
     *   <li>如果怪物死亡则进行击杀处理</li>
     * </ol>
     * 
     * @param chr 攻击者角色
     * @param monster 被攻击的怪物
     * @param damage 造成的伤害值
     * @return 如果成功处理伤害则返回true，否则返回false
     */
    public boolean damageMonster(final Character chr, final Monster monster, final int damage) {
        if (monster.getId() == MobId.ZAKUM_1) {
            for (MapObject object : chr.getMap().getMapObjects()) {
                Monster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                if (mons != null) {
                    if (mons.getId() >= MobId.ZAKUM_ARM_1 && mons.getId() <= MobId.ZAKUM_ARM_8) {
                        return true;
                    }
                }
            }
        }
        if (monster.isAlive()) {
            boolean killed = monster.damage(chr, damage, false);

            selfDestruction selfDestr = monster.getStats().selfDestruction();
            if (selfDestr != null && selfDestr.getHp() > -1) {// should work ;p
                if (monster.getHp() <= selfDestr.getHp()) {
                    killMonster(monster, chr, true, selfDestr.getAction());
                    return true;
                }
            }
            if (killed) {
                killMonster(monster, chr, true);
            }
            return true;
        }
        return false;
    }

    // 巴洛古(Balrog)讨伐胜利广播
    public void broadcastBalrogVictory(String leaderName) {
        getWorldServer().dropMessage(6,"[远征凯旋] " + leaderName + "的远征队成功讨伐了火焰魔神巴洛古！" + "让我们歌颂这支队伍，他们以" + countAlivePlayers() + "名幸存者的战绩完成了壮举！");
    }

    // 暗黑龙王(Horntail)讨伐胜利广播
    public void broadcastHorntailVictory() {
        getWorldServer().dropMessage(6,"[远征凯旋] 致历经无数次挑战最终征服暗黑龙王的勇士们：" + "谨以此礼赞献给真正的神木村英雄！");
    }

    // 扎昆(Zakum)讨伐胜利广播
    public void broadcastZakumVictory() {
        getWorldServer().dropMessage(6,"[远征凯旋] 长久笼罩天空之城的邪恶之树终于倾倒！" +"致那些历经无数次尝试最终征服扎昆的远征队，胜利属于你们！" +"你们是天空之城真正的传说！");
    }

    // 品克缤(PinkBean)讨伐胜利广播
    public void broadcastPinkBeanVictory(int channel) {
        getWorldServer().dropMessage(6,"[远征凯旋] 在" + channel + "频道挑战品克缤的远征队，" +  "以雷霆之势完成了终极讨伐！时间神殿重现璀璨光辉，" + "当英雄们从战场凯旋之时，被夺走的白昼终于归来！"
        );
    }


    private boolean removeKilledMonsterObject(Monster monster) {
        monster.lockMonster();
        try {
            if (monster.getHp() < 0) {
                return false;
            }

            spawnedMonstersOnMap.decrementAndGet();
            removeMapObject(monster);
            monster.disposeMapObject();
            if (monster.hasBossHPBar()) {   // thanks resinate for noticing boss HPbar not clearing after mob defeat in certain scenarios   //感谢resinate注意到在某些情况下暴徒失败后老板HPbar没有清除
                broadcastBossHpMessage(monster, monster.hashCode(), monster.makeBossHPBarPacket(), monster.getPosition());
            }

            return true;
        } finally {
            monster.unlockMonster();
        }
    }

    /**
     * 击杀怪物（使用默认动画）
     * 
     * <p>击杀指定的怪物，使用默认的死亡动画效果，并根据参数决定是否生成掉落物。</p>
     * 
     * @param monster 要击杀的怪物对象
     * @param chr 执行击杀操作的角色，如果是系统击杀则传入null
     * @param withDrops 是否生成掉落物
     */
    /**
     * 击杀怪物（使用默认动画）
     * 
     * <p>击杀指定的怪物，使用默认的死亡动画效果，并根据参数决定是否生成掉落物。</p>
     * 
     * <p>此方法是killMonster的便捷方法，使用默认的死亡动画（值为1）。
     * 它会调用带有动画参数的完整版本来执行实际的击杀逻辑。</p>
     * 
     * @param monster 要击杀的怪物对象
     * @param chr 执行击杀操作的角色，如果是系统击杀则传入null
     * @param withDrops 是否生成掉落物
     */
    public void killMonster(final Monster monster, final Character chr, final boolean withDrops) {
        killMonster(monster, chr, withDrops, 1);
    }

    /**
     * 击杀怪物（指定动画效果）
     * 
     * <p>击杀指定的怪物，使用指定的死亡动画效果，并根据参数决定是否生成掉落物。
     * 如果指定了角色，则会进行等级检查以防止作弊，处理特殊奖励（如CP值），
     * 应用怪物给予的增益效果，处理扎昆相关逻辑，以及掉落物生成等。</p>
     * 
     * <p>主要处理逻辑包括：</p>
     * <ul>
     *   <li>反作弊检查：如果怪物等级远高于角色等级则发出警告</li>
     *   <li>CP值奖励：在CPQ地图击杀怪物时给予CP值</li>
     *   <li>增益效果：应用怪物死亡时给予玩家的增益效果</li>
     *   <li>扎昆逻辑：处理扎昆手臂被击杀后的实体化逻辑</li>
     *   <li>掉落物生成：根据参数决定是否生成掉落物</li>
     *   <li>BOSS处理：重置玩家对BOSS的仇恨目标</li>
     * </ul>
     * 
     * @param monster 要击杀的怪物对象
     * @param chr 执行击杀操作的角色，如果是系统击杀则传入null
     * @param withDrops 是否生成掉落物
     * @param animation 死亡动画效果类型
     */
    public void killMonster(final Monster monster, final Character chr, final boolean withDrops, int animation) {
        if (monster == null) {
            return;
        }

        if (chr == null) {
            if (removeKilledMonsterObject(monster)) {
                monster.dispatchMonsterKilled(false);
                broadcastMessage(PacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
                monster.aggroSwitchController(null, false);
            }
        } else {
            if (removeKilledMonsterObject(monster)) {
                try {
                    // 检查是否击杀远高于自身等级的怪物（防刷经验）
                    if (monster.getStats().getLevel() >= chr.getLevel() + 30 && !chr.isGM()) {
                        AutobanFactory.GENERAL.alert(chr, "因击杀超过自身30级的怪物[" + monster.getName() + "]被系统警告");
                    }

                    /*if (chr.getQuest(Quest.getInstance(29400)).getStatus().equals(QuestStatus.Status.STARTED)) {
                     if (chr.getLevel() >= 120 && monster.getStats().getLevel() >= 120) {
                     //FIX MEDAL SHET
                     } else if (monster.getStats().getLevel() >= chr.getLevel()) {
                     }
                     }*/

                    // 如果是CPQ地图且怪物提供CP值，则给予玩家CP
                    if (monster.getCP() > 0 && chr.getMap().isCPQMap()) {
                        chr.gainCP(monster.getCP());
                    }

                    // 应用怪物死亡时给予玩家的增益效果
                    int buff = monster.getBuffToGive();
                    if (buff > -1) {
                        ItemInformationProvider mii = ItemInformationProvider.getInstance();
                        for (MapObject mmo : this.getPlayers()) {
                            Character character = (Character) mmo;
                            if (character.isAlive()) {
                                StatEffect statEffect = mii.getItemEffect(buff);
                                character.sendPacket(PacketCreator.showOwnBuffEffect(buff, 1));
                                broadcastMessage(character, PacketCreator.showBuffEffect(character.getId(), buff, 1), false);
                                statEffect.applyTo(character);
                            }
                        }
                    }

                    // 处理扎昆手臂相关逻辑：当所有手臂都被击杀时，使扎昆实体化
                    if (MobId.isZakumArm(monster.getId())) {
                        boolean makeZakReal = true;
                        Collection<MapObject> objects = getMapObjects();
                        for (MapObject object : objects) {
                            Monster mons = getMonsterByOid(object.getObjectId());
                            if (mons != null) {
                                if (MobId.isZakumArm(mons.getId())) {
                                    makeZakReal = false;
                                    break;
                                }
                            }
                        }
                        if (makeZakReal) {
                            MapleMap map = chr.getMap();

                            for (MapObject object : objects) {
                                Monster mons = map.getMonsterByOid(object.getObjectId());
                                if (mons != null) {
                                    if (mons.getId() == MobId.ZAKUM_1) {
                                        makeMonsterReal(mons);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // 记录击杀者并处理掉落物
                    Character dropOwner = monster.killBy(chr);
                    if (withDrops && !monster.dropsDisabled()) {
                        if (dropOwner == null) {
                            dropOwner = chr;
                        }
                        dropFromMonster(dropOwner, monster, false);
                    }

                    // 如果是BOSS怪物，重置玩家的仇恨目标
                    if (monster.hasBossHPBar()) {
                        for (Character mc : this.getAllPlayers()) {
                            if (mc.getTargetHpBarHash() == monster.hashCode()) {
                                mc.resetPlayerAggro();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {     // thanks resinate for pointing out a memory leak possibly from an exception thrown
                    monster.dispatchMonsterKilled(true);
                    broadcastMessage(PacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
                }
            }
        }
    }

    /**
     * 击杀友好怪物
     * 
     * <p>击杀指定的友好怪物（NPC类型的怪物），使用地图上的第一个玩家作为击杀者，
     * 不生成掉落物。通常用于处理特殊事件或任务相关的友好怪物。</p>
     * 
     * @param mob 要击杀的友好怪物对象
     */
    public void killFriendlies(Monster mob) {
        this.killMonster(mob, (Character) getPlayers().get(0), false);
    }

    /**
     * 根据怪物ID击杀地图上的怪物
     * 
     * <p>根据指定的怪物ID在当前地图上查找并击杀对应的怪物，
     * 使用地图上第一个玩家作为击杀者，并不生成掉落物。</p>
     * 
     * @param mobId 要击杀的怪物ID
     */
    public void killMonster(int mobId) {
        Character chr = (Character) getPlayers().get(0);
        List<Monster> mobList = getAllMonsters();

        for (Monster mob : mobList) {
            if (mob.getId() == mobId) {
                this.killMonster(mob, chr, false);
            }
        }
    }

    /**
     * 根据怪物ID击杀地图上的怪物（带掉落物）
     * 
     * <p>根据指定的怪物ID在当前地图上查找并击杀对应的怪物，
     * 优先选择对该怪物造成最高伤害的玩家作为击杀者，如果找不到则使用默认玩家，
     * 并生成掉落物。</p>
     * 
     * @param mobId 要击杀的怪物ID
     */
    public void killMonsterWithDrops(int mobId) {
        Map<Integer, Character> mapChars = this.getMapPlayers();

        if (!mapChars.isEmpty()) {
            Character defaultChr = mapChars.entrySet().iterator().next().getValue();
            List<Monster> mobList = getAllMonsters();

            for (Monster mob : mobList) {
                if (mob.getId() == mobId) {
                    Character chr = mapChars.get(mob.getHighestDamagerId());
                    if (chr == null) {
                        chr = defaultChr;
                    }

                    this.killMonster(mob, chr, true);
                }
            }
        }
    }

    public void softKillAllMonsters() {
        closeMapSpawnPoints();

        for (MapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster monster = (Monster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }

            if (removeKilledMonsterObject(monster)) {
                monster.dispatchMonsterKilled(false);
            }
        }
    }

    public void killAllMonstersNotFriendly() {
        closeMapSpawnPoints();

        for (MapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster monster = (Monster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }

            killMonster(monster, null, false, 1);
        }
    }

    public void killAllMonsters() {
        closeMapSpawnPoints();

        for (MapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.MONSTER))) {
            Monster monster = (Monster) monstermo;

            killMonster(monster, null, false, 1);
        }
    }

    public final void destroyReactors(final int first, final int last) {
        List<Reactor> toDestroy = new ArrayList<>();
        List<MapObject> reactors = getReactors();

        for (MapObject obj : reactors) {
            Reactor mr = (Reactor) obj;
            if (mr.getId() >= first && mr.getId() <= last) {
                toDestroy.add(mr);
            }
        }

        for (Reactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactor(int oid) {
        final Reactor reactor = getReactorByOid(oid);

        if (reactor != null) {
            if (reactor.destroy()) {
                removeMapObject(reactor);
            }
        }
    }

    public void resetReactors() {
        List<Reactor> list = new ArrayList<>();

        objectRLock.lock();
        try {
            for (MapObject o : mapobjects.values()) {
                if (o.getType() == MapObjectType.REACTOR) {
                    final Reactor r = ((Reactor) o);
                    list.add(r);
                }
            }
        } finally {
            objectRLock.unlock();
        }

        resetReactors(list);
    }

    public final void resetReactors(List<Reactor> list) {
        for (Reactor r : list) {
            if (r.forceDelayedRespawn()) {  // thanks Conrad for suggesting reactor with delay respawning immediately
                continue;
            }

            r.lockReactor();
            try {
                r.resetReactorActions(0);
                r.setAlive(true);
                broadcastMessage(PacketCreator.triggerReactor(r, 0));
            } finally {
                r.unlockReactor();
            }
        }
    }

    public void shuffleReactors() {
        List<Point> points = new ArrayList<>();
        objectRLock.lock();
        try {
            for (MapObject o : mapobjects.values()) {
                if (o.getType() == MapObjectType.REACTOR) {
                    points.add(o.getPosition());
                }
            }
            Collections.shuffle(points);
            for (MapObject o : mapobjects.values()) {
                if (o.getType() == MapObjectType.REACTOR) {
                    o.setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            objectRLock.unlock();
        }
    }

    public final void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<>();
        List<MapObject> reactors = getReactors();
        List<MapObject> targets = new LinkedList<>();

        for (MapObject obj : reactors) {
            Reactor mr = (Reactor) obj;
            if (mr.getId() >= first && mr.getId() <= last) {
                points.add(mr.getPosition());
                targets.add(obj);
            }
        }
        Collections.shuffle(points);
        for (MapObject obj : targets) {
            Reactor mr = (Reactor) obj;
            mr.setPosition(points.remove(points.size() - 1));
        }
    }

    public final void shuffleReactors(List<Object> list) {
        List<Point> points = new ArrayList<>();
        List<MapObject> listObjects = new ArrayList<>();
        List<MapObject> targets = new LinkedList<>();

        objectRLock.lock();
        try {
            for (Object ob : list) {
                if (ob instanceof MapObject mmo) {

                    if (mapobjects.containsValue(mmo) && mmo.getType() == MapObjectType.REACTOR) {
                        listObjects.add(mmo);
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }

        for (MapObject obj : listObjects) {
            Reactor mr = (Reactor) obj;

            points.add(mr.getPosition());
            targets.add(obj);
        }
        Collections.shuffle(points);
        for (MapObject obj : targets) {
            Reactor mr = (Reactor) obj;
            mr.setPosition(points.remove(points.size() - 1));
        }
    }

    private Map<Integer, MapObject> getCopyMapObjects() {
        objectRLock.lock();
        try {
            return new HashMap<>(mapobjects);
        } finally {
            objectRLock.unlock();
        }
    }

    public List<MapObject> getMapObjects() {
        objectRLock.lock();
        try {
            return new LinkedList(mapobjects.values());
        } finally {
            objectRLock.unlock();
        }
    }

    public NPC getNPCById(int id) {
        for (MapObject obj : getMapObjects()) {
            if (obj.getType() == MapObjectType.NPC) {
                NPC npc = (NPC) obj;
                if (npc.getId() == id) {
                    return npc;
                }
            }
        }

        return null;
    }

    public boolean containsNPC(int npcid) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.NPC) {
                    if (((NPC) obj).getId() == npcid) {
                        return true;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return false;
    }

    public void destroyNPC(int npcid) {     // assumption: there's at most one of the same NPC in a map.
        List<MapObject> npcs = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.NPC));

        chrRLock.lock();
        objectWLock.lock();
        try {
            for (MapObject obj : npcs) {
                if (((NPC) obj).getId() == npcid) {
                    broadcastMessage(PacketCreator.removeNPCController(obj.getObjectId()));
                    broadcastMessage(PacketCreator.removeNPC(obj.getObjectId()));

                    this.mapobjects.remove(obj.getObjectId());
                }
            }
        } finally {
            objectWLock.unlock();
            chrRLock.unlock();
        }
    }

    public MapObject getMapObject(int oid) {
        objectRLock.lock();
        try {
            return mapobjects.get(oid);
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns
     * null
     *
     * @param oid
     * @return
     */
    public Monster getMonsterByOid(int oid) {
        MapObject mmo = getMapObject(oid);
        return (mmo != null && mmo.getType() == MapObjectType.MONSTER) ? (Monster) mmo : null;
    }

    public Reactor getReactorByOid(int oid) {
        MapObject mmo = getMapObject(oid);
        return (mmo != null && mmo.getType() == MapObjectType.REACTOR) ? (Reactor) mmo : null;
    }

    public Reactor getReactorById(int Id) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.REACTOR) {
                    if (((Reactor) obj).getId() == Id) {
                        return (Reactor) obj;
                    }
                }
            }
            return null;
        } finally {
            objectRLock.unlock();
        }
    }

    public List<Reactor> getReactorsByIdRange(final int first, final int last) {
        List<Reactor> list = new LinkedList<>();

        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.REACTOR) {
                    Reactor mr = (Reactor) obj;

                    if (mr.getId() >= first && mr.getId() <= last) {
                        list.add(mr);
                    }
                }
            }

            return list;
        } finally {
            objectRLock.unlock();
        }
    }

    public Reactor getReactorByName(String name) {
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.REACTOR) {
                    if (((Reactor) obj).getName().equals(name)) {
                        return (Reactor) obj;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return null;
    }

    /**
     * 在指定位置生成怪物
     * 
     * <p>根据给定的怪物ID和坐标位置，在地面下方生成一个怪物。
     * 此方法会自动计算合适的地面位置，确保怪物站在地面上。</p>
     * 
     * @param id 怪物ID
     * @param x X坐标
     * @param y Y坐标
     */
    public void spawnMonsterOnGroundBelow(int id, int x, int y) {
        Monster mob = LifeFactory.getMonster(id);
        spawnMonsterOnGroundBelow(mob, new Point(x, y));
    }

    /**
     * 在指定位置生成怪物
     * 
     * <p>根据给定的怪物对象和位置，在地面下方生成怪物。
     * 通过计算合适的地面位置，确保怪物站在地面上。</p>
     * 
     * @param mob 怪物对象
     * @param pos 期望生成的位置
     */
    public void spawnMonsterOnGroundBelow(Monster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        spawnMonster(mob);
    }

    /**
     * 在CPQ（怪物嘉年华）地图生成怪物
     * 
     * <p>在怪物嘉年华（Monster Carnival）地图中生成怪物，并设置其团队归属。
     * 此方法用于CPQ活动，确保怪物生成在合适的位置并分配到正确的团队。</p>
     * 
     * @param mob 怪物对象
     * @param pos 期望生成的位置
     * @param team 团队ID（用于区分红队或蓝队）
     */
    public void spawnCPQMonster(Monster mob, Point pos, int team) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        mob.setTeam(team);
        spawnMonster(mob);
    }

    private void monsterItemDrop(final Monster m, long delay) {
        m.dropFromFriendlyMonster(delay);
    }

    public void spawnFakeMonsterOnGroundBelow(Monster mob, Point pos) {
        Point spos = getGroundBelow(pos);
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    public Point getGroundBelow(Point pos) {
        Point spos = new Point(pos.x, pos.y - 14); // Using -14 fixes spawning pets causing a lot of issues.
        spos = calcPointBelow(spos);
        spos.y--;//shouldn't be null!
        return spos;
    }

    public Point getPointBelow(Point pos) {
        return calcPointBelow(pos);
    }

    public void spawnRevives(final Monster monster) {
        monster.setMap(this);
        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnMonster(monster, false)));

        monster.aggroUpdateController();
        updateBossSpawn(monster);

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
        applyRemoveAfter(monster);
    }

    private void applyRemoveAfter(final Monster monster) {
        final selfDestruction selfDestruction = monster.getStats().selfDestruction();
        if (monster.getStats().removeAfter() > 0 || selfDestruction != null && selfDestruction.getHp() < 0) {
            Runnable removeAfterAction;

            if (selfDestruction == null) {
                removeAfterAction = () -> killMonster(monster, null, false);

                registerMapSchedule(removeAfterAction, SECONDS.toMillis(monster.getStats().removeAfter()));
            } else {
                removeAfterAction = () -> killMonster(monster, null, false, selfDestruction.getAction());

                registerMapSchedule(removeAfterAction, SECONDS.toMillis(selfDestruction.removeAfter()));
            }

            monster.pushRemoveAfterAction(removeAfterAction);
        }
    }

    public void dismissRemoveAfter(final Monster monster) {
        Runnable removeAfterAction = monster.popRemoveAfterAction();
        if (removeAfterAction != null) {
            OverallService service = (OverallService) this.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
            service.forceRunOverallAction(mapid, removeAfterAction);
        }
    }

    private List<SpawnPoint> getMonsterSpawn() {
        synchronized (monsterSpawn) {
            return new ArrayList<>(monsterSpawn);
        }
    }

    private List<SpawnPoint> getAllMonsterSpawn() {
        synchronized (allMonsterSpawn) {
            return new ArrayList<>(allMonsterSpawn);
        }
    }

    public void spawnAllMonsterIdFromMapSpawnList(int id) {
        spawnAllMonsterIdFromMapSpawnList(id, 1, false);
    }

    public void spawnAllMonsterIdFromMapSpawnList(int id, int difficulty, boolean isPq) {
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            if (sp.getMonsterId() == id && sp.shouldForceSpawn()) {
                spawnMonster(sp.getMonster(), difficulty, isPq);
            }
        }
    }

    public void spawnAllMonstersFromMapSpawnList() {
        spawnAllMonstersFromMapSpawnList(1, false);
    }

    public void spawnAllMonstersFromMapSpawnList(int difficulty, boolean isPq) {
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            spawnMonster(sp.getMonster(), difficulty, isPq);
        }
    }

    /**
     * 在地图上生成怪物（使用默认难度）
     * 
     * <p>将指定的怪物对象添加到当前地图中，设置怪物的基本属性，
     * 并向地图上的玩家广播怪物生成消息。此方法使用默认难度（1）和非组队任务模式。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>将怪物添加到地图对象集合</li>
     *   <li>向地图上的玩家广播怪物生成数据</li>
     *   <li>更新怪物的仇恨目标控制器</li>
     *   <li>处理BOSS怪物的特殊逻辑</li>
     *   <li>应用怪物的自毁机制</li>
     * </ul>
     * 
     * @param monster 要生成的怪物对象
     */
    public void spawnMonster(final Monster monster) {
        spawnMonster(monster, 1, false);
    }

    /**
     * 在地图上生成怪物（指定难度和是否为组队任务）
     * 
     * <p>将指定的怪物对象添加到当前地图中，根据指定的难度和组队任务标志调整怪物属性，
     * 并向地图上的玩家广播怪物生成消息。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>检查地图怪物容量限制</li>
     *   <li>调整怪物的难度属性</li>
     *   <li>应用BOSS配置（HP/EXP/伤害倍率）</li>
     *   <li>将怪物添加到地图对象集合</li>
     *   <li>向地图上的玩家广播怪物生成数据</li>
     *   <li>更新怪物的仇恨目标控制器</li>
     *   <li>处理CPQ（怪物嘉年华）相关逻辑</li>
     *   <li>处理定时掉落逻辑</li>
     *   <li>更新怪物计数并应用自毁机制</li>
     * </ul>
     * 
     * @param monster 要生成的怪物对象
     * @param difficulty 怪物难度系数
     * @param isPq 是否为组队任务怪物
     */
    public void spawnMonster(final Monster monster, int difficulty, boolean isPq) {
        if (mobCapacity != -1 && mobCapacity == spawnedMonstersOnMap.get()) {
            return;//PyPQ
        }

        monster.changeDifficulty(difficulty, isPq);

        // 应用BOSS属性倍率配置（HP/EXP/伤害）
        BossConfigManager.applyBossConfig(monster);

        monster.setMap(this);
        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnMonster(monster, true)), null);

        monster.aggroUpdateController();
        updateBossSpawn(monster);

        if ((monster.getTeam() == 1 || monster.getTeam() == 0) && (isCPQMap() || isCPQMap2())) {
            List<MCSkill> teamS = null;
            if (monster.getTeam() == 0) {
                teamS = redTeamBuffs;
            } else if (monster.getTeam() == 1) {
                teamS = blueTeamBuffs;
            }
            if (teamS != null) {
                for (MCSkill skil : teamS) {
                    if (skil != null) {
                        skil.getSkill().applyEffect(null, monster, false, null);
                    }
                }
            }
        }

        if (monster.getDropPeriodTime() > 0) { //9300102 - Watchhog, 9300061 - Moon Bunny (HPQ), 9300093 - Tylus    //9300102-护卫用小浣猪，9300061-月妙（HPQ），9300093-冒牌泰勒斯
            if (monster.getId() == MobId.WATCH_HOG) {
                monsterItemDrop(monster, monster.getDropPeriodTime());
            } else if (monster.getId() == MobId.MOON_BUNNY) {
                monsterItemDrop(monster, monster.getDropPeriodTime() / 3);
            } else if (monster.getId() == MobId.TYLUS) {
                monsterItemDrop(monster, monster.getDropPeriodTime());
            } else if (monster.getId() == MobId.GIANT_SNOWMAN_LV5_EASY || monster.getId() == MobId.GIANT_SNOWMAN_LV5_MEDIUM || monster.getId() == MobId.GIANT_SNOWMAN_LV5_HARD) {
                monsterItemDrop(monster, monster.getDropPeriodTime());
            } else {
                log.error("[异常刷怪] 检测到未配置定时刷新的怪物: ID={}", monster.getId());
            }
        }

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
        applyRemoveAfter(monster);  // thanks LightRyuzaki for pointing issues with spawned CWKPQ mobs not applying this
    }

    public void spawnDojoMonster(final Monster monster) {
        Point[] pts = {new Point(140, 0), new Point(190, 7), new Point(187, 7)};
        spawnMonsterWithEffect(monster, 15, pts[Randomizer.nextInt(3)]);
    }

    public void spawnMonsterWithEffect(final Monster monster, final int effect, Point pos) {
        monster.setMap(this);
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        if (spos == null) {
            return;
        }

        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spos.y--;
        monster.setPosition(spos);
        monster.setSpawnEffect(effect);

        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnMonster(monster, true, effect)));

        monster.aggroUpdateController();
        updateBossSpawn(monster);

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
        applyRemoveAfter(monster);
    }

    public void spawnFakeMonster(final Monster monster) {
        monster.setMap(this);
        monster.setFake(true);
        spawnAndAddRangedMapObject(monster, c -> c.sendPacket(PacketCreator.spawnFakeMonster(monster, 0)));

        spawnedMonstersOnMap.incrementAndGet();
        addSelfDestructive(monster);
    }

    public void makeMonsterReal(final Monster monster) {
        monster.setFake(false);
        broadcastMessage(PacketCreator.makeMonsterReal(monster));
        monster.aggroUpdateController();
        updateBossSpawn(monster);
    }

    /**
     * 在地图上生成反应器
     * 
     * <p>将指定的反应器对象添加到当前地图中，并向地图上的玩家广播反应器生成消息。
     * 反应器是地图上可以被玩家交互的动态对象，例如可破坏的罐子、开关等。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>设置反应器的地图引用</li>
     *   <li>将反应器添加到地图对象集合</li>
     *   <li>向地图上的玩家广播反应器生成数据</li>
     * </ul>
     * 
     * @param reactor 要生成的反应器对象
     */
    public void spawnReactor(final Reactor reactor) {
        reactor.setMap(this);
        spawnAndAddRangedMapObject(reactor, c -> c.sendPacket(reactor.makeSpawnData()));
    }

    /**
     * 在地图上生成门对象
     * 
     * <p>将指定的门对象添加到当前地图中，并向地图上的玩家广播门的生成消息。
     * 门对象通常是技能产生的临时障碍物，只对特定地图的玩家可见。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>将门对象添加到地图对象集合</li>
     *   <li>向指定范围内的玩家广播门的生成数据</li>
     *   <li>确保只有来自相同地图的玩家才能看到门</li>
     *   <li>将门添加到玩家的可见对象列表中</li>
     * </ul>
     * 
     * @param door 要生成的门对象
     */
    public void spawnDoor(final DoorObject door) {
        spawnAndAddRangedMapObject(door, c -> {
            Character chr = c.getPlayer();
            if (chr != null) {
                door.sendSpawnData(c, false);
                chr.addVisibleMapObject(door);
            }
        }, chr -> chr.getMapId() == door.getFrom().getId());
    }

    public Portal getDoorPortal(int doorid) {
        Portal doorPortal = portals.get(0x80 + doorid);
        if (doorPortal == null) {
            log.warn("[传动点] 地图 {} (ID:{}) 不存在传送门ID为 {} 的入口", mapName, mapid, doorid);
            return portals.get(0x80);
        }

        return doorPortal;
    }

    /**
     * 在地图上生成召唤兽
     * 
     * <p>将指定的召唤兽对象添加到当前地图中，并向地图上的玩家广播召唤兽生成消息。
     * 召唤兽是玩家技能产生的临时伙伴，具有特定的行为模式和持续时间。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>将召唤兽添加到地图对象集合</li>
     *   <li>向地图上的玩家广播召唤兽的生成数据</li>
     *   <li>使召唤兽对玩家可见</li>
     * </ul>
     * 
     * @param summon 要生成的召唤兽对象
     */
    public void spawnSummon(final Summon summon) {
        spawnAndAddRangedMapObject(summon, c -> c.sendPacket(PacketCreator.spawnSummon(summon, true)), null);
    }

    /**
     * 在地图上生成迷雾效果
     * 
     * <p>将指定的迷雾对象添加到当前地图中，用于实现毒雾、治疗雾等区域效果。
     * 迷雾是技能产生的区域效果，会对范围内的角色产生持续影响。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>将迷雾添加到地图对象集合</li>
     *   <li>向地图上的玩家广播迷雾的生成数据</li>
     *   <li>根据参数设置迷雾的类型（毒性、虚假、恢复等）</li>
     *   <li>启动相应的定时任务处理迷雾效果</li>
     * </ul>
     * 
     * @param mist 要生成的迷雾对象
     * @param duration 持续时间（毫秒）
     * @param poison 是否为毒性迷雾
     * @param fake 是否为虚假迷雾（不产生实际效果）
     * @param recovery 是否为恢复性迷雾
     */
    public void spawnMist(final Mist mist, final int duration, boolean poison, boolean fake, boolean recovery) {
        addMapObject(mist);
        broadcastMessage(fake ? mist.makeFakeSpawnData(30) : mist.makeSpawnData());
        TimerManager tMan = TimerManager.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = () -> {
                List<MapObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapObjectType.MONSTER));
                for (MapObject mo : affectedMonsters) {
                    if (mist.makeChanceResult()) {
                        MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), null, false);
                        ((Monster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else if (recovery) {
            Runnable poisonTask = () -> {
                List<MapObject> players = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapObjectType.PLAYER));
                for (MapObject mo : players) {
                    if (mist.makeChanceResult()) {
                        Character chr = (Character) mo;
                        if (mist.getOwner().getId() == chr.getId() || mist.getOwner().getParty() != null && mist.getOwner().getParty().containsMembers(chr.getMPC())) {
                            chr.addMP(mist.getSourceSkill().getEffect(chr.getSkillLevel(mist.getSourceSkill().getId())).getX() * chr.getMp() / 100);
                        }
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }

        Runnable mistSchedule = () -> {
            removeMapObject(mist);
            if (poisonSchedule != null) {
                poisonSchedule.cancel(false);
            }
            broadcastMessage(mist.makeDestroyData());
        };

        MobMistService service = (MobMistService) this.getChannelServer().getServiceAccess(ChannelServices.MOB_MIST);
        service.registerMobMistCancelAction(mapid, mistSchedule, duration);
    }

    /**
     * 在地图上生成风筝
     * 
     * <p>将指定的风筝对象添加到当前地图中，并向地图上的玩家广播风筝生成消息。
     * 风筝是弓箭手技能产生的特殊对象，会在一段时间后自动消失。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>将风筝添加到地图对象集合</li>
     *   <li>向地图上的玩家广播风筝的生成数据</li>
     *   <li>注册定时任务在指定时间后移除风筝</li>
     *   <li>向玩家广播风筝的销毁数据</li>
     * </ul>
     * 
     * @param kite 要生成的风筝对象
     */
    public void spawnKite(final Kite kite) {
        addMapObject(kite);
        broadcastMessage(kite.makeSpawnData());

        Runnable expireKite = () -> {
            removeMapObject(kite);
            broadcastMessage(kite.makeDestroyData());
        };

        getWorldServer().registerTimedMapObject(expireKite, GameConfig.getServerLong("kite_expire_time"));
    }

    public final void spawnItemDrop(final MapObject dropper, final Character owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        spawnItemDrop(dropper, owner, item, pos, (byte) (ffaDrop ? 2 : 0), playerDrop);
    }

    public final void spawnItemDrop(final MapObject dropper, final Character owner, final Item item, Point pos, final byte dropType, final boolean playerDrop) {
        if (FieldLimit.DROP_LIMIT.check(this.getFieldLimit())) { // thanks Conrad for noticing some maps shouldn't have loots available
            this.disappearingItemDrop(dropper, owner, item, pos);
            return;
        }

        final Point droppos = calcDropPos(pos, pos);
        final MapItem mdrop = new MapItem(item, droppos, dropper, owner, owner.getClient(), dropType, playerDrop);
        mdrop.setDropTime(Server.getInstance().getCurrentTime());

        spawnAndAddRangedMapObject(mdrop, c -> {
            mdrop.lockItem();
            try {
                c.sendPacket(PacketCreator.dropItemFromMapObject(c.getPlayer(), mdrop, dropper.getPosition(), droppos, (byte) 1));
            } finally {
                mdrop.unlockItem();
            }
        }, null);

        mdrop.lockItem();
        try {
            broadcastItemDropMessage(mdrop, dropper.getPosition(), droppos, (byte) 0);
        } finally {
            mdrop.unlockItem();
        }

        instantiateItemDrop(mdrop);
        activateItemReactors(mdrop, owner.getClient());
    }

    public final void spawnItemDropList(List<Integer> list, final MapObject dropper, final Character owner, Point pos) {
        spawnItemDropList(list, 1, 1, dropper, owner, pos, true, false);
    }

    public final void spawnItemDropList(List<Integer> list, int minCopies, int maxCopies, final MapObject dropper, final Character owner, Point pos) {
        spawnItemDropList(list, minCopies, maxCopies, dropper, owner, pos, true, false);
    }

    // spawns item instances of all defined item ids on a list
    public final void spawnItemDropList(List<Integer> list, int minCopies, int maxCopies, final MapObject dropper, final Character owner, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        int copies = (maxCopies - minCopies) + 1;
        if (copies < 1) {
            return;
        }

        Collections.shuffle(list);

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Random rnd = new Random();

        final Point dropPos = new Point(pos);
        dropPos.x -= (12 * list.size());

        for (Integer integer : list) {
            if (integer == 0) {
                spawnMesoDrop(owner != null ? NumberTool.floatToInt(10 * owner.getMesoRate()) : 10, calcDropPos(dropPos, pos), dropper, owner, playerDrop, (byte) (ffaDrop ? 2 : 0));
            } else {
                final Item drop;
                int randomedId = integer;

                if (ItemConstants.getInventoryType(randomedId) != InventoryType.EQUIP) {
                    drop = new Item(randomedId, (short) 0, (short) (rnd.nextInt(copies) + minCopies));
                } else {
                    drop = ii.randomizeStats((Equip) ii.getEquipById(randomedId));
                }

                spawnItemDrop(dropper, owner, drop, calcDropPos(dropPos, pos), ffaDrop, playerDrop);
            }

            dropPos.x += 25;
        }
    }

    private void registerMapSchedule(Runnable r, long delay) {
        OverallService service = (OverallService) this.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        service.registerOverallAction(mapid, r, delay);
    }

    private void activateItemReactors(final MapItem drop, final Client c) {
        final Item item = drop.getItem();

        for (final MapObject o : getReactors()) {
            final Reactor react = (Reactor) o;

            if (react.getReactorType() == 100) {
                if (react.getReactItem(react.getEventState()).getLeft() == item.getItemId() && react.getReactItem(react.getEventState()).getRight() == item.getQuantity()) {

                    if (react.getArea().contains(drop.getPosition())) {
                        registerMapSchedule(new ActivateItemReactor(drop, react, c), 5000);
                        break;
                    }
                }
            }
        }
    }

    public void searchItemReactors(final Reactor react) {
        if (react.getReactorType() == 100) {
            Pair<Integer, Integer> reactProp = react.getReactItem(react.getEventState());
            int reactItem = reactProp.getLeft(), reactQty = reactProp.getRight();
            Rectangle reactArea = react.getArea();

            List<MapItem> list;
            objectRLock.lock();
            try {
                list = new ArrayList<>(droppedItems.keySet());
            } finally {
                objectRLock.unlock();
            }

            for (final MapItem drop : list) {
                drop.lockItem();
                try {
                    if (!drop.isPickedUp()) {
                        final Item item = drop.getItem();

                        if (item != null && reactItem == item.getItemId() && reactQty == item.getQuantity()) {
                            if (reactArea.contains(drop.getPosition())) {
                                Client owner = drop.getOwnerClient();
                                if (owner != null) {
                                    registerMapSchedule(new ActivateItemReactor(drop, react, owner), 5000);
                                }
                            }
                        }
                    }
                } finally {
                    drop.unlockItem();
                }
            }
        }
    }

    public void changeEnvironment(String mapObj, int newState) {
        broadcastMessage(PacketCreator.environmentChange(mapObj, newState));
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 30000);
    }

    public void startMapEffect(String msg, int itemId, long time) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());

        Runnable r = () -> {
            broadcastMessage(mapEffect.makeDestroyData());
            mapEffect = null;
        };

        registerMapSchedule(r, time);
    }

    public Character getAnyCharacterFromParty(int partyid) {
        for (Character chr : this.getAllPlayers()) {
            if (chr.getPartyId() == partyid) {
                return chr;
            }
        }

        return null;
    }

    private void addPartyMemberInternal(Character chr, int partyid) {
        if (partyid == -1) {
            return;
        }

        Set<Integer> partyEntry = mapParty.get(partyid);
        if (partyEntry == null) {
            partyEntry = new LinkedHashSet<>();
            partyEntry.add(chr.getId());

            mapParty.put(partyid, partyEntry);
        } else {
            partyEntry.add(chr.getId());
        }
    }

    private void removePartyMemberInternal(Character chr, int partyid) {
        if (partyid == -1) {
            return;
        }

        Set<Integer> partyEntry = mapParty.get(partyid);
        if (partyEntry != null) {
            if (partyEntry.size() > 1) {
                partyEntry.remove(chr.getId());
            } else {
                mapParty.remove(partyid);
            }
        }
    }

    public void addPartyMember(Character chr, int partyid) {
        chrWLock.lock();
        try {
            addPartyMemberInternal(chr, partyid);
        } finally {
            chrWLock.unlock();
        }
    }

    public void removePartyMember(Character chr, int partyid) {
        chrWLock.lock();
        try {
            removePartyMemberInternal(chr, partyid);
        } finally {
            chrWLock.unlock();
        }
    }

    public void removeParty(int partyid) {
        chrWLock.lock();
        try {
            mapParty.remove(partyid);
        } finally {
            chrWLock.unlock();
        }
    }

    /**
     * 添加玩家到地图
     * 
     * <p>将指定的角色添加到当前地图中，执行必要的初始化操作，
     * 包括队伍管理、效果更新、脚本执行、定时器启动等。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>将玩家添加到地图的角色集合中</li>
     *   <li>处理队伍成员关系</li>
     *   <li>更新玩家的地图ID和激活效果</li>
     *   <li>根据地图设置启动或停止HP减少效果</li>
     *   <li>执行地图进入脚本（首次进入或每次进入）</li>
     *   <li>处理地图限制（如坐骑限制）</li>
     *   <li>启动定时任务（如旅行计时器）</li>
     * </ul>
     * 
     * <p>重要流程：</p>
     * <ol>
     *   <li>将玩家添加到地图角色集合</li>
     *   <li>处理队伍相关逻辑</li>
     *   <li>检查是否为地图第一个玩家并执行首次进入脚本</li>
     *   <li>处理宠物显示</li>
     *   <li>处理隐藏状态玩家的特殊显示</li>
     *   <li>向地图其他玩家广播新玩家进入</li>
     *   <li>发送现有地图对象给新玩家</li>
     * </ol>
     * 
     * @param chr 要添加到地图的角色
     */
    public void addPlayer(final Character chr) {
        int chrSize;
        Party party = chr.getParty();
        chrWLock.lock();
        try {
            characters.add(chr);
            chrSize = characters.size();

            if (party != null && party.getMemberById(chr.getId()) != null) {
                addPartyMemberInternal(chr, party.getId());
            }
            itemMonitorTimeout = 1;
        } finally {
            chrWLock.unlock();
        }

        chr.setMapId(mapid);
        chr.updateActiveEffects();

        if (this.getHPDec() > 0) {
            getWorldServer().addPlayerHpDecrease(chr);
        } else {
            getWorldServer().removePlayerHpDecrease(chr);
        }

        MapScriptManager msm = MapScriptManager.getInstance();
        if (chrSize == 1) {
            if (!hasItemMonitor()) {
                startItemMonitor();
                aggroMonitor.startAggroCoordinator();
            }

            if (onFirstUserEnter.length() != 0) {
                msm.runMapScript(chr.getClient(), "onFirstUserEnter/" + onFirstUserEnter, true);
            }
        }
        if (onUserEnter.length() != 0) {
            if (onUserEnter.equals("cygnusTest") && !MapId.isCygnusIntro(mapid)) {
                chr.saveLocation("INTRO");
            }

            msm.runMapScript(chr.getClient(), "onUserEnter/" + onUserEnter, false);
        }
        if (FieldLimit.CANNOTUSEMOUNTS.check(fieldLimit) && chr.getBuffedValue(BuffStat.MONSTER_RIDING) != null) {
            chr.cancelEffectFromBuffStat(BuffStat.MONSTER_RIDING);
            chr.cancelBuffStats(BuffStat.MONSTER_RIDING);
        }

        if (mapid == MapId.FROM_LITH_TO_RIEN) { // To Rien
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(1));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_LITH_TO_RIEN) {
                    chr.changeMap(MapId.DANGEROUS_FOREST, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_RIEN_TO_LITH) { // To Lith Harbor
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(1));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_RIEN_TO_LITH) {
                    chr.changeMap(MapId.LITH_HARBOUR, 3);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_ELLINIA_TO_EREVE) { // To Ereve (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(2));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_ELLINIA_TO_EREVE) {
                    chr.changeMap(MapId.SKY_FERRY, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_EREVE_TO_ELLINIA) { // To Victoria Island (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(2));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_EREVE_TO_ELLINIA) {
                    chr.changeMap(MapId.ELLINIA_SKY_FERRY, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_EREVE_TO_ORBIS) { // To Orbis (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(8));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_EREVE_TO_ORBIS) {
                    chr.changeMap(MapId.ORBIS_STATION, 0);
                }
            }, travelTime);
        } else if (mapid == MapId.FROM_ORBIS_TO_EREVE) { // To Ereve From Orbis (SkyFerry)
            int travelTime = getWorldServer().getTransportationTime((int) MINUTES.toMillis(8));
            chr.sendPacket(PacketCreator.getClock(travelTime / 1000));
            TimerManager.getInstance().schedule(() -> {
                if (chr.getMapId() == MapId.FROM_ORBIS_TO_EREVE) {
                    chr.changeMap(MapId.SKY_FERRY, 0);
                }
            }, travelTime);
        } else if (MiniDungeonInfo.isDungeonMap(mapid)) {
            MiniDungeon mmd = chr.getClient().getChannelServer().getMiniDungeon(mapid);
            if (mmd != null) {
                mmd.registerPlayer(chr);
            }
        } else if (GameConstants.isAriantColiseumArena(mapid)) {
            int pqTimer = (int) MINUTES.toMillis(10);
            chr.sendPacket(PacketCreator.getClock(pqTimer / 1000));
        }

        Pet[] pets = chr.getPets();
        for (Pet pet : pets) {
            if (pet != null) {
                pet.setPos(getGroundBelow(chr.getPosition()));
                chr.sendPacket(PacketCreator.showPet(chr, pet, false, false));
            } else {
                break;
            }
        }
        chr.commitExcludedItems();  // thanks OishiiKawaiiDesu for noticing pet item ignore registry erasing upon changing maps

        if (chr.getMonsterCarnival() != null) {
            chr.sendPacket(PacketCreator.getClock(chr.getMonsterCarnival().getTimeLeftSeconds()));
            if (isCPQMap()) {
                int team = -1;
                int oposition = -1;
                if (chr.getTeam() == 0) {
                    team = 0;
                    oposition = 1;
                }
                if (chr.getTeam() == 1) {
                    team = 1;
                    oposition = 0;
                }
                chr.sendPacket(PacketCreator.startMonsterCarnival(chr, team, oposition));
            }
        }

        chr.removeSandboxItems();

        if (chr.getChalkboard() != null) {
            if (!GameConstants.isFreeMarketRoom(mapid)) {
                chr.sendPacket(PacketCreator.useChalkboard(chr, false)); // update player's chalkboard when changing maps found thanks to Vcoc
            } else {
                chr.setChalkboard(null);
            }
        }

        if (chr.isHidden()) {
            broadcastGMSpawnPlayerMapObjectMessage(chr, chr, true);
            chr.sendPacket(PacketCreator.getGMEffect(0x10, (byte) 1));

            List<Pair<BuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(BuffStat.DARKSIGHT, 0));
            broadcastGMMessage(chr, PacketCreator.giveForeignBuff(chr.getId(), dsstat), false);
        } else {
            broadcastSpawnPlayerMapObjectMessage(chr, chr, true);
        }

        sendObjectPlacement(chr.getClient());

        if (isStartingEventMap() && !eventStarted()) {
            chr.getMap().getPortal("join00").setPortalStatus(false);
        }
        if (hasForcedEquip()) {
            chr.sendPacket(PacketCreator.showForcedEquip(-1));
        }
        if (specialEquip()) {
            chr.sendPacket(PacketCreator.coconutScore(0, 0));
            chr.sendPacket(PacketCreator.showForcedEquip(chr.getTeam()));
        }
        objectWLock.lock();
        try {
            this.mapobjects.put(chr.getObjectId(), chr);
        } finally {
            objectWLock.unlock();
        }

        if (chr.getPlayerShop() != null) {
            addMapObject(chr.getPlayerShop());
        }

        final Dragon dragon = chr.getDragon();
        if (dragon != null) {
            dragon.setPosition(chr.getPosition());
            this.addMapObject(dragon);
            if (chr.isHidden()) {
                this.broadcastGMPacket(chr, PacketCreator.spawnDragon(dragon));
            } else {
                this.broadcastPacket(chr, PacketCreator.spawnDragon(dragon));
            }
        }

        StatEffect summonStat = chr.getStatForBuff(BuffStat.SUMMON);
        if (summonStat != null) {
            Summon summon = chr.getSummonByKey(summonStat.getSourceId());
            summon.setPosition(chr.getPosition());
            chr.getMap().spawnSummon(summon);
            updateMapObjectVisibility(chr, summon);
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        chr.sendPacket(PacketCreator.resetForcedStats());
        if (MapId.isGodlyStatMap(mapid)) {
            chr.sendPacket(PacketCreator.aranGodlyStats());
        }
        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
            chr.sendPacket(PacketCreator.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
        }
        if (chr.getFitness() != null && chr.getFitness().isTimerStarted()) {
            chr.sendPacket(PacketCreator.getClock((int) (chr.getFitness().getTimeLeft() / 1000)));
        }

        if (chr.getOla() != null && chr.getOla().isTimerStarted()) {
            chr.sendPacket(PacketCreator.getClock((int) (chr.getOla().getTimeLeft() / 1000)));
        }

        if (mapid == MapId.EVENT_SNOWBALL) {
            chr.sendPacket(PacketCreator.rollSnowBall(true, 0, null, null));
        }

        if (hasClock()) {
            Calendar cal = Calendar.getInstance();
            chr.sendPacket(PacketCreator.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));
        }
        if (hasBoat() > 0) {
            if (hasBoat() == 1) {
                chr.sendPacket((PacketCreator.boatPacket(true)));
            } else {
                chr.sendPacket(PacketCreator.boatPacket(false));
            }
        }

        chr.receivePartyMemberHP();
        announcePlayerDiseases(chr.getClient());
    }

    private static void announcePlayerDiseases(final Client c) {
        Server.getInstance().registerAnnouncePlayerDiseases(c);
    }

    public Portal getRandomPlayerSpawnpoint() {
        List<Portal> spawnPoints = new ArrayList<>();
        for (Portal portal : portals.values()) {
            if (portal.getType() >= 0 && portal.getType() <= 1 && portal.getTargetMapId() == MapId.NONE) {
                spawnPoints.add(portal);
            }
        }
        Portal portal = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        return portal != null ? portal : getPortal(0);
    }

    public Portal findClosestTeleportPortal(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() == Portal.TELEPORT_PORTAL && distance < shortestDistance && portal.getTargetMapId() != MapId.NONE) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public Portal findClosestPlayerSpawnpoint(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 1 && distance < shortestDistance && portal.getTargetMapId() == MapId.NONE) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public Portal findClosestPortal(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public Portal findMarketPortal() {
        for (Portal portal : portals.values()) {
            String ptScript = portal.getScriptName();
            if (ptScript != null && ptScript.contains("market")) {
                return portal;
            }
        }
        return null;
    }

    /*
    public Collection<Portal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }
    */

    public void addPlayerPuppet(Character player) {
        for (Monster mm : this.getAllMonsters()) {
            mm.aggroAddPuppet(player);
        }
    }

    public void removePlayerPuppet(Character player) {
        for (Monster mm : this.getAllMonsters()) {
            mm.aggroRemovePuppet(player);
        }
    }

    /**
     * 移除玩家从地图
     * 
     * <p>将指定的角色从当前地图中移除，执行必要的清理操作，
     * 包括队伍管理、召唤物处理、龙处理、广播消息等。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>从地图的角色集合中移除玩家</li>
     *   <li>处理队伍成员关系</li>
     *   <li>处理迷你地下城相关逻辑</li>
     *   <li>移除玩家相关的地图对象（召唤物、龙等）</li>
     *   <li>向地图上的其他玩家广播玩家离开消息</li>
     *   <li>清理玩家的椅子BUFF</li>
     * </ul>
     * 
     * <p>重要流程：</p>
     * <ol>
     *   <li>处理队伍相关逻辑</li>
     *   <li>从地图角色集合中移除玩家</li>
     *   <li>处理迷你地下城注销</li>
     *   <li>移除玩家对象</li>
     *   <li>广播玩家离开消息</li>
     *   <li>处理玩家召唤物</li>
     *   <li>处理玩家龙</li>
     * </ol>
     * 
     * @param chr 要从地图移除的角色
     */
    public void removePlayer(Character chr) {
        Channel cserv = chr.getClient().getChannelServer();
        chr.unregisterChairBuff();

        Party party = chr.getParty();
        chrWLock.lock();
        try {
            if (party != null && party.getMemberById(chr.getId()) != null) {
                removePartyMemberInternal(chr, party.getId());
            }

            characters.remove(chr);
        } finally {
            chrWLock.unlock();
        }

        if (MiniDungeonInfo.isDungeonMap(mapid)) {
            MiniDungeon mmd = cserv.getMiniDungeon(mapid);
            if (mmd != null) {
                if (!mmd.unregisterPlayer(chr)) {
                    cserv.removeMiniDungeon(mapid);
                }
            }
        }

        removeMapObject(chr.getObjectId());
        if (!chr.isHidden()) {
            broadcastMessage(PacketCreator.removePlayerFromMap(chr.getId()));
        } else {
            broadcastGMMessage(PacketCreator.removePlayerFromMap(chr.getId()));
        }

        chr.leaveMap();

        for (Summon summon : new ArrayList<>(chr.getSummonsValues())) {
            if (summon.isStationary()) {
                chr.cancelEffectFromBuffStat(BuffStat.PUPPET);
            } else {
                removeMapObject(summon);
            }
        }

        if (chr.getDragon() != null) {
            removeMapObject(chr.getDragon());
            if (chr.isHidden()) {
                this.broadcastGMPacket(chr, PacketCreator.removeDragon(chr.getId()));
            } else {
                this.broadcastPacket(chr, PacketCreator.removeDragon(chr.getId()));
            }
        }
    }

    /**
     * 无条件地将消息广播给所有玩家。
     *
     * <p>将指定的数据包广播给当前地图上的所有玩家，不受距离和角色权限限制。</p>
     *
     * <p>此方法是广播系统的入口点之一，会将消息发送给地图上的所有在线玩家，
     * 适用于需要向所有玩家同步状态或信息的场景。</p>
     *
     * @param packet 要广播的数据包
     */
    public void broadcastMessage(Packet packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * 无条件地将管理员消息广播给所有玩家。
     *
     * <p>将指定的数据包仅广播给当前地图上的管理员玩家，普通玩家不会接收到此消息。</p>
     *
     * <p>此方法用于向地图上的GM（游戏管理员）发送特殊信息，
     * 如调试信息、管理指令反馈等，普通玩家不会看到这些消息。</p>
     *
     * @param packet 要广播的数据包
     */
    public void broadcastGMMessage(Packet packet) {
        broadcastGMMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * 根据 repeatToSource 参数决定是否将消息重复发送给源角色，并无范围限制地广播消息。
     *
     * Broadcasts a message based on the repeatToSource parameter, repeating it to the source character if specified,
     * and broadcasts it without any range restrictions.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {boolean} repeatToSource - 是否重复发送给源角色。Whether to repeat the message to the source character.
     */
    public void broadcastMessage(Character source, Packet packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * 根据 repeatToSource 和 ranged 参数决定是否将消息重复发送给源角色以及是否限定在一定范围内广播消息。
     *
     * Broadcasts a message based on the repeatToSource and ranged parameters, repeating it to the source character if specified,
     * and broadcasting it within a certain range if ranged is true.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {boolean} repeatToSource - 是否重复发送给源角色。Whether to repeat the message to the source character.
     * @param {boolean} ranged - 是否限定在一定范围内广播消息。Whether to broadcast the message within a certain range.
     */
    public void broadcastMessage(Character source, Packet packet, boolean repeatToSource, boolean ranged) {
        broadcastMessage(repeatToSource ? null : source, packet, ranged ? getRangedDistance() : Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * 从指定点开始，在一定范围内广播消息。
     *
     * Broadcasts a message starting from a specified point within a certain range.
     *
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {Point} rangedFrom - 广播的起点位置。The starting point for broadcasting.
     */
    public void broadcastMessage(Packet packet, Point rangedFrom) {
        broadcastMessage(null, packet, getRangedDistance(), rangedFrom);
    }

    /**
     * 从指定点开始，在一定范围内广播消息，并且不向源角色发送消息。
     *
     * Broadcasts a message starting from a specified point within a certain range and does not send it to the source character.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {Point} rangedFrom - 广播的起点位置。The starting point for broadcasting.
     */
    public void broadcastMessage(Character source, Packet packet, Point rangedFrom) {
        broadcastMessage(source, packet, getRangedDistance(), rangedFrom);
    }

    /**
     * 核心广播方法，负责实际的消息分发工作。
     *
     * Core method responsible for actually dispatching the message.
     *
     * @param {Character} source - 消息的源角色。The source character of the message.
     * @param {Packet} packet - 要广播的数据包。The packet to be broadcasted.
     * @param {double} rangeSq - 广播的最大距离平方值。The maximum distance squared for broadcasting.
     * @param {Point} rangedFrom - 广播的起点位置。The starting point for broadcasting.
     */
    private void broadcastMessage(Character source, Packet packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.sendPacket(packet);
                        }
                    } else {
                        chr.sendPacket(packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    private boolean chrDisconnected(Iterator<Character> iterator, Character chr) {
        // 如果玩家已经掉线，则移除地图该玩家，但不确保频道、大区该玩家是否仍会引发异常
        if (chr == null || chr.getClient() == null) {
            iterator.remove();
            return true;
        }
        return false;
    }

    private void updateBossSpawn(Monster monster) {
        if (monster.hasBossHPBar()) {
            broadcastBossHpMessage(monster, monster.hashCode(), monster.makeBossHPBarPacket(), monster.getPosition());
        }
        if (monster.isBoss()) {
            if (unclaimOwnership() != null) {
                String mobName = MonsterInformationProvider.getInstance().getMobNameFromId(monster.getId());
                if (mobName != null) {
                    mobName = mobName.trim();
                    this.dropMessage(5, "这片草坪已被" + mobName + "的部队占领，击败他们才能夺回控制权！");
                }
            }
        }
    }

    public void broadcastBossHpMessage(Monster mm, int bossHash, Packet packet) {
        broadcastBossHpMessage(mm, bossHash, null, packet, Double.POSITIVE_INFINITY, null);
    }

    public void broadcastBossHpMessage(Monster mm, int bossHash, Packet packet, Point rangedFrom) {
        broadcastBossHpMessage(mm, bossHash, null, packet, getRangedDistance(), rangedFrom);
    }

    private void broadcastBossHpMessage(Monster mm, int bossHash, Character source, Packet packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            for (Character chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.getClient().announceBossHpBar(mm, bossHash, packet);
                        }
                    } else {
                        chr.getClient().announceBossHpBar(mm, bossHash, packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    private void broadcastItemDropMessage(MapItem mdrop, Point dropperPos, Point dropPos, byte mod, Point rangedFrom) {
        broadcastItemDropMessage(mdrop, dropperPos, dropPos, mod, getRangedDistance(), rangedFrom);
    }

    private void broadcastItemDropMessage(MapItem mdrop, Point dropperPos, Point dropPos, byte mod) {
        broadcastItemDropMessage(mdrop, dropperPos, dropPos, mod, Double.POSITIVE_INFINITY, null);
    }

    private void broadcastItemDropMessage(MapItem mdrop, Point dropperPos, Point dropPos, byte mod, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                Packet packet = PacketCreator.dropItemFromMapObject(chr, mdrop, dropperPos, dropPos, mod);

                if (rangeSq < Double.POSITIVE_INFINITY) {
                    if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                        chr.sendPacket(packet);
                    }
                } else {
                    chr.sendPacket(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void broadcastSpawnPlayerMapObjectMessage(Character source, Character player, boolean enteringField) {
        broadcastSpawnPlayerMapObjectMessage(source, player, enteringField, false);
    }

    public void broadcastGMSpawnPlayerMapObjectMessage(Character source, Character player, boolean enteringField) {
        broadcastSpawnPlayerMapObjectMessage(source, player, enteringField, true);
    }

    private void broadcastSpawnPlayerMapObjectMessage(Character source, Character player, boolean enteringField, boolean gmBroadcast) {
        chrRLock.lock();
        try {
            if (gmBroadcast) {
                Iterator<Character> iterator = characters.iterator();
                while (iterator.hasNext()) {
                    Character chr = iterator.next();
                    if (chrDisconnected(iterator, chr)) {
                        continue;
                    }
                    if (chr.isGM()) {
                        if (chr != source) {
                            chr.sendPacket(PacketCreator.spawnPlayerMapObject(chr.getClient(), player, enteringField));
                        }
                    }
                }
            } else {
                Iterator<Character> iterator = characters.iterator();
                while (iterator.hasNext()) {
                    Character chr = iterator.next();
                    if (chrDisconnected(iterator, chr)) {
                        continue;
                    }
                    if (chr != source) {
                        chr.sendPacket(PacketCreator.spawnPlayerMapObject(chr.getClient(), player, enteringField));
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void broadcastUpdateCharLookMessage(Character source, Character player) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source) {
                    chr.sendPacket(PacketCreator.updateCharLook(chr.getClient(), player));
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void dropMessage(int type, String message) {
        broadcastStringMessage(type, message);
    }

    public void broadcastStringMessage(int type, String message) {
        broadcastMessage(PacketCreator.serverNotice(type, message));
    }

    /**
     * 判断地图对象类型是否为近程类型
     * <p>近程类型的对象在广播消息时需要考虑距离限制</p>
     * @param type 地图对象类型
     * @return true 如果是近程类型
     */
    private static boolean isNonRangedType(MapObjectType type) {
        // NPC、玩家、雇佣商店、玩家创建的NPC、龙、迷雾、风筝属于近程类型
        // 这些对象在广播时需要计算与接收者的距离
        switch (type) {
            case NPC:
            case PLAYER:
            case HIRED_MERCHANT:
            case PLAYER_NPC:
            case DRAGON:
            case MIST:
            case KITE:
                return true;
            default:
                return false;
        }
    }

    private void sendObjectPlacement(Client c) {
        Character chr = c.getPlayer();
        Collection<MapObject> objects;

        objectRLock.lock();
        try {
            objects = new ArrayList<>(mapobjects.values());
        } finally {
            objectRLock.unlock();
        }

        for (MapObject o : objects) {
            if (isNonRangedType(o.getType())) {
                o.sendSpawnData(c);
            } else if (o.getType() == MapObjectType.SUMMON) {
                Summon summon = (Summon) o;
                if (summon.getOwner() == chr) {
                    if (chr.isSummonsEmpty() || !chr.containsSummon(summon)) {
                        objectWLock.lock();
                        try {
                            mapobjects.remove(o.getObjectId());
                        } finally {
                            objectWLock.unlock();
                        }

                        //continue;
                    }
                }
            }
        }

        if (chr != null) {
            for (MapObject o : getMapObjectsInRange(chr.getPosition(), getRangedDistance(), rangedMapobjectTypes)) {
                if (o.getType() == MapObjectType.REACTOR) {
                    if (((Reactor) o).isAlive()) {
                        o.sendSpawnData(chr.getClient());
                        chr.addVisibleMapObject(o);
                    }
                } else {
                    o.sendSpawnData(chr.getClient());
                    chr.addVisibleMapObject(o);

                    if (o.getType() == MapObjectType.MONSTER) {
                        ((Monster) o).aggroUpdateController();
                    }
                }
            }
        }
    }

    /**
     * 获取指定范围内的地图对象
     * 
     * <p>根据给定的位置点和距离平方值，在地图中查找指定类型的对象。
     * 此方法用于检测玩家周围一定范围内的可交互对象，如怪物、物品、反应堆等。</p>
     * 
     * @param from 起始位置点
     * @param rangeSq 距离的平方值（避免开方运算提高性能）
     * @param types 要查找的对象类型列表
     * @return 在指定范围内的地图对象列表
     */
    public List<MapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapObjectType> types) {
        List<MapObject> ret = new LinkedList<>();
        objectRLock.lock();
        try {
            for (MapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (from.distanceSq(l.getPosition()) <= rangeSq) {
                        ret.add(l);
                    }
                }
            }
            return ret;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 获取指定矩形区域内的地图对象
     * 
     * <p>在指定的矩形区域内查找特定类型的地图对象。
     * 此方法常用于范围技能检测或区域事件触发。</p>
     * 
     * @param box 检测的矩形区域
     * @param types 要查找的对象类型列表
     * @return 在指定矩形区域内的地图对象列表
     */
    public List<MapObject> getMapObjectsInBox(Rectangle box, List<MapObjectType> types) {
        List<MapObject> ret = new LinkedList<>();
        objectRLock.lock();
        try {
            for (MapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
            return ret;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * 添加传送门到地图
     * 
     * <p>将指定的传送门对象添加到地图的传送门集合中，使其可以在地图上使用。</p>
     * 
     * @param myPortal 要添加的传送门对象
     */
    public void addPortal(Portal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    /**
     * 根据名称获取传送门
     * 
     * <p>通过传送门的名称在地图中查找对应的传送门对象。
     * 如果找不到匹配的传送门，则返回null。</p>
     * 
     * @param portalname 传送门的名称
     * @return 对应名称的传送门对象，如果不存在则返回null
     */
    public Portal getPortal(String portalname) {
        for (Portal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    /**
     * 根据ID获取传送门
     * 
     * <p>通过传送门的ID直接从地图的传送门集合中获取对应的传送门对象。
     * 如果不存在对应ID的传送门，则返回null。</p>
     * 
     * @param portalid 传送门的ID
     * @return 对应ID的传送门对象，如果不存在则返回null
     */
    public Portal getPortal(int portalid) {
        return portals.get(portalid);
    }

    /**
     * 添加枫叶区域到地图
     * 
     * <p>将指定的矩形区域添加到地图的区域集合中。
     * 枫叶区域通常用于定义地图上的特殊区域，如安全区、战斗区等。</p>
     * 
     * @param rec 要添加的矩形区域
     */
    public void addMapleArea(Rectangle rec) {
        areas.add(rec);
    }

    /**
     * 获取地图的所有区域
     * 
     * <p>返回地图中所有定义的区域的副本列表。
     * 返回的是原始列表的副本，以防止外部代码直接修改内部区域数据。</p>
     * 
     * @return 地图中所有区域的副本列表
     */
    public List<Rectangle> getAreas() {
        return new ArrayList<>(areas);
    }

    /**
     * 根据索引获取指定区域
     * 
     * <p>通过索引从地图的区域列表中获取对应的矩形区域。
     * 索引从0开始，必须小于区域列表的大小。</p>
     * 
     * @param index 区域的索引
     * @return 指定索引处的矩形区域
     */
    public Rectangle getArea(int index) {
        return areas.get(index);
    }

    /**
     * 设置地图的落脚点树
     * 
     * <p>为地图设置落脚点树，该树用于处理角色在地图上的移动和跳跃逻辑。
     * 落脚点树包含了地图上的所有可行走平台和路径。</p>
     * 
     * @param footholds 地图的落脚点树
     */
    public void setFootholds(FootholdTree footholds) {
        this.footholds = footholds;
    }

    /**
     * 获取地图的落脚点树
     * 
     * <p>返回地图的落脚点树，该树包含了地图上的所有可行走平台和路径信息，
     * 用于角色移动、跳跃和碰撞检测。</p>
     * 
     * @return 地图的落脚点树
     */
    public FootholdTree getFootholds() {
        return footholds;
    }

    public void setMapPointBoundings(int px, int py, int h, int w) {
        mapArea.setBounds(px, py, w, h);
    }

    public void setMapLineBoundings(int vrTop, int vrBottom, int vrLeft, int vrRight) {
        mapArea.setBounds(vrLeft, vrTop, vrRight - vrLeft, vrBottom - vrTop);
    }

    public MonsterAggroCoordinator getAggroCoordinator() {
        return aggroMonitor;
    }

    /**
     * it's threadsafe, gtfo :D
     *
     * @param monster
     * @param mobTime
     */
    public void addMonsterSpawn(Monster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        monsterSpawn.add(sp);
        if (sp.shouldSpawn() || mobTime == -1) {// -1 does not respawn and should not either but force ONE spawn
            spawnMonster(sp.getMonster());
        }
    }

    public void addAllMonsterSpawn(Monster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        allMonsterSpawn.add(sp);
    }

    public void removeMonsterSpawn(int mobId, int x, int y) {
        // assumption: spawn points identifies by tuple (lifeid, x, y)

        Point checkpos = calcPointBelow(new Point(x, y));
        checkpos.y -= 1;

        List<SpawnPoint> toRemove = new LinkedList<>();
        for (SpawnPoint sp : getMonsterSpawn()) {
            Point pos = sp.getPosition();
            if (sp.getMonsterId() == mobId && checkpos.equals(pos)) {
                toRemove.add(sp);
            }
        }

        if (!toRemove.isEmpty()) {
            synchronized (monsterSpawn) {
                for (SpawnPoint sp : toRemove) {
                    monsterSpawn.remove(sp);
                }
            }
        }
    }

    public void removeAllMonsterSpawn(int mobId, int x, int y) {
        // assumption: spawn points identifies by tuple (lifeid, x, y)

        Point checkpos = calcPointBelow(new Point(x, y));
        checkpos.y -= 1;

        List<SpawnPoint> toRemove = new LinkedList<>();
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            Point pos = sp.getPosition();
            if (sp.getMonsterId() == mobId && checkpos.equals(pos)) {
                toRemove.add(sp);
            }
        }

        if (!toRemove.isEmpty()) {
            synchronized (allMonsterSpawn) {
                for (SpawnPoint sp : toRemove) {
                    allMonsterSpawn.remove(sp);
                }
            }
        }
    }

    public void reportMonsterSpawnPoints(Character chr) {
        // 输出地图刷怪点统计信息头
        chr.dropMessage(6, "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        chr.dropMessage(6, "┃ 地图ID: " + getId() + " | 总刷怪点: " + monsterSpawn.size() +  " | 已刷怪: " + spawnedMonstersOnMap.get());
        chr.dropMessage(6, "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 遍历所有刷怪点输出详细信息
        for (SpawnPoint sp : getAllMonsterSpawn()) {
            chr.dropMessage(6,
                    "┃ ID:" + sp.getMonsterId() + " | 可刷怪:" + (sp.getDenySpawn() ? "×" : "√") + " | 现存:" + sp.getSpawned() + "\n" +
                    "┃ 坐标:(" +(int) sp.getPosition().getX() + " , " + (int) sp.getPosition().getY() + ") | 刷新:" + sp.getMobTime() + "ms | 阵营:" + sp.getTeam()
            );
        }
        chr.dropMessage(6, "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public Map<Integer, Character> getMapPlayers() {
        chrRLock.lock();
        try {
            Map<Integer, Character> mapChars = new HashMap<>(characters.size());

            for (Character chr : characters) {
                mapChars.put(chr.getId(), chr);
            }

            return mapChars;
        } finally {
            chrRLock.unlock();
        }
    }

    /**
     * 获取当前地图上的所有角色集合
     * 
     * <p>返回当前地图上所有在线角色的不可修改集合视图。此方法使用读锁确保
     * 在获取角色集合时不会有并发修改，保证返回数据的一致性。</p>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>返回的是不可修改的集合视图，任何试图修改集合的操作都会抛出异常</li>
     *   <li>使用读锁保护，允许多个线程同时读取角色列表</li>
     *   <li>适用于需要遍历地图上所有角色的场景（如广播消息、统计信息等）</li>
     * </ul>
     * 
     * @return 当前地图上的所有角色集合（不可修改）
     */
    public Collection<Character> getCharacters() {
        chrRLock.lock();
        try {
            return Collections.unmodifiableCollection(this.characters);
        } finally {
            chrRLock.unlock();
        }
    }

    public Character getCharacterById(int id) {
        chrRLock.lock();
        try {
            for (Character chr : this.characters) {
                if (chr.getId() == id) {
                    return chr;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return null;
    }

    private static void updateMapObjectVisibility(Character chr, MapObject mo) {
        if (!chr.isMapObjectVisible(mo)) { // object entered view range
            if (mo.getType() == MapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= getRangedDistance()) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else if (mo.getType() != MapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > getRangedDistance()) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        }
    }

    public void moveMonster(Monster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        for (Character chr : getAllPlayers()) {
            updateMapObjectVisibility(chr, monster);
        }
    }

    public void movePlayer(Character player, Point newPosition) {
        player.setPosition(newPosition);

        try {
            MapObject[] visibleObjects = player.getVisibleMapObjects();

            Map<Integer, MapObject> mapObjects = getCopyMapObjects();
            for (MapObject mo : visibleObjects) {
                if (mo != null) {
                    if (mapObjects.get(mo.getObjectId()) == mo) {
                        updateMapObjectVisibility(player, mo);
                    } else {
                        player.removeVisibleMapObject(mo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (MapObject mo : getMapObjectsInRange(player.getPosition(), getRangedDistance(), rangedMapobjectTypes)) {
            if (!player.isMapObjectVisible(mo)) {
                mo.sendSpawnData(player.getClient());
                player.addVisibleMapObject(mo);
            }
        }
    }

    public final void toggleEnvironment(final String ms) {
        Map<String, Integer> env = getEnvironment();

        if (env.containsKey(ms)) {
            moveEnvironment(ms, env.get(ms) == 1 ? 2 : 1);
        } else {
            moveEnvironment(ms, 1);
        }
    }

    public final void moveEnvironment(final String ms, final int type) {
        broadcastMessage(PacketCreator.environmentMove(ms, type));

        objectWLock.lock();
        try {
            environment.put(ms, type);
        } finally {
            objectWLock.unlock();
        }
    }

    public final Map<String, Integer> getEnvironment() {
        objectRLock.lock();
        try {
            return Collections.unmodifiableMap(environment);
        } finally {
            objectRLock.unlock();
        }
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean hasClock() {
        return clock;
    }

    public void setTown(boolean isTown) {
        this.town = isTown;
    }

    public boolean isTown() {
        return town;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean mute) {
        isMuted = mute;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public boolean getEverlast() {
        return everlast;
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    public void setMobCapacity(int capacity) {
        this.mobCapacity = capacity;
    }

    public void setBackgroundTypes(HashMap<Integer, Integer> backTypes) {
        backgroundTypes.putAll(backTypes);
    }

    // not really costly to keep generating imo
    public void sendNightEffect(Character chr) {
        for (Entry<Integer, Integer> types : backgroundTypes.entrySet()) {
            if (types.getValue() >= 3) { // 3 is a special number
                chr.sendPacket(PacketCreator.changeBackgroundEffect(true, types.getKey(), 0));
            }
        }
    }

    public void broadcastNightEffect() {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                sendNightEffect(chr);
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public Character getCharacterByName(String name) {
        chrRLock.lock();
        try {
            for (Character chr : this.characters) {
                if (chr.getName().equalsIgnoreCase(name)) {
                    return chr;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return null;
    }

    public boolean makeDisappearItemFromMap(MapObject mapobj) {
        if (mapobj instanceof MapItem) {
            return makeDisappearItemFromMap((MapItem) mapobj);
        } else {
            return mapobj == null;  // no drop to make disappear...
        }
    }

    public boolean makeDisappearItemFromMap(MapItem mapitem) {
        if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
            mapitem.lockItem();
            try {
                if (mapitem.isPickedUp()) {
                    return true;
                }

                MapleMap.this.pickItemDrop(PacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0), mapitem);
                return true;
            } finally {
                mapitem.unlockItem();
            }
        }

        return false;
    }

    private class MobLootEntry implements Runnable {

        private final byte droptype;
        private final int mobpos;
        private final float chRate;
        private final Point pos;
        private final List<MonsterDropEntry> dropEntry;
        private final List<MonsterDropEntry> visibleQuestEntry;
        private final List<MonsterDropEntry> otherQuestEntry;
        private final List<MonsterGlobalDropEntry> globalEntry;
        private final Character chr;
        private final Monster mob;

        protected MobLootEntry(byte droptype, int mobpos, float chRate, Point pos, List<MonsterDropEntry> dropEntry, List<MonsterDropEntry> visibleQuestEntry, List<MonsterDropEntry> otherQuestEntry, List<MonsterGlobalDropEntry> globalEntry, Character chr, Monster mob) {
            this.droptype = droptype;
            this.mobpos = mobpos;
            this.chRate = chRate;
            this.pos = pos;
            this.dropEntry = dropEntry;
            this.visibleQuestEntry = visibleQuestEntry;
            this.otherQuestEntry = otherQuestEntry;
            this.globalEntry = globalEntry;
            this.chr = chr;
            this.mob = mob;
        }

        @Override
        public void run() {
            byte d = 1;

            // Normal Drops
            d = dropItemsFromMonsterOnMap(dropEntry, pos, d, chRate, droptype, mobpos, chr, mob);

            // Global Drops
            d = dropGlobalItemsFromMonsterOnMap(globalEntry, pos, d, droptype, mobpos, chr, mob);

            // Quest Drops
            d = dropItemsFromMonsterOnMap(visibleQuestEntry, pos, d, chRate, droptype, mobpos, chr, mob);
            dropItemsFromMonsterOnMap(otherQuestEntry, pos, d, chRate, droptype, mobpos, chr, mob);
        }
    }

    private class ActivateItemReactor implements Runnable {

        private final MapItem mapitem;
        private final Reactor reactor;
        private final Client c;

        public ActivateItemReactor(MapItem mapitem, Reactor reactor, Client c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            reactor.hitLockReactor();
            try {
                if (reactor.getReactorType() == 100) {
                    if (reactor.getShouldCollect() == true && mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                        mapitem.lockItem();
                        try {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            mapitem.setPickedUp(true);
                            unregisterItemDrop(mapitem);

                            reactor.setShouldCollect(false);
                            MapleMap.this.broadcastMessage(PacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0), mapitem.getPosition());

                            droppedItemCount.decrementAndGet();
                            MapleMap.this.removeMapObject(mapitem);

                            reactor.hitReactor(c);

                            if (reactor.getDelay() > 0) {
                                MapleMap reactorMap = reactor.getMap();

                                OverallService service = (OverallService) reactorMap.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
                                service.registerOverallAction(reactorMap.getId(), () -> {
                                    reactor.lockReactor();
                                    try {
                                        reactor.resetReactorActions(0);
                                        reactor.setAlive(true);
                                        broadcastMessage(PacketCreator.triggerReactor(reactor, 0));
                                    } finally {
                                        reactor.unlockReactor();
                                    }
                                }, reactor.getDelay());
                            }
                        } finally {
                            mapitem.unlockItem();
                        }
                    }
                }
            } finally {
                reactor.hitUnlockReactor();
            }
        }
    }

    public void instanceMapFirstSpawn(int difficulty, boolean isPq) {
        for (SpawnPoint spawnPoint : getAllMonsterSpawn()) {
            if (spawnPoint.getMobTime() == -1) {   //just those allowed to be spawned only once
                spawnMonster(spawnPoint.getMonster());
            }
        }
    }

    public void instanceMapRespawn() {
        if (!allowSummons) {
            return;
        }

        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));//Fking lol'd
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = getMonsterSpawn();
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public void instanceMapForceRespawn() {
        if (!allowSummons) {
            return;
        }

        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));//Fking lol'd
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = getMonsterSpawn();
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldForceSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public void closeMapSpawnPoints() {
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            spawnPoint.setDenySpawn(true);
        }
    }

    public void restoreMapSpawnPoints() {
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            spawnPoint.setDenySpawn(false);
        }
    }

    /**
     * 设置所有刷怪点的重生速率倍率（轮回石碑加速用）。
     *
     * @param multiplier 倍率（0.1~1.0），例如 0.3 表示重生时间缩短到原来的30%
     * @return 旧的倍率值，用于后续 applyRespawnReduction
     */
    public float setRespawnAcceleration(float multiplier) {
        float oldMultiplier = 1.0f;
        List<SpawnPoint> spawns = getMonsterSpawn();
        synchronized (monsterSpawn) {
            if (!spawns.isEmpty()) {
                oldMultiplier = spawns.get(0).getRespawnRateMultiplier();
            }
            for (SpawnPoint sp : spawns) {
                sp.setRespawnRateMultiplier(multiplier);
                sp.applyRespawnReduction(oldMultiplier);
            }
        }
        return oldMultiplier;
    }

    /**
     * 获取当前刷怪倍率（0.1~1.0，1.0=正常速度）
     */
    public float getRespawnAcceleration() {
        List<SpawnPoint> spawns = getMonsterSpawn();
        synchronized (monsterSpawn) {
            if (spawns.isEmpty()) {
                return 1.0f;
            }
            return spawns.get(0).getRespawnRateMultiplier();
        }
    }

    public void setAllowSpawnPointInBox(boolean allow, Rectangle box) {
        for (SpawnPoint sp : getMonsterSpawn()) {
            if (box.contains(sp.getPosition())) {
                sp.setDenySpawn(!allow);
            }
        }
    }

    public void setAllowSpawnPointInRange(boolean allow, Point from, double rangeSq) {
        for (SpawnPoint sp : getMonsterSpawn()) {
            if (from.distanceSq(sp.getPosition()) <= rangeSq) {
                sp.setDenySpawn(!allow);
            }
        }
    }

    public SpawnPoint findClosestSpawnpoint(Point from) {
        SpawnPoint closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (SpawnPoint sp : getMonsterSpawn()) {
            double distance = sp.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = sp;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    private static double getCurrentSpawnRate(int numPlayers) {
        return 0.70 + (0.05 * Math.min(6, numPlayers));
    }

    private int getNumShouldSpawn(int numPlayers) {
        /*
        System.out.println("----------------------------------");
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            System.out.println("sp " + spawnPoint.getPosition().getX() + ", " + spawnPoint.getPosition().getY() + ": " + spawnPoint.getDenySpawn());
        }
        System.out.println("try " + monsterSpawn.size() + " - " + spawnedMonstersOnMap.get());
        System.out.println("----------------------------------");
        */

        if (GameConfig.getServerBoolean("use_enable_full_respawn")) {
            return (monsterSpawn.size() - spawnedMonstersOnMap.get());
        }

        int maxNumShouldSpawn = (int) Math.ceil(getCurrentSpawnRate(numPlayers) * monsterSpawn.size());
        return maxNumShouldSpawn - spawnedMonstersOnMap.get();
    }

    /**
     * 执行地图怪物刷新操作
     * 
     * <p>根据当前地图上的玩家数量和预设的刷新规则，计算需要生成的怪物数量，
     * 并从可用的刷新点中随机选择位置生成怪物。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>检查是否允许召唤（allowSummons标志）</li>
     *   <li>检查轮回石碑是否过期并进行清理</li>
     *   <li>获取当前地图上的玩家数量</li>
     *   <li>计算应该生成的怪物数量</li>
     *   <li>随机化刷新点顺序以平衡生成位置</li>
     *   <li>生成符合刷新条件的怪物</li>
     * </ul>
     * 
     * <p>刷新策略：</p>
     * <ul>
     *   <li>基于地图上的玩家数量动态调整刷新率</li>
     *   <li>随机化刷新点以避免怪物聚集在同一位置</li>
     *   <li>考虑刷新间隔和怪物存活情况</li>
     * </ul>
     * 
     * @since 2024-07-18
     */
    public void respawn() {
        if (!allowSummons) {
            return;
        }

        // 检查轮回石碑是否过期（兜底机制，防止定时器未触发）
        if (samsaraExpireTime > 0 && System.currentTimeMillis() > samsaraExpireTime) {
            removeSamsaraStoneNpc();
        }

        int numPlayers;
        chrRLock.lock();
        try {
            numPlayers = characters.size();

            if (numPlayers == 0) {
                return;
            }
        } finally {
            chrRLock.unlock();
        }

        int numShouldSpawn = getNumShouldSpawn(numPlayers);
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = new ArrayList<>(getMonsterSpawn());
            Collections.shuffle(randomSpawn);
            short spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;

                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public void mobMpRecovery() {
        for (Monster mob : this.getAllMonsters()) {
            if (mob.isAlive()) {
                mob.heal(0, mob.getLevel());
            }
        }
    }

    public final int getNumPlayersInArea(final int index) {
        return getNumPlayersInRect(getArea(index));
    }

    public final int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;

        chrRLock.lock();
        try {
            final Iterator<Character> ltr = characters.iterator();
            while (ltr.hasNext()) {
                if (rect.contains(ltr.next().getPosition())) {
                    ret++;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return ret;
    }

    public final int getNumPlayersItemsInArea(final int index) {
        return getNumPlayersItemsInRect(getArea(index));
    }

    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int retP = getNumPlayersInRect(rect);
        int retI = getMapObjectsInBox(rect, Arrays.asList(MapObjectType.ITEM)).size();

        return retP + retI;
    }

    private interface DelayedPacketCreation {

        void sendPackets(Client c);
    }

    private interface SpawnCondition {

        boolean canSpawn(Character chr);
    }

    /**
     * 获取地图HP减少值
     * 
     * <p>返回当前地图每秒对玩家造成的HP减少值。
     * 某些特殊地图（如危险区域）会持续对玩家造成伤害。</p>
     * 
     * @return 地图每秒HP减少值
     */
    public int getHPDec() {
        return decHP;
    }

    /**
     * 设置地图HP减少值
     * 
     * <p>设置当前地图每秒对玩家造成的HP减少值。
     * 某些特殊地图（如危险区域）会持续对玩家造成伤害。</p>
     * 
     * @param delta 要设置的HP减少值
     */
    public void setHPDec(int delta) {
        decHP = delta;
    }

    /**
     * 获取地图HP减少保护物品ID
     * 
     * <p>返回能够保护玩家免受地图HP减少效果的物品ID。
     * 如果玩家持有此物品，则不会受到地图的持续伤害。</p>
     * 
     * @return 地图HP减少保护物品ID，0表示无保护物品
     */
    public int getHPDecProtect() {
        return protectItem;
    }

    /**
     * 设置地图HP减少保护物品ID
     * 
     * <p>设置能够保护玩家免受地图HP减少效果的物品ID。
     * 如果玩家持有此物品，则不会受到地图的持续伤害。</p>
     * 
     * @param delta 要设置的保护物品ID
     */
    public void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    /**
     * 获取地图恢复率
     * 
     * <p>返回当前地图的HP/MP自然恢复倍率。
     * 此值影响玩家在地图上的自然恢复速度。</p>
     * 
     * @return 地图恢复倍率，默认为1.0f
     */
    public float getRecovery() {
        return recovery;
    }

    /**
     * 设置地图恢复率
     * 
     * <p>设置当前地图的HP/MP自然恢复倍率。
     * 此值影响玩家在地图上的自然恢复速度。</p>
     * 
     * @param recRate 要设置的恢复倍率
     */
    public void setRecovery(float recRate) {
        recovery = recRate;
    }

    /**
     * 检查地图船只状态
     * 
     * <p>返回当前地图的船只状态，用于判断地图上是否有船只以及船只的状态。</p>
     * 
     * <p>返回值含义：</p>
     * <ul>
     *   <li>0 - 地图上没有船只</li>
     *   <li>1 - 地图上有船只且已靠岸</li>
     *   <li>2 - 地图上有船只但未靠岸</li>
     * </ul>
     * 
     * @return 地图船只状态码
     */
    private int hasBoat() {
        return !boat ? 0 : (docked ? 1 : 2);
    }

    public void setBoat(boolean hasBoat) {
        this.boat = hasBoat;
    }

    public void setDocked(boolean isDocked) {
        this.docked = isDocked;
    }

    public boolean getDocked() {
        return this.docked;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getSeats() {
        return seats;
    }

    public void broadcastGMMessage(Character source, Packet packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    private void broadcastGMMessage(Character source, Packet packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source && chr.isGM()) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.sendPacket(packet);
                        }
                    } else {
                        chr.sendPacket(packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void broadcastNONGMMessage(Character source, Packet packet, boolean repeatToSource) {
        chrRLock.lock();
        try {
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character chr = iterator.next();
                if (chrDisconnected(iterator, chr)) {
                    continue;
                }
                if (chr != source && !chr.isGM()) {
                    chr.sendPacket(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public OxQuiz getOx() {
        return ox;
    }

    public void setOx(OxQuiz set) {
        this.ox = set;
    }

    public void setOxQuiz(boolean b) {
        this.isOxQuiz = b;
    }

    public boolean isOxQuiz() {
        return isOxQuiz;
    }

    public void setOnUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public void setOnFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    private boolean hasForcedEquip() {
        return fieldType == 81 || fieldType == 82;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public void clearDrops(Character player) {
        for (MapObject i : getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            droppedItemCount.decrementAndGet();
            removeMapObject(i);
            this.broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, player.getId()));
        }
    }

    public void clearDrops() {
        for (MapObject i : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            droppedItemCount.decrementAndGet();
            removeMapObject(i);
            this.broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, 0));
        }
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public int getFieldLimit() {
        return fieldLimit;
    }

    public void allowSummonState(boolean b) {
        MapleMap.this.allowSummons = b;
    }

    public boolean getSummonState() {
        return MapleMap.this.allowSummons;
    }

    public void warpEveryone(int to) {
        List<Character> players = new ArrayList<>(getCharacters());

        for (Character chr : players) {
            chr.changeMap(to);
        }
    }

    public void warpEveryone(int to, int pto) {
        List<Character> players = new ArrayList<>(getCharacters());

        for (Character chr : players) {
            chr.changeMap(to, pto);
        }
    }

    // BEGIN EVENTS
    /**
     * 设置雪球对象到指定队伍
     * @param team 队伍编号（0或1）
     * @param ball 雪球对象
     */
    public void setSnowball(int team, Snowball ball) {
        switch (team) {
            case 0:
                // 队伍0的雪球
                this.snowball0 = ball;
                break;
            case 1:
                // 队伍1的雪球
                this.snowball1 = ball;
                break;
            default:
                // 无效队伍，不处理
                break;
        }
    }

    /**
     * 获取指定队伍的雪球对象
     * @param team 队伍编号（0或1）
     * @return 对应队伍的雪球对象
     */
    public Snowball getSnowball(int team) {
        switch (team) {
            case 0:
                // 返回队伍0的雪球
                return snowball0;
            case 1:
                // 返回队伍1的雪球
                return snowball1;
            default:
                // 无效队伍，返回null
                return null;
        }
    }

    private boolean specialEquip() {//Maybe I shouldn't use fieldType :\
        return fieldType == 4 || fieldType == 19;
    }

    public void setCoconut(Coconut nut) {
        this.coconut = nut;
    }

    public Coconut getCoconut() {
        return coconut;
    }

    public void warpOutByTeam(int team, int mapid) {
        List<Character> chars = new ArrayList<>(getCharacters());
        for (Character chr : chars) {
            if (chr != null) {
                if (chr.getTeam() == team) {
                    chr.changeMap(mapid);
                }
            }
        }
    }

    public void startEvent(final Character chr) {
        if (this.mapid == MapId.EVENT_COCONUT_HARVEST && getCoconut() == null) {
            setCoconut(new Coconut(this));
            coconut.startEvent();
        } else if (this.mapid == MapId.EVENT_PHYSICAL_FITNESS) {
            chr.setFitness(new Fitness(chr));
            chr.getFitness().startFitness();
        } else if (this.mapid == MapId.EVENT_OLA_OLA_1 || this.mapid == MapId.EVENT_OLA_OLA_2 ||
                this.mapid == MapId.EVENT_OLA_OLA_3 || this.mapid == MapId.EVENT_OLA_OLA_4) {
            chr.setOla(new Ola(chr));
            chr.getOla().startOla();
        } else if (this.mapid == MapId.EVENT_OX_QUIZ && getOx() == null) {
            setOx(new OxQuiz(this));
            getOx().sendQuestion();
            setOxQuiz(true);
        } else if (this.mapid == MapId.EVENT_SNOWBALL && getSnowball(chr.getTeam()) == null) {
            setSnowball(0, new Snowball(0, this));
            setSnowball(1, new Snowball(1, this));
            getSnowball(chr.getTeam()).startEvent();
        }
    }

    public boolean eventStarted() {
        return eventstarted;
    }

    public void startEvent() {
        this.eventstarted = true;
    }

    public void setEventStarted(boolean event) {
        this.eventstarted = event;
    }

    public String getEventNPC() {
        StringBuilder sb = new StringBuilder();
        sb.append("请与 "+ mapName + " 的 ");
        if (mapid == MapId.SOUTHPERRY) {
            sb.append("珀尔");
        } else if (mapid == MapId.LITH_HARBOUR) {
            sb.append("江");
        } else if (mapid == MapId.ORBIS) {
            sb.append("马丁");
        } else if (mapid == MapId.LUDIBRIUM) {
            sb.append("托尼");
        } else {
            return null;
        }
        sb.append(" 进行对话。");
        return sb.toString();
    }

    public boolean hasEventNPC() {
        return this.mapid == 60000 || this.mapid == MapId.LITH_HARBOUR || this.mapid == MapId.ORBIS || this.mapid == MapId.LUDIBRIUM;
    }

    public boolean isStartingEventMap() {
        return this.mapid == MapId.EVENT_PHYSICAL_FITNESS || this.mapid == MapId.EVENT_OX_QUIZ ||
                this.mapid == MapId.EVENT_FIND_THE_JEWEL || this.mapid == MapId.EVENT_OLA_OLA_0 || this.mapid == MapId.EVENT_OLA_OLA_1;
    }

    public boolean isEventMap() {
        return this.mapid >= MapId.EVENT_FIND_THE_JEWEL && this.mapid < MapId.EVENT_WINNER || this.mapid > MapId.EVENT_EXIT && this.mapid <= 109090000;
    }

    public void setTimeMob(int id, String msg) {
        timeMob = new Pair<>(id, msg);
    }

    public Pair<Integer, String> getTimeMob() {
        return timeMob;
    }

    public void toggleHiddenNPC(int id) {
        chrRLock.lock();
        objectRLock.lock();
        try {
            for (MapObject obj : mapobjects.values()) {
                if (obj.getType() == MapObjectType.NPC) {
                    NPC npc = (NPC) obj;
                    if (npc.getId() == id) {
                        npc.setHide(!npc.isHidden());
                        if (!npc.isHidden()) //Should only be hidden upon changing maps
                        {
                            broadcastMessage(PacketCreator.spawnNPC(npc));
                        }
                    }
                }
            }
        } finally {
            objectRLock.unlock();
            chrRLock.unlock();
        }
    }

    public void setMobInterval(short interval) {
        this.mobInterval = interval;
    }

    public short getMobInterval() {
        return mobInterval;
    }

    // ==================== 轮回石碑（Samsara Stone）====================

    /**
     * 在地图上生成轮回石碑NPC，启动怪物刷新加速，并注册自动消失定时器。
     *
     * @param owner            石碑所有者
     * @param npcId            NPC模板ID
     * @param durationMinutes  持续时间（分钟）
     * @param accelerationRate 刷怪加速倍率（如 0.3f = 30%原始重生时间）
     */
    public void spawnSamsaraStoneNpc(Character owner, int npcId, int durationMinutes, float accelerationRate) {
        // 先移除旧的轮回石碑（如果存在）
        removeSamsaraStoneNpc();

        // 记录状态（NPC可能不存在，但加速效果仍然生效）
        this.samsaraOwner = owner;
        this.samsaraExpireTime = System.currentTimeMillis() + (long) durationMinutes * 60 * 1000;

        // 创建并放置视觉NPC（如果WZ中存在该NPC ID）
        NPC npc = LifeFactory.getNPC(npcId);
        if (npc != null) {
            Point pos = owner.getPosition();
            npc.setPosition(new Point(pos));
            npc.setCy(pos.y);
            npc.setRx0(pos.x + 50);
            npc.setRx1(pos.x - 50);
            Foothold fh = getFootholds().findBelow(pos);
            if (fh != null) {
                npc.setFh(fh.getId());
            }
            addMapObject(npc);
            broadcastMessage(PacketCreator.spawnNPC(npc));
            this.samsaraStoneNpc = npc;
        }

        // 启动刷怪加速
        setRespawnAcceleration(accelerationRate);

        // 注册自动消失定时器
        this.samsaraRemoveTask = TimerManager.getInstance().schedule(() -> {
            removeSamsaraStoneNpc();
        }, durationMinutes * 60 * 1000);

        // 广播提示消息
        dropMessage(6, "[" + owner.getName() + "] 召唤了轮回石碑，怪物刷新速度提升！持续 " + durationMinutes + " 分钟。");
    }

    /**
     * 移除轮回石碑NPC，恢复正常刷怪速度，取消定时器。
     */
    public void removeSamsaraStoneNpc() {
        // 检查是否有活跃的轮回石碑
        if (samsaraExpireTime == 0 && samsaraStoneNpc == null) {
            return;
        }

        // 取消定时器
        if (samsaraRemoveTask != null) {
            samsaraRemoveTask.cancel(false);
            samsaraRemoveTask = null;
        }

        // 移除视觉NPC
        if (samsaraStoneNpc != null) {
            broadcastMessage(PacketCreator.removeNPCController(samsaraStoneNpc.getObjectId()));
            broadcastMessage(PacketCreator.removeNPC(samsaraStoneNpc.getObjectId()));
            removeMapObject(samsaraStoneNpc.getObjectId());
            samsaraStoneNpc = null;
        }

        // 发送提示消息
        dropMessage(6, "轮回石碑的效果消失了，怪物刷新速度恢复正常。");

        // 恢复刷怪速度
        setRespawnAcceleration(1.0f);

        // 清理状态
        samsaraOwner = null;
        samsaraExpireTime = 0;
    }

    /** 当前地图是否有活跃的轮回石碑效果 */
    public boolean hasSamsaraStone() {
        return samsaraExpireTime > 0 && System.currentTimeMillis() < samsaraExpireTime;
    }

    /** 获取轮回石碑所有者 */
    public Character getSamsaraOwner() {
        return samsaraOwner;
    }

    /** 获取轮回石碑过期时间戳 */
    public long getSamsaraExpireTime() {
        return samsaraExpireTime;
    }

    // ==================== 轮回石碑 END ====================

    /**
     * 清理地图上的所有对象
     * 
     * <p>清理地图上的所有动态对象，包括掉落物、怪物和反应器等。
     * 此方法用于重置地图状态或在地图清理时使用。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>清理所有掉落物品</li>
     *   <li>击杀所有怪物</li>
     *   <li>重置所有反应器状态</li>
     * </ul>
     * 
     * @since 2024-07-18
     */
    public void clearMapObjects() {
        clearDrops();
        killAllMonsters();
        resetReactors();
    }

    public final void resetFully() {
        resetMapObjects();
    }

    public void resetMapObjects() {
        resetMapObjects(1, false);
    }

    public void resetPQ() {
        resetPQ(1);
    }

    public void resetPQ(int difficulty) {
        resetMapObjects(difficulty, true);
    }

    public void resetMapObjects(int difficulty, boolean isPq) {
        clearMapObjects();

        restoreMapSpawnPoints();
        instanceMapFirstSpawn(difficulty, isPq);
    }

    public void broadcastShip(final boolean state) {
        broadcastMessage(PacketCreator.boatPacket(state));
        this.setDocked(state);
    }

    public void broadcastEnemyShip(final boolean state) {
        broadcastMessage(PacketCreator.crogBoatPacket(state));
        this.setDocked(state);
    }

    public boolean isHorntailDefeated() {   // all parts of dead horntail can be found here?
        for (int i = MobId.DEAD_HORNTAIL_MIN; i <= MobId.DEAD_HORNTAIL_MAX; i++) {
            if (getMonsterById(i) == null) {
                return false;
            }
        }

        return true;
    }

    public void spawnHorntailOnGroundBelow(final Point targetPoint) {   // ayy lmao
        Monster htIntro = LifeFactory.getMonster(MobId.SUMMON_HORNTAIL);
        spawnMonsterOnGroundBelow(htIntro, targetPoint);    // htintro spawn animation converting into horntail detected thanks to Arnah

        final Monster ht = LifeFactory.getMonster(MobId.HORNTAIL);
        ht.setParentMobOid(htIntro.getObjectId());
        ht.addListener(new MonsterListener() {
            @Override
            public void monsterKilled(int aniTime) {
            }

            @Override
            public void monsterDamaged(Character from, int trueDmg) {
                ht.addHp(trueDmg);
            }

            @Override
            public void monsterHealed(int trueHeal) {
                ht.addHp(-trueHeal);
            }
        });
        spawnMonsterOnGroundBelow(ht, targetPoint);

        for (int mobId = MobId.HORNTAIL_HEAD_A; mobId <= MobId.HORNTAIL_TAIL; mobId++) {
            Monster m = LifeFactory.getMonster(mobId);
            m.setParentMobOid(htIntro.getObjectId());

            m.addListener(new MonsterListener() {
                @Override
                public void monsterKilled(int aniTime) {
                }

                @Override
                public void monsterDamaged(Character from, int trueDmg) {
                    // thanks Halcyon for noticing HT not dropping loots due to propagated damage not registering attacker
                    ht.applyFakeDamage(from, trueDmg, true);
                }

                @Override
                public void monsterHealed(int trueHeal) {
                    ht.addHp(trueHeal);
                }
            });

            spawnMonsterOnGroundBelow(m, targetPoint);
        }
    }

    public boolean claimOwnership(Character chr) {
        if (mapOwner == null) {
            this.mapOwner = chr;
            chr.setOwnedMap(this);

            mapOwnerLastActivityTime = Server.getInstance().getCurrentTime();

            getChannelServer().registerOwnedMap(this);
            return true;
        } else {
            return chr == mapOwner;
        }
    }

    public Character unclaimOwnership() {
        Character lastOwner = this.mapOwner;
        return unclaimOwnership(lastOwner) ? lastOwner : null;
    }

    public boolean unclaimOwnership(Character chr) {
        if (chr != null && mapOwner == chr) {
            this.mapOwner = null;
            chr.setOwnedMap(null);

            mapOwnerLastActivityTime = Long.MAX_VALUE;

            getChannelServer().unregisterOwnedMap(this);
            return true;
        } else {
            return false;
        }
    }

    private void refreshOwnership() {
        mapOwnerLastActivityTime = Server.getInstance().getCurrentTime();
    }

    public boolean isOwnershipRestricted(Character chr) {
        Character owner = mapOwner;

        if (owner != null) {
            if (owner != chr && !owner.isPartyMember(chr)) {    // thanks Vcoc & BHB for suggesting the map ownership feature
                chr.showMapOwnershipInfo(owner);
                return true;
            } else {
                this.refreshOwnership();
            }
        }

        return false;
    }

    public void checkMapOwnerActivity() {
        long timeNow = Server.getInstance().getCurrentTime();
        if (timeNow - mapOwnerLastActivityTime > 60000) {
            if (unclaimOwnership() != null) {
                this.dropMessage(5, "这里现在是无主之地了。");
            }
        }
    }

    private final List<Point> takenSpawns = new LinkedList<>();
    private final List<GuardianSpawnPoint> guardianSpawns = new LinkedList<>();
    private final List<MCSkill> blueTeamBuffs = new ArrayList();
    private final List<MCSkill> redTeamBuffs = new ArrayList();
    private final List<Integer> skillIds = new ArrayList();
    private final List<Pair<Integer, Integer>> mobsToSpawn = new ArrayList();

    public List<MCSkill> getBlueTeamBuffs() {
        return blueTeamBuffs;
    }

    public List<MCSkill> getRedTeamBuffs() {
        return redTeamBuffs;
    }

    public void clearBuffList() {
        redTeamBuffs.clear();
        blueTeamBuffs.clear();
    }

    public List<MapObject> getAllPlayer() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER));
    }

    /**
     * 判断当前地图是否为CPQ（组队任务）比赛地图
     * <p>CPQ比赛地图包括多个地图ID，用于不同的比赛场地</p>
     * @return true 如果是CPQ比赛地图
     */
    public boolean isCPQMap() {
        switch (this.getId()) {
            // CPQ比赛地图：射手训练场、林中之城训练场等
            case 980000101:
            case 980000201:
            case 980000301:
            case 980000401:
            case 980000501:
            case 980000601:
            case 980031100:
            case 980032100:
            case 980033100:
                return true;
        }
        return false;
    }

    /**
     * 判断当前地图是否为CPQ第二轮比赛地图
     * <p>只有特定的三个地图ID属于第二轮比赛</p>
     * @return true 如果是CPQ第二轮比赛地图
     */
    public boolean isCPQMap2() {
        switch (this.getId()) {
            // CPQ第二轮比赛地图
            case 980031100:
            case 980032100:
            case 980033100:
                return true;
        }
        return false;
    }

    /**
     * 判断当前地图是否为CPQ大厅地图
     * <p>大厅地图是比赛开始前的等待区域</p>
     * @return true 如果是CPQ大厅地图
     */
    public boolean isCPQLobby() {
        switch (this.getId()) {
            // CPQ大厅地图
            case 980000100:
            case 980000200:
            case 980000300:
            case 980000400:
            case 980000500:
            case 980000600:
                return true;
        }
        return false;
    }

    /**
     * 判断当前地图是否为CPQ蓝队比赛地图
     * <p>蓝队比赛地图用于蓝方队伍的比赛</p>
     * @return true 如果是CPQ蓝队比赛地图
     */
    public boolean isBlueCPQMap() {
        switch (this.getId()) {
            // CPQ蓝队比赛地图
            case 980000501:
            case 980000601:
            case 980031200:
            case 980032200:
            case 980033200:
                return true;
        }
        return false;
    }

    /**
     * 判断当前地图是否为CPQ紫队比赛地图
     * <p>紫队比赛地图用于紫方队伍的比赛</p>
     * @return true 如果是CPQ紫队比赛地图
     */
    public boolean isPurpleCPQMap() {
        switch (this.getId()) {
            // CPQ紫队比赛地图
            case 980000301:
            case 980000401:
            case 980031200:
            case 980032200:
            case 980033200:
                return true;
        }
        return false;
    }

    public Point getRandomSP(int team) {
        if (takenSpawns.size() > 0) {
            for (SpawnPoint sp : monsterSpawn) {
                for (Point pt : takenSpawns) {
                    if ((sp.getPosition().x == pt.x && sp.getPosition().y == pt.y) || (sp.getTeam() != team && !this.isBlueCPQMap())) {
                        continue;
                    } else {
                        takenSpawns.add(pt);
                        return sp.getPosition();
                    }
                }
            }
        } else {
            for (SpawnPoint sp : monsterSpawn) {
                if (sp.getTeam() == team || this.isBlueCPQMap()) {
                    takenSpawns.add(sp.getPosition());
                    return sp.getPosition();
                }
            }
        }
        return null;
    }

    public GuardianSpawnPoint getRandomGuardianSpawn(int team) {
        boolean alltaken = false;
        for (GuardianSpawnPoint a : this.guardianSpawns) {
            if (!a.isTaken()) {
                alltaken = false;
                break;
            }
        }
        if (alltaken) {
            return null;
        }
        if (this.guardianSpawns.size() > 0) {
            while (true) {
                for (GuardianSpawnPoint gsp : this.guardianSpawns) {
                    if (!gsp.isTaken() && Math.random() < 0.3 && (gsp.getTeam() == -1 || gsp.getTeam() == team)) {
                        return gsp;
                    }
                }
            }
        }
        return null;
    }

    public void addGuardianSpawnPoint(GuardianSpawnPoint a) {
        this.guardianSpawns.add(a);
    }

    public int spawnGuardian(int team, int num) {
        try {
            if (team == 0 && redTeamBuffs.size() >= 4 || team == 1 && blueTeamBuffs.size() >= 4) {
                return 2;
            }
            final MCSkill skill = CarnivalFactory.getInstance().getGuardian(num);
            if (team == 0 && redTeamBuffs.contains(skill)) {
                return 0;
            } else if (team == 1 && blueTeamBuffs.contains(skill)) {
                return 0;
            }
            GuardianSpawnPoint pt = this.getRandomGuardianSpawn(team);
            if (pt == null) {
                return -1;
            }
            int reactorID = 9980000 + team;
            Reactor reactor = new Reactor(ReactorFactory.getReactorS(reactorID), reactorID);
            pt.setTaken(true);
            reactor.setPosition(pt.getPosition());
            reactor.setName(team + "" + num); //lol
            reactor.resetReactorActions(0);
            this.spawnReactor(reactor);
            reactor.setGuardian(pt);
            this.buffMonsters(team, skill);
            getReactorByOid(reactor.getObjectId()).hitReactor(((Character) this.getAllPlayer().get(0)).getClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void buffMonsters(int team, MCSkill skill) {
        if (skill == null) {
            return;
        }

        if (team == 0) {
            redTeamBuffs.add(skill);
        } else if (team == 1) {
            blueTeamBuffs.add(skill);
        }
        for (MapObject mmo : this.mapobjects.values()) {
            if (mmo.getType() == MapObjectType.MONSTER) {
                Monster mob = (Monster) mmo;
                if (mob.getTeam() == team) {
                    skill.getSkill().applyEffect(null, mob, false, null);
                }
            }
        }
    }

    public final List<Integer> getSkillIds() {
        return skillIds;
    }

    public final void addSkillId(int z) {
        this.skillIds.add(z);
    }

    public final void addMobSpawn(int mobId, int spendCP) {
        this.mobsToSpawn.add(new Pair<>(mobId, spendCP));
    }

    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return mobsToSpawn;
    }

    public boolean isCPQWinnerMap() {
        switch (this.getId()) {
            case 980000103:
            case 980000203:
            case 980000303:
            case 980000403:
            case 980000503:
            case 980000603:
            case 980031300:
            case 980032300:
            case 980033300:
                return true;
        }
        return false;
    }

    public boolean isCPQLoserMap() {
        switch (this.getId()) {
            case 980000104:
            case 980000204:
            case 980000304:
            case 980000404:
            case 980000504:
            case 980000604:
            case 980031400:
            case 980032400:
            case 980033400:
                return true;
        }
        return false;
    }

    public void runCharacterStatUpdate() {
        if (!statUpdateRunnables.isEmpty()) {
            List<Runnable> toRun = new ArrayList<>(statUpdateRunnables);
            statUpdateRunnables.clear();

            for (Runnable r : toRun) {
                r.run();
            }
        }
    }

    public void registerCharacterStatUpdate(Runnable r) {
        statUpdateRunnables.add(r);
    }

    /**
     * 释放地图资源并执行清理操作
     * 
     * <p>清理地图对象占用的所有资源，包括怪物、物品、定时任务等，
     * 以防止内存泄漏并确保资源的正确回收。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>释放所有怪物资源</li>
     *   <li>清除地图上的所有对象</li>
     *   <li>取消并清理所有定时任务（物品监控、过期清理、怪物掉落生成、角色状态更新等）</li>
     *   <li>清理轮回石碑相关资源</li>
     *   <li>清理事件管理器、地形数据、传送门等引用</li>
     *   <li>释放怪物仇恨协调器资源</li>
     * </ul>
     * 
     * <p>资源清理顺序：</p>
     * <ol>
     *   <li>清理怪物资源</li>
     *   <li>清理地图对象</li>
     *   <li>清理轮回石碑资源</li>
     *   <li>清理定时任务</li>
     *   <li>清理其他引用</li>
     * </ol>
     * 
     * @since 2024-07-18
     */
    public void dispose() {
        for (Monster mm : this.getAllMonsters()) {
            mm.dispose();
        }

        clearMapObjects();

        // 清理轮回石碑定时器，防止内存泄漏
        if (samsaraRemoveTask != null) {
            samsaraRemoveTask.cancel(false);
            samsaraRemoveTask = null;
        }
        samsaraStoneNpc = null;
        samsaraOwner = null;

        event = null;
        footholds = null;
        portals.clear();
        mapEffect = null;

        chrWLock.lock();
        try {
            aggroMonitor.dispose();
            aggroMonitor = null;

            if (itemMonitor != null) {
                itemMonitor.cancel(false);
                itemMonitor = null;
            }

            if (expireItemsTask != null) {
                expireItemsTask.cancel(false);
                expireItemsTask = null;
            }

            if (mobSpawnLootTask != null) {
                mobSpawnLootTask.cancel(false);
                mobSpawnLootTask = null;
            }

            if (characterStatUpdateTask != null) {
                characterStatUpdateTask.cancel(false);
                characterStatUpdateTask = null;
            }
        } finally {
            chrWLock.unlock();
        }
    }

    public int getMaxMobs() {
        return maxMobs;
    }

    public void setMaxMobs(int maxMobs) {
        this.maxMobs = maxMobs;
    }

    public int getMaxReactors() {
        return maxReactors;
    }

    public void setMaxReactors(int maxReactors) {
        this.maxReactors = maxReactors;
    }

    public int getDeathCP() {
        return deathCP;
    }

    public void setDeathCP(int deathCP) {
        this.deathCP = deathCP;
    }

    public int getTimeDefault() {
        return timeDefault;
    }

    public void setTimeDefault(int timeDefault) {
        this.timeDefault = timeDefault;
    }

    public int getTimeExpand() {
        return timeExpand;
    }

    public void setTimeExpand(int timeExpand) {
        this.timeExpand = timeExpand;
    }

}