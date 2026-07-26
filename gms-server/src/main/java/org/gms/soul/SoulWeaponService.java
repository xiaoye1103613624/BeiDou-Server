package org.gms.soul;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.constants.inventory.ItemConstants;
import org.gms.potential.PotentialHyperService;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 灵魂武器：开槽 / 镶珠 / 清除 / 属性加成 / 灵魂技能（服务端侧，无 SoulCollector UI）。
 */
public final class SoulWeaponService {
    private static final Logger log = LoggerFactory.getLogger(SoulWeaponService.class);

    /** charId → buff 结束时间 ms */
    private static final Map<Integer, Long> BUFF_EXPIRE = new ConcurrentHashMap<>();
    /** charId → FINAL_DAM_R 值（如 100=×2） */
    private static final Map<Integer, Integer> BUFF_FD = new ConcurrentHashMap<>();
    /** charId → 技能 CD 结束时间 ms */
    private static final Map<Integer, Long> SKILL_CD = new ConcurrentHashMap<>();

    private SoulWeaponService() {}

    public static boolean hasSoulSlot(Equip equip) {
        if (equip == null) {
            return false;
        }
        int id = equip.getSoulId();
        if (id == SoulOrbConfig.SLOT_OPEN) {
            return true;
        }
        if (SoulOrbConfig.isOrb(id)) {
            return true;
        }
        // 旧版 2049914 随机灵魂：视为已开槽
        return id == 2049914 && equip.getSoulOption() > 0;
    }

    public static boolean hasOrb(Equip equip) {
        return equip != null && SoulOrbConfig.isOrb(equip.getSoulId());
    }

    public static SoulOrbConfig.OrbDef getEquippedOrb(Equip equip) {
        if (!hasOrb(equip)) {
            return null;
        }
        return SoulOrbConfig.getOrb(equip.getSoulId());
    }

    /** 把宝珠固定属性叠进潜能结算（与 ItemOption 灵魂线并存：旧版走 option，新版走配置）。 */
    public static void applyOrbBonus(PotentialHyperService.StatBonus b, Equip equip) {
        if (b == null || equip == null) {
            return;
        }
        SoulOrbConfig.OrbDef orb = getEquippedOrb(equip);
        if (orb == null) {
            return;
        }
        b.strR += orb.strR();
        b.dexR += orb.dexR();
        b.intR += orb.intR();
        b.lukR += orb.lukR();
        b.damR += orb.damR();
        b.bossDamR += orb.bossDamR();
        b.ignoreDef += orb.ignoreDef();
        b.padR += orb.padR() + SoulOrbConfig.FULL_SOUL_PAD_R;
        b.madR += orb.madR() + SoulOrbConfig.FULL_SOUL_MAD_R;
    }

    public static PotentialHyperService.Result applyEnchanter(Character chr, Equip equip, int itemId,
                                                             boolean forceSuccess) {
        if (chr == null || equip == null) {
            return PotentialHyperService.Result.INVALID;
        }
        SoulOrbConfig.EnchanterDef enc = SoulOrbConfig.getEnchanter(itemId);
        if (enc == null) {
            return PotentialHyperService.Result.INVALID;
        }
        if (!ItemConstants.isWeapon(equip.getItemId())) {
            chr.dropMessage(5, "【灵魂】只能对武器使用附魔石/开槽卷。");
            return PotentialHyperService.Result.INVALID;
        }
        int req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        if (req > 0 && req < SoulOrbConfig.MIN_WEAPON_LEVEL) {
            chr.dropMessage(5, "【灵魂】需要 " + SoulOrbConfig.MIN_WEAPON_LEVEL + " 级以上武器（当前需求等级 "
                    + req + "）。");
            return PotentialHyperService.Result.INVALID;
        }
        if (hasSoulSlot(equip)) {
            chr.dropMessage(5, "【灵魂】该武器已开槽"
                    + (hasOrb(equip) ? "（已镶嵌" + getEquippedOrb(equip).name() + "）" : "")
                    + "。换珠请直接砸宝珠；清除请用 2049915。");
            return PotentialHyperService.Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= enc.successRate()) {
            log.info("soul enchanter fail char={} equip={} item={} rate={}",
                    chr.getId(), equip.getItemId(), itemId, enc.successRate());
            return PotentialHyperService.Result.FAIL;
        }
        equip.setSoulId(SoulOrbConfig.SLOT_OPEN);
        equip.setSoulOption(0);
        equip.setEquipSkillId(0);
        equip.setEquipSkillLevel(0);
        log.info("soul slot opened char={} equip={} via={}", chr.getId(), equip.getItemId(), itemId);
        return PotentialHyperService.Result.SUCCESS;
    }

