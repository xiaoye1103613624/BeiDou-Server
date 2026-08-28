/**
 * @description 经验戒指系统
 * 功能：消耗金币购买经验加成戒指（临时道具/永久装备），支持多个等级
 * 入口：9900001.js case 503
 */

// 经验戒指配置：[金币价格, ItemID, 名称, 描述]
var RING_LIST = [
    [5000000,   1112000, "新手经验戒指",   "经验加成 10%，永久装备"],
    [20000000,  1112001, "进阶经验戒指",   "经验加成 20%，永久装备"],
    [50000000,  1112003, "精英经验戒指",   "经验加成 30%，永久装备"],
    [100000000, 1112801, "大师经验戒指",   "经验加成 50%，永久装备"]
];

var status = -1;
var pendingIdx = -1;

function start() {
    status = -1;
    pendingIdx = -1;
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
            doPurchase();
        } else {
            cm.sendOk("已取消购买。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var player = cm.getPlayer();
    var meso = player.getMeso();

    var text = "#e经验戒指商店#n\r\n\r\n";
    text += "当前金币：#r" + meso.toLocaleString() + "#k\r\n";
    text += "#d经验戒指装备后可增加经验获取百分比！#k\r\n\r\n";
    text += "━━━ 可购买戒指 ━━━\r\n\r\n";

    for (var i = 0; i < RING_LIST.length; i++) {
        var ring = RING_LIST[i];
        var price = ring[0];
        var itemId = ring[1];
        var name = ring[2];
        var desc = ring[3];
        var affordable = meso >= price;

        text += "#L" + i + "#";
        text += "#i" + itemId + "# #b" + name + "#k";
        text += "\r\n   #d" + desc + "#k";
        if (affordable) {
            text += "   价格：#r" + (price / 10000) + "W金币#k";
        } else {
            text += "   价格：#d" + (price / 10000) + "W金币（金币不足）#k";
        }
        text += "#l\r\n\r\n";
    }

    text += "#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function handleSelection(selection) {
    if (selection === 9) {
        cm.dispose();
        return;
    }
    if (selection < 0 || selection >= RING_LIST.length) {
        cm.dispose();
        return;
    }

    var ring = RING_LIST[selection];
    var price = ring[0];
    var itemId = ring[1];
    var name = ring[2];

    if (cm.getMeso() < price) {
        cm.sendOk("#r金币不足！\r\n需要：#b" + price.toLocaleString() + "#k 金币\r\n当前：#r" + cm.getMeso().toLocaleString() + "#k 金币");
        cm.dispose();
        return;
    }

    pendingIdx = selection;
    cm.sendYesNo("确认购买 #i" + itemId + "# #b" + name + "#k？\r\n花费：#r" + (price / 10000) + "W金币#k");
}

function doPurchase() {
    if (pendingIdx < 0 || pendingIdx >= RING_LIST.length) {
        cm.sendOk("购买数据异常，请重试。");
        cm.dispose();
        return;
    }

    var ring = RING_LIST[pendingIdx];
    var price = ring[0];
    var itemId = ring[1];
    var name = ring[2];

    if (cm.getMeso() < price) {
        cm.sendOk("#r金币不足！#k");
        cm.dispose();
        return;
    }

    if (!cm.canHold(itemId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包！#k");
        cm.dispose();
        return;
    }

    cm.gainMeso(-price);
    cm.gainItem(itemId, 1);
    cm.sendOk("购买成功！\r\n\r\n#i" + itemId + "# #b" + name + "#k 已放入背包。\r\n#g祝你经验大丰收！#k");
    cm.dispose();
}
