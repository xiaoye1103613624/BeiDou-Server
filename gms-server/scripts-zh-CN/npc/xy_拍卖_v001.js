/**
 * @description 拍卖行中心 / 脚本中心
 * @NPC: 9900001
 * 写法沿用本服 9900001.js 标准模式：start → action → doSelect(switch) → openNpc
 */

var changeLine = "\r\n";
var 翅膀左 = "★";
var 翅膀中间 = "━";
var 翅膀右 = "★";
var Logo = "#fEffect/UIWindow/AdminClaim/default/1#";
var 禁止脚本 = "#fEffect/UIWindow/AdminClaim/BtClaim/disabled/0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 皇冠 = "#fEffect/UIWindow/UserInfo/bossPetCrown#";
var 分割线3 = "#fEffect/UIWindow/AdminClaim/default/3#";
var 师徒系统 = "#fEffect/UIWindow/AdminClaim/default/6#";
var 快捷传送 = "#fEffect/UIWindow/AdminClaim/BtCancel/normal/0#";
var 快捷商店 = "#fEffect/UIWindow/AdminClaim/BtCancel/disabled/0#";
var 通行证 = "#fEffect/UIWindow/AdminClaim/BtCancel/mouseOver/0#";
var 每日福利 = "#fEffect/UIWindow/AdminClaim/BtCancel/pressed/0#";
var 全服排行 = "#fEffect/UIWindow/AdminClaim/BtCClaim/disabled/0#";
var 日常任务 = "#fEffect/UIWindow/AdminClaim/BtCClaim/mouseOver/0#";
var 超级仓库 = "#fEffect/UIWindow/AdminClaim/BtCClaim/normal/0#";
var 赞助福利 = "#fEffect/UIWindow/AdminClaim/BtCClaim/pressed/0#";
var 每日福利 = "#fEffect/UIWindow/AdminClaim/BtCancel/pressed/0#";
var 右箭头 = "#fEffect/UIWindow/UserList/Guild/MakeMark/BtRight/disabled/0#";

var OldTitle = "\t\t\t\t\t\t\t\t   " + Logo + "\r\n" +
    "\t\t\t\t" + 翅膀左 + 翅膀中间.repeat(2) + "\t\t#e欢迎来到  #r萧曳  #k冒险岛#n\t\t" + 翅膀中间.repeat(2) + 翅膀右 + "\t" + changeLine +
    禁止脚本 + changeLine;

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        cm.sendSimple(buildMenuText());
    } else if (status === 1) {
        doSelect(selection);
    } else {
        cm.dispose();
    }
}

function buildMenuText() {
    var text = OldTitle + buildHeaderInfo();
    text += "\t\t\t\t\t\t" + redSelect(1, "[自由市场]") + "\t\t" + redSelect(2, "[匠人街区]") + changeLine.repeat(3);

    text += generalSelect(11, 快捷传送) + generalSelect(12, 快捷商店) + generalSelect(13, 超级仓库) + generalSelect(21, 日常任务);
    text += changeLine.repeat(2);

    text += generalSelect(41, 每日福利) + generalSelect(31, 师徒系统) + generalSelect(42, 赞助福利)+generalSelect(43, "CD_KEY");
    text += changeLine.repeat(2);

    text += generalSelect(22, " 收集系统")+ "\t" + generalSelect(32, "家族系统") + "\t" + generalSelect(44, "银行系统")+ "\t" + generalSelect(33, "兑换中心");
    text += changeLine.repeat(2);

    if (cm.getPlayer().isGM()) {
        text += generalSelect(900, "GM商店") + "\t" + generalSelect(901, "一键刷道具") + "\t" + generalSelect(902, "在线玩家") + "\t" + generalSelect(903, "UI查询");
        text += changeLine;
        text += generalSelect(904, "发送公告") + "\t" + generalSelect(905, "巡查面板") + "\t" + generalSelect(906, "召唤BOSS") + "\t" + generalSelect(907, "封禁");
        text += changeLine;
        text += generalSelect(908, "物品查询") + "\t" + generalSelect(909, "虚空索物") + "\t" + generalSelect(910, "任意门") + "\t" + generalSelect(911, "装备制作");
        text += changeLine.repeat(2);
    }

    return text;
}

