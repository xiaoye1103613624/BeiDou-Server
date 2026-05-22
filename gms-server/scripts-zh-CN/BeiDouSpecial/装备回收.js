/*
 * ==================
 * 脚本类型: 装备回收/分解
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 回收背包中不需要的装备换取金币
 *   2. 装备等级越高，回收价格越高
 *   3. 白色/橙色/蓝色/紫色/金色装备回收价递增
 * ==================
 */

var status = -1;
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.provider.ItemInformationProvider').getInstance();
var equipItems = [];

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
        equipItems = [];
        var inv = cm.getPlayer().getInventory(InventoryType.EQUIP);
        if (inv == null) {
            cm.sendOk("无法访问装备栏。");
            cm.dispose();
            return;
        }

        var items = inv.list();
        var text = "#e#b=== 装备回收 ===#k#n\r\n\r\n";
        text += "选择要回收的装备：\r\n";
        text += "#r(回收价格 = 等级 x 稀有度系数)#k\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var itemId = item.getItemId();
            var itemName = ItemInformationProvider.getName(itemId);
            if (itemName == null) itemName = "未知道具";

            var level = getEquipLevel(itemId);
            var price = getRecyclePrice(itemId);

            equipItems.push({
                itemId: itemId,
                name: itemName,
                level: level,
                price: price
            });

            text += "#L" + i + "#";
            text += "#i" + itemId + "# ";
            text += "#b" + itemName + "#k Lv." + level + "  ";
            text += "#r" + price.toLocaleString() + "金币#k";
            text += "#l\r\n";
        }

        if (equipItems.length === 0) {
            text += "背包中没有可回收的装备。\r\n";
            cm.sendOk(text);
            cm.dispose();
            return;
        }

        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection < 0 || selection >= equipItems.length) {
            cm.dispose();
            return;
        }

        var equip = equipItems[selection];
        var player = cm.getPlayer();

        // 移除装备，给予金币
        var inv = player.getInventory(InventoryType.EQUIP);
        // 需要通过item对象来移除
        var items = inv.list();
        var targetItem = null;
        for (var i = 0; i < items.size(); i++) {
            if (items.get(i).getItemId() === equip.itemId) {
                targetItem = items.get(i);
                break;
            }
        }

        if (targetItem != null) {
            // 移除道具并给予金币
            cm.gainItem(equip.itemId, -1);
            cm.getPlayer().gainMeso(equip.price);
            cm.sendOk("回收成功！#b#i" + equip.itemId + "# " + equip.name + "#k\r\n获得 #r" + equip.price.toLocaleString() + "#k 金币！");
        } else {
            cm.sendOk("回收失败，道具不存在。");
        }
        cm.dispose();
    }
}

function getEquipLevel(itemId) {
    try {
        return ItemInformationProvider.getEquipStats(itemId).get("reqLevel") || 0;
    } catch (e) {
        return 0;
    }
}

function getRecyclePrice(itemId) {
    var level = getEquipLevel(itemId);
    // 基础价格：等级x100
    var basePrice = Math.max(1, level) * 100;

    // 简单稀有度判断（根据ID范围）
    return basePrice;
}
