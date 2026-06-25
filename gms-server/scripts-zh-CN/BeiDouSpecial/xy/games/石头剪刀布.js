/**
 * @description 石头剪刀布小游戏
 * 玩家与NPC猜拳，猜中(玩家赢)即可获得金币奖励，小概率额外获得道具奖励
 * 入口：cm.openNpc(9900001, "xy/games/石头剪刀布")
 */

var 奖励金币 = 100000;       // 获胜奖励金币数量
var 额外奖励物品 = 4031189;  // 获胜后小概率额外获得的道具
var 额外奖励概率 = 0.05;     // 5%概率

var 手势名 = ["剪刀", "石头", "布"];
var phase = "init"; // init: 初始进入 -> choice: 等待玩家出拳 -> replay: 等待是否再来一局

function start() {
    phase = "init";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }

    if (phase === "init") {
        phase = "choice";
        showMenu();
    } else if (phase === "choice") {
        if (selection === 9) { cm.dispose(); return; }
        playRound(selection);
        phase = "replay";
    } else if (phase === "replay") {
        if (type === 1) { // 玩家选择"是"，再来一局
            phase = "choice";
            showMenu();
        } else {
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function showMenu() {
    var text = "";
    text += "#e#b石头剪刀布#k#n\r\n\r\n";
    text += "#d请出拳！与我猜拳，猜中即可获得 #r" + 奖励金币 + " 金币#k#d 奖励！#k\r\n\r\n";
    text += "#L0#✂ 剪刀#l\r\n";
    text += "#L1#✊ 石头#l\r\n";
    text += "#L2#🖐 布#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function playRound(playerChoice) {
    var npcChoice = Math.floor(Math.random() * 3);
    var result = (playerChoice - npcChoice + 3) % 3; // 0=平局 1=玩家赢 2=玩家输

    var text = "你出了【" + 手势名[playerChoice] + "】，我出了【" + 手势名[npcChoice] + "】。\r\n\r\n";

    if (result === 1) {
        cm.gainMeso(奖励金币);
        text += "#r恭喜你猜拳获胜！#k 获得 #b" + 奖励金币 + " 金币#k 奖励！\r\n";
        if (Math.random() < 额外奖励概率) {
            cm.gainItem(额外奖励物品, 1);
            text += "运气爆棚！还额外获得了一份神秘奖励道具！\r\n";
        }
    } else if (result === 2) {
        text += "#b很遗憾，你输了，再接再厉！#k\r\n";
    } else {
        text += "#o势均力敌，打成平局！#k\r\n";
    }

    text += "\r\n是否再来一局？";
    cm.sendYesNo(text);
}
