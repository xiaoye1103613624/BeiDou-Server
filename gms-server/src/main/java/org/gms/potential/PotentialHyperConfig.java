package org.gms.potential;

/**
 * 潜能 / Hyper / 附加潜能 / 魔方 / 灵魂 / 星岩 移植配置。
 * <p>
 * Hyper 上限与成功率对齐卷轴 String 文案（常见 T 卷 1~10 星表）；失败默认不炸装。
 */
public final class PotentialHyperConfig {
    private PotentialHyperConfig() {}

    /** Hyper 卷 ID 段：id/100 == 20493 */
    public static boolean isHyperScroll(int itemId) {
        return itemId / 100 == 20493;
    }

    /** 潜能附加卷：id/100 == 20494 */
    public static boolean isPotentialScroll(int itemId) {
        return itemId / 100 == 20494;
    }

    /**
     * 附加潜能卷：仅 Phase3 自研 ID，勿用 /100==20499（会误伤 2049910+）。
     */
    public static boolean isBonusPotentialScroll(int itemId) {
        return itemId == 2049902;
    }

    /**
     * 经典 A 级（史诗）潜能卷：095 String「A级潜能卷轴」+ 20497xx 族。
     * 含 2049408~2049411（095PLUS2）与 2049700~2049749。
     */
    public static boolean isClassicEpicPotentialScroll(int itemId) {
        return (itemId >= 2049408 && itemId <= 2049411)
                || (itemId >= 2049700 && itemId < 2049750);
    }

    /**
     * 经典 S 级（独特）潜能卷：095 String「S级潜能卷轴」+ 204975x 族。
     * 含 2049412~2049413 与 2049750~2049758。
     */
    public static boolean isClassicUniquePotentialScroll(int itemId) {
        return itemId == 2049412 || itemId == 2049413
                || (itemId >= 2049750 && itemId <= 2049758);
    }

    public static boolean isClassicGradePotentialScroll(int itemId) {
        return isClassicEpicPotentialScroll(itemId) || isClassicUniquePotentialScroll(itemId);
    }

    /**
     * 095 普通/经典潜能卷成功率。
     * A/S 百分比卷优先对齐 String 官名（15/30/50/80/100），不依赖残缺 WZ success。
     */
    public static int successForPotentialScroll(int scrollId) {
        if (isClassicGradePotentialScroll(scrollId)) {
            return switch (scrollId) {
                case 2049408, 2049700, 2049702, 2049703 -> 100;
                case 2049412, 2049750 -> 80;
                case 2049411, 2049705 -> 50;
                case 2049410, 2049704 -> 30;
                case 2049409, 2049701, 2049753 -> 15;
                case 2049413, 2049751 -> 50;
                case 2049752 -> 30;
                case 2049754 -> 10;
                case 2049757, 2049758, 2049709 -> 50;
                default -> 100;
            };
        }
        return switch (scrollId) {
            case 2049402, 2049404, 2049405, 2049406, 2049419 -> 100;
            case 2049400 -> 90;
            case 2049421, 2049424 -> 80;
            case 2049401, 2049416 -> 70;
            case 2049407 -> 30;
            default -> DEFAULT_POTENTIAL_SUCCESS;
        };
    }

    /**
     * 潜能卷失败损毁率（对齐 095 String/WZ cursed）。
     * A 级多为 0；S 级 2049412/13、2049750~52 为 100；2049753/54 为 60。
     */
    public static int curseForPotentialScroll(int scrollId) {
        return switch (scrollId) {
            case 2049412, 2049413, 2049750, 2049751, 2049752 -> 100;
            case 2049753, 2049754 -> 60;
            default -> 0;
        };
    }

