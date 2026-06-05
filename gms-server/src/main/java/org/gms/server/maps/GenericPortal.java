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
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.MapId;
import org.gms.scripting.portal.PortalScriptManager;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 【类型】GenericPortal（class），包 `org.gms.server.maps`。
 * 
 * <p>通用传送门类，实现了Portal接口，是游戏中各种传送门的基础实现。
 * 传送门是连接不同地图的通道，玩家可以通过传送门从一个地图移动到另一个地图。
 * 支持脚本执行和状态管理。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>存储传送门的基本信息（名称、位置、目标地图等）</li>
 *   <li>管理传送门的开启/关闭状态</li>
 *   <li>执行传送门相关的脚本</li>
 *   <li>处理玩家进入传送门的逻辑</li>
 * </ul>
 */
public class GenericPortal implements Portal {
    /** 传送门名称 */
    private String name;
    /** 目标传送门名称 */
    private String target;
    /** 传送门位置坐标 */
    private Point position;
    /** 目标地图ID */
    private int targetmap;
    /** 传送门类型 */
    private final int type;
    /** 传送门状态（true为开启，false为关闭） */
    private boolean status = true;
    /** 传送门ID */
    private int id;
    /** 关联的脚本名称 */
    private String scriptName;
    /** 传送门状态标志 */
    private boolean portalState;
    /** 脚本执行锁，用于多线程安全 */
    private Lock scriptLock = null;

    /**
     * 构造函数：创建通用传送门实例
     * 
     * @param type 传送门类型（由Portal常量定义）
     */
    public GenericPortal(int type) {
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * 设置传送门ID
     * 
     * @param id 传送门的唯一标识符
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setPortalStatus(boolean newStatus) {
        this.status = newStatus;
    }

    @Override
    public boolean getPortalStatus() {
        return status;
    }

    @Override
    public int getTargetMapId() {
        return targetmap;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    /**
     * 设置传送门名称
     * 
     * @param name 传送门的名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置传送门位置
     * 
     * @param position 传送门在地图上的位置坐标
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * 设置目标传送门名称
     * 
     * @param target 目标传送门的名称
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 设置目标地图ID
     * 
     * @param targetmapid 目标地图的ID
     */
    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    /**
     * 设置关联的脚本名称
     * 
     * <p>设置与传送门关联的脚本名称，当有脚本名称时会初始化脚本锁，
     * 以确保脚本执行时的线程安全；当脚本名称为空时，清除脚本锁。</p>
     * 
     * @param scriptName 要关联的脚本名称，null表示不关联脚本
     */
    @Override
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;

        if (scriptName != null) {
            if (scriptLock == null) {
                scriptLock = new ReentrantLock(true);
            }
        } else {
            scriptLock = null;
        }
    }

    /**
     * 处理玩家进入传送门的逻辑
     * 
     * <p>当玩家进入传送门时执行此方法，根据传送门的配置执行相应的操作。
     * 如果传送门有关联的脚本，则执行脚本；否则，将玩家传送到目标地图。</p>
     * 
     * <p>处理流程：</p>
     * <ol>
     *   <li>如果有脚本名称，执行关联的脚本</li>
     *   <li>如果没有脚本但有目标地图ID，将玩家传送到目标地图</li>
     *   <li>如果以上都不满足，发送动作启用包</li>
     * </ol>
     * 
     * @param c 进入传送门的客户端
     */
    @Override
    public void enterPortal(Client c) {
        boolean changed = false;
        if (getScriptName() != null) {
            try {
                scriptLock.lock();
                try {
                    changed = PortalScriptManager.getInstance().executePortalScript(this, c);
                } finally {
                    scriptLock.unlock();
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        } else if (getTargetMapId() != MapId.NONE) {
            Character chr = c.getPlayer();
            if (!(chr.getChalkboard() != null && GameConstants.isFreeMarketRoom(getTargetMapId()))) {
                MapleMap to = chr.getEventInstance() == null ? c.getChannelServer().getMapFactory().getMap(getTargetMapId()) : chr.getEventInstance().getMapInstance(getTargetMapId());
                Portal pto = to.getPortal(getTarget());
                if (pto == null) {// fallback for missing portals - no real life case anymore - interesting for not implemented areas
                    pto = to.getPortal(0);
                }
                chr.changeMap(to, pto); //late resolving makes this harder but prevents us from loading the whole world at once
                changed = true;
            } else {
                chr.dropMessage(5, "You cannot enter this map with the chalkboard opened.");
            }
        }
        if (!changed) {
            c.sendPacket(PacketCreator.enableActions());
        }
    }

    /**
     * 设置传送门状态
     * 
     * @param state 传送门的新状态（true为开启，false为关闭）
     */
    @Override
    public void setPortalState(boolean state) {
        this.portalState = state;
    }

    /**
     * 获取传送门状态
     * 
     * @return 传送门当前状态（true为开启，false为关闭）
     */
    @Override
    public boolean getPortalState() {
        return portalState;
    }
}