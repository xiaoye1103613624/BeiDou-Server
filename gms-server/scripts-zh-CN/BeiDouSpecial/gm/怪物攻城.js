/*
 * ==================
 * 脚本类型: GM怪物攻城管理
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 快速攻城：预设难度，选择城镇/线路/时长即可启动
 *   2. 自定义攻城：自由配置波次怪物和奖励
 *   3. 查看/取消进行中的攻城
 * ==================
 */

var Server = Java.type('org.gms.net.server.Server');
var MonsterInvasionManager = Java.type('org.gms.server.events.gm.MonsterInvasionManager');
var MonsterInformationProvider = Java.type('org.gms.server.life.MonsterInformationProvider');
var LifeFactory = Java.type('org.gms.server.life.LifeFactory');
var MapId = Java.type('org.gms.constants.id.MapId');

var status = -1;
var mode = "";                    // "quick" | "custom" | "view" | "cancel"
var mgr = MonsterInvasionManager.getInstance();

var selectedDifficulty = -1;
var selectedTownMapId = -1;
var selectedTownName = "";
var selectedChannelId = -1;
var selectedDurationMin = -1;

// 自定义模式数据
var customWaves = [];
var currentWaveMobs = [];
var currentWaveDelay = 0;
var searchResults = [];
var searchInput = "";
var selectedSearchIndex = -1;   // 记录在搜索结果列表中选择的怪物索引

var rewardExpRate = 1.0;
var rewardExpDur = 30;
var rewardDropRate = 1.0;
var rewardDropDur = 30;
var rewardMesoRate = 1.0;
var rewardMesoDur = 30;
var rewardCash = 0;
var rewardMeso = 0;
var rewardItemId = 0;
var rewardItemCount = 0;

// ==================== 难度预设 ====================
// 怪物ID来自WZ确认: 100100=蜗牛 100101=蓝蜗牛 100120=花蘑菇 210100=绿水灵
// 1110100=绿蘑菇 1210100=丝带猪 2130100=蝙蝠 2220100=冰之眼 2230100=野猪 2230101=僵尸蘑菇 2300100=火野猪
var DIFFICULTY_PRESETS = [
    {
        name: "简单",
        desc: "蜗牛/绿水灵x30 → 花蘑菇x15",
        waves: [
            { delay: 0, mobs: [{id: 100100, count: 15}, {id: 100101, count: 10}, {id: 210100, count: 5}] },
            { delay: 90, mobs: [{id: 100120, count: 15}] }
        ],
        expRate: 2.0, expDur: 30, dropRate: 1.0, dropDur: 0, mesoRate: 1.0, mesoDur: 0,
        cash: 0, meso: 50000, itemId: 0, itemCount: 0
    },
    {
        name: "普通",
        desc: "花蘑菇x20 → 丝带猪x15 + 蝙蝠x10",
        waves: [
            { delay: 0, mobs: [{id: 100120, count: 20}] },
            { delay: 60, mobs: [{id: 1210100, count: 15}, {id: 2130100, count: 10}] }
        ],
        expRate: 2.0, expDur: 60, dropRate: 2.0, dropDur: 60, mesoRate: 1.0, mesoDur: 0,
        cash: 500, meso: 200000, itemId: 0, itemCount: 0
    },
    {
        name: "困难",
        desc: "绿蘑菇x20 → 野猪x15 + 冰之眼x10 → 僵尸蘑菇x10 + 火野猪x5",
        waves: [
            { delay: 0, mobs: [{id: 1110100, count: 20}] },
            { delay: 60, mobs: [{id: 2230100, count: 15}, {id: 2220100, count: 10}] },
            { delay: 120, mobs: [{id: 2230101, count: 10}, {id: 2300100, count: 5}] }
        ],
        expRate: 3.0, expDur: 120, dropRate: 3.0, dropDur: 120, mesoRate: 2.0, mesoDur: 120,
        cash: 1000, meso: 500000, itemId: 0, itemCount: 0
    },
    {
        name: "地狱",
        desc: "火野猪x20 → 僵尸蘑菇王x10 + 冰之眼x15 → 全怪物x30",
        waves: [
            { delay: 0, mobs: [{id: 2300100, count: 20}] },
            { delay: 60, mobs: [{id: 2230101, count: 10}, {id: 2220100, count: 15}] },
            { delay: 120, mobs: [{id: 1110100, count: 10}, {id: 2230100, count: 10}, {id: 1210100, count: 10}] }
        ],
        expRate: 5.0, expDur: 180, dropRate: 5.0, dropDur: 180, mesoRate: 3.0, mesoDur: 180,
        cash: 3000, meso: 1000000, itemId: 0, itemCount: 0
    }
];

