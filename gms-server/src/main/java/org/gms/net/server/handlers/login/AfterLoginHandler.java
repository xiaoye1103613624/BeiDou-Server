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
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.util.PacketCreator;

/**
 * 登录后处理器
 * 处理登录成功后的PIN码验证流程，包括PIN注册、验证和跳过
 */
public final class AfterLoginHandler extends AbstractPacketHandler {

    /**
     * 处理登录后的PIN码操作
     * 根据客户端发来的不同类型码，执行PIN注册、PIN验证或跳过PIN流程
     *
     * @param p 输入数据包
     * @param c 客户端会话
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        byte c2 = p.readByte();
        byte c3 = 5;
        if (p.available() > 0) {
            c3 = p.readByte();
        }
        if (c2 == 1 && c3 == 1) {
            if (c.getPin() == null || c.getPin().equals("")) {
                c.sendPacket(PacketCreator.registerPin());
            } else {
                c.sendPacket(PacketCreator.requestPin());
            }
        } else if (c2 == 1 && c3 == 0) {
            String pin = p.readString();
            if (c.checkPin(pin)) {
                c.sendPacket(PacketCreator.pinAccepted());
            } else {
                c.sendPacket(PacketCreator.requestPinAfterFailure());
            }
        } else if (c2 == 2 && c3 == 0) {
            String pin = p.readString();
            if (c.checkPin(pin)) {
                c.sendPacket(PacketCreator.registerPin());
            } else {
                c.sendPacket(PacketCreator.requestPinAfterFailure());
            }
        } else if (c2 == 0 && c3 == 5) {
            SessionCoordinator.getInstance().closeSession(c, null);
            c.updateLoginState(Client.LOGIN_NOTLOGGEDIN);
        }
    }
}
