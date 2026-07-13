package org.gms.constants.inventory;

/**
 * @author The Spookster (The Real Spookster)
 */
public enum EquipSlot {

    // ========== 头部 ==========
    HAT("Cp", -1),                // 普通帽子/头盔
    SPECIAL_HAT("HrCp", -1),      // 特殊帽子（如扎昆头盔、各种活动头饰）

    // ========== 面部 & 眼部 & 耳部 ==========
    FACE_ACCESSORY("Af", -2),     // 脸饰（眼罩、口罩等）
    EYE_ACCESSORY("Ay", -3),      // 眼饰（眼镜、瞳孔装饰等）
    EARRINGS("Ae", -4),           // 耳环

    // ========== 上衣 & 套服 & 下衣 ==========
    TOP("Ma", -5),                // 上衣（仅上半身）
    OVERALL("MaPn", -5),          // 套服（连体衣，同时占用上衣和下衣位）
    PANTS("Pn", -6),              // 裤子/下衣

    // ========== 鞋 & 手套 & 披风 ==========
    SHOES("So", -7),              // 鞋子
    GLOVES("GlGw", -8),           // 普通手套
    CASH_GLOVES("Gv", -8),        // 现金/点装手套（外观覆盖）
    CAPE("Sr", -9),               // 披风/斗篷

    // ========== 副手 & 武器 ==========
    SHIELD("Si", -10),            // 盾牌（战士/法师副手）
    WEAPON("Wp", -11),            // 主武器（双手武器或法杖等）
    WEAPON_2("WpSi", -11),        // 单手武器（可搭配盾牌，如单手剑、单手斧）
    LOW_WEAPON("WpSp", -11),      // 低/特殊武器（如弓、弩、拳套，通常双手持握）

    // ========== 饰品（戒指 / 项链） ==========
    RING("Ri", -12, -13, -15, -16), // 戒指（4个栏位，跳过了 -14，留给远古龙神戒指或特殊活动位）
    PENDANT("Pe", -17),            // 项链/吊坠

    // ========== 坐骑 & 勋章 & 腰带 ==========
    TAMED_MOB("Tm", -18),          // 驯服怪物（骑宠本体）
    SADDLE("Sd", -19),             // 鞍具（骑宠装备）
    MEDAL("Me", -49),              // 勋章（任务/活动获得，索引在很后面）
    BELT("Be", -50),               // 腰带（也是后期加入的，排在勋章后面）

    // ========== 宠物（特殊逻辑，无索引） ==========
    PET_EQUIP,                     // 宠物装备（走宠物数据包，不走角色穿戴包，所以不需要String和int）

    //SHOULDER("Sd", -8),   // 肩膀（部分高版本用此代码，但注意会和手套的 -8 冲突，实际需单独分配新索引）
    //ANDROID("Dr", -21),   // 机器人（部分版本）
    //HEART("Ht", -22);     // 心脏（机器人心脏）
    ;

    private String name;
    private int[] allowed;

    EquipSlot() {
    }

    EquipSlot(String wz, int... in) {
        name = wz;
        allowed = in;
    }

    public String getName() {
        return name;
    }

    public boolean isAllowed(int slot, boolean cash) {
        if (slot < 0) {
            if (allowed != null) {
                for (Integer allow : allowed) {
                    int condition = cash ? allow - 100 : allow;
                    if (slot == condition) {
                        return true;
                    }
                }
            }
        }
        return cash && slot < 0;
    }

    public static EquipSlot getFromTextSlot(String slot) {
        if (slot == null || slot.isEmpty()) {
            return PET_EQUIP;
        }
        for (EquipSlot c : values()) {
            if (c.getName() != null) {
                if (c.getName().equals(slot)) {
                    return c;
                }
            }
        }
        return PET_EQUIP;
    }
}
