/*
 * ==================
 * 脚本类型: 全能转职
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 可将任意职业转职为新手或任意一转职业
 *   2. 玩家默认操作自己
 *   3. GM可搜索并选择其他玩家，展示玩家信息确认后再转职
 *   4. 支持冒险家/骑士团/英雄全系列
 * ==================
 */

var Job = Java.type('org.gms.client.Job');
var Server = Java.type('org.gms.net.server.Server');

var status = -1;
var isGM = false;

// targetMode: "self" = 给自己转职, "other" = 给其他玩家转职
var targetMode = "self";

// 玩家选择相关
var selectedWorld = -1;
var selectedChannelIdx = -1;
var playerList = [];
var selectedPlayerName = "";

// 转职目标选择
var selectedCategory = -1;
var pendingJobId = -1;

// 转职分类
var jobCategories = [
    {
        name: "初心者系",
        jobs: [
            { id: 0,    name: "初心者 (Beginner)" },
            { id: 1000, name: "贵族 (Noblesse)" },
            { id: 2000, name: "战神初心者 (Aran Beginner)" },
            { id: 2001, name: "龙神初心者 (Evan Beginner)" }
        ]
    },
    {
        name: "冒险家一转",
        jobs: [
            { id: 100, name: "战士 (Warrior)" },
            { id: 200, name: "魔法师 (Magician)" },
            { id: 300, name: "弓箭手 (Bowman)" },
            { id: 400, name: "飞侠 (Thief)" },
            { id: 500, name: "海盗 (Pirate)" }
        ]
    },
    {
        name: "骑士团一转",
        jobs: [
            { id: 1100, name: "魂骑士 (Dawn Warrior)" },
            { id: 1200, name: "炎术士 (Blaze Wizard)" },
            { id: 1300, name: "风灵使者 (Wind Archer)" },
            { id: 1400, name: "夜行者 (Night Walker)" },
            { id: 1500, name: "奇袭者 (Thunder Breaker)" }
        ]
    },
    {
        name: "英雄一转",
        jobs: [
            { id: 2100, name: "战神 (Aran)" },
            { id: 2200, name: "龙神 (Evan)" }
        ]
    }
];

function start() {
    status = -1;
    isGM = cm.getPlayer().isGM();
    targetMode = "self";
    selectedWorld = -1;
    selectedChannelIdx = -1;
    playerList = [];
    selectedPlayerName = "";
    selectedCategory = -1;
    pendingJobId = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        handleBack();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }

    status++;

    // ==================== SELF 模式 ====================
    if (targetMode === "self") {
        if (status === 0) {
            showMainMenu();
        } else if (status === 1) {
            if (selection === 99) {
                // GM 选择了"为其他玩家转职"
                targetMode = "other";
                status = 0; // 下一步 status 1 显示频道列表
                showChannelList();
                return;
            }
            // 选择了职业分类
            selectedCategory = selection;
            showJobsInCategory(selectedCategory, cm.getPlayer());
        } else if (status === 2) {
            // 选择了具体职业
            var cat = jobCategories[selectedCategory];
            if (selection < cat.jobs.length) {
                pendingJobId = cat.jobs[selection].id;
                executeJobChange(cm.getPlayer(), pendingJobId);
            } else {
                cm.dispose();
            }
        }
        return;
    }

    // ==================== OTHER 模式 (GM为其他玩家转职) ====================
    if (status === 1) {
        // 频道列表
        if (selection === 99999) {
            // 返回主菜单
            targetMode = "self";
            status = -1;
            action(1, 0, 0);
            return;
        }
        selectedWorld = Math.floor(selection / 10000);
        selectedChannelIdx = selection % 10000;
        showPlayerList();

    } else if (status === 2) {
        // 玩家列表
        if (selection === 99999) {
            status = 0; // 回到频道列表
            showChannelList();
            return;
        }
        selectedPlayerName = playerList[selection];
        showPlayerDetail();

    } else if (status === 3) {
        // 玩家详情 + 职业分类
        if (selection === 99999) {
            status = 1; // 回到玩家列表
            showPlayerList();
            return;
        }
        selectedCategory = selection;
        showJobsInCategoryForTarget();

    } else if (status === 4) {
        // 具体职业选择
        if (selection === 99999) {
            status = 2; // 回到玩家详情
            showPlayerDetail();
            return;
        }
        var cat = jobCategories[selectedCategory];
        if (selection < cat.jobs.length) {
            pendingJobId = cat.jobs[selection].id;
            confirmAndExecuteForTarget();
        } else {
            cm.dispose();
        }

    } else if (status === 5) {
        // 确认执行 (sendYesNo已通过mode区分是/否，能走到这里说明用户点击了"是")
        doExecuteOnTarget();
    }
}

