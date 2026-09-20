package org.gms.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 本地人偶合成请求。
 * <p>
 * 人偶不再依赖外部 CDN 整体渲染图，而是用仓库 {@code Character.wz} 的部位节点 + 客户端像素，
 * 服务端按真实 WZ 1:1 坐标合成（见 {@link CharacterDollRtnDTO} 的锚点语义）。
 */
@Data
public class CharacterDollReqDTO {
    /**
     * 皮肤 ID，决定基础件：
     * <ul>
     *   <li>身体/四肢：{@code Character.wz/0000{skinId}.img.xml}</li>
     *   <li>头底：{@code Character.wz/0001{skinId}.img.xml}</li>
     * </ul>
     * 默认 2000。
     */
    private Integer skinId;

    /** 脸型 itemId（{@code Character.wz/Face}），默认 20000。 */
    private Integer faceId;

    /** 发型 itemId（{@code Character.wz/Hair}），默认 30000。 */
    private Integer hairId;

    /** 装备 itemId 列表（Cap / Coat / Longcoat / Pants / Shoes / Glove / Weapon ...）。 */
    private List<Integer> equipIds;

    /** 姿势：{@code sit | stand1 | walk1 ...}，默认 sit。 */
    private String pose;

    /** 帧序号，默认 0。 */
    private Integer frame;

    /** true 时忽略合成缓存重新渲染（如刚改过 WZ 想要立刻生效）。 */
    private Boolean refresh;
}
