package org.gms.spirit;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;

import java.util.List;

/**
 * 灵韵觉醒门面：供 NPC 脚本调用。
 */
public final class SpiritAwakenService {
    private SpiritAwakenService() {}

    public record AwakenResult(boolean ok, boolean success, int skillId, int skillLevel, String message) {
        public static AwakenResult fail(String msg) {
            return new AwakenResult(false, false, 0, 0, msg);
        }

        public static AwakenResult rollFail(String msg) {
            return new AwakenResult(true, false, 0, 0, msg);
        }

        public static AwakenResult win(int skillId, int skillLevel, String msg) {
            return new AwakenResult(true, true, skillId, skillLevel, msg);
        }
    }

    /** 描述装备当前灵韵（脚本展示用）。 */
    public static String describeEquip(Equip equip) {
        if (equip == null) {
            return "无效装备。";
        }
        if (equip.getEquipSkillId() <= 0 || equip.getEquipSkillLevel() <= 0) {
            return "当前无灵韵。";
        }
        return "灵韵：#s" + equip.getEquipSkillId() + "#  Lv." + equip.getEquipSkillLevel()
                + (SpiritAwakenConfig.isT0(equip.getEquipSkillId()) ? " #r[T0]#k" : "");
    }

    /** 清空装备灵韵字段（交易 / 重置）。 */
    public static void clearSpirit(Equip equip) {
        if (equip == null) {
            return;
        }
        equip.setEquipSkillId(0);
        equip.setEquipSkillLevel(0);
        equip.setEquipSkillExpire(0L);
    }

    /**
     * 清除背包武器灵韵（需额外材料）。
     */
    public static AwakenResult reset(Character chr, short slot) {
        if (chr == null) {
            return AwakenResult.fail("角色无效。");
        }
        Inventory equipInv = chr.getInventory(InventoryType.EQUIP);
        Item item = equipInv.getItem(slot);
        if (!(item instanceof Equip equip)) {
            return AwakenResult.fail("请选择装备栏中的武器。");
        }
        if (!ItemConstants.isWeapon(equip.getItemId())) {
            return AwakenResult.fail("只能清除武器上的灵韵。");
        }
        if (equip.getEquipSkillId() <= 0 || equip.getEquipSkillLevel() <= 0) {
            return AwakenResult.fail("该武器当前没有灵韵。");
        }
        if (chr.getItemQuantity(SpiritAwakenConfig.COST_ITEM_ID, false) < SpiritAwakenConfig.RESET_COST_ITEM_QTY) {
            return AwakenResult.fail("需要 #v" + SpiritAwakenConfig.COST_ITEM_ID + "# ×"
                    + SpiritAwakenConfig.RESET_COST_ITEM_QTY + "。");
        }
        if (chr.getMeso() < SpiritAwakenConfig.RESET_COST_MESO) {
            return AwakenResult.fail("需要金币 " + (SpiritAwakenConfig.RESET_COST_MESO / 10000) + " 万。");
        }
        InventoryManipulator.removeById(chr.getClient(),
                ItemConstants.getInventoryType(SpiritAwakenConfig.COST_ITEM_ID),
                SpiritAwakenConfig.COST_ITEM_ID,
                SpiritAwakenConfig.RESET_COST_ITEM_QTY, false, false);
        chr.gainMeso(-SpiritAwakenConfig.RESET_COST_MESO, false);
        clearSpirit(equip);
        chr.forceUpdateItem(equip);
        chr.markCombatStatsDirty();
        chr.equipChanged();
        return AwakenResult.win(0, 0, "灵韵已清除。");
    }

