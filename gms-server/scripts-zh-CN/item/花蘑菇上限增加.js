/*
 * ==================
 * 脚本类型: 消耗道具 - 花蘑菇HP/MP上限增加
 * 脚本作者：北斗项目组
 * 对应物品：2430102（请在WZ中配置 spec/script="花蘑菇上限增加"）
 * 功能说明：
 *   1. 消耗10个花蘑菇盖 + 500W金币，选择增加HP或MP上限
 *   2. 随机增加0~100点（权重偏向50以下，期望约33点）
 * ==================
 */

/* ===== 可配置区域 ===== */
var ITEM_ID = 2430102;              // 本道具的物品ID
var COST_ITEM_ID = 4000001;         // 花蘑菇盖
var COST_ITEM_QTY = 10;             // 每次消耗数量
var COST_MESO = 5000000;            // 每次消耗金币 (500W)
var HP_CAP = 30000;                 // HP上限硬限制
var MP_CAP = 30000;                 // MP上限硬限制
/* ======================== */

var status = -1;
var chosenStat = ""; // "hp" or "mp"

function start() {
    status = -1;
    chosenStat = "";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        im.dispose();
        return;
    }

    status++;

    if (status === 0) {
        showMainMenu();
    } else if (status === 1) {
        if (selection === 0) {
            chosenStat = "hp";
            executeBoost();
        } else if (selection === 1) {
            chosenStat = "mp";
            executeBoost();
        } else {
            im.dispose();
        }
    }
}

function showMainMenu() {
    var player = im.getPlayer();
    var haveMushroom = im.itemQuantity(COST_ITEM_ID);
    var haveMeso = player.getMeso();

    var text = "#e#b=== 花蘑菇上限增加 ===#k#n\r\n\r\n";
    text += "#d" + "".padStart(26, "——") + "#k\r\n";
    text += "当前HP上限：#b" + player.getMaxHp() + "#k\r\n";
    text += "当前MP上限：#b" + player.getMaxMp() + "#k\r\n";
    text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
    text += "消耗材料：\r\n";
    text += "  #b#t" + COST_ITEM_ID + "##k × #r" + COST_ITEM_QTY + "#k";
    text += "  (持有: " + haveMushroom + ")\r\n";
    text += "  金币 × #r" + (COST_MESO / 10000).toFixed(0) + "W#k";
    text += "  (持有: " + haveMeso.toLocaleString() + ")\r\n";
    text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
    text += "随机增加 #b0~100#k 点上限（权重偏向50以下）\r\n\r\n";

    var canAfford = haveMushroom >= COST_ITEM_QTY && haveMeso >= COST_MESO;
    var hpCapped = player.getMaxHp() >= HP_CAP;
    var mpCapped = player.getMaxMp() >= MP_CAP;

    if (!canAfford) {
        text += "#r材料不足，无法使用！#k\r\n";
        if (haveMushroom < COST_ITEM_QTY) {
            text += "缺少 #b#t" + COST_ITEM_ID + "##k × #r" + (COST_ITEM_QTY - haveMushroom) + "#k\r\n";
        }
        if (haveMeso < COST_MESO) {
            text += "缺少金币 #r" + (COST_MESO - haveMeso).toLocaleString() + "#k\r\n";
        }
        im.sendOk(text);
        im.dispose();
        return;
    }

    text += "请选择要提升的属性：\r\n\r\n";
    if (!hpCapped) {
        text += "#L0##b增加HP上限#k (当前: " + player.getMaxHp() + ")#l\r\n";
    } else {
        text += "#rHP已达上限 " + HP_CAP + "#k\r\n";
    }
    if (!mpCapped) {
        text += "#L1##b增加MP上限#k (当前: " + player.getMaxMp() + ")#l\r\n";
    } else {
        text += "#rMP已达上限 " + MP_CAP + "#k\r\n";
    }

    if (hpCapped && mpCapped) {
        im.sendOk("你的HP和MP上限都已达到 #r" + HP_CAP + "#k，无法继续提升。");
        im.dispose();
        return;
    }

    im.sendSimple(text);
}

function executeBoost() {
    var player = im.getPlayer();

    // 验证材料
    var haveMushroom = im.itemQuantity(COST_ITEM_ID);
    var haveMeso = player.getMeso();
    if (haveMushroom < COST_ITEM_QTY || haveMeso < COST_MESO) {
        im.sendOk("#r材料不足，操作取消。#k");
        im.dispose();
        return;
    }

    // 验证上限
    if (chosenStat === "hp" && player.getMaxHp() >= HP_CAP) {
        im.sendOk("#rHP上限已达到 " + HP_CAP + "，无法继续提升。#k");
        im.dispose();
        return;
    }
    if (chosenStat === "mp" && player.getMaxMp() >= MP_CAP) {
        im.sendOk("#rMP上限已达到 " + MP_CAP + "，无法继续提升。#k");
        im.dispose();
        return;
    }

    // 扣除材料
    im.gainItem(COST_ITEM_ID, -COST_ITEM_QTY);
    im.gainMeso(-COST_MESO);

    // 随机增加
    var boost = randomBoost();
    var oldVal, newVal;

    if (chosenStat === "hp") {
        oldVal = player.getMaxHp();
        if (oldVal + boost > HP_CAP) {
            boost = HP_CAP - oldVal;
        }
        player.addMaxHP(boost);
        newVal = player.getMaxHp();
    } else {
        oldVal = player.getMaxMp();
        if (oldVal + boost > MP_CAP) {
            boost = MP_CAP - oldVal;
        }
        player.addMaxMP(boost);
        newVal = player.getMaxMp();
    }

    var statName = chosenStat === "hp" ? "HP" : "MP";
    var msg = "#e#b=== 使用完成 ===#k#n\r\n\r\n";
    msg += "已消耗：\r\n";
    msg += "  #b#t" + COST_ITEM_ID + "##k × #r" + COST_ITEM_QTY + "#k\r\n";
    msg += "  金币 × #r" + (COST_MESO / 10000).toFixed(0) + "W#k\r\n\r\n";
    msg += "原" + statName + "上限：#b" + oldVal + "#k\r\n";
    msg += "新" + statName + "上限：#b" + newVal + "#k  (+" + boost + ")";
    if (newVal >= (chosenStat === "hp" ? HP_CAP : MP_CAP)) {
        msg += "\r\n\r\n#r已达到" + statName + "上限！#k";
    }

    im.sendOk(msg);
    im.dispose();
}

function randomBoost() {
    var a = Math.floor(Math.random() * 101);
    var b = Math.floor(Math.random() * 101);
    return Math.min(a, b);
}