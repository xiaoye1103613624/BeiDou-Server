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
 * 【类型】AbstractPacketHandler（abstract class），包 `org.gms.net`。
 *
 * 数据包处理器的抽象基类，实现 {@link PacketHandler} 接口。
 * 提供默认的身份验证逻辑（检查客户端是否已登录）和服务器时间戳工具方法。
 *
 * 大部分频道服的 handler 继承此类，少数需要特殊身份校验的 handler 会覆盖 {@link #validateState(Client)} 方法。
 * handler 实例通过 {@link PacketProcessor#registerHandler} 注册到对应的 opcode 上。
 *
 * @see PacketHandler
 * @see PacketProcessor
 */
public abstract class AbstractPacketHandler implements PacketHandler {
    /** 默认验证：客户端必须处于已登录状态 */
    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn();
    }

    /** @return 服务器当前时间戳（毫秒），用于封包中的时间字段 */
    protected static long currentServerTime() {
        return Server.getInstance().getCurrentTime();
    }
}