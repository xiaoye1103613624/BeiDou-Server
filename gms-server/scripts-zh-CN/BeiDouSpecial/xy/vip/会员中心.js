/**
 * @description VIP会员中心
 * 功能：查看当前VIP状态及等级，消耗点券购买/续费VIP，查看VIP特权说明
 * 入口：9900001.js case 600
 */

// VIP道具 ID（临时道具，每个代表7天VIP资格）
var VIP_ITEM_ID = 5211000;    // VIP卡道具（每个=7天）
var VIP_EXTEND_KEY = "vip_expire";  // 持久化key，存储VIP到期时间戳（ms）

// VIP等级配置：[等级名, 最低累计点券, 每月点券/购买价格]
var VIP_PLANS = [
    { name: "体验VIP",   nx: 1000,  days: 7,   desc: "解锁VIP商店，专属频道BUFF" },
    { name: "标准VIP",   nx: 3000,  days: 30,  desc: "以上+仓库扩展，快速传送" },
    { name: "高级VIP",   nx: 8000,  days: 30,  desc: "以上+时装洗练折扣，日常双倍加速" },
    { name: "至尊VIP",   nx: 20000, days: 30,  desc: "以上+全服公告，独家称号" }
];

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        showMainMenu();
    } else if (status === 1) {
        handleMainSelection(selection);
    } else if (status === 2) {
        if (type === 1) {
            doPurchaseVip(pendingPlanIdx);
        } else {
            cm.sendOk("已取消购买。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var player = cm.getPlayer();
    var nx = player.getCashShop().getCash(1);
    var expireTs = getVipExpire();
    var now = new Date().getTime();
    var isVip = expireTs > now;
    var daysLeft = isVip ? Math.ceil((expireTs - now) / 86400000) : 0;

    var text = "#e会员中心#n\r\n\r\n";
    text += "当前点券：#r" + nx + "#k\r\n";
    if (isVip) {
        text += "VIP状态：#b【VIP会员】#k  剩余 #r" + daysLeft + "#k 天\r\n\r\n";
    } else {
        text += "VIP状态：#d未开通#k\r\n\r\n";
    }

    text += "━━━ VIP套餐 ━━━\r\n\r\n";
    for (var i = 0; i < VIP_PLANS.length; i++) {
        var plan = VIP_PLANS[i];
        text += "#L" + i + "#";
        text += "#b" + plan.name + "#k  " + plan.days + "天  价格：#r" + plan.nx + "#k 点券\r\n";
        text += "   #d" + plan.desc + "#k";
        text += "#l\r\n\r\n";
    }

    text += "#L9##d查看VIP特权说明#k#l\r\n";
    text += "#L8#关闭#l\r\n";
    cm.sendSimple(text);
}

var pendingPlanIdx = -1;

function handleMainSelection(selection) {
    if (selection === 8) {
        cm.dispose();
        return;
    }
    if (selection === 9) {
        showPrivilegeInfo();
        return;
    }
    if (selection < 0 || selection >= VIP_PLANS.length) {
        cm.dispose();
        return;
    }

    var plan = VIP_PLANS[selection];
    var nx = cm.getPlayer().getCashShop().getCash(1);

    if (nx < plan.nx) {
        cm.sendOk("#r点券不足！\r\n需要：#b" + plan.nx + "#k 点券\r\n当前：#r" + nx + "#k 点券");
        cm.dispose();
        return;
    }

    pendingPlanIdx = selection;
    cm.sendYesNo("确认购买 #b" + plan.name + "#k " + plan.days + " 天？\r\n花费：#r" + plan.nx + "#k 点券");
}

function doPurchaseVip(idx) {
    if (idx < 0 || idx >= VIP_PLANS.length) {
        cm.sendOk("数据异常，请重试。");
        cm.dispose();
        return;
    }

    var plan = VIP_PLANS[idx];
    var nx = cm.getPlayer().getCashShop().getCash(1);

    if (nx < plan.nx) {
        cm.sendOk("#r点券不足！#k");
        cm.dispose();
        return;
    }

    cm.getPlayer().getCashShop().gainCash(1, -plan.nx);

    // 更新VIP到期时间（叠加）
    var now = new Date().getTime();
    var expireTs = getVipExpire();
    var base = expireTs > now ? expireTs : now;
    var newExpire = base + plan.days * 86400000;
    cm.saveOrUpdateCharacterExtendValue(VIP_EXTEND_KEY, String(newExpire));

    var daysLeft = Math.ceil((newExpire - now) / 86400000);
    cm.sendOk("开通成功！\r\n\r\n#b" + plan.name + "#k 已激活！\r\nVIP 到期时间：#r" + daysLeft + "#k 天后\r\n#g感谢支持！#k");
    cm.dispose();
}

function showPrivilegeInfo() {
    var text = "#eVIP特权说明#n\r\n\r\n";
    for (var i = 0; i < VIP_PLANS.length; i++) {
        var plan = VIP_PLANS[i];
        text += "#b【" + plan.name + "】#k\r\n";
        text += "  " + plan.desc + "\r\n\r\n";
    }
    text += "#d注：VIP特权叠加，高级VIP包含所有低级VIP特权。#k\r\n";
    cm.sendOk(text);
    cm.dispose();
}

/** 获取VIP到期时间戳（ms），若无记录返回0 */
function getVipExpire() {
    var val = cm.getCharacterExtendValue(VIP_EXTEND_KEY);
    if (val != null && val !== "" && val !== "null") {
        try { return Number(val); } catch (e) {}
    }
    return 0;
}
