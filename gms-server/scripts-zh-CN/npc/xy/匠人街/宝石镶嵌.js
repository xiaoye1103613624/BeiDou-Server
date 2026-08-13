// 匠人街 · 梅兹 · 宝石镶嵌（宝1~宝16）
// 16级固定表：每级所需等级宝石 + 对应属性水晶 + 金币；成功率逐级递减。
// 属性每级新增增量，累积叠加：攻击/魔攻 +等级、对应属性 +2×等级。
// 智慧水晶加魔法力，其余水晶加攻击力。
// 成功：消耗 等级宝石+水晶+金币，等级+1（不可跳级）。
// 失败：消耗 水晶+金币，保留 等级宝石，装备等级不变。
var GemService = Java.type("org.gms.gem.GemService");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ModifyInventory = Java.type("org.gms.client.inventory.ModifyInventory");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var OpLogManager = Java.type("org.gms.log.OpLogManager");

var status = -1;
var selectedSlot = -1;
var equipObj = null;
var chosenType = -1;

function start() {
    status = -1;
    selectedSlot = -1;
    equipObj = null;
    chosenType = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        showEquipList();
    } else if (status === 1) {
        if (selection === 9000) {
            cm.dispose();
            return;
        }
        selectedSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
        if (equipObj == null || !GemService.isInlayable(equipObj)) {
            cm.sendOk("#r该装备不可进行宝石镶嵌。#k\r\n仅支持 武器 / 上衣 / 裤子 / 套服。");
            cm.dispose();
            return;
        }
        showCrystalType();
    } else if (status === 2) {
        if (selection === 9000) {
            cm.dispose();
            return;
        }
        chosenType = selection;
        showConfirm();
    } else if (status === 3) {
        if (selection === 0) {
            doInlay();
        } else {
            cm.dispose();
        }
    }
}

function equipName(eq) {
    var n = ItemInformationProvider.getInstance().getName(eq.getItemId());
    return n || ("#" + eq.getItemId());
}

function showEquipList() {
    var t = "#e#b<宝石镶嵌>#k#n\r\n\r\n";
    t += "为装备逐级镶嵌 1~16 级宝石（#b宝1~宝16#k）。\r\n";
    t += "#r任意选择一种属性水晶，智慧水晶加魔法力，其余加攻击力。#k\r\n\r\n";
    t += "选择要镶嵌的装备（背包装备栏）：\r\n";
    var equips = listInlayable();
    if (equips.length === 0) {
        t += "#r背包装备栏没有可镶嵌装备（需 武器/上衣/裤子/套服）。#k\r\n";
    } else {
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.inlay > 0) t += " #e#r宝" + e.inlay + "#k#n";
            t += "#l\r\n";
        }
    }
    t += "\r\n#L9000##g返回#k#l";
    cm.sendSimple(t);
}

function showCrystalType() {
    var eq = equipObj;
    var cur = GemService.levelOf(eq);
    if (GemService.isMax(eq)) {
        cm.sendOk("#r已达最高镶嵌等级（宝" + GemService.MAX_LEVEL + "）！无法继续镶嵌。#k");
        cm.dispose();
        return;
    }
    var target = GemService.nextLevelOf(eq);

    var t = "#e#b>宝石镶嵌>#k#n\r\n\r\n";
    t += "装备：#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
    t += "当前镶嵌：#b宝" + cur + "#k / 宝" + GemService.MAX_LEVEL + "\r\n";
    t += "本次目标：#b宝" + target + "#k\r\n\r\n";
    t += "请选择本次镶嵌的水晶属性（每种水晶对应不同加成）：\r\n\r\n";
    for (var i = 0; i < 4; i++) {
        var cid = GemService.crystalId(i);
        t += "#L" + i + "##v" + cid + "# #t" + cid + "#  #r" + GemService.describeDelta(target, i) + "#k#l\r\n";
    }
    t += "#L9000##g返回#k#l";
    cm.sendSimple(t);
}

