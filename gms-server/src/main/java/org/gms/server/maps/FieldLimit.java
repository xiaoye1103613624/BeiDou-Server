/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
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
package org.gms.server.maps;

/**
 * 地图字段限制枚举
 * 定义地图的各种功能限制，使用位掩码形式组合多种限制
 * 从WZ文件的fieldLimit字段读取，通过位运算检查特定限制
 *
 * @author AngelSL
 */
public enum FieldLimit {
    /** 禁止跳跃 */
    JUMP(0x01),
    /** 禁止移动技能 */
    MOVEMENTSKILLS(0x02),
    /** 禁止召唤兽 */
    SUMMON(0x04),
    /** 禁止传送门 */
    DOOR(0x08),
    /** 禁止切换频道/使用回城卷轴/进入商城 */
    CANNOTMIGRATE(0x10),
    /** 禁止使用VIP传送石 */
    CANNOTVIPROCK(0x40),
    /** 禁止小游戏 */
    CANNOTMINIGAME(0x80),
    /** 禁止使用坐骑 */
    CANNOTUSEMOUNTS(0x200),
    /** 禁止使用药剂 */
    CANNOTUSEPOTION(0x1000),
    /** 禁止向下跳 */
    CANNOTJUMPDOWN(0x20000),
    /** 死亡不掉经验 */
    NO_EXP_DECREASE(0x80000),
    /** 禁止掉落物品 */
    DROP_LIMIT(0x400000);

    /** 位掩码值 */
    private final long i;

    FieldLimit(long i) {
        this.i = i;
    }

    /**
     * 获取位掩码值
     *
     * @return 位掩码
     */
    public long getValue() {
        return i;
    }

    /**
     * 检查fieldLimit中是否包含当前限制
     *
     * @param fieldLimit 地图的fieldLimit值
     * @return true表示包含该限制
     */
    public boolean check(int fieldLimit) {
        return (fieldLimit & i) == i;
    }
}