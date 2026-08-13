package org.gms.gem;

import org.gms.client.inventory.Equip;
import org.gms.potential.PotentialHyperService;
import org.gms.util.Randomizer;

/**
 * 装备宝石镶嵌（梅兹 · 宝X）系统。
 * <p>共 16 级固定表：每级固定等级宝石 + 对应属性水晶 + 金币，成功率逐级递减；
 * 每次只升 1 级（不可跳级）；起手为当前等级 +1。</p>
 * <p>属性「每级新增增量」，累积叠加：{@code 攻击/魔攻 +等级}、{@code 对应属性 +2×等级}。
 * 智慧水晶只加魔法力，其余水晶加攻击力。</p>
 * <p>成功/失败均消耗水晶与金币；失败不消耗对应等级宝石（保留宝石），装备等级不变。<p>
 * <p>镶嵌数据：{@code gemInlay}=当前等级(0~16)；{@code gemTypes}=每级 2 bit 水晶类型。</p>
 */
public final class GemService {

    private GemService() {
    }

    public static final int MAX_LEVEL = 16;

    public static final int TYPE_STR = 0; // 力量
    public static final int TYPE_DEX = 1; // 敏捷
    public static final int TYPE_INT = 2; // 智慧
    public static final int TYPE_LUK = 3; // 幸运

    /** 各职业属性水晶 ID：力量/敏捷/智慧/幸运 */
    public static final int[] CRYSTALS = {4005000, 4005002, 4005001, 4005003};
    public static final String[] TYPE_NAMES = {"力量", "敏捷", "智慧", "幸运"};

    /** 等级 (1~16) 对应所需装备宝石 ID */
    private static final int[] GEMS = {
            4441300, 4443300, 4442300, 4440300, // 1~4
            4441200, 4443200, 4442200, 4440200, // 5~8
            4441101, 4443101, 4442101, 4440101, // 9~12
            4441001, 4443001, 4442001, 4440001  // 13~16
    };

    private static final int[] RATES = {
            100, 95, 90, 90, 85, 80, 80, 75, 70, 70, 60, 60, 55, 50, 45, 5
    };

    /** 目标等级 (1~16) 配置。 */
    public static final class Tier {
        public final int level;
        public final int gemId;
        public final int ratePct;
        public final long meso;
        public final int crystals;

        Tier(int level, int gemId, int ratePct, long meso, int crystals) {
            this.level = level;
            this.gemId = gemId;
            this.ratePct = ratePct;
            this.meso = meso;
            this.crystals = crystals;
        }
    }

