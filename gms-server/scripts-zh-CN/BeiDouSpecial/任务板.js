/*
 * ==================
 * 脚本类型: 每日/每周任务板
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 每日随机任务：打怪/收集任务
 *   2. 每周随机任务：较大奖励
 *   3. 进度存储在extend_value中
 * ==================
 */

var status = -1;
var DAILY_KEY = "dailyQuestBoard";
var WEEKLY_KEY = "weeklyQuestBoard";

var dailyQuests = [
    { name: "击杀20只蜗牛", mobId: 100100, target: 20, reward: "50000金币+500经验", gold: 50000, exp: 500 },
    { name: "击杀15只绿水灵", mobId: 1110100, target: 15, reward: "80000金币+800经验", gold: 80000, exp: 800 },
    { name: "击杀10只蘑菇", mobId: 1210100, target: 10, reward: "60000金币+600经验", gold: 60000, exp: 600 },
    { name: "击杀25只猪", mobId: 1210101, target: 25, reward: "100000金币+1000经验", gold: 100000, exp: 1000 },
    { name: "击杀15只僵尸", mobId: 5130100, target: 15, reward: "120000金币+1500经验", gold: 120000, exp: 1500 },
    { name: "击杀20只骷髅", mobId: 5130101, target: 20, reward: "100000金币+1200经验", gold: 100000, exp: 1200 },
    { name: "击杀10只火龙", mobId: 7130100, target: 10, reward: "200000金币+2000经验", gold: 200000, exp: 2000 },
    { name: "击杀30只玩具兵", mobId: 4230100, target: 30, reward: "150000金币+1500经验", gold: 150000, exp: 1500 }
];

var weeklyQuests = [
    { name: "击杀100只任意怪物", target: 100, reward: "50万金币+5000经验", gold: 500000, exp: 5000 },
    { name: "击杀5个Boss", target: 5, reward: "100万金币+10000经验", gold: 1000000, exp: 10000 },
    { name: "完成3个城镇任务", target: 3, reward: "80万金币+8000经验", gold: 800000, exp: 8000 }
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 任务板 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "#L0##b每日任务#k#l\r\n";
        text += "#L1##b每周任务#k#l\r\n";
        text += "#L2##b领取奖励#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            showDailyQuests();
        } else if (selection === 1) {
            showWeeklyQuests();
        } else if (selection === 2) {
            claimRewards();
        }
    } else if (status === 2 && selection < dailyQuests.length) {
        acceptDailyQuest(selection);
        cm.dispose();
    } else if (status === 2 && selection >= 100 && selection < 100 + weeklyQuests.length) {
        acceptWeeklyQuest(selection - 100);
        cm.dispose();
    }
}

function showDailyQuests() {
    var text = "#e#b=== 每日任务 ===#k#n\r\n\r\n";
    var today = getToday();
    var dailyData = getData(DAILY_KEY);

    if (dailyData.date === today && dailyData.accepted) {
        text += "今日任务已接取：\r\n";
        text += "#b" + dailyData.questName + "#k\r\n";
        text += "进度：#r" + dailyData.progress + "#k / #b" + dailyData.target + "#k\r\n";
        text += "奖励：#d" + dailyData.reward + "#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "选择一个每日任务：\r\n\r\n";
    for (var i = 0; i < dailyQuests.length; i++) {
        var q = dailyQuests[i];
        text += "#L" + i + "##b" + q.name + "#k  奖励:#d" + q.reward + "#k#l\r\n";
    }
    cm.sendSimple(text);
}

function showWeeklyQuests() {
    var text = "#e#b=== 每周任务 ===#k#n\r\n\r\n";
    var weekNum = getWeekNumber();
    var weeklyData = getData(WEEKLY_KEY);

    if (weeklyData.week === weekNum && weeklyData.accepted) {
        text += "本周任务已接取：\r\n";
        text += "#b" + weeklyData.questName + "#k\r\n";
        text += "进度：#r" + weeklyData.progress + "#k / #b" + weeklyData.target + "#k\r\n";
        text += "奖励：#d" + weeklyData.reward + "#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "选择一个每周任务：\r\n\r\n";
    for (var i = 0; i < weeklyQuests.length; i++) {
        var q = weeklyQuests[i];
        text += "#L" + (100 + i) + "##b" + q.name + "#k  奖励:#d" + q.reward + "#k#l\r\n";
    }
    cm.sendSimple(text);
}

function acceptDailyQuest(idx) {
    var q = dailyQuests[idx];
    var today = getToday();
    saveData(DAILY_KEY, {
        date: today,
        accepted: true,
        questName: q.name,
        target: q.target,
        progress: 0,
        reward: q.reward,
        gold: q.gold,
        exp: q.exp,
        mobId: q.mobId
    });
    cm.sendOk("已接取每日任务：#b" + q.name + "#k\r\n请在今天内完成！");
}

function acceptWeeklyQuest(idx) {
    var q = weeklyQuests[idx];
    var weekNum = getWeekNumber();
    saveData(WEEKLY_KEY, {
        week: weekNum,
        accepted: true,
        questName: q.name,
        target: q.target,
        progress: 0,
        reward: q.reward,
        gold: q.gold,
        exp: q.exp
    });
    cm.sendOk("已接取每周任务：#b" + q.name + "#k\r\n请在本周内完成！");
}

function claimRewards() {
    var today = getToday();
    var weekNum = getWeekNumber();
    var text = "";

    var dailyData = getData(DAILY_KEY);
    if (dailyData.date === today && dailyData.progress >= dailyData.target) {
        cm.getPlayer().gainMeso(dailyData.gold);
        cm.getPlayer().gainExp(dailyData.exp, true, true);
        text += "每日任务完成！获得 #b" + dailyData.gold.toLocaleString() + "#k 金币 + #b" + dailyData.exp + "#k 经验\r\n";
        saveData(DAILY_KEY, {});
    }

    var weeklyData = getData(WEEKLY_KEY);
    if (weeklyData.week === weekNum && weeklyData.progress >= weeklyData.target) {
        cm.getPlayer().gainMeso(weeklyData.gold);
        cm.getPlayer().gainExp(weeklyData.exp, true, true);
        text += "每周任务完成！获得 #b" + weeklyData.gold.toLocaleString() + "#k 金币 + #b" + weeklyData.exp + "#k 经验\r\n";
        saveData(WEEKLY_KEY, {});
    }

    if (text === "") {
        text = "暂无可领取的奖励。\r\n请先接取并完成任务。";
    }

    cm.sendOk(text);
    cm.dispose();
}

function getData(key) {
    var data = cm.getCharacterExtendValue(key);
    if (data == null || data === "") return {};
    try { return JSON.parse(data); } catch (e) { return {}; }
}

function saveData(key, data) {
    cm.saveOrUpdateCharacterExtendValue(key, JSON.stringify(data));
}

function getToday() {
    var d = new Date();
    return d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
}

function getWeekNumber() {
    var d = new Date();
    d.setHours(0, 0, 0, 0);
    d.setDate(d.getDate() + 3 - (d.getDay() + 6) % 7);
    var week1 = new Date(d.getFullYear(), 0, 4);
    return Math.ceil(((d - week1) / 86400000 + 1) / 7);
}
