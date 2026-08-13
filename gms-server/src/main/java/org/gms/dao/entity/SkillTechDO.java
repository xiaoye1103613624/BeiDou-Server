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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_skill_tech")
public class SkillTechDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Integer skillId;
    private String skillName;
    /** 手动加点上限（原始最高等级） */
    private Integer spMaxLevel;
    /** 效果最高等级（可高于 spMaxLevel） */
    private Integer effectMaxLevel;
    /** 等级属性覆盖 JSON */
    private String levelsJson;
    private Integer enabled;
    private Integer clientSynced;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
