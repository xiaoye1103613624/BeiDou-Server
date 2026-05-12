/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
import org.gms.server.life.LifeFactory.BanishInfo;
import org.gms.server.life.Monster;

/**
 * 频道服务器入站封包处理器「MobBanishPlayerHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class MobBanishPlayerHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        int mobid = p.readInt();     // mob banish handling detected thanks to MedicOP

        Character chr = c.getPlayer();
        Monster mob = chr.getMap().getMonsterById(mobid);

        if (mob != null) {
            BanishInfo banishInfo = mob.getBanish();
            if (banishInfo != null) {
                chr.changeMapBanish(banishInfo.getMap(), banishInfo.getPortal(), banishInfo.getMsg());
            }
        }
    }
}