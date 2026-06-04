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
 * 【类型】Inventory（class），包 `org.gms.client.inventory`。
 *
 * 背包系统的核心类，代表角色的一个背包（装备栏、消耗栏、物品栏、特殊栏、现金栏等）。
 * 使用 {@link LinkedHashMap} 维护物品按槽位顺序，支持线程安全的增删改查操作。
 *
 * 背包类型由 {@link InventoryType} 枚举定义：
 * <ul>
 *   <li>EQUIP (1)：装备背包</li>
 *   <li>USE (2)：消耗品背包</li>
 *   <li>SETUP (3)：设置物品背包（椅子、特效等）</li>
 *   <li>ETC (4)：其他物品背包（任务物品、矿物等）</li>
 *   <li>CASH (5)：现金栏背包（商城道具、宠物等）</li>
 *   <li>EQUIPPED (-1)：已装备栏（虚拟背包，实际装备中的物品）</li>
 * </ul>
 *
 * 物品操作委托给 {@link InventoryManipulator} 执行，它会同时处理数据库持久化和客户端封包通知。
 *
 * @author Matze, Ronan
 */
public class Inventory implements Iterable<Item> {
    private static final Logger log = LoggerFactory.getLogger(Inventory.class);
    /** 物品存储：key=槽位号(short), value=物品实例，LinkedHashMap 保证插入顺序 */
    protected final Map<Short, Item> inventory;
    /** 背包类型：定义背包的种类（装备栏、消耗栏等） */
    protected final InventoryType type;
    /** 背包操作锁：用于确保多线程环境下的数据一致性 */
    protected final Lock lock = new ReentrantLock(true);

    /** 背包所属角色：该背包关联的角色对象 */
    protected Character owner;
    /** 该背包的最大槽位数：表示背包最多能容纳的物品数量 */
    protected byte slotLimit;
    /** 是否已完成首次校验：标记背包是否已经过初始化校验 */
    protected boolean checked = false;

    /**
     * 构造函数：创建背包实例。
     * 
     * @param mc 背包所属的角色
     * @param type 背包类型
     * @param slotLimit 背包的最大槽位数
     */
    public Inventory(Character mc, InventoryType type, byte slotLimit) {
        this.owner = mc;
        this.inventory = new LinkedHashMap<>();
        this.type = type;
        this.slotLimit = slotLimit;
    }

    /**
     * 检查背包是否可以扩展。
     * 
     * <p>某些类型的背包不能扩展，包括未定义、已装备和现金栏。</p>
     * 
     * @return 如果可以扩展则返回true，否则返回false
     */
    public boolean isExtendableInventory() { // not sure about cash, basing this on the previous one.
        return !(type.equals(InventoryType.UNDEFINED) || type.equals(InventoryType.EQUIPPED) || type.equals(InventoryType.CASH));
    }

    /**
     * 检查是否为装备背包。
     * 
     * @return 如果是装备背包或已装备栏则返回true，否则返回false
     */
    public boolean isEquipInventory() {
        return type.equals(InventoryType.EQUIP) || type.equals(InventoryType.EQUIPPED);
    }

    /**
     * 获取背包的最大槽位数。
     * 
     * @return 最大槽位数
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
     * 设置背包的最大槽位数。
     * 
     * <p>如果新限制小于当前限制，则移除超出新限制的物品槽位。</p>
     * 
     * @param newLimit 新的最大槽位数
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
     * 获取背包中所有物品的不可修改集合。
     * 
     * @return 包含所有物品的不可修改集合
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
     * 根据物品ID查找物品。
     * 
     * @param itemId 物品ID
     * @return 找到的物品，如果不存在则返回null
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
     * 根据物品名称查找物品。
     * 
     * @param name 物品名称（不区分大小写）
     * @return 找到的物品，如果不存在则返回null
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
     * 计算指定ID物品的总数量。
     * 
     * @param itemId 物品ID
     * @return 该物品的总数量
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
     * 计算指定ID且未拥有者的物品总数量。
     * 
     * @param itemId 物品ID
     * @return 该物品中未设定拥有者的总数量
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
     * 向背包中添加物品。
     * 
     * @param item 要添加的物品
     * @return 成功时返回物品槽位号，失败时返回-1
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
     * 从数据库添加物品到背包。
     * 
     * <p>主要用于加载角色数据时添加物品到背包。</p>
     * 
     * @param item 要添加的物品
     */
    public void addItemFromDB(Item item) {
        if (item.getPosition() < 0 && !type.equals(InventoryType.EQUIPPED)) {
            return;
        }
        addSlotFromDB(item.getPosition(), item);
    }

