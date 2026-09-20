package org.gms.talent;

import org.gms.client.Character;
import org.gms.constants.inventory.ItemConstants;
import org.gms.client.inventory.manipulator.InventoryManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * 天赋静态门面：供 NPC 脚本（{@code 9031014.js}）与 GM 调用。
 */
public final class TalentService {
    private TalentService() {
    }

    public static List<TalentId> listByTier(TalentTier tier) {
        List<TalentId> out = new ArrayList<>();
        for (TalentId t : TalentId.values()) {
            if (t.getTier() == tier) {
                out.add(t);
            }
        }
        return out;
    }

    public static String tierStatusText(Character player) {
        StringBuilder sb = new StringBuilder("天赋阶层状态：\r\n");
        for (TalentTier t : TalentTier.values()) {
            boolean unlocked = player.getLevel() >= t.getUnlockLevel();
            sb.append(t.getLabel()).append('：')
                    .append(unlocked ? "已解锁" : "需 " + t.getUnlockLevel() + " 级")
                    .append("\r\n");
        }
        return sb.toString();
    }

    public static boolean isTierUnlocked(Character player, TalentTier tier) {
        return player.getLevel() >= tier.getUnlockLevel();
    }

    public static int getLevel(Character player, int talentId) {
        TalentId tid = TalentId.fromId(talentId);
        return tid == null ? 0 : player.getTalentManager().getLevel(tid);
    }

    public static TalentManager.LearnResult learn(Character player, int talentId) {
        TalentId tid = TalentId.fromId(talentId);
        if (tid == null) {
            return new TalentManager.LearnResult(false, false, 0, "无效天赋。");
        }
        return player.getTalentManager().learn(tid);
    }

    public static int ultimateLearnRate(Character player, int talentId) {
        TalentId tid = TalentId.fromId(talentId);
        return tid == null ? 0 : TalentConfig.ultimateSuccessRate(player.getTalentManager().getLevel(tid));
    }

    public static String exchangeTalentBook(Character player, int itemId) {
        TalentId tid = TalentId.fromItemId(itemId);
        if (tid == null) {
            return "该道具不对应任何天赋。";
        }
        int next = player.getTalentManager().exchangeOneBook(tid);
        return next > 0 ? "兑换成功，天赋提升至 Lv." + next : "无法兑换（可能未学习或已满级）。";
    }

    public static String buyTalentBook(Character player, int itemId) {
        TalentId tid = TalentId.fromItemId(itemId);
        if (tid == null) {
            return "该道具不对应任何天赋。";
        }
        int price = 1_000_000 * tid.getTier().getOrder();
        if (player.getMeso() < price) {
            return "金币不足。";
        }
        if (!InventoryManipulator.checkSpace(player.getClient(), itemId, 1, "")) {
            return "背包空间不足。";
        }
        player.gainMeso(-price, false);
        InventoryManipulator.addById(player.getClient(), itemId, (short) 1);
        return "购买成功。";
    }
}
