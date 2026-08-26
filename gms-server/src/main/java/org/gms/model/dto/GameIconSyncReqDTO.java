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
public class GameIconSyncReqDTO {
    /** 小册子版本，默认 227 */
    private Integer version;
    /** 地区，默认 GMS */
    private String region;
    /** true=覆盖已有；false=仅补缺失 */
    private Boolean force;
    /** 限定分类：mob / item；空=两者 */
    private List<String> categories;
    /** 仅同步指定怪物相关（该怪 + 其掉落物品） */
    private Integer dropperId;
    /** 直接指定物体 ID 列表（配合 categories） */
    private List<Integer> objectIds;
    /** true=从抽奖奖池收集 itemId（可配 lotteryNpcId） */
    private Boolean fromLottery;
    /** 仅同步该抽奖机 NPC 的奖品图标；空=全部抽奖机 */
    private Integer lotteryNpcId;
}
