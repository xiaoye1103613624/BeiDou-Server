/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

/**
 * 家族谱系打开处理器
 * 处理玩家查看指定角色家族谱系的操作
 *
 * @author Ubaware
 */
public final class OpenFamilyPedigreeHandler extends AbstractPacketHandler {

    /**
     * 处理打开家族谱系包，根据目标角色名称获取其家族谱系信息并发送给客户端
     *
     * @param p 输入数据包，包含目标角色名称
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        if (!GameConfig.getServerBoolean("use_family_system")) {
            return;
        }
        Character target = c.getChannelServer().getPlayerStorage().getCharacterByName(p.readString());
        if (target != null && target.getFamily() != null) {
            c.sendPacket(PacketCreator.showPedigree(target.getFamilyEntry()));
        }
    }
}

