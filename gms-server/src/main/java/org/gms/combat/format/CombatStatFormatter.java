package org.gms.combat.format;

import org.gms.client.SkillFactory;
import org.gms.combat.stat.CombatStatType;
import org.gms.server.setitem.SetBonus;
import org.gms.server.setitem.SetBonusColor;

public final class CombatStatFormatter {
    private CombatStatFormatter() {}

    public static void appendSetBonusLines(StringBuilder sb, SetBonus bonus) {
        appendSetBonusLines(sb, bonus, SetBonusColor.SET_BONUS);
    }

    public static void appendSetBonusLines(StringBuilder sb, SetBonus bonus, SetBonusColor color) {
        if (bonus == null || bonus.isEmpty()) {
            return;
        }
        String prefix = color != null ? color.getCode() : SetBonusColor.SET_BONUS.getCode();
        appendInt(sb, prefix, "力量", bonus.str);
        appendInt(sb, prefix, "敏捷", bonus.dex);
        appendInt(sb, prefix, "智力", bonus.int_);
        appendInt(sb, prefix, "运气", bonus.luk);
        appendInt(sb, prefix, "物理攻击", bonus.pad);
        appendInt(sb, prefix, "魔法攻击", bonus.mad);
        appendInt(sb, prefix, "物理防御", bonus.pdd);
        appendInt(sb, prefix, "魔法防御", bonus.mdd);
        appendInt(sb, prefix, "命中", bonus.acc);
        appendInt(sb, prefix, "回避", bonus.eva);
        appendInt(sb, prefix, "HP", bonus.mhp);
        appendInt(sb, prefix, "MP", bonus.mmp);
        appendInt(sb, prefix, "移速", bonus.speed);
        appendInt(sb, prefix, "跳跃", bonus.jump);
        appendPercent(sb, prefix, "力量", bonus.strR);
        appendPercent(sb, prefix, "敏捷", bonus.dexR);
        appendPercent(sb, prefix, "智力", bonus.intR);
        appendPercent(sb, prefix, "运气", bonus.lukR);
        appendPercent(sb, prefix, "HP", bonus.mhpR);
        appendPercent(sb, prefix, "MP", bonus.mmpR);

        for (CombatStatType type : CombatStatType.values()) {
            if (type == CombatStatType.FINAL_DAM_R) {
                continue;
            }
            int v = bonus.getCombatStat(type);
            if (v != 0) {
                sb.append(prefix).append(type.getLabel()).append(" +").append(v).append("%#k\r\n");
            }
        }
        for (int fd : bonus.collectFinalDamageSources()) {
            if (fd != 0) {
                sb.append(prefix).append("最终伤害 +").append(fd).append("%#k\r\n");
            }
        }

        for (var e : bonus.skillLevels.entrySet()) {
            String name = SkillFactory.getSkillName(e.getKey());
            if (name == null || name.isBlank()) {
                name = "技能[" + e.getKey() + "]";
            }
            sb.append(prefix).append(name).append(" 等级 +").append(e.getValue()).append("#k\r\n");
        }
    }

    private static void appendInt(StringBuilder sb, String prefix, String label, int v) {
        if (v != 0) {
            sb.append(prefix).append(label).append(" +").append(v).append("#k\r\n");
        }
    }

    private static void appendPercent(StringBuilder sb, String prefix, String label, int v) {
        if (v != 0) {
            sb.append(prefix).append(label).append(" +").append(v).append("%#k\r\n");
        }
    }
}
