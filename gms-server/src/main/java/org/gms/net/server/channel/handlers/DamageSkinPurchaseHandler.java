package org.gms.net.server.channel.handlers;

import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.DamageSkinCatalog;
import org.gms.client.DamageSkinInventory;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

@Slf4j
public final class DamageSkinPurchaseHandler extends AbstractPacketHandler {
    private static final int OP_PURCHASE = 2;

    private static int mesoPacket(long meso) {
        return meso > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) meso;
    }

    @Override
    public void handlePacket(InPacket p, Client c) {
        int skinId = p.readInt();
        Character chr = c.getPlayer();
        if (chr == null) return;
        final long curMesos = chr.getMeso();
        if (skinId == DamageSkinInventory.DEFAULT_SKIN_ID) {
            c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, false, skinId, mesoPacket(curMesos)));
            return;
        }
        long priceL = DamageSkinCatalog.getPrice(skinId);
        if (priceL < 0) {
            log.info("chr {} tried to buy unknown damage skin {}", chr.getId(), skinId);
            c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, false, skinId, mesoPacket(curMesos)));
            return;
        }
        DamageSkinInventory inv = chr.getDamageSkinInventory();
        if (inv.ownsSkin(skinId)) {
            c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, false, skinId, mesoPacket(curMesos)));
            return;
        }
        int priceI = priceL > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) priceL;
        if (curMesos < priceI) {
            c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, false, skinId, mesoPacket(curMesos)));
            return;
        }
        try {
            if (!inv.addSkin(chr.getId(), skinId)) {
                c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, false, skinId, mesoPacket(curMesos)));
                return;
            }
        } catch (Exception e) {
            log.error("damage skin insert failed for chr {} skin {}", chr.getId(), skinId, e);
            c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, false, skinId, mesoPacket(curMesos)));
            return;
        }
        chr.gainMeso(-priceI, true);
        c.sendPacket(PacketCreator.damageSkinResult(OP_PURCHASE, true, skinId, mesoPacket(chr.getMeso())));
        c.sendPacket(PacketCreator.damageSkinInventory(chr));
        log.info("chr {} purchased damage skin {}", chr.getId(), skinId);
    }
}
