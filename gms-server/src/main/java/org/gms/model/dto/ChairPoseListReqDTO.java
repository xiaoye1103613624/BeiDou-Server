package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChairPoseListReqDTO {
    /** 关键字：ID 或名称模糊 */
    private String keyword;
    /** 仅含 effect2 */
    private Boolean effect2Only;
    private Integer page;
    private Integer pageSize;
}
