// =============== xy_装备合成.js ===============
// 装备合成/升阶系统 —— 将原料装备+材料合成为高一级装备
// 模仿高版本"装备合成"玩法，适配GMS v083

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');

var status = -1;
var synthSlots = [];                    // 待合成列表: {slot, itemId, resultId, costItem, costCount, rate}
var selectedIdx = -1;

// =============== 合成配方配置 ===============
// 格式: baseItemId → {result, costItem, costCount, rate(成功率0~100)}
// 请根据实际装备ID修改
var SYNTH_RECIPES = {
    // ---- 武器 ----
    1302000: {result: 1302017, costItem: 4030000, costCount: 10, rate: 80},
    1312004: {result: 1312007, costItem: 4030000, costCount: 10, rate: 75},
    1322000: {result: 1322001, costItem: 4030000, costCount: 8,  rate: 80},
    1402000: {result: 1402001, costItem: 4030000, costCount: 10, rate: 75},
    1382000: {result: 1382001, costItem: 4030000, costCount: 10, rate: 80},
    1452000: {result: 1452001, costItem: 4030000, costCount: 10, rate: 75},
    1472000: {result: 1472001, costItem: 4030000, costCount: 10, rate: 75},
    1492000: {result: 1492001, costItem: 4030000, costCount: 10, rate: 75},
    // ---- 防具 ----
    1002000: {result: 1002001, costItem: 4030000, costCount: 5,  rate: 85},
    1040001: {result: 1040002, costItem: 4030000, costCount: 5,  rate: 85},
    1060001: {result: 1060002, costItem: 4030000, costCount: 5,  rate: 85},
    1072000: {result: 1072001, costItem: 4030000, costCount: 5,  rate: 85},
    1082000: {result: 1082001, costItem: 4030000, costCount: 5,  rate: 85},
};

// =============== 入口 ===============
function start() { status = -1; action(1, 0, 0); }

function action(mode, type, selection) {
    if (mode === -1) { cm.dispose(); return; }
    if (mode === 0) { cm.dispose(); return; }
    if (mode === 1) status++;

    if (status === 0) {
        cm.sendSimple("#b装备合成系统#k\r\n#L0#查看可合成装备#l\r\n#L1#查看全部配方#l");
    } else if (status === 1) {
        if (selection === 0) {
            var player = cm.getPlayer();
            var equipInv = player.getInventory(InventoryType.EQUIP);
            var menu = "可合成的装备:\r\n";
            synthSlots = [];
            var idx = 0;

            equipInv.lockInventory();
            try {
                var items = equipInv.list().toArray();
                for (var i = 0; i < items.length; i++) {
                    var itemId = items[i].getItemId();
                    var recipe = SYNTH_RECIPES[itemId];
                    if (recipe) {
                        menu += "#L" + idx + "#" + cm.getItemName(itemId)
                            + " → #b" + cm.getItemName(recipe.result) + "#k"
                            + " (成功率:" + recipe.rate + "%"
                            + " 材料:" + cm.getItemName(recipe.costItem) + "×" + recipe.costCount
                            + ")#l\r\n";
                        synthSlots.push({
                            slot: items[i].getPosition(),
                            itemId: itemId,
                            resultId: recipe.result,
                            costItem: recipe.costItem,
                            costCount: recipe.costCount,
                            rate: recipe.rate
                        });
                        idx++;
                    }
                }
            } finally {
                equipInv.unlockInventory();
            }

            if (idx === 0) {
                cm.sendOk("装备背包中没有可合成的装备。");
                cm.dispose();
                return;
            }
            cm.sendSimple(menu);
        } else if (selection === 1) {
            var msg = "#b全部合成配方#k\r\n\r\n";
            for (var base in SYNTH_RECIPES) {
                var r = SYNTH_RECIPES[base];
                msg += cm.getItemName(parseInt(base)) + " → #b" + cm.getItemName(r.result) + "#k";
                msg += " | 成功率:" + r.rate + "%";
                msg += " | " + cm.getItemName(r.costItem) + "×" + r.costCount;
                msg += "\r\n";
            }
            cm.sendOk(msg);
            cm.dispose();
        }
    } else if (status === 2) {
        selectedIdx = selection;
        var info = synthSlots[selection];
        var msg = "【合成确认】\r\n";
        msg += "原料: #b" + cm.getItemName(info.itemId) + "#k\r\n";
        msg += "产物: #r" + cm.getItemName(info.resultId) + "#k\r\n";
        msg += "成功率: #b" + info.rate + "%#k | 材料: " + cm.getItemName(info.costItem) + "×" + info.costCount;
        msg += "\r\n\r\n#b注意：合成后原料装备将消失#k\r\n";
        msg += "#L0#确认合成#l\r\n#L1#取消#l";
        cm.sendSimple(msg);
    } else if (status === 3) {
        if (selection === 1) {
            cm.sendOk("已取消。");
            cm.dispose();
            return;
        }
        var info = synthSlots[selectedIdx];

        // 检查材料
        if (!cm.haveItem(info.costItem, info.costCount)) {
            cm.sendOk("材料不足！需要 " + cm.getItemName(info.costItem) + " ×" + info.costCount);
            cm.dispose();
            return;
        }

        // 扣除材料
        cm.gainItem(info.costItem, -info.costCount);

        // 成功判定
        var roll = Math.floor(Math.random() * 100);
        if (roll < info.rate) {
            // 移除原料装备
            var player = cm.getPlayer();
            var c = player.getClient();
            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIP, info.slot, 1, false);
            // 给予产物
            cm.gainItem(info.resultId, 1);
            cm.sendOk("合成成功！获得 #r" + cm.getItemName(info.resultId) + "#k！");
        } else {
            cm.sendOk("合成失败！材料已消耗，原料装备保留。");
        }
        cm.dispose();
    }
}
