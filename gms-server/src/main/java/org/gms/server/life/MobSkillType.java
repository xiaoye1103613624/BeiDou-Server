package org.gms.server.life;

import java.util.Arrays;
import java.util.Optional;

public enum MobSkillType {
    ATTACK_UP(100),
    MAGIC_ATTACK_UP(101),
    DEFENSE_UP(102),
    MAGIC_DEFENSE_UP(103),
    ATTACK_UP_M(110),
    MAGIC_ATTACK_UP_M(111),
    DEFENSE_UP_M(112),
    MAGIC_DEFENSE_UP_M(113),
    HEAL_M(114),
    HASTE_M(115),
    SEAL(120),
    DARKNESS(121),
    WEAKNESS(122),
    STUN(123),
    CURSE(124),
    POISON(125),
    SLOW(126),
    DISPEL(127),
    SEDUCE(128),
    BANISH(129),
    AREA_POISON(131),
    REVERSE_INPUT(132),
    UNDEAD(133),
    STOP_POTION(134),
    STOP_MOTION(135),
    FEAR(136),
    PHYSICAL_IMMUNE(140),
    MAGIC_IMMUNE(141),
    HARD_SKIN(142),
    PHYSICAL_COUNTER(143),
    MAGIC_COUNTER(144),
    PHYSICAL_AND_MAGIC_COUNTER(145),
    PAD(150),
    MAD(151),
    PDR(152),
    MDR(153),
    ACC(154),
    EVA(155),
    SPEED(156),
    SEAL_SKILL(157),
    SUMMON(200),
    // 高版本扩展技能 (186版新增, 占位待实现具体效果)
    SUMMON_BOMB(201),   // 召唤炸弹/爆炸召唤物 (186版154个怪物使用)
    SUMMON_MULTI(202),  // 多重召唤 (186版14个怪物使用)
    UNKNOWN_207(207);   // 未知高版本技能 (186版27个怪物使用)

    // 扩展上限从200→210以支持高版本

    private final int id;

    MobSkillType(int id) {
        this.id = id;
    }

    public static Optional<MobSkillType> from(int id) {
        if (isOutOfIdRange(id)) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(type -> type.getId() == id)
                .findAny();
    }

    private static boolean isOutOfIdRange(int id) {
        // 扩展上限200→210以支持186版新技能ID(201/202/207)
        return id < 100 || id > 210;
    }

    public int getId() {
        return id;
    }
}
