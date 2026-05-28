// =============== xy_时装洗练.js ===============
// 时装洗练系统 - 从现金背包读取未锁定时装, 消耗金币随机生成/重铸词条属性
//
// 机制说明:
// - 读取现金(CASH)背包中所有时装, 排除已锁定的物品
// - 每次洗练生成1~3个随机词条, 词条数只会增加不会减少(最多3个)
// - 10种词条类型, 5个等级(①②③④⑤), 各有不同概率权重
// - 可锁定词条(最多2个), 锁定词条在洗练时保留, 但费用大幅增加
// - 洗练数据持久化存储, 装备实际属性需Java端配合读取生效

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
var ii = ItemInformationProvider.getInstance();

// ===================== 可配置参数 =====================
var REFINE_KEY = "fashionRefine_v2"; // characterExtendValue存储key

// 词条类型及权重(权重总和≈340, 各词条概率=权重/总权重)
var AFFIX_TYPES   = ["力", "敏", "智", "运", "全", "攻", "魔", "仙", "圣", "神"];
var AFFIX_WEIGHTS = [ 80,   80,   80,   80,   10,   3,    3,    2,   1.5,  0.5];

// 词条等级权重: ①60% ②31% ③5% ④3% ⑤1%
var LEVEL_WEIGHTS = [60, 31, 5, 3, 1];
var LEVEL_SYMBOLS  = ["①", "②", "③", "④", "⑤"];

// 词条属性值[等级1, 等级2, 等级3, 等级4, 等级5] (等级对应LEVEL_SYMBOLS索引)
var AFFIX_STATS = {
    "力": { STR: [1,2,3,5,7], DEX: [1,2,3,4,5] },
    "敏": { DEX: [1,2,3,4,5], STR: [1,2,3,4,5] },
    "智": { INT: [1,2,4,6,8] },
    "运": { LUK: [1,2,3,4,5], DEX: [1,2,3,4,5] },
    "全": { STR: [1,2,3,5,7], DEX: [1,2,3,5,7], INT: [1,2,3,5,7], LUK: [1,2,3,5,7], WATK: [1,2,3,4,5], MATK: [1,2,3,4,5] },
    "攻": { WATK: [1,2,3,5,7] },
    "魔": { MATK: [2,4,6,8,10] },
    "仙": { STR: [1,2,3,4,5], DEX: [1,2,3,4,5], INT: [1,2,3,5,7], LUK: [1,2,3,4,5], WATK: [1,2,3,4,5], MATK: [1,2,3,5,7] },
    "圣": { STR: [1,2,3,4,5], DEX: [1,2,3,4,5], INT: [1,2,3,4,5], LUK: [1,2,3,4,5], WATK: [1,2,3,4,5], MATK: [1,2,3,4,5] },
    "神": { STR: [2,3,4,5,6], DEX: [2,3,4,5,6], INT: [2,3,4,5,6], LUK: [2,3,4,5,6], WATK: [2,3,4,5,6], MATK: [2,3,4,5,6] }
};

// 词条/属性显示名
var AFFIX_DISPLAY = { "力":"力量", "敏":"敏捷", "智":"智力", "运":"运气", "全":"全属性", "攻":"攻击", "魔":"魔力", "仙":"仙人", "圣":"圣灵", "神":"神灵" };
var STAT_DISPLAY  = { "STR":"力量", "DEX":"敏捷", "INT":"智力", "LUK":"运气", "WATK":"攻击力", "MATK":"魔力", "WDEF":"物防", "MDEF":"魔防", "HP":"HP", "MP":"MP" };

// 费用配置
var COST_BASE    = 1000000;   // 0锁: 100W
var COST_LOCK_1  = 10000000;  // 1锁: 1000W
var COST_LOCK_2  = 20000000;  // 2锁: 2000W
var MAX_AFFIX    = 3;         // 最大词条数
var MAX_LOCK     = 2;         // 最大可锁定数

// ===================== 状态变量 =====================
var status = -1;
var fashionItems  = [];   // [{equip, itemId, slot}]
var selectedIdx   = -1;
var selectedEquip = null;
var selectedItemId = -1;
var selectedSlot  = -1;
var slotKey       = "";
var allData       = {};
var itemData      = null;   // 已保存的洗练数据
var tempAffixes   = [];     // 洗练临时结果(未确认)

