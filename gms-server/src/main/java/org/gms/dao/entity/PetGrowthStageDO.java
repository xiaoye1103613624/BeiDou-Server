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
 * 宠物成长阶段配置（喂养经验 → 进阶 → 召唤倍率）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_pet_growth_stage")
public class PetGrowthStageDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 进阶链编码 */
    private String chainCode;

    /** 阶段序号 */
    private Integer stage;

    /** 展示名 */
    private String name;

    /** 当前形态宠物物品ID */
    private Integer petId;

    /** 进阶目标宠物ID，空=终阶 */
    private Integer nextPetId;

    /** 本阶段进阶所需成长经验 */
    private Integer needExp;

    /** 每次喂养增加成长经验 */
    private Integer expPerFeed;

    /** 允许的喂养道具ID，逗号分隔；空=任意212宠物食品 */
    private String feedItemIds;

    /** 召唤时经验倍率 */
    private Double expRate;

    /** 召唤时爆率倍率 */
    private Double dropRate;

    /** 召唤时金币倍率 */
    private Double mesoRate;

    private Integer sortOrder;

    /** 0禁用 1启用 */
    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
