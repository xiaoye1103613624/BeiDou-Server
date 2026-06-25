/*
    卷轴综合系统 NPC
    1. 卷轴分解：消耗栏中的普通卷轴(10%/30%/60%/100%) → 1:1分解为卷轴碎片(4001136)
    2. 卷轴兑换：卷轴碎片按兑换级别(100:1 / 1000:1 / 10000:1)兑换为指定卷轴
    3. 卷轴购买：点卷购买祝福卷轴(2340000，10000点卷/张)、混沌卷轴(2049100，5000点卷/张)
*/

// ==================== 通用配置 ====================
var FRAGMENT_ID   = 4001136;     // 卷轴碎片
var FRAGMENT_NAME = "卷轴碎片";
var CASH_NX_CREDIT = 1;          // 点券/点卷货币类型，对应 CashShop.java

// 当前会话状态
var screen = "main";

// ==================== 1. 卷轴分解 状态 ====================
var decomposeList = [];   // [{itemId, qty, rate}]
var decomposeIdx  = -1;
var decomposeQty  = 0;

// ==================== 2. 卷轴兑换 配置与状态 ====================
// 兑换级别配置：碎片数量 -> 可兑换的卷轴ID列表（按需在此增删卷轴ID）
var EXCHANGE_CONFIG = {
    100:   [],   // 100碎片 兑换 1张，低价卷轴在此填入itemId
    1000:  [],   // 1000碎片 兑换 1张，中价卷轴在此填入itemId
    10000: []    // 10000碎片 兑换 1张，高价卷轴在此填入itemId
};
var EXCHANGE_TIERS = [100, 1000, 10000];
var exchangeGroupIdx = -1; // 选中的价格档在 EXCHANGE_TIERS 中的索引
var exchangeItemIdx  = -1; // 选中的卷轴在该价格档列表中的索引
var exchangeQty      = 0;

// ==================== 3. 卷轴购买 配置与状态 ====================
// 购买列表：单价为点卷(点券)数量
var BUY_LIST = [
    { 代码: 2340000, 价格: 10000, 名称: "祝福卷轴" },
    { 代码: 2049100, 价格: 5000,  名称: "混沌卷轴" }
];
var buyIdx = -1;
var buyQty = 0;

// ==================== 入口 ====================

function start() {
    screen = "main";
    showMain();
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }

    if (screen == "main") {
        if (selection == 1) { decomposeList = []; decomposeIdx = -1; screen = "decompose_list"; showDecomposeList(); }
        else if (selection == 2) { screen = "exchange_groups"; showExchangeGroups(); }
        else if (selection == 3) { screen = "buy_list"; showBuyList(); }
        else { cm.dispose(); }
        return;
    }

    // ---------- 卷轴分解 ----------
    if (screen == "decompose_list") { handleDecomposeListSelect(selection); return; }
    if (screen == "decompose_qty")  { handleDecomposeQty(selection); return; }
    if (screen == "decompose_confirm") {
        if (mode == 1) doDecompose(); else cm.dispose();
        return;
    }

    // ---------- 卷轴兑换 ----------
    if (screen == "exchange_groups") { handleExchangeGroupSelect(selection); return; }
    if (screen == "exchange_list")   { handleExchangeItemSelect(selection); return; }
    if (screen == "exchange_qty")    { handleExchangeQty(selection); return; }
    if (screen == "exchange_confirm") {
        if (mode == 1) doExchange(); else cm.dispose();
        return;
    }

    // ---------- 卷轴购买 ----------
    if (screen == "buy_list") { handleBuyListSelect(selection); return; }
    if (screen == "buy_qty")  { handleBuyQty(selection); return; }
    if (screen == "buy_confirm") {
        if (mode == 1) doBuy(); else cm.dispose();
        return;
    }

    cm.dispose();
}

function showMain() {
    var text = "#e#d卷轴系统#n#k\r\n\r\n";
    text += "#L1##b1. 卷轴分解#k  (普通卷轴 → " + FRAGMENT_NAME + ")#l\r\n";
    text += "#L2##b2. 卷轴兑换#k  (" + FRAGMENT_NAME + " → 指定卷轴)#l\r\n";
    text += "#L3##b3. 卷轴购买#k  (点卷购买)#l\r\n";
    cm.sendSimple(text);
}

