package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 【类型】EquipmentInfoRtnDTO（class），包 `org.gms.model.dto`。
 */
@Setter
@Getter
public class EquipmentInfoRtnDTO {
    /** 世界ID */
//    private Integer worldId;
    /** 玩家ID */
//    private Integer playerId;
    /** 玩家名称 */
//    private String player;
    /** 类型 */
//    private Byte type;
    /** 物品ID */
//    private Integer id;
    /** 数量 */
//    private Integer quantity;
    /** 概率/比率 */
//    private Integer rate;
    /** 力量 */
    private Short str;
    /** 敏捷 */
    private Short dex;
    /** 智力 */
    @JsonProperty("int")
    private Short _int;
    /** 运气 */
    private Short luk;
    /** 生命值 */
    private Short hp;
    /** 魔法值 */
    private Short mp;
    /** 物理攻击力 */
    private Short pAtk;
    /** 魔法攻击力 */
    private Short mAtk;
    /** 物理防御力 */
    private Short pDef;
    /** 魔法防御力 */
    private Short mDef;
    /** 命中率 */
    private Short acc;
    /** 回避率 */
    private Short avoid;
    /** 手技 */
    private Short hands;
    /** 移动速度 */
    private Short speed;
    /** 跳跃力 */
    private Short jump;
    /** 升级槽位 */
    private Byte upgradeSlot;
    /** 过期时间 */
    private Long expire;
}
