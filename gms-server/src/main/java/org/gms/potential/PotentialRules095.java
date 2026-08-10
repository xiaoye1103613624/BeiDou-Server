package org.gms.potential;

import org.gms.constants.inventory.ItemConstants;
import org.gms.util.Randomizer;

/**
 * 从 095 开源（{@code GameConstants.potentialIDFits / optionTypeFits}、
 * {@code Equip.resetPotential*}、鉴定条数逻辑）移植的规则。
 * <p>
 * 源：{@code E:\pro\_095_extract}（095开源源码.zip）。
 * 本服用 {@code potentialGrade 1~5} 存品阶；洗潜时映射到 095 的 state 5~8 再套 fits。
 * <p>
 * <b>刻意不照抄 095 私服改坏处</b>（已用 TMS120 / BMS250 / wiki 交叉验证）：
 * <ul>
 *   <li>095 {@code state==8} 主档 {@code >=40000} 无上界，会把 60xxx 附加潜能抽进主潜能；
 *       本服主档锁 {@code [40000,50000)}，并在洗池硬排除 {@code >=60000}。</li>
 *   <li>095 传说次档 {@code >=30000 && <41008}（注释 xml say so）把部分 40xxx 混进次档；
 *       本服次档锁 {@code [30000,40000)}，与史诗/独特对称。</li>
 *   <li>095/TMS120 独特主档 {@code >=30000} 无上界；有 40xxx 表时会越阶抽传说词条；
 *       本服主档锁 {@code [30000,40000)}。</li>
 *   <li>TMS120 无 state8（时代无传说）；传说段以 095 超魔方 + KMS391 ItemOption 为准，
 *       而非照搬 095 fits 的破上界写法。</li>
 * </ul>
 */
public final class PotentialRules095 {
    private PotentialRules095() {}

    /** 本服 grade → 095 magnify/cube state（用于 potentialIDFits）。 */
    public static int gradeTo095State(int grade) {
        return switch (Math.max(1, Math.min(5, grade))) {
            case 1, 2 -> 5; // 普通/稀有 → 095「特殊」池
            case 3 -> 6;    // 史诗
            case 4 -> 7;    // 独特
            case 5 -> 8;    // 传说
            default -> 5;
        };
    }

    /**
     * 装备 reqLevel → ItemOption {@code level} 节点名（1~20）。
     * <p>
     * 对齐 095 客户端 {@code BasicStat/SecondaryStat::SetFrom}：
     * {@code nLevel = (nrLevel - 1) / 10} 作为 0-based 数组下标，而
     * {@code LoadItemOptionLevelData} 把 WZ {@code level/"N"} 装进 {@code a[N-1]}，
     * 故节点名 {@code N = (req - 1) / 10 + 1}（req≤0 时按 1）。
     * 例：req 1~10→1，11~20→2，39→4，100→10。
     * 品阶本身不改索引；高品阶靠抽到更高 optionId 族。
     */
    public static int equipOptionLevel(int equipReqLevel) {
        int req = Math.max(0, equipReqLevel);
        int node = req <= 0 ? 1 : (req - 1) / 10 + 1;
        return Math.max(1, Math.min(20, node));
    }

    /**
     * 当前品阶「主档」ID 段（首条必出；二三条约 10% 仍可摸到）。
     * 稀有→1xxxx / 史诗→2xxxx / 独特→3xxxx / 传说→4xxxx。
     */
    public static boolean inPreferredBand(int potentialID, int newState) {
        if (potentialID <= 0 || potentialID >= 60000) {
            return false;
        }
        return switch (newState) {
            case 8 -> potentialID >= 40000 && potentialID < 50000;
            case 7 -> potentialID >= 30000 && potentialID < 40000;
            case 6 -> potentialID >= 20000 && potentialID < 30000;
            case 5 -> potentialID >= 10000 && potentialID < 20000;
            default -> false;
        };
    }

    /**
     * 当前品阶「降一档」ID 段（二三条默认池，约 90%）。
     * 稀有→0xxxx / 史诗→1xxxx / 独特→2xxxx / 传说→3xxxx。
     */
    public static boolean inSecondaryBand(int potentialID, int newState) {
        if (potentialID <= 0 || potentialID >= 60000) {
            return false;
        }
        return switch (newState) {
            case 8 -> potentialID >= 30000 && potentialID < 40000;
            case 7 -> potentialID >= 20000 && potentialID < 30000;
            case 6 -> potentialID >= 10000 && potentialID < 20000;
            case 5 -> potentialID < 10000;
            default -> false;
        };
    }