// ==================== 1. 卷轴分解 ====================

function showDecomposeList() {
    scanDecomposeScrolls();
    if (decomposeList.length == 0) {
        cm.sendOk("消耗栏中没有可分解的普通卷轴(10%/30%/60%/100%)。");
        cm.dispose();
        return;
    }
    var text = "#e卷轴分解#n  产出：#v" + FRAGMENT_ID + "# #b" + FRAGMENT_NAME + "#k (1:1)\r\n\r\n";
    for (var i = 0; i < decomposeList.length; i++) {
        var si = decomposeList[i];
        text += "#L" + i + "##v" + si.itemId + "# #b#z" + si.itemId + "##k  #d[" + si.rate + "]#k  #r×" + si.qty + "#k#l\r\n";
    }
    cm.sendSimple(text);
}

function handleDecomposeListSelect(selection) {
    decomposeIdx = selection;
    if (decomposeIdx < 0 || decomposeIdx >= decomposeList.length) { cm.dispose(); return; }
    var si = decomposeList[decomposeIdx];
    screen = "decompose_qty";
    cm.sendGetNumber(
        "#v" + si.itemId + "# #b#z" + si.itemId + "##k  #d[" + si.rate + "]#k\r\n\r\n" +
        "库存：#r" + si.qty + "个#k\r\n" +
        "产出：#v" + FRAGMENT_ID + "# #b" + FRAGMENT_NAME + "#k（1:1）\r\n\r\n" +
        "请输入要分解的数量：",
        1, 1, Math.min(si.qty, 999)
    );
}

function handleDecomposeQty(selection) {
    decomposeQty = selection;
    if (decomposeQty < 1) { cm.dispose(); return; }
    var si = decomposeList[decomposeIdx];
    if (decomposeQty > si.qty) decomposeQty = si.qty;
    screen = "decompose_confirm";
    cm.sendYesNo(
        "#e分解确认#n\r\n\r\n" +
        "卷轴：#b#z" + si.itemId + "##k  #d[" + si.rate + "]#k\r\n" +
        "分解数量：#r" + decomposeQty + "个#k\r\n" +
        "将获得：#v" + FRAGMENT_ID + "# #b" + decomposeQty + "个 " + FRAGMENT_NAME + "#k\r\n\r\n" +
        "确认分解？"
    );
}

function doDecompose() {
    if (decomposeIdx < 0 || decomposeIdx >= decomposeList.length || decomposeQty < 1) { cm.dispose(); return; }
    var si = decomposeList[decomposeIdx];
    var owned = cm.getItemQuantity(si.itemId);
    if (owned < decomposeQty) {
        cm.sendOk("该卷轴数量不足！库存 #b" + owned + "个#k，需要 #r" + decomposeQty + "个#k。");
        cm.dispose();
        return;
    }
    cm.gainItem(si.itemId, -decomposeQty);
    cm.gainItem(FRAGMENT_ID, decomposeQty);
    cm.sendOk(
        "#b分解成功！#k\r\n\r\n分解 #b" + decomposeQty + "个#k 卷轴 → 获得 #v" + FRAGMENT_ID + "# #b" +
        decomposeQty + "个 " + FRAGMENT_NAME + "#k。"
    );
    cm.dispose();
}

function scanDecomposeScrolls() {
    decomposeList = [];
    var iter = cm.getInventory(2).list().iterator();
    while (iter.hasNext()) {
        var item = iter.next();
        var itemId = item.getItemId();
        var rate = getScrollRate(itemId);
        if (rate == "") continue; // 仅普通10%/30%/60%/100%卷轴可分解
        decomposeList.push({ itemId: itemId, qty: item.getQuantity(), rate: rate });
    }
}

function getScrollRate(itemId) {
    try {
        var IIP = Java.type('org.gms.server.ItemInformationProvider');
        var name = IIP.getInstance().getName(itemId);
        if (name == null || name.indexOf("卷轴") < 0) return "";
        if (name.indexOf("100%") >= 0) return "100%";
        if (name.indexOf("60%") >= 0) return "60%";
        if (name.indexOf("30%") >= 0) return "30%";
        if (name.indexOf("10%") >= 0) return "10%";
        return "";
    } catch (e) { return ""; }
}

// ==================== 2. 卷轴兑换 ====================

