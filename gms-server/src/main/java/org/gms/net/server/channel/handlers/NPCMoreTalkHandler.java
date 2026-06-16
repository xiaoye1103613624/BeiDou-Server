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
import org.gms.config.GameConfig;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.scripting.quest.QuestScriptManager;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;

/**
 * @author Matze
 * NPC对话继续处理器
 * 处理玩家在NPC对话框中的"下一步"/选择选项等操作，包含操作限流防止快速点击
 */
public final class NPCMoreTalkHandler extends AbstractPacketHandler {
    @Override
    public final void handlePacket(InPacket p, Client c) {
        // NPC交互限流：防止快速连续点击导致脚本竞态条件或刷物品
        if (currentServerTime() - c.getPlayer().getNpcCooldown() < GameConfig.getServerInt("block_npc_race_condition")) {
            c.getPlayer().dropMessage(5, I18nUtil.getMessage("NPCTalkHandler.handlePacket.message2"));
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

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

    /**
     * NPC对话路由方法，根据是否有下一级上下文选择执行action或nextLevel
     *
     * @param c        客户端连接
     * @param action   操作类型
     * @param lastMsg  上一条消息类型
     * @param selection 玩家选择的选项索引
     */
    private void cmRouting(Client c, byte action, byte lastMsg, int selection) {
        if (c.getCM().getNextLevelContext().getLevelType() == null) {
            NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
        } else {
            NPCScriptManager.getInstance().nextLevel(c, action, lastMsg, selection);
        }
    }
}