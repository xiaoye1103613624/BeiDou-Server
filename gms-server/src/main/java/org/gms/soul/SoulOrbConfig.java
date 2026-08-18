package org.gms.soul;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 早期 CMS/JMS 灵魂宝珠（2591000~2591009）+ 附魔石配置。
 * <p>
 * 编码约定（写在 {@code Equip.soulId}）：
 * <ul>
 *   <li>0 — 未开槽</li>
 *   <li>{@link #SLOT_OPEN} — 已开槽、未镶珠</li>
 *   <li>2591000~2591009 — 已镶对应宝珠</li>
 *   <li>2049914 + soulOption&gt;0 — 旧版随机灵魂（兼容，仍走 ItemOption）</li>
 * </ul>
 * 私服默认：镶珠 100% 成功、失败不毁装（可用开关改回官设 25%/可毁）。
 */
public final class SoulOrbConfig {
    private SoulOrbConfig() {}

    /** 已开槽标记（非物品 ID） */
    public static final int SLOT_OPEN = 1;

    /** 武器最低需求等级 */
    public static final int MIN_WEAPON_LEVEL = 75;

    /** 私服：镶珠成功率（官设早期 25） */
    public static final int ORB_SUCCESS_RATE = 100;

    /** 私服：镶珠失败是否毁装 */
    public static final boolean ORB_DESTROY_ON_FAIL = false;

    /** 简化满魂：镶珠后额外攻击%/魔法% */
    public static final int FULL_SOUL_PAD_R = 10;
    public static final int FULL_SOUL_MAD_R = 10;

    /**
     * 镶珠武器身上环绕特效（客户端 {@code Effect/ItemEff.img/2591999}，复用 SoulStandard/On）。
     * 非背包道具，仅服务端 {@code Character.itemEffect} 引用。
     */
    public static final int ITEM_EFFECT_ID = 2591999;

    public enum SkillKind {
        NONE,
        /** 最终伤害 +N%（武公：100 → ×2） */
        BUFF_FINAL_DAM,
        /** 对周围怪造成 maxBase×倍率/100 伤害 */
        AOE_DAMAGE,
        /** 对最近一只怪造成伤害 */
        SINGLE_DAMAGE
    }

    public record OrbDef(
            int itemId,
            String name,
            int strR, int dexR, int intR, int lukR,
            int damR, int bossDamR, int ignoreDef,
            int padR, int madR,
            SkillKind skill,
            int skillValue,
            int skillDurationSec,
            int skillCooldownSec,
            String skillName
    ) {}

    public record EnchanterDef(int itemId, String name, int successRate) {}

    private static final Map<Integer, OrbDef> ORBS;
    private static final Map<Integer, EnchanterDef> ENCHANTERS;

    static {
        Map<Integer, OrbDef> orbs = new LinkedHashMap<>();
        // 属性对齐早期 CMS 老版表（论坛/wiki 常见值），技能为 v083 可落地简化版
        putOrb(orbs, new OrbDef(2591000, "蝙蝠怪",
                0, 0, 0, 0, 5, 0, 0, 0, 0,
                SkillKind.AOE_DAMAGE, 1500, 0, 300, "地狱火饱嗝"));
        putOrb(orbs, new OrbDef(2591001, "暗黑龙王",
                0, 0, 0, 0, 3, 0, 0, 3, 3,
                SkillKind.AOE_DAMAGE, 1800, 0, 300, "黑龙吐息"));
        putOrb(orbs, new OrbDef(2591002, "莱格斯",
                0, 0, 0, 0, 0, 0, 10, 0, 0,
                SkillKind.AOE_DAMAGE, 1000, 0, 300, "冲撞"));
        putOrb(orbs, new OrbDef(2591003, "品克缤",
                0, 0, 0, 0, 8, 0, 0, 0, 0,
                SkillKind.AOE_DAMAGE, 1200, 0, 300, "可怕的可爱"));
        putOrb(orbs, new OrbDef(2591004, "御龙魔",
                0, 0, 3, 0, 0, 0, 0, 0, 0,
                SkillKind.AOE_DAMAGE, 1000, 0, 300, "掌心火焰"));
        putOrb(orbs, new OrbDef(2591005, "班·雷昂",
                0, 0, 0, 0, 0, 20, 0, 0, 0,
                SkillKind.SINGLE_DAMAGE, 3000, 0, 300, "猫咪法则"));
        putOrb(orbs, new OrbDef(2591006, "扎昆",
                0, 6, 0, 0, 0, 0, 0, 0, 0,
                SkillKind.AOE_DAMAGE, 1400, 0, 300, "扎昆之怒"));
        putOrb(orbs, new OrbDef(2591007, "摇滚之魂",
                0, 0, 0, 3, 0, 0, 0, 0, 0,
                SkillKind.AOE_DAMAGE, 900, 0, 300, "摇滚宝贝"));
        putOrb(orbs, new OrbDef(2591008, "武公熊猫",
                6, 0, 0, 0, 0, 0, 0, 0, 0,
                SkillKind.BUFF_FINAL_DAM, 100, 30, 300, "武公之绝对攻击"));
        putOrb(orbs, new OrbDef(2591009, "阿尼",
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                SkillKind.SINGLE_DAMAGE, 2000, 0, 300, "我在这儿"));
        ORBS = Collections.unmodifiableMap(orbs);

        Map<Integer, EnchanterDef> enc = new LinkedHashMap<>();
        // 2049914：原「附加卷」改为 100% 开槽（低风险、沿用已有物品）
        putEnc(enc, new EnchanterDef(2049914, "灵魂宝珠开槽卷", 100));
        putEnc(enc, new EnchanterDef(2590004, "普及型灵魂附魔石", 30));
        putEnc(enc, new EnchanterDef(2590005, "普及型灵魂附魔石", 30));
        putEnc(enc, new EnchanterDef(2590006, "强化型灵魂附魔石", 60));
        putEnc(enc, new EnchanterDef(2590007, "强化型灵魂附魔石", 60));
        putEnc(enc, new EnchanterDef(2590014, "强化型灵魂附魔石", 60));
        putEnc(enc, new EnchanterDef(2590020, "强化型灵魂附魔石", 60));
        putEnc(enc, new EnchanterDef(2590008, "特殊灵魂附魔石", 100));
        putEnc(enc, new EnchanterDef(2590009, "特殊灵魂附魔石", 100));
        putEnc(enc, new EnchanterDef(2590010, "特殊灵魂附魔石", 100));
        ENCHANTERS = Collections.unmodifiableMap(enc);
    }

    private static void putOrb(Map<Integer, OrbDef> map, OrbDef def) {
        map.put(def.itemId(), def);
    }

    private static void putEnc(Map<Integer, EnchanterDef> map, EnchanterDef def) {
        map.put(def.itemId(), def);
    }

    public static boolean isOrb(int itemId) {
        return ORBS.containsKey(itemId);
    }

    public static boolean isEnchanter(int itemId) {
        return ENCHANTERS.containsKey(itemId);
    }

    public static OrbDef getOrb(int itemId) {
        return ORBS.get(itemId);
    }

    public static EnchanterDef getEnchanter(int itemId) {
        return ENCHANTERS.get(itemId);
    }

    public static Set<Integer> allOrbIds() {
        return ORBS.keySet();
    }

    public static Set<Integer> allEnchanterIds() {
        return ENCHANTERS.keySet();
    }

    public static boolean isSoulFamilyItem(int itemId) {
        return isOrb(itemId) || isEnchanter(itemId) || itemId == 2049915;
    }
}
