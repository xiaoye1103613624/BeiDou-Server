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
package org.gms.net.server;

import org.gms.client.Character;
import org.gms.client.Client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 玩家存储
 * 线程安全的玩家容器，支持按ID或名称查找、遍历在线玩家
 */
public class PlayerStorage {
    /** 角色ID映射存储 */
    private final Map<Integer, Character> storage = new LinkedHashMap<>();
    /** 角色名称映射存储 */
    private final Map<String, Character> nameStorage = new LinkedHashMap<>();
    /** 读锁 */
    private final Lock rlock;
    /** 写锁 */
    private final Lock wlock;

    /**
     * 构造玩家存储
     */
    public PlayerStorage() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.rlock = readWriteLock.readLock();
        this.wlock = readWriteLock.writeLock();
    }

    /**
     * 添加玩家
     *
     * @param chr 玩家角色对象
     */
    public void addPlayer(Character chr) {
        wlock.lock();
        try {
            storage.put(chr.getId(), chr);
            nameStorage.put(chr.getName().toLowerCase(), chr);
        } finally {
            wlock.unlock();
        }
    }

    /**
     * 移除玩家
     *
     * @param chr 角色ID
     * @return 被移除的角色对象，若不存在则返回null
     */
    public Character removePlayer(int chr) {
        wlock.lock();
        try {
            Character mc = storage.remove(chr);
            if (mc != null) {
                nameStorage.remove(mc.getName().toLowerCase());
            }

            return mc;
        } finally {
            wlock.unlock();
        }
    }

    /**
     * 根据名称查找玩家
     *
     * @param name 角色名称
     * @return 角色对象，若不存在则返回null
     */
    public Character getCharacterByName(String name) {
        rlock.lock();
        try {
            return nameStorage.get(name.toLowerCase());
        } finally {
            rlock.unlock();
        }
    }

    /**
     * 根据ID查找玩家
     *
     * @param id 角色ID
     * @return 角色对象，若不存在则返回null
     */
    public Character getCharacterById(int id) {
        rlock.lock();
        try {
            return storage.get(id);
        } finally {
            rlock.unlock();
        }
    }

    /**
     * 获取所有在线玩家
     *
     * @return 所有角色对象的集合
     */
    public Collection<Character> getAllCharacters() {
        rlock.lock();
        try {
            return new ArrayList<>(storage.values());
        } finally {
            rlock.unlock();
        }
    }

    /**
     * 断开所有玩家的连接
     */
    public final void disconnectAll() {
        List<Character> chrList;
        rlock.lock();
        try {
            chrList = new ArrayList<>(storage.values());
        } finally {
            rlock.unlock();
        }

        for (Character mc : chrList) {
            Client client = mc.getClient();
            if (client != null) {
                client.forceDisconnect();
            }
        }

        wlock.lock();
        try {
            storage.clear();
        } finally {
            wlock.unlock();
        }
    }

    /**
     * 获取在线玩家数量
     *
     * @return 玩家数量
     */
    public int getSize() {
        rlock.lock();
        try {
            return storage.size();
        } finally {
            rlock.unlock();
        }
    }
}