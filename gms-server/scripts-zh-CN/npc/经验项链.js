/**
 * 阿里山守护者项链（经验项链）制作 & 成长
 * NPC：匠人街 · 武器中心(9031003) → openNpc(..., "经验项链")
 *
 * 升级规则：
 *   - 每次：STR/DEX/INT/LUK +1，攻击+0，魔力+0（预留后期改）
 *   - 逆袭银币消耗：5 / 10 / 20 / 30 / 40 ...（第1次5，之后 10×当前等级）
 *   - 等级记在装备 level 字段（byte）
 */

var InventoryType = Java.type("org.gms.client.inventory.InventoryType");

var NECKLACE_ID = 1122311;     // 阿里山守护者项链
var COIN_ID = 4032181;         // 逆袭银币（逆奥银币）
var MAX_LEVEL = 40;

var BLUE_SHELL = 4000000;
var RED_SHELL = 4000016;
var GREEN_SHELL = 4000019;

var CRAFT_MATERIALS = [
    [BLUE_SHELL, 50],
    [RED_SHELL, 50],
    [GREEN_SHELL, 50],
    [COIN_ID, 5]
];

// 预留：后期可改每级攻击/魔力增量
var GAIN_STR = 1;
var GAIN_DEX = 1;
var GAIN_INT = 1;
var GAIN_LUK = 1;
var GAIN_WATK = 0;
var GAIN_MATK = 0;

var status = 0;
var menu = -1;

