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
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;

/**
 * GM公告命令（玩家等级0）
 * GM使用此命令向全服发送黄字公告提示和系统通知
 * 附带随机GM小贴士，命令内容长度至少3个字符
 *
 * @author Arthur L
 */
public class GmCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("GmCommand.message1"));
    }

    private static final Logger log = LoggerFactory.getLogger(GmCommand.class);

    /**
     * 发送GM公告：广播黄字提示和系统通知，记录日志，附带随机小贴士
     *
     * @param c      客户端会话
     * @param params 命令参数（公告内容）
     */
    @Override
    public void execute(Client c, String[] params) {
        String[] tips = {
                I18nUtil.getMessage("GmCommand.message2"),
                I18nUtil.getMessage("GmCommand.message3"),
                I18nUtil.getMessage("GmCommand.message4"),
                I18nUtil.getMessage("GmCommand.message5"),
                I18nUtil.getMessage("GmCommand.message6"),
        };
        Character player = c.getPlayer();
        // 内容长度至少3个字符，过滤无效请求
        if (params.length < 1 || params[0].length() < 3) {
            player.dropMessage(5, I18nUtil.getMessage("GmCommand.message7"));
            return;
        }
        String message = player.getLastCommandMessage();
        // 广播黄字提示和系统通知
        Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.sendYellowTip(I18nUtil.getMessage("GmCommand.message8") + Character.makeMapleReadable(player.getName()) + ": " + message));
        Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.serverNotice(1, message));
        log.info("{}: {}", Character.makeMapleReadable(player.getName()), message);
        player.dropMessage(5, I18nUtil.getMessage("GmCommand.message9", message));
        // 附带随机GM小贴士
        player.dropMessage(5, tips[Randomizer.nextInt(tips.length)]);
    }
}