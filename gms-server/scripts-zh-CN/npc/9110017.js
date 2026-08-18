// 自由市场 · 飞天猪 9110017 · 点券抽奖
// 单抽/十连 1000点券一次；查看奖池（椅子类）

var CASH_NX = 1;
var COST = 1000;
var PAGE_SIZE = 12;
var SEL_SINGLE = 1;
var SEL_TEN = 2;
var SEL_POOL = 3;
var SEL_PREV = 997;
var SEL_NEXT = 998;
var SEL_BACK = 999;

var status = 0;
var currentPage = 0;
var chairPool = null;
var drawTimes = 1;

function getChairPool() {
    if (chairPool !== null) {
        return chairPool;
    }
    chairPool = [];
    var provider = Java.type("org.gms.server.ItemInformationProvider").getInstance();
    var allItems = provider.getAllItems();
    for (var i = 0; i < allItems.size(); i++) {
        var pair = allItems.get(i);
        var id = pair.getLeft();
        if (Math.floor(id / 10000) === 301) {
            chairPool.push([id, pair.getRight()]);
        }
    }
    return chairPool;
}

function start() {
    status = 0;
    currentPage = 0;
    drawTimes = 1;
    action(1, 0, 0);
}

function showMain() {
    var pool = getChairPool();
    var balance = cm.getPlayer().getCashShop().getCash(CASH_NX);
    var text = "#e飞天猪 · 点券抽奖#n\r\n\r\n";
    text += "每次消耗 #r" + COST + "#k 点券，随机获得奖池内一把椅子。\r\n";
    text += "奖池数量：#b" + pool.length + "#k　点券余额：#b" + balance + "#k\r\n\r\n";
    text += "#L" + SEL_SINGLE + "##b开始抽奖 - 单抽（" + COST + "点券）#k#l\r\n";
    text += "#L" + SEL_TEN + "##b开始抽奖 - 十连（" + (COST * 10) + "点券）#k#l\r\n";
    text += "#L" + SEL_POOL + "##b查看奖池#k#l\r\n";
    cm.sendSimple(text);
}

function showPoolPage() {
    var pool = getChairPool();
    var total = pool.length;
    var totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    if (currentPage < 0) {
        currentPage = 0;
    }
    if (currentPage > totalPages - 1) {
        currentPage = totalPages - 1;
    }
    var startIdx = currentPage * PAGE_SIZE;
    var endIdx = Math.min(startIdx + PAGE_SIZE, total);
    var text = "#e飞天猪奖池#n　共 #r" + total + "#k 种";
    if (totalPages > 1) {
        text += "（" + (currentPage + 1) + "/" + totalPages + "）";
    }
    text += "\r\n#g----------------------------------------------\r\n";
    for (var i = startIdx; i < endIdx; i++) {
        var id = pool[i][0];
        text += "#v" + id + "# #z" + id + "#\r\n";
    }
    text += "#g----------------------------------------------\r\n";
    if (currentPage > 0) {
        text += "#L" + SEL_PREV + "#◀ 上一页#l  ";
    }
    if (currentPage < totalPages - 1) {
        text += "#L" + SEL_NEXT + "#下一页 ▶#l";
    }
    text += "\r\n#L" + SEL_BACK + "##b返回#k#l";
    cm.sendSimple(text);
}

function doDraw(times) {
    var pool = getChairPool();
    if (pool.length === 0) {
        cm.sendOk("奖池为空，请联系管理员。");
        cm.dispose();
        return;
    }
    var totalCost = COST * times;
    var balance = cm.getPlayer().getCashShop().getCash(CASH_NX);
    if (balance < totalCost) {
        cm.sendOk("点券不足。需要 #r" + totalCost + "#k，当前 #b" + balance + "#k。");
        cm.dispose();
        return;
    }
    for (var t = 0; t < times; t++) {
        var pick = pool[Math.floor(Math.random() * pool.length)][0];
        if (!cm.canHold(pick, 1)) {
            cm.sendOk("背包空间不足，已中止。\r\n已抽 #b" + t + "#k 次，点券未继续扣除。");
            cm.dispose();
            return;
        }
    }
    cm.getPlayer().getCashShop().gainCash(CASH_NX, -totalCost);
    var text = "#e抽奖结果#n（共 #r" + times + "#k 次）\r\n\r\n";
    for (var i = 0; i < times; i++) {
        var id = pool[Math.floor(Math.random() * pool.length)][0];
        cm.gainItem(id, 1);
        text += (i + 1) + ". #v" + id + "# #z" + id + "#\r\n";
    }
    text += "\r\n已扣除 #r" + totalCost + "#k 点券。";
    cm.sendOk(text);
    cm.dispose();
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    if (status === 0) {
        showMain();
        status = 1;
        return;
    }
    if (status === 1) {
        if (selection === SEL_SINGLE) {
            doDraw(1);
            return;
        }
        if (selection === SEL_TEN) {
            doDraw(10);
            return;
        }
        if (selection === SEL_POOL) {
            currentPage = 0;
            showPoolPage();
            status = 2;
            return;
        }
        cm.dispose();
        return;
    }
    if (status === 2) {
        if (selection === SEL_BACK) {
            status = 0;
            action(1, 0, 0);
            return;
        }
        if (selection === SEL_PREV) {
            currentPage--;
            showPoolPage();
            return;
        }
        if (selection === SEL_NEXT) {
            currentPage++;
            showPoolPage();
            return;
        }
        cm.dispose();
    }
}
