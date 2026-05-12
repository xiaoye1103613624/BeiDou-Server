package org.gms.net.packet.logging;

import io.netty.buffer.Unpooled;
import org.gms.net.opcodes.RecvOpcode;

import java.util.Set;

/**
 * 封包日志与可观测性「LoggingUtil」。
 * 用于记录、过滤或诊断收发包内容，便于 GM 与开发排查协议问题。
 */
public class LoggingUtil {
    private static final Set<Short> ignoredDebugRecvPackets = Set.of(
            (short) RecvOpcode.MOVE_PLAYER.getValue(), // 41
            (short) RecvOpcode.HEAL_OVER_TIME.getValue(), // 89
            (short) RecvOpcode.SPECIAL_MOVE.getValue(), // 91
            (short) RecvOpcode.QUEST_ACTION.getValue(), // 107
            (short) RecvOpcode.MOVE_PET.getValue(), // 167
            (short) RecvOpcode.MOVE_LIFE.getValue(), // 188
            (short) RecvOpcode.NPC_ACTION.getValue() // 197
    );

    public static short readFirstShort(byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes).readShortLE();
    }

    public static boolean isIgnoredRecvPacket(short opcode) {
        return ignoredDebugRecvPackets.contains(opcode);
    }
}
