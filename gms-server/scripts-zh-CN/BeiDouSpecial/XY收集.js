/*
 * ==================
 * 脚本类型: XY收集
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 玩家通过收集指定物品来完成各阶段任务
 *   2. 每个类型包含多个阶段，每个阶段有需求物品列表
 *   3. 阶段完成后获得阶段奖励（点卷/抵用券/金币/AP点）
 *   4. 类型全部阶段完成后获得类型奖励
 *   5. 进度持久化，支持跨会话继续收集
 *   6. 配置通过Web后台管理
 * ==================
 */

var XyCollectionManager = Java.type('org.gms.config.XyCollectionManager');
var types = XyCollectionManager.getEnabledTypes();

var status = -1;
var currentTypeIdx = -1;
var currentStageIdx = -1;

function start() {
    status = -1;
    currentTypeIdx = -1;
    currentStageIdx = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        if (currentStageIdx >= 0) {
            currentStageIdx = -1;
            status = -1;
            showStageList();
            return;
        }
        if (currentTypeIdx >= 0) {
            currentTypeIdx = -1;
            currentStageIdx = -1;
            status = -1;
            showMainMenu();
            return;
        }
        cm.dispose();
        return;
    }
    if (mode === 0) { cm.dispose(); return; }

    status++;

    if (currentTypeIdx === -1) {
        if (status === 0) showMainMenu();
        else handleMainSelection(selection);
    } else if (currentStageIdx === -1) {
        if (status === 0) showStageList();
        else handleStageSelection(selection);
    } else {
        if (status === 0) showStageDetail();
        else handleDetailAction(selection);
    }
}

// ==================== 主菜单 ====================
function showMainMenu() {
    var progress = loadProgress();
    var text = "#e#b=== XY收集系统 ===#k#n\r\n\r\n";
    text += "请选择收集类型：\r\n";

    for (var i = 0; i < types.size(); i++) {
        var t = types.get(i);
        var allDone = isTypeCompleted(t.getId(), progress);
        var rewardClaimed = isTypeRewardClaimed(t.getId(), progress);
        var icon = allDone ? (rewardClaimed ? "#g[已完成]#k " : "#r[可领取]#k ") : "";
        var stageCount = t.getStages().size();
        var completedStages = countCompletedStages(t, progress);
        text += "#L" + i + "#" + icon + t.getName() + " (" + completedStages + "/" + stageCount + ")#l\r\n";
    }

    status = 0;
    cm.sendSimple(text);
}

function handleMainSelection(selection) {
    currentTypeIdx = selection;
    currentStageIdx = -1;
    status = -1;
    showStageList();
}

// ==================== 阶段列表 ====================
function showStageList() {
    var t = types.get(currentTypeIdx);
    var stages = t.getStages();
    var progress = loadProgress();
    var typeRewardClaimed = isTypeRewardClaimed(t.getId(), progress);

    var text = "#e#b" + t.getName() + "#k#n\r\n";
    if (t.getDescription()) text += t.getDescription() + "\r\n";
    text += "\r\n阶段列表：\r\n";

    for (var i = 0; i < stages.size(); i++) {
        var s = stages.get(i);
        var done = isStageCompleted(t, s, progress);
        var claimed = isStageRewardClaimed(t.getId(), s.getId(), progress);
        var icon;
        if (done && claimed) icon = "#g[已完成]#k ";
        else if (done) icon = "#r[可领取奖励]#k ";
        else icon = "[进行中] ";
        text += "#L" + i + "#" + icon + s.getName();
        if (s.getRewardType()) text += " (奖励: " + s.getRewardAmount() + getRewardName(s.getRewardType()) + ")";
        text += "#l\r\n";
    }

    text += "\r\n#L" + stages.size() + "##r[返回主菜单]#l\r\n";

    if (isTypeCompleted(t.getId(), progress) && !typeRewardClaimed) {
        text += "#L" + (stages.size() + 1) + "##d[领取类型完成奖励: " + t.getRewardAmount() + getRewardName(t.getRewardType()) + "]#l\r\n";
    }

    status = 0;
    cm.sendSimple(text);
}

