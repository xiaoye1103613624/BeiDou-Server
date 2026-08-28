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

    CAPE("Sr", -9, -109),         // 披风/斗篷（−109 = cash mirror）



    // ========== 副手 & 武器 ==========

    // 私服真正分槽：109 盾 → 原生 Si −10；134/135 辅助武器 → sidecar −62，

    // UI 在经典装备栏鞋右侧（非 Addon）。双手主武仍只卸 −10；纹章 119 禁止走 Si。

    SHIELD("Si", -10),            // 仅盾牌 109

    AUX_WEAPON("Aw", -62, -162),  // 辅助武器 134/135（经典装备栏红10）

    WEAPON("Wp", -11),            // 主武器（双手武器或法杖等）

    WEAPON_2("WpSi", -11),        // 单手武器（可搭配盾牌，如单手剑、单手斧）

    LOW_WEAPON("WpSp", -11),      // 低/特殊武器（如弓、弩、拳套，通常双手持握）



    // ========== 饰品（戒指 / 项链） ==========

    // 6 戒：原 −12/−13/−15/−16 + 新增 −52/−53（UI 红标 3/4）。

    // 客户端 RING_34_BIND_20260726aa：GetItem/SetItem 放行 −52/−53 + draw→BP53 + get_bodypart×6。

    RING("Ri", -12, -13, -15, -16, -52, -53, -152, -153),

    // 第二吊坠 −51（083 原生 BP51）。UI：pendant2_ui=true 进图后挂槽。

    PENDANT("Pe", -17, -51),       // 项链/吊坠：主 −17 + 第二 −51



    // ========== 坐骑 & 肩饰 & 勋章 & 腰带 & 口袋 ==========

    TAMED_MOB("Tm", -18),          // 驯服怪物（骑宠本体）

    SADDLE("Sd", -19),             // 鞍具（骑宠装备）

    SHOULDER("Sh", -20),           // 肩饰（115xxx；客户端 BP20；勿用 Sd/−8，与手套冲突）

    // 口袋 116xxxx：主栏红 9 (104,200) / BP33（经典装备栏，不走 Addon 第三行）

    POCKET("Po", -33, -133),

    MEDAL("Me", -49),              // 勋章（任务/活动获得，索引在很后面）

    BELT("Be", -50),               // 腰带（也是后期加入的，排在勋章后面）





    // ========== 扩展装备栏 Addon 2×4 ==========

    // Top: Totem×4 BP55–58；Bot: Emblem59 / Android60 / Heart61 / Badge54

    // 口袋 BP33/−33、辅助 −62 在经典装备栏；109 → −10；徽章/图腾/纹章永不进 −10。

    // 119→−59 only（WZ islot Si 不得走 SHIELD）。

    // 注意：v083 客户端 BP21/22 = 宠物名牌/道具袋（−121/−122 亦为宠物 cash），

    // 机器人/心脏必须用 sidecar BP60/61（cash −160/−161），禁止 −21/−22。

    BADGE("Ba", -54, -154),

    TOTEM("To", -55, -56, -57, -58, -155, -156, -157, -158),

    EMBLEM("Em", -59, -159),       // 纹章 119xxxx（sidecar 可穿）

    ANDROID("Dr", -60, -160),      // 机器人 166xxxx（sidecar BP60）

    HEART("Ht", -61, -161),        // 心脏 167xxxx（sidecar BP61 / Machine Heart）



    // ========== 宠物（特殊逻辑，无 WZ islot 字符串） ==========

    PET_EQUIP,                     // 宠物装备（180–183；仅宠物 BP 存储位）

    ;



    /**

     * Vanilla {@code is_correct_bodypart} pet seats (180–183). Storage is −bp

     * (normal) or −(bp+100) (cash). Character fashion must NEVER match these.

     */

    private static final int[] PET_BODY_PARTS = {

            14,

            21, 22, 23, 24, 25, 26, 27, 28, 29,

            30, 31, 32, 33, 34, 35, 36, 37, 38,

            39, 40, 41, 42, 43, 44, 45, 46, 47, 48

    };



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



    /** True if equipped inventory position is a pet-equip storage seat. */

    public static boolean isPetEquipStorageSlot(int slot) {

        if (slot >= 0) {

            return false;

        }

        int bp = -slot;

        if (bp >= 100 && bp <= 199) {

            bp -= 100;

        }

        for (int petBp : PET_BODY_PARTS) {

            if (petBp == bp) {

                return true;

            }

        }

        return false;

    }



    /**

     * Dual-band seats (Badge/Totem/… list both −bp and −(bp+100)): accept <b>exact</b>

     * match so cash items can land on normal −bp (sidecar arena; GetItem normal-only).

     * Classic seats list only normal −bp: cash still matches via {@code allow - 100}.

     * <p>

     * FORBIDDEN catch-all {@code cash && slot < 0}: that let cash hats (−101 expected)

     * land on pet seats (−121/−122/…) — 「帽子时装装备到宠物」.

     */

    public boolean isAllowed(int slot, boolean cash) {

        if (this == PET_EQUIP) {

            return isPetEquipStorageSlot(slot);

        }

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

            return false;

        }

        return false;

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


