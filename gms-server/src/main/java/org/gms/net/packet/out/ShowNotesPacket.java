package org.gms.net.packet.out;

import org.gms.dao.entity.NotesDO;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.ByteBufOutPacket;
import org.gms.util.PacketCreator;

import java.util.List;
import java.util.Objects;

/**
 * 出站协议构造「ShowNotesPacket」。
 * 将服务端状态序列化为发往客户端的二进制 {@link org.gms.net.packet.Packet}（或子类）。
 */
public final class ShowNotesPacket extends ByteBufOutPacket {

    public ShowNotesPacket(List<NotesDO> notes) {
        super(SendOpcode.MEMO_RESULT);
        Objects.requireNonNull(notes);

        writeByte(3);
        writeByte(notes.size());
        notes.forEach(this::writeNote);
    }

    private void writeNote(NotesDO note) {
        writeInt(note.getId());
        writeString(note.getFrom() + " "); //Stupid nexon forgot space lol
        writeString(note.getMessage());
        writeLong(PacketCreator.getTime(note.getTimestamp()));
        writeByte(note.getFame());
    }
}
