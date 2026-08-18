// 匠人街 · 蒙斯 · 材料商人
// 经验商店 / 特殊商店 / 限购商店 / 材料商店 / 强化商店 / 潜能商店
// 日限：[已购 / 上限]，跨天自动清零（quest progress 记 date|count）
// 多币种支付 = 同时扣除全部货币（金币/点卷/抵用券/赞助）
// 限购兑换日志：白底粉字（serverNotice type=5），记录「当前 / 剩余」兑换次数

var OpLogManager = Java.type('org.gms.log.OpLogManager');

var CASH_NX = 1;       // 点卷
var CASH_MAPLE = 2;    // 抵用券
// 赞助：角色可消费赞助 cm.getSponsor() / cm.spendSponsor()

// 日购记录 quest（infoNumber = logKey）
var DAILY_QUEST_SPECIAL = 9900311;
var DAILY_QUEST_LIMITED = 9900312;
var DAILY_QUEST_ENHANCE = 9900313;
var DAILY_QUEST_POTENTIAL = 9900314;
var DAILY_QUEST_MATERIAL = 9900315;

var status = -1;
var shopType = 0;      // 1经验 2特殊 3限购 4强化 5潜能 6材料
var selectedIdx = -1;
var buyTimes = 1;      // 购买次数（每包）

// ==================== 物品配置 ====================

// 经验商店：每件消耗经验
var EXP_ITEMS = [
    { id: 4032181, qty: 1, exp: 500000 }
];

// 强化商店：星之力卷轴、灵魂、星岩、保护符等
// dailyMax=每日可购次数；qty=每次获得数量；meso/nx/maple/prepaid 全部同时扣除
var ENHANCE_ITEMS = [
    // 星之力卷轴系列
    { id: 2049300, qty: 1, dailyMax: 30, meso: 500000, nx: 200, name: "T5强化卷" },
    { id: 2049301, qty: 1, dailyMax: 20, meso: 1000000, nx: 400, name: "T4强化卷" },
    { id: 2049302, qty: 1, dailyMax: 10, meso: 2000000, nx: 800, name: "T3强化卷" },
    // 灵魂
    { id: 2049914, qty: 1, dailyMax: 10, meso: 3000000, nx: 1000, name: "灵魂附魔石" },
    { id: 2049915, qty: 1, dailyMax: 10, meso: 1000000, nx: 300, name: "灵魂清除卷" },
    // 星岩
    { id: 2049913, qty: 1, dailyMax: 10, meso: 2000000, nx: 600, name: "星岩镶嵌卷" },
    // 洗炼石（装备之石复用）
    { id: 4032171, qty: 1, dailyMax: 30, meso: 500000, nx: 100, name: "洗炼石/装备之石" },
    // 灵韵结晶
    { id: 4021017, qty: 1, dailyMax: 10, meso: 5000000, nx: 500, name: "灵韵结晶" }
];

// 潜能商店：潜能卷、放大镜、魔方
var POTENTIAL_ITEMS = [
    // 潜能附加卷
    { id: 2049402, qty: 1, dailyMax: 20, meso: 1000000, nx: 200, name: "潜能附加卷100%" },
    { id: 2049400, qty: 1, dailyMax: 10, meso: 800000, nx: 150, name: "潜能附加卷90%" },
    { id: 2049401, qty: 1, dailyMax: 10, meso: 600000, nx: 100, name: "潜能附加卷70%" },
    // 放大镜
    { id: 2460000, qty: 1, dailyMax: 30, meso: 200000, nx: 50, name: "放大镜(Lv30-)" },
    { id: 2460001, qty: 1, dailyMax: 20, meso: 500000, nx: 150, name: "放大镜(Lv70-)" },
    { id: 2460002, qty: 1, dailyMax: 10, meso: 1000000, nx: 300, name: "放大镜(Lv120-)" },
    { id: 2460003, qty: 1, dailyMax: 5, meso: 3000000, nx: 800, name: "特级放大镜" },
    // 魔方
    { id: 5062000, qty: 1, dailyMax: 10, meso: 5000000, nx: 500, name: "神奇魔方" },
    { id: 5062001, qty: 1, dailyMax: 5, meso: 10000000, nx: 1000, name: "高级神奇魔方" },
    { id: 5062002, qty: 1, dailyMax: 3, meso: 20000000, nx: 2000, name: "超级神奇魔方" },
    // 附加潜能
    { id: 2049902, qty: 1, dailyMax: 10, meso: 3000000, nx: 400, name: "附加潜能卷" },
    // 附加魔方
    { id: 2049911, qty: 1, dailyMax: 5, meso: 8000000, nx: 800, name: "附加魔方" },
    // 品阶提升
    { id: 2049912, qty: 1, dailyMax: 3, meso: 15000000, nx: 1500, name: "品阶提升卷" }
];

