/**
 * @description 小游戏脚本中心入口（可挂载到任意NPC或物品）
 * 包含：石头剪刀布、连连看、踩地雷、老虎机、OX井字棋
 */

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) { showMenu(); }
    else if (status === 1) { doSelect(selection); }
    else { cm.dispose(); }
}

function showMenu() {
    var text = "";
    text += "#e#b小游戏脚本中心#k#n\r\n\r\n";
    text += "#d欢迎光临！这里有多种趣味小游戏，快来挑战试试运气吧！#k\r\n\r\n";
    text += "#L1#✂ 石头剪刀布  #d与NPC猜拳，猜中即可获得奖励#k#l\r\n";
    text += "#L2#🀄 连连看  #d消除全部卡牌图案，赢取大奖#k#l\r\n";
    text += "#L3#💣 踩地雷  #d考验运气与判断的扫雷小游戏#k#l\r\n";
    text += "#L4#🎰 老虎机  #d下注金币，拉霸赢取高额回报#k#l\r\n";
    text += "#L5#⭕ OX井字棋  #d与NPC对战井字棋，赢了就有奖#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 1: openSub("xy/games/石头剪刀布"); break;
        case 2: openSub("xy/games/连连看"); break;
        case 3: openSub("xy/games/踩地雷"); break;
        case 4: openSub("xy/games/老虎机"); break;
        case 5: openSub("xy/games/OX井字棋"); break;
        case 9: cm.dispose(); break;
        default:
            cm.sendOk("该游戏暂未开放，敬请期待！");
            cm.dispose();
    }
}

function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
