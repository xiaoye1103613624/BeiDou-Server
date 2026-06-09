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
 * 神秘之门
 * 管理神秘之门（Mystic Door）的创建、更新和销毁，由一对DoorObject组成（城镇门和野外门），
 * 支持玩家和同队成员在地图间传送
 *
 * @author Matze
 * @author Ronan
 */
public class Door {
    /** 门的所有者ID */
    private int ownerId;
    /** 城镇侧地图 */
    private MapleMap town;
    /** 城镇侧传送门 */
    private Portal townPortal;
    /** 野外侧地图 */
    private final MapleMap target;
    /** 门的位置状态（用于位置校验） */
    private Pair<String, Integer> posStatus = null;
    /** 门的部署时间 */
    private long deployTime;
    /** 门是否激活 */
    private boolean active;

    /** 城镇侧门对象 */
    private DoorObject townDoor;
    /** 野外侧门对象 */
    private DoorObject areaDoor;

    /**
     * 构造方法，创建神秘之门
     * 检查目标位置是否可部署，分配城镇传送门，创建一对DoorObject
     *
     * @param owner          门的所有者
     * @param targetPosition 目标位置
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
     * 更新门的传送门（当玩家切换门槽位时）
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
     * 广播移除门，从两个地图上移除门对象并通知所有玩家
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

        // 如果当前传送门是共享传送门，需要重新显示其他玩家的门
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
     * 尝试移除门，考虑门部署效果动画的持续时间
     */
    public static void attemptRemoveDoor(final Character owner) {
        final Door destroyDoor = owner.getPlayerDoor();
        if (destroyDoor != null && destroyDoor.dispose()) {
            // portal deployment effect duration
            long effectTimeLeft = 3000 - destroyDoor.getElapsedDeployTime();
            if (effectTimeLeft > 0) {
                MapleMap town = destroyDoor.getTown();

                OverallService service = (OverallService) town.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
                service.registerOverallAction(town.getId(), () -> {
                    // thanks BHB88 for noticing doors crashing players when instantly cancelling buff
                    destroyDoor.broadcastRemoveDoor(owner);
                }, effectTimeLeft);
            } else {
                destroyDoor.broadcastRemoveDoor(owner);
            }
        }
    }

    /**
     * 获取城镇门传送门
     */
    private Portal getTownDoorPortal(int doorid) {
        return town.getDoorPortal(doorid);
    }

    public int getOwnerId() {
        return ownerId;
    }

    public DoorObject getTownDoor() {
        return townDoor;
    }

    public DoorObject getAreaDoor() {
        return areaDoor;
    }

    public MapleMap getTown() {
        return town;
    }

    public Portal getTownPortal() {
        return townPortal;
    }

    public MapleMap getTarget() {
        return target;
    }

    public Pair<String, Integer> getDoorStatus() {
        return posStatus;
    }

    /**
     * 获取门已部署的时长（毫秒）
     */
    public long getElapsedDeployTime() {
        return System.currentTimeMillis() - deployTime;
    }

    /**
     * 销毁门，标记为不活跃，返回是否成功销毁
     */
    private boolean dispose() {
        if (active) {
            active = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isActive() {
        return active;
    }
}