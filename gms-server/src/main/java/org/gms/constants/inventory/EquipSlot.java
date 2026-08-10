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
    // 私服真正分槽（ADDON_AUX_SLOT62）：
    //   109 盾 → 原生 Si −10；134/135 辅助武器 → Addon 独立槽 −62（可同时穿，互不顶掉）。
    // 双手主武仍只卸 −10；纹章 119 等 Addon 禁止走 Si。
    SHIELD("Si", -10),            // 仅盾牌 109
    AUX_WEAPON("Aw", -62, -162),  // 辅助武器 134/135（Addon 第三行；非官服同槽）
    WEAPON("Wp", -11),            // 主武器（双手武器或法杖等）
    WEAPON_2("WpSi", -11),        // 单手武器（可搭配盾牌，如单手剑、单手斧）
    LOW_WEAPON("WpSp", -11),      // 低/特殊武器（如弓、弩、拳套，通常双手持握）

    // ========== 饰品（戒指 / 项链） ==========
    // 6 戒：原 −12/−13/−15/−16 + 新增 −52/−53（UI 红标 3/4）。
    // 客户端 RING_34_BIND_20260726aa：GetItem/SetItem 放行 −52/−53 + draw→BP53 + get_bodypart×6。
    RING("Ri", -12, -13, -15, -16, -52, -53),
    // 第二吊坠 −51（083 原生 BP51）。UI：pendant2_ui=true 进图后挂槽。
    PENDANT("Pe", -17, -51),       // 项链/吊坠：主 −17 + 第二 −51

    // ========== 坐骑 & 肩饰 & 勋章 & 腰带 & 口袋 ==========
    TAMED_MOB("Tm", -18),          // 驯服怪物（骑宠本体）
    SADDLE("Sd", -19),             // 鞍具（骑宠装备）
    SHOULDER("Sh", -20),           // 肩饰（115xxx；客户端 BP20；勿用 Sd/−8，与手套冲突）
    // 口袋 116xxxx：主栏红 9 (104,200) / BP33（ijl15 ADDON_FULLSLOTS_20260801）
    POCKET("Po", -33, -133),
    MEDAL("Me", -49),              // 勋章（任务/活动获得，索引在很后面）
    BELT("Be", -50),               // 腰带（也是后期加入的，排在勋章后面）


    // ========== 扩展装备栏 Addon 2×4（ijl15 ADDON_RULES_STATS_20260802）==========
    // Top: Totem×4 BP55–58（恰好 4，拒第 5）；Bot: Emblem59 / Android60 / Heart61 / Badge54
    // 口袋 BP33/−33；辅助 134/135 → −62（Addon row3）；109 → −10；徽章/图腾/纹章永不进 −10。
    // 119→−59 only（WZ islot Si 不得走 SHIELD）。
    // 注意：v083 客户端 BP21/22 = 宠物名牌/道具袋（−121/−122 亦为宠物 cash），
    // 机器人/心脏必须用 sidecar BP60/61（cash −160/−161），禁止 −21/−22。
    BADGE("Ba", -54, -154),
    TOTEM("To", -55, -56, -57, -58, -155, -156, -157, -158),
    EMBLEM("Em", -59, -159),       // 纹章 119xxxx（sidecar 可穿）
    ANDROID("Dr", -60, -160),      // 机器人 166xxxx（sidecar BP60）
    HEART("Ht", -61, -161),        // 心脏 167xxxx（sidecar BP61 / Machine Heart）

    // ========== 宠物（特殊逻辑，无索引） ==========
    PET_EQUIP,                     // 宠物装备（走宠物数据包，不走角色穿戴包，所以不需要String和int）
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

    /**
     * Dual-band seats (Badge/Totem/… list both −bp and −(bp+100)): accept <b>exact</b>
     * match so cash items can land on normal −bp (sidecar arena; GetItem normal-only).
     * Classic seats list only normal −bp: cash still matches via {@code allow - 100}.
     * <p>
     * Old {@code cash ? allow-100 : allow} rejected cash badge/aux at −54/−62 when the
     * array already contained −154/−162 (double-shift → −254/−262) — wear fail / desync.
     */
    public boolean isAllowed(int slot, boolean cash) {
        if (slot < 0 && allowed != null) {
            for (int allow : allowed) {
                if (slot == allow) {
                    return true;
                }
                // Classic cash mirror when allowed[] lists only the normal seat.
                if (cash && allow > -100 && slot == allow - 100) {
                    return true;
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
