package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 套装伤害加成配置DTO（套装ID + 件数档位）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetDamageBonusConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 套装ID(对应Etc.wz/SetItemInfo.img节点ID) */
    private Integer setItemId;

    /** 套装名称 */
    private String setName;

    /** 生效所需穿戴件数档位 */
    private Integer tierCount;

    /** 普通伤害加成百分比（如10表示+10%） */
    private Integer damagePct;

    /** Boss伤害加成百分比（仅对Boss类怪物生效） */
    private Integer bossDamagePct;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;
}
