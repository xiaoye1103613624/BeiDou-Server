/**
 * @description 每日Boss系统（环式）
 * 功能：从精英Boss池随机分配任务 → 击杀指定Boss → 提交 → 下一环 → 全部完成领最终奖励
 * 依赖：DailyBossConfigManager (Java), CharacterExtendValue (日清持久化)
 * 入口：9900001.js case 201
 */

var DailyBossConfigManager = Java.type("org.gms.config.DailyBossConfigManager");

// 持久化 Key（使用 CharacterExtendValue daily 类型，午夜自动清空）
var EXTEND_KEY = "每日BossRing";

// 菜单选项 Key（客户端只认整数）
var SEL_RETURN = 0;   // 返回上一页
var SEL_START = 1;    // 开始新环
var SEL_SUBMIT = 2;   // 提交任务
var SEL_ABANDON = 3;  // 放弃任务
var SEL_POOL = 4;     // 查看Boss池
var SEL_INFO = 5;     // 查看奖励规则

// ==================== 数据持久化 ====================

/**
 * 加载环式数据（日清类型）
 * 返回格式：{ count: 已完成环数, activeBossMobId: 当前Boss怪物ID, activeBossName: 当前Boss名称, activeKillRequired: 需击杀数 }
 */
function loadData() {
    var raw = cm.getCharacterExtendValue(EXTEND_KEY, true);
    if (raw && raw !== "" && raw !== "null") {
        try { return JSON.parse(String(raw)); } catch (e) {}
    }
    return { count: 0, activeBossMobId: 0, activeBossName: "", activeKillRequired: 0 };
}

function saveData(data) {
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, JSON.stringify(data), true);
}

// ==================== 游戏参数 ====================

function getDailyLimit() { return DailyBossConfigManager.getDailyLimit(); }
function getExpBase() { return DailyBossConfigManager.getExpBase(); }
function getMesoBase() { return DailyBossConfigManager.getMesoBase(); }
function getKillMin() { return DailyBossConfigManager.getKillMin(); }
function getKillMax() { return DailyBossConfigManager.getKillMax(); }
function getAbandonFee() { return DailyBossConfigManager.getAbandonFee(); }
function getFinalItemId() { return DailyBossConfigManager.getFinalItemId(); }
function getFinalItemQty() { return DailyBossConfigManager.getFinalItemQty(); }

function loadMilestoneRewards() {
    try { return JSON.parse(String(DailyBossConfigManager.getMilestoneRewardsJson())); } catch (e) {}
    return [];
}

function loadRandomRewards() {
    try { return JSON.parse(String(DailyBossConfigManager.getRandomRewardsJson())); } catch (e) {}
    return [];
}

function loadBossPool() {
    return DailyBossConfigManager.queryEnabledConfigs();
}

// ==================== 入口 ====================

function start() {
    if (DailyBossConfigManager.getBossRingEnabled() === 1) {
        levelMain();
    } else {
        // 旧系统入口（原里程碑+扫荡逻辑，保留在此函数名）
        levelLegacy();
    }
}

// ==================== 主菜单 ====================

