/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
   @Author: Arthur L - Refactored command content into modules
*/
package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * 离开活动命令（玩家等级0）
 * 退出当前参与的Ola/Fitness等活动，返回活动前保存的地图位置
 * 同时重置活动计时器并释放活动参与名额
 *
 * @author Arthur L
 */
public class LeaveEventCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("LeaveEventCommand.message1"));
    }

    /**
     * 离开活动：重置活动状态→返回原地图→释放活动名额
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        int returnMap = player.getSavedLocation("EVENT");
        if (returnMap != -1) {
            // 重置Ola活动状态
            if (player.getOla() != null) {
                player.getOla().resetTimes();
                player.setOla(null);
            }
            // 重置Fitness活动状态
            if (player.getFitness() != null) {
                player.getFitness().resetTimes();
                player.setFitness(null);
            }

            player.saveLocationOnWarp();
            player.changeMap(returnMap);
            // 释放活动参与名额
            if (c.getChannelServer().getEvent() != null) {
                c.getChannelServer().getEvent().addLimit();
            }
        } else {
            player.dropMessage(5, I18nUtil.getMessage("LeaveEventCommand.message2"));
        }

    }
}