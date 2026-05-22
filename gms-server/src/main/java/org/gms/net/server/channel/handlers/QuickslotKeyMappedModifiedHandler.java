package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.keybind.QuickslotBinding;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#CHANGE_QUICKSLOT} 封包。
 * 负责处理客户端快捷栏按键修改操作。
 */
public class QuickslotKeyMappedModifiedHandler extends AbstractPacketHandler {
    @Override
    public void handlePacket(InPacket p, Client c) {
        // Invalid size for the packet.
        if (p.available() != QuickslotBinding.QUICKSLOT_SIZE * Integer.BYTES ||
                // not logged in-game
                c.getPlayer() == null) {
            return;
        }

        byte[] aQuickslotKeyMapped = new byte[QuickslotBinding.QUICKSLOT_SIZE];

        for (int i = 0; i < QuickslotBinding.QUICKSLOT_SIZE; i++) {
            aQuickslotKeyMapped[i] = (byte) p.readInt();
        }

        c.getPlayer().changeQuickslotKeybinding(aQuickslotKeyMapped);
    }
}
