/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/*
 *
 * @author BubblesDev
 */
/**
 * 频道服务器入站封包处理器「ReportHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class ReportHandler extends AbstractPacketHandler {
    public final void handlePacket(InPacket p, Client c) {
        int type = p.readByte(); //00 = Illegal program claim, 01 = Conversation claim
        String victim = p.readString();
        int reason = p.readByte();
        String description = p.readString();
        if (type == 0) {
            if (c.getPlayer().getPossibleReports() > 0) {
                if (c.getPlayer().getMeso() > 299) {
                    c.getPlayer().decreaseReports();
                    c.getPlayer().gainMeso(-300, true);
                } else {
                    c.sendPacket(PacketCreator.reportResponse((byte) 4));
                    return;
                }
            } else {
                c.sendPacket(PacketCreator.reportResponse((byte) 2));
                return;
            }
            Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.serverNotice(6, victim + " was reported for: " + description));
            addReport(c.getPlayer().getId(), Character.getIdByName(victim), 0, description, "");
        } else if (type == 1) {
            String chatlog = p.readString();
            if (chatlog == null) {
                return;
            }
            if (c.getPlayer().getPossibleReports() > 0) {
                if (c.getPlayer().getMeso() > 299) {
                    c.getPlayer().decreaseReports();
                    c.getPlayer().gainMeso(-300, true);
                } else {
                    c.sendPacket(PacketCreator.reportResponse((byte) 4));
                    return;
                }
            }
            Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.serverNotice(6, victim + " was reported for: " + description));
            addReport(c.getPlayer().getId(), Character.getIdByName(victim), reason, description, chatlog);
        } else {
            Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.serverNotice(6, c.getPlayer().getName() + " is probably packet editing. Got unknown report type, which is impossible."));
        }
    }

    private void addReport(int reporterid, int victimid, int reason, String description, String chatlog) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO reports (`reporttime`, `reporterid`, `victimid`, `reason`, `chatlog`, `description`) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setTimestamp(1, Timestamp.from(Instant.now()));
            ps.setInt(2, reporterid);
            ps.setInt(3, victimid);
            ps.setInt(4, reason);
            ps.setString(5, chatlog);
            ps.setString(6, description);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
