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

import org.gms.client.Client;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.constants.game.GameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 仓库
 * 管理玩家仓库存储，支持物品存取、金币存取、槽位扩容等功能
 * 使用ReentrantLock保证多线程安全，缓存仓库容量信息
 *
 * @author Matze
 */
public class Storage {
    private static final Logger log = LoggerFactory.getLogger(Storage.class);
    /** 仓库取出费用缓存（NPC ID -> 费用） */
    private static final Map<Integer, Integer> trunkGetCache = new HashMap<>();
    /** 仓库存入费用缓存（NPC ID -> 费用） */
    private static final Map<Integer, Integer> trunkPutCache = new HashMap<>();

    /** 仓库ID（账户ID） */
    private final int id;
    /** 当前NPC ID */
    private int currentNpcid;
    /** 仓库金币 */
    private int meso;
    /** 仓库槽位数 */
    private byte slots;
    /** 仓库物品（按类型分组） */
    private final Map<InventoryType, List<Item>> typeItems = new HashMap<>();
    /** 仓库物品列表 */
    private List<Item> items = new LinkedList<>();
    /** 仓库锁，保证多线程安全 */
    private final Lock lock = new ReentrantLock(true);

    /**
     * 构造函数
     *
     * @param id    仓库ID
     * @param slots 槽位数
     * @param meso  金币数
     */
    private Storage(int id, byte slots, int meso) {
        this.id = id;
        this.slots = slots;
        this.meso = meso;
    }

