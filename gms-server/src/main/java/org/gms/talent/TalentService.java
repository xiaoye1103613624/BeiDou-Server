package org.gms.talent;

import org.gms.client.Character;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * NPC / 脚本 / 战斗钩子门面。
 */
public final class TalentService {
    private TalentService() {}

    public static TalentManager manager(Character chr) {
        return chr == null ? null : chr.getTalentManager();
    }

    public static int getLevel(Character chr, int talentId) {
        TalentManager m = manager(chr);
        return m == null ? 0 : m.getLevel(talentId);
    }

    public static TalentManager.LearnResult learn(Character chr, int talentId) {
        TalentManager m = manager(chr);
        TalentId tid = TalentId.fromId(talentId);
        if (m == null || tid == null) {
            return TalentManager.LearnResult.fail("无效的天赋。");
        }
        return m.learn(tid);
    }

    public static boolean isTierUnlocked(Character chr, TalentTier tier) {
        TalentManager m = manager(chr);
        return m != null && m.isTierUnlocked(tier);
    }

    public static int pointsSpent(Character chr, TalentTier tier) {
        TalentManager m = manager(chr);
        return m == null ? 0 : m.pointsSpent(tier);
    }

    // ——— 战斗钩子 ———

    public static int applyOutgoingDamageBonus(Character chr, Monster mob, int damage, boolean magic) {
        long v = TalentEffects.modifyOutgoingDamage(chr, mob, damage, magic);
        return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
    }

    public static void onDealDamageToMonster(Character chr, MapleMap map, Monster mob, int damage) {
        if (damage <= 0) {
            return;
        }
        TalentEffects.applyFission(chr, map, mob, damage);
        TalentEffects.onAttackHit(chr, true);
    }

    /** @return 掉宝加成小数（0~0.20），供 chRate *= (1 + bonus)。 */
    public static double getDropBonus(Character chr, MapleMap map) {
        return getDropBonus(chr, map, null);
    }

    public static double getDropBonus(Character chr, MapleMap map, Monster mob) {
        float mul = TalentEffects.dropRateMultiplier(chr, mob);
        return Math.max(0.0, mul - 1.0);
    }

    public static double getMapSpawnMultiplier(MapleMap map) {
        return TalentEffects.mapSpawnMultiplier(map);
    }

    public static boolean tryDodge(Character chr) {
        return TalentEffects.rollDodge(chr);
    }

    public static int reduceDamageTaken(Character chr, Monster attacker, int damage) {
        return TalentEffects.applyDamageTakenReduce(chr, damage);
    }

    public static boolean tryCheatDeath(Character chr) {
        int lv = TalentEffects.level(chr, TalentId.DEATH_REBORN);
        if (lv <= 0) {
            return false;
        }
        // 用 damage=HP 触发试探，成功后调用方会把伤害改为 hp-1
        int simulated = TalentEffects.tryDeathReborn(chr, chr.getHp());
        return simulated < chr.getHp();
    }

    public static void applyThorns(Character chr, Monster attacker, MapleMap map, int oid, int damage) {
        if (chr == null || attacker == null || map == null || damage <= 0) {
            return;
        }
        int thorn = TalentEffects.reflectDamage(chr, damage);
        if (thorn <= 0) {
            return;
        }
        thorn = (int) Math.min(thorn, Math.max(1, Math.min(Integer.MAX_VALUE, attacker.getMaxHp() / 5)));
        map.damageMonster(chr, attacker, thorn);
        map.broadcastMessage(chr, PacketCreator.damageMonster(oid, thorn), false, true);
        attacker.aggroMonsterDamage(chr, thorn);
    }

    public static void painTrainingExp(Character chr, int damage) {
        TalentEffects.tryPainTrainExp(chr, damage);
    }

    public static int reduceSkillMpCost(Character chr, int mpCon) {
        return TalentEffects.reduceMpCost(chr, mpCon);
    }

    public static int reduceSkillHpCost(Character chr, int hpCon) {
        return TalentEffects.reduceHpCost(chr, hpCon);
    }

    // ——— NPC 兑换/购买 ———

    public static String exchangeOneBook(Character chr, TalentId book) {
        if (chr == null || book == null) {
            return "无效。";
        }
        if (book.tier() == TalentTier.ULTIMATE) {
            return exchangeUltimate(chr, book);
        }
        int mat = TalentConfig.exchangeMatItem(book.tier());
        if (chr.getItemQuantity(mat, false) < TalentConfig.EXCHANGE_RATE) {
            return "材料不足，需要 " + TalentConfig.EXCHANGE_RATE + " 个材料。";
        }
        if (!InventoryManipulator.checkSpace(chr.getClient(), book.itemId(), 1, "")) {
            return "背包空间不足。";
        }
        InventoryManipulator.removeById(chr.getClient(), ItemConstants.getInventoryType(mat),
                mat, TalentConfig.EXCHANGE_RATE, false, false);
        InventoryManipulator.addById(chr.getClient(), book.itemId(), (short) 1);
        return "兑换成功：获得 " + book.displayName() + " ×1";
    }

    public static String exchangeUltimate(Character chr, TalentId book) {
        if (book == null || book.tier() != TalentTier.ULTIMATE) {
            return "请选择终极天赋书。";
        }
        int mat = TalentConfig.MAT_ULT;
        if (chr.getItemQuantity(mat, false) < TalentConfig.EXCHANGE_RATE) {
            return "材料不足，需要 " + TalentConfig.EXCHANGE_RATE + " 个 #z" + mat + "#。";
        }
        if (!InventoryManipulator.checkSpace(chr.getClient(), book.itemId(), 1, "")) {
            return "背包空间不足。";
        }
        InventoryManipulator.removeById(chr.getClient(), ItemConstants.getInventoryType(mat),
                mat, TalentConfig.EXCHANGE_RATE, false, false);
        InventoryManipulator.addById(chr.getClient(), book.itemId(), (short) 1);
        return "兑换成功：获得 " + book.displayName() + " ×1";
    }

    public static String buyBook(Character chr, TalentId book) {
        if (chr == null || book == null) {
            return "无效。";
        }
        int price = TalentConfig.buyPrice(book.tier());
        if (chr.getMeso() < price) {
            return "金币不足，需要 " + price + " 金币。";
        }
        if (!InventoryManipulator.checkSpace(chr.getClient(), book.itemId(), 1, "")) {
            return "背包空间不足。";
        }
        chr.gainMeso(-price, false);
        InventoryManipulator.addById(chr.getClient(), book.itemId(), (short) 1);
        return "购买成功：获得 " + book.displayName() + " ×1（花费 " + price + " 金币）";
    }

    public static List<TalentId> listByTier(TalentTier tier) {
        List<TalentId> list = new ArrayList<>();
        for (TalentId t : TalentId.values()) {
            if (t.tier() == tier) {
                list.add(t);
            }
        }
        return list;
    }

    public static String tierStatusText(Character chr) {
        return "初级点数: " + pointsSpent(chr, TalentTier.PRIMARY) + "/20 解锁中级\r\n"
                + "中级点数: " + pointsSpent(chr, TalentTier.MID) + "/20 解锁高级\r\n"
                + "高级解锁: " + (isTierUnlocked(chr, TalentTier.ADVANCED) ? "是" : "否") + "\r\n"
                + "终极解锁: " + (isTierUnlocked(chr, TalentTier.ULTIMATE) ? "是" : "否");
    }

    public static String itemName(int itemId) {
        try {
            return ItemInformationProvider.getInstance().getName(itemId);
        } catch (Exception e) {
            return String.valueOf(itemId);
        }
    }
}