    public static PotentialHyperService.Result applyOrb(Character chr, Equip equip, int orbId,
                                                       boolean forceSuccess) {
        if (chr == null || equip == null) {
            return PotentialHyperService.Result.INVALID;
        }
        SoulOrbConfig.OrbDef orb = SoulOrbConfig.getOrb(orbId);
        if (orb == null) {
            return PotentialHyperService.Result.INVALID;
        }
        if (!ItemConstants.isWeapon(equip.getItemId())) {
            chr.dropMessage(5, "【灵魂】宝珠只能镶嵌在武器上。");
            return PotentialHyperService.Result.INVALID;
        }
        if (!hasSoulSlot(equip)) {
            chr.dropMessage(5, "【灵魂】请先使用附魔石/开槽卷（如 2049914、2590008）开启灵魂槽。");
            return PotentialHyperService.Result.INVALID;
        }
        if (!forceSuccess && Randomizer.nextInt(100) >= SoulOrbConfig.ORB_SUCCESS_RATE) {
            if (SoulOrbConfig.ORB_DESTROY_ON_FAIL) {
                log.info("soul orb curse char={} equip={} orb={}", chr.getId(), equip.getItemId(), orbId);
                return PotentialHyperService.Result.CURSE;
            }
            return PotentialHyperService.Result.FAIL;
        }
        String old = hasOrb(equip) ? getEquippedOrb(equip).name() : null;
        equip.setSoulId(orb.itemId());
        equip.setSoulOption(0);
        // 用 equipSkill* 给 tip/GM 留痕（非客户端官方灵魂技能）
        equip.setEquipSkillId(orb.itemId());
        equip.setEquipSkillLevel(1);
        log.info("soul orb applied char={} equip={} orb={} replaced={}",
                chr.getId(), equip.getItemId(), orbId, old);
        return PotentialHyperService.Result.SUCCESS;
    }

    public static PotentialHyperService.Result clearSoul(Character chr, Equip equip) {
        if (equip == null) {
            return PotentialHyperService.Result.INVALID;
        }
        if (equip.getSoulId() <= 0 && equip.getSoulOption() <= 0) {
            if (chr != null) {
                chr.dropMessage(5, "【灵魂】装备没有灵魂，无需清除。");
            }
            return PotentialHyperService.Result.INVALID;
        }
        int oldId = equip.getSoulId();
        int oldOpt = equip.getSoulOption();
        equip.setSoulId(0);
        equip.setSoulOption(0);
        equip.setEquipSkillId(0);
        equip.setEquipSkillLevel(0);
        if (chr != null) {
            clearBuff(chr.getId());
            log.info("soul cleared char={} equip={} was={}:{}", chr.getId(), equip.getItemId(), oldId, oldOpt);
        }
        return PotentialHyperService.Result.SUCCESS;
    }

    /**
     * ScrollHandler / Phase4 统一入口。
     */
    public static PotentialHyperService.Result applyScroll(Character chr, Equip equip, int scrollId,
                                                          boolean forceSuccess) {
        if (scrollId == 2049915) {
            return clearSoul(chr, equip);
        }
        if (SoulOrbConfig.isEnchanter(scrollId)) {
            return applyEnchanter(chr, equip, scrollId, forceSuccess);
        }
        if (SoulOrbConfig.isOrb(scrollId)) {
            return applyOrb(chr, equip, scrollId, forceSuccess);
        }
        return PotentialHyperService.Result.INVALID;
    }

    public static String describe(Equip equip) {
        if (equip == null) {
            return "";
        }
        if (hasOrb(equip)) {
            SoulOrbConfig.OrbDef o = getEquippedOrb(equip);
            return "宝珠[" + o.name() + "/" + o.skillName() + "]";
        }
        if (equip.getSoulId() == SoulOrbConfig.SLOT_OPEN) {
            return "灵魂[已开槽]";
        }
        if (equip.getSoulId() == 2049914 && equip.getSoulOption() > 0) {
            return "灵魂[旧版option:" + equip.getSoulOption() + "]";
        }
        if (equip.getSoulId() > 0 || equip.getSoulOption() > 0) {
            return "灵魂[" + equip.getSoulId() + ":" + equip.getSoulOption() + "]";
        }
        return "";
    }

    // ─── 灵魂技能 ─────────────────────────────────────────────

