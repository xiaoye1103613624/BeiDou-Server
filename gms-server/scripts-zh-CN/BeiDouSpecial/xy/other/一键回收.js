/**
 * @description 一键回收系统
 * 功能：快速出售背包中的指定类型道具，换取金币
 * 支持选择回收：白色装备、USE/ETC中的低价值道具
 */

var InventoryType = Java.type("org.gms.constants.inventory.InventoryType");

// 回收单价配置（ItemID → 单价，0表示不回收）
// 未配置的道具按默认价格回收
var DEFAULT_RECYCLE_PRICE = 100;      // 未配置道具的默认回收价（金币/个）
var WHITE_EQUIP_PRICE = 500;          // 白色装备回收价（金币/件）

var status = -1;
var recycleMode = -1;    // 0=回收ETC/USE, 1=回收白色装备

function start() {
    status = -1;
    recycleMode = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) {
        showMenu();
    } else if (status === 1) {
        recycleMode = selection;
        handleModeSelection(selection);
    } else if (status === 2) {
        if (type === 1) {
            doRecycle();
        } else {
            cm.sendOk("已取消回收。");
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function showMenu() {
    var player = cm.getPlayer();
    var etcCount = countRecyclableEtc(player);
    var equipCount = countRecyclableEquip(player);

    var text = "#e一键回收#n\r\n\r\n";
    text += "金币：#r" + new Intl.NumberFormat().format(player.getMeso()) + "#k\r\n\r\n";
    text += "#b选择回收类型：#k\r\n\r\n";
    text += "#L0#回收 ETC/USE 道具  #d约#r" + etcCount + "#k件可回收#k#l\r\n";
    text += "#L1#回收白色装备  #d约#r" + equipCount + "#k件可回收，每件 " + WHITE_EQUIP_PRICE + " 金币#k#l\r\n";
    text += "\r\n#L9#取消#l\r\n";
    cm.sendSimple(text);
}

function handleModeSelection(selection) {
    if (selection === 9) { cm.dispose(); return; }

    var player = cm.getPlayer();
    var count = 0;
    var totalMeso = 0;

    if (selection === 0) {
        // ETC和USE道具回收预估
        count = countRecyclableEtc(player);
        totalMeso = count * DEFAULT_RECYCLE_PRICE;
        cm.sendYesNo("确认回收背包中的 ETC/USE 道具？\r\n\r\n"
            + "预计回收：#r" + count + "#k件\r\n"
            + "预计获得：#r" + new Intl.NumberFormat().format(totalMeso) + "#k 金币\r\n\r\n"
            + "#d（装备、任务道具、不常见道具不会回收）#k");
    } else if (selection === 1) {
        // 白色装备回收
        count = countRecyclableEquip(player);
        totalMeso = count * WHITE_EQUIP_PRICE;
        cm.sendYesNo("确认回收背包中的白色（普通品质）装备？\r\n\r\n"
            + "预计回收：#r" + count + "#k件\r\n"
            + "预计获得：#r" + new Intl.NumberFormat().format(totalMeso) + "#k 金币\r\n\r\n"
            + "#r（蓝色/橙色/唯一品质装备不会被回收）#k");
    } else {
        cm.dispose();
    }
}

function doRecycle() {
    var player = cm.getPlayer();
    var totalGained = 0;
    var totalCount = 0;

    if (recycleMode === 0) {
        // 回收ETC和USE道具
        var etcInv = player.getInventory(InventoryType.ETC);
        var useInv = player.getInventory(InventoryType.USE);

        totalCount += recycleInventory(etcInv, function(item) {
            return true;  // ETC全回收
        }, DEFAULT_RECYCLE_PRICE);

        totalCount += recycleInventory(useInv, function(item) {
            // USE中保留特殊道具（经验卡、任务道具等）
            return !isSpecialUseItem(item.getItemId());
        }, DEFAULT_RECYCLE_PRICE);

        totalGained = totalCount * DEFAULT_RECYCLE_PRICE;
    } else if (recycleMode === 1) {
        // 回收白色装备
        var equipInv = player.getInventory(InventoryType.EQUIP);
        var equips = equipInv.list();
        for (var i = 0; i < equips.size(); i++) {
            var equip = equips.get(i);
            if (equip == null) continue;
            if (equip.getPosition() < 0) continue;  // 跳过已穿戴的装备
            // 简单判断：只有增加值全为0的装备才视为白色
            if (isWhiteEquip(equip)) {
                cm.gainItem(equip.getItemId(), -1);
                totalCount++;
            }
        }
        totalGained = totalCount * WHITE_EQUIP_PRICE;
    }

    if (totalCount > 0) {
        cm.gainMeso(totalGained);
        cm.getPlayer().dropMessage(6, "[" + cm.getPlayer().getName() + "玩家] 一键回收完成，回收 " + totalCount + " 件道具，获得 " + new Intl.NumberFormat().format(totalGained) + " 金币");
        cm.sendOk("回收完成！\r\n\r\n"
            + "回收数量：#r" + totalCount + "#k件\r\n"
            + "获得金币：#r" + new Intl.NumberFormat().format(totalGained) + "#k");
    } else {
        cm.sendOk("没有可回收的道具。");
    }
    cm.dispose();
}

/** 回收整个背包，返回回收数量 */
function recycleInventory(inv, filterFn, price) {
    var count = 0;
    var items = inv.list();
    for (var i = 0; i < items.size(); i++) {
        var item = items.get(i);
        if (item == null) continue;
        if (filterFn(item)) {
            cm.gainItem(item.getItemId(), -item.getQuantity());
            count += item.getQuantity();
        }
    }
    return count;
}

/** 统计可回收ETC/USE道具数量 */
function countRecyclableEtc(player) {
    var count = 0;
    var etcInv = player.getInventory(InventoryType.ETC);
    var useInv = player.getInventory(InventoryType.USE);
    var etcList = etcInv.list();
    var useList = useInv.list();
    for (var i = 0; i < etcList.size(); i++) {
        if (etcList.get(i) != null) count += etcList.get(i).getQuantity();
    }
    for (var j = 0; j < useList.size(); j++) {
        if (useList.get(j) != null && !isSpecialUseItem(useList.get(j).getItemId())) {
            count += useList.get(j).getQuantity();
        }
    }
    return count;
}

/** 统计可回收白色装备数量 */
function countRecyclableEquip(player) {
    var count = 0;
    var inv = player.getInventory(InventoryType.EQUIP);
    var list = inv.list();
    for (var i = 0; i < list.size(); i++) {
        var item = list.get(i);
        if (item != null && item.getPosition() >= 0 && isWhiteEquip(item)) count++;
    }
    return count;
}

/** 判断USE道具是否为特殊道具（不回收） */
function isSpecialUseItem(itemId) {
    // 经验卡(2290xxx)、技能书(2280xxx)、传送符(2030xxx) 不回收
    var prefix = Math.floor(itemId / 1000);
    return prefix === 2290 || prefix === 2280 || prefix === 2030 || prefix === 2049;
}

/** 判断是否白色装备（所有buff属性都为0） */
function isWhiteEquip(equip) {
    return equip.getStr() === 0 && equip.getDex() === 0
        && equip.getInt() === 0 && equip.getLuk() === 0
        && equip.getWatk() === 0 && equip.getMatk() === 0
        && equip.getWdef() === 0 && equip.getMdef() === 0
        && equip.getHp() === 0 && equip.getMp() === 0;
}
