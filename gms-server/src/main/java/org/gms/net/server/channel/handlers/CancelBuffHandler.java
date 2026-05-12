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
import org.gms.client.SkillFactory;
import org.gms.constants.skills.Bishop;
import org.gms.constants.skills.Bowmaster;
import org.gms.constants.skills.Corsair;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.FPArchMage;
import org.gms.constants.skills.ILArchMage;
import org.gms.constants.skills.Marksman;
import org.gms.constants.skills.WindArcher;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.PacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 频道服务器入站封包处理器「CancelBuffHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class CancelBuffHandler extends AbstractPacketHandler implements PacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        int sourceid = p.readInt();

        switch (sourceid) {
            case FPArchMage.BIG_BANG:
            case ILArchMage.BIG_BANG:
            case Bishop.BIG_BANG:
            case Bowmaster.HURRICANE:
            case Marksman.PIERCING_ARROW:
            case Corsair.RAPID_FIRE:
            case WindArcher.HURRICANE:
            case Evan.FIRE_BREATH:
            case Evan.ICE_BREATH:
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.skillCancel(c.getPlayer(), sourceid), false);
                break;

            default:
                c.getPlayer().cancelEffect(SkillFactory.getSkill(sourceid).getEffect(1), false, -1);
                break;
        }
    }
}
