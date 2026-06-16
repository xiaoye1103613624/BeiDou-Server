/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

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
import org.gms.client.inventory.Item;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 地图物品
 * 表示地图上的掉落物品或金币，管理物品的归属权、拾取条件和过期时间
 * 使用{@link ReentrantLock}保证拾取操作的线程安全
 */
public class MapItem extends AbstractMapObject {
    /** 掉落者客户端 */
    protected Client ownerClient;
    /** 物品对象 */
    protected Item item;
    /** 掉落者 */
    protected MapObject dropper;
    /** 物品所有者角色ID */
    protected int character_ownerid;
    /** 物品所有者队伍ID */
    protected int party_ownerid;
    /** 金币数量（>0表示金币） */
    protected int meso;
    /** 关联任务ID */
    protected int questid = -1;
    /** 掉落类型 */
    protected byte type;
    /** 是否已被拾取 */
    protected boolean pickedUp = false;
    /** 是否玩家丢弃 */
    protected boolean playerDrop;
    /** 是否队伍掉落 */
    protected boolean partyDrop;
    /** 掉落时间戳 */
    protected long dropTime;
    /** 物品锁，保证拾取操作的线程安全 */
    private final Lock itemLock = new ReentrantLock();

    /**
     * 构造物品掉落（无任务关联）
     *
     * @param item        物品
     * @param position    掉落位置
     * @param dropper     掉落者
     * @param owner       所有者
     * @param ownerClient 所有者客户端
     * @param type        掉落类型
     * @param playerDrop  是否玩家丢弃
     */
    public MapItem(Item item, Point position, MapObject dropper, Character owner, Client ownerClient, byte type, boolean playerDrop) {
        setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.party_ownerid = owner.getPartyId();
        this.partyDrop = this.party_ownerid != -1;
        this.ownerClient = owner.getClient();
        this.meso = 0;
        this.type = type;
        this.playerDrop = playerDrop;
    }

    public MapItem(Item item, Point position, MapObject dropper, Character owner, Client ownerClient, byte type, boolean playerDrop, int questid) {
        setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.party_ownerid = owner.getPartyId();
        this.partyDrop = this.party_ownerid != -1;
        this.ownerClient = owner.getClient();
        this.meso = 0;
        this.type = type;
        this.playerDrop = playerDrop;
        this.questid = questid;
    }

    /**
     * 构造金币掉落
     *
     * @param meso        金币数量
     * @param position    掉落位置
     * @param dropper     掉落者
     * @param owner       所有者
     * @param ownerClient 所有者客户端
     * @param type        掉落类型
     * @param playerDrop  是否玩家丢弃
     */
    public MapItem(int meso, Point position, MapObject dropper, Character owner, Client ownerClient, byte type, boolean playerDrop) {
        setPosition(position);
        this.item = null;
        this.dropper = dropper;
        this.character_ownerid = owner.getId();
        this.party_ownerid = owner.getPartyId();
        this.partyDrop = this.party_ownerid != -1;
        this.ownerClient = owner.getClient();
        this.meso = meso;
        this.type = type;
        this.playerDrop = playerDrop;
    }

    public final Item getItem() {
        return item;
    }

    public final int getQuest() {
        return questid;
    }

    public final int getItemId() {
        if (meso > 0) {
            return meso;
        }
        return item.getItemId();
    }

    public final MapObject getDropper() {
        return dropper;
    }

    public final int getOwnerId() {
        return character_ownerid;
    }

    public final int getPartyOwnerId() {
        return party_ownerid;
    }

    public final void setPartyOwnerId(int partyid) {
        party_ownerid = partyid;
    }

    public final int getClientsideOwnerId() {
        // thanks nozphex (RedHat) for noting an issue with collecting party items
        if (this.party_ownerid == -1) {
            return this.character_ownerid;
        } else {
            return this.party_ownerid;
        }
    }

    /**
     * 检查玩家是否拥有客户端显示所有权
     * 满足以下任一条件即有所有权：角色ID匹配、队伍ID匹配、或物品已过期
     *
     * @param player 玩家
     * @return true表示有所有权
     */
    public final boolean hasClientsideOwnership(Character player) {
        return this.character_ownerid == player.getId() || this.party_ownerid == player.getPartyId() || hasExpiredOwnershipTime();
    }

    public final boolean isFFADrop() {
        return type == 2 || type == 3 || hasExpiredOwnershipTime();
    }

    /** 是否为独立掉落物品（远征BOSS每人独立一份时，初始120秒仅归属者可拾取） */
    private boolean independentDrop = false;

    public final boolean hasExpiredOwnershipTime() {
        long expireSec = independentDrop ? 120 : 15;
        return System.currentTimeMillis() - dropTime >= SECONDS.toMillis(expireSec);
    }

    public void setIndependentDrop(boolean independentDrop) {
        this.independentDrop = independentDrop;
    }

    /**
     * 检查物品是否可被指定玩家拾取
     * 拾取规则：无所有者、FFA掉落、角色ID匹配、队伍ID匹配、或已过期
     * 若同队拾取，则自动将物品标记为队伍掉落
     *
     * @param chr 玩家
     * @return true表示可拾取
     */
    public final boolean canBePickedBy(Character chr) {
        if (character_ownerid <= 0 || isFFADrop()) {
            return true;
        }

        if (party_ownerid == -1) {
            if (chr.getId() == character_ownerid) {
                return true;
            } else if (chr.isPartyMember(character_ownerid)) {
                party_ownerid = chr.getPartyId();
                return true;
            }
        } else {
            if (chr.getPartyId() == party_ownerid) {
                return true;
            } else if (chr.getId() == character_ownerid) {
                party_ownerid = chr.getPartyId();
                return true;
            }
        }

        return hasExpiredOwnershipTime();
    }

    public final Client getOwnerClient() {
        return (ownerClient.isLoggedIn() && !ownerClient.getPlayer().isAwayFromWorld()) ? ownerClient : null;
    }

    public final int getMeso() {
        return meso;
    }

    public final boolean isPlayerDrop() {
        return playerDrop;
    }

    public final boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(final boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public long getDropTime() {
        return dropTime;
    }

    public void setDropTime(long time) {
        this.dropTime = time;
    }

    public byte getDropType() {
        return type;
    }

    public void lockItem() {
        itemLock.lock();
    }

    public void unlockItem() {
        itemLock.unlock();
    }

    @Override
    public final MapObjectType getType() {
        return MapObjectType.ITEM;
    }

    @Override
    public void sendSpawnData(final Client client) {
        Character chr = client.getPlayer();

        if (chr.needQuestItem(questid, getItemId())) {
            this.lockItem();
            try {
                client.sendPacket(PacketCreator.dropItemFromMapObject(chr, this, null, getPosition(), (byte) 2));
            } finally {
                this.unlockItem();
            }
        }
    }

    @Override
    public void sendDestroyData(final Client client) {
        client.sendPacket(PacketCreator.removeItemFromMap(getObjectId(), 1, 0));
    }
}