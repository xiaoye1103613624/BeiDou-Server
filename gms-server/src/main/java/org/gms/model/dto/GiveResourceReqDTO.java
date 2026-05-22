package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * GM发放资源请求DTO
 * <p>用于GM向玩家发放物品或装备的请求参数</p>
 */
@Setter
@Getter
public class GiveResourceReqDTO {
    /** 世界ID */
    private Integer worldId;
    /** 玩家角色ID */
    private Integer playerId;
    /** 玩家角色名称 */
    private String player;
    /** 发放类型（物品/装备等） */
    private Byte type;
    /** 物品ID */
    private Integer id;
    /** 物品数量 */
    private Integer quantity;
    /** 概率/比率 */
    private Float rate;
    /** 力量属性 */
    private Short str;
    /** 敏捷属性 */
    private Short dex;
    /** 智力属性 */
    @JsonProperty("int")
    private Short _int;
    /** 运气属性 */
    private Short luk;
    /** 生命值加成 */
    private Short hp;
    /** 魔法值加成 */
    private Short mp;
    /** 物理攻击 */
    @JsonProperty("pAtk")
    private Short pAtk;
    /** 魔法攻击 */
    @JsonProperty("mAtk")
    private Short mAtk;
    /** 物理防御 */
    @JsonProperty("pDef")
    private Short pDef;
    /** 魔法防御 */
    @JsonProperty("mDef")
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
    /** 可升级次数 */
    private Byte upgradeSlot;
    /** 过期时间 */
    private Long expire;
}
