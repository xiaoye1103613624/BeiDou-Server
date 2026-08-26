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
package org.gms.client.inventory.manipulator;

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.client.inventory.Pet;
import org.gms.model.pojo.NewYearCardRecord;
import org.gms.config.GameConfig;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.EquipSlot;
import org.gms.constants.inventory.ExtendedEquipRegistry;
import org.gms.constants.inventory.ItemConstants;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Matze
 * @author Ronan - improved check space feature and removed redundant object calls
 */
public class InventoryManipulator {
    private static final Logger log = LoggerFactory.getLogger(InventoryManipulator.class);

    public static boolean addById(Client c, int itemId, short quantity) {
        return addById(c, itemId, quantity, null, -1, -1);
    }

    public static boolean addById(Client c, int itemId, short quantity, long expiration) {
        return addById(c, itemId, quantity, null, -1, (byte) 0, expiration);
    }

    public static boolean addById(Client c, int itemId, short quantity, String owner, int petid) {
        return addById(c, itemId, quantity, owner, petid, -1);
    }

    public static boolean addById(Client c, int itemId, short quantity, String owner, int petid, long expiration) {
        return addById(c, itemId, quantity, owner, petid, (byte) 0, expiration);
    }

    public static boolean addById(Client c, int itemId, short quantity, String owner, int petid, short flag, long expiration) {
        Character chr = c.getPlayer();
        InventoryType type = ItemConstants.getInventoryType(itemId);

        Inventory inv = chr.getInventory(type);
        inv.lockInventory();
        try {
            boolean ok = addByIdInternal(c, chr, type, inv, itemId, quantity, owner, petid, flag, expiration);
            if (ok) {
                chr.onCarryCombatItemChanged(itemId);
            }
            return ok;
        } finally {
            inv.unlockInventory();
        }
    }

