/*
 * ==================
 * 脚本类型: GM玩家技能给予工具
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 搜索在线玩家（按世界/频道浏览）
 *   2. 选择职业后展示该职业的所有主动技能
 *   3. 选择技能后给予该玩家满级技能并绑定到T键
 * ==================
 */

var Server = Java.type('org.gms.net.server.Server');
var Job = Java.type('org.gms.client.Job');
var SkillFactory = Java.type('org.gms.client.SkillFactory');
var KeyBinding = Java.type('org.gms.client.keybind.KeyBinding');
var DataProviderFactory = Java.type('org.gms.provider.DataProviderFactory');
var WZFiles = Java.type('org.gms.provider.wz.WZFiles');
var DataTool = Java.type('org.gms.provider.DataTool');

// T键的键码 (标准GMS v083键盘: Q=29, W=30, E=31, R=32, T=33)
var T_KEY = 33;

var status = -1;
var selectedWorld = -1;
var selectedChannelIdx = -1;
var playerList = [];
var selectedPlayerName = "";
var selectedCategory = -1;
var selectedJobId = -1;
var skillList = [];

// 职业分类（包含所有转职阶段）
var jobCategories = [
    {
        name: "初心者系",
        jobs: [
            { id: 0,    name: "初心者 (Beginner)" },
            { id: 1000, name: "贵族 (Noblesse)" },
            { id: 2000, name: "战神初心者 (Aran)" },
            { id: 2001, name: "龙神初心者 (Evan)" }
        ]
    },
    {
        name: "冒险家-战士系",
        jobs: [
            { id: 100, name: "战士 (一转)" },
            { id: 110, name: "剑客 (二转)" },
            { id: 111, name: "骑士 (三转)" },
            { id: 112, name: "英雄 (四转)" },
            { id: 120, name: "准骑士 (二转)" },
            { id: 121, name: "骑士 (三转)" },
            { id: 122, name: "圣骑士 (四转)" },
            { id: 130, name: "枪战士 (二转)" },
            { id: 131, name: "龙骑士 (三转)" },
            { id: 132, name: "黑骑士 (四转)" }
        ]
    },
    {
        name: "冒险家-法师系",
        jobs: [
            { id: 200, name: "魔法师 (一转)" },
            { id: 210, name: "火毒法师 (二转)" },
            { id: 211, name: "火毒巫师 (三转)" },
            { id: 212, name: "火毒大魔导士 (四转)" },
            { id: 220, name: "冰雷法师 (二转)" },
            { id: 221, name: "冰雷巫师 (三转)" },
            { id: 222, name: "冰雷大魔导士 (四转)" },
            { id: 230, name: "牧师 (二转)" },
            { id: 231, name: "祭司 (三转)" },
            { id: 232, name: "主教 (四转)" }
        ]
    },
    {
        name: "冒险家-弓箭手系",
        jobs: [
            { id: 300, name: "弓箭手 (一转)" },
            { id: 310, name: "猎人 (二转)" },
            { id: 311, name: "游侠 (三转)" },
            { id: 312, name: "神箭手 (四转)" },
            { id: 320, name: "弩弓手 (二转)" },
            { id: 321, name: "狙击手 (三转)" },
            { id: 322, name: "神射手 (四转)" }
        ]
    },
    {
        name: "冒险家-飞侠系",
        jobs: [
            { id: 400, name: "飞侠 (一转)" },
            { id: 410, name: "刺客 (二转)" },
            { id: 411, name: "隐士 (三转)" },
            { id: 412, name: "隐士 (四转)" },
            { id: 420, name: "侠客 (二转)" },
            { id: 421, name: "独行侠 (三转)" },
            { id: 422, name: "侠盗 (四转)" }
        ]
    },
    {
        name: "冒险家-海盗系",
        jobs: [
            { id: 500, name: "海盗 (一转)" },
            { id: 510, name: "打手 (二转)" },
            { id: 511, name: "斗士 (三转)" },
            { id: 512, name: "冲锋队长 (四转)" },
            { id: 520, name: "枪手 (二转)" },
            { id: 521, name: "掠夺者 (三转)" },
            { id: 522, name: "船长 (四转)" }
        ]
    },
    {
        name: "骑士团-魂骑士",
        jobs: [
            { id: 1100, name: "魂骑士 (一转)" },
            { id: 1110, name: "魂骑士 (二转)" },
            { id: 1111, name: "魂骑士 (三转)" },
            { id: 1112, name: "魂骑士 (四转)" }
        ]
    },
    {
        name: "骑士团-炎术士",
        jobs: [
            { id: 1200, name: "炎术士 (一转)" },
            { id: 1210, name: "炎术士 (二转)" },
            { id: 1211, name: "炎术士 (三转)" },
            { id: 1212, name: "炎术士 (四转)" }
        ]
    },
    {
        name: "骑士团-风灵使者",
        jobs: [
            { id: 1300, name: "风灵使者 (一转)" },
            { id: 1310, name: "风灵使者 (二转)" },
            { id: 1311, name: "风灵使者 (三转)" },
            { id: 1312, name: "风灵使者 (四转)" }
        ]
    },
    {
        name: "骑士团-夜行者",
        jobs: [
            { id: 1400, name: "夜行者 (一转)" },
            { id: 1410, name: "夜行者 (二转)" },
            { id: 1411, name: "夜行者 (三转)" },
            { id: 1412, name: "夜行者 (四转)" }
        ]
    },
    {
        name: "骑士团-奇袭者",
        jobs: [
            { id: 1500, name: "奇袭者 (一转)" },
            { id: 1510, name: "奇袭者 (二转)" },
            { id: 1511, name: "奇袭者 (三转)" },
            { id: 1512, name: "奇袭者 (四转)" }
        ]
    },
    {
        name: "英雄-战神",
        jobs: [
            { id: 2100, name: "战神 (一转)" },
            { id: 2110, name: "战神 (二转)" },
            { id: 2111, name: "战神 (三转)" },
            { id: 2112, name: "战神 (四转)" }
        ]
    },
    {
        name: "英雄-龙神",
        jobs: [
            { id: 2200, name: "龙神 (一转)" },
            { id: 2210, name: "龙神 (二转)" },
            { id: 2211, name: "龙神 (三转)" },
            { id: 2212, name: "龙神 (四转)" },
            { id: 2213, name: "龙神 (五转)" },
            { id: 2214, name: "龙神 (六转)" },
            { id: 2215, name: "龙神 (七转)" },
            { id: 2216, name: "龙神 (八转)" },
            { id: 2217, name: "龙神 (九转)" },
            { id: 2218, name: "龙神 (十转)" }
        ]
    }
];

