// 匠人街 · 分解机器 (NPC 9031011)
// 卷轴分解 / 卷轴兑换 / 装备分解 / 时装分解 / 天赋分解

var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var InventoryManipulator = Java.type("org.gms.client.inventory.manipulator.InventoryManipulator");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var TalentId = Java.type("org.gms.talent.TalentId");
var TalentTier = Java.type("org.gms.talent.TalentTier");
var TalentConfig = Java.type("org.gms.talent.TalentConfig");
var Equip = Java.type("org.gms.client.inventory.Equip");

var status = -1;
var feature = 0; // 1卷轴分解 2卷轴兑换 3装备分解 4时装分解 5天赋分解

var PAGE_SIZE = 8;
var SEL_BACK = 9000;
var SEL_ONECLICK = 9001;
var SEL_PREV = 9002;
var SEL_NEXT = 9003;
var SEL_DO_ONECLICK = 9004;
var SEL_TOGGLE_FRONT = 9005;
var SEL_TOGGLE_BACK = 9006;

var SCROLL_FRAG = 4001136;
var EQUIP_STONE = 4032171;
var FASHION_NX = 300;
var EXCHANGE_COST = 10;

// 083 基础 10% 卷轴（success=10），用于兑换目录
var SCROLL_10PCT = [
    2040002, 2040005, 2040026, 2040031, 2040100, 2040105, 2040200, 2040205,
    2040302, 2040310, 2040318, 2040323, 2040328, 2040329, 2040330, 2040331,
    2040402, 2040412, 2040419, 2040422, 2040427, 2040502, 2040505, 2040514,
    2040517, 2040534, 2040602, 2040612, 2040619, 2040622, 2040627, 2040702,
    2040705, 2040708, 2040727, 2040802, 2040805, 2040816, 2040825, 2040902,
    2040915, 2040920, 2040925, 2040928, 2040933, 2041002, 2041005, 2041008,
    2041011, 2041014, 2041017, 2041020, 2041023, 2041058, 2041102, 2041105,
    2041108, 2041111, 2041302, 2041305, 2041308, 2041311, 2043002, 2043008,
    2043019, 2043102, 2043114, 2043202, 2043214, 2043302, 2043702, 2043802,
    2044002, 2044014, 2044015, 2044102, 2044114, 2044202, 2044214, 2044302,
    2044314, 2044402, 2044414, 2044502, 2044602, 2044702, 2044802, 2044809,
    2044902, 2048002, 2048005, 2040760
];

var list = [];
var currentPage = 0;
var selectedIdx = -1;
var inputQty = 0;
var keepFront = true;
var keepBack = true;

function start() {
    status = -1;
    feature = 0;
    list = [];
    currentPage = 0;
    selectedIdx = -1;
    inputQty = 0;
    keepFront = true;
    keepBack = true;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        if (status <= 0) {
            cm.dispose();
            return;
        }
        // 任意取消回到主菜单
        status = -1;
        feature = 0;
        action(1, 0, 0);
        return;
    }
    status++;

    if (feature === 0) {
        handleMain(selection);
    } else if (feature === 1) {
        handleScrollDecomp(selection);
    } else if (feature === 2) {
        handleScrollExchange(selection);
    } else if (feature === 3) {
        handleEquipDecomp(selection);
    } else if (feature === 4) {
        handleFashion(selection);
    } else if (feature === 41) {
        handleFashionOneClick(selection);
    } else if (feature === 5) {
        handleTalent(selection);
    } else {
        cm.dispose();
    }
}

// ==================== 主菜单 ====================

function handleMain(selection) {
    if (status === 0) {
        var text = "#e#b<材料大师 · 蒙斯>#k#n\r\n";
        text += "冒险家你好，我是材料大师蒙斯，我可以帮助分解各种物品，帮助你成为这个冒险世界中的强者。\r\n\r\n";
        text += "#L1#卷轴分解#l 「将您的卷轴分解为卷轴残卷。」\r\n";
        text += "#L2#卷轴兑换#l 「将您的卷轴残卷兑换为卷轴。」\r\n";
        text += "#L3#装备分解#l 「可以将特定的装备分解为强化石。」\r\n";
        text += "#L4#时装分解#l 「可以将特定的时装分解为点券。」\r\n";
        text += "#L5#天赋分解#l 「可以将天赋书分解为兑换材料。」\r\n";
        cm.sendSimple(text);
        return;
    }
    if (status === 1) {
        feature = selection;
        status = 0;
        if (feature === 1) {
            handleScrollDecomp(0);
        } else if (feature === 2) {
            handleScrollExchange(0);
        } else if (feature === 3) {
            handleEquipDecomp(0);
        } else if (feature === 4) {
            handleFashion(0);
        } else if (feature === 5) {
            handleTalent(0);
        } else {
            cm.dispose();
        }
    }
}