// ==================== 城镇列表 ====================
var TOWNS = [
    { name: "射手村", mapId: 100000000 },
    { name: "魔法密林", mapId: 101000000 },
    { name: "勇士部落", mapId: 102000000 },
    { name: "废弃都市", mapId: 103000000 },
    { name: "港口", mapId: 104000000 },
    { name: "诺特勒斯", mapId: 120000000 },
    { name: "天空之城", mapId: 200000000 },
    { name: "冰封雪域", mapId: 211000000 },
    { name: "玩具城", mapId: 220000000 },
    { name: "水下世界", mapId: 230000000 },
    { name: "神木村", mapId: 240000000 },
    { name: "武陵", mapId: 250000000 },
    { name: "百草村", mapId: 251000000 },
    { name: "阿里安特", mapId: 260000000 },
    { name: "玛加提亚", mapId: 261000000 },
    { name: "圣地", mapId: 130000000 },
    { name: "里恩", mapId: 140000000 },
    { name: "埃德尔斯坦", mapId: 310000000 }
];

function start() {
    status = -1;
    mode = "";
    selectedDifficulty = -1;
    selectedTownMapId = -1;
    selectedTownName = "";
    selectedChannelId = -1;
    selectedDurationMin = -1;
    customWaves = [];
    currentWaveMobs = [];
    currentWaveDelay = 0;
    searchResults = [];
    searchInput = "";
    rewardExpRate = 1.0; rewardExpDur = 30;
    rewardDropRate = 1.0; rewardDropDur = 30;
    rewardMesoRate = 1.0; rewardMesoDur = 30;
    rewardCash = 0; rewardMeso = 0;
    rewardItemId = 0; rewardItemCount = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) { handleBack(); return; }
    if (mode === 0) { cm.dispose(); return; }
    if (!cm.getPlayer().isGM()) { cm.sendOk("该功能仅GM可用。"); cm.dispose(); return; }
    if (mode === 1) { status++; }

    // ========================================
    // status 0: 主菜单
    // ========================================
    if (status === 0) {
        var text = "#e#b=== 怪物攻城管理 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L0#快速攻城（预设难度）#l\r\n";
        text += "#L1#自定义攻城（自由配置）#l\r\n";
        text += "#L2#查看进行中的攻城#l\r\n";
        text += "#L3#取消攻城#l\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L99999##r关闭#k#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // 快速攻城: status 1-6
    // ========================================
    } else if (status === 1 && mode === "quick") {
        // 选择难度
        var text = "#e#b=== 快速攻城 - 选择难度 ===#k#n\r\n\r\n";
        for (var i = 0; i < DIFFICULTY_PRESETS.length; i++) {
            var d = DIFFICULTY_PRESETS[i];
            text += "#L" + i + "##b" + d.name + "#k - " + d.desc + "#l\r\n";
        }
        text += "\r\n#L99999##b返回#k#l\r\n";
        cm.sendSimple(text);

    } else if (status === 2 && mode === "quick") {
        if (selection === 99999) { status = -1; action(1, 0, 0); return; }
        selectedDifficulty = selection;
        showTownList();

    } else if (status === 3 && mode === "quick") {
        if (selection === 99999) { status = 1; action(1, 0, 0); return; }
        if (selection >= TOWNS.length) { cm.dispose(); return; }
        selectedTownMapId = TOWNS[selection].mapId;
        selectedTownName = TOWNS[selection].name;
        showChannelList();

    } else if (status === 4 && mode === "quick") {
        if (selection === 99999) { status = 2; action(1, 0, 0); return; }
        selectedChannelId = selection + 1;
        cm.sendGetNumber("请输入攻城时长（分钟）:", 10, 1, 120);

    } else if (status === 5 && mode === "quick") {
        selectedDurationMin = selection;
        if (selectedDurationMin < 1) { cm.dispose(); return; }
        showQuickConfirm();

    } else if (status === 6 && mode === "quick") {
        if (selection === 0) {
            doLaunchQuick();
        } else {
            cm.sendOk("已取消。");
            cm.dispose();
        }

    // ========================================
    // 自定义攻城: status 1→town, 2→channel, 3→duration, 4→wave setup, 5→reward setup, 6→confirm
    // ========================================
    } else if (status === 1 && mode === "custom") {
        showTownList();

    } else if (status === 2 && mode === "custom") {
        if (selection === 99999) { status = -1; action(1, 0, 0); return; }
        if (selection >= TOWNS.length) { cm.dispose(); return; }
        selectedTownMapId = TOWNS[selection].mapId;
        selectedTownName = TOWNS[selection].name;
        showChannelList();

    } else if (status === 3 && mode === "custom") {
        if (selection === 99999) { status = 1; action(1, 0, 0); return; }
        selectedChannelId = selection + 1;
        cm.sendGetNumber("请输入攻城时长（分钟）:", 10, 1, 120);

    } else if (status === 4 && mode === "custom") {
        selectedDurationMin = selection;
        if (selectedDurationMin < 1) { cm.dispose(); return; }
        // 开始配置第一波
        customWaves = [];
        startWaveSetup();

    } else if (status === 5 && mode === "custom") {
        // 波次配置子流程（由子函数管理）
        handleWaveSetup(selection);

    } else if (status === 6 && mode === "custom") {
        // 奖励配置
        handleRewardSetup(selection);

    } else if (status === 7 && mode === "custom") {
        // 确认
        showCustomConfirm();
        status = 7;

    } else if (status === 8 && mode === "custom") {
        if (selection === 0) {
            doLaunchCustom();
        } else {
            cm.sendOk("已取消。");
            cm.dispose();
        }

    // ========================================
    // 主菜单分支处理
    // ========================================
    } else if (status === 1 && mode === "") {
        if (selection === 0) {
            mode = "quick";
            status = 0;
            action(1, 0, 0);
        } else if (selection === 1) {
            mode = "custom";
            status = 0;
            action(1, 0, 0);
        } else if (selection === 2) {
            showActiveInvasions();
        } else if (selection === 3) {
            showCancelInvasion();
        } else {
            cm.dispose();
        }

    // 取消攻城确认回调
    } else if (status === 3 && mode === "cancel") {
        if (selection === 1) {
            var worldId = cm.getPlayer().getWorld();
            mgr.cancelInvasion(worldId);
            cm.sendOk("攻城已取消。");
        } else {
            cm.sendOk("已取消操作。");
        }
        cm.dispose();
    }
}

