/*
    卷轴兑换脚本 — 用卷轴碎片按不同比例兑换卷轴
    相同碎片价格的卷轴归为一类，先选价格档 → 再选具体卷轴 → 输入数量 → 兑换
    兑换列表由 ScrollDecomposeManager 从 Web 配置页动态读取

    ========== 配置项 ==========
**/
var ScrollDecomposeManager = Java.type("org.gms.config.ScrollDecomposeManager");
var status = 0;
var costGroups = [];       // buildCostGroups()自动生成
var selectedGroupIdx = -1;
var selectedItemIdx = -1;
var exchangeQty = 0;

// ==================== 消耗货币 ====================
var CURRENCY_ITEM_ID   = 4001136;
var CURRENCY_ITEM_NAME = "卷轴碎片";

// ==================== UI文案 ====================
var TITLE_TEXT     = "#e#d卷轴兑换中心#n#k";
var CANCEL_TEXT    = "欢迎再来！";
var CONFIRM_TITLE  = "#e兑换确认#n";

// ==================== 逻辑代码 ====================

function start() {
    status = -1;
    costGroups = buildCostGroups();
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0 && status >= 0) { cm.sendOk(CANCEL_TEXT); cm.dispose(); return; }
    if (mode == 1) status++; else status--;

    if (status == 0) {
        // ===== 价格档列表 =====
        var owned = cm.getItemQuantity(CURRENCY_ITEM_ID);
        var text = TITLE_TEXT + "\r\n\r\n";
        text += "#v" + CURRENCY_ITEM_ID + "# #b" + CURRENCY_ITEM_NAME + "#k  拥有：#r" + owned + "个#k\r\n\r\n";
        text += "#e#d选择兑换价格档：#n#k\r\n\r\n";

        for (var g = 0; g < costGroups.length; g++) {
            var group = costGroups[g];
            var canAfford = owned >= group.cost;
            text += "#L" + g + "#";
            text += (canAfford ? "" : "#d[不足]#k ");
            text += "#b" + group.cost + "碎片#k  —  #d" + group.items.length + "种卷轴#k";
            text += "#l\r\n";
        }
        cm.sendSimple(text);

    } else if (status == 1) {
        // ===== 该价格档内的卷轴列表 =====
        selectedGroupIdx = selection;
        if (selectedGroupIdx < 0 || selectedGroupIdx >= costGroups.length) { cm.dispose(); return; }
        var group = costGroups[selectedGroupIdx];
        var owned = cm.getItemQuantity(CURRENCY_ITEM_ID);

        var text = TITLE_TEXT + "\r\n\r\n";
        text += "#b" + group.cost + "碎片/个#k  |  拥有：#r" + owned + "个#k\r\n\r\n";

        for (var i = 0; i < group.items.length; i++) {
            var id = group.items[i];
            text += "#L" + i + "##v" + id + "# #b#z" + id + "##k#l\r\n";
        }
        cm.sendSimple(text);

    } else if (status == 2) {
        // ===== 输入数量 =====
        selectedItemIdx = selection;
        var group = costGroups[selectedGroupIdx];
        if (selectedItemIdx < 0 || selectedItemIdx >= group.items.length) { cm.dispose(); return; }
        var scrollId = group.items[selectedItemIdx];
        var owned = cm.getItemQuantity(CURRENCY_ITEM_ID);
        var maxQty = Math.floor(owned / group.cost);
        if (maxQty <= 0) {
            cm.sendOk("碎片不足！\r\n\r\n需要 #r" + group.cost + "个 " + CURRENCY_ITEM_NAME + "#k，只有 #b" + owned + "个#k。");
            cm.dispose(); return;
        }
        maxQty = Math.min(maxQty, 999);
        cm.sendGetNumber(
            "#v" + scrollId + "# #b#z" + scrollId + "##k\r\n\r\n" +
            "单价：#r" + group.cost + "个 " + CURRENCY_ITEM_NAME + "#k\r\n" +
            "拥有：#r" + owned + "个 " + CURRENCY_ITEM_NAME + "#k\r\n" +
            "最大可兑：#r" + maxQty + "个#k\r\n\r\n" +
            "请输入兑换数量：",
            1, 1, maxQty
        );

    } else if (status == 3) {
        // ===== 确认 =====
        exchangeQty = selection;
        if (exchangeQty < 1) { cm.dispose(); return; }
        var group = costGroups[selectedGroupIdx];
        var scrollId = group.items[selectedItemIdx];
        var totalCost = group.cost * exchangeQty;
        var owned = cm.getItemQuantity(CURRENCY_ITEM_ID);

        cm.sendYesNo(
            CONFIRM_TITLE + "\r\n\r\n" +
            "卷轴：#b#z" + scrollId + "##k\r\n" +
            "数量：#r" + exchangeQty + "个#k\r\n" +
            "消耗：#r" + totalCost + "个 " + CURRENCY_ITEM_NAME + "#k\r\n" +
            "拥有：#b" + owned + "个#k\r\n\r\n" +
            "确认兑换？"
        );

    } else if (status == 4) {
        // ===== 执行 =====
        var group = costGroups[selectedGroupIdx];
        var scrollId = group.items[selectedItemIdx];
        var totalCost = group.cost * exchangeQty;
        var owned = cm.getItemQuantity(CURRENCY_ITEM_ID);

        if (owned < totalCost) {
            cm.sendOk("碎片不足！需要 #r" + totalCost + "个#k，只有 #b" + owned + "个#k。");
            cm.dispose(); return;
        }
        if (!cm.canHold(scrollId, exchangeQty)) {
            cm.sendOk("背包空间不足，请清理后再来。");
            cm.dispose(); return;
        }

        cm.gainItem(CURRENCY_ITEM_ID, -totalCost);
        cm.gainItem(scrollId, exchangeQty);

        cm.sendOk(
            "#b兑换成功！#k\r\n\r\n" +
            "获得：#v" + scrollId + "# #b#z" + scrollId + "##k ×" + exchangeQty + "\r\n" +
            "消耗：#r" + totalCost + "个 " + CURRENCY_ITEM_NAME + "#k\r\n" +
            "剩余碎片：#b" + cm.getItemQuantity(CURRENCY_ITEM_ID) + "个#k"
        );
        cm.dispose();
    }
}

// ==================== 辅助函数 ====================

/** 从 ScrollDecomposeManager 读取已启用的兑换配置，按cost分组并升序排列 */
function buildCostGroups() {
    // 从Java配置管理器读取已启用的兑换列表（已按cost升序排序）
    var exchangeList = ScrollDecomposeManager.getEnabledExchangeList();
    if (exchangeList.isEmpty()) {
        return [];
    }
    var map = {};
    for (var i = 0; i < exchangeList.size(); i++) {
        var ex = exchangeList.get(i);
        var key = String(ex.getCost());
        if (!map[key]) {
            map[key] = { cost: ex.getCost(), items: [] };
        }
        map[key].items.push(ex.getScrollId());
    }
    var result = [];
    for (var k in map) {
        result.push(map[k]);
    }
    result.sort(function(a, b) { return a.cost - b.cost; });
    return result;
}