// ===================== 数据操作 =====================
function loadData() {
    var raw = cm.getCharacterExtendValue(REFINE_KEY);
    if (raw && raw !== "") {
        try { return JSON.parse(raw); } catch (e) { return {}; }
    }
    return {};
}
function saveData(data) { cm.saveOrUpdateCharacterExtendValue(REFINE_KEY, JSON.stringify(data)); }

// ===================== 装备操作 =====================
function getFashionItems() {
    var cashInv = cm.getPlayer().getInventory(InventoryType.CASH);
    var items = cashInv.list().toArray();
    var result = [];
    for (var i = 0; i < items.length; i++) {
        var eq = items[i];
        // 仅处理时装(现金装备), 排除已锁定的物品
        if (isCashEquip(eq.getItemId()) && (eq.getFlag() & 1) !== 1) {
            result.push({equip: eq, itemId: eq.getItemId(), slot: eq.getPosition()});
        }
    }
    return result;
}
function isCashEquip(itemId) {
    var stats = ii.getEquipStats(itemId);
    if (!stats) return false;
    return stats.get("cash") === 1;
}
function getSlotKey(slot) { return "cash_" + slot; }
function getItemData() {
    if (!allData[slotKey]) return null;
    var d = allData[slotKey];
    if (d.itemId !== selectedItemId) return null; // 装备已更换
    return d;
}

// ===================== 词条随机 =====================
function weightedIndex(weights) {
    var total = 0;
    for (var i = 0; i < weights.length; i++) total += weights[i];
    var roll = Math.random() * total;
    var cum = 0;
    for (var i = 0; i < weights.length; i++) {
        cum += weights[i];
        if (roll < cum) return i;
    }
    return weights.length - 1;
}
function rollAffixType(excludeTypes) {
    var availTypes = [], availWeights = [];
    for (var i = 0; i < AFFIX_TYPES.length; i++) {
        if (!excludeTypes || !excludeTypes[AFFIX_TYPES[i]]) {
            availTypes.push(AFFIX_TYPES[i]);
            availWeights.push(AFFIX_WEIGHTS[i]);
        }
    }
    if (availTypes.length === 0) return null;
    return availTypes[weightedIndex(availWeights)];
}
function rollAffixLevel() { return weightedIndex(LEVEL_WEIGHTS); }

/** 生成新词条列表: 保留锁定的, 其余重新随机; 词条数量只增不减 */
function rollAffixes(savedAffixes) {
    var lockedList = [];
    var excludeTypes = {};
    for (var i = 0; i < savedAffixes.length; i++) {
        if (savedAffixes[i].locked) {
            lockedList.push({type: savedAffixes[i].type, level: savedAffixes[i].level});
            excludeTypes[savedAffixes[i].type] = true;
        }
    }
    // 词条总数: 保底=当前数量(至少1), 上限=MAX_AFFIX, 等概率随机
    var minTotal = Math.max(1, savedAffixes.length);
    var possible = [];
    for (var c = minTotal; c <= MAX_AFFIX; c++) possible.push(c);
    var totalCount = possible[Math.floor(Math.random() * possible.length)];
    // 先放锁定的
    var result = [];
    for (var i = 0; i < lockedList.length; i++) {
        result.push({type: lockedList[i].type, level: lockedList[i].level});
    }
    // 随机补充
    while (result.length < totalCount) {
        var t = rollAffixType(excludeTypes);
        if (!t) break;
        excludeTypes[t] = true;
        result.push({type: t, level: rollAffixLevel()});
    }
    return result;
}

// ===================== 属性计算 =====================
function calcAffixStat(affix) {
    var config = AFFIX_STATS[affix.type];
    if (!config) return {};
    var lv = affix.level;
    var result = {};
    for (var key in config) {
        var arr = config[key];
        if (lv < arr.length) result[key] = arr[lv];
    }
    return result;
}
function calcTotalStats(affixes) {
    var total = {STR:0,DEX:0,INT:0,LUK:0,WATK:0,MATK:0,WDEF:0,MDEF:0,HP:0,MP:0};
    for (var i = 0; i < affixes.length; i++) {
        var b = calcAffixStat(affixes[i]);
        for (var k in b) { if (total[k] !== undefined) total[k] += b[k]; }
    }
    return total;
}

