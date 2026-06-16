/*
 * 名称：一键售卖（确认弹窗）
 * 功能：弹窗列出装备栏后48格所有装备（图片+名称+价格），确认后售出，聊天框逐条显示已售装备和金币
 * 触发道具：02432078（可通过修改spec/script切换到此脚本）
 * 作者：xy
 * 日期：2026-06-15
 *
 * 说明：
 * - 弹窗展示待售装备清单（图片、名称、售价）
 * - 确认后执行售卖，每售出一件在聊天框输出装备名称和售价
 * - 取消则关闭弹窗不做任何操作
 */

var itemList = []; // 存储 {slot, itemId, price, name} 待售装备列表

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    levelmain();
}

function levelmain() {
    buildItemList();
    if (itemList.length == 0) {
        im.sendOkLevel("dispose", "装备栏后48格没有可售卖的装备。");
    } else {
        var msg = buildItemDisplay();
        im.sendYesNoLevel("dispose", "doSell", msg);
    }
}

function leveldoSell() {
    var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
    var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');

    var player = im.getPlayer();
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var totalMeso = 0;
    var actualSold = 0;

    // 开始售卖提示
    im.dropMessage(6, "[便携功能] 开始售出装备栏后48格装备（共 " + itemList.length + " 件）...");

    // 重新获取物品并逐条售出（防止期间被移动）
    for (var i = 0; i < itemList.length; i++) {
        var info = itemList[i];
        var item = equipInv.getItem(info.slot);
        if (item != null && item.getItemId() == info.itemId) {
            if (info.price > 0) {
                totalMeso += info.price;
                // 聊天框输出已售装备名称和金币
                im.dropMessage(6, "[便携功能] 售出：【" + info.name + "】，获得 " + info.price.toLocaleString() + " 金币");
            } else {
                // 无售价装备仅移除
                im.dropMessage(6, "[便携功能] 移除：【" + info.name + "】（无售价）");
            }
            InventoryManipulator.removeFromSlot(im.getClient(), InventoryType.EQUIP, info.slot, item.getQuantity(), false);
            actualSold++;
        }
    }

    // 发放金币
    if (totalMeso > 0) {
        im.gainMeso(totalMeso);
    }

    // 聊天框输出汇总
    im.dropMessage(6, "[便携功能] 完成！共售出 " + actualSold + " 件装备，"
        + (totalMeso > 0 ? "获得 " + totalMeso.toLocaleString() + " 金币。" : "无可售出价值的装备已被移除。"));

    // 弹窗显示结果
    im.sendOkLevel("dispose", "售出完成！\r\n实际售出 #b" + actualSold + " 件#k 装备\r\n获得 #r" + totalMeso.toLocaleString() + " 金币#k");
}

function leveldispose() {
    im.dispose();
}

function buildItemList() {
    var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
    var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

    var player = im.getPlayer();
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var slotLimit = equipInv.getSlotLimit();
    var startSlot = Math.max(1, slotLimit - 47); // 槽位不足48时从第1格开始
    var ii = ItemInformationProvider.getInstance();

    itemList = [];
    for (var slot = startSlot; slot <= slotLimit; slot++) {
        var item = equipInv.getItem(slot);
        if (item != null && item.getItemId() > 0) {
            var itemId = item.getItemId();
            var price = ii.getPrice(itemId, 1);
            var itemName = ii.getName(itemId); // 获取装备名称，供聊天框输出
            itemList.push({
                slot: slot,
                itemId: itemId,
                price: price > 0 ? price : 0,
                name: itemName
            });
        }
    }
}

function buildItemDisplay() {
    var msg = "#e#b装备栏后48格待售装备清单#k#n\r\n\r\n";
    msg += "以下装备将被售出：\r\n";
    msg += "#fUI/CashShop.img/CSDiscount/line#\r\n\r\n";

    var totalPrice = 0;
    for (var i = 0; i < itemList.length; i++) {
        var info = itemList[i];
        totalPrice += info.price;
        msg += "#i" + info.itemId + "#  #t" + info.itemId + "#";
        if (info.price > 0) {
            msg += "  (售价: #r" + info.price.toLocaleString() + " 金币#k)";
        } else {
            msg += "  (#r无法售出#k)";
        }
        msg += "\r\n";
    }

    msg += "\r\n#fUI/CashShop.img/CSDiscount/line#\r\n";
    msg += "共 #b" + itemList.length + " 件#k 装备，预计获得 #r" + totalPrice.toLocaleString() + " 金币#k\r\n\r\n";
    msg += "确定要售出以上装备吗？";

    return msg;
}
