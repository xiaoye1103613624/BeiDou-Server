package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;

public class PartyTrackerCommand extends Command {
    {
        setDescription("Toggle party EXP/Meso tracker. Usage: @partytracker [on|off]");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        boolean enabled;
        if (params.length == 0) {
            enabled = !player.isPartyTrackerVisible();
        } else if (params.length == 1 &&
                (params[0].equalsIgnoreCase("on") || params[0].equalsIgnoreCase("off"))) {
            enabled = params[0].equalsIgnoreCase("on");
        } else {
            player.yellowMessage("Usage: @partytracker [on|off]");
            return;
        }

        player.setPartyTrackerVisible(enabled);
        player.yellowMessage("Party tracker " + (enabled ? "enabled." : "disabled."));
    }
}
