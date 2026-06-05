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
 * 【类型】MapItem（class），包 `org.gms.server.maps`。
 * 
 * <p>地图物品类，表示掉落在地图上的物品或金币。
 * 地图物品可以是玩家掉落的物品、怪物掉落的物品或金币，
 * 并具有所有权和拾取规则。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>表示地图上的掉落物品</li>
 *   <li>管理物品的所有权（个人或队伍）</li>
 *   <li>处理物品的拾取规则</li>
 *   <li>支持任务物品的特殊处理</li>
 * </ul>
 */
public class MapItem extends AbstractMapObject {
    /** 物品所有者的客户端 */
    protected Client ownerClient;
    /** 物品对象（如果是金币则为null） */
    protected Item item;
    /** 掉落此物品的对象 */
    protected MapObject dropper;
    /** 物品所有者的角色ID */
    protected int character_ownerid, 
    /** 物品所有者的队伍ID */
    party_ownerid, 
    /** 金币数量（如果不是金币则为0） */
    meso, 
    /** 任务ID（如果是任务物品） */
    questid = -1;
    /** 掉落类型 */
    protected byte type;
    /** 是否已被拾取 */
    protected boolean pickedUp = false, 
    /** 是否为玩家主动掉落 */
    playerDrop, 
    /** 是否为队伍掉落 */
    partyDrop;
    /** 掉落时间戳 */
    protected long dropTime;
    /** 物品操作锁，用于线程安全 */
    private final Lock itemLock = new ReentrantLock();

    /**
     * 构造函数：创建地图物品实例（普通物品）
     * 
     * @param item 物品对象
     * @param position 物品在地图上的位置
     * @param dropper 掉落此物品的对象
     * @param owner 物品所有者角色
     * @param ownerClient 物品所有者客户端
     * @param type 掉落类型
     * @param playerDrop 是否为玩家主动掉落
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

    /**
     * 构造函数：创建地图物品实例（任务物品）
     * 
     * @param item 物品对象
     * @param position 物品在地图上的位置
     * @param dropper 掉落此物品的对象
     * @param owner 物品所有者角色
     * @param ownerClient 物品所有者客户端
     * @param type 掉落类型
     * @param playerDrop 是否为玩家主动掉落
     * @param questid 任务ID（如果是任务物品）
     */
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
     * 构造函数：创建地图物品实例（金币）
     * 
     * @param meso 金币数量
     * @param position 金币在地图上的位置
     * @param dropper 掉落此金币的对象
     * @param owner 金币所有者角色
     * @param ownerClient 金币所有者客户端
     * @param type 掉落类型
     * @param playerDrop 是否为玩家主动掉落
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

    /**
     * 获取物品对象
     * 
     * @return 物品对象，如果是金币则返回null
     */
    public final Item getItem() {
        return item;
    }

    /**
     * 获取任务ID
     * 
     * @return 任务ID，如果不是任务物品则返回-1
     */
    public final int getQuest() {
        return questid;
    }

    /**
     * 获取物品ID
     * 
     * <p>如果是金币，则返回金币数量；否则返回物品ID。</p>
     * 
     * @return 物品ID或金币数量
     */
    public final int getItemId() {
        if (meso > 0) {
            return meso;
        }
        return item.getItemId();
    }

    /**
     * 获取掉落者对象
     * 
     * @return 掉落此物品的对象
     */
    public final MapObject getDropper() {
        return dropper;
    }

    /**
     * 获取所有者角色ID
     * 
     * @return 物品所有者的角色ID
     */
    public final int getOwnerId() {
        return character_ownerid;
    }

    /**
     * 获取所有者队伍ID
     * 
     * @return 物品所有者的队伍ID
     */
    public final int getPartyOwnerId() {
        return party_ownerid;
    }

    /**
     * 设置所有者队伍ID
     * 
     * @param partyid 要设置的队伍ID
     */
    public final void setPartyOwnerId(int partyid) {
        party_ownerid = partyid;
    }

