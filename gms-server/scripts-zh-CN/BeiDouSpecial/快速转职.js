/*
 * ==================
 * 脚本类型: 快速转职
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 支持所有职业的一转/二转/三转/四转
 *   2. 冒险家(5系)、骑士团(5系)、战神、龙神全支持
 *   3. 可根据当前职业自动检测可转职路径
 *   4. 不设等级限制，直接转职
 * ==================
 */

var Job = Java.type('org.gms.client.Job');

var status = -1;
var availableOptions = [];

function start() {
    status = -1;
    availableOptions = [];
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        showCurrentAndOptions();
    } else if (status === 1) {
        if (selection < availableOptions.length) {
            var opt = availableOptions[selection];
            var newJob = Job.getById(opt.id);
            cm.getPlayer().changeJob(newJob);
            cm.sendOk("转职成功！\r\n新职业：#b" + getJobDisplayName(opt.id) + "#k");
        }
        cm.dispose();
    }
}

function showCurrentAndOptions() {
    var curJobId = cm.getJobId();
    var curJobObj = cm.getJob();
    availableOptions = getAdvancementOptions(curJobId);

    var text = "#e#b=== 快速转职 ===#k#n\r\n\r\n";
    text += "当前职业：#b" + getJobDisplayName(curJobId) + "#k (ID: " + curJobId + ")\r\n";
    text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";

    if (availableOptions.length === 0) {
        text += "当前职业已达到最高转职阶段，无法再转职。\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "可选转职路径：\r\n\r\n";
    for (var i = 0; i < availableOptions.length; i++) {
        var opt = availableOptions[i];
        text += "#L" + i + "##b" + opt.label + "#k#l\r\n";
    }

    cm.sendSimple(text);
}

function getAdvancementOptions(curJobId) {
    var opts = [];

    // 初心者 → 一转
    if (curJobId === 0) {
        opts.push({ id: 100, label: "战士 (Warrior)" });
        opts.push({ id: 200, label: "魔法师 (Magician)" });
        opts.push({ id: 300, label: "弓箭手 (Bowman)" });
        opts.push({ id: 400, label: "飞侠 (Thief)" });
        opts.push({ id: 500, label: "海盗 (Pirate)" });
        return opts;
    }

    // 贵族 → 骑士团一转
    if (curJobId === 1000) {
        opts.push({ id: 1100, label: "魂骑士 (Dawn Warrior)" });
        opts.push({ id: 1200, label: "炎术士 (Blaze Wizard)" });
        opts.push({ id: 1300, label: "风灵使者 (Wind Archer)" });
        opts.push({ id: 1400, label: "夜行者 (Night Walker)" });
        opts.push({ id: 1500, label: "奇袭者 (Thunder Breaker)" });
        return opts;
    }

    // 战神初心者 → 战神一转
    if (curJobId === 2000) {
        opts.push({ id: 2100, label: "战神 (Aran) 一转" });
        return opts;
    }

    // 龙神初心者 → 龙神一转
    if (curJobId === 2001) {
        opts.push({ id: 2200, label: "龙神 (Evan) 一转" });
        return opts;
    }

    // 冒险家一转 → 二转分支
    if (curJobId === 100) {
        opts.push({ id: 110, label: "剑客 (Fighter) → 英雄" });
        opts.push({ id: 120, label: "准骑士 (Page) → 圣骑士" });
        opts.push({ id: 130, label: "枪战士 (Spearman) → 黑骑士" });
        return opts;
    }
    if (curJobId === 200) {
        opts.push({ id: 210, label: "火毒法师 (FP Wizard) → 火毒大魔导士" });
        opts.push({ id: 220, label: "冰雷法师 (IL Wizard) → 冰雷大魔导士" });
        opts.push({ id: 230, label: "牧师 (Cleric) → 主教" });
        return opts;
    }
    if (curJobId === 300) {
        opts.push({ id: 310, label: "猎人 (Hunter) → 神箭手" });
        opts.push({ id: 320, label: "弩弓手 (Crossbowman) → 神射手" });
        return opts;
    }
    if (curJobId === 400) {
        opts.push({ id: 410, label: "刺客 (Assassin) → 隐士" });
        opts.push({ id: 420, label: "侠客 (Bandit) → 侠盗" });
        return opts;
    }
    if (curJobId === 500) {
        opts.push({ id: 510, label: "打手 (Brawler) → 冲锋队长" });
        opts.push({ id: 520, label: "枪手 (Gunslinger) → 船长" });
        return opts;
    }

    // 骑士团一转 → 二转 (1100→1110, 1200→1210, etc.)
    if (curJobId >= 1100 && curJobId <= 1500 && curJobId % 100 === 0) {
        opts.push({ id: curJobId + 10, label: getJobDisplayName(curJobId) + " 二转" });
        return opts;
    }

    // 二转 → 三转 (jobId末尾为0: 110→111, 1210→1211, 2110→2111)
    if (curJobId % 10 === 0 && curJobId % 100 !== 0 && curJobId > 0) {
        opts.push({ id: curJobId + 1, label: getJobDisplayName(curJobId) + " 三转" });
        return opts;
    }

    // 三转 → 四转 (jobId末尾为1: 111→112, 1211→1212, 2111→2112)
    if (curJobId % 10 === 1) {
        // 龙神特殊：2201→2210...2218
        if (curJobId >= 2200 && curJobId < 2218) {
            var evanNext = getEvanNextJob(curJobId);
            if (evanNext > 0) {
                opts.push({ id: evanNext, label: getJobDisplayName(curJobId) + " → 进阶" });
            }
        } else {
            opts.push({ id: curJobId + 1, label: getJobDisplayName(curJobId) + " 四转" });
        }
        return opts;
    }

    // 龙神特殊进阶路径 (2210→2211→2212...2218)
    if (curJobId >= 2200 && curJobId < 2218) {
        var evanNext = getEvanNextJob(curJobId);
        if (evanNext > 0) {
            opts.push({ id: evanNext, label: getJobDisplayName(curJobId) + " → 龙神进阶" });
        }
        return opts;
    }

    return opts;
}

function getEvanNextJob(curId) {
    // Evan path: 2001→2200→2210→2211→2212→2213→2214→2215→2216→2217→2218
    if (curId === 2200) return 2210;
    if (curId >= 2210 && curId < 2218) return curId + 1;
    return 0;
}

function getJobDisplayName(jobId) {
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
