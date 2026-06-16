/*
 * 物品查询脚本（GM工具）
 * 功能：输入物品名称模糊查询物品列表，支持翻页浏览，选择后可输入数量获取对应物品
 *
 * 更新日志：
 * 2026-06-15 v3: 翻页改用全局索引+固定导航ID（对齐卷轴分解.js的成熟模式）；
 *              物品名称改用 #z# 标签由客户端渲染，避免特殊字符损坏NPC文本格式；
 *              每页8条带图标，防止数据包溢出导致闪退
 */
var status = 0;
var inputName = "";
var searchResults = [];   // 搜索结果缓存：[itemId, itemName]
var pendingItem = null;   // 待获取的物品 { itemId, itemName }
var PAGE_SIZE = 8;        // 每页显示条数
var currentPage = 0;      // 当前页码（从0开始）
var totalPages = 0;       // 总页数

// 导航专用选择ID（远大于任何可能的数组索引，避免与物品索引冲突）
var NAV_HOME   = 99990;
var NAV_PREV   = 99991;
var NAV_NEXT   = 99992;
var NAV_END    = 99993;
var NAV_SEARCH = 99994;  // 返回搜索

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    var playerName = cm.getPlayer().getName();
    var logTag = "[物品查询][" + playerName + "]";

    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.sendOk("你取消了物品查询。");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        cm.sendGetText("请输入物品名称进行模糊查询：\r\n\r\n#b提示：#k支持模糊搜索，输入部分名称即可。");
    } else if (status == 1) {
        inputName = cm.getText();
        if (inputName == null || inputName.length == 0) {
            cm.sendOk("物品名称不能为空。");
            cm.dispose();
            return;
        }

        print(logTag + " 搜索开始，关键词: " + inputName);

        try {
            var ItemInfoProvider = Java.type('org.gms.server.ItemInformationProvider');
            var startTime = java.lang.System.currentTimeMillis();
            var resultList = ItemInfoProvider.getItemsIDsFromName(inputName);
            var endTime = java.lang.System.currentTimeMillis();

            print(logTag + " 搜索完成，耗时: " + (endTime - startTime) + "ms，结果数: " + resultList.size());

            searchResults = [];
            var skippedCount = 0;  // 统计因数据异常跳过的物品数

            for (var i = 0; i < resultList.size(); i++) {
                var pair = resultList.get(i);
                var id = pair.getLeft();
                var name = pair.getRight();

                // 名称校验
                if (name == null || name.length == 0) {
                    print(logTag + " [跳过] ID=" + id + " 名称为空");
                    skippedCount++;
                    continue;
                }

                // WZ数据校验：getName()内部会查String.wz，无效物品返回null
                // 如果客户端渲染 #z# 或 #i# 时找不到数据就会闪退，这里提前过滤
                var verifyName = ItemInfoProvider.getInstance().getName(id);
                if (verifyName == null) {
                    print(logTag + " [跳过] ID=" + id + " name=" + name + " WZ数据缺失(name=null)，客户端渲染会闪退");
                    skippedCount++;
                    continue;
                }

                searchResults.push([id, name]);
            }

            if (skippedCount > 0) {
                print(logTag + " 共跳过 " + skippedCount + " 个数据异常物品");
                cm.dropMessage(1, "[物品查询] 已自动跳过 " + skippedCount + " 个数据异常的物品（详情见服务端日志）");
            }

            if (searchResults.length == 0) {
                cm.sendOk("未找到包含 \"" + inputName + "\" 的物品。\r\n\r\n请尝试更换关键词。");
                cm.dispose();
                return;
            }

            totalPages = Math.ceil(searchResults.length / PAGE_SIZE);
            currentPage = 0;
            print(logTag + " 搜索结果: " + searchResults.length + "条，" + totalPages + "页");

            cm.sendSimple(buildPage(logTag));
        } catch (e) {
            var errType = e.name || "UnknownError";
            var errMsg = e.message || String(e);
            print(logTag + " 搜索异常[" + errType + "]: " + errMsg);
            if (e.stack) print(logTag + " 堆栈: " + e.stack);
            cm.dropMessage(1, "[物品查询] " + errType + ": " + errMsg);
            cm.sendOk("查询异常：\r\n\r\n#r" + errType + "#k\r\n" + errMsg);
            cm.dispose();
        }
    } else if (status == 2) {
        // 处理选择：翻页 or 选中物品
        print(logTag + " selection=" + selection + " curPage=" + currentPage + "/" + totalPages);

        if (selection == NAV_HOME) {
            currentPage = 0;
            status = 1;
            cm.sendSimple(buildPage(logTag));
        } else if (selection == NAV_PREV) {
            currentPage = Math.max(0, currentPage - 1);
            status = 1;
            cm.sendSimple(buildPage(logTag));
        } else if (selection == NAV_NEXT) {
            currentPage = Math.min(totalPages - 1, currentPage + 1);
            status = 1;
            cm.sendSimple(buildPage(logTag));
        } else if (selection == NAV_END) {
            currentPage = totalPages - 1;
            status = 1;
            cm.sendSimple(buildPage(logTag));
        } else if (selection == NAV_SEARCH) {
            // 返回搜索页，重新输入关键词
            status = 0;
            cm.sendGetText("请输入物品名称进行模糊查询：\r\n\r\n#b提示：#k支持模糊搜索，输入部分名称即可。");
        } else {
            // selection 就是全局数组索引，直接取物品
            var selected = searchResults[selection];
            if (!selected) {
                print(logTag + " 无效索引: " + selection);
                cm.sendOk("选择无效，请重新查询。");
                cm.dispose();
                return;
            }
            pendingItem = { itemId: selected[0], itemName: selected[1] };
            print(logTag + " 选中: " + pendingItem.itemId + " " + pendingItem.itemName + "（idx=" + selection + "）");
            cm.sendGetText("请输入要获取的数量：\r\n\r\n物品：#b#i" + pendingItem.itemId + "# #z" + pendingItem.itemId + "##k\r\n物品ID：#r" + pendingItem.itemId + "#k\r\n\r\n#b请输入数量（1~9999）：");
        }
    } else if (status == 3) {
        var qtyText = cm.getText();
        var qty = parseInt(qtyText);
        if (isNaN(qty) || qty <= 0 || qty > 9999) {
            cm.sendOk("无效的数量！请输入 1~9999 之间的数字。");
            cm.dispose();
            return;
        }

        try {
            cm.gainItem(pendingItem.itemId, qty);
            print(logTag + " 发放: " + pendingItem.itemId + " x" + qty);
            cm.sendOk("已获得物品：\r\n\r\n#b#i" + pendingItem.itemId + "# #z" + pendingItem.itemId + "##k × #r" + qty + "#k\r\n物品ID：#r" + pendingItem.itemId + "#k\r\n\r\n请检查背包。");
        } catch (e) {
            var errType2 = e.name || "UnknownError";
            var errMsg2 = e.message || String(e);
            print(logTag + " 发放异常[" + errType2 + "]: " + errMsg2 + " itemId=" + pendingItem.itemId);
            cm.dropMessage(1, "[物品查询] 发放失败: " + errType2 + " - " + errMsg2);
            cm.sendOk("发放物品失败：\r\n\r\n#r" + errType2 + "#k\r\n" + errMsg2 + "\r\n\r\n物品ID: " + pendingItem.itemId);
        }
        cm.dispose();
    }
}

