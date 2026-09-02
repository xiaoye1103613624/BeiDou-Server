/**
 * @description 天赋学习系统
 * 功能：消耗 AP（能力点）为角色加点到力量/敏捷/智力/幸运
 * 入口：9900001.js case 502
 */

var status = -1;
var pendingStatType = "";  // 待加点的属性名
var pendingAmount = 0;     // 待加点的数量

// 属性类型映射
var STAT_OPTIONS = [
    { label: "力量 (STR)", type: "str" },
    { label: "敏捷 (DEX)", type: "dex" },
    { label: "智力 (INT)", type: "int" },
    { label: "幸运 (LUK)", type: "luk" }
];

function start() {
    status = -1;
    pendingStatType = "";
    pendingAmount = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }

    if (status === 0) {
        showMainMenu();
    } else if (status === 1) {
        handleMainSelection(selection);
    } else if (status === 2) {
        // 选择加点数量
        handleAmountSelection(selection);
    } else if (status === 3) {
        // 确认加点
        if (type === 1) {
            doAddStat();
        } else {
            cm.sendOk("已取消加点。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var player = cm.getPlayer();
    var ap = player.getRemainingAp();

    var text = "#e天赋学习系统#n\r\n\r\n";
    text += "当前剩余 AP：#r" + ap + "#k\r\n\r\n";
    text += "当前基础属性：\r\n";
    text += "  力量(STR)：#b" + player.getStr() + "#k\r\n";
    text += "  敏捷(DEX)：#b" + player.getDex() + "#k\r\n";
    text += "  智力(INT)：#b" + player.getInt() + "#k\r\n";
    text += "  幸运(LUK)：#b" + player.getLuk() + "#k\r\n\r\n";

    if (ap <= 0) {
        text += "#r当前没有可用的 AP，请先升级或通过任务获取 AP！#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "请选择要加点的属性：\r\n";
    for (var i = 0; i < STAT_OPTIONS.length; i++) {
        text += "#L" + i + "#" + STAT_OPTIONS[i].label + "#l\r\n";
    }
    text += "#L9#取消#l\r\n";
    cm.sendSimple(text);
}

function handleMainSelection(selection) {
    if (selection === 9) {
        cm.sendOk("已退出天赋学习。");
        cm.dispose();
        return;
    }

    if (selection < 0 || selection >= STAT_OPTIONS.length) {
        cm.dispose();
        return;
    }

    pendingStatType = STAT_OPTIONS[selection].type;
    var ap = cm.getPlayer().getRemainingAp();

    var text = "请选择为 #b" + STAT_OPTIONS[selection].label + "#k 加点的数量：\r\n";
    text += "（当前 AP：#r" + ap + "#k）\r\n\r\n";

    // 提供几个快速选项
    var amounts = [1, 5, 10, 50];
    for (var i = 0; i < amounts.length; i++) {
        if (amounts[i] <= ap) {
            text += "#L" + amounts[i] + "#加 #r" + amounts[i] + "#k 点#l\r\n";
        }
    }
    text += "#L0#取消#l\r\n";
    cm.sendSimple(text);
}

function handleAmountSelection(selection) {
    if (selection === 0) {
        // 返回主菜单
        status = 0;
        showMainMenu();
        return;
    }

    var ap = cm.getPlayer().getRemainingAp();
    if (selection > ap) {
        cm.sendOk("#r AP 不足！当前 AP：#b" + ap + "#k，需要：#r" + selection + "#k");
        cm.dispose();
        return;
    }

    pendingAmount = selection;
    cm.sendYesNo("确认为 #b" + getStatLabel(pendingStatType) + "#k 加 #r" + pendingAmount + "#k 点 AP？");
}

function doAddStat() {
    var player = cm.getPlayer();
    var ap = player.getRemainingAp();

    if (pendingAmount <= 0 || pendingAmount > ap) {
        cm.sendOk("#r AP 不足或加点数量异常！");
        cm.dispose();
        return;
    }

    // 扣除AP并加点（使用 AbstractCharacterObject.assign* 方法，会自动广播属性更新包）
    try {
        // 先扣除AP
        player.gainAp(-pendingAmount, true);

        var ok = false;
        if (pendingStatType === "str") {
            ok = player.assignStr(pendingAmount);
        } else if (pendingStatType === "dex") {
            ok = player.assignDex(pendingAmount);
        } else if (pendingStatType === "int") {
            ok = player.assignInt(pendingAmount);
        } else if (pendingStatType === "luk") {
            ok = player.assignLuk(pendingAmount);
        }

        if (ok) {
            cm.sendOk("加点成功！\r\n\r\n"
                + "#b" + getStatLabel(pendingStatType) + "#k + #r" + pendingAmount + "#k\r\n"
                + "剩余 AP：#r" + player.getRemainingAp() + "#k");
        } else {
            // 加点失败，退还AP
            player.gainAp(pendingAmount, true);
            cm.sendOk("#r加点失败，属性已满或发生异常，已退还 AP。#k");
        }
    } catch (e) {
        cm.sendOk("#r加点失败：#k" + e.getMessage());
    }
    cm.dispose();
}

function getStatLabel(type) {
    for (var i = 0; i < STAT_OPTIONS.length; i++) {
        if (STAT_OPTIONS[i].type === type) return STAT_OPTIONS[i].label;
    }
    return type;
}
