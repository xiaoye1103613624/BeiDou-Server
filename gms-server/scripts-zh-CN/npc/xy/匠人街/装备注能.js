// 匠人街 · 装备注能（⚡ 9031012 装备强化大师菜单进入）
// 10级固定表：每级固定金币/邮票/枫叶，成功率逐级递减；成功/失败均消耗材料；成功仅提升1级（不可跳级）。
// 属性为「每级新增增量」，累积叠加；⚡数值 = 已达注能等级。

var InfusionService = Java.type("org.gms.infusion.InfusionService");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ModifyInventory = Java.type("org.gms.client.inventory.ModifyInventory");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var OpLogManager = Java.type("org.gms.log.OpLogManager");

var status = -1;
var selectedSlot = -1;
var equipObj = null;

function start() {
    status = -1;
    selectedSlot = -1;
    equipObj = null;
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
        if (equipObj == null || !(equipObj instanceof Java.type("org.gms.client.inventory.Equip"))) {
            cm.sendOk("未找到该装备。");
            cm.dispose();
            return;
        }
        showDetail();
    } else if (status === 2) {
        if (selection === 0) {
            doInfusion();
        } else {
            cm.dispose();
        }
    }
}

function showEquipList() {
    var t = "#e#b<装备注能 ⚡>#k#n\r\n\r\n";
    t += "为装备注入雷电之力（⚡），每级新增增量属性，累积叠加。\r\n";
    t += "与装备类型无关，所有装备均可注能。\r\n\r\n";
    t += "请选择要注能的装备（背包装备栏）：\r\n";
    var equips = listEquipInventory();
    if (equips.length === 0) {
        t += "#r背包装备栏为空。#k\r\n";
    } else {
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.infusion > 0) t += " #e#b⚡" + e.infusion + "#k#n";
            t += "#l\r\n";
        }
    }
    t += "\r\n#L9000##g返回#k#l";
    cm.sendSimple(t);
}

function showDetail() {
    var eq = equipObj;
    var cur = InfusionService.levelOf(eq);

    var t = "#e#b>装备注能 ⚡>#k#n\r\n\r\n";
    t += "装备：#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
    t += "当前注能：#b⚡" + cur + "#k / ⚡" + InfusionService.MAX_LEVEL + "\r\n\r\n";

    if (InfusionService.isMax(eq)) {
        t += "#r已达最高注能等级！#k\r\n";
        cm.sendOk(t);
        cm.dispose();
        return;
    }

    var tier = InfusionService.tier(cur);
    t += "本次提升：#b" + InfusionService.describeDelta(cur + 1) + "#k\r\n\r\n";
    t += "成功率：#r" + tier.ratePct + "%#k\r\n";
    t += "消耗：#b" + formatCost(tier) + "#k\r\n";
    t += "#r注意：无论成功与否，材料与金币都会消耗；成功才提升 1 级，失败保持当前等级。#k\r\n\r\n";
    t += "#L0#确认注能#l\r\n#L1#返回#l";
    cm.sendSimple(t);
}

function doInfusion() {
    var eq = equipObj;
    var cur = InfusionService.levelOf(eq);
    if (InfusionService.isMax(eq)) {
        cm.sendOk("已达最高注能等级。");
        cm.dispose();
        return;
    }
    var tier = InfusionService.tier(cur);

    // 校验并扣除材料（成功/失败均消耗）
    var shortage = checkAndConsume(tier);
    if (shortage !== null) {
        cm.sendOk(shortage);
        cm.dispose();
        return;
    }

    var ok = InfusionService.roll(tier.ratePct);
    var newLevel = cur;
    if (ok) {
        InfusionService.upgrade(eq);
        newLevel = InfusionService.levelOf(eq);
    }
    refreshEquip(eq);

    if (ok) {
        var name = ItemInformationProvider.getInstance().getName(eq.getItemId());
        cm.getPlayer().dropMessage(5, "【注能】⚡ 注能成功！" + (name || "#" + eq.getItemId()) + " 升至 ⚡" + newLevel);
        cm.sendOk("#b⚡ 注能成功！#k\r\n" + (name || ("#" + eq.getItemId())) + " 升至 ⚡" + newLevel);
        OpLogManager.recordInfusion(cm.getPlayer(), "⚡" + newLevel + " " + (name || ("#" + eq.getItemId())), "注能成功 " + (cur + "→" + newLevel) + " 装备#" + eq.getItemId());
    } else {
        cm.sendOk("#r注能失败……#k\r\n材料与金币已消耗，装备注能仍为 ⚡" + cur + "。");
        OpLogManager.recordInfusion(cm.getPlayer(), "⚡" + cur + " 失败", "注能失败(保持 " + cur + ") 装备#" + eq.getItemId());
    }
    cm.dispose();
}

function checkAndConsume(tier) {
    if (cm.getMeso() < tier.meso) {
        return "需要金币 #b" + fmtMeso(tier.meso) + "#k\r\n当前金币不足。";
    }
    if (!cm.haveItem(InfusionService.LEAF, tier.leaf)) {
        return "需要 #r#t" + InfusionService.LEAF + "# ×" + tier.leaf + "#k\r\n枫叶不足。";
    }
    for (var i = 0; i < tier.stampIds.length; i++) {
        if (!cm.haveItem(tier.stampIds[i], tier.stamps)) {
            return "需要 #r#t" + tier.stampIds[i] + "# ×" + tier.stamps + "#k\r\n邮票不足。";
        }
    }
    cm.gainMeso(-tier.meso);
    cm.gainItem(InfusionService.LEAF, -tier.leaf);
    for (var i = 0; i < tier.stampIds.length; i++) {
        cm.gainItem(tier.stampIds[i], -tier.stamps);
    }
    return null;
}

function formatCost(tier) {
    var parts = [];
    parts.push("金币 " + fmtMeso(tier.meso));
    if (tier.leaf > 0) parts.push("#t" + InfusionService.LEAF + "# ×" + tier.leaf);
    for (var i = 0; i < tier.stampIds.length; i++) {
        parts.push("#t" + tier.stampIds[i] + "# ×" + tier.stamps);
    }
    return parts.join("、");
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

function listEquipInventory() {
    var result = [];
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip")) {
            result.push({
                slot: item.getPosition(),
                id: item.getItemId(),
                infusion: item.getInfusion() & 0xFF
            });
        }
    }
    return result;
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