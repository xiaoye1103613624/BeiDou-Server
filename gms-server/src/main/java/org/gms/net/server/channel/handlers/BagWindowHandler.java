package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.inventory.manipulator.KarmaManipulator;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.ItemInformationProvider;
import org.gms.server.OreStorage;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 收纳背包窗口协议处理 (Recv 0x3724 / Send 0x3725)。
 */
public final class BagWindowHandler extends AbstractPacketHandler {

    private static final Logger log = LoggerFactory.getLogger(BagWindowHandler.class);
    private static final int REQ_OPEN = 0, REQ_WITHDRAW = 1, REQ_DEPOSIT = 2, REQ_MERGE = 3, REQ_SET_AUTO = 4, REQ_MOVE = 5;
    private static final String[] BAG_NAME = {"矿石包", "卷轴包", "椅子包", "坐骑包"};

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character player = c.getPlayer();
        if (player == null) {
            return;
        }
        int action = p.readByte();
        int bagKind = p.readByte();
        if (bagKind < 0 || bagKind > 3) {
            return;
        }
        OreStorage storage = storageFor(player, bagKind);
        if (storage == null) {
            player.dropMessage(1, "收纳背包未加载，请重新登录后再试。");
            return;
        }

        try {
            switch (action) {
                case REQ_OPEN -> { /* snapshot below */ }
                case REQ_WITHDRAW -> {
                    int srcSlot = p.readShort();
                    int targetInvSlot = p.readShort();
                    withdraw(c, player, storage, bagKind, srcSlot, targetInvSlot);
                }
                case REQ_DEPOSIT -> {
                    int srcInvType = p.readShort();
                    int srcInvPos = p.readShort();
                    int targetBagSlot = p.readShort();
                    deposit(c, player, storage, bagKind, srcInvType, srcInvPos, targetBagSlot);
                }
                case REQ_MOVE -> {
                    int srcSlot = p.readShort();
                    int dstSlot = p.readShort();
                    storage.move(srcSlot, dstSlot, c);
                }
                case REQ_MERGE -> storage.mergeStacks(c);
                case REQ_SET_AUTO -> {
                    int on = p.readByte();
                    setAuto(player, bagKind, on != 0);
                }
                default -> {
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("[BagWindow] action={} bagKind={} threw", action, bagKind, e);
        }

        c.sendPacket(PacketCreator.bagWindowSnapshot(bagKind, storage, isAuto(player, bagKind)));
    }

    private static OreStorage storageFor(Character player, int kind) {
        return switch (kind) {
            case 1 -> player.getScrollStorage();
            case 2 -> player.getChairStorage();
            case 3 -> player.getMountStorage();
            default -> player.getOreStorage();
        };
    }

    private static void withdraw(Client c, Character player, OreStorage storage, int kind, int srcSlot, int targetInvSlot) {
        Item item = storage.getItemAtSlot(srcSlot);
        if (item == null) {
            return;
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && player.haveItemWithId(item.getItemId(), true)) {
            player.dropMessage(1, "你已拥有该唯一物品。");
            return;
        }
        if (!InventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
            player.dropMessage(1, "背包空间不足，无法取出。");
            return;
        }
        if (storage.takeOut(item)) {
            KarmaManipulator.toggleKarmaFlagToUntradeable(item);
            if (!placeInInventory(c, player, item, targetInvSlot)) {
                storage.storeMerge(item, c);
                player.dropMessage(1, "无法将物品放入背包。");
                return;
            }
            markUsed(player, kind);
        }
    }

    private static boolean placeInInventory(Client c, Character player, Item item, int targetInvSlot) {
        InventoryType type = item.getInventoryType();
        Inventory inv = player.getInventory(type);
        if (inv != null && targetInvSlot >= 1 && targetInvSlot <= inv.getSlotLimit()) {
            boolean placed = false;
            inv.lockInventory();
            try {
                if (inv.getItem((short) targetInvSlot) == null) {
                    item.setPosition((short) targetInvSlot);
                    inv.addItemFromDB(item);
                    placed = true;
                }
            } finally {
                inv.unlockInventory();
            }
            if (placed) {
                c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, item))));
                return true;
            }
        }
        return InventoryManipulator.addFromDrop(c, item, false);
    }

    private static void deposit(Client c, Character player, OreStorage storage, int kind,
                                int srcInvType, int srcInvPos, int targetBagSlot) {
        InventoryType type = InventoryType.getByType((byte) srcInvType);
        if (type == null) {
            return;
        }
        Inventory inv = player.getInventory(type);
        if (inv == null || srcInvPos < 1 || srcInvPos > inv.getSlotLimit()) {
            return;
        }
        Item item = inv.getItem((short) srcInvPos);
        if (item == null) {
            return;
        }
        int itemId = item.getItemId();
        if (!Character.bagAccepts(kind, itemId)) {
            player.dropMessage(1, BAG_NAME[kind] + "无法存放该物品！");
            return;
        }
        if (storage.isFull()) {
            return;
        }
        short qty;
        inv.lockInventory();
        try {
            item = inv.getItem((short) srcInvPos);
            if (item == null || item.getItemId() != itemId) {
                return;
            }
            qty = item.getQuantity();
            InventoryManipulator.removeFromSlot(c, type, (short) srcInvPos, qty, false);
            item = item.copy();
        } finally {
            inv.unlockInventory();
        }
        KarmaManipulator.toggleKarmaFlagToUntradeable(item);
        item.setQuantity(qty);
        if (!storage.storeAt(item, targetBagSlot, c)) {
            InventoryManipulator.addFromDrop(c, item, false);
            player.dropMessage(1, BAG_NAME[kind] + "已满。");
            return;
        }
        markUsed(player, kind);
    }

    private static void markUsed(Character player, int kind) {
        switch (kind) {
            case 1 -> player.setUsedScrollStorage();
            case 2 -> player.setUsedChairStorage();
            case 3 -> player.setUsedMountStorage();
            default -> player.setUsedOreStorage();
        }
    }

    private static boolean isAuto(Character player, int kind) {
        return switch (kind) {
            case 1 -> player.isAutoScrollStorage();
            case 2 -> player.isAutoChairStorage();
            case 3 -> player.isAutoMountStorage();
            default -> player.isAutoOreStorage();
        };
    }

    private static void setAuto(Character player, int kind, boolean on) {
        switch (kind) {
            case 1 -> player.setAutoScrollStorage(on);
            case 2 -> player.setAutoChairStorage(on);
            case 3 -> player.setAutoMountStorage(on);
            default -> player.setAutoOreStorage(on);
        }
    }
}
