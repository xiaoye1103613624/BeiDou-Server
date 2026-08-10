package org.gms.constants.inventory;

import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Extended equip seat registry — Java mirror of BeiDou-ijl15
 * {@code extendequip/ExtendedBodyPart.h}.
 * <p>
 * Policy = live {@code ADDON_APPEND_ONLY_EXTEND_20260804} ({@code 60A3E54F}):
 * <b>sidecar = storage only; logic = vanilla parity</b> (mode-2 wear/unequip, occupy
 * reject unlocks via {@code enableActions}, no empty invent / dual alias / forceUpdate).
 * ExtraRing client Get/Set+UI ON, Occ/−152 OFF (still server-blind for STAT);
 * Addon −54…−62 blind (sidecar arena on vanilla 52-slot aEquipped); pocket −33 / Si −10
 * never blind (UI = Addon row3); cash rings never −152/−153.
 * Aux −62: gated by {@link #GREEN_ENTER_OMIT_AUX62} (true = omit wire / reject wear).
 * <p>
 * Sidecar wire: always normal band −54…−62 (cash item flag does <b>not</b> move storage
 * to −154…−162). Client GetItem hook is normal-only; −154 invent ops miss arena → AV/空白.
 * <p>
 * Occupy: {@link #isStrictOccupySeat(short)} is <b>documentation only</b> — server
 * <b>replaces</b> like classic (never hard-reject). Client may UX-hint when GetItem
 * non-null; empty invent / dual mode-3 / forceUpdate remain FORBIDDEN.
 */
public final class ExtendedEquipRegistry {

    private ExtendedEquipRegistry() {
    }

    public enum UiSeat { MAIN_EQUIP, ADDON_DOCK, HIDDEN_PARK }

    public enum StorageTier { NATIVE_APPLY_MAX_55, SHADOW_GET_SET, SIDECAR_ZREF }

    public enum DrawPolicy { VANILLA, REMAP_XY, ADDON_OVERLAY, NEVER_DRAW_ENTER }

    public enum WireWhen { ALWAYS, POST_FIELD, EQUIP_OPEN, GATED_OFF }

    /**
     * @param slots equipped inventory positions (negative), non-cash first
     */
    public record Seat(
            int bp,
            int prefix,
            String islot,
            UiSeat uiSeat,
            StorageTier storage,
            boolean clientBlind,
            DrawPolicy drawPolicy,
            WireWhen wireWhen,
            short[] slots,
            String notes
    ) {
    }

    /** Live table — keep in sync with C++ {@code ExtEquip::kBodyParts}. */
    public static final List<Seat> SEATS = Collections.unmodifiableList(Arrays.asList(
            new Seat(20, 115, "Sh", UiSeat.MAIN_EQUIP, StorageTier.NATIVE_APPLY_MAX_55,
                    false, DrawPolicy.REMAP_XY, WireWhen.ALWAYS, new short[]{-20}, "shoulder"),
            new Seat(51, 112, "Pe", UiSeat.MAIN_EQUIP, StorageTier.NATIVE_APPLY_MAX_55,
                    false, DrawPolicy.REMAP_XY, WireWhen.POST_FIELD, new short[]{-51, -151}, "pendant2"),
            new Seat(52, 111, "Ri", UiSeat.MAIN_EQUIP, StorageTier.SHADOW_GET_SET,
                    true, DrawPolicy.REMAP_XY, WireWhen.ALWAYS, new short[]{-52}, "ExtraRing red3"),
            new Seat(53, 111, "Ri", UiSeat.MAIN_EQUIP, StorageTier.SHADOW_GET_SET,
                    true, DrawPolicy.REMAP_XY, WireWhen.ALWAYS, new short[]{-53}, "ExtraRing red4"),
            // Pocket −33 Addon row3; 109 shield −10 vanilla; 134/135 aux −62 Addon (true split).
            new Seat(33, 116, "Po", UiSeat.ADDON_DOCK, StorageTier.NATIVE_APPLY_MAX_55,
                    false, DrawPolicy.ADDON_OVERLAY, WireWhen.EQUIP_OPEN, new short[]{-33, -133}, "pocket Addon row3"),
            new Seat(10, 109, "Si", UiSeat.MAIN_EQUIP, StorageTier.NATIVE_APPLY_MAX_55,
                    false, DrawPolicy.VANILLA, WireWhen.ALWAYS, new short[]{-10, -110}, "109 shield only"),
            new Seat(62, 134, "Aw", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-62, -162}, "134/135 aux Addon row3"),
            // BP54–62: APPEND_ONLY sidecar (never native aEquipped — BP54≡cash face, BP55≡eye).
            new Seat(54, 118, "Ba", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-54, -154}, "badge"),
            new Seat(55, 120, "To", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-55, -155}, "totem1"),
            new Seat(56, 120, "To", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-56, -156}, "totem2 (−56/−156 cash)"),
            new Seat(57, 120, "To", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-57, -157}, "totem3 (−57/−157 cash)"),
            new Seat(58, 120, "To", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-58, -158}, "totem4 (−58/−158 cash)"),
            new Seat(59, 119, "Em", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-59, -159}, "emblem"),
            new Seat(60, 166, "Dr", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-60, -160}, "android"),
            new Seat(61, 167, "Ht", UiSeat.ADDON_DOCK, StorageTier.SIDECAR_ZREF,
                    true, DrawPolicy.ADDON_OVERLAY, WireWhen.ALWAYS, new short[]{-61, -161}, "heart")
    ));

    public static final short[] TOTEM_SLOTS = {-55, -56, -57, -58};
    public static final short[] TOTEM_CASH_SLOTS = {-155, -156, -157, -158};

    /**
     * ADDON_SERVER_STATS — Occ-OFF seats injected via STAT_CHANGED.
     * Never list Si −10 or pocket −33 (client-native fold).
     */
    public static boolean isClientBlindEquipSlot(short pos) {
        return switch (pos) {
            case -52, -53 -> true;
            case -54, -55, -56, -57, -58, -59, -60, -61, -62 -> true;
            case -154, -155, -156, -157, -158, -159, -160, -161, -162 -> true;
            default -> false;
        };
    }

    /** Prefix-first AbleToWear: returns null if registry does not own the prefix. */
    public static Boolean isPrefixSlotAllowed(int prefix, int dst, boolean cash) {
        return switch (prefix) {
            case 119 -> EquipSlot.EMBLEM.isAllowed(dst, cash);
            case 118 -> EquipSlot.BADGE.isAllowed(dst, cash);
            case 120 -> EquipSlot.TOTEM.isAllowed(dst, cash);
            case 166 -> EquipSlot.ANDROID.isAllowed(dst, cash);
            case 167 -> EquipSlot.HEART.isAllowed(dst, cash);
            case 116 -> EquipSlot.POCKET.isAllowed(dst, cash);
            case 109 -> EquipSlot.SHIELD.isAllowed(dst, cash);
            case 134, 135 -> EquipSlot.AUX_WEAPON.isAllowed(dst, cash);
            default -> null;
        };
    }

    /**
     * Fixed dst for single-seat prefixes. Returns 0 if caller must fill (totem) or
     * leave vanilla routing.
     * <p>
     * Sidecar Addon (118/119/120/166/167/134/135): <b>always</b> normal −bp — cash and
     * non-cash share one arena seat (matches ijl15 GetItem normal-only + SetItem alias).
     * Native dual-band kept only for pocket −33/−133 and shield −10/−110.
     */
    public static short resolveFixedDst(int prefix, boolean cashItem) {
        return switch (prefix) {
            case 116 -> (short) (cashItem ? -133 : -33);
            case 119 -> -59;
            case 118 -> -54;
            case 166 -> -60;
            case 167 -> -61;
            case 109 -> (short) (cashItem ? -110 : -10);
            case 134, 135 -> -62;
            default -> 0;
        };
    }

    /**
     * Client invent wire slot for Addon alias seats. Live GetItem hook only sees
     * −54…−62; map cash mirrors −154…−162 → normal so mode-2/mode-3 hit sidecar.
     */
    public static short toClientWireSlot(short pos) {
        if (pos <= -154 && pos >= -162) {
            return (short) (pos + 100);
        }
        return pos;
    }

    public static boolean isPrefixOwned(int prefix) {
        return switch (prefix) {
            case 116, 118, 119, 120, 166, 167, 109, 134, 135 -> true;
            default -> false;
        };
    }

    /** Sidecar totems always −55…−58 (cash mirrors share arena; do not land −155…). */
    @SuppressWarnings("unused")
    public static short[] totemSlots(boolean cash) {
        return TOTEM_SLOTS;
    }

    /** True if {@code pos} is a normal/cash totem seat (−55…−58 / −155…−158). */
    public static boolean isTotemSeat(short pos) {
        for (short s : TOTEM_SLOTS) {
            if (pos == s || pos == (short) (s - 100)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Count prefix-120 items on totem seats (either alias side counts once per BP).
     */
    public static int countEquippedTotems(Inventory equipped) {
        if (equipped == null) {
            return 0;
        }
        int n = 0;
        for (short s : TOTEM_SLOTS) {
            Item a = equipped.getItem(s);
            Item b = equipped.getItem((short) (s - 100));
            Item it = a != null ? a : b;
            if (it != null && it.getItemId() / 10000 == 120) {
                ++n;
            }
        }
        return n;
    }

    /**
     * Resolve totem dst: prefer filling empty −55…−58 over replacing a single seat.
     * Client sidecar-blind often spam-sends −55; without this, seats 2–4 never fill and
     * UX looks like “unlimited wear” (always replace −55).
     * <ul>
     *   <li>Empty seats remain → first empty (prefer client seat if that seat empty).</li>
     *   <li>All 4 full → replace client seat if occupied; else reject ({@code 0}).</li>
     * </ul>
     */
    public static short resolveTotemDst(Inventory equipped, short clientDst) {
        if (equipped == null) {
            return 0;
        }
        short preferred = 0;
        if (isTotemSeat(clientDst)) {
            preferred = clientDst <= -155 ? (short) (clientDst + 100) : clientDst;
        }
        short firstEmpty = 0;
        int occupied = 0;
        for (short s : TOTEM_SLOTS) {
            boolean taken = equipped.getItem(s) != null
                    || equipped.getItem((short) (s - 100)) != null;
            if (taken) {
                ++occupied;
            } else if (firstEmpty == 0) {
                firstEmpty = s;
            }
        }
        if (occupied >= 4) {
            if (preferred != 0 && (equipped.getItem(preferred) != null
                    || equipped.getItem((short) (preferred - 100)) != null)) {
                return preferred; // replace that seat
            }
            return 0; // full — reject 5th
        }
        // Prefer empty fill; only use preferred when that seat is free.
        if (preferred != 0 && equipped.getItem(preferred) == null
                && equipped.getItem((short) (preferred - 100)) == null) {
            return preferred;
        }
        return firstEmpty;
    }

    /**
     * Single-seat Addon prefixes (badge/aux/emblem/android/heart): bag every extra
     * copy beyond the canonical seat (and its cash alias). Returns parked count.
     */
    public static int countPrefixOnEquipped(Inventory equipped, int prefix) {
        if (equipped == null) {
            return 0;
        }
        int n = 0;
        for (Item it : equipped.list()) {
            if (it != null && it.getItemId() / 10000 == prefix) {
                ++n;
            }
        }
        return n;
    }

    /**
     * Addon / cash mirror seats: −54…−62 ↔ −154…−162.
     * Live green ijl15 ({@code 6D612F01}) GetItem aliases −156→−56 and may send the
     * UI seat (−bp) while the item lives at cash −(bp+100) after login migrate.
     */
    public static boolean isAddonAliasPairSeat(short pos) {
        return (pos <= -54 && pos >= -62) || (pos <= -154 && pos >= -162);
    }

    /**
     * Historical occupy-hard-reject seats (out.log 23:05 storm). <b>Do not hard-reject</b>
     * — vanilla replace. Kept so guards/docs can name the band; callers must not branch
     * on this to refuse wear.
     */
    public static boolean isStrictOccupySeat(short pos) {
        return isAddonAliasPairSeat(pos);
    }

    /**
     * Regression guard markers (must appear in compiled classes / out.log policy).
     * Scripts: {@code tools/guard_addon_error_ops.ps1}.
     */
    public static final String GUARD_NO_OCCUPY_REJECT = "VANILLA_REPLACE_NO_OCCUPY_REJECT";
    public static final String GUARD_GHOST_ENABLE_ACTIONS_ONLY = "GHOST_ENABLE_ACTIONS_ONLY";

    /**
     * If either side of an Addon alias pair holds an item, return that position;
     * otherwise {@code 0}.
     */
    public static short findOccupiedAliasSeat(Inventory equipped, short requested) {
        if (equipped == null || !isAddonAliasPairSeat(requested)) {
            return 0;
        }
        if (equipped.getItem(requested) != null) {
            return requested;
        }
        short other = (short) (requested <= -154 ? requested + 100 : requested - 100);
        if (isAddonAliasPairSeat(other) && equipped.getItem(other) != null) {
            return other;
        }
        return 0;
    }

    /**
     * Resolve the equipped inventory slot that actually holds the item when the
     * client addresses either side of an Addon cash/normal alias pair.
     * Returns {@code requested} unchanged when no alias applies or neither side has an item.
     */
    public static short resolveEquippedSlotAlias(Inventory equipped, short requested) {
        if (equipped == null || !isAddonAliasPairSeat(requested)) {
            return requested;
        }
        Item direct = equipped.getItem(requested);
        if (direct != null) {
            return requested;
        }
        short other = (short) (requested <= -154 ? requested + 100 : requested - 100);
        if (!isAddonAliasPairSeat(other)) {
            return requested;
        }
        Item aliased = equipped.getItem(other);
        return aliased != null ? other : requested;
    }

    /**
     * <b>SINGLE FLIP</b> for aux −62 wire / wear when Client_1 BP62 is enter-green.
     * <ul>
     *   <li>{@code true}: CharInfo omits −62/−162; equip reject-wear; login parks 134/135 → bag;
     *       sort/merge flush ghost −62. Use only while Client_1 lacks enter-green BP62 peer.</li>
     *   <li>{@code false} (live after PEER_ASLR_V2 enter-green + PEER_COMPLETE): wire −62;
     *       allow wear; login migrate −10→−62; same pipeline as other Addon seats.</li>
     * </ul>
     * Flip checklist: {@code gms-server/docs/ADDON_AUX_SERVER_PARITY.md}.
     * All-or-nothing: do not leave omit/reject only on aux while totem/badge/android wear normally.
     */
    public static final boolean GREEN_ENTER_OMIT_AUX62 = false;

    /**
     * Whether CharInfo / wear / migrate must omit aux seat {@code pos} (−62/−162 only).
     * All reject-wear / wire-omit / ghost-flush paths gate on this — flip
     * {@link #GREEN_ENTER_OMIT_AUX62} only.
     */
    public static boolean isGreenEnterWireOmit(short pos) {
        if (!GREEN_ENTER_OMIT_AUX62) {
            return false;
        }
        return pos == -62 || pos == -162;
    }
}
