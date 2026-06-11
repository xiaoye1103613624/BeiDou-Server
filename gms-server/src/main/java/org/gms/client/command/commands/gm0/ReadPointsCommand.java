package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * 点数查询命令（玩家等级0）
 * 显示角色的积分（RewardPoints）和投票点数（VotePoints）
 * 支持通过参数分别查询rp或vp
 *
 * @author Arthur L
 */
public class ReadPointsCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("ReadPointsCommand.message1"));
    }

    /**
     * 查询点数：无参数显示全部，rp查积分，vp查投票点
     *
     * @param client 客户端会话
     * @param params 命令参数（rp/vp，可选）
     */
    @Override
    public void execute(Client client, String[] params) {

        Character player = client.getPlayer();
        // 参数过多提示
        if (params.length > 2) {
            player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message2"));
            return;
        } else if (params.length == 0) {
            // 无参数显示所有点数
            player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message3") + player.getRewardPoints() + " | "
                    + I18nUtil.getMessage("ReadPointsCommand.message4") + player.getClient().getVotePoints());
            return;
        }

        switch (params[0]) {
            case "rp":
                player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message3") + player.getRewardPoints());
                break;
            case "vp":
                player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message4") + player.getClient().getVotePoints());
                break;
            default:
                player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message3") + player.getRewardPoints() + " | "
                        + I18nUtil.getMessage("ReadPointsCommand.message4") + player.getClient().getVotePoints());
                break;
        }
    }
}