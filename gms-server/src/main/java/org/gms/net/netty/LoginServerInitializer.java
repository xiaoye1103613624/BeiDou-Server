package org.gms.net.netty;

import org.gms.client.Client;
import io.netty.channel.socket.SocketChannel;
import org.gms.net.PacketProcessor;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.util.I18nUtil;
import org.gms.util.RateLimitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty 网络组件「LoginServerInitializer」。
 * 参与 Channel 管道上的编解码、握手、空闲检测或与 ChannelServer 绑定的 IO 逻辑。
 */
public class LoginServerInitializer extends ServerChannelInitializer {
    private static final Logger log = LoggerFactory.getLogger(LoginServerInitializer.class);

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
        }
        final Client client = Client.createLoginClient(clientSessionId, remoteAddress, packetProcessor, LoginServer.WORLD_ID, LoginServer.CHANNEL_ID);

        if (!SessionCoordinator.getInstance().canStartLoginSession(client)) {
            socketChannel.close();
            return;
        }

        initPipeline(socketChannel, client);
    }
}
