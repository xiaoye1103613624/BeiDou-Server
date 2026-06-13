package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 师徒关系实体类
 * <p>
 * 记录师父与徒弟之间的绑定关系。
 * 每个徒弟同时只能有一位师父（disciple_character_id 唯一约束）。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_mentor_relationship")
public class MentorRelationshipDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 师父角色ID（FK → characters.id） */
    private Integer masterCharacterId;

    /** 徒弟角色ID（FK → characters.id，唯一约束，一个徒弟只能有一个师父） */
    private Integer discipleCharacterId;

    /** 关系状态（0=在师门中 1=已出师 2=已退出） */
    private Integer status;

    /** 拜师时间 */
    private LocalDateTime createTime;

    /** 出师时间 */
    private LocalDateTime graduateTime;
}
