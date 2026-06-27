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

package org.gms.server.expeditions;

import org.gms.client.Character;
import org.gms.constants.id.MapId;
import org.gms.constants.id.MobId;
import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.TimerManager;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 远征队
 * 管理Boss远征队的组队、报名、人数限制和Boss挑战流程
 * 使用并发集合保证多线程安全，支持踢人、超时自动开始
 *
 * @author Alan (SharpAceX)
 */
public class Expedition {
    private static final Logger log = LoggerFactory.getLogger(Expedition.class);

    /** 支持远征挑战的Boss怪物ID数组 */
    private static final int[] EXPEDITION_BOSSES = {
            MobId.ZAKUM_1,
            MobId.ZAKUM_2,
            MobId.ZAKUM_3,
            MobId.ZAKUM_ARM_1,
            MobId.ZAKUM_ARM_2,
            MobId.ZAKUM_ARM_3,
            MobId.ZAKUM_ARM_4,
            MobId.ZAKUM_ARM_5,
            MobId.ZAKUM_ARM_6,
            MobId.ZAKUM_ARM_7,
            MobId.ZAKUM_ARM_8,
            MobId.HORNTAIL_PREHEAD_LEFT,
            MobId.HORNTAIL_PREHEAD_RIGHT,
            MobId.HORNTAIL_HEAD_A,
            MobId.HORNTAIL_HEAD_B,
            MobId.HORNTAIL_HEAD_C,
            MobId.HORNTAIL_HAND_LEFT,
            MobId.HORNTAIL_HAND_RIGHT,
            MobId.HORNTAIL_WINGS,
            MobId.HORNTAIL_LEGS,
            MobId.HORNTAIL_TAIL,
            MobId.SCARLION_STATUE,
            MobId.SCARLION,
            MobId.ANGRY_SCARLION,
            MobId.FURIOUS_SCARLION,
            MobId.TARGA_STATUE,
            MobId.TARGA,
            MobId.ANGRY_TARGA,
            MobId.FURIOUS_TARGA,
    };

    /** 队长 */
    private final Character leader;
    /** 远征类型 */
    private final ExpeditionType type;
    /** 是否正在报名 */
    private boolean registering;
    /** 开始地图 */
    private final MapleMap startMap;
    /** Boss日志 */
    private final List<String> bossLogs;
    /** 报名定时器 */
    private ScheduledFuture<?> schedule;
    /** 队员列表（角色ID -> 角色名） */
    private final Map<Integer, String> members = new ConcurrentHashMap<>();
    /** 被禁入队员ID列表 */
    private final List<Integer> banned = new CopyOnWriteArrayList<>();
    /** 开始时间戳 */
    private long startTime;
    /** 属性配置 */
    private final Properties props = new Properties();
    /** 是否静默模式 */
    private final boolean silent;
    /** 最小人数 */
    private final int minSize;
    /** 最大人数 */
    private final int maxSize;
    /** 玩家列表操作锁 */
    private final Lock pL = new ReentrantLock(true);

