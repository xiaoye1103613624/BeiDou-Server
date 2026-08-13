package org.gms.client.command.commands.gm0;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.beauty.BeautyPackets;

public class BeautyCommand extends Command {
    {
        setDescription("打开自定义美容院界面。");
    }

    @Override
    public void execute(Client c, String[] params) {
        if (c.getPlayer() == null) {
            return;
        }
        // OPEN then DATA — same path as NPC 9105006 / 便民工具.
        BeautyPackets.openSalon(c);
    }
}
