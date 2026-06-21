/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：十字旅团戒指升级系统
 *   1. 可领取初始戒指(1112599)，固有道具不可交换，背包/已装备中有则不可领取
 *   2. 戒指可逐级升级（兑换下一级），每次消耗指定材料
 *   3. 初始戒指四维属性各+5（力量/敏捷/智力/运气+5）
 *   4. 升级后戒指按职业增加二维属性+10/级：
 *      战士: 力量+敏捷 | 弓箭手: 敏捷+力量 | 法师: 智力+运气
 *      飞侠: 运气+敏捷 | 海盗: 力量+敏捷
 *   5. 升级后物理职业（战士/弓箭手/飞侠/海盗）攻击力+5/级，法师魔力+5/级
 *   6. 倍率变量(RATE)控制材料消耗，除戒指外所有材料乘以该倍率
 * ==================
 */
// ===== 材料常量（内联自材料.js，避免load()兼容问题） =====
var _枫叶_ = 4001126;
var _青铜母矿_ = 4010000;
var _钢铁母矿_ = 4010001;
var _朱矿石母矿_ = 4010003;
var _银母矿_ = 4010004;
var _紫矿石母矿_ = 4010005;
var _黄金母矿_ = 4010006;
var _石榴石母矿_ = 4020000;
var _海蓝石母矿_ = 4020002;
var _蛋白石母矿_ = 4020004;
var _蓝宝石母矿_ = 4020005;
var _黄晶母矿_ = 4020006;
var _钻石母矿_ = 4020007;
var _黑水晶母矿_ = 4020008;
var _黑暗水晶母矿_ = 4004004;
var _力量水晶_ = 4005000;
var _智慧水晶_ = 4005001;
var _敏捷水晶_ = 4005002;
var _幸运水晶_ = 4005003;
var _星石_ = 4021009;
var _月石_ = 4011007;

// ===== 职业群常量与方法（内联自职业群.js，避免load()兼容问题） =====
var JOB_GROUP = {
    BEGINNER: 0,  // 新手
    WARRIOR: 1,  // 战士
    ARCHER: 2,  // 弓箭手
    MAGICIAN: 3,  // 法师
    THIEF: 4,  // 飞侠
    PIRATE: 5   // 海盗
};

/**
 * 根据 jobId 获取职业群编号 0~5
 * 兼容冒险家、骑士团(1100~)、战神(2100~)、龙神(2200~) 等分支
 */
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

/**
 * 获取职业群中文名
 */
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

// ===== 倍率配置（默认1，修改此处可整体调整材料消耗） =====
var RATE = 1;

// ===== Java类型导入 =====
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

