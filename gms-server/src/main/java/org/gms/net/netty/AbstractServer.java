package org.gms.net.netty;

/**
 * Netty 网络组件「AbstractServer」。
 * 参与 Channel 管道上的编解码、握手、空闲检测或与 ChannelServer 绑定的 IO 逻辑。
 */
public abstract class AbstractServer {
    final int port;

    AbstractServer(int port) {
        this.port = port;
    }

    public abstract void start();
    public abstract void stop();
}
