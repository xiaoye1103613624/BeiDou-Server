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
package org.gms.net;

import org.gms.client.Client;
import org.gms.net.server.Server;

/**
 * 抽象数据包处理器
 * 提供数据包处理的默认实现，验证客户端登录状态和获取当前服务器时间
 */
public abstract class AbstractPacketHandler implements PacketHandler {
    /**
     * 验证客户端状态
     * 默认验证客户端是否已登录
     *
     * @param c 客户端对象
     * @return true表示状态有效
     */
    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn();
    }

    /**
     * 获取当前服务器时间
     *
     * @return 服务器当前时间戳
     */
    protected static long currentServerTime() {
        return Server.getInstance().getCurrentTime();
    }
}