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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.constants.id.MapId;
import org.gms.server.events.gm.Event;
import org.gms.server.maps.FieldLimit;
import org.gms.util.I18nUtil;

/**
 * 参加活动命令（玩家等级0）
 * 传送到当前频道正在进行的GM活动地图
 * 检查地图限制和活动名额，保存原位置以备返回
 *
 * @author Arthur L
 */
public class JoinEventCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("JoinEventCommand.message1"));
    }

    /**
     * 参与活动：检查限制→保存位置→分配队伍（椰子/雪球活动）→传送
     *
     * @param c      客户端会话
     * @param params 命令参数（无）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        // 检查地图是否允许传送
        if (!FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
            Event event = c.getChannelServer().getEvent();
            if (event != null) {
                if (event.getMapId() != player.getMapId()) {
                    if (event.getLimit() > 0) {
                        player.saveLocation("EVENT");

                        // 椰子收获/雪球入口活动需要分配队伍
                        if (event.getMapId() == MapId.EVENT_COCONUT_HARVEST || event.getMapId() == MapId.EVENT_SNOWBALL_ENTRANCE) {
                            player.setTeam(event.getLimit() % 2);
                        }

                        event.minusLimit();

                        player.saveLocationOnWarp();
                        player.changeMap(event.getMapId());
                    } else {
                        player.dropMessage(5, I18nUtil.getMessage("JoinEventCommand.message2"));
                    }
                } else {
                    player.dropMessage(5, I18nUtil.getMessage("JoinEventCommand.message3"));
                }
            } else {
                player.dropMessage(5, I18nUtil.getMessage("JoinEventCommand.message4"));
            }
        } else {
            player.dropMessage(5, I18nUtil.getMessage("JoinEventCommand.message5"));
        }
    }
}