/**
 * @description GM工具入口（仅GM可见，可挂载到任意NPC）
 * 包含：发送公告、封禁、巡查面板、在线玩家、召唤Boss、物品查询、虚空索物、任意门
 */

var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    // GM权限验证
    if (!cm.getPlayer().isGM()) {
        cm.sendOk("#r权限不足！#k此入口仅限GM使用。");
        cm.dispose();
        return;
    }

    if (status === 0) { showMenu(); }
    else if (status === 1) { doSelect(selection); }
    else { cm.dispose(); }
}

function showMenu() {
    var player = cm.getPlayer();
    var text = "";
    text += "\t★━━\t#e #rGM工具箱#k#n \t━━★\r\n";
    text += "\t" + 皇冠白.repeat(6) + "\r\n\r\n";
    text += "操作员：#r" + player.getName() + "#k  GM等级：#b" + player.getGMLevel() + "#k\r\n\r\n";
    text += "━━━ 公告与管理 ━━━\r\n";
    text += "#L900#发送公告  #d向全服/频道发送公告#k#l\r\n";
    text += "#L903##r封禁玩家#k  #d封禁/解封指定玩家#k#l\r\n";
    text += "#L904#在线玩家  #d查看当前在线玩家列表#k#l\r\n";
    text += "#L901#巡查面板  #d监控服务器状态#k#l\r\n";
    text += "\r\n━━━ 道具工具 ━━━\r\n";
    text += "#L905#物品查询  #d查询道具信息#k#l\r\n";
    text += "#L906#虚空索物  #d远程获取任意道具#k#l\r\n";
    text += "#L990#GM商店  #d打开GM专属商店#k#l\r\n";
    text += "#L66#一键刷道具  #d快速刷新道具#k#l\r\n";
    text += "\r\n━━━ 地图工具 ━━━\r\n";
    text += "#L907#任意门  #d传送到任意地图#k#l\r\n";
    text += "#L902#召唤Boss  #d在当前地图召唤Boss#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 900: openSub("xy/gm/发送公告"); break;
        case 901: openSub("xy/gm/巡查面板"); break;
        case 902: openSub("xy/gm/召唤野外BOSS"); break;
        case 903: openSub("xy/gm/封禁"); break;
        case 904: openSub("xy/gm/在线玩家"); break;
        case 905: openSub("xy/gm/物品查询"); break;
        case 906: openSub("xy/gm/虚空索物"); break;
        case 907: openSub("xy/gm/虚空索物"); break;
        case 990:
            cm.openShopNPC(9900001);
            cm.dispose();
            break;
        case 66: openSub("一键刷道具"); break;
        case 9:  cm.dispose(); break;
        default:
            cm.sendOk("该功能暂未开放，敬请期待！");
            cm.dispose();
    }
}

function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
