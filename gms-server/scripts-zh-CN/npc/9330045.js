// 自由市场 · 渔场管理人余夫 9330045
// 1) 传送精灵钓鱼场 749050502  2) 点券购买钓竿/钓鱼椅

var CASH_NX = 1;
var PRICE = 5000;
var FISH_MAP = 749050502;
var ROD_ID = 5340000;      // 钓竿（渔具）
var CHAIR_ID = 3011000;    // 钓鱼用椅子

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || (mode === 0 && status <= 0)) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var nx = cm.getPlayer().getCashShop().getCash(CASH_NX);
        var text = "#e渔场管理人 · 余夫#n\r\n\r\n";
        text += "钓鱼讲究耐心，也讲究时机！\r\n";
        text += "目标地图：#b#m" + FISH_MAP + "##k（精灵钓鱼场）\r\n";
        text += "点券余额：#r" + nx + "#k\r\n\r\n";
        text += "#L1#传送到钓鱼场#l\r\n";
        text += "#L2#购买渔具 #v" + ROD_ID + "# 钓竿（#r" + PRICE + "#k 点券）#l\r\n";
        text += "#L3#购买椅子 #v" + CHAIR_ID + "# 钓鱼椅（#r" + PRICE + "#k 点券）#l\r\n";
        text += "#L0#离开#l";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            return;
        }
        if (selection === 1) {
            cm.warp(FISH_MAP, 0);
            cm.dispose();
            return;
        }
        var itemId = (selection === 2) ? ROD_ID : CHAIR_ID;
        var nx = cm.getPlayer().getCashShop().getCash(CASH_NX);
        if (nx < PRICE) {
            cm.sendOk("点券不足。需要 #r" + PRICE + "#k，当前 #b" + nx + "#k。");
            cm.dispose();
            return;
        }
        if (!cm.canHold(itemId, 1)) {
            cm.sendOk("背包空间不足。");
            cm.dispose();
            return;
        }
        cm.getPlayer().getCashShop().gainCash(CASH_NX, -PRICE);
        cm.gainItem(itemId, 1);
        cm.sendOk("购买成功！获得 #v" + itemId + "# #z" + itemId + "#\r\n已扣除 #r" + PRICE + "#k 点券。\r\n\r\n#d提示：到钓鱼场坐上钓鱼椅，丢金币可尝试钓鱼。#k");
        cm.dispose();
    } else {
        cm.dispose();
    }
}
