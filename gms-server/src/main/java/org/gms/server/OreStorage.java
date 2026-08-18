package org.gms.server;

import org.gms.client.Client;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.constants.inventory.ItemConstants;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 收纳背包（矿石/卷轴/椅子/坐骑）数据层。每角色独立，物品存 inventoryitems type 10-13。
 */
public class OreStorage {
    private static final Logger log = LoggerFactory.getLogger(OreStorage.class);

    private static final String[] KIND_NAME = {"ore", "scroll", "chair", "mount"};
    private static final int BAG_SLOTS = 200;

    private static ItemFactory factoryFor(int kind) {
        return switch (kind) {
            case 1 -> ItemFactory.SCROLLBAG;
            case 2 -> ItemFactory.CHAIRBAG;
            case 3 -> ItemFactory.MOUNTBAG;
            default -> ItemFactory.OREBAG;
        };
    }

    private final int kind;
    private final ItemFactory factory;
    private final int id;
    private int slots;
    private final Map<InventoryType, List<Item>> typeItems = new HashMap<>();
    private List<Item> items = new LinkedList<>();
    private final Lock lock = new ReentrantLock(true);

    private OreStorage(int kind, int characterId, int slots) {
        this.kind = kind;
        this.factory = factoryFor(kind);
        this.id = characterId;
        this.slots = slots;
    }

    public static OreStorage loadOreStorage(int characterId) {
        return loadFromDB(0, characterId);
    }

    public static OreStorage loadScrollStorage(int characterId) {
        return loadFromDB(1, characterId);
    }

    public static OreStorage loadChairStorage(int characterId) {
        return loadFromDB(2, characterId);
    }

    public static OreStorage loadMountStorage(int characterId) {
        return loadFromDB(3, characterId);
    }

    private static OreStorage loadFromDB(int kind, int characterId) {
        OreStorage ret = new OreStorage(kind, characterId, BAG_SLOTS);
        try {
            for (Pair<Item, InventoryType> item : ret.factory.loadItems(characterId, false)) {
                ret.placeOnLoad(item.getLeft());
            }
        } catch (SQLException ex) {
            log.error("SQL error loading {} bag for characterId {}", KIND_NAME[kind], characterId, ex);
            throw new RuntimeException(ex);
        }
        return ret;
    }

    public void saveToDB(Connection con) throws SQLException {
        List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();
        for (Item item : getItems()) {
            itemsWithType.add(new Pair<>(item, item.getInventoryType()));
        }
        factory.saveItems(itemsWithType, id, con);
    }

    public Item getItemAtSlot(int slot) {
        lock.lock();
        try {
            return itemAtSlot(slot);
        } finally {
            lock.unlock();
        }
    }

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

    private int firstFreeSlot() {
        boolean[] used = new boolean[slots];
        for (Item it : items) {
            int p = it.getPosition();
            if (p >= 0 && p < slots) {
                used[p] = true;
            }
        }
        for (int i = 0; i < slots; i++) {
            if (!used[i]) {
                return i;
            }
        }
        return -1;
    }

    private Item itemAtSlot(int slot) {
        for (Item it : items) {
            if (it.getPosition() == slot) {
                return it;
            }
        }
        return null;
    }

    private void placeOnLoad(Item item) {
        int p = item.getPosition();
        if (p < 0 || p >= slots || itemAtSlot(p) != null) {
            p = firstFreeSlot();
            if (p < 0) {
                return;
            }
        }
        item.setPosition((short) p);
        items.add(item);
    }

    public boolean storeAt(Item item, int slot, Client c) {
        lock.lock();
        try {
            if (item == null) {
                return false;
            }
            if (slot < 0 || slot >= slots) {
                return storeMerge(item, c);
            }
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            int itemId = item.getItemId();
            boolean stackable = item.getInventoryType() != InventoryType.EQUIP
                    && !ItemConstants.isRechargeable(itemId)
                    && !ii.isPickupRestricted(itemId);
            Item target = itemAtSlot(slot);
            if (target == null) {
                item.setPosition((short) slot);
                items.add(item);
                return true;
            }
            if (stackable && target.getItemId() == itemId && Objects.equals(target.getOwner(), item.getOwner())) {
                short slotMax = ii.getSlotMax(c, itemId);
                int room = slotMax - target.getQuantity();
                if (room >= item.getQuantity()) {
                    target.setQuantity((short) (target.getQuantity() + item.getQuantity()));
                    return true;
                }
                if (room > 0) {
                    target.setQuantity(slotMax);
                    item.setQuantity((short) (item.getQuantity() - room));
                }
                return storeMerge(item, c);
            }
            return storeMerge(item, c);
        } finally {
            lock.unlock();
        }
    }