function handleStageSelection(selection) {
    var t = types.get(currentTypeIdx);
    var stages = t.getStages();
    var progress = loadProgress();

    if (selection < stages.size()) {
        currentStageIdx = selection;
        status = -1;
        showStageDetail();
    } else if (selection === stages.size()) {
        currentTypeIdx = -1;
        currentStageIdx = -1;
        status = -1;
        showMainMenu();
    } else if (selection === stages.size() + 1) {
        if (isTypeCompleted(t.getId(), progress) && !isTypeRewardClaimed(t.getId(), progress)) {
            claimTypeReward(t, progress);
        } else {
            cm.sendOk("该类型奖励已经领取过了。");
        }
        status = -1;
        showStageList();
    }
}

// ==================== 阶段详情（物品提交） ====================
function showStageDetail() {
    var t = types.get(currentTypeIdx);
    var s = t.getStages().get(currentStageIdx);
    var items = s.getItems();
    var progress = loadProgress();
    var stageDone = isStageCompleted(t, s, progress);
    var stageClaimed = isStageRewardClaimed(t.getId(), s.getId(), progress);

    var text = "#e#b" + t.getName() + " → " + s.getName() + "#k#n\r\n\r\n";
    text += "需求物品清单：\r\n";
    text += "#r现有数量 = 背包中数量 | 已提交数量 | 需求数量#k\r\n\r\n";

    var allDone = true;
    for (var i = 0; i < items.size(); i++) {
        var item = items.get(i);
        var itemId = item.getItemId();
        var required = item.getQuantity();
        var submitted = getSubmittedCount(t.getId(), s.getId(), itemId, progress);
        var inventory = cm.getItemQuantity(itemId);
        var remaining = Math.max(0, required - submitted);
        var status_icon;
        if (submitted >= required) status_icon = "#g[完成]#k ";
        else if (inventory + submitted >= required) status_icon = "#b[可提交]#k ";
        else status_icon = "";
        text += status_icon + "#i" + itemId + "#  " + inventory + " | " + submitted + " | " + required + "\r\n";
        if (submitted < required) allDone = false;
    }

    text += "\r\n";

    if (stageDone && stageClaimed) {
        text += "#g该阶段已完成，奖励已领取#k\r\n";
    } else if (stageDone && !stageClaimed) {
        text += "#L0##r[领取阶段奖励: " + s.getRewardAmount() + getRewardName(s.getRewardType()) + "]#l\r\n";
    } else {
        var canSubmit = false;
        for (var j = 0; j < items.size(); j++) {
            var itm = items.get(j);
            var sub = getSubmittedCount(t.getId(), s.getId(), itm.getItemId(), progress);
            var invCount = cm.getItemQuantity(itm.getItemId());
            var need = itm.getQuantity() - sub;
            if (need > 0 && invCount > 0) { canSubmit = true; break; }
        }
        if (canSubmit) {
            text += "#L1##b[提交收集物品]#l\r\n";
        } else {
            text += "#r背包中没有可提交的物品#k\r\n";
        }
    }

    text += "\r\n#L2##r[返回阶段列表]#l\r\n";

    status = 0;
    cm.sendSimple(text);
}

function handleDetailAction(selection) {
    var t = types.get(currentTypeIdx);
    var s = t.getStages().get(currentStageIdx);
    var progress = loadProgress();

    if (selection === 0) {
        // 领取阶段奖励
        if (isStageCompleted(t, s, progress) && !isStageRewardClaimed(t.getId(), s.getId(), progress)) {
            claimStageReward(t, s, progress);
        } else {
            cm.sendOk("该阶段奖励已经领取过了。");
        }
    } else if (selection === 1) {
        // 提交物品
        submitItems(t, s, progress);
    } else if (selection === 2) {
        currentStageIdx = -1;
        status = -1;
        showStageList();
        return;
    }

    status = -1;
    showStageDetail();
}

