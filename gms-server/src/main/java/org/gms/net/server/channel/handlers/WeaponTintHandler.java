package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.ItemInformationProvider;
import org.gms.server.colorprism.ColorPrismPackets;

/**
 * Handles {@code RecvOpcode.WEAPON_TINT_ACTION} (0x372E), emitted by the client DLL
 * ({@code DLL/src/weapontint.cpp}) for the Coloring Prism window.
 * <ul>
 *   <li>{@code 0} request - resend every tint this character owns</li>
 *   <li>{@code 1} apply       - invType(1) invPos(2) itemId(4) hue(2) chroma(1) bright(1) prismPos(2) layer(1)</li>
 *   <li>{@code 2} restore     - invType(1) invPos(2) itemId(4) prismPos(2) layer(1)</li>
 *   <li>{@code 3} applyLook   - kind(1) hue(2) chroma(1) bright(1) prismPos(2)</li>
 *   <li>{@code 4} restoreLook - kind(1) prismPos(2)</li>
 * </ul>
 * ONE item, {@link ItemId#COLORING_PRISM}, pays for all four of the window's tabs. Actions 1 and 2
 * dye an EQUIP -- {@code layer} picks its body or its effect sprites -- and name their target
 * explicitly. Actions 3 and 4 dye the character's own HAIR or FACE, which have no inventory address,
 * so they carry a kind instead and the server reads the look off the character. Everything the client sends is a HINT and is re-derived here: the
 * item at that position must still exist, must still be the id the client thinks it is, and must be
 * a Cash equip. The prism itself is validated the same way - by ownership rather than by the slot
 * it was double-clicked from - which is the rule {@link GachaponTicketHandler} follows.
 */
public final class WeaponTintHandler extends AbstractPacketHandler {

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character player = c.getPlayer();
        if (player == null || !c.isLoggedIn() || !player.isLoggedInWorld()) {
            return;
        }
        if (p.available() < Byte.BYTES) {
            return;
        }

