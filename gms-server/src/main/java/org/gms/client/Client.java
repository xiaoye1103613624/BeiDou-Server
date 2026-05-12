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
package org.gms.client;

import lombok.Getter;
import org.gms.client.inventory.InventoryType;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.MapId;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.gms.net.PacketHandler;
import org.gms.net.PacketProcessor;
import org.gms.net.netty.InvalidPacketHeaderException;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.Packet;
import org.gms.net.packet.logging.LoggingUtil;
import org.gms.net.packet.logging.MonitoredChrLogger;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.login.LoginBypassCoordinator;
import org.gms.net.server.coordinator.session.Hwid;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.net.server.coordinator.session.SessionCoordinator.AntiMulticlientResult;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.guild.GuildCharacter;
import org.gms.net.server.guild.GuildPackets;
import org.gms.net.server.world.MessengerCharacter;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.PartyOperation;
import org.gms.net.server.world.World;
import org.gms.server.SystemRescue;
import org.gms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.event.EventManager;
import org.gms.scripting.npc.NPCConversationManager;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.scripting.quest.QuestActionManager;
import org.gms.scripting.quest.QuestScriptManager;
import org.gms.server.MapleLeafLogger;
import org.gms.server.ThreadManager;
import org.gms.server.TimerManager;
import org.gms.server.life.Monster;
import org.gms.server.maps.FieldLimit;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MiniDungeonInfo;

import javax.script.ScriptEngine;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 游戏客户端会话：封装一条 Netty TCP 连接与对应账号/角色的服务端状态。
 * <p>
 * 主要职责包括：
 * <ul>
 *   <li>作为 {@link ChannelInboundHandlerAdapter} 处理连接激活、入站封包、空闲、异常与断开；</li>
 *   <li>根据封包头 opcode 从 {@link PacketProcessor} 取得 {@link PacketHandler} 并执行业务逻辑；</li>
 *   <li>维护登录状态、会话过渡、PIN/PIC、HWID/MAC、投票点、脚本引擎等与账号相关的数据；</li>
 *   <li>提供向客户端写包、换频道、断线保存与清理（队伍、公会、好友等）的统一入口。</li>
 * </ul>
 * 登录服与频道服上的连接均使用本类，通过 {@link Type} 区分行为（如断线时走不同会话关闭路径）。
 */
public class Client extends ChannelInboundHandlerAdapter {
    /** 本类日志记录器。 */
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    /** 账号在数据库中的登录状态：未登录（可正常登录）。 */
    public static final int LOGIN_NOTLOGGEDIN = 0;
    /** 账号正在服务器间切换（例如选角后进频道、换频道），此时不应视为完全在线。 */
    public static final int LOGIN_SERVER_TRANSITION = 1;
    /** 账号已登录（已建立有效游戏会话）。 */
    public static final int LOGIN_LOGGEDIN = 2;

    /** 本会话所属类型：登录服或频道服。 */
    private final Type type;
    /** 由会话协调器分配的全局唯一会话 ID，用于多开检测与会话管理。 */
    private final long sessionId;
    /** 当前连接使用的封包处理器（opcode → Handler 映射）。 */
    private final PacketProcessor packetProcessor;

    /** 客户端硬件标识，用于封禁与登录绕过判定。 */
    private Hwid hwid;
    /** 对端 IP 字符串（用于日志、IP 封禁查询等）。 */
    private String remoteAddress;
    /** 是否处于角色/频道切换等“过渡”状态，过渡中断线时不应重复执行完整断线逻辑。 */
    private volatile boolean inTransition;

    /** Netty 通道，所有出站数据经此写出。 */
    private io.netty.channel.Channel ioChannel;
    /** 当前绑定的游戏角色；登录选角前可能为 null。 */
    private Character player;
    /** 当前频道编号（1 起）；特殊值如 -1 表示现金店等场景。 */
    private int channel = 1;
    /** 数据库账号主键；-4 未初始化，-2/-3 等为登录流程中的哨兵值。 */
    private int accId = -4;
    /** 内存中的“已登录”标记，与 {@link #getLoginState()} 数据库状态配合使用。 */
    private boolean loggedIn = false;
    /** 是否处于跨服/跨频道过渡（与 {@link Client#LOGIN_SERVER_TRANSITION} 对应）。 */
    private boolean serverTransition = false;
    /** 账号生日（用于删除角色等敏感操作的二次校验）。 */
    private Calendar birthday = null;
    /** 当前登录的账号名。 */
    private String accountName = null;
    /** 大区（世界）编号。 */
    private int world;
    /** 最近一次收到客户端 Pong 的时间戳（毫秒），用于空闲断开检测。 */
    private volatile long lastPong;
    /** GM 等级（权限）。 */
    private int gmlevel;
    /** 本账号已记录的 MAC 地址集合（不可变视图通过 {@link #getMacs()} 暴露）。 */
    private Set<String> macs = new HashSet<>();
    /** NPC/任务等脚本引擎缓存，按名称索引。 */
    private Map<String, ScriptEngine> engines = new HashMap<>();
    /** 账号允许创建的角色栏位数。 */
    private byte characterSlots = 3;
    /** 本次连接上的登录失败尝试次数，过多将踢线。 */
    private byte loginattempt = 0;
    /** 二级密码 PIN（字符串形式，与数据库一致）。 */
    private String pin = "";
    /** PIN 校验失败次数，超过阈值将关闭会话。 */
    private int pinattempt = 0;
    /** 图片密保 PIC。 */
    private String pic = "";
    /** PIC 校验失败次数。 */
    private int picattempt = 0;
    /** 现金券相关操作短时尝试次数，防刷。 */
    private byte csattempt = 0;
    /** 账号性别；-1 表示未设置。 */
    private byte gender = -1;
    /** 是否已进入断线流程，防止并发重复断开。 */
    private boolean disconnecting = false;
    /**
     * 与 {@link #lock}、{@link #encoderLock} 配合的许可量，限制同时进入“客户端临界区”的并发数，
     * 减轻共享锁竞争（社区贡献的并发优化思路）。
     */
    private final Semaphore actionsSemaphore = new Semaphore(7);
    /** 玩家逻辑与脚本等使用的可重入公平锁。 */
    private final Lock lock = new ReentrantLock(true);
    /** 登录状态更新等需要与写包顺序互斥时使用的锁。 */
    private final Lock encoderLock = new ReentrantLock(true);
    /** 出站 {@link #sendPacket} 串行化，避免多线程交错写导致客户端解析异常。 */
    private final Lock announcerLock = new ReentrantLock(true);
    /** 账号临时封禁截止时间；非 null 表示处于封禁期内。 */
    private Calendar tempBanCalendar;
    /** 内存缓存的投票点数（与数据库同步）。 */
    private int votePoints;
    /** 上次投票记录的时间戳缓存；-1 表示未加载。 */
    private int voteTime = -1;
    /** 客户端请求服务器列表时可见的世界数量（用于相关限流或展示）。 */
    private int visibleWorlds;
    /** 上次 NPC 点击或请求角色列表的时间（服务器时钟），用于防连点。 */
    private long lastNpcClick;
    /** 上次收到任意客户端封包的时间，用于统计与空闲策略。 */
    private long lastPacket = System.currentTimeMillis();
    /** 客户端语言/区域偏好（与账号表 language 字段对应）。 */
    private int lang = 0;
    /**
     * 系统救援实例：在 {@link #exceptionCaught} 等场景尝试把卡地图的角色拉回安全地图。
     * 注意：设为 static 且随 {@link #setPlayer} 覆盖，多会话下以最后一次设置为准。
     */
    @Getter
    private static SystemRescue sysRescue;

    /**
     * 客户端连接所挂接的服务器类型。
     */
    public enum Type {
        /** 登录服务器（处理账号登录、选角等）。 */
        LOGIN,
        /** 频道服务器（处理游戏内逻辑）。 */
        CHANNEL
    }

