/**
 * @description 时装升星系统
 * 功能：消耗材料对时装进行+1到+10星强化，成功率随星级降低
 * 参考：参考服务端 时装升星~~.js 材料表+强化表逻辑
 */

var InventoryType = Java.type("org.gms.constants.inventory.InventoryType");

// 升星材料表：不同星级段需要的材料和金币
// [星级上限, 材料ItemID, 材料数量, 金币消耗]
var MATERIAL_TABLE = [
    { maxStar: 3,  itemId: 4000021, qty: 5,  meso: 50000  },   // +1~+3 需要碎矿石
    { maxStar: 6,  itemId: 4000021, qty: 10, meso: 150000 },   // +4~+6 需要更多
    { maxStar: 9,  itemId: 4000024, qty: 5,  meso: 500000 },   // +7~+9 需要宝石矿石
    { maxStar: 10, itemId: 4000024, qty: 10, meso: 1000000 }   // +10 需要更多宝石矿石
];

// 强化成功率表：索引=当前星级（0=+0→+1, 9=+9→+10）
var SUCCESS_RATES = [90, 85, 80, 70, 60, 50, 40, 30, 20, 10];

// 存储升星信息的ExtendValue key（存JSON）
var STAR_KEY_PREFIX = "fashion_star_";

var status = -1;
var selectedEquip = null;
var currentStar = 0;

function start() {
    status = -1;
    selectedEquip = null;
    currentStar = 0;
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
            doEnhance();
        } else {
            cm.sendOk("已取消升星。");
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

    var text = "#e时装升星系统#n\r\n\r\n";
    text += "#b消耗材料和金币对时装进行升星强化！#k\r\n";
    text += "#d最高可升至+10星，失败不掉星。#k\r\n\r\n";
    text += "金币：#r" + new Intl.NumberFormat().format(player.getMeso()) + "#k\r\n\r\n";
    text += "请选择要升星的时装（仅显示时装类装备）：\r\n\r\n";

    var hasFashion = false;
    var idx = 1;
    for (var i = 0; i < equips.size(); i++) {
        var item = equips.get(i);
        if (item == null) continue;
        var itemId = item.getItemId();
        // 时装类：1002xxx(帽), 1040xxx/1050xxx(上衣/套装), 1060xxx(下装), 1072xxx(鞋), 1082xxx(手套), 1102xxx(披风)
        if (!isFashionItem(itemId)) continue;

        var star = getStarLevel(itemId, item.getPosition());
        text += "#L" + idx + "##i" + itemId + "# ";
        text += "(+" + star + "星)";
        if (star >= 10) text += " #r[已满星]#k";
        text += "#l\r\n";
        hasFashion = true;
        idx++;
    }

    if (!hasFashion) {
        cm.sendOk("背包中没有可升星的时装！\r\n\r\n#d时装类别：帽子/上衣/套装/下装/鞋/手套/披风#k");
        cm.dispose();
        return;
    }

    text += "\r\n#L0#取消#l\r\n";
    cm.sendSimple(text);
}

function handleEquipSelection(selection) {
    if (selection === 0) { cm.dispose(); return; }

    var player = cm.getPlayer();
    var inv = player.getInventory(InventoryType.EQUIP);
    var equips = inv.list();

    var idx = 0;
    for (var i = 0; i < equips.size(); i++) {
        var item = equips.get(i);
        if (item == null) continue;
        if (!isFashionItem(item.getItemId())) continue;
        idx++;
        if (idx === selection) {
            selectedEquip = item;
            currentStar = getStarLevel(item.getItemId(), item.getPosition());
            break;
        }
    }

    if (selectedEquip == null) {
        cm.sendOk("#r选择的装备不存在！#k");
        cm.dispose();
        return;
    }

    if (currentStar >= 10) {
        cm.sendOk("该时装已满星（+10），无法继续升星！");
        cm.dispose();
        return;
    }

    // 获取本次升星所需材料
    var mat = getMaterial(currentStar);
    var useInv = player.getInventory(InventoryType.USE);
    var etcInv = player.getInventory(InventoryType.ETC);
    var haveQty = (useInv.countById(mat.itemId) + etcInv.countById(mat.itemId));
    var rate = SUCCESS_RATES[currentStar];

    var text = "时装升星确认\r\n\r\n";
    text += "#i" + selectedEquip.getItemId() + "# 当前：#b+" + currentStar + "星#k → #r+" + (currentStar + 1) + "星#k\r\n\r\n";
    text += "成功率：#r" + rate + "%#k\r\n";
    text += "消耗材料：#i" + mat.itemId + "# × #r" + mat.qty + "#k （持有：" + haveQty + "）\r\n";
    text += "消耗金币：#r" + new Intl.NumberFormat().format(mat.meso) + "#k\r\n\r\n";
    if (haveQty < mat.qty) {
        text += "#r材料不足！#k";
        cm.sendOk(text);
        cm.dispose();
        return;
    }
    if (player.getMeso() < mat.meso) {
        text += "#r金币不足！#k";
        cm.sendOk(text);
        cm.dispose();
        return;
    }
    text += "失败不掉星，是否升星？";
    cm.sendYesNo(text);
}

