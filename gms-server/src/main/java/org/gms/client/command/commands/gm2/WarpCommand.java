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
package org.gms.client.command.commands.gm2;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.maps.FieldLimit;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MiniDungeonInfo;
import org.gms.util.I18nUtil;
import org.gms.server.maps.Portal;

/**
 * 传送命令（GM等级2）
 * 传送到指定地图ID，自动保存当前位置以便返回
 * 普通玩家受地图限制和副本限制，GM不受限
 *
 * @author Arthur L
 */
public class WarpCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("WarpCommand.message1"));
    }

    /**
     * 传送到目标地图：保存原位置→查找传送门→执行地图切换
     *
     * @param c      客户端会话
     * @param params 命令参数（地图ID [传送门名]）
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage(I18nUtil.getMessage("WarpCommand.message2"));
            return;
        }

        try {
            MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
            if (target == null) {
                player.yellowMessage(I18nUtil.getMessage("WarpCommand.message3", params[0]));
                return;
            }

            if (!player.isAlive()) {
                player.dropMessage(1, I18nUtil.getMessage("WarpCommand.message4"));
                return;
            }

            // 非GM玩家检查地图限制和副本限制
            if (!player.isGM()) {
                if (player.getEventInstance() != null || MiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
                    player.dropMessage(1, I18nUtil.getMessage("WarpCommand.message5"));
                    return;
                }
            }

            player.saveLocationOnWarp();
            Portal portal = null;
            if (params.length >= 2) {
                // 按名称查找传送门
                portal = target.getPortal(params[1]);
                if (portal == null && params[1].matches("\\d+")) {
                    // 名称不存在时尝试按ID查找
                    portal = target.getPortal(Integer.parseInt(params[1]));
                }
            }
            if (portal == null) {
                // 兜底：随机出生点
                portal = target.getRandomPlayerSpawnpoint();
            }
            player.changeMap(target, portal);
        } catch (Exception ex) {
            player.yellowMessage(I18nUtil.getMessage("WarpCommand.message3", params[0]));
        }
    }
}