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
import org.gms.util.PacketCreator;

/**
 * @author kevintjuh93
 * <p>
 * Modified by -- Ronan - concurrency protection
 */
public class UseGachaExpHandler extends AbstractPacketHandler {

    @Override
    public void handlePacket(InPacket p, Client c) {

        if (c.tryacquireClient()) {
            try {
                if (c.getPlayer().getGachaExp() <= 0) {
                    AutobanFactory.GACHA_EXP.autoBan(c.getPlayer(), "Player tried to redeem GachaEXP, but had none to redeem.");
                }
                c.getPlayer().gainGachaExp();
            } finally {
                c.releaseClient();
            }
        }

        c.sendPacket(PacketCreator.enableActions());
    }
}
