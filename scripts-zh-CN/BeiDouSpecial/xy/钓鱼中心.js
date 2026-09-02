/**
 * @description 钓鱼中心
 * 功能：消耗鱼饵（道具）进行一次钓鱼，随机获得道具或金币奖励
 * 入口：9900001.js case 304
 */

// 鱼饵 ItemID（消耗品）
var BAIT_ITEM_ID = 4011006;  // 鱼饵（矿物/消耗道具类，根据实际情况调整）
var BAIT_COST = 1;            // 每次消耗1个鱼饵

// 钓鱼奖励池：[itemId, 最小数量, 最大数量, 权重, 名称]
// itemId=0 表示金币奖励
var REWARD_POOL = [
    [0,       50000,   200000,  30, "金币"],
    [2000001, 3,       10,      25, "红药水（大）"],
    [2000002, 3,       10,      20, "蓝药水（大）"],
    [4011001, 1,       3,       10, "蓝宝石"],
    [4011002, 1,       2,       8,  "黑水晶"],
    [4021008, 1,       2,       5,  "暗蓝宝石"],
    [2290096, 1,       1,       2,  "100%经验券（30分钟）"]
];

// 计算总权重
var TOTAL_WEIGHT = 0;
for (var i = 0; i < REWARD_POOL.length; i++) {
    TOTAL_WEIGHT += REWARD_POOL[i][3];
}

var status = -1;

function start() {
    status = -1;
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
            doFish();
        } else {
            cm.sendOk("好吧，下次想钓鱼再来找我！");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var baitCount = cm.getPlayer().getInventory(
        org.gms.constants.inventory.InventoryType.ETC
    ).countById(BAIT_ITEM_ID);

    var text = "#e钓鱼中心#n\r\n\r\n";
    text += "#d欢迎来到钓鱼中心！消耗鱼饵可随机钓取珍贵道具！#k\r\n\r\n";
    text += "持有鱼饵 #i" + BAIT_ITEM_ID + "#：#r" + baitCount + "#k 个\r\n\r\n";
    text += "━━━ 奖励池一览 ━━━\r\n";
    for (var i = 0; i < REWARD_POOL.length; i++) {
        var r = REWARD_POOL[i];
        var chance = Math.round(r[3] / TOTAL_WEIGHT * 100);
        if (r[0] === 0) {
            text += "  金币 " + r[1].toLocaleString() + "~" + r[2].toLocaleString() + " (" + chance + "%)\r\n";
        } else {
            text += "  #i" + r[0] + "# #t" + r[0] + "# ×" + r[1] + "~" + r[2] + " (" + chance + "%)\r\n";
        }
    }
    text += "\r\n";

    if (baitCount < BAIT_COST) {
        text += "#r鱼饵不足！请先获取鱼饵 #i" + BAIT_ITEM_ID + "#！#k\r\n";
        text += "#L0#关闭#l\r\n";
    } else {
        text += "#L1##r开始钓鱼#k（消耗 " + BAIT_COST + " 个鱼饵）#l\r\n";
        text += "#L2##b连续钓鱼 5 次#k（消耗 " + (BAIT_COST * 5) + " 个鱼饵）#l\r\n";
        text += "#L0#关闭#l\r\n";
    }
    cm.sendSimple(text);
}

function handleSelection(selection) {
    if (selection === 0) {
        cm.dispose();
        return;
    }

    var baitCount = cm.getPlayer().getInventory(
        org.gms.constants.inventory.InventoryType.ETC
    ).countById(BAIT_ITEM_ID);

    var times = (selection === 2) ? 5 : 1;
    var totalCost = BAIT_COST * times;

    if (baitCount < totalCost) {
        cm.sendOk("#r鱼饵不足！需要 #b" + totalCost + "#k 个，当前持有 #r" + baitCount + "#k 个。#k");
        cm.dispose();
        return;
    }

    fishTimes = times;
    cm.sendYesNo("确认消耗 #r" + totalCost + "#k 个鱼饵钓鱼 #r" + times + "#k 次？");
}

var fishTimes = 1;

function doFish() {
    var baitCount = cm.getPlayer().getInventory(
        org.gms.constants.inventory.InventoryType.ETC
    ).countById(BAIT_ITEM_ID);

    var totalCost = BAIT_COST * fishTimes;
    if (baitCount < totalCost) {
        cm.sendOk("#r鱼饵不足！#k");
        cm.dispose();
        return;
    }

    cm.gainItem(BAIT_ITEM_ID, -totalCost);

    var text = "#e钓鱼结果#n\r\n\r\n";
    for (var t = 0; t < fishTimes; t++) {
        var reward = pickReward();
        if (reward[0] === 0) {
            var meso = reward[1] + Math.floor(Math.random() * (reward[2] - reward[1] + 1));
            cm.gainMeso(meso);
            text += "第" + (t + 1) + "次：#r金币 +" + meso.toLocaleString() + "#k\r\n";
        } else {
            var qty = reward[1] + Math.floor(Math.random() * (reward[2] - reward[1] + 1));
            if (cm.canHold(reward[0], qty)) {
                cm.gainItem(reward[0], qty);
                text += "第" + (t + 1) + "次：#i" + reward[0] + "# #t" + reward[0] + "# × #r" + qty + "#k\r\n";
            } else {
                text += "第" + (t + 1) + "次：#r背包满，#t" + reward[0] + "# ×" + qty + " 无法放入#k\r\n";
            }
        }
    }

    text += "\r\n剩余鱼饵：#r" + (baitCount - totalCost) + "#k 个";
    cm.sendOk(text);
    cm.dispose();
}

/** 按权重随机选取一个奖励 */
function pickReward() {
    var rand = Math.floor(Math.random() * TOTAL_WEIGHT);
    var acc = 0;
    for (var i = 0; i < REWARD_POOL.length; i++) {
        acc += REWARD_POOL[i][3];
        if (rand < acc) return REWARD_POOL[i];
    }
    return REWARD_POOL[REWARD_POOL.length - 1];
}
