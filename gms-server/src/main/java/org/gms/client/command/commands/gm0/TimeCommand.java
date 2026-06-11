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

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间查询命令（玩家等级0）
 * 显示当前服务器所在时区的系统时间（HH:mm:ss格式）
 *
 * @author Arthur L
 */
public class TimeCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("TimeCommand.message1"));
    }

    /**
     * 获取并显示系统当前时间
     *
     * @param client 客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client client, String[] params) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        client.getPlayer().yellowMessage(I18nUtil.getMessage("TimeCommand.message2") + dateFormat.format(new Date()));
    }
}