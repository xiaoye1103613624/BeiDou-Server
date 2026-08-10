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

    /** 洗炼词条1：高16位=affixOrdinal 低16位=prefixLv */
    private Integer reforge1;
    /** 洗炼词条2 */
    private Integer reforge2;
    /** 洗炼词条3 */
    private Integer reforge3;
    /** 洗炼锁定位掩码 */
    private Integer reforgeLock;

    /** 注能等级 0~10（⚡） */
    private Integer infusion;

    /** 宝石镶嵌等级 0~16（宝X） */
    private Integer gemInlay;
    /** 每级2bit水晶类型 0力量/1敏捷/2智慧/3幸运 */
    private Integer gemTypes;

    /** 破界等级 0~50 */
    private Integer breakthrough;
    /** 破界 13 属性激活掩码 */
    private Integer breakthroughPool;

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

    @Column("bonusPotential1")
    private Integer bonuspotential1;

    @Column("bonusPotential2")
    private Integer bonuspotential2;

    @Column("bonusPotential3")
    private Integer bonuspotential3;

    @Column("bonusPotentialGrade")
    private Integer bonuspotentialgrade;

    @Column("soulId")
    private Integer soulid;

    @Column("soulOption")
    private Integer souloption;

    @Column("socket1")
    private Integer socket1;

    @Column("socket2")
    private Integer socket2;

    @Column("socket3")
    private Integer socket3;

    @Column("chaosStr")
    private Integer chaosstr;
    @Column("chaosDex")
    private Integer chaosdex;
    @Column("chaosInt")
    private Integer chaosint;
    @Column("chaosLuk")
    private Integer chaosluk;
    @Column("chaosHp")
    private Integer chaoshp;
    @Column("chaosMp")
    private Integer chaosmp;
    @Column("chaosWatk")
    private Integer chaoswatk;
    @Column("chaosMatk")
    private Integer chaosmatk;
    @Column("chaosWdef")
    private Integer chaoswdef;
    @Column("chaosMdef")
    private Integer chaosmdef;
    @Column("chaosAcc")
    private Integer chaosacc;
    @Column("chaosAvoid")
    private Integer chaosavoid;
    @Column("chaosSpeed")
    private Integer chaosspeed;
    @Column("chaosJump")
    private Integer chaosjump;
    @Column("exGradeOption")
    private Long exgradeoption;

}
