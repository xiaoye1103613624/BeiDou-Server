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
 * 装备强化等级配置实体（定义每个强化等级的属性加成和消耗）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_equip_enhance_level")
public class EquipEnhanceLevelDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联 xy_equip_enhance_config.id */
    private Long configId;

    /** 强化等级（1~N） */
    private Integer enhanceLevel;

    /** 成功率（0~100） */
    private Integer successRate;

    /** 失败是否销毁装备（0=保留 1=销毁） */
    private Integer destroyOnFail;

    /** 金币消耗 */
    private Integer mesoCost;

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
