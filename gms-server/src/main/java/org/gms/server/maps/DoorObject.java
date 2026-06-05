/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
import org.gms.constants.id.MapId;
import org.gms.net.server.world.Party;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 【类型】DoorObject，class，包 {@code org.gms.server.maps}。
 *
 * <p>地图传送门对象，表示由技能（如魔法师的"神秘门"）创建的临时传送门，
 * 支持双向传送并仅对队长和队员可见。</p>
 * 
 * <p>DoorObject 实现了游戏中的技能传送门功能，允许玩家在指定位置之间进行传送。
 * 传送门具有所有者概念，只有传送门所有者及其队伍成员才能使用该传送门。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>表示由技能创建的临时传送门</li>
 *   <li>支持队伍成员之间的传送</li>
 *   <li>提供线程安全的操作</li>
 *   <li>管理传送门的显示和销毁</li>
 * </ul>
 *
 * @author Ronan
 */
public class DoorObject extends AbstractMapObject {
    /** 传送门所有者的ID */
    private final int ownerId;
    /** 配对的传送门对象ID */
    private int pairOid;

    /** 源地图（传送门创建位置） */
    private final MapleMap from;
    /** 目标地图（传送目的地） */
    private final MapleMap to;
    /** 连接的传送门ID */
    private int linkedPortalId;
    /** 连接的传送门位置 */
    private Point linkedPos;

    /** 读锁，用于线程安全访问 */
    private final Lock rlock;
    /** 写锁，用于线程安全修改 */
    private final Lock wlock;

    /**
     * 构造函数：创建传送门对象实例
     * 
     * @param owner 传送门所有者ID
     * @param destination 目标地图
     * @param origin 源地图
     * @param townPortalId 连接的传送门ID（-1表示城镇传送门）
     * @param targetPosition 传送门在源地图的位置
     * @param toPosition 传送门在目标地图的对应位置
     */
    public DoorObject(int owner, MapleMap destination, MapleMap origin, int townPortalId, Point targetPosition, Point toPosition) {
        super();
        setPosition(targetPosition);

        ownerId = owner;
        linkedPortalId = townPortalId;
        from = origin;
        to = destination;
        linkedPos = toPosition;

        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        this.rlock = lock.readLock();
        this.wlock = lock.writeLock();
    }

    /**
     * 更新传送门的目标信息
     * 
     * @param townPortalId 新的传送门ID
     * @param toPosition 新的传送门位置
     */
    public void update(int townPortalId, Point toPosition) {
        wlock.lock();
        try {
            linkedPortalId = townPortalId;
            linkedPos = toPosition;
        } finally {
            wlock.unlock();
        }
    }

    /**
     * 获取连接的传送门ID（私有方法）
     * 
     * @return 连接的传送门ID
     */
    private int getLinkedPortalId() {
        rlock.lock();
        try {
            return linkedPortalId;
        } finally {
            rlock.unlock();
        }
    }

    /**
     * 获取连接的传送门位置（私有方法）
     * 
     * @return 连接的传送门位置
     */
    private Point getLinkedPortalPosition() {
        rlock.lock();
        try {
            return linkedPos;
        } finally {
            rlock.unlock();
        }
    }

    /**
     * 将角色传送到传送门另一侧
     * 
     * <p>只有传送门所有者或其队伍成员才能使用传送门。
     * 如果传送门不在城镇且角色没有组队，则使用传送门ID传送；
     * 否则使用传送门位置传送。</p>
     * 
     * @param chr 要传送的角色
     */
    public void warp(final Character chr) {
        Party party = chr.getParty();
        if (chr.getId() == ownerId || (party != null && party.getMemberById(ownerId) != null)) {
            chr.sendPacket(PacketCreator.playPortalSound());

            if (!inTown() && party == null) {
                chr.changeMap(to, getLinkedPortalId());
            } else {
                chr.changeMap(to, getLinkedPortalPosition());
            }
        } else {
            chr.sendPacket(PacketCreator.blockedMessage(6));
            chr.sendPacket(PacketCreator.enableActions());
        }
    }

    @Override
    public void sendSpawnData(Client client) {
        sendSpawnData(client, true);
    }

