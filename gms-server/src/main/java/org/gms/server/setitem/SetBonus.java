package org.gms.server.setitem;

import org.gms.combat.stat.CombatStatType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 套装单档位或汇总加成数据。
 */
public class SetBonus {
    public int requiredCount;
    /** 档位是否启用；false 时不参与累加与 Tooltip */
    public boolean tierEnabled = true;

    // --- 固定数值 ---
    public int str;
    public int dex;
    public int int_;
    public int luk;
    public int pad;
    public int mad;
    public int pdd;
    public int mdd;
    public int acc;
    public int eva;
    public int mhp;
    public int mmp;
    public int speed;
    public int jump;

    // --- 基础百分比（按角色裸属性，不含装备与其他加成）---
    public int strR;
    public int dexR;
    public int intR;
    public int lukR;
    public int mhpR;
    public int mmpR;

    /** @deprecated 使用 {@link #combatStats} 中的 fdR；merge 时仍收集到 finalDamageSources */
    public int finalDamagePercent;
    public int damageSkinId;

    /** 战斗属性：damR, bdR, fdR, ignoreMobpdpR 等 */
    public final Map<String, Integer> combatStats = new HashMap<>();

    /** 技能展示加成（0x179），不写进基础等级 */
    public final Map<Integer, Integer> skillLevels = new HashMap<>();
    /** WZ activeSkill：实际授予的技能等级 */
    public final Map<Integer, Integer> activeSkills = new HashMap<>();
    /** 技能段数等扩展 */
    public final List<SetSkillMod> skillMods = new ArrayList<>();

    public SetBonus() {
        this.requiredCount = 0;
    }

    public SetBonus(int requiredCount) {
        this.requiredCount = requiredCount;
    }

    public void putCombatStat(String key, int value) {
        if (key == null || value == 0) {
            return;
        }
        combatStats.merge(key, value, Integer::sum);
    }

    public int getCombatStat(CombatStatType type) {
        if (type == null) {
            return 0;
        }
        return combatStats.getOrDefault(type.getKey(), 0);
    }

    /** 累加后的最终伤害%来源（乘法，非字段相加） */
    public final List<Integer> finalDamageSources = new ArrayList<>();

    public List<Integer> collectFinalDamageSources() {
        if (!finalDamageSources.isEmpty()) {
            return new ArrayList<>(finalDamageSources);
        }
        List<Integer> out = new ArrayList<>();
        int fd = getCombatStat(CombatStatType.FINAL_DAM_R);
        if (fd != 0) {
            out.add(fd);
        }
        if (finalDamagePercent != 0 && finalDamagePercent != fd) {
            out.add(finalDamagePercent);
        }
        return out;
    }

    public void merge(SetBonus other) {
        if (other == null) {
            return;
        }
        str += other.str;
        dex += other.dex;
        int_ += other.int_;
        luk += other.luk;
        pad += other.pad;
        mad += other.mad;
        pdd += other.pdd;
        mdd += other.mdd;
        acc += other.acc;
        eva += other.eva;
        mhp += other.mhp;
        mmp += other.mmp;
        speed += other.speed;
        jump += other.jump;
        strR += other.strR;
        dexR += other.dexR;
        intR += other.intR;
        lukR += other.lukR;
        mhpR += other.mhpR;
        mmpR += other.mmpR;
        // finalDamagePercent 不累加，由 collectFinalDamageSources 处理
        if (other.damageSkinId > 0) {
            damageSkinId = other.damageSkinId;
        }
        for (Map.Entry<String, Integer> e : other.combatStats.entrySet()) {
            if (CombatStatType.FINAL_DAM_R.getKey().equals(e.getKey())) {
                continue;
            }
            combatStats.merge(e.getKey(), e.getValue(), Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : other.skillLevels.entrySet()) {
            skillLevels.merge(e.getKey(), e.getValue(), Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : other.activeSkills.entrySet()) {
            activeSkills.merge(e.getKey(), e.getValue(), Math::max);
        }
        skillMods.addAll(other.skillMods);
        finalDamageSources.addAll(other.collectFinalDamageSources());
    }

    public boolean isEmpty() {
        return str == 0 && dex == 0 && int_ == 0 && luk == 0 && pad == 0 && mad == 0
                && pdd == 0 && mdd == 0 && acc == 0 && eva == 0 && mhp == 0 && mmp == 0
                && speed == 0 && jump == 0 && strR == 0 && dexR == 0 && intR == 0 && lukR == 0
                && mhpR == 0 && mmpR == 0 && finalDamagePercent == 0 && damageSkinId == 0
                && combatStats.isEmpty() && skillLevels.isEmpty() && activeSkills.isEmpty() && skillMods.isEmpty();
    }
}
