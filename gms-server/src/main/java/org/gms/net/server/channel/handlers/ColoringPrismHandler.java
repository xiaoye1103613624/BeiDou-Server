package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.ItemInformationProvider;
import org.gms.server.coloring.ColoringPrismDye;
import org.gms.server.coloring.ColoringPrismPackets;
import org.gms.server.coloring.ColoringPrismStorage;
import org.gms.server.maps.MapleMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 七彩棱镜 C→S：REQUEST / CONFIRM / CLEAR。
 */
public final class ColoringPrismHandler extends AbstractPacketHandler {
    public static final byte ACTION_REQUEST = 0;
    public static final byte ACTION_CONFIRM = 1;
    public static final byte ACTION_CLEAR = 2;

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        byte action = p.readByte();
        switch (action) {
            case ACTION_REQUEST -> {
                // 侧栏 / 请求列表：打开窗口并下发本人染色
                c.sendPacket(ColoringPrismPackets.open());
                c.sendPacket(ColoringPrismPackets.dyeList(ColoringPrismStorage.loadByCharacter(chr.getId())));
            }
            case ACTION_CONFIRM -> handleConfirm(p, c, chr);
            case ACTION_CLEAR -> handleClear(p, c, chr);
            default -> {
            }
        }
    }

    private void handleConfirm(InPacket p, Client c, Character chr) {
        // 有符号 byte：已穿戴 slot 发 invType=0xFF → -1 = EQUIPPED
        byte invType = p.readByte();
        short slot = p.readShort();
        int itemId = p.readInt();
        float hue = Float.intBitsToFloat(p.readInt());
        float sat = Float.intBitsToFloat(p.readInt());
        float light = Float.intBitsToFloat(p.readInt());

        if (!isDyeableCategory(itemId)) {
            return;
        }
        Item item = resolveOwnedItem(chr, invType, slot, itemId);
        if (item == null || !ItemInformationProvider.getInstance().isCash(itemId)) {
            return;
        }

        Optional<Integer> invPk = ColoringPrismStorage.resolveInventoryItemId(chr.getId(), invType, slot, itemId);
        if (invPk.isEmpty()) {
            return;
        }

        ColoringPrismDye dye = new ColoringPrismDye(invPk.get(), chr.getId(), itemId, hue, sat, light).clamped();
        if (dye.isNearZero()) {
            ColoringPrismStorage.deleteByInventoryItemId(invPk.get());
            ColoringPrismStorage.deleteByCharacterAndItemId(chr.getId(), itemId);
            pushSelfAndBroadcastFull(c, chr);
            return;
        }

        ColoringPrismStorage.upsert(dye);
        List<ColoringPrismDye> self = ColoringPrismStorage.loadByCharacter(chr.getId());
        c.sendPacket(ColoringPrismPackets.dyeList(self));
        MapleMap map = chr.getMap();
        if (map != null) {
            map.broadcastMessage(chr, ColoringPrismPackets.dyeMerge(chr.getId(), Collections.singletonList(dye)), false);
        }
    }

    private void handleClear(InPacket p, Client c, Character chr) {
        byte invType = p.readByte();
        short slot = p.readShort();
        int itemId = p.readInt();

        Optional<Integer> invPk = ColoringPrismStorage.resolveInventoryItemId(chr.getId(), invType, slot, itemId);
        invPk.ifPresent(ColoringPrismStorage::deleteByInventoryItemId);
        ColoringPrismStorage.deleteByCharacterAndItemId(chr.getId(), itemId);
        pushSelfAndBroadcastFull(c, chr);
    }

    private void pushSelfAndBroadcastFull(Client c, Character chr) {
        List<ColoringPrismDye> self = ColoringPrismStorage.loadByCharacter(chr.getId());
        c.sendPacket(ColoringPrismPackets.dyeList(self));
        MapleMap map = chr.getMap();
        if (map != null) {
            map.broadcastMessage(chr, ColoringPrismPackets.dyeMerge(chr.getId(), self), false);
        }
    }

    private static Item resolveOwnedItem(Character chr, byte invTypeRaw, short slot, int itemId) {
        InventoryType mit = InventoryType.getByType(invTypeRaw);
        if (mit == InventoryType.UNDEFINED && invTypeRaw == (byte) 0xFF) {
            mit = InventoryType.EQUIPPED;
        }
        if (mit == InventoryType.UNDEFINED) {
            // 已穿戴优先
            if (slot < 0) {
                mit = InventoryType.EQUIPPED;
            } else {
                mit = InventoryType.CASH;
            }
        }
        Inventory inv = chr.getInventory(mit);
        if (inv == null) {
            return null;
        }
        Item item = inv.getItem(slot);
        if (item != null && item.getItemId() == itemId) {
            return item;
        }
        // 回退：EQUIPPED / CASH / EQUIP 按 position 扫
        for (InventoryType t : new InventoryType[]{InventoryType.EQUIPPED, InventoryType.CASH, InventoryType.EQUIP}) {
            Inventory i2 = chr.getInventory(t);
            if (i2 == null) {
                continue;
            }
            Item it = i2.getItem(slot);
            if (it != null && it.getItemId() == itemId) {
                return it;
            }
        }
        return null;
    }

    /** 上衣/套服/裤/披风/现金武器。 */
    public static boolean isDyeableCategory(int itemId) {
        int cat = itemId / 10000;
        return cat == 104 || cat == 105 || cat == 106 || cat == 110 || cat == 170;
    }
}
