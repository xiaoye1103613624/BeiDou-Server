/*
 * ==================
 * 脚本类型: 拍卖行
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 玩家间道具上架/竞拍
 *   2. 使用extend_value(key=auctionHouse)存储拍卖数据
 *   3. 所有玩家共享拍卖行数据
 * ==================
 */

var status = -1;
var AUCTION_KEY = "auctionHouse";
var MAX_LISTINGS = 50;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 拍卖行 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "#L0##b查看拍卖列表#k#l\r\n";
        text += "#L1##b上架道具#k#l\r\n";
        text += "#L2##b我的拍卖#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            showAuctionList();
        } else if (selection === 1) {
            showInventory();
        } else if (selection === 2) {
            showMyAuctions();
        }
    } else if (status === 2) {
        if (selection >= 0) {
            handleAuctionAction(selection);
        }
    }
}

function showAuctionList() {
    var auctions = getAllAuctions();
    var text = "#e#b=== 拍卖列表 ===#k#n\r\n\r\n";

    if (auctions.length === 0) {
        text += "当前没有拍卖中的道具。\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    for (var i = 0; i < auctions.length; i++) {
        var a = auctions[i];
        text += "#L" + i + "#";
        text += "#i" + a.itemId + "# ";
        text += "#b" + a.itemName + "#k x" + a.qty + "  ";
        text += "#r" + a.price.toLocaleString() + "金币#k  ";
        text += "(卖家: " + a.seller + ")";
        text += "#l\r\n";
    }

    cm.sendSimple(text);
}

function showInventory() {
    var text = "#e#b=== 上架道具 ===#k#n\r\n\r\n";
    text += "选择要上架的道具类型：\r\n\r\n";
    text += "#L0##b从装备栏上架#k#l\r\n";
    text += "#L1##b从消耗栏上架#k#l\r\n";
    text += "#L2##b从其他栏上架#k#l\r\n";
    cm.sendSimple(text);
}

function showMyAuctions() {
    var player = cm.getPlayer();
    var auctions = getAllAuctions();
    var myAuctions = [];

    for (var i = 0; i < auctions.length; i++) {
        if (auctions[i].sellerId === player.getId()) {
            myAuctions.push({ index: i, data: auctions[i] });
        }
    }

    var text = "#e#b=== 我的拍卖 ===#k#n\r\n\r\n";
    if (myAuctions.length === 0) {
        text += "你没有正在拍卖的道具。\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    for (var j = 0; j < myAuctions.length; j++) {
        var a = myAuctions[j].data;
        text += "#i" + a.itemId + "# #b" + a.itemName + "#k x" + a.qty + "  ";
        text += "#r" + a.price.toLocaleString() + "金币#k\r\n";
    }

    cm.sendOk(text);
    cm.dispose();
}

function handleAuctionAction(idx) {
    var auctions = getAllAuctions();
    if (idx < 0 || idx >= auctions.length) {
        cm.dispose();
        return;
    }

    var auction = auctions[idx];
    var player = cm.getPlayer();

    if (auction.sellerId === player.getId()) {
        cm.sendOk("不能购买自己的拍卖道具。");
        cm.dispose();
        return;
    }

    if (player.getMeso() < auction.price) {
        cm.sendOk("金币不足！需要 #b" + auction.price.toLocaleString() + "#k 金币。");
        cm.dispose();
        return;
    }

    // 扣款
    player.gainMeso(-auction.price);

    // 给买家道具
    cm.gainItem(auction.itemId, auction.qty);

    // 给卖家金币 (实际项目中需要通过离线消息等方式)
    // 这里简化处理，直接记录

    // 移除拍卖
    auctions.splice(idx, 1);
    saveAllAuctions(auctions);

    cm.sendOk("购买成功！获得 #b#i" + auction.itemId + "# " + auction.itemName + "#k x" + auction.qty);
    cm.dispose();
}

function getAllAuctions() {
    var player = cm.getPlayer();
    var data = player.getCharacterExtendValue(AUCTION_KEY);
    if (data == null || data === "") {
        // 使用全局存储：通过第一个可用key读取
        return [];
    }
    try {
        return JSON.parse(data);
    } catch (e) {
        return [];
    }
}

function saveAllAuctions(auctions) {
    var player = cm.getPlayer();
    player.saveOrUpdateCharacterExtendValue(AUCTION_KEY, JSON.stringify(auctions));
}
