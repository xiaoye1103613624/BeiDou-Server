/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：套服进阶系统
 *   1. 入口两个选项：① 购买初始装备(1052165，100W金币，固有道具不可交换)  ② 套服进阶
 *   2. 套服为单一进阶链：Lv0(1052165) → Lv1(1052166) → ... → Lv13(1052669)，共14级
 *   3. 每次进阶在【待进阶装备当前属性】基础上，叠加所选方向对应的属性增量
 *   4. 初始装备(Lv0)固定属性：四维(力量/敏捷/智力/运气)各+10，血量+100，魔法值+100，物理防御+5，魔法防御+5
 *   5. 每次进阶提供3种方向供选择：① 伤害提升 ② 均衡提升 ③ 血量提升
 *   6. 伤害提升：二维(按职业)各+20，血量+100，攻击力/魔力+15
 *      均衡提升：二维(按职业)各+15，血量+300，攻击力/魔力+10
 *      血量提升：二维(按职业)各+10，血量+600，攻击力/魔力+8
 *   7. 进阶材料：第N次进阶消耗 4002000/4002001/4002002/4002003 各 (20N-10)*倍率，金币 (200N-100)万*倍率
 *      即第1次10/10/10/10+100W，第2次30/30/30/30+300W，第3次50/50/50/50+500W，第4次70/70/70/70+700W，按等差递增
 * ==================
 */

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

/**
 * 根据职业群获取主二维属性键
 * 战士/海盗→[str,dex] 弓箭手→[dex,str] 法师→[int,luk] 飞侠→[luk,dex] 新手默认战士
 */
function getStatKeysByGroup(groupId) {
    switch (groupId) {
        case JOB_GROUP.ARCHER:
            return ["dex", "str"];
        case JOB_GROUP.MAGICIAN:
            return ["int", "luk"];
        case JOB_GROUP.THIEF:
            return ["luk", "dex"];
        case JOB_GROUP.PIRATE:
            return ["str", "dex"];
        case JOB_GROUP.WARRIOR:
        default:
            return ["str", "dex"];
    }
}

/**
 * 判断职业群是物理系（用攻击力watk）还是魔法系（用魔力matk）
 */
function isPhysicalJobGroup(groupId) {
    return groupId !== JOB_GROUP.MAGICIAN;
}

// ===== 倍率配置（默认1，修改此处可整体调整材料/金币消耗） =====
var RATE = 1;

// ===== Java类型导入 =====
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

// ===== 初始装备购买价格（金币） =====
var INIT_PRICE = 1000000;

// ===== 初始装备(Lv0)固定属性 =====
var INIT_STATS = {str: 10, dex: 10, int: 10, luk: 10, hp: 100, mp: 100, pad: 5, mad: 5, watk: 0, matk: 0};

// ===== 进阶材料ID（固定4种） =====
var UPGRADE_MATERIAL_IDS = [4002000, 4002001, 4002002, 4002003];

/**
 * 第N次进阶（N从1开始）所需材料与金币
 * 材料数量 = (20N-10) * RATE；金币 = (200N-100)万 * RATE
 */
function getStepCost(step) {
    var qty = Math.floor((20 * step - 10) * RATE);
    var meso = Math.floor((200 * step - 100) * 10000 * RATE);
    var materials = [];
    for (var i = 0; i < UPGRADE_MATERIAL_IDS.length; i++) {
        materials.push([UPGRADE_MATERIAL_IDS[i], qty]);
    }
    return {materials: materials, meso: meso};
}

// ===== 3种进阶方向 =====
var UPGRADE_CHOICE = {
    DAMAGE: 1, // 伤害提升
    BALANCE: 2, // 均衡提升
    HP: 3  // 血量提升
};

var CHOICE_CONFIG = {};
CHOICE_CONFIG[UPGRADE_CHOICE.DAMAGE] = {name: "伤害提升", mainStat: 20, hp: 100, atk: 15};
CHOICE_CONFIG[UPGRADE_CHOICE.BALANCE] = {name: "均衡提升", mainStat: 15, hp: 300, atk: 10};
CHOICE_CONFIG[UPGRADE_CHOICE.HP] = {name: "血量提升", mainStat: 10, hp: 600, atk: 8};

/**
 * 根据所选方向 + 职业群，生成本次进阶的属性增量对象
 */
