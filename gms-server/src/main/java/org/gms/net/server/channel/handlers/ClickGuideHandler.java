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
import org.gms.client.Job;
import org.gms.constants.id.NpcId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.npc.NPCScriptManager;

/**
 * @author kevintjuh93
 */
public class ClickGuideHandler extends AbstractPacketHandler {
    @Override
    public void handlePacket(InPacket p, Client c) {
        if (c.getPlayer().getJob().equals(Job.NOBLESSE)) {
            NPCScriptManager.getNpcInstance().start(c, NpcId.MIMO, null);
        } else {
            NPCScriptManager.getNpcInstance().start(c, NpcId.LILIN, null);
        }
    }

}