    /** 是否属于该品阶允许的任一档（主档或降一档）。 */
    public static boolean inGradeBands(int potentialID, int newState) {
        return inPreferredBand(potentialID, newState) || inSecondaryBand(potentialID, newState);
    }

    /**
     * 095 {@code GameConstants.potentialIDFits}。
     * 第一条倾向当前品阶高段，二三条可略降一档（10% 仍可摸高段）。
     */
    public static boolean potentialIDFits(int potentialID, int newState, int lineIndex) {
        if (potentialID >= 60000) {
            return false;
        }
        boolean preferHigh = lineIndex == 0 || Randomizer.nextInt(10) == 0;
        return preferHigh ? inPreferredBand(potentialID, newState)
                : inSecondaryBand(potentialID, newState);
    }

    /**
     * 附加潜能（60xxx）品质档 1~4（稀有/史诗/独特/传说），对齐主潜能 % 阶梯约 3/6/9/12。
     * 60xxx 无万段对称 ID，按 level 表战斗向数值分档；非战斗壳返回 0。
     * <p>
     * 与主潜能相同：本品阶主档只出本档，二三条默认可降一档（不可跨多档洗低）。
     */
    public static int bonusQualityTier(java.util.Map<String, Integer> stats) {
        if (stats == null || stats.isEmpty()) {
            return 0;
        }
        boolean combat = false;
        for (String k : stats.keySet()) {
            if (k == null || "prop".equals(k) || "time".equals(k) || "attackType".equals(k)
                    || "level".equals(k) || "boss".equals(k)) {
                continue;
            }
            if (k.startsWith("inc") || "ignoreTargetDEF".equals(k) || "ignoreDAM".equals(k)
                    || "ignoreDAMr".equals(k) || "DAMreflect".equals(k)) {
                combat = true;
                break;
            }
        }
        if (!combat) {
            return 0;
        }
        int allstat = 0;
        if (stats.containsKey("incSTRr") && stats.containsKey("incDEXr")
                && stats.containsKey("incINTr") && stats.containsKey("incLUKr")) {
            allstat = Math.min(Math.min(stats.get("incSTRr"), stats.get("incDEXr")),
                    Math.min(stats.get("incINTr"), stats.get("incLUKr")));
        }
        int single = maxOf(stats, "incSTRr", "incDEXr", "incINTr", "incLUKr",
                "incMHPr", "incMMPr", "incPADr", "incMADr");
        int dam = stats.getOrDefault("incDAMr", 0);
        int boss = stats.getOrDefault("boss", 0) > 0 ? dam : 0;
        int ignore = stats.getOrDefault("ignoreTargetDEF", 0);
        int crit = stats.getOrDefault("incCr", 0);
        int critDam = stats.getOrDefault("incCriticaldamage", 0);
        if (critDam == 0) {
            critDam = Math.max(stats.getOrDefault("incCriticaldamageMin", 0),
                    stats.getOrDefault("incCriticaldamageMax", 0));
        }
        int flatAtk = Math.max(stats.getOrDefault("incPAD", 0), stats.getOrDefault("incMAD", 0));
        int drop = stats.getOrDefault("incRewardProp", 0);
        int meso = stats.getOrDefault("incMesoProp", 0);

        // 传说 ≈ 主档 12% / Boss30 / 无视30+
        if (allstat >= 15 || boss >= 25 || ignore >= 30 || single >= 12
                || (dam >= 12 && boss == 0) || crit >= 12) {
            return 4;
        }
        // 独特 ≈ 9%
        if (allstat >= 8 || boss >= 15 || ignore >= 15 || single >= 8 || dam >= 8
                || crit >= 9 || critDam >= 8 || flatAtk >= 15) {
            return 3;
        }
        // 史诗 ≈ 6%
        if (allstat >= 5 || boss >= 8 || ignore >= 8 || single >= 5 || dam >= 5
                || crit >= 6 || critDam >= 5 || flatAtk >= 8 || drop >= 3 || meso >= 3) {
            return 2;
        }
        return 1; // 稀有
    }

    private static int maxOf(java.util.Map<String, Integer> stats, String... keys) {
        int m = 0;
        for (String k : keys) {
            m = Math.max(m, stats.getOrDefault(k, 0));
        }
        return m;
    }

    /** 附加潜能当前品阶「主档」品质（稀有1/史诗2/独特3/传说4）。 */
    public static int bonusPreferredTier(int newState) {
        return switch (newState) {
            case 8 -> 4;
            case 7 -> 3;
            case 6 -> 2;
            case 5 -> 1;
            default -> 0;
        };
    }

    /** 附加潜能「降一档」品质；稀有无更低档（0）。 */
    public static int bonusSecondaryTier(int newState) {
        return switch (newState) {
            case 8 -> 3;
            case 7 -> 2;
            case 6 -> 1;
            default -> 0;
        };
    }

