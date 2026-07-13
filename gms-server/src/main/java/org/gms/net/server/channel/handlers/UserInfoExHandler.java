package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.maps.MapObject;
import org.gms.util.PacketCreator;

/**
 * 查看他人装备详情 (Recv 0x3726 / Send 0x3727)。
 * 客户端在 CUIUserInfoDetail 悬停装备时请求完整属性。
 */
public final class UserInfoExHandler extends AbstractPacketHandler {

    private static final int REQ_EQUIP_DETAIL = 1;

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character viewer = c.getPlayer();
        if (viewer == null) {
            return;
        }

        int type = p.readByte();
        if (type != REQ_EQUIP_DETAIL) {
            return;
        }

        int targetCid = p.readInt();
        int itemId = p.readInt();
        if (targetCid <= 0 || itemId <= 0) {
            return;
        }

        MapObject targetObj = viewer.getMap().getMapObject(targetCid);
        if (!(targetObj instanceof Character target) || target.getId() == viewer.getId()) {
            return;
        }

        Equip equip = findDisplayedEquip(target, itemId);
        if (equip == null) {
            return;
        }

        c.sendPacket(PacketCreator.userInfoExEquip(targetCid, equip));
    }

    private static Equip findDisplayedEquip(Character chr, int displayItemId) {
        Inventory equipInv = chr.getInventory(InventoryType.EQUIPPED);
        if (equipInv == null) {
            return null;
        }
        for (Item item : equipInv.list()) {
            if (!(item instanceof Equip equip)) {
                continue;
            }
            int visualId = equip.getItemId();
            if (equip.getAnvilItemId() != 0) {
                visualId = equip.getAnvilItemId();
            }
            if (visualId == displayItemId || equip.getItemId() == displayItemId) {
                return equip;
            }
        }
        return null;
    }
}