function start() {
    status = -1;
    selectedWorld = -1;
    selectedChannelIdx = -1;
    playerList = [];
    selectedPlayerName = "";
    selectedCategory = -1;
    selectedJobId = -1;
    skillList = [];
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

    if (!cm.getPlayer().isGM()) {
        cm.sendOk("该功能仅GM可用。");
        cm.dispose();
        return;
    }

    if (mode === 1) { status++; }

    // ========================================
    // status 0: 世界/频道选择
    // ========================================
    if (status === 0) {
        var text = "#e#b=== 玩家技能给予 ===#k#n\r\n\r\n";
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
        text += "#L99999##r关闭#k#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // status 1: 玩家列表
    // ========================================
    } else if (status === 1) {
        if (selection === 99999) { cm.dispose(); return; }

        selectedWorld = Math.floor(selection / 10000);
        selectedChannelIdx = selection % 10000;

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

    // ========================================
    // status 2: 玩家详情 + 职业分类选择
    // ========================================
    } else if (status === 2) {
        if (selection === 99999) { status = -1; action(1, 0, 0); return; }

        selectedPlayerName = playerList[selection];

        // 验证玩家在线
        if (getTargetPlayer() === null) {
            cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
            cm.dispose();
            return;
        }

        showPlayerDetailAndCategories();

    // ========================================
    // status 3: 选择分类中的具体职业
    // ========================================
    } else if (status === 3) {
        if (selection === 99999) { status = 1; action(1, 0, 0); return; }

        selectedCategory = selection;
        var cat = jobCategories[selectedCategory];

        var text = "#e#b=== 选择职业 ===#k#n\r\n\r\n";
        text += "目标玩家：#b" + selectedPlayerName + "#k\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "【#b" + cat.name + "#k】中的职业：\r\n\r\n";

        for (var i = 0; i < cat.jobs.length; i++) {
            text += "#L" + i + "##b" + cat.jobs[i].name + "#k (ID: " + cat.jobs[i].id + ")#l\r\n";
        }

        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L99999##b返回上级#k#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // status 4: 展示该职业的所有技能
    // ========================================
    } else if (status === 4) {
        if (selection === 99999) { status = 2; action(1, 0, 0); return; }

        var cat = jobCategories[selectedCategory];
        if (selection >= cat.jobs.length) { cm.dispose(); return; }

        selectedJobId = cat.jobs[selection].id;
        skillList = getSkillsForJob(selectedJobId);

        if (skillList.length === 0) {
            cm.sendOk("#r职业【" + getJobName(selectedJobId) + "】(ID:" + selectedJobId + ") 没有找到技能数据。#k\r\n\r\n可能是该职业的技能尚未在WZ数据中定义。");
            cm.dispose();
            return;
        }

        var text = "#e#b=== " + getJobName(selectedJobId) + " (ID:" + selectedJobId + ") 的技能列表 ===#k#n\r\n\r\n";
        text += "目标玩家：#b" + selectedPlayerName + "#k\r\n";
        text += "共 #b" + skillList.length + "#k 个技能\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        for (var i = 0; i < skillList.length; i++) {
            var sk = skillList[i];
            text += "#L" + i + "##b" + sk.name + "#k (ID:" + sk.id + ") 最高等级:" + sk.maxLevel + "#l\r\n";
        }

        text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
        text += "#L99999##b返回上级#k#l\r\n";
        cm.sendSimple(text);

    // ========================================
    // status 5: 确认并给予技能
    // ========================================
    } else if (status === 5) {
        if (selection === 99999) { status = 3; action(1, 0, 0); return; }

        if (selection >= skillList.length) { cm.dispose(); return; }

        var skillInfo = skillList[selection];
        var skill = SkillFactory.getSkill(skillInfo.id);

        if (skill === null) {
            cm.sendOk("#r技能数据无效。#k");
            cm.dispose();
            return;
        }

        var victim = getTargetPlayer();
        if (victim === null) {
            cm.sendOk("#r玩家 " + selectedPlayerName + " 已不在线。#k");
            cm.dispose();
            return;
        }

        // 给予满级技能
        var maxLevel = skill.getMaxLevel();
        victim.changeSkillLevel(skill, Java.type('java.lang.Byte').parseByte(String(maxLevel)), maxLevel, -1);

        // 设置到T键 (type=1表示技能, action=技能ID)
        victim.changeKeybinding(T_KEY, new KeyBinding(1, skillInfo.id));
        victim.sendKeymap();

        var gmName = cm.getPlayer().getName();
        victim.yellowMessage("GM " + gmName + " 给予你技能【" + skillInfo.name + "】(Lv." + maxLevel + ")，已绑定到T键");
        victim.dropMessage(5, "获得新技能: " + skillInfo.name + " (满级), 快捷键: T");

        cm.sendOk("#e#b=== 给予成功 ===#k#n\r\n\r\n"
            + "玩家：#b" + selectedPlayerName + "#k\r\n"
            + "技能：#b" + skillInfo.name + "#k (ID:" + skillInfo.id + ")\r\n"
            + "等级：#b" + maxLevel + " (满级)#k\r\n"
            + "快捷键：#bT键#k\r\n\r\n"
            + "#r玩家重新登录或切换地图后快捷键生效#k");
        cm.dispose();
    }
}

