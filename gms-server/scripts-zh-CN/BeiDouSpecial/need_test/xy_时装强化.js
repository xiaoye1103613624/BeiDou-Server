// =============== xy_时装强化.js ===============
// 时装/现金道具强化系统
// 默认选中玩家装备栏第一格装备，仅限现金装备(时装)，最多强化7次
// 强化消耗花蘑菇盖+金币，成功率逐次递减

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

var ENHANCE_KEY = "cashEquipEnhance_v2";   // characterExtendValue key
var ii = ItemInformationProvider.getInstance();

// ===================== 可配置参数 =====================
var COST_ITEM_ID    = 4000001;     // 消耗物品ID（花蘑菇盖）
var COST_ITEM_COUNT = [1,2,3,4,5,6,7];   // 第n次强化消耗数量(x→x+1)
var MESO_COST       = [1*1000000,2*1000000,3*1000000,4*1000000,5*1000000,6*1000000,7*1000000]; // 第n次强化金币消耗
var MAX_ENHANCE     = 7;           // 最大强化次数
var RATES           = [100,80,60,40,20,10,5]; // 每次强化成功率(%)

// ===================== 属性加成表(★1~7) =====================
// 每级强化提供的属性(记录在持久化数据中，实际生效需配合Java端)
var STAT_BONUS = [
    {STR:0, DEX:0, INT:0, LUK:0, WATK:1, MATK:1, WDEF:0, MDEF:0, HP:0, MP:0},  // ★1
    {STR:0, DEX:0, INT:0, LUK:0, WATK:2, MATK:2, WDEF:0, MDEF:0, HP:0, MP:0},  // ★2
    {STR:1, DEX:1, INT:1, LUK:1, WATK:2, MATK:2, WDEF:5, MDEF:5, HP:0, MP:0},  // ★3
    {STR:1, DEX:1, INT:1, LUK:1, WATK:3, MATK:3, WDEF:10,MDEF:10,HP:0, MP:0},  // ★4
    {STR:2, DEX:2, INT:2, LUK:2, WATK:3, MATK:3, WDEF:15,MDEF:15,HP:10,MP:10}, // ★5
    {STR:2, DEX:2, INT:2, LUK:2, WATK:4, MATK:4, WDEF:20,MDEF:20,HP:20,MP:20}, // ★6
    {STR:3, DEX:3, INT:3, LUK:3, WATK:5, MATK:5, WDEF:25,MDEF:25,HP:30,MP:30}, // ★7
];

var status = -1;
var firstItemId = 0;
var firstSlot = 0;
var currentLevel = 0;
var targetLevel = 0;
var equippedItem = null;          // 缓存装备对象，跨action调用

// ===================== 辅助函数 =====================
function loadEnhanceData() {
    var raw = cm.getCharacterExtendValue(ENHANCE_KEY);
    if (raw && raw !== "") {
        try { return JSON.parse(raw); } catch (e) { return {}; }
    }
    return {};
}
function saveEnhanceData(data) {
    cm.saveOrUpdateCharacterExtendValue(ENHANCE_KEY, JSON.stringify(data));
}

function getFirstEquippedItem() {
    var player = cm.getPlayer();
    var equippedInv = player.getInventory(InventoryType.EQUIPPED);
    var items = equippedInv.list().toArray();
    if (items.length === 0) return null;
    return items[0];
}

function isCashEquip(itemId) {
    var stats = ii.getEquipStats(itemId);
    if (!stats) return false;
    return stats.get("cash") === 1;
}

function getSlotKey(slot) {
    return "slot_" + slot;
}

// ===================== 入口 =====================
function start() { status = -1; action(1, 0, 0); }

