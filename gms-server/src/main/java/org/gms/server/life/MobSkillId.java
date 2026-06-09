package org.gms.server.life;

/**
 * 怪物技能标识
 * 使用record类型组合技能类型和技能等级，作为怪物技能的唯一标识
 *
 * @param type 技能类型（{@link MobSkillType}）
 * @param level 技能等级
 */
public record MobSkillId(MobSkillType type, int level) {}