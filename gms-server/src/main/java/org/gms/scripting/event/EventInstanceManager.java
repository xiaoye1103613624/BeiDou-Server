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
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.config.GameConfig;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.server.coordinator.world.EventRecallCoordinator;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.util.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.scripting.event.scheduler.EventScriptScheduler;
import org.gms.server.ItemInformationProvider;
import org.gms.server.StatEffect;
import org.gms.server.ThreadManager;
import org.gms.server.TimerManager;
import org.gms.server.expeditions.Expedition;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.NPC;
import org.gms.server.maps.MapManager;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Portal;
import org.gms.server.maps.Reactor;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import javax.script.ScriptException;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 事件实例管理器类，负责管理游戏内事件实例的创建、运行和销毁。
 * 处理玩家加入/退出、怪物生成/击杀、奖励发放、地图切换等事件相关逻辑。
 * 提供线程安全的事件状态管理和脚本调度功能。
 */
public class EventInstanceManager {
    private static final Logger log = LoggerFactory.getLogger(EventInstanceManager.class);

    /** 存储参与事件的玩家，key为玩家ID */
    private final Map<Integer, Character> chars = new HashMap<>();
    /** 事件队伍领袖ID，-1表示未设置 */
    private int leaderId = -1;
    /** 事件中生成的怪物列表 */
    private final List<Monster> mobs = new LinkedList<>();
    /** 玩家击杀计数，key为玩家对象 */
    private final Map<Character, Integer> killCount = new HashMap<>();
    /** 所属事件管理器 */
    private EventManager em;
    /** 事件脚本调度器，管理延时任务 */
    private EventScriptScheduler ess;
    /** 地图管理器，负责事件专属地图实例的创建与管理 */
    private MapManager mapManager;
    /** 事件实例名称 */
    private String name;

    /** 事件属性存储（字符串键值对） */
    private final Properties props = new Properties();
    /** 事件对象属性存储，支持任意类型值 */
    private final Map<String, Object> objectProps = new HashMap<>();

    /** 事件开始时间戳 */
    private long timeStarted = 0;
    /** 事件总时长（毫秒） */
    private long eventTime = 0;

    /** 事件关联的远征队 */
    private Expedition expedition = null;

    /** 事件使用的地图ID列表 */
    private final List<Integer> mapIds = new LinkedList<>();

    /** 读写锁的读锁，用于遍历玩家列表等读操作 */
    private final Lock readLock;
    /** 读写锁的写锁，用于注册/注销玩家等写操作 */
    private final Lock writeLock;

    /** 属性操作锁，保证props和objectProps的线程安全 */
    private final Lock propertyLock = new ReentrantLock(true);
    /** 脚本操作锁，保证脚本调用与事件状态变更的互斥 */
    private final Lock scriptLock = new ReentrantLock(true);

    /** 事件调度任务句柄，用于取消定时任务 */
    private ScheduledFuture<?> event_schedule = null;

    /** 是否已销毁 */
    private boolean disposed = false;
    /** 事件是否已完成通关 */
    private boolean eventCleared = false;
    /** 事件是否已开始 */
    private boolean eventStarted = false;

    /** 事件奖励物品集合，key为事件等级 */
    private final Map<Integer, List<Integer>> collectionSet = new HashMap<>(GameConfig.getServerInt("max_event_levels"));
    /** 事件奖励物品数量，key为事件等级 */
    private final Map<Integer, List<Integer>> collectionQty = new HashMap<>(GameConfig.getServerInt("max_event_levels"));
    /** 事件奖励经验值，key为事件等级 */
    private final Map<Integer, Integer> collectionExp = new HashMap<>(GameConfig.getServerInt("max_event_levels"));

    /** 清理阶段奖励经验值列表 */
    private final List<Integer> onMapClearExp = new ArrayList<>();
    /** 清理阶段奖励金币列表 */
    private final List<Integer> onMapClearMeso = new ArrayList<>();

    /** 玩家状态网格，key为玩家ID，value为状态值 */
    private final Map<Integer, Integer> playerGrid = new HashMap<>();

    /** 已开启的门记录，key为地图ID，value为(门对象名, 新状态) */
    private final Map<Integer, Pair<String, Integer>> openedGates = new HashMap<>();

    /** 事件专属物品ID集合，用于玩家退出时自动回收 */
    private final Set<Integer> exclusiveItems = new HashSet<>();

