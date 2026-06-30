package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 装备伤害加成配置DTO（单件装备）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipDamageBonusConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 装备物品ID */
    private Integer itemId;

    /** 装备名称 */
    private String itemName;

    /** 普通伤害加成百分比（如10表示+10%） */
    private Integer damagePct;

    /** Boss伤害加成百分比（仅对Boss类怪物生效） */
    private Integer bossDamagePct;

    /** 额外攻击段数（每段独立造成一次百分比加成后的单段伤害） */
    private Long fixedDamage;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;
}
