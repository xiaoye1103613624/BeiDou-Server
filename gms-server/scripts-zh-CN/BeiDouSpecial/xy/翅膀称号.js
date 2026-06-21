/**
 * @description 翅膀与称号商店
 * 功能：消耗点券购买翅膀（背部装饰）和称号（勋章），按分类展示
 * 入口：9900001.js case 505
 */

// 翅膀配置：[ItemID, 点券价格, 名称]
var WINGS = [
    [1102041, 3000, "天使之翼"],
    [1102056, 3000, "月光翅膀"],
    [1102084, 5000, "暗夜蝙蝠翼"],
    [1102100, 5000, "凤凰烈焰翅膀"],
    [1102123, 8000, "龙族荣耀之翼"],
    [1102144, 8000, "星辉仙羽翅膀"],
    [1102200, 12000, "神圣之翼（限定）"]
];

// 称号配置：[ItemID, 点券价格, 名称]
var TITLES = [
    [1142000, 2000, "冒险家"],
    [1142001, 2000, "精英猎手"],
    [1142006, 3000, "传说勇者"],
    [1142010, 5000, "世界之王"],
    [1142020, 5000, "枫叶英雄"],
    [1142030, 8000, "至尊神明（限定）"]
];

var status = -1;
var selectedCategory = -1;  // 0=翅膀, 1=称号

function start() {
    status = -1;
    selectedCategory = -1;
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
        showCategoryMenu();
    } else if (status === 1) {
        handleCategorySelection(selection);
    } else if (status === 2) {
        handleItemSelection(selection);
    } else if (status === 3) {
        if (type === 1) {
            doPurchase();
        } else {
            cm.sendOk("已取消购买。");
            cm.dispose();
        }
    }
}

function showCategoryMenu() {
    var player = cm.getPlayer();
    var nx = player.getCashShop().getCash(1);

    var text = "#e翅膀与称号商店#n\r\n\r\n";
    text += "当前点券：#r" + nx + "#k\r\n\r\n";
    text += "#d在这里可以购买各种酷炫的翅膀和称号！#k\r\n\r\n";
    text += '#L0##b 翅膀 #k#l\r\n';
    text += '#L1##b 称号 #k#l\r\n';
    text += "#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function handleCategorySelection(selection) {
    if (selection === 9) {
        cm.dispose();
        return;
    }
    if (selection !== 0 && selection !== 1) {
        cm.dispose();
        return;
    }

    selectedCategory = selection;
    showItemList();
}

function showItemList() {
    var items = selectedCategory === 0 ? WINGS : TITLES;
    var categoryName = selectedCategory === 0 ? "翅膀" : "称号";
    var nx = cm.getPlayer().getCashShop().getCash(1);

    var text = "#e" + categoryName + "列表#n\r\n\r\n";
    text += "当前点券：#r" + nx + "#k\r\n\r\n";

    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var itemId = item[0];
        var price = item[1];
        var name = item[2];
        var affordable = nx >= price;
        var owned = cm.haveItem(itemId);

        text += "#L" + i + "#";
        text += "#i" + itemId + "# #b" + name + "#k";
        if (owned) {
            text += "  #g[已拥有]#k";
        } else if (affordable) {
            text += "  #r" + price + "点券#k";
        } else {
            text += "  #d" + price + "点券（不足）#k";
        }
        text += "#l\r\n";
    }

    text += "\r\n#L99#返回分类#l\r\n";
    cm.sendSimple(text);
}

var pendingItemId = -1;
var pendingPrice = -1;
var pendingName = "";

function handleItemSelection(selection) {
    if (selection === 99) {
        // 返回分类菜单
        status = 0;
        showCategoryMenu();
        return;
    }

    var items = selectedCategory === 0 ? WINGS : TITLES;
    if (selection < 0 || selection >= items.length) {
        cm.dispose();
        return;
    }

    var item = items[selection];
    pendingItemId = item[0];
    pendingPrice = item[1];
    pendingName = item[2];
    var nx = cm.getPlayer().getCashShop().getCash(1);

    if (cm.haveItem(pendingItemId)) {
        cm.sendOk("你已经拥有 #i" + pendingItemId + "# #b" + pendingName + "#k 了！");
        cm.dispose();
        return;
    }

    if (nx < pendingPrice) {
        cm.sendOk("#r点券不足！\r\n需要：#b" + pendingPrice + "#k 点券\r\n当前：#r" + nx + "#k 点券");
        cm.dispose();
        return;
    }

    cm.sendYesNo("确认购买 #i" + pendingItemId + "# #b" + pendingName + "#k？\r\n花费：#r" + pendingPrice + "#k 点券");
}

function doPurchase() {
    var nx = cm.getPlayer().getCashShop().getCash(1);

    if (nx < pendingPrice) {
        cm.sendOk("#r点券不足！#k");
        cm.dispose();
        return;
    }

    if (!cm.canHold(pendingItemId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包！#k");
        cm.dispose();
        return;
    }

    cm.getPlayer().getCashShop().gainCash(1, -pendingPrice);
    cm.gainItem(pendingItemId, 1);
    cm.sendOk("购买成功！\r\n\r\n#i" + pendingItemId + "# #b" + pendingName + "#k 已放入背包。\r\n#g祝你冒险愉快！#k");
    cm.dispose();
}