    /**
     * 魔方 / 品阶 / 星岩 / 灵魂（Phase4+9）。
     * <p>
     * <b>主路径 = 官方 Cash ID</b>（对齐 095 {@code UseCashItem}）：
     * <ul>
     *   <li>{@code 5062000} 奇迹魔方 Miracle</li>
     *   <li>{@code 5062001} / {@code 5062100} 高级/珍贵（可独特，不可传说）</li>
     *   <li>{@code 5062002} 超级奇迹</li>
     * </ul>
     * USE 自研 {@code 2049910}/{@code 2049916} 仍作<b>别名</b>一版兼容；附加魔方暂无官方 Cash，保留 {@code 2049911}。
     */
    /** @deprecated 请用 {@link #ITEM_MIRACLE_CUBE}；仍作别名 */
    public static final int ITEM_MAIN_CUBE_ALIAS = 2049910;
    public static final int ITEM_MIRACLE_CUBE = 5062000;
    /** 095PLUS2「[7周年]神奇魔方」— 逻辑同奇迹魔方 */
    public static final int ITEM_MIRACLE_CUBE_ANNIV = 5062102;
    public static final int ITEM_PREMIUM_CUBE = 5062001;
    public static final int ITEM_PREMIUM_CUBE_ALT = 5062100;
    public static final int ITEM_SUPER_CUBE = 5062002;
    /** @deprecated 请用 {@link #ITEM_SUPER_CUBE}；仍作别名 */
    public static final int ITEM_SUPER_CUBE_ALIAS = 2049916;
    /** 终极神奇魔方（国服升阶表简化）：Cash + USE 别名 */
    public static final int ITEM_ULTIMATE_CUBE = 5062003;
    public static final int ITEM_ULTIMATE_CUBE_ALIAS = 2049917;
    /** 怪异魔方：仅稀有/史诗（B/A）；官方 2710000 + Cash/USE 别名 */
    public static final int ITEM_WEIRD_CUBE = 2710000;
    public static final int ITEM_WEIRD_CUBE_CASH = 5062004;
    public static final int ITEM_WEIRD_CUBE_ALIAS = 2049919;
    /** 主魔方 canonical（GM / 文档默认） */
    public static final int ITEM_MAIN_CUBE = ITEM_MIRACLE_CUBE;

    /**
     * 附加潜能魔方。官方 Cash {@code 5062500} 大师附加；USE {@code 2049911} 兼容。
     */
    public static final int ITEM_BONUS_CUBE = 2049911;
    public static final int ITEM_BONUS_CUBE_CASH = 5062500;
    public static final int ITEM_GRADE_UP = 2049912;
    public static final int ITEM_SOCKET = 2049913;
    public static final int ITEM_SOUL_APPLY = 2049914;
    public static final int ITEM_SOUL_CLEAR = 2049915;

    /** 095 使用奇迹/高级魔方时掉落的方块碎片 */
    public static final int ITEM_CUBE_FRAGMENT = 2430112;
    /** 095 超级魔方碎片 */
    public static final int ITEM_SUPER_CUBE_FRAGMENT = 2430481;

    public static boolean isCubeOrGradeOrSocketScroll(int itemId) {
        return isMainCube(itemId) || isPremiumCube(itemId) || isBonusCube(itemId) || isSuperCube(itemId)
                || isUltimateCube(itemId) || isWeirdCube(itemId)
                || isGradeUpgradeScroll(itemId) || isSocketScroll(itemId);
    }

    /** 奇迹魔方：官方 5062000 + 095PLUS2 七周年 5062102 + 别名 2049910 */
    public static boolean isMainCube(int itemId) {
        return itemId == ITEM_MIRACLE_CUBE
                || itemId == ITEM_MIRACLE_CUBE_ANNIV
                || itemId == ITEM_MAIN_CUBE_ALIAS;
    }

    /** 高级/珍贵魔方：5062001 / 5062100 */
    public static boolean isPremiumCube(int itemId) {
        return itemId == ITEM_PREMIUM_CUBE || itemId == ITEM_PREMIUM_CUBE_ALT;
    }

    public static boolean isBonusCube(int itemId) {
        return itemId == ITEM_BONUS_CUBE || itemId == ITEM_BONUS_CUBE_CASH;
    }

    /** 超级魔方：官方 5062002 + 别名 2049916 */
    public static boolean isSuperCube(int itemId) {
        return itemId == ITEM_SUPER_CUBE || itemId == ITEM_SUPER_CUBE_ALIAS;
    }

    public static boolean isUltimateCube(int itemId) {
        return itemId == ITEM_ULTIMATE_CUBE || itemId == ITEM_ULTIMATE_CUBE_ALIAS;
    }

    public static boolean isWeirdCube(int itemId) {
        return itemId == ITEM_WEIRD_CUBE || itemId == ITEM_WEIRD_CUBE_CASH || itemId == ITEM_WEIRD_CUBE_ALIAS;
    }

    /** Cash 栏双击使用的魔方 */
    public static boolean isCashCube(int itemId) {
        return itemId == ITEM_MIRACLE_CUBE
                || itemId == ITEM_MIRACLE_CUBE_ANNIV
                || itemId == ITEM_PREMIUM_CUBE
                || itemId == ITEM_PREMIUM_CUBE_ALT
                || itemId == ITEM_SUPER_CUBE
                || itemId == ITEM_ULTIMATE_CUBE
                || itemId == ITEM_WEIRD_CUBE_CASH
                || itemId == ITEM_BONUS_CUBE_CASH;
    }

