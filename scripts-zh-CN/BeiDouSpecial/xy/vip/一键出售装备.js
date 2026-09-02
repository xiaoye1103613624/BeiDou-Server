/*
 * 一键出售装备（无需确认）
 * 自动出售装备栏后48格（保留前48格）的所有装备
 * 出售过程不弹出确认窗口，每件出售的装备名称与获得金币会显示在聊天框（左下角消息）中
 */

// 装备栏保留格数（前48格不出售）
var KEEP_SLOT = 48;

function start() {
    const ShopFactory = Java.type('org.gms.server.ShopFactory');
    const InventoryType = Java.type('org.gms.client.inventory.InventoryType');
    const ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

    var ii = ItemInformationProvider.getInstance();
    var inv = cm.getInventory(1); // 1=装备栏
    var slotLimit = inv.getSlotLimit();

    if (slotLimit <= KEEP_SLOT) {
        cm.dropMessage(1, "装备栏未超过" + KEEP_SLOT + "格，没有可出售的装备。");
        cm.dispose();
        return;
    }

    var soldCount = 0;
    var totalMeso = 0;
    for (var i = KEEP_SLOT; i < slotLimit; i++) {
        var item = inv.getItem(i);
        if (item == null) {
            continue;
        }
        var itemId = item.getItemId();
        var quantity = item.getQuantity();
        var price = ii.getPrice(itemId, quantity);

        ShopFactory.getInstance().getShop(11000).sell(cm.getClient(), InventoryType.EQUIP, i, quantity);

        cm.dropMessage(1, "出售 #b#t" + itemId + "##k，获得 #r" + price + "#k 金币");
        soldCount++;
        totalMeso += price;
    }

    if (soldCount > 0) {
        cm.dropMessage(1, "一键出售完成，共出售 #r" + soldCount + "#k 件装备，获得金币 #r" + totalMeso + "#k！");
    } else {
        cm.dropMessage(1, "装备栏后" + KEEP_SLOT + "格没有可出售的装备。");
    }
    cm.dispose();
}
