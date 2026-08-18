package org.gms.server.cashshop;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 窗口商城分类 ↔ 客户端 {@code cashshopwnd.cpp} kTabs/kCats（Etc.wz/Category.img）
 * 以及 {@code cashshop/catalog.tsv} 的 legacy (tab, category) 桶。
 * <p>
 * 客户端目前硬编码 kTabs，不解析 RESP_TAXONOMY；因此自动同步必须写入
 * tab 2/3/5/6/7 下已有的 kCats，不能另造「特效/消息/现金包」等客户端没有的分类。
 */
public final class CashShopTaxonomy {
    private CashShopTaxonomy() {
    }

    public record Bucket(String key, String name, int sort, int legacyTab, int legacyCategory) {
    }

    /** 扫描 Character 散目录用；展示分类以 {@link #forItemId(int)} / kCats 为准。 */
    private static final List<Bucket> CHARACTER_FOLDERS = List.of(
            new Bucket("Cap", "帽子", 10, 2, 0),
            new Bucket("Coat", "上衣", 50, 2, 4),
            new Bucket("Longcoat", "套服", 40, 2, 3),
            new Bucket("Pants", "裤装", 60, 2, 5),
            new Bucket("Shoes", "鞋子", 70, 2, 6),
            new Bucket("Glove", "手套", 80, 2, 7),
            new Bucket("Cape", "披风", 110, 2, 11),
            new Bucket("Shield", "武器", 90, 2, 8),
            new Bucket("Weapon", "武器", 90, 2, 8),
            new Bucket("Accessory", "饰品", 100, 2, 9),
            new Bucket("Ring", "戒指", 100, 2, 9),
            new Bucket("Pendant", "戒指", 100, 2, 9),
            new Bucket("Belt", "戒指", 100, 2, 9),
            new Bucket("Medal", "戒指", 100, 2, 9),
            new Bucket("Shoulder", "戒指", 100, 2, 9),
            new Bucket("Android", "戒指", 100, 2, 9),
            new Bucket("Face", "脸饰", 20, 2, 1),
            new Bucket("Eye", "眼饰", 30, 2, 2),
            new Bucket("Ear", "戒指", 100, 2, 9)
    );

    // kCats 展示桶（与 cashshopwnd.cpp 顺序一致）
    public static final Bucket CAP = new Bucket("2:0", "帽子", 10, 2, 0);
    public static final Bucket FACE = new Bucket("2:1", "脸饰", 20, 2, 1);
    public static final Bucket EYE = new Bucket("2:2", "眼饰", 30, 2, 2);
    public static final Bucket OVERALL = new Bucket("2:3", "套服", 40, 2, 3);
    public static final Bucket COAT = new Bucket("2:4", "上衣", 50, 2, 4);
    public static final Bucket PANTS = new Bucket("2:5", "裤裙", 60, 2, 5);
    public static final Bucket SHOES = new Bucket("2:6", "鞋子", 70, 2, 6);
    public static final Bucket GLOVE = new Bucket("2:7", "手套", 80, 2, 7);
    public static final Bucket WEAPON = new Bucket("2:8", "武器", 90, 2, 8);
    public static final Bucket RING = new Bucket("2:9", "戒指", 100, 2, 9);
    public static final Bucket CAPE = new Bucket("2:11", "披风", 110, 2, 11);

    public static final Bucket TELEPORT = new Bucket("3:1", "传送", 200, 3, 1);
    public static final Bucket WEATHER = new Bucket("3:2", "气象", 210, 3, 2);

    public static final Bucket BEAUTY = new Bucket("5:0", "美容", 300, 5, 0);
    public static final Bucket STORE = new Bucket("5:1", "商店", 310, 5, 1);
    public static final Bucket GAME = new Bucket("5:2", "游戏", 320, 5, 2);
    public static final Bucket EMOTION = new Bucket("5:3", "表情", 330, 5, 3);
    public static final Bucket WEDDING = new Bucket("5:4", "婚礼", 340, 5, 4);
    public static final Bucket EFFECT = new Bucket("5:5", "效果", 350, 5, 5);
    public static final Bucket CHARACTER = new Bucket("5:6", "角色", 360, 5, 6);

    public static final Bucket PET = new Bucket("6:0", "宠物", 400, 6, 0);
    public static final Bucket PET_EQ = new Bucket("6:1", "宠装", 410, 6, 1);
    public static final Bucket PET_USE = new Bucket("6:2", "宠用", 420, 6, 2);

    public static final Bucket PACKAGE = new Bucket("7:0", "礼包", 500, 7, 0);

