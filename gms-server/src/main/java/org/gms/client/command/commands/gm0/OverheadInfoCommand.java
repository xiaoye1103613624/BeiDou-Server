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
   @Author: 北斗 - 头顶信息展示命令
*/
package org.gms.client.command.commands.gm0;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * 头顶信息展示开关命令（玩家等级0）
 * 开启后在角色头顶周期性显示经验倍率、掉落倍率、BOSS爆率、金币倍率、网络延迟等实时信息
 * 使用CHATTEXT气泡包发送给玩家自身，类似角色说话时的头顶气泡效果
 *
 * @author 北斗
 */
public class OverheadInfoCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("OverheadInfoCommand.message1"));
    }

    /**
     * 切换头顶信息展示的开启/关闭状态
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        if (c.tryacquireClient()) {
            try {
                c.getPlayer().toggleOverheadInfo();
            } finally {
                c.releaseClient();
            }
        }
    }
}