// ==================== 返回处理 ====================
function handleBack() {
    if (mode === "quick") {
        if (status >= 3) { status -= 2; action(1, 0, 0); return; }
    } else if (mode === "custom") {
        if (status >= 3) { status -= 2; action(1, 0, 0); return; }
    }
    if (status >= 1) { status -= 2; action(1, 0, 0); return; }
    cm.dispose();
}

// ==================== UI 辅助函数 ====================
function showTownList() {
    var text = "#e#b=== 选择目标城镇 ===#k#n\r\n\r\n";
    for (var i = 0; i < TOWNS.length; i++) {
        text += "#L" + i + "#" + TOWNS[i].name + " (地图ID:" + TOWNS[i].mapId + ")#l\r\n";
    }
    text += "\r\n#L99999##b返回#k#l\r\n";
    cm.sendSimple(text);
}

function showChannelList() {
    var text = "#e#b=== 选择目标线路 ===#k#n\r\n\r\n";
    text += "目标城镇：#b" + selectedTownName + "#k\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

    var world = Server.getInstance().getWorld(cm.getPlayer().getWorld());
    var channels = world.getChannels();
    for (var c = 0; c < channels.size(); c++) {
        var ch = channels.get(c);
        var online = ch.getPlayerStorage().getSize();
        text += "#L" + c + "#频道#b" + ch.getId() + "#k  在线:#r" + online + "#k人#l\r\n";
    }
    text += "\r\n#L99999##b返回#k#l\r\n";
    cm.sendSimple(text);
}

