/**
 * @description 仓库管理系统
 * 功能：分类查看、存入/取出物品、一键存入/取出
 * 依赖：WarehouseManager (Java), WarehouseService (Java)
 * 使用：通过 9900001.js 导航菜单 → case 166 → 仓库
 */

var WarehouseManager = Java.type("org.gms.config.WarehouseManager");
var INV_TYPE_NAMES = ["", "装备", "消耗", "设置", "其他", "现金"];
var accountId;
var characterId;
var isShared;

// 全局上下文变量（用于多步骤操作间传递状态）
var pendingInvType = 0;
var pendingItemId = 0;

function start() {
    accountId = cm.getPlayer().getAccountId();
    characterId = cm.getPlayer().getId();
    isShared = WarehouseManager.isAccountShared();
    levelMain();
}

// ==================== 主菜单 ====================

/**
 * 主菜单 —— 选择物品栏分类
 */
function levelMain() {
    var text = "#e仓库管理#n\r\n\r\n";
    text += "请选择要查看的分类：\r\n";
    for (var i = 1; i <= 5; i++) {
        text += "#L" + (i - 1) + "##e" + INV_TYPE_NAMES[i] + "栏仓库#n#l\r\n";
    }
    text += "\r\n";
    text += "#L5##r返回首页#k#l\r\n";
    cm.sendNextSelectLevel("ShowType", text);
}

/**
 * 显示选中分类的仓库物品
 * @param selection 0=装备 1=消耗 2=设置 3=其他 4=现金 5=返回
 */
function levelShowType(selection) {
    if (selection == 5) {
        cm.dispose();
        return;
    }
    var invType = parseInt(selection) + 1;
    pendingInvType = invType;

    // 查询仓库物品
    var charId = isShared ? null : characterId;
    var warehouseItems = WarehouseManager.queryItems(accountId, charId, invType);

    var text = "#e" + INV_TYPE_NAMES[invType] + "栏仓库#n\r\n\r\n";

    if (warehouseItems.isEmpty()) {
        text += "该分类下暂无物品。\r\n\r\n";
    } else {
        text += "━━━ 仓库物品（点击取出）━━━\r\n";
        for (var i = 0; i < warehouseItems.size(); i++) {
            var wItem = warehouseItems.get(i);
            var itemId = wItem.getItemId();
            var qty = wItem.getQuantity();
            text += "#L" + itemId + "##i" + itemId + "# #t" + itemId + "# × #r" + qty + "#k#l\r\n";
        }
        text += "\r\n";
    }

    // 操作按钮（使用0/1/2避免与物品ID冲突，物品ID均≥1000000）
    text += "━━━ 快捷操作 ━━━\r\n";
    text += "#L1##r一键存入" + INV_TYPE_NAMES[invType] + "栏物品#k#l\r\n";
    text += "#L2##b一键取出" + INV_TYPE_NAMES[invType] + "栏物品#k#l\r\n";
    text += "\r\n";
    text += "#L0##g返回分类选择#k#l\r\n";

    cm.sendNextSelectLevel("HandleAction_" + invType, text);
}

// ==================== 操作路由 ====================

function levelHandleAction_1(selection) { handleAction(1, selection); }
function levelHandleAction_2(selection) { handleAction(2, selection); }
function levelHandleAction_3(selection) { handleAction(3, selection); }
function levelHandleAction_4(selection) { handleAction(4, selection); }
function levelHandleAction_5(selection) { handleAction(5, selection); }

// 特殊选择值常量（与levelShowType中的#L值对应，物品ID均≥1000000不会冲突）
var SEL_BACK = 0;         // 返回分类选择
var SEL_DEPOSIT_ALL = 1;  // 一键存入
var SEL_WITHDRAW_ALL = 2; // 一键取出

function handleAction(invType, selection) {
    if (selection == SEL_BACK) {
        levelMain();
        return;
    }
    if (selection == SEL_DEPOSIT_ALL) {
        doDepositAll(invType);
        return;
    }
    if (selection == SEL_WITHDRAW_ALL) {
        doWithdrawAll(invType);
        return;
    }

    // 选中具体物品 → 默认取物品，弹出数量输入
    var itemId = parseInt(selection);
    var charId = isShared ? null : characterId;
    var storedQty = WarehouseManager.queryItemQuantity(accountId, charId, itemId);

    if (storedQty <= 0) {
        cm.sendOkLevel("Main", "该物品已不在仓库中。");
        return;
    }

    // 保存上下文，供 levelDoWithdraw 使用
    pendingInvType = invType;
    pendingItemId = itemId;

    var text = "取出物品\r\n";
    text += "#i" + itemId + "# #t" + itemId + "#\r\n";
    text += "仓库数量：#r" + storedQty + "#k\r\n";
    text += "请输入取出数量：";

    // 使用固定前缀 "DoWithdraw"，由 levelDoWithdraw(inputNum) 统一处理
    cm.getInputNumberLevel("DoWithdraw", text, 1, 1, storedQty);
}

/**
 * 处理取出数量输入（由 getInputNumberLevel 回调）
 */
function levelDoWithdraw(inputNum) {
    doWithdrawItem(pendingInvType, pendingItemId, inputNum);
}

