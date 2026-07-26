package org.gms.potential;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 潜能附加 / Hyper 强化 / 附加潜能 / 魔方洗潜 / 品阶 / 灵魂宝珠 / 星岩结算。
 */
public final class PotentialHyperService {
    private static final Logger log = LoggerFactory.getLogger(PotentialHyperService.class);

    private PotentialHyperService() {}

    public enum Result {
        SUCCESS, FAIL, CURSE, INVALID
    }

    public static final class StatBonus {
        public int str, dex, inte, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, speed, jump;
        /** 百分比面板（095 percent_*） */
        public int strR, dexR, intR, lukR, hpR, mpR, padR, madR;
        /** 战斗向（接入 CombatProfile） */
        public int critRate, critDam, damR, bossDamR, ignoreDef;
        /** Phase11 tip→实装：掉落%/金币%/反伤%/全技能+1/冷却秒 */
        public int dropProp, mesoProp, damReflect, damReflectProp, allSkill, cooltimeReduce;
        /** Phase11+ tip→实装：技能 MP 消耗减免%（mpconReduce） */
        public int mpconReduce;
    }

    /**
     * 从装备潜能 + 附加潜能 + 灵魂 + 星岩 + Hyper 星汇总属性加成（不改装备本体数值）。
     * 封包 tip 用 {@code charLevel=0}（等级缩放另由 STAT_CHANGED 补）；战斗/面板用真实等级。
     */
    public static StatBonus computeBonus(Equip equip) {
        return computeBonus(equip, 0);
    }