// ==================== 返回处理 ====================
function handleBack() {
    if (targetMode === "self") {
        if (status >= 1) {
            status -= 2;
            action(1, 0, 0);
        } else {
            cm.dispose();
        }
    } else {
        // other 模式: status 1(频道)→2(玩家)→3(详情)→4(职业)→5(确认)
        if (status >= 2) {
            status -= 2;
            action(1, 0, 0);
        } else if (status === 1) {
            targetMode = "self";
            status = -1;
            action(1, 0, 0);
        } else {
            cm.dispose();
        }
    }
}

// ==================== 主菜单 ====================
function showMainMenu() {
    var player = cm.getPlayer();
    var curJob = Job.getById(player.getJob().getId());

    var text = "#e#b=== 全能转职 ===#k#n\r\n\r\n";
    text += "当前角色：#b" + player.getName() + "#k\r\n";
    text += "当前职业：#b" + getJobName(player.getJob().getId()) + "#k (ID: " + player.getJob().getId() + ")\r\n";
    text += "当前等级：#bLv." + player.getLevel() + "#k\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
    text += "#e选择目标职业分类：#n\r\n\r\n";

    for (var i = 0; i < jobCategories.length; i++) {
        text += "#L" + i + "##b" + jobCategories[i].name + "#k";
        text += "  (" + jobCategories[i].jobs.length + "个职业)#l\r\n";
    }

    if (isGM) {
        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L99##r[GM] 为其他玩家转职#k#l\r\n";
    }

    cm.sendSimple(text);
}

// ==================== 频道列表 ====================
function showChannelList() {
    var text = "#e#b=== 选择目标玩家 ===#k#n\r\n\r\n";
    text += "请选择目标玩家所在频道：\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

    var worlds = Server.getInstance().getWorlds();
    var channelCount = 0;

    for (var w = 0; w < worlds.size(); w++) {
        var world = worlds.get(w);
        var channels = world.getChannels();

        for (var c = 0; c < channels.size(); c++) {
            var channel = channels.get(c);
            var playerCount = channel.getPlayerStorage().getSize();
            var encode = w * 10000 + c;
            text += "#L" + encode + "#";
            text += "世界#b" + world.getId() + "#k 频道#b" + (c + 1) + "#k  ";
            text += "在线: #r" + playerCount + "#k 人";
            text += "#l\r\n";
            channelCount++;
        }
    }

    if (channelCount === 0) {
        text += "#r没有可用频道#k\r\n";
    }

    text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#L99999##b返回主菜单#k#l\r\n";
    cm.sendSimple(text);
}

// ==================== 玩家列表 ====================
function showPlayerList() {
    var world = Server.getInstance().getWorlds().get(selectedWorld);
    var channel = world.getChannels().get(selectedChannelIdx);
    var allPlayers = channel.getPlayerStorage().getAllCharacters().toArray();

    playerList = [];

    var text = "#e#b=== 世界 " + world.getId() + " 频道 " + (selectedChannelIdx + 1) + " ===#k#n\r\n\r\n";

    if (allPlayers.length === 0) {
        text += "#r该频道无在线玩家#k\r\n\r\n";
        text += "#L99999##b返回上级#k#l\r\n";
    } else {
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        for (var i = 0; i < allPlayers.length; i++) {
            var p = allPlayers[i];
            playerList.push(p.getName());
            var pJob = Job.getById(p.getJob().getId());
            var gmTag = p.isGM() ? " #r[GM]#k" : "";

            text += "#L" + i + "#";
            text += "#b" + (i + 1) + ".#k ";
            text += "#b" + p.getName() + "#k" + gmTag + "  ";
            text += "Lv." + p.getLevel() + "  " + pJob.getName() + "  ";
            text += "地图:" + p.getMapId();
            text += "#l\r\n";
        }
        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L99999##b返回上级#k#l\r\n";
    }

    cm.sendSimple(text);
}

