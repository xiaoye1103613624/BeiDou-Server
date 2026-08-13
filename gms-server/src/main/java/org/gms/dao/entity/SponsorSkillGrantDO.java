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
 * 赞助技能发放审计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_skill_grant")
public class SponsorSkillGrantDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer characterId;
    private Integer configId;
    private Integer rewardId;
    private Integer skillId;
    private Integer skillLevel;
    /** 实际绑定的快捷键；0=未绑定 */
    private Integer boundKey;
    private Date grantTime;
}