function levelMain() {
    var data = loadData();
    var dailyLimit = getDailyLimit();
    var hasActiveTask = data.activeBossMobId > 0;

    var text = "#e每日Boss#n\r\n\r\n";
    text += "今日进度：#r" + data.count + "#k / " + dailyLimit + " 环\r\n";

    if (data.count >= dailyLimit) {
        text += "\r\n#r今日Boss任务已全部完成，请明天再来！#k\r\n";
        text += "奖励规则：每环 EXP=" + getExpBase() + "×环数，Meso=" + getMesoBase() + "×环数\r\n";
        cm.sendOkLevel("", text);
        cm.dispose();
        return;
    }

    if (hasActiveTask) {
        text += "━━━ 当前任务 ━━━\r\n";
        text += "击杀：#r" + data.activeBossName + "#k（怪物ID：" + data.activeBossMobId + "）\r\n";
        text += "需要击杀：#r" + data.activeKillRequired + "#k 只\r\n\r\n";
        text += "击杀完成后回到此处提交任务。\r\n\r\n";
        text += "#L" + SEL_SUBMIT + "##r提交任务（完成第 " + (data.count + 1) + " 环）#k#l\r\n";
        text += "#L" + SEL_ABANDON + "##b放弃任务（重新随机，手续费 #r" + getAbandonFee().toLocaleString() + "#k 金币）#k#l\r\n";
    } else {
        text += "━━━ 开始任务 ━━━\r\n";
        text += "每次随机分配一个精英Boss击杀任务，\r\n";
        text += "完成任务可获得经验和金币奖励！\r\n\r\n";
        text += "#L" + SEL_START + "##r开始第 " + (data.count + 1) + " 环#k#l\r\n";
    }

    text += "\r\n";
    text += "#L" + SEL_POOL + "#查看精英Boss池#l\r\n";
    text += "#L" + SEL_INFO + "#查看奖励规则#l\r\n";
    text += "#L" + SEL_RETURN + "##g返回上一页#k#l\r\n";

    cm.sendNextSelectLevel("HandleMain", text);
}

// ==================== 操作路由 ====================

function levelHandleMain(selection) {
    if (selection === SEL_RETURN) { cm.dispose(); cm.openNpc(9900001); return; }
    if (selection === SEL_INFO) { showRewardInfo(); return; }
    if (selection === SEL_POOL) { showBossPool(); return; }

    var data = loadData();

    if (selection === SEL_START) {
        startNewRing(data);
    } else if (selection === SEL_SUBMIT) {
        submitRing(data);
    } else if (selection === SEL_ABANDON) {
        abandonRing(data);
    }
}

// ==================== 开始新环 ====================

