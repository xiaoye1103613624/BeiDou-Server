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
package org.gms.server.events.gm;

/**
 * 椰子（打椰子活动）
 * 记录单个椰子的命中次数和可命中状态，支持冷却判定
 *
 * @author kevintjuh93
 */
public class Coconuts {
    /** 椰子ID */
    private final int id;
    /** 已命中次数 */
    private int hits = 0;
    /** 是否可命中 */
    private boolean hittable = false;
    /** 可命中时间戳 */
    private long hittime = System.currentTimeMillis();

    /**
     * 构造椰子
     *
     * @param id 椰子ID
     */
    public Coconuts(int id) {
        this.id = id;
    }

    /**
     * 命中椰子
     * 更新可命中冷却时间和命中次数
     */
    public void hit() {
        this.hittime = System.currentTimeMillis() + 750;
        hits++;
    }

    /**
     * 获取命中次数
     *
     * @return 命中次数
     */
    public int getHits() {
        return hits;
    }

    /**
     * 重置命中次数
     */
    public void resetHits() {
        hits = 0;
    }

    /**
     * 判断是否可以命中
     *
     * @return true可命中，false不可命中
     */
    public boolean isHittable() {
        return hittable;
    }

    /**
     * 设置是否可命中
     *
     * @param hittable true可命中，false不可命中
     */
    public void setHittable(boolean hittable) {
        this.hittable = hittable;
    }

    /**
     * 获取可命中时间戳
     *
     * @return 可命中时间戳
     */
    public long getHitTime() {
        return hittime;
    }
}