package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 师徒系统配置 DTO
 * 用于前后端数据传输，映射 xy_mentor_config 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorConfigDTO {

    /** 主键ID（更新时必填） */
    private Long id;

    /** 配置键（create_master_level/max_disciples/max_be_disciple_level/graduate_level） */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置说明 */
    private String description;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;
}
