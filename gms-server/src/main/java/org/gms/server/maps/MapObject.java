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

import java.awt.*;

/**
 * 【接口】MapObject（interface），包 {@code org.gms.server.maps}。
 * 
 * <p>地图对象基础契约接口，定义所有地图内对象的通用行为规范，包括标识符管理、位置操作、网络数据同步等核心功能。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义地图对象的基础属性（ID、类型、位置）</li>
 *   <li>提供对象在网络中的生成和销毁数据发送方法</li>
 *   <li>统一管理地图对象的生命周期</li>
 * </ul>
 * 
 * <p>实现要求：</p>
 * <ul>
 *   <li>所有地图对象都必须实现此接口</li>
 *   <li>需正确实现网络数据的序列化方法</li>
 *   <li>确保对象状态的一致性</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public interface MapObject {
    /**
     * 获取对象唯一标识符
     * 
     * @return 对象ID
     */
    int getObjectId();
    
    /**
     * 设置对象唯一标识符
     * 
     * @param id 对象ID
     */
    void setObjectId(int id);
    
    /**
     * 获取对象类型
     * 
     * @return MapObjectType 对象类型枚举
     */
    MapObjectType getType();
    
    /**
     * 获取对象当前位置
     * 
     * @return Point 位置坐标
     */
    Point getPosition();
    
    /**
     * 设置对象位置
     * 
     * @param position 新位置坐标
     */
    void setPosition(Point position);
    
    /**
     * 发送对象生成数据到客户端
     * 
     * <p>当对象首次出现在地图上时，向指定客户端发送生成数据包。</p>
     * 
     * @param client 目标客户端
     */
    void sendSpawnData(Client client);
    
    /**
     * 发送对象销毁数据到客户端
     * 
     * <p>当对象从地图移除时，向指定客户端发送销毁数据包。</p>
     * 
     * @param client 目标客户端
     */
    void sendDestroyData(Client client);
    
    /**
     * 清空位置信息
     * 
     * <p>将位置引用设为null，通常在对象从地图移除时调用。</p>
     */
    void nullifyPosition();
}