    /**
     * 对装备栏（EQUIP）指定栏位武器执行一次觉醒。
     *
     * @param slot 装备栏绝对栏位（通常 1~N）
     */
    public static AwakenResult awaken(Character chr, short slot) {
        if (chr == null) {
            return AwakenResult.fail("角色无效。");
        }
        Inventory equipInv = chr.getInventory(InventoryType.EQUIP);
        Item item = equipInv.getItem(slot);
        if (!(item instanceof Equip equip)) {
            return AwakenResult.fail("请选择装备栏中的武器。");
        }
        int itemId = equip.getItemId();
        if (!ItemConstants.isWeapon(itemId)) {
            return AwakenResult.fail("只能对武器进行灵韵觉醒。");
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.isCash(itemId)) {
            return AwakenResult.fail("现金武器无法觉醒。");
        }
        Integer reqObj = ii.getEquipLevelReq(itemId);
        int reqLevel = reqObj != null ? reqObj : 0;
        if (reqLevel < SpiritAwakenConfig.MIN_WEAPON_REQ_LEVEL) {
            return AwakenResult.fail("武器需求等级需达到 " + SpiritAwakenConfig.MIN_WEAPON_REQ_LEVEL + " 级。");
        }
        if (chr.getItemQuantity(SpiritAwakenConfig.COST_ITEM_ID, false) < SpiritAwakenConfig.COST_ITEM_QTY) {
            return AwakenResult.fail("需要 #v" + SpiritAwakenConfig.COST_ITEM_ID + "# ×"
                    + SpiritAwakenConfig.COST_ITEM_QTY + "。");
        }
        if (chr.getMeso() < SpiritAwakenConfig.COST_MESO) {
            return AwakenResult.fail("需要金币 " + (SpiritAwakenConfig.COST_MESO / 10000) + " 万。");
        }

        InventoryManipulator.removeById(chr.getClient(),
                ItemConstants.getInventoryType(SpiritAwakenConfig.COST_ITEM_ID),
                SpiritAwakenConfig.COST_ITEM_ID,
                SpiritAwakenConfig.COST_ITEM_QTY, false, false);
        chr.gainMeso(-SpiritAwakenConfig.COST_MESO, false);

        if (Randomizer.nextInt(100) >= SpiritAwakenConfig.SUCCESS_RATE) {
            return AwakenResult.rollFail("灵韵未稳，觉醒失败……\r\n材料已消耗，现有灵韵保持不变。");
        }

        int skillId = rollSkill(chr.getJob().getId());
        if (skillId <= 0 || SpiritAwakenConfig.isBanned(skillId)) {
            return AwakenResult.rollFail("抽池异常，请联系管理员。（材料已扣，请报销）");
        }

        org.gms.client.Skill skill = org.gms.client.SkillFactory.getSkill(skillId);
        int cappedMax = SpiritAwakenConfig.maxLevel(skillId);
        if (skill != null && skill.getMaxLevel() > 0) {
            cappedMax = Math.min(cappedMax, skill.getMaxLevel());
        }

        int oldId = equip.getEquipSkillId();
        int oldLv = equip.getEquipSkillLevel();
        int newLevel;
        if (oldId == skillId && oldLv > 0) {
            newLevel = Math.min(oldLv + 1, cappedMax);
        } else {
            newLevel = 1;
        }
        equip.setEquipSkillId(skillId);
        equip.setEquipSkillLevel(newLevel);
        equip.setEquipSkillExpire(0L);
        chr.forceUpdateItem(equip);
        chr.markCombatStatsDirty();
        // 若正好穿着同 item 引用（少见，一般从背包操作）或同步刷新套装/灵韵层
        chr.equipChanged();

        String msg = "灵韵觉醒成功！\r\n附加技能：#s" + skillId + "#  Lv." + newLevel;
        if (oldId > 0 && oldId != skillId) {
            msg += "\r\n（原灵韵 #s" + oldId + "# 已被覆盖）";
        } else if (oldId == skillId && newLevel > oldLv) {
            msg += "\r\n（同技提升 " + oldLv + " → " + newLevel + "）";
        }
        return AwakenResult.win(skillId, newLevel, msg);
    }

    static int rollSkill(int jobId) {
        boolean useCommon = Randomizer.nextInt(100) < SpiritAwakenConfig.COMMON_POOL_CHANCE;
        List<SpiritAwakenPools.WeightedSkill> pool;
        if (useCommon) {
            pool = SpiritAwakenPools.buildCommonPool();
        } else {
            SpiritAwakenPools.JobBranch branch = SpiritAwakenPools.resolveBranch(jobId);
            pool = SpiritAwakenPools.forBranch(branch);
            if (pool.isEmpty()) {
                pool = SpiritAwakenPools.buildCommonPool();
            }
        }
        return pickWeighted(pool);
    }

    static int pickWeighted(List<SpiritAwakenPools.WeightedSkill> pool) {
        int total = 0;
        for (SpiritAwakenPools.WeightedSkill s : pool) {
            if (SpiritAwakenConfig.isBanned(s.skillId())) {
                continue;
            }
            total += Math.max(0, s.weight());
        }
        if (total <= 0) {
            return 0;
        }
        int roll = Randomizer.nextInt(total);
        int acc = 0;
        for (SpiritAwakenPools.WeightedSkill s : pool) {
            if (SpiritAwakenConfig.isBanned(s.skillId())) {
                continue;
            }
            acc += Math.max(0, s.weight());
            if (roll < acc) {
                return s.skillId();
            }
        }
        return 0;
    }

    /** 从角色当前穿戴收集灵韵技能（已映射为本职 ID）。 */
    public static void collectEquippedSkills(Character chr, java.util.Map<Integer, Integer> into) {
        SpiritWearSkills.collectRemapped(chr, into);
    }

    /** @deprecated 改用 {@link SpiritWearSkills#sync} */
    @Deprecated
    public static void syncWearSkills(Character chr) {
        SpiritWearSkills.sync(chr);
    }
}
