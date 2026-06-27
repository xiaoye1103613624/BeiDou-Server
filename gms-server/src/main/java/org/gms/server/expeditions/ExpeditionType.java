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

package org.gms.server.expeditions;

import org.gms.config.GameConfig;

/**
 * 远征队类型枚举
 * 定义各种远征队的配置：最小/最大人数、等级限制、注册时间等
 *
 * @author Alan (SharpAceX)
 */
public enum ExpeditionType {
    /** 简单巴尔洛 */
    BALROG_EASY(3, 30, 50, 255, 5),
    /** 普通巴尔洛 */
    BALROG_NORMAL(6, 30, 50, 255, 5),
    /** 斯卡加 */
    SCARGA(6, 30, 100, 255, 5),
    /** 昭和 */
    SHOWA(3, 30, 100, 255, 5),
    /** 扎昆 */
    ZAKUM(6, 30, 50, 255, 5),
    /** 黑龙 */
    HORNTAIL(6, 30, 100, 255, 5),
    /** 混沌扎昆 */
    CHAOS_ZAKUM(6, 30, 120, 255, 5),
    /** 混沌黑龙 */
    CHAOS_HORNTAIL(6, 30, 120, 255, 5),
    /** 阿里安特 */
    ARIANT(2, 7, 20, 30, 5),
    ARIANT1(2, 7, 20, 30, 5),
    ARIANT2(2, 7, 20, 30, 5),
    /** 品客缤 */
    PINKBEAN(6, 30, 120, 255, 5),
    /** 绯红要塞 */
    CWKPQ(6, 30, 90, 255, 5),
    /** 熊狮王 */
    SCARTAR(6, 30, 90, 255, 5),
    /** 核心烈焰 */
    CORE_BLAZE(6, 30, 100, 255, 5),
    /** 杜纳斯2 */
    DUNAS2(6, 30, 100, 255, 5),
    /** 奥夫海本 */
    AUFHEBEN(6, 30, 100, 255, 5),
    /**  Vergamot */
    VERGAMOT(6, 30, 100, 255, 5),
    /** 尼伯根 */
    NIBERGEN(6, 30, 100, 255, 5),
    /** 杜纳斯 */
    DUNAS(6, 30, 100, 255, 5),
    /** 无名魔法怪物 */
    NAMELESS(6, 30, 100, 255, 5),
    /** 凡雷恩 */
    VONLEON(6, 30, 120, 255, 5),
    /** Boss Balrog */
    BOSS_BALROG(6, 30, 50, 255, 5);

    /** 最小人数 */
    private final int minSize;
    /** 最大人数 */
    private final int maxSize;
    /** 最小等级 */
    private final int minLevel;
    /** 最大等级 */
    private final int maxLevel;
    /** 注册时间（分钟） */
    private final int registrationMinutes;

    /**
     * 构造远征类型
     *
     * @param minSize  最小人数
     * @param maxSize  最大人数
     * @param minLevel 最小等级
     * @param maxLevel 最大等级
     * @param minutes  注册时间（分钟）
     */
    ExpeditionType(int minSize, int maxSize, int minLevel, int maxLevel, int minutes) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.registrationMinutes = minutes;
    }

    /**
     * 获取最小人数
     * 若启用了单人远征配置，则最小人数返回1
     *
     * @return 最小人数
     */
    public int getMinSize() {
        return !GameConfig.getServerBoolean("use_enable_solo_expeditions") ? minSize : 1;
    }

    /**
     * 获取最大人数
     *
     * @return 最大人数
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 获取最小等级
     *
     * @return 最小等级
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * 获取最大等级
     *
     * @return 最大等级
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 获取注册时间（分钟）
     *
     * @return 注册分钟数
     */
    public int getRegistrationMinutes() {
        return registrationMinutes;
    }
}