    /**
     * 奇迹魔方是否允许该品阶：独特(4)拒绝；传说(5)与更低（有潜能）允许。
     * 对应 095 {@code state>=5 && state!=7}。
     */
    public static boolean allowsMainCubeGrade(int grade) {
        return grade > 0 && grade != 4;
    }

    /**
     * 高级魔方：可独特，不可传说。对应 095 {@code state>=5 && state!=8}。
     */
    public static boolean allowsPremiumCubeGrade(int grade) {
        return grade > 0 && grade != 5;
    }

    /**
     * 超级/终极魔方是否允许该品阶：有主潜能即可（含独特/传说）。
     * 对应 095 {@code state>=5}。
     */
    public static boolean allowsSuperCubeGrade(int grade) {
        return grade > 0;
    }

    /** 怪异魔方：仅稀有(2)/史诗(3)。 */
    public static boolean allowsWeirdCubeGrade(int grade) {
        return grade == 2 || grade == 3;
    }

    /** 095 超级魔方升阶概率（百分数）。 */
    public static final int SUPER_CUBE_UPGRADE_CHANCE = 8;

    /** Wiki 终极：稀有→史诗 15%；史诗→独特 / 独特→传说 用万分比。 */
    public static final int ULTIMATE_RARE_TO_EPIC_PCT = 15;
    public static final int ULTIMATE_EPIC_TO_UNIQUE_BP = 350;
    public static final int ULTIMATE_UNIQUE_TO_LEGEND_BP = 140;

    /** Wiki 大师附加：稀有→史诗约 4.8%，其它约 0.5%。 */
    public static final int BONUS_CUBE_RARE_UP_BP = 480;
    public static final int BONUS_CUBE_OTHER_UP_BP = 50;

    /** 星岩槽数上限（socket1~3；封包尺寸 Phase10 → 0x140）。 */
    public static final int MAX_SOCKET_SLOTS = 3;

    public static boolean isGradeUpgradeScroll(int itemId) {
        return itemId == ITEM_GRADE_UP;
    }

    public static boolean isSocketScroll(int itemId) {
        return itemId == ITEM_SOCKET;
    }

    /**
     * 灵魂相关卷轴/道具（开槽、宝珠、清除）。
     * <ul>
     *   <li>2049914 开槽（100%）</li>
     *   <li>2049915 清除</li>
     *   <li>2590004~ 附魔石</li>
     *   <li>2591000~2591009 早期宝珠</li>
     * </ul>
     */
    public static boolean isSoulScroll(int itemId) {
        return isSoulClearScroll(itemId)
                || isSoulApplyScroll(itemId)
                || org.gms.soul.SoulOrbConfig.isEnchanter(itemId)
                || org.gms.soul.SoulOrbConfig.isOrb(itemId);
    }

    public static boolean isSoulApplyScroll(int itemId) {
        return itemId == ITEM_SOUL_APPLY || org.gms.soul.SoulOrbConfig.isEnchanter(itemId);
    }

    public static boolean isSoulClearScroll(int itemId) {
        return itemId == ITEM_SOUL_CLEAR;
    }

    public static boolean isSoulOrbItem(int itemId) {
        return org.gms.soul.SoulOrbConfig.isOrb(itemId);
    }

    /**
     * Phase7 鉴定放大镜（对齐 095 2460000~2460003）。
     * 装等档：reqLevel/10 ≤ 3/7/12；特级无上限。
     */
    public static final int ITEM_MAGNIFY_LV30 = 2460000;
    public static final int ITEM_MAGNIFY_LV70 = 2460001;
    public static final int ITEM_MAGNIFY_LV120 = 2460002;
    public static final int ITEM_MAGNIFY_ANY = 2460003;

    public static boolean isMagnifyingGlass(int itemId) {
        return itemId >= ITEM_MAGNIFY_LV30 && itemId <= ITEM_MAGNIFY_ANY;
    }

    /**
     * 095：{@code reqLevel = equipReqLevel/10}；
     * 2460000≤3、2460001≤7、2460002≤12、2460003 全通。
     *
     * @param equipReqLevel 装备穿戴需求等级（非 /10）
     */
    public static boolean magnifyFitsEquipLevel(int magnifyId, int equipReqLevel) {
        if (!isMagnifyingGlass(magnifyId)) {
            return false;
        }
        if (magnifyId == ITEM_MAGNIFY_ANY) {
            return true;
        }
        int band = Math.max(0, equipReqLevel) / 10;
        return switch (magnifyId) {
            case ITEM_MAGNIFY_LV30 -> band <= 3;
            case ITEM_MAGNIFY_LV70 -> band <= 7;
            case ITEM_MAGNIFY_LV120 -> band <= 12;
            default -> false;
        };
    }

