// 匠人街 · 每日任务子脚本（通过 9031000 副本讨伐中心菜单进入）
// 每日随机3任务：讨伐/收集/Boss，奖励抵用券+匠人币+材料+经验

var QUEST_ID_BASE = 9900400; // 三个日任：400/401/402
var QUEST_KEY_TODAY = "dailyDate";
var QUEST_KEY_COMPLETE = "dailyDone";

// 日任配置：{name, desc, targetMob, targetCount, rewardMaple, rewardMesos, rewardExp, rewardItem, rewardItemQty}
var HUNT_TASKS = [
    {name: "绿蘑菇讨伐", desc: "击败 #b50只#k 绿蘑菇", mobId: 1110100, count: 50, rewardMaple: 100, rewardExp: 50000, rewardItem: 4001126, rewardItemQty: 5},
    {name: "僵尸蘑菇讨伐", desc: "击败 #b40只#k 僵尸蘑菇", mobId: 2230101, count: 40, rewardMaple: 150, rewardExp: 80000, rewardItem: 4001126, rewardItemQty: 8},
    {name: "火野猪讨伐", desc: "击败 #b45只#k 火野猪", mobId: 2110200, count: 45, rewardMaple: 120, rewardExp: 65000, rewardItem: 4000313, rewardItemQty: 1},
    {name: "月牙牛魔王讨伐", desc: "击败 #b30只#k 月牙牛魔王", mobId: 4230100, count: 30, rewardMaple: 200, rewardExp: 100000, rewardItem: 4001126, rewardItemQty: 10},
    {name: "龙骨精讨伐", desc: "击败 #b25只#k 龙骨精", mobId: 5130100, count: 25, rewardMaple: 250, rewardExp: 150000, rewardItem: 4000313, rewardItemQty: 3},
    {name: "双刀蜥蜴讨伐", desc: "击败 #b35只#k 双刀蜥蜴", mobId: 5120100, count: 35, rewardMaple: 180, rewardExp: 120000, rewardItem: 4001126, rewardItemQty: 12},
    {name: "白狼讨伐", desc: "击败 #b40只#k 白狼", mobId: 4230104, count: 40, rewardMaple: 160, rewardExp: 90000, rewardItem: 4000313, rewardItemQty: 2},
    {name: "人马讨伐", desc: "击败 #b30只#k 人马", mobId: 5120503, count: 30, rewardMaple: 300, rewardExp: 180000, rewardItem: 4001126, rewardItemQty: 15}
];

var COLLECT_TASKS = [
    {name: "矿石收集", desc: "收集 #b20个#k 青铜矿石", itemId: 4010000, count: 20, rewardMaple: 100, rewardExp: 30000, rewardItem: 4001126, rewardItemQty: 8},
    {name: "草药采集", desc: "收集 #b15个#k 杂草", itemId: 4000000, count: 15, rewardMaple: 80, rewardExp: 25000, rewardItem: 4001126, rewardItemQty: 5},
    {name: "怪物掉落收集", desc: "收集 #b30个#k 怪物掉落物", itemId: 4000003, count: 30, rewardMaple: 120, rewardExp: 40000, rewardItem: 4000313, rewardItemQty: 1},
    {name: "装备碎片收集", desc: "收集 #b10个#k 卷轴碎片", itemId: 4001136, count: 10, rewardMaple: 200, rewardExp: 60000, rewardItem: 4001126, rewardItemQty: 10}
];

var BOSS_TASKS = [
    {name: "扎昆讨伐", desc: "击败 #b1次#k 扎昆", bossId: 8800000, count: 1, rewardMaple: 500, rewardExp: 200000, rewardItem: 4000314, rewardItemQty: 1},
    {name: "黑龙讨伐", desc: "击败 #b1次#k 黑龙", bossId: 8810018, count: 1, rewardMaple: 800, rewardExp: 300000, rewardItem: 4000314, rewardItemQty: 2},
    {name: "品克缤讨伐", desc: "击败 #b1次#k 品克缤", bossId: 8820001, count: 1, rewardMaple: 1000, rewardExp: 500000, rewardItem: 4021017, rewardItemQty: 1}
];

var dailyQuests = []; // 今日的三个任务
var status = -1;

function start() {
    status = -1;
    initDaily();
    action(1, 0, 0);
}

