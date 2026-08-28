/*
    卷轴分解脚本 — 将消耗栏中的卷轴分解为卷轴碎片（4001136）
    选择卷轴 → 输入数量 → 确认 → 批量获得等量碎片 → 回到列表（可连续分解）
    无需手续费
    白名单由 ScrollDecomposeManager（Web配置页）控制

    ========== 配置项 ==========
**/
var ScrollDecomposeManager = Java.type("org.gms.config.ScrollDecomposeManager");
var status = 0;
var scrollList = [];        // [{itemId, qty, rate}]
var selectedIdx = -1;       // 选中的卷轴索引
var dismantleQty = 0;       // 输入的要分解的数量
var currentPage = 0;        // 当前页码

// ==================== 识别与产出配置 ====================
var REWARD_ITEM_ID   = 4001136;
var REWARD_ITEM_NAME = "卷轴碎片";

// ==================== 分页配置 ====================
var PAGE_SIZE = 8;

// ==================== UI文案 ====================
var TITLE_TEXT      = "#e#d卷轴分解机#n#k";
var CANCEL_TEXT     = "欢迎再来！";
var NO_SCROLL_TEXT  = "消耗栏中没有可分解的卷轴。请先在后台配置白名单。";
var CONFIRM_TITLE   = "#e分解确认#n";
var SEL_PREV = 9998;
var SEL_NEXT = 9999;

// ==================== 逻辑代码 ====================

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0 && status >= 0) { cm.sendOk(CANCEL_TEXT); cm.dispose(); return; }
    if (mode == 1) status++; else status--;

    if (status == 0) {
        // ===== 主菜单 =====
        var text = TITLE_TEXT + "\r\n\r\n";
        text += "#b扫描消耗栏中白名单内的卷轴，选择后输入数量进行批量分解。#k\r\n";
        text += "每分解 #b1个#k 卷轴 → 获得 #r1个#k #v" + REWARD_ITEM_ID + "# #b" + REWARD_ITEM_NAME + "#k\r\n";
        text += "#d无需手续费#k\r\n\r\n";
        text += "#d是否开始扫描消耗栏中的卷轴？#k";
        cm.sendYesNo(text);

    } else if (status == 1) {
        // ===== 扫描并展示首页 =====
        scanScrolls();
        if (scrollList.length == 0) { cm.sendOk(NO_SCROLL_TEXT); cm.dispose(); return; }
        currentPage = 0;
        showPage();

    } else if (status == 2) {
        // ===== 翻页 or 选卷轴 → 输入数量 =====
        if (selection == SEL_PREV) {
            currentPage--; status = 1; showPage();
        } else if (selection == SEL_NEXT) {
            currentPage++; status = 1; showPage();
        } else {
            selectedIdx = selection;
            if (selectedIdx < 0 || selectedIdx >= scrollList.length) { cm.dispose(); return; }
            var si = scrollList[selectedIdx];
            cm.sendGetNumber(
                "#v" + si.itemId + "# #b#z" + si.itemId + "##k  #d[" + si.rate + "]#k\r\n\r\n" +
                "库存：#r" + si.qty + "个#k\r\n" +
                "产出：#v" + REWARD_ITEM_ID + "# #b" + REWARD_ITEM_NAME + "#k （1:1）\r\n\r\n" +
                "请输入要分解的数量：",
                1, 1, Math.min(si.qty, 999)
            );
        }

    } else if (status == 3) {
        // ===== 确认批量分解 =====
        dismantleQty = selection;  // sendGetNumber返回的数量
        if (dismantleQty < 1) { cm.dispose(); return; }
        var si = scrollList[selectedIdx];
        if (dismantleQty > si.qty) dismantleQty = si.qty;
        cm.sendYesNo(
            CONFIRM_TITLE + "\r\n\r\n" +
            "卷轴：#b#z" + si.itemId + "##k  #d[" + si.rate + "]#k\r\n" +
            "库存：#r" + si.qty + "个#k\r\n" +
            "分解数量：#r" + dismantleQty + "个#k\r\n\r\n" +
            "将获得：#v" + REWARD_ITEM_ID + "# #b" + dismantleQty + "个 " + REWARD_ITEM_NAME + "#k\r\n\r\n" +
            "确认分解？"
        );

    } else if (status == 4) {
        // ===== 执行批量分解 → 回到列表 =====
        if (selectedIdx < 0 || selectedIdx >= scrollList.length || dismantleQty < 1) { cm.dispose(); return; }
        var si = scrollList[selectedIdx];

        var owned = cm.getItemQuantity(si.itemId);
        if (owned < dismantleQty) {
            cm.sendOk("该卷轴数量不足！库存 #b" + owned + "个#k，需要 #r" + dismantleQty + "个#k。");
            cm.dispose(); return;
        }

        // 批量扣除 + 发放
        cm.gainItem(si.itemId, -dismantleQty);
        cm.gainItem(REWARD_ITEM_ID, dismantleQty);

        scanScrolls();
        if (scrollList.length == 0) {
            cm.sendOk("分解成功！\r\n分解 #b" + dismantleQty + "个#k 卷轴 → 获得 #v" + REWARD_ITEM_ID + "# #b" + dismantleQty + "个 " + REWARD_ITEM_NAME + "#k。\r\n\r\n消耗栏中已没有可分解的卷轴。");
            cm.dispose(); return;
        }

        currentPage = 0;
        status = 1;
        showPage();
    }
}

