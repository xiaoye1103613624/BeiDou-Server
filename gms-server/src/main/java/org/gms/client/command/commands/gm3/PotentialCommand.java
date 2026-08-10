package org.gms.client.command.commands.gm3;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.potential.PotentialHyperConfig;
import org.gms.potential.PotentialHyperService;
import org.gms.potential.PotentialRules095;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * 潜能 / Hyper / 附加潜能 / 灵魂 / 星岩 测试命令。
 * <pre>
 * !potential                         — 列出穿戴+背包装备栏里已有潜能/星的装备
 * !potential star &lt;槽&gt; &lt;星&gt;
 * !potential set &lt;槽&gt; &lt;品阶&gt; o1 o2 o3
 * !potential clear &lt;槽&gt;
 * !potential roll &lt;槽&gt;                 — 附加隐藏潜能（需 reveal/放大镜）
 * !potential reveal &lt;槽&gt;               — GM 立即鉴定
 * !potential hide &lt;槽&gt;                 — GM 压回隐藏态
 * !potential magnify &lt;槽&gt; [镜ID]       — 模拟放大镜
 * !potential req &lt;槽&gt;                  — 查 info/reqLevel（与 tip/放大镜档一致）
 * !potential bonus set|roll|clear ...
 * !potential cube &lt;槽&gt;               — 主潜能魔方重随（独特不可用）
 * !potential supercube &lt;槽&gt;          — 超级魔方重随（可用独特/传说，~8%升阶）
 * !potential bonuscube &lt;槽&gt;          — 附加潜能魔方重随
 * !potential grade &lt;槽&gt;              — 主潜能品阶+1
 * !potential soul set &lt;槽&gt; &lt;soulId&gt; &lt;option&gt;
 * !potential soul clear &lt;槽&gt;
 * !potential socket set &lt;槽&gt; &lt;option&gt;
 * !potential socket clear &lt;槽&gt;
 * </pre>
 */
public class PotentialCommand extends Command {
    {
        setDescription("潜能/Hyper/附加/灵魂/星岩 查看与强制设置");
    }

