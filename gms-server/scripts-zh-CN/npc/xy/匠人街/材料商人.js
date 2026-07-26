// 匠人街 · 蒙斯 · 材料商人
// 经验商店 / 特殊商店 / 限购商店
// 日限：[已购 / 上限]，跨天自动清零（quest progress 记 date|count）

var CASH_NX = 1;       // 点卷
var CASH_MAPLE = 2;    // 抵用券
// 赞助：角色可消费赞助 cm.getSponsor() / cm.spendSponsor()

// 日购记录 quest（infoNumber = logKey）
var DAILY_QUEST_SPECIAL = 9900311;
var DAILY_QUEST_LIMITED = 9900312;

var status = -1;
var shopType = 0;      // 1经验 2特殊 3限购
var selectedIdx = -1;
var payChoice = -1;    // 多币种时选择的支付方式下标
var buyTimes = 1;      // 购买次数（每包）

// 经验商店：每件消耗经验
var EXP_ITEMS = [
    { id: 4032181, qty: 1, exp: 500000 }
];

// 特殊商店：meso/nx/maple 有值即可作为支付选项（多币种=任选其一）
// dailyMax=每日可购次数；qty=每次获得数量
// pending=true 表示价格待配置，禁止购买
var SPECIAL_ITEMS = [
    { id: 2431952, qty: 1, dailyMax: 99, meso: 10000000, nx: 800 },
    // 挑战恢复剂价格待配置
    { id: 2004900, qty: 1, dailyMax: 99, pending: true },
    { id: 2004901, qty: 1, dailyMax: 99, pending: true },
    { id: 2004902, qty: 1, dailyMax: 99, pending: true },
    { id: 2300001, qty: 1000, dailyMax: 30000, maple: 2000, nx: 2000 },
    { id: 5360016, qty: 1, dailyMax: 1, maple: 5000, nx: 5000 },
    { id: 2614000, qty: 1, dailyMax: 10, meso: 99999999, nx: 500 }
];

// 限购商店：赞助=角色可消费赞助（扣减不减少总赞助）
var LIMITED_ITEMS = [
    { id: 4032171, qty: 100, dailyMax: 9, prepaid: 20 },
    { id: 4032169, qty: 100, dailyMax: 9, prepaid: 20 },
    { id: 2432443, qty: 1, dailyMax: 9, prepaid: 1 },
    { id: 2432444, qty: 1, dailyMax: 9, prepaid: 3 },
    { id: 2432445, qty: 1, dailyMax: 9, prepaid: 6 },
    { id: 2432446, qty: 1, dailyMax: 9, prepaid: 10 },
    { id: 4032862, qty: 1, dailyMax: 9, prepaid: 22 },
    { id: 4032873, qty: 1, dailyMax: 9, prepaid: 44 },
    { id: 4032863, qty: 1, dailyMax: 9, prepaid: 66 },
    { id: 4032868, qty: 1, dailyMax: 9, prepaid: 88 },
    { id: 2614000, qty: 1, dailyMax: 10, prepaid: 10 }
];

function start() {
    status = -1;
    shopType = 0;
    selectedIdx = -1;
    payChoice = -1;
    buyTimes = 1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (shopType === 0) {
        handleMain(selection);
    } else if (shopType === 1) {
        handleExp(selection);
    } else if (shopType === 2) {
        handleSpecial(selection);
    } else if (shopType === 3) {
        handleLimited(selection);
    } else {
        cm.dispose();
    }
}

// ==================== 主菜单 ====================

function handleMain(selection) {
    if (status === 0) {
        var t = "#e#b材料商人#k#n\r\n\r\n";
        t += "当前经验：#r" + fmt(cm.getPlayer().getExp()) + "#k\r\n";
        t += "点卷：#r" + fmt(getCash(CASH_NX)) + "#k  抵用：#r" + fmt(getCash(CASH_MAPLE)) + "#k\r\n";
        t += "赞助：#r" + fmt(cm.getSponsor()) + "#k  总赞助：#r" + fmt(cm.getTotalSponsor()) + "#k\r\n\r\n";
        t += "#L1##b经验商店#k#l\r\n";
        t += "#L2##b特殊商店#k#l\r\n";
        t += "#L3##b限购商店#k#l\r\n";
        cm.sendSimple(t);
        return;
    }
    if (status === 1) {
        if (selection === 1) {
            shopType = 1;
            status = -1;
            action(1, 0, 0);
        } else if (selection === 2) {
            shopType = 2;
            status = -1;
            action(1, 0, 0);
        } else if (selection === 3) {
            shopType = 3;
            status = -1;
            action(1, 0, 0);
        } else {
            cm.dispose();
        }
    }
}