// ==================== 辅助函数 ====================

function scanScrolls() {
    scrollList = [];
    var iter = cm.getInventory(2).list().iterator();
    while (iter.hasNext()) {
        var item = iter.next();
        var itemId = item.getItemId();
        // 白名单检查：只有Web配置中启用且ID在白名单中的卷轴才能被分解
        if (!ScrollDecomposeManager.canDecompose(itemId)) continue;
        scrollList.push({ itemId: itemId, qty: item.getQuantity(), rate: getScrollRate(itemId) });
    }
}

function getScrollRate(itemId) {
    try {
        var IIP = Java.type('org.gms.server.ItemInformationProvider');
        var name = IIP.getInstance().getName(itemId);
        if (name == null) return "";
        if (name.indexOf("100%") >= 0) return "100%";
        if (name.indexOf("60%") >= 0 || name.indexOf("70%") >= 0) return "60%";
        if (name.indexOf("10%") >= 0 || name.indexOf("30%") >= 0) return "10%";
        return "";
    } catch (e) { return ""; }
}

function showPage() {
    var totalPages = Math.ceil(scrollList.length / PAGE_SIZE);
    if (currentPage >= totalPages) currentPage = totalPages - 1;
    if (currentPage < 0) currentPage = 0;

    var startIdx = currentPage * PAGE_SIZE;
    var endIdx = Math.min(startIdx + PAGE_SIZE, scrollList.length);

    var text = TITLE_TEXT + "  共#b" + scrollList.length + "#k种";
    text += "  (#b" + (currentPage + 1) + "/" + totalPages + "#k)\r\n";
    text += "产出：#v" + REWARD_ITEM_ID + "# #b" + REWARD_ITEM_NAME + "#k  1:1  #d免费#k\r\n\r\n";

    if (currentPage > 0) text += "#L" + SEL_PREV + "##b◀ 上一页#k#l\r\n";
    for (var i = startIdx; i < endIdx; i++) {
        var si = scrollList[i];
        text += "#L" + i + "##v" + si.itemId + "# #b#z" + si.itemId + "##k  #d[" + si.rate + "]#k  #r×" + si.qty + "#k#l\r\n";
    }
    if (currentPage < totalPages - 1) text += "#L" + SEL_NEXT + "##b▶ 下一页#k#l\r\n";

    cm.sendSimple(text);
}
