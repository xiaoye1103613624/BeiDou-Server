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
 * 装备进阶阶段配置实体（每个路线的每个阶段定义目标装备和属性加成）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_equip_advance_stage")
public class EquipAdvanceStageDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联 xy_equip_advance_route.id */
    private Long routeId;

    /** 阶段顺序（0=初始装备，1=一阶，2=二阶...） */
    private Integer stageOrder;

    /** 该阶段目标装备ID */
    private Integer targetItemId;

    /** 目标装备名称 */
    private String targetItemName;

    /** 金币消耗 */
    private Integer mesoCost;

    /** 点卷消耗 */
    private Integer cashCost;

    /** 抵用券消耗 */
    private Integer creditCost;

    /** 力量加成 */
    private Integer strAdd;
    /** 敏捷加成 */
    private Integer dexAdd;
    /** 智力加成 */
    private Integer intAdd;
    /** 运气加成 */
    private Integer lukAdd;
    /** HP加成 */
    private Integer hpAdd;
    /** MP加成 */
    private Integer mpAdd;
    /** 物理攻击加成 */
    private Integer watkAdd;
    /** 魔法攻击加成 */
    private Integer matkAdd;
    /** 物理防御加成 */
    private Integer wdefAdd;
    /** 魔法防御加成 */
    private Integer mdefAdd;
    /** 命中加成 */
    private Integer accAdd;
    /** 回避加成 */
    private Integer avoidAdd;
    /** 速度加成 */
    private Integer speedAdd;
    /** 跳跃加成 */
    private Integer jumpAdd;

    private Date createTime;
    private Date updateTime;
}
