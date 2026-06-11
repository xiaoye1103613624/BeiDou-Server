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
package org.gms.client.inventory;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.server.ThreadManager;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 物品背包
 * 管理角色单类物品（装备/消耗/现金等），使用LinkedHashMap按槽位存储
 * 支持物品查找、添加、移动、移除、合并和空格检查，使用ReentrantLock保证线程安全
 *
 * @author Matze, Ronan
 */
public class Inventory implements Iterable<Item> {
    private static final Logger log = LoggerFactory.getLogger(Inventory.class);

    /** 物品存储 Map，key=槽位编号(short)，value=物品对象 */
    protected final Map<Short, Item> inventory;
    /** 背包类型（装备/消耗/现金等） */
    protected final InventoryType type;
    /** 公平可重入锁，保护并发读写 */
    protected final Lock lock = new ReentrantLock(true);

    /** 所属角色 */
    protected Character owner;
    /** 背包槽位上限 */
    protected byte slotLimit;
    /** 背包容量是否已经过校验 */
    protected boolean checked = false;

    public Inventory(Character mc, InventoryType type, byte slotLimit) {
        this.owner = mc;
        this.inventory = new LinkedHashMap<>();
        this.type = type;
        this.slotLimit = slotLimit;
    }

    /**
     * 是否可扩展槽位的背包类型
     * UNDEFINED/EQUIPPED/CASH 不可扩展
     */
    public boolean isExtendableInventory() {
        return !(type.equals(InventoryType.UNDEFINED) || type.equals(InventoryType.EQUIPPED) || type.equals(InventoryType.CASH));
    }

    /**
     * 是否为装备类背包
     */
    public boolean isEquipInventory() {
        return type.equals(InventoryType.EQUIP) || type.equals(InventoryType.EQUIPPED);
    }