// ==================== 一键存入 ====================

function doDepositAll(invType) {
    var inv = cm.getInventory(invType);
    var items = inv.list().toArray();
    var depositedCount = 0;
    var skippedCount = 0;
    var maxStack = WarehouseManager.getMaxStack();
    var charId = isShared ? null : characterId;

    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item == null) continue;
        var itemId = item.getItemId();
        var itemQty = item.getQuantity();

        // 检查是否在白名单中
        if (!WarehouseManager.canStoreItem(itemId)) {
            skippedCount++;
            continue;
        }

        // 计算可存入数量（不超过堆叠上限）
        var currentStored = WarehouseManager.queryItemQuantity(accountId, charId, itemId);
        var canStore = maxStack - currentStored;
        if (canStore <= 0) {
            skippedCount++;
            continue;
        }
        var storeQty = Math.min(itemQty, canStore);

        // 存入仓库并从背包移除
        if (WarehouseManager.depositItem(accountId, characterId, itemId, invType, storeQty)) {
            cm.gainItem(itemId, -storeQty);
            depositedCount++;
        } else {
            skippedCount++;
        }
    }

    var text = "一键存入完成！\r\n";
    text += "成功存入：#r" + depositedCount + "#k 种物品\r\n";
    if (skippedCount > 0) {
        text += "跳过：#b" + skippedCount + "#k 种物品（不在白名单或已达上限）\r\n";
    }
    cm.sendOkLevel("Main", text);
}

// ==================== 一键取出 ====================

function doWithdrawAll(invType) {
    var charId = isShared ? null : characterId;
    var warehouseItems = WarehouseManager.queryItems(accountId, charId, invType);

    if (warehouseItems.isEmpty()) {
        cm.sendOkLevel("Main", "该分类下没有物品。");
        return;
    }

    var withdrawnCount = 0;
    var skippedCount = 0;
    var inv = cm.getInventory(invType);

    for (var i = 0; i < warehouseItems.size(); i++) {
        var wItem = warehouseItems.get(i);
        var itemId = wItem.getItemId();
        var storedQty = wItem.getQuantity();

        if (storedQty <= 0) continue;

        // 计算实际能取出的数量（考虑背包空间和堆叠上限）
        var maxCanTake = calculateMaxCanTake(inv, invType, itemId, storedQty);
        if (maxCanTake <= 0) {
            skippedCount++;
            continue;
        }

        // 从仓库取出并加入背包
        var taken = WarehouseManager.withdrawItem(accountId, characterId, itemId, invType, maxCanTake);
        if (taken > 0) {
            cm.gainItem(itemId, taken);
            withdrawnCount++;
        } else {
            skippedCount++;
        }
    }

    var text = "一键取出完成！\r\n";
    text += "成功取出：#r" + withdrawnCount + "#k 种物品\r\n";
    if (skippedCount > 0) {
        text += "跳过：#b" + skippedCount + "#k 种物品（背包空间不足或已达上限）\r\n";
    }
    cm.sendOkLevel("Main", text);
}

// ==================== 取出指定数量 ====================

function doWithdrawItem(invType, itemId, qty) {
    var charId = isShared ? null : characterId;
    var storedQty = WarehouseManager.queryItemQuantity(accountId, charId, itemId);

    if (qty > storedQty) {
        qty = storedQty;
    }

    // 检查背包空间和堆叠上限
    var inv = cm.getInventory(invType);
    var maxCanTake = calculateMaxCanTake(inv, invType, itemId, qty);
    if (maxCanTake <= 0) {
        cm.sendOkLevel("Main", "背包空间不足，无法取出该物品。\r\n请先清理背包后再试。");
        return;
    }
    qty = Math.min(qty, maxCanTake);

    var taken = WarehouseManager.withdrawItem(accountId, characterId, itemId, invType, qty);
    if (taken > 0) {
        cm.gainItem(itemId, taken);
        cm.sendOkLevel("Main", "成功取出 #r" + taken + "#k 个 #i" + itemId + "# #t" + itemId + "#。");
    } else {
        cm.sendOkLevel("Main", "取物品失败，请稍后再试。");
    }
}

// ==================== 空间计算 ====================

/**
 * 计算背包中该物品最多能接收多少数量
 * 考虑因素：空槽数量、现有堆叠的空间
 *
 * @param inv 背包 Inventory 对象
 * @param invType 物品栏类型（1=装备）
 * @param itemId 物品ID
 * @param desiredQty 期望取出的数量
 * @return 实际最多可取出的数量
 */
function calculateMaxCanTake(inv, invType, itemId, desiredQty) {
    // 装备类物品：每个占一个槽位，且不可堆叠
    if (invType == 1) {
        var freeSlots = 0;
        var slotLimit = inv.getSlotLimit();
        for (var s = 1; s <= slotLimit; s++) {
            if (inv.getItem(s) == null) freeSlots++;
        }
        return Math.min(desiredQty, freeSlots);
    }

    // 非装备类物品：可在已有堆叠上累加，也可开新槽
    // 使用一个保守的 slotMax（游戏物品的默认单格上限通常为 100）
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