    public static boolean inBonusPreferredBand(int qualityTier, int newState) {
        int want = bonusPreferredTier(newState);
        return want > 0 && qualityTier == want;
    }

    public static boolean inBonusSecondaryBand(int qualityTier, int newState) {
        int want = bonusSecondaryTier(newState);
        return want > 0 && qualityTier == want;
    }

    public static boolean inBonusGradeBands(int qualityTier, int newState) {
        return inBonusPreferredBand(qualityTier, newState) || inBonusSecondaryBand(qualityTier, newState);
    }

    /**
     * 附加潜能 fits：仅 60xxx，且品质档落在本品阶主档/降一档；
     * 第一条倾向主档，二三条约 10% 仍可摸主档。
     */
    public static boolean bonusPotentialIDFits(int potentialID, int qualityTier, int newState, int lineIndex) {
        if (potentialID < 60000 || qualityTier <= 0) {
            return false;
        }
        boolean preferHigh = lineIndex == 0 || Randomizer.nextInt(10) == 0;
        return preferHigh ? inBonusPreferredBand(qualityTier, newState)
                : inBonusSecondaryBand(qualityTier, newState);
    }

    /** 095 {@code GameConstants.optionTypeFits}。 */
    public static boolean optionTypeFits(int optionType, int itemId) {
        return switch (optionType) {
            case 10 -> ItemConstants.isWeapon(itemId);
            case 11 -> !ItemConstants.isWeapon(itemId);
            case 20 -> itemId / 10000 == 109;
            case 21 -> itemId / 10000 == 180;
            case 40 -> ItemConstants.isAccessory(itemId);
            case 51 -> itemId / 10000 == 100;
            case 52 -> itemId / 10000 == 110;
            case 53 -> {
                int t = itemId / 10000;
                yield t == 104 || t == 105 || t == 106;
            }
            case 54 -> itemId / 10000 == 108;
            case 55 -> itemId / 10000 == 107;
            case 90 -> false;
            default -> true;
        };
    }

    /**
     * 本服已鉴定态条数：pot3&gt;0 → 3 线，否则 2 线。
     * （095 放大镜：隐藏态 pot2!=0 才出 3 线；鉴定后 2 线是 pot1+pot2、pot3=0。）
     */
    public static int revealedLineCount(int pot1, int pot2, int pot3) {
        if (pot1 <= 0 && pot2 <= 0 && pot3 <= 0) {
            return 0;
        }
        return pot3 > 0 ? 3 : 2;
    }

    /**
     * Phase7 隐藏态判定：{@code potentialGrade &gt; 0} 且三条选项均 ≤0（无正 optionId）。
     * 与已鉴定（pot1&gt;0）及旧 DB 数据兼容：旧装已 roll 出正 ID 仍视为已鉴定。
     * 三线提示：隐藏时 pot2=-1（对齐 095 隐藏态 pot2!=0）。
     */
    public static boolean isMainHidden(int grade, int pot1, int pot2, int pot3) {
        return grade > 0 && pot1 <= 0 && pot2 <= 0 && pot3 <= 0;
    }

    public static boolean isMainRevealed(int pot1) {
        return pot1 > 0;
    }

    public static boolean hasMainPotential(int grade, int pot1) {
        return grade > 0 || pot1 > 0;
    }

    /** 隐藏态待鉴定条数：pot2!=0 → 3 线，否则 2 线（095 UseMagnify）。 */
    public static int pendingLineCount(int pot2) {
        return pot2 != 0 ? 3 : 2;
    }

    /** 魔方重随：已鉴定看 pot3；若误传入隐藏态则看 pot2 提示。 */
    public static int cubeLineCount(int pot1, int pot2, int pot3) {
        if (pot1 <= 0 && pot2 <= 0 && pot3 <= 0) {
            return pendingLineCount(pot2);
        }
        return pot3 > 0 ? 3 : 2;
    }

    /**
     * 095 {@code Equip.renewPotential(false)} 品阶跃迁（本服 grade 1~5）。
     * <ul>
     *   <li>稀有(2/state5)：2% 升史诗</li>
     *   <li>其它：0.5%（50/10000）升一阶</li>
     *   <li>独特(4/state7) 不可用奇迹魔方跃迁；传说(5) 封顶为独特（095 把 -8 压回 -7）</li>
     * </ul>
     *
     * @return 洗后品阶（至少保留当前）
     */
    public static int renewGradeMiracle(int grade) {
        int g = Math.max(1, Math.min(5, grade));
        if (g >= 4) {
            // 095：state==7 不升；rank==-8 → -7
            return Math.min(4, g);
        }
        boolean bump;
        if (g <= 2) {
            bump = Randomizer.nextInt(100) < 2; // rare → epic ~2%
        } else {
            bump = Randomizer.nextInt(10000) < 50; // 0.5%
        }
        if (!bump) {
            return g;
        }
        int next = g + 1;
        return Math.min(4, next); // 奇迹魔方最高独特
    }

