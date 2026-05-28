/*
 * ==================
 * 快速售卖其他 - 直接售卖消耗/设置/其他栏72格之后的物品（无确认弹窗）
 * ==================
 */

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

function start() {
    var player = cm.getPlayer();
    var c = cm.getClient();
    var ii = ItemInformationProvider.getInstance();

    var targetTypes = [InventoryType.USE, InventoryType.SETUP, InventoryType.ETC];
    var soldCount = 0;
    var soldMesos = 0;

    for (var t = 0; t < targetTypes.length; t++) {
        var inv = player.getInventory(targetTypes[t]);

        // 先收集
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

    if (soldCount === 0) {
        player.dropMessage(1, "消耗/设置/其他栏72格之后没有可售卖的物品。");
    } else {
        player.dropMessage(1, "已快速售出 " + soldCount + " 组物品，获得 " + soldMesos.toLocaleString() + " 金币");
    }
    cm.dispose();
}
