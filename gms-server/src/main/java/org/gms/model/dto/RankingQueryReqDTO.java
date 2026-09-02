package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingQueryReqDTO {
    /** null / 0 / -1 = 总榜；战力榜为 jobNiche（job DIV 100）；装备榜为 slotCategory */
    private Integer filter;
    /** 返回条数，默认 20，最大 50 */
    private Integer limit;
}
