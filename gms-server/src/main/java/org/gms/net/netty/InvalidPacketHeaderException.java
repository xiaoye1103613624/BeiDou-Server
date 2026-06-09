package org.gms.net.netty;

/**
 * 无效包头部异常
 * 当接收到无效数据包头时抛出
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