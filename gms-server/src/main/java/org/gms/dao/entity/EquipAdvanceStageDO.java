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
 * 装备进阶阶段配置实体
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

    /** 关联路线ID */
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

    private Integer strAdd;
    private Integer dexAdd;
    private Integer intAdd;
    private Integer lukAdd;
    private Integer hpAdd;
    private Integer mpAdd;
    private Integer watkAdd;
    private Integer matkAdd;
    private Integer wdefAdd;
    private Integer mdefAdd;
    private Integer accAdd;
    private Integer avoidAdd;
    private Integer speedAdd;
    private Integer jumpAdd;

    private Date createTime;
    private Date updateTime;
}