function showExchangeGroups() {
    var owned = cm.getItemQuantity(FRAGMENT_ID);
    var text = "#e卷轴兑换中心#n\r\n\r\n";
    text += "#v" + FRAGMENT_ID + "# #b" + FRAGMENT_NAME + "#k  拥有：#r" + owned + "个#k\r\n\r\n";
    text += "#e#d选择兑换价格档：#n#k\r\n\r\n";
    for (var g = 0; g < EXCHANGE_TIERS.length; g++) {
        var cost = EXCHANGE_TIERS[g];
        var items = EXCHANGE_CONFIG[cost] || [];
        var canAfford = owned >= cost;
        text += "#L" + g + "#";
        text += (canAfford ? "" : "#d[不足]#k ");
        text += "#b" + cost + "碎片#k 兑换 1张  —  #d" + items.length + "种卷轴#k";
        text += "#l\r\n";
    }
    cm.sendSimple(text);
}

function handleExchangeGroupSelect(selection) {
    exchangeGroupIdx = selection;
    if (exchangeGroupIdx < 0 || exchangeGroupIdx >= EXCHANGE_TIERS.length) { cm.dispose(); return; }
    var cost = EXCHANGE_TIERS[exchangeGroupIdx];
    var items = EXCHANGE_CONFIG[cost] || [];
    if (items.length == 0) {
        cm.sendOk("该价格档暂未配置任何卷轴，请联系管理员配置。");
        cm.dispose();
        return;
    }
    screen = "exchange_list";
    showExchangeItemList();
}

function showExchangeItemList() {
    var cost = EXCHANGE_TIERS[exchangeGroupIdx];
    var items = EXCHANGE_CONFIG[cost] || [];
    var owned = cm.getItemQuantity(FRAGMENT_ID);
    var text = "#e卷轴兑换中心#n\r\n\r\n";
    text += "#b" + cost + "碎片/张#k  |  拥有：#r" + owned + "个#k\r\n\r\n";
    for (var i = 0; i < items.length; i++) {
        var id = items[i];
        text += "#L" + i + "##v" + id + "# #b#z" + id + "##k#l\r\n";
    }
    cm.sendSimple(text);
}

function handleExchangeItemSelect(selection) {
    var cost = EXCHANGE_TIERS[exchangeGroupIdx];
    var items = EXCHANGE_CONFIG[cost] || [];
    exchangeItemIdx = selection;
    if (exchangeItemIdx < 0 || exchangeItemIdx >= items.length) { cm.dispose(); return; }
    var scrollId = items[exchangeItemIdx];
    var owned = cm.getItemQuantity(FRAGMENT_ID);
    var maxQty = Math.floor(owned / cost);
    if (maxQty <= 0) {
        cm.sendOk("碎片不足！\r\n\r\n需要 #r" + cost + "个 " + FRAGMENT_NAME + "#k，只有 #b" + owned + "个#k。");
        cm.dispose();
        return;
    }
    maxQty = Math.min(maxQty, 999);
    screen = "exchange_qty";
    cm.sendGetNumber(
        "#v" + scrollId + "# #b#z" + scrollId + "##k\r\n\r\n" +
        "单价：#r" + cost + "个 " + FRAGMENT_NAME + "#k\r\n" +
        "拥有：#r" + owned + "个 " + FRAGMENT_NAME + "#k\r\n" +
        "最大可兑：#r" + maxQty + "个#k\r\n\r\n请输入兑换数量：",
        1, 1, maxQty
    );
}

function handleExchangeQty(selection) {
    exchangeQty = selection;
    if (exchangeQty < 1) { cm.dispose(); return; }
    var cost = EXCHANGE_TIERS[exchangeGroupIdx];
    var items = EXCHANGE_CONFIG[cost] || [];
    var scrollId = items[exchangeItemIdx];
    var totalCost = cost * exchangeQty;
    var owned = cm.getItemQuantity(FRAGMENT_ID);
    screen = "exchange_confirm";
    cm.sendYesNo(
        "#e兑换确认#n\r\n\r\n" +
        "卷轴：#b#z" + scrollId + "##k\r\n" +
        "数量：#r" + exchangeQty + "个#k\r\n" +
        "消耗：#r" + totalCost + "个 " + FRAGMENT_NAME + "#k\r\n" +
        "拥有：#b" + owned + "个#k\r\n\r\n确认兑换？"
    );
}