    /**
     * 095 {@code Equip.renewPotential1}（高级/珍贵魔方）：稀有 2%，其它 1%（10/1000）；
     * 不可对传说再升；仍把 -8 压回 -7（最高独特）。
     */
    public static int renewGradePremium(int grade) {
        int g = Math.max(1, Math.min(5, grade));
        if (g >= 5) {
            return 4; // 095 caps at unique for these cubes
        }
        boolean bump;
        if (g <= 2) {
            bump = Randomizer.nextInt(100) < 2;
        } else {
            bump = Randomizer.nextInt(1000) < 10;
        }
        if (!bump || g >= 4) {
            return Math.min(4, g);
        }
        return Math.min(4, g + 1);
    }

    /**
     * 095 {@code renewPotential_super}：8% 升一阶，可到传说（state!=8 才 bump）。
     * 传说保阶；独特可 8% → 传说。不降阶。
     */
    public static int renewGradeSuper(int grade) {
        int g = Math.max(1, Math.min(5, grade));
        if (g >= 5) {
            return 5;
        }
        if (Randomizer.nextInt(100) < PotentialHyperConfig.SUPER_CUBE_UPGRADE_CHANCE) {
            return Math.min(5, g + 1);
        }
        return g;
    }

    /**
     * Wiki 终极神奇魔方升阶（映射稀有/史诗/独特/传说）。
     */
    public static int renewGradeUltimate(int grade) {
        int g = Math.max(1, Math.min(5, grade));
        if (g >= 5) {
            return 5;
        }
        boolean bump;
        if (g <= 2) {
            bump = Randomizer.nextInt(100) < PotentialHyperConfig.ULTIMATE_RARE_TO_EPIC_PCT;
        } else if (g == 3) {
            bump = Randomizer.nextInt(10000) < PotentialHyperConfig.ULTIMATE_EPIC_TO_UNIQUE_BP;
        } else {
            bump = Randomizer.nextInt(10000) < PotentialHyperConfig.ULTIMATE_UNIQUE_TO_LEGEND_BP;
        }
        return bump ? Math.min(5, g + 1) : g;
    }

    /** 怪异魔方：不升阶，保阶重随（最高史诗）。 */
    public static int renewGradeWeird(int grade) {
        int g = Math.max(1, Math.min(5, grade));
        return Math.min(3, g);
    }

    /** 大师附加魔方升阶（保阶或 +1，最高传说）。 */
    public static int renewGradeBonusCube(int grade) {
        int g = Math.max(1, Math.min(5, grade));
        if (g >= 5) {
            return 5;
        }
        int bp = g <= 2
                ? PotentialHyperConfig.BONUS_CUBE_RARE_UP_BP
                : PotentialHyperConfig.BONUS_CUBE_OTHER_UP_BP;
        if (Randomizer.nextInt(10000) < bp) {
            return Math.min(5, g + 1);
        }
        return g;
    }

    /** 普通潜能卷首次：10% 三线（095 resetPotential）。 */
    public static int firstIdentifyLinesNormal() {
        return Randomizer.nextInt(10) == 0 ? 3 : 2;
    }

    /** 095 resetPotential 初始品阶：绝大多数稀有(2)，小概率史诗/独特。 */
    public static int rollInitialGradeNormal() {
        if (Randomizer.nextInt(100) < 4) {
            if (Randomizer.nextInt(100) < 4) {
                return 4; // 独特（095 -7）
            }
            return 3; // 史诗（095 -6）
        }
        return 2; // 稀有（095 -5）
    }

    /** Epic Potential Scroll → 史诗。 */
    public static int initialGradeEpic() {
        return 3;
    }

    /** Unique Potential Scroll → 独特。 */
    public static int initialGradeUnique() {
        return 4;
    }

    /** 首次附加时 3 线概率（095 resetPotentialA/S：nextInt(10) &lt;= 1 → 约 20%）。 */
    public static int firstIdentifyLinesWide() {
        return Randomizer.nextInt(10) <= 1 ? 3 : 2;
    }

    /** 附加魔方：2→3 线解锁（与宽池首鉴同约 20%）。 */
    public static boolean bonusCubeUnlockThirdLine() {
        return Randomizer.nextInt(10) <= 1;
    }
}
