package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryEquipRtnDTO {
    /**
     * 自增id，对应inventoryequipmentid
     */
    private Long id;
    /**
     * 外键，关联inventoryitems表id
     */
    private Long inventoryItemId;
    /**
     * 砸券次数，对应upgradeslots
     */
    private Byte upgradeSlots;
    /**
     * 装备等级，对应level
     */
    private Byte level;
    /**
     * 力量，对应str
     */
    private Short attStr;
    /**
     * 敏捷，对应dex
     */
    private Short attDex;
    /**
     * 智力，对应int
     */
    private Short attInt;
    /**
     * 运气，对应luk
     */
    private Short attLuk;
    /**
     * 血量，对应hp
     */
    private Short hp;
    /**
     * 蓝量，对应mp
     */
    private Short mp;
    /**
     * 物理攻击，对应watk
     */
    private Short pAtk;
    /**
     * 魔法攻击，对应matk
     */
    private Short mAtk;
    /**
     * 物理防御，对应wdef
     */
    private Short pDef;
    /**
     * 魔法防御，对应mDef
     */
    private Short mDef;
    /**
     * 命中，对应acc
     */
    private Short acc;
    /**
     * 回避，对应avoid
     */
    private Short avoid;
    /**
     * 攻速，对应hands
     */
    private Short hands;
    /**
     * 移速，对应speed
     */
    private Short speed;
    /**
     * 跳跃力，对应jump
     */
    private Short jump;
    /**
     * 锁定，对应locked
     */
    private Integer locked;
    /**
     * 锤子次数，对应vicious
     */
    private Short vicious;
    /**
     * 装备升级等级，对应itemlevel
     */
    private Byte itemLevel;
    /**
     * 装备升级经验，对应itemexp
     */
    private Integer itemExp;
    /**
     * 戒指id，对应ringid
     */
    private Integer ringId;
    /**
     * 幻化外观源装备 id，对应 anvilItemId
     */
    private Integer anvilItemId;
    /**
     * 灵韵技能 ID
     */
    private Integer equipSkillId;
    /**
     * 灵韵技能等级
     */
    private Integer equipSkillLevel;
    /**
     * 灵韵过期时间，0=永久
     */
    private Long equipSkillExpire;
    /** 潜能 1~3 */
    private Integer potential1;
    private Integer potential2;
    private Integer potential3;
    /** 潜能品阶 */
    private Integer potentialGrade;
    /** Hyper 星级 */
    private Integer enhance;
    /** 附加潜能 */
    private Integer bonusPotential1;
    private Integer bonusPotential2;
    private Integer bonusPotential3;
    private Integer bonusPotentialGrade;
    /** 灵魂 / 星岩 */
    private Integer soulId;
    private Integer soulOption;
    private Integer socket1;
    private Integer socket2;
    private Integer socket3;
    /** 白金锤已用次数 */
    private Integer platinum;
    /** 洗炼 */
    private Integer reforge1;
    private Integer reforge2;
    private Integer reforge3;
    private Integer reforgeLock;
    /** 注能等级 0~10（⚡） */
    private Integer infusion;
    /** 宝石镶嵌等级（宝X），0~16 */
    private Integer gemInlay;
    /** 破界等级 0~50 */
    private Integer breakthrough;
    /** 破界 13 属性激活掩码 */
    private Integer breakthroughPool;
    /** 混沌卷累计（可负）；橙桶 = 本体增量 − 混沌 */
    private Integer chaosStr;
    private Integer chaosDex;
    private Integer chaosInt;
    private Integer chaosLuk;
    private Integer chaosHp;
    private Integer chaosMp;
    private Integer chaosWatk;
    private Integer chaosMatk;
    private Integer chaosWdef;
    private Integer chaosMdef;
    private Integer chaosAcc;
    private Integer chaosAvoid;
    private Integer chaosSpeed;
    private Integer chaosJump;
}
