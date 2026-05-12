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
 * 登录服务器入站封包处理器「CharSelectedWithPicHandler」。
 * 处理账号登录、选角、创建角色、PIN/PIC、服务器列表等与尚未进入频道相关的协议。
 * 在验证通过后可能更新 {@link org.gms.client.Client} 的账号状态并切换会话阶段。
 */
public class CharSelectedWithPicHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(CharSelectedWithPicHandler.class);

    private static int parseAntiMulticlientError(AntiMulticlientResult res) {
        return switch (res) {
            case REMOTE_PROCESSING -> 10;
            case REMOTE_LOGGEDIN -> 7;
            case REMOTE_NO_MATCH -> 17;
            case COORDINATOR_ERROR -> 8;
            default -> 9;
        };
    }

    @Override
    public void handlePacket(InPacket p, Client c) {
        String pic = p.readString();
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

        if (c.hasBannedMac() || c.hasBannedHWID()) {
            SessionCoordinator.getInstance().closeSession(c, true);
            return;
        }

        Server server = Server.getInstance();
        if (!server.haveCharacterEntry(c.getAccID(), charId)) {
            SessionCoordinator.getInstance().closeSession(c, true);
            return;
        }

        if (c.checkPic(pic)) {
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

            AntiMulticlientResult res = SessionCoordinator.getInstance().attemptGameSession(c, c.getAccID(), hwid);
            if (res != AntiMulticlientResult.SUCCESS) {
                c.sendPacket(PacketCreator.getAfterLoginError(parseAntiMulticlientError(res)));
                return;
            }

            server.unregisterLoginState(c);
            c.setCharacterOnSessionTransitionState(charId);

            try {
                c.sendPacket(PacketCreator.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } catch (UnknownHostException | NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            c.sendPacket(PacketCreator.wrongPic());
        }
    }
}