/**
 * 构建当前页的NPC文本
 * 使用全局数组索引作为 selection ID（对齐卷轴分解.js 模式）
 * 物品名称使用 #z# 标签由客户端渲染，避免特殊字符破坏格式
 */
function buildPage(logTag) {
    var startIdx = currentPage * PAGE_SIZE;
    var endIdx = Math.min(startIdx + PAGE_SIZE, searchResults.length);
    print(logTag + " 构建第" + (currentPage + 1) + "/" + totalPages + "页 idx=" + startIdx + "-" + (endIdx - 1));

    var text = "#e#r物品查询结果#k#n\r\n\r\n";
    text += "搜索：#b" + inputName + "#k | 共 #r" + searchResults.length + "#k 条\r\n";
    text += "第 #r" + (currentPage + 1) + "#k / #r" + totalPages + "#k 页\r\n\r\n";
    text += "#b请选择物品或翻页：#k\r\n\r\n";

    // 物品列表（selection ID = 全局数组索引）
    for (var i = startIdx; i < endIdx; i++) {
        var itemId = searchResults[i][0];
        text += "#L" + i + "##b#i" + itemId + "# #z" + itemId + "##k（ID: " + itemId + "）#l\r\n";
    }

    // 翻页导航（固定高ID，不冲突）
    text += "\r\n#b──── 翻页 ────#k\r\n";
    text += "#L" + NAV_HOME + "##b<< 首页#k#l  ";
    text += "#L" + NAV_PREV + "##b< 上页#k#l  ";
    text += "#L" + NAV_NEXT + "##b下页 >#k#l  ";
    text += "#L" + NAV_END + "##b末页 >>#k#l\r\n";
    text += "#L" + NAV_SEARCH + "##r🔍 重新搜索#k#l\r\n";

    return text;
}