    /**
     * 获取当前背包槽位上限
     */
    public byte getSlotLimit() {
        lock.lock();
        try {
            return slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置背包槽位上限
     * 如果新上限小于旧上限，超出部分的物品将被移除
     *
     * @param newLimit 新槽位上限
     */
    public void setSlotLimit(int newLimit) {
        lock.lock();
        try {
            if (newLimit < slotLimit) {
                List<Short> toRemove = new LinkedList<>();
                for (Item it : list()) {
                    if (it.getPosition() > newLimit) {
                        toRemove.add(it.getPosition());
                    }
                }

                for (Short slot : toRemove) {
                    removeSlot(slot);
                }
            }

            slotLimit = (byte) newLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 返回背包中所有物品的只读集合
     */
    public Collection<Item> list() {
        lock.lock();
        try {
            return Collections.unmodifiableCollection(inventory.values());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 按物品ID查找（返回第一个匹配）
     *
     * @param itemId 物品ID
     * @return 找到的物品，未找到返回null
     */
    public Item findById(int itemId) {
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 按物品名称查找（忽略大小写）
     *
     * @param name 物品名称
     * @return 找到的物品，未找到返回null
     */
    public Item findByName(String name) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        for (Item item : list()) {
            String itemName = ii.getName(item.getItemId());
            if (itemName == null) {
                log.error("[CRITICAL] Item {} has no name", item.getItemId());
                continue;
            }

            if (name.compareToIgnoreCase(itemName) == 0) {
                return item;
            }
        }
        return null;
    }

    /**
     * 统计指定物品ID的总数量（累加所有堆叠）
     *
     * @param itemId 物品ID
     * @return 总数量
     */
    public int countById(int itemId) {
        int qty = 0;
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    /**
     * 统计无归属者（owner为空）的同ID物品数量
     */
    public int countNotOwnedById(int itemId) {
        int qty = 0;
        for (Item item : list()) {
            if (item.getItemId() == itemId && item.getOwner().equals("")) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    /**
     * 计算容纳指定数量物品所需释放的槽位数
     *
     * @param itemId   物品ID
     * @param required 需要容纳的数量
     * @return 所需释放的槽位数，不足则返回-1
     */
    public int freeSlotCountById(int itemId, int required) {
        List<Item> itemList = listById(itemId);
        int openSlot = 0;

        if (!ItemConstants.isRechargeable(itemId)) {
            for (Item item : itemList) {
                required -= item.getQuantity();

                if (required >= 0) {
                    openSlot++;
                    if (required == 0) {
                        return openSlot;
                    }
                } else {
                    return openSlot;
                }
            }
        } else {
            // 充能道具每个占1格
            for (Item item : itemList) {
                required -= 1;

                if (required >= 0) {
                    openSlot++;
                    if (required == 0) {
                        return openSlot;
                    }
                } else {
                    return openSlot;
                }
            }
        }

        return -1;
    }

    /**
     * 列出所有指定ID的物品（按槽位排序）
     */
    public List<Item> listById(int itemId) {
        List<Item> ret = new ArrayList<>();
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }

        if (ret.size() > 1) {
            ret.sort((i1, i2) -> i1.getPosition() - i2.getPosition());
        }

        return ret;
    }

    /**
     * 列出所有指定ID的物品（LinkedList版本，按槽位排序）
     */
    public List<Item> linkedListById(int itemId) {
        List<Item> ret = new LinkedList<>();
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }

        if (ret.size() > 1) {
            ret.sort((i1, i2) -> i1.getPosition() - i2.getPosition());
        }

        return ret;
    }

    /**
     * 添加物品到背包（自动分配槽位）
     *
     * @param item 物品对象
     * @return 分配的槽位编号，背包满返回-1
     */
    public short addItem(Item item) {
        short slotId = addSlot(item);
        if (slotId == -1) {
            return -1;
        }
        item.setPosition(slotId);
        return slotId;
    }

    /**
     * 从数据库加载物品到背包（保留原槽位）
     */
    public void addItemFromDB(Item item) {
        if (item.getPosition() < 0 && !type.equals(InventoryType.EQUIPPED)) {
            return;
        }
        addSlotFromDB(item.getPosition(), item);
    }

    /**
     * 判断两个物品归属者是否相同
     */
    private static boolean isSameOwner(Item source, Item target) {
        return source.getOwner().equals(target.getOwner());
    }

    /**
     * 移动/合并物品槽位
     * 目标为空时直接移动；目标为同ID同归属者时尝试合并（装备/现金互换）
     *
     * @param sSlot   源槽位
     * @param dSlot   目标槽位
     * @param slotMax 单格最大堆叠数
     */
    public void move(short sSlot, short dSlot, short slotMax) {
        lock.lock();
        try {
            Item source = inventory.get(sSlot);
            Item target = inventory.get(dSlot);
            if (source == null) {
                return;
            }
            if (target == null) {
                // 目标为空：直接移动
                source.setPosition(dSlot);
                inventory.put(dSlot, source);
                inventory.remove(sSlot);
            } else if (target.getItemId() == source.getItemId() && !ItemConstants.isRechargeable(source.getItemId()) && isSameOwner(source, target)) {
                // 同ID同归属者：尝试合并
                if (type.getType() == InventoryType.EQUIP.getType() || type.getType() == InventoryType.CASH.getType()) {
                    swap(target, source);
                } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                    short rest = (short) ((source.getQuantity() + target.getQuantity()) - slotMax);
                    source.setQuantity(rest);
                    target.setQuantity(slotMax);
                } else {
                    target.setQuantity((short) (source.getQuantity() + target.getQuantity()));
                    inventory.remove(sSlot);
                }
            } else {
                // 同ID不同归属者或不同物品：交换位置
                swap(target, source);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 交换两个物品的位置
     */
    private void swap(Item source, Item target) {
        inventory.remove(source.getPosition());
        inventory.remove(target.getPosition());
        short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        inventory.put(source.getPosition(), source);
        inventory.put(target.getPosition(), target);
    }

    /**
     * 获取指定槽位的物品
     *
     * @param slot 槽位编号
     * @return 物品对象，空槽返回null
     */
    public Item getItem(short slot) {
        lock.lock();
        try {
            return inventory.get(slot);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除指定槽位1个物品
     */
    public void removeItem(short slot) {
        removeItem(slot, (short) 1, false);
    }

    /**
     * 移除指定槽位指定数量的物品
     *
     * @param slot      槽位编号
     * @param quantity  移除数量
     * @param allowZero 数量归零时是否保留槽位（true=保留空槽）
     */
    public void removeItem(short slot, short quantity, boolean allowZero) {
        Item item = getItem(slot);
        if (item == null) {
            return;
        }
        item.setQuantity((short) (item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short) 0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            removeSlot(slot);
        }
    }

    /**
     * 将物品放入下一个空闲槽位
     *
     * @param item 物品对象
     * @return 分配的槽位，背包满返回-1
     */
    protected short addSlot(Item item) {
        if (item == null) {
            return -1;
        }

        short slotId;
        lock.lock();
        try {
            slotId = getNextFreeSlot();
            if (slotId < 0) {
                return -1;
            }

            inventory.put(slotId, item);
        } finally {
            lock.unlock();
        }

        // 如果是倍率券，异步更新角色倍率
        if (ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }

        return slotId;
    }

    /**
     * 从数据库恢复物品到指定槽位（不清空原位置）
     */
    protected void addSlotFromDB(short slot, Item item) {
        lock.lock();
        try {
            inventory.put(slot, item);
        } finally {
            lock.unlock();
        }

        // 如果是倍率券，异步更新角色倍率
        if (ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 移除指定槽位的物品（并释放该槽位）
     */
    public void removeSlot(short slot) {
        Item item;
        lock.lock();
        try {
            item = inventory.remove(slot);
        } finally {
            lock.unlock();
        }

        // 移除倍率券后异步更新角色倍率
        if (item != null && ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 背包是否已满
     */
    public boolean isFull() {
        lock.lock();
        try {
            return inventory.size() >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 背包在多余margin个物品后是否会满
     *
     * @param margin 额外物品数量
     */
    public boolean isFull(int margin) {
        lock.lock();
        try {
            return inventory.size() + margin >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 背包在多余margin个物品且已有used个占用后是否会满
     *
     * @param margin 额外物品数量
     * @param used   已占用槽位数
     */
    public boolean isFullAfterSomeItems(int margin, int used) {
        lock.lock();
        try {
            return inventory.size() + margin >= slotLimit - used;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取下一个空闲槽位编号（从1开始扫描）
     *
     * @return 空闲槽位编号，背包满返回-1
     */
    public short getNextFreeSlot() {
        if (isFull()) {
            return -1;
        }

        lock.lock();
        try {
            for (short i = 1; i <= slotLimit; i++) {
                if (!inventory.containsKey(i)) {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前空闲槽位数量
     */
    public short getNumFreeSlot() {
        if (isFull()) {
            return 0;
        }

        lock.lock();
        try {
            short free = 0;
            for (short i = 1; i <= slotLimit; i++) {
                if (!inventory.containsKey(i)) {
                    free++;
                }
            }
            return free;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查物品列表中是否存在拾取受限物品数量>1的情况
     * 受限物品不允许堆叠，用于快速否定
     */
    private static boolean checkItemRestricted(List<Pair<Item, InventoryType>> items) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (Pair<Item, InventoryType> p : items) {
            int itemid = p.getLeft().getItemId();
            if (ii.isPickupRestricted(itemid) && p.getLeft().getQuantity() > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查角色背包是否有空间容纳单个物品
     */
    public static boolean checkSpot(Character chr, Item item) {
        return checkSpot(chr, Collections.singletonList(item));
    }

    /**
     * 检查角色背包是否有空间容纳物品列表
     */
    public static boolean checkSpot(Character chr, List<Item> items) {
        List<Pair<Item, InventoryType>> listItems = new LinkedList<>();
        for (Item item : items) {
            listItems.add(new Pair<>(item, item.getInventoryType()));
        }

        return checkSpotsAndOwnership(chr, listItems);
    }

    /**
     * 检查角色背包是否有空间容纳物品列表（忽略归属者）
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpots(chr, items, false);
    }

    /**
     * 检查角色背包是否有空间容纳物品列表（指定零值槽位用量表）
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        int invTypesSize = InventoryType.values().length;
        List<Integer> zeroedList = new ArrayList<>(invTypesSize);
        for (byte i = 0; i < invTypesSize; i++) {
            zeroedList.add(0);
        }

        return checkSpots(chr, items, zeroedList, useProofInv);
    }

    /**
     * 核心空格检查：按物品ID+归属者分组，逐类逐项渐进检查空间
     * 排除 UNDEFINED/EQUIPPED 类型，所有计数 >= 0
     *
     * @param chr           角色对象
     * @param items         物品-背包类型配对列表
     * @param typesSlotsUsed 各类背包已占用槽位数
     * @param useProofInv   是否使用临时容器空间
     * @return true=有足够空间
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        if (!checkItemRestricted(items)) {
            return false;
        }

        Map<Integer, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Integer, Byte> rcvTypes = new LinkedHashMap<>();

        // 按物品ID分组，合并同ID非装备/非充能物品的数量
        for (Pair<Item, InventoryType> item : items) {
            Integer itemId = item.left.getItemId();
            List<Integer> qty = rcvItems.get(itemId);

            if (qty == null) {
                List<Integer> itemQtyList = new LinkedList<>();
                itemQtyList.add((int) item.left.getQuantity());

                rcvItems.put(itemId, itemQtyList);
                rcvTypes.put(itemId, item.right.getType());
            } else {
                if (!ItemConstants.isEquipment(itemId) && !ItemConstants.isRechargeable(itemId)) {
                    qty.set(0, qty.get(0) + item.left.getQuantity());
                } else {
                    qty.add((int) item.left.getQuantity());
                }
            }
        }

        Client c = chr.getClient();
        for (Entry<Integer, List<Integer>> it : rcvItems.entrySet()) {
            int itemType = rcvTypes.get(it.getKey()) - 1;

            for (Integer itValue : it.getValue()) {
                int usedSlots = typesSlotsUsed.get(itemType);

                int result = InventoryManipulator.checkSpaceProgressively(c, it.getKey(), itValue, "", usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    /**
     * FNV-1a 32位哈希算法
     * 用于物品归属者字符串哈希
     */
    private static long fnvHash32(final String k) {
        final int FNV_32_INIT = 0x811c9dc5;
        final int FNV_32_PRIME = 0x01000193;

        int rv = FNV_32_INIT;
        final int len = k.length();
        for (int i = 0; i < len; i++) {
            rv ^= k.charAt(i);
            rv *= FNV_32_PRIME;
        }

        return rv >= 0 ? rv : (2L * Integer.MAX_VALUE) + rv;
    }

    /**
     * 生成物品+归属者的联合哈希key
     * 高32位为itemId，低32位为owner的FNV哈希
     */
    private static Long hashKey(Integer itemId, String owner) {
        return (itemId.longValue() << 32L) + fnvHash32(owner);
    }

    /**
     * 检查角色背包是否有空间容纳物品列表（考虑归属者匹配）
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpotsAndOwnership(chr, items, false);
    }

    /**
     * 检查角色背包是否有空间（显式指定零值槽位表）
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        List<Integer> zeroedList = new ArrayList<>(5);
        for (byte i = 0; i < 5; i++) {
            zeroedList.add(0);
        }

        return checkSpotsAndOwnership(chr, items, zeroedList, useProofInv);
    }

    /**
     * 核心空格检查（含归属者匹配）
     * 按物品ID+归属者分组，确保相同归属者的物品优先堆叠到已有物品上
     *
     * @param chr            角色对象
     * @param items          物品-背包类型配对列表
     * @param typesSlotsUsed 各类背包已占用槽位数
     * @param useProofInv    是否使用临时容器空间
     * @return true=有足够空间
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        if (!checkItemRestricted(items)) {
            return false;
        }

        // 按物品ID+归属者的联合哈希分组
        Map<Long, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Long, Byte> rcvTypes = new LinkedHashMap<>();
        Map<Long, String> rcvOwners = new LinkedHashMap<>();

        for (Pair<Item, InventoryType> item : items) {
            Long itemHash = hashKey(item.left.getItemId(), item.left.getOwner());
            List<Integer> qty = rcvItems.get(itemHash);

            if (qty == null) {
                List<Integer> itemQtyList = new LinkedList<>();
                itemQtyList.add((int) item.left.getQuantity());

                rcvItems.put(itemHash, itemQtyList);
                rcvTypes.put(itemHash, item.right.getType());
                rcvOwners.put(itemHash, item.left.getOwner());
            } else {
                // 非装备且非充能物品合并数量（同ID同归属者可堆叠）
                if (!ItemConstants.isEquipment(item.left.getItemId()) && !ItemConstants.isRechargeable(item.left.getItemId())) {
                    qty.set(0, qty.get(0) + item.left.getQuantity());
                } else {
                    qty.add((int) item.left.getQuantity());
                }
            }
        }

        Client c = chr.getClient();
        for (Entry<Long, List<Integer>> it : rcvItems.entrySet()) {
            int itemType = rcvTypes.get(it.getKey()) - 1;
            int itemId = (int) (it.getKey() >> 32L);

            for (Integer itValue : it.getValue()) {
                int usedSlots = typesSlotsUsed.get(itemType);

                int result = InventoryManipulator.checkSpaceProgressively(c, itemId, itValue, rcvOwners.get(it.getKey()), usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    /**
     * 获取背包类型
     */
    public InventoryType getType() {
        return type;
    }

    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(list()).iterator();
    }

    /**
     * 按现金物品ID查找（匹配petId、ringId或cashId）
     */
    public Item findByCashId(int cashId) {
        boolean isRing = false;
        Equip equip = null;
        for (Item item : list()) {
            if (item.getInventoryType().equals(InventoryType.EQUIP)) {
                equip = (Equip) item;
                isRing = equip.getRingId() > -1;
            }
            if ((item.getPetId() > -1 ? item.getPetId() : isRing ? equip.getRingId() : item.getCashId()) == cashId) {
                return item;
            }
        }

        return null;
    }

    /**
     * 背包容量是否已校验
     */
    public boolean checked() {
        lock.lock();
        try {
            return checked;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置背包容量校验标志
     */
    public void checked(boolean yes) {
        lock.lock();
        try {
            checked = yes;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 手动获取背包锁（用于跨背包操作的原子性保证）
     */
    public void lockInventory() {
        lock.lock();
    }

    /**
     * 手动释放背包锁
     */
    public void unlockInventory() {
        lock.unlock();
    }

    /**
     * 清理资源，断开角色引用
     */
    public void dispose() {
        owner = null;
    }
}