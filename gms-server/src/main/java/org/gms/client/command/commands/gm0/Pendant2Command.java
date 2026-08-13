package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.server.ItemInformationProvider;

/**
 * 第二吊坠（−51/−151）查询/卸下。v083 无 BP51 UI 时装备栏看不见，用此指令取回。
 * 不依赖 ForceDrawLoop。
 */
public class Pendant2Command extends Command {
    {
        setDescription("第二吊坠：查看或卸下 −51 槽（@第二坠 / @pendant2）");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character chr = c.getPlayer();
        Inventory eqpd = chr.getInventory(InventoryType.EQUIPPED);
        Inventory eqp = chr.getInventory(InventoryType.EQUIP);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        Equip main = (Equip) eqpd.getItem((short) -17);
        Equip cashMain = (Equip) eqpd.getItem((short) -117);
        Equip second = (Equip) eqpd.getItem((short) -51);
        Equip cashSecond = (Equip) eqpd.getItem((short) -151);

        boolean unequip = params.length > 0 && ("unequip".equalsIgnoreCase(params[0])
                || "卸下".equals(params[0]) || "off".equalsIgnoreCase(params[0]));

        if (!unequip) {
            chr.dropMessage(5, "主坠 −17: " + formatSlot(ii, main)
                    + " | 第二坠 −51: " + formatSlot(ii, second));
            if (second != null) {
                // 核对 −51 是否进 recalcEquipStats（四维应随穿卸变化）
                chr.dropMessage(5, "−51 装备四维: " + formatStats(second)
                        + " | 面板合计 STR/DEX/INT/LUK="
                        + chr.getTotalStr() + "/" + chr.getTotalDex() + "/"
                        + chr.getTotalInt() + "/" + chr.getTotalLuk());
            }
            if (cashMain != null || cashSecond != null) {
                chr.dropMessage(5, "点装 −117: " + formatSlot(ii, cashMain)
                        + " | −151: " + formatSlot(ii, cashSecond));
            }
            chr.dropMessage(5, "卸下第二坠：@第二坠 卸下  （或 @pendant2 unequip）");
            return;
        }

        short slot = second != null ? (short) -51 : (cashSecond != null ? (short) -151 : 0);
        if (slot == 0) {
            chr.dropMessage(5, "第二吊坠槽为空，无需卸下。");
            return;
        }
        short dst = eqp.getNextFreeSlot();
        if (dst < 0) {
            chr.dropMessage(5, "装备背包已满，无法卸下第二吊坠。");
            return;
        }
        InventoryManipulator.unequip(c, slot, dst);
        chr.dropMessage(5, "已卸下第二吊坠到装备栏第 " + dst + " 格。");
    }

    private static String formatSlot(ItemInformationProvider ii, Equip eq) {
        if (eq == null) {
            return "(空)";
        }
        String name = ii.getName(eq.getItemId());
        if (name == null || name.isEmpty()) {
            name = String.valueOf(eq.getItemId());
        }
        return name + "(" + eq.getItemId() + ")";
    }

    private static String formatStats(Equip eq) {
        return "STR" + eq.getStr() + " DEX" + eq.getDex()
                + " INT" + eq.getInt() + " LUK" + eq.getLuk();
    }
}