    private static final List<Bucket> KCATS = List.of(
            CAP, FACE, EYE, OVERALL, COAT, PANTS, SHOES, GLOVE, WEAPON, RING, CAPE,
            TELEPORT, WEATHER,
            BEAUTY, STORE, GAME, EMOTION, WEDDING, EFFECT, CHARACTER,
            PET, PET_EQ, PET_USE,
            PACKAGE
    );

    /** 旧版按 Cash ID 段发明的分类名（客户端 kCats 没有）。 */
    private static final Set<String> OBSOLETE_AUTO_NAMES = Set.of(
            "特效", "消息", "商店道具", "其它现金", "其他现金", "现金包",
            "其它装备", "杂项", "消耗", "设置", "其他",
            "盾牌", "吊坠", "腰带", "勋章", "肩饰", "机器人", "耳饰", "饰品"
    );

    public static List<Bucket> characterFolders() {
        return CHARACTER_FOLDERS;
    }

    public static List<Bucket> kCats() {
        return KCATS;
    }

    public static boolean isKCatsPair(Integer tab, Integer cat) {
        if (tab == null || cat == null) {
            return false;
        }
        for (Bucket b : KCATS) {
            if (b.legacyTab() == tab && b.legacyCategory() == cat) {
                return true;
            }
        }
        return false;
    }

    public static boolean isObsoleteAutoName(String name) {
        return name != null && OBSOLETE_AUTO_NAMES.contains(name.strip());
    }

    public static Bucket forCharacterFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return RING;
        }
        for (Bucket b : CHARACTER_FOLDERS) {
            if (b.key().equalsIgnoreCase(folder)) {
                return forItemType(equipTypeHint(b.key()));
            }
        }
        return RING;
    }

    private static int equipTypeHint(String folder) {
        return switch (folder.toLowerCase(Locale.ROOT)) {
            case "cap" -> 100;
            case "face" -> 101;
            case "eye" -> 102;
            case "coat" -> 104;
            case "longcoat" -> 105;
            case "pants" -> 106;
            case "shoes" -> 107;
            case "glove" -> 108;
            case "shield" -> 109;
            case "cape" -> 110;
            case "ring" -> 111;
            case "weapon" -> 170;
            default -> 111;
        };
    }

    public static Bucket forItemId(int itemId) {
        int type = itemId / 10000;
        if (itemId >= 1000000 && itemId < 2000000) {
            return forItemType(type);
        }
        if (itemId >= 5000000 && itemId < 5010000) {
            return PET;
        }
        return switch (type) {
            case 501, 528, 529 -> EFFECT;
            case 503, 514 -> STORE;
            case 504 -> TELEPORT;
            case 512 -> WEATHER;
            case 515 -> BEAUTY;
            case 516 -> EMOTION;
            case 517 -> CHARACTER;
            case 518, 519, 524, 546 -> PET_USE;
            case 522, 549, 553 -> PACKAGE;
            case 525 -> WEDDING;
            default -> GAME;
        };
    }

    public static Bucket forItemType(int type) {
        if (type == 100) {
            return CAP;
        }
        if (type == 101) {
            return FACE;
        }
        if (type == 102) {
            return EYE;
        }
        if (type == 104) {
            return COAT;
        }
        if (type == 105) {
            return OVERALL;
        }
        if (type == 106) {
            return PANTS;
        }
        if (type == 107) {
            return SHOES;
        }
        if (type == 108) {
            return GLOVE;
        }
        if (type == 110) {
            return CAPE;
        }
        if (type == 109 || (type >= 130 && type <= 149) || type == 170) {
            return WEAPON;
        }
        if (type >= 180 && type <= 183) {
            return PET_EQ;
        }
        if (type == 103 || (type >= 111 && type <= 115)) {
            return RING;
        }
        return RING;
    }

    public static Map<String, Bucket> defaultBuckets() {
        Map<String, Bucket> m = new LinkedHashMap<>();
        for (Bucket b : KCATS) {
            m.put(b.key(), b);
        }
        return m;
    }

    public static Integer parseImgItemId(String fileName) {
        if (fileName == null) {
            return null;
        }
        String name = fileName;
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        if (name.toLowerCase(Locale.ROOT).endsWith(".img")) {
            name = name.substring(0, name.length() - 4);
        }
        if (!name.matches("\\d{7,8}")) {
            return null;
        }
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer parsePackPrefix(String fileName) {
        if (fileName == null) {
            return null;
        }
        String name = fileName;
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        if (name.toLowerCase(Locale.ROOT).endsWith(".img")) {
            name = name.substring(0, name.length() - 4);
        }
        if (!name.matches("\\d{4}")) {
            return null;
        }
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
