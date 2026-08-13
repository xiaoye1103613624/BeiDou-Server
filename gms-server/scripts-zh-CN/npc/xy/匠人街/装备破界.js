// 匠人街 · 装备破界（9031003 装备铸造中心菜单进入）
// 破界等级 N(0~50) = 使用强化卷次数，每次 +1，封顶 50。
// 每次破界：随机重掷 13 属性池，每条独立激活（+固定值）或 +0，本次结果【覆盖】上一次（不累计）。
// 每次消耗：对应强化卷 ×1（普通装 2439102 / 点装 2439101）+ 500 点券。
// 成功/失败消息随机，只影响提示；N 一律 +1。

var BreakthroughService = Java.type("org.gms.breakthrough.BreakthroughService");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ModifyInventory = Java.type("org.gms.client.inventory.ModifyInventory");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var CashShop = Java.type("org.gms.server.CashShop");
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
            doBreakthrough();
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
    var t = "#e#b<装备破界>#k#n\r\n\r\n";
    t += "为装备注入破界之力（破界+N），随机激活 13 属性池中的属性。\r\n";
    t += "本次结果覆盖上次，不叠加；每个装备独立。\r\n\r\n";
    t += "请选择要破界的装备（背包装备栏）：\r\n";
    var equips = listEquipInventory();
    if (equips.length === 0) {
        t += "#r背包装备栏为空。#k\r\n";
    } else {
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.lv > 0) t += " #e#b破界+" + e.lv + "#k#n";
            t += "#l\r\n";
        }
    }
    t += "\r\n#L9000##g返回#k#l";
    cm.sendSimple(t);
}

function showDetail() {
    var eq = equipObj;
    var cur = BreakthroughService.levelOf(eq);
    var scrollId = BreakthroughService.scrollFor(eq.getItemId());

    var t = "#e#b装备破界 > 装备详情#k#n\r\n\r\n";
    t += "装备：#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
    t += "当前破界：#b破界+" + cur + "#k / 破界+" + BreakthroughService.MAX_LEVEL + "\r\n\r\n";

    if (BreakthroughService.isMax(eq)) {
        t += "#r已达最高破界等级！#k\r\n";
        cm.sendOk(t);
        cm.dispose();
        return;
    }

    t += "属性池：#r" + BreakthroughService.describePool() + "#k\r\n\r\n";
    var actives = BreakthroughService.describeActives(eq);
    t += "当前激活：" + (actives ? "#b" + actives + "#k" : "#r（未激活）#k") + "\r\n";
    t += "\r\n消耗：#b#t" + scrollId + "# ×1#k + 点券 " + BreakthroughService.COST_NX + "\r\n";
    t += "#r每次消耗上述材料；破界等级 +1，属性会重新随机（覆盖上次）。#k\r\n\r\n";
    t += "#L0#确认破界#l\r\n#L1#返回#l";
    cm.sendSimple(t);
}

function doBreakthrough() {
    var eq = equipObj;
    var cur = BreakthroughService.levelOf(eq);
    if (BreakthroughService.isMax(eq)) {
        cm.sendOk("已达最高破界等级。");
        cm.dispose();
        return;
    }
    var scrollId = BreakthroughService.scrollFor(eq.getItemId());

    if (!cm.haveItem(scrollId, 1)) {
        cm.sendOk("需要 #r#t" + scrollId + "# ×1#k\r\n强化卷不足。");
        cm.dispose();
        return;
    }
    var cash = cm.getPlayer().getCashShop().getCash(CashShop.NX_CREDIT);
    if (cash < BreakthroughService.COST_NX) {
        cm.sendOk("需要 #r500 点券#k，当前 #r" + cash + "#k 点券。");
        cm.dispose();
        return;
    }

    cm.gainItem(scrollId, -1);
    cm.getPlayer().getCashShop().gainCash(CashShop.NX_CREDIT, -BreakthroughService.COST_NX);

    BreakthroughService.reroll(eq);
    BreakthroughService.upgrade(eq);
    var newLevel = BreakthroughService.levelOf(eq);
    refreshEquip(eq);

    var name = equipName(eq);
    var actives = BreakthroughService.describeActives(eq) || "无";
    var ok = Math.random() < 0.5;
    if (ok) {
        cm.getPlayer().dropMessage(5, "【破界】破界成功！" + name + " 升至 破界+" + newLevel + "（" + actives + "）");
        cm.sendOk("#b破界成功！#k\r\n" + name + " 已升至 #b破界+" + newLevel + "#k。\r\n当前激活：" + actives);
        OpLogManager.recordBreakthrough(cm.getPlayer(), "破界+" + newLevel + " " + name, "破界成功(" + cur + "→" + newLevel + ") 激活[" + actives + "] 装备#" + eq.getItemId() + " 卷轴#" + scrollId);
    } else {
        cm.getPlayer().dropMessage(5, "【破界】破界完成！" + name + " 破界+" + newLevel + "（" + actives + "）");
        cm.sendOk("#e破界完成。#k\r\n" + name + " 已升至 #b破界+" + newLevel + "#k。\r\n当前激活：" + actives);
        OpLogManager.recordBreakthrough(cm.getPlayer(), "破界+" + newLevel + " " + name, "破界完成(" + cur + "→" + newLevel + ") 激活[" + actives + "] 装备#" + eq.getItemId() + " 卷轴#" + scrollId);
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

function listEquipInventory() {
    var result = [];
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip")) {
            var pos = item.getPosition();
            if (pos < 0) continue; // 已穿戴的不操作（需放背包）
            result.push({
                slot: pos,
                id: item.getItemId(),
                lv: BreakthroughService.levelOf(item)
            });
        }
    }
    return result;
}