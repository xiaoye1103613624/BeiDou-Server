package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 *  实体类。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventoryequipment")
public class InventoryequipmentDO implements Serializable  {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long inventoryequipmentid;

    private Long inventoryitemid;

    private Integer upgradeslots;

    private Integer level;

    private Integer str;

    private Integer dex;

    @Column("int")
    private Integer inte;

    private Integer luk;

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

    private Integer vicious;

    private Integer itemlevel;

    private Integer itemexp;

    // 095 潜能/魔方：3 条主潜能 + 2 条 bonus 潜能（值为 PotentialOption 行 id，0 = 空）。
    private Integer potential1;

    private Integer potential2;

    private Integer potential3;

    private Integer bonusPotential1;

    private Integer bonusPotential2;

    private Integer ringid;

}