    /**
     * 检查两个物品是否具有相同的拥有者。
     * 
     * @param source 源物品
     * @param target 目标物品
     * @return 如果拥有者相同则返回true，否则返回false
     */
    private static boolean isSameOwner(Item source, Item target) {
        return source.getOwner().equals(target.getOwner());
    }

    /**
     * 移动物品从一个槽位到另一个槽位。
     * 
     * <p>处理物品移动、合并和交换等操作，根据物品类型和数量执行相应逻辑。</p>
     * 
     * @param sSlot 源槽位
     * @param dSlot 目标槽位
     * @param slotMax 槽位最大数量限制
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
                source.setPosition(dSlot);
                inventory.put(dSlot, source);
                inventory.remove(sSlot);
            } else if (target.getItemId() == source.getItemId() && !ItemConstants.isRechargeable(source.getItemId()) && isSameOwner(source, target)) {
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
                swap(target, source);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 交换两个物品的槽位位置。
     * 
     * @param source 源物品
     * @param target 目标物品
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
     * 根据槽位号获取物品。
     * 
     * @param slot 槽位号
     * @return 指定槽位的物品，如果不存在则返回null
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
     * 移除指定槽位的物品（默认移除1个）。
     * 
     * @param slot 槽位号
     */
    public void removeItem(short slot) {
        removeItem(slot, (short) 1, false);
    }

