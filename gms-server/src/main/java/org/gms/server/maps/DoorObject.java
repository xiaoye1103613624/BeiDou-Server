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
 * 门对象
 * 表示神秘之门（Mystic Door）的地图对象，为一对门（城镇门和野外门），
 * 支持玩家和同队成员在地图间传送，使用读写锁保证线程安全
 *
 * @author Ronan
 */
public class DoorObject extends AbstractMapObject {
    /** 门的所有者ID */
    private final int ownerId;
    /** 配对门的对象ID */
    private int pairOid;

    /** 门所在的地图（来源） */
    private final MapleMap from;
    /** 门指向的地图（目标） */
    private final MapleMap to;
    /** 关联传送门ID */
    private int linkedPortalId;
    /** 关联传送门位置 */
    private Point linkedPos;

    /** 读锁，保证线程安全读取 */
    private final Lock rlock;
    /** 写锁，保证线程安全写入 */
    private final Lock wlock;

    /**
     * 构造方法，创建门对象并初始化读写锁
     *
     * @param owner          门的所有者ID
     * @param destination    目标地图
     * @param origin         来源地图
     * @param townPortalId   城镇传送门ID
     * @param targetPosition 目标位置
     * @param toPosition     到达位置
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
     * 更新门的传送门关联信息（写锁保护）
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
     * 获取关联传送门ID（读锁保护）
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
     * 获取关联传送门位置（读锁保护）
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
     * 传送玩家到门的另一侧
     * 只有门的所有者或同队成员可以使用
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
     * 发送门生成数据到客户端
     *
     * @param client   客户端
     * @param launched 是否已启动
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
     * 发送门销毁数据到客户端，并更新队伍传送门状态
     */
    public void sendDestroyData(Client client, boolean partyUpdate) {
        if (client != null && from.getId() == client.getPlayer().getMapId()) {
            client.sendPacket(PacketCreator.partyPortal(MapId.NONE, MapId.NONE, new Point(-1, -1)));
            client.sendPacket(PacketCreator.removeDoor(ownerId, inTown()));
        }
    }

    /**
     * 获取门的所有者ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * 设置配对门的对象ID
     */
    public void setPairOid(int oid) {
        this.pairOid = oid;
    }

    /**
     * 获取配对门的对象ID
     */
    public int getPairOid() {
        return pairOid;
    }

    /**
     * 判断门是否在城镇中（linkedPortalId为-1表示城镇）
     */
    public boolean inTown() {
        return getLinkedPortalId() == -1;
    }

    /**
     * 获取门所在的地图（来源）
     */
    public MapleMap getFrom() {
        return from;
    }

    /**
     * 获取门指向的地图（目标）
     */
    public MapleMap getTo() {
        return to;
    }

    /**
     * 获取城镇侧的地图
     */
    public MapleMap getTown() {
        return inTown() ? from : to;
    }

    /**
     * 获取野外侧的地图
     */
    public MapleMap getArea() {
        return !inTown() ? from : to;
    }

    /**
     * 获取野外侧的位置
     */
    public Point getAreaPosition() {
        return !inTown() ? getPosition() : getLinkedPortalPosition();
    }

    /**
     * 获取目标位置
     */
    public Point toPosition() {
        return getLinkedPortalPosition();
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.DOOR;
    }
}