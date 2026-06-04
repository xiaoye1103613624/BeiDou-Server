package org.gms.server.life;

import java.util.Arrays;
import java.util.Optional;

/**
 * 【枚举】MobSkillType：定义怪物技能类型常量。
 * <p>技能类型分为以下几大类：</p>
 * <ul>
 *   <li>100-103: 基础属性增益（攻击/魔攻/防御/魔防）</li>
 *   <li>110-115: 群体增益（群体攻击/魔攻/防御/魔防/治疗/加速）</li>
 *   <li>120-136: 负面状态（封印/黑暗/虚弱/眩晕/诅咒/中毒/减速/驱散/诱惑/放逐/区域毒雾/混乱/僵尸/禁药/定身/恐惧）</li>
 *   <li>140-145: 免疫与反伤（物理免疫/魔法免疫/硬化皮肤/物理反伤/魔法反伤/双重反伤）</li>
 *   <li>150-157: 属性强化（PAD/MAD/PDR/MDR/命中/回避/速度/封印技能）</li>
 *   <li>200: 召唤技能</li>
 * </ul>
 */
public enum MobSkillType {
    ATTACK_UP(100),           // 物理攻击力提升
    MAGIC_ATTACK_UP(101),     // 魔法攻击力提升
    DEFENSE_UP(102),          // 物理防御力提升
    MAGIC_DEFENSE_UP(103),    // 魔法防御力提升
    ATTACK_UP_M(110),         // 群体物理攻击力提升
    MAGIC_ATTACK_UP_M(111),   // 群体魔法攻击力提升
    DEFENSE_UP_M(112),        // 群体物理防御力提升
    MAGIC_DEFENSE_UP_M(113),  // 群体魔法防御力提升
    HEAL_M(114),              // 群体治疗
    HASTE_M(115),             // 群体加速
    SEAL(120),                // 封印（无法使用技能）
    DARKNESS(121),            // 黑暗（降低命中）
    WEAKNESS(122),            // 虚弱（降低攻击力）
    STUN(123),                // 眩晕（无法移动和攻击）
    CURSE(124),               // 诅咒
    POISON(125),              // 中毒（持续伤害）
    SLOW(126),                // 减速
    DISPEL(127),              // 驱散（移除增益效果）
    SEDUCE(128),              // 诱惑（强制移动）
    BANISH(129),              // 放逐（传送出地图）
    AREA_POISON(131),         // 区域毒雾
    REVERSE_INPUT(132),       // 操作反转
    UNDEAD(133),              // 僵尸化
    STOP_POTION(134),         // 禁止使用药水
    STOP_MOTION(135),         // 定身
    FEAR(136),                // 恐惧
    PHYSICAL_IMMUNE(140),     // 物理免疫
    MAGIC_IMMUNE(141),        // 魔法免疫
    HARD_SKIN(142),           // 硬化皮肤
    PHYSICAL_COUNTER(143),    // 物理反伤
    MAGIC_COUNTER(144),       // 魔法反伤
    PHYSICAL_AND_MAGIC_COUNTER(145), // 双重反伤
    PAD(150),                 // 物理攻击力
    MAD(151),                 // 魔法攻击力
    PDR(152),                 // 物理防御力
    MDR(153),                 // 魔法防御力
    ACC(154),                 // 命中
    EVA(155),                 // 回避
    SPEED(156),               // 速度
    SEAL_SKILL(157),          // 封印技能
    SUMMON(200);              // 召唤怪物

    /** 技能类型ID */
    private final int id;

    MobSkillType(int id) {
        this.id = id;
    }

    /**
     * 根据ID获取技能类型枚举
     * @param id 技能类型ID
     * @return Optional包装的技能类型，不存在则返回空
     */
    public static Optional<MobSkillType> from(int id) {
        if (isOutOfIdRange(id)) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(type -> type.getId() == id)
                .findAny();
    }

    /**
     * 检查ID是否在有效范围内（100-200）
     * @param id 技能类型ID
     * @return true=超出范围, false=在范围内
     */
    private static boolean isOutOfIdRange(int id) {
        return id < 100 || id > 200;
    }

    /**
     * 获取技能类型ID
     * @return 技能类型ID
     */
    public int getId() {
        return id;
    }
}