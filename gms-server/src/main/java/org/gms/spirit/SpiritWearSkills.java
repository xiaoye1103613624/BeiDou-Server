package org.gms.spirit;

import org.gms.client.Character;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 穿戴灵韵：本职可学会 → 直接 changeSkillLevel 写入技能窗；
 * 本职已学会 → 走套装同款 setSkillBonusLevels，客户端显示 (+N) 且战斗取生效等级。
 */
public final class SpiritWearSkills {
    private SpiritWearSkills() {}

    /** @return 需要并入 setSkillBonusLevels 的 {skillId → 加成等级} */
    public static Map<Integer, Integer> sync(Character chr) {
        Map<Integer, Integer> displayBonus = new HashMap<>();
        if (chr == null) {
            return displayBonus;
        }

        Map<Integer, Integer> want = new HashMap<>();
        collectRemapped(chr, want);
        Map<Integer, Byte> backup = chr.getSpiritSkillBackup();

        // 卸下：恢复备份、清掉加成
        Iterator<Map.Entry<Integer, Byte>> it = backup.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Byte> e = it.next();
            if (want.containsKey(e.getKey())) {
                continue;
            }
            Skill skill = SkillFactory.getSkill(e.getKey());
            if (skill != null) {
                byte restore = e.getValue() == null ? 0 : e.getValue();
                if (restore <= 0) {
                    // 曾由灵韵新建：彻底移除
                    if (chr.getSkillLevelRaw(skill) > 0) {
                        chr.changeSkillLevel(skill, (byte) -1, 0, -1);
                    }
                } else if (chr.getSkillLevelRaw(skill) != restore) {
                    chr.changeSkillLevel(skill, restore, skill.getMaxLevel(), -1);
                }
            }
            it.remove();
        }

        for (Map.Entry<Integer, Integer> e : want.entrySet()) {
            int skillId = e.getKey();
            int spiritLv = e.getValue();
            Skill skill = SkillFactory.getSkill(skillId);
            if (skill == null || spiritLv <= 0) {
                continue;
            }
            int maxLv = skill.getMaxLevel() > 0 ? skill.getMaxLevel() : SpiritAwakenConfig.maxLevel(skillId);
            boolean fresh = !backup.containsKey(skillId);
            byte owned;
            if (!fresh) {
                owned = backup.get(skillId);
            } else {
                owned = chr.getSkillLevelRaw(skill);
                backup.put(skillId, owned);
            }

            if (owned <= 0) {
                // 本职未学会：教会并设等级（进入技能窗）
                byte apply = (byte) Math.min(maxLv, spiritLv);
                if (chr.getSkillLevelRaw(skill) != apply) {
                    chr.changeSkillLevel(skill, apply, Math.max(maxLv, apply), -1);
                    notify(chr, skillId, apply, owned, spiritLv, false);
                }
            } else {
                // 已学会：基础等级不动，加成走 (+N) 展示 + getSkillLevel 叠加
                displayBonus.put(skillId, spiritLv);
                int effective = Math.min(maxLv, owned + spiritLv);
                if (fresh) {
                    notify(chr, skillId, effective, owned, spiritLv, true);
                }
            }
        }
        return displayBonus;
    }

    private static void notify(Character chr, int skillId, int effective, byte owned, int spiritLv, boolean bonusMode) {
        String name = SkillFactory.getSkillName(skillId);
        if (name == null || name.isEmpty()) {
            name = "技能" + skillId;
        }
        if (bonusMode) {
            chr.dropMessage(5, "灵韵生效：" + name + "  Lv." + owned + "(+" + spiritLv + ") → 生效 " + effective);
        } else {
            chr.dropMessage(5, "灵韵生效：" + name + "  Lv." + effective + "（已写入技能窗）");
        }
    }

    static void collectRemapped(Character chr, Map<Integer, Integer> into) {
        Item weapon = chr.getInventory(InventoryType.EQUIPPED).getItem((short) -11);
        if (!(weapon instanceof Equip equip)) {
            return;
        }
        int sid = equip.getEquipSkillId();
        int lv = equip.getEquipSkillLevel();
        if (sid <= 0 || lv <= 0 || SpiritAwakenConfig.isBanned(sid)) {
            return;
        }
        long expire = equip.getEquipSkillExpire();
        if (expire > 0 && expire < System.currentTimeMillis()) {
            return;
        }
        int mapped = SpiritSkillRemap.forJob(sid, chr.getJob().getId());
        into.merge(mapped, lv, Math::max);
    }
}
