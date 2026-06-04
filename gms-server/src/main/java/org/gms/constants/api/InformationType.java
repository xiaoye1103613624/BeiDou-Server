package org.gms.constants.api;

import lombok.Getter;

/**
 * 信息类型枚举
 * <p>用于API查询时指定查询的信息类型</p>
 */
@Getter
public enum InformationType {
    /** 现金道具 */
    CASH("cash"),
    /** 消耗品 */
    CONSUME("consume"),
    /** 装备 */
    EQP("eqp"),
    /** 其他物品 */
    ETC("etc"),
    /** 内在能力 */
    INS("ins"),
    /** 地图 */
    MAP("map"),
    /** 怪物 */
    MOB("mob"),
    /** NPC */
    NPC("npc"),
    /** 宠物 */
    PET("pet"),
    /** 技能 */
    SKILL("skill"),
    ;

    /** 类型标识 */
    private final String type;

    InformationType(final String type) {
        this.type = type;
    }

    /**
     * 根据类型字符串获取枚举值
     * @param type 类型字符串
     * @return 对应的枚举值，不存在返回null
     */
    public static InformationType ofType(final String type) {
        for (InformationType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}