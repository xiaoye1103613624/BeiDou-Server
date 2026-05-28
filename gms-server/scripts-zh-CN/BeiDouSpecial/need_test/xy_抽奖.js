// =============== 配置区 ===============
var ticketId = 4030000;        // 门票物品ID
var ticketPerDraw = 1;         // 每次抽奖消耗几张门票（例如1）
var drawTimes = 5;             // 抽奖次数（可改为玩家输入）

// 奖品列表：ID + 权重
var prizes = [
    {id: 1102174, weight: 2000},  // 披风  20%
    {id: 4000001, weight: 3000},  // 金币  30%
    {id: 2022002, weight: 5000}   // 经验券 50%
];

// =============== 辅助函数 ===============

// 获取物品的背包类型（1消耗 2装备 3其他 4设置 5特殊）
function getItemType(itemId) {
    var id = parseInt(itemId);
    if (id >= 1000000 && id < 2000000) return 1;    // 装备
    if (id >= 2000000 && id < 3000000) return 2;    // 消耗
    if (id >= 3000000 && id < 4000000) return 3;    // 其他
    if (id >= 4000000 && id < 5000000) return 4;    // 设置
    if (id >= 5000000 && id < 6000000) return 5;    // 特殊
    return 3; // 默认其他
}

// 获取物品的堆叠上限（常见值，可根据需要补充）
function getMaxStack(itemId) {
    var type = getItemType(itemId);
    if (type == 1) return 1;                     // 装备不可堆叠
    if (type == 2) return 4000;                  // 消耗品多数可堆叠4000
    if (type == 3) return 4000;                  // 其他类大多可堆叠
    if (type == 4) return 100;                   // 设置类通常100
    if (type == 5) return 1;                     // 特殊类不可堆叠
    return 100;
}

// 检测玩家能否安全获得指定数量的某物品（不触发背包满）
function canAddItem(itemId, addQty) {
    var type = getItemType(itemId);
    var maxStack = getMaxStack(itemId);
    var haveQty = cm.getItemQuantity(itemId);    // 玩家当前拥有数量
    var emptySlots = cm.getEmptySlots(type);     // 该类型背包剩余格子数

    // 计算现在该物品占用了几格
    var currentSlotsUsed = Math.ceil(haveQty / maxStack);
    // 计算添加后需要几格
    var newSlotsUsed = Math.ceil((haveQty + addQty) / maxStack);
    var needExtraSlots = newSlotsUsed - currentSlotsUsed;

    // 需要的额外格子 <= 该类型剩余空格数
    return needExtraSlots <= emptySlots;
}

// =============== 主逻辑 ===============
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }

    // 1. 检查门票数量是否足够
    var needTicket = ticketPerDraw * drawTimes;
    if (!cm.haveItem(ticketId, needTicket)) {
        cm.sendOk("你需要 " + needTicket + " 张 " + cm.getItemName(ticketId) + " 才能进行 " + drawTimes + " 次抽奖。");
        cm.dispose();
        return;
    }

    // 2. 模拟抽奖，统计最终要获得的物品及数量
    var totalWeight = 0;
    for (var i = 0; i < prizes.length; i++) {
        totalWeight += prizes[i].weight;
    }

    var rewardMap = {};   // 记录 {itemId: 总数量}
    for (var t = 0; t < drawTimes; t++) {
        var rand = Math.floor(Math.random() * totalWeight);
        var cumulative = 0;
        var selectedId = null;
        for (var i = 0; i < prizes.length; i++) {
            cumulative += prizes[i].weight;
            if (rand < cumulative) {
                selectedId = prizes[i].id;
                break;
            }
        }
        if (selectedId === null) selectedId = prizes[0].id; // 安全fallback
        rewardMap[selectedId] = (rewardMap[selectedId] || 0) + 1;
    }

    // 3. 背包空间预检测
    var canAll = true;
    for (var itemId in rewardMap) {
        var qty = rewardMap[itemId];
        if (!canAddItem(parseInt(itemId), qty)) {
            canAll = false;
            cm.sendOk("背包空间不足！请清理 " + cm.getItemName(parseInt(itemId)) + " 对应的背包栏位后再试。");
            break;
        }
    }

    if (!canAll) {
        cm.dispose();
        return;
    }

    // 4. 通过检测 → 扣除门票
    cm.gainItem(ticketId, -needTicket);

    // 5. 实际发放奖励
    for (var itemId in rewardMap) {
        var qty = rewardMap[itemId];
        cm.gainItem(parseInt(itemId), qty);
    }

    // 6. 展示结果
    var resultMsg = "🎉 抽奖完成！共进行 " + drawTimes + " 次抽奖，获得：\n";
    for (var itemId in rewardMap) {
        resultMsg += cm.getItemName(parseInt(itemId)) + " × " + rewardMap[itemId] + "\n";
    }
    cm.sendOk(resultMsg);

    cm.dispose();
}