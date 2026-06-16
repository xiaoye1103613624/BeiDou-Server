/*
 * 名称：一键售卖（自动售出）
 * 功能：自动售出装备栏后48格的所有装备，无确认直接售卖，售出后聊天框逐条显示已售装备和金币
 * 触发道具：2432076（02432076）
 * 作者：xy
 * 日期：2026-06-15
 *
 * 说明：
 * - 遍历装备栏后48格（若总槽位不足48则售出全部）
 * - 直接移除装备并根据商店售价发放金币
 * - 每售出一件装备在聊天框输出装备名称和售价
 * - 不可售出（售价为0）的装备也会被移除但不发放金币
 */

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

function start() {
    var player = im.getPlayer();
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var slotLimit = equipInv.getSlotLimit();

    // 计算后48格的起始槽位（槽位不足48时从第1格开始）
    var startSlot = Math.max(1, slotLimit - 47);

    var ii = ItemInformationProvider.getInstance();
    var totalMeso = 0;
    var soldCount = 0;   // 成功售出（有金币收益）的件数
    var totalCount = 0;  // 移除的装备总数

    // 先收集待处理的槽位、价格和名称，避免边遍历边修改
    var sellList = [];
    for (var slot = startSlot; slot <= slotLimit; slot++) {
        var item = equipInv.getItem(slot);
        if (item != null && item.getItemId() > 0) {
            var price = ii.getPrice(item.getItemId(), 1);
            var itemName = ii.getName(item.getItemId()); // 获取装备名称
            sellList.push({slot: slot, price: price > 0 ? price : 0, itemId: item.getItemId(), name: itemName});
        }
    }

    if (sellList.length == 0) {
        im.dropMessage(6, "[便携功能] 装备栏后48格没有装备可售。");
        im.dispose();
        return;
    }

    // 开始售卖提示
    im.dropMessage(6, "[便携功能] 开始售出装备栏后48格装备（共 " + sellList.length + " 件）...");

    // 执行移除和结算，逐条输出售出信息到聊天框
    for (var i = 0; i < sellList.length; i++) {
        var info = sellList[i];
        var item = equipInv.getItem(info.slot);
        // 二次校验：物品未被移动或变更
        if (item != null && item.getItemId() == info.itemId) {
            InventoryManipulator.removeFromSlot(im.getClient(), InventoryType.EQUIP, info.slot, item.getQuantity(), false);
            totalCount++;
            if (info.price > 0) {
                totalMeso += info.price;
                soldCount++;
                // 聊天框输出已售装备名称和金币
                im.dropMessage(6, "[便携功能] 售出：【" + info.name + "】，获得 " + info.price.toLocaleString() + " 金币");
            } else {
                // 无售价装备仅移除，也输出到聊天框
                im.dropMessage(6, "[便携功能] 移除：【" + info.name + "】（无售价）");
            }
        }
    }

    // 发放金币
    if (totalMeso > 0) {
        im.gainMeso(totalMeso);
    }

    // 结果汇总
    im.dropMessage(6, "[便携功能] 完成！共售出 " + totalCount + " 件装备，"
        + (totalMeso > 0 ? "获得 " + totalMeso.toLocaleString() + " 金币。" : "无可售出价值的装备已被移除。"));

    im.dispose();
}
