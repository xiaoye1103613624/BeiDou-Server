package org.gms.client.command.commands.gm3;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionDeathCount;
import org.gms.util.PacketCreator;

/**
 * 查看 / 设置当前远征死亡次数。!deathcount [n]  （-1 关闭）
 */
public class DeathCountCommand extends Command {
    {
        setDescription("Inspect/set expedition death count. !deathcount [n]");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        EventInstanceManager eim = player.getEventInstance();
        Expedition exped = eim != null ? eim.getExpedition() : null;
        if (exped == null) {
            player.yellowMessage("[DeathCount] 当前不在远征副本中。");
            return;
        }

        if (params.length > 0) {
            int value;
            try {
                value = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                player.yellowMessage("语法: !deathcount [n]   (-1 关闭计数)");
                return;
            }
            exped.setDeathCount(value);
            eim.broadcastPacket(PacketCreator.expeditionDeathCount(exped.getDeathCount()));
        }

        player.yellowMessage("[DeathCount] " + exped.getType() + " count=" + exped.getDeathCount()
                + " (-1=关) configured=" + ExpeditionDeathCount.configuredFor(exped.getType()));
    }
}