    /**
     * 构造一个客户端会话（通常通过 {@link #createLoginClient} / {@link #createChannelClient} 创建）。
     *
     * @param type            登录服或频道服
     * @param sessionId       会话唯一 ID
     * @param remoteAddress   初始远端地址字符串（连接建立后可能被覆盖）
     * @param packetProcessor 封包处理器
     * @param world           世界编号
     * @param channel         频道编号
     */
    public Client(Type type, long sessionId, String remoteAddress, PacketProcessor packetProcessor, int world, int channel) {
        this.type = type;
        this.sessionId = sessionId;
        this.remoteAddress = remoteAddress;
        this.packetProcessor = packetProcessor;
        this.world = world;
        this.channel = channel;
    }

    /**
     * 创建挂在登录服务器上的客户端实例。
     *
     * @param sessionId       会话 ID
     * @param remoteAddress   远端地址描述
     * @param packetProcessor 封包处理器
     * @param world           世界编号
     * @param channel         频道编号
     * @return 登录服 {@link Client}
     */
    public static Client createLoginClient(long sessionId, String remoteAddress, PacketProcessor packetProcessor,
                                           int world, int channel) {
        return new Client(Type.LOGIN, sessionId, remoteAddress, packetProcessor, world, channel);
    }

    /**
     * 创建挂在游戏频道服务器上的客户端实例。
     *
     * @param sessionId       会话 ID
     * @param remoteAddress   远端地址描述
     * @param packetProcessor 封包处理器
     * @param world           世界编号
     * @param channel         频道编号
     * @return 频道服 {@link Client}
     */
    public static Client createChannelClient(long sessionId, String remoteAddress, PacketProcessor packetProcessor,
                                             int world, int channel) {
        return new Client(Type.CHANNEL, sessionId, remoteAddress, packetProcessor, world, channel);
    }

    /**
     * 构造用于单元测试或占位场景的“空”客户端（无类型、无处理器）。
     * 不可用于真实网络连接。
     *
     * @return 测试用 Client
     */
    public static Client createMock() {
        return new Client(null, -1, null, null, -123, -123);
    }

    /**
     * Netty：通道就绪时记录真实远端 IP 并保存 {@link #ioChannel}。
     * 若服务器已关闭则直接关闭连接。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final io.netty.channel.Channel channel = ctx.channel();
        if (!Server.getInstance().isOnline()) {
            channel.close();
            return;
        }

        this.remoteAddress = getRemoteAddress(channel);
        this.ioChannel = channel;
    }

    /**
     * 从 Netty 通道解析对端 IP 字符串；失败时返回字面量 {@code "null"} 并打日志。
     *
     * @param channel Netty 通道
     * @return IPv4/IPv6 字符串或 {@code "null"}
     */
    private static String getRemoteAddress(io.netty.channel.Channel channel) {
        String remoteAddress = "null";
        try {
            remoteAddress = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        } catch (NullPointerException npe) {
            log.warn("无法获取客户端的远程地址", npe);
        }

        return remoteAddress;
    }

    /**
     * Netty：收到二进制数据时已由上游解码为 {@link InPacket}。
     * 读取 opcode，校验处理器状态后分发到对应 {@link PacketHandler}；
     * 异常时记录日志并 {@link #enableActions()} 解除客户端假死，最后更新 {@link #lastPacket}。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof InPacket packet)) {
            log.warn("收到无效封包: {}", msg);
            return;
        }

        short opcode = packet.readShort();
        final PacketHandler handler = packetProcessor.getHandler(opcode);

        if (GameConfig.getServerBoolean("use_debug_show_rcvd_packet") && !LoggingUtil.isIgnoredRecvPacket(opcode)) {
            log.info("收到封包 包头ID [{}] 内容： {}", String.format("0x%02X", opcode),packet);
        }

        if (handler != null && handler.validateState(this)) {
            try {
                ThreadLocalUtil.setCurrentClient(this);
                MonitoredChrLogger.logPacketIfMonitored(this, opcode, packet.getBytes());
                handler.handlePacket(packet, this);
            } catch (final Throwable t) {
                final String chrInfo = player != null ? player.getName() + " 地图 [" + player.getMap().getMapName() + "] (" + player.getMapId() + ")" : "?";
                log.warn("封包处理器 {} 出错. 账号 {}, 玩家 {}. 封包: {}", handler.getClass().getSimpleName(),
                        getAccountName(), chrInfo, packet, t);
                enableActions();//解除客户端假死
            } finally {
                ThreadLocalUtil.removeCurrentClient();
            }
        }

        updateLastPacket();
    }

    /**
     * Netty：用户事件（此处主要处理读空闲），委托 {@link #checkIfIdle(IdleStateEvent)}。
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) {
        if (event instanceof IdleStateEvent idleEvent) {
            checkIfIdle(idleEvent);
        }
    }

    /**
     * Netty：管道异常。非法包头会强制关会话；{@link IOException} 走正常关线；
     * 若角色不在线世界状态则尝试 {@link SystemRescue} 解救卡图。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (player != null && !player.isLoggedInWorld()) {  //判断玩家不为空且不在线才进行救援
            String MapName = player.getMap().getMapName().isEmpty() ? I18nUtil.getLogMessage("SystemRescue.info.map.message1") : player.getMap().getMapName();  //读取出错地图名称，这里是读取服务端String.wz地图名称，不存在则设为 未知地图
            log.warn(I18nUtil.getLogMessage("Client.warn.map.message1"), player, MapName , player.getMapId(), cause);
            sysRescue.setMapChange(player);   // 尝试解救那些卡地图的倒霉蛋。
        }

        if (cause instanceof InvalidPacketHeaderException) {
            SessionCoordinator.getInstance().closeSession(this, true);
        } else if (cause instanceof IOException) {
            closeMapleSession();
        }
    }

    /**
     * Netty：通道关闭（对端断开或本地关闭）时执行与 {@link #closeMapleSession()} 相同的关线流程。
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeMapleSession();
    }

    /**
     * 根据 {@link #type} 通知会话协调器关闭登录或频道会话，并在非过渡状态下触发 {@link #disconnect(boolean, boolean)}，
     * 最后关闭底层 Netty 通道。
     */
    private void closeMapleSession() {
        switch (type) {
            case LOGIN -> SessionCoordinator.getInstance().closeLoginSession(this);
            case CHANNEL -> SessionCoordinator.getInstance().closeSession(this, null);
        }

        try {
            // client freeze issues on session transition states found thanks to yolinlin, Omo Oppa, Nozphex
            if (!inTransition) {
                disconnect(false, false);
            }
        } catch (Throwable t) {
            log.warn("账号卡住", t);
        } finally {
            closeSession();
        }
    }

    /**
     * 将“最后收到封包时间”更新为当前时间，供空闲检测等使用。
     */
    public void updateLastPacket() {
        lastPacket = System.currentTimeMillis();
    }

    /**
     * @return 上次收到任意客户端封包的时间戳（毫秒）
     */
    public long getLastPacket() {
        return lastPacket;
    }

    /**
     * 主动关闭 Netty 通道（会触发 {@link #channelInactive}）。
     */
    public void closeSession() {
        ioChannel.close();
    }

    /**
     * 断开传输层连接（与 {@link #closeSession()} 语义不同，依 Netty 实现而定）。
     */
    public void disconnectSession() {
        ioChannel.disconnect();
    }

    /**
     * @return 当前客户端 HWID，可能为 null
     */
    public Hwid getHwid() {
        return hwid;
    }

    /**
     * 设置内存中的 HWID（不自动写库，写库请用 {@link #updateHwid}）。
     *
     * @param hwid 硬件 ID
     */
    public void setHwid(Hwid hwid) {
        this.hwid = hwid;
    }

    /**
     * @return 对端 IP 字符串
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * @return 是否处于选角/换频道等会话过渡状态
     */
    public boolean isInTransition() {
        return inTransition;
    }

