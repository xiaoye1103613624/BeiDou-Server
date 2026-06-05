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
 * 【类】Foothold（class），包 {@code org.gms.server.maps}。
 * 
 * <p>立足点类，定义地图中角色可行走的地面线段，用于碰撞检测和物理计算。
 * 立足点是构成地图可行走区域的基础单元，通常表示一段地面、平台或墙壁。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>表示地图中的可行走线段</li>
 *   <li>支持碰撞检测计算</li>
 *   <li>提供高度计算功能</li>
 *   <li>支持立足点间的连接关系</li>
 * </ul>
 */
public class Foothold implements Comparable<Foothold> {
    /** 立足点的第一个端点 */
    private final Point p1; 
    /** 立足点的第二个端点 */
    private final Point p2; 
    /** 立足点的唯一标识符 */
    private final int id; 
    /** 下一个连接的立足点ID */
    private int next; 
    /** 上一个连接的立足点ID */
    private int prev; 

    /**
     * 构造函数：创建立足点实例
     * 
     * @param p1 立足点的第一个端点
     * @param p2 立足点的第二个端点
     * @param id 立足点的唯一标识符
     */
    public Foothold(Point p1, Point p2, int id) {
        this.p1 = p1;
        this.p2 = p2;
        this.id = id;
    }

    /**
     * 检查是否为墙
     * 
     * <p>判断此立足点是否为垂直墙面（即两端点x坐标相同）。</p>
     * 
     * @return 如果是墙面则返回true，否则返回false
     */
    public boolean isWall() {
        return p1.x == p2.x;
    }

    /**
     * 获取第一个端点的X坐标
     * 
     * @return 第一个端点的X坐标
     */
    public int getX1() {
        return p1.x;
    }

    /**
     * 获取第二个端点的X坐标
     * 
     * @return 第二个端点的X坐标
     */
    public int getX2() {
        return p2.x;
    }

    /**
     * 获取第一个端点的Y坐标
     * 
     * @return 第一个端点的Y坐标
     */
    public int getY1() {
        return p1.y;
    }

    /**
     * 获取第二个端点的Y坐标
     * 
     * @return 第二个端点的Y坐标
     */
    public int getY2() {
        return p2.y;
    }

    /**
     * 计算指定X坐标处的高度
     * 
     * <p>根据直线方程计算在指定X坐标处的Y值（高度）。
     * 如果两端点Y坐标相同，则直接返回Y坐标值。</p>
     * 
     * @param x 要计算高度的X坐标
     * @return 对应X坐标处的Y坐标（高度）
     */
    public int calculateFooting(int x) {
        if (p1.y == p2.y) {
            return p2.y; // y at both ends is the same
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

    /**
     * 获取立足点ID
     * 
     * @return 立足点的唯一标识符
     */
    public int getId() {
        return id;
    }

    /**
     * 获取下一个连接的立足点ID
     * 
     * @return 下一个立足点的ID
     */
    public int getNext() {
        return next;
    }

    /**
     * 设置下一个连接的立足点ID
     * 
     * @param next 要设置的下一个立足点ID
     */
    public void setNext(int next) {
        this.next = next;
    }

    /**
     * 获取上一个连接的立足点ID
     * 
     * @return 上一个立足点的ID
     */
    public int getPrev() {
        return prev;
    }

    /**
     * 设置上一个连接的立足点ID
     * 
     * @param prev 要设置的上一个立足点ID
     */
    public void setPrev(int prev) {
        this.prev = prev;
    }
}