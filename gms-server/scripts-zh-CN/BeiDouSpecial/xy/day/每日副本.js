/**
 * 每日副本脚本
 * 功能：
 *   1. 打开脚本可实时查看每个副本的完成进度（进度条 + 已完成次数/配置次数）
 *   2. 单个副本完成后可领取该副本奖励（每副本每日限领一次）
 *   3. 全部副本完成后可领取每日额外奖励（每日限领一次）
 *   4. VIP玩家（持有指定物品）可直接传送到副本地图
 * 进度持久化：使用 CharacterExtendValue（每日自动重置）
 */

// ==================== 配置加载 ====================
var DailyDungeonConfigManager = Java.type("org.gms.config.DailyDungeonConfigManager");
var dungeons = loadDungeonConfigs();
var dailyRewards = loadDailyRewards();
var vipConfigs = loadVipConfigs();
var sweepRounds = DailyDungeonConfigManager.getSweepRounds();

/**
 * 加载启用的副本配置列表（含各副本的奖励列表）
 */
function loadDungeonConfigs() {
    try {
        var configs = DailyDungeonConfigManager.queryEnabledConfigs();
        var result = [];
        for (var i = 0; i < configs.size(); i++) {
            var c = configs.get(i);
            var rewards = DailyDungeonConfigManager.queryRewards(c.getId());
            var rewardList = [];
            for (var j = 0; j < rewards.size(); j++) {
                var r = rewards.get(j);
                rewardList.push([r.getItemId(), r.getQuantity()]);
            }
            result.push({
                key: c.getDungeonKey(),
                name: c.getDungeonName(),
                mapId: c.getMapId(),
                mapName: c.getMapName() || ("地图" + c.getMapId()),
                completeCount: c.getCompleteCount() || 3,
                rewards: rewardList
            });
        }
        return result;
    } catch (e) {
        print("[每日副本] 加载配置异常：" + e);
        return [];
    }
}

/**
 * 加载每日额外奖励列表（所有副本完成后可领取）
 */
function loadDailyRewards() {
    try {
        var list = DailyDungeonConfigManager.queryDailyRewards();
        var result = [];
        for (var i = 0; i < list.size(); i++) {
            var r = list.get(i);
            result.push([r.getItemId(), r.getQuantity()]);
        }
        return result;
    } catch (e) {
        print("[每日副本] 加载每日奖励异常：" + e);
        return [];
    }
}

/**
 * 加载VIP物品配置列表（持有任一物品即可解锁VIP传送功能）
 */
function loadVipConfigs() {
    try {
        var list = DailyDungeonConfigManager.queryVipConfigs();
        var result = [];
        for (var i = 0; i < list.size(); i++) {
            var v = list.get(i);
            result.push({ itemId: v.getItemId(), desc: v.getDescription() || "VIP传送" });
        }
        return result;
    } catch (e) {
        print("[每日副本] 加载VIP配置异常：" + e);
        return [];
    }
}

// ==================== 进度持久化（每日自动重置） ====================
var EXTEND_KEY = "daily_dungeon_progress";

/**
 * 加载玩家今日副本进度
 * 自动补充新增副本的初始化数据（管理员热更配置后无需清档）
 */
