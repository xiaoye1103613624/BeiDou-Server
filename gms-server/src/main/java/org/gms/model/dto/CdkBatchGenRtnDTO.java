package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CDK批量生成返回DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkBatchGenRtnDTO {

    /** 批次号（用于后续分组查询） */
    private String batchNo;

    /** 成功生成数量 */
    private Integer totalCount;

    /** 生成的兑换码列表 */
    private List<String> codeList;
}
