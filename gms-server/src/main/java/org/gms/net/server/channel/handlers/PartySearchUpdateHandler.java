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

import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 组队搜索更新处理器
 * 脚本、GM指令传送玩家到指定地图触发，用于注销玩家的组队搜索登记
 */
public final class PartySearchUpdateHandler extends AbstractPacketHandler {

    /**
     * 处理组队搜索更新包，将当前玩家从组队搜索协调器中注销
     *
     * @param p 输入数据包
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        c.getWorldServer().getPartySearchCoordinator().unregisterPartyLeader(c.getPlayer());
    }
}