// ==================== 卷轴分解 ====================

function handleScrollDecomp(selection) {
    if (status === 0) {
        scanBasicScrolls();
        currentPage = 0;
        showScrollDecompPage();
        return;
    }
    if (status === 1) {
        if (selection === SEL_BACK) {
            backToMain();
            return;
        }
        if (selection === SEL_PREV) {
            currentPage--;
            status = 0;
            showScrollDecompPage();
            return;
        }
        if (selection === SEL_NEXT) {
            currentPage++;
            status = 0;
            showScrollDecompPage();
            return;
        }
        if (selection === SEL_ONECLICK) {
            if (list.length === 0) {
                cm.sendOk("消耗栏中没有可分解的基础卷轴。");
                cm.dispose();
                return;
            }
            var total = 0;
            for (var i = 0; i < list.length; i++) {
                total += list[i].qty;
            }
            cm.sendYesNo(
                "#e一键分解确认#n\r\n\r\n" +
                "将分解列表中全部 #r" + total + "#k 个基础卷轴\r\n" +
                "获得 #v" + SCROLL_FRAG + "# ×#b" + total + "#k\r\n\r\n确认？"
            );
            return;
        }
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= list.length) {
            cm.dispose();
            return;
        }
        var si = list[selectedIdx];
        cm.sendGetNumber(
            "#v" + si.itemId + "# #b#z" + si.itemId + "##k  #d[" + si.rate + "%]#k\r\n" +
            "库存：#r" + si.qty + "#k\r\n" +
            "产出：#v" + SCROLL_FRAG + "# （1:1）\r\n\r\n请输入分解数量：",
            1, 1, Math.min(si.qty, 999)
        );
        return;
    }
    if (status === 2) {
        // 一键 or 数量确认
        if (selectedIdx < 0) {
            // 一键确认后的 Yes
            doScrollOneClick();
            return;
        }
        inputQty = selection;
        if (inputQty < 1) {
            cm.dispose();
            return;
        }
        var si2 = list[selectedIdx];
        if (inputQty > si2.qty) {
            inputQty = si2.qty;
        }
        cm.sendYesNo(
            "#e分解确认#n\r\n\r\n" +
            "#v" + si2.itemId + "# #b#z" + si2.itemId + "##k ×#r" + inputQty + "#k\r\n" +
            "获得 #v" + SCROLL_FRAG + "# ×#b" + inputQty + "#k\r\n\r\n确认？"
        );
        return;
    }
    if (status === 3) {
        var si3 = list[selectedIdx];
        var owned = cm.getItemQuantity(si3.itemId);
        if (owned < inputQty) {
            cm.sendOk("数量不足。");
            cm.dispose();
            return;
        }
        if (!cm.canHold(SCROLL_FRAG, inputQty)) {
            cm.sendOk("其他栏空间不足，无法放入卷轴残卷。");
            cm.dispose();
            return;
        }
        cm.gainItem(si3.itemId, -inputQty);
        cm.gainItem(SCROLL_FRAG, inputQty);
        cm.sendOk("分解成功！获得 #v" + SCROLL_FRAG + "# ×#b" + inputQty + "#k");
        // 回到列表
        status = -1;
        selectedIdx = -1;
        feature = 1;
        action(1, 0, 0);
    }
}

function doScrollOneClick() {
    scanBasicScrolls();
    if (list.length === 0) {
        cm.sendOk("没有可分解的卷轴。");
        cm.dispose();
        return;
    }
    var total = 0;
    for (var i = 0; i < list.length; i++) {
        total += list[i].qty;
    }
    if (!cm.canHold(SCROLL_FRAG, total)) {
        cm.sendOk("其他栏空间不足，无法放入卷轴残卷。");
        cm.dispose();
        return;
    }
    for (var j = 0; j < list.length; j++) {
        var s = list[j];
        cm.gainItem(s.itemId, -s.qty);
    }
    cm.gainItem(SCROLL_FRAG, total);
    cm.sendOk("一键分解完成！共分解 #r" + total + "#k 个卷轴，获得 #v" + SCROLL_FRAG + "# ×#b" + total + "#k");
    cm.dispose();
}

