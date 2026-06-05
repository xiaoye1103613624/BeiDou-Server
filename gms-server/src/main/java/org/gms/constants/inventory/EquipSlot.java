package org.gms.constants.inventory;

/**
 * 【枚举】EquipSlot，包 {@code org.gms.constants.inventory}。
 * 装备槽位常量枚举。
 *
 * <p>定义角色装备栏位及其WZ配置文件映射名称和允许的槽位编号。</p>
 *
 * <p>每个枚举值对应游戏中的一种装备槽位，包括：</p>
 * <ul>
 *   <li>帽子(HAT)</li>
 *   <li>上衣(TOP)</li>
 *   <li>裤子(PANTS)</li>
 *   <li>武器(WEAPON)</li>
 *   <li>戒指(RING)</li>
 *   <li>披风(CAPE)</li>
 *   <li>勋章(MEDAL)</li>
 *   <li>等</li>
 * </ul>
 *
 * <p>每个槽位都有对应的WZ映射名称和允许的槽位编号范围，用于装备验证和穿戴判断。</p>
 *
 * @author The Spookster (The Real Spookster)
 */
public enum EquipSlot {
    /** 帽子 - WZ映射名称Cp，允许槽位-1 */
    HAT("Cp", -1),
    /** 特殊帽子 - WZ映射名称HrCp，允许槽位-1 */
    SPECIAL_HAT("HrCp", -1),
    /** 脸饰 - WZ映射名称Af，允许槽位-2 */
    FACE_ACCESSORY("Af", -2),
    /** 眼饰 - WZ映射名称Ay，允许槽位-3 */
    EYE_ACCESSORY("Ay", -3),
    /** 耳环 - WZ映射名称Ae，允许槽位-4 */
    EARRINGS("Ae", -4),
    /** 上衣 - WZ映射名称Ma，允许槽位-5 */
    TOP("Ma", -5),
    /** 套装（上衣+裤子一体）- WZ映射名称MaPn，允许槽位-5 */
    OVERALL("MaPn", -5),
    /** 裤子 - WZ映射名称Pn，允许槽位-6 */
    PANTS("Pn", -6),
    /** 鞋子 - WZ映射名称So，允许槽位-7 */
    SHOES("So", -7),
    /** 手套 - WZ映射名称GlGw，允许槽位-8 */
    GLOVES("GlGw", -8),
    /** 现金手套 - WZ映射名称Gv，允许槽位-8 */
    CASH_GLOVES("Gv", -8),
    /** 披风 - WZ映射名称Sr，允许槽位-9 */
    CAPE("Sr", -9),
    /** 盾牌 - WZ映射名称Si，允许槽位-10 */
    SHIELD("Si", -10),
    /** 主武器 - WZ映射名称Wp，允许槽位-11 */
    WEAPON("Wp", -11),
    /** 副武器 - WZ映射名称WpSi，允许槽位-11 */
    WEAPON_2("WpSi", -11),
    /** 低级武器 - WZ映射名称WpSp，允许槽位-11 */
    LOW_WEAPON("WpSp", -11),
    /** 戒指 - WZ映射名称Ri，允许槽位-12、-13、-15、-16（多个戒指位） */
    RING("Ri", -12, -13, -15, -16),
    /** 项链/吊坠 - WZ映射名称Pe，允许槽位-17 */
    PENDANT("Pe", -17),
    /** 骑乘宠物 - WZ映射名称Tm，允许槽位-18 */
    TAMED_MOB("Tm", -18),
    /** 马鞍 - WZ映射名称Sd，允许槽位-19 */
    SADDLE("Sd", -19),
    /** 勋章 - WZ映射名称Me，允许槽位-49 */
    MEDAL("Me", -49),
    /** 腰带 - WZ映射名称Be，允许槽位-50 */
    BELT("Be", -50),
    /** 宠物装备栏 - 无特定槽位 */
    PET_EQUIP;

    /** WZ配置文件中的映射名称 */
    private String name;

    /** 允许的槽位编号数组 */
    private int[] allowed;

    /** 无参构造函数，用于无特定槽位的枚举值 */
    EquipSlot() {
    }

    /**
     * 构造函数
     * @param wz WZ配置文件映射名称
     * @param in 允许的槽位编号，可变参数
     */
    EquipSlot(String wz, int... in) {
        name = wz;
        allowed = in;
    }

    /**
     * 获取WZ配置文件映射名称
     * @return WZ映射名称
     */
    public String getName() {
        return name;
    }

    /**
     * 判断指定槽位是否允许此装备槽位
     * <p>根据槽位编号和是否为现金装备来判断是否允许穿戴。</p>
     * @param slot 槽位编号
     * @param cash 是否为现金装备
     * @return 是否允许
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
     * 根据文本槽位名称获取对应的装备槽位枚举
     * @param slot 文本槽位名称
     * @return 对应的装备槽位枚举，若未找到则返回PET_EQUIP
     */
    public static EquipSlot getFromTextSlot(String slot) {
        if (!slot.isEmpty()) {
            for (EquipSlot c : values()) {
                if (c.getName() != null) {
                    if (c.getName().equals(slot)) {
                        return c;
                    }
                }
            }
        }
        return PET_EQUIP;
    }
}