function fmtNum(n) {
    try {
        return new Intl.NumberFormat().format(n);
    } catch (e) {
        return String(n);
    }
}

function buildHeaderInfo() {
    var cashShop = cm.getPlayer().getCashShop();
    var onlineMs = cm.getOnlineTime();
    var timeStr;
    if (onlineMs < 3600) {
        timeStr = "在线时间：#e#r" + Math.floor(onlineMs / 60) + "#k#n 分钟";
    } else {
        var hour = Math.floor(onlineMs / 3600);
        var min = Math.floor((onlineMs % 3600) / 60);
        timeStr = "在线时间：#e#r" + hour + "#k#n 小时 #e#r" + min + "#k#n 分钟";
    }
    var info = "";
    info += "\t" + 正方箭头 + " 点券：" + fmtNum(cashShop.getCash(1)) + "\t\t\t\t" + timeStr + changeLine;
    info += "\t" + 正方箭头 + " 抵用：" + fmtNum(cashShop.getCash(2)) + "\t\t\t\t" + 正方箭头 + " 信用：" + fmtNum(cashShop.getCash(4)) + changeLine;
    info += "\t" + 正方箭头 + " 金币：" + fmtNum(cm.getPlayer().getMeso()) + changeLine;
    return info;
}

function doSelect(selection) {
    switch (selection) {
        case 1:
            cm.getPlayer().saveLocation("FREE_MARKET");
            cm.warp(910000000, "out00");
            break;
        case 2:
            cm.getPlayer().saveLocationOnWarp();
            cm.getPlayer().dropMessage(6, "[传送中心]：[" + cm.getPlayer().getName() + "玩家] [线路-" + cm.getPlayer().getClient().getChannel() + "] 传送至 匠人街");
            cm.warp(910001000);
            cm.dispose();
            break;
        case 11:
            openNpc("万能传送");
            break;
        case 12:
            cm.dispose();
            cm.openShopNPC(9900001);
            break;
        case 13:
            openNpc("xy/仓库");
            break;
        case 21:
            openNpc("每日签到");
            break;
        case 22:
            openNpc("xy/卡片收集");
            break;
        case 31:
            openNpc("xy/mentor/师徒系统");
            break;
        case 32:
            openNpc("xy/家族系统");
            break;
        case 33:
            openNpc("xy/other/物品兑换");
            break;
        case 41:
            openNpc("新人福利");
            break;
        case 42:
            openNpc("xy/vip/赞助中心");
            break;
        case 43:
            openNpc("xy/vip/CDK_兑换");
            break;
        case 44:
            openNpc("xy/other/银行系统");
            break;
        case 900:
            cm.dispose();
            cm.openShopNPC(9900001);
            break;
        case 901:
            openNpc("一键刷道具");
            break;
        case 902:
            openNpc("xy/gm/在线玩家");
            break;
        case 903:
            openNpc("UI查询");
            break;
        case 904:
            openNpc("xy/gm/发送公告");
            break;
        case 905:
            openNpc("xy/gm/巡查面板");
            break;
        case 906:
            openNpc("xy/gm/召唤野外BOSS");
            break;
        case 907:
            openNpc("xy/gm/封禁");
            break;
        case 908:
            openNpc("xy/gm/物品查询");
            break;
        case 909:
            openNpc("xy/gm/虚空索物");
            break;
        case 910:
            openNpc("xy/gm/任意门");
            break;
        case 911:
            openNpc("xy/装备系统/v000/套装制作升级");
            break;
        default:
            cm.sendOk("该功能暂不支持，敬请期待！");
            cm.dispose();
    }
}

function openNpc(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}

/**
 * 红色选项
 * @param idNum
 * @param text
 * @returns {string}
 */
function redSelect(idNum, text) {
    return `#L${idNum}##r${text}#k#n#l`;
}

/**
 * 一般选项
 * @param idNum
 * @param text
 * @returns {string}
 */
function generalSelect(idNum, text) {
    return `#L${idNum}#${text}#l`;
}
