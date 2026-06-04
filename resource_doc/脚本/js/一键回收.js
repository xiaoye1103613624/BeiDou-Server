load('nashorn:mozilla_compat.js');
importPackage(Packages.util);
importPackage(Packages.client.inventory);
importPackage(Packages.server.life);

var status;
var h1 = -1; // 用于存储玩家选择的装备索引

// 分解装备的配置（示例）
var 分解 = [
    { 代码: 1122174, 积分: 100 }, // 示例装备ID和对应的积分
    { 代码: 1002, 积分: 200 },
    { 代码: 1003, 积分: 300 }
];

function start() {
    status = -1;
    cm.sendSimple("#r一键回收系统#k\r\n#L0#回收指定装备#l");
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
        cm.dispose();
        return;
    }

    switch (status) {
        case 0:
            if (selection == 0) {
                // 显示可回收装备列表
                var inventory = cm.getInventory(Packages.client.inventory.MapleInventoryType.EQUIP);
                var items = inventory.list(); // 假设返回的是一个数组
                var message = "请选择要回收的装备：\r\n";
                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    message += "#L" + i + "##i" + item.getItemId() + "# " + item.getName() + "#l\r\n";
                }
                cm.sendSimple(message);
            }
            break;
        case 1:
            // 玩家选择装备
            h1 = selection; // 保存玩家选择的装备索引
            分解装备B(h1); // 调用分解装备函数
            cm.dispose();
            break;
    }
}

function 更改积分(sum) {
    var chr = cm.getPlayer();
    sqlMultiPurpose("UPDATE characters SET ps = ps + " + sum + " WHERE id = " + chr.getId() + "");
    if (sum >= 1) {
        cm.getPlayer().dropMessage(5, "获得：" + sum + " 积分(BOSS积分商店专属)");
    } else if (sum < 0) {
        cm.getPlayer().dropMessage(5, "消费：" + sum + " 积分(BOSS积分商店专属)");
    }
}

function 分解装备B(sele1) {
    // 获取玩家的装备库存
    var inventory = cm.getInventory(Packages.client.inventory.MapleInventoryType.EQUIP); // 确保使用正确的库存类型
    var items = inventory.list(); // 获取库存中的所有装备

    // 打印日志以确认库存中的装备数量
    cm.log("库存中的装备数量：" + items.length);

    // 检查玩家选择的装备索引是否有效
    if (sele1 < 0 || sele1 >= items.length) {
        cm.sendOk("选择的装备索引无效，请重新选择。");
        return;
    }

    // 获取玩家选择的装备
    var Eq = items[sele1]; // 使用数组索引访问装备

    // 检查装备是否为 null
    if (Eq == null) {
        cm.sendOk("无法找到该装备，请检查后重试。");
        return;
    }

    // 获取装备的ID
    var itemId = Eq.getItemId();

    // 检查装备是否在分解列表中
    for (var i = 0; i < 分解.length; i++) {
        if (itemId == 分解[i].代码) {
            更改积分(分解[i].积分); // 调用更改积分函数
            cm.removeSlot(Packages.client.inventory.MapleInventoryType.EQUIP.ordinal(), sele1, 1); // 移除装备
            cm.sendOk("装备已成功回收，获得积分：" + 分解[i].积分);
            return;
        }
    }

    // 如果装备不在分解列表中
    cm.sendOk("该装备无法回收。");
}