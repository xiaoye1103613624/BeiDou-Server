/**
 * @description 现金道具商店
 * 三个独立购买分类，各自维护独立的"道具+价格"列表，互不影响：
 *   1. 点券购买  —— 扣 CashShop.NX_CREDIT(1)
 *   2. 抵用券购买 —— 扣 CashShop.MAPLE_POINT(2)
 *   3. 现金购买（赞助/充值）—— 扣 CashShop.NX_PREPAID(4)
 * 不限购买次数/数量，余额足够即可反复购买。
 * 道具列表/价格请直接在下方三个数组中增删配置。
 */

// ==================== 货币类型常量（对应 CashShop.java） ====================
var CASH_NX_CREDIT = 1;   // 点券
var CASH_MAPLE_POINT = 2; // 抵用券
var CASH_NX_PREPAID = 4;  // 信用/现金（赞助充值）

// ==================== 商品列表配置（按需增删） ====================
// 格式：{ 代码: 道具ID, 价格: 数量, 名称: 显示名(可选，留空则显示道具ID) }
var 点券商品 = [
    // { 代码: 5060000, 价格: 100, 名称: "传送卷轴" },
    { 代码: 5520000, 价格: 10000, 名称: "点券" },
    // ---- 弓箭手职业箭矢(按品质分级定价: 强化<锋利/优质<钛质<白金) ----
    { 代码: 2060009, 价格: 50, 名称: "弓专用强化箭矢" },
    { 代码: 2061007, 价格: 50, 名称: "弩专用强化箭矢" },
    { 代码: 2060010, 价格: 100, 名称: "弓专用锋利箭矢" },
    { 代码: 2061006, 价格: 100, 名称: "弩专用优质箭矢" },
    { 代码: 2060011, 价格: 200, 名称: "弓专用钛质箭矢" },
    { 代码: 2061009, 价格: 200, 名称: "弩专用钛质箭矢" },
    { 代码: 2060013, 价格: 500, 名称: "白金箭矢" },
    { 代码: 2061012, 价格: 500, 名称: "白金短箭" },
];

var 抵用券商品 = [
    // { 代码: 5060000, 价格: 50, 名称: "传送卷轴" },
];

var 现金商品 = [
    // { 代码: 5060000, 价格: 10, 名称: "传送卷轴" },
    { 代码: 2614000, 价格: 10, 名称: "赞助" },
    { 代码: 2614001, 价格: 100, 名称: "赞助" },
    // ---- 弓箭手职业箭矢(按品质分级定价: 强化<锋利/优质<钛质<白金) ----
    { 代码: 2060009, 价格: 5, 名称: "弓专用强化箭矢" },
    { 代码: 2061007, 价格: 5, 名称: "弩专用强化箭矢" },
    { 代码: 2060010, 价格: 10, 名称: "弓专用锋利箭矢" },
    { 代码: 2061006, 价格: 10, 名称: "弩专用优质箭矢" },
    { 代码: 2060011, 价格: 20, 名称: "弓专用钛质箭矢" },
    { 代码: 2061009, 价格: 20, 名称: "弩专用钛质箭矢" },
    { 代码: 2060013, 价格: 50, 名称: "白金箭矢" },
    { 代码: 2061012, 价格: 50, 名称: "白金短箭" },
];

// 菜单选项
var SEL_RETURN = 0;
var SEL_POINT = 1;
var SEL_VOUCHER = 2;
var SEL_CASH = 3;

// 购买数量上限（单次输入数量的最大值，仅用于限制单次输入框范围，不限制总购买次数）
var MAX_BUY_QTY = 999;

// 临时保存"当前正在购买的商品"，用于数量输入回调时取回（同一次对话内有效）
var 当前购买 = null; // { list, cashType, 货币名, backToList, item }

// ==================== 入口 ====================

function start() {
    levelMain();
}

function levelMain() {
    var text = "#e现金道具商店#n\r\n\r\n";
    text += "#L" + SEL_POINT + "##b点券购买#k#l\r\n";
    text += "#L" + SEL_VOUCHER + "##b抵用券购买#k#l\r\n";
    text += "#L" + SEL_CASH + "##b现金购买(赞助/充值)#k#l\r\n\r\n";
    text += "#L" + SEL_RETURN + "##g返回上一页#k#l\r\n";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection === SEL_RETURN) {
        cm.dispose();
        cm.openNpc(9900001);
        return;
    }
    if (selection === SEL_POINT) {
        levelListPoint();
        return;
    }
    if (selection === SEL_VOUCHER) {
        levelListVoucher();
        return;
    }
    if (selection === SEL_CASH) {
        levelListCash();
        return;
    }
    levelMain();
}

