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
 * 【接口】Portal（interface），包 {@code org.gms.server.maps}。
 * 
 * <p>传送门系统契约接口，定义地图传送点的类型、状态和进入行为规范，提供传送门的基本属性访问和状态管理方法。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义传送门的基础属性（类型、ID、位置、名称等）</li>
 *   <li>提供传送门状态管理（开启/关闭）</li>
 *   <li>处理玩家进入传送门的行为</li>
 *   <li>支持脚本化传送门功能</li>
 * </ul>
 * 
 * <p>包含以下传送门类型：</p>
 * <ul>
 *   <li>TELEPORT_PORTAL：传送类型传送门</li>
 *   <li>MAP_PORTAL：地图内传送门</li>
 *   <li>DOOR_PORTAL：门类型传送门</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public interface Portal {
    /** 传送类型传送门 */
    int TELEPORT_PORTAL = 1;
    /** 地图内传送门 */
    int MAP_PORTAL = 2;
    /** 门类型传送门 */
    int DOOR_PORTAL = 6;
    /** 传送门开启状态 */
    boolean OPEN = true;
    /** 传送门关闭状态 */
    boolean CLOSED = false;
    
    /**
     * 获取传送门类型
     * 
     * @return 传送门类型ID
     */
    int getType();
    
    /**
     * 获取传送门ID
     * 
     * @return 传送门唯一ID
     */
    int getId();
    
    /**
     * 获取传送门位置
     * 
     * @return 传送门在地图上的坐标位置
     */
    Point getPosition();
    
    /**
     * 获取传送门名称
     * 
     * @return 传送门名称字符串
     */
    String getName();
    
    /**
     * 获取传送门目标信息
     * 
     * @return 目标地图或目标传送门名称
     */
    String getTarget();
    
    /**
     * 获取关联脚本名称
     * 
     * @return 脚本名称
     */
    String getScriptName();
    
    /**
     * 设置关联脚本名称
     * 
     * @param newName 新脚本名称
     */
    void setScriptName(String newName);
    
    /**
     * 设置传送门状态
     * 
     * @param newStatus 新状态（开启/关闭）
     */
    void setPortalStatus(boolean newStatus);
    
    /**
     * 获取传送门状态
     * 
     * @return 当前状态（开启/关闭）
     */
    boolean getPortalStatus();
    
    /**
     * 获取目标地图ID
     * 
     * @return 目标地图ID
     */
    int getTargetMapId();
    
    /**
     * 进入传送门
     * 
     * <p>处理玩家进入传送门的行为，执行相应的传送逻辑。</p>
     * 
     * @param c 进入传送门的客户端
     */
    void enterPortal(Client c);
    
    /**
     * 设置传送门状态
     * 
     * @param state 新状态
     */
    void setPortalState(boolean state);
    
    /**
     * 获取传送门状态
     * 
     * @return 当前状态
     */
    boolean getPortalState();
}