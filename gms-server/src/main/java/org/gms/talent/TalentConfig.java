package org.gms.talent;

import org.gms.server.life.Monster;

import java.util.Collections;
import java.util.Set;

/**
 * 天赋全局常量与野外精英判定。
 */
public final class TalentConfig {
    private TalentConfig() {}

    /** 所有天赋掉宝来源合计加成上限（百分点）。 */
    public static final int DROP_BONUS_CAP_PERCENT = 20;

    /** 裂变溅射范围（像素）。 */
    public static final int FISSION_RANGE = 175;

    /** 死而后生冷却（毫秒）。 */
    public static final long DEATH_REBORN_COOLDOWN_MS = 180_000L;

    /** 痛苦训练经验转化冷却。 */
    public static final long PAIN_TRAIN_COOLDOWN_MS = 1_000L;

    /** 终极 buff 刷新时长（毫秒）。 */
    public static final int ULTIMATE_BUFF_DURATION_MS = 30_000;

    /** 兑换材料。 */
    public static final int MAT_PRIMARY = 4032862;   // → 初级书 (4461xxx)
    public static final int MAT_MID = 4032873;       // → 中级书 (4462xxx)
    public static final int MAT_ADV = 4031739;       // → 高级书 (4463xxx)
    public static final int MAT_ULT = 4032868;       // → 终极书 (4464xxx)
    public static final int EXCHANGE_RATE = 10;

    /** 购买价格（金币）。 */
    public static final int PRICE_PRIMARY = 50_000;
    public static final int PRICE_MID = 100_000;
    public static final int PRICE_ADV = 200_000;
    public static final int PRICE_ULT = 500_000;

    /** 图标模板物品。 */
    public static final int ICON_PRIMARY = 4460001;
    public static final int ICON_MID = 4460004;
    public static final int ICON_ADV = 4460002;
    public static final int ICON_ULT = 4460000;

    /**
     * GMS v083 常见野外精英 / 地图王 ID（不含扎昆等远征 Boss）。
     * 判定以本集合为准，不单靠 isBoss()。
     */
    public static final Set<Integer> FIELD_ELITE_IDS = Set.of(
            2220000, // 红蜗牛王 Red Snail King
            3220000, // 树妖王 Stumpy
            3220001, // 大宇 Deo
            4220000, // 歇尔夫（部分渠道洞穴王 ID 备用）
            5220000, // 歇尔夫 King Clang
            5220002, // 浮士德 Faust
            5220003, // 提莫 Timer
            5220004, // 巨型蜈蚣
            6130101, // 蘑菇王 Mushmom
            6220000, // 多尔 Dyle
            6300005, // 僵尸蘑菇王
            7220000, // 拉温 Tae Roon
            7220001, // 歇尔夫（雪原相关备用）
            7220002, // 九尾狐
            8130100, // 蝙蝠魔 Jr.Balrog（废都野外）
            8220000, // 艾里葛斯 Eliza
            8220001, // 雪人 / 相关野外王
            9300003, // 绿水灵王（非事件实例时）
            9400205  // 绿水灵王备用
    );

    public static boolean isFieldElite(Monster monster) {
        if (monster == null) {
            return false;
        }
        return isFieldEliteId(monster.getId());
    }

    public static boolean isFieldEliteId(int mobId) {
        return FIELD_ELITE_IDS.contains(mobId);
    }

    /** 普通怪：非野外精英、非远征/副本 Boss 标记也可，但效果侧另行判断。 */
    public static boolean isNormalMob(Monster monster) {
        if (monster == null) {
            return false;
        }
        return !isFieldElite(monster) && !monster.isBoss();
    }

    /**
     * 终极天赋学习成功率（百分数）。
     * 学第 1 级固定 100%；否则 max(5, 100 - (当前等级)*3)，即学完第 30 级时按升到 30 的成功率。
     * 公式：升到 nextLevel 时 success = max(5, 100 - (nextLevel - 1) * 3)。
     */
    public static int ultimateSuccessRate(int currentLevel) {
        int nextLevel = currentLevel + 1;
        if (nextLevel <= 1) {
            return 100;
        }
        return Math.max(5, 100 - (nextLevel - 1) * 3);
    }

    public static int buyPrice(TalentTier tier) {
        return switch (tier) {
            case PRIMARY -> PRICE_PRIMARY;
            case MID -> PRICE_MID;
            case ADVANCED -> PRICE_ADV;
            case ULTIMATE -> PRICE_ULT;
        };
    }

    public static int exchangeMatItem(TalentTier tier) {
        return switch (tier) {
            case PRIMARY -> MAT_PRIMARY;
            case MID -> MAT_MID;
            case ADVANCED -> MAT_ADV;
            case ULTIMATE -> MAT_ULT;
        };
    }

    public static Set<Integer> fieldEliteIdsView() {
        return Collections.unmodifiableSet(FIELD_ELITE_IDS);
    }
}
