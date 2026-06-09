package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 现金商城搜索结果返回参数
 * 包含分类信息、物品详情和价格设置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashShopSearchRtnDTO {
    /** 分类ID */
    private Integer categoryId;
    /** 分类名称 */
    private String categoryName;
    /** 子分类ID */
    private Integer subcategoryId;
    /** 子分类名称 */
    private String subcategoryName;
    /** 序列号 */
    private Integer sn;
    /** 物品ID */
    private Integer itemId;
    /** 物品名称 */
    private String itemName;
    /** 价格 */
    private Integer price;
    /** 默认价格 */
    private Integer defaultPrice;
    /** 周期 */
    private Long period;
    /** 默认周期 */
    private Long defaultPeriod;
    /** 优先级 */
    private Integer priority;
    /** 默认优先级 */
    private Integer defaultPriority;
    /** 数量 */
    private Short count;
    /** 默认数量 */
    private Short defaultCount;
    /** 是否上架 */
    private Integer onSale;
    /** 默认上架状态 */
    private Integer defaultOnSale;
    /** 奖励 */
    private Integer bonus;
    private Integer defaultBonus;
    private Integer maplePoint;
    private Integer defaultMaplePoint;
    private Integer meso;
    private Integer defaultMeso;
    private Integer forPremiumUser;
    private Integer defaultForPremiumUser;
    private Integer gender;
    private Integer defaultGender;
    private Integer clz;
    private Integer defaultClz;
    private Integer limit;
    private Integer defaultLimit;
    private Integer pbCash;
    private Integer defaultPBCash;
    private Integer pbPoint;
    private Integer defaultPBPoint;
    private Integer pbGift;
    private Integer defaultPBGift;
    private Integer packageSn;
    private Integer defaultPackageSn;
}