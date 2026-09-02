/**
 * @description 连连看(记忆配对)小游戏
 * 16个格子(8种宝石图案各2张)，每次翻开两格，图案相同即消除并获得金币奖励
 * 全部消除后获得通关大奖；图案不同则两格重新盖上，可重复挑战
 * 入口：cm.openNpc(9900001, "xy/games/连连看")
 */

const PacketCreator = Java.type('org.gms.util.PacketCreator');
const CashShop = Java.type('org.gms.server.CashShop');

var 配对图案 = [4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007]; // 8种宝石图案
var 单次配对奖励金币 = 50000;
var 通关奖励金币 = 2000000;
var 通关奖励物品 = 4031189;

var board = [];
var matched = [];
var firstIdx = -1;
var phase = "init"; // init -> board(等待第一次翻牌) -> second(等待第二次翻牌) -> mismatchAck(不匹配确认)

function start() {
    initBoard();
    phase = "init";
    action(1, 0, 0);
}

function initBoard() {
    var pool = 配对图案.concat(配对图案);
    for (var i = pool.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var tmp = pool[i]; pool[i] = pool[j]; pool[j] = tmp;
    }
    board = pool;
    matched = [];
    for (var k = 0; k < board.length; k++) matched.push(false);
    firstIdx = -1;
}

function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }

    if (phase === "init") {
        phase = "board";
        showBoard();
    } else if (phase === "board") {
        if (selection === 99) { cm.dispose(); return; }
        if (selection < 0 || selection >= board.length || matched[selection]) {
            showBoard();
            return;
        }
        firstIdx = selection;
        phase = "second";
        showBoard();
    } else if (phase === "second") {
        if (selection === 99) { cm.dispose(); return; }
        if (selection === firstIdx || selection < 0 || selection >= board.length || matched[selection]) {
            firstIdx = -1;
            phase = "board";
            showBoard();
            return;
        }
        var secondIdx = selection;
        if (board[firstIdx] === board[secondIdx]) {
            matched[firstIdx] = true;
            matched[secondIdx] = true;
            firstIdx = -1;
            cm.gainMeso(单次配对奖励金币);

            if (matched.every(function (m) { return m; })) {
                finishGame();
                return;
            }
            phase = "board";
            showBoard();
        } else {
            firstIdx = -1;
            phase = "mismatchAck";
            cm.sendOk("#r图案不匹配#k，两张卡牌已重新盖上，再试一次吧！");
        }
    } else if (phase === "mismatchAck") {
        phase = "board";
        showBoard();
    } else {
        cm.dispose();
    }
}

function showBoard() {
    var remaining = matched.filter(function (m) { return !m; }).length;
    var text = "#e#b连连看#k#n\r\n\r\n";
    text += "#d翻开两张图案相同的卡牌即可消除，剩余 #r" + remaining + " #d张未消除！#k\r\n\r\n";

    for (var i = 0; i < board.length; i++) {
        if (matched[i]) continue;
        var icon = (i === firstIdx) ? ("#i" + board[i] + "#") : "❓";
        text += "#L" + i + "#" + icon + " 第" + (i + 1) + "格#l\r\n";
    }
    text += "\r\n#L99#放弃本局#l\r\n";
    cm.sendSimple(text);
}

function finishGame() {
    cm.gainMeso(通关奖励金币);
    cm.gainItem(通关奖励物品, 1);
    cm.getPlayer().getWorldServer().broadcastPacket(
        PacketCreator.serverNotice(6, "『连连看』恭喜玩家 " + cm.getPlayer().getName() + " 成功消除全部卡牌，获得通关大奖！")
    );
    cm.sendOk("#r恭喜你成功消除全部卡牌！#k\r\n获得 #b" + 通关奖励金币 + " 金币#k 及神秘奖励道具！");
    cm.dispose();
}
