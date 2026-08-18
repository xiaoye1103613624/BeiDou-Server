package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 赞助技能组可选技能
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_skill_option")
public class SponsorSkillOptionDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /** 所属奖励行（type=skill_group） */
    private Integer rewardId;

    private Integer skillId;

    /** 0 = 发放时按技能最大等级 */
    private Integer skillLevel;

    /** 默认快捷键；0 = 自动找空闲偏好键 */
    private Integer defaultKey;

    private Integer sortOrder;

    private Date createTime;
}