    private static boolean addByIdInternal(Client c, Character chr, InventoryType type, Inventory inv, int itemId, short quantity, String owner, int petid, short flag, long expiration) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (!type.equals(InventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemId);
            List<Item> existing = inv.listById(itemId);
            if (!ItemConstants.isRechargeable(itemId) && petid == -1) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            Item eItem = i.next();
                            short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && ((eItem.getOwner().equals(owner) || owner == null) && eItem.getFlag() == flag)) {
                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                eItem.setExpiration(expiration);
                                c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, eItem))));
                            }
                        } else {
                            break;
                        }
                    }
                }
                boolean sandboxItem = (flag & ItemConstants.SANDBOX) == ItemConstants.SANDBOX;
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        Item nItem = new Item(itemId, (short) 0, newQ, petid);
                        nItem.setFlag(flag);
                        nItem.setExpiration(expiration);
                        short newSlot = inv.addItem(nItem);
                        if (newSlot == -1) {
                            c.sendPacket(PacketCreator.getInventoryFull());
                            c.sendPacket(PacketCreator.getShowInventoryFull());
                            return false;
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                        if (sandboxItem) {
                            chr.setHasSandboxItem();
                        }
                    } else {
                        c.sendPacket(PacketCreator.enableActions());
                        return false;
                    }
                }
            } else {
                Item nItem = new Item(itemId, (short) 0, quantity, petid);
                nItem.setFlag(flag);
                nItem.setExpiration(expiration);
                short newSlot = inv.addItem(nItem);
                if (newSlot == -1) {
                    c.sendPacket(PacketCreator.getInventoryFull());
                    c.sendPacket(PacketCreator.getShowInventoryFull());
                    return false;
                }
                c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                if (InventoryManipulator.isSandboxItem(nItem)) {
                    chr.setHasSandboxItem();
                }
            }
        } else if (quantity == 1) {
            Item nEquip = ii.getEquipById(itemId);
            nEquip.setFlag(flag);
            nEquip.setExpiration(expiration);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            short newSlot = inv.addItem(nEquip);
            if (newSlot == -1) {
                c.sendPacket(PacketCreator.getInventoryFull());
                c.sendPacket(PacketCreator.getShowInventoryFull());
                return false;
            }
            c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nEquip))));
            if (InventoryManipulator.isSandboxItem(nEquip)) {
                chr.setHasSandboxItem();
            }
        } else {
            throw new RuntimeException("Trying to create equip with non-one quantity");
        }
        return true;
    }

    public static boolean addFromDrop(Client c, Item item) {
        return addFromDrop(c, item, true);
    }

    public static boolean addFromDrop(Client c, Item item, boolean show) {
        return addFromDrop(c, item, show, item.getPetId());
    }

    public static boolean addFromDrop(Client c, Item item, boolean show, int petId) {
        Character chr = c.getPlayer();
        InventoryType type = item.getInventoryType();

        Inventory inv = chr.getInventory(type);
        inv.lockInventory();
        try {
            boolean ok = addFromDropInternal(c, chr, type, inv, item, show, petId);
            if (ok) {
                chr.onCarryCombatItemChanged(item.getItemId());
            }
            return ok;
        } finally {
            inv.unlockInventory();
        }
    }

    private static boolean addFromDropInternal(Client c, Character chr, InventoryType type, Inventory inv, Item item, boolean show, int petId) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        int itemid = item.getItemId();
        if (ii.isPickupRestricted(itemid) && chr.haveItemWithId(itemid, true)) {
            c.sendPacket(PacketCreator.getInventoryFull());
            c.sendPacket(PacketCreator.showItemUnavailable());
            return false;
        }
        short quantity = item.getQuantity();

        if (!type.equals(InventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemid);
            List<Item> existing = inv.listById(itemid);
            if (!ItemConstants.isRechargeable(itemid) && petId == -1) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            Item eItem = i.next();
                            short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && item.getFlag() == eItem.getFlag() && item.getOwner().equals(eItem.getOwner())) {
                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                item.setPosition(eItem.getPosition());
                                c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, eItem))));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // Missing WZ / slotMax==0 would spin forever (qty never decreases) and soft-lock the player handler.
                if (slotMax <= 0) {
                    slotMax = 100;
                }
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    Item nItem = new Item(itemid, (short) 0, newQ, petId);
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setFlag(item.getFlag());
                    short newSlot = inv.addItem(nItem);
                    if (newSlot == -1) {
                        c.sendPacket(PacketCreator.getInventoryFull());
                        c.sendPacket(PacketCreator.getShowInventoryFull());
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    nItem.setPosition(newSlot);
                    item.setPosition(newSlot);
                    c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                    if (InventoryManipulator.isSandboxItem(nItem)) {
                        chr.setHasSandboxItem();
                    }
                }
            } else {
                Item nItem = new Item(itemid, (short) 0, quantity, petId);
                nItem.setExpiration(item.getExpiration());
                nItem.setFlag(item.getFlag());

                short newSlot = inv.addItem(nItem);
                if (newSlot == -1) {
                    c.sendPacket(PacketCreator.getInventoryFull());
                    c.sendPacket(PacketCreator.getShowInventoryFull());
                    return false;
                }
                nItem.setPosition(newSlot);
                item.setPosition(newSlot);
                c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                if (InventoryManipulator.isSandboxItem(nItem)) {
                    chr.setHasSandboxItem();
                }
                c.sendPacket(PacketCreator.enableActions());
            }
        } else if (quantity == 1) {
            short newSlot = inv.addItem(item);
            if (newSlot == -1) {
                c.sendPacket(PacketCreator.getInventoryFull());
                c.sendPacket(PacketCreator.getShowInventoryFull());
                return false;
            }
            item.setPosition(newSlot);
            c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, item))));
            if (InventoryManipulator.isSandboxItem(item)) {
                chr.setHasSandboxItem();
            }
        } else {
            log.warn("Tried to pickup Equip id {} containing more than 1 quantity --> {}", itemid, quantity);
            c.sendPacket(PacketCreator.getInventoryFull());
            c.sendPacket(PacketCreator.showItemUnavailable());
            return false;
        }
        if (show) {
            c.sendPacket(PacketCreator.getShowItemGain(itemid, item.getQuantity()));
        }
        org.gms.reincarnation.ReincarnationSupport.onInventoryChanged(chr, itemid);
        return true;
    }

    private static boolean haveItemWithId(Inventory inv, int itemid) {
        return inv.findById(itemid) != null;
    }

    public static boolean checkSpace(Client c, int itemid, int quantity, String owner) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        InventoryType type = ItemConstants.getInventoryType(itemid);
        Character chr = c.getPlayer();
        Inventory inv = chr.getInventory(type);

        if (ii.isPickupRestricted(itemid)) {
            if (haveItemWithId(inv, itemid)) {
                return false;
            } else if (ItemConstants.isEquipment(itemid) && haveItemWithId(chr.getInventory(InventoryType.EQUIPPED), itemid)) {
                return false;
            }
        }

        if (!type.equals(InventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemid);
            List<Item> existing = inv.listById(itemid);

            final int numSlotsNeeded;
            if (ItemConstants.isRechargeable(itemid)) {
                numSlotsNeeded = 1;
            } else {
                if (existing.size() > 0) // first update all existing slots to slotMax
                {
                    for (Item eItem : existing) {
                        short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                            short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity -= (newQ - oldQ);
                        }
                        if (quantity <= 0) {
                            break;
                        }
                    }
                }

                if (slotMax > 0) {
                    numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
                } else {
                    numSlotsNeeded = 1;
                }
            }

            return !inv.isFull(numSlotsNeeded - 1);
        } else {
            return !inv.isFull();
        }
    }

    public static int checkSpaceProgressively(Client c, int itemid, int quantity, String owner, int usedSlots, boolean useProofInv) {
        // return value --> bit0: if has space for this one;
        //                  value after: new slots filled;
        // assumption: equipments always have slotMax == 1.

        int returnValue;

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        InventoryType type = !useProofInv ? ItemConstants.getInventoryType(itemid) : InventoryType.CANHOLD;
        Character chr = c.getPlayer();
        Inventory inv = chr.getInventory(type);

        if (ii.isPickupRestricted(itemid)) {
            if (haveItemWithId(inv, itemid)) {
                return 0;
            } else if (ItemConstants.isEquipment(itemid) && haveItemWithId(chr.getInventory(InventoryType.EQUIPPED), itemid)) {
                return 0;   // thanks Captain & Aika & Vcoc for pointing out inventory checkup on player trades missing out one-of-a-kind items.
            }
        }

        if (!type.equals(InventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(c, itemid);
            final int numSlotsNeeded;

            if (ItemConstants.isRechargeable(itemid)) {
                numSlotsNeeded = 1;
            } else {
                List<Item> existing = inv.listById(itemid);

                if (existing.size() > 0) // first update all existing slots to slotMax
                {
                    for (Item eItem : existing) {
                        short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                            short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity -= (newQ - oldQ);
                        }
                        if (quantity <= 0) {
                            break;
                        }
                    }
                }

                if (slotMax > 0) {
                    numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
                } else {
                    numSlotsNeeded = 1;
                }
            }

            returnValue = ((numSlotsNeeded + usedSlots) << 1);
            returnValue += (numSlotsNeeded == 0 || !inv.isFullAfterSomeItems(numSlotsNeeded - 1, usedSlots)) ? 1 : 0;
            //System.out.print(" needed " + numSlotsNeeded + " used " + usedSlots + " rval " + returnValue);
        } else {
            returnValue = ((quantity + usedSlots) << 1);
            returnValue += (!inv.isFullAfterSomeItems(0, usedSlots)) ? 1 : 0;
            //System.out.print(" eqpneeded " + 1 + " used " + usedSlots + " rval " + returnValue);
        }

        return returnValue;
    }

    public static void removeFromSlot(Client c, InventoryType type, short slot, short quantity, boolean fromDrop) {
        removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static void removeFromSlot(Client c, InventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
        Character chr = c.getPlayer();
        Inventory inv = chr.getInventory(type);
        Item item = inv.getItem(slot);
        int combatItemId = item != null ? item.getItemId() : 0;
        boolean allowZero = consume && ItemConstants.isRechargeable(item.getItemId());

        if (type == InventoryType.EQUIPPED) {
            inv.lockInventory();
            try {
                chr.unequippedItem((Equip) item);
                inv.removeItem(slot, quantity, allowZero);
            } finally {
                inv.unlockInventory();
            }

            announceModifyInventory(c, item, fromDrop, allowZero);
        } else {
            int petid = item.getPetId();
            if (petid > -1) { // thanks Vcoc for finding a d/c issue with equipped pets and pets remaining on DB here
                int petIdx = chr.getPetIndex(petid);
                if (petIdx > -1) {
                    Pet pet = chr.getPet(petIdx);
                    chr.unEquipPet(pet, true);
                }

                inv.removeItem(slot, quantity, allowZero);
                if (type != InventoryType.CANHOLD) {
                    announceModifyInventory(c, item, fromDrop, allowZero);
                }

                // thanks Robin Schulz for noticing pet issues when moving pets out of inventory
            } else {
                inv.removeItem(slot, quantity, allowZero);
                if (type != InventoryType.CANHOLD) {
                    announceModifyInventory(c, item, fromDrop, allowZero);
                }
            }
        }
        if (combatItemId > 0) {
            chr.onCarryCombatItemChanged(combatItemId);
            org.gms.reincarnation.ReincarnationSupport.onInventoryChanged(chr, combatItemId);
        }
    }

    private static void announceModifyInventory(Client c, Item item, boolean fromDrop, boolean allowZero) {
        if (item.getQuantity() == 0 && !allowZero) {
            c.sendPacket(PacketCreator.modifyInventory(fromDrop, Collections.singletonList(new ModifyInventory(3, item))));
        } else {
            c.sendPacket(PacketCreator.modifyInventory(fromDrop, Collections.singletonList(new ModifyInventory(1, item))));
        }
    }

    public static void removeById(Client c, InventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
        int removeQuantity = quantity;
        Inventory inv = c.getPlayer().getInventory(type);
        int slotLimit = type == InventoryType.EQUIPPED ? 128 : inv.getSlotLimit();

        for (short i = 0; i <= slotLimit; i++) {
            Item item = inv.getItem((short) (type == InventoryType.EQUIPPED ? -i : i));
            if (item != null) {
                if (item.getItemId() == itemId || item.getCashId() == itemId) {
                    if (removeQuantity <= item.getQuantity()) {
                        removeFromSlot(c, type, item.getPosition(), (short) removeQuantity, fromDrop, consume);
                        removeQuantity = 0;
                        break;
                    } else {
                        removeQuantity -= item.getQuantity();
                        removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume);
                    }
                }
            }
        }
        if (removeQuantity > 0 && type != InventoryType.CANHOLD) {
            throw new RuntimeException("[Hack] Not enough items available of Item:" + itemId + ", Quantity (After Quantity/Over Current Quantity): " + (quantity - removeQuantity) + "/" + quantity);
        }
    }

    private static boolean isSameOwner(Item source, Item target) {
        return source.getOwner().equals(target.getOwner());
    }

    public static void move(Client c, InventoryType type, short src, short dst) {
        Inventory inv = c.getPlayer().getInventory(type);

        if (src < 0 || dst < 0) {
            return;
        }
        if (dst > inv.getSlotLimit()) {
            return;
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item source = inv.getItem(src);
        Item initialTarget = inv.getItem(dst);
        if (source == null) {
            return;
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        short oldsrcQ = source.getQuantity();
        short slotMax = ii.getSlotMax(c, source.getItemId());
        inv.move(src, dst, slotMax);
        final List<ModifyInventory> mods = new ArrayList<>();
        // Cash stackables (cubes) need quantity-update packets too. Pets stay on the move/swap path.
        final boolean cashPet = type.equals(InventoryType.CASH)
                && (ItemConstants.isPet(source.getItemId()) || source.getPet() != null
                || (initialTarget != null && initialTarget.getPet() != null));
        if (!type.equals(InventoryType.EQUIP) && !cashPet
                && initialTarget != null && initialTarget.getItemId() == source.getItemId()
                && !ItemConstants.isRechargeable(source.getItemId()) && isSameOwner(source, initialTarget)) {
            if ((olddstQ + oldsrcQ) > slotMax) {
                mods.add(new ModifyInventory(1, source));
                mods.add(new ModifyInventory(1, initialTarget));
            } else {
                mods.add(new ModifyInventory(3, source));
                mods.add(new ModifyInventory(1, initialTarget));
            }
        } else {
            mods.add(new ModifyInventory(2, source, src));
        }
        c.sendPacket(PacketCreator.modifyInventory(true, mods));
        // 添加物品代码提示
        if (GameConfig.getServerBoolean("use_debug") && c.getPlayer().isGM()) { // 假设isGM()是检查玩家是否是管理员的方法
            int itemID = source.getItemId();
            c.getPlayer().dropMessage(5, I18nUtil.getMessage("InventoryManipulator.handlePacket.message1")  + itemID);
        }
    }

    /*
    穿上装备判断
     */
    public static void equip(Client c, short src, short dst) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        Character chr = c.getPlayer();
        Inventory eqpInv = chr.getInventory(InventoryType.EQUIP);
        Inventory eqpdInv = chr.getInventory(InventoryType.EQUIPPED);

        Equip source = (Equip) eqpInv.getItem(src);
        if (source == null) {
            // Silent reject left Addon wear SendBusy + client ghosts undiagnosed.
            log.info("equip reject source-null src={} dst={} char={}", src, dst, chr.getName());
            unstickClientInventory(c);
            return;
        }
        // FIX_EXT_SLOT_UI_RELOG_20260727av / aw + ADDON_SLOTS_TOTEM4_20260801：
        // 徽章/图腾×4/纹章/机器人/心脏 → 固定 Addon 槽；点装走 −100 镜像。
        // 属性：recalcEquipStats 遍历全部 EQUIPPED，不按 cash/槽过滤，两侧均生效。
        {
            final int itemId = source.getItemId();
            final int prefix = itemId / 10000;
            final String slotName = ii.getEquipmentSlot(itemId);
            final boolean cashItem = ii.isCash(itemId);
            // Phase2 ExtendedEquipRegistry: same prefix||islot order as STATS_UNEQUIP.
            // 119xxxx WZ islot is often still "Si" — must NOT hit SHIELD → −10.
            if (prefix == 116 || EquipSlot.POCKET.getName().equals(slotName)) {
                dst = ExtendedEquipRegistry.resolveFixedDst(116, cashItem);
            } else if (prefix == 119 || EquipSlot.EMBLEM.getName().equals(slotName)) {
                dst = ExtendedEquipRegistry.resolveFixedDst(119, cashItem);
            } else if (prefix == 118 || EquipSlot.BADGE.getName().equals(slotName)) {
                dst = ExtendedEquipRegistry.resolveFixedDst(118, cashItem);
                // Badge ≤1: park stray 118 off −54/−154 before mode-2 replace.
                if (!stripStrayPrefix(c, eqpdInv, eqpInv, 118, dst)) {
                    return;
                }
            } else if (prefix == 166 || EquipSlot.ANDROID.getName().equals(slotName)) {
                dst = ExtendedEquipRegistry.resolveFixedDst(166, cashItem);
            } else if (prefix == 167 || EquipSlot.HEART.getName().equals(slotName)) {
                dst = ExtendedEquipRegistry.resolveFixedDst(167, cashItem);
            } else if (prefix == 120
                    || itemId == org.gms.reincarnation.ReincarnationSupport.EQUIP_TOTEM) {
                // Totem×4 → −55..−58 only. Client sidecar-blind often spam-sends −55;
                // resolveTotemDst fills empty seats first (never leave 2–4 unused while
                // replacing −55). Reject only when all 4 occupied and no replace target.
                dst = ExtendedEquipRegistry.resolveTotemDst(eqpdInv, dst);
                if (dst == 0) {
                    unstickClientInventory(c);
                    chr.dropMessage(1, "图腾栏已满（最多4件）");
                    return;
                }
            } else if (prefix == 109) {
                dst = ExtendedEquipRegistry.resolveFixedDst(109, cashItem);
                // Shield ≤1: strip stray 109 off non-target seats before replace.
                if (!stripStrayPrefix(c, eqpdInv, eqpInv, 109, dst)) {
                    return;
                }
            } else if (prefix == 134 || prefix == 135) {
                // Zero coupling: 134/135 → Aw/−62 only (never Si/−10; never kick 109).
                dst = ExtendedEquipRegistry.resolveFixedDst(prefix, cashItem);
                // REJECT-WEAR (gated by GREEN_ENTER_OMIT_AUX62): while omit, CharInfo has no −62
                // ZRef; INVENTORY_OPERATION to −62 → SendBusy / 整理假死. Flip flag → this block gone.
                if (ExtendedEquipRegistry.isGreenEnterWireOmit(dst)) {
                    unstickClientInventory(c);
                    chr.dropMessage(5, "辅助武器暂不可穿戴（客户端未开放−62；请先放背包。整理前会自动卸下幽灵−62）");
                    log.info("equip refuse aux wire-omit id={} dst={} char={}", itemId, dst, chr.getName());
                    return;
                }
                // Aux ≤1: strip EVERY stray 134/135 off non-target seats (legacy Si −10/−110
                // and any wrong Addon/classic slot) before replace-at−62.
                if (!stripStrayPrefixes(c, eqpdInv, eqpInv, new int[]{134, 135}, dst)) {
                    return;
                }
            }
            // POCKET_BP33_FIX: pocket −33 reuses pet BP33 — client HT / pet path may
            // SendChange fashion (e.g. 100xxxx hat) to −33. Re-resolve from prefix/islot.
            if ((dst == -33 || dst == -133)
                    && prefix != 116
                    && !EquipSlot.POCKET.getName().equals(slotName)) {
                short corrected = resolveMisroutedExtendedDst(ii, itemId, cashItem, slotName);
                if (corrected != 0) {
                    log.info("equip pocket-dst fix id={} {} -> {} char={}",
                            itemId, dst, corrected, chr.getName());
                    dst = corrected;
                }
            }
            // AUX62_MISROUTE R31: fashion→−62 must REJECT (enableActions), never remap
            // to −1. Remap turned aux dblclick misroute into "wear hat" and left −62
            // looking cleared. Pocket −33 still remaps above (preserve POCKET_BP33_FIX).
            if ((dst == -62 || dst == -162)
                    && prefix != 134 && prefix != 135
                    && !EquipSlot.AUX_WEAPON.getName().equals(slotName)) {
                unstickClientInventory(c);
                log.info("equip aux-dst reject id={} dst={} (no remap) char={}",
                        itemId, dst, chr.getName());
                return;
            }
        }
        // VANILLA PARITY: occupied Addon seats REPLACE like classic −1…−11 (mode-2 swap
        // below). Hard occupy-reject left client blank (GetItem/sidecar desync) unable to
        // wear while server still held 118/135/120 — out.log 23:05 occupy storm.
        // Client may still early-out when local GetItem non-null (UX); server must not.
        // Totem×4 full remains rejected above (product: exactly 4).
        // Guard: class must NOT contain log "equip reject occupy" (see GUARD_NO_OCCUPY_REJECT).
        int itemGender = ItemId.getGender(source.getItemId());
        //控制台参数为true时进行校验判断
        if(GameConfig.getServerBoolean("use_equipment_gender_limit") && itemGender != 2 && itemGender != chr.getGender()) {  //判断装备是否要求角色性别
            c.sendPacket(PacketCreator.enableActions());
            chr.dropMessage(1,I18nUtil.getMessage("InventoryManipulator.equip.message1"));    //发送弹窗提示性别不符
            log.warn(I18nUtil.getLogMessage("InventoryManipulator.warn.equip.message1"),      //后台记录信息
                    chr.getName(),
                    chr.getGender() <= 0 ? I18nUtil.getMessage("Character.Gender0") : I18nUtil.getMessage("Character.Gender1"),
                    ii.getName(source.getItemId()),
                    itemGender <= 0 ? I18nUtil.getMessage("Character.Gender0") : I18nUtil.getMessage("Character.Gender1"),
                    source.getItemId()
            );
            return;
        }
        if (!ii.canWearEquipment(chr, source, dst)) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        } else if ((ItemId.isExplorerMount(source.getItemId()) && chr.isCygnus()) ||
                ((ItemId.isCygnusMount(source.getItemId())) && !chr.isCygnus())) {// Adventurer taming equipment    //冒险家驯服设备
            // ADDON_ROW3_UNEQUIP_UI: bare return left client SendBusy stuck → all unequip froze.
            c.sendPacket(PacketCreator.enableActions());
            return;
        }
        // 第二吊坠：v083 客户端无 BP51 UI（ijl15 hooks 仍 OFF）时，穿戴包 dst 几乎总是 −17/−117。
        // 主槽已有 Pe 且 −51/−151 空 → 改路由到第二槽，避免只能替换黑龙/主坠。
        // 不启用 ForceDrawLoop；属性靠服务端 recalcEquipStats 遍历 EQUIPPED（含 −51）。
        // 路由只改 dst，下方仍是单次 removeSlot(src) + addItemFromDB(dst)，无重复删/漏加。
        boolean routedSecondaryPendant = false;
        if ((dst == -17 || dst == -117) && EquipSlot.PENDANT.getName().equals(ii.getEquipmentSlot(source.getItemId()))) {
            short secondary = (short) (dst == -17 ? -51 : -151);
            if (eqpdInv.getItem(dst) != null && eqpdInv.getItem(secondary) == null) {
                dst = secondary;
                routedSecondaryPendant = true;
            }
        }
        // 六戒：拖拽到指定槽必须只换该槽（客户端 dst 原样落库）。空槽搜索 / 六满轮询只在
        // 客户端背包双击路径（empty-search + DblClick_RingFullRotate），服务端不得 preferEmpty/rotate。
        // FIX_RING_CRASH_20260727am：魂戒等 cash=1 戒必须以 *物品* isCash 判定，不能看 dst<=-100。
        // R26: −52/−53 normal ExtraRing; −152/−153 fashion cash (separate slots, same UI).
        if (EquipSlot.RING.getName().equals(ii.getEquipmentSlot(source.getItemId()))) {
            final boolean cashRing = ii.isCash(source.getItemId());
            if (cashRing) {
                if (dst == -12 || dst == -13 || dst == -15 || dst == -16) {
                    dst = (short) (dst - 100);
                } else if (dst == -52 || dst == -53) {
                    // normal ExtraRing — keep
                } else if (dst == -152 || dst == -153) {
                    // fashion ExtraRing — keep separate from −52/−53
                } else if (dst > -100 && dst < 0) {
                    dst = -112;
                }
            }
            // 时装魂戒（1115201~34）整系 onlyEquip：先卸其它已穿魂戒，避免双 CharacterEff 进图闪退
            if (org.gms.soul.SoulFashionRing.isSoulFashionRing(source.getItemId())) {
                org.gms.soul.SoulFashionRing.unequipOthers(chr, source.getItemId());
                // unequip 可能改动了装备栏引用，重新取 source
                source = (Equip) eqpInv.getItem(src);
                if (source == null) {
                    c.sendPacket(PacketCreator.enableActions());
                    return;
                }
            }
        }
        boolean itemChanged = false;

        if (ii.isUntradeableOnEquip(source.getItemId())) {
            short flag = source.getFlag();      // thanks BHB for noticing flags missing after equipping these      //感谢BHB在安装这些设备后发现旗帜丢失
            flag |= ItemConstants.UNTRADEABLE;
            source.setFlag(flag);

            itemChanged = true;
        }
        switch (dst) {
        case -6: // unequip the overall
            Item top = eqpdInv.getItem((short) -5);
            if (top != null && ItemConstants.isOverall(top.getItemId())) {
                if (eqpInv.isFull()) {
                    c.sendPacket(PacketCreator.getInventoryFull());
                    c.sendPacket(PacketCreator.getShowInventoryFull());
                    c.sendPacket(PacketCreator.enableActions());
                    return;
                }
                unequip(c, (byte) -5, eqpInv.getNextFreeSlot());
            }
            break;
        case -5:
            final Item bottom = eqpdInv.getItem((short) -6);
            if (bottom != null && ItemConstants.isOverall(source.getItemId())) {
                if (eqpInv.isFull()) {
                    c.sendPacket(PacketCreator.getInventoryFull());
                    c.sendPacket(PacketCreator.getShowInventoryFull());
                    c.sendPacket(PacketCreator.enableActions());
                    return;
                }
                unequip(c, (byte) -6, eqpInv.getNextFreeSlot());
            }
            break;
        case -10: // check if weapon is two-handed
            Item weapon = eqpdInv.getItem((short) -11);
            if (weapon != null && ii.isTwoHanded(weapon.getItemId())) {
                if (eqpInv.isFull()) {
                    c.sendPacket(PacketCreator.getInventoryFull());
                    c.sendPacket(PacketCreator.getShowInventoryFull());
                    c.sendPacket(PacketCreator.enableActions());
                    return;
                }
                unequip(c, (byte) -11, eqpInv.getNextFreeSlot());
            }
            break;
        case -11:
            Item shield = eqpdInv.getItem((short) -10);
            if (shield != null && ii.isTwoHanded(source.getItemId())) {
                if (eqpInv.isFull()) {
                    c.sendPacket(PacketCreator.getInventoryFull());
                    c.sendPacket(PacketCreator.getShowInventoryFull());
                    c.sendPacket(PacketCreator.enableActions());
                    return;
                }
                unequip(c, (byte) -10, eqpInv.getNextFreeSlot());
            }
            break;
        case -18:
            if (chr.getMapleMount() != null) {
                chr.getMapleMount().setItemId(source.getItemId());
            }
            break;
        }

        //1112413, 1112414, 1112405 (Lilin's Ring)
        source = (Equip) eqpInv.getItem(src);
        eqpInv.removeSlot(src);

        Equip target;
        // Addon sidecar: remove from whichever alias holds the item, then land at
        // normal −bp (client GetItem hook is normal-only; −154 mode-2 misses arena).
        short removeFrom = dst;
        if (ExtendedEquipRegistry.isAddonAliasPairSeat(dst)) {
            short occ = ExtendedEquipRegistry.findOccupiedAliasSeat(eqpdInv, dst);
            if (occ != 0) {
                removeFrom = occ;
            }
            dst = ExtendedEquipRegistry.toClientWireSlot(dst);
        } else if (ExtendedEquipRegistry.isExtraRingWireSeat(dst)) {
            // Fashion (−152/−153) and normal (−52/−53) share UI but not DB slot.
            removeFrom = dst;
        } else {
            removeFrom = ExtendedEquipRegistry.resolveEquippedSlotAlias(eqpdInv, dst);
        }
        eqpdInv.lockInventory();
        try {
            target = (Equip) eqpdInv.getItem(removeFrom);
            if (target != null) {
                chr.unequippedItem(target);
                eqpdInv.removeSlot(removeFrom);
            }
        } finally {
            eqpdInv.unlockInventory();
        }

        final List<ModifyInventory> mods = new ArrayList<>();
        if (itemChanged) {
            mods.add(new ModifyInventory(3, source));
            mods.add(new ModifyInventory(0, source.copy()));//to prevent crashes
        }

        source.setPosition(dst);

        eqpdInv.lockInventory();
        try {
            if (source.getRingId() > -1) {
                chr.getRingById(source.getRingId()).equip();
            }
            chr.equippedItem(source);
            eqpdInv.addItemFromDB(source);
        } finally {
            eqpdInv.unlockInventory();
        }

        // Pet skill equips (1812xxx / ribbons) — refresh PetSkill short so client sends PET_LOOT.
        int equippedId = source.getItemId();
        if (equippedId >= 1802000 && equippedId < 1842000) {
            for (byte i = 0; i < 3; i++) {
                if (chr.getPet(i) != null) {
                    chr.syncPetSkillsFromEquips(i);
                }
            }
        }

        if (target != null) {
            target.setPosition(src);
            eqpInv.addItemFromDB(target);
        }
        if (chr.getBuffedValue(BuffStat.BOOSTER) != null && ItemConstants.isWeapon(source.getItemId())) {
            chr.cancelBuffStats(BuffStat.BOOSTER);
        }

        int petIndex = ItemConstants.PETS_NAME_TAG.indexOf(dst);
        if (petIndex != -1) {
            Pet pet = chr.getPet(petIndex);
            if (pet != null) {
                chr.getMap().broadcastMessage(chr, PacketCreator.changePetName(chr, pet.getName(), (byte)petIndex), false);
            }
        }

        mods.add(new ModifyInventory(2, source, src));
        c.sendPacket(PacketCreator.modifyInventory(true, mods));
        // 灵韵：穿上后强制整包刷新，确保客户端 tip 拿到 skill 字段（move 模式本身不重发 item body）
        // Addon sidecar: forceUpdate = mode-3+0 → Client_1 AV — skip (mode-2 already applied).
        if (source.getEquipSkillId() > 0 && source.getEquipSkillLevel() > 0
                && !ExtendedEquipRegistry.isAddonAliasPairSeat(source.getPosition())) {
            chr.forceUpdateItem(source);
        }
        // ADDON_CRASH_20260804: do NOT forceUpdateItem (mode-3+0) on Addon seats after wear.
        // Live out.log: "equip addon forceUpdate dst=-55" → ~3s abrupt char-save (client AV).
        // Vanilla parity: mode-2 move is enough; SetItem hook lands −54…−62 into sidecar.
        // FORBIDDEN specials: empty modifyInventory([]), dual −bp/−(bp+100) mode-3, forceUpdate.
        if (routedSecondaryPendant) {
            // 客户端无 BP51 格：背包格会空、装备栏看不见 → 易被当成「消失」；属性仍生效。
            chr.dropMessage(5, "第二吊坠已装备（装备栏暂不显示属正常，属性已生效）。卸下请用 @第二坠");
        }
        chr.equipChanged();
        chr.forceUpdateLocalStats();
        // ADDON_STATS_UNEQUIP_20260802: wear 后强制再推盲区四维（防 refresh 早退）
        chr.forceSyncClientDisplayStats();
        org.gms.reincarnation.ReincarnationSupport.onEquipped(chr, source.getItemId());
    }

    public static void unequip(Client c, short src, short dst) {
        Character chr = c.getPlayer();
        Inventory eqpInv = chr.getInventory(InventoryType.EQUIP);
        Inventory eqpdInv = chr.getInventory(InventoryType.EQUIPPED);

        // ENTERSAFE: green client GetItem aliases −156→−56 and may send −bp while
        // the item lives at cash −(bp+100). Resolve either side — do NOT require
        // client TOTEM2 PacketSlot (that family = select→enter 0xC0000409).
        final short askedSrc = src;
        short resolvedSrc = ExtendedEquipRegistry.resolveEquippedSlotAlias(eqpdInv, src);
        if (resolvedSrc != src) {
            log.info("unequip alias {} → {}", src, resolvedSrc);
            src = resolvedSrc;
        }
        Equip source = (Equip) eqpdInv.getItem(src);
        // ADDON_POCKET_UNEQUIP_20260802: always enableActions on reject so Addon
        // sidecar unequip (−59 etc.) cannot leave the client send-busy / item-lost.
        //
        // Green ROW3 FindEmptyEquipBagSlot can desync (client thinks bag N empty
        // while server still has an item there) — e.g. unequip −55→bag=43 while
        // 43 holds 1190407 → silent inventory-full reject. Prefer a real free slot
        // instead of trusting the client's dest when it is invalid/occupied.
        if (dst < 1 || dst > eqpInv.getSlotLimit() || eqpInv.getItem(dst) != null) {
            short free = eqpInv.getNextFreeSlot();
            if (free > 0) {
                if (dst != free) {
                    log.info("unequip dest remap {} → {} (char={})", dst, free, chr.getName());
                }
                dst = free;
            } else {
                log.info("unequip reject bag-full src={} askedDst={} char={}", src, dst, chr.getName());
                // STAT enableActions only — empty INVENTORY_OPERATION unstick removed (ADDON_CRASH_20260804).
                unstickClientInventory(c);
                return;
            }
        }
        if (source == null) {
            // Retry alias pair once (out.log: occupy saw −62 then unequip source-null).
            short occ = ExtendedEquipRegistry.findOccupiedAliasSeat(eqpdInv, askedSrc);
            if (occ == 0) {
                occ = ExtendedEquipRegistry.findOccupiedAliasSeat(eqpdInv, src);
            }
            if (occ != 0) {
                log.info("unequip source-null retry occ={} asked={} char={}", occ, askedSrc, chr.getName());
                src = occ;
                source = (Equip) eqpdInv.getItem(src);
            }
        }
        if (source == null) {
            // Ghost arena (server EQUIPPED empty): vanilla refuse = enableActions only.
            // FORBIDDEN: mode-3 remove on equipped (addMovement=2) — tip hang / ALL_IDLE.
            // Client MUST ClearSidecar when invent never arrives (ijl15 "GHOST heal").
            // Live DB 2026-08-05: −54/−55 already in bag while UI still painted → this path.
            log.info("unequip reject source-null src={} asked={} dst={} char={} (enableActions only; client must GHOST-heal)",
                    src, askedSrc, dst, chr.getName());
            // Mid-session sidecar ghost (item already in bag / never on server): cannot
            // invent-clear (FORBIDDEN mode-3). Tell player — LoginClear on relog heals.
            if (ExtendedEquipRegistry.isAddonAliasPairSeat(askedSrc)
                    || ExtendedEquipRegistry.isAddonAliasPairSeat(src)) {
                chr.dropMessage(5, "扩展栏不同步（服务端该槽已空）。请关闭装备栏后重开，或小退再进；勿整理背包。");
            }
            unstickClientInventory(c);
            return;
        }
        Equip target = (Equip) eqpInv.getItem(dst);
        if (target != null && src <= 0) {
            // Should be unreachable after dest remap; keep as safety net.
            log.info("unequip reject dest-occupied src={} dst={} char={}", src, dst, chr.getName());
            unstickClientInventory(c);
            return;
        }

        eqpdInv.lockInventory();
        try {
            if (source.getRingId() > -1) {
                chr.getRingById(source.getRingId()).unequip();
            }
            chr.unequippedItem(source);
            eqpdInv.removeSlot(src);
        } finally {
            eqpdInv.unlockInventory();
        }

        if (target != null) {
            eqpInv.removeSlot(dst);
        }
        source.setPosition(dst);
        eqpInv.addItemFromDB(source);
        if (target != null) {
            target.setPosition(src);
            eqpdInv.addItemFromDB(target);
        }

        int petIndex = ItemConstants.PETS_NAME_TAG.indexOf(src);
        if (petIndex != -1) {
            Pet pet = chr.getPet(petIndex);
            if (pet != null) {
                chr.getMap().broadcastMessage(chr, PacketCreator.changePetName(chr, pet.getName(), (byte)petIndex), false);
            }
        }

        // Vanilla mode-2 swap. Wire oldPos to client-visible −bp (GetItem normal-only);
        // server may have held the item at cash mirror −(bp+100) after legacy migrate.
        final short wireOld = ExtendedEquipRegistry.isAddonAliasPairSeat(src)
                || ExtendedEquipRegistry.isAddonAliasPairSeat(askedSrc)
                ? ExtendedEquipRegistry.toClientWireSlot(src)
                : src;
        c.sendPacket(PacketCreator.modifyInventory(true,
                Collections.singletonList(new ModifyInventory(2, source, wireOld))));
        // Pet skill unequip — refresh PetSkill (bits may still OR; server pouch gate is authoritative).
        int unequippedId = source.getItemId();
        if (unequippedId >= 1802000 && unequippedId < 1842000) {
            for (byte i = 0; i < 3; i++) {
                if (chr.getPet(i) != null) {
                    chr.syncPetSkillsFromEquips(i);
                }
            }
        }
        chr.equipChanged();
        chr.forceUpdateLocalStats();
        // ADDON_STATS_UNEQUIP_20260802: 卸 Addon 后必须重推 STAT，否则 Occ-OFF 注入残留
        chr.forceSyncClientDisplayStats();
        org.gms.reincarnation.ReincarnationSupport.onUnequipped(chr, source.getItemId());
    }

    private static boolean isDisappearingItemDrop(Item it) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.isDropRestricted(it.getItemId())) {
            return true;
        } else if (ii.isCash(it.getItemId())) {
            if (GameConfig.getServerBoolean("use_enforce_unmerchable_cash")) {     // thanks Ari for noticing cash drops not available server-side
                return true;
            } else {
                return ItemConstants.isPet(it.getItemId()) && GameConfig.getServerBoolean("use_enforce_unmerchable_pet");
            }
        } else if (isDroppedItemRestricted(it)) {
            return true;
        } else {
            return ItemId.isWeddingRing(it.getItemId());
        }
    }

    public static void drop(Client c, InventoryType type, short src, short quantity) {
        if (src < 0) {
            type = InventoryType.EQUIPPED;
        }

        Character chr = c.getPlayer();
        Inventory inv = chr.getInventory(type);
        Item source = inv.getItem(src);

        if (chr.isGM() && chr.gmLevel() < GameConfig.getServerInt("minimum_gm_level_to_drop")) {
            chr.message("You cannot drop items at your GM level.");
            log.info("GM %s tried to drop item id %d", chr.getName(), source.getItemId());
            return;
        }

        if (chr.getTrade() != null || chr.getMiniGame() != null || source == null) { //Only check needed would prob be merchants (to see if the player is in one)
            return;
        }
        int itemId = source.getItemId();

        MapleMap map = chr.getMap();
        if ((!ItemConstants.isRechargeable(itemId) && source.getQuantity() < quantity) || quantity < 0) {
            return;
        }

        int petid = source.getPetId();
        if (petid > -1) {
            int petIdx = chr.getPetIndex(petid);
            if (petIdx > -1) {
                Pet pet = chr.getPet(petIdx);
                chr.unEquipPet(pet, true);
            }
        }

        Point dropPos = new Point(chr.getPosition());
        if (quantity < source.getQuantity() && !ItemConstants.isRechargeable(itemId)) {
            Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));
            c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, source))));

            if (ItemConstants.isNewYearCardEtc(itemId)) {
                if (itemId == ItemId.NEW_YEARS_CARD_SEND) {
                    NewYearCardRecord.removeAllNewYearCard(true, chr);
                    c.getAbstractPlayerInteraction().removeAll(ItemId.NEW_YEARS_CARD_SEND);
                } else {
                    NewYearCardRecord.removeAllNewYearCard(false, chr);
                    c.getAbstractPlayerInteraction().removeAll(ItemId.NEW_YEARS_CARD_RECEIVED);
                }
            }

            if (isDisappearingItemDrop(target)) {
                map.disappearingItemDrop(chr, chr, target, dropPos);
            } else {
                map.spawnItemDrop(chr, chr, target, dropPos, true, true);
            }
        } else {
            if (type == InventoryType.EQUIPPED) {
                inv.lockInventory();
                try {
                    chr.unequippedItem((Equip) source);
                    inv.removeSlot(src);
                } finally {
                    inv.unlockInventory();
                }
            } else {
                inv.removeSlot(src);
            }

            c.sendPacket(PacketCreator.modifyInventory(true, Collections.singletonList(new ModifyInventory(3, source))));
            if (src < 0) {
                chr.equipChanged();
            } else if (ItemConstants.isNewYearCardEtc(itemId)) {
                if (itemId == ItemId.NEW_YEARS_CARD_SEND) {
                    NewYearCardRecord.removeAllNewYearCard(true, chr);
                    c.getAbstractPlayerInteraction().removeAll(ItemId.NEW_YEARS_CARD_SEND);
                } else {
                    NewYearCardRecord.removeAllNewYearCard(false, chr);
                    c.getAbstractPlayerInteraction().removeAll(ItemId.NEW_YEARS_CARD_RECEIVED);
                }
            }

            if (isDisappearingItemDrop(source)) {
                map.disappearingItemDrop(chr, chr, source, dropPos);
            } else {
                map.spawnItemDrop(chr, chr, source, dropPos, true, true);
            }
        }

        int quantityNow = chr.getItemQuantity(itemId, false);
        if (itemId == chr.getItemEffect()) {
            if (quantityNow <= 0) {
                chr.setItemEffect(0);
                map.broadcastMessage(PacketCreator.itemEffect(chr.getId(), 0));
            }
        } else if (itemId == ItemId.CHALKBOARD_1 || itemId == ItemId.CHALKBOARD_2) {
            if (source.getQuantity() <= 0) {
                chr.setChalkboard(null);
            }
        } else if (itemId == ItemId.ARPQ_SPIRIT_JEWEL) {
            chr.updateAriantScore(quantityNow);
        }
    }

    private static boolean isDroppedItemRestricted(Item it) {
        return GameConfig.getServerBoolean("use_erase_untradeable_drop") && it.isUntradeable();
    }

    public static boolean isSandboxItem(Item it) {
        return (it.getFlag() & ItemConstants.SANDBOX) == ItemConstants.SANDBOX;
    }

    /**
     * FIX_LOGIN_CRASH_20260727ar: was FIX_RING34_RELOG_SYNC_20260727aq remove(3)+add(0)
     * right after getCharInfo. That packet sets addMovement=2 on equipped remove and runs
     * before CField/map is ready → char-select crash. Also remove-on-empty crashes if
     * login decode skipped the slot.
     * <p>
     * With ijl15 {@code ADDON_PERSIST_UNEQUIP_20260802}, login jg-allow 56–61 +
     * sidecar ApplyEquip install at DllMain (before getCharInfo). Do not re-sync
     * via INVENTORY_OPERATION — remove+add still risks enter-map crash (ar/aw).
     */
    public static void syncExtendedEquipSlotsToClient(Client c, Character chr) {
        // Intentional no-op (ar). Persist is client login-apply caves.
    }

    /**
     * KEEP OFF (ar/aw): equipped remove always sets addMovement=2 in modifyInventory.
     * Sending remove+add for −52…−61 on login — even after map.addPlayer — caused enter-map
     * crash /「数据无效」. Refill stays on ijl15 DllMain login caves only
     * ({@code ADDON_PERSIST_UNEQUIP_20260802}).
     */
    public static void syncExtendedEquipSlotsAfterMap(Client c, Character chr) {
        // Intentional no-op. Client DllMain persist; do not re-enable remove+add.
    }

    /**
     * FIX_ENTER_INVALID_20260727an: move cash rings off −152/−153 onto classic
     * cash ring slots −112…/−116 before getCharInfo (abandoned am cash extended).
     * <p>
     * FIX_EXT_KEEP_5253_20260728: do <b>NOT</b> migrate −52/−53 anymore — those are
     * real extended ring seats (shadow/CD64). Migrating cash=1 rings off them on
     * every PlayerLoggedin emptied red 3/4 after full client restart while soft
     * logout-to-login kept in-memory UI. Only −152/−153 + −154/−155 aliases move.
     */
    public static void migrateCashRingsOffExtendedSlots(Character chr) {
        if (chr == null) {
            return;
        }
        Inventory eqpd = chr.getInventory(InventoryType.EQUIPPED);
        Inventory eqpBag = chr.getInventory(InventoryType.EQUIP);
        // R26: −152/−153 are ExtraRing fashion-cash aliases of −52/−53 — do not migrate.
        migrateExtendedCashToNormal(eqpd, (short) -154, (short) -54);
        migrateExtendedCashToNormal(eqpd, (short) -155, (short) -55);
        migrateExtendedCashToNormal(eqpd, (short) -156, (short) -56);
        migrateExtendedCashToNormal(eqpd, (short) -157, (short) -57);
        migrateExtendedCashToNormal(eqpd, (short) -158, (short) -58);
        migrateExtendedCashToNormal(eqpd, (short) -159, (short) -59);
        migrateExtendedCashToNormal(eqpd, (short) -160, (short) -60);
        migrateExtendedCashToNormal(eqpd, (short) -161, (short) -61);
        migrateExtendedCashToNormal(eqpd, (short) -162, (short) -62);
        // ADDON_VANILLA_PARITY_20260804: do NOT migrate cash Addon items onto −154…−162.
        // Sidecar GetItem is normal-only; cash mirrors break mode-2 unequip / display.
        // One-way −154→−54 above is enough. (migrateCashTotemOrBadgeToCashSlot retired.)
        // ADDON_SLOTMAP_910: 166/167 曾误占宠物槽 −21/−22/−121/−122 → sidecar −60/−61/−160/−161
        migrateAndroidHeartOffPetSlots(eqpd, eqpBag);
        // Character cash/fashion (100xxxx hats etc.) must never stay on pet storage seats.
        migrateCharacterGearOffPetSlots(eqpd, eqpBag);
        // ADDON_AUX_SLOT62: 134/135 曾与盾共用 −10 → 独立 −62（可与 109 同穿）
        migrateAuxWeaponOffShieldSlot(eqpd, eqpBag);
    }

    /**
     * Move 134/135 off shared Si −10/−110 onto Addon aux −62/−162.
     * Leaves real shields (109) on −10. If −62 occupied, park to bag.
     * <p>
     * While {@link ExtendedEquipRegistry#GREEN_ENTER_OMIT_AUX62}: never park on −62
     * (client JG_ONLY cannot hold it). Flush −62/−162 and any aux still on −10/−110 → bag
     * before CharInfo so the item appears in bag inventory (no −62 wire).
     * When omit false: migrate −10→−62 / −110→−162 (replace conflict → bag).
     */
    private static void migrateAuxWeaponOffShieldSlot(Inventory eqpd, Inventory eqpBag) {
        if (ExtendedEquipRegistry.isGreenEnterWireOmit((short) -62)) {
            parkPrefixToBag(eqpd, eqpBag, (short) -10, 134);
            parkPrefixToBag(eqpd, eqpBag, (short) -10, 135);
            parkPrefixToBag(eqpd, eqpBag, (short) -110, 134);
            parkPrefixToBag(eqpd, eqpBag, (short) -110, 135);
            parkSeatToBag(eqpd, eqpBag, (short) -62);
            parkSeatToBag(eqpd, eqpBag, (short) -162);
            return;
        }
        migratePrefixSlot(eqpd, eqpBag, (short) -10, (short) -62, 134);
        migratePrefixSlot(eqpd, eqpBag, (short) -10, (short) -62, 135);
        // Cash aux on −110 → same sidecar −62 (not −162; GetItem normal-only).
        migratePrefixSlot(eqpd, eqpBag, (short) -110, (short) -62, 134);
        migratePrefixSlot(eqpd, eqpBag, (short) -110, (short) -62, 135);
    }

    /**
     * Mid-session: move wire-omitted aux (−62/−162) into the equip bag without referencing
     * −62 in the packet (login omit means client never had the ZRef). Always enableActions
     * when omit is active (sort/merge callers rely on unstick).
     * <p>
     * When {@link ExtendedEquipRegistry#GREEN_ENTER_OMIT_AUX62} is false: no-op (BP62 wires
     * −62; replace −62→bag is normal equip path). Call before 整理 while omit=true.
     */
    public static void recoverWireOmittedAuxToBag(Client c) {
        if (c == null || c.getPlayer() == null) {
            return;
        }
        // omit=false (BP62 green): leave −62 alone; sort/merge need no ghost flush.
        if (!ExtendedEquipRegistry.isGreenEnterWireOmit((short) -62)) {
            return;
        }
        Character chr = c.getPlayer();
        Inventory eqpd = chr.getInventory(InventoryType.EQUIPPED);
        Inventory eqpBag = chr.getInventory(InventoryType.EQUIP);
        List<ModifyInventory> mods = new ArrayList<>();
        for (short seat : new short[]{-62, -162}) {
            Item it = eqpd.getItem(seat);
            if (it == null) {
                continue;
            }
            short bag = eqpBag.getNextFreeSlot();
            if (bag <= 0) {
                log.warn("recoverWireOmittedAux: bag full seat={} id={} char={}", seat, it.getItemId(), chr.getName());
                continue;
            }
            eqpd.lockInventory();
            try {
                chr.unequippedItem((Equip) it);
                eqpd.removeSlot(seat);
            } finally {
                eqpd.unlockInventory();
            }
            it.setPosition(bag);
            eqpBag.addItemFromDB(it);
            // Bag-only add — do NOT send remove/move from −62 (client never held that seat).
            mods.add(new ModifyInventory(0, it.copy()));
            log.info("recoverWireOmittedAux: seat {} → bag {} id={} char={}", seat, bag, it.getItemId(), chr.getName());
        }
        if (!mods.isEmpty()) {
            c.sendPacket(PacketCreator.modifyInventory(true, mods));
            chr.equipChanged();
            chr.forceUpdateLocalStats();
            chr.forceSyncClientDisplayStats();
            chr.dropMessage(5, "已将未同步的辅助武器移回背包（−62 暂未对客户端开放）");
        }
        // Always unstick while omit active (even if no ghost) — 整理 must not hang.
        c.sendPacket(PacketCreator.enableActions());
    }

    private static void parkSeatToBag(Inventory eqpd, Inventory eqpBag, short seat) {
        Item it = eqpd.getItem(seat);
        if (it == null) {
            return;
        }
        eqpd.removeSlot(seat);
        short bag = eqpBag.getNextFreeSlot();
        if (bag > 0) {
            it.setPosition(bag);
            eqpBag.addItemFromDB(it);
            log.info("parkSeatToBag: {} → bag {} id={}", seat, bag, it.getItemId());
        } else {
            it.setPosition(seat);
            eqpd.addItemFromDB(it);
            log.warn("parkSeatToBag: bag full, left at {} id={}", seat, it.getItemId());
        }
    }

    private static void parkPrefixToBag(Inventory eqpd, Inventory eqpBag, short from, int expectPrefix) {
        Item it = eqpd.getItem(from);
        if (it == null || it.getItemId() / 10000 != expectPrefix) {
            return;
        }
        eqpd.removeSlot(from);
        short bag = eqpBag.getNextFreeSlot();
        if (bag > 0) {
            it.setPosition(bag);
            eqpBag.addItemFromDB(it);
            log.info("parkPrefixToBag: {} → bag {} id={}", from, bag, it.getItemId());
        } else {
            it.setPosition(from);
            eqpd.addItemFromDB(it);
            log.warn("parkPrefixToBag: bag full, left at {} id={}", from, it.getItemId());
        }
    }

    /**
     * Move Android(166)/Heart(167) off v083 pet seats (−21/−22/−121/−122) onto sidecar
     * −60/−61/−160/−161. Leave real pet items (180–183) untouched.
     */
    private static void migrateAndroidHeartOffPetSlots(Inventory eqpd, Inventory eqpBag) {
        migratePrefixSlot(eqpd, eqpBag, (short) -21, (short) -60, 166);
        migratePrefixSlot(eqpd, eqpBag, (short) -121, (short) -160, 166);
        migratePrefixSlot(eqpd, eqpBag, (short) -22, (short) -61, 167);
        migratePrefixSlot(eqpd, eqpBag, (short) -122, (short) -161, 167);
    }

    /**
     * Evict non-pet gear from pet storage seats (e.g. cash hat −101 wrongly on −121).
     * Real pet equips (180–183) stay. Parks to equip bag.
     */
    private static void migrateCharacterGearOffPetSlots(Inventory eqpd, Inventory eqpBag) {
        // Mirror EquipSlot.PET_BODY_PARTS: −bp and −(bp+100).
        final int[] petBps = {
                14,
                21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38,
                39, 40, 41, 42, 43, 44, 45, 46, 47, 48
        };
        for (int bp : petBps) {
            parkNonPetFromSeat(eqpd, eqpBag, (short) -bp);
            parkNonPetFromSeat(eqpd, eqpBag, (short) -(bp + 100));
        }
    }

    private static void parkNonPetFromSeat(Inventory eqpd, Inventory eqpBag, short seat) {
        Item it = eqpd.getItem(seat);
        if (it == null) {
            return;
        }
        final int itemId = it.getItemId();
        final int prefix = itemId / 10000;
        if (prefix >= 180 && prefix <= 183) {
            return;
        }
        // Pocket 116 may legitimately use −33/−133 (shares BP33 with pet#2 pouch).
        if (prefix == 116 && (seat == -33 || seat == -133)) {
            return;
        }
        parkSeatToBag(eqpd, eqpBag, seat);
        log.info("migrateCharOffPet: seat {} id={} (non-pet on pet seat)", seat, itemId);
    }

    private static void migratePrefixSlot(Inventory eqpd, Inventory eqpBag, short from, short to,
                                          int expectPrefix) {
        Item it = eqpd.getItem(from);
        if (it == null || it.getItemId() / 10000 != expectPrefix) {
            return;
        }
        eqpd.removeSlot(from);
        if (eqpd.getItem(to) == null) {
            it.setPosition(to);
            eqpd.addItemFromDB(it);
            log.info("migrateAndroidHeart: {} → {} id={}", from, to, it.getItemId());
            return;
        }
        short bag = eqpBag.getNextFreeSlot();
        if (bag > 0) {
            it.setPosition(bag);
            eqpBag.addItemFromDB(it);
            log.info("migrateAndroidHeart: {} → bag {} id={}", from, bag, it.getItemId());
        } else {
            it.setPosition(from);
            eqpd.addItemFromDB(it);
            log.warn("migrateAndroidHeart: stuck at {} id={} (dest+bag full)", from, it.getItemId());
        }
    }

    /**
     * Minimal unlock after Addon early-reject.
     * <p>
     * FORBIDDEN (ADDON_FORBIDDEN_OPS): empty {@code INVENTORY_OPERATION}, equipped
     * mode-3 ghost clear (addMovement=2 → tip hang), dual alias clears, Addon
     * {@code forceUpdateItem}. Prefer STAT {@code enableActions} only —
     * {@link org.gms.net.server.channel.handlers.ItemMoveHandler} finally also sends it.
     */
    /**
     * Unequip every equipped item whose prefix matches except the target seat
     * and its cash alias. Returns false if bag full (caller must abort wear).
     */
    private static boolean stripStrayPrefix(Client c, Inventory eqpdInv, Inventory eqpInv,
                                           int prefix, short dst) {
        return stripStrayPrefixes(c, eqpdInv, eqpInv, new int[]{prefix}, dst);
    }

    private static boolean stripStrayPrefixes(Client c, Inventory eqpdInv, Inventory eqpInv,
                                             int[] prefixes, short dst) {
        java.util.ArrayList<Short> stray = new java.util.ArrayList<>();
        for (Item occ : eqpdInv.list()) {
            if (occ == null) {
                continue;
            }
            short opos = occ.getPosition();
            if (opos == dst || opos == (short) (dst - 100) || opos == (short) (dst + 100)) {
                continue;
            }
            int op = occ.getItemId() / 10000;
            for (int p : prefixes) {
                if (op == p) {
                    stray.add(opos);
                    break;
                }
            }
        }
        for (short s : stray) {
            if (eqpInv.isFull()) {
                c.sendPacket(PacketCreator.getInventoryFull());
                c.sendPacket(PacketCreator.enableActions());
                return false;
            }
            unequip(c, s, eqpInv.getNextFreeSlot());
        }
        return true;
    }

    private static void unstickClientInventory(Client c) {
        if (c == null) {
            return;
        }
        c.sendPacket(PacketCreator.enableActions());
    }

    /**
     * @deprecated FORBIDDEN — equipped mode-3 sets addMovement=2 and hung Client_1 tip
     * (2026-08-04 23:08 ALL_IDLE). Do not call. Ghost clear = client local sidecar only.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private static void appendEquippedGhostClear(List<ModifyInventory> mods, short pos) {
        // intentionally no-op — see ADDON_FORBIDDEN_OPS.md
    }

    private static void migrateCashTotemOrBadgeToCashSlot(Inventory eqpd, short fromNormal, short toCash) {
        Item it = eqpd.getItem(fromNormal);
        if (it == null || !ItemInformationProvider.getInstance().isCash(it.getItemId())) {
            return;
        }
        if (eqpd.getItem(toCash) != null) {
            // Occupied: leave layout alone (no swap — preserves Addon seat order on relog).
            return;
        }
        eqpd.removeSlot(fromNormal);
        it.setPosition(toCash);
        eqpd.addItemFromDB(it);
        log.info("migrateCashSlot: {} → {} (empty dest only)", fromNormal, toCash);
    }

    private static void migrateExtendedCashToNormal(Inventory eqpd, short from, short to) {
        Item it = eqpd.getItem(from);
        if (it == null) {
            return;
        }
        // 点装徽章/图腾保留 cash 槽（−154/−155），否则会与普通 −54/−55 互相顶替
        if (ItemInformationProvider.getInstance().isCash(it.getItemId())) {
            return;
        }
        // Non-cash wrongly on cash alias: move only if normal seat empty — never swap.
        if (eqpd.getItem(to) != null) {
            return;
        }
        eqpd.removeSlot(from);
        it.setPosition(to);
        eqpd.addItemFromDB(it);
        log.info("migrateExtCash: {} → {} (empty dest only)", from, to);
    }

    /**
     * When client mis-routes to pocket −33/−133 (HT overlap / GetBodyPartFromPoint
     * invent), map back to the item's canonical classic slot from WZ islot / registry
     * prefix. Never returns Po/−33 or Aw/−62 for the misrouted item itself (caller
     * already excluded those prefixes).
     * Note: aux −62/−162 fashion misroute is REJECTED by equip() (R31) — not remapped.
     */
    private static short resolveMisroutedExtendedDst(ItemInformationProvider ii, int itemId,
                                                     boolean cash, String slotName) {
        if (itemId <= 0 || slotName == null) {
            return 0;
        }
        if (EquipSlot.POCKET.getName().equals(slotName)
                || EquipSlot.AUX_WEAPON.getName().equals(slotName)) {
            return 0;
        }
        final int prefix = itemId / 10000;
        short fixed = ExtendedEquipRegistry.resolveFixedDst(prefix, cash);
        if (fixed != 0) {
            // Never "correct" into the seat we are escaping (would no-op / loop).
            if (fixed == -33 || fixed == -133 || fixed == -62 || fixed == -162) {
                return 0;
            }
            return fixed;
        }
        EquipSlot slot = EquipSlot.getFromTextSlot(slotName);
        if (slot == EquipSlot.PET_EQUIP) {
            return 0;
        }
        return switch (slotName) {
            case "Cp", "HrCp" -> (short) (cash ? -101 : -1);
            case "Af" -> (short) (cash ? -102 : -2);
            case "Ay" -> (short) (cash ? -103 : -3);
            case "Ae" -> (short) (cash ? -104 : -4);
            case "Ma", "MaPn" -> (short) (cash ? -105 : -5);
            case "Pn" -> (short) (cash ? -106 : -6);
            case "So" -> (short) (cash ? -107 : -7);
            case "GlGw", "Gv" -> (short) (cash ? -108 : -8);
            case "Sr" -> (short) (cash ? -109 : -9);
            case "Si" -> (short) (cash ? -110 : -10);
            case "Wp", "WpSi", "WpSp" -> (short) (cash ? -111 : -11);
            case "Ri" -> (short) (cash ? -112 : -12);
            case "Pe" -> (short) (cash ? -117 : -17);
            case "Tm" -> -18;
            case "Sd" -> -19;
            case "Sh" -> -20;
            case "Me" -> -49;
            case "Be" -> (short) (cash ? -150 : -50);
            default -> 0;
        };
    }
}