// ===================== 锁定/费用 =====================
function countLocked(affixes) {
    if (!affixes) return 0;
    var n = 0;
    for (var i = 0; i < affixes.length; i++) { if (affixes[i].locked) n++; }
    return n;
}
function getRefineCost(affixes) {
    var n = countLocked(affixes);
    if (n >= 2) return COST_LOCK_2;
    if (n === 1) return COST_LOCK_1;
    return COST_BASE;
}
function formatMeso(amount) {
    if (amount >= 100000000) return (amount / 100000000).toFixed(1) + "亿";
    return (amount / 10000) + "W";
}

// ===================== 显示构建 =====================
function buildItemListText() {
    var msg = "#b════ 时装洗练 ════#k\r\n\r\n请选择要洗练的时装:\r\n";
    for (var i = 0; i < fashionItems.length; i++) {
        msg += "#L" + i + "##b" + cm.getItemName(fashionItems[i].itemId) + "#k (槽位" + fashionItems[i].slot + ")#l\r\n";
    }
    return msg;
}

function buildMainMenuText() {
    var msg = "#b════ 时装洗练 ════#k\r\n\r\n";
    msg += "装备: #b" + cm.getItemName(selectedItemId) + "#k (槽位" + selectedSlot + ")\r\n";
    // 当前词条
    var affixes = (itemData && itemData.affixes) ? itemData.affixes : [];
    if (affixes.length === 0) {
        msg += "当前词条: #r未洗练#k\r\n";
    } else {
        msg += "当前词条:\r\n";
        for (var i = 0; i < affixes.length; i++) {
            var name = AFFIX_DISPLAY[affixes[i].type] || affixes[i].type;
            var sym = LEVEL_SYMBOLS[affixes[i].level] || "?";
            var lockStr = affixes[i].locked ? " #r[锁定]#k" : "";
            msg += "  [" + name + sym + "]" + lockStr + "\r\n";
        }
        // 属性汇总
        var total = calcTotalStats(affixes);
        var statLines = [];
        var keys = ["STR","DEX","INT","LUK","WATK","MATK"];
        for (var j = 0; j < keys.length; j++) {
            var k = keys[j];
            if (total[k] > 0) statLines.push(STAT_DISPLAY[k] + "+" + total[k]);
        }
        if (statLines.length > 0) msg += "累计属性: #g" + statLines.join(", ") + "#k\r\n";
    }
    // 锁定&费用
    var lc = countLocked(affixes);
    msg += "\r\n锁定词条: " + lc + "/" + MAX_LOCK + "\r\n";
    msg += "洗练费用: #r" + formatMeso(getRefineCost(affixes)) + "金币#k\r\n\r\n";
    // 选项
    msg += "#L0#开始洗练#l\r\n";
    if (affixes.length > 0) {
        msg += "#L1#管理锁定词条#l\r\n";
    }
    msg += "#L2#退出#l";
    return msg;
}

function buildLockMenuText() {
    var affixes = itemData.affixes;
    var lc = countLocked(affixes);
    var msg = "#b════ 管理锁定 ════#k\r\n\r\n";
    msg += "当前锁定: " + lc + "/" + MAX_LOCK + " 个词条\r\n";
    msg += "锁定后洗练时该词条#r不会变动#k, 但费用大幅增加\r\n\r\n";
    for (var i = 0; i < affixes.length; i++) {
        var name = AFFIX_DISPLAY[affixes[i].type] || affixes[i].type;
        var sym = LEVEL_SYMBOLS[affixes[i].level] || "?";
        if (affixes[i].locked) {
            msg += "#L" + i + "#[" + name + sym + "] #r[锁定]#k → 点击解锁#l\r\n";
        } else if (lc < MAX_LOCK) {
            msg += "#L" + i + "#[" + name + sym + "] → 点击锁定#l\r\n";
        } else {
            msg += "  [" + name + sym + "] #r已达最大锁定数#k\r\n";
        }
    }
    msg += "\r\n#L98#返回主菜单#l";
    return msg;
}

