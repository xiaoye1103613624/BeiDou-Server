package org.gms.net.netty;

/**
 * 抽象服务器基类
 * 定义Netty网络服务器的启动和停止接口
 */
public abstract class AbstractServer {
    final int port;

    AbstractServer(int port) {
        this.port = port;
    }

    public abstract void start();
    public abstract void stop();
}