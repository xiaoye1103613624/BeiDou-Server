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

import java.awt.*;

/**
 * 【类型】AbstractMapObject（abstract class），包 {@code org.gms.server.maps}。
 * 
 * <p>地图对象抽象基类，为所有地图对象提供基础属性和通用方法实现，包括位置坐标和唯一对象ID的管理。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理地图对象的位置坐标</li>
 *   <li>维护对象的唯一标识符</li>
 *   <li>提供位置和ID的获取与设置方法</li>
 *   <li>作为所有地图对象的统一基类</li>
 * </ul>
 * 
 * <p>设计说明：</p>
 * <ul>
 *   <li>这是一个抽象类，不能直接实例化</li>
 *   <li>必须实现MapObject接口</li>
 *   <li>子类需要实现getType()方法</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public abstract class AbstractMapObject implements MapObject {
    /** 地图对象当前位置坐标 */
    private Point position = new Point();
    /** 地图对象唯一标识符 */
    private int objectId;

    /**
     * 获取地图对象类型
     * 
     * <p>此方法必须由子类实现，返回当前对象的具体类型。</p>
     * 
     * @return MapObjectType 对象类型枚举
     */
    @Override
    public abstract MapObjectType getType();

    /**
     * 获取地图对象当前位置
     * 
     * <p>返回当前位置坐标的副本，防止外部修改影响内部状态。</p>
     * 
     * @return Point 当前位置坐标副本
     */
    @Override
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * 设置地图对象位置
     * 
     * <p>更新对象在地图上的坐标位置。</p>
     * 
     * @param position 新的位置坐标
     */
    @Override
    public void setPosition(Point position) {
        this.position.move(position.x, position.y);
    }

    /**
     * 获取地图对象唯一ID
     * 
     * <p>返回分配给此对象的唯一标识符，用于在地图中区分不同对象。</p>
     * 
     * @return int 对象唯一ID
     */
    @Override
    public int getObjectId() {
        return objectId;
    }

    /**
     * 设置地图对象唯一ID
     * 
     * <p>为对象分配唯一标识符，通常由地图管理系统自动分配。</p>
     * 
     * @param id 对象唯一ID
     */
    @Override
    public void setObjectId(int id) {
        this.objectId = id;
    }

    /**
     * 清空位置信息
     * 
     * <p>将位置引用设为null，通常在对象从地图移除时调用。</p>
     */
    @Override
    public void nullifyPosition() {
        this.position = null;
    }
}