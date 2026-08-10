package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 技能组选项视图（供 JS：getId / getSkillId / getSkillLevel / getDefaultKey / getName / getMaxLevel）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorSkillOptionView {

    private int id;
    private int skillId;
    /** 配置等级；0 表示最大等级 */
    private int skillLevel;
    private int defaultKey;
    private int sortOrder;
    /** String.wz 技能名；未知时为空串 */
    private String name;
    /** SkillFactory 最大等级 */
    private int maxLevel;
}
