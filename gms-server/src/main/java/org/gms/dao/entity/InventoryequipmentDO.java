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

    /** 白金锤已用次数（永久） */
    private Integer platinum;

    private Integer itemlevel;

    private Integer itemexp;

    private Integer ringid;

    @Column("anvilItemId")
    private Integer anvilitemid;

    /** 灵韵技能 ID */
    @Column("equipSkillId")
    private Integer equipskillid;

    /** 灵韵技能等级 */
    @Column("equipSkillLevel")
    private Integer equipskilllevel;

    /** 灵韵过期时间，0=永久 */
    @Column("equipSkillExpire")
    private Long equipskillexpire;

    @Column("potential1")
    private Integer potential1;

    @Column("potential2")
    private Integer potential2;

    @Column("potential3")
    private Integer potential3;

    @Column("potentialGrade")
    private Integer potentialgrade;

    @Column("enhance")
    private Integer enhance;

}