    /** 放大镜档位说明（失败提示用）。 */
    public static String magnifyLevelBandDesc(int magnifyId) {
        return switch (magnifyId) {
            case ITEM_MAGNIFY_LV30 -> "需求等级≤30";
            case ITEM_MAGNIFY_LV70 -> "需求等级≤70";
            case ITEM_MAGNIFY_LV120 -> "需求等级≤120";
            case ITEM_MAGNIFY_ANY -> "全等级";
            default -> "未知";
        };
    }

    /** 是否走潜能/Hyper 专用 ScrollHandler 分支（含 Phase4 卷 / 放大镜） */
    public static boolean isPotentialFamilyScroll(int itemId) {
        return isHyperScroll(itemId)
                || isPotentialScroll(itemId)
                || isBonusPotentialScroll(itemId)
                || isClassicGradePotentialScroll(itemId)
                || isCubeOrGradeOrSocketScroll(itemId)
                || isSoulScroll(itemId)
                || isMagnifyingGlass(itemId)
                || org.gms.constants.inventory.ItemConstants.isResetScroll(itemId);
    }

    /**
     * Hyper 全局星数上限（对齐常见卷轴「1~10星」文案；tip/companion 同步 10）。
     * 个别「N星装备强化卷」另见 {@link #getHyperMaxEnhance(int)}。
     */
    public static final int MAX_ENHANCE = 10;
    public static final int MAX_GRADE = 5;

    /**
     * @deprecated 请用 {@link HyperEnhanceTable}；保留常量供旧 tip/测试对照（★1~5 段均值）。
     */
    @Deprecated
    public static final int HYPER_STAT_PER_STAR = 2;
    /** @deprecated 请用 {@link HyperEnhanceTable} */
    @Deprecated
    public static final int HYPER_ATK_PER_STAR = 2;

    /** 潜能附加默认成功率（WZ 无 success 时） */
    public static final int DEFAULT_POTENTIAL_SUCCESS = 90;

    /** 附加潜能默认成功率 */
    public static final int DEFAULT_BONUS_POTENTIAL_SUCCESS = 70;

    /**
     * Hyper 缺表时的兜底：对齐 T5(2049300) 1~10 星文案。
     * @deprecated 请用 {@link #getHyperSuccessRate(int, int)}
     */
    public static final int DEFAULT_HYPER_SUCCESS = 100;

    /**
     * Hyper 失败是否允许炸装（对齐 095 {@code scrollEnhance}：WZ cursed，缺省 100；noCursed=0）。
     * {@code forceSuccess} / GM {@code !potential star} 仍安全。默认开启；个别「失败不破坏」卷见 {@link #curseForHyperScroll}。
     */
    public static final boolean HYPER_DESTROY_ON_FAIL = true;

    /**
     * Hyper 失败诅咒兜底（百分数）。仅当 {@link #HYPER_DESTROY_ON_FAIL} 且卷无专用表项时使用。
     * Phase1 曾恒 0；Phase10 对齐 095 缺 cursed 时默认 100。
     */
    public static final int DEFAULT_HYPER_CURSED = 100;

    /**
     * 单卷 Hyper 失败炸装率。{@code 2049302} 等保星卷=0；文案「失败不破坏」族=0；其余对齐 095。
     */
    public static int curseForHyperScroll(int scrollId) {
        if (!HYPER_DESTROY_ON_FAIL) {
            return 0;
        }
        return switch (scrollId) {
            // 095：2049302 强制成功且 curse=0；部分「不破坏」卷
            case 2049302, 2049305, 2049314, 2049315, 2049316, 2049317, 2049318 -> 0;
            // 常见强化卷：失败按 cursed≈100（与 095 缺表默认一致）
            case 2049300, 2049301, 2049303, 2049304, 2049306, 2049307,
                 2049308, 2049309, 2049310, 2049311, 2049312, 2049313 -> 100;
            default -> DEFAULT_HYPER_CURSED;
        };
    }

    /** 魔方默认成功率（官服近乎必成；私服可调） */
    public static final int DEFAULT_CUBE_SUCCESS = 100;

    /**
     * 主潜能魔方 / 品阶提升卷成功后是否回到<strong>隐藏态</strong>（需放大镜再鉴定）。
     * {@code true} = 对齐 095 {@code renewPotential*}（只写负 rank / 条数提示）；
     * {@code false} = 旧行为：立即 roll 出 optionId。
     */
    public static final boolean CUBE_RESET_TO_HIDDEN = true;

