package org.gms.net.encryption;

/**
 * 协议加解密与编解码「InitializationVector」。
 * 在客户端与服务器之间对帧或载荷进行变换，与 PacketCodec / Protocol 配置一致。
 */
public class InitializationVector {
    private final byte[] bytes;

    private InitializationVector(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static InitializationVector generateSend() {
        byte[] ivSend = {82, 48, 120, getRandomByte()};
        return new InitializationVector(ivSend);
    }

    public static InitializationVector generateReceive() {
        byte[] ivRecv = {70, 114, 122, getRandomByte()};
        return new InitializationVector(ivRecv);
    }

    private static byte getRandomByte() {
        return (byte) (Math.random() * 255);
    }
}
