/**
 * @description 装备属性重置
 * 功能：将装备上STR+DEX+INT+LUK的总点数随机重新分配
 * 需要消耗重置卷轴（可配置）
 * 参考：参考服务端 重置属性.JS 逻辑
 */

var InventoryType = Java.type("org.gms.constants.inventory.InventoryType");
var MapleItemInformationProvider = Java.type("org.gms.server.MapleItemInformationProvider");
var PacketCreator = Java.type("org.gms.util.PacketCreator");

// 消耗的重置卷轴ID（0=不消耗道具，直接重置）
var RESET_SCROLL_ID = 0;
// 重置卷轴数量
var RESET_SCROLL_QTY = 1;

var status = -1;
var selectedEquip = null;  // 选中的装备对象
var selectedSlot = -1;     // 装备槽位

function start() {
    status = -1;
    selectedEquip = null;
    selectedSlot = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) {
        showEquipList();
    } else if (status === 1) {
        handleEquipSelection(selection);
    } else if (status === 2) {
        if (type === 1) {
            doReset();
        } else {
            cm.sendOk("已取消重置。");
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function showEquipList() {
    var player = cm.getPlayer();
    var inv = player.getInventory(InventoryType.EQUIP);
    var equips = inv.list();

    var text = "#e装备属性重置#n\r\n\r\n";
    text += "#d随机重新分配装备上STR+DEX+INT+LUK的总点数。#k\r\n";
    if (RESET_SCROLL_ID > 0) {
        text += "每次消耗：#i" + RESET_SCROLL_ID + "# × #r" + RESET_SCROLL_QTY + "#k\r\n";
    }
    text += "\r\n#b请选择要重置的装备（仅显示有基础属性的装备）：#k\r\n\r\n";

    var hasEquip = false;
    for (var i = 0; i < equips.size(); i++) {
        var item = equips.get(i);
        if (item == null) continue;
        var equip = item;
        // 只显示有属性的装备
        var total = equip.getStr() + equip.getDex() + equip.getInt() + equip.getLuk();
        if (total <= 0) continue;

        var slot = equip.getPosition();
        text += "#L" + (i + 1) + "##i" + equip.getItemId() + "# ";
        text += "[STR+" + equip.getStr() + " DEX+" + equip.getDex();
        text += " INT+" + equip.getInt() + " LUK+" + equip.getLuk() + "]#l\r\n";
        hasEquip = true;
    }

    if (!hasEquip) {
        cm.sendOk("背包中没有可重置属性的装备（需要STR/DEX/INT/LUK属性大于0）。");
        cm.dispose();
        return;
    }

    text += "\r\n#L0#取消#l\r\n";
    cm.sendSimple(text);
}

function handleEquipSelection(selection) {
    if (selection === 0) {
        cm.dispose();
        return;
    }

    var player = cm.getPlayer();
    var inv = player.getInventory(InventoryType.EQUIP);
    var equips = inv.list();

    var idx = 0;
    for (var i = 0; i < equips.size(); i++) {
        var item = equips.get(i);
        if (item == null) continue;
        var equip = item;
        var total = equip.getStr() + equip.getDex() + equip.getInt() + equip.getLuk();
        if (total <= 0) continue;
        idx++;
        if (idx === selection) {
            selectedEquip = equip;
            selectedSlot = equip.getPosition();
            break;
        }
    }

    if (selectedEquip == null) {
        cm.sendOk("#r选择的装备不存在！#k");
        cm.dispose();
        return;
    }

    // 检查消耗道具
    if (RESET_SCROLL_ID > 0) {
        var useInv = player.getInventory(InventoryType.USE);
        var have = useInv.countById(RESET_SCROLL_ID);
        if (have < RESET_SCROLL_QTY) {
            cm.sendOk("#r重置卷轴不足！#k\r\n需要：#i" + RESET_SCROLL_ID + "# × " + RESET_SCROLL_QTY
                + "，当前持有：" + have + "");
            cm.dispose();
            return;
        }
    }

    var total = selectedEquip.getStr() + selectedEquip.getDex() + selectedEquip.getInt() + selectedEquip.getLuk();
    var text = "确认重置以下装备的属性？\r\n\r\n";
    text += "#i" + selectedEquip.getItemId() + "#\r\n";
    text += "当前属性：STR+" + selectedEquip.getStr() + " DEX+" + selectedEquip.getDex()
        + " INT+" + selectedEquip.getInt() + " LUK+" + selectedEquip.getLuk() + "\r\n";
    text += "总点数：#r" + total + "#k（将被随机重新分配）\r\n\r\n";
    if (RESET_SCROLL_ID > 0) {
        text += "消耗：#i" + RESET_SCROLL_ID + "# × " + RESET_SCROLL_QTY + "\r\n\r\n";
    }
    text += "#r重置后属性随机，无法撤销！#k";
    cm.sendYesNo(text);
}

function doReset() {
    if (selectedEquip == null) {
        cm.sendOk("数据异常，请重试。");
        cm.dispose();
        return;
    }

    var player = cm.getPlayer();

    // 再次检查消耗道具
    if (RESET_SCROLL_ID > 0) {
        var useInv = player.getInventory(InventoryType.USE);
        if (useInv.countById(RESET_SCROLL_ID) < RESET_SCROLL_QTY) {
            cm.sendOk("#r重置卷轴不足！#k");
            cm.dispose();
            return;
        }
        cm.gainItem(RESET_SCROLL_ID, -RESET_SCROLL_QTY);
    }

    // 获取总点数，随机分配
    var total = selectedEquip.getStr() + selectedEquip.getDex() + selectedEquip.getInt() + selectedEquip.getLuk();
    var newStats = randomDistribute(total, 4);

    selectedEquip.setStr(newStats[0]);
    selectedEquip.setDex(newStats[1]);
    selectedEquip.setInt(newStats[2]);
    selectedEquip.setLuk(newStats[3]);

    // 通知客户端更新装备
    player.forceUpdateItem(selectedEquip);

    cm.sendOk("属性重置成功！\r\n\r\n"
        + "新属性：STR+" + newStats[0] + " DEX+" + newStats[1]
        + " INT+" + newStats[2] + " LUK+" + newStats[3] + "\r\n"
        + "总点数不变：#b" + total + "#k");
    cm.dispose();
}

/**
 * 将total点数随机分配到count个槽中（每槽至少0点）
 * 使用"折棍法"均匀随机分配
 */
function randomDistribute(total, count) {
    if (total <= 0) return [0, 0, 0, 0];

    // 生成count-1个随机切割点（0到total之间），排序后分割
    var cuts = [];
    for (var i = 0; i < count - 1; i++) {
        cuts.push(Math.floor(Math.random() * (total + 1)));
    }
    cuts.sort(function(a, b) { return a - b; });

    var result = [];
    var prev = 0;
    for (var j = 0; j < cuts.length; j++) {
        result.push(cuts[j] - prev);
        prev = cuts[j];
    }
    result.push(total - prev);
    return result;
}
