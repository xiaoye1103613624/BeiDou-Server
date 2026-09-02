package org.gms.combat.power;

import org.gms.constants.inventory.EquipSlot;
import org.gms.server.ItemInformationProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备排行榜部位分类（与 {@link EquipSlot} 对齐，戒指/项链等多槽合并）。
 */
public final class EquipRankCategory {
    public static final int ALL = 0;
    public static final int HAT = 1;
    public static final int FACE = 2;
    public static final int EYE = 3;
    public static final int EARRING = 4;
    public static final int TOP = 5;
    public static final int BOTTOM = 6;
    public static final int SHOES = 7;
    public static final int GLOVES = 8;
    public static final int CAPE = 9;
    public static final int SHIELD = 10;
    public static final int WEAPON = 11;
    public static final int RING = 12;
    public static final int PENDANT = 13;
    public static final int BELT = 14;
    public static final int MEDAL = 15;
    public static final int SHOULDER = 16;
    public static final int POCKET = 17;
    public static final int BADGE = 18;
    public static final int TOTEM = 19;
    public static final int EMBLEM = 20;
    public static final int ANDROID = 21;
    public static final int HEART = 22;
    public static final int OTHER = 99;

    private EquipRankCategory() {}

    public static List<Integer> listCategories() {
        List<Integer> list = new ArrayList<>();
        list.add(ALL);
        list.add(HAT);
        list.add(FACE);
        list.add(EYE);
        list.add(EARRING);
        list.add(TOP);
        list.add(BOTTOM);
        list.add(SHOES);
        list.add(GLOVES);
        list.add(CAPE);
        list.add(SHIELD);
        list.add(WEAPON);
        list.add(RING);
        list.add(PENDANT);
        list.add(BELT);
        list.add(MEDAL);
        list.add(SHOULDER);
        list.add(POCKET);
        list.add(BADGE);
        list.add(TOTEM);
        list.add(EMBLEM);
        list.add(ANDROID);
        list.add(HEART);
        return list;
    }

    /** 已穿戴：按背包 position（负槽）归类。 */
    public static int fromEquippedPosition(short position) {
        int bp = -position;
        if (bp >= 100 && bp <= 199) {
            bp -= 100;
        }
        return switch (bp) {
            case 1 -> HAT;
            case 2 -> FACE;
            case 3 -> EYE;
            case 4 -> EARRING;
            case 5 -> TOP;
            case 6 -> BOTTOM;
            case 7 -> SHOES;
            case 8 -> GLOVES;
            case 9 -> CAPE;
            case 10, 62 -> SHIELD;
            case 11 -> WEAPON;
            case 12, 13, 15, 16, 52, 53 -> RING;
            case 17, 51 -> PENDANT;
            case 20 -> SHOULDER;
            case 33 -> POCKET;
            case 49 -> MEDAL;
            case 50 -> BELT;
            case 54 -> BADGE;
            case 55, 56, 57, 58 -> TOTEM;
            case 59 -> EMBLEM;
            case 60 -> ANDROID;
            case 61 -> HEART;
            default -> OTHER;
        };
    }

    /** 未穿戴：按 WZ islot 归类。 */
    public static int fromItemId(int itemId) {
        String islot = ItemInformationProvider.getInstance().getEquipmentSlot(itemId);
        if (islot == null || islot.isEmpty()) {
            return OTHER;
        }
        EquipSlot slot = EquipSlot.getFromTextSlot(islot);
        if (slot == null) {
            return OTHER;
        }
        return switch (slot) {
            case HAT, SPECIAL_HAT -> HAT;
            case FACE_ACCESSORY -> FACE;
            case EYE_ACCESSORY -> EYE;
            case EARRINGS -> EARRING;
            case TOP, OVERALL -> TOP;
            case PANTS -> BOTTOM;
            case SHOES -> SHOES;
            case GLOVES, CASH_GLOVES -> GLOVES;
            case CAPE -> CAPE;
            case SHIELD, AUX_WEAPON -> SHIELD;
            case WEAPON, WEAPON_2, LOW_WEAPON -> WEAPON;
            case RING -> RING;
            case PENDANT -> PENDANT;
            case BELT -> BELT;
            case MEDAL -> MEDAL;
            case SHOULDER -> SHOULDER;
            case POCKET -> POCKET;
            case BADGE -> BADGE;
            case TOTEM -> TOTEM;
            case EMBLEM -> EMBLEM;
            case ANDROID -> ANDROID;
            case HEART -> HEART;
            default -> OTHER;
        };
    }
}
