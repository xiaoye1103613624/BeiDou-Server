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
package org.gms.client.command.commands.gm5;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.config.GameConfig;
import org.gms.dao.entity.GameConfigDO;
import org.gms.util.I18nUtil;

/**
 * 【GM指令】ShowPacketsCommand（class），包 `org.gms.client.command.commands.gm5`。
 *
 * GM指令：切换是否在控制台显示接收到的数据包日志。
 *
 * @author Arthur L
 */
public class ShowPacketsCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("ShowPacketsCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        GameConfig.update(GameConfigDO.builder()
                .configType("server")
                .configSubType("Debug")
                .configCode("use_debug_show_rcvd_packet")
                .configValue(String.valueOf(!GameConfig.getServerBoolean("use_debug_show_rcvd_packet")))
                .build());
    }
}
