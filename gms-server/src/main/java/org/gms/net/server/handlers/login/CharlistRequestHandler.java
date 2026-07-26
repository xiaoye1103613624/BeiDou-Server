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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CharlistRequestHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(CharlistRequestHandler.class);

    @Override
    public final void handlePacket(InPacket p, Client c) {
        p.readByte();
        int world = p.readByte();

        World wserv = Server.getInstance().getWorld(world);
        if (wserv == null || wserv.isWorldCapacityFull()) {
            log.warn("[LoginFlow] CharList rejected worldFull/missing world={} account={}", world, c.getAccountName());
            c.sendPacket(PacketCreator.getServerStatus(2));
            return;
        }

        int channel = p.readByte() + 1;
        Channel ch = wserv.getChannel(channel);
        if (ch == null) {
            log.warn("[LoginFlow] CharList rejected badChannel world={} channel={} account={}", world, channel, c.getAccountName());
            c.sendPacket(PacketCreator.getServerStatus(2));
            return;
        }

        c.setWorld(world);
        c.setChannel(channel);
        log.info("[LoginFlow] CharListRequest account={} world={} channel={}", c.getAccountName(), world, channel);
        try {
            c.sendCharList(world);
            log.info("[LoginFlow] CharList sent account={} world={} channel={}", c.getAccountName(), world, channel);
        } catch (Exception e) {
            log.error("[LoginFlow] CharList FAILED account={} world={}", c.getAccountName(), world, e);
            throw e;
        }
    }
}