package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写回骑宠坐姿锚点。
 * <p>
 * {@code writeMode}：{@code currentFrame} | {@code action} | {@code allActions}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TamingMobPoseWriteReqDTO {
    private Integer mobId;
    /** currentFrame | action | allActions */
    private String writeMode;
    private String action;
    private Integer frameIndex;
    private Integer navelX;
    private Integer navelY;
    /** 可选：同步写 origin */
    private Integer originX;
    private Integer originY;
    /** 可选：canvas z（字符串，如 tamingMobMid） */
    private String z;
}
