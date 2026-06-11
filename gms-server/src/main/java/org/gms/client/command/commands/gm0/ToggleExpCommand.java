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
   @Author: Ronan
*/
package org.gms.client.command.commands.gm0;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * 经验开关命令（玩家等级0）
 * 切换角色是否获取经验值，用于卡级或保留低等级状态
 *
 * @author Ronan
 */
public class ToggleExpCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("ToggleExpCommand.message1"));
    }

    /**
     * 切换经验获取状态：获取客户端锁后翻转经验开关
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        if (c.tryacquireClient()) {
            try {
                // 翻转经验获取开关
                c.getPlayer().toggleExpGain();
            } finally {
                c.releaseClient();
            }
        }
    }
}