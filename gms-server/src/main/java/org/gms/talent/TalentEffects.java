package org.gms.talent;

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Disease;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.constants.skills.Bowmaster;
import org.gms.constants.skills.Hero;
import org.gms.net.server.Server;
import org.gms.server.StatEffect;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 天赋战斗 / 掉宝 / 异常 / 刷怪效果。
 */
public final class TalentEffects {
    private TalentEffects() {}

    public static int level(Character chr, TalentId id) {
        if (chr == null || chr.getTalentManager() == null) {
            return 0;
        }
        return chr.getTalentManager().getLevel(id);
    }

    public static int bonusMaxHp(Character chr) {
        return (level(chr, TalentId.BODY_BOOST) + level(chr, TalentId.BODY_TRAIN)) * 100;
    }

    public static int bonusMaxMp(Character chr) {
        return (level(chr, TalentId.MP_BOOST) + level(chr, TalentId.MP_TRAIN)) * 100;
    }

    /** 对普通怪伤害加成（百分点），区分物理/魔法。 */
    public static int normalDamageBonusPercent(Character chr, boolean magic) {
        return magic ? level(chr, TalentId.MAGIC_BASIC) : level(chr, TalentId.PHYS_BASIC);
    }

    /** 对野外精英伤害加成（百分点）。 */
    public static int eliteDamageBonusPercent(Character chr, boolean magic) {
        return magic ? level(chr, TalentId.MAGIC_ENHANCE) * 2 : level(chr, TalentId.PHYS_ENHANCE) * 2;
    }

    public static long modifyOutgoingDamage(Character chr, Monster mob, long damage, boolean magic) {
        if (chr == null || mob == null || damage <= 0) {
            return damage;
        }
        double mul = 1.0;
        if (TalentConfig.isFieldElite(mob)) {
            int p = eliteDamageBonusPercent(chr, magic);
            if (p > 0) {
                mul += p / 100.0;
            }
        } else if (!mob.isBoss()) {
            int p = normalDamageBonusPercent(chr, magic);
            if (p > 0) {
                mul += p / 100.0;
            }
        }
        if (mul == 1.0) {
            return damage;
        }
        return Math.max(1L, Math.round(damage * mul));
    }

    /** 伤害减免百分点（坚韧之力+意志）。 */
    public static int damageTakenReducePercent(Character chr) {
        return level(chr, TalentId.TOUGH_FORCE) + level(chr, TalentId.TOUGH_WILL);
    }

    public static int dodgePercent(Character chr) {
        return level(chr, TalentId.FOX_FORCE) + level(chr, TalentId.FOX_WILL);
    }

    public static boolean rollDodge(Character chr) {
        int pct = dodgePercent(chr);
        return pct > 0 && Randomizer.nextInt(100) < pct;
    }

    public static int applyDamageTakenReduce(Character chr, int damage) {
        if (damage <= 0) {
            return damage;
        }
        int red = damageTakenReducePercent(chr);
        if (red <= 0) {
            return damage;
        }
        return Math.max(1, (int) Math.round(damage * (1.0 - Math.min(80, red) / 100.0)));
    }

    /** 裂变溅射：对主目标以外附近怪物造成 hitDamage * (lv%) 伤害。 */
    public static void applyFission(Character chr, MapleMap map, Monster main, int hitDamage) {
        if (chr == null || map == null || main == null || hitDamage <= 0) {
            return;
        }
        int lv = level(chr, TalentId.FISSION);
        if (lv <= 0) {
            return;
        }
        int splash = Math.max(1, (int) (hitDamage * (Math.min(10, lv) / 100.0)));
        Point pos = main.getPosition();
        double range = TalentConfig.FISSION_RANGE;
        List<MapObject> objs = map.getMapObjectsInRange(pos, range * range,
                Collections.singletonList(MapObjectType.MONSTER));
        for (MapObject obj : objs) {
            if (!(obj instanceof Monster other) || other.getObjectId() == main.getObjectId() || !other.isAlive()) {
                continue;
            }
            map.damageMonster(chr, other, splash);
            map.broadcastMessage(PacketCreator.damageMonster(other.getObjectId(), splash));
        }
    }

