package org.gms.talent;

import java.util.HashMap;
import java.util.Map;

/**
 * 天赋条目。talentId = itemId，便于书本一一对应。
 */
public enum TalentId {
    // —— 初级 ——
    PHYS_BASIC(4461001, TalentTier.PRIMARY, 10, "物理基础"),
    HUNT_WAY(4461002, TalentTier.PRIMARY, 10, "狩猎之道"),
    GRUDGE_REBORN(4461003, TalentTier.PRIMARY, 10, "怨念重生"),
    MAGIC_BASIC(4461004, TalentTier.PRIMARY, 10, "魔法基础"),
    TOUGH_FORCE(4461005, TalentTier.PRIMARY, 10, "坚韧之力"),
    FOX_FORCE(4461006, TalentTier.PRIMARY, 10, "灵狐之力"),
    BODY_BOOST(4461007, TalentTier.PRIMARY, 10, "体能强化"),
    MP_BOOST(4461008, TalentTier.PRIMARY, 10, "魔力强化"),
    PAIN_TRAIN(4461009, TalentTier.PRIMARY, 10, "痛苦训练"),
    MP_SUPPRESS(4461010, TalentTier.PRIMARY, 10, "魔力抑制"),
    BLEED_SUPPRESS(4461011, TalentTier.PRIMARY, 10, "流血抑制"),
    STUN_RESIST(4461012, TalentTier.PRIMARY, 10, "昏迷抵抗"),
    WEAKEN_RESIST(4461013, TalentTier.PRIMARY, 10, "虚弱抵抗"),
    SEAL_RESIST(4461014, TalentTier.PRIMARY, 10, "封印抵抗"),
    DARK_RESIST(4461015, TalentTier.PRIMARY, 10, "黑暗抵抗"),

    // —— 中级 ——
    PHYS_ENHANCE(4462001, TalentTier.MID, 10, "物理强化"),
    HUNT_WILL(4462002, TalentTier.MID, 10, "狩猎意志"),
    MAGIC_ENHANCE(4462003, TalentTier.MID, 10, "魔法强化"),
    TOUGH_WILL(4462004, TalentTier.MID, 10, "坚韧意志"),
    FOX_WILL(4462005, TalentTier.MID, 10, "灵狐意志"),
    DEATH_REBORN(4462006, TalentTier.MID, 10, "死而后生"),
    BODY_TRAIN(4462007, TalentTier.MID, 10, "体能锻炼"),
    MP_TRAIN(4462008, TalentTier.MID, 10, "魔力锻炼"),
    SEAL_IMMUNE(4462009, TalentTier.MID, 10, "封印无效"),
    DARK_IMMUNE(4462010, TalentTier.MID, 10, "黑暗无效"),
    WEAKEN_IMMUNE(4462011, TalentTier.MID, 10, "虚弱无效"),
    STUN_IMMUNE(4462012, TalentTier.MID, 10, "昏迷无效"),
    THORN_ARMOR(4462013, TalentTier.MID, 10, "荆棘之甲"),
    MP_SUPER(4462014, TalentTier.MID, 10, "魔力超导"),
    COAGULATE(4462015, TalentTier.MID, 10, "凝血抗性"),

    // —— 高级 ——
    FISSION(4463001, TalentTier.ADVANCED, 10, "裂变"),
    GRANDMASTER(4463002, TalentTier.ADVANCED, 10, "大宗师"),
    SPIRIT_REFRESH(4463003, TalentTier.ADVANCED, 10, "精神焕发"),
    LONE_STAR(4463004, TalentTier.ADVANCED, 10, "天煞孤星"),

    // —— 终极 ——
    SHARP_EYES(4464001, TalentTier.ULTIMATE, 30, "火眼晶晶"),
    STANCE(4464002, TalentTier.ULTIMATE, 30, "稳如泰山");

    private final int id;
    private final TalentTier tier;
    private final int maxLevel;
    private final String displayName;

    private static final Map<Integer, TalentId> BY_ID = new HashMap<>();

    static {
        for (TalentId t : values()) {
            BY_ID.put(t.id, t);
        }
    }

    TalentId(int id, TalentTier tier, int maxLevel, String displayName) {
        this.id = id;
        this.tier = tier;
        this.maxLevel = maxLevel;
        this.displayName = displayName;
    }

    public int id() {
        return id;
    }

    /** 天赋书物品 ID（与 talentId 相同）。 */
    public int itemId() {
        return id;
    }

    public TalentTier tier() {
        return tier;
    }

    public int maxLevel() {
        return maxLevel;
    }

    public String displayName() {
        return displayName;
    }

    public static TalentId fromId(int talentId) {
        return BY_ID.get(talentId);
    }

    public static TalentId fromItemId(int itemId) {
        return BY_ID.get(itemId);
    }
}