// ==================== 物品提交逻辑 ====================
function submitItems(t, s, progress) {
    var items = s.getItems();
    var submittedAny = false;
    var allCompleted = true;
    var submitMsgs = [];

    for (var i = 0; i < items.size(); i++) {
        var item = items.get(i);
        var itemId = item.getItemId();
        var required = item.getQuantity();
        var submitted = getSubmittedCount(t.getId(), s.getId(), itemId, progress);
        var need = Math.max(0, required - submitted);

        if (need <= 0) continue;

        var inInventory = cm.getItemQuantity(itemId);
        if (inInventory <= 0) {
            submitMsgs.push("#i" + itemId + "# 背包中没有该物品");
            allCompleted = false;
            continue;
        }

        var takeAmount = Math.min(need, inInventory);
        cm.gainItem(itemId, -takeAmount);
        var newSubmitted = submitted + takeAmount;
        setSubmittedCount(t.getId(), s.getId(), itemId, newSubmitted, progress);
        submitMsgs.push("#i" + itemId + "# 提交了 #r" + takeAmount + "#k 个 (总计 " + newSubmitted + "/" + required + ")");
        submittedAny = true;

        if (newSubmitted < required) allCompleted = false;
    }

    if (!submittedAny) {
        cm.sendOk("背包中没有可提交的物品。\r\n请收集所需物品后再来提交。");
        saveProgress(progress);
        return;
    }

    var msg = "#e#b提交结果#k#n\r\n\r\n";
    for (var j = 0; j < submitMsgs.length; j++) {
        msg += submitMsgs[j] + "\r\n";
    }

    // 检查阶段是否完成
    if (allCompleted) {
        // 阶段完成, 自动发放阶段奖励
        setStageCompleted(t.getId(), s.getId(), progress);
        giveReward(t.getId(), s.getRewardType(), s.getRewardAmount(), s.getName());
        msg += "\r\n#g恭喜！阶段【" + s.getName() + "】已完成！#k\r\n";
        msg += "获得阶段奖励: #r" + s.getRewardAmount() + getRewardName(s.getRewardType()) + "#k\r\n";

        // 检查类型是否完成
        if (isTypeCompleted(t.getId(), progress)) {
            setTypeCompleted(t.getId(), progress);
            giveReward(t.getId(), t.getRewardType(), t.getRewardAmount(), t.getName());
            msg += "\r\n#d恭喜！整个【" + t.getName() + "】收集全部完成！#k\r\n";
            msg += "获得类型奖励: #r" + t.getRewardAmount() + getRewardName(t.getRewardType()) + "#k\r\n";
        }
    }

    saveProgress(progress);
    cm.sendOk(msg);
}

// ==================== 奖励发放 ====================
function giveReward(typeId, rewardType, amount, name) {
    if (!rewardType || amount <= 0) return;
    switch (rewardType) {
        case "CASH":
            cm.getPlayer().getCashShop().gainCash(1, amount);
            break;
        case "MAPLE_POINT":
            cm.getPlayer().getCashShop().gainCash(2, amount);
            break;
        case "MESO":
            cm.getPlayer().gainMeso(amount);
            break;
        case "AP":
            cm.getPlayer().gainAp(amount, true);
            break;
    }
}

function getRewardName(rewardType) {
    switch (rewardType) {
        case "CASH": return "点卷";
        case "MAPLE_POINT": return "抵用券";
        case "MESO": return "金币";
        case "AP": return "AP点";
        default: return "";
    }
}

function claimStageReward(t, s, progress) {
    setStageRewardClaimed(t.getId(), s.getId(), progress);
    giveReward(s.getId(), s.getRewardType(), s.getRewardAmount(), s.getName());
    saveProgress(progress);
    cm.sendOk("领取阶段奖励: #r" + s.getRewardAmount() + getRewardName(s.getRewardType()) + "#k");
}

function claimTypeReward(t, progress) {
    setTypeRewardClaimed(t.getId(), progress);
    giveReward(t.getId(), t.getRewardType(), t.getRewardAmount(), t.getName());
    saveProgress(progress);
    cm.sendOk("领取类型奖励: #r" + t.getRewardAmount() + getRewardName(t.getRewardType()) + "#k");
}