    /**
     * 构造函数，初始化事件实例
     * @param em 事件管理器
     * @param name 事件实例名称
     */
    public EventInstanceManager(EventManager em, String name) {
        this.em = em;
        this.name = name;
        // 创建事件脚本调度器，管理延时任务
        this.ess = new EventScriptScheduler();
        // 创建事件专属地图管理器，管理本事件的所有地图实例
        this.mapManager = new MapManager(this, em.getWorldServer().getId(), em.getChannelServer().getId());

        // 初始化公平读写锁，保证事件状态操作的线程安全
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventManager getEm() {
        scriptLock.lock();
        try {
            return em;
        } finally {
            scriptLock.unlock();
        }
    }

    public int getEventPlayersJobs() {
        /* Bits -> 0:BEGINNER 1:WARRIOR 2:MAGICIAN 3:BOWMAN 4:THIEF 5:PIRATE */
        int mask = 0;
        /* 遍历所有参与玩家，按职业类型设置对应的bit位 */
        for (Character chr : getPlayers()) {
            mask |= (1 << chr.getJob().getJobNiche());
        }

        return mask;
    }

    public void applyEventPlayersItemBuff(int itemId) {
        List<Character> players = getPlayerList();
        StatEffect mse = ItemInformationProvider.getInstance().getItemEffect(itemId);

        if (mse != null) {
            for (Character player : players) {
                mse.applyTo(player);
            }
        }
    }

    public void applyEventPlayersSkillBuff(int skillId) {
        applyEventPlayersSkillBuff(skillId, Integer.MAX_VALUE);
    }

    public void applyEventPlayersSkillBuff(int skillId, int skillLv) {
        List<Character> players = getPlayerList();
        Skill skill = SkillFactory.getSkill(skillId);

        if (skill != null) {
            StatEffect mse = skill.getEffect(Math.min(skillLv, skill.getMaxLevel()));
            if (mse != null) {
                for (Character player : players) {
                    mse.applyTo(player);
                }
            }
        }
    }

    public void giveEventPlayersExp(int gain) {
        giveEventPlayersExp(gain, -1);
    }

    public void giveEventPlayersExp(int gain, int mapId) {
        if (gain == 0) {
            return;
        }

        List<Character> players = getPlayerList();

        if (mapId == -1) {
            for (Character mc : players) {
                mc.gainExp(NumberTool.floatToInt(gain * mc.getExpRate()), true, true);
            }
        } else {
            for (Character mc : players) {
                if (mc.getMapId() == mapId) {
                    mc.gainExp(NumberTool.floatToInt(gain * mc.getExpRate()), true, true);
                }
            }
        }
    }

    public void giveEventPlayersMeso(int gain) {
        giveEventPlayersMeso(gain, -1);
    }

    public void giveEventPlayersMeso(int gain, int mapId) {
        if (gain == 0) {
            return;
        }

        List<Character> players = getPlayerList();

        if (mapId == -1) {
            for (Character mc : players) {
                mc.gainMeso(NumberTool.floatToInt(gain * mc.getMesoRate()));
            }
        } else {
            for (Character mc : players) {
                if (mc.getMapId() == mapId) {
                    mc.gainMeso(NumberTool.floatToInt(gain * mc.getMesoRate()));
                }
            }
        }

    }

    public Object invokeScriptFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        if (!disposed) {
            return em.getIv().invokeFunction(name, args);
        } else {
            return null;
        }
    }

    public synchronized void registerPlayer(final Character chr) {
        registerPlayer(chr, true);
    }