// ==================== 经验商店 ====================

function handleExp(selection) {
    if (status === 0) {
        var t = "#e#b经验商店#k#n\r\n";
        t += "当前经验：#r" + fmt(cm.getPlayer().getExp()) + "#k\r\n\r\n";
        for (var i = 0; i < EXP_ITEMS.length; i++) {
            var it = EXP_ITEMS[i];
            t += "#L" + i + "##v" + it.id + "# #t" + it.id + "# ×" + it.qty
                + "  需要：#r" + fmt(it.exp) + "#k 经验#l\r\n";
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
        return;
    }
    if (status === 1) {
        if (selection === 9000) {
            backMain();
            return;
        }
        selectedIdx = selection;
        var it = EXP_ITEMS[selectedIdx];
        if (!it) {
            cm.dispose();
            return;
        }
        var maxByExp = Math.floor(cm.getPlayer().getExp() / it.exp);
        if (maxByExp <= 0) {
            cm.sendOk("经验不足！兑换 1 份需要 #r" + fmt(it.exp) + "#k 经验。");
            cm.dispose();
            return;
        }
        cm.sendGetNumber("#v" + it.id + "# #t" + it.id + "#\r\n单个需要：#r" + fmt(it.exp) + "#k 经验\r\n请输入兑换份数：", 1, 1, Math.min(maxByExp, 999));
        return;
    }
    if (status === 2) {
        buyTimes = selection;
        var it2 = EXP_ITEMS[selectedIdx];
        var cost = it2.exp * buyTimes;
        var gain = it2.qty * buyTimes;
        if (cm.getPlayer().getExp() < cost) {
            cm.sendOk("经验不足！");
            cm.dispose();
            return;
        }
        if (!cm.canHold(it2.id, gain)) {
            cm.sendOk("背包空间不足。");
            cm.dispose();
            return;
        }
        cm.getPlayer().loseExp(cost, true, true);
        cm.gainItem(it2.id, gain);
        cm.sendOk("兑换成功！\r\n获得 #v" + it2.id + "# ×" + gain + "\r\n消耗经验：#r" + fmt(cost) + "#k");
        cm.dispose();
    }
}

// ==================== 特殊商店 ====================

function handleSpecial(selection) {
    if (status === 0) {
        cm.sendSimple(buildShopList(SPECIAL_ITEMS, DAILY_QUEST_SPECIAL, "特殊商店"));
        return;
    }
    if (status === 1) {
        if (selection === 9000) {
            backMain();
            return;
        }
        selectedIdx = selection;
        var it = SPECIAL_ITEMS[selectedIdx];
        if (!it) {
            cm.dispose();
            return;
        }
        if (it.pending) {
            cm.sendOk("#v" + it.id + "# #t" + it.id + "#\r\n#r价格尚未配置，暂不可购买。#k");
            cm.dispose();
            return;
        }
        var bought = getDailyCount(DAILY_QUEST_SPECIAL, selectedIdx);
        var remain = it.dailyMax - bought;
        if (remain <= 0) {
            cm.sendOk("今日购买次数已达上限 [" + bought + " / " + it.dailyMax + "]。");
            cm.dispose();
            return;
        }
        var pays = getPayOptions(it);
        if (pays.length === 0) {
            cm.sendOk("该商品未配置价格。");
            cm.dispose();
            return;
        }
        if (pays.length === 1) {
            payChoice = 0;
            cm.sendGetNumber(buildBuyPrompt(it, bought, pays[0]), 1, 1, remain);
        } else {
            // 先选支付方式
            status = 10; // 跳到支付选择
            var t = "#v" + it.id + "# #t" + it.id + "# ×" + it.qty + "\r\n";
            t += "今日：#r[" + bought + " / " + it.dailyMax + "]#k\r\n\r\n请选择支付方式：\r\n";
            for (var i = 0; i < pays.length; i++) {
                t += "#L" + i + "#" + pays[i].label + "：#r" + fmt(pays[i].price) + "#k#l\r\n";
            }
            cm.sendSimple(t);
        }
        return;
    }
    if (status === 11) {
        // 支付方式已选
        payChoice = selection;
        var it3 = SPECIAL_ITEMS[selectedIdx];
        var bought3 = getDailyCount(DAILY_QUEST_SPECIAL, selectedIdx);
        var remain3 = it3.dailyMax - bought3;
        var pays3 = getPayOptions(it3);
        if (payChoice < 0 || payChoice >= pays3.length) {
            cm.dispose();
            return;
        }
        cm.sendGetNumber(buildBuyPrompt(it3, bought3, pays3[payChoice]), 1, 1, remain3);
        return;
    }
    if (status === 2 || status === 12) {
        buyTimes = selection;
        doCashBuy(SPECIAL_ITEMS[selectedIdx], DAILY_QUEST_SPECIAL, selectedIdx);
    }
}

// ==================== 限购商店 ====================

function handleLimited(selection) {
    if (status === 0) {
        cm.sendSimple(buildShopList(LIMITED_ITEMS, DAILY_QUEST_LIMITED, "限购商店"));
        return;
    }
    if (status === 1) {
        if (selection === 9000) {
            backMain();
            return;
        }
        selectedIdx = selection;
        var it = LIMITED_ITEMS[selectedIdx];
        if (!it) {
            cm.dispose();
            return;
        }
        var bought = getDailyCount(DAILY_QUEST_LIMITED, selectedIdx);
        var remain = it.dailyMax - bought;
        if (remain <= 0) {
            cm.sendOk("今日购买次数已达上限 [" + bought + " / " + it.dailyMax + "]。");
            cm.dispose();
            return;
        }
        payChoice = 0;
        var pays = getPayOptions(it);
        cm.sendGetNumber(buildBuyPrompt(it, bought, pays[0]), 1, 1, remain);
        return;
    }
    if (status === 2) {
        buyTimes = selection;
        doCashBuy(LIMITED_ITEMS[selectedIdx], DAILY_QUEST_LIMITED, selectedIdx);
    }
}

// ==================== 购买执行 ====================

function doCashBuy(it, questId, logKey) {
    if (!it || buyTimes <= 0) {
        cm.dispose();
        return;
    }
    if (it.pending) {
        cm.sendOk("价格尚未配置，暂不可购买。");
        cm.dispose();
        return;
    }
    var bought = getDailyCount(questId, logKey);
    if (bought + buyTimes > it.dailyMax) {
        cm.sendOk("超过今日剩余可购次数。剩余：" + (it.dailyMax - bought));
        cm.dispose();
        return;
    }
    var pays = getPayOptions(it);
    if (payChoice < 0 || payChoice >= pays.length) {
        cm.dispose();
        return;
    }
    var pay = pays[payChoice];
    var totalPrice = pay.price * buyTimes;
    var gain = it.qty * buyTimes;

    if (pay.type === "meso") {
        if (cm.getPlayer().getMeso() < totalPrice) {
            cm.sendOk("金币不足！需要 #r" + fmt(totalPrice) + "#k。");
            cm.dispose();
            return;
        }
    } else if (pay.type === "sponsor") {
        if (cm.getSponsor() < totalPrice) {
            cm.sendOk("赞助不足！需要 #r" + fmt(totalPrice) + "#k，当前 #r" + fmt(cm.getSponsor()) + "#k。");
            cm.dispose();
            return;
        }
    } else {
        if (getCash(pay.cashType) < totalPrice) {
            cm.sendOk(pay.label + "不足！需要 #r" + fmt(totalPrice) + "#k。");
            cm.dispose();
            return;
        }
    }
    if (!cm.canHold(it.id, gain)) {
        cm.sendOk("背包空间不足。");
        cm.dispose();
        return;
    }

    if (pay.type === "meso") {
        cm.gainMeso(-totalPrice);
    } else if (pay.type === "sponsor") {
        if (!cm.spendSponsor(totalPrice)) {
            cm.sendOk("赞助扣减失败，请重试。");
            cm.dispose();
            return;
        }
    } else {
        cm.getPlayer().getCashShop().gainCash(pay.cashType, -totalPrice);
    }
    cm.gainItem(it.id, gain);
    addDailyCount(questId, logKey, buyTimes);

    var after = getDailyCount(questId, logKey);
    cm.sendOk("购买成功！\r\n获得 #v" + it.id + "# ×" + gain
        + "\r\n消耗 " + pay.label + "：#r" + fmt(totalPrice) + "#k"
        + "\r\n今日：#b[" + after + " / " + it.dailyMax + "]#k");
    cm.dispose();
}

// ==================== UI 构建 ====================

function buildShopList(list, questId, title) {
    var t = "#e#b" + title + "#k#n\r\n";
    t += "格式：物品 ×数量  [已购/日限]  价格\r\n\r\n";
    for (var i = 0; i < list.length; i++) {
        var it = list[i];
        var bought = getDailyCount(questId, i);
        t += "#L" + i + "##v" + it.id + "# #t" + it.id + "# ×" + it.qty
            + "  #r[" + bought + " / " + it.dailyMax + "]#k  "
            + formatPriceLine(it) + "#l\r\n";
    }
    t += "\r\n#L9000##g返回#k#l";
    return t;
}

function buildBuyPrompt(it, bought, pay) {
    var remain = it.dailyMax - bought;
    return "#v" + it.id + "# #t" + it.id + "# ×" + it.qty + " / 次\r\n"
        + "支付：" + pay.label + " #r" + fmt(pay.price) + "#k / 次\r\n"
        + "今日：#r[" + bought + " / " + it.dailyMax + "]#k  剩余 #b" + remain + "#k 次\r\n\r\n"
        + "请输入购买次数：";
}

function formatPriceLine(it) {
    if (it.pending) {
        return "#r价格待配置#k";
    }
    var parts = [];
    if (it.meso > 0) {
        parts.push("金币:" + fmt(it.meso));
    }
    if (it.nx > 0) {
        parts.push("点卷:" + fmt(it.nx));
    }
    if (it.maple > 0) {
        parts.push("抵用:" + fmt(it.maple));
    }
    if (it.prepaid > 0) {
        parts.push("赞助:" + fmt(it.prepaid));
    }
    return parts.length > 0 ? parts.join(" / ") : "#r无价格#k";
}

function getPayOptions(it) {
    var opts = [];
    if (it.meso > 0) {
        opts.push({ type: "meso", label: "金币", price: it.meso });
    }
    if (it.nx > 0) {
        opts.push({ type: "cash", cashType: CASH_NX, label: "点卷", price: it.nx });
    }
    if (it.maple > 0) {
        opts.push({ type: "cash", cashType: CASH_MAPLE, label: "抵用券", price: it.maple });
    }
    if (it.prepaid > 0) {
        opts.push({ type: "sponsor", label: "赞助", price: it.prepaid });
    }
    return opts;
}

// ==================== 日限记录 ====================

function todayStr() {
    var cal = java.util.Calendar.getInstance();
    var y = cal.get(java.util.Calendar.YEAR);
    var m = cal.get(java.util.Calendar.MONTH) + 1;
    var d = cal.get(java.util.Calendar.DAY_OF_MONTH);
    return "" + y + (m < 10 ? "0" : "") + m + (d < 10 ? "0" : "") + d;
}

function getDailyCount(questId, logKey) {
    ensureQuest(questId);
    var raw = cm.getQuestProgress(questId, logKey);
    if (raw == null || raw === "") {
        return 0;
    }
    var parts = ("" + raw).split("|");
    if (parts.length < 2 || parts[0] !== todayStr()) {
        return 0;
    }
    var n = parseInt(parts[1], 10);
    return isNaN(n) ? 0 : n;
}

function addDailyCount(questId, logKey, add) {
    ensureQuest(questId);
    var cur = getDailyCount(questId, logKey);
    cm.setQuestProgress(questId, logKey, todayStr() + "|" + (cur + add));
}

function ensureQuest(questId) {
    try {
        if (!cm.isQuestStarted(questId) && !cm.isQuestCompleted(questId)) {
            cm.forceStartQuest(questId);
        }
    } catch (e) {
        // 忽略
    }
}

// ==================== 工具 ====================

function backMain() {
    shopType = 0;
    selectedIdx = -1;
    payChoice = -1;
    status = -1;
    action(1, 0, 0);
}

function getCash(type) {
    return cm.getPlayer().getCashShop().getCash(type);
}

function fmt(num) {
    if (num == null) {
        return "0";
    }
    return ("" + num).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
