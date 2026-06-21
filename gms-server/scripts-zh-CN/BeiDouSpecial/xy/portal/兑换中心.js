/**
 * @description 兑换中心入口（可挂载到任意NPC或物品）
 * 包含：物品兑换、金币兑换、枫叶兑换、道具抽奖、金币抽奖、金币赌博、卷轴中心、口令礼包
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
    var meso = cm.getPlayer().getMeso();
    var text = "";
    text += "\t#fUI/ChatBalloon/118/nw##fUI/ChatBalloon/118/n##fUI/ChatBalloon/118/n#\t#e #r兑换中心#k#n \t#fUI/ChatBalloon/118/n##fUI/ChatBalloon/118/n##fUI/ChatBalloon/118/ne#\r\n";
    text += "\t" + 奖励.repeat(6) + "\r\n\r\n";
    text += "金币：#r" + new Intl.NumberFormat().format(meso) + "#k\r\n\r\n";
    text += "━━━ 道具兑换 ━━━\r\n";
    text += "#L167#物品兑换  #d用道具兑换其他道具#k#l\r\n";
    text += "#L165#卷轴中心  #d各类卷轴一站购买#k#l\r\n";
    text += "#L171#枫叶兑换  #d用枫叶兑换珍贵道具#k#l\r\n";
    text += "#L511#口令礼包  #d输入口令领取礼包#k#l\r\n";
    text += "\r\n━━━ 金币消耗 ━━━\r\n";
    text += "#L170#道具抽奖  #d消耗金币抽取随机道具#k#l\r\n";
    text += "#L169#金币抽奖  #d消耗金币参与抽奖#k#l\r\n";
    text += "#L168#金币赌博  #d高风险高回报的赌博游戏#k#l\r\n";
    text += "#L180#金币兑换  #d金币兑换其他货币#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 167: openSub("xy/other/物品兑换"); break;
        case 165: openSub("xy/other/卷轴中心"); break;
        case 171: openSub("xy/other/枫叶兑换"); break;
        case 511: openSub("xy/other/口令礼包"); break;
        case 170: openSub("xy/other/道具抽奖"); break;
        case 169: openSub("xy/other/金币抽奖"); break;
        case 168: openSub("xy/other/金币赌博"); break;
        case 180: openSub("xy/other/金币兑换"); break;
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
