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
 * 登录服务器入站封包处理器「ServerlistRequestHandler」。
 * 处理账号登录、选角、创建角色、PIN/PIC、服务器列表等与尚未进入频道相关的协议。
 * 在验证通过后可能更新 {@link org.gms.client.Client} 的账号状态并切换会话阶段。
 */
public final class ServerlistRequestHandler extends AbstractPacketHandler {

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