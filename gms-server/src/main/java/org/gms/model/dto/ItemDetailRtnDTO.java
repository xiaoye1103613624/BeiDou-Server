package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物品详情查询返回参数
 * 包含物品的基本信息、价格、属性、穿戴要求等详细数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDetailRtnDTO {
    // ========== 基本信息 ==========
    /** 物品ID */
    private Integer itemId;
    /** 物品名称 */
    private String name;
    /** 物品描述 */
    private String desc;
    /** 物品类型（cash/consume/eqp/etc/ins/pet） */
    private String type;

    // ========== 通用属性 ==========
    /** 单价（消耗品按单价出售） */
    private Double unitPrice;
    /** 总价（装备整件价格） */
    private Integer wholePrice;
    /** 最大堆叠数（0表示不可堆叠） */
    private Integer slotMax;
    /** 是否为道具类物品 */
    private Boolean cashItem;

    // ========== 限制标记 ==========
    /** 是否为任务物品 */
    private Boolean questItem;
    /** 是否不可交易 */
    private Boolean untradeable;
    /** 是否账号绑定 */
    private Boolean accountRestricted;
    /** 是否不可丢弃 */
    private Boolean dropRestricted;

    // ========== 装备扩展属性 ==========
    /** 力量加成 */
    private Short str;
    /** 敏捷加成 */
    private Short dex;
    /** 智力加成 */
    @JsonProperty("int")
    private Short intVal;
    /** 运气加成 */
    private Short luk;
    /** HP加成 */
    private Short hp;
    /** MP加成 */
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
    /** 升级次数（剩余卷轴强化槽位） */
    private Integer upgradeSlots;

    // ========== 装备穿戴要求 ==========
    /** 穿戴等级要求 */
    private Integer reqLevel;
    /** 穿戴力量要求 */
    private Integer reqStr;
    /** 穿戴敏捷要求 */
    private Integer reqDex;
    /** 穿戴智力要求 */
    private Integer reqInt;
    /** 穿戴运气要求 */
    private Integer reqLuk;
    /** 穿戴职业要求（位掩码） */
    private Integer reqJob;

    // ========== 装备额外属性 ==========
    /** 是否为现金装备 */
    private Boolean equipCash;
    /** 是否可升级 */
    private Boolean upgradeable;
}
