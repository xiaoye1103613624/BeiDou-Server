package org.gms.model.pojo;

/**
 * 技能条目
 * 记录角色的技能等级、大师等级和过期时间
 */
public class SkillEntry {
    /** 大师等级 */
    public int masterLevel;
    /** 技能等级 */
    public byte skillLevel;
    /** 过期时间戳 */
    public long expiration;

    public SkillEntry(byte skillLevel, int masterLevel, long expiration) {
        this.skillLevel = skillLevel;
        this.masterLevel = masterLevel;
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return skillLevel + ":" + masterLevel;
    }
}