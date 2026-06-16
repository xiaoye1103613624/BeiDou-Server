package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 独立掉落怪物配置 DTO
 * 用于前后端数据传输，映射 xy_independent_drop_config 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndependentDropSaveDTO {

    /** 主键ID（更新时必填） */
    private Long id;

    /** BOSS怪物ID */
    private Integer mobId;

    /** 怪物名称（备注用） */
    private String mobName;

    /** 是否启用独立掉落（0=禁用 1=启用） */
    private Integer enabled;
}
