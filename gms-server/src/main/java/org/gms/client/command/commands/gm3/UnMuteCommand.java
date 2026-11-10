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
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package org.gms.client.command.commands.gm3;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;

// D2：解除玩家禁言（对应 MuteCommand）。!unmute <玩家名>
public class UnMuteCommand extends Command {
    {
        setDescription("解除指定玩家禁言（!unmute <玩家名>）");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("用法： !unmute <玩家名>");
            return;
        }
        Character target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
        if (target != null) {
            target.setMuteUntil(0);
            target.yellowMessage("你的禁言已被解除。");
            player.yellowMessage("已解除 " + target.getName() + " 的禁言。");
        } else {
            player.yellowMessage("未找到在线玩家：" + params[0]);
        }
    }
}
