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

import java.util.HashSet;
import java.util.Set;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.config.GameConfig;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

public final class InventoryMergeHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        p.readInt();
        chr.getAutoBanManager().setTimestamp(2, Server.getInstance().getCurrentTimestamp(), 4);

        // AUX wire-omit (GREEN_ENTER_OMIT_AUX62): flush ghost −62 before merge/sort.
        // When flag false: recover is no-op.
        InventoryManipulator.recoverWireOmittedAuxToBag(c);

        if (!GameConfig.getServerBoolean("use_item_sort")) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        byte invType = p.readByte();
        if (invType < 1 || invType > 5) {
            c.disconnect(false, false);
            return;
        }

        try {
            if (p.available() > 0) {
                handleSlotLockMerge(p, c, invType);
                return;
            }

            InventoryType inventoryType = InventoryType.getByType(invType);
            Inventory inventory = c.getPlayer().getInventory(inventoryType);
            inventory.lockInventory();
            try {
                //------------------- RonanLana's SLOT MERGER -----------------

                ItemInformationProvider ii = ItemInformationProvider.getInstance();
                Item srcItem, dstItem;

                for (short dst = 1; dst <= inventory.getSlotLimit(); dst++) {
                    dstItem = inventory.getItem(dst);
                    if (dstItem == null) {
                        continue;
                    }

                    for (short src = (short) (dst + 1); src <= inventory.getSlotLimit(); src++) {
                        srcItem = inventory.getItem(src);
                        if (srcItem == null) {
                            continue;
                        }

                        if (dstItem.getItemId() != srcItem.getItemId()) {
                            continue;
                        }
                        if (dstItem.getQuantity() == ii.getSlotMax(c, inventory.getItem(dst).getItemId())) {
                            break;
                        }

                        final short beforeQty = dstItem.getQuantity();
                        InventoryManipulator.move(c, inventoryType, src, dst);
                        dstItem = inventory.getItem(dst);
                        // Guard: if move could not increase dst stack (e.g. pet/cash swap), stop.
                        if (dstItem == null || dstItem.getQuantity() <= beforeQty) {
                            break;
                        }
                    }
                }

                //------------------------------------------------------------

                inventory = c.getPlayer().getInventory(inventoryType);
                boolean sorted = false;

                while (!sorted) {
                    short freeSlot = inventory.getNextFreeSlot();

                    if (freeSlot != -1) {
                        short itemSlot = -1;
                        for (short i = (short) (freeSlot + 1); i <= inventory.getSlotLimit(); i = (short) (i + 1)) {
                            if (inventory.getItem(i) != null) {
                                itemSlot = i;
                                break;
                            }
                        }
                        if (itemSlot > 0) {
                            InventoryManipulator.move(c, inventoryType, itemSlot, freeSlot);
                        } else {
                            sorted = true;
                        }
                    } else {
                        sorted = true;
                    }
                }
            } finally {
                inventory.unlockInventory();
            }

            c.sendPacket(PacketCreator.finishedSort(inventoryType.getType()));
        } finally {
            c.sendPacket(PacketCreator.enableActions());
        }
    }

    private void handleSlotLockMerge(InPacket p, Client c, byte invType) {
        Set<Integer> lockedSlots = new HashSet<>();
        final int lockSize = Short.toUnsignedInt(p.readUnsignedByte());
        for (int i = 0; i < lockSize; i++) {
            lockedSlots.add(Short.toUnsignedInt(p.readUnsignedByte()));
        }

        InventoryType inventoryType = InventoryType.getByType(invType);
        Inventory inventory = c.getPlayer().getInventory(inventoryType);
        inventory.lockInventory();
        try {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            Item srcItem, dstItem;

            for (short dst = 1; dst <= inventory.getSlotLimit(); dst++) {
                if (lockedSlots.contains((int) dst)) {
                    continue;
                }
                dstItem = inventory.getItem(dst);
                if (dstItem == null) {
                    continue;
                }

                for (short src = (short) (dst + 1); src <= inventory.getSlotLimit(); src++) {
                    if (lockedSlots.contains((int) src)) {
                        continue;
                    }
                    srcItem = inventory.getItem(src);
                    if (srcItem == null) {
                        continue;
                    }
                    if (dstItem.getItemId() != srcItem.getItemId()) {
                        continue;
                    }
                    if (dstItem.getQuantity() == ii.getSlotMax(c, inventory.getItem(dst).getItemId())) {
                        break;
                    }

                    final short beforeQty = dstItem.getQuantity();
                    InventoryManipulator.move(c, inventoryType, src, dst);
                    dstItem = inventory.getItem(dst);
                    if (dstItem == null || dstItem.getQuantity() <= beforeQty) {
                        break;
                    }
                }
            }

            inventory = c.getPlayer().getInventory(inventoryType);
            boolean sorted = false;

            while (!sorted) {
                short freeSlot = -1;
                for (short i = 1; i <= inventory.getSlotLimit(); i++) {
                    if (!lockedSlots.contains((int) i) && inventory.getItem(i) == null) {
                        freeSlot = i;
                        break;
                    }
                }

                if (freeSlot != -1) {
                    short itemSlot = -1;
                    for (short i = (short) (freeSlot + 1); i <= inventory.getSlotLimit(); i++) {
                        if (!lockedSlots.contains((int) i) && inventory.getItem(i) != null) {
                            itemSlot = i;
                            break;
                        }
                    }
                    if (itemSlot > 0) {
                        InventoryManipulator.move(c, inventoryType, itemSlot, freeSlot);
                    } else {
                        sorted = true;
                    }
                } else {
                    sorted = true;
                }
            }
        } finally {
            inventory.unlockInventory();
        }

        c.sendPacket(PacketCreator.finishedSort(inventoryType.getType()));
        // enableActions: outer handlePacket finally
    }
}
