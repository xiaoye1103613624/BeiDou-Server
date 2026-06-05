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
import org.gms.config.GameConfig;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.util.Pair;

import java.awt.*;
import java.util.Collection;

/**
 * 【类】Door（class），包 {@code org.gms.server.maps}。
 * 
 * <p>神秘之门系统，实现角色技能创建的双向传送门，连接城镇与狩猎地图。
 * 此类管理由玩家技能（如魔法密法师的"神秘之门"）创建的传送门，
 * 允许玩家在城镇和狩猎地图之间快速往返。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>创建双向传送门系统</li>
 *   <li>管理传送门的生命周期</li>
 *   <li>处理传送门的部署和移除</li>
 *   <li>控制传送门的激活状态</li>
 * </ul>
 */
public class Door {
    /** 传送门所有者ID */
    private int ownerId; 
    /** 城镇地图 */
    private MapleMap town; 
    /** 城镇传送点 */
    private Portal townPortal; 
    /** 目标地图（狩猎地图） */
    private final MapleMap target; 
    /** 位置状态（用于验证传送门位置的有效性） */
    private Pair<String, Integer> posStatus = null; 
    /** 部署时间戳 */
    private long deployTime; 
    /** 是否活跃状态 */
    private boolean active; 

    /** 城镇侧门对象 */
    private DoorObject townDoor; 
    /** 狩猎侧门对象 */
    private DoorObject areaDoor; 

    /**
     * 构造函数：创建神秘之门实例
     * 
     * @param owner 传送门创建者
     * @param targetPosition 传送门在目标地图的位置
     */
    public Door(Character owner, Point targetPosition) {
        this.ownerId = owner.getId();
        this.target = owner.getMap();

        if (target.canDeployDoor(targetPosition)) {
            if (GameConfig.getServerBoolean("use_enforce_mystic_door_position")) {
                posStatus = target.getDoorPositionStatus(targetPosition);
            }

            if (posStatus == null) {
                this.town = this.target.getReturnMap();
                this.townPortal = getTownDoorPortal(owner.getDoorSlot());
                this.deployTime = System.currentTimeMillis();
                this.active = true;

                if (townPortal != null) {
                    this.areaDoor = new DoorObject(ownerId, town, target, townPortal.getId(), targetPosition, townPortal.getPosition());
                    this.townDoor = new DoorObject(ownerId, target, town, -1, townPortal.getPosition(), targetPosition);

                    this.areaDoor.setPairOid(this.townDoor.getObjectId());
                    this.townDoor.setPairOid(this.areaDoor.getObjectId());
                } else {
                    this.ownerId = -1;
                }
            } else {
                this.ownerId = -3;
            }
        } else {
            this.ownerId = -2;
        }
    }

    /**
     * 更新传送门的城镇传送点
     * 
     * @param owner 传送门所有者
     */
    public void updateDoorPortal(Character owner) {
        int slot = owner.fetchDoorSlot();

        Portal nextTownPortal = getTownDoorPortal(slot);
        if (nextTownPortal != null) {
            townPortal = nextTownPortal;
            areaDoor.update(nextTownPortal.getId(), nextTownPortal.getPosition());
        }
    }

    /**
     * 广播移除传送门
     * 
     * @param owner 传送门所有者
     */
    private void broadcastRemoveDoor(Character owner) {
        DoorObject areaDoor = this.getAreaDoor();
        DoorObject townDoor = this.getTownDoor();

        MapleMap target = this.getTarget();
        MapleMap town = this.getTown();

        Collection<Character> targetChars = target.getCharacters();
        Collection<Character> townChars = town.getCharacters();

        target.removeMapObject(areaDoor);
        town.removeMapObject(townDoor);

        for (Character chr : targetChars) {
            areaDoor.sendDestroyData(chr.getClient());
            chr.removeVisibleMapObject(areaDoor);
        }

        for (Character chr : townChars) {
            townDoor.sendDestroyData(chr.getClient());
            chr.removeVisibleMapObject(townDoor);
        }

        owner.removePartyDoor(false);

        if (this.getTownPortal().getId() == 0x80) {
            for (Character chr : townChars) {
                Door door = chr.getMainTownDoor();
                if (door != null) {
                    townDoor.sendSpawnData(chr.getClient());
                    chr.addVisibleMapObject(townDoor);
                }
            }
        }
    }

    /**
     * 尝试移除传送门
     * 
     * @param owner 传送门所有者
     */
    public static void attemptRemoveDoor(final Character owner) {
        final Door destroyDoor = owner.getPlayerDoor();
        if (destroyDoor != null && destroyDoor.dispose()) {
            long effectTimeLeft = 3000 - destroyDoor.getElapsedDeployTime();   // portal deployment effect duration
            if (effectTimeLeft > 0) {
                MapleMap town = destroyDoor.getTown();

                OverallService service = (OverallService) town.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
                service.registerOverallAction(town.getId(), () -> {
                    destroyDoor.broadcastRemoveDoor(owner);   // thanks BHB88 for noticing doors crashing players when instantly cancelling buff
                }, effectTimeLeft);
            } else {
                destroyDoor.broadcastRemoveDoor(owner);
            }
        }
    }

    /**
     * 获取城镇传送点
     * 
     * @param doorid 传送点ID
     * @return 传送点对象
     */
    private Portal getTownDoorPortal(int doorid) {
        return town.getDoorPortal(doorid);
    }

    /**
     * 获取传送门所有者ID
     * 
     * @return 传送门所有者ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * 获取城镇侧门对象
     * 
     * @return 城镇侧门对象
     */
    public DoorObject getTownDoor() {
        return townDoor;
    }

    /**
     * 获取狩猎侧门对象
     * 
     * @return 狩猎侧门对象
     */
    public DoorObject getAreaDoor() {
        return areaDoor;
    }

    /**
     * 获取城镇地图
     * 
     * @return 城镇地图
     */
    public MapleMap getTown() {
        return town;
    }

    /**
     * 获取城镇传送点
     * 
     * @return 城镇传送点
     */
    public Portal getTownPortal() {
        return townPortal;
    }

    /**
     * 获取目标地图
     * 
     * @return 目标地图
     */
    public MapleMap getTarget() {
        return target;
    }

    /**
     * 获取传送门状态
     * 
     * @return 传送门状态
     */
    public Pair<String, Integer> getDoorStatus() {
        return posStatus;
    }

    /**
     * 获取已部署的时间
     * 
     * @return 已部署的时间（毫秒）
     */
    public long getElapsedDeployTime() {
        return System.currentTimeMillis() - deployTime;
    }

    /**
     * 标记传送门为非活跃状态
     * 
     * @return 如果传送门原本是活跃的则返回true，否则返回false
     */
    private boolean dispose() {
        if (active) {
            active = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查传送门是否活跃
     * 
     * @return 如果传送门活跃则返回true，否则返回false
     */
    public boolean isActive() {
        return active;
    }
}