    /**
     * 构造远征队
     *
     * @param player      创建远征队的玩家（队长）
     * @param met         远征类型
     * @param sil         是否静默模式
     * @param minPlayers  最小人数，0使用类型默认值
     * @param maxPlayers  最大人数，0使用类型默认值
     */
    public Expedition(Character player, ExpeditionType met, boolean sil, int minPlayers, int maxPlayers) {
        leader = player;
        members.put(player.getId(), player.getName());
        startMap = player.getMap();
        type = met;
        silent = sil;
        minSize = (minPlayers != 0) ? minPlayers : type.getMinSize();
        maxSize = (maxPlayers != 0) ? maxPlayers : type.getMaxSize();
        bossLogs = new CopyOnWriteArrayList<>();
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void beginRegistration() {
        registering = true;
        leader.sendPacket(PacketCreator.getClock((int) MINUTES.toSeconds(type.getRegistrationMinutes())));
        if (!silent) {
            startMap.broadcastMessage(leader, PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.beginRegistration.message1", leader.getName())), false);
            leader.sendPacket(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.beginRegistration.message2")));
        }
        scheduleRegistrationEnd();
    }

    private void scheduleRegistrationEnd() {
        final Expedition exped = this;
        startTime = System.currentTimeMillis() + MINUTES.toMillis(type.getRegistrationMinutes());

        schedule = TimerManager.getInstance().schedule(() -> {
            if (registering) {
                exped.removeChannelExpedition(startMap.getChannelServer());
                if (!silent) {
                    startMap.broadcastMessage(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.scheduleRegistrationEnd.message1")));
                }

                dispose(false);
            }
        }, MINUTES.toMillis(type.getRegistrationMinutes()));
    }

    public void dispose(boolean log) {
        broadcastExped(PacketCreator.removeClock());

        if (schedule != null) {
            schedule.cancel(false);
        }
        if (log && !registering) {
            log();
        }
    }

    private void log() {
        final String gmMessage = I18nUtil.getMessage("Expedition.log.message1", type, leader.getName(), getTimeString(getStartTime()));
        Server.getInstance().broadcastGMMessage(getLeader().getWorld(), PacketCreator.serverNotice(6, gmMessage));

        String log = I18nUtil.getMessage("Expedition.log.message2", type) + "\r\n";
        log += getTimeString(startTime) + "\r\n";

        for (String memberName : getMembers().values()) {
            log += ">>" + memberName + "\r\n";
        }
        log += I18nUtil.getMessage("Expedition.log.message3") + "\r\n";
        for (String message : bossLogs) {
            log += message;
        }
        log += "\r\n";

        Expedition.log.info(log);
    }

    /**
     * 获取格式化时间字符串
     *
     * @param then 起始时间戳
     * @return 格式化的时间差字符串
     */
    private static String getTimeString(long then) {
        long duration = System.currentTimeMillis() - then;
        int seconds = (int) (duration / SECONDS.toMillis(1)) % 60;
        int minutes = (int) ((duration / MINUTES.toMillis(1)) % 60);
        return I18nUtil.getMessage("Expedition.getTimeString.message1", minutes, seconds);
    }

    public void finishRegistration() {
        registering = false;
    }

    /**
     * 开始远征挑战
     * 结束报名，记录开始时间，向GM广播开始消息
     */
    public void start() {
        finishRegistration();
        registerExpeditionAttempt();
        broadcastExped(PacketCreator.removeClock());
        if (!silent) {
            broadcastExped(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.start.message1")));
        }
        startTime = System.currentTimeMillis();
        Server.getInstance().broadcastGMMessage(startMap.getWorld(), PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.start.message2", type.toString(), leader.getName())));
    }

    public String addMember(Character player) {
        if (!registering) {
            return I18nUtil.getMessage("Expedition.addMember.message1");
        }
        if (banned.contains(player.getId())) {
            return I18nUtil.getMessage("Expedition.addMember.message2", leader.getName());
        }
        if (members.size() >= this.getMaxSize()) {
            // Would be a miracle if anybody ever saw this
            return I18nUtil.getMessage("Expedition.addMember.message3");
        }

        int channel = this.getRecruitingMap().getChannelServer().getId();
        if (!ExpeditionBossLog.attemptBoss(player.getId(), channel, this, false)) {
            // thanks Conrad, Cato for noticing some expeditions have entry limit
            return I18nUtil.getMessage("Expedition.addMember.message4");
        }

        members.put(player.getId(), player.getName());
        player.sendPacket(PacketCreator.getClock((int) (startTime - System.currentTimeMillis()) / 1000));
        if (!silent) {
            broadcastExped(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.addMember.message5", player.getName())));
        }
        return I18nUtil.getMessage("Expedition.addMember.message6");
    }

    public int addMemberInt(Character player) {
        if (!registering) {
            return 1; //"Sorry, this expedition is already underway. Registration is closed!";
        }
        if (banned.contains(player.getId())) {
            return 2; //"Sorry, you've been banned from this expedition by #b" + leader.getName() + "#k.";
        }
        if (members.size() >= this.getMaxSize()) { //Would be a miracle if anybody ever saw this
            return 3; //"Sorry, this expedition is full!";
        }

        members.put(player.getId(), player.getName());
        player.sendPacket(PacketCreator.getClock((int) (startTime - System.currentTimeMillis()) / 1000));
        if (!silent) {
            broadcastExped(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.addMemberInt.message1", player.getName())));
        }
        return 0; //"You have registered for the expedition successfully!";
    }

    /**
     * 注册所有队员的远征尝试记录
     */
    private void registerExpeditionAttempt() {
        int channel = this.getRecruitingMap().getChannelServer().getId();

        for (Character chr : getActiveMembers()) {
            ExpeditionBossLog.attemptBoss(chr.getId(), channel, this, true);
        }
    }

    /**
     * 向所有在线队员广播数据包
     *
     * @param packet 数据包
     */
    private void broadcastExped(Packet packet) {
        for (Character chr : getActiveMembers()) {
            chr.sendPacket(packet);
        }
    }

    /**
     * 移除队员
     *
     * @param chr 玩家
     * @return true移除成功，false玩家不在远征队中
     */
    public boolean removeMember(Character chr) {
        if (members.remove(chr.getId()) != null) {
            chr.sendPacket(PacketCreator.removeClock());
            if (!silent) {
                broadcastExped(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.removeMember.message1", chr.getName())));
                chr.dropMessage(6, I18nUtil.getMessage("Expedition.removeMember.message2"));
            }
            return true;
        }

        return false;
    }

    public void ban(Entry<Integer, String> chr) {
        int cid = chr.getKey();
        if (!banned.contains(cid)) {
            banned.add(cid);
            members.remove(cid);

            if (!silent) {
                broadcastExped(PacketCreator.serverNotice(6, I18nUtil.getMessage("Expedition.ban.message1", chr.getValue())));
            }

            Character player = startMap.getWorldServer().getPlayerStorage().getCharacterById(cid);
            if (player != null && player.isLoggedInWorld()) {
                player.sendPacket(PacketCreator.removeClock());
                if (!silent) {
                    player.dropMessage(6, I18nUtil.getMessage("Expedition.ban.message2"));
                }
                if (ExpeditionType.ARIANT.equals(type) || ExpeditionType.ARIANT1.equals(type) || ExpeditionType.ARIANT2.equals(type)) {
                    player.changeMap(MapId.ARPQ_LOBBY);
                }
            }
        }
    }

    public void unban(int cid) {
        banned.remove(Integer.valueOf(cid));
    }

    public List<Integer> getBanned() {
        return new LinkedList<>(banned);
    }

    public void monsterKilled(Character chr, Monster mob) {
        for (int expeditionBoss : EXPEDITION_BOSSES) {
            if (mob.getId() == expeditionBoss) { //If the monster killed was a boss
                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                bossLogs.add(I18nUtil.getMessage("Expedition.monsterKilled.message1", mob.getName(), getTimeString(startTime), timeStamp) + "\r\n");
                return;
            }
        }
    }

    public void setProperty(String key, String value) {
        pL.lock();
        try {
            props.setProperty(key, value);
        } finally {
            pL.unlock();
        }
    }

    /**
     * 获取属性
     *
     * @param key 属性键
     * @return 属性值
     */
    public String getProperty(String key) {
        pL.lock();
        try {
            return props.getProperty(key);
        } finally {
            pL.unlock();
        }
    }

    public ExpeditionType getType() {
        return type;
    }

    public List<Character> getActiveMembers() {    // thanks MedicOP for figuring out an issue with broadcasting packets to offline members
        PlayerStorage ps = startMap.getWorldServer().getPlayerStorage();

        List<Character> activeMembers = new LinkedList<>();
        for (Integer chrid : getMembers().keySet()) {
            Character chr = ps.getCharacterById(chrid);
            if (chr != null && chr.isLoggedInWorld()) {
                activeMembers.add(chr);
            }
        }

        return activeMembers;
    }

    /**
     * 获取队员映射（副本）
     *
     * @return 队员ID->名称映射
     */
    public Map<Integer, String> getMembers() {
        return new HashMap<>(members);
    }

    public List<Entry<Integer, String>> getMemberList() {
        List<Entry<Integer, String>> memberList = new LinkedList<>();
        Entry<Integer, String> leaderEntry = null;

        for (Entry<Integer, String> e : getMembers().entrySet()) {
            if (!isLeader(e.getKey())) {
                memberList.add(e);
            } else {
                leaderEntry = e;
            }
        }

        if (leaderEntry != null) {
            memberList.add(0, leaderEntry);
        }

        return memberList;
    }

    /**
     * 检查远征队是否在同一地图
     *
     * @return true全在同一地图
     */
    public final boolean isExpeditionTeamTogether() {
        List<Character> chars = getActiveMembers();
        if (chars.size() <= 1) {
            return true;
        }

        Iterator<Character> iterator = chars.iterator();
        Character mc = iterator.next();
        int mapId = mc.getMapId();

        for (; iterator.hasNext(); ) {
            mc = iterator.next();
            if (mc.getMapId() != mapId) {
                return false;
            }
        }

        return true;
    }

    /**
     * 传送远征队在特定地图的成员
     *
     * @param warpFrom 源地图ID
     * @param warpTo   目标地图ID
     */
    public final void warpExpeditionTeam(int warpFrom, int warpTo) {
        List<Character> players = getActiveMembers();

        for (Character chr : players) {
            if (chr.getMapId() == warpFrom) {
                chr.changeMap(warpTo);
            }
        }
    }

    public final void warpExpeditionTeam(int warpTo) {
        List<Character> players = getActiveMembers();

        for (Character chr : players) {
            chr.changeMap(warpTo);
        }
    }

    public final void warpExpeditionTeamToMapSpawnPoint(int warpFrom, int warpTo, int toSp) {
        List<Character> players = getActiveMembers();

        for (Character chr : players) {
            if (chr.getMapId() == warpFrom) {
                chr.changeMap(warpTo, toSp);
            }
        }
    }

    public final void warpExpeditionTeamToMapSpawnPoint(int warpTo, int toSp) {
        List<Character> players = getActiveMembers();

        for (Character chr : players) {
            chr.changeMap(warpTo, toSp);
        }
    }

    public final boolean addChannelExpedition(Channel ch) {
        return ch.addExpedition(this);
    }

    /**
     * 从频道移除远征队
     *
     * @param ch 频道
     */
    public final void removeChannelExpedition(Channel ch) {
        ch.removeExpedition(this);
    }

    /**
     * 获取队长
     *
     * @return 队长
     */
    public Character getLeader() {
        return leader;
    }

    /**
     * 获取招募地图
     *
     * @return 招募地图
     */
    public MapleMap getRecruitingMap() {
        return startMap;
    }

    /**
     * 判断玩家是否在远征队中
     *
     * @param player 玩家
     * @return true在远征队中
     */
    public boolean contains(Character player) {
        return members.containsKey(player.getId()) || isLeader(player);
    }

    /**
     * 判断是否为队长
     *
     * @param player 玩家
     * @return true是队长
     */
    public boolean isLeader(Character player) {
        return isLeader(player.getId());
    }

    /**
     * 根据ID判断是否为队长
     *
     * @param playerid 玩家ID
     * @return true是队长
     */
    public boolean isLeader(int playerid) {
        return leader.getId() == playerid;
    }

    /**
     * 判断是否在报名中
     *
     * @return true正在报名
     */
    public boolean isRegistering() {
        return registering;
    }

    /**
     * 判断是否已开始
     *
     * @return true已开始
     */
    public boolean isInProgress() {
        return !registering;
    }

    /**
     * 获取开始时间
     *
     * @return 开始时间戳
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 获取Boss日志
     *
     * @return 日志列表
     */
    public List<String> getBossLogs() {
        return bossLogs;
    }
}