package org.gms.net.encryption.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.gms.client.Client;
import org.gms.net.encryption.InitializationVector;
import org.gms.net.packet.Packet;

import java.util.List;

/**
 * 数据包协议接口
 * 定义网络数据包的编解码协议，支持加密/解密和初始握手包
 */
public interface PacketProtocol {
    void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out);
    void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out);
    void writeInitialUnencryptedHelloPacket(SocketChannel socketChannel, InitializationVector sendIv, InitializationVector recvIv, Client client);
}