function action(mode, type, selection) {
    if (mode === -1) { cm.dispose(); return; }
    if (mode === 0)  { cm.dispose(); return; }
    if (mode === 1) status++;

    if (status === 0) {
        // 1. 获取装备栏第一格装备
        equippedItem = getFirstEquippedItem();
        if (!equippedItem) {
            cm.sendOk("你没有穿戴任何装备，不符合强化条件。");
            cm.dispose();
            return;
        }

        firstItemId = equippedItem.getItemId();
        firstSlot = equippedItem.getPosition();

        // 2. 检查是否为现金装备
        if (!isCashEquip(firstItemId)) {
            cm.sendOk("#b" + cm.getItemName(firstItemId) + "#k 不是现金装备(时装)，不符合强化条件。\r\n\r\n"
                + "仅支持 #r现金/时装类装备#k 进行强化。");
            cm.dispose();
            return;
        }

        // 3. 检查当前强化等级
        var data = loadEnhanceData();
        var slotKey = getSlotKey(firstSlot);
        var slotData = data[slotKey];
        currentLevel = 0;

        // 同一槽位换过装备 → 重置强化等级
        if (slotData && slotData.itemId === firstItemId) {
            currentLevel = slotData.level || 0;
        }

        // 4. 是否已满
        if (currentLevel >= MAX_ENHANCE) {
            cm.sendOk("#b" + cm.getItemName(firstItemId) + "#k 已强化至最高等级 #r★" + MAX_ENHANCE + "#k，不符合强化条件。\r\n\r\n"
                + "已累计属性:\r\n" + buildStatSummary(MAX_ENHANCE));
            cm.dispose();
            return;
        }

        targetLevel = currentLevel + 1;
        var rate = RATES[currentLevel];
        var costCount = COST_ITEM_COUNT[currentLevel];
        var mesoCost = MESO_COST[currentLevel];

        // 5. 展示强化确认界面
        var msg = "#b════ 时装强化 ════#k\r\n\r\n";
        msg += "装备: #b" + cm.getItemName(firstItemId) + "#k\r\n";
        msg += "槽位: #b" + firstSlot + "#k\r\n";
        msg += "当前等级: " + (currentLevel > 0 ? "#r★" + currentLevel + "#k" : "未强化") + "\r\n";
        msg += "目标等级: #r★" + targetLevel + "#k\r\n";
        msg += "成功概率: #b" + rate + "%#k\r\n\r\n";
        msg += "消耗材料:\r\n";
        msg += "  " + cm.getItemName(COST_ITEM_ID) + " ×#b" + costCount + "#k\r\n";
        msg += "  金币 ×#b" + mesoCost.toLocaleString() + "#k\r\n\r\n";

        if (currentLevel > 0) {
            msg += "当前属性加成:\r\n" + buildStatSummary(currentLevel) + "\r\n";
        }
        msg += "强化成功属性:\r\n" + buildStatSummary(targetLevel) + "\r\n";
        msg += "\r\n#L0#确认强化#l\r\n#L1#取消#l";
        cm.sendSimple(msg);

    } else if (status === 1) {
        if (selection === 1) {
            cm.sendOk("已取消强化。");
            cm.dispose();
            return;
        }

        // === 执行强化 ===
        var rate = RATES[currentLevel];
        var costCount = COST_ITEM_COUNT[currentLevel];
        var mesoCost = MESO_COST[currentLevel];

        // 检查材料
        if (!cm.haveItem(COST_ITEM_ID, costCount)) {
            cm.sendOk("材料不足！\r\n需要 " + cm.getItemName(COST_ITEM_ID) + " ×" + costCount
                + "\r\n你当前拥有: " + cm.getItemQuantity(COST_ITEM_ID) + " 个");
            cm.dispose();
            return;
        }

        // 检查金币
        var player = cm.getPlayer();
        if (player.getMeso() < mesoCost) {
            cm.sendOk("金币不足！\r\n需要 " + mesoCost.toLocaleString() + " 金币"
                + "\r\n你当前拥有: " + player.getMeso().toLocaleString() + " 金币");
            cm.dispose();
            return;
        }

        // 扣除材料与金币
        cm.gainItem(COST_ITEM_ID, -costCount);
        player.gainMeso(-mesoCost, false);

        // 强化判定
        var roll = Math.floor(Math.random() * 100);
        if (roll < rate) {
            // 成功
            var data = loadEnhanceData();
            var slotKey = getSlotKey(firstSlot);
            var oldCumulativeStats = getCumulativeStats(currentLevel);   // 当前已生效的累计属性
            var newCumulativeStats = getCumulativeStats(targetLevel);   // 目标累计属性

            // 推算基础WDEF/MDEF = 当前值 - 旧累计值（兼容DB已持久化旧加成的情况）
            var baseWdef = equippedItem.getWdef() - oldCumulativeStats.WDEF;
            var baseMdef = equippedItem.getMdef() - oldCumulativeStats.MDEF;

            data[slotKey] = {
                itemId: firstItemId,
                level: targetLevel,
                baseWdef: baseWdef,
                baseMdef: baseMdef,
                stats: newCumulativeStats   // 累计属性，供Java端recalcEquipStats读取
            };
            saveEnhanceData(data);

            // WDEF/MDEF直接修改Equip对象（这两项通过addItemInfo发包，不经过recalcEquipStats）
            equippedItem.setWdef(baseWdef + newCumulativeStats.WDEF);
            equippedItem.setMdef(baseMdef + newCumulativeStats.MDEF);
            cm.getPlayer().forceUpdateItem(equippedItem);

            // 通知Java端重新计算装备属性（STR/DEX/INT/LUK/WATK/MATK/HP/MP通过recalcEquipStats生效）
            cm.getPlayer().equipChanged();

            var msg = "强化成功！#r★" + targetLevel + "#k\r\n\r\n";
            msg += "#b" + cm.getItemName(firstItemId) + "#k\r\n";
            msg += "当前属性加成:\r\n" + buildStatSummary(targetLevel);
            cm.sendOk(msg);
        } else {
            // 失败
            cm.sendOk("#r强化失败！#k\r\n\r\n材料与金币已消耗，装备保留。\r\n"
                + "当前仍为 ★" + currentLevel + "，可再次尝试。");
        }
        cm.dispose();
    }
}

// 计算累计属性
function getCumulativeStats(level) {
    var stats = {STR:0, DEX:0, INT:0, LUK:0, WATK:0, MATK:0, WDEF:0, MDEF:0, HP:0, MP:0};
    for (var i = 0; i < level; i++) {
        var bonus = STAT_BONUS[i];
        for (var key in bonus) {
            stats[key] += bonus[key];
        }
    }
    return stats;
}

function buildStatSummary(level) {
    var stats = getCumulativeStats(level);
    var lines = [];
    if (stats.STR > 0) lines.push("  力量: +" + stats.STR);
    if (stats.DEX > 0) lines.push("  敏捷: +" + stats.DEX);
    if (stats.INT > 0) lines.push("  智力: +" + stats.INT);
    if (stats.LUK > 0) lines.push("  运气: +" + stats.LUK);
    if (stats.WATK > 0) lines.push("  物理攻击: +" + stats.WATK);
    if (stats.MATK > 0) lines.push("  魔法攻击: +" + stats.MATK);
    if (stats.WDEF > 0) lines.push("  物理防御: +" + stats.WDEF);
    if (stats.MDEF > 0) lines.push("  魔法防御: +" + stats.MDEF);
    if (stats.HP > 0) lines.push("  最大HP: +" + stats.HP);
    if (stats.MP > 0) lines.push("  最大MP: +" + stats.MP);
    if (lines.length === 0) return "  无属性加成";
    return lines.join("\r\n");
}
