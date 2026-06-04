package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * BOSS配置返回DTO（含WZ基础属性，供前端展示）。
 *
 * @author 萧曵
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BossConfigRtnDTO {
    /** 配置ID */
    private Long id;
    /** 怪物ID */
    private Integer mobId;
    /** BOSS名称 */
    private String bossName;

    // 倍率配置
    private BigDecimal hpMultiplier;
    private BigDecimal expMultiplier;
    private BigDecimal damageMultiplier;

    // 直接覆盖值（NULL=使用WZ默认）
    private Integer level;
    private Integer hp;
    private Integer mp;
    private Integer exp;
    private Integer pdd;
    private Integer mdd;
    private Integer acc;
    private Integer eva;

    // WZ基础属性（只读展示）
    private Integer wzLevel;
    private Integer wzHp;
    private Integer wzMp;
    private Integer wzExp;
    private Integer wzPdd;
    private Integer wzMdd;
    private Integer wzAcc;
    private Integer wzEva;
    private Integer wzPadamage;
    private Integer wzMadamage;
    private Boolean wzBoss;

    /** 是否启用 */
    private Integer enabled;
    /** 更新时间 */
    private Date updateTime;
}