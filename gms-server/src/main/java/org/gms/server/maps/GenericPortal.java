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
 * 通用传送门
 * 实现Portal接口的通用传送门类，支持脚本传送和直接地图传送，
 * 使用ReentrantLock保证脚本执行的线程安全
 */
public class GenericPortal implements Portal {
    /** 传送门名称 */
    private String name;
    /** 目标传送门名称 */
    private String target;
    /** 传送门位置 */
    private Point position;
    /** 目标地图ID */
    private int targetmap;
    /** 传送门类型 */
    private final int type;
    /** 传送门状态（开启/关闭） */
    private boolean status = true;
    /** 传送门ID */
    private int id;
    /** 关联的脚本名称 */
    private String scriptName;
    /** 传送门状态（用于脚本控制） */
    private boolean portalState;
    /** 脚本执行锁，保证同一传送门脚本不会被并发执行 */
    private Lock scriptLock = null;

    /**
     * 构造方法
     *
     * @param type 传送门类型
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
     * @param id 传送门ID
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
     * @param name 传送门名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置传送门位置
     *
     * @param position 传送门位置
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * 设置目标传送门名称
     *
     * @param target 目标传送门名称
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 设置目标地图ID
     *
     * @param targetmapid 目标地图ID
     */
    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    /**
     * 设置脚本名称，若不为null则初始化脚本锁
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
     * 玩家进入传送门
     * 优先执行关联脚本，若脚本不存在则直接传送到目标地图
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
                if (pto == null) {
                    // fallback for missing portals - no real life case anymore - interesting for not implemented areas
                    pto = to.getPortal(0);
                }
                chr.dropMessage(5, "[DEBUG] 传送: 目标地图=" + getTargetMapId() + " 目标门=" + getTarget() + " -> 实际门=" + (pto != null ? pto.getName() : "null") + " 位置=(" + pto.getPosition().x + "," + pto.getPosition().y + ")");
                // late resolving makes this harder but prevents us from loading the whole world at once
                chr.changeMap(to, pto);
                changed = true;
            } else {
                chr.dropMessage(5, "You cannot enter this map with the chalkboard opened.");
            }
        }
        if (!changed) {
            c.sendPacket(PacketCreator.enableActions());
        }
    }

    @Override
    public void setPortalState(boolean state) {
        this.portalState = state;
    }

    @Override
    public boolean getPortalState() {
        return portalState;
    }
}