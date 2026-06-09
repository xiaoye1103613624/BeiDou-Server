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

/**
 * 需要登录的空操作处理器
 * 对已登录客户端的数据包不做任何处理（占位处理器），单例模式
 */
public final class LoginRequiringNoOpHandler implements PacketHandler {
    /** 单例实例 */
    private static final LoginRequiringNoOpHandler instance = new LoginRequiringNoOpHandler();

    /**
     * 获取单例实例
     *
     * @return 单例
     */
    public static LoginRequiringNoOpHandler getInstance() {
        return instance;
    }

    /**
     * 空操作处理
     *
     * @param p 输入数据包
     * @param c 客户端会话
     */
    public void handlePacket(InPacket p, Client c) {
    }

    /**
     * 验证客户端状态
     *
     * @param c 客户端会话
     * @return 仅已登录状态返回true
     */
    public boolean validateState(Client c) {
        return c.isLoggedIn();
    }
}
