package org.gms.net.encryption;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.gms.net.encryption.protocol.ProtocolFactory;

/**
 * 协议加解密与编解码「PacketCodec」。
 * 在客户端与服务器之间对帧或载荷进行变换，与 PacketCodec / Protocol 配置一致。
 */
public class PacketCodec extends CombinedChannelDuplexHandler<PacketDecoder, PacketEncoder> {
    public PacketCodec(ProtocolFactory protocolFactory) {
        super(new PacketDecoder(protocolFactory), new PacketEncoder(protocolFactory));
    }
}
