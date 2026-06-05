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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.util.PacketCreator;


/**
 * 【类型】Dragon（class），包 `org.gms.server.maps`。
 * 
 * <p>龙类，表示玩家角色身上的龙形态或龙相关效果。
 * 此类继承自AbstractAnimatedMapObject，具有动画效果和姿态管理功能，
 * 用于在游戏中显示龙相关的视觉效果。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理龙形态的显示</li>
 *   <li>处理龙的生成和销毁数据包</li>
 *   <li>关联龙与其拥有者角色</li>
 * </ul>
 */
public class Dragon extends AbstractAnimatedMapObject {
    /** 龙的所有者角色 */
    private final Character owner;

    /**
     * 构造函数：创建龙实例
     * 
     * @param chr 龙的所有者角色
     */
    public Dragon(Character chr) {
        super();
        this.owner = chr;
        this.setPosition(chr.getPosition());
        this.setStance(chr.getStance());
        this.sendSpawnData(chr.getClient());
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.DRAGON;
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnDragon(this));
    }

    @Override
    public int getObjectId() {
        return owner.getId();
    }

    @Override
    public void sendDestroyData(Client c) {
        c.sendPacket(PacketCreator.removeDragon(owner.getId()));
    }

    /**
     * 获取龙的所有者
     * 
     * @return 龙的所有者角色
     */
    public Character getOwner() {
        return owner;
    }
}