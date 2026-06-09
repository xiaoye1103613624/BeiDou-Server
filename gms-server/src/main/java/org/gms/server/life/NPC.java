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
 * 非玩家角色
 * 继承AbstractLoadedLife，代表游戏地图中的NPC，支持商店功能和脚本交互
 *
 * @author OdinMS Team
 */
public class NPC extends AbstractLoadedLife {
    /** NPC属性信息 */
    private final NPCStats stats;

    /**
     * 构造一个NPC对象
     *
     * @param id NPC ID
     * @param stats NPC属性
     */
    public NPC(int id, NPCStats stats) {
        super(id);
        this.stats = stats;
    }

    /**
     * 判断该NPC是否拥有商店
     *
     * @return true表示NPC有商店，false表示没有
     */
    public boolean hasShop() {
        return ShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    /**
     * 向客户端发送商店界面
     *
     * @param c 客户端
     */
    public void sendShop(Client c) {
        ShopFactory.getInstance().getShopForNPC(getId()).sendShop(c);
    }

    /**
     * 发送NPC生成数据包给客户端
     * 包含NPC生成和控制器请求
     *
     * @param client 客户端
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnNPC(this));
        client.sendPacket(PacketCreator.spawnNPCRequestController(this, true));
    }

    /**
     * 发送NPC销毁数据包给客户端
     * 移除NPC控制器和NPC本身
     *
     * @param client 客户端
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeNPCController(getObjectId()));
        client.sendPacket(PacketCreator.removeNPC(getObjectId()));
    }

    /**
     * 获取地图对象类型
     *
     * @return 返回MapObjectType.NPC
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.NPC;
    }

    /**
     * 获取NPC名称
     *
     * @return NPC名称
     */
    public String getName() {
        return stats.getName();
    }
}