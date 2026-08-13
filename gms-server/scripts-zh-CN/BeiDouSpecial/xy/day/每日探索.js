/**
 * @description 每日探索系统
 * 功能：从地图池随机选取地图作为目标，到达后提交 → 随机奖励 → 完成奖励 → 下一轮
 * 依赖：DailyExploreConfigManager (Java), CharacterExtendValue (日清持久化)
 * 入口：9900001.js case 200
 */

var DailyExploreConfigManager = Java.type("org.gms.config.DailyExploreConfigManager");

var EXTEND_KEY = "每日探索";

function loadData() {
    var raw = cm.getCharacterExtendValue(EXTEND_KEY, true);
    if (raw && raw !== "" && raw !== "null") {
        try { return JSON.parse(raw); } catch (e) {}
    }
    return { count: 0, currentMapId: 0 };
}

function saveData(data) {
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, JSON.stringify(data), true);
}

function getDailyLimit() { return DailyExploreConfigManager.getDailyLimit(); }

/** 根据地图ID查找配置（走内存缓存，不查DB） */
function getMapConfig(mapId) {
    var maps = DailyExploreConfigManager.getEnabledMaps();  // 缓存，非DB
    for (var i = 0; i < maps.size(); i++) {
        if (maps.get(i).getMapId() == mapId) return maps.get(i);
    }
    return null;
}

/**
 * 生成地图小地图的 UI WZ 路径，供 #f...# 展示
 * 需要提前将 minimap PNG 导入到客户端 UIWindow.img/MapMinimap/{mapId}/0
 */
function getMapMinimapImg(mapId) {
    return "#fUI/UIWindow.img/MapMinimap/" + mapId + "/0#";
}

function start() { levelMain(); }

// ==================== 主菜单 ====================

function levelMain() {
    var data = loadData();
    var dailyLimit = getDailyLimit();
    var hasActiveTask = data.currentMapId > 0;

    var text = "#e每日探索#n\r\n\r\n";
    text += "今日进度：#r" + data.count + "#k / " + dailyLimit + " 次\r\n";

    if (data.count >= dailyLimit) {
        text += "\r\n#r今日探索次数已用完，请明天再来！#k\r\n";
        cm.sendOkLevel("", text);
        cm.dispose();
        return;
    }

    if (hasActiveTask) {
        // 有活跃任务 → 显示目标地图信息（sendSimple中#m可点击打开世界地图）
        var mapConfig = getMapConfig(data.currentMapId);
        text += "━━━ 当前探索目标 ━━━\r\n";
        text += "#fMap/MapHelper/minimap/startnpc# #e目标地图：#n#b#m" + data.currentMapId + "##k（ID:#r" + data.currentMapId + "#k）\r\n";
        if (mapConfig) {
            var mn = mapConfig.getMapName();
            var md = mapConfig.getDescription();
            if (mn && mn !== "") text += "地图名称：#b" + mn + "#k\r\n";
            if (md && md !== "") text += "地图描述：#g" + md + "#k\r\n";
        }
        // 地图小地图预览（需将导出的PNG导入到 UIWindow.img/MapMinimap/{mapId}/0）
        text += getMapMinimapImg(data.currentMapId) + "\r\n";
        text += "\r\n";
        text += "#L1##r提交任务（第 " + (data.count + 1) + " 次探索）#k#l\r\n";
        text += "#L2##b放弃任务（重新随机，手续费 #r10万金币#k）#k#l\r\n";
    } else {
        // 无活跃任务 → 开始新探索
        text += "━━━ 开始探索 ━━━\r\n";
        text += "每次探索会随机分配一个目标地图，\r\n";
        text += "到达该地图后即可提交获得奖励！\r\n\r\n";
        text += "#L1##r开始探索（第 " + (data.count + 1) + " 次）#k#l\r\n";
    }

    text += "\r\n";
    text += "#L3#查看奖励规则#l\r\n";
    text += "#L0##g返回首页#k#l\r\n";

    cm.sendNextSelectLevel("HandleMain", text);
}

