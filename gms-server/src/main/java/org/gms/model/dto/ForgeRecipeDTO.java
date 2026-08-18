package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 打造配方配置DTO（管理后台增删改查用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgeRecipeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 配方名称(展示用，如：智慧戒指Ⅶ) */
    private String name;

    /** 所需锻造师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师 */
    private Integer tierRequired;

    /** 打造产出装备ID */
    private Integer resultItemId;

    /** 打造成功增加的锻造师经验 */
    private Integer expGain;

    /** 打造消耗金币 */
    private Long mesoCost;

    /** 打造消耗体力 */
    private Integer staminaCost;

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

    /** 材料6物品ID */
    private Integer material6ItemId;

    /** 材料6所需数量 */
    private Integer material6Count;

    /** 材料7物品ID */
    private Integer material7ItemId;

    /** 材料7所需数量 */
    private Integer material7Count;

    /** 材料8物品ID */
    private Integer material8ItemId;

    /** 材料8所需数量 */
    private Integer material8Count;

    /** 力量默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer strMin;

    /** 力量默认随机区间上限 */
    private Integer strMax;

    /** 敏捷默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer dexMin;

    /** 敏捷默认随机区间上限 */
    private Integer dexMax;

    /** 智力默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer intMin;

    /** 智力默认随机区间上限 */
    private Integer intMax;

    /** 运气默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer lukMin;

    /** 运气默认随机区间上限 */
    private Integer lukMax;

    /** 攻击力默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer watkMin;

    /** 攻击力默认随机区间上限 */
    private Integer watkMax;

    /** 魔攻默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer matkMin;

    /** 魔攻默认随机区间上限 */
    private Integer matkMax;

    /** 物理防御力默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer pddMin;

    /** 物理防御力默认随机区间上限 */
    private Integer pddMax;

    /** 魔法防御力默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer mddMin;

    /** 魔法防御力默认随机区间上限 */
    private Integer mddMax;

    /** MaxHP默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer hpMin;

    /** MaxHP默认随机区间上限 */
    private Integer hpMax;

    /** MaxMP默认随机区间下限(0表示该配方不涉及此属性) */
    private Integer mpMin;

    /** MaxMP默认随机区间上限 */
    private Integer mpMax;

    /** 同品级内显示排序 */
    private Integer sortOrder;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;
}
