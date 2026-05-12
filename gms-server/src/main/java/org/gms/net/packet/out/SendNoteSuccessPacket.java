package org.gms.net.packet.out;

import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.ByteBufOutPacket;

/**
 * 出站协议构造「SendNoteSuccessPacket」。
 * 将服务端状态序列化为发往客户端的二进制 {@link org.gms.net.packet.Packet}（或子类）。
 */
public final class SendNoteSuccessPacket extends ByteBufOutPacket {

    public SendNoteSuccessPacket() {
        super(SendOpcode.MEMO_RESULT);

        writeByte(4);
    }
}
