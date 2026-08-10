/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：十字旅团成长戒指升级系统（野外Boss材料驱动）
 *   1. 可领取初始戒指(1112599)，固有道具不可交换，背包/已装备中有则不可领取
 *   2. 戒指可逐级升级（兑换下一级），每级材料 = 野外Boss特有掉落 + 地区小怪掉落×3种×200 + 金币
 *   3. 初始戒指四维属性各+5
 *   4. 升级后按职业增加二维属性+10/级；物理职业攻+5/级，法师魔力+5/级
 *   5. 倍率变量(RATE)控制材料与金币消耗（除上一级戒指外）
 * ==================
 */

// ===== 职业群常量 =====
var JOB_GROUP = {
    BEGINNER: 0,
    WARRIOR: 1,
    ARCHER: 2,
    MAGICIAN: 3,
    THIEF: 4,
    PIRATE: 5
};

function getJobGroup(jobId) {
    if (jobId === 0 || jobId === 1000 || jobId === 2000 || jobId === 800 || jobId === 900 || jobId === 910) {
        return JOB_GROUP.BEGINNER;
    }
    var rawGroup = Math.floor(jobId / 100) % 10;
    var mapping = {};
    mapping[0] = JOB_GROUP.BEGINNER;
    mapping[1] = JOB_GROUP.WARRIOR;
    mapping[2] = JOB_GROUP.MAGICIAN;
    mapping[3] = JOB_GROUP.ARCHER;
    mapping[4] = JOB_GROUP.THIEF;
    mapping[5] = JOB_GROUP.PIRATE;
    return mapping[rawGroup] !== undefined ? mapping[rawGroup] : JOB_GROUP.BEGINNER;
}

function getJobGroupName(groupId) {
    var names = {};
    names[JOB_GROUP.BEGINNER] = "新手";
    names[JOB_GROUP.WARRIOR] = "战士";
    names[JOB_GROUP.ARCHER] = "弓箭手";
    names[JOB_GROUP.MAGICIAN] = "法师";
    names[JOB_GROUP.THIEF] = "飞侠";
    names[JOB_GROUP.PIRATE] = "海盗";
    return names[groupId] || "未知";
}

// ===== 倍率配置（默认1，修改此处可整体调整材料/金币消耗） =====
var RATE = 1;

// ===== Java类型导入 =====
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

/**
 * 金币阶梯（万）：10 → 50 → 100 → 200 → 300 → 500 → 700 → 1000 → 1500 → 2000 → ...
 * 下标对应 RING_LEVELS 索引（领取初始 / 升到该级）
 */
var MESO_COSTS_WAN = [10, 50, 100, 200, 300, 500, 700, 1000, 1500, 2000, 2500, 3000, 4000, 5000, 6000];

function getMesoCost(levelIndex) {
    var wan = MESO_COSTS_WAN[levelIndex] || (MESO_COSTS_WAN[MESO_COSTS_WAN.length - 1] + (levelIndex - MESO_COSTS_WAN.length + 1) * 1000);
    return Math.floor(wan * 10000 * RATE);
}

function formatMesoWan(meso) {
    return (meso / 10000) + "W";
}

/**
 * 戒指升级配置
 * 格式：[戒指ID, 戒指名称, 野外Boss名, Boss怪物ID, [[材料ID, 基础数量], ...]]
 * 材料规则：野外Boss特有掉落×1 + 该地区常有/特有小怪掉落3种×200
 * 第一级为初始领取；后续升级额外消耗上一级戒指×1
 */
