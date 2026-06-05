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
 * 【类型】FootholdTree，class，包 {@code org.gms.server.maps}。
 *
 * <p>立足点空间索引树，使用四叉树结构管理地图上的所有立足点（Foothold），
 * 支持高效的空间查询（如 findBelow 查找脚下立足点、findWall 查找墙壁）。</p>
 * 
 * <p>FootholdTree 实现了一个四叉树数据结构，用于快速定位地图上的立足点。
 * 它将地图空间递归地划分为四个象限，直到达到最大深度或满足特定条件，
 * 从而实现高效的碰撞检测和物理计算。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>组织和索引地图上的立足点</li>
 *   <li>支持快速查找指定位置的立足点</li>
 *   <li>提供碰撞检测功能</li>
 *   <li>优化物理计算性能</li>
 * </ul>
 *
 * @author Matze
 */
public class FootholdTree {
    /** 西北象限的子树 */
    private FootholdTree nw = null;
    /** 东北象限的子树 */
    private FootholdTree ne = null;
    /** 西南象限的子树 */
    private FootholdTree sw = null;
    /** 东南象限的子树 */
    private FootholdTree se = null;
    /** 当前节点包含的立足点列表 */
    private final List<Foothold> footholds = new LinkedList<>();
    /** 区域左上角点 */
    private final Point p1;
    /** 区域右下角点 */
    private final Point p2;
    /** 区域中心点 */
    private final Point center;
    /** 当前节点的深度 */
    private int depth = 0;
    /** 最大深度限制 */
    private static final int maxDepth = 8;
    /** 最大X坐标（用于掉落物范围） */
    private int maxDropX;
    /** 最小X坐标（用于掉落物范围） */
    private int minDropX;

    /**
     * 构造函数：创建四叉树根节点
     * 
     * @param p1 区域的左上角点
     * @param p2 区域的右下角点
     */
    public FootholdTree(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    /**
     * 构造函数：创建指定深度的四叉树节点
     * 
     * @param p1 区域的左上角点
     * @param p2 区域的右下角点
     * @param depth 节点的深度
     */
    public FootholdTree(Point p1, Point p2, int depth) {
        this.p1 = p1;
        this.p2 = p2;
        this.depth = depth;
        center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    /**
     * 插入立足点到四叉树中
     * 
     * <p>将指定的立足点插入到四叉树的适当位置。如果当前节点达到最大深度，
     * 或者立足点完全位于当前区域内，则将其添加到当前节点的列表中；
     * 否则，根据立足点的位置递归插入到适当的子象限中。</p>
     * 
     * <p>同时更新最大和最小X坐标值，用于确定掉落物的有效范围。</p>
     * 
     * @param f 要插入的立足点
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

    private List<Foothold> getRelevants(Point p) {
        return getRelevants(p, new LinkedList<>());
    }

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
     * 查找指定区域内的墙体立足点
     * 
     * <p>查找指定区域内是否存在墙体类型的立足点。
     * 此方法要求两个点的Y坐标相同（即水平线段），
     * 用于检测水平方向上的墙体障碍。</p>
     * 
     * @param p1 区域起始点
     * @param p2 区域结束点
     * @return 找到的墙体立足点，如果不存在则返回null
     * @throws IllegalArgumentException 如果两点的Y坐标不相等
     */
    public Foothold findWall(Point p1, Point p2) {
        if (p1.y != p2.y) {
            throw new IllegalArgumentException();
        }
        return findWallR(p1, p2);
    }

    /**
     * 查找指定点下方的立足点
     * 
     * <p>查找指定点正下方的立足点，用于碰撞检测和物理计算。
     * 此方法会遍历所有相关的立足点，找出位于指定点X坐标范围内，
     * 且在该点下方（Y坐标更大）的立足点。</p>
     * 
     * <p>算法步骤：</p>
     * <ol>
     *   <li>获取与指定点相关的所有立足点</li>
     *   <li>筛选出X坐标范围内（fh.getX1() <= p.x && fh.getX2() >= p.x）的立足点</li>
     *   <li>对筛选结果进行排序</li>
     *   <li>检查每个立足点是否为非墙面且位于指定点下方</li>
     *   <li>对于倾斜的立足点，计算其在指定X坐标处的实际高度</li>
     * </ol>
     * 
     * @param p 要查找的点
     * @return 位于指定点下方的立足点，如果不存在则返回null
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
     * 获取区域左上角的X坐标
     * 
     * @return 区域左上角的X坐标值
     */
    public int getX1() {
        return p1.x;
    }

    /**
     * 获取区域右下角的X坐标
     * 
     * @return 区域右下角的X坐标值
     */
    public int getX2() {
        return p2.x;
    }

    /**
     * 获取区域左上角的Y坐标
     * 
     * @return 区域左上角的Y坐标值
     */
    public int getY1() {
        return p1.y;
    }

    /**
     * 获取区域右下角的Y坐标
     * 
     * @return 区域右下角的Y坐标值
     */
    public int getY2() {
        return p2.y;
    }

    /**
     * 获取最大掉落X坐标
     * 
     * <p>返回区域内所有立足点的最大X坐标值，
     * 用于确定掉落物的有效范围。</p>
     * 
     * @return 最大掉落X坐标值
     */
    public int getMaxDropX() {
        return maxDropX;
    }

    /**
     * 获取最小掉落X坐标
     * 
     * <p>返回区域内所有立足点的最小X坐标值，
     * 用于确定掉落物的有效范围。</p>
     * 
     * @return 最小掉落X坐标值
     */
    public int getMinDropX() {
        return minDropX;
    }
}