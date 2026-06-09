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
import org.gms.net.server.world.Party;
import org.gms.net.server.world.World;
import org.gms.util.PacketCreator;

/**
 * 组队搜索开始处理器
 * 处理玩家开始组队搜索的操作
 *
 * @author XoticStory
 * @author BubblesDev
 * @author Ronan
 */
public class PartySearchStartHandler extends AbstractPacketHandler {

    /**
     * 处理组队搜索开始包，验证等级范围和职业条件后注册队伍队长到组队搜索协调器
     *
     * @param p 输入数据包，包含最小/最大等级、成员数和职业过滤条件
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public void handlePacket(InPacket p, Client c) {
        int min = p.readInt();
        int max = p.readInt();

        Character chr = c.getPlayer();
        if (min > max) {
            chr.dropMessage(1, "The min. value is higher than the max!");
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        if (max - min > 30) {
            chr.dropMessage(1, "You can only search for party members within a range of 30 levels.");
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        if (chr.getLevel() < min || chr.getLevel() > max) {
            chr.dropMessage(1, "The range of level for search has to include your own level.");
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        p.readInt(); // members
        int jobs = p.readInt();

        Party party = c.getPlayer().getParty();
        if (party == null || !c.getPlayer().isPartyLeader()) {
            return;
        }

        World world = c.getWorldServer();
        world.getPartySearchCoordinator().registerPartyLeader(chr, min, max, jobs);
    }
}