// ==================== 数据持久化 ====================
function loadProgress() {
    var data = cm.getCharacterExtendValue("xyCollection");
    if (data == null || data === "") return {};
    try { return JSON.parse(data); } catch (e) { return {}; }
}

function saveProgress(data) {
    cm.saveOrUpdateCharacterExtendValue("xyCollection", JSON.stringify(data));
}

// ==================== 进度辅助函数 ====================
function getSubmittedCount(typeId, stageId, itemId, progress) {
    var tid = String(typeId);
    var sid = String(stageId);
    var iid = String(itemId);
    if (!progress[tid]) return 0;
    if (!progress[tid].stages) return 0;
    if (!progress[tid].stages[sid]) return 0;
    if (!progress[tid].stages[sid].items) return 0;
    return progress[tid].stages[sid].items[iid] || 0;
}

function setSubmittedCount(typeId, stageId, itemId, count, progress) {
    var tid = String(typeId);
    var sid = String(stageId);
    var iid = String(itemId);
    if (!progress[tid]) progress[tid] = { stages: {} };
    if (!progress[tid].stages) progress[tid].stages = {};
    if (!progress[tid].stages[sid]) progress[tid].stages[sid] = { items: {} };
    if (!progress[tid].stages[sid].items) progress[tid].stages[sid].items = {};
    progress[tid].stages[sid].items[iid] = count;
}

function setStageCompleted(typeId, stageId, progress) {
    var tid = String(typeId);
    var sid = String(stageId);
    if (!progress[tid]) progress[tid] = { stages: {} };
    if (!progress[tid].stages) progress[tid].stages = {};
    if (!progress[tid].stages[sid]) progress[tid].stages[sid] = { items: {} };
    progress[tid].stages[sid].stageCompleted = true;
}

function setStageRewardClaimed(typeId, stageId, progress) {
    var tid = String(typeId);
    var sid = String(stageId);
    if (!progress[tid]) progress[tid] = { stages: {} };
    if (!progress[tid].stages) progress[tid].stages = {};
    if (!progress[tid].stages[sid]) progress[tid].stages[sid] = { items: {} };
    progress[tid].stages[sid].stageRewardClaimed = true;
}

function setTypeCompleted(typeId, progress) {
    var tid = String(typeId);
    if (!progress[tid]) progress[tid] = { stages: {} };
    progress[tid].typeCompleted = true;
}

function setTypeRewardClaimed(typeId, progress) {
    var tid = String(typeId);
    if (!progress[tid]) progress[tid] = { stages: {} };
    progress[tid].typeRewardClaimed = true;
}

function isStageCompleted(t, s, progress) {
    var items = s.getItems();
    for (var i = 0; i < items.size(); i++) {
        var item = items.get(i);
        var submitted = getSubmittedCount(t.getId(), s.getId(), item.getItemId(), progress);
        if (submitted < item.getQuantity()) return false;
    }
    return true;
}

function isStageRewardClaimed(typeId, stageId, progress) {
    var tid = String(typeId);
    var sid = String(stageId);
    return progress[tid] && progress[tid].stages && progress[tid].stages[sid] && progress[tid].stages[sid].stageRewardClaimed === true;
}

function isTypeCompleted(typeId, progress) {
    var t = null;
    for (var i = 0; i < types.size(); i++) {
        if (types.get(i).getId() == typeId) { t = types.get(i); break; }
    }
    if (!t) return false;
    var stages = t.getStages();
    for (var j = 0; j < stages.size(); j++) {
        if (!isStageCompleted(t, stages.get(j), progress)) return false;
    }
    return true;
}

function isTypeRewardClaimed(typeId, progress) {
    var tid = String(typeId);
    return progress[tid] && progress[tid].typeRewardClaimed === true;
}

function countCompletedStages(t, progress) {
    var stages = t.getStages();
    var count = 0;
    for (var i = 0; i < stages.size(); i++) {
        if (isStageCompleted(t, stages.get(i), progress)) count++;
    }
    return count;
}
