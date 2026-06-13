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
 * 师门实体类
 * <p>
 * 记录已创建师门的师父角色。
 * 角色达到指定等级后可创建师门，成为师父后方可收徒。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_mentor_master")
public class MentorMasterDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 师父角色ID（FK → characters.id） */
    private Integer characterId;

    /** 创建师门时间 */
    private LocalDateTime createTime;
}