// ==================== 操作处理 ====================

function levelHandleMain(selection) {
    if (selection == 0) { cm.dispose(); return; }  // 返回首页
    if (selection == 3) { showRewardInfo(); return; }  // 查看奖励规则

    var data = loadData();

    if (selection == 1) {  // 开始探索 / 提交任务（两个状态不同时出现，根据currentMapId判断）
        if (data.currentMapId > 0) {
            submitExplore(data);
        } else {
            startNewExplore(data);
        }
    } else if (selection == 2) {  // 放弃任务
        abandonExplore(data);
    }
}

// ==================== 开始新探索 ====================

function startNewExplore(data) {
    // 从地图池随机选取
    var maps = DailyExploreConfigManager.getEnabledMaps();  // 缓存
    if (maps.isEmpty()) {
        cm.sendNextLevel("Main", "探索地图池为空，请联系管理员配置！");
        return;
    }

    var idx = Math.floor(Math.random() * maps.size());
    var config = maps.get(idx);
    // 转为JS数字防止Java Integer序列化问题
    data.currentMapId = Number(config.getMapId());
    saveData(data);

    // 直接回主菜单（sendSimple中#m可点击打开世界地图）
    var mapName = config.getMapName();
    var desc = config.getDescription();
    var msg = "#e探索任务开始！#n\r\n\r\n";
    msg += "#fUI/UIWindow.img/QuestIcon/7/0# 第 #r" + (data.count + 1) + "#k 次探索目标已分配\r\n\r\n";
    if (mapName && mapName !== "") {
        msg += "目标：#b" + mapName + "#k\r\n";
    }
    // 返回主菜单查看完整地图信息
    cm.sendNextLevel("Main", msg);
}

// ==================== 提交任务 ====================

