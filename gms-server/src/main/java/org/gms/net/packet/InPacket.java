package org.gms.net.packet;

import java.awt.*;

/**
 * 入站数据包接口
 * 定义从客户端接收数据包的读取操作，支持各种基本类型的反序列化
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