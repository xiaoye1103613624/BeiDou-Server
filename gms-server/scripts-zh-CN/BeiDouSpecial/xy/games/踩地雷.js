/**
 * @description 踩地雷(扫雷)小游戏
 * 改编自社区流传脚本，适配BeiDou服务端API(worldMessage->世界广播包, modifyCSPoints->CashShop.gainCash)
 * 入口：cm.openNpc(9900001, "xy/games/踩地雷")
 */

const PacketCreator = Java.type('org.gms.util.PacketCreator');
const CashShop = Java.type('org.gms.server.CashShop');

var 难度列表 = [["#d简单#k", 5, 5, 4], ["#b中等#k", 6, 6, 8], ["#r困难#k", 7, 7, 15]];
var 通关点券奖励 = 200;
var 通关道具奖励 = 4031189;

var rows = 0, cols = 0, mineCount = 0;
var board = [];   // -1=地雷 0~8=周边地雷数
var revealed = [];
var difficultyText = "";
var startTime = 0;
var phase = "init"; // init -> chooseDifficulty -> playing

function start() {
    phase = "init";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }

    if (phase === "init") {
        phase = "chooseDifficulty";
        showDifficultyMenu();
    } else if (phase === "chooseDifficulty") {
        if (selection === 99) { cm.dispose(); return; }
        if (selection < 0 || selection >= 难度列表.length) { cm.dispose(); return; }
        initGame(selection);
        phase = "playing";
        showBoard();
    } else if (phase === "playing") {
        if (selection === 99) { cm.dispose(); return; }
        handleReveal(selection);
    } else {
        cm.dispose();
    }
}

function showDifficultyMenu() {
    var text = "#e#b踩地雷#k#n\r\n\r\n#d请选择游戏难度：#k\r\n\r\n";
    for (var i = 0; i < 难度列表.length; i++) {
        text += "#L" + i + "#" + 难度列表[i][0] + " (" + 难度列表[i][1] + "x" + 难度列表[i][2] + "，地雷x" + 难度列表[i][3] + ")#l\r\n";
    }
    text += "\r\n#L99#关闭#l\r\n";
    cm.sendSimple(text);
}

function initGame(level) {
    var d = 难度列表[level];
    difficultyText = d[0];
    rows = d[1]; cols = d[2]; mineCount = d[3];
    startTime = Date.now();

    var total = rows * cols;
    board = new Array(total).fill(0);
    revealed = new Array(total).fill(false);

    var placed = 0;
    while (placed < mineCount) {
        var idx = Math.floor(Math.random() * total);
        if (board[idx] !== -1) { board[idx] = -1; placed++; }
    }
    for (var i = 0; i < rows; i++) {
        for (var j = 0; j < cols; j++) {
            var idx2 = i * cols + j;
            if (board[idx2] === -1) continue;
            board[idx2] = countNeighborMines(i, j);
        }
    }
}

function countNeighborMines(r, c) {
    var count = 0;
    for (var dr = -1; dr <= 1; dr++) {
        for (var dc = -1; dc <= 1; dc++) {
            if (dr === 0 && dc === 0) continue;
            var nr = r + dr, nc = c + dc;
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                if (board[nr * cols + nc] === -1) count++;
            }
        }
    }
    return count;
}

function handleReveal(idx) {
    if (idx < 0 || idx >= board.length || revealed[idx]) {
        showBoard();
        return;
    }
    revealed[idx] = true;

    if (board[idx] === -1) {
        cm.sendOk("#r踩到地雷了！游戏失败！#k\r\n再接再厉，挑战下一局吧！");
        cm.dispose();
        return;
    }

    if (revealed.filter(function (v) { return v; }).length === rows * cols - mineCount) {
        finishGame();
        return;
    }
    showBoard();
}

function finishGame() {
    var costSeconds = Math.floor((Date.now() - startTime) / 1000);
    cm.getPlayer().getCashShop().gainCash(CashShop.NX_CREDIT, 通关点券奖励);
    cm.gainItem(通关道具奖励, 1);
    cm.getPlayer().getWorldServer().broadcastPacket(
        PacketCreator.serverNotice(6, "『踩地雷小游戏』：恭喜玩家 " + cm.getPlayer().getName() + " 花费 " + costSeconds + " 秒，成功完成了" + difficultyText + "难度的踩地雷！！！")
    );
    cm.sendOk("#r恭喜你成功排除所有地雷！#k\r\n用时 " + costSeconds + " 秒，获得 #b" + 通关点券奖励 + " 点券#k 及神秘奖励道具！");
    cm.dispose();
}

function showBoard() {
    var text = "#e#b踩地雷#k#n（" + difficultyText + "）\r\n\r\n";
    text += "#d点击格子进行翻开，翻开非地雷的全部格子即可通关！#k\r\n\r\n";
    for (var i = 0; i < rows; i++) {
        var line = "";
        for (var j = 0; j < cols; j++) {
            var idx = i * cols + j;
            if (revealed[idx]) {
                line += "[" + board[idx] + "]";
            } else {
                line += "#L" + idx + "#[?]#l";
            }
        }
        text += line + "\r\n";
    }
    text += "\r\n#L99#放弃本局#l\r\n";
    cm.sendSimple(text);
}