    public static int getActiveFinalDamR(int charId) {
        Long exp = BUFF_EXPIRE.get(charId);
        if (exp == null || exp <= System.currentTimeMillis()) {
            clearBuff(charId);
            return 0;
        }
        return BUFF_FD.getOrDefault(charId, 0);
    }

    public static void clearBuff(int charId) {
        BUFF_EXPIRE.remove(charId);
        BUFF_FD.remove(charId);
    }

    public static Equip findEquippedSoulWeapon(Character chr) {
        if (chr == null) {
            return null;
        }
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (item instanceof Equip eq && ItemConstants.isWeapon(eq.getItemId()) && hasOrb(eq)) {
                return eq;
            }
        }
        return null;
    }

    public static void useSoulSkill(Character chr) {
        if (chr == null) {
            return;
        }
        Equip weapon = findEquippedSoulWeapon(chr);
        if (weapon == null) {
            chr.dropMessage(5, "【灵魂技能】请先穿戴已镶嵌宝珠的武器。");
            return;
        }
        SoulOrbConfig.OrbDef orb = getEquippedOrb(weapon);
        if (orb == null || orb.skill() == SoulOrbConfig.SkillKind.NONE) {
            chr.dropMessage(5, "【灵魂技能】该宝珠没有技能。");
            return;
        }
        long now = System.currentTimeMillis();
        Long cd = SKILL_CD.get(chr.getId());
        if (cd != null && cd > now) {
            chr.dropMessage(5, "【灵魂技能】冷却中，剩余 " + ((cd - now + 999) / 1000) + " 秒。");
            return;
        }

        boolean ok = switch (orb.skill()) {
            case BUFF_FINAL_DAM -> applyFinalDamBuff(chr, orb);
            case AOE_DAMAGE -> dealMapDamage(chr, orb, true);
            case SINGLE_DAMAGE -> dealMapDamage(chr, orb, false);
            default -> false;
        };
        if (ok) {
            SKILL_CD.put(chr.getId(), now + orb.skillCooldownSec() * 1000L);
            PacketCreator.broadcastSoulWeaponEffect(chr);
        }
    }

    private static boolean applyFinalDamBuff(Character chr, SoulOrbConfig.OrbDef orb) {
        int fd = Math.max(0, orb.skillValue());
        int dur = Math.max(1, orb.skillDurationSec());
        BUFF_FD.put(chr.getId(), fd);
        BUFF_EXPIRE.put(chr.getId(), System.currentTimeMillis() + dur * 1000L);
        chr.equipChanged(); // 刷新战斗 Profile
        chr.dropMessage(5, "【灵魂技能】" + orb.skillName() + "！最终伤害 +" + fd + "%，持续 " + dur + " 秒。");
        return true;
    }

    private static boolean dealMapDamage(Character chr, SoulOrbConfig.OrbDef orb, boolean aoe) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }
        int base = Math.max(1, chr.calculateMaxBaseDamage(chr.getTotalWatk()));
        if (chr.getTotalMagic() > chr.getTotalWatk()) {
            base = Math.max(base, Math.max(1, (int) (chr.getTotalMagic() * 1.2)));
        }
        long raw = (long) base * Math.max(1, orb.skillValue()) / 100L;
        int dmg = (int) Math.min(Integer.MAX_VALUE - 1, Math.max(1, raw));

        List<Monster> targets = new ArrayList<>();
        Point pos = chr.getPosition();
        for (MapObject obj : map.getMapObjectsInRange(pos, aoe ? 400000 : 90000,
                List.of(MapObjectType.MONSTER))) {
            if (obj instanceof Monster m && m.isAlive()) {
                targets.add(m);
            }
        }
        if (targets.isEmpty()) {
            chr.dropMessage(5, "【灵魂技能】附近没有怪物。");
            return false;
        }
        if (!aoe) {
            Monster nearest = null;
            double best = Double.MAX_VALUE;
            for (Monster m : targets) {
                double d = m.getPosition().distanceSq(pos);
                if (d < best) {
                    best = d;
                    nearest = m;
                }
            }
            targets = nearest == null ? List.of() : List.of(nearest);
        } else if (targets.size() > 15) {
            targets = targets.subList(0, 15);
        }

        int hits = 0;
        for (Monster m : targets) {
            map.damageMonster(chr, m, dmg);
            map.broadcastMessage(chr, PacketCreator.damageMonster(m.getObjectId(), dmg), true);
            hits++;
        }
        chr.dropMessage(5, "【灵魂技能】" + orb.skillName() + "！命中 " + hits + " 只，单段约 " + dmg + "。");
        return hits > 0;
    }
}