function buildResultText(affixes) {
    var msg = "#b════ 洗练结果 ════#k\r\n\r\n";
    msg += "装备: #b" + cm.getItemName(selectedItemId) + "#k\r\n\r\n";
    msg += "新词条:\r\n";
    for (var i = 0; i < affixes.length; i++) {
        msg += formatAffixDetail(affixes[i]) + "\r\n";
    }
    var total = calcTotalStats(affixes);
    msg += "\r\n属性合计:\r\n";
    var keys = ["STR","DEX","INT","LUK","WATK","MATK"];
    var hasAny = false;
    for (var j = 0; j < keys.length; j++) {
        var k = keys[j];
        if (total[k] > 0) {
            msg += "  #g" + STAT_DISPLAY[k] + ": +" + total[k] + "#k\r\n";
            hasAny = true;
        }
    }
    if (!hasAny) msg += "  无属性加成\r\n";
    var oldAffixes = (itemData && itemData.affixes) ? itemData.affixes : [];
    msg += "\r\n#L0#确认保存属性 (当前词条将#r永久生效#k)#l\r\n";
    msg += "#L1#继续洗练 (放弃本次结果, 费用#r" + formatMeso(getRefineCost(oldAffixes)) + "金币#k)#l\r\n";
    msg += "#L2#取消 (#r放弃本次结果#k, 保留旧词条)#l";
    return msg;
}

function formatAffixDetail(affix) {
    var name = AFFIX_DISPLAY[affix.type] || affix.type;
    var sym = LEVEL_SYMBOLS[affix.level] || "?";
    var bonus = calcAffixStat(affix);
    var parts = [];
    for (var k in bonus) {
        var sn = STAT_DISPLAY[k] || k;
        parts.push(sn + "+" + bonus[k]);
    }
    return "  [" + name + sym + "] " + parts.join(", ");
}

// ===================== 核心操作 =====================
function applyAndSave(affixes) {
    // 物品在背包中未装备, 仅保存洗练数据, 属性需Java端读取后应用到装备
    itemData.affixes = affixes;
    itemData.itemId = selectedItemId;
    allData[slotKey] = itemData;
    saveData(allData);
}

function doRefine() {
    var savedAffixes = (itemData && itemData.affixes) ? itemData.affixes.slice() : [];
    // 检查材料-金币
    var cost = getRefineCost(savedAffixes);
    var player = cm.getPlayer();
    if (player.getMeso() < cost) {
        cm.sendOk("金币不足！\r\n需要 #r" + formatMeso(cost) + "金币#k\r\n你当前拥有: #b" + formatMeso(player.getMeso()) + "金币#k");
        return false;
    }
    // 扣除金币
    player.gainMeso(-cost, false);
    // 生成新词条
    tempAffixes = rollAffixes(savedAffixes);
    return true;
}

// ===================== 入口 =====================
function start() { status = -1; action(1, 0, 0); }

