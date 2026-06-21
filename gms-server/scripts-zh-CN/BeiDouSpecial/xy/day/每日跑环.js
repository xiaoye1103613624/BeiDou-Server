/**
 * @description 每日跑环系统
 * 功能：从物品池随机选取物品作为任务，收集达标后提交 → 发放奖励 → 下一环
 * 依赖：PaohuanConfigManager (Java), CharacterExtendValue (日清持久化)
 * 入口：9900001.js case 202
 */

var PaohuanConfigManager = Java.type("org.gms.config.PaohuanConfigManager");
var WarehouseManager = Java.type("org.gms.config.WarehouseManager");

// 持久化 Key（使用 CharacterExtendValue daily 类型，午夜自动清空）
var EXTEND_KEY = "每日跑环";

// 常量
var ABANDON_FEE = 50000;  // 放弃任务手续费

// 菜单选项 Key（客户端只认整数，#Lback# 等非数字会被解析为 0 导致冲突）
var SEL_BACK = 0;      // 返回首页
var SEL_START = 1;     // 开始跑环
var SEL_ITEMS = 2;     // 查看跑环物品池
var SEL_INFO = 3;      // 查看奖励规则
var SEL_SUBMIT = 4;    // 提交任务
var SEL_ABANDON = 5;   // 放弃任务
var SEL_TELEPORT = 6;  // 传送至掉落地图（VIP）

/**
 * 加载跑环数据（日清类型，extendType "22"）
 * 返回格式：{ count: 已完成环数, activeItemId: 当前环物品ID, activeQuantity: 当前环物品数量 }
 */
function loadData() {
    var raw = cm.getCharacterExtendValue(EXTEND_KEY, true);
    if (raw && raw !== "" && raw !== "null") {
        try {
            return JSON.parse(String(raw));
        } catch (e) {
        }
    }
    return {count: 0, activeItemId: 0, activeQuantity: 0};
}

/**
 * 保存跑环数据
 */
function saveData(data) {
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, JSON.stringify(data), true);
}

/**
 * 获取游戏参数
 */
function getDailyLimit() {
    return PaohuanConfigManager.getDailyLimit();
}

function getExpPerRing() {
    return PaohuanConfigManager.getExpPerRing();
}

function getMesoPerRing() {
    return PaohuanConfigManager.getMesoPerRing();
}

function start() {
    levelMain();
}

// ==================== 主菜单 ====================

function levelMain() {
    var data = loadData();
    var dailyLimit = getDailyLimit();
    var hasActiveTask = data.activeItemId > 0;

    var text = "#e每日跑环#n\r\n\r\n";
    text += "今日进度：#r" + data.count + "#k / " + dailyLimit + " 环\r\n";

    if (data.count >= dailyLimit) {
        text += "\r\n#r今日跑环次数已用完，请明天再来！#k\r\n";
        text += "奖励规则：每环 EXP=" + getExpPerRing() + "×环数，Meso=" + getMesoPerRing() + "×环数\r\n";
        cm.sendOkLevel("", text);
        cm.dispose();
        return;
    }

    if (hasActiveTask) {
        // 有活跃任务 → 显示任务详情
        var heldQty = cm.getItemQuantity(data.activeItemId);
        text += "━━━ 当前任务 ━━━\r\n";
        text += "收集：#i" + data.activeItemId + "# #t" + data.activeItemId + "#\r\n";
        text += "需要：#r" + data.activeQuantity + "#k 个\r\n";
        text += "持有：#b" + heldQty + "#k 个\r\n";
        text += "\r\n";
        text += "#L" + SEL_SUBMIT + "##r提交任务（完成第 " + (data.count + 1) + " 环）#k#l\r\n";
        text += "#L" + SEL_ABANDON + "##b放弃任务（重新随机，手续费 #r" + ABANDON_FEE.toLocaleString() + "#k 金币）#k#l\r\n";

        // VIP传送：物品配置了掉落地图即可传送（VIP物品判断暂时注释）
        var vipItemId = PaohuanConfigManager.getVipItemId();
        if (vipItemId > 0 && cm.haveItem(vipItemId)) {
            var dropMap = PaohuanConfigManager.queryDropMap(data.activeItemId);
            if (dropMap > 0) {
                text += "#L" + SEL_TELEPORT + "##d传送至掉落地图#k#l\r\n";
            }
        }
    } else {
        // 无活跃任务 → 开始新环
        text += "━━━ 开始跑环 ━━━\r\n";
        text += "每次跑环随机分配一个收集任务，\r\n";
        text += "完成任务可获得经验和金币奖励！\r\n\r\n";
        text += "#L" + SEL_START + "##r开始跑环（第 " + (data.count + 1) + " 环）#k#l\r\n";
    }

    text += "\r\n";
    text += "#L" + SEL_ITEMS + "#查看跑环物品池#l\r\n";
    text += "#L" + SEL_INFO + "#查看奖励规则#l\r\n";
    text += "#L" + SEL_BACK + "##g返回上一页#k#l\r\n";

    cm.sendNextSelectLevel("HandleMain", text);
}

