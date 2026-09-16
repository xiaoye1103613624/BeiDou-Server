package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChairPosePatchReqDTO {
    /** true=仅导出；false=尝试写入客户端。缺省按接口强制。 */
    private Boolean dryRun;
    /** 可选：限制导出的椅子 ID；空=整份 0301 Install */
    private List<Integer> itemIds;
    /** 可选：限制导出的骑宠 ID；空=目录下全部 TamingMob */
    private List<Integer> mobIds;
}