function action(mode, type, selection) {
    // ---- 全局mode处理 ----
    if (mode === -1) { cm.dispose(); return; }
    if (mode === 0) {
        // 洗练结果界面点关闭 → 丢弃临时结果返回主菜单
        if (tempAffixes.length > 0) {
            tempAffixes = [];
            status = 1; // 设置为1, 下面直接显示主菜单(不走递增)
            cm.sendSimple(buildMainMenuText());
            return;
        }
        // 锁定管理界面(status=4时点关闭) → 返回主菜单
        if (status === 3) { // 锁定菜单在status=2时发出, status被手动设为3
            status = 1;
            cm.sendSimple(buildMainMenuText());
            return;
        }
        cm.dispose();
        return;
    }
    status++;

    // ===== status 0: 显示装备选择列表 =====
    if (status === 0) {
        fashionItems = getFashionItems();
        if (fashionItems.length === 0) {
            cm.sendOk("现金背包中没有可洗练的时装。\r\n请确保时装在现金背包中且未锁定。");
            cm.dispose();
            return;
        }
        cm.sendSimple(buildItemListText());
        return;
    }

    // ===== status 1: 处理装备选择 → 显示主菜单 =====
    if (status === 1) {
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= fashionItems.length) { cm.dispose(); return; }
        var fi = fashionItems[selectedIdx];
        selectedEquip  = fi.equip;
        selectedItemId = fi.itemId;
        selectedSlot   = fi.slot;
        slotKey        = getSlotKey(selectedSlot);
        allData  = loadData();
        itemData = getItemData();
        // 首次访问: 初始化空数据
        if (!itemData) {
            itemData = { itemId: selectedItemId, affixes: [] };
            allData[slotKey] = itemData;
        }
        // 清空临时结果
        tempAffixes = [];
        cm.sendSimple(buildMainMenuText());
        return;
    }

    // ===== status 2: 处理主菜单选择 =====
    if (status === 2) {
        if (selection === 0) {
            // --- 开始洗练 ---
            if (!doRefine()) { cm.dispose(); return; }
            cm.sendSimple(buildResultText(tempAffixes));
            // status保持2, next→3 处理洗练结果
        } else if (selection === 1) {
            // --- 管理锁定 ---
            if (!itemData || !itemData.affixes || itemData.affixes.length === 0) {
                cm.sendOk("当前没有词条可以管理锁定。\r\n请先进行一次洗练获得词条。");
                cm.dispose();
                return;
            }
            cm.sendSimple(buildLockMenuText());
            status = 3; // next→4 处理锁定选择
        } else {
            // --- 退出 ---
            cm.dispose();
        }
        return;
    }

    // ===== status 3: 处理洗练结果选择 =====
    if (status === 3) {
        if (selection === 0) {
            // --- 确认保存 ---
            applyAndSave(tempAffixes);
            var savedStats = calcTotalStats(tempAffixes);
            var msg = "#b════ 保存成功 ════#k\r\n\r\n";
            msg += "装备: #b" + cm.getItemName(selectedItemId) + "#k\r\n\r\n";
            msg += "已生效属性:\r\n" + formatStatSummary(savedStats);
            tempAffixes = [];
            cm.sendOk(msg);
            status = 98; // next→99 dispose
        } else if (selection === 1) {
            // --- 继续洗练 ---
            tempAffixes = []; // 丢弃本次临时结果
            if (!doRefine()) { cm.dispose(); return; }
            cm.sendSimple(buildResultText(tempAffixes));
            status = 2; // next→3 再次处理洗练结果
        } else {
            // --- 取消(保留旧词条) ---
            tempAffixes = [];
            cm.sendSimple(buildMainMenuText());
            status = 1; // next→2 处理主菜单
        }
        return;
    }

    // ===== status 4: 处理锁定切换 =====
    if (status === 4) {
        if (selection === 98) {
            // 返回主菜单
            cm.sendSimple(buildMainMenuText());
            status = 1;
            return;
        }
        // 切换指定词条的锁定状态
        var affixes = itemData.affixes;
        if (selection >= 0 && selection < affixes.length) {
            if (affixes[selection].locked) {
                affixes[selection].locked = false;
            } else if (countLocked(affixes) < MAX_LOCK) {
                affixes[selection].locked = true;
            }
            // 立即保存锁定状态
            saveData(allData);
        }
        cm.sendSimple(buildMainMenuText());
        status = 1; // next→2 处理主菜单
        return;
    }

    // ===== 终态: 保存成功后自动关闭 =====
    if (status >= 98) {
        cm.dispose();
        return;
    }
}

// ===================== 格式化工具 =====================
function formatStatSummary(stats) {
    var lines = [];
    var keys = ["STR","DEX","INT","LUK","WATK","MATK","WDEF","MDEF"];
    for (var i = 0; i < keys.length; i++) {
        var k = keys[i];
        if (stats[k] > 0) lines.push("  " + STAT_DISPLAY[k] + ": +" + stats[k]);
    }
    if (lines.length === 0) return "  无属性加成";
    return lines.join("\r\n");
}
