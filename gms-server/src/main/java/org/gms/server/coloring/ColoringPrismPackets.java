package org.gms.server.coloring;

import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;

import java.util.Collection;
import java.util.List;

/**
 * 七彩棱镜 S→C 封包（SendOpcode.COLORING_PRISM = 0x184）。
 */
public final class ColoringPrismPackets {
    public static final byte RESP_OPEN = 0;
    public static final byte RESP_DYE_LIST = 1;
    public static final byte RESP_DYE_MERGE = 2;

    private static final int MAX_ENTRIES = 256;

    private ColoringPrismPackets() {
    }

    /** 打开客户端染色窗口。 */
    public static Packet open() {
        OutPacket p = OutPacket.create(SendOpcode.COLORING_PRISM);
        p.writeByte(RESP_OPEN);
        return p;
    }

    /** 替换本人染色列表。 */
    public static Packet dyeList(Collection<ColoringPrismDye> dyes) {
        List<ColoringPrismDye> filtered = filterMeaningful(dyes);
        OutPacket p = OutPacket.create(SendOpcode.COLORING_PRISM);
        p.writeByte(RESP_DYE_LIST);
        writeEntries(p, filtered);
        return p;
    }

    /** 某角色染色合并给同图他人（或进图同步）。 */
    public static Packet dyeMerge(int characterId, Collection<ColoringPrismDye> dyes) {
        List<ColoringPrismDye> filtered = filterMeaningful(dyes);
        OutPacket p = OutPacket.create(SendOpcode.COLORING_PRISM);
        p.writeByte(RESP_DYE_MERGE);
        p.writeInt(characterId);
        writeEntries(p, filtered);
        return p;
    }

    private static List<ColoringPrismDye> filterMeaningful(Collection<ColoringPrismDye> dyes) {
        return dyes.stream()
                .filter(d -> d != null && !d.isNearZero())
                .limit(MAX_ENTRIES)
                .toList();
    }

    private static void writeEntries(OutPacket p, List<ColoringPrismDye> dyes) {
        p.writeShort(dyes.size());
        for (ColoringPrismDye d : dyes) {
            p.writeInt(d.itemId());
            p.writeInt(Float.floatToIntBits(d.hue()));
            p.writeInt(Float.floatToIntBits(d.sat()));
            p.writeInt(Float.floatToIntBits(d.light()));
        }
    }
}
