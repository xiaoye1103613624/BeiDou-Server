package org.gms.net.netty;

/**
 * Netty 网络组件「InvalidPacketHeaderException」。
 * 参与 Channel 管道上的编解码、握手、空闲检测或与 ChannelServer 绑定的 IO 逻辑。
 */
public class InvalidPacketHeaderException extends RuntimeException {
    private final int header;

    public InvalidPacketHeaderException(String message, int header) {
        super(message);
        this.header = header;
    }

    public int getHeader() {
        return header;
    }
}