function getIncrementByChoice(choice, jobGroup) {
    var cfg = CHOICE_CONFIG[choice];
    var keys = getStatKeysByGroup(jobGroup);
    var physical = isPhysicalJobGroup(jobGroup);
    var inc = {str: 0, dex: 0, int: 0, luk: 0, hp: cfg.hp, mp: 0, pad: 0, mad: 0, watk: 0, matk: 0};
    inc[keys[0]] += cfg.mainStat;
    inc[keys[1]] += cfg.mainStat;
    if (physical) {
        inc.watk += cfg.atk;
    } else {
        inc.matk += cfg.atk;
    }
    return inc;
}

// ===== 套服进阶链配置 =====
// 格式：[套装名称, 物品ID或职业映射]
//   单职业：数字itemId，全职业通用
//   多职业：{1:战士ID, 2:弓箭手ID, 3:法师ID, 4:飞侠ID, 5:海盗ID}
var SUIT_LEVELS = [
    ["降落伞工作人员套装", 1052165],                                                                          // Lv0
    ["无敌工作人员套装", 1052166],                                                                          // Lv1
    ["冒险岛铂金外套", 1052358],                                                                          // Lv2
    ["紫金枫叶套装", 1052457],                                                                          // Lv3
    ["传说冒险岛套服", 1052405],                                                                          // Lv4
    ["专属紫金枫叶套装", 1052461],                                                                          // Lv5
    ["10周年黑色套装", 1052612],                                                                          // Lv6
    ["11周年神圣套装", 1052758],                                                                          // Lv7
    ["风暴连帽长袍", 1052467],                                                                          // Lv8
    ["终极连身衣", 1052569],                                                                          // Lv9
    ["超越套装", 1053193],                                                                          // Lv10
    ["冒险岛寻宝套装", 1052929],                                                                          // Lv11
    ["皇家班·雷昂套服", {1: 1052804, 2: 1052806, 3: 1052805, 4: 1052807, 5: 1052808}], // Lv12 战/弓/法/飞/海
    ["血色锁子甲系列", {1: 1052319, 2: 1052321, 3: 1052320, 4: 1052322, 5: 1052323}], // Lv13 阿加雷斯/伊布斯/艾里格斯/赫尔巴斯/维帕尔
    ["芬撒里尔套服", {1: 1052799, 2: 1052801, 3: 1052800, 4: 1052802, 5: 1052803}], // Lv14
    ["天照铠甲系列", {1: 1052509, 2: 1052511, 3: 1052510, 4: 1052512, 5: 1052513}], // Lv15 天照/天钿女命/大山祇神/月夜见尊/素盏呜尊
    ["埃苏莱布斯套服", {1: 1052882, 2: 1052888, 3: 1052887, 4: 1052889, 5: 1052890}], // Lv16
    ["凯梅尔兹外套", 1052673],                                                                          // Lv17
    ["神秘之影套服", {1: 1053063, 2: 1053065, 3: 1053064, 4: 1053066, 5: 1053067}]  // Lv18
];

/**
 * 获取指定等级套装的物品ID（根据职业自动选择）
 * @param levelIndex 套装等级索引
 * @param jobGroup 职业群ID
 * @returns 物品ID
 */
function getSuitItemId(levelIndex, jobGroup) {
    var items = SUIT_LEVELS[levelIndex][1];
    if (typeof items === 'number') return items;
    return items[jobGroup] || items[JOB_GROUP.WARRIOR]; // 找不到时回退战士
}

/**
 * 获取指定等级套装的显示名称
 */
function getSuitName(levelIndex) {
    return SUIT_LEVELS[levelIndex][0];
}

var status = -1;
var currentSuitIndex = -1;   // 玩家当前拥有的套装级别索引（-1表示没有）
var pendingTargetIndex = -1; // 待兑换的目标套装索引
var pendingChoice = -1;      // 待兑换的进阶方向
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";

// ===== 入口 =====

function start() {
    status = -1;
    currentSuitIndex = -1;
    pendingTargetIndex = -1;
    pendingChoice = -1;
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
        showEntryMenu();
    } else if (status == 1) {
        handleEntrySelection(selection);
    } else if (status == 2) {
        showSuitMenu();
    } else if (status == 3) {
        handleSuitMenuSelection(selection);
    } else if (status == 4) {
        handleChoiceSelection(selection);
    } else if (status == 5) {
        handleConfirmMenuSelection(selection);
    } else if (status == 6) {
        if (type == 1) {
            doExchange();
        } else {
            cm.sendOk("已取消。");
            cm.dispose();
        }
    }
}

