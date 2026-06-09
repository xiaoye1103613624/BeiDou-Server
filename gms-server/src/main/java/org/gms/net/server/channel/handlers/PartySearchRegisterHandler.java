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

/**
 * 组队搜索注册处理器
 * 处理玩家注册组队搜索的操作
 *
 * @author Quasar
 */
public class PartySearchRegisterHandler extends AbstractPacketHandler {

    /**
     * 处理组队搜索注册包（当前为空实现，预留扩展）
     *
     * @param p 输入数据包
     * @param c 客户端连接
     */
    @Override
    public void handlePacket(InPacket p, Client c) {}
}