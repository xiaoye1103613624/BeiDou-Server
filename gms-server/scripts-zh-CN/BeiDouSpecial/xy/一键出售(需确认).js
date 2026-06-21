/**
 * @description 一键出售系统（需确认版）
 * 功能：出售背包后72格（slot 24~95）的道具，前24格（3行）保留不售
 *      选择背包栏后会先列出待出售道具数量与预计获得金币，确认后才执行出售
 * 入口：道具 02432074_sell_confirm 触发
 */
var text;
var column = ["装备", "消耗", "设置", "其他", "商城"];
var sel;
// 后72格：slot 24 ~ 95（0索引），前24格保留
var SELL_START = 24;
var SELL_END = 95;

function start() {
    levelStart();
}

// 对话开始
function levelStart() {
    text = "#e一键出售：出售栏位后72格道具#n\r\n\r\n";
    text += "#d（前24格 / 3行保留不售，出售前会先确认）#k\r\n\r\n";
    for (let i = 1; i <= 5; i++) {
        text += "#L" + i + "#出售" + column[i - 1] + "栏后72格的道具#l\r\n";
    }
    cm.sendNextSelectLevel("ChooseInventory", text);
}

// 选择了背包栏，统计待出售道具并弹出确认窗口
function levelChooseInventory(choose) {
    sel = choose;
    const ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
    var ii = ItemInformationProvider.getInstance();

    var count = 0;
    var totalMeso = 0;
    var confirmText = "#e即将出售" + column[sel - 1] + "栏后72格的以下道具：#n\r\n\r\n";
    for (let i = SELL_START; i <= SELL_END; i++) {
        let item = cm.getInventory(sel).getItem(i);
        if (item) {
            var price = ii.getPrice(item.getItemId(), item.getQuantity());
            confirmText += "#b#t" + item.getItemId() + "##k × " + item.getQuantity() + "  #r" + price + "#k 金币\r\n";
            count++;
            totalMeso += price;
        }
    }

    if (count === 0) {
        cm.sendOkLevel("Start", column[sel - 1] + "栏后72格没有可出售的道具！");
        return;
    }

    confirmText += "\r\n共 #r" + count + "#k 件，预计获得金币 #r" + totalMeso + "#k\r\n\r\n是否确认出售？";
    cm.sendYesNoLevel("Start", "DoSell", confirmText);
}

// 确认后执行出售
function levelDoSell() {
    const ShopFactory = Java.type('org.gms.server.ShopFactory');
    const InventoryType = Java.type('org.gms.client.inventory.InventoryType');
    // 根据选择的背包栏映射 InventoryType
    var type = InventoryType.EQUIP;       // 1=装备
    if (sel == 2) { type = InventoryType.USE; }         // 2=消耗
    else if (sel == 3) { type = InventoryType.SETUP; }  // 3=设置
    else if (sel == 4) { type = InventoryType.ETC; }    // 4=其他
    else if (sel == 5) { type = InventoryType.CASH; }   // 5=商城
    // 遍历后72格（slot 24 ~ 95）
    for (let i = SELL_START; i <= SELL_END; i++) {
        let item = cm.getInventory(sel).getItem(i);
        if (item) {
            ShopFactory.getInstance().getShop(11000).sell(cm.getClient(), type, i, item.getQuantity());
        }
    }
    cm.sendOk("出售" + column[sel - 1] + "栏后72格成功！");
    cm.dispose();
}