function showQuickConfirm() {
    var diff = DIFFICULTY_PRESETS[selectedDifficulty];
    var text = "#e#b=== 确认攻城配置 ===#k#n\r\n\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "难度：#r" + diff.name + "#k\r\n";
    text += "城镇：#b" + selectedTownName + "#k (ID:" + selectedTownMapId + ")\r\n";
    text += "线路：#b频道" + selectedChannelId + "#k\r\n";
    text += "时长：#b" + selectedDurationMin + "分钟#k\r\n\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#e怪物波次：#n\r\n";
    for (var i = 0; i < diff.waves.length; i++) {
        var w = diff.waves[i];
        text += "第" + (i + 1) + "波 (延迟" + w.delay + "秒): ";
        var mobNames = [];
        for (var j = 0; j < w.mobs.length; j++) {
            mobNames.push(getMobName(w.mobs[j].id) + "x" + w.mobs[j].count);
        }
        text += mobNames.join(", ") + "\r\n";
    }
    text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#e奖励：#n\r\n";
    var rewards = [];
    if (diff.expRate > 1) rewards.push("EXP" + diff.expRate + "x " + diff.expDur + "分钟");
    if (diff.dropRate > 1) rewards.push("掉率" + diff.dropRate + "x " + diff.dropDur + "分钟");
    if (diff.mesoRate > 1) rewards.push("金币" + diff.mesoRate + "x " + diff.mesoDur + "分钟");
    if (diff.cash > 0) rewards.push("点券" + diff.cash);
    if (diff.meso > 0) rewards.push("金币" + diff.meso.toLocaleString());
    text += rewards.length > 0 ? rewards.join(", ") : "无";
    text += "\r\n\r\n";
    text += "#L0##b确认启动攻城#k#l\r\n";
    text += "#L1##r取消#k#l";
    cm.sendSimple(text);
}

// ==================== 快速攻城启动 ====================
function doLaunchQuick() {
    var diff = DIFFICULTY_PRESETS[selectedDifficulty];
    var worldId = cm.getPlayer().getWorld();
    var cfg = mgr.createConfig();
    cfg.setWorldId(worldId);
    cfg.setChannelId(selectedChannelId);
    cfg.setMapId(selectedTownMapId);
    cfg.setDurationSeconds(selectedDurationMin * 60);

    for (var i = 0; i < diff.waves.length; i++) {
        var wData = diff.waves[i];
        var w = mgr.createWaveConfig();
        w.setDelaySeconds(wData.delay);
        for (var j = 0; j < wData.mobs.length; j++) {
            w.addMob(wData.mobs[j].id, wData.mobs[j].count);
        }
        cfg.addWave(w);
    }

    cfg.setExpRate(diff.expRate);
    cfg.setExpDurationMinutes(diff.expDur);
    cfg.setDropRate(diff.dropRate);
    cfg.setDropDurationMinutes(diff.dropDur);
    cfg.setMesoRate(diff.mesoRate);
    cfg.setMesoDurationMinutes(diff.mesoDur);
    cfg.setCashReward(diff.cash);
    cfg.setMesoReward(diff.meso);

    var ok = mgr.startInvasion(cfg);
    if (ok) {
        cm.sendOk("#e#b攻城已启动！#k#n\r\n\r\n"
            + "难度：#r" + diff.name + "#k\r\n"
            + "城镇：#b" + selectedTownName + "#k\r\n"
            + "时长：#b" + selectedDurationMin + "分钟#k\r\n\r\n"
            + "全服公告已发送，怪物即将入侵！");
    } else {
        cm.sendOk("#r该世界已有进行中的攻城，请先取消再创建新的。#k");
    }
    cm.dispose();
}

// ==================== 自定义攻城 - 波次配置 ====================
var waveSetupStep = 0;          // 0=输入延迟, 1=搜索怪物, 2=显示搜索结果, 3=输入数量选怪, 4=问是否继续加怪, 5=问是否加新波

function startWaveSetup() {
    waveSetupStep = 0;
    currentWaveMobs = [];
    currentWaveDelay = 0;
    var waveNum = customWaves.length + 1;
    cm.sendGetNumber("第" + waveNum + "波 - 请输入该波延迟（秒，0=立即出现）:", 0, 0, 600);
}

