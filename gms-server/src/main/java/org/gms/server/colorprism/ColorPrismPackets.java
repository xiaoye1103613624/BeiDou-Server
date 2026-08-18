package org.gms.server.colorprism;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;
import org.gms.server.maps.MapleMap;

import java.util.ArrayList;
import java.util.List;

/**
 * LP_WeaponTint (0x372F): tells the client which of a character's Cash equips have been dyed and to what,
 * so {@code DLL/src/weapontint.cpp} can recolour their sprites at render time. The v83 client has no
 * item-dye system at all, so every byte of this feature is a side channel; nothing is folded into the
 * item packet.
 * <p>
 * Two shapes, because they answer different questions. The SNAPSHOT is what the recipient owns, worn or
 * not, since the prism window must show the stored colour of an item you have only just dropped on it.
 * The MAP TABLE is what everyone in the map is WEARING, since that is all onlookers can see.
 *
 * @see net.server.channel.handlers.WeaponTintHandler
 */
public final class ColorPrismPackets {
    /** Client -&gt; server actions. Must match {@code DLL/src/weapontint.cpp}. */
    public static final byte ACTION_REQUEST_SNAPSHOT = 0;
    public static final byte ACTION_APPLY = 1;
    public static final byte ACTION_RESTORE = 2;
    /** Hair / Face. Separate actions because they carry no inventory address to re-verify. */
    public static final byte ACTION_APPLY_LOOK = 3;
    public static final byte ACTION_RESTORE_LOOK = 4;

    /** Server -&gt; client subtypes. Must match {@code DLL/src/weapontint.cpp}. */
    private static final byte RESP_SNAPSHOT = 1;
    private static final byte RESP_RESULT = 2;
    private static final byte RESP_REMOTE_TABLE = 3;

    /**
     * Guards on the wire size. A character cannot realistically wear or hoard more dyed equips than this,
     * and a v83 map holds far fewer than MAX_PLAYERS; both caps exist so a crowded map cannot produce a
     * packet the client's own bounded parse would reject anyway. Both counts are sent as a BYTE, so
     * neither may exceed 255.
     */
    private static final int MAX_ENTRIES = 120;
    private static final int MAX_PLAYERS = 200;

    /**
     * The window's Hair and Face tabs ride this same channel as two extra entries whose
     * "itemId" is a KIND SENTINEL rather than a real id. 1 and 2 cannot collide with the equip
     * ids sharing the table, which are all 1xxxxxx, and the client's reader admits them
     * unchanged -- its only filter is {@code itemId > 0}. Must match {@code kTintKey_Hair} /
     * {@code kTintKey_Face} in {@code DLL/src/weapontint.h}.
     * <p>
     * Keyed by KIND and not by the hair/face WZ id on purpose: the client already knows which
     * hair and face an avatar wears (it reads them off that avatar's own look, which for a
     * remote player is the only correct source), and a colour the player chose should survive
     * a restyle.
     */
    public static final int TINT_KEY_HAIR = 1;
    public static final int TINT_KEY_FACE = 2;

    /**
     * An item's EFFECT sprites carry a second tint of their own, so they need a second key per
     * item. Biasing the id keeps one table and one wire format: equip ids top out at 1999999, so
     * id + 10000000 collides with no real id, neither look sentinel, and no other item's effect
     * key. Must match {@code kTintKey_EffectBias} in {@code DLL/src/weapontint.h}.
     */
    public static final int TINT_KEY_EFFECT_BIAS = 10000000;

    /** Which half of an item a tint applies to. Matches {@code kTintLayer_*} in weapontint.h. */
    public static final int LAYER_BODY = 0;
    public static final int LAYER_EFFECTS = 1;

    /** Result codes, mirrored by {@code WeaponTintResult} in {@code DLL/src/weapontint.h}. */
    public static final byte RESULT_OK = 1;
    public static final byte RESULT_NO_CASH_WEAPON = 2;
    public static final byte RESULT_NO_ITEM = 3;
    public static final byte RESULT_NOT_TINTED = 4;
    public static final byte RESULT_FAILED = 5;

    private ColorPrismPackets() {
    }

    /**
     * Every DYED equip this character owns, in either the EQUIP tab or worn. Unworn items are
     * included because the prism window has to show the stored colour of the item you just dropped
     * on it, which may not be on your back.
     */
    public static Packet snapshot(Character player) {
        List<Equip> dyed = new ArrayList<>();
        collectDyed(player, InventoryType.EQUIPPED, dyed);
        collectDyed(player, InventoryType.EQUIP, dyed);

        List<int[]> looks = looksOf(player);

        OutPacket p = OutPacket.create(SendOpcode.WEAPON_TINT_SYNC);
        p.writeByte(RESP_SNAPSHOT);
        // The look entries are appended OUTSIDE the MAX_ENTRIES cap: that cap bounds the
        // equip list, and dropping a player's hair colour because they happen to own 120
        // dyed equips would be an odd way to enforce it. The count is a byte, so the
        // ceiling that actually matters is 255.
        p.writeByte(countEntries(dyed) + looks.size());
        writeEntries(p, dyed);
        writeLooks(p, looks);
        return p;
    }

