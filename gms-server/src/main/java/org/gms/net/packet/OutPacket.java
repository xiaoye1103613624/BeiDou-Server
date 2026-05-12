package org.gms.net.packet;

import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.SendOpcode;

import java.awt.*;

/**
 * 网络协议层类型「OutPacket」。
 * 属于 org.gms.net.packet 下的通用封包、读写或工具定义。
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