    /** 目标等级（1~16）的消耗与成功率配置。 */
    public static Tier tier(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            return null;
        }
        long meso = Math.min(level * 1_000_000L, 5_000_000L);
        int crystals = Math.min(level * 10, 100);
        return new Tier(level, GEMS[level - 1], RATES[level - 1], meso, crystals);
    }

    /** 某级装备宝石 ID。 */
    public static int gemId(int level) {
        return GEMS[level - 1];
    }

    /** 某属性水晶 ID。 */
    public static int crystalId(int type) {
        return CRYSTALS[type & 3];
    }

    /** 某属性中文名。 */
    public static String typeName(int type) {
        return TYPE_NAMES[type & 3];
    }

    /** 某等级当前水晶类型（未到该等级返回 -1）。 */
    public static int typeOf(Equip equip, int level) {
        if (equip == null || level < 1 || level > MAX_LEVEL) {
            return -1;
        }
        return (equip.getGemTypes() >>> ((level - 1) * 2)) & 3;
    }

    /**
     * 本次目标等级：返回当前等级 + 1；已达上限返回 -1。
     */
    public static int nextLevelOf(Equip equip) {
        int cur = levelOf(equip);
        return cur >= MAX_LEVEL ? -1 : cur + 1;
    }

    /** 装备当前镶嵌等级（0~16）。 */
    public static int levelOf(Equip equip) {
        if (equip == null) {
            return 0;
        }
        return Math.max(0, Math.min(MAX_LEVEL, equip.getGemInlay() & 0xFF));
    }

    public static boolean isMax(Equip equip) {
        return levelOf(equip) >= MAX_LEVEL;
    }

    /** 判定本次镶嵌是否成功（ratePct 为成功率%）。 */
    public static boolean roll(int ratePct) {
        return Randomizer.nextInt(100) < ratePct;
    }

    /**
     * 记录某等级所选水晶类型。
     */
    public static void applyType(Equip equip, int level, int type) {
        if (equip == null || level < 1 || level > MAX_LEVEL) {
            return;
        }
        int mask = 0x3 << ((level - 1) * 2);
        int cleared = equip.getGemTypes() & ~mask;
        equip.setGemTypes(cleared | ((type & 3) << ((level - 1) * 2)));
    }

    /** 镶嵌成功：等级 +1（不可跳级，封顶 {@link #MAX_LEVEL}）。 */
    public static void upgrade(Equip equip) {
        if (equip == null) {
            return;
        }
        int lv = levelOf(equip);
        if (lv < MAX_LEVEL) {
            equip.setGemInlay((byte) (lv + 1));
        }
    }

    /**
     * 把 level 级、types 型的累积镶嵌加成写入 StatBonus（战斗/tip 统一入口）。
     * <p>每级：{@code 主属性 +2N}、{@code 智慧→魔攻+N / 其余→攻击+N}。</p>
     */
    public static void applyCumulative(PotentialHyperService.StatBonus b, int level, int types) {
        if (b == null) {
            return;
        }
        int cur = Math.max(0, Math.min(MAX_LEVEL, level));
        for (int i = 1; i <= cur; i++) {
            int type = (types >>> ((i - 1) * 2)) & 3;
            int atk = i;
            int stat = i * 2;
            switch (type) {
                case TYPE_STR -> {
                    b.str += stat;
                    b.watk += atk;
                }
                case TYPE_DEX -> {
                    b.dex += stat;
                    b.watk += atk;
                }
                case TYPE_INT -> {
                    b.inte += stat;
                    b.matk += atk;
                }
                default -> {
                    b.luk += stat;
                    b.watk += atk;
                }
            }
        }
    }

    /** 装备当前累积镶嵌加成。 */
    public static PotentialHyperService.StatBonus computeBonus(Equip equip) {
        PotentialHyperService.StatBonus b = new PotentialHyperService.StatBonus();
        applyCumulative(b, levelOf(equip), equip == null ? 0 : equip.getGemTypes());
        return b;
    }

    /** 该装备当前「宝X」显示（X=当前等级；0 等级返回 null）。 */
    public static String affixLabel(Equip equip) {
        int lv = levelOf(equip);
        return lv <= 0 ? null : ("宝" + lv);
    }

    /**
     * 格式化目标等级+类型的水晶增量（如 "攻击力+5 力量+10"）。
     */
    public static String describeDelta(int level, int type) {
        if (level < 1 || level > MAX_LEVEL) {
            return "";
        }
        String atkName = type == TYPE_INT ? "魔法力" : "攻击力";
        return atkName + "+" + level + "、" + typeName(type) + "+" + (level * 2);
    }

    /**
     * 装备是否可镶嵌：武器 / 上衣 / 裤子 / 套服。
     */
    public static boolean isInlayable(Equip equip) {
        if (equip == null) {
            return false;
        }
        return isInlayable(equip.getItemId());
    }

    public static boolean isInlayable(int itemId) {
        int t = itemId / 1000000;
        if (t != 1) {
            return false;
        }
        int body = itemId % 1000000 / 10000;
        switch (body) {
            case 104: // 上衣
            case 105: // 套服
            case 106: // 裤子
                return true;
            default:
                return body >= 130 && body <= 200; // 武器
        }
    }
}