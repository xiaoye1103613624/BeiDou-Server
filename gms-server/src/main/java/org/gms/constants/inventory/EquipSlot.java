package org.gms.constants.inventory;

/**
 * 装备槽位枚举类，定义游戏中所有可装备物品的槽位类型。
 * <p>
 * 每个槽位对应 wz 数据文件中的标识名称，并定义了允许装备的槽位编号范围。
 * 槽位编号规则：负值表示普通装备槽，负值减 100 表示现金装备槽（如 -1 对应普通帽子，-101 对应现金帽子）。
 *
 * @author The Spookster (The Real Spookster)
 */
public enum EquipSlot {

    /** 帽子槽位 */
    HAT("Cp", -1),
    /** 特殊帽子槽位（如节日帽子） */
    SPECIAL_HAT("HrCp", -1),
    /** 脸部装饰槽位 */
    FACE_ACCESSORY("Af", -2),
    /** 眼部装饰槽位 */
    EYE_ACCESSORY("Ay", -3),
    /** 耳环槽位 */
    EARRINGS("Ae", -4),
    /** 上衣槽位 */
    TOP("Ma", -5),
    /** 套装槽位（上下衣一体） */
    OVERALL("MaPn", -5),
    /** 裤子槽位 */
    PANTS("Pn", -6),
    /** 鞋子槽位 */
    SHOES("So", -7),
    /** 手套槽位 */
    GLOVES("GlGw", -8),
    /** 现金手套槽位 */
    CASH_GLOVES("Gv", -8),
    /** 披风槽位 */
    CAPE("Sr", -9),
    /** 盾牌槽位 */
    SHIELD("Si", -10),
    /** 武器槽位 */
    WEAPON("Wp", -11),
    /** 副手武器槽位（如盾+武器组合） */
    WEAPON_2("WpSi", -11),
    /** 低等级武器槽位 */
    LOW_WEAPON("WpSp", -11),
    /**
     * 戒指槽位（支持4个原生戒指位置：-12, -13, -15, -16）
     * 以及3个扩展槛位：-90/-91（戒指5/6）、-92（图腾，复用戒指校验逻辑，无独立wz类别）。
     * 扩展槛位仅由客户端ImGui插件渲染与交互，原生客户端UI不可见。
     */
    RING("Ri", -12, -13, -15, -16, -90, -91, -92),
    /** 项链槽位 */
    PENDANT("Pe", -17),
    /** 骑宠槽位 */
    TAMED_MOB("Tm", -18),
    /** 鞍座槽位 */
    SADDLE("Sd", -19),
    /** 勋章槽位 */
    MEDAL("Me", -49),
    /** 腰带槽位 */
    BELT("Be", -50),
    /** 宠物装备槽位（无 wz 名称和固定槽位编号） */
    PET_EQUIP;

    /** wz 数据文件中的槽位标识名称 */
    private String name;

    /** 允许装备的槽位编号数组 */
    private int[] allowed;

    /**
     * 默认构造函数，用于无特定槽位的装备类型（如宠物装备）
     */
    EquipSlot() {
    }

    /**
     * 构造函数
     *
     * @param wz  wz 数据文件中的槽位标识名称
     * @param in 允许装备的槽位编号列表
     */
    EquipSlot(String wz, int... in) {
        name = wz;
        allowed = in;
    }

    /**
     * 获取 wz 数据文件中的槽位标识名称
     *
     * @return wz 槽位名称
     */
    public String getName() {
        return name;
    }

    /**
     * 检查指定槽位是否允许装备当前类型的物品
     *
     * @param slot 槽位编号（负值为普通槽，负值减100为现金槽）
     * @param cash 是否为现金装备
     * @return 如果允许装备返回 true，否则返回 false
     */
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

    /**
     * 根据 wz 文本槽位名称获取对应的枚举值
     *
     * @param slot wz 槽位标识名称
     * @return 对应的 EquipSlot 枚举值，未找到则返回 PET_EQUIP
     */
    public static EquipSlot getFromTextSlot(String slot) {
        if (slot != null && !slot.isEmpty()) {
            for (EquipSlot c : values()) {
                if (c.getName() != null && c.getName().equals(slot)) {
                    return c;
                }
            }
        }
        return PET_EQUIP;
    }
}