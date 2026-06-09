/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Item;
import org.gms.config.GameConfig;
import org.gms.constants.inventory.ItemConstants;

import java.util.*;

/**
 * 仓库库存
 * 管理仓库物品的排序、合并和移动逻辑
 * 按装备/现金和新品/消耗品分类，支持物品堆叠和自动合并
 *
 * @author RonanLana
 */
public class StorageInventory {
    /** 所属客户端 */
    private final Client c;
    /** 仓库物品（槽位ID -> 物品） */
    private Map<Short, Item> inventory = new LinkedHashMap<>();
    /** 槽位上限 */
    private final byte slotLimit;

    /**
     * 构造函数
     *
     * @param c      客户端
     * @param toSort 待排序的物品列表
     */
    public StorageInventory(Client c, List<Item> toSort) {
        this.inventory = new LinkedHashMap<>();
        this.slotLimit = (byte) toSort.size();
        this.c = c;

        for (Item item : toSort) {
            this.addItem(item);
        }
    }

    /**
     * 获取槽位上限
     *
     * @return 槽位上限
     */
    private byte getSlotLimit() {
        return slotLimit;
    }

    /**
     * 获取物品列表（不可修改）
     *
     * @return 物品集合
     */
    private Collection<Item> list() {
        return Collections.unmodifiableCollection(inventory.values());
    }

    /**
     * 添加物品到下一个可用槽位
     *
     * @param item 物品
     * @return 槽位ID，失败返回-1
     */
    private short addItem(Item item) {
        short slotId = getNextFreeSlot();
        if (slotId < 0 || item == null) {
            return -1;
        }
        addSlot(slotId, item);
        item.setPosition(slotId);
        return slotId;
    }

    /**
     * 判断物品是否为装备或现金物品
     *
     * @param item 物品
     * @return 是装备/现金返回true
     */
    private static boolean isEquipOrCash(Item item) {
        int type = item.getItemId() / 1000000;
        return type == 1 || type == 5;
    }

    /**
     * 判断两物品所有权人是否相同
     *
     * @param source 源物品
     * @param target 目标物品
     * @return 相同返回true
     */
    private static boolean isSameOwner(Item source, Item target) {
        return source.getOwner().equals(target.getOwner());
    }

