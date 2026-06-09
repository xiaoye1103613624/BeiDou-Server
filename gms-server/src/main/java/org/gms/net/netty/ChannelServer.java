package org.gms.net.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 频道服务器
 * 基于Netty实现，监听频道端口，处理游戏频道连接及数据包
 */
public class ChannelServer extends AbstractServer {
    private final int world;
    private final int channel;
    private Channel nettyChannel;

    public ChannelServer(int port, int world, int channel) {
        super(port);
        this.world = world;
        this.channel = channel;
    }

    @Override
    public void start() {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelServerInitializer(world, channel));

        this.nettyChannel = bootstrap.bind(port).syncUninterruptibly().channel();
    }

    @Override
    public void stop() {
        if (nettyChannel == null) {
            throw new IllegalStateException("Must start ChannelServer before stopping it");
        }

        nettyChannel.close().syncUninterruptibly();
    }
}