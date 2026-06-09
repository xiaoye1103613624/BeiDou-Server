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
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.net.server.coordinator.world.InviteCoordinator;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteResult;
import org.gms.net.server.coordinator.world.InviteCoordinator.InviteType;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 联盟
 * 管理多个公会的联盟系统，包括创建、解散、成员管理和消息广播
 *
 * @author XoticStory
 * @author Ronan
 */
public class Alliance {
    /** 联盟中的公会ID列表 */
    final private List<Integer> guilds = new LinkedList<>();

    /** 联盟ID */
    private int allianceId = -1;
    /** 联盟容量 */
    private int capacity;
    /** 联盟名称 */
    private String name;
    /** 联盟公告 */
    private String notice = "";
    /** 联盟等级头衔 */
    private String[] rankTitles = new String[5];

    /**
     * 构造联盟
     *
     * @param name 联盟名称
     * @param id   联盟ID
     */
    public Alliance(String name, int id) {
        this.name = name;
        allianceId = id;
        String[] ranks = {"Master", "Jr. Master", "Member", "Member", "Member"};
        for (int i = 0; i < 5; i++) {
            rankTitles[i] = ranks[i];
        }
    }

    /**
     * 检查联盟名称是否可用
     *
     * @param name 联盟名称
     * @return 是否可用
     */
    public static boolean canBeUsedAllianceName(String name) {
        if (name.contains(" ") || name.length() > 12) {
            return false;
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT name FROM alliance WHERE name = ?")) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取队伍中的公会会长列表
     *
     * @param party 队伍
     * @return 会长列表
     */
    private static List<Character> getPartyGuildMasters(Party party) {
        List<Character> mcl = new LinkedList<>();

        for (PartyCharacter mpc : party.getMembers()) {
            Character chr = mpc.getPlayer();
            if (chr != null) {
                Character lchr = party.getLeader().getPlayer();
                if (chr.getGuildRank() == 1 && lchr != null && chr.getMapId() == lchr.getMapId()) {
                    mcl.add(chr);
                }
            }
        }

        if (!mcl.isEmpty() && !mcl.get(0).isPartyLeader()) {
            for (int i = 1; i < mcl.size(); i++) {
                if (mcl.get(i).isPartyLeader()) {
                    Character temp = mcl.get(0);
                    mcl.set(0, mcl.get(i));
                    mcl.set(i, temp);
                }
            }
        }

        return mcl;
    }

    /**
     * 创建联盟
     *
     * @param party 发起队伍
     * @param name  联盟名称
     * @return 创建的联盟对象，失败返回null
     */
    public static Alliance createAlliance(Party party, String name) {
        List<Character> guildMasters = getPartyGuildMasters(party);
        if (guildMasters.size() != 2) {
            return null;
        }

        List<Integer> guilds = new LinkedList<>();
        for (Character mc : guildMasters) {
            guilds.add(mc.getGuildId());
        }
        Alliance alliance = Alliance.createAllianceOnDb(guilds, name);
        if (alliance != null) {
            alliance.setCapacity(guilds.size());
            for (Integer g : guilds) {
                alliance.addGuild(g);
            }

            int id = alliance.getId();
            try {
                for (int i = 0; i < guildMasters.size(); i++) {
                    Server.getInstance().setGuildAllianceId(guilds.get(i), id);
                    Server.getInstance().resetAllianceGuildPlayersRank(guilds.get(i));

                    Character chr = guildMasters.get(i);
                    chr.getMGC().setAllianceRank((i == 0) ? 1 : 2);
                    Server.getInstance().getGuild(chr.getGuildId()).getMGC(chr.getId()).setAllianceRank((i == 0) ? 1 : 2);
                    chr.saveGuildStatus();
                }

                Server.getInstance().addAlliance(id, alliance);

                int worldid = guildMasters.get(0).getWorld();
                Server.getInstance().allianceMessage(id, GuildPackets.updateAllianceInfo(alliance, worldid), -1, -1);
                // thanks Vcoc for noticing guilds from other alliances being visually stacked here due to this not being updated
                Server.getInstance().allianceMessage(id, GuildPackets.getGuildAlliances(alliance, worldid), -1, -1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return alliance;
    }

    /**
     * 在数据库中创建联盟
     * will create an alliance, where the first guild listed is the leader and the alliance name MUST BE already checked for unicity.
     *
     * @param guilds 公会ID列表
     * @param name   联盟名称
     * @return 创建的联盟对象
     */
    public static Alliance createAllianceOnDb(List<Integer> guilds, String name) {
        int id = -1;
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO `alliance` (`name`) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    id = rs.getInt(1);
                }
            }

            for (int guild : guilds) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `allianceguilds` (`allianceid`, `guildid`) VALUES (?, ?)")) {
                    ps.setInt(1, id);
                    ps.setInt(2, guild);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return new Alliance(name, id);
    }

    /**
     * 从数据库加载联盟
     *
     * @param id 联盟ID
     * @return 联盟对象，不存在返回null
     */
    public static Alliance loadAlliance(int id) {
        if (id <= 0) {
            return null;
        }
        Alliance alliance = new Alliance(null, -1);
        try (Connection con = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM alliance WHERE id = ?")) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    alliance.allianceId = id;
                    alliance.capacity = rs.getInt("capacity");
                    alliance.name = rs.getString("name");
                    alliance.notice = rs.getString("notice");

                    String[] ranks = new String[5];
                    ranks[0] = rs.getString("rank1");
                    ranks[1] = rs.getString("rank2");
                    ranks[2] = rs.getString("rank3");
                    ranks[3] = rs.getString("rank4");
                    ranks[4] = rs.getString("rank5");
                    alliance.rankTitles = ranks;
                }
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT guildid FROM allianceguilds WHERE allianceid = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        alliance.addGuild(rs.getInt("guildid"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alliance;
    }

    /**
     * 保存联盟数据到数据库
     */
    public void saveToDB() {
        try (Connection con = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE `alliance` SET capacity = ?, notice = ?, rank1 = ?, rank2 = ?, rank3 = ?, rank4 = ?, rank5 = ? WHERE id = ?")) {
                ps.setInt(1, this.capacity);
                ps.setString(2, this.notice);

                ps.setString(3, this.rankTitles[0]);
                ps.setString(4, this.rankTitles[1]);
                ps.setString(5, this.rankTitles[2]);
                ps.setString(6, this.rankTitles[3]);
                ps.setString(7, this.rankTitles[4]);

                ps.setInt(8, this.allianceId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `allianceguilds` WHERE allianceid = ?")) {
                ps.setInt(1, this.allianceId);
                ps.executeUpdate();
            }

            for (int guild : guilds) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `allianceguilds` (`allianceid`, `guildid`) VALUES (?, ?)")) {
                    ps.setInt(1, this.allianceId);
                    ps.setInt(2, guild);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解散联盟
     *
     * @param allianceId 联盟ID
     */
    public static void disbandAlliance(int allianceId) {
        try (Connection con = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `alliance` WHERE id = ?")) {
                ps.setInt(1, allianceId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `allianceguilds` WHERE allianceid = ?")) {
                ps.setInt(1, allianceId);
                ps.executeUpdate();
            }

            Server.getInstance().allianceMessage(allianceId, GuildPackets.disbandAlliance(allianceId), -1, -1);
            Server.getInstance().disbandAlliance(allianceId);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 从数据库中移除公会与联盟的关联
     *
     * @param guildId 公会ID
     */
    private static void removeGuildFromAllianceOnDb(int guildId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM `allianceguilds` WHERE guildid = ?")) {
            ps.setInt(1, guildId);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 从联盟中移除公会
     *
     * @param allianceId 联盟ID
     * @param guildId    公会ID
     * @param worldId    世界ID
     * @return 是否成功移除
     */
    public static boolean removeGuildFromAlliance(int allianceId, int guildId, int worldId) {
        Server srv = Server.getInstance();
        Alliance alliance = srv.getAlliance(allianceId);

        if (alliance.getLeader().getGuildId() == guildId) {
            return false;
        }

        srv.allianceMessage(alliance.getId(), GuildPackets.removeGuildFromAlliance(alliance, guildId, worldId), -1, -1);
        srv.removeGuildFromAlliance(alliance.getId(), guildId);
        removeGuildFromAllianceOnDb(guildId);

        srv.allianceMessage(alliance.getId(), GuildPackets.getGuildAlliances(alliance, worldId), -1, -1);
        srv.allianceMessage(alliance.getId(), GuildPackets.allianceNotice(alliance.getId(), alliance.getNotice()), -1, -1);
        srv.guildMessage(guildId, GuildPackets.disbandAlliance(alliance.getId()));

        alliance.dropMessage("[" + srv.getGuild(guildId, worldId).getName() + "] guild has left the union.");
        return true;
    }

    /**
     * 更新联盟数据包
     *
     * @param chr 角色对象
     */
    public void updateAlliancePackets(Character chr) {
        if (allianceId > 0) {
            this.broadcastMessage(GuildPackets.updateAllianceInfo(this, chr.getWorld()));
            this.broadcastMessage(GuildPackets.allianceNotice(this.getId(), this.getNotice()));
        }
    }

    /**
     * 从联盟中移除公会
     *
     * @param gid 公会ID
     * @return 是否成功移除
     */
    public boolean removeGuild(int gid) {
        synchronized (guilds) {
            int index = getGuildIndex(gid);
            if (index == -1) {
                return false;
            }

            guilds.remove(index);
            return true;
        }
    }

    /**
     * 向联盟中添加公会
     *
     * @param gid 公会ID
     * @return 是否成功添加
     */
    public boolean addGuild(int gid) {
        synchronized (guilds) {
            if (guilds.size() == capacity || getGuildIndex(gid) > -1) {
                return false;
            }

            guilds.add(gid);
            return true;
        }
    }

    /**
     * 获取公会在联盟中的索引
     *
     * @param gid 公会ID
     * @return 索引位置，-1表示未找到
     */
    private int getGuildIndex(int gid) {
        synchronized (guilds) {
            for (int i = 0; i < guilds.size(); i++) {
                if (guilds.get(i) == gid) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 设置联盟等级头衔
     *
     * @param ranks 头衔数组
     */
    public void setRankTitle(String[] ranks) {
        rankTitles = ranks;
    }

    /**
     * 获取指定等级的联盟头衔
     *
     * @param rank 等级（1-5）
     * @return 头衔名称
     */
    public String getRankTitle(int rank) {
        return rankTitles[rank - 1];
    }

    /**
     * 获取联盟中的公会ID列表
     *
     * @return 公会ID列表
     */
    public List<Integer> getGuilds() {
        synchronized (guilds) {
            List<Integer> guilds_ = new LinkedList<>();
            for (int guild : guilds) {
                if (guild != -1) {
                    guilds_.add(guild);
                }
            }
            return guilds_;
        }
    }

    /**
     * 获取联盟公告
     *
     * @return 公告内容
     */
    public String getAllianceNotice() {
        return notice;
    }

    /**
     * 获取联盟公告
     *
     * @return 公告内容
     */
    public String getNotice() {
        return notice;
    }

    /**
     * 设置联盟公告
     *
     * @param notice 公告内容
     */
    public void setNotice(String notice) {
        this.notice = notice;
    }

    /**
     * 增加联盟容量
     *
     * @param inc 增加的容量
     */
    public void increaseCapacity(int inc) {
        this.capacity += inc;
    }

    /**
     * 设置联盟容量
     *
     * @param newCapacity 新容量
     */
    public void setCapacity(int newCapacity) {
        this.capacity = newCapacity;
    }

    /**
     * 获取联盟容量
     *
     * @return 容量值
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * 获取联盟ID
     *
     * @return 联盟ID
     */
    public int getId() {
        return allianceId;
    }

    /**
     * 获取联盟名称
     *
     * @return 联盟名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取联盟盟主
     *
     * @return 盟主公会角色
     */
    public GuildCharacter getLeader() {
        synchronized (guilds) {
            for (Integer gId : guilds) {
                Guild guild = Server.getInstance().getGuild(gId);
                GuildCharacter mgc = guild.getMGC(guild.getLeaderId());

                if (mgc.getAllianceRank() == 1) {
                    return mgc;
                }
            }

            return null;
        }
    }

    /**
     * 向联盟中所有公会发送消息
     *
     * @param message 消息内容
     */
    public void dropMessage(String message) {
        dropMessage(5, message);
    }

    /**
     * 向联盟中所有公会发送指定类型的消息
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void dropMessage(int type, String message) {
        synchronized (guilds) {
            for (Integer gId : guilds) {
                Guild guild = Server.getInstance().getGuild(gId);
                guild.dropMessage(type, message);
            }
        }
    }

    /**
     * 广播数据包到联盟中的所有公会
     *
     * @param packet 数据包
     */
    public void broadcastMessage(Packet packet) {
        Server.getInstance().allianceMessage(allianceId, packet, -1, -1);
    }

    /**
     * 发送联盟邀请
     *
     * @param c              发起邀请的客户端
     * @param targetGuildName 目标公会名称
     * @param allianceId     联盟ID
     */
    public static void sendInvitation(Client c, String targetGuildName, int allianceId) {
        Guild mg = Server.getInstance().getGuildByName(targetGuildName);
        if (mg == null) {
            c.getPlayer().dropMessage(5, "The entered guild does not exist.");
        } else {
            if (mg.getAllianceId() > 0) {
                c.getPlayer().dropMessage(5, "The entered guild is already registered on a guild alliance.");
            } else {
                Character victim = mg.getMGC(mg.getLeaderId()).getCharacter();
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "The master of the guild that you offered an invitation is currently not online.");
                } else {
                    if (InviteCoordinator.createInvite(InviteType.ALLIANCE, c.getPlayer(), allianceId, victim.getId())) {
                        victim.sendPacket(GuildPackets.allianceInvite(allianceId, c.getPlayer()));
                    } else {
                        c.getPlayer().dropMessage(5, "The master of the guild that you offered an invitation is currently managing another invite.");
                    }
                }
            }
        }
    }

    /**
     * 答复联盟邀请
     *
     * @param targetId       目标玩家ID
     * @param targetGuildName 目标公会名称
     * @param allianceId     联盟ID
     * @param answer         是否接受
     * @return 是否接受邀请
     */
    public static boolean answerInvitation(int targetId, String targetGuildName, int allianceId, boolean answer) {
        InviteResult res = InviteCoordinator.answerInvite(InviteType.ALLIANCE, targetId, allianceId, answer);

        String msg;
        Character sender = res.from;
        switch (res.result) {
            case ACCEPTED:
                return true;

            case DENIED:
                msg = "[" + targetGuildName + "] guild has denied your guild alliance invitation.";
                break;

            default:
                msg = "The guild alliance request has not been accepted, since the invitation expired.";
        }

        if (sender != null) {
            sender.dropMessage(5, msg);
        }

        return false;
    }
}