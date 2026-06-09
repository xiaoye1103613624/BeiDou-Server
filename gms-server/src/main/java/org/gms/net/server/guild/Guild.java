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
package org.gms.net.server.guild;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.matchchecker.MatchCheckerCoordinator;
import org.gms.net.server.coordinator.world.InviteCoordinator;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteResult;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.service.NoteService;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 公会
 * 管理公会成员、等级头衔、徽章、公告以及邀请和升级等公会核心功能
 */
public class Guild {
    private static final Logger log = LoggerFactory.getLogger(Guild.class);

    /** 广播操作类型：无、解散、徽章变更 */
    private enum BCOp {
        NONE, DISBAND, EMBLEMCHANGE
    }

    /** 公会成员列表 */
    private final List<GuildCharacter> members;
    /** 成员列表线程锁 */
    private final Lock membersLock = new ReentrantLock(true);

    // 1 = master, 2 = jr, 5 = lowest member
    /** 公会等级头衔数组 */
    private final String[] rankTitles = new String[5];
    /** 公会名称 */
    private String name;
    /** 公会公告 */
    private String notice;
    /** 公会ID */
    private int id;
    /** 公会GP值 */
    private int gp;
    /** 公会徽标 */
    private int logo;
    /** 公会徽标颜色 */
    private int logoColor;
    /** 会长ID */
    private int leader;
    /** 公会容量 */
    private int capacity;
    /** 徽标背景 */
    private int logoBG;
    /** 徽标背景颜色 */
    private int logoBGColor;
    /** 公会签名 */
    private int signature;
    /** 联盟ID */
    private int allianceId;
    /** 所在世界 */
    private final int world;
    /** 频道通知映射（频道号 -> 角色ID列表） */
    private final Map<Integer, List<Integer>> notifications = new LinkedHashMap<>();
    /** 通知数据是否需要重建 */
    private boolean bDirty = true;

