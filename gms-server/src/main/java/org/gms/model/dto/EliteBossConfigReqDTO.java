package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 精英BOSS配置请求DTO（查询列表/新增/修改）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EliteBossConfigReqDTO extends BasePageDTO {
    private Long id;
    private Integer mapId;
    private Integer bossId;
    private String bossName;
    private Integer bossTime;
    private String scriptName;
    private Integer enabled;
}
