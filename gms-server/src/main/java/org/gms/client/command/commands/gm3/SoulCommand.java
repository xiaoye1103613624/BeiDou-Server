package org.gms.client.command.commands.gm3;

import org.gms.client.Client;
import org.gms.client.Character;
import org.gms.client.command.Command;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.constants.inventory.ItemConstants;
import org.gms.potential.PotentialHyperService;
import org.gms.soul.SoulOrbConfig;
import org.gms.soul.SoulWeaponService;
import org.gms.util.PacketCreator;

import java.util.List;

/**
 * 灵魂武器测试命令。
 * <pre>
 * !soul help
 * !soulskill / !soul skill
 * !soul set w 2591008     — 对当前穿戴武器强制开槽+镶珠（推荐）
 * !soul set -11 2591008   — 武器槽固定为 -11（-1 是帽子！）
 * !soul open w | clear w | info | list
 * </pre>
 */
public class SoulCommand extends Command {
    /** 主武器穿戴槽（帽子是 -1，勿混用） */
    private static final short WEAPON_SLOT = -11;

    {
        setDescription("灵魂武器：开槽/镶珠/技能");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character chr = c.getPlayer();
        if (params.length < 1) {
            help(chr);
            return;
        }
        String op = params[0].toLowerCase();
        switch (op) {
            case "help", "?" -> help(chr);
            case "skill", "cast" -> SoulWeaponService.useSoulSkill(chr);
            case "info", "status" -> showInfo(chr);
            case "open" -> forceOpen(c, chr, params);
            case "set" -> forceSet(c, chr, params);
            case "clear" -> forceClear(c, chr, params);
            case "list" -> listOrbs(chr);
            default -> help(chr);
        }
    }

    private static void help(Character chr) {
        chr.dropMessage(5, "=== 灵魂武器 ===");
        chr.dropMessage(5, "!soulskill — 释放技能");
        chr.dropMessage(5, "!soul set w 2591008  — 推荐：当前穿戴武器开槽+镶珠");
        chr.dropMessage(5, "!soul set -11 2591008 — 武器槽；!soul open w | clear w | info | list");
        chr.dropMessage(5, "注意: 穿戴槽 -1=帽子, -11=武器。流程也可: 砸2049914→砸259100x");
    }

    private static void listOrbs(Character chr) {
        chr.dropMessage(5, "--- 早期宝珠 ---");
        for (int id : SoulOrbConfig.allOrbIds()) {
            SoulOrbConfig.OrbDef o = SoulOrbConfig.getOrb(id);
            chr.dropMessage(5, id + " " + o.name() + " → " + o.skillName() + " (" + o.skill() + ")");
        }
    }

    private static void showInfo(Character chr) {
        Equip w = findEquippedWeapon(chr);
        if (w == null) {
            chr.dropMessage(5, "当前未穿戴武器（槽 -11）。");
            return;
        }
        chr.dropMessage(5, "穿戴武器 slot=" + w.getPosition() + " id=" + w.getItemId()
                + " " + SoulWeaponService.describe(w));
        int fd = SoulWeaponService.getActiveFinalDamR(chr.getId());
        if (fd > 0) {
            chr.dropMessage(5, "灵魂Buff: 最终伤害 +" + fd + "%");
        }
    }

    private static void forceOpen(Client c, Character chr, String[] params) {
        if (params.length < 2) {
            chr.dropMessage(5, "用法: !soul open w  或  !soul open -11");
            return;
        }
        Equip eq = resolveEquip(chr, params[1]);
        if (eq == null) {
            return;
        }
        if (!ItemConstants.isWeapon(eq.getItemId())) {
            chr.dropMessage(5, "【灵魂】目标不是武器（item=" + eq.getItemId()
                    + " slot=" + eq.getPosition() + "）。武器槽是 -11，-1 是帽子。");
            return;
        }
        var r = SoulWeaponService.applyEnchanter(chr, eq, 2049914, true);
        refresh(c, chr, eq);
        chr.dropMessage(5, "结果=" + r + " " + SoulWeaponService.describe(eq));
    }

