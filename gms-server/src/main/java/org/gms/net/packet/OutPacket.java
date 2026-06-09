package org.gms.net.packet;

import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.SendOpcode;

import java.awt.*;

/**
 * 出站数据包接口
 * 定义向客户端发送数据包的写入操作，支持各种基本类型的序列化
 */
public interface OutPacket extends Packet {
    void writeByte(byte value);
    void writeByte(int value);
    void writeBytes(byte[] value);
    void writeShort(int value);
    void writeInt(int value);
    void writeLong(long value);
    void writeBool(boolean value);
    void writeString(String value);
    void writeFixedString(String value);
    void writeFixedString(String value, int fixed);
    void writePos(Point value);
    void skip(int numberOfBytes);

    static OutPacket create(Opcode opcode) {
        return new ByteBufOutPacket(opcode);
    }
}