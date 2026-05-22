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
import org.gms.client.autoban.AutobanFactory;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#CHANGE_CHANNEL} 封包。
 * 负责处理客户端的换频道操作。
 */
public final class ChangeChannelHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        int channel = p.readByte() + 1; // 客户端值从0开始，服务端从1开始
        p.readInt();
        // 更新反作弊时间戳
        c.getPlayer().getAutoBanManager().setTimestamp(6, Server.getInstance().getCurrentTimestamp(), 3);
        // 切到同一频道视为异常
        if (c.getChannel() == channel) {
            AutobanFactory.GENERAL.alert(c.getPlayer(), "CCing to same channel.");
            c.disconnect(false, false);
            return;
        // 商城/小游戏/商店中禁止切频道
        } else if (c.getPlayer().getCashShop().isOpened() || c.getPlayer().getMiniGame() != null || c.getPlayer().getPlayerShop() != null) {
            return;
        }

        // 执行频道切换
        c.changeChannel(channel);
    }
}