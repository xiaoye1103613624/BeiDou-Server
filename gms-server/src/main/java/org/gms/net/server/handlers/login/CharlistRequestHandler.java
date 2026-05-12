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
package org.gms.net.server.handlers.login;

import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.util.PacketCreator;

/**
 * 登录服务器入站封包处理器「CharlistRequestHandler」。
 * 处理账号登录、选角、创建角色、PIN/PIC、服务器列表等与尚未进入频道相关的协议。
 * 在验证通过后可能更新 {@link org.gms.client.Client} 的账号状态并切换会话阶段。
 */
public final class CharlistRequestHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        p.readByte();
        int world = p.readByte();

        World wserv = Server.getInstance().getWorld(world);
        if (wserv == null || wserv.isWorldCapacityFull()) {
            c.sendPacket(PacketCreator.getServerStatus(2));
            return;
        }

        int channel = p.readByte() + 1;
        Channel ch = wserv.getChannel(channel);
        if (ch == null) {
            c.sendPacket(PacketCreator.getServerStatus(2));
            return;
        }

        c.setWorld(world);
        c.setChannel(channel);
        c.sendCharList(world);
    }
}