// ==================== 操作处理 ====================

function levelHandleMain(selection) {
    if (selection === SEL_BACK) {
        cm.dispose();
        cm.openNpc(9900001);
        return;
    }
    if (selection === SEL_INFO) {
        showRewardInfo();
        return;
    }
    if (selection === SEL_ITEMS) {
        showItemPool();
        return;
    }

    var data = loadData();

    if (selection === SEL_START) {
        startNewRing(data);
    } else if (selection === SEL_SUBMIT) {
        submitTask(data);
    } else if (selection === SEL_ABANDON) {
        abandonTask(data);
    } else if (selection === SEL_TELEPORT) {
        doTeleport(data);
    }
}

// ==================== 开始新环 ====================

function startNewRing(data) {
    // 从物品池随机选取
    var items = PaohuanConfigManager.queryEnabledItems();
    if (items.isEmpty()) {
        cm.sendOkLevel("Main", "跑环物品池为空，请联系管理员配置！");
        return;
    }

    var idx = Math.floor(Math.random() * items.size());
    var config = items.get(idx);

    data.activeItemId = config.getItemId();
    data.activeQuantity = config.getQuantity();
    saveData(data);

    var text = "#e跑环任务开始！#n\r\n\r\n";
    text += "第 #r" + (data.count + 1) + "#k 环任务：\r\n";
    text += "收集 #i" + data.activeItemId + "# #t" + data.activeItemId + "#\r\n";
    text += "数量：#r" + data.activeQuantity + "#k 个\r\n\r\n";
    text += "完成奖励：\r\n";
    text += "经验：+#r" + (getExpPerRing() * (data.count + 1)).toLocaleString() + "#k\r\n";
    text += "金币：+#r" + (getMesoPerRing() * (data.count + 1)).toLocaleString() + "#k\r\n";

    // 显示里程碑奖励预览
    var rewards = PaohuanConfigManager.queryRewardsByRing(data.count + 1);
    if (!rewards.isEmpty()) {
        text += "\r\n里程碑奖励（第" + (data.count + 1) + "环）：\r\n";
        for (var i = 0; i < rewards.size(); i++) {
            var r = rewards.get(i);
            if (r.getItemId() == 0) {
                text += "  #e" + r.getRewardDesc() + "#n：+#r" + r.getQuantity().toLocaleString() + "#k 金币\r\n";
            } else {
                text += "  #e" + r.getRewardDesc() + "#n：#i" + r.getItemId() + "# #t" + r.getItemId() + "# ×" + r.getQuantity() + "\r\n";
            }
        }
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 提交任务 ====================

function submitTask(data) {
    var itemId = data.activeItemId;
    var required = data.activeQuantity;

    // 查询背包持有量
    var heldQty = cm.getItemQuantity(itemId);

    // 查询仓库持有量
    var accountId = cm.getPlayer().getAccountId();
    var charId = WarehouseManager.isAccountShared() ? null : cm.getPlayer().getId();
    var warehouseQty = WarehouseManager.queryItemQuantity(accountId, charId, itemId);

    // 总计可用数量
    var totalAvailable = heldQty + warehouseQty;

    if (totalAvailable < required) {
        var text = "物品不足！\r\n\r\n";
        text += "需要：#i" + itemId + "# #t" + itemId + "# × #r" + required + "#k\r\n";
        text += "背包：#b" + heldQty + "#k 个\r\n";
        if (warehouseQty > 0) {
            text += "仓库：#b" + warehouseQty + "#k 个\r\n";
        }
        text += "还差：#r" + (required - totalAvailable) + "#k 个\r\n";
        cm.sendOkLevel("Main", text);
        return;
    }

    // 优先从仓库扣减，再从背包扣减
    var fromWarehouse = Math.min(warehouseQty, required);
    var fromBackpack = required - fromWarehouse;

    if (fromWarehouse > 0) {
        var invType = Java.type("org.gms.constants.inventory.ItemConstants").getInventoryType(itemId).getType();
        WarehouseManager.withdrawItem(accountId, charId, itemId, invType, fromWarehouse);
    }
    if (fromBackpack > 0) {
        cm.gainItem(itemId, -fromBackpack);
    }

    // 增加环数
    data.count++;
    var ringNum = data.count;

    // 发放基础奖励
    var expReward = ringNum * getExpPerRing();
    var mesoReward = ringNum * getMesoPerRing();
    cm.gainMeso(mesoReward);
    cm.gainExp(expReward);

    var text = "#e任务完成！#n\r\n\r\n";
    text += "第 #r" + ringNum + "#k 环完成！\r\n";
    text += "经验：+#r" + expReward.toLocaleString() + "#k\r\n";
    text += "金币：+#r" + mesoReward.toLocaleString() + "#k\r\n";

    // 发放里程碑奖励
    var rewards = PaohuanConfigManager.queryRewardsByRing(ringNum);
    if (!rewards.isEmpty()) {
        text += "\r\n━━━ 里程碑奖励 ━━━\r\n";
        for (var i = 0; i < rewards.size(); i++) {
            var r = rewards.get(i);
            if (r.getItemId() == 0) {
                cm.gainMeso(r.getQuantity());
                text += "#e" + r.getRewardDesc() + "#n：+#r" + r.getQuantity().toLocaleString() + "#k 金币\r\n";
            } else {
                if (cm.canHold(r.getItemId(), r.getQuantity())) {
                    cm.gainItem(r.getItemId(), r.getQuantity());
                    text += "#e" + r.getRewardDesc() + "#n：#i" + r.getItemId() + "# #t" + r.getItemId() + "# ×" + r.getQuantity() + "\r\n";
                } else {
                    text += "#r" + r.getRewardDesc() + "#n：背包满，请找GM补发\r\n";
                }
            }
        }
    }

    // 发放每环随机奖励（从奖励池中按权重随机选取 ~3 个，数量在 min~max 之间随机）
    var ringRewardPool = PaohuanConfigManager.queryRingRewards();
    if (!ringRewardPool.isEmpty()) {
        // 构建权重表
        var totalWeight = 0;
        var weightedItems = [];
        for (var i = 0; i < ringRewardPool.size(); i++) {
            var rw = ringRewardPool.get(i);
            var w = rw.getWeight();
            totalWeight += w;
            weightedItems.push({
                itemId: rw.getItemId(), minQty: rw.getMinQuantity(), maxQty: rw.getMaxQuantity(), weight: w
            });
        }

        // 随机抽 1~3 种（不超过池子大小）
        var pickCount = Math.min(1 + Math.floor(Math.random() * 3), weightedItems.length);
        var picked = [];
        var poolCopy = weightedItems.slice(); // 浅拷贝，避免重复选中同一奖励

        for (var p = 0; p < pickCount && poolCopy.length > 0; p++) {
            // 按权重随机选取
            var randW = Math.floor(Math.random() * totalWeight);
            var accW = 0;
            var pickIdx = 0;
            for (var j = 0; j < poolCopy.length; j++) {
                accW += poolCopy[j].weight;
                if (randW < accW) {
                    pickIdx = j;
                    break;
                }
            }
            var chosen = poolCopy[pickIdx];
            picked.push(chosen);
            // 从池中移除已选中的，重新计算权重
            totalWeight -= chosen.weight;
            poolCopy.splice(pickIdx, 1);
        }

        if (picked.length > 0) {
            text += "\r\n━━━ 随机奖励 ━━━\r\n";
            for (var k = 0; k < picked.length; k++) {
                var pk = picked[k];
                var rQty = pk.minQty + Math.floor(Math.random() * (pk.maxQty - pk.minQty + 1));
                if (pk.itemId == 0) {
                    cm.gainMeso(rQty);
                    text += "#i0# 金币：+#r" + rQty.toLocaleString() + "#k\r\n";
                } else {
                    if (cm.canHold(pk.itemId, rQty)) {
                        cm.gainItem(pk.itemId, rQty);
                        text += "#i" + pk.itemId + "# #t" + pk.itemId + "# × #r" + rQty + "#k\r\n";
                    } else {
                        text += "#r#t" + pk.itemId + "# ×" + rQty + "（背包满，请找GM补发）#k\r\n";
                    }
                }
            }
        }
    }

    // 清除当前任务
    data.activeItemId = 0;
    data.activeQuantity = 0;
    saveData(data);

    // 检查是否达到上限
    if (data.count >= getDailyLimit()) {
        text += "\r\n#r今日跑环次数已全部完成！#k\r\n";
        cm.sendOkLevel("Main", text);
    } else {
        text += "\r\n#b点击确定自动开始下一环#k\r\n";
        cm.sendOkLevel("HandleAutoStart", text);
    }
}

function levelHandleAutoStart() {
    var data = loadData();
    startNewRing(data);
}

// ==================== 放弃任务 ====================

function abandonTask(data) {
    // 检查金币是否足够
    if (cm.getMeso() < ABANDON_FEE) {
        cm.sendOkLevel("Main", "金币不足！放弃任务需要 #r" + ABANDON_FEE.toLocaleString() + "#k 金币，你当前只有 #b" + cm.getMeso().toLocaleString() + "#k 金币。");
        return;
    }

    cm.gainMeso(-ABANDON_FEE);
    data.activeItemId = 0;
    data.activeQuantity = 0;
    saveData(data);

    cm.sendOkLevel("Main", "已花费 #r" + ABANDON_FEE.toLocaleString() + "#k 金币放弃当前任务，可重新开始。");
}

// ==================== VIP传送 ====================

function doTeleport(data) {
    var dropMap = PaohuanConfigManager.queryDropMap(data.activeItemId);
    if (dropMap <= 0) {
        cm.sendOkLevel("Main", "该物品未配置掉落地图，无法传送。");
        return;
    }
    cm.getPlayer().saveLocationOnWarp();
    cm.getPlayer().dropMessage(6, "[传送中心]：[" + cm.getPlayer().getName() + "玩家] [线路-" + cm.getPlayer().getClient().getChannel() + "] 传送至 " + cm.getPlayer().getClient().getChannelServer().getMapFactory().getMap(dropMap).getMapName());
    cm.warp(dropMap);
    cm.dispose();
}

// ==================== 奖励规则 ====================

function showRewardInfo() {
    var text = "#e跑环奖励规则#n\r\n\r\n";
    text += "基础奖励：每环 EXP = " + getExpPerRing() + " × 环数\r\n";
    text += "          每环 Meso = " + getMesoPerRing() + " × 环数\r\n";
    text += "每日上限：" + getDailyLimit() + " 环（午夜重置）\r\n\r\n";

    text += "里程碑奖励（指定环数额外奖励）：\r\n";
    var hasRewards = false;
    for (var ring = 1; ring <= getDailyLimit(); ring++) {
        var rewards = PaohuanConfigManager.queryRewardsByRing(ring);
        if (!rewards.isEmpty()) {
            hasRewards = true;
            text += "第 " + ring + " 环：\r\n";
            for (var i = 0; i < rewards.size(); i++) {
                var r = rewards.get(i);
                if (r.getItemId() == 0) {
                    text += "  " + r.getRewardDesc() + "：+" + r.getQuantity().toLocaleString() + " 金币\r\n";
                } else {
                    text += "  " + r.getRewardDesc() + "：#i" + r.getItemId() + "# " + r.getQuantity() + "个\r\n";
                }
            }
        }
    }
    if (!hasRewards) {
        text += "暂无里程碑奖励配置。\r\n";
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 跑环物品池 ====================

function showItemPool() {
    var items = PaohuanConfigManager.queryEnabledItems();
    var totalItems = items.size();

    var text = "#e跑环物品池#n\r\n\r\n";

    if (totalItems === 0) {
        text += "跑环物品池暂无配置，请联系管理员！\r\n";
    } else {
        text += "━━━ 物品列表（共" + totalItems + "种）━━━\r\n\r\n";
        for (var i = 0; i < totalItems; i++) {
            var config = items.get(i);
            var itemId = config.getItemId();
            text += "#i" + itemId + "# ";
        }
    }

    cm.sendOkLevel("Main", text);
}
