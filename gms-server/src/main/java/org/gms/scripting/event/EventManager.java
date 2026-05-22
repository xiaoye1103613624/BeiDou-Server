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
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.World;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.scheduler.EventScriptScheduler;
import org.gms.server.Marriage;
import org.gms.server.ThreadManager;
import org.gms.server.expeditions.Expedition;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
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
 * 【类型】EventManager（class），包 {@code org.gms.scripting.event}。
 * 事件管理器，负责管理事件实例（EventInstanceManager）的生命周期，包括创建、注册怪物、玩家进出、
 * 大厅调度、队列管理等功能。每个事件脚本对应一个 EventManager，由 {@link EventScriptManager} 创建。
 *
 * @author Matze
 * @author Ronan
 */
public class EventManager {
    private static final Logger log = LoggerFactory.getLogger(EventManager.class);
    /** 可调用的 JS 脚本引擎，用于调用事件脚本中的函数 */
    private Invocable iv;
    /** 所属频道服务器 */
    private Channel cserv;
    /** 所属世界服务器 */
    private World wserv;
    /** 主服务器实例 */
    private Server server;
    /** 事件脚本调度器，管理定时任务 */
    private final EventScriptScheduler ess = new EventScriptScheduler();
    /** 事件实例名 -> EventInstanceManager 的映射 */
    private final Map<String, EventInstanceManager> instances = new HashMap<>();
    /** 实例名 -> 大厅ID 的锁定映射 */
    private final Map<String, Integer> instanceLocks = new HashMap<>();
    /** 公会排队队列 */
    private final Queue<Integer> queuedGuilds = new LinkedList<>();
    /** 排队公会ID -> 会长ID 的映射 */
    private final Map<Integer, Integer> queuedGuildLeaders = new HashMap<>();
    /** 大厅列表，记录每个大厅的锁定状态与时间戳 */
    private final List<Pair<Boolean, Long>> openedLobbys;
    /** 预创建好的就绪实例队列 */
    private final List<EventInstanceManager> readyInstances = new LinkedList<>();
    /** 就绪实例ID自增计数器与正在加载的实例数 */
    private Integer readyId = 0, onLoadInstances = 0;
    /** 事件属性配置 */
    private final Properties props = new Properties();
    /** 事件名称 */
    private final String name;
    /** 大厅锁 */
    private final Lock lobbyLock = new ReentrantLock();
    /** 队列锁 */
    private final Lock queueLock = new ReentrantLock();
    /** 启动锁 */
    private final Lock startLock = new ReentrantLock();

    /** 已获启动许可的玩家ID集合 */
    private final Set<Integer> playerPermit = new HashSet<>();
    /** 启动信号量，限制并发启动数 */
    private final Semaphore startSemaphore = new Semaphore(7);

    /** 一个事件管理器最多支持同时运行的大厅数量 */
    private static final int maxLobbys = 8;

    /**
     * 构造函数。
     *
     * @param cserv 所属频道服务器
     * @param iv    可调用的 JS 脚本引擎
     * @param name  事件名称
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
     * 检查此事件管理器是否已被释放/销毁。
     *
     * @return true 表示已释放
     */
    private boolean isDisposed() {
        return onLoadInstances <= -1000;
    }