// ==================== 玩家详情 ====================
function showPlayerDetail() {
    var victim = getTargetPlayer();
    if (victim === null) {
        cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
        cm.dispose();
        return;
    }

    var victimJob = Job.getById(victim.getJob().getId());

    var text = "#e#b=== 目标玩家信息 ===#k#n\r\n\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "角色名：#b" + victim.getName() + "#k";
    if (victim.isGM()) { text += "  #r[GM]#k"; }
    text += "\r\n";
    text += "等级：  #bLv." + victim.getLevel() + "#k\r\n";
    text += "当前职业：#b" + getJobName(victim.getJob().getId()) + "#k (ID: " + victim.getJob().getId() + ")\r\n";
    text += "HP：#b" + victim.getHp() + "#k / #b" + victim.getCurrentMaxHp() + "#k  ";
    text += "MP：#b" + victim.getMp() + "#k / #b" + victim.getCurrentMaxMp() + "#k\r\n";
    text += "力量:#b" + victim.getStr() + "#k  敏捷:#b" + victim.getDex() + "#k  ";
    text += "智力:#b" + victim.getInt() + "#k  运气:#b" + victim.getLuk() + "#k\r\n";
    text += "\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#e选择目标职业分类：#n\r\n\r\n";

    for (var i = 0; i < jobCategories.length; i++) {
        text += "#L" + i + "##b" + jobCategories[i].name + "#k";
        text += "  (" + jobCategories[i].jobs.length + "个职业)#l\r\n";
    }

    text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#L99999##b返回玩家列表#k#l\r\n";
    cm.sendSimple(text);
}

// ==================== 目标玩家的具体职业列表 ====================
function showJobsInCategoryForTarget() {
    var victim = getTargetPlayer();
    if (victim === null) {
        cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
        cm.dispose();
        return;
    }

    var cat = jobCategories[selectedCategory];
    var text = "#e#b=== 目标玩家：" + victim.getName() + " ===#k#n\r\n\r\n";
    text += "当前职业：#b" + getJobName(victim.getJob().getId()) + "#k (ID: " + victim.getJob().getId() + ")\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
    text += "选择【#b" + cat.name + "#k】中的目标职业：\r\n\r\n";

    for (var i = 0; i < cat.jobs.length; i++) {
        text += "#L" + i + "##b" + cat.jobs[i].name + "#k (ID: " + cat.jobs[i].id + ")#l\r\n";
    }

    text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#L99999##b返回上级#k#l\r\n";
    cm.sendSimple(text);
}

// ==================== 确认执行（目标玩家） ====================
function confirmAndExecuteForTarget() {
    var victim = getTargetPlayer();
    if (victim === null) {
        cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
        cm.dispose();
        return;
    }

    var oldJobName = getJobName(victim.getJob().getId());
    var newJobName = getJobName(pendingJobId);

    var text = "#e#b=== 确认转职 ===#k#n\r\n\r\n";
    text += "目标玩家：#b" + victim.getName() + "#k\r\n";
    text += "当前职业：#b" + oldJobName + "#k (ID: " + victim.getJob().getId() + ")\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "目标职业：#r" + newJobName + "#k (ID: " + pendingJobId + ")\r\n\r\n";
    text += "#r警告：转职后技能点/能力点将会重新计算，请确认操作！#k";

    cm.sendYesNo(text);
}

// ==================== 执行转职（目标玩家） ====================
function doExecuteOnTarget() {
    var victim = getTargetPlayer();
    if (victim === null) {
        cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
        cm.dispose();
        return;
    }

    var oldJobName = getJobName(victim.getJob().getId());
    var newJob = Job.getById(pendingJobId);
    victim.changeJob(newJob);

    var gmName = cm.getPlayer().getName();
    victim.yellowMessage("GM " + gmName + " 已将你的职业变更为 " + getJobName(pendingJobId));

    cm.sendOk("#b转职成功！#k\r\n\r\n"
        + "玩家：#b" + victim.getName() + "#k\r\n"
        + "原职业：#b" + oldJobName + "#k\r\n"
        + "新职业：#b" + getJobName(pendingJobId) + "#k (ID: " + pendingJobId + ")");
    cm.dispose();
}

