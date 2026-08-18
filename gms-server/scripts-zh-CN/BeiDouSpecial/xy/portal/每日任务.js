/**
 * @description 每日任务入口（可挂载到任意NPC或物品）
 * 包含：每日签到、在线奖励、每日探索、每日副本、每日跑环、跑环仓库、每日Boss
 */

var 粉心 = "#fEffect/CharacterEff/1112903/0/1#";
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
    var player = cm.getPlayer();
    var text = "";
    text += "\t★━━\t#e #r每日任务#k#n \t━━★\r\n";
    text += "\t" + 粉心.repeat(8) + "\r\n\r\n";
    text += "#b每日任务每天重置，完成可获得丰厚奖励！#k\r\n\r\n";
    text += "#L1##r签到打卡#k  #d每日登录必做#k#l\r\n";
    text += "#L2#在线奖励  #d在线累计领取#k#l\r\n";
    text += "#L200#每日探索  #d探索不同地图#k#l\r\n";
    text += "#L201#每日副本  #d挑战每日副本#k#l\r\n";
    text += "#L202##r每日跑环#k  #d跑图刷怪赚金币#k#l\r\n";
    text += "#L203#跑环仓库  #d查看跑环道具#k#l\r\n";
    text += "#L205#每日Boss  #d挑战每日Boss#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 1:   openSub("每日签到"); break;
        case 2:   openSub("在线奖励_nextlevel"); break;
        case 200: openSub("xy/day/每日探索"); break;
        case 201: openSub("xy/day/每日副本"); break;
        case 202: openSub("xy/day/每日跑环"); break;
        case 203: openSub("xy/day/跑环仓库"); break;
        case 205: openSub("xy/day/每日Boss"); break;
        case 9:   cm.dispose(); break;
        default:
            cm.sendOk("该功能暂未开放，敬请期待！");
            cm.dispose();
    }
}

// 跳转子脚本（统一通过9900001加载BeiDouSpecial路径下的脚本）
function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
