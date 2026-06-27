/**
 * @description 椅子抽奖
 * 1.开始抽奖：消耗1000点券，随机获得一把椅子（坐具）
 * 2.椅子列表：分页展示当前奖池内的全部椅子，紧凑形式，带小图标
 *
 * 奖池来源：动态扫描 ItemInformationProvider.getAllItems()，
 * 过滤出 itemId/10000 == 301（椅子分类码）的物品，
 * 这样后续新增/移除椅子无需改动本脚本。
 */

var CASH_NX_CREDIT = 1;   // 点券（对应 CashShop.java）
var COST = 1000;          // 每次抽奖花费点券

var PAGE_SIZE = 12;       // 列表每页展示数量（带图标，紧凑显示）
var SEL_DRAW = 1;
var SEL_LIST = 2;
var SEL_PREV = 997;
var SEL_NEXT = 998;
var SEL_BACK = 999;

var status = 0;
var currentPage = 0;
var chairPool = null;     // [[itemId, name], ...] 懒加载缓存

// ── 扫描奖池：所有椅子分类(301xxxxx)的物品 ──
function getChairPool() {
    if (chairPool !== null) return chairPool;
    chairPool = [];
    var provider = Java.type('org.gms.server.ItemInformationProvider').getInstance();
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
    action(1, 0, 0);
}

function showMain() {
    var pool = getChairPool();
    var balance = cm.getPlayer().getCashShop().getCash(CASH_NX_CREDIT);
    var text = "#e椅子抽奖#n\r\n\r\n";
    text += "每次抽奖消耗 #r" + COST + "#k 点券，随机获得奖池内的一把椅子。\r\n";
    text += "当前奖池椅子数：#b" + pool.length + "#k 把\r\n";
    text += "您的点券余额：#b" + balance + "#k\r\n\r\n";
    text += "#L" + SEL_DRAW + "##b开始抽奖（" + COST + "点券/次）#k#l\r\n";
    text += "#L" + SEL_LIST + "##b查看椅子列表#k#l\r\n";
    cm.sendSimple(text);
}

// ── 椅子列表：分页、紧凑、带小图标 ──
function showListPage() {
    var pool = getChairPool();
    var total = pool.length;
    var totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    if (currentPage < 0) currentPage = 0;
    if (currentPage > totalPages - 1) currentPage = totalPages - 1;
    var pageStart = currentPage * PAGE_SIZE;
    var pageEnd = Math.min(pageStart + PAGE_SIZE, total);

    var text = "#e椅子列表#n　共 #r" + total + "#k 把";
    if (totalPages > 1) text += "（第 #r" + (currentPage + 1) + "#k / " + totalPages + " 页）";
    text += "\r\n#g----------------------------------------------\r\n";

    for (var i = pageStart; i < pageEnd; i++) {
        var id = pool[i][0];
        var name = pool[i][1];
        // 紧凑单行：小图标 + 名称 + ID（图标语法参考 现金商店.js：#v物品ID:##z物品ID#）
        text += "#v" + id + ":##z" + id + "#  " + name + "（" + id + "）\r\n";
    }

    text += "#g----------------------------------------------\r\n";
    var navLine = "";
    if (currentPage > 0) navLine += "#L" + SEL_PREV + "# < 上一页#l";
    if (currentPage < totalPages - 1) navLine += (navLine ? "    " : "") + "#L" + SEL_NEXT + "# 下一页 >#l";
    if (navLine) text += navLine + "\r\n";
    text += "#L" + SEL_BACK + "##g返回上一页#k#l\r\n";

    cm.sendSimple(text);
}

function doDraw() {
    var balance = cm.getPlayer().getCashShop().getCash(CASH_NX_CREDIT);
    if (balance < COST) {
        cm.sendOk("您的点券不足，无法抽奖。\r\n需要：#r" + COST + "#k 点券\r\n当前余额：#r" + balance + "#k 点券");
        cm.dispose();
        return;
    }
    var pool = getChairPool();
    if (pool.length === 0) {
        cm.sendOk("奖池目前没有可用的椅子，请联系管理员配置。");
        cm.dispose();
        return;
    }
    var idx = Math.floor(Math.random() * pool.length);
    var id = pool[idx][0];
    var name = pool[idx][1];

    if (!cm.canHold(id)) {
        cm.sendOk("背包空间不足，请清理背包后再来抽奖。");
        cm.dispose();
        return;
    }

    try {
        cm.getPlayer().getCashShop().gainCash(CASH_NX_CREDIT, -COST);
        cm.gainItem(id, 1);
        cm.sendOk("恭喜，抽到了：#v" + id + "##z" + id + "#\r\n#b" + name + "#k（" + id + "）\r\n已扣除 #r" + COST + "#k 点券。");
    } catch (e) {
        cm.sendOk("该椅子数据异常，未能发放，点券未扣除，请联系管理员。\r\n（" + name + " / " + id + "）");
    }
    cm.dispose();
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }

    if (status == 0) {
        showMain();
        status = 1;
        return;
    }

    if (status == 1) {
        if (selection == SEL_DRAW) {
            doDraw();
            return;
        } else if (selection == SEL_LIST) {
            currentPage = 0;
            status = 2;
            showListPage();
            return;
        }
        cm.dispose();
        return;
    }

    if (status == 2) {
        if (selection == SEL_NEXT) {
            currentPage++;
            showListPage();
            return;
        } else if (selection == SEL_PREV) {
            currentPage--;
            showListPage();
            return;
        } else if (selection == SEL_BACK) {
            status = 1;
            showMain();
            return;
        }
        showListPage();
        return;
    }

    cm.dispose();
}
