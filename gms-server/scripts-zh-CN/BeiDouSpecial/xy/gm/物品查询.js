/*
 * 物品查询脚本（GM工具）
 * 功能：输入物品名称模糊查询物品列表，选择后可输入数量获取对应物品
 */
var status = 0;
var inputName = "";
var searchResults = [];   // 搜索结果缓存：[itemId, itemName]
var pendingItem = null;   // 待获取的物品 { itemId, itemName }

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
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
        // 输入物品名称进行模糊搜索
        cm.sendGetText("请输入物品名称进行模糊查询：\r\n\r\n#b提示：#k支持模糊搜索，输入部分名称即可。");
    } else if (status == 1) {
        inputName = cm.getText();
        if (inputName == null || inputName.length == 0) {
            cm.sendOk("物品名称不能为空。");
            cm.dispose();
            return;
        }

        // 调用Java后端模糊搜索物品
        var ItemInfoProvider = Java.type('org.gms.server.ItemInformationProvider');
        var resultList = ItemInfoProvider.getItemsIDsFromName(inputName);

        // 将Java List转为JS数组
        searchResults = [];
        for (var i = 0; i < resultList.size(); i++) {
            var pair = resultList.get(i);
            searchResults.push([pair.getLeft(), pair.getRight()]);
        }

        if (searchResults.length == 0) {
            cm.sendOk("未找到包含 \"" + inputName + "\" 的物品。\r\n\r\n请尝试更换关键词。");
            cm.dispose();
            return;
        }

        // 限制最大显示数量，防止列表过长导致客户端崩溃
        var maxShow = 100;
        var totalCount = searchResults.length;
        var showCount = totalCount > maxShow ? maxShow : totalCount;

        var text = "\t\t#r#e< 物品查询结果 >#k#n\r\n\r\n";
        text += "搜索关键词：#b" + inputName + "#k\r\n";
        text += "共找到 #r" + totalCount + "#k 个结果";
        if (totalCount > maxShow) {
            text += "（仅显示前" + maxShow + "个，请缩小搜索范围）";
        }
        text += "\r\n\r\n";
        text += "#b请选择要获取的物品：#k\r\n\r\n";

        for (var i = 0; i < showCount; i++) {
            var itemId = searchResults[i][0];
            var itemName = searchResults[i][1];
            text += "#L" + i + "##b#i" + itemId + "# " + itemName + "#k（ID: " + itemId + "）#l\r\n";
        }

        cm.sendSimple(text);
    } else if (status == 2) {
        // 记录选中的物品，弹出数量输入框
        var selected = searchResults[selection];
        pendingItem = { itemId: selected[0], itemName: selected[1] };
        cm.sendGetText("请输入要获取的数量：\r\n\r\n物品：#b#i" + pendingItem.itemId + "# " + pendingItem.itemName + "#k\r\n物品ID：#r" + pendingItem.itemId + "#k\r\n\r\n#b请输入数量（1~9999）：");
    } else if (status == 3) {
        // 解析数量并发放物品
        var qtyText = cm.getText();
        var qty = parseInt(qtyText);
        if (isNaN(qty) || qty <= 0 || qty > 9999) {
            cm.sendOk("无效的数量！请输入 1~9999 之间的数字。");
            cm.dispose();
            return;
        }
        cm.gainItem(pendingItem.itemId, qty);
        cm.sendOk("已获得物品：\r\n\r\n#b#i" + pendingItem.itemId + "# " + pendingItem.itemName + "#k × #r" + qty + "#k\r\n物品ID：#r" + pendingItem.itemId + "#k\r\n\r\n请检查背包。");
        cm.dispose();
    }
}