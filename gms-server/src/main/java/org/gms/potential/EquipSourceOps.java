package org.gms.potential;

import org.gms.client.inventory.Equip;
import org.gms.dao.entity.ReforgeAffixDO;
import org.gms.reforge.ReforgeService;
import org.gms.server.ItemInformationProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 按养成来源单独读/清/迁装备强化数据。禁止把 virtual 来源写进 body short。
 */
public final class EquipSourceOps {
    private EquipSourceOps() {}

    public enum Source {
        SCROLL_BODY,
        CHAOS,
        FLAME,
        HYPER,
        POTENTIAL,
        BONUS_POTENTIAL,
        SOUL,
        SOCKET,
        REFORGE,
        INFUSION,
        GEM,
        BREAKTHROUGH,
        PLATINUM_META
    }

    /** 面板分解（flat）；战斗仍走 {@link PotentialHyperService#computeBonus}。 */
    public static final class Breakdown {
        public final PotentialHyperService.StatBonus hyper = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus potential = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus bonusPotential = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus soul = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus socket = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus reforge = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus infusion = new PotentialHyperService.StatBonus();
        public final PotentialHyperService.StatBonus gem = new PotentialHyperService.StatBonus();
        /** 火花/涅槃（绿字，虚拟加算）。 */
        public final PotentialHyperService.StatBonus flame = new PotentialHyperService.StatBonus();
    }

    public static Breakdown getBreakdown(Equip equip) {
        return getBreakdown(equip, 0, Collections.emptyList());
    }

    public static Breakdown getBreakdown(Equip equip, int charLevel, List<ReforgeAffixDO> affixes) {
        Breakdown out = new Breakdown();
        if (equip == null) {
            return out;
        }
        int req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        int potLevel = PotentialRules095.equipOptionLevel(req);
        ItemOptionProvider opt = ItemOptionProvider.getInstance();

        applyOpts(out.potential, opt, potLevel, charLevel,
                equip.getPotential1(), equip.getPotential2(), equip.getPotential3());
        applyOpts(out.bonusPotential, opt, potLevel, charLevel,
                equip.getBonusPotential1(), equip.getBonusPotential2(), equip.getBonusPotential3());

        if (!org.gms.soul.SoulOrbConfig.isOrb(equip.getSoulId())) {
            applyOpts(out.soul, opt, potLevel, charLevel, equip.getSoulOption());
        }
        org.gms.soul.SoulWeaponService.applyOrbBonus(out.soul, equip);

        applyOpts(out.socket, opt, potLevel, charLevel,
                equip.getSocket1(), equip.getSocket2(), equip.getSocket3());

        int star = Math.max(0, Math.min(PotentialHyperConfig.MAX_ENHANCE, equip.getEnhance()));
        if (star > 0) {
            int all = HyperEnhanceTable.cumulativeAllStat(star);
            int atk = HyperEnhanceTable.cumulativeAtk(star, equip.getItemId());
            out.hyper.str += all;
            out.hyper.dex += all;
            out.hyper.inte += all;
            out.hyper.luk += all;
            out.hyper.watk += atk;
            out.hyper.matk += atk;
        }

        if (affixes != null && !affixes.isEmpty()) {
            Map<String, Integer> total = ReforgeService.computeTotalStats(equip, affixes);
            applyReforgeMap(out.reforge, total);
        }

        org.gms.infusion.InfusionService.applyCumulative(out.infusion, equip.getInfusion() & 0xFF);
        org.gms.gem.GemService.applyCumulative(out.gem, equip.getGemInlay() & 0xFF, equip.getGemTypes());

        org.gms.flame.EquipFlame fl = equip.getFlameStat();
        if (fl != null && (equip.getExGradeOption() != 0
                || fl.str != 0 || fl.dex != 0 || fl.inte != 0 || fl.luk != 0
                || fl.pad != 0 || fl.mad != 0 || fl.pdd != 0
                || fl.hp != 0 || fl.mp != 0 || fl.speed != 0 || fl.jump != 0
                || fl.allStatR != 0 || fl.bossDamageR != 0 || fl.damageR != 0)) {
            out.flame.str += fl.str;
            out.flame.dex += fl.dex;
            out.flame.inte += fl.inte;
            out.flame.luk += fl.luk;
            out.flame.watk += fl.pad;
            out.flame.matk += fl.mad;
            out.flame.wdef += fl.pdd;
            out.flame.mdef += fl.pdd;
            out.flame.hp += fl.hp;
            out.flame.mp += fl.mp;
            out.flame.speed += fl.speed;
            out.flame.jump += fl.jump;
            // allStatR / boss / damage：百分比进 combat 字段
            if (fl.allStatR != 0) {
                out.flame.strR += fl.allStatR;
                out.flame.dexR += fl.allStatR;
                out.flame.intR += fl.allStatR;
                out.flame.lukR += fl.allStatR;
            }
            out.flame.bossDamR += fl.bossDamageR;
            out.flame.damR += fl.damageR;
        }
        return out;
    }

