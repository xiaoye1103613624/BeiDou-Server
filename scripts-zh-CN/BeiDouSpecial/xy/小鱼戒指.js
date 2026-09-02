/**
 * @description 小鱼戒指兑换
 * 功能：收集指定数量的鱼（道具）后可兑换对应的钓鱼戒指
 * 入口：9900001.js case 305
 */

// 鱼道具 ID
var FISH_ITEM_ID = 4010002;  // 小鱼干（钓鱼系统的收获道具，根据实际情况调整）

// 戒指配置：[消耗鱼数量, 戒指ItemID, 戒指名称]
var RING_TIERS = [
    [10,  1119005, "铜质钓鱼戒指"],
    [30,  1119006, "银质钓鱼戒指"],
    [80,  1119007, "黄金钓鱼戒指"],
    [200, 1119008, "大师钓鱼戒指"],
    [500, 1119009, "传说钓鱼戒指"]
];

var status = -1;
var pendingTierIdx = -1;

function start() {
    status = -1;
    pendingTierIdx = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        showMainMenu();
    } else if (status === 1) {
        handleSelection(selection);
    } else if (status === 2) {
        if (type === 1) {
            doExchange();
        } else {
            cm.sendOk("已取消兑换。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var fishCount = cm.getPlayer().getInventory(
        org.gms.constants.inventory.InventoryType.ETC
    ).countById(FISH_ITEM_ID);

    var text = "#e小鱼戒指兑换#n\r\n\r\n";
    text += "#d钓到的鱼可兑换强力戒指！#k\r\n\r\n";
    text += "当前持有 #i" + FISH_ITEM_ID + "#：#r" + fishCount + "#k 条\r\n\r\n";
    text += "━━━ 可兑换戒指 ━━━\r\n\r\n";

    for (var i = 0; i < RING_TIERS.length; i++) {
        var tier = RING_TIERS[i];
        var cost = tier[0];
        var itemId = tier[1];
        var name = tier[2];
        var owned = cm.haveItem(itemId);
        var canAfford = fishCount >= cost;

        text += "#L" + i + "#";
        text += "#i" + itemId + "# #b" + name + "#k";
        if (owned) {
            text += "  #g[已拥有]#k";
        } else if (canAfford) {
            text += "  需要 #r" + cost + "#k 条鱼 → #r可兑换#k";
        } else {
            text += "  需要 #r" + cost + "#k 条鱼 #d（还差 " + (cost - fishCount) + " 条）#k";
        }
        text += "#l\r\n";
    }

    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function handleSelection(selection) {
    if (selection === 9) {
        cm.dispose();
        return;
    }
    if (selection < 0 || selection >= RING_TIERS.length) {
        cm.dispose();
        return;
    }

    var tier = RING_TIERS[selection];
    var cost = tier[0];
    var itemId = tier[1];
    var name = tier[2];
    var fishCount = cm.getPlayer().getInventory(
        org.gms.constants.inventory.InventoryType.ETC
    ).countById(FISH_ITEM_ID);

    if (cm.haveItem(itemId)) {
        cm.sendOk("你已经拥有 #b" + name + "#k 了！");
        cm.dispose();
        return;
    }
    if (fishCount < cost) {
        cm.sendOk("#r鱼不足！需要 #b" + cost + "#k 条，当前 #r" + fishCount + "#k 条。#k");
        cm.dispose();
        return;
    }

    pendingTierIdx = selection;
    cm.sendYesNo("确认消耗 #r" + cost + "#k 条鱼兑换\r\n#i" + itemId + "# #b" + name + "#k？");
}

function doExchange() {
    if (pendingTierIdx < 0 || pendingTierIdx >= RING_TIERS.length) {
        cm.sendOk("兑换数据异常，请重试。");
        cm.dispose();
        return;
    }

    var tier = RING_TIERS[pendingTierIdx];
    var cost = tier[0];
    var itemId = tier[1];
    var name = tier[2];
    var fishCount = cm.getPlayer().getInventory(
        org.gms.constants.inventory.InventoryType.ETC
    ).countById(FISH_ITEM_ID);

    if (fishCount < cost) {
        cm.sendOk("#r鱼不足！#k");
        cm.dispose();
        return;
    }
    if (!cm.canHold(itemId, 1)) {
        cm.sendOk("#r背包空间不足！#k");
        cm.dispose();
        return;
    }

    cm.gainItem(FISH_ITEM_ID, -cost);
    cm.gainItem(itemId, 1);
    cm.sendOk("兑换成功！\r\n\r\n#i" + itemId + "# #b" + name + "#k 已放入背包。\r\n#g祝你钓鱼愉快！#k");
    cm.dispose();
}
