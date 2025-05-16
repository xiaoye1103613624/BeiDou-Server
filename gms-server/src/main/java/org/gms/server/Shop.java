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
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
/**
 * 商店 与 NPC 对应类 附带商品信息
 * `shops` (`shopid`, `npcid`)
 */

/**
 * @author Matze
 */
public class Shop {
    private static final Logger log = LoggerFactory.getLogger(Shop.class);
    private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();
    /**
     * 商店id
     */
    private final int id;
    /**
     * npcId
     */
    private final int npcId;
    /**
     * 商品列表
     */
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
        // doesn't exist
        rechargeableItems.remove(ItemId.DEVIL_RAIN_THROWING_STAR);
        for (int bulletId : ItemId.allBulletIds()) {
            rechargeableItems.add(bulletId);
        }
    }

    /**
     * 私有的构造函数，用于创建Shop对象。
     *
     * @param id Shop的唯一标识符
     * @param npcId 与Shop关联的NPC的唯一标识符
     */
    private Shop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        items = new ArrayList<>();
    }

    private void addItem(ShopItem item) {
        items.add(item);
    }

    /**
     * 将商店发送给客户端
     *
     * @param c 客户端对象
     */
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
                            InventoryManipulator.removeById(c, InventoryType.ETC, ItemId.PERFECT_PITCH, amount, false,
                                    false);
                        } else {
                            short slotMax = ii.getSlotMax(c, item.getItemId());
                            quantity = slotMax;
                            InventoryManipulator.addById(c, itemId, quantity, "", -1);
                            InventoryManipulator.removeById(c, InventoryType.ETC, ItemId.PERFECT_PITCH, amount, false,
                                    false);
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
                    int diff = cardreduce + c.getPlayer().getMeso();
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

    private static boolean canSell(Item item, short quantity) {
        if (item == null) { //Basic check
            return false;
        }

        short iQuant = item.getQuantity();
        if (iQuant == 0xFFFF) {
            iQuant = 1;
        } else if (iQuant < 0) {
            return false;
        }

        if (!ItemConstants.isRechargeable(item.getItemId())) {
            return iQuant != 0 && quantity <= iQuant;
        }

        return true;
    }

    private static short getSellingQuantity(Item item, short quantity) {
        if (ItemConstants.isRechargeable(item.getItemId())) {
            quantity = item.getQuantity();
            if (quantity == 0xFFFF) {
                quantity = 1;
            }
        }

        return quantity;
    }

    public void sell(Client c, InventoryType type, short slot, short quantity) {
        if (quantity == 0xFFFF || quantity == 0) {
            quantity = 1;
        } else if (quantity < 0) {
            return;
        }

        Inventory inventory = c.getPlayer().getInventory(type);
        Item item = inventory.getItem(slot);
        inventory.lockInventory();
        try {
            if (canSell(item, quantity)) {
                quantity = getSellingQuantity(item, quantity);
                InventoryManipulator.removeFromSlot(c, type, (byte) slot, quantity, false);

                ItemInformationProvider ii = ItemInformationProvider.getInstance();
                int recvMesos = ii.getPrice(item.getItemId(), quantity);
                if (recvMesos > 0) {
                    c.getPlayer().gainMeso(recvMesos, false);
                }
                c.sendPacket(PacketCreator.shopTransaction((byte) 0x8));
            } else {
                c.sendPacket(PacketCreator.shopTransaction((byte) 0x5));
            }
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

    /**
     * 从数据库中查询并 创建商店对象
     *
     * @param id 数据库中的ID;商店id或者npcid
     * @param isShopId 是否为店铺ID
     * @return 创建的Shop对象，如果找不到则返回null
     */
    public static Shop createFromDB(int id, boolean isShopId) {
        // 商店对象
        Shop shop = null;
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
                        shop = new Shop(shopId, rs.getInt("npcid"));
                    } else {
                        // 库中没有
                        return null;
                    }
                }
            }
            // 添加商店中的商品信息

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT itemid, price, pitch FROM shopitems WHERE shopid = ? ORDER BY position DESC")) {
                ps.setInt(1, shopId);

                try (ResultSet rs = ps.executeQuery()) {
                    List<Integer> recharges = new ArrayList<>(rechargeableItems);
                    while (rs.next()) {
                        if (ItemConstants.isRechargeable(rs.getInt("itemid"))) {
                            ShopItem starItem = new ShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"),
                                    rs.getInt("pitch"));
                            shop.addItem(starItem);
                            if (rechargeableItems.contains(starItem.getItemId())) {
                                recharges.remove(Integer.valueOf(starItem.getItemId()));
                            }
                        } else {
                            shop.addItem(new ShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price"),
                                    rs.getInt("pitch")));
                        }
                    }
                    for (Integer recharge : recharges) {
                        shop.addItem(new ShopItem((short) 1000, recharge, 0, 0));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shop;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getId() {
        return id;
    }
}
