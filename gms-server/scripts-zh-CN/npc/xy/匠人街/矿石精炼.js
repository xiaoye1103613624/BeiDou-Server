// 匠人街 · 诺布 · 矿石结晶提炼
var status = -1;
var refineRocks = true;
var refineCrystals = true;
var refineSpecials = true;
var feeMultiplier = 1.0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var text = "#e#b<矿石结晶提炼>#k#n\r\n";
        text += "把母矿和水晶交给我，我来帮你提炼。\r\n\r\n";
        text += "#L0#精炼矿石母矿#l\r\n";
        text += "#L1#精炼宝石母矿#l\r\n";
        if (refineCrystals) {
            text += "#L2#精炼水晶母矿#l\r\n";
        }
        if (refineRocks) {
            text += "#L3#合成月石 / 星石#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        var allDone = false;
        if (selection === 0) {
            allDone = refineItems(0);
        } else if (selection === 1) {
            allDone = refineItems(1);
        } else if (selection === 2 && refineCrystals) {
            allDone = refineItems(2);
        } else if (selection === 3) {
            allDone = refineRockItems();
        }
        if (allDone) {
            cm.sendOk("提炼完成！请查看背包。");
        } else {
            cm.sendOk("没有找到可提炼的材料，或背包空间不足。");
        }
        cm.dispose();
    }
}

function isRefineTarget(refineType, refineItemid) {
    if (refineType === 0) {
        return refineItemid >= 4010000 && refineItemid <= 4010007 && !(refineItemid === 4010007 && !refineSpecials);
    }
    if (refineType === 1) {
        return refineItemid >= 4020000 && refineItemid <= 4020008 && !(refineItemid === 4020008 && !refineSpecials);
    }
    if (refineType === 2) {
        return refineItemid >= 4004000 && refineItemid <= 4004004 && !(refineItemid === 4004004 && !refineSpecials);
    }
    return false;
}

function getRockRefineTarget(refineItemid) {
    if (refineItemid >= 4011000 && refineItemid <= 4011006) {
        return 4011007;
    }
    if (refineItemid >= 4021000 && refineItemid <= 4021008) {
        return 4021009;
    }
    return -1;
}

function refineItems(refineType) {
    var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
    var iter = cm.getPlayer().getInventory(InventoryType.ETC).list().iterator();
    var refineFees = [[300, 300, 300, 500, 500, 500, 800, 270], [500, 500, 500, 500, 500, 500, 500, 1000, 3000], [5000, 5000, 5000, 5000, 1000000]];
    var feeIndex = [4010000, 4010001, 4010002, 4010003, 4010004, 4010005, 4010006, 4010007, 4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006, 4020007, 4020008, 4004000, 4004001, 4004002, 4004003, 4004004];
    var any = false;

    while (iter.hasNext()) {
        var item = iter.next();
        var itemid = item.getItemId();
        if (!isRefineTarget(refineType, itemid)) {
            continue;
        }
        var itemqty = item.getQuantity();
        var refineQty = (itemqty / 10) | 0;
        if (refineQty <= 0) {
            continue;
        }
        itemqty = refineQty * 10;
        var fee = refineFees[refineType][Math.max(0, feeIndex.indexOf(itemid))] * feeMultiplier;
        if (cm.getMeso() < fee) {
            continue;
        }
        var reward = itemid + 1;
        if (!cm.canHold(reward, refineQty)) {
            continue;
        }
        cm.gainItem(itemid, -itemqty);
        cm.gainMeso(-fee);
        cm.gainItem(reward, refineQty);
        any = true;
    }
    return any;
}

function refineRockItems() {
    var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
    var iter = cm.getPlayer().getInventory(InventoryType.ETC).list().iterator();
    var any = false;
    while (iter.hasNext()) {
        var item = iter.next();
        var itemid = item.getItemId();
        var target = getRockRefineTarget(itemid);
        if (target === -1) {
            continue;
        }
        var qty = item.getQuantity();
        if (qty < 10) {
            continue;
        }
        var fee = 7000 * feeMultiplier;
        if (cm.getMeso() < fee) {
            continue;
        }
        if (!cm.canHold(target, 1)) {
            continue;
        }
        cm.gainItem(itemid, -10);
        cm.gainMeso(-fee);
        cm.gainItem(target, 1);
        any = true;
    }
    return any;
}
