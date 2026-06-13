/**
 * @description 玩具收集系统
 *   1. 按分类浏览收集列表，每分类展示物品ID、需求数量、背包持有量
 *   2. 支持单个提交（每物品旁[提交1个]按钮）和一键提交（分类底部）
 *   3. 提交时从背包扣除对应物品，完成后发放奖励
 *   4. 角色进度隔离（CharacterExtendValue日清持久化）
 *   5. 分类切换时刷新背包匹配列表
 * 依赖：ToyCollectionConfigManager (Java)
 * 入口：9900001.js case 301
 */

var ToyCollectionConfigManager = Java.type("org.gms.config.ToyCollectionConfigManager");

// ==================== 常量 ====================
var EXTEND_KEY = "玩具收集";          // 持久化Key
var EQUIP_INV = 1;                   // 装备栏类型
var SEL_BACK = 0;                    // 返回上一页
var SEL_SUBMIT_ALL = 998;            // 一键提交本分类

// 当前浏览的分类ID（跨level共享）
var currentCategoryId = 0;

// ==================== 入口 ====================
function start() {
    levelMain();
}

// ==================== 数据持久化 ====================
/**
 * 加载玩家收集进度
 * 格式：{ "itemConfigId": submittedQuantity, ... }
 */
function loadData() {
    var raw = cm.getCharacterExtendValue(EXTEND_KEY, true);
    if (raw && raw !== "" && raw !== "null") {
        try { return JSON.parse(String(raw)); } catch (e) {}
    }
    return {};
}

function saveData(data) {
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, JSON.stringify(data), true);
}

