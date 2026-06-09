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
 * 传送门玩家交互
 * 封装传送门相关的玩家交互操作，继承自AbstractPlayerInteraction
 */
public class PortalPlayerInteraction extends AbstractPlayerInteraction {
    /** 关联的传送门对象 */
    private final Portal portal;

    public PortalPlayerInteraction(Client c, Portal portal) {
        super(c);
        this.portal = portal;
    }

    /**
     * 获取关联的传送门
     *
     * @return 传送门对象
     */
    public Portal getPortal() {
        return portal;
    }

    /**
     * 执行地图进入脚本
     */
    public void runMapScript() {
        MapScriptManager msm = MapScriptManager.getInstance();
        msm.runMapScript(c, "onUserEnter/" + portal.getScriptName(), false);
    }

    /**
     * 判断玩家账号下是否有等级≥30的角色
     * 先查数据库，再检查当前角色等级
     *
     * @return 是否有30级以上角色
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
     * 封锁当前传送门，禁止该玩家使用
     */
    public void blockPortal() {
        c.getPlayer().blockPortal(getPortal().getScriptName());
    }

    /**
     * 解除当前传送门封锁
     */
    public void unblockPortal() {
        c.getPlayer().unblockPortal(getPortal().getScriptName());
    }

    /**
     * 播放传送门生效音效
     */
    public void playPortalSound() {
        c.sendPacket(PacketCreator.playPortalSound());
    }
}