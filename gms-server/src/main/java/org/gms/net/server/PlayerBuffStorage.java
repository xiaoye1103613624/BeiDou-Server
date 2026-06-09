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

import org.gms.client.Disease;
import org.gms.server.life.MobSkill;
import org.gms.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 玩家Buff存储
 * 线程安全地管理玩家身上的Buff和Debuff效果，支持效果和异常状态的叠加处理
 */
public class PlayerBuffStorage {
    /** 唯一标识ID */
    private final int id = (int) (Math.random() * 100);
    /** 线程锁 */
    private final Lock lock = new ReentrantLock(true);
    /** 角色Buff映射（角色ID -> Buff列表） */
    private final Map<Integer, List<PlayerBuffValueHolder>> buffs = new HashMap<>();
    /** 角色异常状态映射（角色ID -> 异常状态类型映射） */
    private final Map<Integer, Map<Disease, Pair<Long, MobSkill>>> diseases = new HashMap<>();

    /**
     * 添加角色的Buff到存储
     *
     * @param chrid   角色ID
     * @param toStore 待存储的Buff列表
     */
    public void addBuffsToStorage(int chrid, List<PlayerBuffValueHolder> toStore) {
        lock.lock();
        try {
            // Old one will be replaced if it's in here.
            buffs.put(chrid, toStore);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从存储中取出并移除角色的Buff
     *
     * @param chrid 角色ID
     * @return Buff列表，若无则返回null
     */
    public List<PlayerBuffValueHolder> getBuffsFromStorage(int chrid) {
        lock.lock();
        try {
            return buffs.remove(chrid);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加角色的异常状态到存储
     *
     * @param chrid   角色ID
     * @param toStore 待存储的异常状态映射
     */
    public void addDiseasesToStorage(int chrid, Map<Disease, Pair<Long, MobSkill>> toStore) {
        lock.lock();
        try {
            diseases.put(chrid, toStore);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从存储中取出并移除角色的异常状态
     *
     * @param chrid 角色ID
     * @return 异常状态映射，若无则返回null
     */
    public Map<Disease, Pair<Long, MobSkill>> getDiseasesFromStorage(int chrid) {
        lock.lock();
        try {
            return diseases.remove(chrid);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerBuffStorage other = (PlayerBuffStorage) obj;
        return id == other.id;
    }
}