    /**
     * 注册玩家到事件实例
     * @param chr 要注册的玩家角色
     * @param runEntryScript 是否执行入口脚本
     */
    public synchronized void registerPlayer(final Character chr, boolean runEntryScript) {
        if (chr == null || !chr.isLoggedInWorld() || disposed) {
            return;
        }

        writeLock.lock();
        try {
            if (chars.containsKey(chr.getId())) {
                return;
            }

            /* 将玩家加入事件玩家集合，并绑定事件实例到玩家 */
            chars.put(chr.getId(), chr);
            chr.setEventInstance(this);
        } finally {
            writeLock.unlock();
        }

        /* 执行入口脚本——通知脚本层有新玩家加入 */
        if (runEntryScript) {
            try {
                invokeScriptFunction("playerEntry", EventInstanceManager.this, chr);
            } catch (ScriptException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void exitPlayer(final Character chr) {
        if (chr == null || !chr.isLoggedIn()) {
            return;
        }

        unregisterPlayer(chr);

        try {
            invokeScriptFunction("playerExit", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void dropMessage(int type, String message) {
        for (Character chr : getPlayers()) {
            chr.dropMessage(type, message);
        }
    }

    public void restartEventTimer(long time) {
        stopEventTimer();
        startEventTimer(time);
    }

    public void startEventTimer(long time) {
        timeStarted = System.currentTimeMillis();
        eventTime = time;

        /* 向所有参与玩家发送倒计时时钟包 */
        for (Character chr : getPlayers()) {
            chr.sendPacket(PacketCreator.getClock((int) (time / 1000)));
        }

        /* 定时到期后调用脚本层scheduledTimeout函数 */
        event_schedule = TimerManager.getInstance().schedule(() -> {
            dismissEventTimer();

            try {
                invokeScriptFunction("scheduledTimeout", EventInstanceManager.this);
            } catch (ScriptException | NoSuchMethodException ex) {
                log.error("事件脚本 {} 没有封装scheduledTimeout函数", em.getName(), ex);
            }
        }, time);
    }

    public void addEventTimer(long time) {
        if (event_schedule != null) {
            // 已有定时器运行中，取消后追加时间再重新调度
            if (event_schedule.cancel(false)) {
                long nextTime = getTimeLeft() + time;
                eventTime += time;

                event_schedule = TimerManager.getInstance().schedule(() -> {
                    dismissEventTimer();

                    try {
                        invokeScriptFunction("scheduledTimeout", EventInstanceManager.this);
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error("事件脚本 {} 没有封装scheduledTimeout函数", em.getName(), ex);
                    }
                }, nextTime);
            }
        } else {
            // 无定时器运行，直接启动
            startEventTimer(time);
        }
    }

    private void dismissEventTimer() {
        /* 移除所有玩家的倒计时UI时钟 */
        for (Character chr : getPlayers()) {
            chr.sendPacket(PacketCreator.removeClock());
        }

        /* 重置定时相关状态 */
        event_schedule = null;
        eventTime = 0;
        timeStarted = 0;
    }

    public void stopEventTimer() {
        if (event_schedule != null) {
            event_schedule.cancel(false);
            event_schedule = null;
        }

        dismissEventTimer();
    }

    public boolean isTimerStarted() {
        return eventTime > 0 && timeStarted > 0;
    }

    public long getTimeLeft() {
        return eventTime - (System.currentTimeMillis() - timeStarted);
    }

    public void registerParty(Character chr) {
        if (chr.isPartyLeader()) {
            registerParty(chr.getParty(), chr.getMap());
        }
    }

    public void registerParty(Party party, MapleMap map) {
        for (PartyCharacter mpc : party.getEligibleMembers()) {
            /* 只注册在线的且在同一地图的队员 */
            if (mpc.isOnline()) {
                Character chr = map.getCharacterById(mpc.getId());
                if (chr != null) {
                    registerPlayer(chr);
                }
            }
        }
    }

    public void registerExpedition(Expedition exped) {
        expedition = exped;
        registerExpeditionTeam(exped, exped.getRecruitingMap().getId());
    }

    private void registerExpeditionTeam(Expedition exped, int recruitMap) {
        expedition = exped;

        /* 只注册在招募地图中的远征队活跃成员 */
        for (Character chr : exped.getActiveMembers()) {
            if (chr.getMapId() == recruitMap) {
                registerPlayer(chr);
            }
        }
    }

    public void unregisterPlayer(final Character chr) {
        // 先通知脚本层玩家即将注销
        try {
            invokeScriptFunction("playerUnregistered", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            log.error("事件脚本 {} 没有封装playerUnregistered函数", em.getName(), ex);
        }

        // 从事件中移除玩家，清除双向引用
        writeLock.lock();
        try {
            chars.remove(chr.getId());
            chr.setEventInstance(null);
        } finally {
            writeLock.unlock();
        }

        // 清除玩家事件状态和专属物品
        gridRemove(chr);
        dropExclusiveItems(chr);
    }

    public int getPlayerCount() {
        readLock.lock();
        try {
            return chars.size();
        } finally {
            readLock.unlock();
        }
    }

    public Character getPlayerById(int id) {
        readLock.lock();
        try {
            return chars.get(id);
        } finally {
            readLock.unlock();
        }
    }

    public List<Character> getPlayers() {
        readLock.lock();
        try {
            return new ArrayList<>(chars.values());
        } finally {
            readLock.unlock();
        }
    }

    private List<Character> getPlayerList() {
        readLock.lock();
        try {
            return new LinkedList<>(chars.values());
        } finally {
            readLock.unlock();
        }
    }

    public void registerMonster(Monster mob) {
        /* 不注册友善怪物（如月兔），它们不属于事件击杀统计范围 */
        if (!mob.getStats().isFriendly()) {
            mobs.add(mob);
        }
    }

    public void movePlayer(final Character chr) {
        try {
            invokeScriptFunction("moveMap", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void changedMap(final Character chr, final int mapId) {
        try {
            invokeScriptFunction("changedMap", EventInstanceManager.this, chr, mapId);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

    public void afterChangedMap(final Character chr, final int mapId) {
        try {
            invokeScriptFunction("afterChangedMap", EventInstanceManager.this, chr, mapId);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

    public synchronized void changedLeader(final PartyCharacter ldr) {
        try {
            invokeScriptFunction("changedLeader", EventInstanceManager.this, ldr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        leaderId = ldr.getId();
    }

    public void monsterKilled(final Monster mob, final boolean hasKiller) {
        int scriptResult = 0;

        scriptLock.lock();
        try {
            mobs.remove(mob);

            if (eventStarted) {
                /* scriptResult=1: 触发monsterKilled脚本 */
                scriptResult = 1;

                /* scriptResult=2: 怪物全部清空，额外触发allMonstersDead脚本 */
                if (mobs.isEmpty()) {
                    scriptResult = 2;
                }
            }
        } finally {
            scriptLock.unlock();
        }

        /* 在锁外调用脚本，避免死锁 */
        if (scriptResult > 0) {
            try {
                invokeScriptFunction("monsterKilled", mob, EventInstanceManager.this, hasKiller);
            } catch (ScriptException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }

            /* 所有怪物死亡后触发通关检查 */
            if (scriptResult > 1) {
                try {
                    invokeScriptFunction("allMonstersDead", EventInstanceManager.this, hasKiller);
                } catch (ScriptException | NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void friendlyKilled(final Monster mob, final boolean hasKiller) {
        try {
            invokeScriptFunction("friendlyKilled", mob, EventInstanceManager.this, hasKiller);
        } catch (ScriptException | NoSuchMethodException ex) {
        } //optional
    }

    public void friendlyDamaged(final Monster mob) {
        try {
            invokeScriptFunction("friendlyDamaged", EventInstanceManager.this, mob);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

    public void friendlyItemDrop(final Monster mob) {
        try {
            invokeScriptFunction("friendlyItemDrop", EventInstanceManager.this, mob);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

    public void playerKilled(final Character chr) {
        ThreadManager.getInstance().newTask(() -> {
            try {
                invokeScriptFunction("playerDead", EventInstanceManager.this, chr);
            } catch (ScriptException | NoSuchMethodException ex) {
            } // optional
        });
    }

    public void reviveMonster(final Monster mob) {
        try {
            invokeScriptFunction("monsterRevive", EventInstanceManager.this, mob);
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional
    }

    public boolean revivePlayer(final Character chr) {
        try {
            Object b = invokeScriptFunction("playerRevive", EventInstanceManager.this, chr);
            if (b instanceof Boolean) {
                return (Boolean) b;
            }
        } catch (ScriptException | NoSuchMethodException ex) {
        } // optional

        return true;
    }

    public void playerDisconnected(final Character chr) {
        try {
            invokeScriptFunction("playerDisconnected", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        EventRecallCoordinator.getInstance().storeEventInstance(chr.getId(), this);
    }

    public void monsterKilled(Character chr, final Monster mob) {
        try {
            /* 脚本层提供该怪物对击杀计数的贡献值 */
            final int inc = (int) invokeScriptFunction("monsterValue", EventInstanceManager.this, mob.getId());

            if (inc != 0) {
                Integer kc = killCount.get(chr);
                /* 更新玩家击杀计数 */
                if (kc == null) {
                    kc = inc;
                } else {
                    kc += inc;
                }
                killCount.put(chr, kc);
                /* 远征队模式下同步更新远征队击杀计数 */
                if (expedition != null) {
                    expedition.monsterKilled(chr, mob);
                }
            }
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public int getKillCount(Character chr) {
        Integer kc = killCount.get(chr);
        return (kc == null) ? 0 : kc;
    }

    public void dispose() {
        /* 先断开所有玩家的引用关系 */
        readLock.lock();
        try {
            for (Character chr : chars.values()) {
                chr.setEventInstance(null);
            }
        } finally {
            readLock.unlock();
        }

        dispose(false);
    }

    public synchronized void dispose(boolean shutdown) {
        if (disposed) {
            return;
        }

        // 调用脚本层dispose函数进行自定义清理
        try {
            invokeScriptFunction("dispose", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        disposed = true;

        // 停止脚本调度器，清除所有延时任务
        ess.dispose();

        // 清除所有玩家和怪物引用
        writeLock.lock();
        try {
            for (Character chr : chars.values()) {
                chr.setEventInstance(null);
            }
            chars.clear();
            mobs.clear();
            ess = null;
        } finally {
            writeLock.unlock();
        }

        // 取消事件定时器
        if (event_schedule != null) {
            event_schedule.cancel(false);
            event_schedule = null;
        }

        // 清理所有数据集合
        killCount.clear();
        mapIds.clear();
        props.clear();
        objectProps.clear();

        disposeExpedition();

        // 未通关的事件实例从事件管理器注销
        scriptLock.lock();
        try {
            if (!eventCleared) {
                em.disposeInstance(name);
            }
        } finally {
            scriptLock.unlock();
        }

        // 延迟1分钟销毁地图管理器，避免对象被立即回收导致引用错误
        TimerManager.getInstance().schedule(() -> {
            mapManager.dispose();
            writeLock.lock();
            try {
                mapManager = null;
                em = null;
            } finally {
                writeLock.unlock();
            }
        }, MINUTES.toMillis(1));
    }

    public MapManager getMapFactory() {
        return mapManager;
    }

    public void schedule(final String methodName, long delay) {
        readLock.lock();
        try {
            if (ess != null) {
                Runnable r = () -> {
                    try {
                        invokeScriptFunction(methodName, EventInstanceManager.this);
                    } catch (ScriptException | NoSuchMethodException ex) {
                        ex.printStackTrace();
                    }
                };

                ess.registerEntry(r, delay);
            }
        } finally {
            readLock.unlock();
        }
    }

    public String getName() {
        return name;
    }

    public MapleMap getMapInstance(int mapId) {
        MapleMap map = mapManager.getMap(mapId);
        map.setEventInstance(this);

        if (!mapManager.isMapLoaded(mapId)) {
            scriptLock.lock();
            try {
                if (em.getProperty("shuffleReactors") != null && em.getProperty("shuffleReactors").equals("true")) {
                    map.shuffleReactors();
                }
            } finally {
                scriptLock.unlock();
            }
        }
        return map;
    }

    public void setIntProperty(String key, Integer value) {
        setProperty(key, value);
    }

    public void setProperty(String key, Integer value) {
        setProperty(key, "" + value);
    }

    public void setProperty(String key, String value) {
        propertyLock.lock();
        try {
            props.setProperty(key, value);
        } finally {
            propertyLock.unlock();
        }
    }

    public Object setProperty(String key, String value, boolean prev) {
        propertyLock.lock();
        try {
            return props.setProperty(key, value);
        } finally {
            propertyLock.unlock();
        }
    }

    public void setObjectProperty(String key, Object obj) {
        propertyLock.lock();
        try {
            objectProps.put(key, obj);
        } finally {
            propertyLock.unlock();
        }
    }

    public String getProperty(String key) {
        propertyLock.lock();
        try {
            return props.getProperty(key);
        } finally {
            propertyLock.unlock();
        }
    }

    public int getIntProperty(String key) {
        propertyLock.lock();
        try {
            return Integer.parseInt(props.getProperty(key) != null ? props.getProperty(key) : String.valueOf(0));
        } finally {
            propertyLock.unlock();
        }
    }

    public Object getObjectProperty(String key) {
        propertyLock.lock();
        try {
            return objectProps.get(key);
        } finally {
            propertyLock.unlock();
        }
    }

    public void leftParty(final Character chr) {
        try {
            invokeScriptFunction("leftParty", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void disbandParty() {
        try {
            invokeScriptFunction("disbandParty", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void clearPQ() {
        try {
            invokeScriptFunction("clearPQ", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void removePlayer(final Character chr) {
        try {
            invokeScriptFunction("playerExit", EventInstanceManager.this, chr);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isLeader(Character chr) {
        return (chr.getParty().getLeaderId() == chr.getId());
    }

    public boolean isEventLeader(Character chr) {
        return (chr.getId() == getLeaderId());
    }

    public final MapleMap getInstanceMap(final int mapid) {
        if (disposed) {
            return null;
        }
        mapIds.add(mapid);
        return getMapFactory().getMap(mapid);
    }

    public final boolean disposeIfPlayerBelow(final byte size, final int towarp) {
        if (disposed) {
            return true;
        }
        if (chars == null) {
            return false;
        }

        MapleMap map = null;
        if (towarp > 0) {
            map = this.getMapFactory().getMap(towarp);
        }

        List<Character> players = getPlayerList();

        try {
            if (players.size() < size) {
                /* 人数不足，将剩余玩家传送到指定地图后销毁事件 */
                for (Character chr : players) {
                    if (chr == null) {
                        continue;
                    }

                    unregisterPlayer(chr);
                    if (towarp > 0) {
                        chr.changeMap(map, map.getPortal(0));
                    }
                }

                dispose();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void spawnNpc(int npcId, Point pos, MapleMap map) {
        NPC npc = LifeFactory.getNPC(npcId);
        if (npc != null) {
            npc.setPosition(pos);
            npc.setCy(pos.y);
            npc.setRx0(pos.x + 50);
            npc.setRx1(pos.x - 50);
            npc.setFh(map.getFootholds().findBelow(pos).getId());
            map.addMapObject(npc);
            map.broadcastMessage(PacketCreator.spawnNPC(npc));
        }
    }

    public void dispatchRaiseQuestMobCount(int mobid, int mapid) {
        Map<Integer, Character> mapChars = getInstanceMap(mapid).getMapPlayers();
        if (!mapChars.isEmpty()) {
            List<Character> eventMembers = getPlayers();

            for (Character evChr : eventMembers) {
                Character chr = mapChars.get(evChr.getId());

                if (chr != null && chr.isLoggedInWorld()) {
                    chr.raiseQuestMobCount(mobid);
                }
            }
        }
    }

    public Monster getMonster(int mid) {
        return (LifeFactory.getMonster(mid));
    }

    private List<Integer> convertToIntegerList(List<Object> objects) {
        List<Integer> intList = new ArrayList<>();

        for (Object object : objects) {
            intList.add((Integer) object);
        }

        return intList;
    }

    public void setEventClearStageExp(List<Object> gain) {
        onMapClearExp.clear();
        onMapClearExp.addAll(convertToIntegerList(gain));
    }

    public void setEventClearStageMeso(List<Object> gain) {
        onMapClearMeso.clear();
        onMapClearMeso.addAll(convertToIntegerList(gain));
    }

    /* 阶段计数从1开始（stage counts from ONE） */
    public Integer getClearStageExp(int stage) {
        if (stage > onMapClearExp.size()) {
            return 0;
        }
        return onMapClearExp.get(stage - 1);
    }

    /* 阶段计数从1开始（stage counts from ONE） */
    public Integer getClearStageMeso(int stage) {
        if (stage > onMapClearMeso.size()) {
            return 0;
        }
        return onMapClearMeso.get(stage - 1);
    }

    public List<Integer> getClearStageBonus(int stage) {
        List<Integer> list = new ArrayList<>();
        list.add(getClearStageExp(stage));
        list.add(getClearStageMeso(stage));

        return list;
    }

    private void dropExclusiveItems(Character chr) {
        AbstractPlayerInteraction api = chr.getAbstractPlayerInteraction();

        for (Integer item : exclusiveItems) {
            api.removeAll(item);
        }
    }

    public void dropAllExclusiveItems() {
        getPlayers().forEach(this::dropExclusiveItems);
    }

    public final void setExclusiveItems(List<Object> items) {
        List<Integer> exclusive = convertToIntegerList(items);

        writeLock.lock();
        try {
            exclusiveItems.addAll(exclusive);
        } finally {
            writeLock.unlock();
        }
    }

    public final void setEventRewards(List<Object> rwds, List<Object> qtys, int expGiven) {
        setEventRewards(1, rwds, qtys, expGiven);
    }

    public final void setEventRewards(List<Object> rwds, List<Object> qtys) {
        setEventRewards(1, rwds, qtys);
    }

    public final void setEventRewards(int eventLevel, List<Object> rwds, List<Object> qtys) {
        setEventRewards(eventLevel, rwds, qtys, 0);
    }

    // 固定经验值将在随机物品发放时一起给予
    public final void setEventRewards(int eventLevel, List<Object> rwds, List<Object> qtys, int expGiven) {

        if (eventLevel <= 0 || eventLevel > GameConfig.getServerInt("max_event_levels")) {
            return;
        }
        /* 事件等级从1开始，内部索引从0开始 */
        eventLevel--;

        List<Integer> rewardIds = convertToIntegerList(rwds);
        List<Integer> rewardQtys = convertToIntegerList(qtys);

        writeLock.lock();
        try {
            collectionSet.put(eventLevel, rewardIds);
            collectionQty.put(eventLevel, rewardQtys);
            collectionExp.put(eventLevel, expGiven);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 获取指定等级奖励所需的背包空间类型位掩码
     * 用于检查玩家背包是否有足够空间领取奖励
     */
    private byte getRewardListRequirements(int level) {
        if (level >= collectionSet.size()) {
            return 0;
        }

        byte rewardTypes = 0;
        List<Integer> list = collectionSet.get(level);

        // 遍历所有奖励物品，按物品所属背包类型设置bit位
        for (Integer itemId : list) {
            rewardTypes |= (1 << ItemConstants.getInventoryType(itemId).getType());
        }

        return rewardTypes;
    }

    /**
     * 检查玩家是否有足够背包空间领取事件奖励
     * 遍历装备/消耗/装饰/特殊/现金五种背包类型
     */
    private boolean hasRewardSlot(Character player, int eventLevel) {
        byte listReq = getRewardListRequirements(eventLevel);

        /* 遍历装备、消耗、装饰、特殊、现金五种背包类型 */
        for (byte type = 1; type <= 5; type++) {
            if ((listReq >> type) % 2 == 1 && !player.hasEmptySlot(type)) {
                return false;
            }
        }

        return true;
    }

    public final boolean giveEventReward(Character player) {
        return giveEventReward(player, 1);
    }

    /* 以类似KPQ/LPQ通关的方式发放经验值和一个随机物品 */
    public final boolean giveEventReward(Character player, int eventLevel) {
        List<Integer> rewardsSet, rewardsQty;
        Integer rewardExp;

        readLock.lock();
        try {
            /* 事件等级从1开始，内部索引从0开始 */
            eventLevel--;
            if (eventLevel >= collectionSet.size()) {
                return true;
            }

            rewardsSet = collectionSet.get(eventLevel);
            rewardsQty = collectionQty.get(eventLevel);

            rewardExp = collectionExp.get(eventLevel);
        } finally {
            readLock.unlock();
        }

        if (rewardExp == null) {
            rewardExp = 0;
        }

        /* 无奖励物品但有经验时，只发放经验 */
        if (rewardsSet == null || rewardsSet.isEmpty()) {
            if (rewardExp > 0) {
                player.gainExp(rewardExp);
            }
            return true;
        }

        // 检查背包空间，空间不足则返回false由调用方处理
        if (!hasRewardSlot(player, eventLevel)) {
            return false;
        }

        AbstractPlayerInteraction api = player.getAbstractPlayerInteraction();
        /* 随机选择奖励物品中的一项 */
        int rnd = (int) Math.floor(Math.random() * rewardsSet.size());

        api.gainItem(rewardsSet.get(rnd), rewardsQty.get(rnd).shortValue());
        if (rewardExp > 0) {
            player.gainExp(rewardExp);
        }
        return true;
    }

    private void disposeExpedition() {
        if (expedition != null) {
            /* 通知远征队事件结束（eventCleared标识是否通关） */
            expedition.dispose(eventCleared);

            /* 从频道服务器移除远征队注册 */
            scriptLock.lock();
            try {
                expedition.removeChannelExpedition(em.getChannelServer());
            } finally {
                scriptLock.unlock();
            }

            expedition = null;
        }
    }

    public final synchronized void startEvent() {
        eventStarted = true;

        /* 事件启动后调用脚本层afterSetup，脚本可在此阶段生成怪物、设置奖励等 */
        try {
            invokeScriptFunction("afterSetup", EventInstanceManager.this);
        } catch (ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public final void setEventCleared() {
        eventCleared = true;

        /* 给所有参与玩家发放活动积分 */
        for (Character chr : getPlayers()) {
            chr.awardQuestPoint(GameConfig.getServerInt("quest_point_per_event_clear"));
        }

        /* 从事件管理器注销实例 */
        scriptLock.lock();
        try {
            em.disposeInstance(name);
        } finally {
            scriptLock.unlock();
        }

        /* 清理远征队 */
        disposeExpedition();
    }

    public final boolean isEventCleared() {
        return eventCleared;
    }

    public final boolean isEventDisposed() {
        return disposed;
    }

    private boolean isEventTeamLeaderOn() {
        for (Character chr : getPlayers()) {
            if (chr.getId() == getLeaderId()) {
                return true;
            }
        }

        return false;
    }

    public final boolean checkEventTeamLacking(boolean leavingEventMap, int minPlayers) {
        /* 已通关且人数多于1人，不算缺人 */
        if (eventCleared && getPlayerCount() > 1) {
            return false;
        }

        /* 未通关、正在离开事件地图、且队长不在线 */
        if (!eventCleared && leavingEventMap && !isEventTeamLeaderOn()) {
            return true;
        }
        /* 人数低于最低要求 */
        return getPlayerCount() < minPlayers;
    }

    public final boolean isExpeditionTeamLackingNow(boolean leavingEventMap, int minPlayers, Character quitter) {
        if (eventCleared) {
            /* 已通关时，仅当离开事件地图且只剩1人时判为缺人 */
            return leavingEventMap && getPlayerCount() <= 1;
        } else {
            /* 远征队模式下不要求队长在场，仅检查人数是否低于等于1 */
            return getPlayerCount() <= 1;
        }
    }

    public final boolean isEventTeamLackingNow(boolean leavingEventMap, int minPlayers, Character quitter) {
        if (eventCleared) {
            /* 已通关时，仅当离开事件地图且只剩1人时判为缺人 */
            return leavingEventMap && getPlayerCount() <= 1;
        } else {
            /* 队长离开事件地图则队伍解散 */
            if (leavingEventMap && getLeaderId() == quitter.getId()) {
                return true;
            }
            /* 人数低于最低要求 */
            return getPlayerCount() <= minPlayers;
        }
    }

    public final boolean isEventTeamTogether() {
        readLock.lock();
        try {
            if (chars.size() <= 1) {
                return true;
            }

            /* 检查所有队员是否在同一地图 */
            Iterator<Character> iterator = chars.values().iterator();
            Character mc = iterator.next();
            int mapId = mc.getMapId();

            for (; iterator.hasNext(); ) {
                mc = iterator.next();
                if (mc.getMapId() != mapId) {
                    return false;
                }
            }

            return true;
        } finally {
            readLock.unlock();
        }
    }

    public final void warpEventTeam(int warpFrom, int warpTo) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            if (chr.getMapId() == warpFrom) {
                chr.changeMap(warpTo);
            }
        }
    }

    public final void warpEventTeam(int warpTo) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            chr.changeMap(warpTo);
        }
    }

    public final void warpEventTeamToMapSpawnPoint(int warpFrom, int warpTo, int toSp) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            if (chr.getMapId() == warpFrom) {
                chr.changeMap(warpTo, toSp);
            }
        }
    }

    public final void warpEventTeamToMapSpawnPoint(int warpTo, int toSp) {
        List<Character> players = getPlayerList();

        for (Character chr : players) {
            chr.changeMap(warpTo, toSp);
        }
    }

    public final int getLeaderId() {
        readLock.lock();
        try {
            return leaderId;
        } finally {
            readLock.unlock();
        }
    }

    public Character getLeader() {
        readLock.lock();
        try {
            return chars.get(leaderId);
        } finally {
            readLock.unlock();
        }
    }

    public final void setLeader(Character chr) {
        writeLock.lock();
        try {
            leaderId = chr.getId();
        } finally {
            writeLock.unlock();
        }
    }

    public final void showWrongEffect() {
        showWrongEffect(getLeader().getMapId());
    }

    public final void showWrongEffect(int mapId) {
        MapleMap map = getMapInstance(mapId);
        /* 发送错误特效（红色叉号）和失败音效 */
        map.broadcastMessage(PacketCreator.showEffect("quest/party/wrong_kor"));
        map.broadcastMessage(PacketCreator.playSound("Party1/Failed"));
    }

    public final void showClearEffect() {
        showClearEffect(false);
    }

    public final void showClearEffect(boolean hasGate) {
        Character leader = getLeader();
        if (leader != null) {
            showClearEffect(hasGate, leader.getMapId());
        }
    }

    public final void showClearEffect(int mapId) {
        showClearEffect(false, mapId);
    }

    public final void showClearEffect(boolean hasGate, int mapId) {
        showClearEffect(hasGate, mapId, "gate", 2);
    }

    public final void showClearEffect(int mapId, String mapObj, int newState) {
        showClearEffect(true, mapId, mapObj, newState);
    }

    public final void showClearEffect(boolean hasGate, int mapId, String mapObj, int newState) {
        MapleMap map = getMapInstance(mapId);
        /* 发送通关特效和成功音效 */
        map.broadcastMessage(PacketCreator.showEffect("quest/party/clear"));
        map.broadcastMessage(PacketCreator.playSound("Party1/Clear"));
        if (hasGate) {
            /* 显示过关门特效（如KPQ中的门）并记录状态 */
            map.broadcastMessage(PacketCreator.environmentChange(mapObj, newState));
            writeLock.lock();
            try {
                openedGates.put(map.getId(), new Pair<>(mapObj, newState));
            } finally {
                writeLock.unlock();
            }
        }
    }

    public final void recoverOpenedGate(Character chr, int thisMapId) {
        Pair<String, Integer> gateData = null;

        readLock.lock();
        try {
            if (openedGates.containsKey(thisMapId)) {
                gateData = openedGates.get(thisMapId);
            }
        } finally {
            readLock.unlock();
        }

        if (gateData != null) {
            chr.sendPacket(PacketCreator.environmentChange(gateData.getLeft(), gateData.getRight()));
        }
    }

    public final void giveEventPlayersStageReward(int thisStage) {
        /* 向事件中所有玩家发放该阶段的奖励经验和金币 */
        List<Integer> list = getClearStageBonus(thisStage);
        giveEventPlayersExp(list.get(0));
        giveEventPlayersMeso(list.get(1));
    }

    public final void linkToNextStage(int thisStage, String eventFamily, int thisMapId) {
        /* 先发放当前阶段奖励 */
        giveEventPlayersStageReward(thisStage);
        /* 阶段从1开始计数，脚本索引从0开始 */
        thisStage--;

        /* 将下一阶段的传送门脚本绑定到事件家族名+阶段索引 */
        MapleMap nextStage = getMapInstance(thisMapId);
        Portal portal = nextStage.getPortal("next00");
        if (portal != null) {
            portal.setScriptName(eventFamily + thisStage);
        }
    }

    public final void linkPortalToScript(int thisStage, String portalName, String scriptName, int thisMapId) {
        /* 先发放当前阶段奖励 */
        giveEventPlayersStageReward(thisStage);
        /* 阶段从1开始计数，脚本索引从0开始 */
        thisStage--;

        MapleMap nextStage = getMapInstance(thisMapId);
        Portal portal = nextStage.getPortal(portalName);
        if (portal != null) {
            portal.setScriptName(scriptName);
        }
    }

    /**
     * 在事件中注册玩家状态
     */
    public final void gridInsert(Character chr, int newStatus) {
        writeLock.lock();
        try {
            playerGrid.put(chr.getId(), newStatus);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 注销事件中玩家的状态
     */
    public final void gridRemove(Character chr) {
        writeLock.lock();
        try {
            playerGrid.remove(chr.getId());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 检查事件中玩家的状态
     * @return 玩家状态值，未注册返回-1
     */
    public final int gridCheck(Character chr) {
        readLock.lock();
        try {
            Integer i = playerGrid.get(chr.getId());
            return (i != null) ? i : -1;
        } finally {
            readLock.unlock();
        }
    }

    public final int gridSize() {
        readLock.lock();
        try {
            return playerGrid.size();
        } finally {
            readLock.unlock();
        }
    }

    public final void gridClear() {
        writeLock.lock();
        try {
            playerGrid.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean activatedAllReactorsOnMap(int mapId, int minReactorId, int maxReactorId) {
        return activatedAllReactorsOnMap(this.getMapInstance(mapId), minReactorId, maxReactorId);
    }

    public boolean activatedAllReactorsOnMap(MapleMap map, int minReactorId, int maxReactorId) {
        if (map == null) {
            return true;
        }

        for (Reactor mr : map.getReactorsByIdRange(minReactorId, maxReactorId)) {
            if (mr.getReactorType() != -1) {
                return false;
            }
        }

        return true;
    }
}