function handleWaveSetup(selection) {
    if (waveSetupStep === 0) {
        // 接收延迟时间
        currentWaveDelay = selection;
        waveSetupStep = 1;
        status = 4;
        cm.sendGetText("第" + (customWaves.length + 1) + "波 - 请输入怪物ID（数字）或名称关键字搜索：");
        return;
    }

    if (waveSetupStep === 1) {
        // 搜索怪物
        searchInput = cm.getText().trim();
        if (searchInput === "") { cm.dispose(); return; }

        searchResults = [];
        var idNum = parseInt(searchInput);
        if (!isNaN(idNum) && idNum.toString() === searchInput) {
            var mob = LifeFactory.getMonster(idNum);
            if (mob != null && mob.getName() !== "MISSINGNO") {
                searchResults.push({id: idNum, name: mob.getName()});
            }
        }
        if (searchResults.length === 0) {
            var nameResults = MonsterInformationProvider.getMobsIDsFromName(searchInput);
            for (var i = 0; i < nameResults.size(); i++) {
                var pair = nameResults.get(i);
                searchResults.push({id: pair.getLeft(), name: pair.getRight()});
            }
        }

        if (searchResults.length === 0) {
            cm.sendOk("#r未找到匹配的怪物。#k");
            cm.dispose();
            return;
        }

        if (searchResults.length === 1) {
            selectedSearchIndex = 0;
            waveSetupStep = 3;
            status = 4;
            cm.sendGetNumber("怪物: #b" + searchResults[0].name + "#k (ID:" + searchResults[0].id + ")\r\n请输入生成数量:", 10, 1, 200);
            return;
        }

        waveSetupStep = 2;
        status = 4;
        var text = "找到 #b" + searchResults.length + "#k 个匹配怪物：\r\n\r\n";
        for (var i = 0; i < Math.min(searchResults.length, 50); i++) {
            text += "#L" + i + "##b" + searchResults[i].name + "#k (ID:" + searchResults[i].id + ")#l\r\n";
        }
        cm.sendSimple(text);
        return;
    }

    if (waveSetupStep === 2) {
        selectedSearchIndex = selection;
        waveSetupStep = 3;
        status = 4;
        cm.sendGetNumber("怪物: #b" + searchResults[selection].name + "#k\r\n请输入生成数量:", 10, 1, 200);
        return;
    }

    if (waveSetupStep === 3) {
        var count = selection;
        // 取搜索结果中选中的怪物
        var mobInfo = searchResults[selectedSearchIndex];
        currentWaveMobs.push({id: mobInfo.id, count: count});
        waveSetupStep = 4;
        status = 4;
        cm.sendYesNo("已添加 #b" + mobInfo.name + "#k x" + count + "\r\n当前波次已配置怪物数: " + currentWaveMobs.length + "\r\n\r\n继续向当前波次添加怪物？");
        return;
    }

    if (waveSetupStep === 4) {
        if (selection === 1) {
            waveSetupStep = 1;
            status = 4;
            cm.sendGetText("请输入怪物ID或名称关键字搜索：");
        } else {
            // 不继续添加 → 保存当前波次
            var w = mgr.createWaveConfig();
            w.setDelaySeconds(currentWaveDelay);
            for (var i = 0; i < currentWaveMobs.length; i++) {
                w.addMob(currentWaveMobs[i].id, currentWaveMobs[i].count);
            }
            customWaves.push({delay: currentWaveDelay, mobs: currentWaveMobs.slice()});

            if (customWaves.length >= 5) {
                // 最多5波，直接进入奖励配置
                waveSetupStep = 99;
                startRewardSetup();
            } else {
                waveSetupStep = 5;
                status = 4;
                cm.sendYesNo("当前已配置 " + customWaves.length + " 波。\r\n是否添加新波次？");
            }
        }
        return;
    }

    if (waveSetupStep === 5) {
        if (selection === 1) {
            startWaveSetup();
            status = 4; // 保持在波次配置状态
        } else {
            startRewardSetup();
        }
    }
}

// ==================== 自定义攻城 - 奖励配置 ====================
var rewardSetupStep = 0;

function startRewardSetup() {
    rewardSetupStep = 0;
    status = 5; // 进入reward setup状态
    cm.sendYesNo("波次配置完成（共" + customWaves.length + "波）。\r\n是否配置攻城奖励？\r\n（选否则使用默认奖励：2x EXP 30分钟）");
}

