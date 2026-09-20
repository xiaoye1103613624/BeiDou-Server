package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 技能 effect 单帧（WZ 元数据 + 抽出的 PNG URL）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillEffectFrameDTO {
    private Integer index;
    /** DumpPoseFrame 节点路径，如 {@code 1121008/effect/0}。 */
    private String nodePath;
    /** 缓存层名：effect 或 effect_{variant}。 */
    private String layer;
    private Integer originX;
    private Integer originY;
    /** 毫秒；缺省由服务端填 120。 */
    private Integer delay;
    private Integer width;
    private Integer height;
    private String imageUrl;
}