// ===== 戒指升级配置 =====
// 格式：[戒指ID, 戒指名称, [[材料ID, 基础数量], ...]]
// 注意：第一级为初始领取，无"上一级戒指"材料
//       后续级别第一个材料固定为上一级戒指（数量1），脚本自动添加
var RING_LEVELS = [
    // Lv0: 初始戒指
    [1112599, "十字旅团新手戒指", [
        // 蜗牛壳
        [4000019, 50],
        // 青铜矿
        [_青铜母矿_, 5]
    ]],
    // Lv1: 熟练 I
    [1112600, "十字旅团熟练戒指 I", [
        [4000019, 100],
        // 蓝蜗牛壳
        [4000000, 100],
        [4000016, 100],
        [_银母矿_, 10],
        [_蛋白石母矿_, 10],
        [_枫叶_, 5]
    ]],
    // Lv2: 熟练 II
    [1112601, "十字旅团熟练戒指 II", [
        // 蘑菇盖
        [4000001, 200],
        // 蓝蘑菇盖
        [4000009, 200],
        // 绿蘑菇盖
        [4000012, 200],
        [_钻石母矿_, 20],
        [_紫矿石母矿_, 20],
        [_黄金母矿_, 20],
        [_枫叶_, 10]
    ]],
    // Lv3: 熟练 III
    [1112602, "十字旅团熟练戒指 III", [
        // 风独眼兽之尾
        [4000013, 100],
        [4000007, 100],
        [4000023, 100],
        [_黄晶母矿_, 20],
        [_蓝宝石母矿_, 20],
        [_钢铁母矿_, 20],
        [_枫叶_, 50]
    ]],
    // Lv4: 老兵 I
    [1112603, "十字旅团老兵戒指 I", [
        [4000022, 100],
        [4000025, 100],
        [4000177, 100],
        [_黑水晶母矿_, 20],
        [_黑暗水晶母矿_, 10],
        [_黄金母矿_, 20],
        [_枫叶_, 100]
    ]],
    // Lv5: 老兵 II
    [1112604, "十字旅团老兵戒指 II", [
        [4000014, 100],
        // 龙皮
        [4000030, 50],
        // 黑龙之角
        [4000186, 100],
        // 火焰羽毛
        [4001006, 5],
        [_黑水晶母矿_, 20],
        [_朱矿石母矿_, 150],
        [_紫矿石母矿_, 150],
        [_枫叶_, 150]
    ]],
    // Lv6: 老兵 III
    [1112605, "十字旅团老兵戒指 III", [
        // 星光精灵
        [4000059, 100],
        [4000060, 100],
        // 日光精灵
        [4000061, 100],
        [_石榴石母矿_, 20],
        [_银母矿_, 20],
        [_海蓝石母矿_, 20],
        [_枫叶_, 200]
    ]],
    // Lv7: 勇士 I
    [1112606, "十字旅团勇士戒指 I", [
        // 小海豹
        [4000154, 100],
        [4000155, 100],
        [4000156, 100],
        // 谢尔夫
        [4032474, 1],
        [_海蓝石母矿_, 20],
        [_黄金母矿_, 20],
        [_蛋白石母矿_, 20],
        [_枫叶_, 300]
    ]],
    // Lv8: 勇士 II
    [1112607, "十字旅团勇士戒指 II", [
        // 火焰人马
        [4000232, 100],
        [4000233, 100],
        [4000234, 100],
        [_力量水晶_, 10],
        [_敏捷水晶_, 10],
        [_智慧水晶_, 10],
        [_幸运水晶_, 10],
        [_枫叶_, 400]
    ]],
    // Lv9: 勇士 III
    [1112608, "十字旅团勇士戒指 III", [
        // 蓝飞龙 红飞龙 黑飞龙
        [4000268, 100],
        [4000269, 100],
        [4000270, 100],
        [_月石_, 1],
        [_星石_, 1],
        [_枫叶_, 500]
    ]],
    // Lv10: 英雄 I
    [1112609, "十字旅团英雄戒指 I", [
        [4000272, 100],
        // 断裂的角
        [4000274, 100],
        // 陈年老骨头
        [4000273, 100],
        [4000235, 1],
        [4000243, 1],
        // 大海兽卡片
        [2388033, 1],
        [_月石_, 3],
        [_星石_, 3],
        [_枫叶_, 600]
    ]],
    // Lv11: 英雄 II
    [1112610, "十字旅团英雄戒指 II", [
        [4000444, 100],
        [4000449, 100],
        [4000454, 100],
        [4000460, 3],
        [_月石_, 5],
        [_星石_, 5],
        [_枫叶_, 700]
    ]],
    // Lv12: 英雄 III
    [1112611, "十字旅团英雄戒指 III", [
        // 绿色高帽
        [4000445, 100],
        [4000450, 100],
        [4000455, 100],
        [4000461, 3],
        [_月石_, 10],
        [_星石_, 10],
        [_枫叶_, 800]
    ]],
    // Lv13: 传说
    [1112612, "十字旅团传说戒指", [
        // 绿色头盔
        [4000447, 100],
        [4000452, 100],
        [4000457, 100],
        [4000462, 3],
        [_月石_, 30],
        [_星石_, 30],
        [_枫叶_, 900]
    ]],
    // Lv14: 降魔（满级）
    [1112613, "十字旅团降魔戒指", [
        // 绿色心脏
        [4000448, 100],
        [4000453, 100],
        [4000458, 100],
        [2388040, 1],
        [2388041, 1],
        [2388042, 1],
        [_月石_, 50],
        [_星石_, 50],
        [_枫叶_, 1000]
    ]]
];

var status = -1;
var currentRingIndex = -1;  // 玩家当前拥有的戒指级别索引（-1表示没有）
var pendingTargetIndex = -1; // 待兑换的目标戒指索引
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";

// ===== 入口 =====

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

