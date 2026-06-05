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
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.inventory.manipulator.KarmaManipulator;
import org.gms.net.packet.Packet;
import org.gms.server.Trade;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 【类型】PlayerShop，class，包 {@code org.gms.server.maps}。
 *
 * <p>玩家个人商店地图对象，提供玩家间实时点对点交易功能。支持访客进入/离开、物品上架/下架、购买、聊天记录、黑名单和交易记录查询。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>创建和管理玩家个人商店</li>
 *   <li>处理访客进入和离开</li>
 *   <li>管理商品上架和销售</li>
 *   <li>维护商店聊天记录</li>
 *   <li>处理黑名单功能</li>
 *   <li>保护并发访问安全</li>
 * </ul>
 *
 * @author Matze
 * @author Ronan - concurrency protection
 */
public class PlayerShop extends AbstractMapObject {
    /** 商店开放状态标志 */
    private final AtomicBoolean open = new AtomicBoolean(false);
    /** 商店所有者 */
    private final Character owner;
    /** 商店道具ID */
    private final int itemid;

    /** 访客数组（最多3个） */
    private final Character[] visitors = new Character[3];
    /** 商店商品列表 */
    private final List<PlayerShopItem> items = new ArrayList<>();
    /** 已售商品列表 */
    private final List<SoldItem> sold = new LinkedList<>();
    /** 商店描述 */
    private String description;
    /** 已售出商品数量 */
    private int boughtnumber = 0;
    /** 黑名单列表 */
    private final List<String> bannedList = new ArrayList<>();
    /** 聊天记录列表 */
    private final List<Pair<Character, String>> chatLog = new LinkedList<>();
    /** 聊天槽位映射 */
    private final Map<Integer, Byte> chatSlot = new LinkedHashMap<>();
    /** 访客操作锁 */
    private final Lock visitorLock = new ReentrantLock(true);

    /**
     * 构造函数：创建玩家商店实例
     * 
     * @param owner 商店所有者
     * @param description 商店描述
     * @param itemid 商店道具ID
     */
    public PlayerShop(Character owner, String description, int itemid) {
        this.setPosition(owner.getPosition());
        this.owner = owner;
        this.description = description;
        this.itemid = itemid;
    }

    /**
     * 获取频道号
     * 
     * @return 频道号
     */
    public int getChannel() {
        return owner.getClient().getChannel();
    }

    /**
     * 获取地图ID
     * 
     * @return 地图ID
     */
    public int getMapId() {
        return owner.getMapId();
    }

    /**
     * 获取商店道具ID
     * 
     * @return 商店道具ID
     */
    public int getItemId() {
        return itemid;
    }

    /**
     * 检查商店是否开放
     * 
     * @return 如果商店开放则返回true，否则返回false
     */
    public boolean isOpen() {
        return open.get();
    }

    /**
     * 设置商店开放状态
     * 
     * @param openShop 开放状态
     */
    public void setOpen(boolean openShop) {
        open.set(openShop);
    }

