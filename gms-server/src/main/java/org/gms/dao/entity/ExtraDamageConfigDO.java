package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 额外伤害全局配置实体（单行全局配置，id固定为1）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_extra_damage_config")
public class ExtraDamageConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    /** 额外伤害总开关（0=禁用 1=启用） */
    private Integer enabled;

    /** 触发阈值：本次普通伤害达到该值才触发额外伤害（与道具触发为"或"关系） */
    private Long damageThreshold;

    /** 道具触发开关对应的物品ID（0=不启用道具触发） */
    private Integer triggerItemId;

    private Integer strRatio;
    private Integer dexRatio;
    private Integer intRatio;
    private Integer lukRatio;
    private Integer watkRatio;
    private Integer matkRatio;
    private Integer wdefRatio;
    private Integer mdefRatio;
    private Integer hpRatio;
    private Integer mpRatio;

    /** 是否以头顶悬浮提示展示额外伤害（0=不展示 1=展示，仅本人可见） */
    private Integer showHint;

    /** 是否以系统喇叭(黄字)展示额外伤害（0=不展示 1=展示） */
    private Integer showMessage;

    private Date createTime;

    private Date updateTime;
}
