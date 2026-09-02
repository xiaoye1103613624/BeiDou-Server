/*
 * 一键出售装备（需确认）
 * 自动出售装备栏后48格（保留前48格）的所有装备
 * 出售前会在确认窗口中列出每件装备的名称与价格，点击确认后才会出售
 */

// 装备栏保留格数（前48格不出售）
var KEEP_SLOT = 48;

function start() {
    showConfirm();
}

function action(mode, type, selection) {
    if (mode === 1) {
        doSell();
    } else {
        cm.sendOk("已取消出售。");
        cm.dispose();
    }
}

// 列出待出售的装备清单并弹出确认窗口
function showConfirm() {
    const ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
    var ii = ItemInformationProvider.getInstance();
    var inv = cm.getInventory(1); // 1=装备栏
    var slotLimit = inv.getSlotLimit();

    if (slotLimit <= KEEP_SLOT) {
        cm.sendOk("装备栏未超过" + KEEP_SLOT + "格，没有可出售的装备。");
        cm.dispose();
        return;
    }

    var text = "#e一键出售装备#n\r\n即将出售以下装备：\r\n\r\n";
    var count = 0;
    var totalMeso = 0;
    for (var i = KEEP_SLOT; i < slotLimit; i++) {
        var item = inv.getItem(i);
        if (item == null) {
            continue;
        }
        var price = ii.getPrice(item.getItemId(), item.getQuantity());
        text += "#i" + item.getItemId() + "#  #b#t" + item.getItemId() + "##k × " + item.getQuantity() + "  #r" + price + "#k 金币\r\n";
        count++;
        totalMeso += price;
    }

    if (count === 0) {
        cm.sendOk("装备栏后" + KEEP_SLOT + "格没有可出售的装备。");
        cm.dispose();
        return;
    }

    text += "\r\n共 #r" + count + "#k 件，预计获得金币 #r" + totalMeso + "#k\r\n\r\n是否确认出售？";
    cm.sendYesNo(text);
}

// 确认后执行实际出售
function doSell() {
    const ShopFactory = Java.type('org.gms.server.ShopFactory');
    const InventoryType = Java.type('org.gms.client.inventory.InventoryType');
    const ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

    var ii = ItemInformationProvider.getInstance();
    var inv = cm.getInventory(1);
    var slotLimit = inv.getSlotLimit();

    var soldCount = 0;
    var totalMeso = 0;
    for (var i = KEEP_SLOT; i < slotLimit; i++) {
        var item = inv.getItem(i);
        if (item == null) {
            continue;
        }
        var quantity = item.getQuantity();
        var price = ii.getPrice(item.getItemId(), quantity);

        ShopFactory.getInstance().getShop(11000).sell(cm.getClient(), InventoryType.EQUIP, i, quantity);

        soldCount++;
        totalMeso += price;
    }

    if (soldCount > 0) {
        cm.sendOk("出售完成，共出售 #r" + soldCount + "#k 件装备，获得金币 #r" + totalMeso + "#k！");
    } else {
        cm.sendOk("装备栏后" + KEEP_SLOT + "格没有可出售的装备。");
    }
    cm.dispose();
}
