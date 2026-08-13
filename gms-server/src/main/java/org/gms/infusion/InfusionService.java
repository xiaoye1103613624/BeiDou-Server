package org.gms.infusion;

import org.gms.client.inventory.Equip;
import org.gms.potential.PotentialHyperService;
import org.gms.util.Randomizer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 装备注能（⚡）系统。
 * <p>共 10 级固定表：每级固定金币/邮票/枫叶消耗，成功率逐级递减；
 * 成功/失败均消耗材料；成功仅提升 1 级（不可跳级）；⚡数值 = 已达注能等级。</p>
 * <p>属性为「每级新增增量」，累积叠加；与装备类型无关（gear-independent）。</p>
 * <p>材料：枫叶=4001126；四种邮票 = 绿蜗牛/蓝蜗牛/木妖/绿水灵 4002000~4002003。</p>
 */
public final class InfusionService {

    private InfusionService() {}

    public static final int MAX_LEVEL = 10;

    /** 枫叶 */
    public static final int LEAF = 4001126;
    /** 四种邮票：绿蜗牛/蓝蜗牛/木妖/绿水灵 */
    public static final int[] STAMPS = {4002000, 4002001, 4002002, 4002003};

    /** 单级配置：从 level 升到 level+1 的消耗与增量。 */
    public static final class Tier {
        /** 成功率（%） */
        public final int ratePct;
        /** 金币 */
        public final long meso;
        /** 所需邮票 ID 列表（Lv1~4 各 1 种；Lv5~10 为 4 种） */
        public final int[] stampIds;
        /** 每种邮票所需数量 */
        public final int stamps;
        /** 枫叶数量 */
        public final int leaf;
        /** 本级别新增增量（STR/DEX/INT/LUK/HP/MP/PAD/MAD/PDD/MDD） */
        public final Map<String, Integer> delta;

        Tier(int ratePct, long meso, int[] stampIds, int stamps, int leaf, Map<String, Integer> delta) {
            this.ratePct = ratePct;
            this.meso = meso;
            this.stampIds = stampIds;
            this.stamps = stamps;
            this.leaf = leaf;
            this.delta = Map.copyOf(delta);
        }
    }

    private static final int[] STAMP_GREEN = {4002000};
    private static final int[] STAMP_BLUE = {4002001};
    private static final int[] STAMP_WOOD = {4002002};
    private static final int[] STAMP_WATER = {4002003};
    private static final int[] STAMP_ALL = {4002000, 4002001, 4002002, 4002003};

    private static final Tier[] TIERS = {
            new Tier(100, 500_000L, STAMP_GREEN, 25, 100, delta(1, 0, 0, 0, 0, 0)),
            new Tier(90, 1_000_000L, STAMP_BLUE, 25, 200, delta(1, 0, 10, 10, 0, 0)),
            new Tier(80, 1_500_000L, STAMP_WOOD, 25, 300, delta(2, 0, 20, 20, 0, 0)),
            new Tier(70, 2_000_000L, STAMP_WATER, 25, 400, delta(3, 0, 40, 40, 0, 0)),
            new Tier(50, 2_500_000L, STAMP_ALL, 25, 1000, delta(4, 1, 80, 80, 0, 0)),
            new Tier(40, 3_000_000L, STAMP_ALL, 30, 2000, delta(5, 2, 100, 100, 0, 0)),
            new Tier(30, 3_500_000L, STAMP_ALL, 35, 4000, delta(6, 3, 120, 120, 0, 0)),
            new Tier(20, 4_000_000L, STAMP_ALL, 40, 8000, delta(7, 4, 140, 140, 0, 0)),
            new Tier(10, 5_000_000L, STAMP_ALL, 45, 16000, delta(8, 5, 160, 160, 0, 0)),
            new Tier(5, 6_000_000L, STAMP_ALL, 50, 32000, delta(9, 6, 180, 180, 10, 10)),
    };

