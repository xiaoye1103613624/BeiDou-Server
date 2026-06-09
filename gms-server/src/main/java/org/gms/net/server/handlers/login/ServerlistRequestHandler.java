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
import org.gms.constants.game.GameConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.util.PacketCreator;

import java.util.List;

/**
 * 服务器列表请求处理器
 * 处理客户端请求获取所有世界服务器列表
 */
public final class ServerlistRequestHandler extends AbstractPacketHandler {

    /**
     * 处理服务器列表请求
     * 遍历所有世界，发送每个世界的信息给客户端
     *
     * @param p 输入数据包
     * @param c 客户端会话
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        Server server = Server.getInstance();
        List<World> worlds = server.getWorlds();
        c.requestedServerlist(worlds.size());

        for (World world : worlds) {
            c.sendPacket(PacketCreator.getServerList(world.getId(), GameConstants.WORLD_NAMES[world.getId()], world.getFlag(), world.getEventMessage(), world.getChannels()));
        }
        c.sendPacket(PacketCreator.getEndOfServerList());
        c.sendPacket(PacketCreator.selectWorld(0));//too lazy to make a check lol
        c.sendPacket(PacketCreator.sendRecommended(server.worldRecommendedList()));
    }
}