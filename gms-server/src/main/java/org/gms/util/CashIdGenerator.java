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
package org.gms.util;

import org.gms.dao.mapper.PetsMapper;
import org.gms.dao.mapper.RingsMapper;
import org.gms.manager.ServerManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 现金物品ID生成器
 * 生成宠物/戒指等现金物品的唯一ID，从数据库加载已存在的ID避免冲突
 *
 * @author RonanLana
 */
public class CashIdGenerator {
    /** 已存在的现金ID集合，用于避让冲突 */
    private final static Set<Integer> existentCashIds = new HashSet<>(10000);
    /** 当前运行中的现金ID */
    private static Integer runningCashId = 0;

    /**
     * 从数据库加载已存在的现金ID
     * 从rings表和pets表获取所有已用ID，并找到第一个可用ID
     */
    public static synchronized void loadExistentCashIdsFromDb() {
        RingsMapper ringsMapper = ServerManager.getApplicationContext().getBean(RingsMapper.class);
        existentCashIds.clear();

        // 从rings表加载所有已分配的戒指ID
        ringsMapper.selectAll().forEach(ringsDO -> {
            if (ringsDO.getId() != null) {
                existentCashIds.add(ringsDO.getId());
            }
        });

        // 从pets表加载所有已分配的宠物ID，petid为Long需转换为Integer
        PetsMapper petsMapper = ServerManager.getApplicationContext().getBean(PetsMapper.class);
        petsMapper.selectAll().forEach(petsDO -> {
            if (petsDO.getPetid() != null) {
                existentCashIds.add(petsDO.getPetid().intValue());
            }
        });

        // 重置计数器，从0开始查找第一个未被占用的ID
        runningCashId = 0;
        do {
            runningCashId++;
        } while (existentCashIds.contains(runningCashId));
    }

    /**
     * 获取下一个可用现金ID
     * 递增runningCashId，超过上限时重新从数据库加载
     */
    private static void getNextAvailableCashId() {
        runningCashId++;
        // 达到上限时重新从数据库同步，实现ID复用
        if (runningCashId >= 777000000) {
            loadExistentCashIdsFromDb();
        }
    }

    /**
     * 生成唯一的现金ID
     * 查找第一个未被占用的ID并返回，线程安全
     *
     * @return 可用的现金ID
     */
    public static synchronized int generateCashId() {
        while (true) {
            if (!existentCashIds.contains(runningCashId)) {
                int ret = runningCashId;
                getNextAvailableCashId();

                // 无需将ret加入existentCashIds，因为ID回绕时会重新从数据库加载已使用的ID
                return ret;
            }

            // 当前ID已被占用，跳过继续查找
            getNextAvailableCashId();
        }
    }

    /**
     * 释放现金ID，回收到可用池
     *
     * @param cashId 要释放的现金ID
     */
    public static synchronized void freeCashId(int cashId) {
        // 从已占用集合中移除此ID，使其可被重新分配
        existentCashIds.remove(cashId);
    }

}