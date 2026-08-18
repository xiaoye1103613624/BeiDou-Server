package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobDropGroupDTO {
    private Integer dropperId;
    private String dropperName;
    private Integer dropCount;
    /** 已持久化图标地址；未同步则为空 */
    private String mobIconUrl;
    private Boolean hasIcon;
}
