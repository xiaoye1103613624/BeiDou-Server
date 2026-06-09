package org.gms.net.server.guild;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;
import org.gms.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 公会数据包工厂类
 * 生成公会和联盟相关的网络数据包，包括信息展示、成员变更、排名等
 */
public class GuildPackets {

    /**
     * 生成显示公会信息的数据包
     *
     * @param chr 角色对象，若为null则显示空公会
     * @return 公会信息数据包
     */
    public static Packet showGuildInfo(Character chr) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        // signature for showing guild info
        p.writeByte(0x1A);
        // show empty guild (used for leaving, expelled)
        if (chr == null) {
            p.writeByte(0);
            return p;
        }
        Guild g = chr.getClient().getWorldServer().getGuild(chr.getMGC());
        // failed to read from DB - don't show a guild
        if (g == null) {
            p.writeByte(0);
            return p;
        }
        if (g.getName() == null) {
            p.writeByte(0);
            return p;
        }
        // bInGuild
        p.writeByte(1);
        p.writeInt(g.getId());
        p.writeString(g.getName());
        for (int i = 1; i <= 5; i++) {
            p.writeString(g.getRankTitle(i));
        }
        Collection<GuildCharacter> members = g.getMembers();
        // then it is the size of all the members
        p.writeByte(members.size());
        // and each of their character ids o_O
        for (GuildCharacter mgc : members) {
            p.writeInt(mgc.getId());
        }
        for (GuildCharacter mgc : members) {
            p.writeFixedString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            p.writeInt(mgc.getJobId());
            p.writeInt(mgc.getLevel());
            p.writeInt(mgc.getGuildRank());
            p.writeInt(mgc.isOnline() ? 1 : 0);
            p.writeInt(g.getSignature());
            p.writeInt(mgc.getAllianceRank());
        }
        p.writeInt(g.getCapacity());
        p.writeShort(g.getLogoBG());
        p.writeByte(g.getLogoBGColor());
        p.writeShort(g.getLogo());
        p.writeByte(g.getLogoColor());
        p.writeString(g.getNotice());
        p.writeInt(g.getGP());
        p.writeInt(g.getAllianceId());
        return p;
    }

    /**
     * 生成公会成员在线状态变更数据包
     *
     * @param guildId 公会ID
     * @param chrId   角色ID
     * @param bOnline 是否在线
     * @return 在线状态数据包
     */
    public static Packet guildMemberOnline(int guildId, int chrId, boolean bOnline) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x3d);
        p.writeInt(guildId);
        p.writeInt(chrId);
        p.writeBool(bOnline);
        return p;
    }

    /**
     * 生成公会邀请数据包
     *
     * @param guildId  公会ID
     * @param charName 被邀请角色名称
     * @return 邀请数据包
     */
    public static Packet guildInvite(int guildId, String charName) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x05);
        p.writeInt(guildId);
        p.writeString(charName);
        return p;
    }

    /**
     * 生成公会创建消息数据包
     *
     * @param masterName 会长名称
     * @param guildName  公会名称
     * @return 创建消息数据包
     */
    public static Packet createGuildMessage(String masterName, String guildName) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x3);
        p.writeInt(0);
        p.writeString(masterName);
        p.writeString(guildName);
        return p;
    }

    /**
     * 获取通用公会消息数据包
     * <p>
     * Possible values for <code>code</code>:<br> 28: guild name already in use<br>
     * 31: problem in locating players during agreement<br> 33/40: already joined a guild<br>
     * 35: Cannot make guild<br> 36: problem in player agreement<br> 38: problem during forming guild<br>
     * 41: max number of players in joining guild<br> 42: character can't be found this channel<br>
     * 45/48: character not in guild<br> 52: problem in disbanding guild<br> 56: admin cannot make guild<br>
     * 57: problem in increasing guild size<br>
     *
     * @param code 响应码
     * @return 公会消息数据包
     */
    public static Packet genericGuildMessage(byte code) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(code);
        return p;
    }

    /**
     * 获取带目标名称的公会消息数据包
     * <p>
     * 53: player not accepting guild invites<br>
     * 54: player already managing an invite<br> 55: player denied an invite<br>
     *
     * @param code       响应码
     * @param targetName 目标角色名称
     * @return 公会消息数据包
     */
    public static Packet responseGuildMessage(byte code, String targetName) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(code);
        p.writeString(targetName);
        return p;
    }

    /**
     * 生成新成员加入公会数据包
     *
     * @param mgc 公会角色
     * @return 新成员数据包
     */
    public static Packet newGuildMember(GuildCharacter mgc) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x27);
        p.writeInt(mgc.getGuildId());
        p.writeInt(mgc.getId());
        p.writeFixedString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
        p.writeInt(mgc.getJobId());
        p.writeInt(mgc.getLevel());
        // should be always 5 but whatevs
        p.writeInt(mgc.getGuildRank());
        // should always be 1 too
        p.writeInt(mgc.isOnline() ? 1 : 0);
        // ? could be guild signature, but doesn't seem to matter
        p.writeInt(1);
        p.writeInt(3);
        return p;
    }

    /**
     * 生成成员离开/被踢出公会数据包
     * mode == 0x2c for leaving, 0x2f for expelled
     *
     * @param mgc       公会角色
     * @param bExpelled 是否被踢出
     * @return 成员离开数据包
     */
    public static Packet memberLeft(GuildCharacter mgc, boolean bExpelled) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(bExpelled ? 0x2f : 0x2c);
        p.writeInt(mgc.getGuildId());
        p.writeInt(mgc.getId());
        p.writeString(mgc.getName());
        return p;
    }

    /**
     * 生成公会等级变更数据包
     *
     * @param mgc 公会角色
     * @return 等级变更数据包
     */
    public static Packet changeRank(GuildCharacter mgc) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x40);
        p.writeInt(mgc.getGuildId());
        p.writeInt(mgc.getId());
        p.writeByte(mgc.getGuildRank());
        return p;
    }

    /**
     * 生成公会公告数据包
     *
     * @param guildId 公会ID
     * @param notice  公告内容
     * @return 公告数据包
     */
    public static Packet guildNotice(int guildId, String notice) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x44);
        p.writeInt(guildId);
        p.writeString(notice);
        return p;
    }

    /**
     * 生成公会成员等级/职业更新数据包
     *
     * @param mgc 公会角色
     * @return 更新数据包
     */
    public static Packet guildMemberLevelJobUpdate(GuildCharacter mgc) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x3C);
        p.writeInt(mgc.getGuildId());
        p.writeInt(mgc.getId());
        p.writeInt(mgc.getLevel());
        p.writeInt(mgc.getJobId());
        return p;
    }

    /**
     * 生成公会等级头衔变更数据包
     *
     * @param guildId 公会ID
     * @param ranks   等级头衔数组
     * @return 头衔变更数据包
     */
    public static Packet rankTitleChange(int guildId, String[] ranks) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x3E);
        p.writeInt(guildId);
        for (int i = 0; i < 5; i++) {
            p.writeString(ranks[i]);
        }
        return p;
    }

    /**
     * 生成公会解散数据包
     *
     * @param guildId 公会ID
     * @return 解散数据包
     */
    public static Packet guildDisband(int guildId) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x32);
        p.writeInt(guildId);
        p.writeByte(1);
        return p;
    }

    /**
     * 生成公会任务等待通知数据包
     *
     * @param channel    频道号
     * @param waitingPos 等待位置
     * @return 等待通知数据包
     */
    public static Packet guildQuestWaitingNotice(byte channel, int waitingPos) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x4C);
        p.writeByte(channel - 1);
        p.writeByte(waitingPos);
        return p;
    }

    /**
     * 生成公会徽章变更数据包
     *
     * @param guildId  公会ID
     * @param bg       徽标背景
     * @param bgcolor  徽标背景颜色
     * @param logo     徽标
     * @param logoColor 徽标颜色
     * @return 徽章变更数据包
     */
    public static Packet guildEmblemChange(int guildId, short bg, byte bgcolor, short logo, byte logoColor) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x42);
        p.writeInt(guildId);
        p.writeShort(bg);
        p.writeByte(bgcolor);
        p.writeShort(logo);
        p.writeByte(logoColor);
        return p;
    }

    /**
     * 生成公会容量变更数据包
     *
     * @param guildId  公会ID
     * @param capacity 新容量
     * @return 容量变更数据包
     */
    public static Packet guildCapacityChange(int guildId, int capacity) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x3A);
        p.writeInt(guildId);
        p.writeByte(capacity);
        return p;
    }

    /**
     * 添加公告板帖子信息到数据包
     *
     * @param p  数据包
     * @param rs 结果集
     * @throws SQLException 数据库异常
     */
    public static void addThread(final OutPacket p, ResultSet rs) throws SQLException {
        p.writeInt(rs.getInt("localthreadid"));
        p.writeInt(rs.getInt("postercid"));
        p.writeString(rs.getString("name"));
        p.writeLong(PacketCreator.getTime(rs.getLong("timestamp")));
        p.writeInt(rs.getInt("icon"));
        p.writeInt(rs.getInt("replycount"));
    }

    /**
     * 生成公告板帖子列表数据包
     *
     * @param rs    结果集
     * @param start 起始位置
     * @return 帖子列表数据包
     * @throws SQLException 数据库异常
     */
    public static Packet BBSThreadList(ResultSet rs, int start) throws SQLException {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_BBS_PACKET);
        p.writeByte(0x06);
        if (!rs.last()) {
            p.writeByte(0);
            p.writeInt(0);
            p.writeInt(0);
            return p;
        }
        int threadCount = rs.getRow();
        // has a notice
        if (rs.getInt("localthreadid") == 0) {
            p.writeByte(1);
            addThread(p, rs);
            // one thread didn't count (because it's a notice)
            threadCount--;
        } else {
            p.writeByte(0);
        }
        // seek to the thread before where we start
        if (!rs.absolute(start + 1)) {
            // uh, we're trying to start at a place past possible
            rs.first();
            start = 0;
        }
        p.writeInt(threadCount);
        p.writeInt(Math.min(10, threadCount - start));
        for (int i = 0; i < Math.min(10, threadCount - start); i++) {
            addThread(p, rs);
            rs.next();
        }
        return p;
    }

    /**
     * 生成显示单个帖子的数据包
     *
     * @param localthreadid 本地帖子ID
     * @param threadRS      帖子结果集
     * @param repliesRS     回复结果集
     * @return 帖子详情数据包
     * @throws SQLException 数据库异常
     * @throws RuntimeException 回复数量不匹配
     */
    public static Packet showThread(int localthreadid, ResultSet threadRS, ResultSet repliesRS) throws SQLException, RuntimeException {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_BBS_PACKET);
        p.writeByte(0x07);
        p.writeInt(localthreadid);
        p.writeInt(threadRS.getInt("postercid"));
        p.writeLong(PacketCreator.getTime(threadRS.getLong("timestamp")));
        p.writeString(threadRS.getString("name"));
        p.writeString(threadRS.getString("startpost"));
        p.writeInt(threadRS.getInt("icon"));
        if (repliesRS != null) {
            int replyCount = threadRS.getInt("replycount");
            p.writeInt(replyCount);
            int i;
            for (i = 0; i < replyCount && repliesRS.next(); i++) {
                p.writeInt(repliesRS.getInt("replyid"));
                p.writeInt(repliesRS.getInt("postercid"));
                p.writeLong(PacketCreator.getTime(repliesRS.getLong("timestamp")));
                p.writeString(repliesRS.getString("content"));
            }
            if (i != replyCount || repliesRS.next()) {
                throw new RuntimeException(String.valueOf(threadRS.getInt("threadid")));
            }
        } else {
            p.writeInt(0);
        }
        return p;
    }

    /**
     * 生成公会排行榜数据包
     *
     * @param npcid NPC ID
     * @param rs    结果集
     * @return 排行榜数据包
     * @throws SQLException 数据库异常
     */
    public static Packet showGuildRanks(int npcid, ResultSet rs) throws SQLException {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x49);
        p.writeInt(npcid);
        // no guilds o.o
        if (!rs.last()) {
            p.writeInt(0);
            return p;
        }
        // number of entries
        p.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            p.writeString(rs.getString("name"));
            p.writeInt(rs.getInt("GP"));
            p.writeInt(rs.getInt("logo"));
            p.writeInt(rs.getInt("logoColor"));
            p.writeInt(rs.getInt("logoBG"));
            p.writeInt(rs.getInt("logoBGColor"));
        }
        return p;
    }

    /**
     * 生成玩家排行榜数据包
     *
     * @param npcid        NPC ID
     * @param worldRanking 世界排名列表
     * @return 排行榜数据包
     */
    public static Packet showPlayerRanks(int npcid, List<Pair<String, Integer>> worldRanking) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x49);
        p.writeInt(npcid);
        if (worldRanking.isEmpty()) {
            p.writeInt(0);
            return p;
        }
        p.writeInt(worldRanking.size());
        for (Pair<String, Integer> wr : worldRanking) {
            p.writeString(wr.getLeft());
            p.writeInt(wr.getRight());
            p.writeInt(0);
            p.writeInt(0);
            p.writeInt(0);
            p.writeInt(0);
        }
        return p;
    }

    /**
     * 生成更新公会GP数据包
     *
     * @param guildId 公会ID
     * @param GP      公会GP值
     * @return GP更新数据包
     */
    public static Packet updateGP(int guildId, int GP) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_OPERATION);
        p.writeByte(0x48);
        p.writeInt(guildId);
        p.writeInt(GP);
        return p;
    }

    /**
     * 将公会信息写入数据包
     *
     * @param p     数据包
     * @param guild 公会对象
     */
    public static void getGuildInfo(OutPacket p, Guild guild) {
        p.writeInt(guild.getId());
        p.writeString(guild.getName());
        for (int i = 1; i <= 5; i++) {
            p.writeString(guild.getRankTitle(i));
        }
        Collection<GuildCharacter> members = guild.getMembers();
        p.writeByte(members.size());
        for (GuildCharacter mgc : members) {
            p.writeInt(mgc.getId());
        }
        for (GuildCharacter mgc : members) {
            p.writeFixedString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            p.writeInt(mgc.getJobId());
            p.writeInt(mgc.getLevel());
            p.writeInt(mgc.getGuildRank());
            p.writeInt(mgc.isOnline() ? 1 : 0);
            p.writeInt(guild.getSignature());
            p.writeInt(mgc.getAllianceRank());
        }
        p.writeInt(guild.getCapacity());
        p.writeShort(guild.getLogoBG());
        p.writeByte(guild.getLogoBGColor());
        p.writeShort(guild.getLogo());
        p.writeByte(guild.getLogoColor());
        p.writeString(guild.getNotice());
        p.writeInt(guild.getGP());
        p.writeInt(guild.getAllianceId());
    }

    /**
     * 生成联盟信息数据包
     *
     * @param alliance 联盟对象
     * @return 联盟信息数据包
     */
    public static Packet getAllianceInfo(Alliance alliance) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x0C);
        p.writeByte(1);
        p.writeInt(alliance.getId());
        p.writeString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            p.writeString(alliance.getRankTitle(i));
        }
        p.writeByte(alliance.getGuilds().size());
        // probably capacity
        p.writeInt(alliance.getCapacity());
        for (Integer guild : alliance.getGuilds()) {
            p.writeInt(guild);
        }
        p.writeString(alliance.getNotice());
        return p;
    }

    /**
     * 生成更新联盟信息数据包
     *
     * @param alliance 联盟对象
     * @param world    世界ID
     * @return 更新联盟信息数据包
     */
    public static Packet updateAllianceInfo(Alliance alliance, int world) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x0F);
        p.writeInt(alliance.getId());
        p.writeString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            p.writeString(alliance.getRankTitle(i));
        }
        p.writeByte(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            p.writeInt(guild);
        }
        // probably capacity
        p.writeInt(alliance.getCapacity());
        p.writeShort(0);
        for (Integer guildid : alliance.getGuilds()) {
            getGuildInfo(p, Server.getInstance().getGuild(guildid, world));
        }
        return p;
    }

    /**
     * 生成联盟公会列表数据包
     *
     * @param alliance 联盟对象
     * @param worldId  世界ID
     * @return 联盟公会列表数据包
     */
    public static Packet getGuildAlliances(Alliance alliance, int worldId) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x0D);
        p.writeInt(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            getGuildInfo(p, Server.getInstance().getGuild(guild, worldId));
        }
        return p;
    }

    /**
     * 生成添加公会到联盟的数据包
     *
     * @param alliance 联盟对象
     * @param newGuild 新公会ID
     * @param c        客户端
     * @return 添加公会数据包
     */
    public static Packet addGuildToAlliance(Alliance alliance, int newGuild, Client c) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x12);
        p.writeInt(alliance.getId());
        p.writeString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            p.writeString(alliance.getRankTitle(i));
        }
        p.writeByte(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            p.writeInt(guild);
        }
        p.writeInt(alliance.getCapacity());
        p.writeString(alliance.getNotice());
        p.writeInt(newGuild);
        getGuildInfo(p, Server.getInstance().getGuild(newGuild, c.getWorld(), null));
        return p;
    }

    /**
     * 生成联盟成员在线状态数据包
     *
     * @param mc     角色对象
     * @param online 是否在线
     * @return 在线状态数据包
     */
    public static Packet allianceMemberOnline(Character mc, boolean online) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x0E);
        p.writeInt(mc.getGuild().getAllianceId());
        p.writeInt(mc.getGuildId());
        p.writeInt(mc.getId());
        p.writeBool(online);
        return p;
    }

    /**
     * 生成联盟公告数据包
     *
     * @param id     联盟ID
     * @param notice 公告内容
     * @return 联盟公告数据包
     */
    public static Packet allianceNotice(int id, String notice) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x1C);
        p.writeInt(id);
        p.writeString(notice);
        return p;
    }

    /**
     * 生成变更联盟等级头衔数据包
     *
     * @param alliance 联盟ID
     * @param ranks    头衔数组
     * @return 头衔变更数据包
     */
    public static Packet changeAllianceRankTitle(int alliance, String[] ranks) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x1A);
        p.writeInt(alliance);
        for (int i = 0; i < 5; i++) {
            p.writeString(ranks[i]);
        }
        return p;
    }

    /**
     * 生成更新联盟成员职业/等级数据包
     *
     * @param mc 角色对象
     * @return 更新数据包
     */
    public static Packet updateAllianceJobLevel(Character mc) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x18);
        p.writeInt(mc.getGuild().getAllianceId());
        p.writeInt(mc.getGuildId());
        p.writeInt(mc.getId());
        p.writeInt(mc.getLevel());
        p.writeInt(mc.getJob().getId());
        return p;
    }

    /**
     * 生成从联盟中移除公会数据包
     *
     * @param alliance      联盟对象
     * @param expelledGuild 被移除的公会ID
     * @param worldId       世界ID
     * @return 移除公会数据包
     */
    public static Packet removeGuildFromAlliance(Alliance alliance, int expelledGuild, int worldId) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x10);
        p.writeInt(alliance.getId());
        p.writeString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            p.writeString(alliance.getRankTitle(i));
        }
        p.writeByte(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            p.writeInt(guild);
        }
        p.writeInt(alliance.getCapacity());
        p.writeString(alliance.getNotice());
        p.writeInt(expelledGuild);
        getGuildInfo(p, Server.getInstance().getGuild(expelledGuild, worldId, null));
        p.writeByte(0x01);
        return p;
    }

    /**
     * 生成解散联盟数据包
     *
     * @param alliance 联盟ID
     * @return 解散联盟数据包
     */
    public static Packet disbandAlliance(int alliance) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x1D);
        p.writeInt(alliance);
        return p;
    }

    /**
     * 生成联盟邀请数据包
     *
     * @param allianceid 联盟ID
     * @param chr        被邀请角色
     * @return 联盟邀请数据包
     */
    public static Packet allianceInvite(int allianceid, Character chr) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x03);
        p.writeInt(allianceid);
        p.writeString(chr.getName());
        p.writeShort(0);
        return p;
    }

    /**
     * 生成公会Boss治疗者移动数据包
     *
     * @param nY 新Y位置
     * @return 移动数据包
     */
    public static Packet GuildBoss_HealerMove(short nY) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_BOSS_HEALER_MOVE);
        // New Y Position
        p.writeShort(nY);
        return p;
    }

    /**
     * 生成公会Boss滑轮状态变更数据包
     *
     * @param nState 滑轮状态
     * @return 状态变更数据包
     */
    public static Packet GuildBoss_PulleyStateChange(byte nState) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_BOSS_PULLEY_STATE_CHANGE);
        p.writeByte(nState);
        return p;
    }

    /**
     * 生成公会名称变更数据包
     * thanks to Arnah (Vertisy)
     *
     * @param chrid     角色ID
     * @param guildName 公会名称，空字符串表示无
     * @return 名称变更数据包
     */
    public static Packet guildNameChanged(int chrid, String guildName) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_NAME_CHANGED);
        p.writeInt(chrid);
        p.writeString(guildName);
        return p;
    }

    /**
     * 生成公会徽标变更数据包
     *
     * @param chrId 角色ID
     * @param guild 公会对象
     * @return 徽标变更数据包
     */
    public static Packet guildMarkChanged(int chrId, Guild guild) {
        OutPacket p = OutPacket.create(SendOpcode.GUILD_MARK_CHANGED);
        p.writeInt(chrId);
        p.writeShort(guild.getLogoBG());
        p.writeByte(guild.getLogoBGColor());
        p.writeShort(guild.getLogo());
        p.writeByte(guild.getLogoColor());
        return p;
    }

    /**
     * 生成显示联盟信息数据包
     *
     * @param allianceid 联盟ID
     * @param playerid   玩家ID
     * @return 显示信息数据包
     */
    public static Packet sendShowInfo(int allianceid, int playerid) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x02);
        p.writeInt(allianceid);
        p.writeInt(playerid);
        return p;
    }

    /**
     * 生成发送联盟邀请数据包
     *
     * @param allianceid 联盟ID
     * @param playerid   玩家ID
     * @param guildname  公会名称
     * @return 邀请数据包
     */
    public static Packet sendInvitation(int allianceid, int playerid, final String guildname) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x05);
        p.writeInt(allianceid);
        p.writeInt(playerid);
        p.writeString(guildname);
        return p;
    }

    /**
     * 生成变更公会联盟数据包
     *
     * @param allianceid 联盟ID
     * @param playerid   玩家ID
     * @param guildid    公会ID
     * @param option     选项
     * @return 变更数据包
     */
    public static Packet sendChangeGuild(int allianceid, int playerid, int guildid, int option) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x07);
        p.writeInt(allianceid);
        p.writeInt(guildid);
        p.writeInt(playerid);
        p.writeByte(option);
        return p;
    }

    /**
     * 生成变更联盟盟主数据包
     *
     * @param allianceid 联盟ID
     * @param playerid   玩家ID
     * @param victim     新任盟主ID
     * @return 变更盟主数据包
     */
    public static Packet sendChangeLeader(int allianceid, int playerid, int victim) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x08);
        p.writeInt(allianceid);
        p.writeInt(playerid);
        p.writeInt(victim);
        return p;
    }

    /**
     * 生成变更联盟等级数据包
     *
     * @param allianceid 联盟ID
     * @param playerid   玩家ID
     * @param int1       参数1
     * @param byte1      参数2
     * @return 变更等级数据包
     */
    public static Packet sendChangeRank(int allianceid, int playerid, int int1, byte byte1) {
        OutPacket p = OutPacket.create(SendOpcode.ALLIANCE_OPERATION);
        p.writeByte(0x09);
        p.writeInt(allianceid);
        p.writeInt(playerid);
        p.writeInt(int1);
        p.writeInt(byte1);
        return p;
    }
}