// ==================== 返回处理 ====================
function handleBack() {
    // status: 0(频道)→1(玩家)→2(详情)→3(职业列表)→4(技能列表)→5(确认)
    if (status >= 2) {
        status -= 2;
        action(1, 0, 0);
    } else {
        cm.dispose();
    }
}

// ==================== 获取目标玩家 ====================
function getTargetPlayer() {
    try {
        var world = Server.getInstance().getWorlds().get(selectedWorld);
        var channel = world.getChannels().get(selectedChannelIdx);
        return channel.getPlayerStorage().getCharacterByName(selectedPlayerName);
    } catch (e) {
        return null;
    }
}

// ==================== 显示玩家详情 + 职业分类 ====================
function showPlayerDetailAndCategories() {
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
    text += "\r\n";
    text += "#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#e选择技能所属职业：#n\r\n\r\n";

    for (var i = 0; i < jobCategories.length; i++) {
        text += "#L" + i + "##b" + jobCategories[i].name + "#k";
        text += " (" + jobCategories[i].jobs.length + "个职业)#l\r\n";
    }

    text += "\r\n#d" + "".padStart(30, "——") + "#k\r\n";
    text += "#L99999##b返回玩家列表#k#l\r\n";
    cm.sendSimple(text);
}

// ==================== 获取指定职业的所有技能 ====================
function getSkillsForJob(jobId) {
    var skills = [];
    var dataProvider = DataProviderFactory.getDataProvider(WZFiles.STRING);
    var skillImgData = dataProvider.getData("Skill.img");

    if (skillImgData === null) { return skills; }

    var children = skillImgData.getChildren();

    for (var i = 0; i < children.size(); i++) {
        var child = children.get(i);
        try {
            var skillId = parseInt(child.getName());
            if (isNaN(skillId)) { continue; }

            // 仅匹配当前职业ID的技能 (skillId / 10000 == jobId)
            var skillJobId = Math.floor(skillId / 10000);
            if (skillJobId !== jobId) { continue; }

            // 跳过GM技能和隐藏技能
            if (isGMSkill(skillId)) { continue; }
            if (isBannedBindSkill(skillId)) { continue; }

            var skill = SkillFactory.getSkill(skillId);
            if (skill === null) { continue; }

            var skillName = getSkillNameFromWZ(skillImgData, skillId);

            skills.push({
                id: skillId,
                name: skillName,
                maxLevel: skill.getMaxLevel()
            });
        } catch (e) {
            // 跳过无法解析的数据节点
        }
    }

    // 按技能ID排序
    skills.sort(function(a, b) { return a.id - b.id; });

    return skills;
}

