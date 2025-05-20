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
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.scripting.quest.QuestScriptManager;

/**
 * @author Matze
 */
public final class NPCMoreTalkHandler extends AbstractPacketHandler {
    @Override
    public final void handlePacket(InPacket p, Client c) {
        byte lastMsg = p.readByte(); // 00 (last msg type I think)
        byte action = p.readByte(); // 00 = end chat, 01 == follow
        // lastMsg等于2有returnText，不等于则没有
        if (lastMsg == 2) {
            if (action != 0) {
                String returnText = p.readString();
                if (c.getQM() != null) {
                    c.getQM().setGetText(returnText);
                    if (c.getQM().isStart()) {
                        QuestScriptManager.getInstance().start(c, action, lastMsg, -1);
                    } else {
                        QuestScriptManager.getInstance().end(c, action, lastMsg, -1);
                    }
                } else {
                    c.getCM().setGetText(returnText);
                    cmRouting(c, action, lastMsg, -1);
                }
            } else if (c.getQM() != null) {
                c.getQM().dispose();
            } else {
                c.getCM().dispose();
            }
        } else {
            int selection = -1;
            if (p.available() >= 4) {
                selection = p.readInt();
            } else if (p.available() > 0) {
                selection = p.readUnsignedByte();
            }
            if (c.getQM() != null) {
                if (c.getQM().isStart()) {
                    QuestScriptManager.getInstance().start(c, action, lastMsg, selection);
                } else {
                    QuestScriptManager.getInstance().end(c, action, lastMsg, selection);
                }
            } else if (c.getCM() != null) {
                cmRouting(c, action, lastMsg, selection);
            }
        }
    }

    private void cmRouting(Client c, byte action, byte lastMsg, int selection) {
        if (c.getCM().getNextLevelContext().getLevelType() == null) {
            NPCScriptManager.getNpcInstance().action(c, action, lastMsg, selection);
        } else {
            NPCScriptManager.getNpcInstance().nextLevel(c, action, lastMsg, selection);
        }
    }
}
