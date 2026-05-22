package org.gms.net.packet;

import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.SendOpcode;

import java.awt.*;

/**
 * 【接口】OutPacket，包 `org.gms.net.packet`。
 *
 * 出站封包接口，定义向客户端数据包中写入各种类型数据（字节、整型、字符串、坐标等）的方法规范。
 *
 * @author Ronan
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
