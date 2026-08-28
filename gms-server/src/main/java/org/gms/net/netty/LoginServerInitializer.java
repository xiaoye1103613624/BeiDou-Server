package org.gms.net.netty;

import org.gms.client.Client;
import io.netty.channel.socket.SocketChannel;
import org.gms.net.PacketProcessor;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.util.I18nUtil;
import org.gms.util.RateLimitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerInitializer extends ServerChannelInitializer {
    private static final Logger log = LoggerFactory.getLogger(LoginServerInitializer.class);
    /**
     * Login clients may sit on the login/char-select UI while WZ/UI modules finish loading.
     * 30s channel idle + 15s ping grace is too aggressive and shows as "长时间断开连接".
     */
    private static final int LOGIN_IDLE_TIME_SECONDS = 180;

    @Override
    protected int getIdleTimeSeconds() {
        return LOGIN_IDLE_TIME_SECONDS;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) {
        final String clientIp = socketChannel.remoteAddress().getHostString();
        log.info(I18nUtil.getLogMessage("LoginServerInitializer.initChannel.info1"), clientIp);

        PacketProcessor packetProcessor = PacketProcessor.getLoginServerProcessor();
        final long clientSessionId = sessionId.getAndIncrement();
        final String remoteAddress = getRemoteAddress(socketChannel);
        if (!RateLimitUtil.getInstance().check(remoteAddress)) {
            log.warn(I18nUtil.getLogMessage("LoginServerInitializer.initChannel.warn1"), remoteAddress);
            socketChannel.close();
            return;
        }
        final Client client = Client.createLoginClient(clientSessionId, remoteAddress, packetProcessor, LoginServer.WORLD_ID, LoginServer.CHANNEL_ID);

        if (!SessionCoordinator.getInstance().canStartLoginSession(client)) {
            socketChannel.close();
            return;
        }

        initPipeline(socketChannel, client);
    }
}