    /**
     * 获取客户端可见的所有者ID
     * 
     * <p>如果是队伍掉落，则返回队伍ID；否则返回个人所有者ID。</p>
     * 这个方法用于解决收集队伍物品时的所有权问题。
     * 
     * @return 客户端可见的所有者ID
     */
    public final int getClientsideOwnerId() {
        if (this.party_ownerid == -1) {
            return this.character_ownerid;
        } else {
            return this.party_ownerid;
        }
    }

    /**
     * 检查玩家是否拥有此物品的所有权
     * 
     * <p>如果玩家是物品所有者，或者是所有者队伍的成员，
     * 或者所有权时间已过期，则玩家拥有所有权。</p>
     * 
     * @param player 要检查的玩家
     * @return 如果玩家拥有所有权则返回true，否则返回false
     */
    public final boolean hasClientsideOwnership(Character player) {
        return this.character_ownerid == player.getId() || this.party_ownerid == player.getPartyId() || hasExpiredOwnershipTime();
    }

    /**
     * 检查是否为自由拾取掉落
     * 
     * <p>自由拾取掉落意味着任何玩家都可以拾取，不受所有权限制。
     * 如果掉落类型为2或3，或者所有权时间已过期，则为自由拾取。</p>
     * 
     * @return 如果是自由拾取掉落则返回true，否则返回false
     */
    public final boolean isFFADrop() {
        return type == 2 || type == 3 || hasExpiredOwnershipTime();
    }

    /**
     * 检查所有权时间是否已过期
     * 
     * <p>所有权过期时间为15秒，过期后任何玩家都可以拾取。</p>
     * 
     * @return 如果所有权时间已过期则返回true，否则返回false
     */
    public final boolean hasExpiredOwnershipTime() {
        return System.currentTimeMillis() - dropTime >= SECONDS.toMillis(15);
    }

    /**
     * 检查指定角色是否可以拾取此物品
     * 
     * <p>根据所有权规则判断角色是否可以拾取此物品：</p>
     * <ul>
     *   <li>如果没有所有者或已经自由拾取，则任何人都可以拾取</li>
     *   <li>如果是个人掉落，只有所有者或所有者的队伍成员可以拾取</li>
     *   <li>如果是队伍掉落，只有队伍成员可以拾取</li>
     *   <li>所有权过期后，任何人都可以拾取</li>
     * </ul>
     * 
     * @param chr 要检查的角色
     * @return 如果角色可以拾取则返回true，否则返回false
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

    /**
     * 获取所有者客户端
     * 
     * <p>只有当所有者客户端在线且没有离开游戏时才返回客户端，
     * 否则返回null。</p>
     * 
     * @return 所有者客户端，如果不在线则返回null
     */
    public final Client getOwnerClient() {
        return (ownerClient.isLoggedIn() && !ownerClient.getPlayer().isAwayFromWorld()) ? ownerClient : null;
    }

    /**
     * 获取金币数量
     * 
     * @return 金币数量，如果不是金币则返回0
     */
    public final int getMeso() {
        return meso;
    }

    /**
     * 检查是否为玩家主动掉落
     * 
     * @return 如果是玩家主动掉落则返回true，否则返回false
     */
    public final boolean isPlayerDrop() {
        return playerDrop;
    }

    /**
     * 检查是否已被拾取
     * 
     * @return 如果已被拾取则返回true，否则返回false
     */
    public final boolean isPickedUp() {
        return pickedUp;
    }

    /**
     * 设置是否已被拾取
     * 
     * @param pickedUp 要设置的拾取状态
     */
    public void setPickedUp(final boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    /**
     * 获取掉落时间
     * 
     * @return 掉落时间戳
     */
    public long getDropTime() {
        return dropTime;
    }

    /**
     * 设置掉落时间
     * 
     * @param time 掉落时间戳
     */
    public void setDropTime(long time) {
        this.dropTime = time;
    }

    /**
     * 获取掉落类型
     * 
     * @return 掉落类型
     */
    public byte getDropType() {
        return type;
    }

    /**
     * 锁定物品，用于线程安全
     * 
     * <p>获取物品操作锁，防止多线程竞争。</p>
     */
    public void lockItem() {
        itemLock.lock();
    }

    /**
     * 解锁物品，用于线程安全
     * 
     * <p>释放物品操作锁。</p>
     */
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