    public boolean move(int src, int dst, Client c) {
        lock.lock();
        try {
            if (src == dst) {
                return true;
            }
            if (src < 0 || src >= slots || dst < 0 || dst >= slots) {
                return false;
            }
            Item source = itemAtSlot(src);
            if (source == null) {
                return false;
            }
            Item target = itemAtSlot(dst);
            if (target == null) {
                source.setPosition((short) dst);
                return true;
            }
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            int itemId = source.getItemId();
            boolean stackable = source.getInventoryType() != InventoryType.EQUIP
                    && !ItemConstants.isRechargeable(itemId)
                    && !ii.isPickupRestricted(itemId);
            if (stackable && target.getItemId() == itemId && Objects.equals(target.getOwner(), source.getOwner())) {
                short slotMax = ii.getSlotMax(c, itemId);
                int total = source.getQuantity() + target.getQuantity();
                if (total > slotMax) {
                    target.setQuantity(slotMax);
                    source.setQuantity((short) (total - slotMax));
                } else {
                    target.setQuantity((short) total);
                    items.remove(source);
                }
                return true;
            }
            source.setPosition((short) dst);
            target.setPosition((short) src);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean storeMerge(Item item, Client c) {
        lock.lock();
        try {
            if (item == null) {
                return false;
            }
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            int itemId = item.getItemId();
            boolean stackable = item.getInventoryType() != InventoryType.EQUIP
                    && !ItemConstants.isRechargeable(itemId)
                    && !ii.isPickupRestricted(itemId);

            if (stackable) {
                short slotMax = ii.getSlotMax(c, itemId);
                int existingRoom = 0;
                for (Item ex : items) {
                    if (ex.getItemId() == itemId && ex.getQuantity() < slotMax
                            && Objects.equals(ex.getOwner(), item.getOwner())) {
                        existingRoom += slotMax - ex.getQuantity();
                    }
                }
                if (isFull() && existingRoom < item.getQuantity()) {
                    return false;
                }
                for (Item ex : items) {
                    if (item.getQuantity() <= 0) {
                        break;
                    }
                    if (ex.getItemId() != itemId || ex.getQuantity() >= slotMax
                            || !Objects.equals(ex.getOwner(), item.getOwner())) {
                        continue;
                    }
                    int move = Math.min(slotMax - ex.getQuantity(), item.getQuantity());
                    ex.setQuantity((short) (ex.getQuantity() + move));
                    item.setQuantity((short) (item.getQuantity() - move));
                }
                if (item.getQuantity() <= 0) {
                    typeItems.put(item.getInventoryType(), new ArrayList<>(filterItems(item.getInventoryType())));
                    return true;
                }
            }

            int slot = firstFreeSlot();
            if (slot < 0) {
                return false;
            }
            item.setPosition((short) slot);
            items.add(item);
            typeItems.put(item.getInventoryType(), new ArrayList<>(filterItems(item.getInventoryType())));
            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<Item> getItems() {
        lock.lock();
        try {
            return new ArrayList<>(items);
        } finally {
            lock.unlock();
        }
    }

    private List<Item> filterItems(InventoryType type) {
        List<Item> ret = new LinkedList<>();
        for (Item item : getItems()) {
            if (item.getInventoryType() == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    public void mergeStacks(Client c) {
        lock.lock();
        try {
            StorageInventory msi = new StorageInventory(c, items);
            msi.mergeItems();
            List<Item> merged = msi.sortItems();
            if (merged.isEmpty() && !items.isEmpty()) {
                log.error("[OreStorage] mergeStacks emptied a non-empty {} bag (characterId {}, {} items) - aborting",
                        KIND_NAME[kind], id, items.size());
                return;
            }
            items = merged;
            short pos = 0;
            for (Item it : items) {
                it.setPosition(pos++);
            }
            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(items));
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return items.size() >= slots;
        } finally {
            lock.unlock();
        }
    }
}
