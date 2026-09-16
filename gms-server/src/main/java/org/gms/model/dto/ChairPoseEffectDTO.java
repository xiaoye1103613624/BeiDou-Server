package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 椅子 effect / effect2 层姿态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChairPoseEffectDTO {
    /** effect | effect2 */
    private String layer;
    private Integer originX;
    private Integer originY;
    private Integer pos;
    private Integer z;
    /** 首帧 canvas 尺寸（只读提示） */
    private Integer canvasWidth;
    private Integer canvasHeight;
    private Integer frameOriginX;
    private Integer frameOriginY;
    private Integer frameZ;
}
