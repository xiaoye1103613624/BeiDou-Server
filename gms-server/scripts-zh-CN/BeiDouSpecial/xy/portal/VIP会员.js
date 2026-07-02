/**
 * @description VIP会员入口（可挂载到任意NPC或物品）
 * 包含：会员中心、会员商店、赞助中心、CDK兑换、全服双倍、全服双爆
 */

var VIP_EXTEND_KEY = "vip_expire";
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
    var nx = player.getCashShop().getCash(1);
    var isVip = checkVip();
    var vipStr = isVip ? "#r[VIP有效]#k" : "#d[未开通VIP]#k";

    var text = "";
    text += "\t★━━\t#e #rVIP会员#k#n \t━━★\r\n";
    text += "\t" + 粉心.repeat(8) + "\r\n\r\n";
    text += "点券：#r" + new Intl.NumberFormat().format(nx) + "#k  │  VIP状态：" + vipStr + "\r\n\r\n";
    text += "━━━ 会员服务 ━━━\r\n";
    text += "#L600##r会员中心#k  #d开通/续费VIP会员#k#l\r\n";
    text += "#L602#会员商店  #d" + (isVip ? "VIP专属道具特价购买" : "需开通VIP才可访问") + "#k#l\r\n";
    text += "#L603#赞助中心  #d赞助服务器获取奖励#k#l\r\n";
    text += "#L608#CDK兑换  #d输入兑换码领取礼包#k#l\r\n";
    text += "\r\n━━━ 全服活动 ━━━\r\n";
    text += "#L604#全服双倍  #d开启全服双倍经验活动#k#l\r\n";
    text += "#L605#全服双爆  #d开启全服双倍爆率活动#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 600: openSub("xy/vip/会员中心"); break;
        case 602: openSub("xy/vip/会员商店"); break;
        case 603: openSub("xy/vip/赞助中心"); break;
        case 608: openSub("xy/vip/CDK_兑换"); break;
        case 604: openSub("xy/all/全服双倍"); break;
        case 605: openSub("xy/all/全服双爆"); break;
        case 9:   cm.dispose(); break;
        default:
            cm.sendOk("该功能暂未开放，敬请期待！");
            cm.dispose();
    }
}

function checkVip() {
    var val = cm.getCharacterExtendValue(VIP_EXTEND_KEY);
    if (val != null && val !== "" && val !== "null") {
        try { return Number(val) > new Date().getTime(); } catch (e) {}
    }
    return false;
}

function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