// ===== 工具方法 =====

/**
 * 查找玩家当前拥有的套装级别索引
 * @returns 套装级别索引（0~15），无则返回-1
 */
/**
 * 检查背包装备栏第一格物品ID
 */
function getEquipSlot1ItemId() {
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var item = equipInv.getItem(1); // 只取第一格
    return item != null ? item.getItemId() : -1;
}

function findCurrentSuitIndex() {
    var slot1Id = getEquipSlot1ItemId();
    for (var i = SUIT_LEVELS.length - 1; i >= 0; i--) {
        var items = SUIT_LEVELS[i][1];
        if (typeof items === 'number') {
            if (slot1Id === items) return i;
        } else {
            // 多职业等级：匹配任一职业变体
            for (var jg in items) {
                if (slot1Id === items[jg]) return i;
            }
        }
    }
    return -1;
}

/**
 * 将属性累加到装备上（在原有属性基础上叠加增量）
 */
/**
 * 将属性增量叠加到装备上（仅涉及计算的属性：四维/攻击力/魔力/HP/MP）
 * 不涉及计算的属性（防御/命中/回避等）不触碰，保留新装备WZ默认值
 */
function applyIncrement(equip, inc) {
    if (inc.str) equip.setStr((equip.getStr() + inc.str) & 0x7FFF);
    if (inc.dex) equip.setDex((equip.getDex() + inc.dex) & 0x7FFF);
    if (inc.int) equip.setInt((equip.getInt() + inc.int) & 0x7FFF);
    if (inc.luk) equip.setLuk((equip.getLuk() + inc.luk) & 0x7FFF);
    if (inc.hp) equip.setHp((equip.getHp() + inc.hp) & 0x7FFF);
    if (inc.mp) equip.setMp((equip.getMp() + inc.mp) & 0x7FFF);
    if (inc.watk) equip.setWatk((equip.getWatk() + inc.watk) & 0x7FFF);
    if (inc.matk) equip.setMatk((equip.getMatk() + inc.matk) & 0x7FFF);
}

/**
 * 拼接属性增量的展示文本（非0字段才显示）
 */
function describeIncrement(inc) {
    var parts = [];
    if (inc.str) parts.push("力量+" + inc.str);
    if (inc.dex) parts.push("敏捷+" + inc.dex);
    if (inc.int) parts.push("智力+" + inc.int);
    if (inc.luk) parts.push("运气+" + inc.luk);
    if (inc.hp) parts.push("血量+" + inc.hp);
    if (inc.mp) parts.push("蓝量+" + inc.mp);
    if (inc.pad) parts.push("物理防御+" + inc.pad);
    if (inc.mad) parts.push("魔法防御+" + inc.mad);
    if (inc.watk) parts.push("攻击力+" + inc.watk);
    if (inc.matk) parts.push("魔力+" + inc.matk);
    return parts.length > 0 ? parts.join(" / ") : "无";
}

/**
 * 拼接材料/金币消耗的展示文本
 */
function describeCost(cost) {
    var text = "金币 x #r" + cost.meso + "#k\r\n";
    for (var i = 0; i < cost.materials.length; i++) {
        var matId = cost.materials[i][0];
        var matQty = cost.materials[i][1];
        text += "#i" + matId + "# #z" + matId + "# x " + matQty + "\r\n";
    }
    return text;
}

// ===== 入口菜单：初始装备购买 / 套服进阶 =====

function showEntryMenu() {
    var text = "#e#d套服进阶中心#n#k\r\n\r\n";
    text += "请选择服务：\r\n\r\n";
    var initId = getSuitItemId(0, JOB_GROUP.WARRIOR); // 初始装备各职业相同
    var initName = getSuitName(0);
    text += "#L0##b购买初始装备#k — #i" + initId + "# #b" + initName + "#k（" + INIT_PRICE + "金币）#l\r\n";
    text += "#L1##b套服进阶#k — 逐级进阶升级套装属性#l\r\n";
    cm.sendSimple(text);
}

function handleEntrySelection(selection) {
    if (selection == 0) {
        handleBuyInitial();
    } else if (selection == 1) {
        status = 2; // 跳过status==2的常规流转，直接渲染套服进阶菜单，使下一次点击命中status==3
        showSuitMenu();
    } else {
        cm.dispose();
    }
}

/**
 * 购买初始装备（金币购买，固有道具不可交换）
 */
