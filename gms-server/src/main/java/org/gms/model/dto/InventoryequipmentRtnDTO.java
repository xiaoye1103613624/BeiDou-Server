package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;

/**
 * 装备信息返回参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryequipmentRtnDTO {
    /** 装备记录ID */
    private Long inventoryequipmentid;
    /** 物品记录ID */
    private Long inventoryitemid;
    /** 可升级次数 */
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

    private Integer mp;

    private Integer watk;

    private Integer matk;

    private Integer wdef;

    private Integer mdef;

    private Integer acc;

    private Integer avoid;

    private Integer hands;

    private Integer speed;

    private Integer jump;

    private Integer locked;

    private Long vicious;

    private Integer itemlevel;

    private Long itemexp;
}