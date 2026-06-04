/*
 * ==================
 * 脚本类型: 消耗道具 - HP上限增加
 * 脚本作者：北斗项目组
 * 对应物品：2430100（请在WZ中配置 spec/script="HP上限增加"）
 * 功能说明：
 *   1. 每次使用随机增加0~100点HP上限（权重偏向50以下）
 *   2. 支持批量操作，默认使用全部持有数量，可手动修改
 *   3. 单次最多使用999个
 * ==================
 */

/* ===== 可配置区域 ===== */
var ITEM_ID = 2430100;       // 本道具的物品ID
var MAX_BATCH = 999;         // 单次批量使用上限
var HP_CAP = 30000;          // HP上限硬限制
/* ======================== */

var status = -1;
var useCount = 0;

function start() {
    status = -1;
    useCount = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    levelmain();
}

function level() {
    im.dispose();
}

function levelnull() {
    level();
}

function leveldispose() {
    level();
}

function levelmain() {
    var player = im.getPlayer();
    var currentMaxHp = player.getMaxHp();
    var itemQty = im.itemQuantity(ITEM_ID);

    if (itemQty <= 0) {
        im.sendOkLevel("dispose", "你没有 #b#t" + ITEM_ID + "##k，无法使用。");
        return;
    }

    if (currentMaxHp >= HP_CAP) {
        im.sendOkLevel("dispose", "你的HP上限已达到 #r" + HP_CAP + "#k 上限，无法继续提升。");
        return;
    }

    var maxCanUse = Math.min(itemQty, MAX_BATCH);
    var msg = "#e#b=== HP上限增加 ===#k#n\r\n\r\n";
    msg += "当前HP上限：#b" + currentMaxHp + "#k\r\n";
    msg += "持有道具：#b#t" + ITEM_ID + "##k × #r" + itemQty + "#k 个\r\n";
    msg += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
    msg += "每次使用随机增加 #b0~100#k 点HP上限\r\n";
    msg += "（权重偏向50以下，期望约33点/个）\r\n\r\n";
    msg += "请输入要使用的数量：";

    im.getInputNumberLevel("confirm", msg, maxCanUse, 1, maxCanUse);
}

function levelconfirm(count) {
    if (count <= 0 || count > Math.min(im.itemQuantity(ITEM_ID), MAX_BATCH)) {
        im.sendOkLevel("main", "输入数量无效，请重新输入。");
        return;
    }

    useCount = count;
    var msg = "#e#b=== 确认使用 ===#k#n\r\n\r\n";
    msg += "将使用 #b#t" + ITEM_ID + "##k × #r" + useCount + "#k 个\r\n";
    msg += "预计增加HP上限：#b0~" + (useCount * 100) + "#k 点\r\n";
    msg += "（实际数值随机，权重偏向低值）\r\n\r\n";
    msg += "确认使用？";

    im.sendYesNoLevel("main", "execute", msg);
}

function levelexecute() {
    var player = im.getPlayer();
    var itemQty = im.itemQuantity(ITEM_ID);

    if (itemQty < useCount) {
        im.sendOkLevel("dispose", "道具数量不足，操作取消。\r\n当前持有：" + itemQty + " 个，需要：" + useCount + " 个");
        return;
    }

    var oldMaxHp = player.getMaxHp();
    var totalBoost = 0;

    for (var i = 0; i < useCount; i++) {
        var boost = randomBoost();
        var newHp = player.getMaxHp() + boost;
        if (newHp > HP_CAP) {
            boost = HP_CAP - player.getMaxHp();
            if (boost <= 0) break;
        }
        player.addMaxHP(boost);
        totalBoost += boost;
        if (player.getMaxHp() >= HP_CAP) break;
    }

    im.gainItem(ITEM_ID, -useCount);

    var msg = "#e#b=== 使用完成 ===#k#n\r\n\r\n";
    msg += "使用道具：#b#t" + ITEM_ID + "##k × #r" + useCount + "#k\r\n";
    msg += "原HP上限：#b" + oldMaxHp + "#k\r\n";
    msg += "新HP上限：#b" + player.getMaxHp() + "#k  (+" + totalBoost + ")\r\n";
    if (useCount > 1) {
        msg += "平均每个：约 #b" + Math.round(totalBoost / useCount) + "#k 点\r\n";
    }
    if (player.getMaxHp() >= HP_CAP) {
        msg += "\r\n#r已达到HP上限 " + HP_CAP + "#k";
    }

    im.sendOkLevel("dispose", msg);
}

function randomBoost() {
    var a = Math.floor(Math.random() * 101);
    var b = Math.floor(Math.random() * 101);
    return Math.min(a, b);
}