    /**
     * @param charLevel 角色等级；&gt;0 时结算 incSTRlv 等（WZ：每满 9 级 × 选项值）
     */
    public static StatBonus computeBonus(Equip equip, int charLevel) {
        StatBonus b = new StatBonus();
        if (equip == null) {
            return b;
        }
        int req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        int potLevel = PotentialRules095.equipOptionLevel(req);

        ItemOptionProvider opt = ItemOptionProvider.getInstance();
        applyOption(b, opt.getStats(equip.getPotential1(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getPotential2(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getPotential3(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getBonusPotential1(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getBonusPotential2(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getBonusPotential3(), potLevel), charLevel);
        // 旧版随机灵魂 option；新版 2591 宝珠由 SoulWeaponService 叠固定表
        if (!org.gms.soul.SoulOrbConfig.isOrb(equip.getSoulId())) {
            applyOption(b, opt.getStats(equip.getSoulOption(), potLevel), charLevel);
        }
        org.gms.soul.SoulWeaponService.applyOrbBonus(b, equip);
        applyOption(b, opt.getStats(equip.getSocket1(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getSocket2(), potLevel), charLevel);
        applyOption(b, opt.getStats(equip.getSocket3(), potLevel), charLevel);

        int star = Math.max(0, Math.min(PotentialHyperConfig.MAX_ENHANCE, equip.getEnhance()));
        if (star > 0) {
            int all = HyperEnhanceTable.cumulativeAllStat(star);
            int atk = HyperEnhanceTable.cumulativeAtk(star, equip.getItemId());
            b.str += all;
            b.dex += all;
            b.inte += all;
            b.luk += all;
            b.watk += atk;
            b.matk += atk;
        }
        return b;
    }

    private static void applyOption(StatBonus b, Map<String, Integer> stats, int charLevel) {
        if (stats == null || stats.isEmpty()) {
            return;
        }
        b.str += stats.getOrDefault("incSTR", 0);
        b.dex += stats.getOrDefault("incDEX", 0);
        b.inte += stats.getOrDefault("incINT", 0);
        b.luk += stats.getOrDefault("incLUK", 0);
        b.hp += stats.getOrDefault("incMHP", 0);
        b.mp += stats.getOrDefault("incMMP", 0);
        b.watk += stats.getOrDefault("incPAD", 0);
        b.matk += stats.getOrDefault("incMAD", 0);
        b.wdef += stats.getOrDefault("incPDD", 0);
        b.mdef += stats.getOrDefault("incMDD", 0);
        b.acc += stats.getOrDefault("incACC", 0);
        b.avoid += stats.getOrDefault("incEVA", 0);
        b.speed += stats.getOrDefault("incSpeed", 0);
        b.jump += stats.getOrDefault("incJump", 0);

        // WZ string: 对角色等级每满9级STR+#incSTRlv
        if (charLevel > 0) {
            int lvMul = charLevel / 9;
            if (lvMul > 0) {
                b.str += stats.getOrDefault("incSTRlv", 0) * lvMul;
                b.dex += stats.getOrDefault("incDEXlv", 0) * lvMul;
                b.inte += stats.getOrDefault("incINTlv", 0) * lvMul;
                b.luk += stats.getOrDefault("incLUKlv", 0) * lvMul;
                b.hp += stats.getOrDefault("incMHPlv", 0) * lvMul;
            }
        }

        b.strR += stats.getOrDefault("incSTRr", 0);
        b.dexR += stats.getOrDefault("incDEXr", 0);
        b.intR += stats.getOrDefault("incINTr", 0);
        b.lukR += stats.getOrDefault("incLUKr", 0);
        b.hpR += stats.getOrDefault("incMHPr", 0);
        b.mpR += stats.getOrDefault("incMMPr", 0);
        b.padR += stats.getOrDefault("incPADr", 0);
        b.madR += stats.getOrDefault("incMADr", 0);

        b.critRate += stats.getOrDefault("incCr", 0);
        // tip 已显示；095 StructPotentialItem 未接，本服补战斗
        int cd = stats.getOrDefault("incCriticaldamage", 0);
        if (cd == 0) {
            int min = stats.getOrDefault("incCriticaldamageMin", 0);
            int max = stats.getOrDefault("incCriticaldamageMax", 0);
            if (min > 0 || max > 0) {
                cd = (min + max) / 2;
                if (cd == 0) {
                    cd = Math.max(min, max);
                }
            }
        }
        b.critDam += cd;

        int dam = stats.getOrDefault("incDAMr", 0);
        if (dam > 0) {
            if (stats.getOrDefault("boss", 0) > 0) {
                b.bossDamR += dam;
            } else {
                b.damR += dam;
            }
        }
        b.ignoreDef += stats.getOrDefault("ignoreTargetDEF", 0);

        // Phase11：原 tip-only 词条
        b.dropProp += stats.getOrDefault("incRewardProp", 0);
        b.mesoProp += stats.getOrDefault("incMesoProp", 0);
        int reflect = stats.getOrDefault("DAMreflect", 0);
        if (reflect > 0) {
            b.damReflect += reflect;
            int prop = stats.getOrDefault("prop", 0);
            b.damReflectProp += prop > 0 ? prop : 100;
        }
        b.allSkill += stats.getOrDefault("incAllskill", 0);
        b.cooltimeReduce += stats.getOrDefault("reduceCooltime", 0);
        b.mpconReduce += stats.getOrDefault("mpconReduce", 0);
    }

    /**
     * 主潜能附加卷：对齐 095 {@code resetPotential*} —— 成功后进入<strong>隐藏潜能</strong>，
     * 需放大镜鉴定才 roll 出具体选项（Phase7）。魔方仍对已鉴定装备立即重随。
     */
    public static Result applyPotentialScroll(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !(PotentialHyperConfig.isPotentialScroll(scrollId)
                || PotentialHyperConfig.isClassicGradePotentialScroll(scrollId))) {
            return Result.INVALID;
        }
        if (PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "该装备已有潜能，请使用神奇魔方（5062000）重随（独特除外）；未鉴定请先用放大镜。");
            return Result.INVALID;
        }
        int success = PotentialHyperConfig.successForPotentialScroll(scrollId);
        if (!forceSuccess && Randomizer.nextInt(100) >= success) {
            // 095 S 级卷失败可损毁；A 级官名多为 cursed=0
            int curse = PotentialHyperConfig.curseForPotentialScroll(scrollId);
            if (curse > 0 && Randomizer.nextInt(100) < curse) {
                return Result.CURSE;
            }
            return Result.FAIL;
        }
        int grade;
        int lines;
        if (PotentialHyperConfig.isClassicEpicPotentialScroll(scrollId)) {
            grade = PotentialRules095.initialGradeEpic();
            lines = PotentialRules095.firstIdentifyLinesWide();
        } else if (PotentialHyperConfig.isClassicUniquePotentialScroll(scrollId)) {
            grade = PotentialRules095.initialGradeUnique();
            lines = PotentialRules095.firstIdentifyLinesWide();
        } else {
            grade = PotentialRules095.rollInitialGradeNormal();
            lines = PotentialRules095.firstIdentifyLinesNormal();
        }
        setMainHidden(equip, grade, lines);
        log.info("potential hidden char={} equip={} scroll={} grade={} pendingLines={}",
                chr.getId(), equip.getItemId(), scrollId, grade, lines);
        return Result.SUCCESS;
    }

    /**
     * Phase7 放大镜鉴定：对齐 095 {@code UseMagnify}。
     * 要求主潜能为隐藏态；按装等档校验镜等级；成功则按当前 grade + 待鉴定条数 roll。
     */
    public static Result applyMagnify(Character chr, Equip equip, int magnifyId) {
        if (equip == null || !PotentialHyperConfig.isMagnifyingGlass(magnifyId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            if (PotentialRules095.isMainRevealed(equip.getPotential1())) {
                chr.dropMessage(5, "该装备潜能已鉴定，无需放大镜。");
            } else {
                chr.dropMessage(5, "该装备没有未鉴定的潜能。");
            }
            return Result.INVALID;
        }
        // 与 tip「要求等级」同源：Character info/reqLevel（getEquipLevelReq）。
        // tip 显示 0 的时装/高版本移植装，服务端 WZ 也须为 0，否则会出现 tip0 却拒初级镜。
        int reqLv = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        if (!PotentialHyperConfig.magnifyFitsEquipLevel(magnifyId, reqLv)) {
            chr.dropMessage(5, "放大镜等级不足（本镜："
                    + PotentialHyperConfig.magnifyLevelBandDesc(magnifyId)
                    + "，装备需求等级" + reqLv + "）。请换更高级镜或特级镜（2460003）。");
            return Result.INVALID; // 不消耗镜子
        }
        int grade = Math.max(1, equip.getPotentialGrade());
        int lines = PotentialRules095.pendingLineCount(equip.getPotential2());
        int[] rolled = rollOptions(equip.getItemId(), grade, lines);
        applyMainLines(equip, grade, rolled);
        log.info("magnify reveal char={} equip={} glass={} grade={} lines={} opts={}/{}/{}",
                chr.getId(), equip.getItemId(), magnifyId, grade, lines, rolled[0], rolled[1], rolled[2]);
        return Result.SUCCESS;
    }

    /**
     * 附加潜能卷：要求已有主潜能；给尚无附加潜能的装备 roll 1~3 条。
     */
    public static Result applyBonusPotentialScroll(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isBonusPotentialScroll(scrollId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "请先附加主潜能，再使用附加潜能卷。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定主潜能，再附加附加潜能。");
            return Result.INVALID;
        }
        if (equip.getBonusPotential1() > 0 || equip.getBonusPotentialGrade() > 0) {
            chr.dropMessage(5, "该装备已有附加潜能，请使用附加魔方（2049911）重随。");
            return Result.INVALID;
        }
        int success = PotentialHyperConfig.DEFAULT_BONUS_POTENTIAL_SUCCESS;
        if (!forceSuccess && Randomizer.nextInt(100) >= success) {
            return Result.FAIL;
        }
        int lines = PotentialRules095.firstIdentifyLinesNormal();
        // 与主潜能首次一致：绝大多数稀有，小概率史诗/独特；池按品阶分档
        int grade = PotentialRules095.rollInitialGradeNormal();
        int[] rolled = rollBonusOptions(equip.getItemId(), grade, lines);
        equip.setBonusPotential1(rolled[0]);
        equip.setBonusPotential2(rolled[1]);
        equip.setBonusPotential3(rolled[2]);
        equip.setBonusPotentialGrade((byte) grade);
        log.info("bonus potential applied char={} equip={} scroll={} grade={} opts={}/{}/{}",
                chr.getId(), equip.getItemId(), scrollId, grade, rolled[0], rolled[1], rolled[2]);
        return Result.SUCCESS;
    }

    /**
     * Hyper 升星：要求 upgradeSlots==0（升级结束）。
     * 成功率按卷轴表；失败按 {@link PotentialHyperConfig#curseForHyperScroll} 可能炸装（095 风格）。
     * {@code forceSuccess} 仅供 GM 命令显式传入；ScrollHandler 不得用 isGM 恒 true。
     * GM 安全设星请用 {@code !potential star}。
     */
    public static Result applyHyperScroll(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isHyperScroll(scrollId)) {
            return Result.INVALID;
        }
        if (equip.getUpgradeSlots() > 0 && !forceSuccess) {
            chr.dropMessage(5, "请先用尽装备的可升级次数（剩余" + equip.getUpgradeSlots()
                    + "次），再使用强化卷。GM 可用 !potential star <槽> <星> 强制设星。");
            return Result.INVALID;
        }
        int maxStar = PotentialHyperConfig.getHyperMaxEnhance(scrollId);
        if (equip.getEnhance() >= maxStar) {
            chr.dropMessage(5, "已达 Hyper 强化上限（★" + maxStar + "）。");
            return Result.INVALID;
        }
        int success = PotentialHyperConfig.getHyperSuccessRate(scrollId, equip.getEnhance());
        if (!forceSuccess && Randomizer.nextInt(100) >= success) {
            int curse = PotentialHyperConfig.curseForHyperScroll(scrollId);
            if (curse > 0 && Randomizer.nextInt(100) < curse) {
                return Result.CURSE;
            }
            return Result.FAIL;
        }
        equip.setEnhance((byte) (equip.getEnhance() + 1));
        log.info("hyper enhance char={} equip={} scroll={} star={} rate={}",
                chr.getId(), equip.getItemId(), scrollId, equip.getEnhance(), success);
        return Result.SUCCESS;
    }

    /**
     * 奇迹魔方（5062000 / 别名 2049910）：对齐 095 {@code renewPotential(false)}。
     * <ul>
     *   <li>独特(grade==4 / state7)：不可用；请用超级魔方 5062002 或高级魔方 5062001</li>
     *   <li>传说(grade==5)：可用，但跃迁/保阶按 095 压回独特（最高独特）</li>
     *   <li>稀有→史诗约 2%，其它 0.5% 升阶；奇迹跃迁最高独特</li>
     *   <li>成功后进入隐藏态，需放大镜鉴定（{@link PotentialHyperConfig#CUBE_RESET_TO_HIDDEN}）</li>
     * </ul>
     */
    public static Result applyMainCube(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isMainCube(scrollId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "装备无主潜能，无法使用魔方。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定潜能，再使用魔方。");
            return Result.INVALID;
        }
        int grade = Math.max(1, equip.getPotentialGrade());
        if (!PotentialHyperConfig.allowsMainCubeGrade(grade)) {
            chr.dropMessage(5, "该装备潜能为独特，无法使用神奇魔方。请使用高级神奇魔方（5062001）或超级神奇魔方（5062002）。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_CUBE_SUCCESS) {
            return Result.FAIL;
        }
        // 095：传说用奇迹也会走 renew（-8→-7 压回独特）；GM force 才保阶
        final int newGrade = forceSuccess ? grade : PotentialRules095.renewGradeMiracle(grade);
        int lines = PotentialRules095.cubeLineCount(
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        applyCubeResult(equip, newGrade, lines);
        log.info("main cube char={} equip={} id={} grade={}→{} lines={} hidden={}",
                chr.getId(), equip.getItemId(), scrollId, grade, newGrade, lines,
                PotentialHyperConfig.CUBE_RESET_TO_HIDDEN);
        return Result.SUCCESS;
    }

    /**
     * 高级/珍贵魔方（5062001 / 5062100）：对齐 095 {@code renewPotential1}。
     * 可独特；不可传说（state!=8）。成功后隐藏待鉴定。
     */
    public static Result applyPremiumCube(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isPremiumCube(scrollId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "装备无主潜能，无法使用高级神奇魔方。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定潜能，再使用高级神奇魔方。");
            return Result.INVALID;
        }
        int grade = Math.max(1, equip.getPotentialGrade());
        if (!PotentialHyperConfig.allowsPremiumCubeGrade(grade)) {
            chr.dropMessage(5, "该装备潜能为传说，无法使用高级神奇魔方。请使用超级神奇魔方（5062002）。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_CUBE_SUCCESS) {
            return Result.FAIL;
        }
        final int newGrade = forceSuccess ? grade : PotentialRules095.renewGradePremium(grade);
        int lines = PotentialRules095.cubeLineCount(
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        // 095 renewPotential1(prem)：非三线时 prem=true 约 2% 扩到三线
        if (lines < 3 && !forceSuccess && Randomizer.nextInt(100) < 2) {
            lines = 3;
        }
        applyCubeResult(equip, newGrade, lines);
        log.info("premium cube char={} equip={} id={} grade={}→{} lines={} hidden={}",
                chr.getId(), equip.getItemId(), scrollId, grade, newGrade, lines,
                PotentialHyperConfig.CUBE_RESET_TO_HIDDEN);
        return Result.SUCCESS;
    }

    /**
     * 超级魔方（5062002 / 别名 2049916）：对齐 095 {@code renewPotential_super}。
     * 成功后隐藏待鉴定。
     */
    public static Result applySuperCube(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isSuperCube(scrollId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "装备无主潜能，无法使用超级神奇魔方。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定潜能，再使用超级神奇魔方。");
            return Result.INVALID;
        }
        int grade = Math.max(1, equip.getPotentialGrade());
        if (!PotentialHyperConfig.allowsSuperCubeGrade(grade)) {
            chr.dropMessage(5, "装备无主潜能，无法使用超级神奇魔方。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_CUBE_SUCCESS) {
            return Result.FAIL;
        }
        final int newGrade = forceSuccess ? grade : PotentialRules095.renewGradeSuper(grade);
        int lines = PotentialRules095.cubeLineCount(
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        applyCubeResult(equip, newGrade, lines);
        log.info("super cube char={} equip={} id={} grade={}→{} lines={} hidden={}",
                chr.getId(), equip.getItemId(), scrollId, grade, newGrade, lines,
                PotentialHyperConfig.CUBE_RESET_TO_HIDDEN);
        return Result.SUCCESS;
    }

    /** 终极神奇魔方：升阶率高于超级，可到传说。 */
    public static Result applyUltimateCube(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isUltimateCube(scrollId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "装备无主潜能，无法使用终极神奇魔方。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定潜能，再使用终极神奇魔方。");
            return Result.INVALID;
        }
        int grade = Math.max(1, equip.getPotentialGrade());
        if (!PotentialHyperConfig.allowsSuperCubeGrade(grade)) {
            chr.dropMessage(5, "装备无主潜能，无法使用终极神奇魔方。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_CUBE_SUCCESS) {
            return Result.FAIL;
        }
        final int newGrade = forceSuccess ? grade : PotentialRules095.renewGradeUltimate(grade);
        int lines = PotentialRules095.cubeLineCount(
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        applyCubeResult(equip, newGrade, lines);
        log.info("ultimate cube char={} equip={} id={} grade={}→{} lines={}",
                chr.getId(), equip.getItemId(), scrollId, grade, newGrade, lines);
        return Result.SUCCESS;
    }

    /** 怪异魔方：仅稀有/史诗，不升独特/传说。 */
    public static Result applyWeirdCube(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isWeirdCube(scrollId)) {
            return Result.INVALID;
        }
        if (!PotentialRules095.hasMainPotential(equip.getPotentialGrade(), equip.getPotential1())) {
            chr.dropMessage(5, "装备无主潜能，无法使用怪异魔方。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定潜能，再使用怪异魔方。");
            return Result.INVALID;
        }
        int grade = Math.max(1, equip.getPotentialGrade());
        if (!PotentialHyperConfig.allowsWeirdCubeGrade(grade)) {
            chr.dropMessage(5, "怪异魔方仅可用于稀有/史诗潜能装备。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_CUBE_SUCCESS) {
            return Result.FAIL;
        }
        final int newGrade = forceSuccess ? grade : PotentialRules095.renewGradeWeird(grade);
        int lines = PotentialRules095.cubeLineCount(
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        applyCubeResult(equip, newGrade, lines);
        log.info("weird cube char={} equip={} id={} grade={}→{} lines={}",
                chr.getId(), equip.getItemId(), scrollId, grade, newGrade, lines);
        return Result.SUCCESS;
    }

    /** 附加潜能魔方。 */
    public static Result applyBonusCube(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isBonusCube(scrollId)) {
            return Result.INVALID;
        }
        if (equip.getBonusPotentialGrade() <= 0 && equip.getBonusPotential1() <= 0) {
            chr.dropMessage(5, "装备无附加潜能，无法使用附加魔方。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_CUBE_SUCCESS) {
            return Result.FAIL;
        }
        int grade = Math.max(1, equip.getBonusPotentialGrade());
        if (!forceSuccess) {
            grade = PotentialRules095.renewGradeBonusCube(grade);
        }
        int lines = PotentialRules095.cubeLineCount(
                equip.getBonusPotential1(), equip.getBonusPotential2(), equip.getBonusPotential3());
        int[] rolled = rollBonusOptions(equip.getItemId(), grade, lines);
        equip.setBonusPotential1(rolled[0]);
        equip.setBonusPotential2(rolled[1]);
        equip.setBonusPotential3(lines >= 3 ? rolled[2] : 0);
        equip.setBonusPotentialGrade((byte) grade);
        log.info("bonus cube char={} equip={} grade={} lines={} opts={}/{}/{}",
                chr.getId(), equip.getItemId(), grade, lines, rolled[0], rolled[1], rolled[2]);
        return Result.SUCCESS;
    }

    /** 主潜能品阶提升（最高传说）。成功后按新品阶隐藏待鉴定（与魔方同流）。 */
    public static Result applyGradeUpgrade(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isGradeUpgradeScroll(scrollId)) {
            return Result.INVALID;
        }
        int grade = equip.getPotentialGrade();
        if (!PotentialRules095.hasMainPotential(grade, equip.getPotential1())) {
            chr.dropMessage(5, "装备无主潜能，无法提升品阶。");
            return Result.INVALID;
        }
        if (PotentialRules095.isMainHidden(grade,
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
            chr.dropMessage(5, "请先用放大镜鉴定潜能，再提升品阶。");
            return Result.INVALID;
        }
        if (grade >= PotentialHyperConfig.MAX_GRADE) {
            chr.dropMessage(5, "主潜能已达传说品阶。");
            return Result.INVALID;
        }
        if (grade <= 0) {
            grade = 1;
        }
        int success = Math.max(8, PotentialHyperConfig.DEFAULT_GRADE_UP_SUCCESS - (grade - 1) * 8);
        if (!forceSuccess && Randomizer.nextInt(100) >= success) {
            return Result.FAIL;
        }
        int newGrade = grade + 1;
        int lines = PotentialRules095.cubeLineCount(
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        applyCubeResult(equip, newGrade, lines);
        log.info("grade up char={} equip={} {}→{} lines={} hidden={}",
                chr.getId(), equip.getItemId(), grade, newGrade, lines,
                PotentialHyperConfig.CUBE_RESET_TO_HIDDEN);
        return Result.SUCCESS;
    }

    /** 灵魂开槽 / 镶珠 / 清除（委托 SoulWeaponService；兼容旧 2049914 随机线装备）。 */
    public static Result applySoulScroll(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isSoulScroll(scrollId)) {
            return Result.INVALID;
        }
        return org.gms.soul.SoulWeaponService.applyScroll(chr, equip, scrollId, forceSuccess);
    }

    /** 星岩镶嵌：填下一空槽（socket1→2→3；封包尺寸 0x140）。 */
    public static Result applySocketScroll(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (equip == null || !PotentialHyperConfig.isSocketScroll(scrollId)) {
            return Result.INVALID;
        }
        int slot;
        if (equip.getSocket1() <= 0) {
            slot = 1;
        } else if (equip.getSocket2() <= 0 && PotentialHyperConfig.MAX_SOCKET_SLOTS >= 2) {
            slot = 2;
        } else if (equip.getSocket3() <= 0 && PotentialHyperConfig.MAX_SOCKET_SLOTS >= 3) {
            slot = 3;
        } else {
            chr.dropMessage(5, "【星岩】槽已满（最多" + PotentialHyperConfig.MAX_SOCKET_SLOTS
                    + "槽）。清除请用 !potential socket clear <槽>。");
            return Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= PotentialHyperConfig.DEFAULT_SOCKET_SUCCESS) {
            return Result.FAIL;
        }
        int[] rolled = rollOptions(equip.getItemId(), 2, 1);
        if (slot == 1) {
            equip.setSocket1(rolled[0]);
        } else if (slot == 2) {
            equip.setSocket2(rolled[0]);
        } else {
            equip.setSocket3(rolled[0]);
        }
        log.info("socket applied char={} equip={} slot={} option={}",
                chr.getId(), equip.getItemId(), slot, rolled[0]);
        return Result.SUCCESS;
    }

    /** Phase4/6/9 卷统一入口（魔方/品阶/灵魂/星岩/超级/高级魔方）。 */
    public static Result applyPhase4Scroll(Character chr, Equip equip, int scrollId, boolean forceSuccess) {
        if (PotentialHyperConfig.isMainCube(scrollId)) {
            return applyMainCube(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isPremiumCube(scrollId)) {
            return applyPremiumCube(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isSuperCube(scrollId)) {
            return applySuperCube(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isUltimateCube(scrollId)) {
            return applyUltimateCube(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isWeirdCube(scrollId)) {
            return applyWeirdCube(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isBonusCube(scrollId)) {
            return applyBonusCube(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isGradeUpgradeScroll(scrollId)) {
            return applyGradeUpgrade(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isSocketScroll(scrollId)) {
            return applySocketScroll(chr, equip, scrollId, forceSuccess);
        }
        if (PotentialHyperConfig.isSoulScroll(scrollId)) {
            return applySoulScroll(chr, equip, scrollId, forceSuccess);
        }
        return Result.INVALID;
    }

    public static void setPotential(Equip equip, int grade, int o1, int o2, int o3) {
        equip.setPotentialGrade((byte) Math.max(0, Math.min(PotentialHyperConfig.MAX_GRADE, grade)));
        equip.setPotential1(o1);
        equip.setPotential2(o2);
        equip.setPotential3(o3);
    }

    /** 设为隐藏潜能：grade + 条数提示（pot2=-1 表示待 3 线）。不清附加/灵魂。 */
    public static void setMainHidden(Equip equip, int grade, int lines) {
        int g = Math.max(1, Math.min(PotentialHyperConfig.MAX_GRADE, grade));
        int ln = Math.max(2, Math.min(3, lines));
        equip.setPotentialGrade((byte) g);
        equip.setPotential1(0);
        equip.setPotential2(ln >= 3 ? -1 : 0);
        equip.setPotential3(0);
    }

    /** GM：把已鉴定主潜能压回隐藏（保留 grade 与条数）。 */
    public static void hideMainPotential(Equip equip) {
        if (equip == null) {
            return;
        }
        int grade = Math.max(1, equip.getPotentialGrade() > 0 ? equip.getPotentialGrade() : 2);
        int lines = PotentialRules095.isMainRevealed(equip.getPotential1())
                ? PotentialRules095.revealedLineCount(equip.getPotential1(), equip.getPotential2(), equip.getPotential3())
                : PotentialRules095.pendingLineCount(equip.getPotential2());
        if (lines < 2) {
            lines = 2;
        }
        setMainHidden(equip, grade, lines);
    }

    /** GM / 鉴定：立即按当前 grade 揭示（等同放大镜成功）。 */
    public static Result revealMainPotential(Character chr, Equip equip) {
        if (equip == null) {
            return Result.INVALID;
        }
        if (PotentialRules095.isMainRevealed(equip.getPotential1())) {
            return Result.INVALID;
        }
        int grade = Math.max(1, equip.getPotentialGrade() > 0 ? equip.getPotentialGrade() : 2);
        if (equip.getPotentialGrade() <= 0) {
            equip.setPotentialGrade((byte) grade);
        }
        int lines = PotentialRules095.pendingLineCount(equip.getPotential2());
        if (lines < 2) {
            lines = 2;
        }
        int[] rolled = rollOptions(equip.getItemId(), grade, lines);
        applyMainLines(equip, grade, rolled);
        if (chr != null) {
            log.info("gm/reveal char={} equip={} grade={} lines={} opts={}/{}/{}",
                    chr.getId(), equip.getItemId(), grade, lines, rolled[0], rolled[1], rolled[2]);
        }
        return Result.SUCCESS;
    }

    public static void setBonusPotential(Equip equip, int grade, int o1, int o2, int o3) {
        equip.setBonusPotentialGrade((byte) Math.max(0, Math.min(PotentialHyperConfig.MAX_GRADE, grade)));
        equip.setBonusPotential1(Math.max(0, o1));
        equip.setBonusPotential2(Math.max(0, o2));
        equip.setBonusPotential3(Math.max(0, o3));
    }

    public static void setSoul(Equip equip, int soulId, int soulOption) {
        equip.setSoulId(Math.max(0, soulId));
        equip.setSoulOption(Math.max(0, soulOption));
        if (org.gms.soul.SoulOrbConfig.isOrb(soulId)) {
            equip.setEquipSkillId(soulId);
            equip.setEquipSkillLevel(1);
        } else if (soulId == org.gms.soul.SoulOrbConfig.SLOT_OPEN) {
            equip.setEquipSkillId(0);
            equip.setEquipSkillLevel(0);
        }
    }

    public static void clearSoul(Equip equip) {
        equip.setSoulId(0);
        equip.setSoulOption(0);
        equip.setEquipSkillId(0);
        equip.setEquipSkillLevel(0);
    }

    public static void setSocket(Equip equip, int socket1) {
        equip.setSocket1(Math.max(0, socket1));
    }

    public static void setSocket(Equip equip, int socket1, int socket2) {
        equip.setSocket1(Math.max(0, socket1));
        equip.setSocket2(Math.max(0, socket2));
    }

    public static void setSocket(Equip equip, int socket1, int socket2, int socket3) {
        equip.setSocket1(Math.max(0, socket1));
        equip.setSocket2(Math.max(0, socket2));
        equip.setSocket3(Math.max(0, socket3));
    }

    public static void clearSocket(Equip equip) {
        equip.setSocket1(0);
        equip.setSocket2(0);
        equip.setSocket3(0);
    }

    /** 交易到手清空潜能族（受 {@link PotentialHyperConfig#CLEAR_ON_TRADE} 控制）。 */
    public static void clearOnTradeIfEnabled(Equip equip) {
        if (equip == null || !PotentialHyperConfig.CLEAR_ON_TRADE) {
            return;
        }
        clear(equip);
    }

    public static void setEnhance(Equip equip, int star) {
        equip.setEnhance((byte) Math.max(0, Math.min(PotentialHyperConfig.MAX_ENHANCE, star)));
    }

    public static void clear(Equip equip) {
        equip.setPotential1(0);
        equip.setPotential2(0);
        equip.setPotential3(0);
        equip.setPotentialGrade((byte) 0);
        equip.setEnhance((byte) 0);
        clearBonus(equip);
        clearSoul(equip);
        clearSocket(equip);
    }

    public static void clearBonus(Equip equip) {
        equip.setBonusPotential1(0);
        equip.setBonusPotential2(0);
        equip.setBonusPotential3(0);
        equip.setBonusPotentialGrade((byte) 0);
    }

    /**
     * 魔方结果仅发给当前角色聊天（dropMessage type 5，他人不可见），不弹客户端结果窗。
     */
    public static void notifyCubeResult(Character chr, String cubeTag, String detail) {
        if (chr == null) {
            return;
        }
        String tag = (cubeTag == null || cubeTag.isBlank()) ? "魔方" : cubeTag.trim();
        String body = detail == null ? "" : detail.trim();
        if (body.isEmpty()) {
            chr.dropMessage(5, "【" + tag + "】重随成功。");
        } else if (body.startsWith("重随") || body.startsWith("主潜能") || body.contains("★")) {
            chr.dropMessage(5, "【" + tag + "】" + body);
        } else {
            chr.dropMessage(5, "【" + tag + "】重随成功。 " + body);
        }
    }

    public static String describe(Equip equip) {
        if (equip == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (equip.getEnhance() > 0) {
            sb.append("★").append(equip.getEnhance()).append(' ');
        }
        if (equip.getPotentialGrade() > 0 || equip.getPotential1() > 0 || equip.getPotential2() < 0) {
            sb.append("潜能[").append(gradeName(equip.getPotentialGrade())).append("] ");
            if (PotentialRules095.isMainHidden(equip.getPotentialGrade(),
                    equip.getPotential1(), equip.getPotential2(), equip.getPotential3())) {
                sb.append("未鉴定(").append(PotentialRules095.pendingLineCount(equip.getPotential2()))
                        .append("线)");
            } else {
                sb.append(equip.getPotential1()).append('/').append(equip.getPotential2())
                        .append('/').append(equip.getPotential3());
            }
        }
        if (equip.getBonusPotentialGrade() > 0 || equip.getBonusPotential1() > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append("附加[").append(gradeName(equip.getBonusPotentialGrade())).append("] ");
            sb.append(equip.getBonusPotential1()).append('/').append(equip.getBonusPotential2())
                    .append('/').append(equip.getBonusPotential3());
        }
        if (equip.getSoulId() > 0 || equip.getSoulOption() > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            String soulDesc = org.gms.soul.SoulWeaponService.describe(equip);
            sb.append(soulDesc.isEmpty()
                    ? "灵魂[" + equip.getSoulId() + ':' + equip.getSoulOption() + ']'
                    : soulDesc);
        }
        if (equip.getSocket1() > 0 || equip.getSocket2() > 0 || equip.getSocket3() > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append("星岩[").append(equip.getSocket1());
            if (equip.getSocket2() > 0 || equip.getSocket3() > 0) {
                sb.append('/').append(equip.getSocket2());
            }
            if (equip.getSocket3() > 0) {
                sb.append('/').append(equip.getSocket3());
            }
            sb.append(']');
        }
        if (equip.getPotentialGrade() > 0 || equip.getPotential1() > 0
                || equip.getBonusPotentialGrade() > 0 || equip.getBonusPotential1() > 0
                || equip.getSoulId() > 0 || equip.getSoulOption() > 0
                || equip.getSocket1() > 0 || equip.getSocket3() > 0
                || equip.getEnhance() > 0) {
            StatBonus b = computeBonus(equip);
            sb.append(String.format(" (+STR%d DEX%d INT%d LUK%d WATK%d MATK%d)",
                    b.str, b.dex, b.inte, b.luk, b.watk, b.matk));
        }
        return sb.toString().trim();
    }

    private static String gradeName(int g) {
        return switch (g) {
            case 1 -> "普通";
            case 2 -> "稀有";
            case 3 -> "史诗";
            case 4 -> "独特";
            case 5 -> "传说";
            default -> "无";
        };
    }

    private static void applyMainLines(Equip equip, int grade, int[] rolled) {
        equip.setPotentialGrade((byte) Math.max(1, Math.min(PotentialHyperConfig.MAX_GRADE, grade)));
        equip.setPotential1(rolled[0]);
        equip.setPotential2(rolled[1]);
        equip.setPotential3(rolled[2]);
    }

    /**
     * 魔方 / 品阶卷结果：默认隐藏待鉴定（095）；{@link PotentialHyperConfig#CUBE_RESET_TO_HIDDEN}=false 时立即 roll。
     */
    private static void applyCubeResult(Equip equip, int grade, int lines) {
        if (PotentialHyperConfig.CUBE_RESET_TO_HIDDEN) {
            setMainHidden(equip, grade, lines);
        } else {
            int[] rolled = rollOptions(equip.getItemId(), grade, lines);
            applyMainLines(equip, grade, rolled);
        }
    }

    /** 是否有可进角色面板的平坦属性（排除仅 prop/time 的触发壳）。 */
    private static boolean hasCombatFlatStats(Map<String, Integer> stats) {
        if (stats == null || stats.isEmpty()) {
            return false;
        }
        for (String k : stats.keySet()) {
            if (k == null || "prop".equals(k) || "time".equals(k)) {
                continue;
            }
            if (k.startsWith("inc") || "ignoreTargetDEF".equals(k) || "ignoreDAM".equals(k)
                    || "ignoreDAMr".equals(k) || "DAMreflect".equals(k)
                    || "mpconReduce".equals(k) || "mpRestore".equals(k)
                    || "RecoveryHP".equals(k) || "RecoveryMP".equals(k) || "RecoveryUP".equals(k)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按 095 {@code potentialIDFits + optionTypeFits + reqLevel} 洗出 lines 条（1~3）。
     * 先按品阶切主档/降一档池，再抽线；绝不跨品阶乱抽。
     * 数值仍由装备 reqLevel→potLevel 在 {@link #computeBonus} 查表决定。
     */
    private static int[] rollOptions(int equipItemId, int grade, int lines) {
        lines = Math.max(1, Math.min(3, lines));
        int state = PotentialRules095.gradeTo095State(grade);
        int potLevel = PotentialRules095.equipOptionLevel(
                ItemInformationProvider.getInstance().getEquipLevelReq(equipItemId));

        ItemOptionProvider opt = ItemOptionProvider.getInstance();
        List<ItemOptionProvider.OptionMeta> candidates = new ArrayList<>();
        for (ItemOptionProvider.OptionMeta m : opt.listMeta()) {
            if (m.optionId <= 0 || m.optionId >= 60000) {
                continue; // 60xxx 留给附加潜能等，不进主潜能池
            }
            if (!PotentialRules095.inGradeBands(m.optionId, state)) {
                continue;
            }
            // 095: pot.reqLevel / 10 <= equipReqLevel/10
            if (m.reqLevel / 10 > potLevel) {
                continue;
            }
            if (!PotentialRules095.optionTypeFits(m.optionType, equipItemId)) {
                continue;
            }
            if (!hasCombatFlatStats(opt.getStats(m.optionId, 1))) {
                continue;
            }
            candidates.add(m);
        }
        if (candidates.isEmpty()) {
            // 兜底：放宽 optionType，仍锁在当前品阶 ID 段
            for (ItemOptionProvider.OptionMeta m : opt.listMeta()) {
                if (m.optionId > 0 && m.optionId < 60000
                        && PotentialRules095.inGradeBands(m.optionId, state)
                        && hasCombatFlatStats(opt.getStats(m.optionId, 1))) {
                    candidates.add(m);
                }
            }
        }
        if (candidates.isEmpty()) {
            // 绝对兜底：仍给当前品阶主档占位，避免跨阶
            int placeholder = switch (state) {
                case 8 -> 40001;
                case 7 -> 30001;
                case 6 -> 20001;
                default -> 10001;
            };
            return new int[]{placeholder, placeholder == 10001 ? 10002 : placeholder, 0};
        }

        List<ItemOptionProvider.OptionMeta> preferred = new ArrayList<>();
        List<ItemOptionProvider.OptionMeta> secondary = new ArrayList<>();
        for (ItemOptionProvider.OptionMeta m : candidates) {
            if (PotentialRules095.inPreferredBand(m.optionId, state)) {
                preferred.add(m);
            } else if (PotentialRules095.inSecondaryBand(m.optionId, state)) {
                secondary.add(m);
            }
        }

        int[] out = new int[3];
        for (int i = 0; i < lines; i++) {
            boolean usePreferred = (i == 0) || Randomizer.nextInt(10) == 0;
            List<ItemOptionProvider.OptionMeta> pool = usePreferred ? preferred : secondary;
            if (pool.isEmpty()) {
                pool = usePreferred ? secondary : preferred;
            }
            if (pool.isEmpty()) {
                pool = candidates;
            }
            out[i] = pickWeighted(pool);
        }
        return out;
    }

    /** 按 ItemOption info/weight 加权抽取（weight 钳制 1~8）。 */
    private static int pickWeighted(List<ItemOptionProvider.OptionMeta> pool) {
        if (pool == null || pool.isEmpty()) {
            return 10001;
        }
        List<ItemOptionProvider.OptionMeta> weighted = new ArrayList<>();
        for (ItemOptionProvider.OptionMeta m : pool) {
            int w = Math.max(1, Math.min(8, m.weight <= 0 ? 1 : m.weight));
            for (int n = 0; n < w; n++) {
                weighted.add(m);
            }
        }
        return weighted.get(Randomizer.nextInt(weighted.size())).optionId;
    }

    /**
     * 附加潜能：60xxx 按品质档分池（对齐主潜能「本品阶主档 + 降一档」），
     * 高品阶不会洗到更低多档的弱线。60xxx 无对称万段，品质由
     * {@link PotentialRules095#bonusQualityTier} 按战斗向数值判定。
     * 本品阶 60xxx 池空时回退主潜能同品阶池（仍锁品阶，不整表乱抽）。
     */
    private static int[] rollBonusOptions(int equipItemId, int grade, int lines) {
        lines = Math.max(1, Math.min(3, lines));
        int state = PotentialRules095.gradeTo095State(grade);
        int potLevel = PotentialRules095.equipOptionLevel(
                ItemInformationProvider.getInstance().getEquipLevelReq(equipItemId));
        ItemOptionProvider opt = ItemOptionProvider.getInstance();

        List<ItemOptionProvider.OptionMeta> preferred = new ArrayList<>();
        List<ItemOptionProvider.OptionMeta> secondary = new ArrayList<>();
        List<ItemOptionProvider.OptionMeta> candidates = new ArrayList<>();
        for (ItemOptionProvider.OptionMeta m : opt.listMeta()) {
            if (m.optionId < 60000) {
                continue;
            }
            if (m.reqLevel / 10 > potLevel) {
                continue;
            }
            if (!PotentialRules095.optionTypeFits(m.optionType, equipItemId)) {
                continue;
            }
            Map<String, Integer> stats = opt.getStats(m.optionId, 1);
            if (!hasCombatFlatStats(stats)) {
                continue;
            }
            int tier = PotentialRules095.bonusQualityTier(stats);
            if (!PotentialRules095.inBonusGradeBands(tier, state)) {
                continue;
            }
            candidates.add(m);
            if (PotentialRules095.inBonusPreferredBand(tier, state)) {
                preferred.add(m);
            } else if (PotentialRules095.inBonusSecondaryBand(tier, state)) {
                secondary.add(m);
            }
        }
        if (candidates.isEmpty()) {
            // 兜底：同品阶主潜能池（仍不跨品阶）
            return rollOptions(equipItemId, grade, lines);
        }

        int[] out = new int[3];
        for (int i = 0; i < lines; i++) {
            boolean usePreferred = (i == 0) || Randomizer.nextInt(10) == 0;
            List<ItemOptionProvider.OptionMeta> pool = usePreferred ? preferred : secondary;
            if (pool.isEmpty()) {
                pool = usePreferred ? secondary : preferred;
            }
            if (pool.isEmpty()) {
                pool = candidates;
            }
            out[i] = pickWeighted(pool);
        }
        return out;
    }
}

