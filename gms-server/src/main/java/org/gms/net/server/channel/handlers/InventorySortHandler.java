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
package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.config.GameConfig;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BubblesDev
 * @author Ronan
 */

class PairedQuicksort {
    private int i = 0;
    private int j = 0;
    private final ArrayList<Integer> intersect;
    ItemInformationProvider ii = ItemInformationProvider.getInstance();

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

    private int getWatkForProjectile(Item item) {
        return ii.getWatkForProjectile(item.getItemId());
    }

    private void PartitionByProjectileAtk(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;

        x = A.get((i + j) / 2);
        do {
            int watk = getWatkForProjectile(x);
            while (watk < getWatkForProjectile(A.get(i))) {
                i++;
            }
            while (watk > getWatkForProjectile(A.get(j))) {
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

    /** Null-safe name for sort — missing String WZ must not NPE mid-整理. */
    private String safeName(int itemId) {
        String n = ii.getName(itemId);
        return n != null ? n : "";
    }

    private void PartitionByName(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;

        x = A.get((i + j) / 2);
        do {
            while (safeName(x.getItemId()).compareTo(safeName(A.get(i).getItemId())) > 0) {
                i++;
            }
            while (safeName(x.getItemId()).compareTo(safeName(A.get(j).getItemId())) < 0) {
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

    private void PartitionByLevel(int Esq, int Dir, ArrayList<Item> A) {
        // Equip tab can theoretically hold a non-Equip Item after desync; never ClassCast mid-sort.
        for (int k = Esq; k <= Dir; k++) {
            if (!(A.get(k) instanceof Equip)) {
                PartitionByItemId(Esq, Dir, A);
                return;
            }
        }

        Equip x, w;

        i = Esq;
        j = Dir;

        x = (Equip) (A.get((i + j) / 2));

        do {

            while (x.getLevel() > ((Equip) A.get(i)).getLevel()) {
                i++;
            }
            while (x.getLevel() < ((Equip) A.get(j)).getLevel()) {
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

    private static int getItemSubtype(Item it) {
        return it.getItemId() / 10000;
    }

    private int[] BinarySearchElement(ArrayList<Item> A, int rangeId) {
        int st = 0, en = A.size() - 1;

        int mid = -1, idx = -1;
        while (en >= st) {
            idx = (st + en) / 2;
            mid = getItemSubtype(A.get(idx));

            if (mid == rangeId) {
                break;
            } else if (mid < rangeId) {
                st = idx + 1;
            } else {
                en = idx - 1;
            }
        }

        if (en < st) {
            return null;
        }

        st = idx - 1;
        en = idx + 1;
        while (st >= 0 && getItemSubtype(A.get(st)) == rangeId) {
            st -= 1;
        }
        st += 1;

        while (en < A.size() && getItemSubtype(A.get(en)) == rangeId) {
            en += 1;
        }
        en -= 1;

        return new int[]{st, en};
    }

    public void reverseSortSublist(ArrayList<Item> A, int[] range) {
        if (range != null) {
            PartitionByProjectileAtk(range[0], range[1], A);
        }
    }

    public PairedQuicksort(ArrayList<Item> A, int primarySort, int secondarySort) {
        intersect = new ArrayList<>();

        if (A.size() > 0) {
            MapleQuicksort(0, A.size() - 1, A, primarySort);

            if (A.get(0).getInventoryType().equals(InventoryType.USE)) {   // thanks KDA & Vcoc for suggesting stronger projectiles coming before weaker ones
                reverseSortSublist(A, BinarySearchElement(A, 206));  // arrows
                reverseSortSublist(A, BinarySearchElement(A, 207));  // stars
                reverseSortSublist(A, BinarySearchElement(A, 233));  // bullets
            }
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

public final class InventorySortHandler extends AbstractPacketHandler {
    /** Block double-click spam only — must stay below client invent busy window. */
    private static final long SORT_DEBOUNCE_MS = 400L;
    private static final java.util.concurrent.ConcurrentHashMap<Integer, Long> lastSortAt =
            new java.util.concurrent.ConcurrentHashMap<>();

    /** True if sorted list order differs from pre-sort slot scan order. */
    private static boolean sortOrderChanged(ArrayList<Item> before, ArrayList<Item> after) {
        if (before.size() != after.size()) {
            return true;
        }
        for (int i = 0; i < before.size(); i++) {
            if (before.get(i).getItemId() != after.get(i).getItemId()) {
                return true;
            }
        }
        return false;
    }

    /** True if items are not already packed into slots 1..n with no gaps. */
    private static boolean needsPackedReorder(ArrayList<Item> itemarray) {
        for (int i = 0; i < itemarray.size(); i++) {
            if (itemarray.get(i).getPosition() != (short) (i + 1)) {
                return true;
            }
        }
        return false;
    }

    private static void sendSortInventoryPackets(Client c, List<ModifyInventory> mods, byte invType) {
        if (!mods.isEmpty()) {
            final int chunk = 200;
            for (int i = 0; i < mods.size(); i += chunk) {
                int to = Math.min(i + chunk, mods.size());
                // Only first chunk needs updateTick — keeps multi-chunk invent one logical batch.
                c.sendPacket(PacketCreator.modifyInventory(i == 0, mods.subList(i, to)));
            }
        }
        c.sendPacket(PacketCreator.finishedSort2(invType));
    }

    private static List<ModifyInventory> applyPackedSort(Inventory inventory,
                                                         ArrayList<Item> itemarray) {
        List<ModifyInventory> mods = new ArrayList<>();

        for (Item item : itemarray) {
            inventory.removeSlot(item.getPosition());
            mods.add(new ModifyInventory(3, item));
        }

        short slotCursor = 1;
        for (Item item : itemarray) {
            if (slotCursor > inventory.getSlotLimit()) {
                break;
            }
            item.setPosition(slotCursor);
            inventory.addItemFromDB(item);
            mods.add(new ModifyInventory(0, item.copy()));
            slotCursor++;
        }
        return mods;
    }

    @Override
    public final void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        p.readInt();
        chr.getAutoBanManager().setTimestamp(3, Server.getInstance().getCurrentTimestamp(), 4);

        byte invType = p.available() > 0 ? p.readByte() : 0;
        if (invType < 1 || invType > 5) {
            c.disconnect(false, false);
            return;
        }

        final int cid = chr.getId();
        final long now = System.currentTimeMillis();
        final Long prev = lastSortAt.put(cid, now);
        if (prev != null && now - prev < SORT_DEBOUNCE_MS) {
            // Must still ack sort UI — client SlotLock latches invent-pending until
            // finishedSort2; enableActions-only left 整理 permanently dead.
            try {
                c.sendPacket(PacketCreator.finishedSort2(invType));
            } catch (Throwable ignored) {
            }
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        // AUX wire-omit (GREEN_ENTER_OMIT_AUX62): flush ghost −62 before bag rearrange.
        // When flag false: recover is no-op; replace −62→bag is normal equip path.
        org.gms.client.inventory.manipulator.InventoryManipulator.recoverWireOmittedAuxToBag(c);

        if (!GameConfig.getServerBoolean("use_item_sort")) {
            try {
                c.sendPacket(PacketCreator.finishedSort2(invType));
            } catch (Throwable ignored) {
            }
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        try {
            // Legacy slot-lock clients appended lock bytes after invType. Feature removed —
            // discard any trailing payload and always pack into contiguous slots (no gaps).
            if (p.available() > 0) {
                final int lockSize = Short.toUnsignedInt(p.readUnsignedByte());
                for (int i = 0; i < lockSize && p.available() > 0; i++) {
                    p.readUnsignedByte();
                }
            }

            ArrayList<Item> itemarray = new ArrayList<>();
            List<ModifyInventory> mods = new ArrayList<>();

            Inventory inventory = chr.getInventory(InventoryType.getByType(invType));
            inventory.lockInventory();
            try {
                for (short i = 1; i <= inventory.getSlotLimit(); i++) {
                    Item item = inventory.getItem(i);
                    if (item != null) {
                        itemarray.add(item.copy());
                    }
                }

                final ArrayList<Item> beforeSort = new ArrayList<>();
                for (Item item : itemarray) {
                    beforeSort.add(item.copy());
                }

                int invTypeCriteria = (InventoryType.getByType(invType) == InventoryType.EQUIP) ? 3 : 1;
                int sortCriteria = GameConfig.getServerBoolean("use_item_sort_by_name") ? 2 : 0;
                // Sort BEFORE remove — NPE/sort fail must not leave bag empty (整理报错/丢物).
                if (itemarray.size() >= 2) {
                    new PairedQuicksort(itemarray, sortCriteria, invTypeCriteria);
                }

                if (sortOrderChanged(beforeSort, itemarray) || needsPackedReorder(itemarray)) {
                    mods = applyPackedSort(inventory, itemarray);
                }
                itemarray.clear();
            } finally {
                inventory.unlockInventory();
            }

            sendSortInventoryPackets(c, mods, invType);
        } catch (Throwable t) {
            // Addon desync / null name / bad slot must NEVER leave client SendBusy
            // (organize hang). Always enableActions in outer finally.
            org.slf4j.LoggerFactory.getLogger(InventorySortHandler.class)
                    .error("inventory sort failed invType={} char={}", invType,
                            chr != null ? chr.getName() : "?", t);
            // Still ack sort UI so client leaves busy (even if invent skipped).
            try {
                c.sendPacket(PacketCreator.finishedSort2(invType));
            } catch (Throwable ignored) {
            }
        } finally {
            c.sendPacket(PacketCreator.enableActions());
        }
    }
}