    /**
     * 取消事件管理器，释放所有实例与资源。
     * 确保在没有玩家在线时调用。
     */
    public void cancel() {
        ess.dispose();

        try {
            iv.invokeFunction("cancelSchedule", (Object) null);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        Collection<EventInstanceManager> eimList;
        synchronized (instances) {
            eimList = getInstances();
            instances.clear();
        }

        for (EventInstanceManager eim : eimList) {
            eim.dispose(true);
        }

        List<EventInstanceManager> readyEims;
        queueLock.lock();
        try {
            readyEims = new ArrayList<>(readyInstances);
            readyInstances.clear();
            onLoadInstances = Integer.MIN_VALUE / 2;
        } finally {
            queueLock.unlock();
        }

        for (EventInstanceManager eim : readyEims) {
            eim.dispose(true);
        }

        props.clear();
        cserv = null;
        wserv = null;
        server = null;
        iv = null;
    }

    /**
     * 将 Object 列表转换为 Integer 列表。
     *
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
     * 获取大厅延迟时间（毫秒），从全局配置读取。
     *
     * @return 延迟毫秒数
     */
    public long getLobbyDelay() {
        return GameConfig.getServerLong("event_lobby_delay");
    }

    /**
     * 获取最大大厅数量。优先调用 JS 脚本中的 {@code getMaxLobbies()}，
     * 若未定义则使用默认值 {@code maxLobbys}。
     *
     * @return 最大大厅数量
     */
    private int getMaxLobbies() {
        try {
            return (int) iv.invokeFunction("getMaxLobbies");
        } catch (ScriptException | NoSuchMethodException ex) {
            return maxLobbys;
        }
    }

    /**
     * 调度一个事件方法，延迟指定毫秒后执行。
     *
     * @param methodName 要调用的 JS 方法名
     * @param delay      延迟时间（毫秒）
     * @return 可取消的调度 Future
     */
    public EventScheduledFuture schedule(String methodName, long delay) {
        return schedule(methodName, null, delay);
    }

    /**
     * 调度一个事件方法（带 EventInstanceManager 参数），延迟指定毫秒后执行。
     *
     * @param methodName 要调用的 JS 方法名
     * @param eim        事件实例管理器
     * @param delay      延迟时间（毫秒）
     * @return 可取消的调度 Future
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
     * 在指定的绝对时间戳调度一个事件方法。
     *
     * @param methodName 要调用的 JS 方法名
     * @param timestamp  绝对时间戳（毫秒）
     * @return 可取消的调度 Future
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

    /** @return 所属世界服务器 */
    public World getWorldServer() {
        return wserv;
    }

    /** @return 所属频道服务器 */
    public Channel getChannelServer() {
        return cserv;
    }

    /** @return 可调用的 JS 脚本引擎 */
    public Invocable getIv() {
        return iv;
    }

    /**
     * 根据名称获取事件实例。
     *
     * @param name 实例名称
     * @return 对应的事件实例管理器，不存在则返回 null
     */
    public EventInstanceManager getInstance(String name) {
        return instances.get(name);
    }

    /**
     * 获取所有事件实例的集合。
     *
     * @return 事件实例管理器集合
     */
    public Collection<EventInstanceManager> getInstances() {
        synchronized (instances) {
            return new LinkedList<>(instances.values());
        }
    }

    /**
     * 创建一个新的事件实例。
     *
     * @param name 实例名称
     * @return 新创建的事件实例管理器
     * @throws EventInstanceInProgressException 如果同名实例已存在
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
     * 创建一个新的婚姻事件实例。
     *
     * @param name 实例名称
     * @return 婚姻事件实例
     * @throws EventInstanceInProgressException 如果同名实例已存在
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
     * 延迟释放指定名称的实例。
     *
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

    /** 设置字符串属性 */
    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    /** 设置整数属性 */
    public void setIntProperty(String key, int value) {
        setProperty(key, value);
    }

    /** 设置整数属性（内部转字符串） */
    public void setProperty(String key, int value) {
        props.setProperty(key, value + "");
    }

    /** 获取字符串属性 */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /** 获取整数属性 */
    public int getIntProperty(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    /**
     * 设置大厅锁定状态。
     *
     * @param lobbyId 大厅ID
     * @param lock    true 锁定，false 解锁
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
     * 尝试启动指定大厅的实例。若大厅未锁定、已超时或 PQ 地图无人，则允许启动。
     *
     * @param lobbyId 大厅ID
     * @return true 表示可以启动
     */
    private boolean startLobbyInstance(int lobbyId) {
        lobbyLock.lock();
        try {
            if (lobbyId < 0) {
                lobbyId = 0;
            } else if (lobbyId >= maxLobbys) {
                lobbyId = maxLobbys - 1;
            }

            Pair<Boolean, Long> pair = openedLobbys.get(lobbyId);
            if (!pair.left || System.currentTimeMillis() - pair.right > getEventTimeout() || isNobodyInPQ()) {
                openedLobbys.set(lobbyId, new  Pair<>(true, System.currentTimeMillis()));
                return true;
            }

            return false;
        } finally {
            lobbyLock.unlock();
        }
    }

    /**
     * 释放大厅实例的锁定。
     *
     * @param lobbyName 大厅/实例名称
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

    /** @return 事件名称 */
    public String getName() {
        return name;
    }

    /**
     * 获取一个可用的大厅ID。
     *
     * @return 大厅ID，没有可用大厅时返回 -1
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
     * 从异常链中提取内部脚本异常消息。
     *
     * @param a 异常对象
     * @return 异常消息，非 ScriptException 时返回 null
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
     * 通过调用 JS 脚本中的方法创建事件实例。
     *
     * @param name JS 方法名（通常为 "setup"）
     * @param args 传给 JS 方法的参数
     * @return 创建好的 EventInstanceManager
     * @throws ScriptException     脚本执行异常
     * @throws NoSuchMethodException JS 方法不存在
     */
    private EventInstanceManager createInstance(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return (EventInstanceManager) iv.invokeFunction(name, args);
    }

    /**
     * 注册事件实例与大厅ID的绑定关系。
     *
     * @param eventName 事件实例名称
     * @param lobbyId   大厅ID
     */
    private void registerEventInstance(String eventName, int lobbyId) {
        Integer oldLobby = instanceLocks.get(eventName);
        if (oldLobby != null) {
            setLockLobby(oldLobby, false);
        }

        instanceLocks.put(eventName, lobbyId);
    }

    // ==================== startInstance 重载方法 ====================

    /** 启动远征队实例 */
    public boolean startInstance(Expedition exped) {
        return startInstance(-1, exped);
    }

    /** 启动远征队实例（指定大厅） */
    public boolean startInstance(int lobbyId, Expedition exped) {
        return startInstance(lobbyId, exped, exped.getLeader());
    }

    /**
     * 启动远征队实例（指定大厅与队长）。
     */
    public boolean startInstance(int lobbyId, Expedition exped, Character leader) {
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
                            eim = createInstance("setup", leader.getClient().getChannel());
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

                        exped.start();
                        eim.registerExpedition(exped);

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

    /** 启动单人实例 */
    public boolean startInstance(Character chr) {
        return startInstance(-1, chr);
    }

    /** 启动单人实例（指定大厅） */
    public boolean startInstance(int lobbyId, Character leader) {
        return startInstance(lobbyId, leader, leader, 1);
    }

    /**
     * 启动单人实例（指定大厅、玩家、队长和难度）。
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

    /** 启动队伍 PQ 实例 */
    public boolean startInstance(Party party, MapleMap map) {
        return startInstance(-1, party, map);
    }

    /** 启动队伍 PQ 实例（指定大厅） */
    public boolean startInstance(int lobbyId, Party party, MapleMap map) {
        return startInstance(lobbyId, party, map, party.getLeader().getPlayer());
    }

    /**
     * 启动队伍 PQ 实例（指定大厅与队长）。
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

    /** 启动队伍 PQ 实例（带难度） */
    public boolean startInstance(Party party, MapleMap map, int difficulty) {
        return startInstance(-1, party, map, difficulty);
    }

    /** 启动队伍 PQ 实例（指定大厅和难度） */
    public boolean startInstance(int lobbyId, Party party, MapleMap map, int difficulty) {
        return startInstance(lobbyId, party, map, difficulty, party.getLeader().getPlayer());
    }

    /**
     * 启动队伍 PQ 实例（指定大厅、难度和队长）。
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

    /** 启动非 PQ 事件实例 */
    public boolean startInstance(EventInstanceManager eim, String ldr) {
        return startInstance(-1, eim, ldr);
    }

    /** 启动非 PQ 事件实例（指定队长对象） */
    public boolean startInstance(EventInstanceManager eim, Character ldr) {
        return startInstance(-1, eim, ldr.getName(), ldr);
    }

    /** 启动非 PQ 事件实例（指定大厅） */
    public boolean startInstance(int lobbyId, EventInstanceManager eim, String ldr) {
        return startInstance(-1, eim, ldr, eim.getEm().getChannelServer().getPlayerStorage().getCharacterByName(ldr));
    }

    /**
     * 启动非 PQ 事件实例（指定大厅与队长）。
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
     * 获取队伍中符合事件条件的成员列表（通过调用 JS 脚本的 {@code getEligibleParty}）。
     *
     * @param party 队伍
     * @return 符合条件的队伍成员列表
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

    /** 清除 PQ 事件 */
    public void clearPQ(EventInstanceManager eim) {
        try {
            iv.invokeFunction("clearPQ", eim);
        } catch (ScriptException | NoSuchMethodException ex) {
            log.error("Event script clearPQ（事件脚本清除PQ）", ex);
        }
    }

    /** 清除 PQ 事件并传送到指定地图 */
    public void clearPQ(EventInstanceManager eim, MapleMap toMap) {
        try {
            iv.invokeFunction("clearPQ", eim, toMap);
        } catch (ScriptException | NoSuchMethodException ex) {
            log.error("Event script clearPQ（事件脚本清除PQ）", ex);
        }
    }

    /**
     * 获取事件超时时间（毫秒），默认 2 小时。
     * 由 JS 脚本的 {@code getEventTimeout()} 定义，超时后大厅锁自动失效。
     *
     * @return 超时毫秒数
     */
    public long getEventTimeout() {
        long timeout = 7200000L;
        try {
            timeout = (long) iv.invokeFunction("getEventTimeout");
        } catch (ScriptException | NoSuchMethodException ignored) {

        }
        return timeout;
    }

    /**
     * 检查事件地图上是否无人。
     * 由 JS 脚本的 {@code getEventMaps()} 定义事件地图列表。
     *
     * @return true 表示所有事件地图上都没有玩家
     */
    public boolean isNobodyInPQ() {
        try {
            boolean nobody = true;
            Object o = iv.invokeFunction("getEventMaps");
            if (o instanceof List<?> mapIds) {
                for (Object mapId : mapIds) {
                    int id;
                    if (mapId instanceof Number) {
                        id = ((Number) mapId).intValue();
                    } else {
                        id = Integer.parseInt(mapId.toString());
                    }
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
     * 根据怪物ID创建 Monster 对象。
     *
     * @param mid 怪物ID（WZ 中的 mob ID）
     * @return 怪物对象
     */
    public Monster getMonster(int mid) {
        return (LifeFactory.getMonster(mid));
    }

    /** 通知已排队的公会准备就绪 */
    private void exportReadyGuild(Integer guildId) {
        Guild mg = server.getGuild(guildId);
        String callout = "[公会任务] 您的公会已成功报名参加频道 " + this.getChannelServer().getId() + " 的" +
                "【家族对抗赛】，当前已进入战略准备阶段。3分钟后将禁止新成员加入任务。" +
                " 请前往勇士之都挖掘现场寻找NPC双了解更多详情。";

        mg.dropMessage(6, callout);
    }

    /** 通知公会在队列中的新位置 */
    private void exportMovedQueueToGuild(Integer guildId, int place) {
        Guild mg = server.getGuild(guildId);
        String callout = "[公会任务] 您的公会已成功报名参加频道 " + this.getChannelServer().getId() + " 的" +
                "【家族对抗赛】，当前在等待队列中排名第 " + GameConstants.ordinal(place) + " 位。";

        mg.dropMessage(6, callout);
    }

    /**
     * 从队列中取出下一个排队的公会。
     *
     * @return 包含 [公会ID, 会长ID] 的列表，队列为空时返回 null
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

    /** @return true 表示公会排队队列已满 */
    public boolean isQueueFull() {
        synchronized (queuedGuilds) {
            return queuedGuilds.size() >= GameConfig.getServerInt("event_max_guild_queue");
        }
    }

    /** @return 当前排队公会数量 */
    public int getQueueSize() {
        synchronized (queuedGuilds) {
            return queuedGuilds.size();
        }
    }

    /**
     * 将公会加入排队队列。
     *
     * @param guildId  公会ID
     * @param leaderId 会长ID
     * @return -1=已在队列中，0=队列已满，1=成功加入队列，2=成功加入并立即开始
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
     * 尝试为队列中的下一个公会启动事件实例。
     *
     * @return true 表示成功启动
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

    /** 强制开始一个任务 */
    public void startQuest(Character chr, int id, int npcid) {
        try {
            Quest.getInstance(id).forceStart(chr, npcid);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /** 强制完成一个任务 */
    public void completeQuest(Character chr, int id, int npcid) {
        try {
            Quest.getInstance(id).forceComplete(chr, npcid);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取修正后的运输时间。
     *
     * @param travelTime 原始旅行时间
     * @return 修正后的运输时间
     */
    public int getTransportationTime(int travelTime) {
        return this.getWorldServer().getTransportationTime(travelTime);
    }

    /**
     * 获取修正后的 Boss 刷新时间。
     *
     * @param BossTime 原始 Boss 刷新时间
     * @return 应用倍率后的刷新时间
     */
    public int getBossTime(int BossTime) {
        return (int) (BossTime * GameConfig.getServerFloat("boss_respawn_mob_time_rate"));
    }

    /** 触发异步填充预创建实例队列 */
    private void fillEimQueue() {
        ThreadManager.getInstance().newTask(new EventManagerTask());
    }

    /**
     * 从就绪队列中取出一个预创建的 EventInstanceManager。
     *
     * @return 就绪的实例，队列为空时返回 null
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

    /** 实例化一个 EventInstanceManager 并放入就绪队列（持续填充直到阈值） */
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
            if (this.isDisposed()) {
                return;
            }

            readyInstances.add(eim);
            onLoadInstances--;
        } finally {
            queueLock.unlock();
        }

        instantiateQueuedInstance();
    }

    /**
     * 异步填充就绪实例队列的任务 Runnable。
     */
    private class EventManagerTask implements Runnable {
        @Override
        public void run() {
            instantiateQueuedInstance();
        }
    }
}