function loadProgress() {
    var raw = cm.getCharacterExtendValue(EXTEND_KEY, true);
    var progress;
    if (raw && raw !== "" && raw !== "null") {
        try {
            progress = JSON.parse(String(raw));
        } catch (e) {
            print("[每日副本] 解析进度数据异常：" + e);
            progress = null;
        }
    }

    // 未初始化或解析失败 → 创建全新进度
    if (!progress || typeof progress.dungeons !== "object") {
        progress = { dungeons: {}, rewardsClaimed: {}, dailyRewardClaimed: false, sweepUsed: false };
    }

    // 自动补充新增副本（管理员热更配置后，已有进度的玩家自动获得新副本槽位）
    var needSave = false;
    // 兼容旧进度（无sweepUsed字段）
    if (progress.sweepUsed === undefined) {
        progress.sweepUsed = false;
        needSave = true;
    }
    for (var i = 0; i < dungeons.length; i++) {
        var key = dungeons[i].key;
        if (progress.dungeons[key] === undefined) {
            progress.dungeons[key] = 0;
            needSave = true;
        }
        if (progress.rewardsClaimed[key] === undefined) {
            progress.rewardsClaimed[key] = false;
            needSave = true;
        }
    }
    // 清理已删除的副本（管理员下架副本后，自动移除对应进度）
    var validKeys = {};
    for (var i = 0; i < dungeons.length; i++) {
        validKeys[dungeons[i].key] = true;
    }
    for (var k in progress.dungeons) {
        if (!validKeys[k]) {
            delete progress.dungeons[k];
            delete progress.rewardsClaimed[k];
            needSave = true;
        }
    }
    if (needSave) {
        saveProgress(progress);
    }

    return progress;
}

/**
 * 持久化保存进度数据
 */
function saveProgress(progress) {
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, JSON.stringify(progress), true);
}

/**
 * 获取指定副本的当前完成次数
 */
function getCompletedCount(progress, key) {
    return progress.dungeons[key] || 0;
}

/**
 * 判断指定副本的奖励是否已领取
 */
function isRewardClaimed(progress, key) {
    return progress.rewardsClaimed[key] === true;
}

/**
 * 判断每日额外奖励是否已领取
 */
function isDailyRewardClaimed(progress) {
    return progress.dailyRewardClaimed === true;
}

// ==================== 进度条绘制 ====================
/**
 * 生成可视化进度条（■=已完成 □=未完成）
 * @param current 当前完成次数
 * @param max     目标完成次数
 * @return 带颜色标记的进度条文本
 */