    /**
     * This character's non-identity look tints as {kind, hue, chroma, bright} rows. Empty when
     * neither is tinted, which is the common case and costs nothing on the wire.
     */
    private static List<int[]> looksOf(Character player) {
        List<int[]> out = new ArrayList<>(2);
        if (player.isHairTinted()) {
            out.add(new int[]{TINT_KEY_HAIR, player.getHairTintHue(),
                    player.getHairTintChroma(), player.getHairTintBright()});
        }
        if (player.isFaceTinted()) {
            out.add(new int[]{TINT_KEY_FACE, player.getFaceTintHue(),
                    player.getFaceTintChroma(), player.getFaceTintBright()});
        }
        return out;
    }

    /**
     * Every player in {@code map} who is wearing at least one dyed equip, keyed by character NAME.
     * <p>
     * <b>Why the name and not the character id.</b> The client has to answer "which character is
     * this avatar?" for an arbitrary {@code CAvatar} the renderer hands it, and the only identity
     * reachable from there is the string at {@code CUser+0x11AC} - this client has no usable
     * {@code CUserPool}, and the id is not stored anywhere the DLL can find it. Names are unique per
     * world, so they key the table exactly as well.
     * <p>
     * WORN items only: that is all anybody else can see. A player with no dyed equip on is omitted
     * rather than sent empty, and the client treats a name vanishing from the table as "back to
     * vanilla" and repaints it, so absence is meaningful and costs nothing.
     */
    public static Packet mapTable(MapleMap map) {
        List<Character> players = new ArrayList<>();
        List<List<Equip>> perPlayer = new ArrayList<>();
        List<List<int[]>> perPlayerLooks = new ArrayList<>();
        for (Character other : map.getCharacters()) {
            List<Equip> dyed = new ArrayList<>();
            collectDyed(other, InventoryType.EQUIPPED, dyed);
            List<int[]> looks = looksOf(other);
            if (!dyed.isEmpty() || !looks.isEmpty()) {
                perPlayerLooks.add(looks);
                players.add(other);
                perPlayer.add(dyed);
                if (players.size() >= MAX_PLAYERS) {
                    break;
                }
            }
        }

        OutPacket p = OutPacket.create(SendOpcode.WEAPON_TINT_SYNC);
        p.writeByte(RESP_REMOTE_TABLE);
        p.writeByte(players.size());
        for (int i = 0; i < players.size(); i++) {
            List<Equip> dyed = perPlayer.get(i);
            List<int[]> looks = perPlayerLooks.get(i);
            p.writeString(players.get(i).getName());
            p.writeByte(countEntries(dyed) + looks.size());
            writeEntries(p, dyed);
            writeLooks(p, looks);
        }
        return p;
    }

    /**
     * Pushes the current map's table to everyone standing in it. Called when a player arrives,
     * changes equipment, or dyes something - every event that can change what anybody in the map
     * should be seeing.
     */
    public static void broadcastMapTable(MapleMap map) {
        if (map == null) {
            return;
        }
        map.broadcastMessage(mapTable(map));
    }

    private static void collectDyed(Character player, InventoryType type, List<Equip> out) {
        Inventory inv = player.getInventory(type);
        inv.lockInventory();
        try {
            for (Item item : inv.list()) {
                if (item instanceof Equip equip && (equip.isTinted() || equip.isFxTinted())
                        && out.size() < MAX_ENTRIES) {
                    out.add(equip);
                }
            }
        } finally {
            inv.unlockInventory();
        }
    }

    /**
     * itemId(4) hue(2) chroma(1) bright(1), matching the reader in weapontint.cpp.
     * <p>
     * An equip can contribute TWO entries: its body tint under its own id, and its effect tint
     * under the biased id. Either may be absent, so the count must be taken from
     * {@link #countEntries} rather than from the list size.
     */
    private static void writeEntries(OutPacket p, List<Equip> dyed) {
        int n = 0;
        for (Equip equip : dyed) {
            if (n >= MAX_ENTRIES) {
                break;
            }
            if (equip.isTinted()) {
                p.writeInt(equip.getItemId());
                p.writeShort(equip.getTintHue());
                p.writeByte(equip.getTintChroma());
                p.writeByte(equip.getTintBright());
                n++;
            }
            if (n < MAX_ENTRIES && equip.isFxTinted()) {
                p.writeInt(equip.getItemId() + TINT_KEY_EFFECT_BIAS);
                p.writeShort(equip.getTintFxHue());
                p.writeByte(equip.getTintFxChroma());
                p.writeByte(equip.getTintFxBright());
                n++;
            }
        }
    }

    /** How many entries {@link #writeEntries} will actually emit for this list. */
    private static int countEntries(List<Equip> dyed) {
        int n = 0;
        for (Equip equip : dyed) {
            if (n < MAX_ENTRIES && equip.isTinted()) {
                n++;
            }
            if (n < MAX_ENTRIES && equip.isFxTinted()) {
                n++;
            }
        }
        return n;
    }

    /** Same 8-byte entry shape as an equip's, with the kind sentinel in the itemId field. */
    private static void writeLooks(OutPacket p, List<int[]> looks) {
        for (int[] e : looks) {
            p.writeInt(e[0]);
            p.writeShort(e[1]);
            p.writeByte(e[2]);
            p.writeByte(e[3]);
        }
    }

    public static Packet result(byte code) {
        OutPacket p = OutPacket.create(SendOpcode.WEAPON_TINT_SYNC);
        p.writeByte(RESP_RESULT);
        p.writeByte(code);
        return p;
    }

}
