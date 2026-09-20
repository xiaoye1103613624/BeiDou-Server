package org.gms.potential;

import org.gms.client.inventory.Equip;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 潜能 / Hyper / 灵魂 / 星岩 结算与服务门面。
 * <p>
 * 设计约束（来自接线点契约）：
 * <ul>
 *   <li>{@link #computeBonus(Equip)} 与 {@link #computeBonus(Equip, int)} 被
 *       {@code Character#recalcEquipStats} / {@code computeClientDisplayBaseFourStats} 与
 *       {@code PacketCreator#userInfoExEquip} 逐件装备调用，必须<b>无 IO</b>。</li>
 *   <li>{@code computeBonus(equip, charLevel)} 的等级缩放：charLevel&gt;0 用角色等级、否则用装备需求等级，
 *       差值即「等级缩放部分」（见 Character 调用点的 full-base 计算）。</li>
 *   <li>平坦/百分比属性进 {@link StatBonus}；暴击/爆伤/伤害%/首领/无视进战斗 CombatStatModifier，
 *       由 {@link org.gms.combat.provider.PotentialStatProvider} 聚合，避免与面板百分比重复。</li>
 * </ul>
 */
public final class PotentialHyperService {
    private PotentialHyperService() {}

    /** 潜能/Hyper 结算结果（与 S9 调用点契约一致）。 */
    public static final class StatBonus {
        public int str, dex, inte, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, speed, jump;
        public int strR, dexR, intR, lukR, hpR, mpR, padR, madR;
        public int dropProp, mesoProp, damReflect, damReflectProp, allSkill, cooltimeReduce, mpconReduce;
    }

    public static StatBonus computeBonus(Equip equip) {
        return computeBonus(equip, 0);
    }

    public static StatBonus computeBonus(Equip equip, int charLevel) {
        StatBonus b = new StatBonus();
        if (equip == null) {
            return b;
        }
        int level = potentialLevel(equip, charLevel);

        addOptionStats(b, equip.getPotential1(), level);
        addOptionStats(b, equip.getPotential2(), level);
        addOptionStats(b, equip.getPotential3(), level);
        addOptionStats(b, equip.getBonusPotential1(), level);
        addOptionStats(b, equip.getBonusPotential2(), level);
        addOptionStats(b, equip.getBonusPotential3(), level);

        HyperEnhanceTable.HyperBonus hb = HyperEnhanceTable.bonusForStar(equip.getEnhance());
        b.str += hb.str();
        b.dex += hb.dex();
        b.inte += hb.inte();
        b.luk += hb.luk();
        b.hp += hb.hp();
        b.mp += hb.mp();
        b.watk += hb.watk();
        b.matk += hb.matk();
        b.wdef += hb.wdef();
        b.mdef += hb.mdef();
        b.acc += hb.acc();
        b.avoid += hb.avoid();

        // 星岩（socket 存词条 ID）
        addOptionStats(b, equip.getSocket1(), level);
        addOptionStats(b, equip.getSocket2(), level);
        addOptionStats(b, equip.getSocket3(), level);

        return b;
    }

    /** 装备潜能等级：角色等级（charLevel>0）或装备需求等级 → (lv+9)/10，钳制 1~20。 */
    private static int potentialLevel(Equip equip, int charLevel) {
        int base;
        if (charLevel > 0) {
            base = (charLevel + 9) / 10;
        } else {
            Integer req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
            base = ((req != null ? req : 0) + 9) / 10;
        }
        return Math.max(1, Math.min(20, base));
    }

    private static void addOptionStats(StatBonus b, int optionId, int level) {
        if (optionId <= 0) {
            return;
        }
        Map<String, Integer> stats = ItemOptionProvider.getInstance().getStats(optionId, level);
        if (stats.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> e : stats.entrySet()) {
            applyStat(b, e.getKey(), e.getValue());
        }
    }

    private static void applyStat(StatBonus b, String key, int v) {
        switch (key) {
            case "incSTR" -> b.str += v;
            case "incDEX" -> b.dex += v;
            case "incINT" -> b.inte += v;
            case "incLUK" -> b.luk += v;
            case "incMHP" -> b.hp += v;
            case "incMMP" -> b.mp += v;
            case "incPAD" -> b.watk += v;
            case "incMAD" -> b.matk += v;
            case "incPDD" -> b.wdef += v;
            case "incMDD" -> b.mdef += v;
            case "incACC" -> b.acc += v;
            case "incEVA" -> b.avoid += v;
            case "incSpeed" -> b.speed += v;
            case "incJump" -> b.jump += v;
            case "incSTRr" -> b.strR += v;
            case "incDEXr" -> b.dexR += v;
            case "incINTr" -> b.intR += v;
            case "incLUKr" -> b.lukR += v;
            case "incMHPr" -> b.hpR += v;
            case "incMMPr" -> b.mpR += v;
            case "incPADr" -> b.padR += v;
            case "incMADr" -> b.madR += v;
            case "incDropProp" -> b.dropProp += v;
            case "incMesoProp" -> b.mesoProp += v;
            case "incAllskill" -> b.allSkill += v;
            case "incCooltimeReduce" -> b.cooltimeReduce += v;
            case "incMpconReduce" -> b.mpconReduce += v;
            default -> { /* 战斗类（incCr/incCD/incDAMr/incBD/ignoreTargetDEF 等）由 collectCombat 处理 */ }
        }
    }

    /**
     * 收集装备的战斗类潜能属性（暴击/爆伤/伤害%/首领/无视），由 {@code PotentialStatProvider} 聚合。
     */
    public static List<CombatStatModifier> collectCombat(Equip equip, int charLevel) {
        List<CombatStatModifier> out = new ArrayList<>();
        if (equip == null) {
            return out;
        }
        int level = potentialLevel(equip, charLevel);
        collectCombatFromOption(out, equip.getPotential1(), level);
        collectCombatFromOption(out, equip.getPotential2(), level);
        collectCombatFromOption(out, equip.getPotential3(), level);
        collectCombatFromOption(out, equip.getBonusPotential1(), level);
        collectCombatFromOption(out, equip.getBonusPotential2(), level);
        collectCombatFromOption(out, equip.getBonusPotential3(), level);
        collectCombatFromOption(out, equip.getSocket1(), level);
        collectCombatFromOption(out, equip.getSocket2(), level);
        collectCombatFromOption(out, equip.getSocket3(), level);
        return out;
    }

    private static void collectCombatFromOption(List<CombatStatModifier> out, int optionId, int level) {
        if (optionId <= 0) {
            return;
        }
        Map<String, Integer> stats = ItemOptionProvider.getInstance().getStats(optionId, level);
        if (stats.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> e : stats.entrySet()) {
            CombatStatType type = combatTypeFor(e.getKey());
            if (type == null) {
                continue;
            }
            int v = e.getValue();
            if (v == 0) {
                continue;
            }
            out.add(new CombatStatModifier(type, v, CombatStatSource.EQUIP, "potential"));
        }
    }

    private static CombatStatType combatTypeFor(String key) {
        return switch (key) {
            case "incCr", "cr" -> CombatStatType.CRIT_RATE;
            case "incCD", "cd" -> CombatStatType.CRIT_DAM;
            case "incDAMr", "damR" -> CombatStatType.DAM_R;
            case "incBD", "boss" -> CombatStatType.BOSS_DAM_R;
            case "ignoreTargetDEF", "ignoreMobpdpR" -> CombatStatType.IGNORE_PDR;
            default -> null;
        };
    }

    /** 交易清空（受 {@link PotentialHyperConfig#CLEAR_ON_TRADE} 开关控制）。 */
    public static void clearOnTradeIfEnabled(Equip equip) {
        if (!PotentialHyperConfig.CLEAR_ON_TRADE || equip == null) {
            return;
        }
        equip.setPotential1(0);
        equip.setPotential2(0);
        equip.setPotential3(0);
        equip.setPotentialGrade((byte) 0);
        equip.setBonusPotential1(0);
        equip.setBonusPotential2(0);
        equip.setBonusPotential3(0);
        equip.setBonusPotentialGrade((byte) 0);
    }

    // ------------------------------------------------------------------
    // GM / 脚本：潜能随机赋予
    // ------------------------------------------------------------------

    /** 按品阶 roll 主潜能（返回长度 3，未激活行为 0）。 */
    public static int[] rollMainPotential(int reqLevel, int grade) {
        int lines = PotentialRules095.mainLineCount(grade);
        return rollLines(lines, grade);
    }

    /** 按品阶 roll 附加潜能（返回长度 3，未激活行为 0）。 */
    public static int[] rollBonusPotential(int reqLevel, int grade) {
        int lines = PotentialRules095.bonusLineCount(grade);
        return rollLines(lines, grade);
    }

    private static int[] rollLines(int lineCount, int grade) {
        int[] out = new int[]{0, 0, 0};
        Set<Integer> keys = ItemOptionProvider.loadedKeys();
        if (keys.isEmpty()) {
            return out;
        }
        List<Integer> pool = new ArrayList<>(keys);
        Collections.sort(pool);
        // 品阶越高，越偏向高 ID（095 高 ID 词条更强）
        int from = (pool.size() * (grade - PotentialRules095.MIN_GRADE))
                / (PotentialRules095.MAX_GRADE - PotentialRules095.MIN_GRADE + 1);
        from = Math.max(0, Math.min(pool.size() - 1, from));
        for (int i = 0; i < lineCount && from < pool.size(); i++) {
            int idx = from + Randomizer.nextInt(pool.size() - from);
            out[i] = pool.get(idx);
        }
        return out;
    }
}
