package org.gms.net.packet;

import java.awt.*;

/**
 * 网络协议层类型「InPacket」。
 * 属于 org.gms.net.packet 下的通用封包、读写或工具定义。
 */
public interface InPacket extends Packet {
    byte readByte();
    short readUnsignedByte();
    short readShort();
    int readInt();
    long readLong();
    Point readPos();
    String readString();
    byte[] readBytes(int numberOfBytes);
    void skip(int numberOfBytes);
    int available();
    void seek(int byteOffset);
    int getPosition();
}
