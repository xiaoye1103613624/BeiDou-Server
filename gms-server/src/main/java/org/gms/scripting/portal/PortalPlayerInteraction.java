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

package org.gms.scripting.portal;

import org.gms.client.Client;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.scripting.map.MapScriptManager;
import org.gms.server.maps.Portal;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 传送门玩家互动脚本。
 */
public class PortalPlayerInteraction extends AbstractPlayerInteraction {
    /**
     * 传送门
     */
    private final Portal portal;

    public PortalPlayerInteraction(Client client, Portal portal) {
        super(client);
        this.portal = portal;
    }

    /**
     * 获取门户对象
     *
     * @return 返回门户对象
     */
    public Portal getPortal() {
        return portal;
    }

    /**
     * 运行地图脚本
     * 该方法首先获取 MapScriptManager 的实例，然后调用其 runMapScript 方法来执行特定的地图脚本。
     */
    public void runMapScript() {
        MapScriptManager msm = MapScriptManager.getInstance();
        msm.runMapScript(client, "onUserEnter/" + portal.getScriptName(), false);
    }

    /**
     * 检查玩家是否拥有等级达到30的角色
     *
     * @return 如果玩家有等级达到30的角色，返回true；否则返回false
     * @throws SQLException 如果数据库操作出现异常，则抛出SQLException异常
     */
    public boolean hasLevel30Character() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `level` FROM `characters` WHERE accountid = ?")) {
            ps.setInt(1, getPlayer().getAccountId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("level") >= 30) {
                        return true;
                    }
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return getPlayer().getLevel() >= 30;
    }

    /**
     * 阻塞指定的传送门
     *
     * <p>通过调用此方法，可以阻塞指定的传送门，使其不可通过。</p>
     */
    public void blockPortal() {
        client.getPlayer().blockPortal(getPortal().getScriptName());
    }

    /**
     * 解锁传送门
     * 调用客户端的玩家的解锁传送门方法，解锁与当前传送门对应的传送门
     */
    public void unblockPortal() {
        client.getPlayer().unblockPortal(getPortal().getScriptName());
    }

    /**
     * 播放传送门音效
     * 发送一个数据包给客户端，使客户端播放传送门音效。
     */
    public void playPortalSound() {
        client.sendPacket(PacketCreator.playPortalSound());
    }
}