    /**
     * 发送传送门生成数据给客户端
     * 
     * <p>向客户端发送传送门的生成数据包，使传送门在客户端上显示。
     * 只有当玩家在源地图且属于传送门所有者队伍时才会显示传送门。</p>
     * 
     * @param client 要发送数据的客户端
     * @param launched 是否已启动（用于门的动画效果）
     */
    public void sendSpawnData(Client client, boolean launched) {
        Character chr = client.getPlayer();
        if (this.getFrom().getId() == chr.getMapId()) {
            if (chr.getParty() != null && (this.getOwnerId() == chr.getId() || chr.getParty().getMemberById(this.getOwnerId()) != null)) {
                chr.sendPacket(PacketCreator.partyPortal(this.getFrom().getId(), this.getTo().getId(), this.toPosition()));
            }

            chr.sendPacket(PacketCreator.spawnPortal(this.getFrom().getId(), this.getTo().getId(), this.toPosition()));
            if (!this.inTown()) {
                chr.sendPacket(PacketCreator.spawnDoor(this.getOwnerId(), this.getPosition(), launched));
            }
        }
    }

    @Override
    public void sendDestroyData(Client client) {
        Character chr = client.getPlayer();
        if (from.getId() == chr.getMapId()) {
            Party party = chr.getParty();
            if (party != null && (ownerId == chr.getId() || party.getMemberById(ownerId) != null)) {
                client.sendPacket(PacketCreator.partyPortal(MapId.NONE, MapId.NONE, new Point(-1, -1)));
            }
            client.sendPacket(PacketCreator.removeDoor(ownerId, inTown()));
        }
    }

    /**
     * 发送传送门销毁数据给客户端
     * 
     * <p>向客户端发送传送门的销毁数据包，使传送门在客户端上消失。</p>
     * 
     * @param client 要发送数据的客户端
     * @param partyUpdate 是否需要更新队伍信息
     */
    public void sendDestroyData(Client client, boolean partyUpdate) {
        if (client != null && from.getId() == client.getPlayer().getMapId()) {
            client.sendPacket(PacketCreator.partyPortal(MapId.NONE, MapId.NONE, new Point(-1, -1)));
            client.sendPacket(PacketCreator.removeDoor(ownerId, inTown()));
        }
    }

    /**
     * 获取传送门所有者ID
     * 
     * @return 传送门所有者的角色ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * 设置配对的传送门对象ID
     * 
     * @param oid 配对的传送门对象ID
     */
    public void setPairOid(int oid) {
        this.pairOid = oid;
    }

    /**
     * 获取配对的传送门对象ID
     * 
     * @return 配对的传送门对象ID
     */
    public int getPairOid() {
        return pairOid;
    }

    /**
     * 检查传送门是否在城镇
     * 
     * @return 如果传送门在城镇则返回true，否则返回false
     */
    public boolean inTown() {
        return getLinkedPortalId() == -1;
    }

    /**
     * 获取源地图
     * 
     * @return 传送门创建所在的源地图
     */
    public MapleMap getFrom() {
        return from;
    }

    /**
     * 获取目标地图
     * 
     * @return 传送门传送的目标地图
     */
    public MapleMap getTo() {
        return to;
    }

    /**
     * 获取城镇地图
     * 
     * @return 如果传送门在城镇则返回源地图，否则返回目标地图
     */
    public MapleMap getTown() {
        return inTown() ? from : to;
    }

    /**
     * 获取区域地图
     * 
     * @return 如果传送门不在城镇则返回源地图，否则返回目标地图
     */
    public MapleMap getArea() {
        return !inTown() ? from : to;
    }

    /**
     * 获取区域位置
     * 
     * @return 如果传送门不在城镇则返回当前位置，否则返回连接的传送门位置
     */
    public Point getAreaPosition() {
        return !inTown() ? getPosition() : getLinkedPortalPosition();
    }

    /**
     * 获取传送门目标位置
     * 
     * @return 传送门在目标地图的对应位置
     */
    public Point toPosition() {
        return getLinkedPortalPosition();
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.DOOR;
    }
}