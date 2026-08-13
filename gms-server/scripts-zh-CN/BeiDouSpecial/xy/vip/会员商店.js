/**
 * @description VIP会员商店
 * 功能：VIP专属道具商店，消耗点券购买只有VIP才能买到的道具
 * 入口：9900001.js case 602
 */

// VIP状态持久化key（与会员中心保持一致）
var VIP_EXTEND_KEY = "vip_expire";

// VIP商店道具配置：[ItemID, 点券价格, 名称, 每次购买上限]
var SHOP_ITEMS = [
    [2290138, 500,  "2倍经验卡（30分钟）",    5],
    [5062000, 1000, "奇迹魔方（高级）",        3],
    [2049100, 800,  "混沌卷轴",               5],
    [2049300, 1500, "白色卷轴（60%）",         3],
    [4000021, 2000, "大勋章石",               1],
    [1122000, 5000, "精灵戒指（强力）",        1],
    [2022179, 300,  "高级HP药水（100瓶）",     10],
    [2022180, 300,  "高级MP药水（100瓶）",     10]
];

var status = -1;
var pendingIdx = -1;
var pendingQty = 1;

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
        // VIP检查
        if (!checkVip()) {
            cm.sendOk("#rVIP专享功能！\r\n#k请先在#b会员中心#k开通VIP才可使用会员商店。");
            cm.dispose();
            return;
        }
        showShopMenu();
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

function showShopMenu() {
    var nx = cm.getPlayer().getCashShop().getCash(1);
    var expireTs = getVipExpire();
    var now = new Date().getTime();
    var daysLeft = Math.ceil((expireTs - now) / 86400000);

    var text = "#e#rVIP会员商店#k#n\r\n\r\n";
    text += "当前点券：#r" + nx + "#k  │  VIP剩余：#b" + daysLeft + "#k天\r\n";
    text += "#dVIP专属特价道具，限量购买！#k\r\n\r\n";
    text += "━━━ VIP专属商品 ━━━\r\n\r\n";

    for (var i = 0; i < SHOP_ITEMS.length; i++) {
        var item = SHOP_ITEMS[i];
        var itemId = item[0];
        var price = item[1];
        var name = item[2];
        var limit = item[3];
        var affordable = nx >= price;

        text += "#L" + i + "#";
        text += "#i" + itemId + "# #b" + name + "#k";
        if (affordable) {
            text += "  #r" + price + "点券#k  (每次限" + limit + "个)";
        } else {
            text += "  #d" + price + "点券（点券不足）#k";
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
    if (selection < 0 || selection >= SHOP_ITEMS.length) {
        cm.dispose();
        return;
    }

    var item = SHOP_ITEMS[selection];
    var price = item[1];
    var name = item[2];
    var limit = item[3];
    var nx = cm.getPlayer().getCashShop().getCash(1);

    if (nx < price) {
        cm.sendOk("#r点券不足！需要 #b" + price + "#k 点券，当前 #r" + nx + "#k 点券。#k");
        cm.dispose();
        return;
    }

    pendingIdx = selection;
    // 计算最多能买多少
    var maxBuy = Math.min(limit, Math.floor(nx / price));
    pendingQty = maxBuy > 1 ? maxBuy : 1;  // 默认购买最大数量

    cm.sendYesNo("确认购买 #i" + item[0] + "# #b" + name + "#k\r\n× #r" + pendingQty + "#k 个，花费 #r" + (price * pendingQty) + "#k 点券？");
}

function doPurchase() {
    if (pendingIdx < 0 || pendingIdx >= SHOP_ITEMS.length) {
        cm.sendOk("数据异常，请重试。");
        cm.dispose();
        return;
    }

    var item = SHOP_ITEMS[pendingIdx];
    var itemId = item[0];
    var price = item[1];
    var name = item[2];
    var totalCost = price * pendingQty;
    var nx = cm.getPlayer().getCashShop().getCash(1);

    if (nx < totalCost) {
        cm.sendOk("#r点券不足！#k");
        cm.dispose();
        return;
    }
    if (!cm.canHold(itemId, pendingQty)) {
        cm.sendOk("#r背包空间不足！#k");
        cm.dispose();
        return;
    }

    cm.getPlayer().getCashShop().gainCash(1, -totalCost);
    cm.gainItem(itemId, pendingQty);
    cm.sendOk("购买成功！\r\n\r\n#i" + itemId + "# #b" + name + "#k × #r" + pendingQty + "#k 已放入背包。");
    cm.dispose();
}

function checkVip() {
    var expireTs = getVipExpire();
    return expireTs > new Date().getTime();
}

function getVipExpire() {
    var val = cm.getCharacterExtendValue(VIP_EXTEND_KEY);
    if (val != null && val !== "" && val !== "null") {
        try { return Number(val); } catch (e) {}
    }
    return 0;
}
