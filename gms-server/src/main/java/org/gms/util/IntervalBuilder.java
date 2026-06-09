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

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 区间管理器
 * 管理一组区间范围，支持区间的添加、合并和查询
 * 使用读写锁保证线程安全
 *
 * @author Ronan
 */
public class IntervalBuilder {
    /** 区间列表，存储合并后的非重叠区间 */
    private final List<Line2D> intervalLimits = new ArrayList<>();
    /** 区间读锁，用于查询操作 */
    private final Lock intervalRlock;
    /** 区间写锁，用于添加/清除操作 */
    private final Lock intervalWlock;

    /**
     * 构造区间管理器，初始化读写锁
     */
    public IntervalBuilder() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.intervalRlock = readWriteLock.readLock();
        this.intervalWlock = readWriteLock.writeLock();
    }

    /**
     * 合并重叠区间
     * 将范围内的已有区间与新加入区间合并为单个区间
     *
     * @param st      起始索引
     * @param en      结束索引（不包含）
     * @param newFrom 新区间起点
     * @param newTo   新区间终点
     */
    private void refitOverlappedIntervals(int st, int en, int newFrom, int newTo) {
        List<Line2D> checkLimits = new ArrayList<>(intervalLimits.subList(st, en));

        float newLimitX1, newLimitX2;
        if (!checkLimits.isEmpty()) {
            Line2D firstLimit = checkLimits.get(0);
            Line2D lastLimit = checkLimits.get(checkLimits.size() - 1);

            // 新区间取原区间范围与新值的最小/最大并集
            newLimitX1 = (float) ((newFrom < firstLimit.getX1()) ? newFrom : firstLimit.getX1());
            newLimitX2 = (float) ((newTo > lastLimit.getX2()) ? newTo : lastLimit.getX2());

            // 删除所有重叠的旧区间
            for (Line2D limit : checkLimits) {
                intervalLimits.remove(st);
            }
        } else {
            // 无重叠区间，直接使用新值
            newLimitX1 = newFrom;
            newLimitX2 = newTo;
        }

        // 在合并位置插入新区间
        intervalLimits.add(st, new Line2D.Float(newLimitX1, 0, newLimitX2, 0));
    }

    /**
     * 二分查找指定值所在的区间索引
     *
     * @param point 查询点
     * @return 所在区间索引，未找到返回-1或最接近的区间索引
     */
    private int bsearchInterval(int point) {
        int st = 0, en = intervalLimits.size() - 1;

        int mid, idx;
        while (en >= st) {
            idx = (st + en) / 2;
            mid = (int) intervalLimits.get(idx).getX1();

            if (mid == point) {
                return idx;
            } else if (mid < point) {
                st = idx + 1;
            } else {
                en = idx - 1;
            }
        }

        // 未精确命中，返回小于point的最近区间索引
        return en;
    }

    /**
     * 添加区间，自动与相邻区间合并
     *
     * @param from 区间起点
     * @param to   区间终点
     */
    public void addInterval(int from, int to) {
        intervalWlock.lock();
        try {
            // 查找起点所在的或最接近的区间索引
            int st = bsearchInterval(from);
            if (st < 0) {
                st = 0;
            } else if (intervalLimits.get(st).getX2() < from) {
                st += 1;
            }

            // 查找终点所在的或最接近的区间索引
            int en = bsearchInterval(to);
            if (en < st) {
                en = st - 1;
            }

            // 将[st, en]范围内的区间与新区间合并
            refitOverlappedIntervals(st, en + 1, from, to);
        } finally {
            intervalWlock.unlock();
        }
    }

    /**
     * 判断单个点是否在任意区间内
     *
     * @param point 查询点
     * @return 是否在区间内
     */
    public boolean inInterval(int point) {
        return inInterval(point, point);
    }

    /**
     * 判断区间是否与已有区间重叠
     *
     * @param from 区间起点
     * @param to   区间终点
     * @return 是否与已有区间重叠
     */
    public boolean inInterval(int from, int to) {
        intervalRlock.lock();
        try {
            int idx = bsearchInterval(from);
            // 检查是否存在区间且该区间能完全覆盖[from, to]
            return idx >= 0 && to <= intervalLimits.get(idx).getX2();
        } finally {
            intervalRlock.unlock();
        }
    }

    /**
     * 清除所有区间
     */
    public void clear() {
        intervalWlock.lock();
        try {
            intervalLimits.clear();
        } finally {
            intervalWlock.unlock();
        }
    }

}