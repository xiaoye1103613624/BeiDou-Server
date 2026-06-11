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
package org.gms.client.command.commands.gm6;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.server.TimerManager;
import org.gms.util.I18nUtil;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 服务器关服命令（GM等级6）
 * 定时关服，可指定分钟数或"now"立即关服
 * 关服前广播倒计时通知并保存所有在线玩家数据
 *
 * @author Arthur L
 */
public class ShutdownCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("ShutdownCommand.message1"));
    }

    /**
     * 执行关服：计算倒计时→广播通知→定时保存并关服
     *
     * @param c      客户端会话
     * @param params 命令参数（分钟数 或 "now"立即关服）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage(I18nUtil.getMessage("ShutdownCommand.message2"));
            return;
        }

        int time = 60000;
        if (params[0].equalsIgnoreCase("now")) {
            // 立即关服（1ms延迟）
            time = 1;
        } else {
            // 分钟转毫秒
            time *= Integer.parseInt(params[0]);
        }

        // 非立即关服时广播倒计时
        if (time > 1) {
            int seconds = (time / (int) SECONDS.toMillis(1)) % 60;
            int minutes = (time / (int) MINUTES.toMillis(1)) % 60;
            int hours = (time / (int) HOURS.toMillis(1)) % 24;
            int days = (time / (int) DAYS.toMillis(1));

            String strTime = "";
            if (days > 0) {
                strTime += I18nUtil.getMessage("ShutdownCommand.message3", days);
            }
            if (hours > 0) {
                strTime += I18nUtil.getMessage("ShutdownCommand.message4", hours);
            }
            strTime += I18nUtil.getMessage("ShutdownCommand.message5", minutes);
            strTime += I18nUtil.getMessage("ShutdownCommand.message6", seconds);

            // 广播倒计时到所有世界
            for (World w : Server.getInstance().getWorlds()) {
                for (Character chr : w.getPlayerStorage().getAllCharacters()) {
                    chr.dropMessage(I18nUtil.getMessage("ShutdownCommand.message7", strTime));
                }
            }
        }

        TimerManager.getInstance().schedule(Server.getInstance().shutdown(false), time);
    }
}