    /** 目标等级（1~10）对应的增量属性表 */
    private static Map<String, Integer> delta(int all4, int atk, int hp, int mp, int pdd, int mdd) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("STR", all4);
        m.put("DEX", all4);
        m.put("INT", all4);
        m.put("LUK", all4);
        if (hp > 0) m.put("HP", hp);
        if (mp > 0) m.put("MP", mp);
        if (atk > 0) {
            m.put("PAD", atk);
            m.put("MAD", atk);
        }
        if (pdd > 0) m.put("PDD", pdd);
        if (mdd > 0) m.put("MDD", mdd);
        return m;
    }

    /** 获取从 currentLevel(0~9) 升到下一级的配置；已满级返回 null。 */
    public static Tier tier(int currentLevel) {
        if (currentLevel < 0 || currentLevel >= MAX_LEVEL) {
            return null;
        }
        return TIERS[currentLevel];
    }

    /** 目标等级（1~10）的增量属性 */
    public static Map<String, Integer> deltaStats(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            return Map.of();
        }
        return TIERS[level - 1].delta;
    }

    /** 装备当前注能等级（0~10） */
    public static int levelOf(Equip equip) {
        if (equip == null) {
            return 0;
        }
        return Math.max(0, Math.min(MAX_LEVEL, equip.getInfusion() & 0xFF));
    }

    public static boolean isMax(Equip equip) {
        return levelOf(equip) >= MAX_LEVEL;
    }

    /** 判定本次升级是否成功（ratePct 为成功率%） */
    public static boolean roll(int ratePct) {
        return Randomizer.nextInt(100) < ratePct;
    }

    /** 注能成功：等级 +1（不可跳级，封顶 {@link #MAX_LEVEL}）。 */
    public static void upgrade(Equip equip) {
        if (equip == null) {
            return;
        }
        int lv = levelOf(equip);
        if (lv < MAX_LEVEL) {
            equip.setInfusion((byte) (lv + 1));
        }
    }

    /** 把 level 级的累积注能加成写入 StatBonus（战斗/tip 统一入口）。 */
    public static void applyCumulative(PotentialHyperService.StatBonus b, int level) {
        int capped = Math.max(0, Math.min(MAX_LEVEL, level));
        for (int i = 0; i < capped; i++) {
            Map<String, Integer> d = TIERS[i].delta;
            b.str += d.getOrDefault("STR", 0);
            b.dex += d.getOrDefault("DEX", 0);
            b.inte += d.getOrDefault("INT", 0);
            b.luk += d.getOrDefault("LUK", 0);
            b.hp += d.getOrDefault("HP", 0);
            b.mp += d.getOrDefault("MP", 0);
            b.watk += d.getOrDefault("PAD", 0);
            b.matk += d.getOrDefault("MAD", 0);
            b.wdef += d.getOrDefault("PDD", 0);
            b.mdef += d.getOrDefault("MDD", 0);
        }
    }

    /** 装备当前累积注能加成 */
    public static PotentialHyperService.StatBonus computeBonus(Equip equip) {
        PotentialHyperService.StatBonus b = new PotentialHyperService.StatBonus();
        applyCumulative(b, levelOf(equip));
        return b;
    }

    /** 格式化显示某目标等级的增量属性（如 "力量+1 敏捷+1 智力+1 运气+1"）。 */
    public static String describeDelta(int level) {
        Map<String, Integer> d = deltaStats(level);
        if (d.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        appendStat(sb, d, "STR", "力量");
        appendStat(sb, d, "DEX", "敏捷");
        appendStat(sb, d, "INT", "智力");
        appendStat(sb, d, "LUK", "运气");
        appendStat(sb, d, "HP", "HP");
        appendStat(sb, d, "MP", "MP");
        appendStat(sb, d, "PAD", "攻击");
        appendStat(sb, d, "MAD", "魔攻");
        appendStat(sb, d, "PDD", "物防");
        appendStat(sb, d, "MDD", "魔防");
        return sb.length() > 0 ? sb.substring(2) : "";
    }

    private static void appendStat(StringBuilder sb, Map<String, Integer> d, String key, String zh) {
        int v = d.getOrDefault(key, 0);
        if (v > 0) {
            sb.append(", ").append(zh).append('+').append(v);
        }
    }
}