function showScrollDecompPage() {
    var text = "#e#b[卷轴分解]#k#n  产出 #v" + SCROLL_FRAG + "# 1:1\r\n";
    text += "仅列出 083 基础卷轴（10% / 60% / 100%）\r\n\r\n";
    text += "#L" + SEL_BACK + "##b返回#k#l\r\n";
    text += "#L" + SEL_ONECLICK + "##r一键分解全部列表卷轴#k#l\r\n\r\n";
    if (list.length === 0) {
        text += "#d消耗栏中没有可分解的基础卷轴。#k";
        cm.sendSimple(text);
        return;
    }
    var totalPages = Math.ceil(list.length / PAGE_SIZE);
    if (currentPage >= totalPages) {
        currentPage = totalPages - 1;
    }
    if (currentPage < 0) {
        currentPage = 0;
    }
    text += "共 #b" + list.length + "#k 种  (#b" + (currentPage + 1) + "/" + totalPages + "#k)\r\n";
    if (currentPage > 0) {
        text += "#L" + SEL_PREV + "##b◀ 上一页#k#l\r\n";
    }
    var start = currentPage * PAGE_SIZE;
    var end = Math.min(start + PAGE_SIZE, list.length);
    for (var i = start; i < end; i++) {
        var si = list[i];
        text += "#L" + i + "##v" + si.itemId + "# #b#z" + si.itemId + "##k #d[" + si.rate + "%]#k #r×" + si.qty + "#k#l\r\n";
    }
    if (currentPage < totalPages - 1) {
        text += "#L" + SEL_NEXT + "##b▶ 下一页#k#l\r\n";
    }
    selectedIdx = -1;
    cm.sendSimple(text);
}

function scanBasicScrolls() {
    list = [];
    var ii = ItemInformationProvider.getInstance();
    var iter = cm.getInventory(2).list().iterator();
    while (iter.hasNext()) {
        var item = iter.next();
        var itemId = item.getItemId();
        var rate = getBasicScrollRate(ii, itemId);
        if (rate < 0) {
            continue;
        }
        list.push({ itemId: itemId, qty: item.getQuantity(), rate: rate });
    }
}

function getBasicScrollRate(ii, itemId) {
    if (Math.floor(itemId / 10000) !== 204) {
        return -1;
    }
    var stats = ii.getEquipStats(itemId);
    if (stats == null || !stats.containsKey("success")) {
        return -1;
    }
    var success = stats.get("success");
    var s = typeof success === "number" ? success : success.intValue();
    if (s !== 10 && s !== 60 && s !== 100) {
        return -1;
    }
    var name = ii.getName(itemId);
    if (name != null) {
        if (name.indexOf("黑暗") >= 0 || name.indexOf("混沌") >= 0 || name.indexOf("祝福") >= 0) {
            return -1;
        }
    }
    return s;
}

// ==================== 卷轴兑换 ====================

