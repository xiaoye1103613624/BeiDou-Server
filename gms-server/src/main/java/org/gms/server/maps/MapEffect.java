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
package org.gms.server.maps;

import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.util.PacketCreator;

/**
 * 地图特效
 * 管理地图上的视觉特效，包括消息文字和物品特效，支持开始和销毁
 */
public class MapEffect {
    /** 特效消息文字 */
    private final String msg;
    /** 特效物品ID */
    private final int itemId;
    /** 是否激活，默认true */
    private final boolean active = true;

    /**
     * 构造方法
     *
     * @param msg    特效消息文字
     * @param itemId 特效物品ID
     */
    public MapEffect(String msg, int itemId) {
        this.msg = msg;
        this.itemId = itemId;
    }

    /**
     * 生成销毁特效的数据包
     *
     * @return 销毁特效的网络包
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeMapEffect();
    }

    /**
     * 生成开始特效的数据包
     *
     * @return 开始特效的网络包
     */
    public final Packet makeStartData() {
        return PacketCreator.startMapEffect(msg, itemId, active);
    }

    /**
     * 向客户端发送开始特效
     *
     * @param client 客户端
     */
    public void sendStartData(Client client) {
        client.sendPacket(makeStartData());
    }
}