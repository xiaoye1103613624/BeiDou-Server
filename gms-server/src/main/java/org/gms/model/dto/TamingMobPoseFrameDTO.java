package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 骑宠某一动作帧上的坐姿锚点（Character.wz/TamingMob canvas）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TamingMobPoseFrameDTO {
    private String action;
    private Integer frameIndex;
    private Integer navelX;
    private Integer navelY;
    private Integer originX;
    private Integer originY;
    private String z;
    private Integer canvasWidth;
    private Integer canvasHeight;
}