function handleRewardSetup(selection) {
    if (rewardSetupStep === 0) {
        if (selection === 1) {
            rewardSetupStep = 1;
            status = 5;
            cm.sendGetNumber("EXP倍率（1.0=正常, 2.0=双倍）:", 2, 1, 10);
        } else {
            // 默认奖励
            rewardExpRate = 2.0; rewardExpDur = 30;
            rewardDropRate = 1.0; rewardDropDur = 0;
            rewardMesoRate = 1.0; rewardMesoDur = 0;
            rewardCash = 0; rewardMeso = 0;
            rewardItemId = 0; rewardItemCount = 0;
            showCustomConfirm();
            status = 7;
        }
        return;
    }

    if (rewardSetupStep === 1) {
        rewardExpRate = parseFloat(selection);
        rewardSetupStep = 2;
        status = 5;
        cm.sendGetNumber("EXP倍率持续时长（分钟）:", 30, 1, 480);
        return;
    }
    if (rewardSetupStep === 2) {
        rewardExpDur = selection;
        rewardSetupStep = 3;
        status = 5;
        cm.sendGetNumber("掉率倍率（1.0=正常, 0=不开启）:", 1, 0, 10);
        return;
    }
    if (rewardSetupStep === 3) {
        rewardDropRate = parseFloat(selection);
        if (rewardDropRate > 1) {
            rewardSetupStep = 4;
            status = 5;
            cm.sendGetNumber("掉率倍率持续时长（分钟）:", 30, 1, 480);
        } else {
            rewardDropDur = 0;
            rewardSetupStep = 5;
            status = 5;
            cm.sendGetNumber("参与奖励点券（每人, 0=不发放）:", 0, 0, 100000);
        }
        return;
    }
    if (rewardSetupStep === 4) {
        rewardDropDur = selection;
        rewardSetupStep = 5;
        status = 5;
        cm.sendGetNumber("参与奖励点券（每人, 0=不发放）:", 0, 0, 100000);
        return;
    }
    if (rewardSetupStep === 5) {
        rewardCash = selection;
        rewardSetupStep = 6;
        status = 5;
        cm.sendGetNumber("参与奖励金币（每人, 0=不发放）:", 0, 0, 2000000000);
        return;
    }
    if (rewardSetupStep === 6) {
        rewardMeso = selection;
        rewardSetupStep = 7;
        status = 5;
        cm.sendGetText("参与奖励道具ID（每人, 0=不发放）：");
        return;
    }
    if (rewardSetupStep === 7) {
        rewardItemId = parseInt(cm.getText());
        if (rewardItemId > 0) {
            rewardSetupStep = 8;
            status = 5;
            cm.sendGetNumber("道具数量:", 1, 1, 200);
        } else {
            rewardItemCount = 0;
            showCustomConfirm();
            status = 7;
        }
        return;
    }
    if (rewardSetupStep === 8) {
        rewardItemCount = selection;
        showCustomConfirm();
        status = 7;
    }
}

function showCustomConfirm() {
    var text = "#e#b=== 确认自定义攻城配置 ===#k#n\r\n\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "城镇：#b" + selectedTownName + "#k (ID:" + selectedTownMapId + ")\r\n";
    text += "线路：#b频道" + selectedChannelId + "#k\r\n";
    text += "时长：#b" + selectedDurationMin + "分钟#k\r\n";
    text += "波次：#b" + customWaves.length + "波#k\r\n";
    for (var i = 0; i < customWaves.length; i++) {
        var w = customWaves[i];
        text += "  第" + (i + 1) + "波 (延迟" + w.delay + "秒): ";
        var names = [];
        for (var j = 0; j < w.mobs.length; j++) {
            names.push(getMobName(w.mobs[j].id) + "x" + w.mobs[j].count);
        }
        text += names.join(", ") + "\r\n";
    }
    text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#e奖励：#n\r\n";
    var r = [];
    if (rewardExpRate > 1) r.push("EXP" + rewardExpRate + "x " + rewardExpDur + "分钟");
    if (rewardDropRate > 1) r.push("掉率" + rewardDropRate + "x " + rewardDropDur + "分钟");
    if (rewardMesoRate > 1) r.push("金币" + rewardMesoRate + "x " + rewardMesoDur + "分钟");
    if (rewardCash > 0) r.push("点券" + rewardCash);
    if (rewardMeso > 0) r.push("金币" + rewardMeso.toLocaleString());
    if (rewardItemId > 0) r.push("道具ID:" + rewardItemId + "x" + rewardItemCount);
    text += (r.length > 0 ? r.join(", ") : "无") + "\r\n\r\n";
    text += "#L0##b确认启动#k#l\r\n";
    text += "#L1##r取消#k#l";
    cm.sendSimple(text);
}