// 特殊商店：meso/nx/maple 均需同时支付；pending=物品尚未实装（待另建）
var SPECIAL_ITEMS = [
    { id: 2431952, qty: 1, dailyMax: 99, meso: 10000000, nx: 800 },
    { id: 2431952, qty: 1, dailyMax: 99, meso: 10000000, nx: 800 },
    { id: 4036009, qty: 1, dailyMax: 99, meso: 10000000, nx: 1000, pending: true },
    { id: 4036006, qty: 1, dailyMax: 99, meso: 10000000, nx: 1500, pending: true },
    { id: 4036005, qty: 1, dailyMax: 99, meso: 10000000, nx: 2000, pending: true },
    { id: 2300001, qty: 1000, dailyMax: 30000, maple: 2000, nx: 2000 },
    { id: 5360016, qty: 1, dailyMax: 1, maple: 5000, nx: 5000, pending: true },
    { id: 2614000, qty: 1, dailyMax: 10, meso: 99999999, nx: 500 }
];

// 限购商店：赞助=角色可消费赞助（扣减不减少总赞助）；grantCash=直接发放货币点卷
var LIMITED_ITEMS = [
    { id: 4032171, qty: 100, dailyMax: 9, prepaid: 20 },
    { id: 4032169, qty: 100, dailyMax: 9, prepaid: 20 },
    { id: 2432443, qty: 1, dailyMax: 9, prepaid: 1 },
    { id: 2432444, qty: 1, dailyMax: 9, prepaid: 3 },
    { id: 2432445, qty: 1, dailyMax: 9, prepaid: 6 },
    { id: 2432446, qty: 1, dailyMax: 9, prepaid: 10 },
    { id: 4032862, qty: 1, dailyMax: 9, prepaid: 22, pending: true },
    { id: 4032873, qty: 1, dailyMax: 9, prepaid: 44, pending: true },
    { id: 4032863, qty: 1, dailyMax: 9, prepaid: 66, pending: true },
    { id: 4032868, qty: 1, dailyMax: 9, prepaid: 88, pending: true },
    { id: 2614000, qty: 1, dailyMax: 10, prepaid: 10 },
    { grantCash: CASH_NX, grantAmt: 10000, qty: 1, dailyMax: 99, prepaid: 1, name: "点卷1W" }
];

// 材料商店：金币 + 点卷 同时扣除
var MATERIAL_ITEMS = [
    { id: 4021009, qty: 1, dailyMax: 999, meso: 1000000, nx: 100, name: "星石" },
    { id: 4011007, qty: 1, dailyMax: 999, meso: 1000000, nx: 100, name: "月石" },
    { id: 4005000, qty: 1, dailyMax: 999, meso: 1000000, nx: 50, name: "力量水晶" },
    { id: 4005002, qty: 1, dailyMax: 999, meso: 1000000, nx: 50, name: "敏捷水晶" },
    { id: 4005001, qty: 1, dailyMax: 999, meso: 1000000, nx: 50, name: "智慧水晶" },
    { id: 4005003, qty: 1, dailyMax: 999, meso: 1000000, nx: 50, name: "幸运水晶" },
    { id: 4005004, qty: 1, dailyMax: 999, meso: 1000000, nx: 50, name: "黑暗水晶" }
];

// ==================== 状态机 ====================

