package org.gms.spirit;

import org.gms.client.inventory.Equip;

/** S9 stub: spirit awaken service not fully ported. */
public final class SpiritAwakenService {
    private SpiritAwakenService() {}

    public static void clearSpirit(Equip equip) {
        if (equip == null) {
            return;
        }
        equip.setEquipSkillId(0);
        equip.setEquipSkillLevel(0);
        equip.setEquipSkillExpire(0L);
    }
}
