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

// D2：玩家禁言（原生 CHATLOG_ADD type:8 系统通道对应的禁言状态）。
//   !mute <玩家名> [分钟]  默认 10 分钟；解除由 UnMuteCommand 处理。
public class MuteCommand extends Command {
    {
        setDescription("禁言指定玩家（!mute <玩家名> [分钟]）");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("用法： !mute <玩家名> [分钟]");
            return;
        }
        String ign = params[0];
        int minutes = 10;
        if (params.length >= 2) {
            try {
                minutes = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                minutes = 10;
            }
        }
        if (minutes <= 0) {
            player.yellowMessage("分钟数必须为正整数。");
            return;
        }
        Character target = c.getChannelServer().getPlayerStorage().getCharacterByName(ign);
        if (target != null) {
            long until = System.currentTimeMillis() + (long) minutes * 60 * 1000;
            target.setMuteUntil(until);
            target.yellowMessage("你已被管理员禁言 " + minutes + " 分钟。");
            player.yellowMessage("已禁言 " + target.getName() + " " + minutes + " 分钟。");
        } else {
            player.yellowMessage("未找到在线玩家：" + ign);
        }
    }
}