function makeProgressBar(current, max) {
    if (max <= 0) {
        return "#r[配置异常]#k";
    }
    var total = 10;
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

// ==================== VIP检查 ====================
/**
 * 检查玩家是否持有VIP物品
 */
function hasVipAccess() {
    for (var i = 0; i < vipConfigs.length; i++) {
        if (cm.haveItem(vipConfigs[i].itemId)) return true;
    }
    return false;
}

/**
 * 检查是否全部副本都已达到完成次数
 */
function allDungeonsComplete(progress) {
    if (dungeons.length === 0) return false;
    for (var i = 0; i < dungeons.length; i++) {
        if (getCompletedCount(progress, dungeons[i].key) < dungeons[i].completeCount) {
            return false;
        }
    }
    return true;
}

// ==================== 状态机 ====================
var status = 0;
var menuSelection = -1;   // 主菜单选择：0=进度查看 1=领取副本奖励 2=领取每日奖励 3=VIP传送
var selectedDungeonIdx = -1; // 子菜单中选中的副本索引

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    // 玩家主动关闭对话
    if (mode == -1) {
        cm.dispose();
        return;
    }
    // 玩家点击"上一步"或"结束对话"（回到上一级菜单或关闭）
    if (mode == 0) {
        // 在子菜单中按返回 → 回到主菜单（menuSelection已设置表示已离开主菜单）
        if (menuSelection != -1) {
            status = 0;
            menuSelection = -1;
            selectedDungeonIdx = -1;
            action(1, 0, 0);
            return;
        }
        cm.dispose();
        return;
    }
    // 正常前进
    if (mode == 1) status++;
    else status--;

    // ==================== 主菜单（status=0） ====================
    if (status == 0) {
        // 无配置时直接提示
        if (dungeons.length === 0) {
            cm.sendOk("#r每日副本系统暂无配置。#k\r\n请联系管理员配置副本信息。");
            cm.dispose();
            return;
        }

        var progress = loadProgress();
        var allDone = allDungeonsComplete(progress);

        var text = "\t\t#r#e< 每日副本中心 >#k#n\r\n\r\n";
        text += "#d完成指定次数的副本即可领取奖励！#k\r\n\r\n";

        // 选项0：查看副本进度（始终可用）
        text += "#L0##b副本进度#k — 查看今日各副本完成情况#l\r\n";

        // 选项1：领取单个副本奖励
        text += "#L1##b领取副本奖励#k — 单个副本完成后领取其奖励#l\r\n";

        // 选项2：每日额外奖励
        if (dailyRewards.length > 0) {
            if (allDone && !isDailyRewardClaimed(progress)) {
                text += "#L2##r每日奖励 ★#k — 全部副本已完成，可领取！#l\r\n";
            } else if (isDailyRewardClaimed(progress)) {
                text += "#L2##d每日奖励 — 已领取#k#l\r\n";
            } else {
                var completedDungeons = 0;
                for (var i = 0; i < dungeons.length; i++) {
                    if (getCompletedCount(progress, dungeons[i].key) >= dungeons[i].completeCount) {
                        completedDungeons++;
                    }
                }
                text += "#L2##d每日奖励 — 还需完成全部副本（" + completedDungeons + "/" + dungeons.length + "）#k#l\r\n";
            }
        }

        // 选项3：传送（所有人可见）
        text += "#L3##g副本传送#k — 直接传送到副本地图#l\r\n";

        // 选项4：VIP一键扫荡（仅VIP可见）
        if (hasVipAccess() && sweepRounds > 0 && !progress.sweepUsed) {
            text += "#L4##rVIP扫荡#k — 一键完成所有副本 " + sweepRounds + " 轮#l\r\n";
        } else if (hasVipAccess() && progress.sweepUsed) {
            text += "#L4##dVIP扫荡 — 今日已使用#k#l\r\n";
        }

        cm.sendSimple(text);

    // ==================== 主菜单选择处理（status=1） ====================
    } else if (status == 1 && menuSelection == -1) {
        menuSelection = selection;
        var progress = loadProgress();

        if (menuSelection == 0) {
            // 查看副本进度（sendOk → 结束对话）
            showProgress(progress);
        } else if (menuSelection == 1) {
            // 进入领取副本奖励子菜单（sendSimple → 继续对话）
            showRewardClaimList(progress);
        } else if (menuSelection == 2) {
            // 领取每日额外奖励（sendOk → 结束对话）
            claimDailyReward(progress);
        } else if (menuSelection == 3) {
            // 进入传送子菜单（sendSimple → 继续对话，所有人可用）
            showTeleport();
        } else if (menuSelection == 4) {
            // VIP一键扫荡（sendOk → 结束对话）
            doSweep(progress);
        } else {
            // 无效选择 → 回到主菜单
            status = 0;
            menuSelection = -1;
            action(1, 0, 0);
        }

    // ==================== 领取副本奖励 - 选中具体副本（status=2, menuSelection=1） ====================
    } else if (status == 2 && menuSelection == 1) {
        // 返回上一页 → 回到主菜单
        if (selection == dungeons.length) {
            status = 0;
            menuSelection = -1;
            selectedDungeonIdx = -1;
            action(1, 0, 0);
            return;
        }
        // 校验选择有效性
        if (selection < 0 || selection >= dungeons.length) {
            status = 1;
            var p = loadProgress();
            showRewardClaimList(p);
            return;
        }

        var progress = loadProgress();
        selectedDungeonIdx = selection;
        var d = dungeons[selectedDungeonIdx];
        var completed = getCompletedCount(progress, d.key);
        var target = d.completeCount;
        var claimed = isRewardClaimed(progress, d.key);

        // 已领取 → 提示后回列表，方便继续看其他副本
        if (claimed) {
            status = 1;
            var freshProgress = loadProgress();
            showRewardClaimList(freshProgress, "#b" + d.name + "#k 的奖励今天已经领取过了！\r\n当前完成：#r" + completed + "/" + target + "#k 次\r\n\r\n");
            return;
        }

        // 未完成 → 提示后回列表
        if (completed < target) {
            status = 1;
            var freshProgress = loadProgress();
            showRewardClaimList(freshProgress, "#b" + d.name + "#k 尚未完成！\r\n需要完成 #r" + target + "#k 次，当前完成 #r" + completed + "#k 次\r\n" + makeProgressBar(completed, target) + " #d" + Math.floor(Math.min(100, completed / Math.max(1, target) * 100)) + "%#k\r\n\r\n");
            return;
        }

        // 无奖励配置
        if (d.rewards.length === 0) {
            status = 1;
            var freshProgress = loadProgress();
            showRewardClaimList(freshProgress, "#b" + d.name + "#k 暂无奖励配置，请联系管理员。\r\n\r\n");
            return;
        }

        // 背包空间检查
        for (var j = 0; j < d.rewards.length; j++) {
            if (!cm.canHold(d.rewards[j][0], d.rewards[j][1])) {
                status = 1;
                var freshProgress = loadProgress();
                showRewardClaimList(freshProgress, "背包空间不足，请清理背包后再来领取奖励。\r\n\r\n");
                return;
            }
        }

        // 发放奖励
        for (var j = 0; j < d.rewards.length; j++) {
            cm.gainItem(d.rewards[j][0], d.rewards[j][1]);
        }
        // 标记已领取并保存
        progress.rewardsClaimed[d.key] = true;
        saveProgress(progress);

        // 构建奖励展示文本
        var rewardText = "";
        for (var j = 0; j < d.rewards.length; j++) {
            rewardText += "#i" + d.rewards[j][0] + "# ×" + d.rewards[j][1] + " ";
        }

        // 领取成功后回到列表页，方便继续领取其他副本奖励
        status = 1;
        var freshProgress = loadProgress();
        showRewardClaimList(freshProgress, "#b领取成功！#k\r\n副本：#b" + d.name + "#k\r\n完成次数：#r" + target + "次#k\r\n奖励：" + rewardText + "\r\n奖励已发放至背包，请查收！\r\n\r\n");

    // ==================== 副本传送 - 选中目标副本（status=2, menuSelection=3） ====================
    } else if (status == 2 && menuSelection == 3) {
        // 返回上一页 → 回到主菜单
        if (selection == dungeons.length) {
            status = 0;
            menuSelection = -1;
            selectedDungeonIdx = -1;
            action(1, 0, 0);
            return;
        }
        if (selection < 0 || selection >= dungeons.length) {
            status = 1;
            showTeleport();
            return;
        }

        selectedDungeonIdx = selection;
        var d = dungeons[selectedDungeonIdx];

        cm.getPlayer().dropMessage(6, "[每日副本] 已传送到 " + d.name + "（" + d.mapName + "）");
        cm.warp(d.mapId);
        cm.dispose();

    // ==================== 异常状态恢复 ====================
    } else {
        // 未知状态 → 重置并回到主菜单
        status = 0;
        menuSelection = -1;
        selectedDungeonIdx = -1;
        action(1, 0, 0);
    }
}

