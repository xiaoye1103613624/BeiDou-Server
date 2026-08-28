package org.gms.server.cashshop;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.server.Server;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * 窗口版现金商城购买路径：购物车为原子操作（先校验全部，再发货，最后扣点）。
 */
public final class CashShopWindowPurchase {
    private static final Logger log = LoggerFactory.getLogger(CashShopWindowPurchase.class);

    public static final int MAX_CART = 24;

    private CashShopWindowPurchase() {
    }

    public record Result(int code, int failedSn, int delivered, int spent) {
        public boolean ok() {
            return code == CashShopWindowPackets.BUY_OK;
        }
    }

    private static Item toItem(CashShopCatalog.Row r) {
        final int itemId = r.itemId();
        int petId = -1;
        if (ItemConstants.isPet(itemId)) {
            petId = Pet.createPet(itemId);
        }

        final Item item;
        if (ItemConstants.getInventoryType(itemId) == InventoryType.EQUIP) {
            item = ItemInformationProvider.getInstance().getEquipById(itemId);
        } else {
            item = new Item(itemId, (short) 0, (short) r.count(), petId);
        }
        if (item == null) {
            return null;
        }

        // Catalog often stores period=0 for pets; WZ info/life and Commodity default
        // are 90 days. Leaving expiration at -1 makes getDriedPets() treat them as
        // dead dolls ( -1 < now ) and the client shows iconD.
        int periodDays = r.period();
        if (ItemConstants.isPet(itemId) && periodDays <= 0) {
            periodDays = 90;
        }
        if (periodDays > 0) {
            final long now = Server.getInstance().getCurrentTime();
            if (periodDays == 1) {
                switch (itemId) {
                    case ItemId.DROP_COUPON_2X_4H, ItemId.EXP_COUPON_2X_4H ->
                            item.setExpiration(now + HOURS.toMillis(4));
                    case ItemId.EXP_COUPON_3X_2H ->
                            item.setExpiration(now + HOURS.toMillis(2));
                    default -> item.setExpiration(now + DAYS.toMillis(1));
                }
            } else {
                item.setExpiration(now + DAYS.toMillis(periodDays));
            }
        }
        item.setSN(r.itemId());
        return item;
    }

    public static Result buy(Client c, Character chr, int[] sns) {
        if (sns == null || sns.length == 0 || sns.length > MAX_CART) {
            return new Result(CashShopWindowPackets.BUY_BAD_CART, 0, 0, 0);
        }
        if (!c.tryacquireClient()) {
            return new Result(CashShopWindowPackets.BUY_BUSY, 0, 0, 0);
        }
        try {
            final ItemInformationProvider ii = ItemInformationProvider.getInstance();

            final List<CashShopCatalog.Row> items = new ArrayList<>(sns.length);
            final Set<Integer> seenSn = new HashSet<>();
            for (int itemId : sns) {
                if (!seenSn.add(itemId)) {
                    return new Result(CashShopWindowPackets.BUY_BAD_CART, itemId, 0, 0);
                }
                final CashShopCatalog.Row r = CashShopCatalog.byItemId(itemId);
                if (r == null) {
                    return new Result(CashShopWindowPackets.BUY_UNKNOWN_ITEM, itemId, 0, 0);
                }
                if (ii.getSlotMax(c, r.itemId()) <= 0) {
                    return new Result(CashShopWindowPackets.BUY_UNKNOWN_ITEM, itemId, 0, 0);
                }
                items.add(r);
            }

            long total = 0;
            for (CashShopCatalog.Row r : items) {
                total += r.price();
            }
            final CashShop cs = chr.getCashShop();
            if (total > cs.getCash(CashShop.NX_CREDIT)) {
                return new Result(CashShopWindowPackets.BUY_NO_NX, 0, 0, 0);
            }

            final List<Pair<Item, InventoryType>> probes = new LinkedList<>();
            final List<Integer> uniques = new LinkedList<>();
            final Set<Integer> restricted = new HashSet<>();
            int petLines = 0;
            for (CashShopCatalog.Row r : items) {
                final int itemId = r.itemId();
                if (ii.isPickupRestricted(itemId) && !restricted.add(itemId)) {
                    return new Result(CashShopWindowPackets.BUY_BAD_CART, r.itemId(), 0, 0);
                }
                uniques.add(itemId);

                if (ItemConstants.isPet(itemId)) {
                    petLines++;
                    continue;
                }
                probes.add(new Pair<>(new Item(itemId, (short) 0, (short) r.count()),
                        ItemConstants.getInventoryType(itemId)));
            }
            if (!chr.canHoldUniques(uniques)) {
                return new Result(CashShopWindowPackets.BUY_BAD_CART, 0, 0, 0);
            }
            if (!probes.isEmpty() && !Inventory.checkSpots(chr, probes)) {
                return new Result(CashShopWindowPackets.BUY_INVENTORY_FULL, 0, 0, 0);
            }
            if (petLines > 0) {
                int cashLines = petLines;
                for (CashShopCatalog.Row r : items) {
                    final int id = r.itemId();
                    if (!ItemConstants.isPet(id)
                            && ItemConstants.getInventoryType(id) == InventoryType.CASH) {
                        cashLines++;
                    }
                }
                if (chr.getInventory(InventoryType.CASH).isFull(cashLines - 1)) {
                    return new Result(CashShopWindowPackets.BUY_INVENTORY_FULL, 0, 0, 0);
                }
            }

            int delivered = 0;
            for (CashShopCatalog.Row r : items) {
                final Item built = toItem(r);
                if (built == null) {
                    log.error("Cash Shop cart: could not build item {} for {}",
                            r.itemId(), chr.getName());
                    return new Result(CashShopWindowPackets.BUY_UNKNOWN_ITEM, r.itemId(), delivered, 0);
                }
                if (!InventoryManipulator.addFromDrop(c, built, false)) {
                    log.error("Cash Shop cart: addFromDrop refused itemId {} for {}; {} free",
                            r.itemId(), chr.getName(), delivered);
                    return new Result(CashShopWindowPackets.BUY_INVENTORY_FULL, r.itemId(), delivered, 0);
                }
                delivered++;
            }

            cs.gainCash(CashShop.NX_CREDIT, (int) -total);

            log.info("{} bought {} cash item(s) for {} NX from the Cash Shop window",
                    chr.getName(), delivered, total);
            return new Result(CashShopWindowPackets.BUY_OK, 0, delivered, (int) total);
        } catch (Exception e) {
            log.error("Cash Shop window: cart purchase failed for {}", chr.getName(), e);
            return new Result(CashShopWindowPackets.BUY_UNKNOWN_ITEM, 0, 0, 0);
        } finally {
            c.releaseClient();
        }
    }
}
