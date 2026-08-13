package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 炼药师配方配置实体（炼制物品/材料/消耗，后台可配置）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_alchemist_recipe")
public class AlchemistRecipeDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 所需炼药师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师 */
    private Integer tierRequired;

    /** 炼制产出物品ID */
    private Integer resultItemId;

    /** 炼制产出物品数量 */
    private Integer resultCount;

    /** 炼制成功增加的炼药师经验 */
    private Integer expGain;

    /** 炼制消耗体力（账号通用体力池） */
    private Integer staminaCost;

    /** 炼制消耗金币 */
    private Long mesoCost;

    /** 材料1物品ID */
    private Integer material1ItemId;

    /** 材料1所需数量 */
    private Integer material1Count;

    /** 材料2物品ID */
    private Integer material2ItemId;

    /** 材料2所需数量 */
    private Integer material2Count;

    /** 材料3物品ID */
    private Integer material3ItemId;

    /** 材料3所需数量 */
    private Integer material3Count;

    /** 材料4物品ID */
    private Integer material4ItemId;

    /** 材料4所需数量 */
    private Integer material4Count;

    /** 材料5物品ID */
    private Integer material5ItemId;

    /** 材料5所需数量 */
    private Integer material5Count;

    /** 同品级内显示排序 */
    private Integer sortOrder;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}