        byte action = p.readByte();
        switch (action) {
            case ColorPrismPackets.ACTION_REQUEST_SNAPSHOT -> player.sendPacket(ColorPrismPackets.snapshot(player));
            case ColorPrismPackets.ACTION_APPLY -> {
                if (p.available() < 1 + 2 + 4 + 2 + 1 + 1 + 2 + 1) {
                    return;
                }
                Target target = readTarget(p);
                int hue = p.readShort() & 0xFFFF;
                int chroma = p.readByte();
                int bright = p.readByte();
                short prismPos = p.readShort();
                int layer = p.readByte();
                handleApply(c, player, target, hue, chroma, bright, prismPos, layer);
            }
            case ColorPrismPackets.ACTION_RESTORE -> {
                if (p.available() < 1 + 2 + 4 + 2 + 1) {
                    return;
                }
                Target target = readTarget(p);
                short prismPos = p.readShort();
                int layer = p.readByte();
                handleRestore(c, player, target, prismPos, layer);
            }
            // Hair / Face. No target: hair and eyes have no inventory address, so
            // the server reads the character's own look instead and there is nothing here the
            // client could lie about beyond which of the two it means.
            case ColorPrismPackets.ACTION_APPLY_LOOK -> {
                if (p.available() < 1 + 2 + 1 + 1 + 2) {
                    return;
                }
                int kind = p.readByte();
                int hue = p.readShort() & 0xFFFF;
                int chroma = p.readByte();
                int bright = p.readByte();
                short prismPos = p.readShort();
                handleApplyLook(c, player, kind, hue, chroma, bright, prismPos);
            }
            case ColorPrismPackets.ACTION_RESTORE_LOOK -> {
                if (p.available() < 1 + 2) {
                    return;
                }
                int kind = p.readByte();
                short prismPos = p.readShort();
                handleRestoreLook(c, player, kind, prismPos);
            }
            default -> {
            }
        }
    }

    private static boolean isLookKind(int kind) {
        return kind == ColorPrismPackets.TINT_KEY_HAIR || kind == ColorPrismPackets.TINT_KEY_FACE;
    }

    private void handleApplyLook(Client c, Character player, int kind, int hue, int chroma, int bright,
                                 short prismPos) {
        if (!isLookKind(kind)) {
            fail(player, ColorPrismPackets.RESULT_FAILED, "That can't be dyed with a Coloring Prism.");
            return;
        }
        short slot = findItem(player, ItemId.COLORING_PRISM, prismPos);
        if (slot == 0) {
            fail(player, ColorPrismPackets.RESULT_NO_ITEM, "You don't have a Coloring Prism.");
            return;
        }
        if (kind == ColorPrismPackets.TINT_KEY_HAIR) {
            player.setHairTint(hue, chroma, bright);
        } else {
            player.setFaceTint(hue, chroma, bright);
        }
        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, slot, (short) 1, false);
        player.saveCharToDB();
        succeed(player);
    }

    private void handleRestoreLook(Client c, Character player, int kind, short prismPos) {
        if (!isLookKind(kind)) {
            fail(player, ColorPrismPackets.RESULT_FAILED, "That can't be dyed with a Coloring Prism.");
            return;
        }
        final boolean tinted = (kind == ColorPrismPackets.TINT_KEY_HAIR)
                ? player.isHairTinted() : player.isFaceTinted();
        if (!tinted) {
            // Nothing to undo. Refuse WITHOUT consuming the prism -- this is the path a player
            // takes by pressing Reset then Confirm on an already-vanilla colour, and burning
            // their item for a no-op would be a trap.
            fail(player, ColorPrismPackets.RESULT_NOT_TINTED,
                    kind == ColorPrismPackets.TINT_KEY_HAIR
                            ? "Your hair is already its original color."
                            : "Your eyes are already their original color.");
            return;
        }
        short slot = findItem(player, ItemId.COLORING_PRISM, prismPos);
        if (slot == 0) {
            fail(player, ColorPrismPackets.RESULT_NO_ITEM, "You don't have a Coloring Prism.");
            return;
        }
        if (kind == ColorPrismPackets.TINT_KEY_HAIR) {
            player.clearHairTint();
        } else {
            player.clearFaceTint();
        }
        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, slot, (short) 1, false);
        player.saveCharToDB();
        succeed(player);
    }

    private record Target(byte invType, short invPos, int itemId) {
    }

    private static Target readTarget(InPacket p) {
        return new Target(p.readByte(), p.readShort(), p.readInt());
    }

    private void handleApply(Client c, Character player, Target target, int hue, int chroma, int bright,
                             short prismPos, int layer) {
        Equip equip = resolve(player, target);
        if (equip == null) {
            fail(player, ColorPrismPackets.RESULT_NO_CASH_WEAPON,
                    "That item can't be dyed with a Coloring Prism.");
            return;
        }
        short slot = findItem(player, ItemId.COLORING_PRISM, prismPos);
        if (slot == 0) {
            fail(player, ColorPrismPackets.RESULT_NO_ITEM, "You don't have a Coloring Prism.");
            return;
        }

        // The window's Equip and Effects tabs dye the same item under different keys.
        if (layer == ColorPrismPackets.LAYER_EFFECTS) {
            equip.setFxTint(hue, chroma, bright);
        } else {
            equip.setTint(hue, chroma, bright);
        }
        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, slot, (short) 1, false);
        // The equip object is the live one in the player's inventory, so the tint is persisted by
        // the normal character save. Force one now so a crash between here and logout cannot lose a
        // consumed prism's worth of colour.
        player.saveCharToDB();
        succeed(player);
    }

    private void handleRestore(Client c, Character player, Target target, short prismPos, int layer) {
        Equip equip = resolve(player, target);
        if (equip == null) {
            fail(player, ColorPrismPackets.RESULT_NO_CASH_WEAPON,
                    "That item can't be dyed with a Coloring Prism.");
            return;
        }
        final boolean fx = layer == ColorPrismPackets.LAYER_EFFECTS;
        if (fx ? !equip.isFxTinted() : !equip.isTinted()) {
            // Nothing to undo: report it and leave the item alone rather than burning it for no effect.
            fail(player, ColorPrismPackets.RESULT_NOT_TINTED, "That item is already its original color.");
            return;
        }
        short slot = findItem(player, ItemId.COLORING_PRISM, prismPos);
        if (slot == 0) {
            fail(player, ColorPrismPackets.RESULT_NO_ITEM, "You don't have a Coloring Prism.");
            return;
        }

        if (fx) {
            equip.clearFxTint();
        } else {
            equip.clearTint();
        }
        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, slot, (short) 1, false);
        player.saveCharToDB();
        succeed(player);
    }

    /**
     * The Cash equip the client is pointing at, or null if it is not one, is not there, or is not
     * what the client said it was. The position is only a hint - an inventory move between the drop
     * and the Confirm would otherwise dye whatever landed in that slot instead.
     * <p>
     * <b>The SIGN of the position picks the inventory, not the reported type.</b> The v83 client
     * calls an equip's inventory EQUIP(1) whether it is worn or sitting in the tab, and distinguishes
     * the two by addressing a worn item at {@code -bodypart} and a worn CASH item at
     * {@code -(100 + bodypart)}. Server-side those both live in EQUIPPED(-1), which spans -1..-128
     * ({@link client.inventory.manipulator.InventoryManipulator} line 467). Trusting the client's
     * type byte for a negative position looks in EQUIP, finds nothing, and rejects every worn item -
     * which is every item a player actually wants to dye.
     */
    private static Equip resolve(Character player, Target target) {
        InventoryType type = target.invPos() < 0
                ? InventoryType.EQUIPPED
                : InventoryType.getByType(target.invType());
        if (type == null) {
            return null;
        }
        Inventory inv = player.getInventory(type);
        inv.lockInventory();
        try {
            Item item = inv.getItem(target.invPos());
            if (!(item instanceof Equip equip)) {
                return null;
            }
            if (equip.getItemId() != target.itemId()) {
                return null;
            }
            if (!ItemInformationProvider.getInstance().isCash(equip.getItemId())) {
                return null;
            }
            return equip;
        } finally {
            inv.unlockInventory();
        }
    }

    private void succeed(Character player) {
        player.sendPacket(ColorPrismPackets.result(ColorPrismPackets.RESULT_OK));
        player.sendPacket(ColorPrismPackets.snapshot(player));
        ColorPrismPackets.broadcastMapTable(player.getMap());
    }

    /**
     * The result code is what the DLL reads; the chat line is what the player reads. Both are sent
     * because the prism window closes the moment Confirm is pressed, so a code alone would be a
     * silent no-op. The snapshot that follows undoes the client's optimistic recolour (see
     * {@code WeaponTint_AdoptOptimistic}).
     */
    private void fail(Character player, byte code, String message) {
        player.sendPacket(ColorPrismPackets.result(code));
        player.sendPacket(ColorPrismPackets.snapshot(player));
        player.dropMessage(1, message);
    }

    /**
     * The CASH-inventory slot holding {@code itemId}, preferring {@code hintPos} when it still holds
     * that item. Returns 0 when the player does not own one - 0 is not a valid inventory position,
     * so it doubles as "none".
     */
    private static short findItem(Character player, int itemId, short hintPos) {
        var cash = player.getInventory(InventoryType.CASH);
        cash.lockInventory();
        try {
            if (hintPos > 0) {
                Item at = cash.getItem(hintPos);
                if (at != null && at.getItemId() == itemId) {
                    return hintPos;
                }
            }
            for (Item item : cash.list()) {
                if (item.getItemId() == itemId) {
                    return item.getPosition();
                }
            }
        } finally {
            cash.unlockInventory();
        }
        return 0;
    }
}