function coinCost(curLevel) {
    // 0→1:5, 1→2:10, 2→3:20, 3→4:30, 4→5:40 ...
    if (curLevel <= 0) {
        return 5;
    }
    return 10 * curLevel;
}

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 1) {
        var text = "#e#b阿里山守护者项链成长#k#n\r\n\r\n";
        text += "每次升级：#r四维各+1#k，攻击/魔力 #d+0（预留）#k\r\n";
        text += "银币消耗：#b5 / 10 / 20 / 30 / 40 ...#k\r\n";
        text += "逆袭银币可在 #b炼金术魔法书#k 用经验兑换（100万经验=1个）。\r\n\r\n";
        text += "#L1##b制作项链#k（蜗牛壳×50×3 + 逆袭银币×5）#l\r\n";
        text += "#L2##b升级项链#k（装备栏第1格）#l\r\n";
        text += "#L0##g返回#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 2) {
        menu = selection;
        if (menu === 0) {
            cm.dispose();
            cm.openNpc(9031003, "xy/匠人街/武器中心");
            return;
        }
        if (menu === 1) {
            showCraftConfirm();
        } else if (menu === 2) {
            showUpgradeConfirm();
        } else {
            cm.dispose();
        }
    } else if (status === 3) {
        if (menu === 1) {
            doCraft();
        } else if (menu === 2) {
            doUpgrade();
        } else {
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function showCraftConfirm() {
    if (cm.haveItem(NECKLACE_ID, 1)) {
        cm.sendOk("你已经拥有 #v" + NECKLACE_ID + "# #t" + NECKLACE_ID + "#，无需重复制作。");
        cm.dispose();
        return;
    }
    if (!cm.canHold(NECKLACE_ID, 1)) {
        cm.sendOk("请保证装备栏至少有1个空位。");
        cm.dispose();
        return;
    }
    var text = "制作 #v" + NECKLACE_ID + "# #t" + NECKLACE_ID + "# 需要：\r\n\r\n";
    for (var i = 0; i < CRAFT_MATERIALS.length; i++) {
        text += "#v" + CRAFT_MATERIALS[i][0] + "# #t" + CRAFT_MATERIALS[i][0] + "# ×" + CRAFT_MATERIALS[i][1] + "\r\n";
    }
    text += "\r\n是否确认制作？";
    cm.sendYesNo(text);
}

function doCraft() {
    for (var i = 0; i < CRAFT_MATERIALS.length; i++) {
        if (!cm.haveItem(CRAFT_MATERIALS[i][0], CRAFT_MATERIALS[i][1])) {
            cm.sendOk("材料不足，无法制作。");
            cm.dispose();
            return;
        }
    }
    for (var j = 0; j < CRAFT_MATERIALS.length; j++) {
        cm.gainItem(CRAFT_MATERIALS[j][0], -CRAFT_MATERIALS[j][1]);
    }
    cm.gainItem(NECKLACE_ID, 1);
    var eq = cm.getPlayer().getInventory(InventoryType.EQUIP).findById(NECKLACE_ID);
    if (eq != null) {
        eq.setLevel(0);
        cm.getPlayer().forceUpdateItem(eq);
    }
    cm.sendOk("制作成功！#v" + NECKLACE_ID + "# 已放入背包。\r\n请将项链放到装备栏第1格后再来升级。");
    cm.dispose();
}

function getNecklaceSlot1() {
    var item = cm.getInventory(1).getItem(1);
    if (item == null || item.getItemId() !== NECKLACE_ID) {
        return null;
    }
    return item;
}

function showUpgradeConfirm() {
    var necklace = getNecklaceSlot1();
    if (necklace == null) {
        cm.sendOk("请将 #v" + NECKLACE_ID + "# #t" + NECKLACE_ID + "# 放到背包装备栏 #r第1格#k 后再来升级。");
        cm.dispose();
        return;
    }
    var cur = necklace.getLevel() & 0xFF;
    if (cur >= MAX_LEVEL) {
        cm.sendOk("项链已达最高成长等级（" + MAX_LEVEL + "）。");
        cm.dispose();
        return;
    }
    var cost = coinCost(cur);
    var text = "当前成长等级：#r" + cur + "#k / " + MAX_LEVEL + "\r\n";
    text += "当前属性：力" + necklace.getStr() + " 敏" + necklace.getDex()
        + " 智" + necklace.getInt() + " 运" + necklace.getLuk()
        + " 攻" + necklace.getWatk() + " 魔" + necklace.getMatk() + "\r\n\r\n";
    text += "升级后增加：#b四维各+" + GAIN_STR + "#k，攻击+" + GAIN_WATK + "，魔力+" + GAIN_MATK + "\r\n";
    text += "消耗：#v" + COIN_ID + "# #t" + COIN_ID + "# ×#r" + cost + "#k\r\n";
    text += "（持有：" + cm.getPlayer().getItemQuantity(COIN_ID, false) + "）\r\n\r\n";
    text += "是否确认升级？";
    cm.sendYesNo(text);
}

function doUpgrade() {
    var necklace = getNecklaceSlot1();
    if (necklace == null) {
        cm.sendOk("未找到装备栏第1格的项链，升级已取消。");
        cm.dispose();
        return;
    }
    var cur = necklace.getLevel() & 0xFF;
    if (cur >= MAX_LEVEL) {
        cm.sendOk("已达最高等级。");
        cm.dispose();
        return;
    }
    var cost = coinCost(cur);
    if (!cm.haveItem(COIN_ID, cost)) {
        cm.sendOk("逆袭银币不足，需要 #r" + cost + "#k 个。\r\n可到炼金术魔法书用经验兑换。");
        cm.dispose();
        return;
    }
    cm.gainItem(COIN_ID, -cost);

    necklace.setStr((necklace.getStr() + GAIN_STR) & 0xFFFF);
    necklace.setDex((necklace.getDex() + GAIN_DEX) & 0xFFFF);
    necklace.setInt((necklace.getInt() + GAIN_INT) & 0xFFFF);
    necklace.setLuk((necklace.getLuk() + GAIN_LUK) & 0xFFFF);
    if (GAIN_WATK !== 0) {
        necklace.setWatk((necklace.getWatk() + GAIN_WATK) & 0xFFFF);
    }
    if (GAIN_MATK !== 0) {
        necklace.setMatk((necklace.getMatk() + GAIN_MATK) & 0xFFFF);
    }
    necklace.setLevel(cur + 1);
    cm.getPlayer().forceUpdateItem(necklace);

    cm.sendOk("升级成功！当前成长等级：#r" + (cur + 1) + "#k\r\n"
        + "属性：力" + necklace.getStr() + " 敏" + necklace.getDex()
        + " 智" + necklace.getInt() + " 运" + necklace.getLuk()
        + " 攻" + necklace.getWatk() + " 魔" + necklace.getMatk());
    cm.dispose();
}
