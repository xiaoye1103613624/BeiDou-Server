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
import org.gms.client.autoban.AutobanFactory;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 频道服务器入站封包处理器「GiveFameHandler」。
 * 对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），
 * 从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。
 * 通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。
 */
public final class GiveFameHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(GiveFameHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character target = (Character) c.getPlayer().getMap().getMapObject(p.readInt());
        int mode = p.readByte();
        int famechange = 2 * mode - 1;
        Character player = c.getPlayer();
        if (target == null || target.getId() == player.getId() || player.getLevel() < 15) {
            return;
        } else if (famechange != 1 && famechange != -1) {
            AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit fame.");
            log.warn(I18nUtil.getLogMessage("GiveFameHandler.handlePacket.warn1"), c.getPlayer().getName(), famechange);
            c.disconnect(true, false);
            return;
        }

        int status = 0;
        if (player.getLastfametime() >= System.currentTimeMillis() - 86400000) {
            status = 3;
        } else if (player.getLastmonthfameids().contains(target.getId())) {
            status = 4;
        }

        if (status == 0) {
            if (target.gainFame(famechange, player, mode)) {
                if (!player.isGM()) {
                    player.hasGivenFame(target);
                }
            } else {
                player.message(I18nUtil.getMessage("GiveFameHandler.handlePacket.message1"));
            }
        } else {
            c.sendPacket(PacketCreator.giveFameErrorResponse(status));
        }
    }
}