    /**
     * 为新账户创建仓库记录
     *
     * @param id    账户ID
     * @param world 世界ID
     * @return 新仓库实例
     * @throws SQLException 数据库异常
     */
    private static Storage create(int id, int world) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO storages (accountid, world, slots, meso) VALUES (?, ?, 4, 0)")) {
            ps.setInt(1, id);
            ps.setInt(2, world);
            ps.executeUpdate();
        }

        return loadOrCreateFromDB(id, world);
    }

    /**
     * 从数据库加载或创建仓库实例
     *
     * @param id    账户ID
     * @param world 世界ID
     * @return 仓库实例
     */
    public static Storage loadOrCreateFromDB(int id, int world) {
        Storage ret;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT storageid, slots, meso FROM storages WHERE accountid = ? AND world = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, world);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret = new Storage(rs.getInt("storageid"), (byte) rs.getInt("slots"), rs.getInt("meso"));
                    for (Pair<Item, InventoryType> item : ItemFactory.STORAGE.loadItems(ret.id, false)) {
                        ret.items.add(item.getLeft());
                    }
                } else {
                    ret = create(id, world);
                }
            }

            return ret;
        } catch (SQLException ex) { // exceptions leading to deploy null storages found thanks to Jefe
            log.error("SQL error occurred when trying to load storage for accId {}, world {}", id, GameConstants.WORLD_NAMES[world], ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取仓库槽位数
     *
     * @return 槽位数
     */
    public byte getSlots() {
        return slots;
    }

    /**
     * 检查是否可以增加槽位
     *
     * @param slots 增加的槽位数
     * @return 可以增加返回true
     */
    public boolean canGainSlots(int slots) {
        slots += this.slots;
        return slots <= 48;
    }

    /**
     * 增加仓库槽位
     *
     * @param slots 增加的槽位数
     * @return 增加成功返回true
     */
    public boolean gainSlots(int slots) {
        lock.lock();
        try {
            if (canGainSlots(slots)) {
                slots += this.slots;
                this.slots = (byte) slots;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 保存仓库数据到数据库
     *
     * @param con 数据库连接
     */
    public void saveToDB(Connection con) {
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ?, meso = ? WHERE storageid = ?")) {
                ps.setInt(1, slots);
                ps.setInt(2, meso);
                ps.setInt(3, id);
                ps.executeUpdate();
            }
            List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();

            List<Item> list = getItems();
            for (Item item : list) {
                itemsWithType.add(new Pair<>(item, item.getInventoryType()));
            }

            ItemFactory.STORAGE.saveItems(itemsWithType, id, con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取指定槽位的物品
     *
     * @param slot 槽位索引
     * @return 物品
     */
    public Item getItem(byte slot) {
        lock.lock();
        try {
            return items.get(slot);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从仓库取出物品
     *
     * @param item 要取出的物品
     * @return 取出成功返回true
     */
    public boolean takeOut(Item item) {
        lock.lock();
        try {
            boolean ret = items.remove(item);

            InventoryType type = item.getInventoryType();
            typeItems.put(type, new ArrayList<>(filterItems(type)));

            return ret;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 存入物品到仓库
     *
     * @param item 要存入的物品
     * @return 存入成功返回true，仓库已满返回false
     */
    public boolean store(Item item) {
        lock.lock();
        try {
            // thanks Optimist for noticing unrestricted amount of insertions here
            if (isFull()) {
                return false;
            }

            items.add(item);

            InventoryType type = item.getInventoryType();
            typeItems.put(type, new ArrayList<>(filterItems(type)));

            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取仓库物品列表（不可修改）
     *
     * @return 物品列表
     */
    public List<Item> getItems() {
        lock.lock();
        try {
            return Collections.unmodifiableList(items);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 按物品栏类型过滤物品
     *
     * @param type 物品栏类型
     * @return 匹配的物品列表
     */
    private List<Item> filterItems(InventoryType type) {
        List<Item> storageItems = getItems();
        List<Item> ret = new LinkedList<>();

        for (Item item : storageItems) {
            if (item.getInventoryType() == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    /**
     * 获取物品在指定类型分组中的全局槽位
     *
     * @param type 物品栏类型
     * @param slot 类型内槽位索引
     * @return 全局槽位索引，未找到返回-1
     */
    public byte getSlot(InventoryType type, byte slot) {
        lock.lock();
        try {
            byte ret = 0;
            List<Item> storageItems = getItems();
            for (Item item : storageItems) {
                if (item == typeItems.get(type).get(slot)) {
                    return ret;
                }
                ret++;
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送仓库界面给客户端
     *
     * @param c     客户端
     * @param npcId NPC ID
     */
    public void sendStorage(Client c, int npcId) {
        if (c.getPlayer().getLevel() < 15) {
            c.getPlayer().dropMessage(1, "15级以后才可以使用仓库服务");
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        lock.lock();
        try {
            items.sort((o1, o2) -> {
                if (o1.getInventoryType().getType() < o2.getInventoryType().getType()) {
                    return -1;
                } else if (o1.getInventoryType() == o2.getInventoryType()) {
                    return 0;
                }
                return 1;
            });

            List<Item> storageItems = getItems();
            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(storageItems));
            }

            currentNpcid = npcId;
            c.sendPacket(PacketCreator.getStorage(npcId, slots, storageItems, meso));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送存入物品数据包
     *
     * @param c    客户端
     * @param type 物品栏类型
     */
    public void sendStored(Client c, InventoryType type) {
        lock.lock();
        try {
            c.sendPacket(PacketCreator.storeStorage(slots, type, typeItems.get(type)));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送取出物品数据包
     *
     * @param c    客户端
     * @param type 物品栏类型
     */
    public void sendTakenOut(Client c, InventoryType type) {
        lock.lock();
        try {
            c.sendPacket(PacketCreator.takeOutStorage(slots, type, typeItems.get(type)));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 整理仓库物品（合并+排序）
     *
     * @param c 客户端
     */
    public void arrangeItems(Client c) {
        lock.lock();
        try {
            StorageInventory msi = new StorageInventory(c, items);
            msi.mergeItems();
            items = msi.sortItems();

            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(items));
            }

            c.sendPacket(PacketCreator.arrangeStorage(slots, items));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取仓库金币
     *
     * @return 金币数
     */
    public int getMeso() {
        return meso;
    }

    /**
     * 设置仓库金币（不能为负数）
     *
     * @param meso 金币数
     */
    public void setMeso(int meso) {
        if (meso < 0) {
            throw new RuntimeException();
        }
        this.meso = meso;
    }

    /**
     * 发送金币数据包
     *
     * @param c 客户端
     */
    public void sendMeso(Client c) {
        c.sendPacket(PacketCreator.mesoStorage(slots, meso));
    }

    /**
     * 获取存入费用
     * thanks to GabrielSin
     *
     * @return 存入费用
     */
    public int getStoreFee() {
        int npcId = currentNpcid;
        Integer fee = trunkPutCache.get(npcId);
        if (fee == null) {
            fee = 100;

            DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
            Data npcData = npc.getData(npcId + ".img");
            if (npcData != null) {
                fee = DataTool.getIntConvert("info/trunkPut", npcData, 100);
            }

            trunkPutCache.put(npcId, fee);
        }

        return fee;
    }

    /**
     * 获取取出费用
     *
     * @return 取出费用
     */
    public int getTakeOutFee() {
        int npcId = currentNpcid;
        Integer fee = trunkGetCache.get(npcId);
        if (fee == null) {
            fee = 0;

            DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
            Data npcData = npc.getData(npcId + ".img");
            if (npcData != null) {
                fee = DataTool.getIntConvert("info/trunkGet", npcData, 0);
            }

            trunkGetCache.put(npcId, fee);
        }

        return fee;
    }

    /**
     * 检查仓库是否已满
     *
     * @return 已满返回true
     */
    public boolean isFull() {
        lock.lock();
        try {
            return items.size() >= slots;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭仓库，清理类型分组缓存
     */
    public void close() {
        lock.lock();
        try {
            typeItems.clear();
        } finally {
            lock.unlock();
        }
    }

}