function handleScrollExchange(selection) {
    if (status === 0) {
        currentPage = 0;
        showScrollExchangePage();
        return;
    }
    if (status === 1) {
        if (selection === SEL_BACK) {
            backToMain();
            return;
        }
        if (selection === SEL_PREV) {
            currentPage--;
            status = 0;
            showScrollExchangePage();
            return;
        }
        if (selection === SEL_NEXT) {
            currentPage++;
            status = 0;
            showScrollExchangePage();
            return;
        }
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= SCROLL_10PCT.length) {
            cm.dispose();
            return;
        }
        var scrollId = SCROLL_10PCT[selectedIdx];
        var owned = cm.getItemQuantity(SCROLL_FRAG);
        var maxQty = Math.floor(owned / EXCHANGE_COST);
        if (maxQty <= 0) {
            cm.sendOk("卷轴残卷不足！需要 #r" + EXCHANGE_COST + "#k 个 #v" + SCROLL_FRAG + "# 兑换 1 个卷轴。\r\n当前拥有：#b" + owned + "#k");
            cm.dispose();
            return;
        }
        maxQty = Math.min(maxQty, 999);
        cm.sendGetNumber(
            "#v" + scrollId + "# #b#z" + scrollId + "##k\r\n" +
            "单价：#r" + EXCHANGE_COST + "#k 个 #v" + SCROLL_FRAG + "#\r\n" +
            "拥有残卷：#b" + owned + "#k\r\n最大可兑：#r" + maxQty + "#k\r\n\r\n请输入兑换数量：",
            1, 1, maxQty
        );
        return;
    }
    if (status === 2) {
        inputQty = selection;
        if (inputQty < 1) {
            cm.dispose();
            return;
        }
        var sid = SCROLL_10PCT[selectedIdx];
        var cost = EXCHANGE_COST * inputQty;
        cm.sendYesNo(
            "#e兑换确认#n\r\n\r\n" +
            "获得 #v" + sid + "# ×#b" + inputQty + "#k\r\n" +
            "消耗 #v" + SCROLL_FRAG + "# ×#r" + cost + "#k\r\n\r\n确认？"
        );
        return;
    }
    if (status === 3) {
        var sid2 = SCROLL_10PCT[selectedIdx];
        var cost2 = EXCHANGE_COST * inputQty;
        var owned2 = cm.getItemQuantity(SCROLL_FRAG);
        if (owned2 < cost2) {
            cm.sendOk("残卷不足。");
            cm.dispose();
            return;
        }
        if (!cm.canHold(sid2, inputQty)) {
            cm.sendOk("消耗栏空间不足。");
            cm.dispose();
            return;
        }
        cm.gainItem(SCROLL_FRAG, -cost2);
        cm.gainItem(sid2, inputQty);
        cm.sendOk("兑换成功！获得 #v" + sid2 + "# ×#b" + inputQty + "#k");
        status = -1;
        feature = 2;
        action(1, 0, 0);
    }
}

function showScrollExchangePage() {
    var owned = cm.getItemQuantity(SCROLL_FRAG);
    var text = "#e#b[卷轴兑换]#k#n\r\n";
    text += "#v" + SCROLL_FRAG + "# 拥有：#r" + owned + "#k\r\n";
    text += "仅可兑换 #b10%#k 基础卷轴，#r" + EXCHANGE_COST + "#k 残卷 = 1 卷轴\r\n\r\n";
    text += "#L" + SEL_BACK + "##b返回#k#l\r\n\r\n";
    var totalPages = Math.ceil(SCROLL_10PCT.length / PAGE_SIZE);
    if (currentPage >= totalPages) {
        currentPage = totalPages - 1;
    }
    if (currentPage < 0) {
        currentPage = 0;
    }
    text += "目录 (#b" + (currentPage + 1) + "/" + totalPages + "#k)\r\n";
    if (currentPage > 0) {
        text += "#L" + SEL_PREV + "##b◀ 上一页#k#l\r\n";
    }
    var start = currentPage * PAGE_SIZE;
    var end = Math.min(start + PAGE_SIZE, SCROLL_10PCT.length);
    for (var i = start; i < end; i++) {
        var id = SCROLL_10PCT[i];
        text += "#L" + i + "##v" + id + "# #b#z" + id + "##k  #d" + EXCHANGE_COST + "残卷#k#l\r\n";
    }
    if (currentPage < totalPages - 1) {
        text += "#L" + SEL_NEXT + "##b▶ 下一页#k#l\r\n";
    }
    cm.sendSimple(text);
}

// ==================== 装备分解（预留） ====================

function handleEquipDecomp(selection) {
    if (status === 0) {
        cm.sendOk(
            "#e#b[装备分解]#k#n\r\n\r\n" +
            "特定装备可分解为 #v" + EQUIP_STONE + "# #z" + EQUIP_STONE + "#（1件装备 → 1 个）。\r\n\r\n" +
            "#d可分解装备列表尚未配置，功能预留中。#k"
        );
        cm.dispose();
    }
}

// ==================== 时装分解 ====================