// ==================== Level: 主菜单 - 分类列表 ====================
function levelMain() {
    var categories = ToyCollectionConfigManager.queryEnabledCategories();
    var progress = loadData();

    var text = "\t\t#r#e< 玩具收集中心 >#k#n\r\n\r\n";
    text += "#d收集指定装备和道具，完成目标领取奖励！#k\r\n\r\n";

    if (categories.isEmpty()) {
        text += "#r暂无可用收集分类，请联系管理员配置。#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "━━━ 请选择收集分类 ━━━\r\n\r\n";

    for (var i = 0; i < categories.size(); i++) {
        var cat = categories.get(i);
        var catId = cat.getId();
        // 统计该分类完成/总数
        var items = ToyCollectionConfigManager.queryItemsByCategory(catId);
        var completed = 0;
        for (var j = 0; j < items.size(); j++) {
            var cfg = items.get(j);
            var submitted = progress[String(cfg.getId())] || 0;
            if (submitted >= cfg.getRequiredQuantity()) completed++;
        }
        text += "#L" + catId + "##b" + cat.getName() + "#k";
        text += "   进度：#b" + completed + "#k/#r" + items.size() + "#k\r\n";
        text += "#l\r\n";
    }

    text += "\r\n#L0##g返回首页#k#l\r\n";
    cm.sendNextSelectLevel("HandleMain", text);
}

// ==================== 选择分类 → 进入物品列表 ====================
function levelHandleMain(selection) {
    if (selection === SEL_BACK) {
        cm.dispose();
        cm.openNpc(9900001);
        return;
    }
    if (selection === SEL_SUBMIT_ALL) {
        // 从刷新页回来，走正常流程
        levelMain();
        return;
    }

    currentCategoryId = selection;
    showCategoryItems(currentCategoryId);
}

// ==================== 显示分类物品列表 ====================
function showCategoryItems(categoryId) {
    var items = ToyCollectionConfigManager.queryItemsByCategory(categoryId);
    var progress = loadData();

    if (items.isEmpty()) {
        cm.sendOkLevel("Main", "#r该分类下暂无收集物品配置。#k\r\n请联系管理员添加收集物品。");
        return;
    }

    // 获取分类名称
    var categories = ToyCollectionConfigManager.queryEnabledCategories();
    var catName = "分类" + categoryId;
    for (var i = 0; i < categories.size(); i++) {
        if (categories.get(i).getId() == categoryId) {
            catName = categories.get(i).getName();
            break;
        }
    }

    // 枚举背包中的匹配物品（按itemId统计）
    var backpackMatches = countBackpackMatches(items);

    var text = "\t\t#r#e< " + catName + " >#k#n\r\n\r\n";
    text += "━━━ 收集物品列表（背包匹配：装备栏+道具栏）━━━\r\n\r\n";

    for (var i = 0; i < items.size(); i++) {
        var cfg = items.get(i);
        var cfgId = cfg.getId();
        var itemId = cfg.getItemId();
        var required = cfg.getRequiredQuantity();
        var submitted = progress[String(cfgId)] || 0;
        var holding = backpackMatches[String(itemId)] || 0;
        var completed = submitted >= required;

        text += "#b" + (i + 1) + ". #k#i" + itemId + "# #t" + itemId + "#\r\n";
        text += "   进度：" + makeProgressBar(submitted, required)
            + " #r" + submitted + "#k/#b" + required + "#k";

        if (completed) {
            text += "  #g[✓已完成]#k";
        }
        text += "\r\n";

        text += "   背包持有：#b" + holding + "#k 个";

        // 未完成且有货 → 显示[提交1个]按钮
        if (!completed && holding > 0) {
            text += "  #L" + cfgId + "##r[提交1个]#k#l";
        }
        text += "\r\n\r\n";
    }

    // 一键提交按钮
    text += "\r\n#L" + SEL_SUBMIT_ALL + "##r一键提交本分类全部物品#k#l\r\n";
    text += "#L" + SEL_BACK + "##g返回分类列表#k#l\r\n";

    cm.sendNextSelectLevel("HandleItemAction", text);
}

// ==================== 处理物品操作 ====================
function levelHandleItemAction(selection) {
    if (selection === SEL_BACK) {
        levelMain();
        return;
    }
    if (selection === SEL_SUBMIT_ALL) {
        submitAllInCategory();
        return;
    }
    // 单个提交：selection = itemConfigId
    submitOne(selection);
}

// ==================== 单个提交 ====================
function submitOne(itemConfigId) {
    var progress = loadData();
    var cfg = ToyCollectionConfigManager.queryItemById(itemConfigId);

    if (cfg == null) {
        cm.sendOkLevel("RefreshCategory", "物品配置不存在，请联系管理员。");
        return;
    }

    var submitted = progress[String(itemConfigId)] || 0;
    if (submitted >= cfg.getRequiredQuantity()) {
        cm.sendOkLevel("RefreshCategory", "该物品已收集完成！");
        return;
    }

    // 从背包扣除1个
    if (!deductFromInventory(cfg.getItemId(), 1)) {
        cm.sendOkLevel("RefreshCategory", "背包中没有足够的 #t" + cfg.getItemId() + "#！");
        return;
    }

    // 更新进度
    submitted += 1;
    progress[String(itemConfigId)] = submitted;
    saveData(progress);

    // 构建结果消息
    var text = "#b提交成功！#k\r\n\r\n";
    text += "#i" + cfg.getItemId() + "# #t" + cfg.getItemId() + "# ×1\r\n";
    text += "进度：#b" + submitted + "#k / #r" + cfg.getRequiredQuantity() + "#k\r\n";

    // 检查是否完成并发奖励
    if (submitted >= cfg.getRequiredQuantity()) {
        text += "\r\n#g★ 收集完成！#k\r\n";
        if (cfg.getRewardItemId() > 0) {
            if (cm.canHold(cfg.getRewardItemId(), cfg.getRewardQuantity())) {
                cm.gainItem(cfg.getRewardItemId(), cfg.getRewardQuantity());
                text += "奖励已发放： #i" + cfg.getRewardItemId() + "# #t" + cfg.getRewardItemId() + "# ×#r" + cfg.getRewardQuantity() + "#k\r\n";
            } else {
                text += "#r奖励发放失败：背包空间不足，请清理背包后联系管理员补发！#k\r\n";
            }
        }
    }

    // 返回物品列表（自动刷新）
    cm.sendOkLevel("RefreshCategory", text);
}

// ==================== 一键提交 ====================
function submitAllInCategory() {
    var progress = loadData();
    var items = ToyCollectionConfigManager.queryItemsByCategory(currentCategoryId);
    var changed = false;
    var summary = "";
    var totalSubmitted = 0;

    for (var i = 0; i < items.size(); i++) {
        var cfg = items.get(i);
        var cfgId = cfg.getId();
        var submitted = progress[String(cfgId)] || 0;
        var needed = cfg.getRequiredQuantity() - submitted;
        if (needed <= 0) continue;

        // 统计背包中该物品数量
        var holding = countInInventory(cfg.getItemId());
        var toSubmit = Math.min(needed, holding);
        if (toSubmit <= 0) continue;

        // 从背包扣除
        if (!deductFromInventory(cfg.getItemId(), toSubmit)) continue;

        // 更新进度
        submitted += toSubmit;
        progress[String(cfgId)] = submitted;
        changed = true;
        totalSubmitted += toSubmit;

        summary += "#i" + cfg.getItemId() + "# ×#b" + toSubmit + "#k ";

        // 检查是否完成并发奖励
        if (submitted >= cfg.getRequiredQuantity() && cfg.getRewardItemId() > 0) {
            if (cm.canHold(cfg.getRewardItemId(), cfg.getRewardQuantity())) {
                cm.gainItem(cfg.getRewardItemId(), cfg.getRewardQuantity());
                summary += "#g[奖励已发]#k ";
            } else {
                summary += "#r[背包满，奖励未发]#k ";
            }
        }
        summary += "\r\n";
    }

    saveData(progress);

    var text;
    if (!changed) {
        text = "没有可提交的物品！\r\n请确认背包中有当前分类所需的收集物品。";
    } else {
        text = "#b一键提交完成！#k 共提交 #r" + totalSubmitted + "#k 个物品\r\n\r\n";
        text += summary;
    }

    cm.sendOkLevel("RefreshCategory", text);
}

// ==================== 刷新页（提交后回到物品列表） ====================
function levelRefreshCategory() {
    showCategoryItems(currentCategoryId);
}

// ==================== 背包工具函数 ====================

/**
 * 枚举背包中与收集列表匹配的物品数量
 * cm.getItemQuantity 已统计所有栏位（装备/消耗/其他/设置/现金），无需手动遍历
 * @returns {object} { "itemId": count, ... }
 */
function countBackpackMatches(items) {
    var matches = {};
    for (var j = 0; j < items.size(); j++) {
        var itemId = String(items.get(j).getItemId());
        var qty = cm.getItemQuantity(items.get(j).getItemId());
        if (qty > 0) {
            matches[itemId] = qty;
        }
    }
    return matches;
}

/**
 * 统计背包中指定物品的数量
 * cm.getItemQuantity 已统计所有栏位（装备/消耗/其他/设置/现金）
 */
function countInInventory(itemId) {
    return cm.getItemQuantity(itemId);
}

/**
 * 从背包中扣除指定数量的物品（优先装备栏，再道具栏）
 * @returns {boolean} 是否全部扣除成功
 */
function deductFromInventory(itemId, quantity) {
    var removed = 0;
    var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
    var InventoryManipulator = Java.type("org.gms.client.inventory.manipulator.InventoryManipulator");

    // 先从装备栏扣除
    var equipInv = cm.getInventory(EQUIP_INV);
    if (equipInv != null) {
        var arr = equipInv.list().toArray();
        // 按槽位排序便于逐个删除
        for (var i = 0; i < arr.length && removed < quantity; i++) {
            var item = arr[i];
            if (item != null && item.getItemId() == itemId) {
                InventoryManipulator.removeFromSlot(cm.getC(), InventoryType.EQUIP, item.getPosition(), 1, false);
                removed++;
            }
        }
    }

    // 剩余从道具栏扣除
    var remaining = quantity - removed;
    if (remaining > 0) {
        var held = cm.getItemQuantity(itemId);
        if (held >= remaining) {
            cm.gainItem(itemId, -remaining);
            removed += remaining;
        }
    }

    return removed >= quantity;
}

// ==================== 进度条 ====================
function makeProgressBar(current, max) {
    if (max <= 0) return "#r[N/A]#k";
    var total = 8;
    var filled = Math.round(current / max * total);
    if (filled < 0) filled = 0;
    if (filled > total) filled = total;
    var empty = total - filled;
    var bar = "#e#r";
    for (var k = 0; k < filled; k++) bar += "■";
    bar += "#k";
    for (var k = 0; k < empty; k++) bar += "□";
    bar += "#n";
    return bar;
}