function initDaily() {
    var chr = cm.getPlayer();
    var questId = QUEST_ID_BASE;

    // 检查是否跨天
    var today = todayStr();
    var lastDate = getQuestData(questId, QUEST_KEY_TODAY);
    if (lastDate !== today) {
        // 新的一天，随机生成三个任务
        var r = new java.util.Random();
        var huntIdx = r.nextInt(HUNT_TASKS.length);
        var collectIdx = r.nextInt(COLLECT_TASKS.length);
        var bossIdx = r.nextInt(BOSS_TASKS.length);

        setQuestData(questId, QUEST_KEY_TODAY, today);
        setQuestData(questId, "task0_type", "hunt");
        setQuestData(questId, "task0_idx", "" + huntIdx);
        setQuestData(questId, "task0_done", "0");
        setQuestData(questId, "task1_type", "collect");
        setQuestData(questId, "task1_idx", "" + collectIdx);
        setQuestData(questId, "task1_done", "0");
        setQuestData(questId, "task2_type", "boss");
        setQuestData(questId, "task2_idx", "" + bossIdx);
        setQuestData(questId, "task2_done", "0");
        setQuestData(questId, QUEST_KEY_COMPLETE, "0");
    }

    // 加载今日任务
    dailyQuests = [];
    for (var i = 0; i < 3; i++) {
        var type = getQuestData(questId, "task" + i + "_type");
        var idx = parseInt(getQuestData(questId, "task" + i + "_idx") || "0");
        var done = getQuestData(questId, "task" + i + "_done") === "1";

        var task = null;
        if (type === "hunt") task = HUNT_TASKS[idx];
        else if (type === "collect") task = COLLECT_TASKS[idx];
        else if (type === "boss") task = BOSS_TASKS[idx];

        if (task != null) {
            task._done = done;
            task._type = type;
            dailyQuests.push(task);
        }
    }
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        var t = "#e#b<每日任务公告板>#k#n\r\n\r\n";
        t += "每日完成3个任务可获得额外奖励！\r\n";
        t += "奖励：抵用券 + 匠人币 + 经验 + 材料\r\n\r\n";

        for (var i = 0; i < dailyQuests.length; i++) {
            var task = dailyQuests[i];
            t += "#L" + i + "#";
            if (task._done) {
                t += "#g✅ " + task.name + "（已完成）#k";
            } else {
                t += "#b📋 " + task.name + "#k\r\n";
                t += "　" + task.desc + "\r\n";
                t += "　奖励：抵用券×" + task.rewardMaple + " 经验×" + commafy(task.rewardExp);
                if (task.rewardItem > 0) t += " #t" + task.rewardItem + "#×" + task.rewardItemQty;
            }
            t += "#l\r\n";
        }

        var allDone = true;
        for (var j = 0; j < dailyQuests.length; j++) {
            if (!dailyQuests[j]._done) { allDone = false; break; }
        }
        if (allDone && getQuestData(QUEST_ID_BASE, QUEST_KEY_COMPLETE) !== "1") {
            t += "\r\n#L3##r🎁 领取全部完成奖励#k#l\r\n";
        }

        t += "\r\n#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { cm.dispose(); return; }
        if (selection === 3) {
            claimAllReward();
            return;
        }

        var task = dailyQuests[selection];
        if (task == null) { cm.dispose(); return; }
        if (task._done) {
            cm.sendOk("该任务今日已完成！\r\n奖励已发放，请明日再来。");
            cm.dispose();
            return;
        }

        // 检查任务完成条件
        var done = false;
        if (task._type === "hunt") {
            // 检查击杀数（通过quest记录）
            var killed = parseInt(getQuestData(QUEST_ID_BASE, "huntKill_" + selection) || "0");
            if (killed >= task.count) done = true;
            else {
                cm.sendOk("进度：" + killed + "/" + task.count + "\r\n请继续讨伐目标怪物！");
                cm.dispose();
                return;
            }
        } else if (task._type === "collect") {
            if (cm.haveItem(task.itemId, task.count)) {
                cm.gainItem(task.itemId, -task.count);
                done = true;
            } else {
                cm.sendOk("物品不足！需要 #b#t" + task.itemId + "# ×" + task.count + "#k。");
                cm.dispose();
                return;
            }
        } else if (task._type === "boss") {
            cm.sendOk("请通过副本系统击败Boss后回来领取。\r\n提示：普通/团队/进阶挑战副本均可。");
            cm.dispose();
            return;
        }

        if (done) {
            claimTaskReward(selection);
        }
    }
}

function claimTaskReward(idx) {
    var task = dailyQuests[idx];
    // 发放奖励
    cm.getPlayer().getCashShop().gainCash(1, task.rewardMaple);
    cm.getPlayer().gainExp(task.rewardExp, true, true);
    if (task.rewardItem > 0) {
        cm.gainItem(task.rewardItem, task.rewardItemQty);
    }

    // 标记完成
    setQuestData(QUEST_ID_BASE, "task" + idx + "_done", "1");
    task._done = true;

    var msg = "#b✅ " + task.name + " 完成！#k\r\n";
    msg += "获得：抵用券×" + task.rewardMaple + "，经验×" + commafy(task.rewardExp);
    if (task.rewardItem > 0) msg += "，#t" + task.rewardItem + "#×" + task.rewardItemQty;
    cm.sendOk(msg);
    cm.dispose();
}

function claimAllReward() {
    // 全部完成后额外奖励
    cm.getPlayer().getCashShop().gainCash(1, 2000);
    cm.getPlayer().gainExp(500000, true, true);
    cm.gainItem(4001126, 50);
    cm.gainItem(4000313, 5);
    setQuestData(QUEST_ID_BASE, QUEST_KEY_COMPLETE, "1");
    cm.sendOk("#b🎉 恭喜完成全部每日任务！#k\r\n\r\n额外奖励：\r\n抵用券×2000\r\n经验×500,000\r\n匠人币×50\r\n勇者之石×5\r\n\r\n明天再来吧！");
    cm.dispose();
}

// ==================== 工具 ====================

function todayStr() {
    var cal = java.util.Calendar.getInstance();
    return "" + cal.get(java.util.Calendar.YEAR) + (cal.get(java.util.Calendar.MONTH) + 1) + cal.get(java.util.Calendar.DAY_OF_MONTH);
}

function getQuestData(questId, key) {
    try {
        var qr = cm.getPlayer().getQuestNAdd(questId);
        return qr.getProgressValue(key);
    } catch (e) {
        return "";
    }
}

function setQuestData(questId, key, value) {
    var qr = cm.getPlayer().getQuestNAdd(questId);
    qr.setProgressValue(key, value);
    cm.getPlayer().updateQuest(qr);
}

function parseInt(v) {
    var n = java.lang.Integer.parseInt(v);
    return n;
}

function commafy(num) {
    var s = "" + num;
    return s.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