function submitExplore(data) {
    // 校验是否在目标地图
    var currentMapId = cm.getPlayer().getMapId();
    if (currentMapId != data.currentMapId) {
        var text = "你还没有到达目标地图！\r\n\r\n";
        text += "当前所在：#m" + currentMapId + "#（ID:" + currentMapId + "）\r\n";
        text += "目标地图：#m" + data.currentMapId + "#（ID:" + data.currentMapId + "）\r\n";
        text += "\r\n请前往目标地图后再提交。";
        cm.sendNextLevel("Main", text);
        return;
    }

    // 增加探索次数
    data.count++;
    var exploreNum = data.count;

    var text = "#e探索完成！#n\r\n\r\n";
    text += "第 #r" + exploreNum + "#k 次探索完成！\r\n";

    // 发放每轮随机奖励（从奖励池中按权重随机选取 1~3 种）
    var ringRewardPool = DailyExploreConfigManager.getRingRewards();  // 缓存
    if (!ringRewardPool.isEmpty()) {
        // 构建权重表
        var totalWeight = 0;
        var weightedItems = [];
        for (var i = 0; i < ringRewardPool.size(); i++) {
            var rw = ringRewardPool.get(i);
            var w = rw.getWeight();
            totalWeight += w;
            weightedItems.push({
                itemId: rw.getItemId(),
                minQty: rw.getMinQuantity(),
                maxQty: rw.getMaxQuantity(),
                weight: w
            });
        }

        // 随机抽 1~3 种
        var pickCount = Math.min(1 + Math.floor(Math.random() * 3), weightedItems.length);
        var picked = [];
        var poolCopy = weightedItems.slice();

        for (var p = 0; p < pickCount && poolCopy.length > 0; p++) {
            var randW = Math.floor(Math.random() * totalWeight);
            var accW = 0;
            var pickIdx = 0;
            for (var j = 0; j < poolCopy.length; j++) {
                accW += poolCopy[j].weight;
                if (randW < accW) { pickIdx = j; break; }
            }
            var chosen = poolCopy[pickIdx];
            picked.push(chosen);
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
                    text += "金币：+#r" + rQty.toLocaleString() + "#k\r\n";
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

    // 发放完成奖励（里程碑式）
    var finalRewards = DailyExploreConfigManager.getRewardsByExploreCount(exploreNum);  // 缓存
    if (!finalRewards.isEmpty()) {
        text += "\r\n━━━ 完成奖励（第" + exploreNum + "次）━━━\r\n";
        for (var i = 0; i < finalRewards.size(); i++) {
            var fr = finalRewards.get(i);
            if (fr.getItemId() == 0) {
                cm.gainMeso(fr.getQuantity());
                text += "#e" + fr.getRewardDesc() + "#n：+#r" + fr.getQuantity().toLocaleString() + "#k 金币\r\n";
            } else {
                if (cm.canHold(fr.getItemId(), fr.getQuantity())) {
                    cm.gainItem(fr.getItemId(), fr.getQuantity());
                    text += "#e" + fr.getRewardDesc() + "#n：#i" + fr.getItemId() + "# #t" + fr.getItemId() + "# ×" + fr.getQuantity() + "\r\n";
                } else {
                    text += "#r" + fr.getRewardDesc() + "#n：背包满，请找GM补发\r\n";
                }
            }
        }
    }

    // 清除当前任务
    data.currentMapId = 0;
    saveData(data);

    // 检查是否达到上限
    if (data.count >= getDailyLimit()) {
        text += "\r\n#r今日探索已全部完成，明天再来吧！#k\r\n";
    } else {
        text += "\r\n#b点击下一步开始下一次探索#k\r\n";
    }

    cm.sendNextLevel("Main", text);
}

// ==================== 放弃任务 ====================

function abandonExplore(data) {
    // 放弃任务需扣除10万金币手续费
    if (cm.getMeso() < 100000) {
        cm.sendNextLevel("Main", "金币不足！放弃任务需要 #r10万金币#k 手续费。\r\n\r\n当前持有：#r" + cm.getMeso().toLocaleString() + "#k 金币");
        return;
    }
    cm.gainMeso(-100000);
    data.currentMapId = 0;
    saveData(data);
    cm.sendNextLevel("Main", "已放弃当前探索目标（手续费 #r10万金币#k 已扣除），可重新开始。");
}

// ==================== 奖励规则 ====================

function showRewardInfo() {
    var dailyLimit = getDailyLimit();
    var text = "#e每日探索规则#n\r\n\r\n";
    text += "每日上限：" + dailyLimit + " 次（午夜重置）\r\n\r\n";

    text += "每轮随机奖励（完成每轮后随机抽取）：\r\n";
    var rewards = DailyExploreConfigManager.getRingRewards();  // 缓存
    if (!rewards.isEmpty()) {
        for (var i = 0; i < rewards.size(); i++) {
            var r = rewards.get(i);
            var range = r.getMinQuantity() + "~" + r.getMaxQuantity();
            if (r.getItemId() == 0) {
                text += "  #r" + range + "#k 金币（权重：" + r.getWeight() + "）\r\n";
            } else {
                text += "  #i" + r.getItemId() + "# " + range + "个（权重：" + r.getWeight() + "）\r\n";
            }
        }
    } else {
        text += "  暂无配置\r\n";
    }

    text += "\r\n完成奖励（达到指定探索次数时触发）：\r\n";
    var hasFinal = false;
    for (var n = 1; n <= dailyLimit; n++) {
        var finalRewards = DailyExploreConfigManager.getRewardsByExploreCount(n);  // 缓存，O(1) map lookup
        if (!finalRewards.isEmpty()) {
            hasFinal = true;
            text += "第 " + n + " 次：\r\n";
            for (var j = 0; j < finalRewards.size(); j++) {
                var fr = finalRewards.get(j);
                if (fr.getItemId() == 0) {
                    text += "  " + fr.getRewardDesc() + "：+" + fr.getQuantity().toLocaleString() + " 金币\r\n";
                } else {
                    text += "  " + fr.getRewardDesc() + "：#i" + fr.getItemId() + "# " + fr.getQuantity() + "个\r\n";
                }
            }
        }
    }
    if (!hasFinal) {
        text += "  暂无配置\r\n";
    }

    cm.sendNextLevel("Main", text);
}
