/*
 * 物品查询脚本（GM工具）
 * 功能：模糊搜索 → 多选加入购物车 → 批量获取
 *
 * 中文输入说明：
 * GMS083 NPC 单行输入框偶发调不出输入法。本脚本支持：
 * 1) 直接输入物品ID（数字，不依赖中文输入法）
 * 2) 名称关键词（可输入法输入；若调不出可从记事本复制后 Ctrl+V 粘贴）
 */
var PAGE_SIZE   = 20;   // 每页物品数（购物车占位后可见条目减少，保持20避免崩溃）
var MAX_CART    = 8;    // 购物车最大数量
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";
var SEL_CONFIRM = 996;
var SEL_PREV    = 997;
var SEL_NEXT    = 998;
var SEL_SEARCH  = 999;

var status = 0;
var inputName    = "";
var searchResults = [];   // [[itemId, itemName], ...]
var currentPage   = 0;
var cartSet       = {};   // { arrayIndex: true }
var searchMode    = 0;    // 0=名称 1=ID（仅提示不同，实际同一输入框均支持）

function start() {
    status = -1;
    action(1, 0, 0);
}

function isEquip(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

function cartSize() {
    return Object.keys(cartSet).length;
}

function getCartKeys() {
    var ks = Object.keys(cartSet);
    var result = [];
    for (var i = 0; i < ks.length; i++) result.push(parseInt(ks[i]));
    return result;
}

function promptSearchText() {
    var tip = "请输入物品名称或物品ID：\r\n\r\n";
    tip += "#b名称：#k支持模糊搜索（调不出中文输入法时，可从记事本复制后 #rCtrl+V#k 粘贴）\r\n";
    tip += "#bID：#k直接输入数字，如 #r1302000#k\r\n";
    if (searchMode == 1) {
        tip += "\r\n#d当前倾向：按ID查询#k";
    }
    cm.sendGetText(tip);
}

// ── 搜索结果列表（含购物车状态）──
function showResultPage() {
    var total      = searchResults.length;
    var totalPages = Math.ceil(total / PAGE_SIZE);
    var pageStart  = currentPage * PAGE_SIZE;
    var pageEnd    = Math.min(pageStart + PAGE_SIZE, total);
    var n          = cartSize();

    var text = "\t\t#r#e< 物品查询 >#k#n\r\n";
    text += "关键词：#b" + inputName + "#k　共 #r" + total + "#k 个";
    if (totalPages > 1) text += "（第 #r" + (currentPage + 1) + "#k / " + totalPages + " 页）";
    if (n > 0) {
        text += "　已选：#r" + n + "#k";
        if (n >= MAX_CART) text += "（已满）";
    }
    text += "\r\n";

    // 购物车区（点击移除）
    if (n > 0) {
        text += "#r已选（点击可移除）：#k\r\n";
        var keys = getCartKeys();
        for (var k = 0; k < keys.length; k++) {
            var idx = keys[k];
            text += "#L" + idx + "##r[✓] " + searchResults[idx][1] + "#k（" + searchResults[idx][0] + "）#l\r\n";
        }
        text += "#g---\r\n";
    }

    // 当前页未选物品（点击添加）
    var hasUnselected = false;
    for (var i = pageStart; i < pageEnd; i++) {
        if (!cartSet[i]) { hasUnselected = true; break; }
    }
    if (hasUnselected) {
        text += "#b点击添加：#k\r\n";
        for (var i = pageStart; i < pageEnd; i++) {
            if (cartSet[i]) continue;
            text += "#L" + i + "# " + searchResults[i][1] + "（" + searchResults[i][0] + "）#l\r\n";
        }
    }

    text += "#g----------------------------------------------\r\n";
    var navLine = "";
    if (currentPage > 0)               navLine += "#L" + SEL_PREV + "# < 上一页#l";
    if (currentPage < totalPages - 1)  navLine += (navLine ? "    " : "") + "#L" + SEL_NEXT + "# 下一页 >#l";
    if (navLine) text += navLine + "\r\n";

    if (n > 0) text += "#L" + SEL_CONFIRM + "##b获取选中（" + n + " 种）#k#l\r\n";
    text += "#L" + SEL_SEARCH + "#" + 返回图标 + "#l\r\n";

    cm.sendSimple(text);
}

// ── 确认页 ──
function showConfirmPage() {
    var keys = getCartKeys();
    var text = "\t\t#r#e< 确认获取 >#k#n\r\n\r\n";
    text += "共 #r" + keys.length + "#k 种物品：\r\n\r\n";
    for (var k = 0; k < keys.length; k++) {
        var idx = keys[k];
        text += "· #b" + searchResults[idx][1] + "#k（" + searchResults[idx][0] + "）\r\n";
    }
    text += "\r\n";
    text += "#L1##b确认（各 × 1 个）#k#l\r\n";
    text += "#L2##b自定义数量（每种相同）#k#l\r\n";
    text += "#L0##b取消#k#l\r\n";
    cm.sendSimple(text);
}

// ── 批量发放 ──
function doGainItems(qty) {
    var keys       = getCartKeys();
    var successList = [];
    var failList    = [];

    for (var k = 0; k < keys.length; k++) {
        var idx  = keys[k];
        var id   = searchResults[idx][0];
        var name = searchResults[idx][1];

        if (isEquip(id)) {
            // 装备每格1个，循环发放
            var given = 0;
            for (var j = 0; j < qty; j++) {
                if (!cm.canHold(id)) break;
                cm.gainItem(id, 1);
                given++;
            }
            if (given > 0) successList.push(name + " ×" + given);
            if (given < qty) failList.push(name + "（背包满，仅得 " + given + " 个）");
        } else {
            cm.gainItem(id, qty);
            successList.push(name + " ×" + qty);
        }
    }

    var msg = "";
    if (successList.length > 0) {
        msg += "#b已获得：#k\r\n";
        for (var s = 0; s < successList.length; s++) msg += "· " + successList[s] + "\r\n";
    }
    if (failList.length > 0) {
        msg += "\r\n#r获取失败：#k\r\n";
        for (var f = 0; f < failList.length; f++) msg += "· " + failList[f] + "\r\n";
    }
    cm.sendOk(msg || "完成。");
    cm.dispose();
}

function doSearch(raw) {
    // GraalJS 下 Java String 统一转 JS 字符串，避免 length/.trim 异常
    inputName = ("" + raw).replace(/^\s+|\s+$/g, "");
    if (!inputName) {
        cm.sendOk("物品名称/ID不能为空。");
        cm.dispose();
        return false;
    }

    var provider = Java.type('org.gms.server.ItemInformationProvider');
    var resultList = provider.getItemsIDsFromName(inputName);

    searchResults = [];
    for (var i = 0; i < resultList.size(); i++) {
        var pair = resultList.get(i);
        searchResults.push([pair.getLeft(), "" + pair.getRight()]);
    }
    cartSet = {};

    if (searchResults.length == 0) {
        cm.sendOk("未找到包含 \"" + inputName + "\" 的物品。\r\n可改试：物品ID数字，或粘贴中文关键词。");
        cm.dispose();
        return false;
    }

    currentPage = 0;
    showResultPage();
    return true;
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) { cm.dispose(); return; }
    status++;

    // ── 0：选择查询方式 ──
    if (status == 0) {
        var menu = "\t\t#r#e< 物品查询 >#k#n\r\n\r\n";
        menu += "请选择查询方式：\r\n";
        menu += "#L0##b按名称查询#k（支持中文，可粘贴）#l\r\n";
        menu += "#L1##b按物品ID查询#k（纯数字，不依赖输入法）#l\r\n";
        menu += "#L2#关闭#l\r\n";
        cm.sendSimple(menu);

    // ── 1：弹出输入框 ──
    } else if (status == 1) {
        if (selection == 2) { cm.dispose(); return; }
        searchMode = (selection == 1) ? 1 : 0;
        promptSearchText();

    // ── 2：执行搜索，展示第一页 ──
    } else if (status == 2) {
        doSearch(cm.getText());

    // ── 3：列表交互（切换购物车 / 翻页 / 确认）──
    } else if (status == 3) {
        if (selection == SEL_NEXT)   { currentPage++; status = 2; showResultPage(); return; }
        if (selection == SEL_PREV)   { currentPage--; status = 2; showResultPage(); return; }
        if (selection == SEL_SEARCH) {
            status = -1; inputName = ""; searchResults = []; cartSet = {};
            action(1, 0, 0);
            return;
        }
        if (selection == SEL_CONFIRM) {
            showConfirmPage();   // status 保持 3，下次 action 进入 status 4
            return;
        }

        // 切换购物车（添加 / 移除）
        if (cartSet[selection]) {
            delete cartSet[selection];
        } else if (cartSize() < MAX_CART) {
            cartSet[selection] = true;
        }
        status = 2;
        showResultPage();
        return;

    // ── 4：确认页选择 ──
    } else if (status == 4) {
        if (selection == 0) { status = 2; showResultPage(); return; }  // 取消，返回列表
        if (selection == 1) { doGainItems(1); return; }                 // 各 ×1
        if (selection == 2) {                                           // 自定义数量
            cm.sendGetNumber(
                "请输入每种物品的获取数量：\r\n（装备类受背包格数限制，消耗品按堆叠上限分格）",
                1, 1, 9999
            );
            return;  // status 保持 4，下次 action 进入 status 5
        }

    // ── 5：自定义数量确认，发放 ──
    } else if (status == 5) {
        doGainItems(selection);  // selection = sendGetNumber 的返回值
    }
}
