package org.gms.flame;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 火花 / 涅槃火焰：档位·条数·数值对齐 265 {@code Equip.flame}。
 * 属性虚拟加算（不写 body）；tip 绿段经成长 tip 扩展下发。
 */
public final class FlameService {
    private static final Logger log = LoggerFactory.getLogger(FlameService.class);

    private FlameService() {}

    public enum Result { SUCCESS, INVALID, FAIL }

    public static boolean canHaveFlame(Equip equip) {
        if (equip == null) {
            return false;
        }
        int id = equip.getItemId();
        if (ItemInformationProvider.getInstance().isCash(id)) {
            return false;
        }
        return FlameConfig.canEquipHaveFlame(id);
    }

    public static short flameLevel(int reqLevel) {
        return (short) Math.ceil((reqLevel + 1.0) / FlameConfig.LEVEL_DIVIDER);
    }

    public static short flameLevelExtended(int reqLevel) {
        return (short) Math.ceil((reqLevel + 1.0) / FlameConfig.LEVEL_DIVIDER_EXTENDED);
    }

    public static Result applyFlameItem(Character chr, Equip equip, int itemId, boolean force) {
        FlameType type = FlameConfig.resolveType(itemId);
        if (type == null || equip == null) {
            return Result.INVALID;
        }
        if (!canHaveFlame(equip)) {
            if (chr != null) {
                chr.dropMessage(5, "【火花】该装备无法附加涅槃火焰属性。");
            }
            return Result.INVALID;
        }
        short lastTier = rollFlame(equip, type);
        if (lastTier <= 0 && equip.getExGradeOption() == 0) {
            return Result.FAIL;
        }
        if (chr != null) {
            chr.dropMessage(5, "【火花】附加成功（" + type.name() + "，末档 T" + lastTier
                    + "）。绿字追加属性已刷新。");
        }
        log.info("flame applied char={} equip={} item={} type={} ex={} tier={}",
                chr != null ? chr.getId() : -1, equip.getItemId(), itemId, type,
                equip.getExGradeOption(), lastTier);
        return Result.SUCCESS;
    }

    /** 随机火焰并写回 exGradeOption + 内存 flameStat。 */
    public static short rollFlame(Equip equip, FlameType type) {
        EquipFlame flame = new EquipFlame();
        flame.reset();
        if (!canHaveFlame(equip)) {
            equip.setExGradeOption(0L);
            equip.setFlameStat(flame);
            return 0;
        }
        final boolean adv = isBossReward(equip.getItemId());
        final int bonusLines = adv ? 4 : (type == FlameType.POWERFUL ? 4 : Randomizer.rand(1, 3));
        final int minTier;
        final int maxTier;
        switch (type) {
            case POWERFUL -> {
                minTier = 1;
                maxTier = adv ? 6 : 4;
            }
            case ETERNAL, BLACK -> {
                minTier = adv ? 4 : 2;
                maxTier = adv ? 7 : 5;
            }
            case ABYSSAL -> {
                minTier = adv ? 5 : 3;
                maxTier = adv ? 7 : 5;
            }
            default -> {
                minTier = 1;
                maxTier = 4;
            }
        }
        int req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        if (req <= 0) {
            req = 1;
        }
        short fl = flameLevel(req);
        short fle = flameLevelExtended(req);
        short lastTier = 0;
        long ex = 0;
        long factor = 1;
        boolean[] used = new boolean[FlameStat.values().length];
        int applied = 0;
        int guard = 0;
        while (applied < bonusLines && guard++ < 200) {
            int r = Randomizer.nextInt(FlameStat.values().length);
            // 265: Util.getRandom(length-1) → 0..length-2，末项 LevelReduction 较低权重；此处全池均匀
            FlameStat fs = FlameStat.byVal(r);
            if (fs == null || used[r]) {
                continue;
            }
            if (fs == FlameStat.LEVEL_REDUCTION && req < 5) {
                continue;
            }
            if ((fs == FlameStat.BOSS_DAMAGE || fs == FlameStat.DAMAGE)
                    && !org.gms.constants.inventory.ItemConstants.isWeapon(equip.getItemId())) {
                continue;
            }
            short tier = (short) Randomizer.rand(minTier, maxTier);
            applyLine(flame, fs, tier, fl, fle, req, equip.getItemId(), adv);
            int line = fs.getExGrade() * 10 + tier;
            ex += factor * line;
            factor *= 1000L;
            used[r] = true;
            lastTier = tier;
            applied++;
        }
        equip.setExGradeOption(ex);
        equip.setFlameStat(flame);
        return lastTier;
    }

