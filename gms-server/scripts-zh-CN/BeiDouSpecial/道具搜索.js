/*
 * ==================
 * 脚本类型: 专用道具搜索
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 按道具ID或名称搜索玩家背包中的道具
 *   2. 支持装备、消耗、设置、其他、特殊栏位
 * ==================
 */

var status = -1;
var searchMode = 0; // 0=按ID, 1=按名称
var searchInput = "";
var results = [];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 道具搜索 ===#k#n\r\n\r\n";
        text += "请选择搜索方式：\r\n\r\n";
        text += "#L0##b按道具ID搜索#k#l\r\n";
        text += "#L1##b按道具名称搜索#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        searchMode = selection;
        if (searchMode === 0) {
            cm.sendGetText("请输入道具ID：");
        } else {
            cm.sendGetText("请输入道具名称关键字：");
        }
    } else if (status === 2) {
        searchInput = cm.getText();
        results = [];
        var player = cm.getPlayer();

        var invTypes = [
            { type: Java.type('org.gms.client.inventory.InventoryType').EQUIP, name: "装备栏" },
            { type: Java.type('org.gms.client.inventory.InventoryType').USE, name: "消耗栏" },
            { type: Java.type('org.gms.client.inventory.InventoryType').SETUP, name: "设置栏" },
            { type: Java.type('org.gms.client.inventory.InventoryType').ETC, name: "其他栏" },
            { type: Java.type('org.gms.client.inventory.InventoryType').CASH, name: "特殊栏" }
        ];

        var ItemInformationProvider = Java.type('org.gms.provider.ItemInformationProvider').getInstance();

        for (var t = 0; t < invTypes.length; t++) {
            var inv = player.getInventory(invTypes[t].type);
            if (inv == null) continue;
            var items = inv.list();
            for (var i = 0; i < items.size(); i++) {
                var item = items.get(i);
                var itemId = item.getItemId();
                var itemName = ItemInformationProvider.getName(itemId);
                if (itemName == null) itemName = "未知道具";

                var match = false;
                if (searchMode === 0) {
                    match = (itemId.toString() === searchInput.trim());
                } else {
                    match = itemName.toLowerCase().indexOf(searchInput.toLowerCase()) >= 0;
                }

                if (match) {
                    results.push({
                        itemId: itemId,
                        name: itemName,
                        qty: item.getQuantity(),
                        invName: invTypes[t].name
                    });
                }
            }
        }

        var text = "#e#b=== 搜索结果 ===#k#n\r\n\r\n";
        if (results.length === 0) {
            text += "未找到匹配的道具。\r\n";
        } else {
            text += "找到 #b" + results.length + "#k 个匹配道具：\r\n\r\n";
            for (var j = 0; j < results.length; j++) {
                if (j >= 50) {
                    text += "... 仅显示前50个结果\r\n";
                    break;
                }
                var r = results[j];
                text += "  #i" + r.itemId + "# #b" + r.name + "#k (ID:" + r.itemId + ") x" + r.qty + " [" + r.invName + "]\r\n";
            }
        }
        cm.sendOk(text);
        cm.dispose();
    }
}
