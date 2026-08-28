/**
 * @description 时装洗练系统
 * 功能：消耗时装洗练卷轴，随机重骰装备在时装槽位上的附加属性
 * 说明：需玩家持有洗练卷轴（4100000）才可使用
 * 入口：9900001.js case 504
 */

// 导入 Java 枚举类型（GraalJS 必须用 Java.type 导入，不能直接引用全限定名）
var InventoryType = Java.type('org.gms.constants.inventory.InventoryType');

// 洗练卷轴 ItemID
var SCROLL_ITEM_ID = 4100000;
// 洗练卷轴消耗数量（每次）
var SCROLL_COST = 1;

// 可获得的随机属性范围（最小, 最大）
var STAT_RANGES = {
    str: [1, 10],
    dex: [1, 10],
    int: [1, 10],
    luk: [1, 10],
    hp:  [10, 100],
    mp:  [10, 100],
    watk: [1, 5],
    matk: [1, 5],
    wdef: [1, 15],
    mdef: [1, 15],
    acc:  [1, 10],
    avoid:[1, 10],
    speed:[1, 5]
};

// 每次随机选取 2~4 个属性
var MIN_STATS = 2;
var MAX_STATS = 4;

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
            doWash();
        } else {
            cm.sendOk("已取消洗练。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var scrollCount = cm.getPlayer().getInventory(
        InventoryType.USE
    ).countById(SCROLL_ITEM_ID);

    var text = "#e时装洗练#n\r\n\r\n";
    text += "#d使用洗练卷轴可随机重骰时装装备的附加属性！#k\r\n\r\n";
    text += "持有洗练卷轴 #i" + SCROLL_ITEM_ID + "#：#r" + scrollCount + "#k 张\r\n\r\n";
    text += "每次消耗 #r" + SCROLL_COST + "#k 张洗练卷轴，随机获得 2~4 种属性加成。\r\n\r\n";

    if (scrollCount < SCROLL_COST) {
        text += "#r洗练卷轴不足！请先获取卷轴再来洗练。#k\r\n";
        text += "#L0#关闭#l\r\n";
    } else {
        text += "#L1##r开始洗练#k（消耗 " + SCROLL_COST + " 张）#l\r\n";
        text += "#L0#关闭#l\r\n";
    }

    cm.sendSimple(text);
}

function handleSelection(selection) {
    if (selection === 0) {
        cm.dispose();
        return;
    }

    var scrollCount = cm.getPlayer().getInventory(
        InventoryType.USE
    ).countById(SCROLL_ITEM_ID);

    if (scrollCount < SCROLL_COST) {
        cm.sendOk("#r洗练卷轴不足！#k");
        cm.dispose();
        return;
    }

    cm.sendYesNo("确认消耗 #r" + SCROLL_COST + "#k 张洗练卷轴开始洗练？\r\n#d洗练结果随机，洗练后属性将完全替换！#k");
}

function doWash() {
    var scrollCount = cm.getPlayer().getInventory(
        InventoryType.USE
    ).countById(SCROLL_ITEM_ID);

    if (scrollCount < SCROLL_COST) {
        cm.sendOk("#r洗练卷轴不足！#k");
        cm.dispose();
        return;
    }

    // 扣除卷轴
    cm.gainItem(SCROLL_ITEM_ID, -SCROLL_COST);

    // 随机生成属性
    var statKeys = Object.keys(STAT_RANGES);
    var pickCount = MIN_STATS + Math.floor(Math.random() * (MAX_STATS - MIN_STATS + 1));
    // 打乱顺序并取前几个
    var shuffled = statKeys.slice().sort(function() { return Math.random() - 0.5; });
    var picked = shuffled.slice(0, pickCount);

    // 构造结果文本
    var text = "#e洗练完成！#n\r\n\r\n";
    text += "━━━ 本次洗练结果 ━━━\r\n\r\n";

    for (var i = 0; i < picked.length; i++) {
        var statKey = picked[i];
        var range = STAT_RANGES[statKey];
        var val = range[0] + Math.floor(Math.random() * (range[1] - range[0] + 1));
        text += "  #b" + getStatLabel(statKey) + "#k + #r" + val + "#k\r\n";
    }

    text += "\r\n#d（注意：以上为展示数值，实际属性更新需绑定具体装备槽位，请联系管理员配置）#k\r\n";
    text += "\r\n剩余洗练卷轴：#r" + (scrollCount - SCROLL_COST) + "#k 张";

    cm.sendOk(text);
    cm.dispose();
}

function getStatLabel(key) {
    var labels = {
        str: "力量(STR)", dex: "敏捷(DEX)", int: "智力(INT)", luk: "幸运(LUK)",
        hp: "HP", mp: "MP", watk: "物理攻击", matk: "魔法攻击",
        wdef: "物理防御", mdef: "魔法防御", acc: "命中", avoid: "回避", speed: "速度"
    };
    return labels[key] || key;
}