var RING_LEVELS = [
    // Lv0 新手 — 红蜗牛王(2220000) · 维多利亚港
    [1112599, "十字旅团新手戒指", "红蜗牛王", 2220000, [
        [2210006, 1],   // 彩虹色蜗牛壳儿（Boss特有）
        [4000000, 200], // 蓝色蜗牛壳
        [4000016, 200], // 红色蜗牛壳
        [4000019, 200]  // 绿色蜗牛壳
    ]],
    // Lv1 熟练 I — 蘑菇王(6130101) · 魔法密林
    [1112600, "十字旅团熟练戒指 I", "蘑菇王", 6130101, [
        [4000040, 1],   // 蘑菇王芽孢（Boss特有）
        [4000001, 200], // 花蘑菇盖
        [4000009, 200], // 蓝蘑菇盖
        [4000012, 200]  // 绿蘑菇盖
    ]],
    // Lv2 熟练 II — 僵尸蘑菇王(6300005) · 废弃都市迷宫
    [1112601, "十字旅团熟练戒指 II", "僵尸蘑菇王", 6300005, [
        [4000176, 1],   // 毒菇（Boss特有）
        [4000015, 200], // 刺蘑菇盖
        [4000001, 200], // 花蘑菇盖
        [4000002, 200]  // 蝴蝶结
    ]],
    // Lv3 熟练 III — 树妖王(3220000) · 北部岩山
    [1112602, "十字旅团熟练戒指 III", "树妖王", 3220000, [
        [4000195, 1],   // 苗木（Boss特有）
        [4000003, 200], // 树枝
        [4000005, 200], // 叶子
        [4000018, 200]  // 木块
    ]],
    // Lv4 老兵 I — 仙人掌王(3220001) · 阿里安特
    [1112603, "十字旅团老兵戒指 I", "仙人掌王", 3220001, [
        [4000329, 1],   // 仙人球（Boss特有）
        [4000330, 200], // 仙人掌的刺
        [4000331, 200], // 仙人掌的花
        [4000328, 200]  // 沙漠蛇的铃铛
    ]],
    // Lv5 老兵 II — 歇尔夫(4220001) · 水下世界
    [1112604, "十字旅团老兵戒指 II", "歇尔夫", 4220001, [
        [4032474, 1],   // 歇尔夫的珍珠（Boss特有）
        [4000166, 200], // 虾肉
        [4000167, 200], // 坚硬的鳞片
        [4000154, 200]  // 小海豹玩偶
    ]],
    // Lv6 老兵 III — 浮士德(5220002) · 魔法密林深处
    [1112605, "十字旅团老兵戒指 III", "浮士德", 5220002, [
        [4000029, 1],   // 香蕉（Boss特有高掉）
        [4000026, 200], // 猴子娃娃
        [4000031, 200], // 诅咒娃娃
        [4000021, 200]  // 动物皮
    ]],
    // Lv7 勇士 I — 提莫(5220003) · 玩具城钟楼
    [1112606, "十字旅团勇士戒指 I", "提莫", 5220003, [
        [4031991, 1],   // 提莫的蛋（Boss特有）
        [4000113, 200], // 表配件
        [4000114, 200], // 小桌表
        [4000115, 200]  // 齿轮
    ]],
    // Lv8 勇士 II — 多尔(6220000) · 湿地
    [1112607, "十字旅团勇士戒指 II", "多尔", 6220000, [
        [4000032, 1],   // 鳄鱼皮（Boss特有高掉）
        [4000033, 200], // 黑鳄鱼皮
        [4000007, 200], // 火独眼兽之尾
        [4000021, 200]  // 动物皮
    ]],
    // Lv9 勇士 III — 泽诺(6220001) · 地球防御本部
    [1112608, "十字旅团勇士戒指 III", "泽诺", 6220001, [
        [4000117, 1],   // 太空食品（Boss特有高掉）
        [4000118, 200], // 小太空船
        [4000119, 200], // 收报机
        [4000115, 200]  // 齿轮
    ]],
    // Lv10 英雄 I — 肯德熊(7220000) · 武陵桃园
    [1112609, "十字旅团英雄戒指 I", "肯德熊", 7220000, [
        [4000283, 1],   // 熊掌（Boss特有高掉）
        [4000284, 200], // 黄腰带
        [4000285, 200], // 红腰带
        [4000172, 200]  // 三尾狐的尾巴
    ]],
    // Lv11 英雄 II — 九尾狐(7220001) · 童话村
    [1112610, "十字旅团英雄戒指 II", "九尾狐", 7220001, [
        [4031793, 1],   // 九尾狐的尾巴（Boss特有）
        [4000172, 200], // 三尾狐的尾巴
        [4000173, 200], // 扫把
        [4000174, 200]  // 压岁钱袋
    ]],
    // Lv12 英雄 III — 妖怪禅师(7220002) · 武陵
    [1112611, "十字旅团英雄戒指 III", "妖怪禅师", 7220002, [
        [4031789, 1],   // 解毒珠子（Boss特有）
        [4000289, 200], // 猫咪娃娃
        [4000298, 200], // 白纸张
        [4000299, 200]  // 天书
    ]],
    // Lv13 传说 — 艾利杰(8220000) · 神木村天空
    [1112612, "十字旅团传说戒指", "艾利杰", 8220000, [
        [4000073, 1],   // 独角狮硬角（Boss特有高掉）
        [4000074, 200], // 黑色飞狮尾
        [4000071, 200], // 黄独角狮尾
        [4000072, 200]  // 蓝独角狮尾
    ]],
    // Lv14 降魔 — 吉米拉(8220002) · 玛加提亚
    [1112613, "十字旅团降魔戒指", "吉米拉", 8220002, [
        [4000356, 1],   // 长颈瓶（Boss特有高掉）
        [4000364, 200], // 电线包
        [4000365, 200], // 插头
        [4000353, 200]  // 筋骨胶
    ]]
];

