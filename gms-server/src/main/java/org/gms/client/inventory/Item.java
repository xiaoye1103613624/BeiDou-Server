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
package org.gms.client.inventory;

import org.gms.client.inventory.manipulator.KarmaManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.server.ItemInformationProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 物品
 * 物品基类，封装物品ID、序列号、数量、位置、过期时间等基本信息
 * 支持物品比较（按类型和位置排序）、克隆和宠物关联
 */
public class Item implements Comparable<Item> {

    /** 现金物品ID自增计数器 */
    private static final AtomicInteger runningCashId = new AtomicInteger(777000000);

    /** 物品ID */
    private final int id;
    /** 现金物品ID */
    private int cashId;
    /** 序列号（数据库唯一标识） */
    private int sn;
    /** 物品在背包中的槽位 */
    private short position;
    /** 物品数量 */
    private short quantity;
    /** 关联宠物ID */
    private int petid = -1;
    /** 关联宠物对象 */
    private Pet pet = null;
    /** 物品所有者 */
    private String owner = "";
    /** 物品日志列表 */
    protected List<String> itemLog;
    /** 物品标记（封印、礼物等） */
    private short flag;
    /** 过期时间戳 */
    private long expiration = -1;
    /** 赠送者名称 */
    private String giftFrom = "";

    public Item(int id, short position, short quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.itemLog = new LinkedList<>();
        this.flag = 0;
    }

    public Item(int id, short position, short quantity, int petid) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        if (petid > -1) {   // issue with null "pet" having petid > -1 found thanks to MedicOP
            this.pet = Pet.loadFromDb(id, position, petid);
            if (this.pet == null) {
                petid = -1;
            }
        }
        this.petid = petid;
        this.flag = 0;
        this.itemLog = new LinkedList<>();
    }

    public Item copy() {
        Item ret = new Item(id, position, quantity, petid);
        ret.flag = flag;
        ret.owner = owner;
        ret.expiration = expiration;
        ret.itemLog = new LinkedList<>(itemLog);
        return ret;
    }

    public void setPosition(short position) {
        this.position = position;
        if (this.pet != null) {
            this.pet.setPosition(position);
        }
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public int getItemId() {
        return id;
    }

    public int getCashId() {
        if (cashId == 0) {
            cashId = runningCashId.getAndIncrement();
        }
        return cashId;
    }

    public short getPosition() {
        return position;
    }

    public short getQuantity() {
        return quantity;
    }

    public InventoryType getInventoryType() {
        return ItemConstants.getInventoryType(id);
    }

    public byte getItemType() { // 1: equip, 3: pet, 2: other
        if (getPetId() > -1) {
            return 3;
        }
        return 2;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPetId() {
        return petid;
    }

    @Override
    public int compareTo(Item other) {
        if (this.id < other.getItemId()) {
            return -1;
        } else if (this.id > other.getItemId()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Item: " + id + " quantity: " + quantity;
    }

    public List<String> getItemLog() {
        return Collections.unmodifiableList(itemLog);
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short b) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.isAccountRestricted(id)) {
            b |= ItemConstants.ACCOUNT_SHARING; // thanks Shinigami15 for noticing ACCOUNT_SHARING flag not being applied properly to items server-side
        }

        this.flag = b;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expire) {
        this.expiration = !ItemConstants.isPermanentItem(id) ? expire : ItemConstants.isPet(id) ? Long.MAX_VALUE : -1;
    }

    public int getSN() {
        return sn;
    }

    public void setSN(int sn) {
        this.sn = sn;
    }

    public String getGiftFrom() {
        return giftFrom;
    }

    public void setGiftFrom(String giftFrom) {
        this.giftFrom = giftFrom;
    }

    public Pet getPet() {
        return pet;
    }

    public boolean isUntradeable() {
        return ((this.getFlag() & ItemConstants.UNTRADEABLE) == ItemConstants.UNTRADEABLE) || (ItemInformationProvider.getInstance().isDropRestricted(this.getItemId()) && !KarmaManipulator.hasKarmaFlag(this));
    }
}