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
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.scripting.quest.QuestScriptManager;
import org.gms.util.PacketCreator;
import org.gms.util.I18nUtil;

/**
 * 解卡命令（玩家等级0）
 * 强制关闭所有NPC对话和任务脚本，恢复玩家操作权限
 * 用于解决玩家卡在对话/脚本界面无法操作的问题
 *
 * @author Arthur L
 */
public class DisposeCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("DisposeCommand.message1"));
    }

    /**
     * 执行解卡：依次关闭NPC脚本、任务脚本，发送恢复操作包，清除点击状态
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        // 关闭NPC对话脚本
        NPCScriptManager.getInstance().dispose(c);
        // 关闭任务脚本
        QuestScriptManager.getInstance().dispose(c);
        // 恢复客户端操作权限
        c.sendPacket(PacketCreator.enableActions());
        c.removeClickedNPC();
        c.getPlayer().message(I18nUtil.getMessage("DisposeCommand.message2"));
    }
}