    public static void clear(Equip equip, Source source) {
        if (equip == null || source == null) {
            return;
        }
        switch (source) {
            case HYPER -> equip.setEnhance((byte) 0);
            case POTENTIAL -> {
                equip.setPotential1(0);
                equip.setPotential2(0);
                equip.setPotential3(0);
                equip.setPotentialGrade((byte) 0);
            }
            case BONUS_POTENTIAL -> {
                equip.setBonusPotential1(0);
                equip.setBonusPotential2(0);
                equip.setBonusPotential3(0);
                equip.setBonusPotentialGrade((byte) 0);
            }
            case SOUL -> {
                equip.setSoulId(0);
                equip.setSoulOption(0);
            }
            case SOCKET -> {
                equip.setSocket1(0);
                equip.setSocket2(0);
                equip.setSocket3(0);
            }
            case REFORGE -> {
                equip.setReforge1(0);
                equip.setReforge2(0);
                equip.setReforge3(0);
                equip.setReforgeLock((byte) 0);
            }
            case INFUSION -> equip.setInfusion((byte) 0);
            case GEM -> {
                equip.setGemInlay((byte) 0);
                equip.setGemTypes(0);
            }
            case BREAKTHROUGH -> org.gms.breakthrough.BreakthroughService.detach(equip);
            case CHAOS -> equip.clearChaosLedger();
            case FLAME -> {
                equip.setExGradeOption(0L);
                equip.setFlameStat(new org.gms.flame.EquipFlame());
            }
            case SCROLL_BODY -> EquipResetService.resetScrollAndHyper(equip);
            case PLATINUM_META -> { /* meta only — 不清；还原卷保留 platinum */ }
            default -> { }
        }
    }

    public static void copy(Source source, Equip from, Equip to) {
        if (from == null || to == null || source == null) {
            return;
        }
        switch (source) {
            case HYPER -> to.setEnhance(from.getEnhance());
            case POTENTIAL -> {
                to.setPotential1(from.getPotential1());
                to.setPotential2(from.getPotential2());
                to.setPotential3(from.getPotential3());
                to.setPotentialGrade(from.getPotentialGrade());
            }
            case BONUS_POTENTIAL -> {
                to.setBonusPotential1(from.getBonusPotential1());
                to.setBonusPotential2(from.getBonusPotential2());
                to.setBonusPotential3(from.getBonusPotential3());
                to.setBonusPotentialGrade(from.getBonusPotentialGrade());
            }
            case SOUL -> {
                to.setSoulId(from.getSoulId());
                to.setSoulOption(from.getSoulOption());
            }
            case SOCKET -> {
                to.setSocket1(from.getSocket1());
                to.setSocket2(from.getSocket2());
                to.setSocket3(from.getSocket3());
            }
            case REFORGE -> {
                to.setReforge1(from.getReforge1());
                to.setReforge2(from.getReforge2());
                to.setReforge3(from.getReforge3());
                to.setReforgeLock(from.getReforgeLock());
            }
            case INFUSION -> to.setInfusion(from.getInfusion());
            case GEM -> {
                to.setGemInlay(from.getGemInlay());
                to.setGemTypes(from.getGemTypes());
            }
            case BREAKTHROUGH -> org.gms.breakthrough.BreakthroughService.attach(to, from);
            case CHAOS -> {
                to.setChaosStr(from.getChaosStr());
                to.setChaosDex(from.getChaosDex());
                to.setChaosInt(from.getChaosInt());
                to.setChaosLuk(from.getChaosLuk());
                to.setChaosHp(from.getChaosHp());
                to.setChaosMp(from.getChaosMp());
                to.setChaosWatk(from.getChaosWatk());
                to.setChaosMatk(from.getChaosMatk());
                to.setChaosWdef(from.getChaosWdef());
                to.setChaosMdef(from.getChaosMdef());
                to.setChaosAcc(from.getChaosAcc());
                to.setChaosAvoid(from.getChaosAvoid());
                to.setChaosSpeed(from.getChaosSpeed());
                to.setChaosJump(from.getChaosJump());
            }
            case FLAME -> {
                to.setExGradeOption(from.getExGradeOption());
                to.setFlameStat(from.getFlameStat() != null
                        ? from.getFlameStat().deepCopy()
                        : new org.gms.flame.EquipFlame());
            }
            case PLATINUM_META -> to.setPlatinum(from.getPlatinum());
            default -> { }
        }
    }

    private static void applyOpts(PotentialHyperService.StatBonus b, ItemOptionProvider opt,
                                  int potLevel, int charLevel, int... optionIds) {
        for (int id : optionIds) {
            if (id > 0) {
                PotentialHyperService.applyOptionPublic(b, opt.getStats(id, potLevel), charLevel);
            }
        }
    }

    private static void applyReforgeMap(PotentialHyperService.StatBonus b, Map<String, Integer> stats) {
        if (stats == null || stats.isEmpty()) {
            return;
        }
        int all = stats.getOrDefault("ALLSTAT", 0);
        b.str += stats.getOrDefault("STR", 0) + all;
        b.dex += stats.getOrDefault("DEX", 0) + all;
        b.inte += stats.getOrDefault("INT", 0) + all;
        b.luk += stats.getOrDefault("LUK", 0) + all;
        b.hp += stats.getOrDefault("HP", 0);
        b.mp += stats.getOrDefault("MP", 0);
        b.watk += stats.getOrDefault("PAD", 0);
        b.matk += stats.getOrDefault("MAD", 0);
        b.wdef += stats.getOrDefault("PDD", 0);
        b.mdef += stats.getOrDefault("MDD", 0);
    }
}
