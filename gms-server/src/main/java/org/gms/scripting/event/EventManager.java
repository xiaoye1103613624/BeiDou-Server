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
package org.gms.scripting.event;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.World;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.scheduler.EventScriptScheduler;
import org.gms.server.Marriage;
import org.gms.server.ThreadManager;
import org.gms.server.expeditions.Expedition;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.OverrideMonsterStats;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MapManager;
import org.gms.server.quest.Quest;
import org.gms.exception.EventInstanceInProgressException;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 事件管理器，负责管理游戏中的各种事件实例
 * @author Matze
 * @author Ronan
 */
public class EventManager {
    private static final Logger log = LoggerFactory.getLogger(EventManager.class);
    /** 可调用的脚本引擎接口 */
    private Invocable iv;
    /** 所属频道服务器 */
    private Channel cserv;
    /** 所属世界服务器 */
    private World wserv;
    /** 主服务器实例 */
    private Server server;
    /** 事件脚本调度器，管理延时任务 */
    private final EventScriptScheduler ess = new EventScriptScheduler();
    /** 事件实例映射表，key为实例名称 */
    private final Map<String, EventInstanceManager> instances = new HashMap<>();
    /** 实例大厅锁定映射表，key为实例名称，value为大厅ID */
    private final Map<String, Integer> instanceLocks = new HashMap<>();
    /** 排队中的公会ID队列 */
    private final Queue<Integer> queuedGuilds = new LinkedList<>();
    /** 排队公会及会长映射，key为公会ID */
    private final Map<Integer, Integer> queuedGuildLeaders = new HashMap<>();
    /** 大厅状态列表，左值表示是否被占用，右值为占用起始时间戳 */
    private final List<Pair<Boolean, Long>> openedLobbys;
    /** 准备就绪的事件实例队列，用于实例复用 */
    private final List<EventInstanceManager> readyInstances = new LinkedList<>();
    /** 准备ID和加载中的实例数（负数表示释放中） */
    private Integer readyId = 0, onLoadInstances = 0;
    /** 事件属性配置 */
    private final Properties props = new Properties();
    /** 事件名称 */
    private final String name;
    /** 大厅锁，保护openedLobbys的并发访问 */
    private final Lock lobbyLock = new ReentrantLock();
    /** 队列锁，保护readyInstances和onLoadInstances的并发访问 */
    private final Lock queueLock = new ReentrantLock();
    /** 启动锁，防止多个实例同时创建 */
    private final Lock startLock = new ReentrantLock();

    /** 玩家许可集合，防止同一玩家并发创建多个事件实例 */
    private final Set<Integer> playerPermit = new HashSet<>();
    /** 启动信号量，限制同时创建事件实例的最大并发数（默认7个） */
    private final Semaphore startSemaphore = new Semaphore(7);

    /** 一个事件管理器最多支持同时运行的大厅数量 */
    private static final int maxLobbys = 8;

    /**
     * 构造函数
     * @param cserv 频道服务器
     * @param iv 可调用的脚本引擎
     * @param name 事件名称
     */
    public EventManager(Channel cserv, Invocable iv, String name) {
        this.server = Server.getInstance();
        this.iv = iv;
        this.cserv = cserv;
        this.wserv = server.getWorld(cserv.getWorld());
        this.name = name;

        this.openedLobbys = new ArrayList<>();
        for (int i = 0; i < maxLobbys; i++) {
            this.openedLobbys.add(new Pair<>(false, 0L));
        }
    }

    /**
     * 检查事件管理器是否已释放
     * @return 是否已释放
     */
    private boolean isDisposed() {
        return onLoadInstances <= -1000;
    }