// ==================== 从String.wz获取技能名称 ====================
function getSkillNameFromWZ(skillImgData, skillId) {
    try {
        var nameData = skillImgData.getChildByPath(skillId + "/name");
        if (nameData !== null) {
            var name = DataTool.getString(nameData, "");
            if (name !== "" && name !== "null") {
                return name;
            }
        }
    } catch (e) {}
    return "技能ID:" + skillId;
}

// ==================== 判断是否为GM技能 ====================
function isGMSkill(skillId) {
    return (skillId >= 9001000 && skillId <= 9101008) || (skillId >= 8001000 && skillId <= 8001001);
}

// ==================== 判断是否为禁止绑定的技能 ====================
function isBannedBindSkill(skillId) {
    // PQ技能
    if ((skillId >= 20000014 && skillId <= 20000018) || skillId === 10000013 || skillId === 20001013) {
        return true;
    }
    var remainder = skillId % 10000000;
    if ((remainder >= 1009 && remainder <= 1011) || remainder === 1020) {
        return true;
    }
    // 战神隐藏技能
    if ([21000002, 21000003, 21000004, 21000005, 21100002, 21100003, 21100004, 21100005].indexOf(skillId) !== -1) {
        return true;
    }
    return false;
}

// ==================== 获取职业名称 ====================
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
        900: "GM", 910: "超级GM",
        1000: "贵族",
        1100: "魂骑士(一转)", 1110: "魂骑士(二转)", 1111: "魂骑士(三转)", 1112: "魂骑士(四转)",
        1200: "炎术士(一转)", 1210: "炎术士(二转)", 1211: "炎术士(三转)", 1212: "炎术士(四转)",
        1300: "风灵使者(一转)", 1310: "风灵使者(二转)", 1311: "风灵使者(三转)", 1312: "风灵使者(四转)",
        1400: "夜行者(一转)", 1410: "夜行者(二转)", 1411: "夜行者(三转)", 1412: "夜行者(四转)",
        1500: "奇袭者(一转)", 1510: "奇袭者(二转)", 1511: "奇袭者(三转)", 1512: "奇袭者(四转)",
        2000: "战神初心者", 2001: "龙神初心者",
        2100: "战神(一转)", 2110: "战神(二转)", 2111: "战神(三转)", 2112: "战神(四转)",
        2200: "龙神(一转)", 2210: "龙神(二转)", 2211: "龙神(三转)",
        2212: "龙神(四转)", 2213: "龙神(五转)", 2214: "龙神(六转)",
        2215: "龙神(七转)", 2216: "龙神(八转)", 2217: "龙神(九转)", 2218: "龙神(十转)"
    };
    return names[jobId] || ("职业ID:" + jobId);
}