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
 * 【枚举】Disease（enum），包 {@code org.gms.client}。
 * 
 * <p>定义角色异常状态（疾病/负面效果）类型常量，每种异常状态对应一个位掩码值，
 * 用于在客户端状态标志中标识当前生效的异常。通过位运算可以同时表示多个状态
 * 的组合，实现高效的状态管理和检查。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义游戏中各种负面状态类型</li>
 *   <li>提供位掩码值用于状态组合表示</li>
 *   <li>关联怪物技能类型以支持技能效果映射</li>
 * </ul>
 * 
 * <p>设计特点：</p>
 * <ul>
 *   <li>位掩码：每个状态使用唯一的位掩码值，支持多状态同时存在</li>
 *   <li>技能关联：部分状态与特定怪物技能类型相关联</li>
 *   <li>静态工具：提供多种便捷的查找和获取方法</li>
 * </ul>
 * 
 * <p>使用方式：</p>
 * <pre>{@code
 * // 检查角色是否处于眩晕状态
 * boolean isStunned = (activeDiseases & Disease.STUN.getValue()) != 0;
 * 
 * // 组合多个负面状态
 * long combinedDiseases = Disease.SLOW.getValue() | Disease.POISON.getValue();
 * 
 * // 根据怪物技能类型获取对应异常状态
 * Disease disease = Disease.getBySkill(MobSkillType.STUN);
 * }</pre>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public enum Disease {
    /** 无状态，位掩码值为0 */
    NULL(0x0),
    /** 减速状态，降低移动速度，关联减速技能 */
    SLOW(0x1, MobSkillType.SLOW),
    /** 诱惑状态，角色被控制移动，关联诱惑技能 */
    SEDUCE(0x80, MobSkillType.SEDUCE),
    /** 可被钓鱼状态，进入钓鱼小游戏模式 */
    FISHABLE(0x100),
    /** 僵尸化状态，角色变为僵尸形态 */
    ZOMBIFY(0x4000),
    /** 混乱状态，方向键输入反转，关联方向反转技能 */
    CONFUSE(0x80000, MobSkillType.REVERSE_INPUT),
    /** 眩晕状态，角色无法行动，关联眩晕技能 */
    STUN(0x2000000000000L, MobSkillType.STUN),
    /** 中毒状态，持续损失HP，关联中毒技能 */
    POISON(0x4000000000000L, MobSkillType.POISON),
    /** 封印状态，无法使用技能，关联封印技能 */
    SEAL(0x8000000000000L, MobSkillType.SEAL),
    /** 黑暗状态，命中率降低，关联黑暗技能 */
    DARKNESS(0x10000000000000L, MobSkillType.DARKNESS),
    /** 虚弱状态，所有能力下降，关联虚弱技能 */
    WEAKEN(0x4000000000000000L, MobSkillType.WEAKNESS),
    /** 诅咒状态，魔法防御下降并持续损失MP，关联诅咒技能 */
    CURSE(0x8000000000000000L, MobSkillType.CURSE);

    /** 状态对应的位掩码值，用于在客户端状态标志中标识当前生效的异常 */
    private final long i;
    /** 对应的怪物技能类型，部分异常状态与特定怪物技能相关联，用于技能效果映射 */
    private final MobSkillType mobSkillType;

    /**
     * 构造函数，创建不关联怪物技能的异常状态
     * 
     * @param i 状态对应的位掩码值
     */
    Disease(long i) {
        this(i, null);
    }

    /**
     * 构造函数，创建关联怪物技能的异常状态
     * 
     * @param i 状态对应的位掩码值
     * @param skill 关联的怪物技能类型，可为null表示不关联任何技能
     */
    Disease(long i, MobSkillType skill) {
        this.i = i;
        this.mobSkillType = skill;
    }

    /**
     * 获取异常状态对应的位掩码值
     * 
     * <p>此方法返回当前异常状态的唯一标识符，用于与其他状态进行位运算操作，
     * 支持多状态的同时检测和管理。</p>
     * 
     * @return 当前异常状态的位掩码值
     */
    public long getValue() {
        return i;
    }

    /**
     * 判断当前状态是否为首个生效状态（占位方法）
     * 
     * <p>此方法固定返回false，在当前实现中主要用于保持与其他状态系统的一致性，
     * 实际上异常状态没有特殊的首状态概念。</p>
     * 
     * @return 固定返回false
     */
    public boolean isFirst() {
        return false;
    }

    /**
     * 获取与此异常状态关联的怪物技能类型
     * 
     * <p>对于部分异常状态，存在对应的怪物技能类型，此方法返回关联的技能类型，
     * 便于追踪状态来源和处理相关的技能效果。</p>
     * 
     * @return 关联的怪物技能类型，若无关联则返回null
     */
    public MobSkillType getMobSkillType() {
        return mobSkillType;
    }

    /**
     * 根据枚举序号获取对应的异常状态
     * 
     * <p>此方法根据枚举常量的声明顺序（从0开始）获取对应的异常状态，
     * 若序号超出有效范围，则返回NULL状态作为安全默认值。</p>
     * 
     * @param ord 异常状态的枚举序号（从0开始）
     * @return 对应的异常状态，若序号无效则返回NULL
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
     * 
     * <p>此方法从预定义的CPQ（组队任务）可用异常状态集合中随机选择一个状态，
     * 通常用于组队任务中的随机负面效果施加。</p>
     * 
     * @return 随机选择的CPQ可用异常状态
     */
    public static final Disease getRandom() {
        Disease[] diseases = GameConstants.CPQ_DISEASES;
        return diseases[(int) (Math.random() * diseases.length)];
    }

    /**
     * 根据怪物技能类型获取对应的异常状态
     * 
     * <p>此方法遍历所有异常状态，查找与指定怪物技能类型关联的状态，
     * 若找到匹配项则返回对应的异常状态，否则返回null。</p>
     * 
     * @param skill 目标怪物技能类型
     * @return 与指定技能类型关联的异常状态，若未找到则返回null
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