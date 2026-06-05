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
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#CANCEL_BUFF} 封包。
 * 负责处理客户端的取消增益效果（技能buff）操作。
 */
public final class CancelBuffHandler extends AbstractPacketHandler implements PacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        int sourceid = p.readInt();

        // 根据技能ID处理BUFF取消（需要广播到地图/单独取消）
        switch (sourceid) {
            // 需要广播到地图的技能效果：法师大爆炸/弓手暴风弓/海盗速射/龙之子气息
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
            // 其他技能：单独取消效果
            default:
                c.getPlayer().cancelEffect(SkillFactory.getSkill(sourceid).getEffect(1), false, -1);
                break;
        }
    }
}