// ===== 职业与属性（基于职业群.js） =====

/**
 * 根据职业群ID获取需要增加的两个属性键名
 * @param groupId 职业群ID（来自getJobGroup()，0=新手 1=战士 2=弓箭手 3=法师 4=飞侠 5=海盗）
 * @returns [属性1键名, 属性2键名]
 */
function getStatKeysByGroup(groupId) {
    if (groupId == JOB_GROUP.WARRIOR) return ["str", "dex"]; // 战士: 力量+敏捷
    if (groupId == JOB_GROUP.ARCHER) return ["dex", "str"]; // 弓箭手: 敏捷+力量
    if (groupId == JOB_GROUP.MAGICIAN) return ["int", "luk"]; // 法师: 智力+运气
    if (groupId == JOB_GROUP.THIEF) return ["luk", "dex"]; // 飞侠: 运气+敏捷
    if (groupId == JOB_GROUP.PIRATE) return ["str", "dex"]; // 海盗: 力量+敏捷
    return ["str", "dex"]; // 新手默认战士
}

/**
 * 获取属性键对应的中文名
 */
function getStatName(key) {
    if (key == "str") return "力量";
    if (key == "dex") return "敏捷";
    if (key == "int") return "智力";
    if (key == "luk") return "运气";
    return key;
}

/**
 * 将属性应用到装备上
 * @param equip Equip对象
 * @param statKey 属性键名
 * @param value 属性值
 */
function applyStatToEquip(equip, statKey, value) {
    if (statKey == "str") equip.setStr(value);
    else if (statKey == "dex") equip.setDex(value);
    else if (statKey == "int") equip.setInt(value);
    else if (statKey == "luk") equip.setLuk(value);
}

/**
 * 判断职业群是否为物理职业（非法师即物理）
 * @param groupId 职业群ID
 * @returns true=物理职业  false=法师
 */
function isPhysicalJobGroup(groupId) {
    return groupId == JOB_GROUP.WARRIOR || groupId == JOB_GROUP.ARCHER ||
        groupId == JOB_GROUP.THIEF || groupId == JOB_GROUP.PIRATE ||
        groupId == JOB_GROUP.BEGINNER; // 新手按物理处理
}

/**
 * 根据职业群获取攻击/魔力加成描述
 * @param groupId 职业群ID
 * @returns {label: 显示名, key: "watk"|"matk"}
 */
function getAtkInfo(groupId) {
    if (groupId == JOB_GROUP.MAGICIAN) {
        return {label: "魔力", key: "matk"};
    }
    return {label: "攻击力", key: "watk"};
}

// ===== 戒指查找 =====

/**
 * 检查背包装备栏第一格是否为指定物品
 * @param itemId 物品ID
 * @returns true=第一格是该物品
 */
function hasItemInEquip(itemId) {
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var item = equipInv.getItem(1); // 只取第一格
    return item != null && item.getItemId() == itemId;
}

/**
 * 查找玩家当前拥有的戒指级别索引（扫描背包装备栏）
 * @returns 戒指级别索引（0~14），无则返回-1
 */
function findCurrentRingIndex() {
    for (var i = RING_LEVELS.length - 1; i >= 0; i--) {
        if (hasItemInEquip(RING_LEVELS[i][0])) {
            return i;
        }
    }
    return -1;
}

// ===== 菜单显示 =====