    /**
     * 移动物品：支持交换、堆叠和合并
     *
     * @param sSlot   源槽位
     * @param dSlot   目标槽位
     * @param slotMax 槽位最大堆叠数
     */
    private void move(short sSlot, short dSlot, short slotMax) {
        Item source = inventory.get(sSlot);
        Item target = inventory.get(dSlot);
        if (source == null) {
            return;
        }
        if (target == null) {
            source.setPosition(dSlot);
            inventory.put(dSlot, source);
            inventory.remove(sSlot);
        } else if (target.getItemId() == source.getItemId() && !ItemConstants.isRechargeable(source.getItemId()) && !ItemInformationProvider.getInstance().isPickupRestricted(source.getItemId()) && isSameOwner(source, target)) {
            if (isEquipOrCash(source)) {
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
    }

    /**
     * 移动物品到目标槽位
     *
     * @param src 源槽位
     * @param dst 目标槽位
     */
    private void moveItem(short src, short dst) {
        if (src < 0 || dst < 0) {
            return;
        }
        if (dst > this.getSlotLimit()) {
            return;
        }

        Item source = this.getItem(src);
        if (source == null) {
            return;
        }
        short slotMax = ItemInformationProvider.getInstance().getSlotMax(c, source.getItemId());
        this.move(src, dst, slotMax);
    }

    /**
     * 交换两个物品的槽位
     *
     * @param source 物品A
     * @param target 物品B
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
     * @param slot 槽位
     * @return 物品
     */
    private Item getItem(short slot) {
        return inventory.get(slot);
    }

    /**
     * 添加物品到指定槽位
     *
     * @param slot 槽位
     * @param item 物品
     */
    private void addSlot(short slot, Item item) {
        inventory.put(slot, item);
    }

    /**
     * 移除指定槽位的物品
     *
     * @param slot 槽位
     */
    private void removeSlot(short slot) {
        inventory.remove(slot);
    }

    /**
     * 检查仓库是否已满
     *
     * @return 已满返回true
     */
    private boolean isFull() {
        return inventory.size() >= slotLimit;
    }

    /**
     * 获取下一个可用槽位
     *
     * @return 槽位ID，已满返回-1
     */
    private short getNextFreeSlot() {
        if (isFull()) {
            return -1;
        }

        for (short i = 1; i <= slotLimit; i++) {
            if (!inventory.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 合并物品（堆叠相同物品）
     */
    public void mergeItems() {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item srcItem, dstItem;

        for (short dst = 1; dst <= this.getSlotLimit(); dst++) {
            dstItem = this.getItem(dst);
            if (dstItem == null) {
                continue;
            }

            for (short src = (short) (dst + 1); src <= this.getSlotLimit(); src++) {
                srcItem = this.getItem(src);
                if (srcItem == null) {
                    continue;
                }

                if (dstItem.getItemId() != srcItem.getItemId()) {
                    continue;
                }
                if (dstItem.getQuantity() == ii.getSlotMax(c, this.getItem(dst).getItemId())) {
                    break;
                }

                moveItem(src, dst);
            }
        }

        boolean sorted = false;

        while (!sorted) {
            short freeSlot = this.getNextFreeSlot();

            if (freeSlot != -1) {
                short itemSlot = -1;
                for (short i = (short) (freeSlot + 1); i <= this.getSlotLimit(); i = (short) (i + 1)) {
                    if (this.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    moveItem(itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
    }

    /**
     * 排序物品并返回排序后的列表
     *
     * @return 排序后的物品列表
     */
    public List<Item> sortItems() {
        ArrayList<Item> itemarray = new ArrayList<>();

        for (short i = 1; i <= this.getSlotLimit(); i++) {
            Item item = this.getItem(i);
            if (item != null) {
                itemarray.add(item.copy());
            }
        }

        for (Item item : itemarray) {
            this.removeSlot(item.getPosition());
        }

        int invTypeCriteria = 1;
        int sortCriteria = GameConfig.getServerBoolean("use_item_sort_by_name") ? 2 : 0;
        PairedQuicksort pq = new PairedQuicksort(itemarray, sortCriteria, invTypeCriteria);

        inventory.clear();
        return itemarray;
    }
}

/**
 * 配对快速排序
 * 支持主排序和次排序两级排序
 * 排序方式：0=物品ID, 1=数量, 2=名称, 3=等级
 */
class PairedQuicksort {
    /** 左指针 */
    private int i = 0;
    /** 右指针 */
    private int j = 0;
    /** 交集索引列表 */
    private final ArrayList<Integer> intersect;
    /** 物品信息提供者实例 */
    ItemInformationProvider ii = ItemInformationProvider.getInstance();

    /**
     * 按物品ID分区
     */
    private void PartitionByItemId(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;

        x = A.get((i + j) / 2);
        do {
            while (x.getItemId() > A.get(i).getItemId()) {
                i++;
            }
            while (x.getItemId() < A.get(j).getItemId()) {
                j--;
            }

            if (i <= j) {
                w = A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }

    /**
     * 按物品名称分区
     */
    private void PartitionByName(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;

        x = A.get((i + j) / 2);
        do {
            while (ii.getName(x.getItemId()).compareTo(ii.getName(A.get(i).getItemId())) > 0) {
                i++;
            }
            while (ii.getName(x.getItemId()).compareTo(ii.getName(A.get(j).getItemId())) < 0) {
                j--;
            }

            if (i <= j) {
                w = A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }

    /**
     * 按物品数量分区
     */
    private void PartitionByQuantity(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;

        x = A.get((i + j) / 2);
        do {
            while (x.getQuantity() > A.get(i).getQuantity()) {
                i++;
            }
            while (x.getQuantity() < A.get(j).getQuantity()) {
                j--;
            }

            if (i <= j) {
                w = A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }

    /**
     * 按装备等级分区
     */
    private void PartitionByLevel(int Esq, int Dir, ArrayList<Item> A) {
        Equip x, w, eqpI, eqpJ;

        i = Esq;
        j = Dir;

        x = (Equip) (A.get((i + j) / 2));

        do {
            eqpI = (Equip) A.get(i);
            eqpJ = (Equip) A.get(j);

            while (x.getLevel() > eqpI.getLevel()) {
                i++;
            }
            while (x.getLevel() < eqpJ.getLevel()) {
                j--;
            }

            if (i <= j) {
                w = (Equip) A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }

    /**
     * 快速排序（递归）
     *
     * @param Esq  左边界
     * @param Dir  右边界
     * @param A    物品数组
     * @param sort 排序方式
     */
    void MapleQuicksort(int Esq, int Dir, ArrayList<Item> A, int sort) {
        switch (sort) {
            case 3:
                PartitionByLevel(Esq, Dir, A);
                break;

            case 2:
                PartitionByName(Esq, Dir, A);
                break;

            case 1:
                PartitionByQuantity(Esq, Dir, A);
                break;

            default:
                PartitionByItemId(Esq, Dir, A);
        }


        if (Esq < j) {
            MapleQuicksort(Esq, j, A, sort);
        }
        if (i < Dir) {
            MapleQuicksort(i, Dir, A, sort);
        }
    }

    /**
     * 构造函数：执行两级快速排序
     *
     * @param A             物品数组
     * @param primarySort   主排序方式
     * @param secondarySort 次排序方式（在相同ID组内排序）
     */
    public PairedQuicksort(ArrayList<Item> A, int primarySort, int secondarySort) {
        intersect = new ArrayList<>();

        if (A.size() > 0) {
            MapleQuicksort(0, A.size() - 1, A, primarySort);
        }

        intersect.add(0);
        for (int ind = 1; ind < A.size(); ind++) {
            if (A.get(ind - 1).getItemId() != A.get(ind).getItemId()) {
                intersect.add(ind);
            }
        }
        intersect.add(A.size());

        for (int ind = 0; ind < intersect.size() - 1; ind++) {
            if (intersect.get(ind + 1) > intersect.get(ind)) {
                MapleQuicksort(intersect.get(ind), intersect.get(ind + 1) - 1, A, secondarySort);
            }
        }
    }
}