    private static void forceSet(Client c, Character chr, String[] params) {
        if (params.length < 3) {
            chr.dropMessage(5, "用法: !soul set w 2591008  或  !soul set -11 2591008");
            return;
        }
        Equip eq = resolveEquip(chr, params[1]);
        if (eq == null) {
            return;
        }
        int orbId;
        try {
            orbId = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            chr.dropMessage(5, "宝珠ID无效: " + params[2]);
            return;
        }
        if (!ItemConstants.isWeapon(eq.getItemId())) {
            chr.dropMessage(5, "【灵魂】目标不是武器（item=" + eq.getItemId()
                    + " slot=" + eq.getPosition() + "）。"
                    + "你写的槽若是 -1 那是帽子；请用: !soul set w " + orbId);
            return;
        }
        if (!SoulOrbConfig.isOrb(orbId)) {
            chr.dropMessage(5, "【灵魂】不是早期宝珠ID: " + orbId + "（应用 2591000~2591009）");
            return;
        }
        if (!SoulWeaponService.hasSoulSlot(eq)) {
            var open = SoulWeaponService.applyEnchanter(chr, eq, 2049914, true);
            if (open != PotentialHyperService.Result.SUCCESS) {
                chr.dropMessage(5, "开槽失败: " + open);
                return;
            }
        }
        var r = SoulWeaponService.applyOrb(chr, eq, orbId, true);
        refresh(c, chr, eq);
        if (r == PotentialHyperService.Result.SUCCESS) {
            PacketCreator.broadcastSoulWeaponEffect(chr);
        }
        chr.dropMessage(5, "结果=" + r + " slot=" + eq.getPosition()
                + " " + SoulWeaponService.describe(eq));
    }

    private static void forceClear(Client c, Character chr, String[] params) {
        if (params.length < 2) {
            chr.dropMessage(5, "用法: !soul clear w  或  !soul clear -11");
            return;
        }
        Equip eq = resolveEquip(chr, params[1]);
        if (eq == null) {
            return;
        }
        var r = SoulWeaponService.clearSoul(chr, eq);
        refresh(c, chr, eq);
        chr.dropMessage(5, "结果=" + r);
    }

    /**
     * 解析槽位：{@code w}/{@code weapon}/{@code -11} → 穿戴武器；数字 → 指定格；
     * {@code -1} 会提示那是帽子。
     */
    private static Equip resolveEquip(Character chr, String token) {
        if (token == null || token.isBlank()) {
            chr.dropMessage(5, "缺少槽位参数。");
            return null;
        }
        String t = token.trim().toLowerCase();
        if (t.equals("w") || t.equals("wp") || t.equals("weapon") || t.equals("武器")) {
            Equip w = findEquippedWeapon(chr);
            if (w == null) {
                chr.dropMessage(5, "未穿戴武器（请先装备武器到 -11 槽）。");
            }
            return w;
        }
        short slot;
        try {
            slot = Short.parseShort(t);
        } catch (NumberFormatException e) {
            chr.dropMessage(5, "槽位无效: " + token + "（可用 w 或 -11）");
            return null;
        }
        if (slot == -1) {
            chr.dropMessage(5, "提示: -1 是帽子槽，不是武器。已改为尝试武器槽 -11。");
            slot = WEAPON_SLOT;
        }
        Equip eq = getEquip(chr, slot);
        if (eq == null) {
            chr.dropMessage(5, "槽 " + slot + " 没有装备"
                    + (slot == WEAPON_SLOT ? "（请先穿上武器）" : "") + "。");
        }
        return eq;
    }

    private static Equip findEquippedWeapon(Character chr) {
        Item it = chr.getInventory(InventoryType.EQUIPPED).getItem(WEAPON_SLOT);
        if (it instanceof Equip eq && ItemConstants.isWeapon(eq.getItemId())) {
            return eq;
        }
        // 兜底：扫穿戴栏
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (item instanceof Equip eq && ItemConstants.isWeapon(eq.getItemId())) {
                return eq;
            }
        }
        return null;
    }

    private static Equip getEquip(Character chr, short slot) {
        Item it;
        if (slot < 0) {
            slot = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                    chr.getInventory(InventoryType.EQUIPPED), slot);
            it = chr.getInventory(InventoryType.EQUIPPED).getItem(slot);
        } else {
            it = chr.getInventory(InventoryType.EQUIP).getItem(slot);
        }
        return it instanceof Equip e ? e : null;
    }

    private static void refresh(Client c, Character chr, Equip eq) {
        c.sendPacket(PacketCreator.modifyInventory(true, List.of(
                new ModifyInventory(3, eq),
                new ModifyInventory(0, eq))));
        if (eq.getPosition() < 0) {
            chr.equipChanged();
        }
    }
}
