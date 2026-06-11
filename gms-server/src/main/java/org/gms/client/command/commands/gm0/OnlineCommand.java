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
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.util.I18nUtil;

/**
 * 在线玩家列表命令（玩家等级0）
 * 遍历当前世界所有频道，列出所有非GM玩家的ID、名称和所在地图
 *
 * @author Arthur L
 */
public class OnlineCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("OnlineCommand.message1"));
    }

    /**
     * 查询在线玩家：按频道分组列出非GM玩家的基本信息
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        // 遍历当前世界的所有频道
        for (Channel ch : Server.getInstance().getChannelsFromWorld(player.getWorld())) {
            player.yellowMessage(I18nUtil.getMessage("OnlineCommand.message2") + ch.getId() + ":");
            // 列出频道内所有非GM玩家
            for (Character chr : ch.getPlayerStorage().getAllCharacters()) {
                if (!chr.isGM()) {
                    player.message(" >> " + chr.getId() + "[" + Character.makeMapleReadable(chr.getName()) + "] " + I18nUtil.getMessage("OnlineCommand.message3") + " " + chr.getMap().getMapName());
                }
            }
        }
    }
}