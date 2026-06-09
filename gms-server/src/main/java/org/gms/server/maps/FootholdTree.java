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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 站脚点四叉树
 * 使用四叉树（Quadtree）结构管理地图上的站脚点，支持快速查找玩家下方的站脚点和墙壁碰撞检测
 * 最大深度为8层，递归分割地图空间
 *
 * @author Matze
 */
public class FootholdTree {
    /** 西北子节点 */
    private FootholdTree nw = null;
    /** 东北子节点 */
    private FootholdTree ne = null;
    /** 西南子节点 */
    private FootholdTree sw = null;
    /** 东南子节点 */
    private FootholdTree se = null;
    /** 当前节点内的站脚点列表 */
    private final List<Foothold> footholds = new LinkedList<>();
    /** 区域左上角 */
    private final Point p1;
    /** 区域右下角 */
    private final Point p2;
    /** 区域中心点 */
    private final Point center;
    /** 当前深度 */
    private int depth = 0;
    /** 最大深度 */
    private static final int maxDepth = 8;
    /** 最大掉落X坐标 */
    private int maxDropX;
    /** 最小掉落X坐标 */
    private int minDropX;

    /**
     * 构造方法（根节点）
     */
    public FootholdTree(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    /**
     * 构造方法（子节点，指定深度）
     */
    public FootholdTree(Point p1, Point p2, int depth) {
        this.p1 = p1;
        this.p2 = p2;
        this.depth = depth;
        center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    /**
     * 插入站脚点到四叉树
     * 若站脚点完全在当前区域内或达到最大深度则直接存储，否则递归插入子节点
     */
    public void insert(Foothold f) {
        if (depth == 0) {
            if (f.getX1() > maxDropX) {
                maxDropX = f.getX1();
            }
            if (f.getX1() < minDropX) {
                minDropX = f.getX1();
            }
            if (f.getX2() > maxDropX) {
                maxDropX = f.getX2();
            }
            if (f.getX2() < minDropX) {
                minDropX = f.getX2();
            }
        }
        if (depth == maxDepth ||
                (f.getX1() >= p1.x && f.getX2() <= p2.x &&
                        f.getY1() >= p1.y && f.getY2() <= p2.y)) {
            footholds.add(f);
        } else {
            if (nw == null) {
                nw = new FootholdTree(p1, center, depth + 1);
                ne = new FootholdTree(new Point(center.x, p1.y), new Point(p2.x, center.y), depth + 1);
                sw = new FootholdTree(new Point(p1.x, center.y), new Point(center.x, p2.y), depth + 1);
                se = new FootholdTree(center, p2, depth + 1);
            }
            if (f.getX2() <= center.x && f.getY2() <= center.y) {
                nw.insert(f);
            } else if (f.getX1() > center.x && f.getY2() <= center.y) {
                ne.insert(f);
            } else if (f.getX2() <= center.x && f.getY1() > center.y) {
                sw.insert(f);
            } else {
                se.insert(f);
            }
        }
    }

    /**
     * 获取指定点相关的所有站脚点
     */
    private List<Foothold> getRelevants(Point p) {
        return getRelevants(p, new LinkedList<>());
    }

    /**
     * 递归获取指定点相关的所有站脚点
     */
    private List<Foothold> getRelevants(Point p, List<Foothold> list) {
        list.addAll(footholds);
        if (nw != null) {
            if (p.x <= center.x && p.y <= center.y) {
                nw.getRelevants(p, list);
            } else if (p.x > center.x && p.y <= center.y) {
                ne.getRelevants(p, list);
            } else if (p.x <= center.x && p.y > center.y) {
                sw.getRelevants(p, list);
            } else {
                se.getRelevants(p, list);
            }
        }
        return list;
    }

    /**
     * 递归查找墙壁站脚点
     */
    private Foothold findWallR(Point p1, Point p2) {
        Foothold ret;
        for (Foothold f : footholds) {
            if (f.isWall() && f.getX1() >= p1.x && f.getX1() <= p2.x &&
                    f.getY1() >= p1.y && f.getY2() <= p1.y) {
                return f;
            }
        }
        if (nw != null) {
            if (p1.x <= center.x && p1.y <= center.y) {
                ret = nw.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
            if ((p1.x > center.x || p2.x > center.x) && p1.y <= center.y) {
                ret = ne.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
            if (p1.x <= center.x && p1.y > center.y) {
                ret = sw.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
            if ((p1.x > center.x || p2.x > center.x) && p1.y > center.y) {
                ret = se.findWallR(p1, p2);
                return ret;
            }
        }
        return null;
    }

    /**
     * 查找墙壁站脚点
     *
     * @throws IllegalArgumentException 如果p1和p2的Y坐标不同
     */
    public Foothold findWall(Point p1, Point p2) {
        if (p1.y != p2.y) {
            throw new IllegalArgumentException();
        }
        return findWallR(p1, p2);
    }

    /**
     * 查找指定点正下方的站脚点
     * 使用三角函数精确计算坡度上的Y坐标
     */
    public Foothold findBelow(Point p) {
        List<Foothold> relevants = getRelevants(p);
        List<Foothold> xMatches = new LinkedList<>();
        for (Foothold fh : relevants) {
            if (fh.getX1() <= p.x && fh.getX2() >= p.x) {
                xMatches.add(fh);
            }
        }
        Collections.sort(xMatches);
        for (Foothold fh : xMatches) {
            if (!fh.isWall()) {
                if (fh.getY1() != fh.getY2()) {
                    int calcY;
                    double s1 = Math.abs(fh.getY2() - fh.getY1());
                    double s2 = Math.abs(fh.getX2() - fh.getX1());
                    double s4 = Math.abs(p.x - fh.getX1());
                    double alpha = Math.atan(s2 / s1);
                    double beta = Math.atan(s1 / s2);
                    double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
                    if (fh.getY2() < fh.getY1()) {
                        calcY = fh.getY1() - (int) s5;
                    } else {
                        calcY = fh.getY1() + (int) s5;
                    }
                    if (calcY >= p.y) {
                        return fh;
                    }
                } else {
                    if (fh.getY1() >= p.y) {
                        return fh;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取区域左上角X坐标
     */
    public int getX1() {
        return p1.x;
    }

    /**
     * 获取区域右下角X坐标
     */
    public int getX2() {
        return p2.x;
    }

    /**
     * 获取区域左上角Y坐标
     */
    public int getY1() {
        return p1.y;
    }

    /**
     * 获取区域右下角Y坐标
     */
    public int getY2() {
        return p2.y;
    }

    /**
     * 获取最大掉落X坐标
     */
    public int getMaxDropX() {
        return maxDropX;
    }

    /**
     * 获取最小掉落X坐标
     */
    public int getMinDropX() {
        return minDropX;
    }
}