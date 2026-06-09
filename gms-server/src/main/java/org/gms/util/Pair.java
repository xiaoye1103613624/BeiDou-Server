/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.util;

/**
 * 二元组数据结构
 * 用于存储两个相关联的对象
 *
 * @param <E> 左元素的类型
 * @param <F> 右元素的类型
 * @author Frz
 * @version 1.0
 * @since Revision 333
 */
public class Pair<E, F> {

    /**
     * 左元素
     */
    public E left;

    /**
     * 右元素
     */
    public F right;

    /**
     * 构造二元组，将两个对象组合在一起
     *
     * @param left  左对象
     * @param right 右对象
     */
    public Pair(E left, F right) {
        this.left = left;
        this.right = right;
    }

    /**
     * 获取左元素
     *
     * @return 左元素
     */
    public E getLeft() {
        return left;
    }

    /**
     * 获取右元素
     *
     * @return 右元素
     */
    public F getRight() {
        return right;
    }

    /**
     * 将二元组转换为字符串
     *
     * @return 两个元素用冒号连接的字符串
     */
    @Override
    public String toString() {
        return left.toString() + ":" + right.toString();
    }

    /**
     * 计算二元组的哈希码
     *
     * @return 哈希码值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    /**
     * 判断两个二元组是否相等
     *
     * @param obj 要比较的对象
     * @return 如果两个二元组的左右元素都相等返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
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
        final Pair other = (Pair) obj;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (right == null) {
            return other.right == null;
        } else return right.equals(other.right);
    }
}