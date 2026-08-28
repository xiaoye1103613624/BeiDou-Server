/**
 * @description 成长系统入口（可挂载到任意NPC或物品）
 * 包含：新人福利、新手礼包、等级奖励、快速转职、技能学习、天赋学习、经验戒指、转世重生
 */

var 火箭 = "#fUI/GuildMark/Mark/Etc/00009022/12#";
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
    text += "\t★━━\t#e #r成长系统#k#n \t━━★\r\n";
    text += "\t" + 火箭.repeat(6) + "\r\n\r\n";
    text += "等级：#r" + player.getLevel() + "#k  转生：#b" + player.getReborns() + "#k次\r\n";
    text += "AP：#r" + player.getRemainingAp() + "#k\r\n\r\n";
    text += "━━━ 新手引导 ━━━\r\n";
    text += "#L0##r新人福利#k  #d新手专属一次性礼包#k#l\r\n";
    text += "#L113#新手礼包  #d开服礼包道具领取#k#l\r\n";
    text += "#L112#等级奖励  #d每个等级门槛专属奖励#k#l\r\n";
    text += "\r\n━━━ 职业与技能 ━━━\r\n";
    text += "#L69#快速转职  #d一键完成职业转换#k#l\r\n";
    text += "#L302#技能学习  #d学习二段跳等辅助技能#k#l\r\n";
    text += "\r\n━━━ 能力强化 ━━━\r\n";
    text += "#L502#天赋学习  #d消耗AP加点基础属性#k#l\r\n";
    text += "#L503#经验戒指  #d购买提升经验加成戒指#k#l\r\n";
    text += "#L507##r转世重生#k  #d满级后转生获得强力加成#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 0:   openSub("新人福利"); break;
        case 113: openSub("新手礼包"); break;
        case 112: openSub("等级奖励"); break;
        case 69:  openSub("快速转职"); break;
        case 302: openSub("xy/技能学习"); break;
        case 502: openSub("xy/天赋学习"); break;
        case 503: openSub("xy/经验戒指"); break;
        case 507: openSub("xy/转世重生"); break;
        case 9:   cm.dispose(); break;
        default:
            cm.sendOk("该功能暂未开放，敬请期待！");
            cm.dispose();
    }
}

function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
