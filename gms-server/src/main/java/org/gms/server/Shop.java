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
 * 商店
 * 管理NPC商店的加载、物品出售和玩家购买逻辑
 * 支持星石/金币兑换、充值物品、宠物、技能重置等特殊物品
 *
 * @author Matze
 */
public class Shop {
    private static final Logger log = LoggerFactory.getLogger(Shop.class);
    /** 可充值物品集合 */
    private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();

    /** 商店ID */
    private final int id;
    /** NPC ID */
    private final int npcId;
    /** 出售物品列表 */
    private final List<ShopItem> items;
    /** 代币价值（1金币 = ?代币） */
    private final int tokenvalue = 1000000000;
    /** 代币物品ID（黄金枫叶） */
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
     * 私有构造函数，通过createFromDB创建
     *
     * @param id    商店ID
     * @param npcId NPC ID
     */
    private Shop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        items = new ArrayList<>();
    }

    /**
     * 添加物品到商店
     *
     * @param item 商店物品
     */
    private void addItem(ShopItem item) {
        items.add(item);
    }

    /**
     * 发送商店数据包给客户端
     *
     * @param c 客户端
     */
    public void sendShop(Client c) {
        c.getPlayer().setShop(this);
        c.sendPacket(PacketCreator.getNPCShop(c, getNpcId(), items));
    }

    /**
     * 玩家购买物品
     *
     * @param c        客户端
     * @param slot     购买项在列表中的索引
     * @param itemId   物品ID
     * @param quantity 购买数量
     */
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
                        // Pets can't be bought from shops
                        if (!ItemConstants.isRechargeable(itemId)) {
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

    /**
     * 检查物品是否可出售
     *
     * @param item     物品
     * @param quantity 数量
     * @return 可出售返回true
     */
    private static boolean canSell(Item item, short quantity) {
        // Basic check
        if (item == null) {
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

    /**
     * 获取实际出售数量（充值物品按当前充能数出售）
     *
     * @param item     物品
     * @param quantity 请求数量
     * @return 实际出售数量
     */
    private static short getSellingQuantity(Item item, short quantity) {
        if (ItemConstants.isRechargeable(item.getItemId())) {
            quantity = item.getQuantity();
            if (quantity == 0xFFFF) {
                quantity = 1;
            }
        }

        return quantity;
    }

    /**
     * 玩家出售物品给商店
     *
     * @param c        客户端
     * @param type     物品栏类型
     * @param slot     物品栏位置
     * @param quantity 出售数量
     */
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

    /**
     * 充能物品（镖/子弹等）
     *
     * @param c    客户端
     * @param slot 物品栏位置
     */
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

    /**
     * 按位置索引查找物品
     *
     * @param slot 位置索引
     * @return 商店物品
     */
    private ShopItem findBySlot(short slot) {
        return items.get(slot);
    }

    /**
     * 从数据库创建商店实例
     *
     * @param id       商店ID或NPC ID
     * @param isShopId true=按商店ID查找, false=按NPC ID查找
     * @return 商店实例，未找到返回null
     */
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
                    List<Integer> recharges = new ArrayList<>(rechargeableItems);
                    while (rs.next()) {
                        if (ItemConstants.isRechargeable(rs.getInt("itemid"))) {
                            ShopItem starItem = new ShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("pitch"));
                            ret.addItem(starItem);
                            if (rechargeableItems.contains(starItem.getItemId())) {
                                recharges.remove(Integer.valueOf(starItem.getItemId()));
                            }
                        } else {
                            ret.addItem(new ShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("pitch")));
                        }
                    }
                    for (Integer recharge : recharges) {
                        ret.addItem(new ShopItem((short) 1000, recharge, 0, 0));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获取NPC ID
     *
     * @return NPC ID
     */
    public int getNpcId() {
        return npcId;
    }

    /**
     * 获取商店ID
     *
     * @return 商店ID
     */
    public int getId() {
        return id;
    }
}