/**
 * @description 收集系统入口（可挂载到任意NPC或物品）
 * 包含：卡片收集、玩具收集、强化戒指、钓鱼中心、小鱼戒指
 */

var 奖励 = "#fUI/CashShop/CSDiscount/bonus#";
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
    text += "\t#fUI/ChatBalloon/118/nw##fUI/ChatBalloon/118/n##fUI/ChatBalloon/118/n#\t#e #r收集系统#k#n \t#fUI/ChatBalloon/118/n##fUI/ChatBalloon/118/n##fUI/ChatBalloon/118/ne#\r\n";
    text += "\t" + 奖励.repeat(6) + "\r\n\r\n";
    text += "#b收集怪物卡片、钓鱼或兑换专属戒指！#k\r\n\r\n";
    text += "━━━ 图鉴收集 ━━━\r\n";
    text += "#L310#卡片收集  #d收集怪物卡片，换取戒指#k#l\r\n";
    text += "#L301#玩具收集  #d收集玩具图鉴#k#l\r\n";
    text += "#L303##r强化戒指#k  #d根据卡片图鉴等级兑换戒指#k#l\r\n";
    text += "\r\n━━━ 钓鱼系统 ━━━\r\n";
    text += "#L304#钓鱼中心  #d消耗鱼饵钓鱼获取奖励#k#l\r\n";
    text += "#L305#小鱼戒指  #d消耗鱼兑换专属戒指#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 310: openSub("xy/collect/卡片收集"); break;
        case 301: openSub("xy/collect/玩具收集"); break;
        case 303: openSub("xy/强化戒指"); break;
        case 304: openSub("xy/钓鱼中心"); break;
        case 305: openSub("xy/小鱼戒指"); break;
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
