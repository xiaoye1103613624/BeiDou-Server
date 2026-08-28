/**
 * @description 跑环仓库 —— 查看已存放的跑环物品、一键存入、取回物品
 * 功能：
 *   1. 只展示当前账号已存入仓库的跑环物品及数量
 *   2. "一键存入"将背包中匹配跑环池的物品自动存入（超上限部分保留在背包）
 *   3. 点击物品可输入指定数量取回（不能超过库存，不能 ≤0）
 * 入口：9900001.js case 203
 */

var PaohuanConfigManager = Java.type("org.gms.config.PaohuanConfigManager");
var WarehouseManager = Java.type("org.gms.config.WarehouseManager");
var ItemConstants = Java.type("org.gms.constants.inventory.ItemConstants");

var accountId;
var characterId;
var isShared;

// 菜单选项 Key（客户端只认正整数，负数会溢出导致匹配失败）
var SEL_BACK = 0;        // 返回上一页
var SEL_DEPOSIT = 1;     // 一键存入（避免与物品ID冲突，物品ID均为6~7位数）

// 待取回物品的上下文
var pendingItemId = 0;
var pendingInvType = 0;

function start() {
    accountId = cm.getPlayer().getAccountId();
    characterId = cm.getPlayer().getId();
    isShared = WarehouseManager.isAccountShared();
    levelMain();
}

// ==================== 主菜单 ====================

function levelMain() {
    // 查询当前账号已存放的跑环物品（按 itemId 升序）
    var items = PaohuanConfigManager.queryDepositedItems(accountId);

    var text = "#e跑环仓库#n\r\n\r\n";

    if (items.isEmpty()) {
        text += "你还没有在跑环仓库中存放任何物品。\r\n";
        text += "请通过下方【一键存入】将背包中的跑环物品存入。\r\n";
    } else {
        text += "━━━ 已存放物品（共" + items.size() + "种）━━━\r\n\r\n";
        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var itemId = item.get("itemId");
            var qty = item.get("quantity");
            text += "#L" + itemId + "##i" + itemId + "# ×#r" + qty + "#k#l";
        }
    }

    text += "\r\n";
    text += "━━━ 操作 ━━━\r\n";
    text += "#L" + SEL_DEPOSIT + "##r一键存入（背包中跑环物品→仓库）#k#l\r\n";
    text += "#L" + SEL_BACK + "##g返回上一页#k#l\r\n";

    cm.sendNextSelectLevel("HandleMain", text);
}

// ==================== 主菜单操作路由 ====================

function levelHandleMain(selection) {
    if (selection === SEL_BACK) { cm.dispose(); cm.openNpc(9900001); return; }
    if (selection === SEL_DEPOSIT) { doDepositAll(); return; }

    // 选中具体物品 → 弹出数量输入取回（客户端已将 #L<itemId># 解析为整数）
    var itemId = selection;
    var charId = isShared ? null : characterId;
    var storedQty = WarehouseManager.queryItemQuantity(accountId, charId, itemId);

    if (storedQty <= 0) {
        cm.sendOkLevel("Main", "该物品已不在仓库中。");
        return;
    }

    pendingItemId = itemId;
    pendingInvType = ItemConstants.getInventoryType(itemId).getType();

    var text = "#e取回物品#n\r\n\r\n";
    text += "#i" + itemId + "# #t" + itemId + "#\r\n";
    text += "仓库库存：#r" + storedQty + "#k 个\r\n\r\n";
    text += "请输入要取回的数量：";

    cm.getInputNumberLevel("DoWithdraw", text, 1, 1, storedQty);
}

// ==================== 取回数量输入回调 ====================

function levelDoWithdraw(inputNum) {
    doWithdrawItem(pendingInvType, pendingItemId, inputNum);
}

// ==================== 一键存入 ====================

