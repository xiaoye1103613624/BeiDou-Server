package org.gms.client.command.commands.gm0;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.soul.SoulWeaponService;

/** 释放当前武器灵魂技能（全员可用）。 */
public class SoulSkillCommand extends Command {
    {
        setDescription("释放武器灵魂宝珠技能");
    }

    @Override
    public void execute(Client c, String[] params) {
        SoulWeaponService.useSoulSkill(c.getPlayer());
    }
}