function doLaunchCustom() {
    var worldId = cm.getPlayer().getWorld();
    var cfg = mgr.createConfig();
    cfg.setWorldId(worldId);
    cfg.setChannelId(selectedChannelId);
    cfg.setMapId(selectedTownMapId);
    cfg.setDurationSeconds(selectedDurationMin * 60);

    for (var i = 0; i < customWaves.length; i++) {
        var wData = customWaves[i];
        var w = mgr.createWaveConfig();
        w.setDelaySeconds(wData.delay);
        for (var j = 0; j < wData.mobs.length; j++) {
            w.addMob(wData.mobs[j].id, wData.mobs[j].count);
        }
        cfg.addWave(w);
    }

    cfg.setExpRate(rewardExpRate);
    cfg.setExpDurationMinutes(rewardExpDur);
    cfg.setDropRate(rewardDropRate);
    cfg.setDropDurationMinutes(rewardDropDur);
    cfg.setMesoRate(rewardMesoRate);
    cfg.setMesoDurationMinutes(rewardMesoDur);
    cfg.setCashReward(rewardCash);
    cfg.setMesoReward(rewardMeso);
    cfg.setRewardItemId(rewardItemId);
    cfg.setRewardItemCount(rewardItemCount);

    var ok = mgr.startInvasion(cfg);
    if (ok) {
        cm.sendOk("#e#b攻城已启动！#k#n\r\n\r\n城镇：#b" + selectedTownName + "#k\r\n时长：#b" + selectedDurationMin + "分钟#k\r\n波次：#b" + customWaves.length + "波#k");
    } else {
        cm.sendOk("#r该世界已有进行中的攻城，请先取消再创建新的。#k");
    }
    cm.dispose();
}

// ==================== 查看/取消 进行中的攻城 ====================
function showActiveInvasions() {
    var worldId = cm.getPlayer().getWorld();
    var st = mgr.getStatus(worldId);

    if (!st.get("active")) {
        cm.sendOk("当前世界没有进行中的攻城。");
        cm.dispose();
        return;
    }

    var text = "#e#b=== 当前攻城状态 ===#k#n\r\n\r\n";
    text += "线路：#b频道" + st.get("channelId") + "#k\r\n";
    text += "地图ID：#b" + st.get("mapId") + "#k\r\n";
    text += "已过时间：#b" + Math.floor(st.get("elapsedSec") / 60) + "分钟#k\r\n";
    text += "剩余时间：#b" + Math.max(0, Math.floor(st.get("remainingSec") / 60)) + "分钟#k\r\n";
    text += "怪物统计：#b存活" + st.get("monstersAlive") + " / 总计" + st.get("totalMonsters") + "#k\r\n";
    text += "参与者：#b" + st.get("participants") + "人#k\r\n\r\n";
    text += "#L99999##b关闭#k#l\r\n";
    cm.sendSimple(text);
}

function showCancelInvasion() {
    var worldId = cm.getPlayer().getWorld();
    if (!mgr.isActive(worldId)) {
        cm.sendOk("当前世界没有进行中的攻城，无需取消。");
        cm.dispose();
        return;
    }
    cm.sendYesNo("#r确认取消当前世界的攻城？#k\r\n\r\n取消后怪物将被清除，攻城终止。");
    status = 2; // 设置标记让下一步处理确认
    mode = "cancel";
}

// ==================== 辅助函数 ====================
function getMobName(mobId) {
    var mob = LifeFactory.getMonster(mobId);
    if (mob != null && mob.getName() !== "MISSINGNO") return mob.getName();
    return "怪物ID:" + mobId;
}
