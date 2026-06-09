package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 发放资源请求参数
 * GM后台向指定玩家发放各种资源（物品、金币、属性点等）
 */
@Setter
@Getter
public class GiveResourceReqDTO {
    /** 世界ID */
    private Integer worldId;
    /** 玩家ID */
    private Integer playerId;
    /** 玩家名称 */
    private String player;
    /** 资源类型 */
    private Byte type;
    /** 物品ID */
    private Integer id;
    /** 数量 */
    private Integer quantity;
    /** 倍率 */
    private Float rate;
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
    /** 魔力值 */
    private Short mp;
    /** 物理攻击力 */
    @JsonProperty("pAtk")
    private Short pAtk;
    /** 魔法攻击力 */
    @JsonProperty("mAtk")
    private Short mAtk;
    /** 物理防御力 */
    @JsonProperty("pDef")
    private Short pDef;
    /** 魔法防御力 */
    @JsonProperty("mDef")
    private Short mDef;
    private Short acc;
    private Short avoid;
    private Short hands;
    private Short speed;
    private Short jump;
    private Byte upgradeSlot;
    private Long expire;
}