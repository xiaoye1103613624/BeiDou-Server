/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：购买初始武器 — 不限职业，可任选武器类型购买圣诞六翼天使初始武器
 *   1. 列出全部支持的武器类型供玩家自由选择
 *   2. 初始属性：四维+20 / 攻击力+1 / 魔力+1 / 攻速+6
 *   3. 固有道具不可交换
 * ==================
 */

// ===== Java类型导入 =====
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

// ===== 武器类型前缀 → 中文名 =====
var WEAPON_TYPE_NAME = {
    130: "单手剑", 131: "单手斧", 132: "单手钝器", 133: "短剑",
    137: "短杖",   138: "长杖",   140: "双手剑",   141: "双手斧",
    142: "双手钝器", 143: "枪",   144: "矛",       145: "弓",
    146: "弩",     147: "拳套",  148: "指节",     149: "手枪"
};

// ===== 初始武器ID映射（按武器类型 → 圣诞六翼天使武器itemId） =====
var INIT_WEAPONS = {
    130: 1302105, 131: 1312039, 132: 1322065, 133: 1332081,
    137: 1372046, 138: 1382062, 140: 1402053, 141: 1412035,
    142: 1422039, 143: 1432050, 144: 1442071, 145: 1452062,
    146: 1462056, 147: 1472077, 148: 1482029, 149: 1492030
};

var ALL_WEAPON_TYPES = [130, 131, 132, 133, 137, 138, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149];

// ===== 购买费用（金币） =====
var PURCHASE_PRICE = 1000000;

var status = -1;
var curWeaponType = -1;
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";

function start() {
    status = -1;
    curWeaponType = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        showBuyMenu();
    } else if (status == 1) {
        if (selection == 99) {
            cm.dispose();
            cm.openNpc(9900001, "xy/装备系统/v002/武器中心");
            return;
        }
        if (INIT_WEAPONS[selection] == null) {
            cm.sendOk("无效的武器类型，请重新选择。");
            cm.dispose();
            return;
        }
        curWeaponType = selection;
        showConfirmDialog();
    } else if (status == 2) {
        if (type == 1) {
            doPurchase();
        } else {
            cm.sendOk("已取消购买。");
            cm.dispose();
        }
    }
}

function showBuyMenu() {
    var text = "#e购买初始武器#n\r\n\r\n";
    text += "请选择要购买的武器类型（#b不限职业#k）：\r\n\r\n";
    for (var i = 0; i < ALL_WEAPON_TYPES.length; i++) {
        var weaponType = ALL_WEAPON_TYPES[i];
        var weaponItemId = INIT_WEAPONS[weaponType];
        if (weaponItemId == null) {
            continue;
        }
        text += "#L" + weaponType + "##i" + weaponItemId + "# #b" + WEAPON_TYPE_NAME[weaponType] + "#k#l\r\n";
    }
    text += "\r\n购买价格：#r" + (PURCHASE_PRICE / 10000) + "W金币#k / 把\r\n";
    text += "#d该武器为固有道具，不可交换。#k\r\n";
    text += "#d后续可通过 武器进阶 逐级升级为更强武器。#k\r\n\r\n";
    text += "#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

function showConfirmDialog() {
    var weaponItemId = INIT_WEAPONS[curWeaponType];
    if (!cm.canHold(weaponItemId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来购买！#k");
        cm.dispose();
        return;
    }

    var text = "确认购买以下武器？\r\n\r\n";
    text += "#i" + weaponItemId + "# #b圣诞六翼天使武器（" + WEAPON_TYPE_NAME[curWeaponType] + "）#k\r\n\r\n";
    text += "初始属性：#r四维+20 / 攻击力+1 / 魔力+1 / 攻速+6#k\r\n";
    text += "价格：#r" + (PURCHASE_PRICE / 10000) + "W金币#k\r\n\r\n";
    cm.sendYesNo(text);
}

function doPurchase() {
    var weaponItemId = INIT_WEAPONS[curWeaponType];

    if (cm.getMeso() < PURCHASE_PRICE) {
        cm.sendOk("#r金币不足！#k\r\n需要 #b" + (PURCHASE_PRICE / 10000) + "W#k 金币，当前只有 #r" + Math.floor(cm.getMeso() / 10000) + "W#k 金币。");
        cm.dispose();
        return;
    }

    if (!cm.canHold(weaponItemId, 1)) {
        cm.sendOk("#r背包空间不足！#k");
        cm.dispose();
        return;
    }

    cm.gainMeso(-PURCHASE_PRICE);
    cm.gainItem(weaponItemId, 1);

    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var initEquip = equipInv.findById(weaponItemId);
    if (initEquip != null) {
        initEquip.setStr(20);
        initEquip.setDex(20);
        initEquip.setInt(20);
        initEquip.setLuk(20);
        initEquip.setWatk(1);
        initEquip.setMatk(1);
        initEquip.setSpeed(6);
        cm.getPlayer().forceUpdateItem(initEquip);
    }

    cm.sendOk("购买成功！\r\n\r\n#i" + weaponItemId + "# #b圣诞六翼天使武器（" + WEAPON_TYPE_NAME[curWeaponType] + "）#k 已放入背包。\r\n\r\n初始属性：#r四维+20 / 攻击力+1 / 魔力+1 / 攻速+6#k\r\n#d该武器为固有道具，不可交换。#k\r\n\r\n#g请前往 武器中心 → 武器进阶 进行进阶升级！#k");
    cm.dispose();
}
