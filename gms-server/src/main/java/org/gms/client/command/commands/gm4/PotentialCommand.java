package org.gms.client.command.commands.gm4;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.potential.PotentialHyperConfig;
import org.gms.potential.PotentialHyperService;
import org.gms.potential.PotentialRules095;
import org.gms.server.ItemInformationProvider;

/**
 * 潜能 / Hyper / 星岩 GM 管理指令。
 * <p>
 * 子命令：
 * <ul>
 *   <li>give &lt;槽位&gt; &lt;品阶&gt;：赋予主潜能</li>
 *   <li>hyper &lt;槽位&gt; [星级]：设置 Hyper 强化星级</li>
 *   <li>cube &lt;槽位&gt;：重铸主潜能</li>
 *   <li>magnify &lt;槽位&gt;：揭示附加潜能</li>
 *   <li>socket &lt;槽位&gt; &lt;词条ID&gt;：设置星岩槽1</li>
 *   <li>clear &lt;槽位&gt;：清空全部潜能/Hyper/星岩</li>
 * </ul>
 * 槽位 &lt; 0 表示身上装备，≥ 0 表示背包装备。
 */
public class PotentialCommand extends Command {
    {
        setDescription("潜能/Hyper管理：!potential <give|hyper|cube|magnify|socket|clear> <槽位> [参数]");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 2) {
            player.yellowMessage("用法：!potential <give|hyper|cube|magnify|socket|clear> <槽位> [参数]");
            return;
        }
        String sub = params[0];
        short slot;
        try {
            slot = Short.parseShort(params[1]);
        } catch (NumberFormatException e) {
            player.yellowMessage("槽位必须是数字。");
            return;
        }
        InventoryType invType = slot < 0 ? InventoryType.EQUIPPED : InventoryType.EQUIP;
        Item item = player.getInventory(invType).getItem(slot);
        if (!(item instanceof Equip equip)) {
            player.yellowMessage("该槽位不是装备。");
            return;
        }
        Integer req = ItemInformationProvider.getInstance().getEquipLevelReq(equip.getItemId());
        int reqLevel = req != null ? req : 0;

        switch (sub) {
            case "give" -> {
                int grade = params.length >= 3 ? parseInt(params[2], 1) : 1;
                grade = PotentialRules095.stateToGrade(grade);
                int[] lines = PotentialHyperService.rollMainPotential(reqLevel, grade);
                equip.setPotential1(lines[0]);
                equip.setPotential2(lines[1]);
                equip.setPotential3(lines[2]);
                equip.setPotentialGrade((byte) grade);
                refresh(player, equip);
                player.dropMessage(5, "已赋予主潜能（品阶 " + grade + "）。");
            }
            case "hyper" -> {
                int star = params.length >= 3 ? parseInt(params[2], 1) : 1;
                star = Math.max(0, Math.min(PotentialHyperConfig.MAX_ENHANCE, star));
                equip.setEnhance((byte) star);
                refresh(player, equip);
                player.dropMessage(5, "Hyper 强化至 " + star + " 星。");
            }
            case "cube" -> {
                int grade = equip.getPotentialGrade() > 0 ? equip.getPotentialGrade() : 1;
                int[] lines = PotentialHyperService.rollMainPotential(reqLevel, grade);
                equip.setPotential1(lines[0]);
                equip.setPotential2(lines[1]);
                equip.setPotential3(lines[2]);
                refresh(player, equip);
                player.dropMessage(5, "主潜能已重铸。");
            }
            case "magnify" -> {
                int grade = equip.getPotentialGrade() > 0 ? equip.getPotentialGrade() : 1;
                int[] lines = PotentialHyperService.rollBonusPotential(reqLevel, grade);
                equip.setBonusPotential1(lines[0]);
                equip.setBonusPotential2(lines[1]);
                equip.setBonusPotential3(lines[2]);
                equip.setBonusPotentialGrade((byte) grade);
                refresh(player, equip);
                player.dropMessage(5, "附加潜能已揭示。");
            }
            case "socket" -> {
                if (params.length < 3) {
                    player.yellowMessage("需指定词条ID：!potential socket <槽位> <词条ID>");
                    return;
                }
                int optId = parseInt(params[2], 0);
                equip.setSocket1(optId);
                refresh(player, equip);
                player.dropMessage(5, "星岩槽1已设置为 " + optId + "。");
            }
            case "clear" -> {
                equip.setPotential1(0);
                equip.setPotential2(0);
                equip.setPotential3(0);
                equip.setPotentialGrade((byte) 0);
                equip.setBonusPotential1(0);
                equip.setBonusPotential2(0);
                equip.setBonusPotential3(0);
                equip.setBonusPotentialGrade((byte) 0);
                equip.setEnhance((byte) 0);
                equip.setSocket1(0);
                equip.setSocket2(0);
                equip.setSocket3(0);
                refresh(player, equip);
                player.dropMessage(5, "潜能/Hyper/星岩已清空。");
            }
            default -> player.yellowMessage("未知子命令：" + sub);
        }
    }

    private static void refresh(Character player, Equip equip) {
        player.forceUpdateItem(equip);
        player.equipChanged();
        player.markCombatStatsDirty();
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
