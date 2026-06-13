package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 师徒系统配置实体类
 * <p>
 * 存储师徒系统的各项可配置参数，
 * 如创建师门等级、最大收徒数、拜师等级上限、出师等级等。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_mentor_config")
public class MentorConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 配置键（create_master_level=创建师门所需等级, max_disciples=最大收徒数, max_be_disciple_level=可拜师最高等级, graduate_level=出师所需等级） */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置说明 */
    private String description;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;
}