function startNewRing(data) {
    var bossPool = loadBossPool();
    if (bossPool.isEmpty()) {
        cm.sendOkLevel("Main", "精英Boss池为空，请联系管理员配置！");
        return;
    }

    // 从Boss池随机选取
    var idx = Math.floor(Math.random() * bossPool.size());
    var boss = bossPool.get(idx);

    // 随机击杀数
    var killMin = getKillMin();
    var killMax = getKillMax();
    var killRequired = killMin + Math.floor(Math.random() * (killMax - killMin + 1));

    data.activeBossMobId = boss.getBossMobId();
    data.activeBossName = boss.getBossName();
    data.activeKillRequired = killRequired;
    saveData(data);

    var ringNum = data.count + 1;
    var text = "#eBoss任务开始！#n\r\n\r\n";
    text += "第 #r" + ringNum + "#k 环任务：\r\n";
    text += "击杀 #r" + data.activeBossName + "#k\r\n";
    text += "数量：#r" + killRequired + "#k 只\r\n\r\n";
    text += "完成奖励：\r\n";
    text += "经验：+#r" + (getExpBase() * ringNum).toLocaleString() + "#k\r\n";
    text += "金币：+#r" + (getMesoBase() * ringNum).toLocaleString() + "#k\r\n";

    // 显示里程碑奖励预览
    var milestones = loadMilestoneRewards();
    var hasMilestone = false;
    for (var i = 0; i < milestones.length; i++) {
        if (milestones[i].ring === ringNum) {
            hasMilestone = true;
            var ms = milestones[i];
            if (ms.itemId === 0) {
                text += "\r\n里程碑奖励（第" + ringNum + "环）：#e" + ms.desc + "#n：+#r" + ms.quantity.toLocaleString() + "#k 金币\r\n";
            } else {
                text += "\r\n里程碑奖励（第" + ringNum + "环）：#e" + ms.desc + "#n：#i" + ms.itemId + "# ×" + ms.quantity + "\r\n";
            }
        }
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 提交任务 ====================

function submitRing(data) {
    var ringNum = data.count + 1;

    // 确认提交（简单确认对话框）
    var text = "#e确认提交#n\r\n\r\n";
    text += "你是否已完成以下任务？\r\n\r\n";
    text += "击杀 #r" + data.activeBossName + "#k × #r" + data.activeKillRequired + "#k\r\n\r\n";
    text += "提交后任务完成，进入下一环。";

    // 使用发送OK弹窗，玩家确认后继续
    cm.sendOkLevel("HandleSubmitConfirm", text);
}

function levelHandleSubmitConfirm() {
    var data = loadData();
    doSubmitRing(data);
}

function doSubmitRing(data) {
    var ringNum = data.count + 1;

    // 增加环数
    data.count++;
    ringNum = data.count;

    // 发放基础奖励
    var expReward = ringNum * getExpBase();
    var mesoReward = ringNum * getMesoBase();
    cm.gainMeso(mesoReward);
    cm.gainExp(expReward);

    var text = "#e任务完成！#n\r\n\r\n";
    text += "第 #r" + ringNum + "#k 环完成！\r\n";
    text += "经验：+#r" + expReward.toLocaleString() + "#k\r\n";
    text += "金币：+#r" + mesoReward.toLocaleString() + "#k\r\n";

    // 发放里程碑奖励
    var milestones = loadMilestoneRewards();
    for (var i = 0; i < milestones.length; i++) {
        var ms = milestones[i];
        if (ms.ring === ringNum) {
            if (ms.itemId === 0) {
                cm.gainMeso(ms.quantity);
                text += "\r\n━━━ 里程碑奖励 ━━━\r\n";
                text += "#e" + ms.desc + "#n：+#r" + ms.quantity.toLocaleString() + "#k 金币\r\n";
            } else {
                if (cm.canHold(ms.itemId, ms.quantity)) {
                    cm.gainItem(ms.itemId, ms.quantity);
                    text += "\r\n━━━ 里程碑奖励 ━━━\r\n";
                    text += "#e" + ms.desc + "#n：#i" + ms.itemId + "# ×" + ms.quantity + "\r\n";
                } else {
                    text += "\r\n#r" + ms.desc + "#n：背包满，请找GM补发\r\n";
                }
            }
        }
    }

    // 发放每环随机奖励（从奖励池按权重随机选1~3种）
    var randomPool = loadRandomRewards();
    if (randomPool.length > 0) {
        // 构建权重表
        var totalWeight = 0;
        var weightedItems = [];
        for (var i = 0; i < randomPool.length; i++) {
            var rw = randomPool[i];
            totalWeight += rw.weight;
            weightedItems.push(rw);
        }

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
                if (pk.itemId === 0) {
                    cm.gainMeso(rQty);
                    text += "金币：+#r" + rQty.toLocaleString() + "#k\r\n";
                } else {
                    if (cm.canHold(pk.itemId, rQty)) {
                        cm.gainItem(pk.itemId, rQty);
                        text += "#i" + pk.itemId + "# ×#r" + rQty + "#k\r\n";
                    } else {
                        text += "#r#t" + pk.itemId + "# ×" + rQty + "（背包满，请找GM补发）#k\r\n";
                    }
                }
            }
        }
    }

    // 检查是否全部完成 → 发放最终奖励
    if (data.count >= getDailyLimit()) {
        var finalItemId = getFinalItemId();
        var finalQty = getFinalItemQty();
        if (finalItemId > 0 && finalQty > 0) {
            if (cm.canHold(finalItemId, finalQty)) {
                cm.gainItem(finalItemId, finalQty);
                text += "\r\n━━━ 全部完成！最终奖励 ━━━\r\n";
                text += "#i" + finalItemId + "# #t" + finalItemId + "# × #r" + finalQty + "#k\r\n";
            } else {
                text += "\r\n#r全部完成！最终奖励（背包满，请找GM补发）：#i" + finalItemId + "# ×" + finalQty + "#k\r\n";
            }
        }
        text += "\r\n#r今日Boss任务已全部完成！#k\r\n";
    } else {
        text += "\r\n#b点击确定开始下一环#k\r\n";
    }

    // 清除当前任务
    data.activeBossMobId = 0;
    data.activeBossName = "";
    data.activeKillRequired = 0;
    saveData(data);

    cm.sendOkLevel("Main", text);
}