function handleFashion(selection) {
    if (status === 0) {
        scanPlainFashion();
        currentPage = 0;
        showFashionPage();
        return;
    }
    if (status === 1) {
        if (selection === SEL_BACK) {
            backToMain();
            return;
        }
        if (selection === SEL_PREV) {
            currentPage--;
            status = 0;
            showFashionPage();
            return;
        }
        if (selection === SEL_NEXT) {
            currentPage++;
            status = 0;
            showFashionPage();
            return;
        }
        if (selection === SEL_ONECLICK) {
            feature = 41;
            status = 0;
            showFashionOneClickPage();
            return;
        }
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= list.length) {
            cm.dispose();
            return;
        }
        var fi = list[selectedIdx];
        cm.sendYesNo(
            "#e时装分解确认#n\r\n\r\n" +
            "#v" + fi.itemId + "# #b#z" + fi.itemId + "##k\r\n" +
            "栏位：#d" + fi.slot + "#k\r\n" +
            "获得：#r" + FASHION_NX + "#k 点券\r\n\r\n确认分解？"
        );
        return;
    }
    if (status === 2) {
        var fi2 = list[selectedIdx];
        var inv = cm.getInventory(1);
        var item = inv.getItem(fi2.slot);
        if (item == null || item.getItemId() !== fi2.itemId) {
            cm.sendOk("物品已不存在。");
            cm.dispose();
            return;
        }
        if (fashionHasStats(item)) {
            cm.sendOk("该时装带有属性，无法分解。");
            cm.dispose();
            return;
        }
        InventoryManipulator.removeFromSlot(cm.getClient(), InventoryType.EQUIP, fi2.slot, 1, false);
        cm.getPlayer().getCashShop().gainCash(1, FASHION_NX);
        cm.sendOk("分解成功！获得 #r" + FASHION_NX + "#k 点券。");
        status = -1;
        feature = 4;
        action(1, 0, 0);
    }
}

function handleFashionOneClick(selection) {
    if (status === 0) {
        showFashionOneClickPage();
        return;
    }
    if (status === 1) {
        if (selection === SEL_BACK) {
            feature = 4;
            status = -1;
            action(1, 0, 0);
            return;
        }
        if (selection === SEL_TOGGLE_FRONT) {
            keepFront = !keepFront;
            status = 0;
            showFashionOneClickPage();
            return;
        }
        if (selection === SEL_TOGGLE_BACK) {
            keepBack = !keepBack;
            status = 0;
            showFashionOneClickPage();
            return;
        }
        if (selection === SEL_DO_ONECLICK) {
            cm.sendYesNo(
                "#e一键分解时装确认#n\r\n\r\n" +
                (keepFront ? "保留前24格；" : "") +
                (keepBack ? "保留后24格；" : "") +
                "\r\n自动跳过有属性时装。\r\n每件 #r" + FASHION_NX + "#k 点券。\r\n\r\n确认执行？"
            );
            return;
        }
        status = 0;
        showFashionOneClickPage();
    }
    if (status === 2) {
        doFashionOneClick();
    }
}

function showFashionPage() {
    var text = "#e#bMAPLESTORY - [时装分解]#k#n\r\n";
    text += "选择你要分解的时装吧（不含有属性时装）\r\n";
    text += "每件分解获得 #r" + FASHION_NX + "#k 点券\r\n\r\n";
    text += "#L" + SEL_BACK + "##b返回#k#l\r\n";
    text += "#L" + SEL_ONECLICK + "##r一键分解时装#k#l\r\n\r\n";
    if (list.length === 0) {
        text += "#d装备栏中没有可分解的无属性时装。#k";
        cm.sendSimple(text);
        return;
    }
    var totalPages = Math.ceil(list.length / PAGE_SIZE);
    if (currentPage >= totalPages) {
        currentPage = totalPages - 1;
    }
    if (currentPage < 0) {
        currentPage = 0;
    }
    text += "共 #b" + list.length + "#k 件  (#b" + (currentPage + 1) + "/" + totalPages + "#k)\r\n";
    if (currentPage > 0) {
        text += "#L" + SEL_PREV + "##b◀ 上一页#k#l\r\n";
    }
    var start = currentPage * PAGE_SIZE;
    var end = Math.min(start + PAGE_SIZE, list.length);
    for (var i = start; i < end; i++) {
        var fi = list[i];
        text += "#L" + i + "##v" + fi.itemId + "# #b#z" + fi.itemId + "##k #r每1/分解" + FASHION_NX + "点券#k#l\r\n";
    }
    if (currentPage < totalPages - 1) {
        text += "#L" + SEL_NEXT + "##b▶ 下一页#k#l\r\n";
    }
    cm.sendSimple(text);
}

