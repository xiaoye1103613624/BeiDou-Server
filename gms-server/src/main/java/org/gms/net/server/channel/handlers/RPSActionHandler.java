package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.constants.id.NpcId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.minigame.RockPaperScissor;
import org.gms.util.PacketCreator;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#RPS_ACTION} 封包。
 * 负责处理客户端石头剪刀布小游戏的操作。
 */
public final class RPSActionHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        RockPaperScissor rps = chr.getRps();

        if (c.tryacquireClient()) {
            try {
                if (p.available() == 0 || !chr.getMap().containsNPC(NpcId.RPS_ADMIN)) {
                    if (rps != null) {
                        rps.dispose(c);
                    }
                    return;
                }
                final byte mode = p.readByte();
                // 根据猜拳游戏操作类型执行相应操作
                switch (mode) {
                    // 0/5: 开始/重试游戏
                    case 0:
                    case 5:
                        if (rps != null) {
                            rps.reward(c);
                        }
                        if (chr.getMeso() >= 1000) {
                            chr.setRPS(new RockPaperScissor(c, mode));
                        } else {
                            c.sendPacket(PacketCreator.rpsMesoError(-1));
                        }
                        break;
                    // 1: 回答（出拳）
                    case 1:
                        if (rps == null || !rps.answer(c, p.readByte())) {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));// 13
                        }
                        break;
                     // 2: 超时（自动判负）
                    case 2:
                        if (rps == null || !rps.timeOut(c)) {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
                        }
                        break;
                    // 3: 继续下一轮
                    case 3:
                        if (rps == null || !rps.nextRound(c)) {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
                        }
                        break;
                    // 4: 离开游戏
                    case 4:
                        if (rps != null) {
                            rps.dispose(c);
                        } else {
                            c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
                        }
                        break;
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}