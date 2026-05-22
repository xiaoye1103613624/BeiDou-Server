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
package org.gms.server.life;

import org.gms.client.Client;
import org.gms.server.ShopFactory;
import org.gms.server.maps.MapObjectType;
import org.gms.util.PacketCreator;

/**
 * 【类型】NPC（class），包 `org.gms.server.life`。
 *
 * 地图上的 NPC 实例，继承 {@link AbstractLoadedLife}，由 {@link LifeFactory#getNPC(int)} 根据 WZ 数据创建。
 * 每个 NPC 持有一个 {@link NPCStats} 属性集（名称、功能标记等），并提供商店快捷访问。
 *
 * NPC 通过 {@link org.gms.scripting.npc.NPCScriptManager} 触发 JS 脚本交互，
 * 脚本通过 {@code cm.openNpc(npcId, "ScriptName")} 调用。
 */
public class NPC extends AbstractLoadedLife {
    /** NPC 属性（名称、是否可移动等） */
    private final NPCStats stats;

    public NPC(int id, NPCStats stats) {
        super(id);
        this.stats = stats;
    }

    /** 检查此 NPC 是否关联了商店 */
    public boolean hasShop() {
        return ShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    /** 向指定客户端发送商店 UI */
    public void sendShop(Client c) {
        ShopFactory.getInstance().getShopForNPC(getId()).sendShop(c);
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnNPC(this));
        client.sendPacket(PacketCreator.spawnNPCRequestController(this, true));
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeNPCController(getObjectId()));
        client.sendPacket(PacketCreator.removeNPC(getObjectId()));
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.NPC;
    }

    public String getName() {
        return stats.getName();
    }
}
