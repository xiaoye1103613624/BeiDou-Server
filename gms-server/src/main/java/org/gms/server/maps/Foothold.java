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

import java.awt.*;

/**
 * 站脚点
 * 地图上玩家可以站立的平台/地面线段，支持计算任意X坐标对应的Y坐标（坡度计算）
 *
 * @author Matze
 */
public class Foothold implements Comparable<Foothold> {
    /** 线段起点 */
    private final Point p1;
    /** 线段终点 */
    private final Point p2;
    /** 站脚点ID */
    private final int id;
    /** 下一个站脚点ID */
    private int next, prev;

    /**
     * 构造方法
     *
     * @param p1 线段起点
     * @param p2 线段终点
     * @param id 站脚点ID
     */
    public Foothold(Point p1, Point p2, int id) {
        this.p1 = p1;
        this.p2 = p2;
        this.id = id;
    }

    /**
     * 判断是否为墙壁（X坐标相同，即为垂直面）
     */
    public boolean isWall() {
        return p1.x == p2.x;
    }

    public int getX1() {
        return p1.x;
    }

    public int getX2() {
        return p2.x;
    }

    public int getY1() {
        return p1.y;
    }

    public int getY2() {
        return p2.y;
    }

    /**
     * 计算给定X坐标对应的站脚点Y坐标
     * 使用线性插值计算坡度上的Y值
     */
    public int calculateFooting(int x) {
        if (p1.y == p2.y) {
            // y at both ends is the same
            return p2.y;
        }
        int slope = (p1.y - p2.y) / (p1.x - p2.x);
        int intercept = p1.y - (slope * p1.x);
        return (slope * x) + intercept;
    }

    @Override
    public int compareTo(Foothold o) {
        Foothold other = o;
        if (p2.y < other.getY1()) {
            return -1;
        } else if (p1.y > other.getY2()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }
}