    /**
     * 从当前频道的事件脚本管理器按名称取得 {@link EventManager}。
     *
     * @param event 事件名
     * @return 事件管理器
     */
    public EventManager getEventManager(String event) {
        return getChannelServer().getEventSM().getEventManager(event);
    }

    /**
     * @return 当前绑定的游戏角色，可能为 null
     */
    public Character getPlayer() {
        return player;
    }

    /**
     * 绑定本连接对应的 {@link Character}，并初始化 {@link #sysRescue} 用于卡图救援。
     *
     * @param player 角色实例，可为 null 表示解绑
     */
    public void setPlayer(Character player) {
        this.player = player;
        this.sysRescue = new SystemRescue();
    }

    /**
     * 构造脚本 API 用的玩家交互封装（每次新建实例）。
     *
     * @return {@link AbstractPlayerInteraction}
     */
    public AbstractPlayerInteraction getAbstractPlayerInteraction() {
        return new AbstractPlayerInteraction(this);
    }

    /**
     * 向客户端发送指定世界下的角色列表封包。
     *
     * @param server 世界/服务器编号
     */
    public void sendCharList(int server) {
        this.sendPacket(PacketCreator.getCharList(this, server, 0));
    }

    /**
     * 从数据库加载该账号在指定世界下的全部角色对象（完整数据）。
     *
     * @param serverId 世界 ID
     * @return 角色列表，加载异常时可能含部分空数据
     */
    public List<Character> loadCharacters(int serverId) {
        List<Character> chars = new ArrayList<>(15);
        try {
            for (CharNameAndId cni : loadCharactersInternal(serverId)) {
                chars.add(Character.loadCharFromDB(cni.id, this, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chars;
    }

    /**
     * 仅加载角色名列表（轻量查询）。
     *
     * @param worldId 世界 ID
     * @return 角色名列表
     */
    public List<String> loadCharacterNames(int worldId) {
        List<String> chars = new ArrayList<>(15);
        for (CharNameAndId cni : loadCharactersInternal(worldId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    /**
     * 查询当前账号在指定世界下的角色 id 与名称。
     *
     * @param worldId 世界 ID
     * @return 内部用的名称与 id 列表
     */
    private List<CharNameAndId> loadCharactersInternal(int worldId) {
        List<CharNameAndId> chars = new ArrayList<>(15);
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, name FROM characters WHERE accountid = ? AND world = ?")) {
            ps.setInt(1, this.getAccID());
            ps.setInt(2, worldId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chars;
    }

    /**
     * @return 内存中标记是否已处于登录成功后的会话（需与数据库状态一致理解）
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * 检查当前 {@link #remoteAddress} 是否匹配 IP 封禁表中的记录。
     *
     * @return 若被封禁返回 true
     */
    public boolean hasBannedIP() {
        boolean ret = false;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')")) {
            ps.setString(1, remoteAddress);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    ret = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 取得上次投票时间（Unix 秒）；首次从 {@code bit_votingrecords} 表加载并缓存。
     *
     * @return 投票时间戳，无记录或出错时可能为 -1
     */
    public int getVoteTime() {
        if (voteTime != -1) {
            return voteTime;
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT date FROM bit_votingrecords WHERE UPPER(account) = UPPER(?)")) {
            ps.setString(1, accountName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }
                voteTime = rs.getInt("date");
            }
        } catch (SQLException e) {
            log.error("获取投票时间时出错");
            return -1;
        }
        return voteTime;
    }

    /** 清除投票时间缓存，使下次 {@link #getVoteTime()} 重新读库。 */
    public void resetVoteTime() {
        voteTime = -1;
    }

    /**
     * 是否在 24 小时内已投过票（与 {@link #getVoteTime()} 差值比较）。
     *
     * @return 若仍处于冷却期返回 true
     */
    public boolean hasVotedAlready() {
        Date currentDate = new Date();
        int timeNow = (int) (currentDate.getTime() / 1000);
        int difference = (timeNow - getVoteTime());
        return difference < 86400 && difference > 0;
    }

    /**
     * 检查当前 HWID 是否在硬件封禁表中。
     *
     * @return 被封禁返回 true；HWID 为空返回 false
     */
    public boolean hasBannedHWID() {
        if (hwid == null) {
            return false;
        }

        boolean ret = false;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM hwidbans WHERE hwid LIKE ?")) {
            ps.setString(1, hwid.hwid());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs != null && rs.next()) {
                    if (rs.getInt(1) > 0) {
                        ret = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 检查本账号已记录的任一 MAC 是否在 MAC 封禁表中。
     *
     * @return 被封禁返回 true
     */
    public boolean hasBannedMac() {
        if (macs.isEmpty()) {
            return false;
        }
        boolean ret = false;
        int i;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
        for (i = 0; i < macs.size(); i++) {
            sql.append("?");
            if (i != macs.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            i = 0;
            for (String mac : macs) {
                ps.setString(++i, mac);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    ret = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 若内存中尚无 HWID，则从 {@code accounts} 表按 {@link #accId} 加载。
     *
     * @throws SQLException 数据库异常
     */
    private void loadHWIDIfNescessary() throws SQLException {
        if (hwid == null) {
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT hwid FROM accounts WHERE id = ?")) {
                ps.setInt(1, accId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hwid = new Hwid(rs.getString("hwid"));
                    }
                }
            }
        }
    }

    // TODO: Recode to close statements...
    /**
     * 若 MAC 集合为空，则从 {@code accounts.macs} 字段解析并填充（逗号分隔）。
     *
     * @throws SQLException 数据库异常
     */
    private void loadMacsIfNescessary() throws SQLException {
        if (macs.isEmpty()) {
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?")) {
                ps.setInt(1, accId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        for (String mac : rs.getString("macs").split(", ")) {
                            if (!mac.equals("")) {
                                macs.add(mac);
                            }
                        }
                    }
                }
            }
        }
    }

    /** 将当前 HWID 写入 {@code hwidbans} 表。 */
    public void banHWID() {
        try {
            loadHWIDIfNescessary();

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO hwidbans (hwid) VALUES (?)")) {
                ps.setString(1, hwid.hwid());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将本账号未命中 {@code macfilters} 过滤规则的 MAC 写入 {@code macbans}。
     */
    public void banMacs() {
        try {
            loadMacsIfNescessary();

            List<String> filtered = new LinkedList<>();
            try (Connection con = DatabaseConnection.getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        filtered.add(rs.getString("filter"));
                    }
                }

                try (PreparedStatement ps = con.prepareStatement("INSERT INTO macbans (mac, aid) VALUES (?, ?)")) {
                    for (String mac : macs) {
                        boolean matched = false;
                        for (String filter : filtered) {
                            if (mac.matches(filter)) {
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) {
                            ps.setString(1, mac);
                            ps.setString(2, String.valueOf(getAccID()));
                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成登录流程中的最后一步：在编码器锁内检查是否重复登录，更新数据库登录状态为 {@link #LOGIN_LOGGEDIN}。
     *
     * @return 0 表示成功；7 表示已处于更高登录状态被拒绝
     */
    public int finishLogin() {
        encoderLock.lock();
        try {
            if (getLoginState() > LOGIN_NOTLOGGEDIN) { // 0 = LOGIN_NOTLOGGEDIN, 1= LOGIN_SERVER_TRANSITION, 2 = LOGIN_LOGGEDIN
                loggedIn = false;
                return 7;
            }
            updateLoginState(Client.LOGIN_LOGGEDIN);
        } finally {
            encoderLock.unlock();
        }

        return 0;
    }

    /**
     * 设置 PIN 并持久化到数据库。
     *
     * @param pin 新 PIN
     */
    public void setPin(String pin) {
        this.pin = pin;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET pin = ? WHERE id = ?")) {
            ps.setString(1, pin);
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** @return 当前账号 PIN 字符串 */
    public String getPin() {
        return pin;
    }

    /**
     * 校验 PIN：未开启 PIN 或可绕过则直接通过；失败累计超过阈值踢线；
     * 成功则重置尝试次数并登记登录绕过。
     *
     * @param other 客户端提交的 PIN
     * @return 是否匹配
     */
    public boolean checkPin(String other) {
        if (!(GameConfig.getServerBoolean("enable_pin") && !canBypassPin())) {
            return true;
        }

        pinattempt++;
        if (pinattempt > 5) {
            SessionCoordinator.getInstance().closeSession(this, false);
        }
        if (pin.equals(other)) {
            pinattempt = 0;
            LoginBypassCoordinator.getInstance().registerLoginBypassEntry(hwid, accId, false);
            return true;
        }
        return false;
    }

    /**
     * 设置 PIC 并写入数据库。
     *
     * @param pic 新 PIC
     */
    public void setPic(String pic) {
        this.pic = pic;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET pic = ? WHERE id = ?")) {
            ps.setString(1, pic);
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** @return 当前 PIC */
    public String getPic() {
        return pic;
    }

    /**
     * 校验 PIC，逻辑同 {@link #checkPin(String)}（含绕过登记，第二个参数为 PIC）。
     *
     * @param other 客户端提交的 PIC
     * @return 是否匹配
     */
    public boolean checkPic(String other) {
        if (!(GameConfig.getServerBoolean("enable_pic") && !canBypassPic())) {
            return true;
        }

        picattempt++;
        if (picattempt > 5) {
            SessionCoordinator.getInstance().closeSession(this, false);
        }
        if (pic.equals(other)) {    // thanks ryantpayton (HeavenClient) for noticing null pics being checked here
            picattempt = 0;
            LoginBypassCoordinator.getInstance().registerLoginBypassEntry(hwid, accId, true);
            return true;
        }
        return false;
    }

    /**
     * 执行账号密码登录校验，并更新内存中的账号字段。
     * <p>
     * 返回值含义与客户端协议一致（0 成功、3 封禁、4 密码错误、6 登录尝试过多、7 已登录、
     * 10/13/16/17 等为 {@link SessionCoordinator} 多开/会话冲突码，23/-23/-10 与 TOS、bcrypt 迁移相关）。
     * </p>
     *
     * @param login 账号名
     * @param pwd   明文密码
     * @param hwid  客户端 HWID
     * @return 登录结果码
     */
    public int login(String login, String pwd, Hwid hwid) {
        int loginok = 5;

        loginattempt++;
        if (loginattempt > 4) {
            loggedIn = false;
            SessionCoordinator.getInstance().closeSession(this, false);
            return 6;   // thanks Survival_Project for finding out an issue with AUTOMATIC_REGISTER here
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, password, gender, banned, pin, pic, characterslots, tos, language FROM accounts WHERE name = ?")) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                accId = -2;
                if (rs.next()) {
                    accId = rs.getInt("id");
                    if (accId <= 0) {
                        log.warn("尝试使用accId登录 {}", accId);
                        return 15;
                    }

                    boolean banned = (rs.getByte("banned") == 1);
                    gmlevel = 0;
                    pin = rs.getString("pin");
                    pic = rs.getString("pic");
                    gender = rs.getByte("gender");
                    characterSlots = rs.getByte("characterslots");
                    lang = rs.getInt("language");
                    String passhash = rs.getString("password");
                    byte tos = rs.getByte("tos");

                    if (banned) {
                        return 3;
                    }

                    if (getLoginState() > LOGIN_NOTLOGGEDIN) { // already loggedin
                        loggedIn = false;
                        loginok = 7;
                    } else if (GameConfig.getServerBoolean("use_debug") && GameConfig.getServerBoolean("no_password")) {
                        return 0;
                    } else if (passhash.charAt(0) == '$' && passhash.charAt(1) == '2' && BCrypt.checkpw(pwd, passhash)) {
                        loginok = (tos == 0) ? 23 : 0;
                    } else if (pwd.equals(passhash) || checkHash(passhash, "SHA-1", pwd) || checkHash(passhash, "SHA-512", pwd)) {
                        // thanks GabrielSin for detecting some no-bcrypt inconsistencies here
                        loginok = (tos == 0) ? (!GameConfig.getServerBoolean("bcrypt_migration") ? 23 : -23) : (!GameConfig.getServerBoolean("bcrypt_migration") ? 0 : -10); // migrate to bcrypt
                    } else {
                        loggedIn = false;
                        loginok = 4;
                    }
                } else {
                    accId = -3;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (loginok == 0 || loginok == 4) {
            AntiMulticlientResult res = SessionCoordinator.getInstance().attemptLoginSession(this, hwid, accId, loginok == 4);  //loginok == 4，但是会导致限制多开参数 deterred_multi_client == true 时密码错误一次返回REMOTE_REACHED_LIMIT，需要重开客户端

            return switch (res) {
                case SUCCESS -> {
                    if (loginok == 0) {
                        loginattempt = 0;
                    }
                    yield loginok;
                }
                case REMOTE_LOGGEDIN -> 17;
                case REMOTE_REACHED_LIMIT -> 13;
                case REMOTE_PROCESSING -> 10;
                case MANY_ACCOUNT_ATTEMPTS -> 16;
                default -> 8;
            };
        } else {
            return loginok;
        }
    }

    /**
     * 从数据库读取账号临时封禁时间；若为默认“无封禁”时间则返回 null。
     *
     * @return 临时封禁的 {@link Calendar}，无封禁或读失败返回 null
     */
    public Calendar getTempBanCalendarFromDB() {
        final Calendar lTempban = Calendar.getInstance();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `tempban` FROM accounts WHERE id = ?")) {
            ps.setInt(1, getAccID());

            final Timestamp tempban;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                tempban = rs.getTimestamp("tempban");
                if (tempban.toLocalDateTime().equals(DefaultDates.getTempban())) {
                    return null;
                }
            }

            lTempban.setTimeInMillis(tempban.getTime());
            tempBanCalendar = lTempban;
            return lTempban;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;//why oh why!?!
    }

    /** @return 内存中缓存的临时封禁日历 */
    public Calendar getTempBanCalendar() {
        return tempBanCalendar;
    }

    /**
     * @return 是否存在有效的临时封禁截止时间
     */
    public boolean hasBeenBanned() {
        return tempBanCalendar != null;
    }

    /**
     * 将点分十进制 IPv4 转为 32 位长整型（用于 IP 数值比较等）。
     *
     * @param dottedQuad 如 {@code "192.168.0.1"}
     * @return 长整型 IP
     * @throws RuntimeException 格式非法
     */
    public static long dottedQuadToLong(String dottedQuad) throws RuntimeException {
        String[] quads = dottedQuad.split("\\.");
        if (quads.length != 4) {
            throw new RuntimeException("IP地址格式无效。");
        }
        long ipAddress = 0;
        for (int i = 0; i < 4; i++) {
            int quad = Integer.parseInt(quads[i]);
            ipAddress += (long) (quad % 256) * (long) Math.pow(256, 4 - i);
        }
        return ipAddress;
    }

    /**
     * 更新 HWID 到内存与 {@code accounts} 表。
     *
     * @param hwid 新 HWID
     */
    public void updateHwid(Hwid hwid) {
        this.hwid = hwid;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET hwid = ? WHERE id = ?")) {
            ps.setString(1, hwid.hwid());
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并客户端上报的 MAC 列表到内存集合并写回 {@code accounts.macs}。
     *
     * @param macData 逗号分隔的 MAC 串
     */
    public void updateMacs(String macData) {
        macs.addAll(Arrays.asList(macData.split(", ")));
        StringBuilder newMacData = new StringBuilder();
        Iterator<String> iter = macs.iterator();
        while (iter.hasNext()) {
            String cur = iter.next();
            newMacData.append(cur);
            if (iter.hasNext()) {
                newMacData.append(", ");
            }
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?")) {
            ps.setString(1, newMacData.toString());
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置内存中的账号 ID（谨慎使用，部分流程会置 0 表示未绑定）。
     *
     * @param id 账号主键
     */
    public void setAccID(int id) {
        this.accId = id;
    }

    /**
     * @return 当前账号数据库 ID
     */
    public int getAccID() {
        return accId;
    }

    /**
     * 更新数据库 {@code accounts.loggedin} 与 {@code lastlogin}，并同步内存 {@link #loggedIn}、{@link #serverTransition}。
     * 设为 {@link #LOGIN_LOGGEDIN} 时会通知会话协调器更新在线客户端。
     * 设为 {@link #LOGIN_NOTLOGGEDIN} 时会清空账号 ID 等状态。
     *
     * @param newState {@link #LOGIN_NOTLOGGEDIN} / {@link #LOGIN_SERVER_TRANSITION} / {@link #LOGIN_LOGGEDIN}
     */
    public void updateLoginState(int newState) {
        // rules out possibility of multiple account entries
        if (newState == LOGIN_LOGGEDIN) {
            SessionCoordinator.getInstance().updateOnlineClient(this);
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, lastlogin = ? WHERE id = ?")) {
            // using sql currenttime here could potentially break the login, thanks Arnah for pointing this out

            ps.setInt(1, newState);
            ps.setTimestamp(2, new java.sql.Timestamp(Server.getInstance().getCurrentTime()));
            ps.setInt(3, getAccID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (newState == LOGIN_NOTLOGGEDIN) {
            loggedIn = false;
            serverTransition = false;
            setAccID(0);
        } else {
            serverTransition = (newState == LOGIN_SERVER_TRANSITION);
            loggedIn = !serverTransition;
        }
    }

    /**
     * 从数据库读取当前账号登录状态，并处理“卡在过渡状态”的修复逻辑（超时则回落为未登录）。
     * 同时加载生日到 {@link #birthday}。
     *
     * @return {@link #LOGIN_NOTLOGGEDIN}、{@link #LOGIN_SERVER_TRANSITION} 或 {@link #LOGIN_LOGGEDIN}
     */
    public int getLoginState() {  // 0 = LOGIN_NOTLOGGEDIN, 1= LOGIN_SERVER_TRANSITION, 2 = LOGIN_LOGGEDIN
        try (Connection con = DatabaseConnection.getConnection()) {
            int state;
            try (PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, birthday FROM accounts WHERE id = ?")) {
                ps.setInt(1, getAccID());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("获取登录状态-客户端账号：" + getAccID());
                    }

                    birthday = Calendar.getInstance();
                    try {
                        birthday.setTime(rs.getDate("birthday"));
                    } catch (SQLException e) {
                    }

                    state = rs.getInt("loggedin");
                    if (state == LOGIN_SERVER_TRANSITION) {
                        Timestamp lastlogin = rs.getTimestamp("lastlogin");
                        // 兼容历史已经创建的账号，和自动注册但未登录的账号
                        if (lastlogin == null || lastlogin.getTime() + 30000 < Server.getInstance().getCurrentTime()) {
                            int accountId = accId;
                            state = LOGIN_NOTLOGGEDIN;
                            updateLoginState(Client.LOGIN_NOTLOGGEDIN);   // ACCID = 0, issue found thanks to Tochi & K u ssss o & Thora & Omo Oppa
                            this.setAccID(accountId);
                        }
                    }
                }
            }
            if (state == LOGIN_LOGGEDIN) {
                loggedIn = true;
            } else if (state == LOGIN_SERVER_TRANSITION) {
                try (PreparedStatement ps2 = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE id = ?")) {
                    ps2.setInt(1, getAccID());
                    ps2.executeUpdate();
                }
            } else {
                loggedIn = false;
            }
            return state;
        } catch (SQLException e) {
            loggedIn = false;
            e.printStackTrace();
            throw new RuntimeException("登录状态");
        }
    }

    /**
     * 校验用户输入的生日是否与账号一致（删号等敏感操作）。
     *
     * @param date 用户选择的日期
     * @return 年月日是否全部一致
     */
    public boolean checkBirthDate(Calendar date) {
        return date.get(Calendar.YEAR) == birthday.get(Calendar.YEAR) && date.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) && date.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 玩家下线时从队伍中移除：标记离线、必要时在队长离开时移交队长给同图最高等级成员。
     *
     * @param wserv 世界实例
     */
    private void removePartyPlayer(World wserv) {
        MapleMap map = player.getMap();
        final Party party = player.getParty();
        final int idz = player.getId();

        if (party != null) {
            final PartyCharacter chrp = new PartyCharacter(player);
            chrp.setOnline(false);
            wserv.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
            if (party.getLeader().getId() == idz && map != null) {
                PartyCharacter lchr = null;
                for (PartyCharacter pchr : party.getMembers()) {
                    if (pchr != null && pchr.getId() != idz && (lchr == null || lchr.getLevel() <= pchr.getLevel()) && map.getCharacterById(pchr.getId()) != null) {
                        lchr = pchr;
                    }
                }
                if (lchr != null) {
                    wserv.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, lchr);
                }
            }
        }
    }

    /**
     * 从地图与世界逻辑中移除玩家：Buff、邀请、组队、事件实例、竞技场等；
     * {@code serverTransition} 为 true 时跳过部分社交清理（换频道场景）。
     *
     * @param wserv            世界
     * @param serverTransition 是否为跨频道/跨服过渡
     */
    private void removePlayer(World wserv, boolean serverTransition) {
        try {
            player.setDisconnectedFromChannelWorld();
            player.notifyMapTransferToPartner(-1);
            player.removeIncomingInvites();
            player.cancelAllBuffs(true);

            player.closePlayerInteractions();
            player.closePartySearchInteractions();

            if (!serverTransition) {    // thanks MedicOP for detecting an issue with party leader change on changing channels
                removePartyPlayer(wserv);

                EventInstanceManager eim = player.getEventInstance();
                if (eim != null) {
                    eim.playerDisconnected(player);
                }

                if (player.getMonsterCarnival() != null) {
                    player.getMonsterCarnival().playerDisconnected(getPlayer().getId());
                }

                if (player.getAriantColiseum() != null) {
                    player.getAriantColiseum().playerDisconnected(getPlayer());
                }
            }

            if (player.getMap() != null) {
                int mapId = player.getMapId();
                player.getMap().removePlayer(player);
                if (MapId.isDojo(mapId)) {
                    this.getChannelServer().freeDojoSectionIfEmpty(mapId);
                }
                
                if (player.getMap().getHPDec() > 0) {
                    getWorldServer().removePlayerHpDecrease(player);
                }
            }

        } catch (final Throwable t) {
            log.error("账号卡住", t);
        }
    }

    /**
     * 异步提交断线任务到线程池（若 {@link #canDisconnect()} 通过）。
     * 正常玩家下线、关服等应传 {@code shutdown}；现金店相关传 {@code cashshop}。
     *
     * @param shutdown  是否关服触发的断开
     * @param cashshop  是否从现金店断开
     */
    public final void disconnect(final boolean shutdown, final boolean cashshop) {
        if (canDisconnect()) {
            ThreadManager.getInstance().newTask(() -> disconnectInternal(shutdown, cashshop));
        }
    }

    /** 强制立即同步断线（不走线程池排队语义与 {@link #disconnect} 不同），用于检测到的异常重复登录等。 */
    public final void forceDisconnect() {
        if (canDisconnect()) {
            disconnectInternal(true, false);
        }
    }

    /**
     * 超时类断开：内部直接调用 {@link #disconnectInternal(boolean, boolean)}，{@code cashshop=true}。
     */
    public void timeoutDisconnect() {
        disconnectInternal(false, true);
    }

    /**
     * 保证每个客户端实例断线逻辑只进入一次。
     *
     * @return 若当前可进入断线流程返回 true 并已置 {@link #disconnecting}
     */
    private synchronized boolean canDisconnect() {
        if (disconnecting) {
            return false;
        }

        disconnecting = true;
        return true;
    }

    /**
     * 断线核心实现：保存角色、清理队伍/公会/好友/信使、从世界移除、更新登录状态、释放脚本引擎引用等。
     * 每个 {@link Client} 实例在正常生命周期内应只完整执行一次主路径。
     *
     * @param shutdown  关服断开
     * @param cashshop  是否视为从现金店断开
     */
    private void disconnectInternal(boolean shutdown, boolean cashshop) {//once per Client instance
        if (player != null && player.isLoggedIn() && player.getClient() != null) {
            final int messengerid = player.getMessenger() == null ? 0 : player.getMessenger().getId();
            //final int fid = player.getFamilyId();
            final BuddyList bl = player.getBuddylist();
            final MessengerCharacter chrm = new MessengerCharacter(player, 0);
            final GuildCharacter chrg = player.getMGC();
            final Guild guild = player.getGuild();

            player.cancelMagicDoor();

            final World wserv = getWorldServer();   // obviously wserv is NOT null if this player was online on it
            try {
                // 保存在线时间
                player.updateOnlineTime();
                removePlayer(wserv, this.serverTransition);

                if (!(channel == -1 || shutdown)) {
                    if (!cashshop) {
                        if (!this.serverTransition) { // meaning not changing channels
                            if (messengerid > 0) {
                                wserv.leaveMessenger(messengerid, chrm);
                            }
                                                        /*      
                                                        if (fid > 0) {
                                                                final Family family = worlda.getFamily(fid);
                                                                family.
                                                        }
                                                        */

                            player.forfeitExpirableQuests();    //This is for those quests that you have to stay logged in for a certain amount of time

                            if (guild != null) {
                                final Server server = Server.getInstance();
                                server.setGuildMemberOnline(player, false, player.getClient().getChannel());
                                player.sendPacket(GuildPackets.showGuildInfo(player));
                            }
                            if (bl != null) {
                                wserv.loggedOff(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
                            }
                        }
                    } else {
                        if (!this.serverTransition) { // if dc inside of cash shop.
                            if (bl != null) {
                                wserv.loggedOff(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                log.error("账号卡住", e);
            } finally {
                if (!this.serverTransition) {
                    if (chrg != null) {
                        chrg.setCharacter(null);
                    }
                    wserv.removePlayer(player);
                    //getChannelServer().removePlayer(player); already being done

                    player.saveCooldowns();
                    player.cancelAllDebuffs();
                    player.saveCharToDB(true);

                    player.logOff();
                    if (GameConfig.getServerBoolean("instant_name_change")) {
                        player.doPendingNameChange();
                    }
                    clear();
                } else {
                    getChannelServer().removePlayer(player);

                    player.saveCooldowns();
                    player.cancelAllDebuffs();
                    player.saveCharToDB();
                }
            }
        }

        SessionCoordinator.getInstance().closeSession(this, false);

        if (!serverTransition && isLoggedIn()) {
            updateLoginState(Client.LOGIN_NOTLOGGEDIN);

            clear();
        } else {
            if (!Server.getInstance().hasCharacteridInTransition(this)) {
                updateLoginState(Client.LOGIN_NOTLOGGEDIN);
            }

            engines = null; // thanks Tochi for pointing out a NPE here
        }
    }

    /**
     * 断线后清空对本 Client 的强引用：角色、账号名、MAC、脚本引擎等，并注销登录状态跟踪。
     */
    private void clear() {
        // player hard reference removal thanks to Steve (kaito1410)
        if (this.player != null) {
            this.player.empty(true); // clears schedules and stuff
        }

        Server.getInstance().unregisterLoginState(this);

        this.accountName = null;
        this.macs = null;
        this.hwid = null;
        this.birthday = null;
        this.engines = null;
        this.player = null;
    }

    /**
     * 进入“带角色 ID 的会话过渡”状态：数据库登录状态置为过渡，并通知 {@link Server} 记录正在切换的角色。
     *
     * @param cid 角色 ID
     */
    public void setCharacterOnSessionTransitionState(int cid) {
        this.updateLoginState(Client.LOGIN_SERVER_TRANSITION);
        this.inTransition = true;
        Server.getInstance().setCharacteridInTransition(this, cid);
    }

    /**
     * @return 当前频道号
     */
    public int getChannel() {
        return channel;
    }

    /**
     * @return 当前世界下本连接所在频道的 {@link Channel} 服务器对象
     */
    public Channel getChannelServer() {
        return Server.getInstance().getChannel(world, channel);
    }

    /**
     * @return 当前所在 {@link World}
     */
    public World getWorldServer() {
        return Server.getInstance().getWorld(world);
    }

    /**
     * 按指定频道号取得频道实例（不一定等于本连接当前 {@link #channel}）。
     *
     * @param channel 频道编号
     * @return 频道服务器
     */
    public Channel getChannelServer(byte channel) {
        return Server.getInstance().getChannel(world, channel);
    }

    /**
     * 删除角色：若仍在队伍中则先离队再删库。
     *
     * @param cid          要删的角色 ID
     * @param senderAccId  发起删除的账号 ID（校验归属）
     * @return 是否删除成功
     */
    public boolean deleteCharacter(int cid, int senderAccId) {
        try {
            Character chr = Character.loadCharFromDB(cid, this, false);

            Integer partyid = chr.getWorldServer().getCharacterPartyid(cid);
            if (partyid != null) {
                this.setPlayer(chr);

                Party party = chr.getWorldServer().getParty(partyid);
                chr.setParty(party);
                chr.getMPC();
                chr.leaveParty();   // thanks Vcoc for pointing out deleted characters would still stay in a party

                this.setPlayer(null);
            }

            return Character.deleteCharFromDB(chr, senderAccId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** @return 当前账号名 */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @param a 账号名
     */
    public void setAccountName(String a) {
        this.accountName = a;
    }

    /**
     * @param channel 本连接逻辑频道号
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /** @return 世界编号 */
    public int getWorld() {
        return world;
    }

    /**
     * @param world 世界编号
     */
    public void setWorld(int world) {
        this.world = world;
    }

    /** 客户端响应 Ping 后调用，更新 {@link #lastPong}。 */
    public void pongReceived() {
        lastPong = System.currentTimeMillis();
    }

    /**
     * 空闲检测：向客户端发 Ping，若在约 15 秒内未收到 Pong 则 {@link #closeMapleSession()}。
     *
     * @param event Netty 空闲事件
     */
    public void checkIfIdle(final IdleStateEvent event) {
        final long pingedAt = System.currentTimeMillis();
        sendPacket(PacketCreator.getPing());
        TimerManager.getInstance().schedule(() -> {
            try {
                if (lastPong < pingedAt) {
                    if (ioChannel.isActive()) {
                        log.info("由于空闲而断开连接 {}。原因：{}", remoteAddress, event.state());
//                        updateLoginState(Client.LOGIN_NOTLOGGEDIN);
//                        disconnectSession();
                        // 按正常的规则去移除这个客户端，避免client被close了，但是对象还在内存中引发后续报错
                        closeMapleSession();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }, SECONDS.toMillis(15));
    }

    /**
     * @return 本账号已绑定 MAC 的不可变视图
     */
    public Set<String> getMacs() {
        return Collections.unmodifiableSet(macs);
    }

    /** @return GM 等级 */
    public int getGMLevel() {
        return gmlevel;
    }

    /**
     * @param level GM 等级
     */
    public void setGMLevel(int level) {
        gmlevel = level;
    }

    /**
     * 缓存脚本引擎实例（NPC/任务等复用）。
     *
     * @param name 脚本上下文名
     * @param e    脚本引擎
     */
    public void setScriptEngine(String name, ScriptEngine e) {
        engines.put(name, e);
    }

    /**
     * @param name 脚本名
     * @return 引擎或 null
     */
    public ScriptEngine getScriptEngine(String name) {
        return engines.get(name);
    }

    /**
     * 移除指定名称的脚本引擎缓存。
     *
     * @param name 脚本名
     */
    public void removeScriptEngine(String name) {
        engines.remove(name);
    }

    /**
     * @return 当前 NPC 对话脚本管理器对本 Client 的 CM
     */
    public NPCConversationManager getCM() {
        return NPCScriptManager.getInstance().getCM(this);
    }

    /**
     * @return 当前任务脚本管理器对本 Client 的 QM
     */
    public QuestActionManager getQM() {
        return QuestScriptManager.getInstance().getQM(this);
    }

    /**
     * 用户接受服务条款：将 {@code accounts.tos} 置 1。
     * 若库中已为 1 则返回 true 表示应断开（重复接受等策略由调用方解释）。
     *
     * @return 若读取到 tos 已为 1 返回 true（调用处用于 disconnect）
     */
    public boolean acceptToS() {
        if (accountName == null) {
            return true;
        }

        boolean disconnect = false;
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `tos` FROM accounts WHERE id = ?")) {
                ps.setInt(1, accId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getByte("tos") == 1) {
                            disconnect = true;
                        }
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tos = 1 WHERE id = ?")) {
                ps.setInt(1, accId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return disconnect;
    }

    /**
     * 配置项开启时：若发现世界内已有同账号其他角色在线则强制踢下线，防止双开漏洞。
     *
     * @param accid 账号 ID
     */
    public void checkChar(int accid) {  /// issue with multiple chars from same account login found by shavit, resinate
        if (!GameConfig.getServerBoolean("use_character_account_check")) {
            return;
        }

        for (World w : Server.getInstance().getWorlds()) {
            for (Character chr : w.getPlayerStorage().getAllCharacters()) {
                if (accid == chr.getAccountId()) {
                    log.warn("玩家 {} 已从世界 {} 中删除。可能存在重复尝试。", chr.getName(), GameConstants.WORLD_NAMES[w.getId()]);
                    chr.getClient().forceDisconnect();
                    w.getPlayerStorage().removePlayer(chr.getId());
                }
            }
        }
    }

    /**
     * 从数据库读取并缓存 {@link #votePoints}。
     *
     * @return 投票点数
     */
    public int getVotePoints() {
        int points = 0;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `votepoints` FROM accounts WHERE id = ?")) {
            ps.setInt(1, accId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    points = rs.getInt("votepoints");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        votePoints = points;
        return votePoints;
    }

    /**
     * 增加投票点并持久化。
     *
     * @param points 增量
     */
    public void addVotePoints(int points) {
        votePoints += points;
        saveVotePoints();
    }

    /**
     * 消费投票点并写库、记日志。
     *
     * @param points 消费量
     */
    public void useVotePoints(int points) {
        if (points > votePoints) {
            //Should not happen, should probably log this
            return;
        }
        votePoints -= points;
        saveVotePoints();
        MapleLeafLogger.log(player, false, Integer.toString(points));
    }

    /** 将 {@link #votePoints} 写回数据库。 */
    private void saveVotePoints() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET votepoints = ? WHERE id = ?")) {
            ps.setInt(1, votePoints);
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对玩家相关逻辑加主互斥锁（与 {@link #releaseClient} / {@link #unlockClient} 成对使用）。
     */
    public void lockClient() {
        lock.lock();
    }

    /** 释放玩家主锁。 */
    public void unlockClient() {
        lock.unlock();
    }

    /**
     * 尝试同时取得 {@link #actionsSemaphore} 许可与玩家锁；用于限制并发封包处理深度。
     *
     * @return 是否成功获取
     */
    public boolean tryacquireClient() {
        if (actionsSemaphore.tryAcquire()) {
            lockClient();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 与 {@link #tryacquireClient} 配对：先解锁再释放信号量。
     */
    public void releaseClient() {
        unlockClient();
        actionsSemaphore.release();
    }

    /**
     * 尝试取得编码器锁（用于需要与登录状态更新互斥的写路径）。
     *
     * @return 是否成功
     */
    public boolean tryacquireEncoder() {
        if (actionsSemaphore.tryAcquire()) {
            encoderLock.lock();
            return true;
        } else {
            return false;
        }
    }

    /** 释放编码器锁与信号量。 */
    public void unlockEncoder() {
        encoderLock.unlock();
        actionsSemaphore.release();
    }

    /**
     * 加载角色列表查询用的轻量结构体（仅角色名与 ID）。
     */
    private static class CharNameAndId {

        /** 角色名。 */
        public String name;
        /** 角色 ID。 */
        public int id;

        /**
         * @param name 角色名
         * @param id   角色 ID
         */
        public CharNameAndId(String name, int id) {
            super();
            this.name = name;
            this.id = id;
        }
    }

    /**
     * 使用指定摘要算法计算密码十六进制摘要并与库存哈希比较（兼容旧 SHA 存库）。
     *
     * @param hash     库存哈希（十六进制小写无空格）
     * @param type     {@link MessageDigest} 算法名，如 SHA-1、SHA-512
     * @param password 明文密码
     * @return 是否匹配
     */
    private static boolean checkHash(String hash, String type, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance(type);
            digester.update(password.getBytes(StandardCharsets.UTF_8), 0, password.length());
            return HexTool.toHexString(digester.digest()).replace(" ", "").toLowerCase().equals(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("对字符串进行编码失败", e);
        }
    }

    /**
     * 全账号剩余可创建角色栏位（所有世界合计上限 15 与配置槽位取差）。
     *
     * @return 剩余栏位
     */
    public short getAvailableCharacterSlots() {
        return (short) Math.max(0, characterSlots - Server.getInstance().getAccountCharacterCount(accId));
    }

    /**
     * 当前 {@link #world} 下剩余可创建角色数。
     */
    public short getAvailableCharacterWorldSlots() {
        return (short) Math.max(0, characterSlots - Server.getInstance().getAccountWorldCharacterCount(accId, world));
    }

    /**
     * 指定世界下剩余可创建角色数。
     *
     * @param world 世界 ID
     */
    public short getAvailableCharacterWorldSlots(int world) {
        return (short) Math.max(0, characterSlots - Server.getInstance().getAccountWorldCharacterCount(accId, world));
    }

    /** @return 账号允许的总栏位数 */
    public short getCharacterSlots() {
        return characterSlots;
    }

    /**
     * @param slots 栏位数
     */
    public void setCharacterSlots(byte slots) {
        characterSlots = slots;
    }

    /**
     * @return 是否仍可扩容栏位（上限 15）
     */
    public boolean canGainCharacterSlot() {
        return characterSlots < 15;
    }

    /**
     * 栏位 +1 并写库，失败时打印栈。
     *
     * @return 是否成功增加
     */
    public synchronized boolean gainCharacterSlot() {
        if (canGainCharacterSlot()) {
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE accounts SET characterslots = ? WHERE id = ?")) {
                ps.setInt(1, this.characterSlots += 1);
                ps.setInt(2, accId);
                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * 从数据库读取账号封禁原因码 {@code greason}。
     *
     * @return 原因字节，读失败返回 0
     */
    public final byte getGReason() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `greason` FROM `accounts` WHERE id = ?")) {
            ps.setInt(1, accId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getByte("greason");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** @return 账号性别 */
    public byte getGender() {
        return gender;
    }

    /**
     * 设置性别并更新数据库。
     *
     * @param m 性别
     */
    public void setGender(byte m) {
        this.gender = m;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET gender = ? WHERE id = ?")) {
            ps.setByte(1, gender);
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 若世界服务器允许，则向客户端发送空广播以关闭顶栏滚动公告（Boss 血条与公告栏冲突处理）。
     */
    private void announceDisableServerMessage() {
        if (!this.getWorldServer().registerDisabledServerMessage(player.getId())) {
            sendPacket(PacketCreator.serverMessage(""));
        }
    }

    /** 发送当前频道配置的顶栏服务器公告。 */
    public void announceServerMessage() {
        sendPacket(PacketCreator.serverMessage(this.getChannelServer().getServerMessage()));
    }

    /**
     * Boss 血条广播：限制频率并与顶栏公告互斥，避免客户端线程互相抢占显示。
     *
     * @param mm      怪物
     * @param mobHash 用于识别血条目标的哈希
     * @param packet  已构造好的血条封包
     */
    public synchronized void announceBossHpBar(Monster mm, final int mobHash, Packet packet) {
        long timeNow = System.currentTimeMillis();
        int targetHash = player.getTargetHpBarHash();

        if (mobHash != targetHash) {
            if (timeNow - player.getTargetHpBarTime() >= SECONDS.toMillis(5)) {
                // is there a way to INTERRUPT this annoying thread running on the client that drops the boss bar after some time at every attack?
                announceDisableServerMessage();
                sendPacket(packet);

                player.setTargetHpBarHash(mobHash);
                player.setTargetHpBarTime(timeNow);
            }
        } else {
            announceDisableServerMessage();
            sendPacket(packet);

            player.setTargetHpBarTime(timeNow);
        }
    }

    /**
     * 线程安全地向本客户端写出封包（通过 {@link #announcerLock} 串行化）。
     *
     * @param packet 出站封包
     */
    public void sendPacket(Packet packet) {
        announcerLock.lock();
        try {
            ioChannel.writeAndFlush(packet);
        } finally {
            announcerLock.unlock();
        }
    }

    /**
     * 发送屏幕提示并 {@link #enableActions()}。
     *
     * @param msg    文本
     * @param length 显示相关长度参数
     */
    public void announceHint(String msg, int length) {
        sendPacket(PacketCreator.sendHint(msg, length, 10));
        enableActions();
    }

    /**
     * 切换游戏频道：校验封禁、存活、地图限制与迷你地牢等，保存 Buff/角色数据，
     * 从当前地图移除玩家并发送频道切换 IP 端口封包。
     *
     * @param channel 目标频道号
     */
    public void changeChannel(int channel) {
        Server server = Server.getInstance();
        if (player.isBanned()) {
            disconnect(false, false);
            return;
        }
        if (!player.isAlive() || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
            enableActions();
            return;
        } else if (MiniDungeonInfo.isDungeonMap(player.getMapId())) {
            sendPacket(PacketCreator.serverNotice(5, "在迷你地牢内时，更改频道或进入现金商店或拍卖行将被禁用。"));
            enableActions();
            return;
        }

        String[] socket = Server.getInstance().getInetSocket(this, getWorld(), channel);
        if (socket == null) {
            sendPacket(PacketCreator.serverNotice(1, "频道 " + channel + " 当前已禁用。请尝试其他频道。"));
            enableActions();
            return;
        }

        player.closePlayerInteractions();
        player.closePartySearchInteractions();

        player.unregisterChairBuff();
        server.getPlayerBuffStorage().addBuffsToStorage(player.getId(), player.getAllBuffs());
        server.getPlayerBuffStorage().addDiseasesToStorage(player.getId(), player.getAllDiseases());
        player.setDisconnectedFromChannelWorld();
        player.notifyMapTransferToPartner(-1);
        player.removeIncomingInvites();
        player.cancelAllBuffs(true);
        player.cancelAllDebuffs();
        player.cancelBuffExpireTask();
        player.cancelDiseaseExpireTask();
        player.cancelSkillCooldownTask();
        player.cancelQuestExpirationTask();
        //Cancelling magicdoor? Nope
        //Cancelling mounts? Noty

        player.getInventory(InventoryType.EQUIPPED).checked(false); //test
        player.getMap().removePlayer(player);
        player.clearBanishPlayerData();
        player.getClient().getChannelServer().removePlayer(player);

        player.saveCharToDB();

        /*
         saveCharToDB后，数据库中的地图已经保存为ForcedReturnId，如果在当前地图下线，再上线，就会传送到ForcedReturnId对应的地图
         因为玩家登录时会优先取内存中的数据，没有才加载数据库，所以玩家切换频道取的是内存中的数据，而导致没有切换到ForcedReturnId对应的地图
         玩家反馈切换频道不传送ForcedReturnId对应的地图反而比较友好，所以该参数默认为false，想贴近官方可以设置为true
         */
        if (GameConfig.getServerBoolean("change_channel_force_return")) {
            int returnedMapId;
            MapleMap map = player.getMap();
            if (map.getForcedReturnId() != MapId.NONE) {
                returnedMapId = player.getMap().getForcedReturnId();
            } else {
                returnedMapId = player.getHp() < 1 ? map.getReturnMapId() : map.getId();
            }
            player.setMap(getChannelServer((byte) channel).getMapFactory().getMap(returnedMapId));
        }

        player.setSessionTransitionState();
        try {
            sendPacket(PacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** @return 会话协调器分配的会话 ID */
    public long getSessionId() {
        return this.sessionId;
    }

    /**
     * 是否允许再次请求角色列表（与上次 NPC/列表操作间隔有关，防刷）。
     */
    public boolean canRequestCharlist() {
        return lastNpcClick + 877 < Server.getInstance().getCurrentTime();
    }

    /**
     * 是否允许点击 NPC（冷却约 500ms，服务器时钟）。
     */
    public boolean canClickNPC() {
        return lastNpcClick + 500 < Server.getInstance().getCurrentTime();
    }

    /** 记录一次 NPC 相关操作时间用于节流。 */
    public void setClickedNPC() {
        lastNpcClick = Server.getInstance().getCurrentTime();
    }

    /** 清除 NPC 点击节流时间戳。 */
    public void removeClickedNPC() {
        lastNpcClick = 0;
    }

    /** @return 客户端可见世界数量 */
    public int getVisibleWorlds() {
        return visibleWorlds;
    }

    /**
     * 客户端请求服务器列表时记录可见世界数并刷新点击时间。
     *
     * @param worlds 世界数量
     */
    public void requestedServerlist(int worlds) {
        visibleWorlds = worlds;
        setClickedNPC();
    }

    /**
     * 关闭 NPC/任务脚本对话并清除 NPC 点击节流状态。
     */
    public void closePlayerScriptInteractions() {
        this.removeClickedNPC();
        NPCScriptManager.getInstance().dispose(this);
        QuestScriptManager.getInstance().dispose(this);
    }

    /**
     * 现金券相关操作次数限制：超过阈值返回 false 并重置计数。
     *
     * @return 是否仍允许尝试
     */
    public boolean attemptCsCoupon() {
        if (csattempt > 2) {
            resetCsCoupon();
            return false;
        }

        csattempt++;
        return true;
    }

    /** 重置现金券尝试计数。 */
    public void resetCsCoupon() {
        csattempt = 0;
    }

    /** 通知客户端可使用现金商店相关功能。 */
    public void enableCSActions() {
        sendPacket(PacketCreator.enableCSUse(player));
    }

    /**
     * 是否可根据 HWID/账号跳过 PIN（由 {@link LoginBypassCoordinator} 判定）。
     */
    public boolean canBypassPin() {
        return LoginBypassCoordinator.getInstance().canLoginBypass(hwid, accId, false);
    }

    /**
     * 是否可跳过 PIC。
     */
    public boolean canBypassPic() {
        return LoginBypassCoordinator.getInstance().canLoginBypass(hwid, accId, true);
    }

    /** @return 客户端/账号语言偏好 */
    public int getLanguage() {
        return lang;
    }

    /**
     * @param lingua 语言代码
     */
    public void setLanguage(int lingua) {
        this.lang = lingua;
    }

    /**
     * 向客户端发送“允许操作”封包，解除因脚本或流程未返回导致的输入假死。
     */
    public void enableActions() {
        sendPacket(PacketCreator.enableActions());
    }
}
