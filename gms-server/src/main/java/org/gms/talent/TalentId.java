package org.gms.talent;

/**
 * 天赋枚举（与脚本 9031014.js 的 TalentId.* 调用一一对应）。
 * 脚本经 Nashorn 调用 {@code id()}/{@code itemId()}/{@code tier()}，故额外暴露同名方法。
 */
public enum TalentId {
    // PRIMARY（精神）
    SPIRIT_VITALITY(20001, TalentTier.PRIMARY),
    SPIRIT_BLOODLINE(20002, TalentTier.PRIMARY),
    SPIRIT_INSTINCT(20003, TalentTier.PRIMARY),
    SPIRIT_BLESSING(20004, TalentTier.PRIMARY),
    SPIRIT_LIGHTWEIGHT(20005, TalentTier.PRIMARY),
    SPIRIT_GLORY(20006, TalentTier.PRIMARY),
    SPIRIT_AWAKENING(20007, TalentTier.PRIMARY),

    // MID（力量）
    STRENGTH_RAGE(20008, TalentTier.MID),
    STRENGTH_SHIELD(20009, TalentTier.MID),
    STRENGTH_BROKEN(20010, TalentTier.MID),
    STRENGTH_CRUSHING(20011, TalentTier.MID),
    STRENGTH_GIGANTIC(20012, TalentTier.MID),
    STRENGTH_RESONANCE(20013, TalentTier.MID),
    STRENGTH_MIRROR(20014, TalentTier.MID),

    // ADVANCED（财富）
    FORTUNE_LUCKY(20015, TalentTier.ADVANCED),
    FORTUNE_TREASURE(20016, TalentTier.ADVANCED),
    FORTUNE_THIEF(20017, TalentTier.ADVANCED),
    FORTUNE_GREED(20018, TalentTier.ADVANCED),
    FORTUNE_INVEST(20019, TalentTier.ADVANCED),
    FORTUNE_HALLOWEEN(20020, TalentTier.ADVANCED),
    FORTUNE_RECOVERY(20021, TalentTier.ADVANCED),

    // ULTIMATE（智慧）
    WISDOM_SAGE(20022, TalentTier.ULTIMATE),
    WISDOM_WILLPOWER(20023, TalentTier.ULTIMATE),
    WISDOM_MAGIC(20024, TalentTier.ULTIMATE),
    WISDOM_DETERMINATION(20025, TalentTier.ULTIMATE),
    WISDOM_ATTACK(20026, TalentTier.ULTIMATE),
    WISDOM_REGRET(20027, TalentTier.ULTIMATE),
    WISDOM_BALANCE(20028, TalentTier.ULTIMATE);

    private final int id;
    private final TalentTier tier;

    TalentId(int id, TalentTier tier) {
        this.id = id;
        this.tier = tier;
    }

    public int getId() {
        return id;
    }

    public TalentTier getTier() {
        return tier;
    }

    /** Nashorn 脚本直接调用：{@code book.id()} */
    public int id() {
        return id;
    }

    /** Nashorn 脚本直接调用：{@code book.itemId()} */
    public int itemId() {
        return tier.getItemId();
    }

    /** Nashorn 脚本直接调用：{@code book.tier()} */
    public TalentTier tier() {
        return tier;
    }

    public static TalentId fromId(int id) {
        for (TalentId t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

    /** 由各阶层「消耗道具」反查对应天赋（取该阶层第一个作为兑换目标）。 */
    public static TalentId fromItemId(int itemId) {
        for (TalentId t : values()) {
            if (t.getTier().getItemId() == itemId) {
                return t;
            }
        }
        return null;
    }
}