function start() {
    status = -1;
    shopType = 0;
    selectedIdx = -1;
    buyTimes = 1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (shopType === 0) {
        handleMain(selection);
    } else if (shopType === 1) {
        handleExp(selection);
    } else if (shopType === 2) {
        handleShop(SPECIAL_ITEMS, DAILY_QUEST_SPECIAL, selection);
    } else if (shopType === 3) {
        handleShop(LIMITED_ITEMS, DAILY_QUEST_LIMITED, selection);
    } else if (shopType === 4) {
        handleShop(ENHANCE_ITEMS, DAILY_QUEST_ENHANCE, selection);
    } else if (shopType === 5) {
        handleShop(POTENTIAL_ITEMS, DAILY_QUEST_POTENTIAL, selection);
    } else if (shopType === 6) {
        handleShop(MATERIAL_ITEMS, DAILY_QUEST_MATERIAL, selection);
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
        t += "#L3##b限购商店（赞助专属）#k#l\r\n";
        t += "#L6##b材料商店（星石/水晶）#k#l\r\n";
        t += "#L4##b⭐ 强化材料（星之力/灵魂/星岩/洗炼/灵韵）#k#l\r\n";
        t += "#L5##b🔮 潜能材料（卷轴/放大镜/魔方）#k#l\r\n";
        cm.sendSimple(t);
        return;
    }
    if (status === 1) {
        if (selection === 1) {
            shopType = 1; status = -1; action(1, 0, 0);
        } else if (selection === 2) {
            shopType = 2; status = -1; action(1, 0, 0);
        } else if (selection === 3) {
            shopType = 3; status = -1; action(1, 0, 0);
        } else if (selection === 4) {
            shopType = 4; status = -1; action(1, 0, 0);
        } else if (selection === 5) {
            shopType = 5; status = -1; action(1, 0, 0);
        } else if (selection === 6) {
            shopType = 6; status = -1; action(1, 0, 0);
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
        try {
            OpLogManager.recordExchange(cm.getPlayer(), it2.id, gain,
                "材料商人经验商店 exp=" + cost + " item=" + it2.id + " x" + gain);
        } catch (ex) { /* 日志失败不影响玩法 */ }
        cm.sendOk("兑换成功！\r\n获得 #v" + it2.id + "# ×" + gain + "\r\n消耗经验：#r" + fmt(cost) + "#k");
        cm.dispose();
    }
}

// ==================== 通用日限商店（特殊/限购/强化/潜能/材料） ====================

function handleShop(list, questId, selection) {
    if (status === 0) {
        cm.sendSimple(buildShopList(list, questId));
        return;
    }
    if (status === 1) {
        if (selection === 9000) {
            backMain();
            return;
        }
        selectedIdx = selection;
        var it = list[selectedIdx];
        if (!it) {
            cm.dispose();
            return;
        }
        if (it.pending) {
            cm.sendOk("#v" + (it.id || 0) + "# " + displayName(it) + "\r\n#r该物品尚未实装，暂不可购买。#k");
            cm.dispose();
            return;
        }
        var bought = getDailyCount(questId, selectedIdx);
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
        cm.sendGetNumber(buildBuyPrompt(it, bought, pays), 1, 1, remain);
        return;
    }
    if (status === 2) {
        buyTimes = selection;
        doBuy(list[selectedIdx], questId, selectedIdx);
    }
}

// ==================== 购买执行（多币种同时扣除） ====================

function doBuy(it, questId, logKey) {
    if (!it || buyTimes <= 0) {
        cm.dispose();
        return;
    }
    if (it.pending) {
        cm.sendOk("该物品尚未实装，暂不可购买。");
        cm.dispose();
        return;
    }
    var bought = getDailyCount(questId, logKey);
    if (bought + buyTimes > it.dailyMax) {
        cm.sendOk("超过今日剩余可购次数。剩余：#r" + (it.dailyMax - bought) + "#k");
        cm.dispose();
        return;
    }
    var pays = getPayOptions(it);
    if (pays.length === 0) {
        cm.sendOk("该商品未配置价格。");
        cm.dispose();
        return;
    }

    // 1) 校验所有货币是否足够（同时扣款，须全部充足）
    var missing = [];
    for (var i = 0; i < pays.length; i++) {
        var need = pays[i].price * buyTimes;
        if (pays[i].type === "meso") {
            if (cm.getPlayer().getMeso() < need) {
                missing.push(pays[i].label + " 需要#r" + fmt(need) + "#k 当前#r" + fmt(cm.getPlayer().getMeso()) + "#k");
            }
        } else if (pays[i].type === "sponsor") {
            if (cm.getSponsor() < need) {
                missing.push(pays[i].label + " 需要#r" + fmt(need) + "#k 当前#r" + fmt(cm.getSponsor()) + "#k");
            }
        } else {
            if (getCash(pays[i].cashType) < need) {
                missing.push(pays[i].label + " 需要#r" + fmt(need) + "#k 当前#r" + fmt(getCash(pays[i].cashType)) + "#k");
            }
        }
    }
    if (missing.length > 0) {
        cm.sendOk("货币不足：\r\n" + missing.join("\r\n"));
        cm.dispose();
        return;
    }

    // 2) 背包空间（发放实物的物品才检查）
    if (!it.grantCash && !cm.canHold(it.id, it.qty * buyTimes)) {
        cm.sendOk("背包空间不足。");
        cm.dispose();
        return;
    }

    // 3) 先扣赞助，再扣其余货币
    for (var j = 0; j < pays.length; j++) {
        if (pays[j].type === "sponsor") {
            if (!cm.spendSponsor(pays[j].price * buyTimes)) {
                cm.sendOk("赞助扣减失败，请重试。");
                cm.dispose();
                return;
            }
        }
    }
    for (var k = 0; k < pays.length; k++) {
        var p = pays[k];
        if (p.type === "sponsor") {
            continue;
        }
        var cost = p.price * buyTimes;
        if (p.type === "meso") {
            cm.gainMeso(-cost);
        } else {
            cm.getPlayer().getCashShop().gainCash(p.cashType, -cost);
        }
    }

    // 4) 发放奖励
    var gainDesc;
    if (it.grantCash) {
        var nxGain = it.grantAmt * buyTimes;
        cm.getPlayer().getCashShop().gainCash(it.grantCash, nxGain);
        gainDesc = "点卷 × #r" + fmt(nxGain) + "#k";
    } else {
        var itemGain = it.qty * buyTimes;
        cm.gainItem(it.id, itemGain);
        gainDesc = "#v" + it.id + "# × " + itemGain;
    }

    // 5) 记录日限并写日志（白底粉字，含当前/剩余）
    addDailyCount(questId, logKey, buyTimes);
    var after = getDailyCount(questId, logKey);
    logPurchase(it, questId, logKey, after);

    var payDesc = [];
    for (var m = 0; m < pays.length; m++) {
        payDesc.push(pays[m].label + ":#r" + fmt(pays[m].price * buyTimes) + "#k");
    }
    cm.sendOk("购买成功！\r\n获得 " + gainDesc
        + "\r\n消耗 " + payDesc.join(" ")
        + "\r\n今日：#b[" + after + " / " + it.dailyMax + "]#k  剩余 #g" + (it.dailyMax - after) + "#k 次");
    cm.dispose();
}

// ==================== 日志 ====================

// 限购类兑换日志：白底粉字广播，记录「当前 / 剩余」兑换次数
function logPurchase(it, questId, logKey, after) {
    try {
        var remain = it.dailyMax - after;
        var gain = (it.grantCash ? it.grantAmt : it.qty) * buyTimes;
        var detail = "材料商人 shopType=" + shopType + " itemId=" + (it.id || 0) + " count=" + gain
            + " daily=" + after + "/" + it.dailyMax + " pay=" + JSON.stringify(getPayOptions(it));
        if (it.grantCash) {
            var summary = "兑换[点卷 * " + gain + "] 当前" + after + "/" + it.dailyMax + " 剩余" + remain;
            OpLogManager.record(cm.getPlayer(), OpLogManager.LIMITED, summary, detail);
        } else {
            OpLogManager.recordLimited(cm.getPlayer(), it.id, gain, after, it.dailyMax, detail);
        }
    } catch (ex) { /* 日志失败不影响玩法 */ }
}

// ==================== UI 构建 ====================

function buildShopList(list, questId) {
    var t = "#e#b" + shopTitle() + "#k#n\r\n";
    t += "格式：物品 ×数量  [已购/日限]  价格（多种货币同时扣除）\r\n\r\n";
    for (var i = 0; i < list.length; i++) {
        var it = list[i];
        var bought = getDailyCount(questId, i);
        t += "#L" + i + "#" + itemIcon(it) + " " + displayName(it) + " ×" + it.qty
            + "  #r[" + bought + " / " + it.dailyMax + "]#k  "
            + formatPriceLine(it) + "#l\r\n";
    }
    t += "\r\n#L9000##g返回#k#l";
    return t;
}

function buildBuyPrompt(it, bought, pays) {
    var remain = it.dailyMax - bought;
    var t = itemIcon(it) + " " + displayName(it) + " ×" + it.qty + " / 次\r\n";
    for (var i = 0; i < pays.length; i++) {
        t += "支付：" + pays[i].label + " #r" + fmt(pays[i].price) + "#k / 次\r\n";
    }
    t += "今日：#r[" + bought + " / " + it.dailyMax + "]#k  剩余 #b" + remain + "#k 次\r\n\r\n"
        + "请输入购买次数：";
    return t;
}

function shopTitle() {
    if (shopType === 2) return "特殊商店";
    if (shopType === 3) return "限购商店";
    if (shopType === 4) return "强化材料商店";
    if (shopType === 5) return "潜能材料商店";
    if (shopType === 6) return "材料商店";
    return "商店";
}

function itemIcon(it) {
    if (it.grantCash) {
        return "#fUI/CashShop.img/CashItem/0#";
    }
    return "#v" + it.id + "#";
}

function displayName(it) {
    if (it.name) {
        return it.name;
    }
    if (it.grantCash) {
        return "点卷";
    }
    return "#t" + it.id + "#";
}

function formatPriceLine(it) {
    if (it.pending) {
        return "#r尚未实装#k";
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
    return parts.length > 0 ? parts.join(" + ") : "#r无价格#k";
}

// 返回该物品「需同时支付」的全部货币项
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
