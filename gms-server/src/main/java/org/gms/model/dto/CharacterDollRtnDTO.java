package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 本地人偶合成结果。
 * <p>
 * 坐标模型（与游戏内绘制一致）：<b>精灵左上 = attach − origin</b>。
 * <ul>
 *   <li>{@code width/height}：合成画布尺寸，WZ 1:1 像素。</li>
 *   <li>{@code bodyOriginX/Y}：身体原点（角色脚底锚）相对合成 PNG 左上角。</li>
 *   <li>{@code navelX/Y}：肚脐锚点相对合成 PNG 左上角，坐骑 {@code map/navel} 的挂载点。</li>
 * </ul>
 * 座椅预览把 {@code bodyOrigin} 对齐椅子 effect 挂载点；坐骑预览把 {@code navel} 对齐坐骑 navel。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDollRtnDTO {
    /** DOLL | NONE — NONE 表示无法合成本地人偶（缺客户端像素），不带 imageUrl。 */
    private String mode;
    private String imageUrl;
    private Integer width;
    private Integer height;
    private Integer bodyOriginX;
    private Integer bodyOriginY;
    private Integer navelX;
    private Integer navelY;
    /** 实际叠加顺序（节点名 + z），用于校验与排障。 */
    private List<String> zOrder;
    /** 缺像素的部位节点路径（多为客户端对应 .img 缺失）。 */
    private List<String> missingParts;
    /** 外观缓存键（skin-face-hair-equips 的稳定摘要）。 */
    private String lookKey;
    private String message;
}