// ==================== 进度查看（sendOk → 展示后结束对话） ====================
function showProgress(progress) {
    var text = "\t\t#r#e< 今日副本进度 >#k#n\r\n\r\n";
    var allDone = allDungeonsComplete(progress);

    for (var i = 0; i < dungeons.length; i++) {
        var d = dungeons[i];
        var completed = getCompletedCount(progress, d.key);
        var target = d.completeCount;
        var pct = Math.min(100, Math.floor(completed / Math.max(1, target) * 100));
        var claimed = isRewardClaimed(progress, d.key);
        var done = completed >= target;

        // 副本标题行
        text += "#b" + (i + 1) + ". " + d.name + "#k";
        if (done && claimed) text += "  #d[已领取]#k";
        else if (done && !claimed) text += "  #r[可领取]#k";
        else text += "  #g[进行中]#k";
        text += "\r\n";

        // 地图信息
        text += "   地图：" + d.mapName + "（ID:" + d.mapId + "）\r\n";

        // 进度条 + 数值
        text += "   进度：" + makeProgressBar(completed, target)
            + " #r" + completed + "/" + target + "#k #d(" + pct + "%)#k\r\n";

        // 奖励预览
        if (d.rewards.length > 0) {
            text += "   奖励：";
            for (var j = 0; j < d.rewards.length; j++) {
                text += "#i" + d.rewards[j][0] + "# ×" + d.rewards[j][1] + " ";
            }
            text += "\r\n";
        }

        text += "\r\n";
    }

    // 每日额外奖励状态
    if (dailyRewards.length > 0) {
        text += "━━━ 每日额外奖励 ━━━\r\n";
        if (allDone && !isDailyRewardClaimed(progress)) {
            text += "#r状态：全部副本已完成，可领取每日奖励！#k\r\n";
        } else if (isDailyRewardClaimed(progress)) {
            text += "#d状态：每日奖励已领取#k\r\n";
        } else {
            text += "状态：还有副本未完成，完成后可领取每日奖励\r\n";
        }
        text += "奖励：";
        for (var j = 0; j < dailyRewards.length; j++) {
            text += "#i" + dailyRewards[j][0] + "# ×" + dailyRewards[j][1] + " ";
        }
        text += "\r\n";
    }

    text += "\r\n#d提示：完成副本后请前往「领取副本奖励」领取对应奖励#k";
    cm.sendOk(text);
    cm.dispose();
}