function showConfirm() {
    var eq = equipObj;
    var target = GemService.nextLevelOf(eq);
    var tier = GemService.tier(target);
    var cur = GemService.levelOf(eq);

    var t = "#e#b>宝石镶嵌 · 确认>#k#n\r\n\r\n";
    t += "装备：#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
    t += "当前 / 目标：#b宝" + cur + "#k → #b宝" + target + "#k\r\n";
    t += "属性：#r" + GemService.describeDelta(target, chosenType) + "#k\r\n\r\n";
    t += "消耗：\r\n";
    t += "• #v" + tier.gemId + "# #t" + tier.gemId + "# ×1\r\n";
    t += "• #v" + GemService.crystalId(chosenType) + "# #t" + GemService.crystalId(chosenType) + "# ×" + tier.crystals + "\r\n";
    t += "• 金币 #b" + fmtMeso(tier.meso) + "#k\r\n\r\n";
    t += "成功率：#r" + tier.ratePct + "%#k\r\n\r\n";
    t += "#r注意：无论成功与否，金币与水晶消耗；成功才消耗等级宝石并 +1 级，失败保留宝石、等级不变。#k\r\n\r\n";
    t += "#L0#确认镶嵌#l\r\n#L1#再想想#l";
    cm.sendSimple(t);
}

function doInlay() {
    var eq = equipObj;
    var cur = GemService.levelOf(eq);
    if (GemService.isMax(eq)) {
        cm.sendOk("已达最高镶嵌等级。");
        cm.dispose();
        return;
    }
    var target = GemService.nextLevelOf(eq);
    var tier = GemService.tier(target);
    var cryId = GemService.crystalId(chosenType);

    // 校验并预扣：金币 + 水晶（成功/失败均消耗）
    if (cm.getMeso() < tier.meso) {
        cm.sendOk("需要金币 #b" + fmtMeso(tier.meso) + "#k\r\n当前金币不足。");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(cryId, tier.crystals)) {
        cm.sendOk("需要 #r#t" + cryId + "# ×" + tier.crystals + "#k\r\n水晶不足。");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(tier.gemId, 1)) {
        cm.sendOk("需要 #r#t" + tier.gemId + "# ×1#k\r\n对应等级装备宝石不足。");
        cm.dispose();
        return;
    }

    cm.gainMeso(-tier.meso);
    cm.gainItem(cryId, -tier.crystals);

    var ok = GemService.roll(tier.ratePct);
    var name = equipName(eq);
    var newLevel = cur;
    if (ok) {
        cm.gainItem(tier.gemId, -1);
        GemService.applyType(eq, target, chosenType);
        GemService.upgrade(eq);
        newLevel = GemService.levelOf(eq);
    }
    refreshEquip(eq);

    if (ok) {
        cm.getPlayer().dropMessage(5, "【宝石镶嵌】#b宝" + newLevel + "#k 镶嵌成功！" + name + " 升至 宝" + newLevel + "（" + GemService.describeDelta(newLevel, chosenType) + "）");
        cm.sendOk("#b宝石镶嵌成功！#k\r\n" + name + " 已升至 #b宝" + newLevel + "#k。");
        OpLogManager.recordGem(cm.getPlayer(), "宝" + newLevel + " " + name, "镶嵌成功(" + cur + "→" + newLevel + ") " + GemService.typeName(chosenType) + " 装备#" + eq.getItemId() + " 目标宝石#" + tier.gemId);
    } else {
        cm.sendOk("#r镶嵌失败……#k\r\n金币与水晶已消耗，等级宝石已保留，装备仍为 宝" + cur + "。");
        OpLogManager.recordGem(cm.getPlayer(), "宝" + cur + " 失败", "镶嵌失败(保持 " + cur + ") " + GemService.typeName(chosenType) + " 装备#" + eq.getItemId());
    }
    cm.dispose();
}

function refreshEquip(eq) {
    var mods = new java.util.ArrayList();
    mods.add(new ModifyInventory(3, eq));
    mods.add(new ModifyInventory(0, eq));
    cm.getClient().sendPacket(PacketCreator.modifyInventory(true, mods));
    if (eq.getPosition() < 0) {
        cm.getPlayer().equipChanged();
    }
}

function fmtMeso(n) {
    var s = String(n);
    var out = "";
    while (s.length > 3) {
        out = "," + s.slice(-3) + out;
        s = s.slice(0, -3);
    }
    return s + out;
}

function listInlayable() {
    var result = [];
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip")) {
            var pos = item.getPosition();
            if (pos < 0) continue; // 已穿戴的不操作（需放背包）
            if (GemService.isInlayable(item)) {
                result.push({
                    slot: pos,
                    id: item.getItemId(),
                    inlay: item.getGemInlay() & 0xFF
                });
            }
        }
    }
    return result;
}