var status = -1;
var currentRingIndex = -1;
var pendingTargetIndex = -1;
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";

function start() {
    status = -1;
    currentRingIndex = -1;
    pendingTargetIndex = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        showMainMenu();
    } else if (status == 1) {
        handleSelection(selection);
    } else if (status == 2) {
        if (type == 1) {
            doExchange();
        } else {
            cm.sendOk("已取消兑换。");
            cm.dispose();
        }
    }
}

function getStatKeysByGroup(groupId) {
    if (groupId == JOB_GROUP.WARRIOR) return ["str", "dex"];
    if (groupId == JOB_GROUP.ARCHER) return ["dex", "str"];
    if (groupId == JOB_GROUP.MAGICIAN) return ["int", "luk"];
    if (groupId == JOB_GROUP.THIEF) return ["luk", "dex"];
    if (groupId == JOB_GROUP.PIRATE) return ["str", "dex"];
    return ["str", "dex"];
}

function getStatName(key) {
    if (key == "str") return "力量";
    if (key == "dex") return "敏捷";
    if (key == "int") return "智力";
    if (key == "luk") return "运气";
    return key;
}

function isPhysicalJobGroup(groupId) {
    return groupId == JOB_GROUP.WARRIOR || groupId == JOB_GROUP.ARCHER ||
        groupId == JOB_GROUP.THIEF || groupId == JOB_GROUP.PIRATE ||
        groupId == JOB_GROUP.BEGINNER;
}

function getAtkInfo(groupId) {
    if (groupId == JOB_GROUP.MAGICIAN) {
        return {label: "魔力", key: "matk"};
    }
    return {label: "攻击力", key: "watk"};
}

function hasItemInEquip(itemId) {
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var item = equipInv.getItem(1);
    return item != null && item.getItemId() == itemId;
}

function findCurrentRingIndex() {
    for (var i = RING_LEVELS.length - 1; i >= 0; i--) {
        if (hasItemInEquip(RING_LEVELS[i][0])) {
            return i;
        }
    }
    return -1;
}

function formatMaterials(materials) {
    var text = "";
    for (var n = 0; n < materials.length; n++) {
        var matId = materials[n][0];
        var matQty = Math.floor(materials[n][1] * RATE);
        var tag = (n == 0) ? " #r[Boss]#k" : " #d[地区]#k";
        text += "  #i" + matId + "# #z" + matId + "# x " + matQty + tag + "\r\n";
    }
    return text;
}