function doEnhance() {
    if (selectedEquip == null) { cm.dispose(); return; }

    var player = cm.getPlayer();
    var mat = getMaterial(currentStar);
    var rate = SUCCESS_RATES[currentStar];

    // 再次检查
    var useInv = player.getInventory(InventoryType.USE);
    var etcInv = player.getInventory(InventoryType.ETC);
    var haveQty = useInv.countById(mat.itemId) + etcInv.countById(mat.itemId);
    if (haveQty < mat.qty || player.getMeso() < mat.meso) {
        cm.sendOk("#r材料或金币不足！#k");
        cm.dispose();
        return;
    }

    // 扣除材料和金币
    cm.gainItem(mat.itemId, -mat.qty);
    cm.gainMeso(-mat.meso);

    // 判断成功
    var roll = Math.floor(Math.random() * 100) + 1;
    var success = roll <= rate;

    if (success) {
        var newStar = currentStar + 1;
        saveStarLevel(selectedEquip.getItemId(), selectedEquip.getPosition(), newStar);
        cm.getPlayer().dropMessage(6, "[" + cm.getPlayer().getName() + "玩家] 时装升星成功！+" + newStar + "星");
        cm.sendOk("升星成功！\r\n\r\n"
            + "#i" + selectedEquip.getItemId() + "#\r\n"
            + "★" + currentStar + " → #r★" + newStar + "#k\r\n\r\n"
            + "恭喜！" + (newStar >= 10 ? "#r已达到最高星级！#k" : "继续加油！"));
    } else {
        cm.sendOk("💔 升星失败...\r\n\r\n"
            + "#i" + selectedEquip.getItemId() + "#\r\n"
            + "当前仍为：#b★" + currentStar + "#k\r\n\r\n"
            + "消耗已扣除，失败不掉星，请再接再厉！");
    }
    cm.dispose();
}

/** 判断是否时装类装备 */
function isFashionItem(itemId) {
    var prefix = Math.floor(itemId / 10000);
    // 100=帽, 104=上衣, 105=套装, 106=下装, 107=鞋, 108=手套, 110=披风
    return prefix === 100 || prefix === 104 || prefix === 105 ||
           prefix === 106 || prefix === 107 || prefix === 108 || prefix === 110;
}

/** 获取材料配置 */
function getMaterial(currentStar) {
    for (var i = 0; i < MATERIAL_TABLE.length; i++) {
        if (currentStar < MATERIAL_TABLE[i].maxStar) return MATERIAL_TABLE[i];
    }
    return MATERIAL_TABLE[MATERIAL_TABLE.length - 1];
}

/** 读取星级（从CharacterExtendValue存储） */
function getStarLevel(itemId, slot) {
    var key = STAR_KEY_PREFIX + itemId + "_" + slot;
    var val = cm.getCharacterExtendValue(key);
    if (val != null && val !== "" && val !== "null") {
        try { return parseInt(val, 10); } catch (e) {}
    }
    return 0;
}

/** 保存星级 */
function saveStarLevel(itemId, slot, star) {
    var key = STAR_KEY_PREFIX + itemId + "_" + slot;
    cm.saveOrUpdateCharacterExtendValue(key, String(star), false);
}
