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
import org.gms.dao.entity.NotesDO;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.service.NoteService;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 便签操作处理器
 * 处理便签（Note）的发送和删除操作，包括商城感谢回复和游戏内便签丢弃
 */
public final class NoteActionHandler extends AbstractPacketHandler {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(NoteActionHandler.class);

    /** 便签服务 */
    private final NoteService noteService;

    /**
     * 构造便签操作处理器
     *
     * @param noteService 便签服务实例
     */
    public NoteActionHandler(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * 处理便签操作包，根据操作类型执行发送（商城感谢回复）或丢弃（获得声望）操作
     *
     * @param p 输入数据包，包含操作类型和便签数据
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public void handlePacket(InPacket p, Client c) {
        int action = p.readByte();
        if (action == 0 && c.getPlayer().getCashShop().getAvailableNotes() > 0) { // Reply to gift in cash shop
            String charname = p.readString();
            String message = p.readString();
            if (c.getPlayer().getCashShop().isOpened()) {
                c.sendPacket(PacketCreator.showCashInventory(c));
            }
            try {
                noteService.sendWithFame(message, c.getPlayer().getName(), charname);
                c.getPlayer().getCashShop().decreaseNotes();
            } catch (Exception e) {
                log.error("Failed to send note", e);
            }
        } else if (action == 1) { // Discard notes in game
            int num = p.readByte();
            p.readByte();
            p.readByte();
            int fame = 0;
            for (int i = 0; i < num; i++) {
                int id = p.readInt();
                p.readByte(); //Fame, but we read it from the database :)

                Optional<NotesDO> discardedNote = noteService.delete(id);
                if (discardedNote.isEmpty()) {
                    log.warn("Note with id {} not able to be discarded. Already discarded?", id);
                    continue;
                }

                fame += discardedNote.get().getFame();
            }
            if (fame > 0) {
                c.getPlayer().gainFame(fame);
            }
        }
    }
}
