/*
 * ==================
 * 一键售卖装备 - 售卖装备栏48格之后的装备
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
        buildEquipSellList();

        if (sellList.length === 0) {
            cm.sendOk("装备栏48格之后没有可售卖的装备。");
            cm.dispose();
            return;
        }

        cm.sendYesNo(
            "#e#b=== 一键售卖装备 ===#k#n\r\n\r\n" +
            "装备栏48格之后共有 #b" + totalCount + "#k 件装备\r\n" +
            "预计获得金币：#r" + totalPrice.toLocaleString() + "#k\r\n\r\n" +
            "#b是否查看详细售卖清单？#k"
        );

    } else if (status === 1) {
        var text = "#e#b=== 待售装备清单 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";

        for (var i = 0; i < sellList.length; i++) {
            var entry = sellList[i];
            text += "#b" + (i + 1) + ".#k " + entry.name + "\r\n";
            text += "    槽位:#b" + entry.slot + "#k  价格:#r" + entry.price.toLocaleString() + "#k\r\n";
        }

        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "共 #b" + totalCount + "#k 件装备\r\n";
        text += "总计获得：#r" + totalPrice.toLocaleString() + "#k 金币\r\n\r\n";
        text += "#r确认售卖以上所有装备？#k";

        cm.sendYesNo(text);

    } else if (status === 2) {
        var result = executeEquipSell();
        cm.sendOk(
            "#e#b=== 售卖完成 ===#k#n\r\n\r\n" +
            "售出装备：#b" + result.count + "#k 件\r\n" +
            "获得金币：#r" + result.mesos.toLocaleString() + "#k"
        );
        cm.dispose();
    }
}

function buildEquipSellList() {
    sellList = [];
    totalPrice = 0;
    totalCount = 0;

    var player = cm.getPlayer();
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var ii = ItemInformationProvider.getInstance();

    equipInv.lockInventory();
    try {
        var items = equipInv.list().toArray();
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var slot = item.getPosition();

            if (slot <= 48) {
                continue;
            }

            var price = ii.getPrice(item.getItemId(), 1);
            if (price <= 0) {
                continue;
            }

            var itemName = ii.getName(item.getItemId());
            if (itemName == null) {
                itemName = "未知装备(" + item.getItemId() + ")";
            }

            sellList.push({
                slot: slot,
                itemId: item.getItemId(),
                name: itemName,
                price: price
            });

            totalPrice += price;
            totalCount++;
        }
    } finally {
        equipInv.unlockInventory();
    }
}

function executeEquipSell() {
    var player = cm.getPlayer();
    var c = player.getClient();
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var ii = ItemInformationProvider.getInstance();
    var soldCount = 0;
    var soldMesos = 0;

    // 先收集待售列表（避免迭代时修改集合）
    var toSell = [];
    equipInv.lockInventory();
    try {
        var items = equipInv.list().toArray();
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var slot = item.getPosition();
            if (slot <= 48) continue;
            var price = ii.getPrice(item.getItemId(), 1);
            if (price <= 0) continue;
            toSell.push({ slot: slot, itemId: item.getItemId(), price: price });
        }
    } finally {
        equipInv.unlockInventory();
    }

    // 逐个售卖
    for (var j = 0; j < toSell.length; j++) {
        var entry = toSell[j];
        try {
            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIP, entry.slot, 1, false);
            player.gainMeso(entry.price, false);
            soldCount++;
            soldMesos += entry.price;
        } catch (e) {}
    }

    return { count: soldCount, mesos: soldMesos };
}
