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
package org.gms.client;

import org.gms.net.packet.Packet;
import org.gms.net.server.PlayerStorage;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 【类型】BuddyList（class），包 {@code org.gms.client}。
 * 
 * <p>好友列表管理系统，负责管理玩家的好友关系、待处理请求和容量上限。
 * 提供好友添加、删除、查询等功能，并支持好友状态广播。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理好友列表数据</li>
 *   <li>处理好友添加/删除操作</li>
 *   <li>维护好友在线状态</li>
 *   <li>处理好友请求队列</li>
 *   <li>支持好友状态广播</li>
 * </ul>
 * 
 * <p>设计特点：</p>
 * <ul>
 *   <li>线程安全：使用同步块保护关键数据操作</li>
 *   <li>容量限制：支持可配置的好友列表容量</li>
 *   <li>状态管理：跟踪好友的可见性和在线状态</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class BuddyList {
    /**
     * 好友操作类型枚举
     * 
     * <p>定义好友列表中可能的操作类型，用于标识添加或删除操作。</p>
     */
    public enum BuddyOperation {
        /** 好友添加操作 */
        ADDED, 
        /** 好友删除操作 */
        DELETED
    }

    /**
     * 好友添加结果枚举
     * 
     * <p>定义好友添加操作可能的结果状态。</p>
     */
    public enum BuddyAddResult {
        /** 好友列表已满 */
        BUDDYLIST_FULL, 
        /** 好友已存在于列表中 */
        ALREADY_ON_LIST, 
        /** 添加成功 */
        OK
    }

    /** 好友映射表（角色ID→好友条目），使用LinkedHashMap保持插入顺序 */
    private final Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<>();
    /** 好友列表容量上限，限制好友数量 */
    private int capacity;
    /** 待处理的好友请求队列，存储待处理的请求 */
    private final Deque<CharacterNameAndId> pendingRequests = new LinkedList<>();

    /**
     * 构造函数：创建好友列表实例
     * 
     * <p>初始化好友列表，设置指定的容量上限。</p>
     * 
     * @param capacity 好友列表容量上限
     */
    public BuddyList(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 检查指定角色ID是否在好友列表中
     * 
     * <p>线程安全地检查指定的角色ID是否存在于好友列表中。</p>
     * 
     * @param characterId 要检查的角色ID
     * @return 如果角色在好友列表中则返回true，否则返回false
     */
    public boolean contains(int characterId) {
        synchronized (buddies) {
            return buddies.containsKey(characterId);
        }
    }

    /**
     * 检查指定角色ID是否在好友列表中且可见
     * 
     * <p>线程安全地检查指定的角色ID是否存在于好友列表中且标记为可见。</p>
     * 
     * @param characterId 要检查的角色ID
     * @return 如果角色在好友列表中且可见则返回true，否则返回false
     */
    public boolean containsVisible(int characterId) {
        BuddylistEntry ble;
        synchronized (buddies) {
            ble = buddies.get(characterId);
        }

        if (ble == null) {
            return false;
        }
        return ble.isVisible();

    }

    /**
     * 获取好友列表容量上限
     * 
     * @return 当前好友列表的容量上限
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * 设置好友列表容量上限
     * 
     * @param capacity 新的好友列表容量上限
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 根据角色ID获取好友条目
     * 
     * <p>线程安全地根据角色ID获取对应的好友条目信息。</p>
     * 
     * @param characterId 角色ID
     * @return 好友条目对象，如果不存在则返回null
     */
    public BuddylistEntry get(int characterId) {
        synchronized (buddies) {
            return buddies.get(characterId);
        }
    }

    /**
     * 根据角色名称获取好友条目
     * 
     * <p>根据角色名称（不区分大小写）查找对应的好友条目。</p>
     * 
     * @param characterName 角色名称
     * @return 好友条目对象，如果不存在则返回null
     */
    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : getBuddies()) {
            if (ble.getName().toLowerCase().equals(lowerCaseName)) {
                return ble;
            }
        }

        return null;
    }

    /**
     * 添加好友条目到列表
     * 
     * <p>线程安全地将指定的好友条目添加到列表中。</p>
     * 
     * @param entry 要添加的好友条目
     */
    public void put(BuddylistEntry entry) {
        synchronized (buddies) {
            buddies.put(entry.getCharacterId(), entry);
        }
    }

    /**
     * 从列表中移除指定角色ID的好友
     * 
     * <p>线程安全地从好友列表中移除指定角色ID的条目。</p>
     * 
     * @param characterId 要移除的角色ID
     */
    public void remove(int characterId) {
        synchronized (buddies) {
            buddies.remove(characterId);
        }
    }

    /**
     * 获取所有好友条目的集合
     * 
     * <p>线程安全地获取好友列表中所有条目的不可修改集合视图。</p>
     * 
     * @return 所有好友条目的集合
     */
    public Collection<BuddylistEntry> getBuddies() {
        synchronized (buddies) {
            return Collections.unmodifiableCollection(buddies.values());
        }
    }

    /**
     * 检查好友列表是否已满
     * 
     * <p>线程安全地检查当前好友数量是否达到容量上限。</p>
     * 
     * @return 如果好友列表已满则返回true，否则返回false
     */
    public boolean isFull() {
        synchronized (buddies) {
            return buddies.size() >= capacity;
        }
    }

    /**
     * 获取所有好友的ID数组
     * 
     * <p>线程安全地获取好友列表中所有角色的ID数组。</p>
     * 
     * @return 好友ID数组
     */
    public int[] getBuddyIds() {
        synchronized (buddies) {
            int[] buddyIds = new int[buddies.size()];
            int i = 0;
            for (BuddylistEntry ble : buddies.values()) {
                buddyIds[i++] = ble.getCharacterId();
            }
            return buddyIds;
        }
    }

    /**
     * 向所有在线好友广播数据包
     * 
     * <p>将指定的数据包发送给当前在线的所有好友。</p>
     * 
     * @param packet 要广播的数据包
     * @param pstorage 玩家存储管理器
     */
    public void broadcast(Packet packet, PlayerStorage pstorage) {
        for (int bid : getBuddyIds()) {
            Character chr = pstorage.getCharacterById(bid);

            if (chr != null && chr.isLoggedInWorld()) {
                chr.sendPacket(packet);
            }
        }
    }

    public void loadFromDb(int characterId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, b.group, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ?")) {
                ps.setInt(1, characterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getInt("pending") == 1) {
                            pendingRequests.push(new CharacterNameAndId(rs.getInt("buddyid"), rs.getString("buddyname")));
                        } else {
                            put(new BuddylistEntry(rs.getString("buddyname"), rs.getString("group"), rs.getInt("buddyid"), (byte) -1, true));
                        }
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ?")) {
                ps.setInt(1, characterId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public CharacterNameAndId pollPendingRequest() {
        return pendingRequests.pollLast();
    }

    public void addBuddyRequest(Client c, int cidFrom, String nameFrom, int channelFrom) {
        put(new BuddylistEntry(nameFrom, "Default Group", cidFrom, channelFrom, false));
        if (pendingRequests.isEmpty()) {
            c.sendPacket(PacketCreator.requestBuddylistAdd(cidFrom, c.getPlayer().getId(), nameFrom));
        } else {
            pendingRequests.push(new CharacterNameAndId(cidFrom, nameFrom));
        }
    }
}