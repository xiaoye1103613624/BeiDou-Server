/*
 * ==================
 * 一键售卖其他 - 售卖消耗/设置/其他栏72格(24*3)之后的物品
 * 售卖前弹窗确认 → 展示清单及价格 → 再次确认 → 执行售卖
 * ==================
 */

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

var status = -1;
var sellList = [];
var totalPrice = 0;
var totalCount = 0;

var targetTypes = [InventoryType.USE, InventoryType.SETUP, InventoryType.ETC];
var typeNames = ["消耗", "设置", "其他"];

function start() {
    status = -1;
    sellList = [];
    totalPrice = 0;
    totalCount = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }

    if (mode === 1) {
        status++;
    }

    if (status === 0) {
        buildSellList();

        if (sellList.length === 0) {
            cm.sendOk("消耗/设置/其他栏72格之后没有可售卖的物品。");
            cm.dispose();
            return;
        }

        cm.sendYesNo(
            "#e#b=== 一键售卖其他 ===#k#n\r\n\r\n" +
            "消耗/设置/其他栏72格之后共有 #b" + totalCount + "#k 组物品\r\n" +
            "预计获得金币：#r" + totalPrice.toLocaleString() + "#k\r\n\r\n" +
            "#b是否查看详细售卖清单？#k"
        );

    } else if (status === 1) {
        var text = "#e#b=== 待售物品清单 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";

        for (var i = 0; i < sellList.length; i++) {
            var entry = sellList[i];
            text += "#b" + (i + 1) + ".#k " + entry.name;
            if (entry.quantity > 1) {
                text += "  x" + entry.quantity;
            }
            text += "\r\n";
            text += "    背包:#b" + entry.bagName + "#k  槽位:#b" + entry.slot + "#k  价格:#r" + entry.price.toLocaleString() + "#k\r\n";
        }

        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "共 #b" + totalCount + "#k 组物品\r\n";
        text += "总计获得：#r" + totalPrice.toLocaleString() + "#k 金币\r\n\r\n";
        text += "#r确认售卖以上所有物品？#k";

        cm.sendYesNo(text);

    } else if (status === 2) {
        var result = executeSell();
        cm.sendOk(
            "#e#b=== 售卖完成 ===#k#n\r\n\r\n" +
            "售出物品：#b" + result.count + "#k 组\r\n" +
            "获得金币：#r" + result.mesos.toLocaleString() + "#k"
        );
        cm.dispose();
    }
}

function buildSellList() {
    sellList = [];
    totalPrice = 0;
    totalCount = 0;

    var player = cm.getPlayer();
    var ii = ItemInformationProvider.getInstance();

    for (var t = 0; t < targetTypes.length; t++) {
        var inv = player.getInventory(targetTypes[t]);

        inv.lockInventory();
        try {
            var items = inv.list().toArray();
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var slot = item.getPosition();

                if (slot <= 72) {
                    continue;
                }

                var qty = item.getQuantity();
                var price = ii.getPrice(item.getItemId(), qty);
                if (price <= 0) {
                    continue;
                }

                var itemName = ii.getName(item.getItemId());
                if (itemName == null) {
                    itemName = "未知物品(" + item.getItemId() + ")";
                }

                sellList.push({
                    slot: slot,
                    itemId: item.getItemId(),
                    name: itemName,
                    quantity: qty,
                    price: price,
                    bagName: typeNames[t],
                    invType: targetTypes[t]
                });

                totalPrice += price;
                totalCount++;
            }
        } finally {
            inv.unlockInventory();
        }
    }
}

function executeSell() {
    var player = cm.getPlayer();
    var c = player.getClient();
    var ii = ItemInformationProvider.getInstance();
    var soldCount = 0;
    var soldMesos = 0;

    for (var t = 0; t < targetTypes.length; t++) {
        var inv = player.getInventory(targetTypes[t]);

        // 先收集，再售卖
        var toSell = [];
        inv.lockInventory();
        try {
            var items = inv.list().toArray();
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var slot = item.getPosition();
                if (slot <= 72) continue;
                var qty = item.getQuantity();
                var price = ii.getPrice(item.getItemId(), qty);
                if (price <= 0) continue;
                toSell.push({ slot: slot, quantity: qty, price: price });
            }
        } finally {
            inv.unlockInventory();
        }

        for (var j = 0; j < toSell.length; j++) {
            var entry = toSell[j];
            try {
                InventoryManipulator.removeFromSlot(c, targetTypes[t], entry.slot, entry.quantity, false);
                player.gainMeso(entry.price, false);
                soldCount++;
                soldMesos += entry.price;
            } catch (e) {}
        }
    }

    return { count: soldCount, mesos: soldMesos };
}