// ==================== 领取副本奖励列表（sendSimple → 子菜单选择） ====================
/**
 * @param progress 副本进度数据
 * @param message  可选提示消息（操作结果反馈，显示在列表顶部）
 */
function showRewardClaimList(progress, message) {
    var text = "\t\t#r#e< 领取副本奖励 >#k#n\r\n\r\n";
    // 如果有操作结果消息，显示在顶部（用颜色高亮）
    if (message) {
        text += message;
    }
    text += "选择副本领取完成奖励：\r\n\r\n";

    var hasAnyClaimable = false;
    for (var i = 0; i < dungeons.length; i++) {
        var d = dungeons[i];
        var completed = getCompletedCount(progress, d.key);
        var target = d.completeCount;
        var claimed = isRewardClaimed(progress, d.key);
        var canClaim = completed >= target && !claimed;

        text += "#L" + i + "##b" + d.name + "#k";
        text += "  " + makeProgressBar(completed, target) + " #r" + completed + "/" + target + "#k";

        if (claimed) {
            text += "  #d[已领取]#k";
        } else if (canClaim) {
            text += "  #r[可领取！]#k";
            hasAnyClaimable = true;
        } else {
            text += "  [未完成]";
        }
        text += "#l\r\n";
    }

    if (!hasAnyClaimable) {
        text += "\r\n#d暂无可领取的奖励，继续完成副本吧！#k\r\n";
    }
    // 返回上一页选项（使用dungeons.length作为索引，与副本索引不冲突）
    text += "\r\n#L" + dungeons.length + "##g返回上一页#k#l\r\n";
    cm.sendSimple(text);
}