    /**
     * 检查是否有空闲槽位
     * 
     * @return 如果有空闲槽位则返回true，否则返回false
     */
    public boolean hasFreeSlot() {
        visitorLock.lock();
        try {
            return visitors[0] == null || visitors[1] == null || visitors[2] == null;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 获取商店房间信息
     * 
     * @return 商店房间信息字节数组
     */
    public byte[] getShopRoomInfo() {
        visitorLock.lock();
        try {
            byte count = 0;
            //if (this.isOpen()) {
            for (Character visitor : visitors) {
                if (visitor != null) {
                    count++;
                }
            }
            //} else {  shouldn't happen since there isn't a "closed" state for player shops.
            //    count = (byte) (visitors.length + 1);
            //}

            return new byte[]{count, (byte) visitors.length};
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 检查是否为商店所有者
     * 
     * @param chr 要检查的角色
     * @return 如果是所有者则返回true，否则返回false
     */
    public boolean isOwner(Character chr) {
        return owner.equals(chr);
    }

    /**
     * 添加访客
     * 
     * @param visitor 要添加的访客
     */
    private void addVisitor(Character visitor) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == null) {
                visitors[i] = visitor;
                visitor.setSlot(i);

                this.broadcast(PacketCreator.getPlayerShopNewVisitor(visitor, i + 1));
                owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
                break;
            }
        }
    }

    /**
     * 强制移除访客
     * 
     * @param visitor 要移除的访客
     */
    public void forceRemoveVisitor(Character visitor) {
        if (visitor == owner) {
            owner.getMap().removeMapObject(this);
            owner.setPlayerShop(null);
        }

        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                    visitors[i].setPlayerShop(null);
                    visitors[i] = null;
                    visitor.setSlot(-1);

                    this.broadcast(PacketCreator.getPlayerShopRemoveVisitor(i + 1));
                    owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
                    return;
                }
            }
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 移除访客
     * 
     * @param visitor 要移除的访客
     */
    public void removeVisitor(Character visitor) {
        if (visitor == owner) {
            owner.getMap().removeMapObject(this);
            owner.setPlayerShop(null);
        } else {
            visitorLock.lock();
            try {
                for (int i = 0; i < 3; i++) {
                    if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                        visitor.setSlot(-1);    //absolutely cant remove player slot for late players without dc'ing them... heh

                        for (int j = i; j < 2; j++) {
                            if (visitors[j] != null) {
                                owner.sendPacket(PacketCreator.getPlayerShopRemoveVisitor(j + 1));
                            }
                            visitors[j] = visitors[j + 1];
                            if (visitors[j] != null) {
                                visitors[j].setSlot(j);
                            }
                        }
                        visitors[2] = null;
                        for (int j = i; j < 2; j++) {
                            if (visitors[j] != null) {
                                owner.sendPacket(PacketCreator.getPlayerShopNewVisitor(visitors[j], j + 1));
                            }
                        }

                        this.broadcastRestoreToVisitors();
                        owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
                        return;
                    }
                }
            } finally {
                visitorLock.unlock();
            }

            owner.getMap().broadcastMessage(PacketCreator.updatePlayerShopBox(this));
        }
    }

    /**
     * 检查是否为商店访客
     * 
     * @param visitor 要检查的访客
     * @return 如果是访客则返回true，否则返回false
     */
    public boolean isVisitor(Character visitor) {
        visitorLock.lock();
        try {
            return visitors[0] == visitor || visitors[1] == visitor || visitors[2] == visitor;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 添加商品到商店
     * 
     * @param item 要添加的商品
     * @return 如果添加成功则返回true，否则返回false
     */
    public boolean addItem(PlayerShopItem item) {
        synchronized (items) {
            if (items.size() >= 16) {
                return false;
            }

            items.add(item);
            return true;
        }
    }

    /**
     * 从槽位移除商品
     * 
     * @param slot 要移除的槽位
     */
    private void removeFromSlot(int slot) {
        items.remove(slot);
    }

    /**
     * 检查客户是否可以购买商品
     * 
     * @param c 客户
     * @param newItem 要购买的新商品
     * @return 如果可以购买则返回true，否则返回false
     */
    private static boolean canBuy(Client c, Item newItem) {
        return InventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && InventoryManipulator.addFromDrop(c, newItem, false);
    }

    /**
     * 取回商品
     * 
     * @param slot 商品槽位
     * @param chr 取回商品的角色
     */
    public void takeItemBack(int slot, Character chr) {
        synchronized (items) {
            PlayerShopItem shopItem = items.get(slot);
            if (shopItem.isExist()) {
                if (shopItem.getBundles() > 0) {
                    Item iitem = shopItem.getItem().copy();
                    iitem.setQuantity((short) (shopItem.getItem().getQuantity() * shopItem.getBundles()));

                    if (!Inventory.checkSpot(chr, iitem)) {
                        chr.sendPacket(PacketCreator.serverNotice(1, "Have a slot available on your inventory to claim back the item."));
                        chr.sendPacket(PacketCreator.enableActions());
                        return;
                    }

                    InventoryManipulator.addFromDrop(chr.getClient(), iitem, true);
                }

                removeFromSlot(slot);
                chr.sendPacket(PacketCreator.getPlayerShopItemUpdate(this));
            }
        }
    }

    /**
     * 购买商品
     * 
     * @param c 客户
     * @param item 商品索引
     * @param quantity 购买数量
     * @return 如果购买成功则返回true，否则返回false
     */
    public boolean buy(Client c, int item, short quantity) {
        synchronized (items) {
            if (isVisitor(c.getPlayer())) {
                PlayerShopItem pItem = items.get(item);
                Item newItem = pItem.getItem().copy();

                newItem.setQuantity((short) ((pItem.getItem().getQuantity() * quantity)));
                if (quantity < 1 || !pItem.isExist() || pItem.getBundles() < quantity) {
                    c.sendPacket(PacketCreator.enableActions());
                    return false;
                } else if (newItem.getInventoryType().equals(InventoryType.EQUIP) && newItem.getQuantity() > 1) {
                    c.sendPacket(PacketCreator.enableActions());
                    return false;
                }

                KarmaManipulator.toggleKarmaFlagToUntradeable(newItem);

                visitorLock.lock();
                try {
                    int price = (int) Math.min((float) pItem.getPrice() * quantity, Integer.MAX_VALUE);

                    if (c.getPlayer().getMeso() >= price) {
                        if (!owner.canHoldMeso(price)) {    // thanks Rohenn for noticing owner hold check misplaced
                            c.getPlayer().dropMessage(1, "Transaction failed since the shop owner can't hold any more mesos.");
                            c.sendPacket(PacketCreator.enableActions());
                            return false;
                        }

                        if (canBuy(c, newItem)) {
                            c.getPlayer().gainMeso(-price, false);
                            price -= Trade.getFee(price);  // thanks BHB for pointing out trade fees not applying here
                            owner.gainMeso(price, true);

                            SoldItem soldItem = new SoldItem(c.getPlayer().getName(), pItem.getItem().getItemId(), quantity, price);
                            owner.sendPacket(PacketCreator.getPlayerShopOwnerUpdate(soldItem, item));

                            synchronized (sold) {
                                sold.add(soldItem);
                            }

                            pItem.setBundles((short) (pItem.getBundles() - quantity));
                            if (pItem.getBundles() < 1) {
                                pItem.setDoesExist(false);
                                if (++boughtnumber == items.size()) {
                                    owner.setPlayerShop(null);
                                    this.setOpen(false);
                                    this.closeShop();
                                    owner.dropMessage(1, "Your items are sold out, and therefore your shop is closed.");
                                }
                            }
                        } else {
                            c.getPlayer().dropMessage(1, "Your inventory is full. Please clear a slot before buying this item.");
                            c.sendPacket(PacketCreator.enableActions());
                            return false;
                        }
                    } else {
                        c.getPlayer().dropMessage(1, "You don't have enough mesos to purchase this item.");
                        c.sendPacket(PacketCreator.enableActions());
                        return false;
                    }

                    return true;
                } finally {
                    visitorLock.unlock();
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 向访客广播数据包
     * 
     * @param packet 要广播的数据包
     */
    public void broadcastToVisitors(Packet packet) {
        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    visitors[i].sendPacket(packet);
                }
            }
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 广播恢复到访客
     * 
     * @return 如果恢复成功则返回true，否则返回false
     */
    public void broadcastRestoreToVisitors() {
        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    visitors[i].sendPacket(PacketCreator.getPlayerShopRemoveVisitor(i + 1));
                }
            }

            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    visitors[i].sendPacket(PacketCreator.getPlayerShop(this, false));
                }
            }

            recoverChatLog();
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 移除所有访客
     */
    public void removeVisitors() {
        List<Character> visitorList = new ArrayList<>(3);

        visitorLock.lock();
        try {
            try {
                for (int i = 0; i < 3; i++) {
                    if (visitors[i] != null) {
                        visitors[i].sendPacket(PacketCreator.shopErrorMessage(10, 1));
                        visitorList.add(visitors[i]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            visitorLock.unlock();
        }

        for (Character mc : visitorList) {
            forceRemoveVisitor(mc);
        }
        if (owner != null) {
            forceRemoveVisitor(owner);
        }
    }

    /**
     * 广播数据包
     * 
     * @param packet 要广播的数据包
     */
    public void broadcast(Packet packet) {
        Client client = owner.getClient();
        if (client != null) {
            client.sendPacket(packet);
        }
        broadcastToVisitors(packet);
    }

    /**
     * 获取访客槽位
     * 
     * @param chr 角色
     * @return 访客槽位
     */
    private byte getVisitorSlot(Character chr) {
        byte s = 0;
        for (Character mc : getVisitors()) {
            s++;
            if (mc != null) {
                if (mc.getName().equalsIgnoreCase(chr.getName())) {
                    break;
                }
            } else if (s == 3) {
                s = 0;
            }
        }

        return s;
    }

    /**
     * 商店聊天
     * 
     * @param c 客户端
     * @param chat 聊天内容
     */
    public void chat(Client c, String chat) {
        byte s = getVisitorSlot(c.getPlayer());

        synchronized (chatLog) {
            chatLog.add(new Pair<>(c.getPlayer(), chat));
            if (chatLog.size() > 25) {
                chatLog.remove(0);
            }
            chatSlot.put(c.getPlayer().getId(), s);
        }

        broadcast(PacketCreator.getPlayerShopChat(c.getPlayer(), chat, s));
    }

    /**
     * 恢复聊天记录
     */
    private void recoverChatLog() {
        synchronized (chatLog) {
            for (Pair<Character, String> it : chatLog) {
                Character chr = it.getLeft();
                Byte pos = chatSlot.get(chr.getId());

                broadcastToVisitors(PacketCreator.getPlayerShopChat(chr, it.getRight(), pos));
            }
        }
    }

    /**
     * 清除聊天记录
     */
    private void clearChatLog() {
        synchronized (chatLog) {
            chatLog.clear();
        }
    }

    /**
     * 关闭商店
     */
    public void closeShop() {
        clearChatLog();
        removeVisitors();
        owner.getMap().broadcastMessage(PacketCreator.removePlayerShopBox(this));
    }

    /**
     * 发送商店信息
     * 
     * @param c 客户端
     */
    public void sendShop(Client c) {
        visitorLock.lock();
        try {
            c.sendPacket(PacketCreator.getPlayerShop(this, isOwner(c.getPlayer())));
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 获取商店所有者
     * 
     * @return 商店所有者
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取访客数组
     * 
     * @return 访客数组
     */
    public Character[] getVisitors() {
        visitorLock.lock();
        try {
            Character[] copy = new Character[3];
            for (int i = 0; i < visitors.length; i++) {
                copy[i] = visitors[i];
            }

            return copy;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 获取商品列表
     * 
     * @return 商品列表
     */
    public List<PlayerShopItem> getItems() {
        synchronized (items) {
            return Collections.unmodifiableList(items);
        }
    }

    /**
     * 检查商店是否包含指定商品
     * 
     * @param itemid 商品ID
     * @return 如果包含该商品则返回true，否则返回false
     */
    public boolean hasItem(int itemid) {
        for (PlayerShopItem mpsi : getItems()) {
            if (mpsi.getItem().getItemId() == itemid && mpsi.isExist() && mpsi.getBundles() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取商店描述
     * 
     * @return 商店描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置商店描述
     * 
     * @param description 新的商店描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 禁止玩家访问商店
     * 
     * @param name 要禁止的玩家名称
     */
    public void banPlayer(String name) {
        if (!bannedList.contains(name)) {
            bannedList.add(name);
        }

        Character target = null;
        visitorLock.lock();
        try {
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null && visitors[i].getName().equals(name)) {
                    target = visitors[i];
                    break;
                }
            }
        } finally {
            visitorLock.unlock();
        }

        if (target != null) {
            target.sendPacket(PacketCreator.shopErrorMessage(5, 1));
            removeVisitor(target);
        }
    }

    /**
     * 检查玩家是否被禁止
     * 
     * @param name 要检查的玩家名称
     * @return 如果被禁止则返回true，否则返回false
     */
    public boolean isBanned(String name) {
        return bannedList.contains(name);
    }

    /**
     * 访问商店
     * 
     * @param chr 要访问的角色
     * @return 如果访问成功则返回true，否则返回false
     */
    public synchronized boolean visitShop(Character chr) {
        if (this.isBanned(chr.getName())) {
            chr.dropMessage(1, "You have been banned from this store.");
            return false;
        }

        visitorLock.lock();
        try {
            if (!open.get()) {
                chr.dropMessage(1, "This store is not yet open.");
                return false;
            }

            if (this.hasFreeSlot() && !this.isVisitor(chr)) {
                this.addVisitor(chr);
                chr.setPlayerShop(this);
                this.sendShop(chr.getClient());

                return true;
            }

            return false;
        } finally {
            visitorLock.unlock();
        }
    }

    /**
     * 发送可用的商品捆绑包
     * 
     * @param itemid 商品ID
     * @return 可用商品捆绑包列表
     */
    public List<PlayerShopItem> sendAvailableBundles(int itemid) {
        List<PlayerShopItem> list = new LinkedList<>();
        List<PlayerShopItem> all = new ArrayList<>();

        synchronized (items) {
            all.addAll(items);
        }

        for (PlayerShopItem mpsi : all) {
            if (mpsi.getItem().getItemId() == itemid && mpsi.getBundles() > 0 && mpsi.isExist()) {
                list.add(mpsi);
            }
        }
        return list;
    }

    /**
     * 获取已售商品列表
     * 
     * @return 已售商品列表
     */
    public List<SoldItem> getSold() {
        synchronized (sold) {
            return Collections.unmodifiableList(sold);
        }
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removePlayerShopBox(this));
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.updatePlayerShopBox(this));
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.SHOP;
    }

    /**
     * 已售商品内部类
     */
    public class SoldItem {

        /** 商品ID */
        int itemid;
        /** 售价（金币） */
        int mesos;
        /** 数量 */
        short quantity;
        /** 买家姓名 */
        String buyer;

        /**
         * 构造函数：创建已售商品实例
         * 
         * @param buyer 买家姓名
         * @param itemid 商品ID
         * @param quantity 数量
         * @param mesos 售价（金币）
         */
        public SoldItem(String buyer, int itemid, short quantity, int mesos) {
            this.buyer = buyer;
            this.itemid = itemid;
            this.quantity = quantity;
            this.mesos = mesos;
        }

        /**
         * 获取买家姓名
         * 
         * @return 买家姓名
         */
        public String getBuyer() {
            return buyer;
        }

        /**
         * 获取商品ID
         * 
         * @return 商品ID
         */
        public int getItemId() {
            return itemid;
        }

        /**
         * 获取数量
         * 
         * @return 数量
         */
        public short getQuantity() {
            return quantity;
        }

        /**
         * 获取售价（金币）
         * 
         * @return 售价（金币）
         */
        public int getMesos() {
            return mesos;
        }
    }
}