/**
 * @description 老虎机小游戏
 * 3x3宝石图案转盘，任意一行/列/斜线三个图案相同即中奖，赢取6倍下注金币
 * 入口：cm.openNpc(9900001, "xy/games/老虎机")
 */

var 下注金额 = 10000000; // 每次下注金币
var 中奖倍数 = 6;
var 金币上限 = 2100000000;
var 中奖额外道具 = 4031189;
var 宝石图案 = [4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007];

// 8条连线：3行、3列、2条斜线（按3x3格子索引0~8，从左到右从上到下）
var 连线组合 = [
    [0, 1, 2], [3, 4, 5], [6, 7, 8],
    [0, 3, 6], [1, 4, 7], [2, 5, 8],
    [0, 4, 8], [2, 4, 6]
];

var phase = "init"; // init -> menu -> resultAck

function start() {
    phase = "init";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }

    if (phase === "init") {
        phase = "menu";
        showMenu();
    } else if (phase === "menu") {
        if (selection === 1) {
            playSlot();
        } else {
            cm.dispose();
        }
    } else if (phase === "resultAck") {
        if (type === 1) {
            phase = "menu";
            showMenu();
        } else {
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function showMenu() {
    var text = "#e#b老虎机#k#n\r\n\r\n";
    text += "#d每次下注 #r" + 下注金额.toLocaleString() + " 金币#k#d，三个图案连线即可获得 #r" + 中奖倍数 + "倍#k#d 奖励！#k\r\n";
    text += "当前金币：#b" + cm.getMeso().toLocaleString() + "#k\r\n\r\n";
    text += "#L1#开始游戏！#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function playSlot() {
    if (cm.getMeso() < 下注金额) {
        cm.sendOk("#r金币不足，无法下注！#k");
        cm.dispose();
        return;
    }
    if (cm.getMeso() + 下注金额 * 中奖倍数 > 金币上限) {
        cm.sendOk("#r你的金币已接近上限，暂时无法进行游戏！#k");
        cm.dispose();
        return;
    }

    var grid = [];
    for (var i = 0; i < 9; i++) {
        grid.push(宝石图案[Math.floor(Math.random() * 宝石图案.length)]);
    }

    var win = false;
    for (var c = 0; c < 连线组合.length; c++) {
        var line = 连线组合[c];
        if (grid[line[0]] === grid[line[1]] && grid[line[1]] === grid[line[2]]) {
            win = true;
            break;
        }
    }

    var text = "本次转出：\r\n";
    for (var r = 0; r < 3; r++) {
        var rowText = "";
        for (var col = 0; col < 3; col++) {
            rowText += "#i" + grid[r * 3 + col] + "#";
        }
        text += rowText + "\r\n";
    }
    text += "\r\n";

    if (win) {
        cm.gainMeso(下注金额 * (中奖倍数 - 1));
        cm.gainItem(中奖额外道具, 1);
        text += "#r三连珠！恭喜中奖！#k 获得 #b" + (下注金额 * 中奖倍数).toLocaleString() + " 金币#k 及神秘奖励道具！\r\n";
    } else {
        cm.gainMeso(-下注金额);
        text += "#b很遗憾，未中奖，扣除下注金币。#k\r\n";
    }

    text += "\r\n是否再玩一次？";
    phase = "resultAck";
    cm.sendYesNo(text);
}