function showFashionOneClickPage() {
    var text = "#e#b[一键分解-时装]#k#n\r\n";
    text += "[Tip]: 自动跳过有属性时装；勾选保留区不会被分解。\r\n\r\n";
    text += "#L" + SEL_DO_ONECLICK + "##r一键分解 [时装类]#k#l\r\n\r\n";
    text += "#L" + SEL_TOGGLE_FRONT + "#" + (keepFront ? "#e[√]#n" : "[ ]") + " 保留前24格#l\r\n";
    text += "#L" + SEL_TOGGLE_BACK + "#" + (keepBack ? "#e[√]#n" : "[ ]") + " 保留后24格#l\r\n\r\n";
    text += "#L" + SEL_BACK + "##b返回时装列表#k#l";
    cm.sendSimple(text);
}

function scanPlainFashion() {
    list = [];
    var ii = ItemInformationProvider.getInstance();
    var inv = cm.getInventory(1);
    var limit = inv.getSlotLimit();
    for (var slot = 1; slot <= limit; slot++) {
        var item = inv.getItem(slot);
        if (item == null) {
            continue;
        }
        var itemId = item.getItemId();
        if (!ii.isCash(itemId) || !isFashionItem(itemId)) {
            continue;
        }
        if (!Equip.class.isInstance(item)) {
            continue;
        }
        if (fashionHasStats(item)) {
            continue;
        }
        list.push({ itemId: itemId, slot: slot });
    }
}

function isFashionItem(itemId) {
    var prefix = Math.floor(itemId / 10000);
    // 帽/上衣/套装/下装/鞋/手套/披风/脸饰/眼饰
    return prefix === 100 || prefix === 104 || prefix === 105 ||
        prefix === 106 || prefix === 107 || prefix === 108 ||
        prefix === 110 || prefix === 101 || prefix === 102;
}

function fashionHasStats(equip) {
    if (equip.getStr() !== 0 || equip.getDex() !== 0 || equip.getInt() !== 0 || equip.getLuk() !== 0) {
        return true;
    }
    if (equip.getWatk() !== 0 || equip.getMatk() !== 0 || equip.getWdef() !== 0 || equip.getMdef() !== 0) {
        return true;
    }
    if (equip.getHp() !== 0 || equip.getMp() !== 0 || equip.getAcc() !== 0 || equip.getAvoid() !== 0) {
        return true;
    }
    if (equip.getSpeed() !== 0 || equip.getJump() !== 0 || equip.getHands() !== 0) {
        return true;
    }
    // WZ 自带属性也算有属性
    var ii = ItemInformationProvider.getInstance();
    var stats = ii.getEquipStats(equip.getItemId());
    if (stats == null) {
        return false;
    }
    var keys = ["STR", "DEX", "INT", "LUK", "PAD", "MAD", "PDD", "MDD", "MHP", "MMP", "ACC", "EVA", "Speed", "Jump"];
    for (var i = 0; i < keys.length; i++) {
        if (stats.containsKey(keys[i])) {
            var v = stats.get(keys[i]);
            var n = typeof v === "number" ? v : v.intValue();
            if (n !== 0) {
                return true;
            }
        }
    }
    return false;
}

function doFashionOneClick() {
    var ii = ItemInformationProvider.getInstance();
    var inv = cm.getInventory(1);
    var limit = inv.getSlotLimit();
    var done = 0;
    var skippedStat = 0;
    var skippedKeep = 0;
    // 从后往前删，避免槽位前移
    for (var slot = limit; slot >= 1; slot--) {
        if (isKeepSlot(slot, limit)) {
            var peek = inv.getItem(slot);
            if (peek != null && ii.isCash(peek.getItemId()) && isFashionItem(peek.getItemId()) && !fashionHasStats(peek)) {
                skippedKeep++;
            }
            continue;
        }
        var item = inv.getItem(slot);
        if (item == null) {
            continue;
        }
        var itemId = item.getItemId();
        if (!ii.isCash(itemId) || !isFashionItem(itemId)) {
            continue;
        }
        if (!Equip.class.isInstance(item)) {
            continue;
        }
        if (fashionHasStats(item)) {
            skippedStat++;
            continue;
        }
        InventoryManipulator.removeFromSlot(cm.getClient(), InventoryType.EQUIP, slot, 1, false);
        done++;
    }
    if (done > 0) {
        cm.getPlayer().getCashShop().gainCash(1, FASHION_NX * done);
    }
    var msg = "#e一键分解完成#n\r\n\r\n";
    msg += "成功分解：#b" + done + "#k 件，获得 #r" + (FASHION_NX * done) + "#k 点券\r\n";
    if (skippedStat > 0) {
        msg += "跳过有属性：#d" + skippedStat + "#k 件\r\n";
    }
    if (skippedKeep > 0) {
        msg += "保留区内未动：#d" + skippedKeep + "#k 件\r\n";
    }
    if (done === 0) {
        msg = "没有可分解的无属性时装（或均在保留区内）。";
    }
    cm.sendOk(msg);
    cm.dispose();
}

