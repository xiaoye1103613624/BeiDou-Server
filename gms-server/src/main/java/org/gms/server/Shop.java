/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.server;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.ItemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Matze
 */
public class Shop {
    private static final Logger log = LoggerFactory.getLogger(Shop.class);
    private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();

    private final int id;
    private final int npcId;
    private final List<ShopItem> items;
    private final int tokenvalue = 1000000000;
    private final int token = ItemId.GOLDEN_MAPLE_LEAF;

    static {
        for (int throwingStarId : ItemId.allThrowingStarIds()) {
            rechargeableItems.add(throwingStarId);
        }
        rechargeableItems.add(ItemId.BLAZE_CAPSULE);
        rechargeableItems.add(ItemId.GLAZE_CAPSULE);
        rechargeableItems.add(ItemId.BALANCED_FURY);
        rechargeableItems.remove(ItemId.DEVIL_RAIN_THROWING_STAR); // doesn't exist
        for (int bulletId : ItemId.allBulletIds()) {
            rechargeableItems.add(bulletId);
        }
    }

    private Shop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        items = new ArrayList<>();
    }

    private void addItem(ShopItem item) {
        items.add(item);
    }

    public void sendShop(Client c) {
        c.getPlayer().setShop(this);
        c.sendPacket(PacketCreator.getNPCShop(c, getNpcId(), items));
    }

    public void buy(Client c, short slot, int itemId, short quantity) {
        ShopItem item = findBySlot(slot);
        if (item != null) {
            if (item.getItemId() != itemId) {
                log.warn("Wrong slot number in shop {}", id);
                return;
            }
        } else {
            return;
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        InventoryType type = ItemConstants.getInventoryType(itemId);
        Character chr = c.getPlayer();
        Inventory inv = chr.getInventory(type);
        inv.lockInventory();
        try {
            if (item.getPrice() > 0) {
                int amount = (int) Math.min((float) item.getPrice() * quantity, Integer.MAX_VALUE);
                if (c.getPlayer().getMeso() >= amount) {
                    if (InventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                        if (!ItemConstants.isRechargeable(itemId)) { //Pets can't be bought from shops
                            InventoryManipulator.addById(c, itemId, quantity, "", -1);
                            c.getPlayer().gainMeso(-amount, false);
                        } else {
                            quantity = ii.getSlotMax(c, item.getItemId());
                            InventoryManipulator.addById(c, itemId, quantity, "", -1);
                            c.getPlayer().gainMeso(-item.getPrice(), false);
                        }
                        c.sendPacket(PacketCreator.shopTransaction((byte) 0));
                    } else {
                        c.sendPacket(PacketCreator.shopTransaction((byte) 3));
                    }

                } else {
                    c.sendPacket(PacketCreator.shopTransaction((byte) 2));
                }

            } else if (item.getPitch() > 0) {
                int amount = (int) Math.min((float) item.getPitch() * quantity, Integer.MAX_VALUE);

                if (c.getPlayer().getInventory(InventoryType.ETC).countById(ItemId.PERFECT_PITCH) >= amount) {
                    if (InventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                        if (!ItemConstants.isRechargeable(itemId)) {
                            InventoryManipulator.addById(c, itemId, quantity, "", -1);
                            InventoryManipulator.removeById(c, InventoryType.ETC, ItemId.PERFECT_PITCH, amount, false, false);
                        } else {
                            short slotMax = ii.getSlotMax(c, item.getItemId());
                            quantity = slotMax;
                            InventoryManipulator.addById(c, itemId, quantity, "", -1);
                            InventoryManipulator.removeById(c, InventoryType.ETC, ItemId.PERFECT_PITCH, amount, false, false);
                        }
                        c.sendPacket(PacketCreator.shopTransaction((byte) 0));
                    } else {
                        c.sendPacket(PacketCreator.shopTransaction((byte) 3));
                    }
                }

            } else if (c.getPlayer().getInventory(InventoryType.CASH).countById(token) != 0) {
                int amount = c.getPlayer().getInventory(InventoryType.CASH).countById(token);
                int value = amount * tokenvalue;
                int cost = item.getPrice() * quantity;
                if (c.getPlayer().getMeso() + value >= cost) {
                    int cardreduce = value - cost;
                    long diff = cardreduce + c.getPlayer().getMeso();
                    if (InventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                        if (ItemConstants.isPet(itemId)) {
                            int petid = Pet.createPet(itemId);
                            InventoryManipulator.addById(c, itemId, quantity, "", petid, -1);
                        } else {
                            InventoryManipulator.addById(c, itemId, quantity, "", -1, -1);
                        }
                        c.getPlayer().gainMeso(diff, false);
                    } else {
                        c.sendPacket(PacketCreator.shopTransaction((byte) 3));
                    }
                    c.sendPacket(PacketCreator.shopTransaction((byte) 0));
                } else {
                    c.sendPacket(PacketCreator.shopTransaction((byte) 2));
                }
            }
        } finally {
            inv.unlockInventory();
        }

    }

    /** Maple often stores unique/rechargeable qty as 0xFFFF; as a Java short that is -1. */
    private static short normalizeQuantity(short quantity) {
        if (quantity == (short) 0xFFFF) {
            return 1;
        }
        return quantity;
    }

    private static boolean isEquipLike(Item item, InventoryType type) {
        return item instanceof Equip || type == InventoryType.EQUIP;
    }

    /**
     * Cash 装备 itemId 仍是 1xxxxxx，但实际在 CASH 栏；仅按 getInventoryType 会找错栏 → 0x5 数量不足。
     * 另：整理 invent 截断会导致「客户端 slot 与服务端 slot 错位」——同一 slot 上 itemId 对不上。
     * v083 extended-equip sell UI may send a garbage itemId whose /1000000 type is wrong;
     * prefer (slot+itemId) across tabs, then findById, then slot occupancy (never trust a
     * garbage-derived hinted tab that happens to have a different item at the same slot).
     */
    private static InventoryType resolveSellInventoryType(Character chr, InventoryType hinted,
                                                          short slot, int expectedItemId) {
        final InventoryType[] order = {
                InventoryType.EQUIP, InventoryType.USE, InventoryType.SETUP,
                InventoryType.ETC, InventoryType.CASH
        };
        if (expectedItemId != 0) {
            for (InventoryType t : order) {
                Item at = chr.getInventory(t).getItem(slot);
                if (at != null && at.getItemId() == expectedItemId) {
                    return t;
                }
            }
            for (InventoryType t : order) {
                if (chr.getInventory(t).findById(expectedItemId) != null) {
                    return t;
                }
            }
            if (ItemInformationProvider.getInstance().isCash(expectedItemId)) {
                if (chr.getInventory(InventoryType.CASH).findById(expectedItemId) != null) {
                    return InventoryType.CASH;
                }
            }
            // Garbage / absent id: do not keep a wrong hinted tab just because that slot
            // is occupied. Prefer the first tab that actually holds something at this slot.
            if (hinted != null && hinted != InventoryType.UNDEFINED
                    && chr.getInventory(hinted).getItem(slot) != null) {
                Item atHint = chr.getInventory(hinted).getItem(slot);
                // Only keep hinted when id was empty/unknown — if we got here expectedId
                // was not found anywhere, so hinted is a last-resort when it alone has stock.
                boolean onlyHinted = true;
                for (InventoryType t : order) {
                    if (t == hinted) {
                        continue;
                    }
                    if (chr.getInventory(t).getItem(slot) != null) {
                        onlyHinted = false;
                        break;
                    }
                }
                if (onlyHinted && atHint != null) {
                    return hinted;
                }
            }
            for (InventoryType t : order) {
                if (chr.getInventory(t).getItem(slot) != null) {
                    if (t != hinted) {
                        log.info("shop sell type from slot occupancy: char={} slot={} packetId={} hinted={} -> {}",
                                chr.getName(), slot, expectedItemId, hinted, t);
                    }
                    return t;
                }
            }
        }
        if (hinted != null && hinted != InventoryType.UNDEFINED) {
            Item hintedItem = chr.getInventory(hinted).getItem(slot);
            if (hintedItem != null) {
                return hinted;
            }
        }
        for (InventoryType t : order) {
            if (chr.getInventory(t).getItem(slot) != null) {
                return t;
            }
        }
        return hinted != null && hinted != InventoryType.UNDEFINED ? hinted : InventoryType.EQUIP;
    }

    /**
     * Packet slot is authoritative when occupied. After sort/invent desync the client may
     * send a stale slot with a correct itemId — only then relocate by id within the tab.
     * v083 extended-equip sell often sends garbage itemId; never relocate away from a
     * filled packet slot just because findById hits a different row.
     */
    private static short resolveSellSlot(Inventory inventory, short packetSlot, int expectedItemId,
                                         Character chr) {
        Item atPacket = inventory.getItem(packetSlot);
        if (atPacket != null && (expectedItemId == 0 || atPacket.getItemId() == expectedItemId)) {
            return packetSlot;
        }
        if (atPacket != null) {
            log.info("shop sell client itemId mismatch, using slot: char={} slot={} expectId={} actualId={}",
                    chr.getName(), packetSlot, expectedItemId, atPacket.getItemId());
            return packetSlot;
        }
        if (expectedItemId == 0) {
            return -1;
        }
        Item byId = inventory.findById(expectedItemId);
        if (byId != null) {
            log.warn("shop sell slot desync: char={} packetSlot={} expectId={} atPacket=null -> actualSlot={}",
                    chr.getName(), packetSlot, expectedItemId, byId.getPosition());
            return byId.getPosition();
        }
        return -1;
    }

    private static boolean canSell(Item item, InventoryType type, short quantity) {
        if (item == null) { //Basic check
            return false;
        }

        // Equips are always qty 1 on the wire; DB/qty-0 / client garbage must not block sell.
        if (isEquipLike(item, type)) {
            return true;
        }

        short iQuant = normalizeQuantity(item.getQuantity());
        if (iQuant < 0) {
            return false;
        }

        if (!ItemConstants.isRechargeable(item.getItemId())) {
            return iQuant != 0 && quantity <= iQuant;
        }

        return true;
    }

    private static short getSellingQuantity(Item item, InventoryType type, short quantity) {
        if (isEquipLike(item, type)) {
            // Client sell-struct +0x34 is unreliable for some equips (extended stats);
            // equips are always a single unit on the server.
            return 1;
        }
        if (ItemConstants.isRechargeable(item.getItemId())) {
            return normalizeQuantity(item.getQuantity());
        }
        return quantity;
    }

    public void sell(Client c, InventoryType type, short slot, short quantity) {
        sell(c, type, slot, quantity, 0);
    }

    public void sell(Client c, InventoryType type, short slot, short quantity, int expectedItemId) {
        final short clientQtyRaw = quantity;
        if (quantity == (short) 0xFFFF || quantity == 0) {
            quantity = 1;
        } else if (quantity < 0) {
            return;
        }

        final InventoryType hintedType = type;
        type = resolveSellInventoryType(c.getPlayer(), type, slot, expectedItemId);
        Inventory inventory = c.getPlayer().getInventory(type);
        inventory.lockInventory();
        try {
            short resolvedSlot = resolveSellSlot(inventory, slot, expectedItemId, c.getPlayer());
            if (resolvedSlot < 1) {
                log.warn("shop sell reject(slot/id): char={} hintedType={} resolvedType={} slot={} expectId={} found={}",
                        c.getPlayer().getName(), hintedType, type, slot, expectedItemId,
                        inventory.getItem(slot) == null ? "null" : inventory.getItem(slot).getItemId());
                c.sendPacket(PacketCreator.shopTransaction((byte) 0x5));
                return;
            }
            slot = resolvedSlot;
            Item item = inventory.getItem(slot);
            if (item == null) {
                log.warn("shop sell reject(empty slot): char={} type={} slot={} expectId={}",
                        c.getPlayer().getName(), type, slot, expectedItemId);
                c.sendPacket(PacketCreator.shopTransaction((byte) 0x5));
                return;
            }
            // Soft mismatch only: log when packet id disagrees with the slot we will sell.
            // Never reject here — garbage extended-equip ids used to break legitimate sells.
            if (expectedItemId != 0 && item.getItemId() != expectedItemId) {
                log.info("shop sell itemId soft-mismatch (selling slot item): char={} slot={} packetId={} actualId={}",
                        c.getPlayer().getName(), slot, expectedItemId, item.getItemId());
            }

            final short serverQty = item.getQuantity();
            // Real anomaly: mob-pickup equips must be qty 1. Log so we can locate writers.
            if (item instanceof Equip && serverQty != 1) {
                log.error("shop sell EQUIP qty anomaly: char={} itemId={} slot={} serverQty={} clientQtyRaw={} — pickup path should have set 1",
                        c.getPlayer().getName(), item.getItemId(), slot, serverQty, clientQtyRaw);
            } else if (isEquipLike(item, type) && clientQtyRaw != 1 && clientQtyRaw != 0
                    && clientQtyRaw != (short) 0xFFFF) {
                // Inventory is in sync (server=1); sell packet field alone is garbage.
                log.info("shop sell equip clientQty garbage (inventory OK): char={} itemId={} slot={} serverQty={} clientQtyRaw={}",
                        c.getPlayer().getName(), item.getItemId(), slot, serverQty, clientQtyRaw);
            }

            // Equips: ignore client qty (often garbage) before canSell; heal qty-0 rows.
            if (isEquipLike(item, type)) {
                quantity = 1;
                if (item instanceof Equip && item.getQuantity() != 1) {
                    // Equip allows 0 or 1; force 1 so removeFromSlot actually clears the slot.
                    item.setQuantity((short) 1);
                }
            }

            if (!canSell(item, type, quantity)) {
                log.warn("shop sell reject(canSell): char={} itemId={} slot={} type={} serverQty={} clientQtyRaw={} useQty={}",
                        c.getPlayer().getName(), item.getItemId(), slot, type, serverQty, clientQtyRaw, quantity);
                c.sendPacket(PacketCreator.shopTransaction((byte) 0x5));
                return;
            }

            quantity = getSellingQuantity(item, type, quantity);

            // Use short slot — (byte) truncates positions > 127 on extended inventories.
            InventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            int recvMesos = ii.getPrice(item.getItemId(), quantity);
            if (recvMesos > 0) {
                c.getPlayer().gainMeso(recvMesos, false);
            }
            c.sendPacket(PacketCreator.shopTransaction((byte) 0x8));
        } finally {
            inventory.unlockInventory();
        }
    }

    public void recharge(Client c, short slot) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Inventory inventory = c.getPlayer().getInventory(InventoryType.USE);
        Item item = inventory.getItem(slot);
        if (item == null || !ItemConstants.isRechargeable(item.getItemId())) {
            return;
        }
        short slotMax = ii.getSlotMax(c, item.getItemId());
        if (item.getQuantity() < 0) {
            return;
        }
        inventory.lockInventory();
        try {
            if (item.getQuantity() < slotMax) {
                int price = (int) Math.ceil(ii.getUnitPrice(item.getItemId()) * (slotMax - item.getQuantity()));
                if (c.getPlayer().getMeso() >= price) {
                    item.setQuantity(slotMax);
                    c.getPlayer().forceUpdateItem(item);
                    c.getPlayer().gainMeso(-price, false, true, false);
                    c.sendPacket(PacketCreator.shopTransaction((byte) 0x8));
                } else {
                    c.sendPacket(PacketCreator.shopTransaction((byte) 0x2));
                }
            }
        } finally {
            inventory.unlockInventory();
        }

    }

    private ShopItem findBySlot(short slot) {
        return items.get(slot);
    }

    public static Shop createFromDB(int id, boolean isShopId) {
        Shop ret = null;
        int shopId;
        try (Connection con = DatabaseConnection.getConnection()) {
            final String query;
            if (isShopId) {
                query = "SELECT * FROM shops WHERE shopid = ?";
            } else {
                query = "SELECT * FROM shops WHERE npcid = ?";
            }

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        shopId = rs.getInt("shopid");
                        ret = new Shop(shopId, rs.getInt("npcid"));
                    } else {
                        return null;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT itemid, price, pitch FROM shopitems WHERE shopid = ? ORDER BY position DESC")) {
                ps.setInt(1, shopId);

                try (ResultSet rs = ps.executeQuery()) {
                    ItemInformationProvider ii = ItemInformationProvider.getInstance();
                    List<Integer> recharges = new ArrayList<>(rechargeableItems);
                    while (rs.next()) {
                        int itemId = rs.getInt("itemid");
                        // 缺 Item.wz 节点 / 客户端无图标的道具会在开店时触发 E_POINTER（无效的指针）
                        if (!ii.itemExists(itemId)) {
                            log.warn("Shop {} skips missing item {}", shopId, itemId);
                            continue;
                        }
                        if (ItemConstants.isRechargeable(itemId)) {
                            ShopItem starItem = new ShopItem((short) 1, itemId, rs.getInt("price"), rs.getInt("pitch"));
                            ret.addItem(starItem);
                            if (rechargeableItems.contains(starItem.getItemId())) {
                                recharges.remove(Integer.valueOf(starItem.getItemId()));
                            }
                        } else {
                            ret.addItem(new ShopItem((short) 1000, itemId, rs.getInt("price"), rs.getInt("pitch")));
                        }
                    }
                    for (Integer recharge : recharges) {
                        if (!ii.itemExists(recharge)) {
                            continue;
                        }
                        ret.addItem(new ShopItem((short) 1000, recharge, 0, 0));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getId() {
        return id;
    }
}
