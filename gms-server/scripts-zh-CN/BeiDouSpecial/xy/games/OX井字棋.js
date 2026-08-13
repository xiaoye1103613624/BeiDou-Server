/**
 * @description OX井字棋(Tic-Tac-Toe)小游戏
 * 玩家(O)与NPC(X，简单AI)对战，玩家获胜可获得奖励
 * 入口：cm.openNpc(9900001, "xy/games/OX井字棋")
 */

var 获胜奖励物品 = 5220000;

var 胜利组合 = [
    [0, 1, 2], [3, 4, 5], [6, 7, 8],
    [0, 3, 6], [1, 4, 7], [2, 5, 8],
    [0, 4, 8], [2, 4, 6]
];

var board = []; // 0=空 1=玩家(O) 2=电脑(X)
var phase = "init"; // init -> playing -> ended

function start() {
    phase = "init";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }

    if (phase === "init") {
        board = new Array(9).fill(0);
        phase = "playing";
        showBoard("请选择落子位置（你是#bO#k，电脑是#rX#k）：");
    } else if (phase === "playing") {
        if (selection === 99) { cm.dispose(); return; }
        handlePlayerMove(selection);
    } else if (phase === "ended") {
        if (type === 1) {
            phase = "init";
            action(1, 0, 0);
        } else {
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function handlePlayerMove(idx) {
    if (idx < 0 || idx >= 9 || board[idx] !== 0) {
        showBoard("该位置不可落子，请重新选择：");
        return;
    }
    board[idx] = 1;

    if (checkWinner(1)) {
        if (cm.canHold(获胜奖励物品)) {
            cm.gainItem(获胜奖励物品, 1);
        }
        endGame("#r恭喜你获胜了！#k 获得神秘奖励道具！\r\n\r\n是否再来一局？");
        return;
    }
    if (board.indexOf(0) === -1) {
        endGame("棋盘已满，双方平局！\r\n\r\n是否再来一局？");
        return;
    }

    computerMove();

    if (checkWinner(2)) {
        endGame("#b很遗憾，电脑获胜了，再接再厉！#k\r\n\r\n是否再来一局？");
        return;
    }
    if (board.indexOf(0) === -1) {
        endGame("棋盘已满，双方平局！\r\n\r\n是否再来一局？");
        return;
    }

    showBoard("请选择落子位置（你是#bO#k，电脑是#rX#k）：");
}

function computerMove() {
    // 简单AI：优先能获胜的位置 -> 优先阻挡玩家获胜的位置 -> 随机
    var move = findBestMove(2);
    if (move === -1) move = findBestMove(1);
    if (move === -1) {
        var empties = [];
        for (var i = 0; i < 9; i++) if (board[i] === 0) empties.push(i);
        move = empties[Math.floor(Math.random() * empties.length)];
    }
    board[move] = 2;
}

function findBestMove(forPlayer) {
    for (var c = 0; c < 胜利组合.length; c++) {
        var line = 胜利组合[c];
        var values = [board[line[0]], board[line[1]], board[line[2]]];
        var countFor = values.filter(function (v) { return v === forPlayer; }).length;
        var countEmpty = values.filter(function (v) { return v === 0; }).length;
        if (countFor === 2 && countEmpty === 1) {
            for (var k = 0; k < line.length; k++) {
                if (board[line[k]] === 0) return line[k];
            }
        }
    }
    return -1;
}

function checkWinner(player) {
    for (var c = 0; c < 胜利组合.length; c++) {
        var line = 胜利组合[c];
        if (board[line[0]] === player && board[line[1]] === player && board[line[2]] === player) {
            return true;
        }
    }
    return false;
}

function endGame(message) {
    phase = "ended";
    cm.sendYesNo(message);
}

function showBoard(prompt) {
    var text = "#e#bOX井字棋#k#n\r\n\r\n" + prompt + "\r\n\r\n";
    for (var r = 0; r < 3; r++) {
        var line = "";
        for (var c = 0; c < 3; c++) {
            var idx = r * 3 + c;
            if (board[idx] === 0) {
                line += "#L" + idx + "#[ ]#l";
            } else if (board[idx] === 1) {
                line += "[O]";
            } else {
                line += "[X]";
            }
        }
        text += line + "\r\n";
    }
    text += "\r\n#L99#放弃本局#l\r\n";
    cm.sendSimple(text);
}