function isKeepSlot(slot, slotLimit) {
    if (keepFront && slot <= 24) {
        return true;
    }
    if (keepBack && slot > slotLimit - 24) {
        return true;
    }
    return false;
}

// ==================== 天赋分解 ====================

function handleTalent(selection) {
    if (status === 0) {
        scanTalentBooks();
        currentPage = 0;
        showTalentPage();
        return;
    }
    if (status === 1) {
        if (selection === SEL_BACK) {
            backToMain();
            return;
        }
        if (selection === SEL_PREV) {
            currentPage--;
            status = 0;
            showTalentPage();
            return;
        }
        if (selection === SEL_NEXT) {
            currentPage++;
            status = 0;
            showTalentPage();
            return;
        }
        if (selection === SEL_ONECLICK) {
            if (list.length === 0) {
                cm.sendOk("没有可分解的天赋书。");
                cm.dispose();
                return;
            }
            cm.sendYesNo(
                "#e一键天赋分解确认#n\r\n\r\n" +
                "将分解列表中全部天赋书（1 书 → 对应兑换材料 1 份）。\r\n确认？"
            );
            selectedIdx = -1;
            return;
        }
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= list.length) {
            cm.dispose();
            return;
        }
        var ti = list[selectedIdx];
        cm.sendGetNumber(
            "#v" + ti.itemId + "# #b" + ti.name + "#k\r\n" +
            "库存：#r" + ti.qty + "#k\r\n" +
            "产出：" + ti.rewardText + " （1:1）\r\n\r\n请输入分解数量：",
            1, 1, Math.min(ti.qty, 999)
        );
        return;
    }
    if (status === 2) {
        if (selectedIdx < 0) {
            doTalentOneClick();
            return;
        }
        inputQty = selection;
        if (inputQty < 1) {
            cm.dispose();
            return;
        }
        var ti2 = list[selectedIdx];
        if (inputQty > ti2.qty) {
            inputQty = ti2.qty;
        }
        cm.sendYesNo(
            "#e天赋分解确认#n\r\n\r\n" +
            "#v" + ti2.itemId + "# ×#r" + inputQty + "#k\r\n" +
            "获得：" + ti2.rewardText + " ×#b" + inputQty + "#k\r\n\r\n确认？"
        );
        return;
    }
    if (status === 3) {
        var ti3 = list[selectedIdx];
        var owned = cm.getItemQuantity(ti3.itemId);
        if (owned < inputQty) {
            cm.sendOk("数量不足。");
            cm.dispose();
            return;
        }
        if (!canHoldTalentRewards(ti3, inputQty)) {
            cm.sendOk("其他栏空间不足。");
            cm.dispose();
            return;
        }
        cm.gainItem(ti3.itemId, -inputQty);
        grantTalentRewards(ti3, inputQty);
        cm.sendOk("分解成功！获得 " + ti3.rewardText + " ×#b" + inputQty + "#k");
        status = -1;
        feature = 5;
        action(1, 0, 0);
    }
}

