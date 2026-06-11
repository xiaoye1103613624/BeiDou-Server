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
import org.gms.manager.ServerManager;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.service.HpMpAlertService;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;

/**
 * 全服保存命令（GM等级6）
 * 保存所有世界中所有在线玩家的角色数据到数据库
 * 同时保存HP/MP预警数据，防止数据丢失
 *
 * @author Arthur L
 */
public class SaveAllCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("SaveAllCommand.message1"));
    }

    /**
     * 执行全服保存：遍历所有世界→逐个保存角色→保存预警数据→广播通知
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        // 遍历所有世界保存在线角色数据
        for (World world : Server.getInstance().getWorlds()) {
            for (Character chr : world.getPlayerStorage().getAllCharacters()) {
                chr.saveCharToDB();
            }
        }
        // 广播保存通知
        Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.serverNotice(5, I18nUtil.getMessage("SaveAllCommand.message2", player.getName())));
        player.message(I18nUtil.getMessage("SaveAllCommand.message3"));
        // 保存HP/MP预警服务数据
        HpMpAlertService hpMpAlertService = ServerManager.getApplicationContext().getBean(HpMpAlertService.class);
        hpMpAlertService.saveAll();
    }
}