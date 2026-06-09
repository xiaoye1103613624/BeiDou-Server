package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 装备信息查询返回参数
 * 返回装备的各项基础属性
 */
@Setter
@Getter
public class EquipmentInfoRtnDTO {
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
    private Short speed;
    private Short jump;
    private Byte upgradeSlot;
    private Long expire;
}