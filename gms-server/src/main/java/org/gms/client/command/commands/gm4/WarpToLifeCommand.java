package org.gms.client.command.commands.gm4;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Portal;
import org.gms.util.I18nUtil;
import org.gms.util.StringUtil;

/**
 * 【GM指令】WarpToLifeCommand（class），包 `org.gms.client.command.commands.gm4`。
 *
 * GM指令：传送到指定生命体（NPC或怪物）所在的地图。
 *
 * @author Arthur L
 */
public class WarpToLifeCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("GotoNPCCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length == 0 || !StringUtil.isNumeric(params[0])) {
            player.yellowMessage(I18nUtil.getMessage("GotoNPCCommand.message2"));
            return;
        }
        int lifeId = Integer.parseInt(params[0]);
        player.message(I18nUtil.getMessage("GotoNPCCommand.message3"));
        MapleMap targetMap = c.getChannelServer().getMapFactory().getMapByLifeId(lifeId);
        if (targetMap == null) {
            player.message(I18nUtil.getMessage("GotoNPCCommand.message4"));
            return;
        }
        Portal targetPortal = targetMap.getRandomPlayerSpawnpoint();
        player.saveLocationOnWarp();
        player.changeMap(targetMap, targetPortal);
    }
}