// ==================== 放弃任务 ====================

function abandonRing(data) {
    var fee = getAbandonFee();
    if (cm.getMeso() < fee) {
        cm.sendOkLevel("Main", "金币不足！放弃任务需要 #r" + fee.toLocaleString() + "#k 金币，你当前只有 #b" + cm.getMeso().toLocaleString() + "#k 金币。");
        return;
    }

    cm.gainMeso(-fee);
    data.activeBossMobId = 0;
    data.activeBossName = "";
    data.activeKillRequired = 0;
    saveData(data);

    cm.sendOkLevel("Main", "已花费 #r" + fee.toLocaleString() + "#k 金币放弃当前任务，可重新开始。");
}

// ==================== 精英Boss池 ====================

function showBossPool() {
    var bossPool = loadBossPool();
    var text = "#e精英Boss池#n\r\n\r\n";

    if (bossPool.isEmpty()) {
        text += "精英Boss池暂无配置，请联系管理员！\r\n";
    } else {
        text += "━━━ Boss列表（共" + bossPool.size() + "种）━━━\r\n\r\n";
        for (var i = 0; i < bossPool.size(); i++) {
            var boss = bossPool.get(i);
            text += "#b" + boss.getBossName() + "#k（ID：" + boss.getBossMobId() + "）\r\n";
        }
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 奖励规则 ====================

function showRewardInfo() {
    var text = "#eBoss跑环奖励规则#n\r\n\r\n";
    text += "基础奖励：每环 EXP = " + getExpBase() + " × 环数\r\n";
    text += "          每环 Meso = " + getMesoBase() + " × 环数\r\n";
    text += "每日上限：" + getDailyLimit() + " 环（午夜重置）\r\n";
    text += "击杀数量：随机 " + getKillMin() + " ~ " + getKillMax() + " 只\r\n";
    text += "放弃费用：" + getAbandonFee().toLocaleString() + " 金币\r\n\r\n";

    text += "里程碑奖励：\r\n";
    var milestones = loadMilestoneRewards();
    if (milestones.length === 0) {
        text += "暂无里程碑奖励配置。\r\n";
    } else {
        for (var i = 0; i < milestones.length; i++) {
            var ms = milestones[i];
            if (ms.itemId === 0) {
                text += "第" + ms.ring + "环：" + ms.desc + "：+" + ms.quantity.toLocaleString() + " 金币\r\n";
            } else {
                text += "第" + ms.ring + "环：" + ms.desc + "：#i" + ms.itemId + "# ×" + ms.quantity + "\r\n";
            }
        }
    }

    text += "\r\n随机奖励池（每环抽取1~3种）：\r\n";
    var randomPool = loadRandomRewards();
    if (randomPool.length === 0) {
        text += "暂无随机奖励配置。\r\n";
    } else {
        for (var i = 0; i < randomPool.length; i++) {
            var rw = randomPool[i];
            if (rw.itemId === 0) {
                text += "金币 " + rw.minQty.toLocaleString() + "~" + rw.maxQty.toLocaleString() + "（权重" + rw.weight + "）\r\n";
            } else {
                text += "#i" + rw.itemId + "# ×" + rw.minQty + "~" + rw.maxQty + "（权重" + rw.weight + "）\r\n";
            }
        }
    }

    text += "\r\n全部环完成奖励：\r\n";
    var finalId = getFinalItemId();
    var finalQty = getFinalItemQty();
    if (finalId > 0) {
        text += "#i" + finalId + "# ×" + finalQty + "\r\n";
    } else {
        text += "暂无配置。\r\n";
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 旧系统（保留向后兼容） ====================

function levelLegacy() {
    // 旧系统：里程碑+扫荡模式（保留原有逻辑入口）
    // 当 boss_ring_enabled=0 时走此路径
    // 具体实现见旧版 每日Boss.js（此处仅保留入口定义）
    cm.sendOk("每日Boss旧系统暂不可用，请联系管理员启用新环式系统（boss_ring_enabled=1）。");
    cm.dispose();
}
