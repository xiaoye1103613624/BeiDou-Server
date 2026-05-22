package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 现金商城商品查询返回DTO
 * <p>包含现金商城商品的完整信息，包括当前值和默认值对比</p>
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
    /** 商品序列号 */
    private Integer sn;
    /** 物品ID */
    private Integer itemId;
    /** 物品名称 */
    private String itemName;
    /** 当前价格 */
    private Integer price;
    /** 默认价格 */
    private Integer defaultPrice;
    /** 当前有效期 */
    private Long period;
    /** 默认有效期 */
    private Long defaultPeriod;
    /** 当前优先级 */
    private Integer priority;
    /** 默认优先级 */
    private Integer defaultPriority;
    /** 当前数量 */
    private Short count;
    /** 默认数量 */
    private Short defaultCount;
    /** 当前上架状态 */
    private Integer onSale;
    /** 默认上架状态 */
    private Integer defaultOnSale;
    /** 当前奖励值 */
    private Integer bonus;
    /** 默认奖励值 */
    private Integer defaultBonus;
    /** 当前枫币价格 */
    private Integer maplePoint;
    /** 默认枫币价格 */
    private Integer defaultMaplePoint;
    /** 当前金币价格 */
    private Integer meso;
    /** 默认金币价格 */
    private Integer defaultMeso;
    /** 当前高级用户专属标识 */
    private Integer forPremiumUser;
    /** 默认高级用户专属标识 */
    private Integer defaultForPremiumUser;
    /** 当前性别限制 */
    private Integer gender;
    /** 默认性别限制 */
    private Integer defaultGender;
    /** 当前职业限制 */
    private Integer clz;
    /** 默认职业限制 */
    private Integer defaultClz;
    /** 当前购买限制 */
    private Integer limit;
    /** 默认购买限制 */
    private Integer defaultLimit;
    /** 当前现金返还 */
    private Integer pbCash;
    /** 默认现金返还 */
    private Integer defaultPBCash;
    /** 当前积分返还 */
    private Integer pbPoint;
    /** 默认积分返还 */
    private Integer defaultPBPoint;
    /** 当前礼物返还 */
    private Integer pbGift;
    /** 默认礼物返还 */
    private Integer defaultPBGift;
    /** 当前礼包序列号 */
    private Integer packageSn;
    /** 默认礼包序列号 */
    private Integer defaultPackageSn;
}