function doDepositAll() {
    // 获取跑环物品池（白名单）
    var configItems = PaohuanConfigManager.queryEnabledItems();
    if (configItems.isEmpty()) {
        cm.sendOkLevel("Main", "跑环物品池为空，请联系管理员配置。");
        return;
    }

    var maxStack = WarehouseManager.getMaxStack();
    var charId = isShared ? null : characterId;
    var depositedList = [];  // 收集存入的物品 { itemId, qty }
    var skippedCount = 0;

    for (var i = 0; i < configItems.size(); i++) {
        var config = configItems.get(i);
        var itemId = config.getItemId();
        var invType = ItemConstants.getInventoryType(itemId).getType();

        // 获取背包中该物品的持有数量
        var heldQty = cm.getItemQuantity(itemId);
        if (heldQty <= 0) continue;

        // 计算可存入数量（不超过堆叠上限）
        var currentStored = WarehouseManager.queryItemQuantity(accountId, charId, itemId);
        var canStore = maxStack - currentStored;
        if (canStore <= 0) {
            skippedCount++;
            continue;
        }
        var storeQty = Math.min(heldQty, canStore);

        // 存入仓库并从背包移除
        if (WarehouseManager.depositItem(accountId, charId, itemId, invType, storeQty)) {
            cm.gainItem(itemId, -storeQty);
            depositedList.push({ itemId: itemId, qty: storeQty });
        } else {
            skippedCount++;
        }
    }

    var text = "#e一键存入结果#n\r\n\r\n";

    if (depositedList.length === 0) {
        text += "没有物品需要存入。\r\n";
        if (skippedCount > 0) {
            text += "#b" + skippedCount + "#k 种物品已达堆叠上限。\r\n";
        }
    } else {
        // 按 itemId 排序展示
        depositedList.sort(function(a, b) { return a.itemId - b.itemId; });
        text += "成功存入 #r" + depositedList.length + "#k 种物品：\r\n\r\n";
        for (var j = 0; j < depositedList.length; j++) {
            var d = depositedList[j];
            text += "#i" + d.itemId + "# ×#r" + d.qty + "#k\r\n";
        }
        if (skippedCount > 0) {
            text += "\r\n#b" + skippedCount + "#k 种物品已达堆叠上限，已跳过。\r\n";
        }
        text += "\r\n超出堆叠上限的部分已保留在背包中。";
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 取回指定数量 ====================

function doWithdrawItem(invType, itemId, qty) {
    var charId = isShared ? null : characterId;
    var storedQty = WarehouseManager.queryItemQuantity(accountId, charId, itemId);

    // 输入校验（getInputNumberLevel 已限制 min/max，这里做二次保护）
    if (qty <= 0) {
        cm.sendOkLevel("Main", "取回数量必须大于 0！");
        return;
    }
    if (qty > storedQty) {
        cm.sendOkLevel("Main", "取回数量不能超过库存 #r" + storedQty + "#k 个！");
        return;
    }

    // 检查背包空间
    var maxCanTake = calculateMaxCanTake(invType, itemId, qty);
    if (maxCanTake <= 0) {
        cm.sendOkLevel("Main", "背包空间不足，请先清理背包后再试。");
        return;
    }
    qty = Math.min(qty, maxCanTake);

    var taken = WarehouseManager.withdrawItem(accountId, characterId, itemId, invType, qty);
    if (taken > 0) {
        cm.gainItem(itemId, taken);
        var tip = taken < qty ? "（因背包空间限制，实际取出 #r" + taken + "#k 个）" : "";
        cm.sendOkLevel("Main", "成功取回 #r" + taken + "#k 个 #i" + itemId + "# #t" + itemId + "#。" + tip);
    } else {
        cm.sendOkLevel("Main", "取回失败，请稍后再试。");
    }
}

// ==================== 背包空间计算 ====================

/**
 * 计算背包中该物品最多能接收多少数量
 */
function calculateMaxCanTake(invType, itemId, desiredQty) {
    var inv = cm.getInventory(invType);
    // 装备类：每件占一个槽位，不可堆叠
    if (invType == 1) {
        var freeSlots = 0;
        var slotLimit = inv.getSlotLimit();
        for (var s = 1; s <= slotLimit; s++) {
            if (inv.getItem(s) == null) freeSlots++;
        }
        return Math.min(desiredQty, freeSlots);
    }

    // 非装备类：可在已有堆叠上累加，也可开新槽
    var slotMax = 100;
    var existingAbsorb = 0;
    var freeSlots = 0;
    var slotLimit = inv.getSlotLimit();

    for (var s = 1; s <= slotLimit; s++) {
        var item = inv.getItem(s);
        if (item == null) {
            freeSlots++;
        } else if (item.getItemId() == itemId) {
            existingAbsorb += (slotMax - item.getQuantity());
        }
    }

    var totalCapacity = existingAbsorb + (freeSlots * slotMax);
    return Math.min(desiredQty, Math.max(0, totalCapacity));
}
