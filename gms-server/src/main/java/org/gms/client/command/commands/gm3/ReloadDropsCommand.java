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
package org.gms.client.command.commands.gm3;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.util.I18nUtil;

/**
 * 重载掉落表命令（GM等级3）
 * 清空服务器内存中的怪物掉落缓存，强制下次查询时从XML重新加载
 * 用于修改掉落配置后无需重启服务器
 *
 * @author Arthur L
 */
public class ReloadDropsCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("ReloadDropsCommand.message1"));
    }

    /**
     * 重载掉落数据：清除内存缓存→后续查询自动重新加载XML
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        MonsterInformationProvider.getInstance().clearDrops();
        player.dropMessage(5, I18nUtil.getMessage("ReloadDropsCommand.message2"));
    }
}