// ==================== 领取每日额外奖励（sendOk → 处理后结束对话） ====================
function claimDailyReward(progress) {
    if (dailyRewards.length === 0) {
        cm.sendOk("暂无每日额外奖励配置。");
        cm.dispose();
        return;
    }

    if (!allDungeonsComplete(progress)) {
        var completedDungeons = 0;
        for (var i = 0; i < dungeons.length; i++) {
            if (getCompletedCount(progress, dungeons[i].key) >= dungeons[i].completeCount) {
                completedDungeons++;
            }
        }
        cm.sendOk("还有副本未完成！\r\n\r\n"
            + "已完成副本数：#b" + completedDungeons + "/" + dungeons.length + "#k\r\n"
            + "请完成全部副本后再来领取每日奖励！");
        cm.dispose();
        return;
    }

    if (isDailyRewardClaimed(progress)) {
        cm.sendOk("今日的每日额外奖励已经领取过了，明天再来吧！");
        cm.dispose();
        return;
    }

    // 背包空间检查
    for (var j = 0; j < dailyRewards.length; j++) {
        if (!cm.canHold(dailyRewards[j][0], dailyRewards[j][1])) {
            cm.sendOk("背包空间不足，请清理背包后再来领取每日奖励。");
            cm.dispose();
            return;
        }
    }

    // 发放每日奖励
    for (var j = 0; j < dailyRewards.length; j++) {
        cm.gainItem(dailyRewards[j][0], dailyRewards[j][1]);
    }
    progress.dailyRewardClaimed = true;
    saveProgress(progress);

    // 构建奖励展示文本
    var rewardText = "";
    for (var j = 0; j < dailyRewards.length; j++) {
        rewardText += "#i" + dailyRewards[j][0] + "# ×" + dailyRewards[j][1] + " ";
    }

    // 地图广播
    cm.mapMessage(6, "[每日副本] 恭喜玩家 " + cm.getPlayer().getName() + " 完成了全部每日副本，领取了额外奖励！");

    cm.sendOk("#b🎉 恭喜完成全部每日副本！#k\r\n\r\n"
        + "每日额外奖励：" + rewardText + "\r\n"
        + "奖励已发放至背包，请查收！");
    cm.dispose();
}

// ==================== 副本传送列表（sendSimple → 子菜单选择） ====================
function showTeleport() {
    var text = "\t\t#r#e< 副本传送 >#k#n\r\n\r\n";
    text += "#d选择要传送到哪个副本：#k\r\n\r\n";

    for (var i = 0; i < dungeons.length; i++) {
        var d = dungeons[i];
        text += "#L" + i + "##b" + d.name + "#k";
        text += " — " + d.mapName + "（地图ID:" + d.mapId + "）";
        text += "#l\r\n";
    }

    text += "\r\n#d传送后可直接进入副本地图#k\r\n";
    // 返回上一页选项（使用dungeons.length作为索引，与副本索引不冲突）
    text += "#L" + dungeons.length + "##g返回上一页#k#l\r\n";
    cm.sendSimple(text);
}

// ==================== VIP一键扫荡（sendOk → 处理后结束对话） ====================
function doSweep(progress) {
    if (!hasVipAccess()) {
        cm.sendOk("你没有VIP权限！");
        cm.dispose();
        return;
    }

    if (sweepRounds <= 0) {
        cm.sendOk("扫荡功能未启用，请联系管理员配置扫荡轮数。");
        cm.dispose();
        return;
    }

    if (progress.sweepUsed) {
        cm.sendOk("今日已使用过扫荡功能，明天再来吧！");
        cm.dispose();
        return;
    }

    // 对所有副本增加扫荡轮数的完成次数
    for (var i = 0; i < dungeons.length; i++) {
        var key = dungeons[i].key;
        progress.dungeons[key] = (progress.dungeons[key] || 0) + sweepRounds;
    }
    progress.sweepUsed = true;
    saveProgress(progress);

    var text = "#e⚡ VIP扫荡完成！#n\r\n\r\n";
    text += "扫荡轮数：#r" + sweepRounds + "#k 轮\r\n";
    text += "所有副本完成次数已增加 #r" + sweepRounds + "#k 次\r\n\r\n";
    text += "请前往「领取副本奖励」和「每日奖励」领取！";

    cm.getPlayer().dropMessage(6, "[每日副本] VIP扫荡完成！所有副本 +" + sweepRounds + " 次，快去领取奖励吧！");
    cm.sendOk(text);
    cm.dispose();
}
