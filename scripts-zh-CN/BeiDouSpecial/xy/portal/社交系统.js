/**
 * @description 社交系统入口（可挂载到任意NPC或物品）
 * 包含：师徒系统、家族系统
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
    text += "\t★━━\t#e #r社交系统#k#n \t━━★\r\n";
    text += "\t" + 粉心.repeat(8) + "\r\n\r\n";
    text += "玩家：#b" + player.getName() + "#k  等级：#r" + player.getLevel() + "#k\r\n\r\n";
    text += "━━━ 师徒系统 ━━━\r\n";
    text += "#L400##r师徒系统#k  #d收徒传授技艺，共享经验加成#k#l\r\n";
    text += "\r\n━━━ 家族系统 ━━━\r\n";
    text += "#L401#家族系统  #d加入或创建家族，共享家族福利#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 400: openSub("xy/mentor/师徒系统"); break;
        case 401: openSub("xy/家族系统"); break;
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
