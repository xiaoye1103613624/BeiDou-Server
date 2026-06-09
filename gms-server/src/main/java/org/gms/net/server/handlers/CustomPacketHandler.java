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
package org.gms.net.server.handlers;

import org.gms.client.Client;
import org.gms.net.PacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 自定义数据包处理器
 * 允许GM级别4以上的管理员发送自定义数据包到客户端
 */
public class CustomPacketHandler implements PacketHandler {
    /**
     * 处理自定义数据包
     * 仅当有数据和GM等级>=4时生效
     *
     * @param p 输入数据包
     * @param c 客户端会话
     */
    @Override
    public void handlePacket(InPacket p, Client c) {
        if (p.available() > 0 && c.getGMLevel() >= 4) {//w/e
            c.sendPacket(PacketCreator.customPacket(p.readBytes(p.available())));
        }
    }

    /**
     * 验证客户端状态
     *
     * @param c 客户端会话
     * @return 始终返回true，允许任何状态
     */
    @Override
    public boolean validateState(Client c) {
        return true;
    }
}