    /** 从 exGradeOption 还原 flameStat（登录加载后调用）。 */
    public static void decodeToFlameStat(Equip equip) {
        EquipFlame flame = new EquipFlame();
        flame.reset();
        if (equip == null) {
            return;
        }
        long ex = equip.getExGradeOption();
        if (ex <= 0 || !canHaveFlame(equip)) {
            equip.setFlameStat(flame);
            return;
        }
        int req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        if (req <= 0) {
            req = 1;
        }
        short fl = flameLevel(req);
        short fle = flameLevelExtended(req);
        boolean adv = isBossReward(equip.getItemId());
        while (ex > 0) {
            int line = (int) (ex % 1000);
            ex /= 1000;
            int exType = line / 10;
            short tier = (short) (line % 10);
            if (tier <= 0) {
                continue;
            }
            FlameStat fs = FlameStat.byExGrade(exType);
            if (fs == null) {
                continue;
            }
            applyLine(flame, fs, tier, fl, fle, req, equip.getItemId(), adv);
        }
        equip.setFlameStat(flame);
    }

    private static void applyLine(EquipFlame flame, FlameStat fs, short tier,
                                  short fl, short fle, int req, int itemId, boolean adv) {
        int added = tier * fl;
        int addedExt = tier * fle;
        switch (fs) {
            case STR -> flame.str += addedExt;
            case DEX -> flame.dex += addedExt;
            case INT -> flame.inte += addedExt;
            case LUK -> flame.luk += addedExt;
            case STRDEX -> {
                flame.str += added;
                flame.dex += added;
            }
            case STRINT -> {
                flame.str += added;
                flame.inte += added;
            }
            case STRLUK -> {
                flame.str += added;
                flame.luk += added;
            }
            case DEXINT -> {
                flame.dex += added;
                flame.inte += added;
            }
            case DEXLUK -> {
                flame.dex += added;
                flame.luk += added;
            }
            case INTLUK -> {
                flame.inte += added;
                flame.luk += added;
            }
            case ATTACK -> flame.pad += attBonus(itemId, tier, fl, adv);
            case MAGIC_ATTACK -> flame.mad += attBonus(itemId, tier, fl, adv);
            case DEFENSE -> flame.pdd += addedExt;
            case MAX_HP -> flame.hp += (req / 10) * 30 * tier;
            case MAX_MP -> flame.mp += (req / 10) * 30 * tier;
            case SPEED -> flame.speed += tier;
            case JUMP -> flame.jump += tier;
            case ALL_STATS -> flame.allStatR += tier;
            case BOSS_DAMAGE -> flame.bossDamageR += tier * 2;
            case DAMAGE -> flame.damageR += tier;
            case LEVEL_REDUCTION -> flame.reduceReqLevel += (byte) (5 * tier);
        }
    }

    private static short attBonus(int itemId, short tier, short fl, boolean adv) {
        if (!org.gms.constants.inventory.ItemConstants.isWeapon(itemId)) {
            return tier;
        }
        double[] mul = adv ? FlameConfig.WEAPON_FLAME_MULTIPLIER_BOSS : FlameConfig.WEAPON_FLAME_MULTIPLIER;
        int idx = Math.max(0, Math.min(mul.length - 1, tier - 1));
        int att = 0;
        try {
            var stats = ItemInformationProvider.getInstance().getEquipStats(itemId);
            if (stats != null) {
                att = Math.max(stats.getOrDefault("PAD", 0), stats.getOrDefault("MAD", 0));
                if (att <= 0) {
                    att = Math.max(stats.getOrDefault("incPAD", 0), stats.getOrDefault("incMAD", 0));
                }
            }
        } catch (Exception ignored) {
        }
        return (short) Math.ceil(att * (mul[idx] * fl) / 100.0);
    }

    private static boolean isBossReward(int itemId) {
        try {
            var stats = ItemInformationProvider.getInstance().getEquipStats(itemId);
            if (stats == null) {
                return false;
            }
            return stats.getOrDefault("bossReward", 0) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