function doExchange() {
    var cost = EXCHANGE_TIERS[exchangeGroupIdx];
    var items = EXCHANGE_CONFIG[cost] || [];
    var scrollId = items[exchangeItemIdx];
    if (scrollId == undefined || exchangeQty < 1) { cm.dispose(); return; }
    var totalCost = cost * exchangeQty;
    var owned = cm.getItemQuantity(FRAGMENT_ID);
    if (owned < totalCost) {
        cm.sendOk("碎片不足！需要 #r" + totalCost + "个#k，只有 #b" + owned + "个#k。");
        cm.dispose();
        return;
    }
    if (!cm.canHold(scrollId, exchangeQty)) {
        cm.sendOk("背包空间不足，请清理后再来。");
        cm.dispose();
        return;
    }
    cm.gainItem(FRAGMENT_ID, -totalCost);
    cm.gainItem(scrollId, exchangeQty);
    cm.sendOk(
        "#b兑换成功！#k\r\n\r\n获得：#v" + scrollId + "# #b#z" + scrollId + "##k ×" + exchangeQty + "\r\n" +
        "消耗：#r" + totalCost + "个 " + FRAGMENT_NAME + "#k\r\n" +
        "剩余碎片：#b" + cm.getItemQuantity(FRAGMENT_ID) + "个#k"
    );
    cm.dispose();
}

// ==================== 3. 卷轴购买 ====================

function showBuyList() {
    var balance = cm.getPlayer().getCashShop().getCash(CASH_NX_CREDIT);
    var text = "#e卷轴购买#n  (点卷购买)\r\n\r\n";
    text += "当前点卷余额：#r" + balance + "#k\r\n\r\n";
    for (var i = 0; i < BUY_LIST.length; i++) {
        var item = BUY_LIST[i];
        text += "#L" + i + "##v" + item.代码 + "# #b" + item.名称 + "#k  单价：#r" + item.价格 + "点卷#k#l\r\n";
    }
    cm.sendSimple(text);
}

function handleBuyListSelect(selection) {
    buyIdx = selection;
    if (buyIdx < 0 || buyIdx >= BUY_LIST.length) { cm.dispose(); return; }
    var item = BUY_LIST[buyIdx];
    screen = "buy_qty";
    cm.sendGetNumber(
        "#v" + item.代码 + "# #b" + item.名称 + "#k\r\n\r\n单价：#r" + item.价格 + "点卷#k\r\n\r\n请输入购买数量：",
        1, 1, 999
    );
}

function handleBuyQty(selection) {
    buyQty = selection;
    if (buyQty < 1) { cm.dispose(); return; }
    var item = BUY_LIST[buyIdx];
    var totalPrice = item.价格 * buyQty;
    var balance = cm.getPlayer().getCashShop().getCash(CASH_NX_CREDIT);
    screen = "buy_confirm";
    cm.sendYesNo(
        "#e购买确认#n\r\n\r\n" +
        "道具：#b" + item.名称 + "#k ×" + buyQty + "\r\n" +
        "总价：#r" + totalPrice + "点卷#k\r\n" +
        "当前余额：#b" + balance + "点卷#k\r\n\r\n确认购买？"
    );
}

function doBuy() {
    var item = BUY_LIST[buyIdx];
    if (!item || buyQty < 1) { cm.dispose(); return; }
    var totalPrice = item.价格 * buyQty;
    var cashShop = cm.getPlayer().getCashShop();
    var balance = cashShop.getCash(CASH_NX_CREDIT);
    if (balance < totalPrice) {
        cm.sendOk("点卷余额不足，无法购买。\r\n需要：#r" + totalPrice + "点卷#k\r\n当前余额：#r" + balance + "点卷#k");
        cm.dispose();
        return;
    }
    if (!cm.canHold(item.代码, buyQty)) {
        cm.sendOk("背包空间不足，请清理背包后再来购买。");
        cm.dispose();
        return;
    }
    cashShop.gainCash(CASH_NX_CREDIT, -totalPrice);
    cm.gainItem(item.代码, buyQty);
    cm.sendOk("购买成功！获得 #b" + item.名称 + "#k ×" + buyQty + "\r\n已扣除 #r" + totalPrice + "点卷#k");
    cm.dispose();
}
