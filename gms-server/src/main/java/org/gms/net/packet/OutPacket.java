package org.gms.net.packet;

import org.gms.net.opcodes.SendOpcode;

import java.awt.*;

/**
 * 输出数据
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

    void writePos(Point value);

    void skip(int numberOfBytes);

    /**
     * 创建一个新的 OutPacket 对象
     *
     * @param opcode 发送的操作码
     * @return 返回一个新的 OutPacket 对象
     */
    static OutPacket create(SendOpcode opcode) {
        return new ByteBufOutPacket(opcode);
    }
}
