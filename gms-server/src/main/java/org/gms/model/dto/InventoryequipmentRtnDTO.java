package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;

/**
 * 装备表查询返回DTO
 * <p>包含装备详情表的完整属性信息</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryequipmentRtnDTO {

    /** 装备记录ID */
    private Long inventoryequipmentid;

    /** 关联的物品记录ID */
    private Long inventoryitemid;

    /** 砸券次数 */
    private Integer upgradeslots;

    /** 装备等级 */
    private Integer level;

    /** 力量 */
    private Integer str;

    /** 敏捷 */
    private Integer dex;

    /** 智力 */
    @Column("int")
    private Integer inte;

    /** 运气 */
    private Integer luk;

    /** 生命值 */
    private Integer hp;

    /** 魔法值 */
    private Integer mp;

    /** 物理攻击 */
    private Integer watk;

    /** 魔法攻击 */
    private Integer matk;

    /** 物理防御 */
    private Integer wdef;

    /** 魔法防御 */
    private Integer mdef;

    /** 命中率 */
    private Integer acc;

    /** 回避率 */
    private Integer avoid;

    /** 手技 */
    private Integer hands;

    /** 移动速度 */
    private Integer speed;

    /** 跳跃力 */
    private Integer jump;

    /** 锁定状态 */
    private Integer locked;

    /** 黄金铁锤次数 */
    private Long vicious;

    /** 装备升级等级 */
    private Integer itemlevel;

    /** 装备升级经验 */
    private Long itemexp;
}