    @Override
    public void execute(Client c, String[] params) {
        Character chr = c.getPlayer();
        if (params.length == 0) {
            listAll(chr);
            return;
        }
        String sub = params[0].toLowerCase();
        try {
            switch (sub) {
                case "set" -> {
                    if (params.length < 6) {
                        chr.dropMessage(5, "用法: !potential set <槽> <品阶> <o1> <o2> <o3>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    int grade = Integer.parseInt(params[2]);
                    int o1 = Integer.parseInt(params[3]);
                    int o2 = Integer.parseInt(params[4]);
                    int o3 = Integer.parseInt(params[5]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    PotentialHyperService.setPotential(eq, grade, o1, o2, o3);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "已设置: " + formatEquip(eq));
                }
                case "star" -> {
                    if (params.length < 3) {
                        chr.dropMessage(5, "用法: !potential star <槽> <星>  （背包第一格用 1，不是 -11）");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    int star = Integer.parseInt(params[2]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    PotentialHyperService.setEnhance(eq, star);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "已设星: " + formatEquip(eq));
                }
                case "clear" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential clear <槽>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    PotentialHyperService.clear(eq);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "已清空: " + formatEquip(eq));
                }
                case "roll" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential roll <槽>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    eq.setPotential1(0);
                    eq.setPotential2(0);
                    eq.setPotential3(0);
                    eq.setPotentialGrade((byte) 0);
                    var r = PotentialHyperService.applyPotentialScroll(chr, eq, 2049402, true);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "roll=" + r + " " + formatEquip(eq) + " （隐藏态，需 !potential reveal 或放大镜）");
                }
                case "reveal" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential reveal <槽>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.revealMainPotential(chr, eq);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "reveal=" + r + " " + formatEquip(eq));
                }
                case "hide" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential hide <槽>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    if (!PotentialRules095.hasMainPotential(eq.getPotentialGrade(), eq.getPotential1())) {
                        chr.dropMessage(5, "该装备无主潜能，无法隐藏。");
                        return;
                    }
                    PotentialHyperService.hideMainPotential(eq);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "已隐藏: " + formatEquip(eq));
                }
                case "magnify" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential magnify <槽> [镜ID=2460003]");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    int glass = params.length >= 3 ? Integer.parseInt(params[2]) : PotentialHyperConfig.ITEM_MAGNIFY_ANY;
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.applyMagnify(chr, eq, glass);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "magnify=" + r + " glass=" + glass + " " + formatEquip(eq));
                }
                case "req" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential req <槽>  （查 tip/放大镜用的 info/reqLevel）");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    int req = ItemInformationProvider.getInstance().getEquipLevelReq(eq.getItemId());
                    boolean m30 = PotentialHyperConfig.magnifyFitsEquipLevel(PotentialHyperConfig.ITEM_MAGNIFY_LV30, req);
                    boolean m70 = PotentialHyperConfig.magnifyFitsEquipLevel(PotentialHyperConfig.ITEM_MAGNIFY_LV70, req);
                    boolean m120 = PotentialHyperConfig.magnifyFitsEquipLevel(PotentialHyperConfig.ITEM_MAGNIFY_LV120, req);
                    chr.dropMessage(5, String.format(
                            "id=%d reqLevel=%d → 镜2460000=%s / 2460001=%s / 2460002=%s / 2460003=OK",
                            eq.getItemId(), req, m30 ? "OK" : "否", m70 ? "OK" : "否", m120 ? "OK" : "否"));
                }
                case "cube" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential cube <槽>  （奇迹魔方 5062000）");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.applyMainCube(chr, eq, PotentialHyperConfig.ITEM_MIRACLE_CUBE, true);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "cube(5062000)=" + r + " " + formatEquip(eq)
                            + (PotentialHyperConfig.CUBE_RESET_TO_HIDDEN ? " →用 !potential magnify 鉴定" : ""));
                }
                case "premiumcube" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential premiumcube <槽>  （高级魔方 5062001）");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.applyPremiumCube(chr, eq, PotentialHyperConfig.ITEM_PREMIUM_CUBE, true);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "premiumcube(5062001)=" + r + " " + formatEquip(eq));
                }
                case "supercube" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential supercube <槽>  （超级魔方 5062002）");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.applySuperCube(chr, eq, PotentialHyperConfig.ITEM_SUPER_CUBE, true);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "supercube(5062002)=" + r + " " + formatEquip(eq));
                }
                case "bonuscube" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential bonuscube <槽>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.applyBonusCube(chr, eq, PotentialHyperConfig.ITEM_BONUS_CUBE, true);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "bonuscube=" + r + " " + formatEquip(eq));
                }
                case "grade" -> {
                    if (params.length < 2) {
                        chr.dropMessage(5, "用法: !potential grade <槽>");
                        return;
                    }
                    short slot = Short.parseShort(params[1]);
                    Equip eq = getEquip(chr, slot);
                    if (eq == null) {
                        chr.dropMessage(5, emptySlotHint(slot));
                        return;
                    }
                    var r = PotentialHyperService.applyGradeUpgrade(chr, eq, PotentialHyperConfig.ITEM_GRADE_UP, true);
                    refresh(c, chr, eq);
                    chr.dropMessage(5, "grade=" + r + " " + formatEquip(eq));
                }
                case "bonus" -> handleBonus(c, chr, params);
                case "soul" -> handleSoul(c, chr, params);
                case "socket" -> handleSocket(c, chr, params);
                case "help" -> printHelp(chr);
                default -> printHelp(chr);
            }
        } catch (Exception e) {
            chr.dropMessage(5, "参数错误: " + e.getMessage());
            printHelp(chr);
        }
    }

    private static void handleBonus(Client c, Character chr, String[] params) {
        if (params.length < 2) {
            chr.dropMessage(5, "用法: !potential bonus set|roll|clear ...");
            return;
        }
        String op = params[1].toLowerCase();
        switch (op) {
            case "set" -> {
                if (params.length < 7) {
                    chr.dropMessage(5, "用法: !potential bonus set <槽> <品阶> <o1> <o2> <o3>");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                int grade = Integer.parseInt(params[3]);
                int o1 = Integer.parseInt(params[4]);
                int o2 = Integer.parseInt(params[5]);
                int o3 = Integer.parseInt(params[6]);
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                if (eq.getPotentialGrade() <= 0 && eq.getPotential1() <= 0) {
                    chr.dropMessage(5, "请先设主潜能（!potential set / roll），再设附加潜能。");
                    return;
                }
                PotentialHyperService.setBonusPotential(eq, grade, o1, o2, o3);
                refresh(c, chr, eq);
                chr.dropMessage(5, "已设附加潜能: " + formatEquip(eq));
            }
            case "roll" -> {
                if (params.length < 3) {
                    chr.dropMessage(5, "用法: !potential bonus roll <槽>");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                PotentialHyperService.clearBonus(eq);
                var r = PotentialHyperService.applyBonusPotentialScroll(chr, eq, 2049902, true);
                refresh(c, chr, eq);
                chr.dropMessage(5, "bonus roll=" + r + " " + formatEquip(eq));
            }
            case "clear" -> {
                if (params.length < 3) {
                    chr.dropMessage(5, "用法: !potential bonus clear <槽>");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                PotentialHyperService.clearBonus(eq);
                refresh(c, chr, eq);
                chr.dropMessage(5, "已清附加潜能: " + formatEquip(eq));
            }
            default -> chr.dropMessage(5, "用法: !potential bonus set|roll|clear ...");
        }
    }

    private static void handleSoul(Client c, Character chr, String[] params) {
        if (params.length < 2) {
            chr.dropMessage(5, "用法: !potential soul set|clear ...");
            return;
        }
        String op = params[1].toLowerCase();
        switch (op) {
            case "set" -> {
                if (params.length < 5) {
                    chr.dropMessage(5, "用法: !potential soul set <槽> <soulId> <option>");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                int soulId = Integer.parseInt(params[3]);
                int option = Integer.parseInt(params[4]);
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                PotentialHyperService.setSoul(eq, soulId, option);
                refresh(c, chr, eq);
                PacketCreator.broadcastSoulWeaponEffect(chr);
                chr.dropMessage(5, "已设灵魂: " + formatEquip(eq));
            }
            case "clear" -> {
                if (params.length < 3) {
                    chr.dropMessage(5, "用法: !potential soul clear <槽>");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                PotentialHyperService.clearSoul(eq);
                refresh(c, chr, eq);
                chr.dropMessage(5, "已清灵魂: " + formatEquip(eq));
            }
            default -> chr.dropMessage(5, "用法: !potential soul set|clear ...");
        }
    }

    private static void handleSocket(Client c, Character chr, String[] params) {
        if (params.length < 2) {
            chr.dropMessage(5, "用法: !potential socket set|clear ...");
            return;
        }
        String op = params[1].toLowerCase();
        switch (op) {
            case "set" -> {
                if (params.length < 4) {
                    chr.dropMessage(5, "用法: !potential socket set <槽> <opt1> [opt2] [opt3]");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                int option = Integer.parseInt(params[3]);
                int option2 = params.length >= 5 ? Integer.parseInt(params[4]) : 0;
                int option3 = params.length >= 6 ? Integer.parseInt(params[5]) : 0;
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                PotentialHyperService.setSocket(eq, option, option2, option3);
                refresh(c, chr, eq);
                chr.dropMessage(5, "已设星岩: " + formatEquip(eq));
            }
            case "clear" -> {
                if (params.length < 3) {
                    chr.dropMessage(5, "用法: !potential socket clear <槽>");
                    return;
                }
                short slot = Short.parseShort(params[2]);
                Equip eq = getEquip(chr, slot);
                if (eq == null) {
                    chr.dropMessage(5, emptySlotHint(slot));
                    return;
                }
                PotentialHyperService.clearSocket(eq);
                refresh(c, chr, eq);
                chr.dropMessage(5, "已清星岩: " + formatEquip(eq));
            }
            default -> chr.dropMessage(5, "用法: !potential socket set|clear ...");
        }
    }

    private static void printHelp(Character chr) {
        chr.dropMessage(5, "用法: !potential | star | set | clear | roll | reveal | hide | magnify | req");
        chr.dropMessage(5, "      cube | premiumcube | supercube | grade | bonus set/roll/clear | bonuscube");
        chr.dropMessage(5, "      soul set/clear | socket set/clear");
        chr.dropMessage(5, "槽: 穿戴用负数(手套=-8)，背包装备栏第一格=1。");
        chr.dropMessage(5, "魔方Cash: 5062000奇迹 / 5062001高级 / 5062002超级（别名2049910/16仍可用）");
    }

    private static String emptySlotHint(short slot) {
        if (slot < 0) {
            return "穿戴槽 " + slot + " 无装备。"
                    + (slot == -11 ? " 注意:-11 不是背包第一格；手套穿戴槽是 -8，背包第一格请用 1。" : "");
        }
        return "背包装备栏槽 " + slot + " 无装备。第一格请用 1。";
    }

    private static String formatEquip(Equip eq) {
        String where = eq.getPosition() < 0 ? "穿戴" : "背包";
        String d = PotentialHyperService.describe(eq);
        return String.format("[%s槽%d] id=%d %s", where, eq.getPosition(), eq.getItemId(),
                d.isEmpty() ? "(无潜能/星)" : d);
    }

    private static Equip getEquip(Character chr, short slot) {
        if (slot < 0) {
            slot = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                    chr.getInventory(InventoryType.EQUIPPED), slot);
            Item it = chr.getInventory(InventoryType.EQUIPPED).getItem(slot);
            return it instanceof Equip e ? e : null;
        }
        Item it = chr.getInventory(InventoryType.EQUIP).getItem(slot);
        return it instanceof Equip e ? e : null;
    }

    private static void refresh(Client c, Character chr, Equip eq) {
        List<ModifyInventory> mods = new ArrayList<>(2);
        mods.add(new ModifyInventory(3, eq));
        mods.add(new ModifyInventory(0, eq));
        c.sendPacket(PacketCreator.modifyInventory(true, mods));
        if (eq.getPosition() < 0) {
            chr.equipChanged();
        }
    }

    private static void listAll(Character chr) {
        boolean any = false;
        for (Item it : chr.getInventory(InventoryType.EQUIPPED)) {
            if (!(it instanceof Equip eq)) {
                continue;
            }
            String d = PotentialHyperService.describe(eq);
            if (!d.isEmpty()) {
                chr.dropMessage(5, formatEquip(eq));
                any = true;
            }
        }
        for (Item it : chr.getInventory(InventoryType.EQUIP)) {
            if (!(it instanceof Equip eq)) {
                continue;
            }
            String d = PotentialHyperService.describe(eq);
            if (!d.isEmpty()) {
                chr.dropMessage(5, formatEquip(eq));
                any = true;
            }
        }
        if (!any) {
            chr.dropMessage(5, "当前穿戴/背包装备栏均无潜能或 Hyper 星。");
            chr.dropMessage(5, "测背包第一格: !potential star 1 1");
            chr.dropMessage(5, "测灵魂: !potential soul set 1 2049914 1");
        }
    }
}