    /**
     * 取消事件管理器（确保在没有玩家在线时调用）
     */
    public void cancel() {
        // 停止脚本调度，清除所有延时任务
        ess.dispose();

        // 调用脚本层cancelSchedule进行自定义清理
        try {
            iv.invokeFunction("cancelSchedule", (Object) null);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        // 销毁所有运行中的事件实例
        Collection<EventInstanceManager> eimList;
        synchronized (instances) {
            eimList = getInstances();
            instances.clear();
        }

        for (EventInstanceManager eim : eimList) {
            eim.dispose(true);
        }

        // 销毁所有就绪队列中的实例
        List<EventInstanceManager> readyEims;
        queueLock.lock();
        try {
            readyEims = new ArrayList<>(readyInstances);
            readyInstances.clear();
            // 将onLoadInstances设置为极小值，标记为已释放
            onLoadInstances = Integer.MIN_VALUE / 2;
        } finally {
            queueLock.unlock();
        }

        for (EventInstanceManager eim : readyEims) {
            eim.dispose(true);
        }

        // 清理所有引用，帮助GC回收
        props.clear();
        cserv = null;
        wserv = null;
        server = null;
        iv = null;
    }

    /**
     * 将对象列表转换为整数列表
     * @param objects 对象列表
     * @return 整数列表
     */
    private List<Integer> convertToIntegerList(List<Object> objects) {
        List<Integer> intList = new ArrayList<>();

        for (Object object : objects) {
            intList.add((Integer) object);
        }

        return intList;
    }

    /**
     * 获取大厅延迟时间
     * @return 延迟时间（毫秒）
     */
    public long getLobbyDelay() {
        return GameConfig.getServerLong("event_lobby_delay");
    }

    /**
     * 获取最大大厅数量
     * @return 最大大厅数量
     */
    private int getMaxLobbies() {
        try {
            return (int) iv.invokeFunction("getMaxLobbies");
        } catch (ScriptException | NoSuchMethodException ex) {
            // 脚本未定义大厅范围时使用默认值
            return maxLobbys;
        }
    }

    /**
     * 调度事件方法
     * @param methodName 方法名
     * @param delay 延迟时间
     * @return 事件调度未来对象
     */
    public EventScheduledFuture schedule(String methodName, long delay) {
        return schedule(methodName, null, delay);
    }

    /**
     * 调度事件方法（带事件实例）
     * @param methodName 方法名
     * @param eim 事件实例管理器
     * @param delay 延迟时间
     * @return 事件调度未来对象
     */
    public EventScheduledFuture schedule(final String methodName, final EventInstanceManager eim, long delay) {
        Runnable r = () -> {
            try {
                iv.invokeFunction(methodName, eim);
            } catch (ScriptException | NoSuchMethodException ex) {
                log.error("eim（{}），methodName（{}），Event script schedule（事件脚本时间表）", eim,methodName,ex);
            }
        };

        ess.registerEntry(r, delay);
        return new EventScheduledFuture(r, ess);
    }

    /**
     * 在指定时间戳调度事件
     * @param methodName 方法名
     * @param timestamp 时间戳
     * @return 事件调度未来对象
     */
    public EventScheduledFuture scheduleAtTimestamp(final String methodName, long timestamp) {
        Runnable r = () -> {
            try {
                iv.invokeFunction(methodName, (Object) null);
            } catch (ScriptException | NoSuchMethodException ex) {
                log.error("Event script scheduleAtTimestamp（事件脚本调度时间戳）", ex);
            }
        };

        ess.registerEntry(r, timestamp - server.getCurrentTime());
        return new EventScheduledFuture(r, ess);
    }

    /**
     * 获取世界服务器
     * @return 世界服务器
     */
    public World getWorldServer() {
        return wserv;
    }

    /**
     * 获取频道服务器
     * @return 频道服务器
     */
    public Channel getChannelServer() {
        return cserv;
    }

    /**
     * 获取地图工厂（委托给频道服务器）
     * 兼容旧版Nashorn脚本中的em.getMapFactory()调用
     * @return 地图工厂
     */
    public MapManager getMapFactory() {
        return cserv.getMapFactory();
    }

    /**
     * 获取可调用的脚本引擎
     * @return 可调用的脚本引擎
     */
    public Invocable getIv() {
        return iv;
    }

    /**
     * 获取指定名称的事件实例
     * @param name 实例名称
     * @return 事件实例管理器
     */
    public EventInstanceManager getInstance(String name) {
        return instances.get(name);
    }

    /**
     * 获取所有事件实例
     * @return 事件实例集合
     */
    public Collection<EventInstanceManager> getInstances() {
        synchronized (instances) {
            return new LinkedList<>(instances.values());
        }
    }

    /**
     * 创建新的事件实例
     * @param name 实例名称
     * @return 事件实例管理器
     * @throws EventInstanceInProgressException 如果实例已存在
     */
    public EventInstanceManager newInstance(String name) throws EventInstanceInProgressException {
        EventInstanceManager ret = getReadyInstance();

        if (ret == null) {
            ret = new EventInstanceManager(this, name);
        } else {
            ret.setName(name);
        }

        synchronized (instances) {
            if (instances.containsKey(name)) {
                throw new EventInstanceInProgressException(name, this.getName());
            }

            instances.put(name, ret);
        }
        return ret;
    }

    /**
     * 创建新的婚姻实例
     * @param name 实例名称
     * @return 婚姻实例
     * @throws EventInstanceInProgressException 如果实例已存在
     */
    public Marriage newMarriage(String name) throws EventInstanceInProgressException {
        Marriage ret = new Marriage(this, name);

        synchronized (instances) {
            if (instances.containsKey(name)) {
                throw new EventInstanceInProgressException(name, this.getName());
            }

            instances.put(name, ret);
        }
        return ret;
    }

    /**
     * 释放指定名称的实例
     * @param name 实例名称
     */
    public void disposeInstance(final String name) {
        ess.registerEntry(() -> {
            freeLobbyInstance(name);

            synchronized (instances) {
                instances.remove(name);
            }
        }, SECONDS.toMillis(GameConfig.getServerLong("event_lobby_delay")));
    }

    /**
     * 设置属性
     * @param key 属性键
     * @param value 属性值
     */
    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    /**
     * 设置整数属性
     * @param key 属性键
     * @param value 属性值
     */
    public void setIntProperty(String key, int value) {
        setProperty(key, value);
    }

    /**
     * 设置属性（整数）
     * @param key 属性键
     * @param value 属性值
     */
    public void setProperty(String key, int value) {
        props.setProperty(key, value + "");
    }

    /**
     * 获取属性
     * @param key 属性键
     * @return 属性值
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     * 获取整数属性
     * @param key 属性键
     * @return 属性值
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    /**
     * 设置大厅锁定状态
     * @param lobbyId 大厅ID
     * @param lock 是否锁定
     */
    private void setLockLobby(int lobbyId, boolean lock) {
        lobbyLock.lock();
        try {
            openedLobbys.set(lobbyId, new Pair<>(lock, System.currentTimeMillis()));
        } finally {
            lobbyLock.unlock();
        }
    }

    /**
     * 启动大厅实例
     * @param lobbyId 大厅ID
     * @return 是否成功启动
     */
    private boolean startLobbyInstance(int lobbyId) {
        lobbyLock.lock();
        try {
            // 修正大厅ID范围
            if (lobbyId < 0) {
                lobbyId = 0;
            } else if (lobbyId >= maxLobbys) {
                lobbyId = maxLobbys - 1;
            }

            Pair<Boolean, Long> pair = openedLobbys.get(lobbyId);
            // 大厅空闲、超时未使用、或PQ中无人时，允许启动
            if (!pair.left || System.currentTimeMillis() - pair.right > getEventTimeout() || isNobodyInPQ()) {
                openedLobbys.set(lobbyId, new Pair<>(true, System.currentTimeMillis()));
                return true;
            }

            return false;
        } finally {
            lobbyLock.unlock();
        }
    }

    /**
     * 释放大厅实例
     * @param lobbyName 大厅名称
     */
    private void freeLobbyInstance(String lobbyName) {
        Integer i = instanceLocks.get(lobbyName);
        if (i == null) {
            return;
        }

        instanceLocks.remove(lobbyName);
        if (i > -1) {
            setLockLobby(i, false);
        }
    }

    /**
     * 获取事件名称
     * @return 事件名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取可用的大厅实例ID
     * @return 大厅ID，如果没有可用则返回-1
     */
    private int availableLobbyInstance() {
        int maxLobbies = getMaxLobbies();

        if (maxLobbies > 0) {
            for (int i = 0; i < maxLobbies; i++) {
                if (startLobbyInstance(i)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 获取内部脚本异常消息
     * @param a 异常对象
     * @return 异常消息
     */
    private String getInternalScriptExceptionMessage(Throwable a) {
        if (!(a instanceof ScriptException)) {
            return null;
        }

        while (true) {
            Throwable t = a;
            a = a.getCause();

            if (a == null) {
                return t.getMessage();
            }
        }
    }

    /**
     * 创建事件实例
     * @param name 实例名称
     * @param args 参数
     * @return 事件实例管理器
     * @throws ScriptException 脚本异常
     * @throws NoSuchMethodException 方法不存在异常
     */
    private EventInstanceManager createInstance(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return (EventInstanceManager) iv.invokeFunction(name, args);
    }

    /**
     * 注册事件实例
     * @param eventName 事件名称
     * @param lobbyId 大厅ID
     */
    private void registerEventInstance(String eventName, int lobbyId) {
        Integer oldLobby = instanceLocks.get(eventName);
        if (oldLobby != null) {
            setLockLobby(oldLobby, false);
        }

        instanceLocks.put(eventName, lobbyId);
    }

    /**
     * 启动远征队实例
     * @param exped 远征队
     * @return 是否成功启动
     */
    public boolean startInstance(Expedition exped) {
        return startInstance(-1, exped);
    }

    /**
     * 启动远征队实例（指定大厅）
     * @param lobbyId 大厅ID
     * @param exped 远征队
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Expedition exped) {
        return startInstance(lobbyId, exped, exped.getLeader());
    }

    /**
     * 启动远征队实例（指定大厅和队长）
     * @param lobbyId 大厅ID
     * @param exped 远征队
     * @param leader 队长
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Expedition exped, Character leader) {
        if (this.isDisposed()) {
            return false;
        }

        try {
            // 检查玩家是否已有进行中的创建请求，同时获取信号量许可
            if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, MILLISECONDS)) {
                playerPermit.add(leader.getId());

                startLock.lock();
                try {
                    try {
                        // 未指定大厅时自动分配可用大厅
                        if (lobbyId == -1) {
                            lobbyId = availableLobbyInstance();
                            if (lobbyId == -1) {
                                return false;
                            }
                        } else {
                            // 指定大厅模式下检查大厅是否可用
                            if (!startLobbyInstance(lobbyId)) {
                                return false;
                            }
                        }

                        // 调用脚本层setup函数创建事件实例
                        EventInstanceManager eim;
                        try {
                            eim = createInstance("setup", leader.getClient().getChannel());
                            registerEventInstance(eim.getName(), lobbyId);
                        } catch (ScriptException | NullPointerException e) {
                            // IIP异常表示该实例正在被其他进程创建中，不是真正的错误
                            String message = getInternalScriptExceptionMessage(e);
                            if (message != null && !message.startsWith(EventInstanceInProgressException.EIIP_KEY)) {
                                throw e;
                            }

                            // 创建失败，释放大厅锁
                            if (lobbyId > -1) {
                                setLockLobby(lobbyId, false);
                            }
                            return false;
                        }

                        // 设置队长并注册远征队所有成员
                        eim.setLeader(leader);

                        exped.start();
                        eim.registerExpedition(exped);

                        // 启动事件，调用脚本afterSetup初始化
                        eim.startEvent();
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("Event script startInstance（事件脚本startInstance）", ex);
                    }

                    return true;
                } finally {
                    startLock.unlock();
                    playerPermit.remove(leader.getId());
                    startSemaphore.release();
                }
            }
        } catch (InterruptedException ie) {
            playerPermit.remove(leader.getId());
        }

        return false;
    }

    /**
     * 启动远征队实例（带地图参数，脚本兼容）
     * @param exped 远征队
     * @param map 地图（暂未使用，由事件脚本内部管理）
     * @return 是否成功启动
     */
    public boolean startInstance(Expedition exped, MapleMap map) {
        return startInstance(exped);
    }

    /**
     * 启动远征队实例（带地图和NPC参数，脚本兼容）
     * @param exped 远征队
     * @param map 地图（暂未使用，由事件脚本内部管理）
     * @param npcId NPC ID（暂未使用）
     * @return 是否成功启动
     */
    public boolean startInstance(Expedition exped, MapleMap map, int npcId) {
        return startInstance(exped);
    }

    /**
     * 启动玩家实例
     * @param chr 玩家
     * @return 是否成功启动
     */
    public boolean startInstance(Character chr) {
        return startInstance(-1, chr);
    }

    /**
     * 启动玩家实例（指定大厅）
     * @param lobbyId 大厅ID
     * @param leader 队长
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Character leader) {
        return startInstance(lobbyId, leader, leader, 1);
    }

    /**
     * 启动玩家实例（指定大厅、玩家、队长和难度）
     * @param lobbyId 大厅ID
     * @param chr 玩家
     * @param leader 队长
     * @param difficulty 难度
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Character chr, Character leader, int difficulty) {
        if (this.isDisposed()) {
            return false;
        }

        try {
            if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, MILLISECONDS)) {
                playerPermit.add(leader.getId());

                startLock.lock();
                try {
                    try {
                        if (lobbyId == -1) {
                            lobbyId = availableLobbyInstance();
                            if (lobbyId == -1) {
                                return false;
                            }
                        } else {
                            if (!startLobbyInstance(lobbyId)) {
                                return false;
                            }
                        }

                        EventInstanceManager eim;
                        try {
                            eim = createInstance("setup", difficulty, (lobbyId > -1) ? lobbyId : leader.getId());
                            registerEventInstance(eim.getName(), lobbyId);
                        } catch (ScriptException | NullPointerException e) {
                            String message = getInternalScriptExceptionMessage(e);
                            if (message != null && !message.startsWith(EventInstanceInProgressException.EIIP_KEY)) {
                                throw e;
                            }

                            if (lobbyId > -1) {
                                setLockLobby(lobbyId, false);
                            }
                            return false;
                        }
                        eim.setLeader(leader);

                        if (chr != null) {
                            eim.registerPlayer(chr);
                        }

                        eim.startEvent();
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("Event script startInstance（事件脚本startInstance）", ex);
                    }

                    return true;
                } finally {
                    startLock.unlock();
                    playerPermit.remove(leader.getId());
                    startSemaphore.release();
                }
            }
        } catch (InterruptedException ie) {
            playerPermit.remove(leader.getId());
        }

        return false;
    }

    /**
     * 启动队伍实例（PQ）
     * @param party 队伍
     * @param map 地图
     * @return 是否成功启动
     */
    public boolean startInstance(Party party, MapleMap map) {
        return startInstance(-1, party, map);
    }

    /**
     * 启动队伍实例（PQ，指定大厅）
     * @param lobbyId 大厅ID
     * @param party 队伍
     * @param map 地图
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Party party, MapleMap map) {
        return startInstance(lobbyId, party, map, party.getLeader().getPlayer());
    }

    /**
     * 启动队伍实例（PQ，指定大厅和队长）
     * @param lobbyId 大厅ID
     * @param party 队伍
     * @param map 地图
     * @param leader 队长
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Party party, MapleMap map, Character leader) {
        if (this.isDisposed()) {
            return false;
        }

        try {
            if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, MILLISECONDS)) {
                playerPermit.add(leader.getId());

                startLock.lock();
                try {
                    try {
                        if (lobbyId == -1) {
                            lobbyId = availableLobbyInstance();
                            if (lobbyId == -1) {
                                return false;
                            }
                        } else {
                            if (!startLobbyInstance(lobbyId)) {
                                return false;
                            }
                        }

                        EventInstanceManager eim;
                        try {
                            eim = createInstance("setup", (Object) null);
                            registerEventInstance(eim.getName(), lobbyId);
                        } catch (ScriptException | NullPointerException e) {
                            String message = getInternalScriptExceptionMessage(e);
                            if (message != null && !message.startsWith(EventInstanceInProgressException.EIIP_KEY)) {
                                throw e;
                            }

                            if (lobbyId > -1) {
                                setLockLobby(lobbyId, false);
                            }
                            return false;
                        }

                        eim.setLeader(leader);

                        eim.registerParty(party, map);
                        party.setEligibleMembers(null);

                        eim.startEvent();
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("Event script startInstance（事件脚本startInstance）", ex);
                    }

                    return true;
                } finally {
                    startLock.unlock();
                    playerPermit.remove(leader.getId());
                    startSemaphore.release();
                }
            }
        } catch (InterruptedException ie) {
            playerPermit.remove(leader.getId());
        }

        return false;
    }

    /**
     * 启动队伍实例（PQ，带难度）
     * @param party 队伍
     * @param map 地图
     * @param difficulty 难度
     * @return 是否成功启动
     */
    public boolean startInstance(Party party, MapleMap map, int difficulty) {
        return startInstance(-1, party, map, difficulty);
    }

    /**
     * 启动队伍实例（PQ，指定大厅和难度）
     * @param lobbyId 大厅ID
     * @param party 队伍
     * @param map 地图
     * @param difficulty 难度
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Party party, MapleMap map, int difficulty) {
        return startInstance(lobbyId, party, map, difficulty, party.getLeader().getPlayer());
    }

    /**
     * 启动队伍实例（PQ，指定大厅、难度和队长）
     * @param lobbyId 大厅ID
     * @param party 队伍
     * @param map 地图
     * @param difficulty 难度
     * @param leader 队长
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, Party party, MapleMap map, int difficulty, Character leader) {
        if (this.isDisposed()) {
            return false;
        }

        try {
            if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, MILLISECONDS)) {
                playerPermit.add(leader.getId());

                startLock.lock();
                try {
                    try {
                        if (lobbyId == -1) {
                            lobbyId = availableLobbyInstance();
                            if (lobbyId == -1) {
                                return false;
                            }
                        } else {
                            if (!startLobbyInstance(lobbyId)) {
                                return false;
                            }
                        }

                        EventInstanceManager eim;
                        try {
                            eim = createInstance("setup", difficulty, (lobbyId > -1) ? lobbyId : party.getLeaderId());
                            registerEventInstance(eim.getName(), lobbyId);
                        } catch (ScriptException | NullPointerException e) {
                            String message = getInternalScriptExceptionMessage(e);
                            if (message != null && !message.startsWith(EventInstanceInProgressException.EIIP_KEY)) {
                                throw e;
                            }

                            if (lobbyId > -1) {
                                setLockLobby(lobbyId, false);
                            }
                            return false;
                        }

                        eim.setLeader(leader);

                        eim.registerParty(party, map);
                        party.setEligibleMembers(null);

                        eim.startEvent();
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("Event script startInstance（事件脚本启动实例）", ex);
                    }

                    return true;
                } finally {
                    startLock.unlock();
                    playerPermit.remove(leader.getId());
                    startSemaphore.release();
                }
            }
        } catch (InterruptedException ie) {
            playerPermit.remove(leader.getId());
        }

        return false;
    }

    /**
     * 启动非PQ事件实例
     * @param eim 事件实例管理器
     * @param ldr 队长名称
     * @return 是否成功启动
     */
    public boolean startInstance(EventInstanceManager eim, String ldr) {
        return startInstance(-1, eim, ldr);
    }

    /**
     * 启动非PQ事件实例（指定队长）
     * @param eim 事件实例管理器
     * @param ldr 队长
     * @return 是否成功启动
     */
    public boolean startInstance(EventInstanceManager eim, Character ldr) {
        return startInstance(-1, eim, ldr.getName(), ldr);
    }

    /**
     * 启动非PQ事件实例（指定大厅）
     * @param lobbyId 大厅ID
     * @param eim 事件实例管理器
     * @param ldr 队长名称
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, EventInstanceManager eim, String ldr) {
        return startInstance(-1, eim, ldr, eim.getEm().getChannelServer().getPlayerStorage().getCharacterByName(ldr));
    }

    /**
     * 启动非PQ事件实例（指定大厅和队长）
     * @param lobbyId 大厅ID
     * @param eim 事件实例管理器
     * @param ldr 队长名称
     * @param leader 队长
     * @return 是否成功启动
     */
    public boolean startInstance(int lobbyId, EventInstanceManager eim, String ldr, Character leader) {
        if (this.isDisposed()) {
            return false;
        }

        try {
            if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, MILLISECONDS)) {
                playerPermit.add(leader.getId());

                startLock.lock();
                try {
                    try {
                        if (lobbyId == -1) {
                            lobbyId = availableLobbyInstance();
                            if (lobbyId == -1) {
                                return false;
                            }
                        } else {
                            if (!startLobbyInstance(lobbyId)) {
                                return false;
                            }
                        }

                        if (eim == null) {
                            if (lobbyId > -1) {
                                setLockLobby(lobbyId, false);
                            }
                            return false;
                        }
                        registerEventInstance(eim.getName(), lobbyId);
                        eim.setLeader(leader);

                        iv.invokeFunction("setup", eim);
                        eim.setProperty("leader", ldr);

                        eim.startEvent();
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("Event script startInstance（事件脚本启动实例）", ex);
                    }

                    return true;
                } finally {
                    startLock.unlock();
                    playerPermit.remove(leader.getId());
                    startSemaphore.release();
                }
            }
        } catch (InterruptedException ie) {
            playerPermit.remove(leader.getId());
        }

        return false;
    }

    /**
     * 获取符合条件的队伍成员
     * @param party 队伍
     * @return 符合条件的成员列表
     */
    public List<PartyCharacter> getEligibleParty(Party party) {
        if (party == null) {
            return new ArrayList<>();
        }
        try {
            Object o = iv.invokeFunction("getEligibleParty", party.getPartyMembersOnline());

            if (o instanceof PartyCharacter[] partyChrs) {
                final List<PartyCharacter> eligibleParty = new ArrayList<>(Arrays.asList(partyChrs));
                party.setEligibleMembers(eligibleParty);
                return eligibleParty;
            }
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * 清除PQ
     * @param eim 事件实例管理器
     */
    public void clearPQ(EventInstanceManager eim) {
        try {
            iv.invokeFunction("clearPQ", eim);
        } catch (ScriptException | NoSuchMethodException ex) {
            log.error("Event script clearPQ（事件脚本清除PQ）", ex);
        }
    }

    /**
     * 清除PQ并传送到指定地图
     * @param eim 事件实例管理器
     * @param toMap 目标地图
     */
    public void clearPQ(EventInstanceManager eim, MapleMap toMap) {
        try {
            iv.invokeFunction("clearPQ", eim, toMap);
        } catch (ScriptException | NoSuchMethodException ex) {
            log.error("Event script clearPQ（事件脚本清除PQ）", ex);
        }
    }

    /**
     * 获取事件超时时间
     * 默认2小时，可在事件脚本中通过getEventTimeout函数自定义
     *
     * @return 超时时间（毫秒）
     */
    public long getEventTimeout() {
        // 默认超时时间 2 小时
        long timeout = 7200000L;
        try {
            // 可在事件脚本中定义超时时间，超过该时间锁自动失效
            timeout = (long) iv.invokeFunction("getEventTimeout");
        } catch (ScriptException | NoSuchMethodException ignored) {

        }
        return timeout;
    }

    /**
     * 检查PQ中是否无人
     * 遍历事件地图列表，检查所有地图上是否有玩家
     *
     * @return true表示PQ中无人
     */
    public boolean isNobodyInPQ() {
        try {
            boolean nobody = true;
            // 可在事件脚本中定义事件地图列表，检查地图上是否有人
            Object o = iv.invokeFunction("getEventMaps");
            if (o instanceof List<?> mapIds) {
                for (Object mapId : mapIds) {
                    int id;
                    if (mapId instanceof Number) {
                        id = ((Number) mapId).intValue();
                    } else {
                        id = Integer.parseInt(mapId.toString());
                    }
                    // 跳过无效的地图ID
                    if (id <= 0) {
                        continue;
                    }
                    if (!cserv.getMapFactory().getMap(id).getAllPlayers().isEmpty()) {
                        nobody = false;
                        break;
                    }
                }

            }
            return nobody;
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * 获取怪物对象
     * @param mid 怪物ID
     * @return 怪物对象
     */
    public Monster getMonster(int mid) {
        return (LifeFactory.getMonster(mid));
    }

    /**
     * 创建怪物属性覆盖对象，用于在事件脚本中动态修改怪物属性
     * @return 新的怪物属性覆盖对象
     */
    public OverrideMonsterStats newMonsterStats() {
        return new OverrideMonsterStats();
    }

    /**
     * 通知公会准备就绪
     * @param guildId 公会ID
     */
    private void exportReadyGuild(Integer guildId) {
        Guild mg = server.getGuild(guildId);
        String callout = "[公会任务] 您的公会已成功报名参加频道 " + this.getChannelServer().getId() + " 的" +
                "【家族对抗赛】，当前已进入战略准备阶段。3分钟后将禁止新成员加入任务。" +
                " 请前往勇士之都挖掘现场寻找NPC双了解更多详情。";

        mg.dropMessage(6, callout);
    }

    /**
     * 通知公会队列位置
     * @param guildId 公会ID
     * @param place 队列位置
     */
    private void exportMovedQueueToGuild(Integer guildId, int place) {
        Guild mg = server.getGuild(guildId);
        String callout = "[公会任务] 您的公会已成功报名参加频道 " + this.getChannelServer().getId() + " 的" +
                "【家族对抗赛】，当前在等待队列中排名第 " + GameConstants.ordinal(place) + " 位。";

        mg.dropMessage(6, callout);
    }

    /**
     * 获取下一个排队的公会
     * @return 公会ID和会长ID列表
     */
    private List<Integer> getNextGuildQueue() {
        synchronized (queuedGuilds) {
            Integer guildId = queuedGuilds.poll();
            if (guildId == null) {
                return null;
            }

            wserv.removeGuildQueued(guildId);
            Integer leaderId = queuedGuildLeaders.remove(guildId);

            int place = 1;
            for (Integer i : queuedGuilds) {
                exportMovedQueueToGuild(i, place);
                place++;
            }

            List<Integer> list = new ArrayList<>(2);
            list.add(guildId);
            list.add(leaderId);
            return list;
        }
    }

    /**
     * 检查队列是否已满
     * @return 是否已满
     */
    public boolean isQueueFull() {
        synchronized (queuedGuilds) {
            return queuedGuilds.size() >= GameConfig.getServerInt("event_max_guild_queue");
        }
    }

    /**
     * 获取队列大小
     * @return 队列大小
     */
    public int getQueueSize() {
        synchronized (queuedGuilds) {
            return queuedGuilds.size();
        }
    }

    /**
     * 添加公会到队列
     * @param guildId 公会ID
     * @param leaderId 会长ID
     * @return 添加结果（-1=已在队列，0=队列已满，1=成功加入，2=成功并立即开始）
     */
    public byte addGuildToQueue(Integer guildId, Integer leaderId) {
        if (wserv.isGuildQueued(guildId)) {
            return -1;
        }

        if (!isQueueFull()) {
            boolean canStartAhead;
            synchronized (queuedGuilds) {
                canStartAhead = queuedGuilds.isEmpty();

                queuedGuilds.add(guildId);
                wserv.putGuildQueued(guildId);
                queuedGuildLeaders.put(guildId, leaderId);

                int place = queuedGuilds.size();
                exportMovedQueueToGuild(guildId, place);
            }

            if (canStartAhead) {
                if (!attemptStartGuildInstance()) {
                    synchronized (queuedGuilds) {
                        queuedGuilds.add(guildId);
                        wserv.putGuildQueued(guildId);
                        queuedGuildLeaders.put(guildId, leaderId);
                    }
                } else {
                    return 2;
                }
            }

            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 尝试启动公会实例
     * @return 是否成功启动
     */
    public boolean attemptStartGuildInstance() {
        Character chr = null;
        List<Integer> guildInstance = null;
        while (chr == null) {
            guildInstance = getNextGuildQueue();
            if (guildInstance == null) {
                return false;
            }

            chr = cserv.getPlayerStorage().getCharacterById(guildInstance.get(1));
        }

        if (startInstance(chr)) {
            exportReadyGuild(guildInstance.get(0));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 强制开始任务
     * @param chr 玩家
     * @param id 任务ID
     * @param npcid NPC ID
     */
    public void startQuest(Character chr, int id, int npcid) {
        try {
            Quest.getInstance(id).forceStart(chr, npcid);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 强制完成任务
     * @param chr 玩家
     * @param id 任务ID
     * @param npcid NPC ID
     */
    public void completeQuest(Character chr, int id, int npcid) {
        try {
            Quest.getInstance(id).forceComplete(chr, npcid);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 修正运输时间
     * @param travelTime 旅行时间
     * @return 修正后的运输时间
     */
    public int getTransportationTime(int travelTime) {
        return this.getWorldServer().getTransportationTime(travelTime);
    }

    /**
     * 修正Boss刷新时间
     * @param BossTime 刷新时间
     * @return 修正后的Boss刷新时间
     */
    public int getBossTime(int BossTime) {
        return (int) (BossTime * GameConfig.getServerFloat("boss_respawn_mob_time_rate"));
    }

    /**
     * 设置世界掉落倍率
     * @param drop 掉落倍率，1.0=默认，2.0=双倍
     */
    public void setDropRate(float drop) {
        this.getWorldServer().setDropRate(drop);
    }

    /**
     * 设置世界经验倍率
     * @param exp 经验倍率，1.0=默认，2.0=双倍
     */
    public void setExpRate(float exp) {
        this.getWorldServer().setExpRate(exp);
    }

    /**
     * 开启双倍经验活动（脚本兼容方法）
     * @param multiplier 经验倍率，如4表示4倍经验
     */
    public void startDoubleExp(int multiplier) {
        this.getWorldServer().setExpRate((float) multiplier);
    }

    /**
     * 向全世界广播黄色系统消息（复用PacketCreator.sendYellowTip + World.broadcastPacket）
     * @param msg 消息内容
     */
    public void broadcastYellowMsg(String msg) {
        this.getWorldServer().broadcastPacket(PacketCreator.sendYellowTip(msg));
    }

    /**
     * 广播服务器公告消息
     * @param type 消息类型：5=粉色文字，6=浅蓝色文字，>1000000=物品公告(顶部滚动)
     * @param msg 消息内容
     * @param gmOnly true=仅GM可见（频道内），false=全服可见
     */
    /**
     * 广播消息（便捷重载，默认全服可见type=5，非GM专属）
     * GraalJS严格参数匹配，与Nashorn不同，需为重载提供完整签名
     */
    public void broadcastServerMsg(String msg) {
        broadcastServerMsg(5, msg, false);
    }

    public void broadcastServerMsg(int type, String msg, boolean gmOnly) {
        Packet packet;
        if (type >= 1000000) {
            // 物品类型公告（使用顶部滚动消息展示物品相关公告）
            packet = PacketCreator.serverNotice(4, msg);
        } else {
            packet = PacketCreator.serverNotice(Math.abs(type), msg);
        }
        if (gmOnly) {
            // 仅GM可见时使用频道GM广播
            cserv.broadcastGMPacket(packet);
        } else {
            this.getWorldServer().broadcastPacket(packet);
        }
    }

    /**
     * 广播玩家消息（复用Channel.dropMessage向当前频道所有玩家发送）
     * @param type 消息类型（同serverNotice的type参数）
     * @param msg 消息内容
     */
    public void broadcastPlayerMsg(int type, String msg) {
        cserv.dropMessage(type, msg);
    }

    /**
     * 将指定地图的所有玩家传送到目标地图（复用MapleMap.warpEveryone）
     * @param fromMapId 源地图ID
     * @param toMapId 目标地图ID
     */
    public void warpAllPlayer(int fromMapId, int toMapId) {
        MapleMap fromMap = cserv.getMapFactory().getMap(fromMapId);
        if (fromMap != null) {
            fromMap.warpEveryone(toMapId);
        }
    }

    /**
     * 广播船只状态变化（兼容zh-CN交通脚本中的em.broadcastShip(mapId, state)调用）
     * state: 1=靠岸(docked/true), 2=离港(departing/false)
     * @param mapId 地图ID（如200000151=Orbis码头, 260000100=Ariant码头）
     * @param state 船只状态：1=到港靠岸，2=离港出发
     */
    public void broadcastShip(int mapId, int state) {
        MapleMap map = cserv.getMapFactory().getMap(mapId);
        if (map != null) {
            map.broadcastShip(state == 1);
        } else {
            log.warn("船只状态广播失败：地图 {} 不存在，无法广播 state={}（事件：{}）", mapId, state, name);
        }
    }

    /**
     * 设置世界事件标记（用于自动事件系统）
     */
    public void setWorldEvent() {
        setProperty("worldEvent", "true");
    }

    /**
     * 调度随机事件（由自动化事件脚本调用，选择一个随机事件启动）
     */
    public void scheduleRandomEvent() {
        // 标记随机事件已触发，具体事件选择由脚本层控制
        setProperty("randomEvent", "true");
    }

    /**
     * 填充EIM队列
     */
    private void fillEimQueue() {
        // 启动新线程异步填充就绪实例队列
        ThreadManager.getInstance().newTask(new EventManagerTask());
    }

    /**
     * 获取准备就绪的实例
     * @return 事件实例管理器
     */
    private EventInstanceManager getReadyInstance() {
        queueLock.lock();
        try {
            if (readyInstances.isEmpty()) {
                fillEimQueue();
                return null;
            }

            EventInstanceManager eim = readyInstances.remove(0);
            fillEimQueue();

            return eim;
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * 实例化队列中的实例
     */
    private void instantiateQueuedInstance() {
        int nextEventId;
        queueLock.lock();
        try {
            if (this.isDisposed() || readyInstances.size() + onLoadInstances >= Math.ceil((double) maxLobbys / 3.0)) {
                return;
            }

            onLoadInstances++;
            nextEventId = readyId;
            readyId++;
        } finally {
            queueLock.unlock();
        }

        EventInstanceManager eim = new EventInstanceManager(this, "sampleName" + nextEventId);
        queueLock.lock();
        try {
            // 事件管理器已释放则不再继续填充
            if (this.isDisposed()) {
                return;
            }

            readyInstances.add(eim);
            onLoadInstances--;
        } finally {
            queueLock.unlock();
        }

        // 持久填充队列直到达到预设阈值
        instantiateQueuedInstance();
    }

    /**
     * 事件管理器任务类
     */
    private class EventManagerTask implements Runnable {
        @Override
        public void run() {
            instantiateQueuedInstance();
        }
    }
}