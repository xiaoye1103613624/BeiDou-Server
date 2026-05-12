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
import org.gms.client.SkillMacro;
import org.gms.client.autoban.AutobanFactory;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

/**
 * 频道服务器入站封包处理器「SkillMacroHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class SkillMacroHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        int num = p.readByte();
        if (num > 5) {
            return;
        }

        for (int i = 0; i < num; i++) {
            String name = p.readString();
            if (name.length() > 12) {
                AutobanFactory.PACKET_EDIT.alert(chr, "Invalid name length " + name + " (" + name.length() + ") for skill macro.");
                c.disconnect(false, false);
                break;
            }

            int shout = p.readByte();
            int skill1 = p.readInt();
            int skill2 = p.readInt();
            int skill3 = p.readInt();
            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }
}
