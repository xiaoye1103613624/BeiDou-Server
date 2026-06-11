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
import org.gms.constants.id.NpcId;
import org.gms.dao.entity.GachaponRewardDO;
import org.gms.manager.ServerManager;
import org.gms.server.gachapon.Gachapon;
import org.gms.service.GachaponService;
import org.gms.util.I18nUtil;

import java.util.List;

/**
 * 扭蛋查询命令（玩家等级0）
 * 根据输入的城市名称查询对应扭蛋机的奖励列表
 * 通过NPC对话界面展示所有可抽取的物品
 *
 * @author Arthur L
 */
public class GachaCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("GachaCommand.message1"));
    }

    /**
     * 查询扭蛋奖励：匹配城市名→查找扭蛋机类型→DB查询奖励→NPC界面展示
     *
     * @param c      客户端会话
     * @param params 命令参数（城市名称）
     */
    @Override
    public void execute(Client c, String[] params) {
        Gachapon.GachaponType gacha = null;
        String search = c.getPlayer().getLastCommandMessage();
        String gachaName = "";
        String[] names = Gachapon.GachaponType.getLootNames();
        int[] ids = Gachapon.GachaponType.getLootIds();
        // 遍历所有扭蛋城市名进行匹配
        for (int i = 0; i < names.length; i++) {
            if (search.equalsIgnoreCase(names[i])) {
                gachaName = names[i];
                gacha = Gachapon.GachaponType.getByNpcId(ids[i]);
            }
        }
        // 未匹配到城市名，列出所有可选城市
        if (gacha == null) {
            c.getPlayer().yellowMessage(I18nUtil.getMessage("GachaCommand.message12"));
            for (String name : names) {
                c.getPlayer().yellowMessage(name);
            }
            return;
        }
        // 构建扭蛋奖励列表展示
        StringBuilder talkStr = new StringBuilder("#b" + gachaName + "#k");
        talkStr.append(I18nUtil.getMessage("GachaCommand.message13"));
        talkStr.append("\r\n\r\n");
        GachaponService gachaponService = ServerManager.getApplicationContext().getBean(GachaponService.class);
        List<GachaponRewardDO> gachaponRewardDOS = gachaponService.getRewardsByNpcId(gacha.getNpcId());
        // 列出所有奖励物品图标和名称
        for (GachaponRewardDO gachaponRewardDO : gachaponRewardDOS) {
            talkStr.append("#v").append(gachaponRewardDO.getItemId()).append("#   -  #z").append(gachaponRewardDO.getItemId()).append("#\r\n");
        }
        talkStr.append("\r\n");
        talkStr.append(I18nUtil.getMessage("GachaCommand.message14"));

        c.getAbstractPlayerInteraction().npcTalk(NpcId.MAPLE_ADMINISTRATOR, talkStr.toString());
    }
}