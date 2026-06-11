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
package org.gms.client.command.commands.gm1;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkillFactory;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * 自我Buff命令（GM等级1）
 * 为GM自身施加多个满级辅助技能（隐身术、神圣祈祷、铁壁等）
 * 并恢复满HP/MP，方便GM日常操作
 *
 * @author Arthur L
 */
public class BuffMeCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("BuffMeCommand.message1"));
    }

    /**
     * 为自己施加Buff：依次应用多个满级辅助技能并恢复HP/MP
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        // 隐身术（暗杀者）
        SkillFactory.getSkill(4101004).getEffect(SkillFactory.getSkill(4101004).getMaxLevel()).applyTo(player);
        // 神圣祈祷（祭司）
        SkillFactory.getSkill(2311003).getEffect(SkillFactory.getSkill(2311003).getMaxLevel()).applyTo(player);
        // 铁壁（枪战士）
        SkillFactory.getSkill(1301007).getEffect(SkillFactory.getSkill(1301007).getMaxLevel()).applyTo(player);
        // 祝福（牧师）
        SkillFactory.getSkill(2301004).getEffect(SkillFactory.getSkill(2301004).getMaxLevel()).applyTo(player);
        // 疾风步（弓箭手）
        SkillFactory.getSkill(1005).getEffect(SkillFactory.getSkill(1005).getMaxLevel()).applyTo(player);
        // 恢复满HP/MP
        player.healHpMp();
    }
}