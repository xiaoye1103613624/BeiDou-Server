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

/**
 * 【枚举】BuffStat：定义角色 Buff/Debuff 状态类型常量。
 * <p>每个状态对应一个位掩码值，用于在客户端状态标志中标识当前生效的状态</p>
 * <p>isFirst 标记表示该状态需要特殊处理（如首次应用时的额外逻辑）</p>
 */
public enum BuffStat {
    // ======== 基础 Buff ========
    MORPH(0x2L),              // 变形（如怪物变身）
    RECOVERY(0x4L),           // 自动恢复（HP/MP持续恢复）
    MAPLE_WARRIOR(0x8L),      // 冒险岛勇士（全属性提升）
    STANCE(0x10L),            // 稳如泰山（抵抗击退）
    SHARP_EYES(0x20L),        // 精准（命中率提升）
    MANA_REFLECTION(0x40L),   // 魔法反射
    SHADOW_CLAW(0x100L),      // 暗影爪（暗影攻击加成）
    INFINITY(0x200L),         // 无限魔力（MP消耗减少）
    HOLY_SHIELD(0x400L),      // 神圣护盾
    HAMSTRING(0x800L),        // 断筋（减速敌人）
    BLIND(0x1000L),           // 致盲（降低命中率）
    CONCENTRATE(0x2000L),     // 集中精力（暴击率提升）
    PUPPET(0x4000L),          // 傀儡（召唤傀儡吸引仇恨）
    ECHO_OF_HERO(0x8000L),    // 英雄回声（经验加成）
    MESO_UP_BY_ITEM(0x10000L),// 金币加成（物品效果）
    GHOST_MORPH(0x20000L),    // 幽灵变形
    AURA(0x40000L),           // 光环
    CONFUSE(0x80000L),        // 混乱（方向反转）

    // ======== 优惠券 Buff ========
    COUPON_EXP1(0x100000L),   // 经验加成券1
    EXP_BUFF(0x40000000L),    // 经验Buff
    COUPON_EXP2(0x200000L),   // 经验加成券2
    COUPON_EXP3(0x400000L),   // 经验加成券3
    COUPON_EXP4(0x400000L),   // 经验加成券4
    COUPON_DRP1(0x800000L),   // 掉宝加成券1
    COUPON_DRP2(0x1000000L),  // 掉宝加成券2
    COUPON_DRP3(0x1000000L),  // 掉宝加成券3

    // ======== 怪物卡片 Buff ========
    ITEM_UP_BY_ITEM(0x100000L),      // 物品掉落加成
    RESPECT_PIMMUNE(0x200000L),      // 物理免疫
    RESPECT_MIMMUNE(0x400000L),      // 魔法免疫
    DEFENSE_ATT(0x800000L),          // 防御提升
    DEFENSE_STATE(0x1000000L),       // 防御状态

    // ======== 属性 Buff ========
    HPREC(0x2000000L),        // HP恢复
    MPREC(0x4000000L),        // MP恢复
    BERSERK_FURY(0x8000000L), // 狂战士之怒
    DIVINE_BODY(0x10000000L), // 神圣身体
    SPARK(0x20000000L),       // 火花（攻击力提升）
    MAP_CHAIR(0x40000000L),   // 椅子状态
    FINALATTACK(0x80000000L), // 最终攻击
    WATK(0x100000000L),       // 物理攻击力
    WDEF(0x200000000L),       // 物理防御力
    MATK(0x400000000L),       // 魔法攻击力
    MDEF(0x800000000L),       // 魔法防御力
    ACC(0x1000000000L),       // 命中率
    AVOID(0x2000000000L),     // 回避率
    HANDS(0x4000000000L),     // 手技（装备限制解除）
    SPEED(0x8000000000L),     // 移动速度
    JUMP(0x10000000000L),     // 跳跃力
    MAGIC_GUARD(0x20000000000L),     // 魔法护盾（用MP抵挡伤害）
    DARKSIGHT(0x40000000000L),       // 黑暗视野（隐身）
    BOOSTER(0x80000000000L),         // 技能加速
    POWERGUARD(0x100000000000L),     // 伤害反弹
    HYPERBODYHP(0x200000000000L),    // 超级身体（HP上限提升）
    HYPERBODYMP(0x400000000000L),    // 超级身体（MP上限提升）
    INVINCIBLE(0x800000000000L),     // 无敌
    SOULARROW(0x1000000000000L),     // 灵魂箭
    STUN(0x2000000000000L),          // 眩晕
    POISON(0x4000000000000L),        // 中毒
    SEAL(0x8000000000000L),          // 封印（无法使用技能）
    DARKNESS(0x10000000000000L),     // 黑暗（命中率降低）
    COMBO(0x20000000000000L),        // 连击
    SUMMON(0x20000000000000L),       // 召唤物
    WK_CHARGE(0x40000000000000L),    // 骑士冲锋
    DRAGONBLOOD(0x80000000000000L),  // 龙血
    HOLY_SYMBOL(0x100000000000000L), // 神圣符号（经验分享）
    MESOUP(0x200000000000000L),      // 金币提升
    SHADOWPARTNER(0x400000000000000L),// 影子分身
    PICKPOCKET(0x800000000000000L),  // 扒窃
    MESOGUARD(0x1000000000000000L),  // 金币护盾
    EXP_INCREASE(0x2000000000000000L),// 经验增加
    WEAKEN(0x4000000000000000L),     // 虚弱
    MAP_PROTECTION(0x8000000000000000L), // 地图保护

    // ======== 特殊状态（isFirst=true） ========
    SLOW(0x200000000L, true),          // 减速
    ELEMENTAL_RESET(0x200000000L, true),// 元素重置
    MAGIC_SHIELD(0x400000000L, true),  // 魔法护盾
    MAGIC_RESISTANCE(0x800000000L, true),// 魔法抵抗
    WIND_WALK(0x400000000L, true),     // 风步（飞侠隐身）
    ARAN_COMBO(0x1000000000L, true),   // 战神连击
    COMBO_DRAIN(0x2000000000L, true),  // 连击吸血
    COMBO_BARRIER(0x4000000000L, true),// 连击屏障
    BODY_PRESSURE(0x8000000000L, true),// 身体压力
    SMART_KNOCKBACK(0x10000000000L, true), // 智能击退
    BERSERK(0x20000000000L, true),     // 狂暴
    ENERGY_CHARGE(0x4000000000000L, true), // 能量充能
    DASH2(0x8000000000000L, true),     // 二段冲刺（速度）
    DASH(0x10000000000000L, true),     // 冲刺（跳跃）
    MONSTER_RIDING(0x20000000000000L, true), // 骑乘怪物
    SPEED_INFUSION(0x40000000000000L, true), // 速度注入
    HOMING_BEACON(0x80000000000000L, true); // 归航信标

    /** 状态位掩码值 */
    private final long i;
    /** 是否需要特殊处理 */
    private final boolean isFirst;

    BuffStat(long i, boolean isFirst) {
        this.i = i;
        this.isFirst = isFirst;
    }

    BuffStat(long i) {
        this.i = i;
        this.isFirst = false;
    }

    /**
     * 获取状态位掩码值
     * @return 位掩码值
     */
    public long getValue() {
        return i;
    }

    /**
     * 判断是否需要特殊处理
     * @return true=需要特殊处理
     */
    public boolean isFirst() {
        return isFirst;
    }

    @Override
    public String toString() {
        return name();
    }
}