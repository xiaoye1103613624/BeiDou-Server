package org.gms.net.packet;

import java.awt.*;

/**
 * 【接口】InPacket，包 `org.gms.net.packet`。
 *
 * 入站封包接口，定义从客户端数据包中读取各种类型数据（字节、整型、字符串、坐标等）的方法规范。
 *
 * @author Ronan
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