    /**
     * 构造公会（从数据库加载）
     *
     * @param guildid 公会ID
     * @param world   世界ID
     */
    public Guild(int guildid, int world) {
        this.world = world;
        members = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds WHERE guildid = " + guildid);
                 ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    id = -1;
                    return;
                }
                id = guildid;
                name = rs.getString("name");
                gp = rs.getInt("GP");
                logo = rs.getInt("logo");
                logoColor = rs.getInt("logoColor");
                logoBG = rs.getInt("logoBG");
                logoBGColor = rs.getInt("logoBGColor");
                capacity = rs.getInt("capacity");
                for (int i = 1; i <= 5; i++) {
                    rankTitles[i - 1] = rs.getString("rank" + i + "title");
                }
                leader = rs.getInt("leader");
                notice = rs.getString("notice");
                signature = rs.getInt("signature");
                allianceId = rs.getInt("allianceId");
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT id, name, level, job, guildrank, allianceRank FROM characters WHERE guildid = ? ORDER BY guildrank ASC, name ASC")) {
                ps.setInt(1, guildid);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }

                    do {
                        members.add(new GuildCharacter(null, rs.getInt("id"), rs.getInt("level"), rs.getString("name"), (byte) -1, world, rs.getInt("job"), rs.getInt("guildrank"), guildid, false, rs.getInt("allianceRank")));
                    } while (rs.next());
                }
            }
        } catch (SQLException se) {
            log.error("Unable to read guild information from sql", se);
        }
    }

    /**
     * 构建频道通知映射
     * 按频道分组在线成员，用于广播消息
     */
    private void buildNotifications() {
        if (!bDirty) {
            return;
        }
        Set<Integer> chs = Server.getInstance().getOpenChannels(world);
        synchronized (notifications) {
            if (notifications.keySet().size() != chs.size()) {
                notifications.clear();
                for (Integer ch : chs) {
                    notifications.put(ch, new LinkedList<>());
                }
            } else {
                for (List<Integer> l : notifications.values()) {
                    l.clear();
                }
            }
        }

        membersLock.lock();
        try {
            for (GuildCharacter mgc : members) {
                if (!mgc.isOnline()) {
                    continue;
                }

                List<Integer> chl;
                synchronized (notifications) {
                    chl = notifications.get(mgc.getChannel());
                }
                if (chl != null) {
                    chl.add(mgc.getId());
                }
                // Unable to connect to Channel... error was here
            }
        } finally {
            membersLock.unlock();
        }

        bDirty = false;
    }

    /**
     * 写入数据库
     *
     * @param bDisband 是否解散公会
     */
    public void writeToDB(boolean bDisband) {
        try (Connection con = DatabaseConnection.getConnection()) {

            if (!bDisband) {
                StringBuilder builder = new StringBuilder();
                builder.append("UPDATE guilds SET GP = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, ");
                for (int i = 0; i < 5; i++) {
                    builder.append("rank").append(i + 1).append("title = ?, ");
                }
                builder.append("capacity = ?, notice = ? WHERE guildid = ?");
                try (PreparedStatement ps = con.prepareStatement(builder.toString())) {
                    ps.setInt(1, gp);
                    ps.setInt(2, logo);
                    ps.setInt(3, logoColor);
                    ps.setInt(4, logoBG);
                    ps.setInt(5, logoBGColor);
                    for (int i = 6; i < 11; i++) {
                        ps.setString(i, rankTitles[i - 6]);
                    }
                    ps.setInt(11, capacity);
                    ps.setString(12, notice);
                    ps.setInt(13, this.id);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = 0, guildrank = 5 WHERE guildid = ?")) {
                    ps.setInt(1, this.id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement("DELETE FROM guilds WHERE guildid = ?")) {
                    ps.setInt(1, this.id);
                    ps.executeUpdate();
                }

                membersLock.lock();
                try {
                    this.broadcast(GuildPackets.guildDisband(this.id));
                } finally {
                    membersLock.unlock();
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * 获取公会ID
     *
     * @return 公会ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取会长ID
     *
     * @return 会长ID
     */
    public int getLeaderId() {
        return leader;
    }

    /**
     * 设置会长ID
     *
     * @param charId 角色ID
     * @return 新会长ID
     */
    public int setLeaderId(int charId) {
        return leader = charId;
    }

    /**
     * 获取公会GP值
     *
     * @return GP值
     */
    public int getGP() {
        return gp;
    }

    /**
     * 获取徽标
     *
     * @return 徽标
     */
    public int getLogo() {
        return logo;
    }

    /**
     * 设置徽标
     *
     * @param l 徽标
     */
    public void setLogo(int l) {
        logo = l;
    }

    /**
     * 获取徽标颜色
     *
     * @return 徽标颜色
     */
    public int getLogoColor() {
        return logoColor;
    }

    /**
     * 设置徽标颜色
     *
     * @param c 颜色
     */
    public void setLogoColor(int c) {
        logoColor = c;
    }

    /**
     * 获取徽标背景
     *
     * @return 徽标背景
     */
    public int getLogoBG() {
        return logoBG;
    }

    /**
     * 设置徽标背景
     *
     * @param bg 徽标背景
     */
    public void setLogoBG(int bg) {
        logoBG = bg;
    }

    /**
     * 获取徽标背景颜色
     *
     * @return 徽标背景颜色
     */
    public int getLogoBGColor() {
        return logoBGColor;
    }

    /**
     * 设置徽标背景颜色
     *
     * @param c 颜色
     */
    public void setLogoBGColor(int c) {
        logoBGColor = c;
    }

    /**
     * 获取公会公告
     *
     * @return 公告内容
     */
    public String getNotice() {
        if (notice == null) {
            return "";
        }
        return notice;
    }

    /**
     * 获取公会名称
     *
     * @return 公会名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取公会成员列表
     *
     * @return 成员列表
     */
    public List<GuildCharacter> getMembers() {
        membersLock.lock();
        try {
            return new ArrayList<>(members);
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 获取公会容量
     *
     * @return 容量
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * 获取公会签名
     *
     * @return 签名
     */
    public int getSignature() {
        return signature;
    }

    /**
     * 广播公会名称变更
     */
    public void broadcastNameChanged() {
        PlayerStorage ps = Server.getInstance().getWorld(world).getPlayerStorage();

        for (GuildCharacter mgc : getMembers()) {
            Character chr = ps.getCharacterById(mgc.getId());
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            Packet packet = GuildPackets.guildNameChanged(chr.getId(), this.getName());
            chr.getMap().broadcastPacket(chr, packet);
        }
    }

    /**
     * 广播公会徽章变更
     */
    public void broadcastEmblemChanged() {
        PlayerStorage ps = Server.getInstance().getWorld(world).getPlayerStorage();

        for (GuildCharacter mgc : getMembers()) {
            Character chr = ps.getCharacterById(mgc.getId());
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            Packet packet = GuildPackets.guildMarkChanged(chr.getId(), this);
            chr.getMap().broadcastPacket(chr, packet);
        }
    }

    /**
     * 广播公会信息变更
     */
    public void broadcastInfoChanged() {
        PlayerStorage ps = Server.getInstance().getWorld(world).getPlayerStorage();

        for (GuildCharacter mgc : getMembers()) {
            Character chr = ps.getCharacterById(mgc.getId());
            if (chr == null || !chr.isLoggedInWorld()) {
                continue;
            }

            chr.sendPacket(GuildPackets.showGuildInfo(chr));
        }
    }

    /**
     * 广播数据包到所有在线成员
     *
     * @param packet 数据包
     */
    public void broadcast(Packet packet) {
        broadcast(packet, -1, BCOp.NONE);
    }

    /**
     * 广播数据包到所有在线成员（排除指定角色）
     *
     * @param packet    数据包
     * @param exception 排除的角色ID
     */
    public void broadcast(Packet packet, int exception) {
        broadcast(packet, exception, BCOp.NONE);
    }

    /**
     * 广播数据包（支持操作类型）
     * membersLock awareness thanks to ProjectNano dev team
     *
     * @param packet      数据包
     * @param exceptionId 排除的角色ID
     * @param bcop        广播操作类型
     */
    public void broadcast(Packet packet, int exceptionId, BCOp bcop) {
        membersLock.lock();
        try {
            synchronized (notifications) {
                if (bDirty) {
                    buildNotifications();
                }
                try {
                    for (Integer b : Server.getInstance().getOpenChannels(world)) {
                        if (notifications.get(b).size() > 0) {
                            if (bcop == BCOp.DISBAND) {
                                Server.getInstance().getWorld(world).setGuildAndRank(notifications.get(b), 0, 5, exceptionId);
                            } else if (bcop == BCOp.EMBLEMCHANGE) {
                                Server.getInstance().getWorld(world).changeEmblem(this.id, notifications.get(b), new GuildSummary(this));
                            } else {
                                Server.getInstance().getWorld(world).sendPacket(notifications.get(b), packet, exceptionId);
                            }
                        }
                    }
                } catch (Exception re) {
                    log.error("Failed to contact channel(s) for broadcast.", re);
                }
            }
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 向所有在线成员发送公会消息
     *
     * @param serverNotice 消息数据包
     */
    public void guildMessage(Packet serverNotice) {
        membersLock.lock();
        try {
            for (GuildCharacter mgc : members) {
                for (Channel cs : Server.getInstance().getChannelsFromWorld(world)) {
                    if (cs.getPlayerStorage().getCharacterById(mgc.getId()) != null) {
                        cs.getPlayerStorage().getCharacterById(mgc.getId()).sendPacket(serverNotice);
                        break;
                    }
                }
            }
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 向在线成员发送聊天消息
     *
     * @param message 消息内容
     */
    public void dropMessage(String message) {
        dropMessage(5, message);
    }

    /**
     * 向在线成员发送指定类型的消息
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void dropMessage(int type, String message) {
        membersLock.lock();
        try {
            for (GuildCharacter mgc : members) {
                if (mgc.getCharacter() != null) {
                    mgc.getCharacter().dropMessage(type, message);
                }
            }
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 广播消息到所有频道
     *
     * @param packet 数据包
     */
    public void broadcastMessage(Packet packet) {
        Server.getInstance().guildMessage(id, packet);
    }

    /**
     * 设置成员在线状态
     *
     * @param cid     角色ID
     * @param online  是否在线
     * @param channel 所在频道
     */
    public final void setOnline(int cid, boolean online, int channel) {
        membersLock.lock();
        try {
            boolean bBroadcast = true;
            for (GuildCharacter mgc : members) {
                if (mgc.getId() == cid) {
                    if (mgc.isOnline() && online) {
                        bBroadcast = false;
                    }
                    mgc.setOnline(online);
                    mgc.setChannel(channel);
                    break;
                }
            }
            if (bBroadcast) {
                this.broadcast(GuildPackets.guildMemberOnline(id, cid, online), cid);
            }
            bDirty = true;
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 发送公会聊天
     *
     * @param name    发送者名称
     * @param cid     发送者ID
     * @param message 聊天内容
     */
    public void guildChat(String name, int cid, String message) {
        membersLock.lock();
        try {
            this.broadcast(PacketCreator.multiChat(name, message, 2), cid);
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 获取指定等级的头衔
     *
     * @param rank 等级（1-5）
     * @return 头衔名称
     */
    public String getRankTitle(int rank) {
        return rankTitles[rank - 1];
    }

    /**
     * 创建公会
     *
     * @param leaderId 会长ID
     * @param name     公会名称
     * @return 公会ID，0表示失败
     */
    public static int createGuild(int leaderId, String name) {
        try (Connection con = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return 0;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO guilds (`leader`, `name`, `signature`) VALUES (?, ?, ?)")) {
                ps.setInt(1, leaderId);
                ps.setString(2, name);
                ps.setInt(3, (int) System.currentTimeMillis());
                ps.executeUpdate();
            }

            final int guildId;
            try (PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE leader = ?")) {
                ps.setInt(1, leaderId);

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    guildId = rs.getInt("guildid");
                }
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ? WHERE id = ?")) {
                ps.setInt(1, guildId);
                ps.setInt(2, leaderId);
                ps.executeUpdate();
            }

            return guildId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 添加公会成员
     *
     * @param mgc 公会角色
     * @param chr 在线角色
     * @return 1成功，0容量已满
     */
    public int addGuildMember(GuildCharacter mgc, Character chr) {
        membersLock.lock();
        try {
            if (members.size() >= capacity) {
                return 0;
            }
            for (int i = members.size() - 1; i >= 0; i--) {
                if (members.get(i).getGuildRank() < 5 || members.get(i).getName().compareTo(mgc.getName()) < 0) {
                    mgc.setCharacter(chr);
                    members.add(i + 1, mgc);
                    bDirty = true;
                    break;
                }
            }

            this.broadcast(GuildPackets.newGuildMember(mgc));
            return 1;
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 成员退出公会
     *
     * @param mgc 公会角色
     */
    public void leaveGuild(GuildCharacter mgc) {
        membersLock.lock();
        try {
            this.broadcast(GuildPackets.memberLeft(mgc, false));
            members.remove(mgc);
            bDirty = true;
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 踢出成员
     *
     * @param initiator   发起者
     * @param name        被踢成员名称
     * @param cid         被踢成员ID
     * @param noteService 信件服务
     */
    public void expelMember(GuildCharacter initiator, String name, int cid, NoteService noteService) {
        membersLock.lock();
        try {
            java.util.Iterator<GuildCharacter> itr = members.iterator();
            GuildCharacter mgc;
            while (itr.hasNext()) {
                mgc = itr.next();
                if (mgc.getId() == cid && initiator.getGuildRank() < mgc.getGuildRank()) {
                    this.broadcast(GuildPackets.memberLeft(mgc, true));
                    itr.remove();
                    bDirty = true;
                    try {
                        if (mgc.isOnline()) {
                            Server.getInstance().getWorld(mgc.getWorld()).setGuildAndRank(cid, 0, 5);
                        } else {
                            noteService.sendNormal("You have been expelled from the guild.", initiator.getName(), mgc.getName());
                            Server.getInstance().getWorld(mgc.getWorld()).setOfflineGuildStatus((short) 0, (byte) 5, cid);
                        }
                    } catch (Exception re) {
                        re.printStackTrace();
                        return;
                    }
                    return;
                }
            }
            log.warn("Unable to find member with name {} and id {}", name, cid);
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 变更成员等级
     *
     * @param cid     角色ID
     * @param newRank 新等级
     */
    public void changeRank(int cid, int newRank) {
        membersLock.lock();
        try {
            for (GuildCharacter mgc : members) {
                if (cid == mgc.getId()) {
                    changeRank(mgc, newRank);
                    return;
                }
            }
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 变更成员等级
     *
     * @param mgc     公会角色
     * @param newRank 新等级
     */
    public void changeRank(GuildCharacter mgc, int newRank) {
        try {
            if (mgc.isOnline()) {
                Server.getInstance().getWorld(mgc.getWorld()).setGuildAndRank(mgc.getId(), this.id, newRank);
                mgc.setGuildRank(newRank);
            } else {
                Server.getInstance().getWorld(mgc.getWorld()).setOfflineGuildStatus((short) this.id, (byte) newRank, mgc.getId());
                mgc.setOfflineGuildRank(newRank);
            }
        } catch (Exception re) {
            re.printStackTrace();
            return;
        }

        membersLock.lock();
        try {
            this.broadcast(GuildPackets.changeRank(mgc));
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 设置公会公告
     *
     * @param notice 公告内容
     */
    public void setGuildNotice(String notice) {
        this.notice = notice;
        this.writeToDB(false);

        membersLock.lock();
        try {
            this.broadcast(GuildPackets.guildNotice(this.id, notice));
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 更新成员等级和职业
     *
     * @param mgc 公会角色
     */
    public void memberLevelJobUpdate(GuildCharacter mgc) {
        membersLock.lock();
        try {
            for (GuildCharacter member : members) {
                if (mgc.equals(member)) {
                    member.setJobId(mgc.getJobId());
                    member.setLevel(mgc.getLevel());
                    this.broadcast(GuildPackets.guildMemberLevelJobUpdate(mgc));
                    break;
                }
            }
        } finally {
            membersLock.unlock();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GuildCharacter o) {
            return (o.getId() == id && o.getName().equals(name));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + this.id;
        return hash;
    }

    /**
     * 变更等级头衔
     *
     * @param ranks 头衔数组
     */
    public void changeRankTitle(String[] ranks) {
        System.arraycopy(ranks, 0, rankTitles, 0, 5);

        membersLock.lock();
        try {
            this.broadcast(GuildPackets.rankTitleChange(this.id, ranks));
        } finally {
            membersLock.unlock();
        }

        this.writeToDB(false);
    }

    /**
     * 解散公会
     * 同时处理联盟关联
     */
    public void disbandGuild() {
        if (allianceId > 0) {
            if (!Alliance.removeGuildFromAlliance(allianceId, id, world)) {
                Alliance.disbandAlliance(allianceId);
            }
        }

        membersLock.lock();
        try {
            this.writeToDB(true);
            this.broadcast(null, -1, BCOp.DISBAND);
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 设置公会徽章
     *
     * @param bg        徽标背景
     * @param bgcolor   徽标背景颜色
     * @param logo      徽标
     * @param logocolor 徽标颜色
     */
    public void setGuildEmblem(short bg, byte bgcolor, short logo, byte logocolor) {
        this.logoBG = bg;
        this.logoBGColor = bgcolor;
        this.logo = logo;
        this.logoColor = logocolor;
        this.writeToDB(false);

        membersLock.lock();
        try {
            this.broadcast(null, -1, BCOp.EMBLEMCHANGE);
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 根据角色ID获取公会角色
     *
     * @param cid 角色ID
     * @return 公会角色对象，未找到返回null
     */
    public GuildCharacter getMGC(int cid) {
        membersLock.lock();
        try {
            for (GuildCharacter mgc : members) {
                if (mgc.getId() == cid) {
                    return mgc;
                }
            }
            return null;
        } finally {
            membersLock.unlock();
        }
    }

    /**
     * 扩容公会
     *
     * @return 是否扩容成功
     */
    public boolean increaseCapacity() {
        if (capacity > 99) {
            return false;
        }
        capacity += 5;
        this.writeToDB(false);

        membersLock.lock();
        try {
            this.broadcast(GuildPackets.guildCapacityChange(this.id, this.capacity));
        } finally {
            membersLock.unlock();
        }

        return true;
    }

    /**
     * 增加公会GP
     *
     * @param amount 增加数量
     */
    public void gainGP(int amount) {
        this.gp += amount;
        this.writeToDB(false);
        this.guildMessage(GuildPackets.updateGP(this.id, this.gp));
        this.guildMessage(PacketCreator.getGPMessage(amount));
    }

    /**
     * 减少公会GP
     *
     * @param amount 减少数量
     */
    public void removeGP(int amount) {
        this.gp -= amount;
        this.writeToDB(false);
        this.guildMessage(GuildPackets.updateGP(this.id, this.gp));
    }

    /**
     * 发送公会邀请
     *
     * @param c          发送邀请的客户端
     * @param targetName 目标角色名称
     * @return 公会响应码，null表示成功发送
     */
    public static GuildResponse sendInvitation(Client c, String targetName) {
        Character mc = c.getChannelServer().getPlayerStorage().getCharacterByName(targetName);
        if (mc == null) {
            return GuildResponse.NOT_IN_CHANNEL;
        }
        if (mc.getGuildId() > 0) {
            return GuildResponse.ALREADY_IN_GUILD;
        }

        Character sender = c.getPlayer();
        if (InviteCoordinator.createInvite(InviteType.GUILD, sender, sender.getGuildId(), mc.getId())) {
            mc.sendPacket(GuildPackets.guildInvite(sender.getGuildId(), sender.getName()));
            return null;
        } else {
            return GuildResponse.MANAGING_INVITE;
        }
    }

    /**
     * 答复公会邀请
     *
     * @param targetId   目标玩家ID
     * @param targetName 目标角色名称
     * @param guildId    公会ID
     * @param answer     是否接受
     * @return 是否接受邀请
     */
    public static boolean answerInvitation(int targetId, String targetName, int guildId, boolean answer) {
        InviteResult res = InviteCoordinator.answerInvite(InviteType.GUILD, targetId, guildId, answer);

        GuildResponse mgr;
        Character sender = res.from;
        switch (res.result) {
            case ACCEPTED:
                return true;

            case DENIED:
                mgr = GuildResponse.DENIED_INVITE;
                break;

            default:
                mgr = GuildResponse.NOT_FOUND_INVITE;
        }

        if (mgr != null && sender != null) {
            sender.sendPacket(mgr.getPacket(targetName));
        }
        return false;
    }

    /**
     * 获取可加入公会的合格玩家
     *
     * @param guildLeader 公会会长
     * @return 合格玩家集合
     */
    public static Set<Character> getEligiblePlayersForGuild(Character guildLeader) {
        Set<Character> guildMembers = new HashSet<>();
        guildMembers.add(guildLeader);

        MatchCheckerCoordinator mmce = guildLeader.getWorldServer().getMatchCheckerCoordinator();
        for (Character chr : guildLeader.getMap().getAllPlayers()) {
            if (chr.getParty() == null && chr.getGuild() == null && mmce.getMatchConfirmationLeaderid(chr.getId()) == -1) {
                guildMembers.add(chr);
            }
        }

        return guildMembers;
    }

    /**
     * 显示公会排行榜
     *
     * @param c     客户端
     * @param npcid NPC ID
     */
    public static void displayGuildRanks(Client c, int npcid) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `name`, `GP`, `logoBG`, `logoBGColor`, `logo`, `logoColor` FROM guilds ORDER BY `GP` DESC LIMIT 50", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = ps.executeQuery()) {
            c.sendPacket(GuildPackets.showGuildRanks(npcid, rs));
        } catch (SQLException e) {
            log.error("Failed to display guild ranks.", e);
        }
    }

    /**
     * 获取联盟ID
     *
     * @return 联盟ID
     */
    public int getAllianceId() {
        return allianceId;
    }

    /**
     * 设置联盟ID
     *
     * @param aid 联盟ID
     */
    public void setAllianceId(int aid) {
        this.allianceId = aid;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE guilds SET allianceId = ? WHERE guildid = ?")) {
            ps.setInt(1, aid);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置公会所有成员的联盟等级
     */
    public void resetAllianceGuildPlayersRank() {
        try {
            membersLock.lock();
            try {
                for (GuildCharacter mgc : members) {
                    if (mgc.isOnline()) {
                        mgc.setAllianceRank(5);
                    }
                }
            } finally {
                membersLock.unlock();
            }

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE characters SET allianceRank = ? WHERE guildid = ?")) {
                ps.setInt(1, 5);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取扩容公会所需费用
     *
     * @param size 当前容量
     * @return 所需费用
     */
    public static int getIncreaseGuildCost(int size) {
        int cost = GameConfig.getServerInt("expand_guild_base_cost") + Math.max(0, (size - 15) / 5) * GameConfig.getServerInt("expand_guild_tier_cost");

        if (size > 30) {
            return Math.min(GameConfig.getServerInt("expand_guild_max_cost"), Math.max(cost, 5000000));
        } else {
            return cost;
        }
    }
}