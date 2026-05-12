package org.gms.net.encryption.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.gms.client.Client;
import org.gms.net.encryption.InitializationVector;
import org.gms.net.packet.Packet;

import java.util.List;

/**
 * 协议加解密与编解码「PacketProtocol」。
 * 在客户端与服务器之间对帧或载荷进行变换，与 PacketCodec / Protocol 配置一致。
 */
public interface PacketProtocol {
    void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out);
    void encode(ChannelHandlerContext ctx, Packet in, ByteBuf out);
    void writeInitialUnencryptedHelloPacket(SocketChannel socketChannel, InitializationVector sendIv, InitializationVector recvIv, Client client);
}