// ==================== 点券购买 ====================

function levelListPoint() {
    showList(点券商品, "点券", "ListPoint");
}

function levelHandleListPoint(selection) {
    handleListSelection(选项点击(selection), 点券商品, CASH_NX_CREDIT, "点券", levelListPoint);
}

// ==================== 抵用券购买 ====================

function levelListVoucher() {
    showList(抵用券商品, "抵用券", "ListVoucher");
}

function levelHandleListVoucher(selection) {
    handleListSelection(选项点击(selection), 抵用券商品, CASH_MAPLE_POINT, "抵用券", levelListVoucher);
}

// ==================== 现金购买 ====================

function levelListCash() {
    showList(现金商品, "现金", "ListCash");
}

function levelHandleListCash(selection) {
    handleListSelection(选项点击(selection), 现金商品, CASH_NX_PREPAID, "现金", levelListCash);
}

// ==================== 公共逻辑 ====================

function 选项点击(selection) {
    return selection;
}

function showList(list, 货币名, levelPrefix) {
    if (list.length === 0) {
        cm.sendOkLevel("Main", "该分类暂未上架任何道具，请联系管理员配置。");
        return;
    }
    var text = "#e" + 货币名 + "购买#n\r\n\r\n";
    for (var i = 0; i < list.length; i++) {
        var item = list[i];
        var 显示名 = item.名称 ? item.名称 : ("#v" + item.代码 + ":##z" + item.代码 + "#");
        text += "#L" + (i + 1) + "#" + 显示名 + "  价格: #r" + item.价格 + " " + 货币名 + "#k#l\r\n";
    }
    text += "\r\n#L0##g返回上一页#k#l\r\n";
    // 路由名必须是 "Handle"+前缀，引擎按 nextLevel 原样拼接"level"调用函数，并非自动加"Handle"
    cm.sendNextSelectLevel("Handle" + levelPrefix, text);
}

function handleListSelection(selection, list, cashType, 货币名, backToList) {
    if (selection === 0) {
        levelMain();
        return;
    }
    var item = list[selection - 1];
    if (!item) {
        backToList();
        return;
    }
    当前购买 = { cashType: cashType, 货币名: 货币名, item: item };
    var 显示名 = item.名称 ? item.名称 : ("#v" + item.代码 + ":##z" + item.代码 + "#");
    var msg = "您选择购买：" + 显示名 + "\r\n单价：#r" + item.价格 + "#k " + 货币名 + "\r\n请输入购买数量：";
    cm.getInputNumberLevel("Buy", msg, 1, 1, MAX_BUY_QTY);
}

function levelBuy(inputNum) {
    if (!当前购买) {
        levelMain();
        return;
    }
    var item = 当前购买.item;
    var cashType = 当前购买.cashType;
    var 货币名= 当前购买.货币名;
    当前购买 = null;

    var qty = parseInt(inputNum, 10);
    if (isNaN(qty) || qty <= 0) {
        cm.sendOkLevel("Main", "购买数量不合法。");
        return;
    }

    var 显示名 = item.名称 ? item.名称 : ("#v" + item.代码 + ":##z" + item.代码 + "#");
    var totalPrice = item.价格 * qty;

    var cashShop = cm.getPlayer().getCashShop();
    var balance = cashShop.getCash(cashType);
    if (balance < totalPrice) {
        cm.sendOkLevel("Main", "您的" + 货币名 + "余额不足，无法购买。\r\n需要：#r" + totalPrice + "#k " + 货币名 + "\r\n当前余额：#r" + balance + "#k " + 货币名);
        return;
    }
    if (!cm.canHold(item.代码, qty)) {
        cm.sendOkLevel("Main", "背包空间不足，请清理背包后再来购买。");
        return;
    }
    cashShop.gainCash(cashType, -totalPrice);
    cm.gainItem(item.代码, qty);
    cm.sendOkLevel("Main", "购买成功！获得 " + 显示名 + " ×" + qty + "\r\n已扣除 #r" + totalPrice + "#k " + 货币名);
}