    /** 品阶提升基础成功率（按当前品阶递减） */
    public static final int DEFAULT_GRADE_UP_SUCCESS = 40;

    /** 灵魂附加成功率 */
    public static final int DEFAULT_SOUL_SUCCESS = 85;

    /** 星岩镶嵌成功率 */
    public static final int DEFAULT_SOCKET_SUCCESS = 80;

    /**
     * 交易/面对面到手是否清空潜能+Hyper+附加+灵魂+星岩。
     * 官方多代对可交易潜能装备有限制；本服对齐「流通清空」防通胀（与灵韵 CLEAR_SPIRIT_ON_TRADE 同策）。
     * 095 开源本身多靠 UNTRADEABLE flag，不直接清字段——私服官方体验取「清空」一侧。
     */
    public static final boolean CLEAR_ON_TRADE = true;

    // —— T5~T0 / 神器 / 史诗：String desc「N星：x%」表（下标 0 = 冲 1 星）——
    private static final int[] RATE_T5 = {100, 95, 90, 85, 80, 75, 70, 65, 60, 55};
    private static final int[] RATE_T4 = {95, 90, 85, 80, 75, 70, 65, 60, 55, 50};
    private static final int[] RATE_T3 = {90, 85, 80, 75, 70, 65, 60, 55, 50, 45};
    private static final int[] RATE_T2 = {85, 80, 75, 70, 65, 60, 55, 50, 45, 40};
    private static final int[] RATE_T1 = {80, 75, 70, 65, 60, 55, 50, 45, 40, 35};
    private static final int[] RATE_T0 = {75, 70, 65, 60, 55, 50, 45, 40, 35, 30};
    private static final int[] RATE_ARTIFACT = {100, 90, 80, 70, 60, 50, 40, 30, 20, 10};
    private static final int[] RATE_EPIC = {80, 70, 60, 50, 40, 30, 20, 10, 5, 5};

    /**
     * 该卷允许的最大星数（不超过 {@link #MAX_ENHANCE}）。
     * 「2/3/4/5星装备强化卷」按名称/autodesc 截断；其余默认 10。
     */
    public static int getHyperMaxEnhance(int scrollId) {
        int cap = switch (scrollId) {
            case 2049309, 2049320, 2049326, 2049327 -> 2;
            case 2049311, 2049319, 2049321, 2049328 -> 3;
            case 2049312 -> 4;
            case 2049308, 2049310, 2049313, 2049322, 2049324 -> 5;
            default -> MAX_ENHANCE;
        };
        return Math.min(MAX_ENHANCE, cap);
    }

    /**
     * 冲下一星的成功率（百分数）。
     *
     * @param scrollId       Hyper 卷 ID
     * @param currentEnhance 当前星数（0~max-1）；表项对应「冲到 currentEnhance+1」
     */
    public static int getHyperSuccessRate(int scrollId, int currentEnhance) {
        int[] table = hyperRateTable(scrollId);
        int idx = Math.max(0, currentEnhance);
        if (idx >= table.length) {
            return Math.max(1, table[table.length - 1]);
        }
        return Math.max(1, Math.min(100, table[idx]));
    }

    private static int[] hyperRateTable(int scrollId) {
        return switch (scrollId) {
            case 2049300 -> RATE_T5;
            case 2049301 -> RATE_T4;
            case 2049302 -> RATE_T3;
            case 2049303 -> RATE_T2;
            case 2049304 -> RATE_T1;
            case 2049305 -> RATE_T0;
            case 2049306, 2049323, 2049325 -> RATE_ARTIFACT;
            case 2049307 -> RATE_EPIC;
            // 固定成功率卷：按 autodesc，长度=该卷星上限
            case 2049308, 2049310 -> flatRate(50, 5);
            case 2049309 -> flatRate(80, 2);
            case 2049311, 2049321 -> flatRate(60, 3);
            case 2049312 -> flatRate(40, 4);
            case 2049313, 2049322, 2049324 -> flatRate(30, 5);
            case 2049319 -> flatRate(60, 3);
            case 2049320, 2049326 -> flatRate(90, 2);
            case 2049327 -> flatRate(30, 2);
            case 2049328 -> flatRate(10, 3);
            default -> RATE_T5;
        };
    }

    private static int[] flatRate(int rate, int stars) {
        int n = Math.max(1, Math.min(MAX_ENHANCE, stars));
        int[] t = new int[n];
        for (int i = 0; i < n; i++) {
            t[i] = rate;
        }
        return t;
    }
}
