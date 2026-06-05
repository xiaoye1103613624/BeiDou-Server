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
 * 【类型】MapEffect（class），包 `org.gms.server.maps`。
 * 
 * <p>地图效果类，用于在游戏中显示文字或图像效果。
 * 地图效果是一种特殊的游戏效果，可以在整个地图上显示消息或视觉效果，
 * 通常由管理员命令或特殊事件触发。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理地图上的特殊效果</li>
 *   <li>处理效果的显示和隐藏</li>
 *   <li>关联效果与特定道具</li>
 * </ul>
 */
public class MapEffect {
    /** 效果显示的消息文本 */
    private final String msg;
    /** 与效果关联的道具ID */
    private final int itemId;
    /** 效果是否活跃 */
    private final boolean active = true;

    /**
     * 构造函数：创建地图效果实例
     * 
     * @param msg 效果显示的消息文本
     * @param itemId 与效果关联的道具ID
     */
    public MapEffect(String msg, int itemId) {
        this.msg = msg;
        this.itemId = itemId;
    }

    /**
     * 创建销毁数据包
     * 
     * @return 地图效果销毁数据包
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeMapEffect();
    }

    /**
     * 创建开始数据包
     * 
     * @return 地图效果开始数据包
     */
    public final Packet makeStartData() {
        return PacketCreator.startMapEffect(msg, itemId, active);
    }

    /**
     * 发送开始数据给客户端
     * 
     * @param client 要发送数据的客户端
     */
    public void sendStartData(Client client) {
        client.sendPacket(makeStartData());
    }
}