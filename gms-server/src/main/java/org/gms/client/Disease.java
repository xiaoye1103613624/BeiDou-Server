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
package org.gms.client;

import org.gms.constants.game.GameConstants;
import org.gms.server.life.MobSkillType;

import java.util.Arrays;

/**
 * 【枚举】Disease：定义角色异常状态（疾病/负面效果）类型常量。
 * <p>每种异常状态对应一个位掩码值，用于在客户端状态标志中标识当前生效的异常</p>
 */
public enum Disease {
    NULL(0x0),                         // 无状态
    SLOW(0x1, MobSkillType.SLOW),      // 减速
    SEDUCE(0x80, MobSkillType.SEDUCE), // 诱惑（被控制移动）
    FISHABLE(0x100),                   // 可被钓鱼（钓鱼小游戏状态）
    ZOMBIFY(0x4000),                   // 僵尸化
    CONFUSE(0x80000, MobSkillType.REVERSE_INPUT), // 混乱（方向反转）
    STUN(0x2000000000000L, MobSkillType.STUN),    // 眩晕
    POISON(0x4000000000000L, MobSkillType.POISON), // 中毒
    SEAL(0x8000000000000L, MobSkillType.SEAL),     // 封印（无法使用技能）
    DARKNESS(0x10000000000000L, MobSkillType.DARKNESS), // 黑暗（命中率降低）
    WEAKEN(0x4000000000000000L, MobSkillType.WEAKNESS), // 虚弱
    CURSE(0x8000000000000000L, MobSkillType.CURSE);     // 诅咒

    /** 状态位掩码值 */
    private final long i;
    /** 对应的怪物技能类型（部分状态关联） */
    private final MobSkillType mobSkillType;

    Disease(long i) {
        this(i, null);
    }

    Disease(long i, MobSkillType skill) {
        this.i = i;
        this.mobSkillType = skill;
    }

    /**
     * 获取状态位掩码值
     * @return 位掩码值
     */
    public long getValue() {
        return i;
    }

    /**
     * 判断是否为首个状态（固定返回false）
     * @return false
     */
    public boolean isFirst() {
        return false;
    }

    /**
     * 获取对应的怪物技能类型
     * @return MobSkillType
     */
    public MobSkillType getMobSkillType() {
        return mobSkillType;
    }

    /**
     * 根据序号获取异常状态
     * @param ord 序号
     * @return 对应的异常状态，越界返回NULL
     */
    public static Disease ordinal(int ord) {
        try {
            return Disease.values()[ord];
        } catch (IndexOutOfBoundsException io) {
            return NULL;
        }
    }

    /**
     * 获取随机的CPQ（组队任务）异常状态
     * @return 随机异常状态
     */
    public static final Disease getRandom() {
        Disease[] diseases = GameConstants.CPQ_DISEASES;
        return diseases[(int) (Math.random() * diseases.length)];
    }

    /**
     * 根据怪物技能类型获取对应的异常状态
     * @param skill 怪物技能类型
     * @return 对应的异常状态，未找到返回null
     */
    public static final Disease getBySkill(MobSkillType skill) {
        if (skill == null) {
            return null;
        }
        return Arrays.stream(Disease.values())
                .filter(d -> d.mobSkillType == skill)
                .findAny()
                .orElse(null);
    }

}