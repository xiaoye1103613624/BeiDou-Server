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
   @Author: xiaoye - paid map teleport command for normal players
*/
package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.server.maps.FieldLimit;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MiniDungeonInfo;
import org.gms.server.maps.Portal;
import org.gms.util.I18nUtil;

import java.util.function.IntPredicate;

public class MapWarpCommand extends Command {
    private static final int COST = 100000;

    {
        setDescription(I18nUtil.getMessage("MapWarpCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage(I18nUtil.getMessage("MapWarpCommand.message2"));
            return;
        }

        int mapId;
        try {
            mapId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            player.yellowMessage(I18nUtil.getMessage("MapWarpCommand.message3"));
            return;
        }

        MapleMap target = c.getChannelServer().getMapFactory().getMap(mapId);
        if (target == null) {
            player.yellowMessage(I18nUtil.getMessage("MapWarpCommand.message4", params[0]));
            return;
        }

        if (!player.isAlive()) {
            player.dropMessage(1, I18nUtil.getMessage("MapWarpCommand.message5"));
            return;
        }

        if (player.getEventInstance() != null || MiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
            player.dropMessage(1, I18nUtil.getMessage("MapWarpCommand.message6"));
            return;
        }

        if (player.getMeso() < COST) {
            player.dropMessage(1, I18nUtil.getMessage("MapWarpCommand.message7", COST));
            return;
        }

        Portal portal = null;
        if (params.length >= 2) {
            portal = target.getPortal(params[1]);
            if (portal == null && params[1].matches("\\d+")) {
                portal = target.getPortal(Integer.parseInt(params[1]));
            }
        }
        if (portal == null) {
            portal = target.getRandomPlayerSpawnpoint();
        }

        IntPredicate commit = balanceAfter -> true;
        boolean ok = player.spendMesoTransactionally(COST, commit);
        if (!ok) {
            player.dropMessage(1, I18nUtil.getMessage("MapWarpCommand.message8"));
            return;
        }

        player.saveLocationOnWarp();
        player.changeMap(target, portal);
        player.dropMessage(1, I18nUtil.getMessage("MapWarpCommand.message9", mapId));
    }
}
