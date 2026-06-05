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
 * 【类型】FieldLimit，enum，包 {@code org.gms.server.maps}。
 *
 * <p>地图字段限制枚举，定义地图上允许或禁止的各种行为标志（如跳跃、移动技能、召唤、传送门、药水使用等），
 * 通过位掩码运算进行组合判断。</p>
 * 
 * <p>此枚举使用位标志的方式表示不同的地图限制，可以在单个整数值中组合多个限制，
 * 便于高效地控制地图上的各种行为。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义地图行为限制标志</li>
 *   <li>支持多种限制的组合</li>
 *   <li>提供便捷的检查方法</li>
 * </ul>
 *
 * @author AngelSL
 */
public enum FieldLimit {
    /** 禁止跳跃 */
    JUMP(0x01),
    /** 禁止移动技能 */
    MOVEMENTSKILLS(0x02),
    /** 禁止召唤宠物/召唤兽 */
    SUMMON(0x04),
    /** 禁止使用传送门 */
    DOOR(0x08),
    /** 禁止迁移（切换频道、城镇传送卷轴、访问现金商店等） */
    CANNOTMIGRATE(0x10),
    //NO_NOTES(0x20),
    /** 禁止使用VIP传送卷轴 */
    CANNOTVIPROCK(0x40),
    /** 禁止使用小游戏 */
    CANNOTMINIGAME(0x80),
    //SPECIFIC_PORTAL_SCROLL_LIMIT(0x100), // APQ and a couple quest maps have this
    /** 禁止使用坐骑 */
    CANNOTUSEMOUNTS(0x200),
    //STAT_CHANGE_ITEM_CONSUME_LIMIT(0x400), // Monster carnival?
    //PARTY_BOSS_CHANGE_LIMIT(0x800), // Monster carnival?
    /** 禁止使用药水 */
    CANNOTUSEPOTION(0x1000),
    //WEDDING_INVITATION_LIMIT(0x2000), // No notes
    //CASH_WEATHER_CONSUME_LIMIT(0x4000),
    //NO_PET(0x8000), // Ariant colosseum-related?
    //ANTI_MACRO_LIMIT(0x10000), // No notes
    /** 禁止向下跳跃 */
    CANNOTJUMPDOWN(0x20000),
    //SUMMON_NPC_LIMIT(0x40000); // Seems to .. disable Rush if 0x2 is set

    //......... EVEN MORE LIMITS ............
    //SUMMON_NPC_LIMIT(0x40000),
    /** 不减少经验值 */
    NO_EXP_DECREASE(0x80000),
    //NO_DAMAGE_ON_FALLING(0x100000),
    //PARCEL_OPEN_LIMIT(0x200000),
    /** 限制掉落 */
    DROP_LIMIT(0x400000);
    //ROCKETBOOSTER_LIMIT(0x800000)     //lol we don't even have mechanics <3

    /** 限制值 */
    private final long i;

    /**
     * 构造函数：创建限制枚举项
     * 
     * @param i 限制值（位标志）
     */
    FieldLimit(long i) {
        this.i = i;
    }

    /**
     * 获取限制值
     * 
     * @return 限制值（位标志）
     */
    public long getValue() {
        return i;
    }

    /**
     * 检查指定的地图限制是否包含此限制
     * 
     * @param fieldlimit 要检查的地图限制值
     * @return 如果包含此限制则返回true，否则返回false
     */
    public boolean check(int fieldlimit) {
        return (fieldlimit & i) == i;
    }
}