/*
    Buyback storage for items sold to NPC shops.

    Items sold to an NPC are kept here for the duration of the player's session
    (dropped on logout) so they can be bought back from the shop window's Buy Back tab.
*/
package org.gms.server.buyback;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.autoban.AutobanFactory;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SoldItemStorage {
    private static final Logger log = LoggerFactory.getLogger(SoldItemStorage.class);

    private static final int MAX_ENTRIES = 30;

    private static final SoldItemStorage instance = new SoldItemStorage();

    private final Lock lock = new ReentrantLock();
    private final Map<Integer, List<Item>> soldItems = new HashMap<>();

    public static SoldItemStorage getInstance() {
        return instance;
    }

    /** Buyback costs twice what the NPC paid when the item was sold. */
    public static int buybackPriceFor(Item item) {
        if (item == null) {
            return 0;
        }
        int sellPrice = ItemInformationProvider.getInstance().getPrice(item.getItemId(), item.getQuantity());
        if (sellPrice <= 0) {
            return 0;
        }
        long doubled = (long) sellPrice * 2L;
        return doubled > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) doubled;
    }

    public void addSoldItem(int chrId, Item item) {
        if (item == null || item.getPetId() > -1) {
            return;
        }
        // 0xFFFF is stored as signed short -1; treat it as a single unit.
        short qty = item.getQuantity();
        if (qty == (short) 0xFFFF) {
            item.setQuantity((short) 1);
            qty = 1;
        }
        if (qty <= 0) {
            return;
        }

        lock.lock();
        try {
            List<Item> items = soldItems.computeIfAbsent(chrId, k -> new ArrayList<>());
            items.add(item);
            while (items.size() > MAX_ENTRIES) {
                items.remove(0);
            }
            log.info("Buyback recorded: chrId={} itemId={} qty={} (list size={})",
                    chrId, item.getItemId(), qty, items.size());
        } finally {
            lock.unlock();
        }
    }

    public List<Item> getSoldItems(int chrId) {
        lock.lock();
        try {
            List<Item> items = soldItems.get(chrId);
            return items != null ? new ArrayList<>(items) : Collections.emptyList();
        } finally {
            lock.unlock();
        }
    }

    public void clear(int chrId) {
        lock.lock();
        try {
            soldItems.remove(chrId);
        } finally {
            lock.unlock();
        }
    }

    public void sendBuybackShop(Client c) {
        Character chr = c.getPlayer();
        if (chr == null || chr.getShop() == null) {
            return;
        }

        List<Item> items = getSoldItems(chr.getId());
        log.info("Buyback tab: sending {} item(s) to {}", items.size(), chr.getName());
        chr.setShopBuybackMode(true);
        c.sendPacket(PacketCreator.shopBuybackMode(true, !items.isEmpty()));
        c.sendPacket(PacketCreator.getBuybackShop(c, chr.getShop().getNpcId(), items));
    }

    public void sendNormalShop(Client c) {
        Character chr = c.getPlayer();
        if (chr == null || chr.getShop() == null) {
            return;
        }
        chr.getShop().sendShop(c);
    }

    public void buyBackFromShop(Client c, short slot, int itemId) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }

        List<Item> shown = getSoldItems(chr.getId());
        if (slot < 0 || slot >= shown.size() || shown.get(slot).getItemId() != itemId) {
            c.sendPacket(PacketCreator.shopTransaction((byte) 8));
            refreshBuybackShop(c);
            return;
        }

        Item bought = shown.get(slot);
        String name = ItemInformationProvider.getInstance().getName(itemId);
        int buybackPrice = buybackPriceFor(bought);

        String failure = buyBack(c, slot, itemId);
        if (failure != null) {
            chr.dropMessage(1, failure.replace("#b", "").replace("#k", ""));
            c.sendPacket(PacketCreator.shopTransaction((byte) 8));
            return;
        }

        c.sendPacket(PacketCreator.shopTransaction((byte) 0));
        chr.yellowMessage("You bought back " + describe(name, itemId, bought.getQuantity())
                + " for " + buybackPrice + " mesos.");
        refreshBuybackShop(c);
    }

    private static String describe(String name, int itemId, short quantity) {
        String label = (name == null || name.isEmpty()) ? "item " + itemId : name;
        return quantity > 1 ? label + " x" + quantity : label;
    }

    public void refreshBuybackShop(Client c) {
        Character chr = c.getPlayer();
        if (chr == null || chr.getShop() == null || !chr.isShopBuybackMode()) {
            return;
        }

        List<Item> items = getSoldItems(chr.getId());
        c.sendPacket(PacketCreator.shopBuybackMode(true, !items.isEmpty()));
        c.sendPacket(PacketCreator.getBuybackShop(c, chr.getShop().getNpcId(), items));
    }

    public void refreshBuybackTab(Client c) {
        Character chr = c.getPlayer();
        if (chr == null || chr.getShop() == null || chr.isShopBuybackMode()) {
            return;
        }
        boolean hasSoldItems = !getSoldItems(chr.getId()).isEmpty();
        c.sendPacket(PacketCreator.shopBuybackMode(false, hasSoldItems));
    }

    private static String checkTransactionState(Character chr) {
        if (!chr.isLoggedIn()) {
            return "You can't do that right now.";
        }
        if (chr.getTrade() != null) {
            return "You can't buy items back while trading.";
        }
        if (chr.getPlayerShop() != null || chr.getHiredMerchant() != null) {
            return "You can't buy items back while using a shop.";
        }
        if (chr.getMiniGame() != null) {
            return "You can't buy items back during a mini game.";
        }
        return null;
    }

    private String buyBack(Client c, int index, int expectedItemId) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return "You can't do that right now.";
        }

        String blocked = checkTransactionState(chr);
        if (blocked != null) {
            log.info("Buyback refused for {} (index {}): {}", chr.getName(), index, blocked);
            return blocked;
        }

        lock.lock();
        try {
            List<Item> items = soldItems.get(chr.getId());
            if (items == null || items.isEmpty()) {
                reportPacketEdit(chr, "requested buyback index " + index + " with an empty buyback list");
                return "You don't have any items left to buy back.";
            }
            if (index < 0 || index >= items.size()) {
                reportPacketEdit(chr, "requested out-of-range buyback index " + index
                        + " (list holds " + items.size() + ")");
                return "That item is no longer available.";
            }

            Item item = items.get(index);
            if (item.getItemId() != expectedItemId) {
                reportPacketEdit(chr, "confirmed buyback of item " + expectedItemId
                        + " but index " + index + " holds item " + item.getItemId());
                return "That item is no longer available.";
            }
            final int buybackPrice = buybackPriceFor(item);
            if (buybackPrice <= 0) {
                return "That item cannot be bought back.";
            }
            if (chr.getMeso() < buybackPrice) {
                return "You need #b" + buybackPrice + " mesos#k to buy an item back.";
            }
            if (!InventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                return "You don't have enough inventory space for that item.";
            }

            long mesoBefore = chr.getMeso();
            chr.gainMeso(-buybackPrice, false);
            long paid = mesoBefore - chr.getMeso();
            if (paid < buybackPrice) {
                if (paid > 0) {
                    chr.gainMeso(paid, false);
                }
                log.warn("Buyback for {} charged {} of {} mesos (had {}), refunded and aborted",
                        chr.getName(), paid, buybackPrice, mesoBefore);
                return "Your mesos changed mid-purchase, nothing was bought. Please try again.";
            }

            Item given = item.copy();
            if (!InventoryManipulator.addFromDrop(c, given, false)) {
                chr.gainMeso(buybackPrice, false);

                short owed = given.getQuantity();
                if (owed > 0 && owed < item.getQuantity()) {
                    log.warn("Buyback for {} partially delivered {} ({} of {} left owed), refunded {} mesos",
                            chr.getName(), item.getItemId(), owed, item.getQuantity(), buybackPrice);
                    item.setQuantity(owed);
                } else if (owed <= 0) {
                    items.remove(index);
                }
                return "You don't have enough inventory space for that item.";
            }

            items.remove(index);
            log.info("Buyback: {} bought back {} x{} for {} mesos ({} -> {})", chr.getName(),
                    item.getItemId(), item.getQuantity(), buybackPrice, mesoBefore, chr.getMeso());
            return null;
        } finally {
            lock.unlock();
        }
    }

    private static void reportPacketEdit(Character chr, String what) {
        log.warn("Buyback packet edit suspected: {} (chr {}, account {}, ip {}) {}",
                chr.getName(), chr.getId(), chr.getAccountId(), chr.getClient().getRemoteAddress(), what);
        AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " " + what + " (buyback).");
    }

    public static void reportSellMismatch(Character chr, int itemId, int expected, int removed) {
        log.warn("Buyback not recorded for {}: sold {} expecting {} removed but inventory lost {}",
                chr.getName(), itemId, expected, removed);
        if (removed <= 0) {
            AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " sold item " + itemId
                    + " that never left their inventory (buyback).");
        }
    }
}
