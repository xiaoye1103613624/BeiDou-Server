package org.gms.net.server.handlers.login;

import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.net.server.coordinator.session.Hwid;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.net.server.coordinator.session.SessionCoordinator.AntiMulticlientResult;
import org.gms.net.server.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.PacketCreator;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 注册PIC处理器
 * 处理第一次登录时注册PIC验证码，验证成功后进入游戏世界
 */
public final class RegisterPicHandler extends AbstractPacketHandler {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(RegisterPicHandler.class);

    /**
     * 将防多客户端检测结果映射为客户端错误码
     *
     * @param res 防多客户端检测结果
     * @return 客户端错误码
     */
    private static int parseAntiMulticlientError(AntiMulticlientResult res) {
        return switch (res) {
            case REMOTE_PROCESSING -> 10;
            case REMOTE_LOGGEDIN -> 7;
            case REMOTE_NO_MATCH -> 17;
            case COORDINATOR_ERROR -> 8;
            default -> 9;
        };
    }

    /**
     * 处理注册PIC请求
     * 验证硬件ID后注册PIC，通过后重定向到游戏世界服务器
     *
     * @param p 输入数据包
     * @param c 客户端会话
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        p.readByte();
        int charId = p.readInt();

        String macs = p.readString();
        String hostString = p.readString();

        final Hwid hwid;
        try {
            hwid = Hwid.fromHostString(hostString);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid host string: {}", hostString, e);
            c.sendPacket(PacketCreator.getAfterLoginError(17));
            return;
        }

        c.updateMacs(macs);
        c.updateHwid(hwid);

        AntiMulticlientResult res = SessionCoordinator.getInstance().attemptGameSession(c, c.getAccID(), hwid);
        if (res != AntiMulticlientResult.SUCCESS) {
            c.sendPacket(PacketCreator.getAfterLoginError(parseAntiMulticlientError(res)));
            return;
        }

        if (c.hasBannedMac() || c.hasBannedHWID()) {
            SessionCoordinator.getInstance().closeSession(c, true);
            return;
        }

        Server server = Server.getInstance();
        if (!server.haveCharacterEntry(c.getAccID(), charId)) {
            SessionCoordinator.getInstance().closeSession(c, true);
            return;
        }

        String pic = p.readString();
        if (c.getPic() == null || c.getPic().equals("")) {
            c.setPic(pic);

            c.setWorld(server.getCharacterWorld(charId));
            World wserv = c.getWorldServer();
            if (wserv == null || wserv.isWorldCapacityFull()) {
                c.sendPacket(PacketCreator.getAfterLoginError(10));
                return;
            }

            String[] socket = server.getInetSocket(c, c.getWorld(), c.getChannel());
            if (socket == null) {
                c.sendPacket(PacketCreator.getAfterLoginError(10));
                return;
            }

            server.unregisterLoginState(c);
            c.setCharacterOnSessionTransitionState(charId);

            try {
                c.sendPacket(PacketCreator.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            SessionCoordinator.getInstance().closeSession(c, true);
        }
    }
}