function showMainMenu() {
    currentRingIndex = findCurrentRingIndex();
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var statKeys = getStatKeysByGroup(jobGroup);
    var jobName = getJobGroupName(jobGroup);
    var atkInfo = getAtkInfo(jobGroup);

    var text = "#e十字旅团戒指升级#n\r\n\r\n";
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
        // 没有戒指 → 显示领取初始戒指
        var initRing = RING_LEVELS[0];
        text += "#d当前未拥有十字旅团戒指#k\r\n\r\n";
        text += "#i" + initRing[0] + "# #b" + initRing[1] + "#k\r\n";
        text += "初始属性：#r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n";
        text += "所需材料：\r\n";
        for (var m = 0; m < initRing[2].length; m++) {
            var matId = initRing[2][m][0];
            var matQty = Math.floor(initRing[2][m][1] * RATE);
            text += "  #i" + matId + "# #z" + matId + "# x " + matQty + "\r\n";
        }
        text += "#L0#领取初始戒指#l\r\n";
    } else if (currentRingIndex >= RING_LEVELS.length - 1) {
        // 已满级
        var maxRing = RING_LEVELS[currentRingIndex];
        text += "当前戒指：#i" + maxRing[0] + "# #b" + maxRing[1] + "#k\r\n";
        text += "累计属性加成：\r\n";
        text += "  #r" + getStatName(statKeys[0]) + "+" + (currentRingIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (currentRingIndex * 10) + "#k\r\n";
        text += "  #r" + atkInfo.label + "+" + (currentRingIndex * 5) + "#k\r\n";
        text += "  #r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k（初始）\r\n\r\n";
        text += "#g恭喜！已达成最高级别戒指！#k\r\n";
    } else {
        // 有戒指 → 显示升级选项
        var curRing = RING_LEVELS[currentRingIndex];
        var nextRing = RING_LEVELS[currentRingIndex + 1];
        var nextLvlMats = nextRing[2];
        var nextIndex = currentRingIndex + 1;

        text += "当前戒指：#i" + curRing[0] + "# #b" + curRing[1] + "#k\r\n";
        text += "累计属性加成：\r\n";
        text += "  #r" + getStatName(statKeys[0]) + "+" + (currentRingIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (currentRingIndex * 10) + "#k\r\n";
        if (currentRingIndex > 0) {
            text += "  #r" + atkInfo.label + "+" + (currentRingIndex * 5) + "#k\r\n";
        }
        text += "  #r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k（初始）\r\n\r\n";
        text += "━━━ 可升级至 ━━━\r\n\r\n";
        text += "#i" + nextRing[0] + "# #b" + nextRing[1] + "#k\r\n";
        text += "升级后属性加成：\r\n";
        text += "  #r" + getStatName(statKeys[0]) + "+" + (nextIndex * 10) + " / " + getStatName(statKeys[1]) + "+" + (nextIndex * 10) + "#k\r\n";
        text += "  #r" + atkInfo.label + "+" + (nextIndex * 5) + "#k\r\n";
        var goldCost = Math.floor(1000000 * nextIndex * RATE); // 100W * 进阶次数 * 倍率
        text += "升级费用：#r" + (goldCost / 10000) + "W金币#k\r\n";
        text += "所需材料：\r\n";
        // 上一级戒指
        text += "  #i" + curRing[0] + "# #z" + curRing[0] + "# x 1\r\n";
        // 其他材料（应用倍率）
        for (var n = 0; n < nextLvlMats.length; n++) {
            var matId2 = nextLvlMats[n][0];
            var matQty2 = Math.floor(nextLvlMats[n][1] * RATE);
            text += "  #i" + matId2 + "# #z" + matId2 + "# x " + matQty2 + "\r\n";
        }
        text += "#L0#升级戒指#l\r\n";
    }

    text += "\r\n#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

// ===== 选择处理 =====

function handleSelection(selection) {
    if (selection == 99) {
        // 返回戒指中心
        cm.dispose();
        cm.openNpc(9900001, "xy/装备系统/v002/戒指中心");
        return;
    }

    // 检查是否可以操作
    if (currentRingIndex < 0) {
        // 领取初始戒指
        handleClaimInitial();
    } else if (currentRingIndex >= RING_LEVELS.length - 1) {
        // 已满级，不应该有选项
        cm.sendOk("已满级！");
        cm.dispose();
    } else {
        // 升级
        handleUpgrade();
    }
}

/**
 * 处理领取初始戒指
 */
function handleClaimInitial() {
    var initRing = RING_LEVELS[0];
    var ringId = initRing[0];
    var ringName = initRing[1];
    var materials = initRing[2];

    // 二次确认：是否已有该戒指
    if (hasItemInEquip(ringId)) {
        cm.sendOk("你已经拥有 #i" + ringId + "# #b" + ringName + "#k，不可重复领取！");
        cm.dispose();
        return;
    }

    // 检查材料
    for (var m = 0; m < materials.length; m++) {
        var matId = materials[m][0];
        var matQty = Math.floor(materials[m][1] * RATE);
        if (!cm.haveItem(matId, matQty)) {
            cm.sendOk("#r材料不足！#k\r\n需要 #i" + matId + "# #b#z" + matId + "##k x #r" + matQty + "#k\r\n请收集材料后再来领取。");
            cm.dispose();
            return;
        }
    }

    // 检查背包空间
    if (!cm.canHold(ringId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来领取！#k");
        cm.dispose();
        return;
    }

    pendingTargetIndex = 0;
    cm.sendYesNo("确认领取 #i" + ringId + "# #b" + ringName + "#k？\r\n\r\n初始属性：#r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n#d该戒指为固有道具，不可交换。#k");
}

/**
 * 处理升级戒指
 */
function handleUpgrade() {
    var nextIndex = currentRingIndex + 1;
    var nextRing = RING_LEVELS[nextIndex];
    var nextRingId = nextRing[0];
    var nextRingName = nextRing[1];
    var materials = nextRing[2];
    var curRing = RING_LEVELS[currentRingIndex];
    var curRingId = curRing[0];

    // 检查是否拥有当前戒指
    if (!hasItemInEquip(curRingId)) {
        cm.sendOk("#r异常：未找到当前戒指，请重试。#k");
        cm.dispose();
        return;
    }

    // 检查材料（包括上一级戒指）
    // 上一级戒指
    if (!hasItemInEquip(curRingId)) {
        cm.sendOk("#r需要上一级戒指，但未找到！#k");
        cm.dispose();
        return;
    }

    for (var m = 0; m < materials.length; m++) {
        var matId = materials[m][0];
        var matQty = Math.floor(materials[m][1] * RATE);
        if (!cm.haveItem(matId, matQty)) {
            cm.sendOk("#r材料不足！#k\r\n需要 #i" + matId + "# #b#z" + matId + "##k x #r" + matQty + "#k\r\n请收集材料后再来升级。");
            cm.dispose();
            return;
        }
    }

    // 检查背包空间（需确认扣除当前戒指后空间是否足够）
    if (!cm.canHold(nextRingId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来升级！#k");
        cm.dispose();
        return;
    }

    pendingTargetIndex = nextIndex;

    var goldCost = Math.floor(1000000 * nextIndex * RATE); // 100W * 进阶次数 * 倍率
    // 检查金币
    if (cm.getMeso() < goldCost) {
        cm.sendOk("#r金币不足！#k\r\n需要 #b" + (goldCost / 10000) + "W#k 金币，当前只有 #r" + Math.floor(cm.getMeso() / 10000) + "W#k 金币。");
        cm.dispose();
        return;
    }

    var confirmText = "确认升级戒指？\r\n\r\n";
    confirmText += "#i" + curRingId + "# #b" + curRing[1] + "#k\r\n";
    confirmText += "  → #i" + nextRingId + "# #b" + nextRingName + "#k\r\n\r\n";
    confirmText += "升级费用：#r" + (goldCost / 10000) + "W金币#k\r\n";
    confirmText += "将消耗以下材料：\r\n";
    confirmText += "  #i" + curRingId + "# #z" + curRingId + "# x 1\r\n";
    for (var n = 0; n < materials.length; n++) {
        var matId2 = materials[n][0];
        var matQty2 = Math.floor(materials[n][1] * RATE);
        confirmText += "  #i" + matId2 + "# #z" + matId2 + "# x " + matQty2 + "\r\n";
    }

    cm.sendYesNo(confirmText);
}

// ===== 执行兑换 =====

function doExchange() {
    if (pendingTargetIndex < 0 || pendingTargetIndex >= RING_LEVELS.length) {
        cm.sendOk("兑换数据异常，请重试。");
        cm.dispose();
        return;
    }

    var targetRing = RING_LEVELS[pendingTargetIndex];
    var targetRingId = targetRing[0];
    var targetRingName = targetRing[1];
    var materials = targetRing[2];
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var statKeys = getStatKeysByGroup(jobGroup);

    if (pendingTargetIndex == 0) {
        // === 领取初始戒指 ===
        // 再次检查是否已有
        if (hasItemInEquip(targetRingId)) {
            cm.sendOk("你已经拥有 #i" + targetRingId + "# #b" + targetRingName + "#k，不可重复领取！");
            cm.dispose();
            return;
        }

        // 检查并扣除材料
        for (var m = 0; m < materials.length; m++) {
            var matId = materials[m][0];
            var matQty = Math.floor(materials[m][1] * RATE);
            if (!cm.haveItem(matId, matQty)) {
                cm.sendOk("#r材料不足！#k");
                cm.dispose();
                return;
            }
        }

        // 扣除材料（应用倍率）
        for (var m2 = 0; m2 < materials.length; m2++) {
            cm.gainItem(materials[m2][0], -Math.floor(materials[m2][1] * RATE));
        }

        // 发放戒指
        cm.gainItem(targetRingId, 1);

        // 初始戒指设置四维+5
        var equipInv0 = cm.getPlayer().getInventory(InventoryType.EQUIP);
        var initEquip = equipInv0.findById(targetRingId);
        if (initEquip != null) {
            initEquip.setStr(5);
            initEquip.setDex(5);
            initEquip.setInt(5);
            initEquip.setLuk(5);
            // 强制推送属性更新到客户端，覆盖WZ自带属性
            cm.getPlayer().forceUpdateItem(initEquip);
        }

        cm.sendOk("领取成功！\r\n\r\n#i" + targetRingId + "# #b" + targetRingName + "#k 已放入背包。\r\n属性：#r力量+5 / 敏捷+5 / 智力+5 / 运气+5#k\r\n#d该戒指为固有道具，不可交换。#k\r\n#g继续收集材料来升级吧！#k");
        cm.dispose();

    } else {
        // === 升级戒指 ===
        var prevRingId = RING_LEVELS[pendingTargetIndex - 1][0];

        // 检查上一级戒指
        if (!hasItemInEquip(prevRingId)) {
            cm.sendOk("#r异常：未找到上一级戒指，请重试。#k");
            cm.dispose();
            return;
        }

        // 检查材料
        for (var m3 = 0; m3 < materials.length; m3++) {
            var matId3 = materials[m3][0];
            var matQty3 = Math.floor(materials[m3][1] * RATE);
            if (!cm.haveItem(matId3, matQty3)) {
                cm.sendOk("#r材料不足！#k");
                cm.dispose();
                return;
            }
        }

        // 检查背包空间
        if (!cm.canHold(targetRingId, 1)) {
            cm.sendOk("#r背包空间不足！#k");
            cm.dispose();
            return;
        }

        // 扣除金币（100W * 进阶次数 * 倍率）
        cm.gainMeso(-Math.floor(1000000 * pendingTargetIndex * RATE));

        // 扣除上一级戒指
        cm.gainItem(prevRingId, -1);

        // 扣除材料（应用倍率）
        for (var m4 = 0; m4 < materials.length; m4++) {
            cm.gainItem(materials[m4][0], -Math.floor(materials[m4][1] * RATE));
        }

        // 发放新戒指
        cm.gainItem(targetRingId, 1);

        // 设置新戒指的属性
        var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
        var newEquip = equipInv.findById(targetRingId);
        if (newEquip != null) {
            // 计算属性：基础四维5 + 职业双维10/级（叠加而非覆盖）
            var statValue = pendingTargetIndex * 10;
            var isFirstStat = (statKeys[0] == "str" || statKeys[1] == "str");
            var isSecondStat = (statKeys[0] == "dex" || statKeys[1] == "dex");
            var isThirdStat = (statKeys[0] == "int" || statKeys[1] == "int");
            var isFourthStat = (statKeys[0] == "luk" || statKeys[1] == "luk");
            newEquip.setStr(5 + (isFirstStat ? statValue : 0));
            newEquip.setDex(5 + (isSecondStat ? statValue : 0));
            newEquip.setInt(5 + (isThirdStat ? statValue : 0));
            newEquip.setLuk(5 + (isFourthStat ? statValue : 0));
            // 物理职业+攻击力/法师+魔力  每级+5
            var atkValue = pendingTargetIndex * 5;
            if (isPhysicalJobGroup(jobGroup)) {
                newEquip.setWatk(atkValue);
            } else {
                newEquip.setMatk(atkValue);
            }
            // 强制推送属性更新到客户端，覆盖WZ自带属性
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
            successText += "#g继续收集材料来升级吧！#k";
        } else {
            successText += "#g恭喜！已达成最高级别戒指！#k";
        }

        cm.sendOk(successText);
        cm.dispose();
    }
}
