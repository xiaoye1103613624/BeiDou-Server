package org.gms.client.command.commands.gm3;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * 【GM指令】GiveRpCommand（class），包 `org.gms.client.command.commands.gm3`。
 *
 * GM指令：给予自身或指定玩家Reward Points（奖励积分）。
 *
 * @author 昨日小睡
 */
public class GiveRpCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("GiveRpCommand.message1"));
    }

    @Override
    public void execute(Client client, String[] params) {
        Character player = client.getPlayer();
        if (params.length == 0) {
            player.yellowMessage(I18nUtil.getMessage("GiveRpCommand.message2"));
            return;
        }

        if (params.length == 1) {
            player.setRewardPoints(player.getRewardPoints() + Integer.parseInt(params[0]));
            player.message(I18nUtil.getMessage("GiveRpCommand.message3"));
        } else {
            Character victim = client.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
            if (victim == null) {
                player.message(I18nUtil.getMessage("BombCommand.message3", params[0]));
            } else {
                victim.setRewardPoints(player.getRewardPoints() + Integer.parseInt(params[1]));
                player.message(I18nUtil.getMessage("GiveRpCommand.message3"));
            }
        }
    }
}
