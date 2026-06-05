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

/**
 * 【接口】AnimatedMapObject：由 `maps` 模块实现的契约。
 * 
 * <p>可动画地图对象接口，定义了具有动画效果的地图对象所需实现的方法。
 * 此接口扩展了MapObject接口，增加了姿态管理和朝向判断功能，
 * 适用于需要显示动画效果的地图对象，如玩家角色、怪物、NPC等。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理对象的姿态值（朝向、动作等）</li>
 *   <li>提供朝向判断功能</li>
 *   <li>继承地图对象的基本功能</li>
 * </ul>
 */
public interface AnimatedMapObject extends MapObject {
    /**
     * 获取姿态值
     * 
     * @return 当前的姿态值
     */
    int getStance();
    
    /**
     * 设置姿态值
     * 
     * @param stance 要设置的姿态值
     */
    void setStance(int stance);
    
    /**
     * 判断是否朝左
     * 
     * @return true=朝左, false=朝右
     */
    boolean isFacingLeft();
}