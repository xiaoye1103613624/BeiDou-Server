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

import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.net.server.Server;
import org.gms.server.TimerManager;
import org.gms.util.DatabaseConnection;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
public class BanCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("BanCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage(I18nUtil.getMessage("BanCommand.message2"));
            return;
        }
        // 玩家名字,参数1,可以填 名称 或者玩家id
        String nameOrId = params[0];
        // 封禁原因
        String reason = joinStringFrom(params, 1);
        // 玩家
        Character target = c.getChannelServer()
                .getPlayerStorage()
                .getCharacterByName(nameOrId);
        if (target != null) {
            // 玩家在线处理

            String readableTargetName = Character.makeMapleReadable(target.getName());
            String ip = target.getClient().getRemoteAddress();
            //Ban ip
            try (Connection con = DatabaseConnection.getConnection()) {
                if (ip.matches("[0-9]{1,3}\\..*")) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?, ?)")) {
                        ps.setString(1, ip);
                        ps.setString(2, String.valueOf(target.getClient().getAccID()));
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                log.error("IP封禁异常,IP: [{}]", ip, ex);
                c.getPlayer().message(I18nUtil.getMessage("BanCommand.message3"));
                c.getPlayer().message(I18nUtil.getMessage("BanCommand.message4", target.getName(), ip));
            }
            // Ban Macs
            target.getClient().banMacs();
            reason = I18nUtil.getMessage("BanCommand.message5", c.getPlayer().getName(), readableTargetName, reason, ip,
                    c.getMacs());
            target.ban(reason);
            target.yellowMessage(I18nUtil.getMessage("BanCommand.message6", c.getPlayer().getName()));
            target.yellowMessage(I18nUtil.getMessage("BanCommand.message7", reason));
            c.sendPacket(PacketCreator.getGMEffect(4, (byte) 0));
            final Character rip = target;
            TimerManager.getInstance()
                    // 延迟5秒踢出玩家
                    .schedule(() -> rip.getClient()
                                    .disconnect(false, false)
                            , 5000);
            Server.getInstance()
                    .broadcastMessage(c.getWorld(),
                            PacketCreator.serverNotice(6, I18nUtil.getMessage("BanCommand.message8", nameOrId))
                    );
        } else if (Character.ban(nameOrId, reason, false)) {
            c.sendPacket(PacketCreator.getGMEffect(4, (byte) 0));
            Server.getInstance()
                    .broadcastMessage(c.getWorld(),
                            PacketCreator.serverNotice(6, I18nUtil.getMessage("BanCommand.message8", nameOrId))
                    );
        } else {
            c.sendPacket(PacketCreator.getGMEffect(6, (byte) 1));
        }
    }
}