function handleBuyInitial() {
    var suitName = getSuitName(0);
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var suitId = getSuitItemId(0, jobGroup);

    if (getEquipSlot1ItemId() === suitId) {
        cm.sendOk("你已经拥有 #i" + suitId + "# #b" + suitName + "#k，不可重复购买！");
        cm.dispose();
        return;
    }

    if (cm.getMeso() < INIT_PRICE) {
        cm.sendOk("#r金币不足！#k\r\n购买需要 #r" + INIT_PRICE + "#k 金币。");
        cm.dispose();
        return;
    }

    if (!cm.canHold(suitId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来购买！#k");
        cm.dispose();
        return;
    }

    cm.gainMeso(-INIT_PRICE);
    cm.gainItem(suitId, 1);

    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var initEquip = equipInv.findById(suitId);
    if (initEquip != null) {
        initEquip.setStr(INIT_STATS.str);
        initEquip.setDex(INIT_STATS.dex);
        initEquip.setInt(INIT_STATS.int);
        initEquip.setLuk(INIT_STATS.luk);
        initEquip.setHp(INIT_STATS.hp);
        initEquip.setMp(INIT_STATS.mp);
        initEquip.setWdef(INIT_STATS.pad);
        initEquip.setMdef(INIT_STATS.mad);
        cm.getPlayer().forceUpdateItem(initEquip);
    }

    cm.sendOk("购买成功！\r\n\r\n#i" + suitId + "# #b" + suitName + "#k 已放入背包。\r\n属性：#r力量+10 / 敏捷+10 / 智力+10 / 运气+10 / 血量+100 / 蓝量+100 / 物理防御+5 / 魔法防御+5#k\r\n#d该套装为固有道具，不可交换。#k\r\n#g前往【套服进阶】继续提升属性吧！#k");
    cm.dispose();
}

// ===== 套服进阶菜单 =====

function showSuitMenu() {
    currentSuitIndex = findCurrentSuitIndex();
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var jobName = getJobGroupName(jobGroup);

    var text = "#e套服进阶#n\r\n\r\n";
    text += "你的职业：#b" + jobName + "#k\r\n";
    text += "━━━━━━━━━━━━━━━━━━\r\n\r\n";

    if (currentSuitIndex < 0) {
        text += "#d你尚未拥有套装，请先购买初始装备！#k\r\n\r\n";
        text += "\r\n#L99#" + 返回图标 + "#l\r\n";
        cm.sendSimple(text);
        return;
    }

    if (currentSuitIndex >= SUIT_LEVELS.length - 1) {
        var maxId = getSuitItemId(currentSuitIndex, jobGroup);
        var maxName = getSuitName(currentSuitIndex);
        text += "当前套装：#i" + maxId + "# #b" + maxName + "#k\r\n\r\n";
        text += "#g恭喜！已达成最高级别套装！#k\r\n";
        text += "\r\n#L99#" + 返回图标 + "#l\r\n";
        cm.sendSimple(text);
        return;
    }

    var curId = getSuitItemId(currentSuitIndex, jobGroup);
    var curName = getSuitName(currentSuitIndex);
    var nextId = getSuitItemId(currentSuitIndex + 1, jobGroup);
    var nextName = getSuitName(currentSuitIndex + 1);
    var step = currentSuitIndex + 1;
    var cost = getStepCost(step);

    text += "当前套装：#i" + curId + "# #b" + curName + "#k\r\n\r\n";
    text += "━━━ 可进阶至 ━━━\r\n\r\n";
    text += "#i" + nextId + "# #b" + nextName + "#k\r\n\r\n";
    text += "本次进阶消耗：\r\n" + describeCost(cost) + "\r\n";
    text += "#L0##b进阶套装#l\r\n";

    text += "\r\n#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

function handleSuitMenuSelection(selection) {
    if (selection != 0) {
        cm.dispose();
        return;
    }

    if (currentSuitIndex < 0 || currentSuitIndex >= SUIT_LEVELS.length - 1) {
        cm.dispose();
        return;
    }

    showChoiceMenu();
}

/**
 * 展示3种进阶方向供选择
 */
function showChoiceMenu() {
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);

    var text = "#e请选择进阶方向#n\r\n\r\n";

    var damageInc = getIncrementByChoice(UPGRADE_CHOICE.DAMAGE, jobGroup);
    var balanceInc = getIncrementByChoice(UPGRADE_CHOICE.BALANCE, jobGroup);
    var hpInc = getIncrementByChoice(UPGRADE_CHOICE.HP, jobGroup);

    text += "#L0##b① 伤害提升#k#l\r\n\r\n";
    text += describeIncrement(damageInc) + "\r\n\r\n";
    text += "#L1##b② 均衡提升#k#l\r\n\r\n";
    text += describeIncrement(balanceInc) + "\r\n\r\n";
    text += "#L2##b③ 血量提升#k#l\r\n\r\n";
    text += describeIncrement(hpInc) + "\r\n\r\n";

    text += "#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

/**
 * 处理方向单选：仅记录所选方向，不立即执行，转入【材料确认+进阶套装】界面
 */
function handleChoiceSelection(selection) {
    var choice = -1;
    if (selection == 0) {
        choice = UPGRADE_CHOICE.DAMAGE;
    } else if (selection == 1) {
        choice = UPGRADE_CHOICE.BALANCE;
    } else if (selection == 2) {
        choice = UPGRADE_CHOICE.HP;
    } else {
        cm.dispose();
        return;
    }

    pendingChoice = choice;
    showConfirmMenu();
}

/**
 * 展示已选方向的属性增量与所需材料，提供"进阶套装"按钮进行最终确认
 */
function showConfirmMenu() {
    var nextIndex = currentSuitIndex + 1;
    var step = nextIndex;
    var cost = getStepCost(step);
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var inc = getIncrementByChoice(pendingChoice, jobGroup);
    var curId = getSuitItemId(currentSuitIndex, jobGroup);
    var curName = getSuitName(currentSuitIndex);
    var nextId = getSuitItemId(nextIndex, jobGroup);
    var nextName = getSuitName(nextIndex);

    var text = "#e已选方向：#b" + CHOICE_CONFIG[pendingChoice].name + "#k#n\r\n\r\n";
    text += "#i" + curId + "# #b" + curName + "#k  →  #i" + nextId + "# #b" + nextName + "#k\r\n\r\n";
    text += "属性增量：#r" + describeIncrement(inc) + "#k\r\n\r\n";
    text += "需消耗材料：\r\n";
    text += "  #i" + curId + "# #z" + curId + "# x 1\r\n";
    text += describeCost(cost);
    text += "\r\n#L0##b进阶套装#l\r\n";

    text += "\r\n#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

function handleConfirmMenuSelection(selection) {
    if (selection != 0) {
        cm.dispose();
        return;
    }
    handleUpgrade();
}

/**
 * 校验材料/金币并弹出最终确认框（确认后执行doExchange）
 */
function handleUpgrade() {
    var nextIndex = currentSuitIndex + 1;
    var step = nextIndex;
    var cost = getStepCost(step);
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var curSuitId = getSuitItemId(currentSuitIndex, jobGroup);
    var curSuitName = getSuitName(currentSuitIndex);
    var nextSuitId = getSuitItemId(nextIndex, jobGroup);
    var nextSuitName = getSuitName(nextIndex);

    if (getEquipSlot1ItemId() !== curSuitId) {
        cm.sendOk("#r异常：未找到当前套装，请重试。#k");
        cm.dispose();
        return;
    }

    if (cm.getMeso() < cost.meso) {
        cm.sendOk("#r金币不足！#k\r\n进阶需要 #r" + cost.meso + "#k 金币。");
        cm.dispose();
        return;
    }

    for (var m = 0; m < cost.materials.length; m++) {
        var matId = cost.materials[m][0];
        var matQty = cost.materials[m][1];
        if (!cm.haveItem(matId, matQty)) {
            cm.sendOk("#r材料不足！#k\r\n需要 #i" + matId + "# #b#z" + matId + "##k x #r" + matQty + "#k\r\n请收集材料后再来进阶。");
            cm.dispose();
            return;
        }
    }

    if (!cm.canHold(nextSuitId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来进阶！#k");
        cm.dispose();
        return;
    }

    pendingTargetIndex = nextIndex;

    var inc = getIncrementByChoice(pendingChoice, jobGroup);

    var confirmText = "确认进阶套装？\r\n\r\n";
    confirmText += "#i" + curSuitId + "# #b" + curSuitName + "#k\r\n";
    confirmText += "  → #i" + nextSuitId + "# #b" + nextSuitName + "#k\r\n\r\n";
    confirmText += "方向：#b" + CHOICE_CONFIG[pendingChoice].name + "#k\r\n";
    confirmText += "属性增量：#r" + describeIncrement(inc) + "#k\r\n\r\n";
    confirmText += "将消耗：\r\n";
    confirmText += "  #i" + curSuitId + "# #z" + curSuitId + "# x 1\r\n";
    confirmText += describeCost(cost);

    cm.sendYesNo(confirmText);
}

// ===== 执行进阶 =====

function doExchange() {
    if (pendingTargetIndex <= 0 || pendingTargetIndex >= SUIT_LEVELS.length || pendingChoice <= 0) {
        cm.sendOk("进阶数据异常，请重试。");
        cm.dispose();
        return;
    }

    var step = pendingTargetIndex;
    var cost = getStepCost(step);
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var inc = getIncrementByChoice(pendingChoice, jobGroup);
    var targetSuitId = getSuitItemId(pendingTargetIndex, jobGroup);
    var targetSuitName = getSuitName(pendingTargetIndex);
    var prevSuitId = getSuitItemId(pendingTargetIndex - 1, jobGroup);

    if (getEquipSlot1ItemId() !== prevSuitId) {
        cm.sendOk("#r异常：未找到上一级套装，请重试。#k");
        cm.dispose();
        return;
    }

    if (cm.getMeso() < cost.meso) {
        cm.sendOk("#r金币不足！#k");
        cm.dispose();
        return;
    }

    for (var m3 = 0; m3 < cost.materials.length; m3++) {
        var matId3 = cost.materials[m3][0];
        var matQty3 = cost.materials[m3][1];
        if (!cm.haveItem(matId3, matQty3)) {
            cm.sendOk("#r材料不足！#k");
            cm.dispose();
            return;
        }
    }

    if (!cm.canHold(targetSuitId, 1)) {
        cm.sendOk("#r背包空间不足！#k");
        cm.dispose();
        return;
    }

    // 读取上一级套装当前属性（仅四维/攻击力/魔力/HP/MP参与计算，叠加后覆盖新装备；
    // 其余属性如物理/魔法防御不参与计算，保留新装备自身的默认值）
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var prevEquip = equipInv.findById(prevSuitId);
    var carryStats = {
        str: prevEquip != null ? prevEquip.getStr() : 0,
        dex: prevEquip != null ? prevEquip.getDex() : 0,
        int: prevEquip != null ? prevEquip.getInt() : 0,
        luk: prevEquip != null ? prevEquip.getLuk() : 0,
        hp: prevEquip != null ? prevEquip.getHp() : 0,
        mp: prevEquip != null ? prevEquip.getMp() : 0,
        watk: prevEquip != null ? prevEquip.getWatk() : 0,
        matk: prevEquip != null ? prevEquip.getMatk() : 0
    };

    // 扣除金币
    cm.gainMeso(-cost.meso);

    // 扣除上一级套装
    cm.gainItem(prevSuitId, -1);

    // 扣除材料
    for (var m4 = 0; m4 < cost.materials.length; m4++) {
        cm.gainItem(cost.materials[m4][0], -cost.materials[m4][1]);
    }

    // 发放新套装
    cm.gainItem(targetSuitId, 1);

    // 在上一级套装属性基础上叠加本次所选方向的增量
    var newEquip = equipInv.findById(targetSuitId);
    if (newEquip != null) {
        newEquip.setStr(carryStats.str);
        newEquip.setDex(carryStats.dex);
        newEquip.setInt(carryStats.int);
        newEquip.setLuk(carryStats.luk);
        newEquip.setHp(carryStats.hp);
        newEquip.setMp(carryStats.mp);
        newEquip.setWatk(carryStats.watk);
        newEquip.setMatk(carryStats.matk);
        applyIncrement(newEquip, inc);
        cm.getPlayer().forceUpdateItem(newEquip);
    }

    var successText = "进阶成功！\r\n\r\n";
    successText += "#i" + targetSuitId + "# #b" + targetSuitName + "#k 已放入背包。\r\n";
    successText += "方向：#b" + CHOICE_CONFIG[pendingChoice].name + "#k\r\n";
    successText += "本次属性增量：#r" + describeIncrement(inc) + "#k\r\n";
    successText += "#d该套装为固有道具，不可交换。#k\r\n";
    if (pendingTargetIndex < SUIT_LEVELS.length - 1) {
        successText += "#g继续收集材料来进阶吧！#k";
    } else {
        successText += "#g恭喜！已达成最高级别套装！#k";
    }

    cm.sendOk(successText);
    cm.dispose();
}
