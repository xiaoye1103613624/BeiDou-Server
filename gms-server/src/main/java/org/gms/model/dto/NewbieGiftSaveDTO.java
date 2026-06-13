package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 新手礼包保存DTO（含物品和货币奖励，一次性传输完整配置）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewbieGiftSaveDTO {

    /** 配置ID（新增时为空） */
    private Long id;

    /** 礼包名称 */
    private String giftName;

    /** 最低领取等级 */
    private Integer minLevel;

    /** 最高领取等级 */
    private Integer maxLevel;

    /** 是否启用 */
    private Integer enabled;

    /** 物品奖励列表 */
    private List<ItemDTO> items;

    /** 货币奖励列表 */
    private List<CurrencyDTO> currencies;

    /** 物品奖励内嵌DTO */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private Long id;
        private Integer itemId;
        private Integer quantity;
    }

    /** 货币奖励内嵌DTO */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyDTO {
        private Long id;
        /** 货币类型（meso/cash/credit） */
        private String currencyType;
        private Integer amount;
    }
}
