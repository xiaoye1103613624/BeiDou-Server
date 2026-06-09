package org.gms.server.life;

import java.util.Arrays;
import java.util.Optional;

/**
 * 怪物技能类型枚举
 * 定义怪物可使用的所有技能类型，按技能ID范围分类：
 * 100-115：增益/治疗类技能，120-136：异常状态类技能，140-157：抗性/特殊类技能，200：召唤类技能
 * 与{@link MobSkillId}、{@link MobSkill}配合使用，构成完整的怪物技能系统
 */
public enum MobSkillType {
    /** 物理攻击上升 */
    ATTACK_UP(100),
    /** 魔法攻击上升 */
    MAGIC_ATTACK_UP(101),
    /** 物理防御上升 */
    DEFENSE_UP(102),
    /** 魔法防御上升 */
    MAGIC_DEFENSE_UP(103),
    /** 物理攻击上升（范围） */
    ATTACK_UP_M(110),
    /** 魔法攻击上升（范围） */
    MAGIC_ATTACK_UP_M(111),
    /** 物理防御上升（范围） */
    DEFENSE_UP_M(112),
    /** 魔法防御上升（范围） */
    MAGIC_DEFENSE_UP_M(113),
    /** 治疗（范围） */
    HEAL_M(114),
    /** 加速（范围） */
    HASTE_M(115),
    /** 封印 */
    SEAL(120),
    /** 黑暗 */
    DARKNESS(121),
    /** 虚弱 */
    WEAKNESS(122),
    /** 眩晕 */
    STUN(123),
    /** 诅咒 */
    CURSE(124),
    /** 中毒 */
    POISON(125),
    /** 减速 */
    SLOW(126),
    /** 驱散 */
    DISPEL(127),
    /** 魅惑 */
    SEDUCE(128),
    /** 放逐 */
    BANISH(129),
    /** 区域毒雾 */
    AREA_POISON(131),
    /** 方向反转 */
    REVERSE_INPUT(132),
    /** 不死化 */
    UNDEAD(133),
    /** 禁止喝药 */
    STOP_POTION(134),
    /** 禁止移动 */
    STOP_MOTION(135),
    /** 恐惧 */
    FEAR(136),
    /** 物理免疫 */
    PHYSICAL_IMMUNE(140),
    /** 魔法免疫 */
    MAGIC_IMMUNE(141),
    /** 皮肤硬化 */
    HARD_SKIN(142),
    /** 物理反击 */
    PHYSICAL_COUNTER(143),
    /** 魔法反击 */
    MAGIC_COUNTER(144),
    /** 物理+魔法反击 */
    PHYSICAL_AND_MAGIC_COUNTER(145),
    /** 物理攻击力 */
    PAD(150),
    /** 魔法攻击力 */
    MAD(151),
    /** 物理防御率 */
    PDR(152),
    /** 魔法防御率 */
    MDR(153),
    /** 命中率 */
    ACC(154),
    /** 回避率 */
    EVA(155),
    /** 速度 */
    SPEED(156),
    /** 技能封印 */
    SEAL_SKILL(157),
    /** 召唤 */
    SUMMON(200);

    /** 技能ID */
    private final int id;

    MobSkillType(int id) {
        this.id = id;
    }

    /**
     * 根据技能ID查找对应的技能类型
     * 先做范围校验排除无效ID，再通过流式匹配查找
     *
     * @param id 技能ID（100-200之间）
     * @return 包含技能类型的Optional，若无效则返回空
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
     * 判断ID是否超出技能ID范围
     *
     * @param id 技能ID
     * @return true表示超出范围
     */
    private static boolean isOutOfIdRange(int id) {
        return id < 100 || id > 200;
    }

    /**
     * 获取技能ID
     *
     * @return 技能ID
     */
    public int getId() {
        return id;
    }
}