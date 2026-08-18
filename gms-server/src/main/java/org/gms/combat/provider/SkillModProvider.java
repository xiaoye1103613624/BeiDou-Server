package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.server.setitem.SetItemManager;

/**
 * 套装 skillMods（如 addAttackCount）查询入口。
 */
public final class SkillModProvider {
    private SkillModProvider() {}

    public static int addAttackCount(Character chr, int skillId) {
        return SetItemManager.getAddAttackCount(chr, skillId);
    }
}
