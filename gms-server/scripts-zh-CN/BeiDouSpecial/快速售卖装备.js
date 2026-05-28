/*
 * ==================
 * 快速售卖装备 - 直接售卖装备栏48格之后的装备（无确认弹窗）
 * ==================
 */

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

function start() {
    var player = cm.getPlayer();
    var c = cm.getClient();
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var ii = ItemInformationProvider.getInstance();

    // 收集待售列表
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
            toSell.push({ slot: slot, price: price });
        }
    } finally {
        equipInv.unlockInventory();
    }

    if (toSell.length === 0) {
        player.dropMessage(1, "装备栏48格之后没有可售卖的装备。");
        cm.dispose();
        return;
    }

    // 直接售卖
    var soldMesos = 0;
    for (var j = 0; j < toSell.length; j++) {
        var entry = toSell[j];
        try {
            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIP, entry.slot, 1, false);
            player.gainMeso(entry.price, false);
            soldMesos += entry.price;
        } catch (e) {}
    }

    player.dropMessage(1, "已快速售出 " + toSell.length + " 件装备，获得 " + soldMesos.toLocaleString() + " 金币");
    cm.dispose();
}
