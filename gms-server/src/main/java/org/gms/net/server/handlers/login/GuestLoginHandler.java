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
import org.gms.util.PacketCreator;

/**
 * 游客登录处理器
 * 处理客户端以游客身份登录的请求，发送服务条款后复用密码登录流程
 *
 * @author David
 */
public final class GuestLoginHandler extends AbstractPacketHandler {

    /**
     * 处理游客登录
     * 先发送游客服务条款，然后委托给LoginPasswordHandler完成登录
     *
     * @param p 输入数据包
     * @param c 客户端会话
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        c.sendPacket(PacketCreator.sendGuestTOS());
        //System.out.println(slea.toString());
        new LoginPasswordHandler().handlePacket(p, c);
    }
}