    /**
     * 移除指定槽位的物品。
     * 
     * @param slot 槽位号
     * @param quantity 要移除的数量
     * @param allowZero 是否允许物品数量为0（保留物品但数量为0）
     */
    public void removeItem(short slot, short quantity, boolean allowZero) {
        Item item = getItem(slot);
        if (item == null) {// TODO is it ok not to throw an exception here?
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
     * 添加物品到下一个可用槽位。
     * 
     * @param item 要添加的物品
     * @return 成功时返回槽位号，失败时返回-1
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

        if (ItemConstants.isRateCoupon(item.getItemId())) {
            // deadlocks with coupons rates found thanks to GabrielSin & Masterrulax
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }

        return slotId;
    }

    /**
     * 从数据库添加物品到指定槽位。
     * 
     * @param slot 槽位号
     * @param item 要添加的物品
     */
    protected void addSlotFromDB(short slot, Item item) {
        lock.lock();
        try {
            inventory.put(slot, item);
        } finally {
            lock.unlock();
        }

        if (ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 从背包中移除指定槽位的物品。
     * 
     * @param slot 槽位号
     */
    public void removeSlot(short slot) {
        Item item;
        lock.lock();
        try {
            item = inventory.remove(slot);
        } finally {
            lock.unlock();
        }

        if (item != null && ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 检查背包是否已满。
     * 
     * @return 如果背包已满则返回true，否则返回false
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
     * 检查背包在考虑额外项目的情况下是否已满。
     * 
     * @param margin 额外考虑的项目数量
     * @return 如果背包在添加额外项目后将满则返回true，否则返回false
     */
    public boolean isFull(int margin) {
        lock.lock();
        try {
            //System.out.print("(" + inventory.size() + " " + margin + " <> " + slotLimit + ")");
            return inventory.size() + margin >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查背包在考虑额外项目和已使用槽位的情况下是否已满。
     * 
     * @param margin 额外考虑的项目数量
     * @param used 已使用的槽位数量
     * @return 如果背包在添加额外项目后将满则返回true，否则返回false
     */
    public boolean isFullAfterSomeItems(int margin, int used) {
        lock.lock();
        try {
            //System.out.print("(" + inventory.size() + " " + margin + " <> " + slotLimit + " -" + used + ")");
            return inventory.size() + margin >= slotLimit - used;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取下一个可用的槽位号。
     * 
     * @return 可用槽位号，如果没有可用槽位则返回-1
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
     * 获取可用槽位的数量。
     * 
     * @return 可用槽位的数量
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

    private static boolean checkItemRestricted(List<Pair<Item, InventoryType>> items) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        // thanks Shavit for noticing set creation that would be only effective in rare situations
        for (Pair<Item, InventoryType> p : items) {
            int itemid = p.getLeft().getItemId();
            if (ii.isPickupRestricted(itemid) && p.getLeft().getQuantity() > 1) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkSpot(Character chr, Item item) {    // thanks Vcoc for noticing pshops not checking item stacks when taking item back
        return checkSpot(chr, Collections.singletonList(item));
    }

    public static boolean checkSpot(Character chr, List<Item> items) {
        List<Pair<Item, InventoryType>> listItems = new LinkedList<>();
        for (Item item : items) {
            listItems.add(new Pair<>(item, item.getInventoryType()));
        }

        return checkSpotsAndOwnership(chr, listItems);
    }

    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpots(chr, items, false);
    }

    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        int invTypesSize = InventoryType.values().length;
        List<Integer> zeroedList = new ArrayList<>(invTypesSize);
        for (byte i = 0; i < invTypesSize; i++) {
            zeroedList.add(0);
        }

        return checkSpots(chr, items, zeroedList, useProofInv);
    }

    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        // assumption: no "UNDEFINED" or "EQUIPPED" items shall be tested here, all counts are >= 0.

        if (!checkItemRestricted(items)) {
            return false;
        }

        Map<Integer, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Integer, Byte> rcvTypes = new LinkedHashMap<>();

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

    private static Long hashKey(Integer itemId, String owner) {
        return (itemId.longValue() << 32L) + fnvHash32(owner);
    }

    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpotsAndOwnership(chr, items, false);
    }

    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        List<Integer> zeroedList = new ArrayList<>(5);
        for (byte i = 0; i < 5; i++) {
            zeroedList.add(0);
        }

        return checkSpotsAndOwnership(chr, items, zeroedList, useProofInv);
    }

    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        //assumption: no "UNDEFINED" or "EQUIPPED" items shall be tested here, all counts are >= 0 and item list to be checked is a legal one.

        if (!checkItemRestricted(items)) {
            return false;
        }

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
                // thanks BHB88 for pointing out an issue with rechargeable items being stacked on inventory check
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

                //System.out.print("inserting " + itemId.intValue() + " with type " + itemType + " qty " + it.getValue() + " owner '" + rcvOwners.get(it.getKey()) + "' current usedSlots:");
                //for(Integer i : typesSlotsUsed) System.out.print(" " + i);
                int result = InventoryManipulator.checkSpaceProgressively(c, itemId, itValue, rcvOwners.get(it.getKey()), usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);
                //System.out.print(" -> hasSpace: " + hasSpace + " RESULT : " + result + "\n");

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    /**
     * 获取背包类型。
     * 
     * @return 背包类型
     */
    public InventoryType getType() {
        return type;
    }

    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(list()).iterator();
    }

    /**
     * 根据现金ID查找物品。
     * 
     * <p>在装备、宠物等物品中搜索匹配的现金ID。</p>
     * 
     * @param cashId 现金ID
     * @return 找到的物品，如果不存在则返回null
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
     * 检查背包是否已完成校验。
     * 
     * @return 如果已校验则返回true，否则返回false
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
     * 设置背包校验状态。
     * 
     * @param yes 是否已校验
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
     * 锁定背包。
     * 
     * <p>防止其他线程同时访问背包数据。</p>
     */
    public void lockInventory() {
        lock.lock();
    }

    /**
     * 解锁背包。
     * 
     * <p>允许其他线程访问背包数据。</p>
     */
    public void unlockInventory() {
        lock.unlock();
    }

    /**
     * 释放背包资源。
     * 
     * <p>清除背包与角色的关联，以便垃圾回收。</p>
     */
    public void dispose() {
        owner = null;
    }
}