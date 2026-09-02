package org.gms.combat.power;

import org.gms.client.Character;
import org.gms.client.Job;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.WeaponType;
import org.gms.client.inventory.Equip;
import org.gms.combat.damage.DefenseCalculator;
import org.gms.combat.damage.DamageCalculator;
import org.gms.combat.provider.CombatProfileService;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.server.ItemInformationProvider;

/**
 * 方案2：以面板最大伤害为基底，叠实战乘区（Boss 向）。
 * <pre>
 * 物理 MaxBase ≈ (武器系数×主属 + 副属) / 100 × 攻击
 * 战力 ≈ MaxBase
 *       × (1 + padR/madR%)
 *       × (1 + damR% + bossDamR%)
 *       × finalDamageMultiplier
 *       × 暴击期望 (1 + critRate% × (0.5 + critDam%))
 *       × 对参考 Boss(PDR=50) 的无视防御乘区
 * </pre>
 * 不含药水等短时 Buff 的意图：在线优先用角色当前面板（含被动技能），
 * 离线用「基础 AP + 已穿装备」估算。
 */
public final class CombatPowerCalculator {
    /** 参考 Boss 物理/魔法防御率（百分比），用于把无视防计入战力。 */
    public static final int REFERENCE_BOSS_DEF_RATE = 50;

    private CombatPowerCalculator() {}

    public static long computeOnline(Character chr) {
        if (chr == null) {
            return 0L;
        }
        boolean magic = isMagicJob(chr.getJob());
        int baseDamage = magic
                ? chr.calculateMaxBaseMagicDamage(chr.getTotalMagic())
                : chr.calculateMaxBaseDamage(chr.getTotalWatk());
        CombatStatProfile profile = CombatProfileService.resolve(chr);
        return applyMultipliers(baseDamage, profile, magic);
    }

    /**
     * 离线估算：基础四维 + 已穿装备属性，无套装战斗乘区时 profile 可传 EMPTY。
     */
    public static long computeOffline(int jobId, int baseStr, int baseDex, int baseInt, int baseLuk,
                                      int sumStr, int sumDex, int sumInt, int sumLuk,
                                      int sumWatk, int sumMatk, int weaponItemId,
                                      CombatStatProfile profile) {
        int str = baseStr + sumStr;
        int dex = baseDex + sumDex;
        int inte = baseInt + sumInt;
        int luk = baseLuk + sumLuk;
        Job job = Job.getById(jobId);
        if (job == null) {
            job = Job.BEGINNER;
        }
        boolean magic = isMagicJob(job);
        WeaponType weapon = WeaponType.NOT_A_WEAPON;
        if (weaponItemId > 0) {
            weapon = ItemInformationProvider.getInstance().getWeaponType(weaponItemId);
            if (!magic && (weapon == WeaponType.WAND || weapon == WeaponType.STAFF)) {
                magic = true;
            }
        }
        int baseDamage;
        if (magic) {
            baseDamage = calcMaxBaseMagicDamage(sumMatk, inte);
        } else {
            baseDamage = calcMaxBasePhysicalDamage(str, dex, luk, sumWatk, weapon, job);
        }
        return applyMultipliers(baseDamage, profile == null ? CombatStatProfile.EMPTY : profile, magic);
    }

    public static long applyMultipliers(int baseDamage, CombatStatProfile profile, boolean magic) {
        if (baseDamage <= 0) {
            return 0L;
        }
        CombatStatProfile p = profile == null ? CombatStatProfile.EMPTY : profile;
        double dmg = baseDamage;
        dmg *= p.attackPercentMultiplier(magic);
        // Boss 向：damR + bossDamR
        int damPct = p.damR + p.bossDamR;
        if (damPct != 0) {
            dmg *= 1.0 + damPct / 100.0;
        }
        dmg *= p.finalDamageMultiplier;
        dmg *= critExpectation(p);
        int ignore = magic ? p.ignoreMDR : p.ignorePDR;
        dmg *= DefenseCalculator.defenseMultiplier(REFERENCE_BOSS_DEF_RATE, ignore);
        return Math.max(0L, Math.round(dmg));
    }

    /** 暴击期望倍率：未暴击 1.0，暴击时 1.5+critDam%。 */
    public static double critExpectation(CombatStatProfile profile) {
        if (profile == null || profile.critRate <= 0) {
            return 1.0;
        }
        double rate = Math.min(100, Math.max(0, profile.critRate)) / 100.0;
        double critMul = DamageCalculator.critMultiplier(profile);
        return (1.0 - rate) + rate * critMul;
    }

    public static boolean isMagicJob(Job job) {
        if (job == null) {
            return false;
        }
        return job.isA(Job.MAGICIAN) || job.isA(Job.BLAZEWIZARD1) || job.isA(Job.EVAN1);
    }

    public static int calcMaxBasePhysicalDamage(int str, int dex, int luk, int watk,
                                                WeaponType weapon, Job job) {
        if (weapon == null || weapon == WeaponType.NOT_A_WEAPON) {
            if (job != null && (job.isA(Job.PIRATE) || job.isA(Job.THUNDERBREAKER1))) {
                double weapMulti = job.getId() % 100 != 0 ? 4.2 : 3.0;
                return (int) Math.ceil((str * weapMulti + dex) * Math.min(31, Math.floor((2D * 1 + 31) / 3)) / 100.0);
            }
            return Math.max(1, watk);
        }
        WeaponType w = weapon;
        if (job != null && job.isA(Job.THIEF) && w == WeaponType.DAGGER_OTHER) {
            w = WeaponType.DAGGER_THIEVES;
        }
        int mainstat;
        int secondarystat;
        if (w == WeaponType.BOW || w == WeaponType.CROSSBOW || w == WeaponType.GUN) {
            mainstat = dex;
            secondarystat = str;
        } else if (w == WeaponType.CLAW || w == WeaponType.DAGGER_THIEVES) {
            mainstat = luk;
            secondarystat = dex + str;
        } else {
            mainstat = str;
            secondarystat = dex;
        }
        return (int) Math.ceil(((w.getMaxDamageMultiplier() * mainstat + secondarystat) / 100.0) * watk);
    }

    public static int calcMaxBaseMagicDamage(int matk, int totalInt) {
        int maxbasedamage = matk;
        if (totalInt > 2000) {
            maxbasedamage -= 2000;
            maxbasedamage += (int) ((0.09033024267 * totalInt) + 3823.8038);
        } else {
            maxbasedamage -= totalInt;
            if (totalInt > 1700) {
                maxbasedamage += (int) (0.1996049769 * Math.pow(totalInt, 1.300631341));
            } else {
                maxbasedamage += (int) (0.1996049769 * Math.pow(totalInt, 1.290631341));
            }
        }
        return (maxbasedamage * 107) / 100;
    }

    /** 从在线角色已穿装备汇总裸属性（不含 Buff），供调试或对照。 */
    public static int[] sumEquippedFlatStats(Character chr) {
        int str = 0, dex = 0, inte = 0, luk = 0, watk = 0, matk = 0, weaponId = 0;
        if (chr == null) {
            return new int[]{str, dex, inte, luk, watk, matk, weaponId};
        }
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (!(item instanceof Equip eq)) {
                continue;
            }
            str += eq.getStr();
            dex += eq.getDex();
            inte += eq.getInt();
            luk += eq.getLuk();
            watk += eq.getWatk();
            matk += eq.getMatk();
            if (eq.getPosition() == -11) {
                weaponId = eq.getItemId();
            }
        }
        return new int[]{str, dex, inte, luk, watk, matk, weaponId};
    }
}