function showMainMenu() {
    currentRingIndex = findCurrentRingIndex();
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var statKeys = getStatKeysByGroup(jobGroup);
    var jobName = getJobGroupName(jobGroup);
    var atkInfo = getAtkInfo(jobGroup);

    var text = "#e十字旅团成长戒指#n\r\n";
    text += "#d材料来源：野外Boss特有掉落 + 地区小怪×200 + 金币#k\r\n\r\n";
    text += "你的职业：#b" + jobName + "#k";
    text += "  升级属性：#b" + getStatName(statKeys[0]) + " + " + getStatName(statKeys[1]) + "#k\r\n";
    text += "每次升级 #r+10#k " + getStatName(statKeys[0]) + " / #r+10#k " + getStatName(statKeys[1]) + "\r\n";
    if (isPhysicalJobGroup(jobGroup)) {
        text += "每次升级 #r+5#k 攻击力\r\n";
    } else {
        text += "每次升级 #r+5#k 魔力\r\n";
    }
    text += "━━━━━━━━━━━━━━━━━━\r\n\r\n";

    if (currentRingIndex < 0) {
        var initRing = RING_LEVELS[0];
        var goldCost0 = getMesoCost(0);
        text += "#d当前未拥有十字旅团戒指#k\r\n\r\n";
        text += "#i" + initRing[0] + "# #b" + initRing[1] + "#k\r\n";
        text += "对应野外Boss：#r" + initRing[2] + "#k\r\n";
        text += "初始属性：#r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n";
        text += "费用：#r" + formatMesoWan(goldCost0) + "金币#k\r\n";
        text += "所需材料：\r\n";
        text += formatMaterials(initRing[4]);
        text += "#L0#领取初始戒指#l\r\n";
    } else if (currentRingIndex >= RING_LEVELS.length - 1) {
        var maxRing = RING_LEVELS[currentRingIndex];
        text += "当前戒指：#i" + maxRing[0] + "# #b" + maxRing[1] + "#k\r\n";
        text += "累计属性加成：\r\n";
        text += "  #r" + getStatName(statKeys[0]) + "+" + (currentRingIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (currentRingIndex * 10) + "#k\r\n";
        text += "  #r" + atkInfo.label + "+" + (currentRingIndex * 5) + "#k\r\n";
        text += "  #r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k（初始）\r\n\r\n";
        text += "#g恭喜！已达成最高级别戒指！#k\r\n";
    } else {
        var curRing = RING_LEVELS[currentRingIndex];
        var nextRing = RING_LEVELS[currentRingIndex + 1];
        var nextIndex = currentRingIndex + 1;
        var goldCost = getMesoCost(nextIndex);

        text += "当前戒指：#i" + curRing[0] + "# #b" + curRing[1] + "#k\r\n";
        text += "累计属性加成：\r\n";
        text += "  #r" + getStatName(statKeys[0]) + "+" + (currentRingIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (currentRingIndex * 10) + "#k\r\n";
        if (currentRingIndex > 0) {
            text += "  #r" + atkInfo.label + "+" + (currentRingIndex * 5) + "#k\r\n";
        }
        text += "  #r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k（初始）\r\n\r\n";
        text += "━━━ 可升级至 ━━━\r\n\r\n";
        text += "#i" + nextRing[0] + "# #b" + nextRing[1] + "#k\r\n";
        text += "对应野外Boss：#r" + nextRing[2] + "#k\r\n";
        text += "升级后属性加成：\r\n";
        text += "  #r" + getStatName(statKeys[0]) + "+" + (nextIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (nextIndex * 10) + "#k\r\n";
        text += "  #r" + atkInfo.label + "+" + (nextIndex * 5) + "#k\r\n";
        text += "升级费用：#r" + formatMesoWan(goldCost) + "金币#k\r\n";
        text += "所需材料：\r\n";
        text += "  #i" + curRing[0] + "# #z" + curRing[0] + "# x 1\r\n";
        text += formatMaterials(nextRing[4]);
        text += "#L0#升级戒指#l\r\n";
    }

    text += "\r\n#L98##b查看全部等级材料表#k#l\r\n";
    text += "#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

function showAllLevels() {
    var text = "#e成长戒指 · 全等级材料#n\r\n";
    text += "#d每级 = Boss特有×1 + 地区小怪3种×200 + 金币#k\r\n\r\n";
    for (var i = 0; i < RING_LEVELS.length; i++) {
        var r = RING_LEVELS[i];
        text += "#bLv" + i + "#k #i" + r[0] + "# " + r[1] + "\r\n";
        text += "  Boss：#r" + r[2] + "#k  金币：#r" + MESO_COSTS_WAN[i] + "W#k\r\n";
        text += "  #i" + r[4][0][0] + "#×" + Math.floor(r[4][0][1] * RATE);
        text += " + #i" + r[4][1][0] + "#×" + Math.floor(r[4][1][1] * RATE);
        text += " + #i" + r[4][2][0] + "#×" + Math.floor(r[4][2][1] * RATE);
        text += " + #i" + r[4][3][0] + "#×" + Math.floor(r[4][3][1] * RATE) + "\r\n\r\n";
    }
    text += "#L99#" + 返回图标 + "#l\r\n";
    status = 0;
    cm.sendSimple(text);
}

function handleSelection(selection) {
    if (selection == 99) {
        cm.dispose();
        cm.openNpc(9031004, "xy/匠人街/戒指中心");
        return;
    }
    if (selection == 98) {
        showAllLevels();
        return;
    }

    if (currentRingIndex < 0) {
        handleClaimInitial();
    } else if (currentRingIndex >= RING_LEVELS.length - 1) {
        cm.sendOk("已满级！");
        cm.dispose();
    } else {
        handleUpgrade();
    }
}

function checkMaterials(materials) {
    for (var m = 0; m < materials.length; m++) {
        var matId = materials[m][0];
        var matQty = Math.floor(materials[m][1] * RATE);
        if (!cm.haveItem(matId, matQty)) {
            return matId + "|" + matQty;
        }
    }
    return null;
}

function handleClaimInitial() {
    var initRing = RING_LEVELS[0];
    var ringId = initRing[0];
    var ringName = initRing[1];
    var materials = initRing[4];
    var goldCost = getMesoCost(0);

    if (hasItemInEquip(ringId)) {
        cm.sendOk("你已经拥有 #i" + ringId + "# #b" + ringName + "#k，不可重复领取！");
        cm.dispose();
        return;
    }

    var miss = checkMaterials(materials);
    if (miss != null) {
        var parts = miss.split("|");
        cm.sendOk("#r材料不足！#k\r\n需要 #i" + parts[0] + "# #b#z" + parts[0] + "##k x #r" + parts[1] + "#k\r\n\r\n#d提示：击杀野外Boss #r" + initRing[2] + "#k 获取特有材料。#k");
        cm.dispose();
        return;
    }

    if (cm.getMeso() < goldCost) {
        cm.sendOk("#r金币不足！#k\r\n需要 #b" + formatMesoWan(goldCost) + "#k 金币。");
        cm.dispose();
        return;
    }

    if (!cm.canHold(ringId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来领取！#k");
        cm.dispose();
        return;
    }

    pendingTargetIndex = 0;
    var confirm = "确认领取 #i" + ringId + "# #b" + ringName + "#k？\r\n\r\n";
    confirm += "对应野外Boss：#r" + initRing[2] + "#k\r\n";
    confirm += "费用：#r" + formatMesoWan(goldCost) + "金币#k\r\n";
    confirm += "消耗材料：\r\n" + formatMaterials(materials);
    confirm += "初始属性：#r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n";
    confirm += "#d该戒指为固有道具，不可交换。#k";
    cm.sendYesNo(confirm);
}

function handleUpgrade() {
    var nextIndex = currentRingIndex + 1;
    var nextRing = RING_LEVELS[nextIndex];
    var nextRingId = nextRing[0];
    var nextRingName = nextRing[1];
    var materials = nextRing[4];
    var curRing = RING_LEVELS[currentRingIndex];
    var curRingId = curRing[0];
    var goldCost = getMesoCost(nextIndex);

    if (!hasItemInEquip(curRingId)) {
        cm.sendOk("#r异常：未找到当前戒指，请重试。#k");
        cm.dispose();
        return;
    }

    var miss = checkMaterials(materials);
    if (miss != null) {
        var parts = miss.split("|");
        cm.sendOk("#r材料不足！#k\r\n需要 #i" + parts[0] + "# #b#z" + parts[0] + "##k x #r" + parts[1] + "#k\r\n\r\n#d提示：击杀野外Boss #r" + nextRing[2] + "#k 获取特有材料。#k");
        cm.dispose();
        return;
    }

    if (cm.getMeso() < goldCost) {
        cm.sendOk("#r金币不足！#k\r\n需要 #b" + formatMesoWan(goldCost) + "#k 金币，当前只有 #r" + formatMesoWan(cm.getMeso()) + "#k 金币。");
        cm.dispose();
        return;
    }

    if (!cm.canHold(nextRingId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来升级！#k");
        cm.dispose();
        return;
    }

    pendingTargetIndex = nextIndex;

    var confirmText = "确认升级戒指？\r\n\r\n";
    confirmText += "#i" + curRingId + "# #b" + curRing[1] + "#k\r\n";
    confirmText += "  → #i" + nextRingId + "# #b" + nextRingName + "#k\r\n";
    confirmText += "对应野外Boss：#r" + nextRing[2] + "#k\r\n\r\n";
    confirmText += "升级费用：#r" + formatMesoWan(goldCost) + "金币#k\r\n";
    confirmText += "将消耗以下材料：\r\n";
    confirmText += "  #i" + curRingId + "# #z" + curRingId + "# x 1\r\n";
    confirmText += formatMaterials(materials);

    cm.sendYesNo(confirmText);
}

function doExchange() {
    if (pendingTargetIndex < 0 || pendingTargetIndex >= RING_LEVELS.length) {
        cm.sendOk("兑换数据异常，请重试。");
        cm.dispose();
        return;
    }

    var targetRing = RING_LEVELS[pendingTargetIndex];
    var targetRingId = targetRing[0];
    var targetRingName = targetRing[1];
    var materials = targetRing[4];
    var goldCost = getMesoCost(pendingTargetIndex);
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var statKeys = getStatKeysByGroup(jobGroup);

    if (pendingTargetIndex == 0) {
        if (hasItemInEquip(targetRingId)) {
            cm.sendOk("你已经拥有 #i" + targetRingId + "# #b" + targetRingName + "#k，不可重复领取！");
            cm.dispose();
            return;
        }

        if (checkMaterials(materials) != null || cm.getMeso() < goldCost) {
            cm.sendOk("#r材料或金币不足！#k");
            cm.dispose();
            return;
        }

        cm.gainMeso(-goldCost);
        for (var m2 = 0; m2 < materials.length; m2++) {
            cm.gainItem(materials[m2][0], -Math.floor(materials[m2][1] * RATE));
        }

        cm.gainItem(targetRingId, 1);

        var equipInv0 = cm.getPlayer().getInventory(InventoryType.EQUIP);
        var initEquip = equipInv0.findById(targetRingId);
        if (initEquip != null) {
            initEquip.setStr(5);
            initEquip.setDex(5);
            initEquip.setInt(5);
            initEquip.setLuk(5);
            cm.getPlayer().forceUpdateItem(initEquip);
        }

        cm.sendOk("领取成功！\r\n\r\n#i" + targetRingId + "# #b" + targetRingName + "#k 已放入背包。\r\n属性：#r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n#d该戒指为固有道具，不可交换。#k\r\n#g下一阶对应野外Boss：#r" + RING_LEVELS[1][2] + "#k#n");
        cm.dispose();

    } else {
        var prevRingId = RING_LEVELS[pendingTargetIndex - 1][0];

        if (!hasItemInEquip(prevRingId)) {
            cm.sendOk("#r异常：未找到上一级戒指，请重试。#k");
            cm.dispose();
            return;
        }

        if (checkMaterials(materials) != null || cm.getMeso() < goldCost) {
            cm.sendOk("#r材料或金币不足！#k");
            cm.dispose();
            return;
        }

        if (!cm.canHold(targetRingId, 1)) {
            cm.sendOk("#r背包空间不足！#k");
            cm.dispose();
            return;
        }

        cm.gainMeso(-goldCost);
        cm.gainItem(prevRingId, -1);
        for (var m4 = 0; m4 < materials.length; m4++) {
            cm.gainItem(materials[m4][0], -Math.floor(materials[m4][1] * RATE));
        }

        cm.gainItem(targetRingId, 1);

        var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
        var newEquip = equipInv.findById(targetRingId);
        if (newEquip != null) {
            var statValue = pendingTargetIndex * 10;
            var isFirstStat = (statKeys[0] == "str" || statKeys[1] == "str");
            var isSecondStat = (statKeys[0] == "dex" || statKeys[1] == "dex");
            var isThirdStat = (statKeys[0] == "int" || statKeys[1] == "int");
            var isFourthStat = (statKeys[0] == "luk" || statKeys[1] == "luk");
            newEquip.setStr(5 + (isFirstStat ? statValue : 0));
            newEquip.setDex(5 + (isSecondStat ? statValue : 0));
            newEquip.setInt(5 + (isThirdStat ? statValue : 0));
            newEquip.setLuk(5 + (isFourthStat ? statValue : 0));
            var atkValue = pendingTargetIndex * 5;
            if (isPhysicalJobGroup(jobGroup)) {
                newEquip.setWatk(atkValue);
            } else {
                newEquip.setMatk(atkValue);
            }
            cm.getPlayer().forceUpdateItem(newEquip);
        }

        var atkInfo2 = getAtkInfo(jobGroup);
        var successText = "升级成功！\r\n\r\n";
        successText += "#i" + targetRingId + "# #b" + targetRingName + "#k 已放入背包。\r\n";
        successText += "属性加成：\r\n";
        successText += "  #r" + getStatName(statKeys[0]) + "+" + (pendingTargetIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (pendingTargetIndex * 10) + "#k\r\n";
        successText += "  #r" + atkInfo2.label + "+" + (pendingTargetIndex * 5) + "#k\r\n";
        successText += "  #r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n";
        successText += "#d该戒指为固有道具，不可交换。#k\r\n";
        if (pendingTargetIndex < RING_LEVELS.length - 1) {
            successText += "#g下一阶对应野外Boss：#r" + RING_LEVELS[pendingTargetIndex + 1][2] + "#k#n";
        } else {
            successText += "#g恭喜！已达成最高级别戒指！#k";
        }

        cm.sendOk(successText);
        cm.dispose();
    }
}
