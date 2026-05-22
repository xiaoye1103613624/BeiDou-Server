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

/**
 * 【GM0指令】EquipLvCommand（class），包 `org.gms.client.command.commands.gm0`。
 *
 * GM指令：查看当前身上所有装备的等级与强化属性。
 *
 * @author Arthur L
 */
public class EquipLvCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("EquipLvCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        c.getPlayer().showAllEquipFeatures();
    }
}