    /** 攻击命中后刷新终极天赋buff，并处理大宗师惠及队友。 */
    public static void onAttackHit(Character chr, boolean damagedSomething) {
        if (chr == null || !damagedSomething) {
            return;
        }
        boolean appliedSharp = applyUltimateBuff(chr, TalentId.SHARP_EYES, Bowmaster.SHARP_EYES);
        boolean appliedStance = applyUltimateBuff(chr, TalentId.STANCE, Hero.STANCE);
        if ((appliedSharp || appliedStance) && level(chr, TalentId.GRANDMASTER) > 0) {
            int chance = level(chr, TalentId.GRANDMASTER) * 2;
            if (Randomizer.nextInt(100) < chance) {
                shareUltimateToParty(chr, appliedSharp, appliedStance);
            }
        }
    }

    private static boolean applyUltimateBuff(Character self, TalentId talent, int skillId) {
        int lv = level(self, talent);
        if (lv <= 0) {
            return false;
        }
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return false;
        }
        int effectLv = Math.min(lv, skill.getMaxLevel() > 0 ? skill.getMaxLevel() : lv);
        StatEffect effect = skill.getEffect(Math.max(1, effectLv));
        if (effect == null) {
            return false;
        }
        effect.applyTo(self);
        return true;
    }

    private static void shareUltimateToParty(Character self, boolean sharp, boolean stance) {
        List<Character> mates = self.getPartyMembersOnSameMap();
        if (mates == null) {
            return;
        }
        for (Character mate : mates) {
            if (mate == null || mate.getId() == self.getId()) {
                continue;
            }
            if (sharp) {
                applyUltimateBuffForce(mate, level(self, TalentId.SHARP_EYES), Bowmaster.SHARP_EYES);
            }
            if (stance) {
                applyUltimateBuffForce(mate, level(self, TalentId.STANCE), Hero.STANCE);
            }
        }
    }

    private static void applyUltimateBuffForce(Character target, int talentLv, int skillId) {
        if (talentLv <= 0) {
            return;
        }
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return;
        }
        int effectLv = Math.min(talentLv, skill.getMaxLevel() > 0 ? skill.getMaxLevel() : talentLv);
        StatEffect effect = skill.getEffect(Math.max(1, effectLv));
        if (effect != null) {
            effect.applyTo(target);
        }
    }

    public static float dropRateMultiplier(Character chr, Monster mob) {
        if (chr == null) {
            return 1.0f;
        }
        int bonus = 0;
        if (mob != null && !mob.isBoss() && !TalentConfig.isFieldElite(mob)) {
            bonus += level(chr, TalentId.HUNT_WAY) * 2;
        }
        bonus += level(chr, TalentId.HUNT_WILL) * 2;
        if (isAloneOnMap(chr)) {
            bonus += level(chr, TalentId.LONE_STAR) * 2;
        }
        bonus = Math.min(TalentConfig.DROP_BONUS_CAP_PERCENT, bonus);
        if (bonus <= 0) {
            return 1.0f;
        }
        return 1.0f + bonus / 100.0f;
    }

    private static boolean isAloneOnMap(Character chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }
        return map.getCharacters().size() <= 1;
    }

    /** 怨念重生：地图刷新倍率，1.0~2.0。 */
    public static double spawnMultiplier(Character chr) {
        int lv = level(chr, TalentId.GRUDGE_REBORN);
        if (lv <= 0) {
            return 1.0;
        }
        return 1.0 + lv * 0.10;
    }

    public static double mapSpawnMultiplier(MapleMap map) {
        if (map == null) {
            return 1.0;
        }
        double max = 1.0;
        for (Character c : map.getCharacters()) {
            if (c != null) {
                max = Math.max(max, spawnMultiplier(c));
            }
        }
        return max;
    }

    public static int mpCostReducePercent(Character chr) {
        return level(chr, TalentId.MP_SUPPRESS) * 2 + level(chr, TalentId.MP_SUPER) * 2;
    }

    public static int hpCostReducePercent(Character chr) {
        return level(chr, TalentId.BLEED_SUPPRESS) * 2 + level(chr, TalentId.COAGULATE) * 2;
    }

    public static int reduceMpCost(Character chr, int mpCon) {
        if (mpCon <= 0) {
            return mpCon;
        }
        int red = Math.min(80, mpCostReducePercent(chr));
        if (red <= 0) {
            return mpCon;
        }
        return Math.max(0, (int) Math.round(mpCon * (1.0 - red / 100.0)));
    }

    public static int reduceHpCost(Character chr, int hpCon) {
        if (hpCon <= 0) {
            return hpCon;
        }
        int red = Math.min(80, hpCostReducePercent(chr));
        if (red <= 0) {
            return hpCon;
        }
        return Math.max(0, (int) Math.round(hpCon * (1.0 - red / 100.0)));
    }

    /**
     * @return true 表示完全免疫，不应上异常。
     */
    public static boolean tryImmunity(Character chr, Disease disease) {
        if (chr == null || disease == null) {
            return false;
        }
        TalentId immune = switch (disease) {
            case SEAL -> TalentId.SEAL_IMMUNE;
            case DARKNESS -> TalentId.DARK_IMMUNE;
            case WEAKEN -> TalentId.WEAKEN_IMMUNE;
            case STUN -> TalentId.STUN_IMMUNE;
            default -> null;
        };
        if (immune == null) {
            return false;
        }
        int chance = level(chr, immune) * 2;
        return chance > 0 && Randomizer.nextInt(100) < chance;
    }

    /** 缩短异常持续时间后的毫秒值。 */
    public static long adjustDebuffDuration(Character chr, Disease disease, long durationMs) {
        if (chr == null || durationMs <= 0) {
            return durationMs;
        }
        int reduce = level(chr, TalentId.SPIRIT_REFRESH) * 3;
        reduce += switch (disease) {
            case STUN -> level(chr, TalentId.STUN_RESIST) * 3;
            case WEAKEN -> level(chr, TalentId.WEAKEN_RESIST) * 3;
            case SEAL -> level(chr, TalentId.SEAL_RESIST) * 3;
            case DARKNESS -> level(chr, TalentId.DARK_RESIST) * 3;
            default -> 0;
        };
        if (reduce <= 0) {
            return durationMs;
        }
        reduce = Math.min(90, reduce);
        return Math.max(100L, Math.round(durationMs * (1.0 - reduce / 100.0)));
    }

    public static int reflectPercent(Character chr) {
        return level(chr, TalentId.THORN_ARMOR) * 2;
    }

    /**
     * 死而后生：致命伤时尝试保命。
     * @return 若触发保命则返回应受到的伤害（使剩余 HP=1），否则返回原 damage。
     */
    public static int tryDeathReborn(Character chr, int damage) {
        if (chr == null || damage <= 0) {
            return damage;
        }
        int lv = level(chr, TalentId.DEATH_REBORN);
        if (lv <= 0) {
            return damage;
        }
        int hp = chr.getHp();
        if (damage < hp) {
            return damage;
        }
        TalentManager tm = chr.getTalentManager();
        long now = Server.getInstance().getCurrentTime();
        if (tm != null && now - tm.getLastDeathRebornMs() < TalentConfig.DEATH_REBORN_COOLDOWN_MS) {
            return damage;
        }
        if (Randomizer.nextInt(100) >= lv) {
            return damage;
        }
        if (tm != null) {
            tm.setLastDeathRebornMs(now);
        }
        chr.dropMessage(5, "死而后生发动！保留了最后一口气。");
        return Math.max(0, hp - 1);
    }

    public static void tryPainTrainExp(Character chr, int damageTaken) {
        if (chr == null || damageTaken <= 0) {
            return;
        }
        int lv = level(chr, TalentId.PAIN_TRAIN);
        if (lv <= 0) {
            return;
        }
        TalentManager tm = chr.getTalentManager();
        long now = Server.getInstance().getCurrentTime();
        if (tm != null && now - tm.getLastPainTrainMs() < TalentConfig.PAIN_TRAIN_COOLDOWN_MS) {
            return;
        }
        int exp = Math.max(1, (int) (damageTaken * (lv / 100.0)));
        if (tm != null) {
            tm.setLastPainTrainMs(now);
        }
        chr.gainExp(exp, false, false);
    }

    /** 荆棘反弹伤害量。 */
    public static int reflectDamage(Character chr, int damage) {
        int pct = reflectPercent(chr);
        if (pct <= 0 || damage <= 0) {
            return 0;
        }
        return Math.max(1, (int) (damage * (pct / 100.0)));
    }

    @SuppressWarnings("unused")
    public static boolean hasStanceLike(Character chr) {
        return chr != null && (chr.getBuffedValue(BuffStat.STANCE) != null
                || level(chr, TalentId.STANCE) > 0);
    }

    public static List<TalentId> talentsOfTier(TalentTier tier) {
        return Arrays.stream(TalentId.values()).filter(t -> t.tier() == tier).toList();
    }
}