function showTalentPage() {
    var text = "#e#b[天赋分解]#k#n\r\n";
    text += "天赋书 → 对应兑换材料（1:1）\r\n";
    text += "初级→#v" + TalentConfig.MAT_PRIMARY + "# 中级→#v" + TalentConfig.MAT_MID + "# 高级→#v" + TalentConfig.MAT_ADV + "#\r\n";
    text += "终极→三种魔法石各 1 个\r\n\r\n";
    text += "#L" + SEL_BACK + "##b返回#k#l\r\n";
    text += "#L" + SEL_ONECLICK + "##r一键分解全部天赋书#k#l\r\n\r\n";
    if (list.length === 0) {
        text += "#d其他栏中没有可分解的天赋书。#k";
        cm.sendSimple(text);
        return;
    }
    var totalPages = Math.ceil(list.length / PAGE_SIZE);
    if (currentPage >= totalPages) {
        currentPage = totalPages - 1;
    }
    if (currentPage < 0) {
        currentPage = 0;
    }
    text += "共 #b" + list.length + "#k 种  (#b" + (currentPage + 1) + "/" + totalPages + "#k)\r\n";
    if (currentPage > 0) {
        text += "#L" + SEL_PREV + "##b◀ 上一页#k#l\r\n";
    }
    var start = currentPage * PAGE_SIZE;
    var end = Math.min(start + PAGE_SIZE, list.length);
    for (var i = start; i < end; i++) {
        var ti = list[i];
        text += "#L" + i + "##v" + ti.itemId + "# #b" + ti.name + "##k #r×" + ti.qty + "#k → " + ti.rewardText + "#l\r\n";
    }
    if (currentPage < totalPages - 1) {
        text += "#L" + SEL_NEXT + "##b▶ 下一页#k#l\r\n";
    }
    selectedIdx = -1;
    cm.sendSimple(text);
}

function scanTalentBooks() {
    list = [];
    var qtyMap = {};
    var iter = cm.getInventory(4).list().iterator();
    while (iter.hasNext()) {
        var item = iter.next();
        var itemId = item.getItemId();
        var tid = TalentId.fromItemId(itemId);
        if (tid == null) {
            continue;
        }
        if (qtyMap[itemId] == null) {
            qtyMap[itemId] = { itemId: itemId, qty: 0, talent: tid };
        }
        qtyMap[itemId].qty += item.getQuantity();
    }
    for (var k in qtyMap) {
        var e = qtyMap[k];
        var reward = talentRewardInfo(e.talent);
        list.push({
            itemId: e.itemId,
            qty: e.qty,
            name: e.talent.displayName(),
            talent: e.talent,
            rewardText: reward.text,
            mats: reward.mats
        });
    }
}

function talentRewardInfo(talent) {
    var tier = talent.tier();
    if (tier === TalentTier.PRIMARY) {
        return { text: "#v" + TalentConfig.MAT_PRIMARY + "#", mats: [{ id: TalentConfig.MAT_PRIMARY, n: 1 }] };
    }
    if (tier === TalentTier.MID) {
        return { text: "#v" + TalentConfig.MAT_MID + "#", mats: [{ id: TalentConfig.MAT_MID, n: 1 }] };
    }
    if (tier === TalentTier.ADVANCED) {
        return { text: "#v" + TalentConfig.MAT_ADV + "#", mats: [{ id: TalentConfig.MAT_ADV, n: 1 }] };
    }
    // 终极：三种各 1
    return {
        text: "#v" + TalentConfig.MAT_PRIMARY + "#+#v" + TalentConfig.MAT_MID + "#+#v" + TalentConfig.MAT_ADV + "#",
        mats: [
            { id: TalentConfig.MAT_PRIMARY, n: 1 },
            { id: TalentConfig.MAT_MID, n: 1 },
            { id: TalentConfig.MAT_ADV, n: 1 }
        ]
    };
}

function canHoldTalentRewards(ti, qty) {
    for (var i = 0; i < ti.mats.length; i++) {
        var m = ti.mats[i];
        if (!cm.canHold(m.id, m.n * qty)) {
            return false;
        }
    }
    return true;
}

function grantTalentRewards(ti, qty) {
    for (var i = 0; i < ti.mats.length; i++) {
        var m = ti.mats[i];
        cm.gainItem(m.id, m.n * qty);
    }
}

function doTalentOneClick() {
    scanTalentBooks();
    if (list.length === 0) {
        cm.sendOk("没有可分解的天赋书。");
        cm.dispose();
        return;
    }
    for (var i = 0; i < list.length; i++) {
        if (!canHoldTalentRewards(list[i], list[i].qty)) {
            cm.sendOk("其他栏空间不足。");
            cm.dispose();
            return;
        }
    }
    var totalBooks = 0;
    for (var j = 0; j < list.length; j++) {
        var ti = list[j];
        cm.gainItem(ti.itemId, -ti.qty);
        grantTalentRewards(ti, ti.qty);
        totalBooks += ti.qty;
    }
    cm.sendOk("一键分解完成！共分解 #r" + totalBooks + "#k 本天赋书。");
    cm.dispose();
}

// ==================== 公共 ====================

function backToMain() {
    status = -1;
    feature = 0;
    list = [];
    selectedIdx = -1;
    action(1, 0, 0);
}