// ==================== 自己的具体职业列表 ====================
function showJobsInCategory(categoryIdx, player) {
    var cat = jobCategories[categoryIdx];
    var text = "#e#b=== 选择目标职业 ===#k#n\r\n\r\n";
    text += "当前职业：#b" + getJobName(player.getJob().getId()) + "#k\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
    text += "【#b" + cat.name + "#k】中的职业：\r\n\r\n";

    for (var i = 0; i < cat.jobs.length; i++) {
        text += "#L" + i + "##b" + cat.jobs[i].name + "#k (ID: " + cat.jobs[i].id + ")#l\r\n";
    }

    cm.sendSimple(text);
}

// ==================== 执行转职（自己） ====================
function executeJobChange(player, jobId) {
    var oldJobName = getJobName(player.getJob().getId());
    var newJob = Job.getById(jobId);
    player.changeJob(newJob);

    cm.sendOk("#b转职成功！#k\r\n\r\n"
        + "原职业：#b" + oldJobName + "#k\r\n"
        + "新职业：#b" + getJobName(jobId) + "#k (ID: " + jobId + ")");
    cm.dispose();
}

// ==================== 辅助函数 ====================
function getTargetPlayer() {
    try {
        var world = Server.getInstance().getWorlds().get(selectedWorld);
        var channel = world.getChannels().get(selectedChannelIdx);
        return channel.getPlayerStorage().getCharacterByName(selectedPlayerName);
    } catch (e) {
        return null;
    }
}

function getJobName(jobId) {
    var names = {
        0: "初心者",
        100: "战士", 110: "剑客", 111: "骑士", 112: "英雄",
        120: "准骑士", 121: "骑士", 122: "圣骑士",
        130: "枪战士", 131: "龙骑士", 132: "黑骑士",
        200: "魔法师", 210: "火毒法师", 211: "火毒巫师", 212: "火毒大魔导士",
        220: "冰雷法师", 221: "冰雷巫师", 222: "冰雷大魔导士",
        230: "牧师", 231: "祭司", 232: "主教",
        300: "弓箭手", 310: "猎人", 311: "游侠", 312: "神箭手",
        320: "弩弓手", 321: "狙击手", 322: "神射手",
        400: "飞侠", 410: "刺客", 411: "隐士", 412: "隐士",
        420: "侠客", 421: "独行侠", 422: "侠盗",
        500: "海盗", 510: "打手", 511: "斗士", 512: "冲锋队长",
        520: "枪手", 521: "掠夺者", 522: "船长",
        800: "枫叶卫兵", 900: "GM", 910: "超级GM",
        1000: "贵族",
        1100: "魂骑士(一转)", 1110: "魂骑士(二转)", 1111: "魂骑士(三转)", 1112: "魂骑士(四转)",
        1200: "炎术士(一转)", 1210: "炎术士(二转)", 1211: "炎术士(三转)", 1212: "炎术士(四转)",
        1300: "风灵使者(一转)", 1310: "风灵使者(二转)", 1311: "风灵使者(三转)", 1312: "风灵使者(四转)",
        1400: "夜行者(一转)", 1410: "夜行者(二转)", 1411: "夜行者(三转)", 1412: "夜行者(四转)",
        1500: "奇袭者(一转)", 1510: "奇袭者(二转)", 1511: "奇袭者(三转)", 1512: "奇袭者(四转)",
        2000: "战神初心者", 2001: "龙神初心者",
        2100: "战神(一转)", 2110: "战神(二转)", 2111: "战神(三转)", 2112: "战神(四转)",
        2200: "龙神(一转)", 2210: "龙神(二阶)", 2211: "龙神(三阶)",
        2212: "龙神(四阶)", 2213: "龙神(五阶)", 2214: "龙神(六阶)",
        2215: "龙神(七阶)", 2216: "龙神(八阶)", 2217: "龙神(九阶)", 2218: "龙神(十阶)"
    };
    return names[jobId] || ("职业ID:" + jobId);
}