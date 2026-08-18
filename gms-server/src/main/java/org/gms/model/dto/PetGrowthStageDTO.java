package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 宠物成长阶段配置 DTO（管理后台）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetGrowthStageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String chainCode;
    private Integer stage;
    private String name;
    private Integer petId;
    private Integer nextPetId;
    private Integer needExp;
    private Integer expPerFeed;
    private String feedItemIds;
    private Double expRate;
    private Double dropRate;
    private Double mesoRate;
    private Integer sortOrder;
    private Integer enabled;

    /** 预览：当前宠服务端 WZ 是否存在 */
    private Boolean petExists;
    /** 预览：下一形态服务端 WZ 是否存在 */
    private Boolean nextPetExists;
    /** 预览：物品名（来自 String/Item） */
    private String petNameResolved;
    private String nextPetNameResolved;
}
