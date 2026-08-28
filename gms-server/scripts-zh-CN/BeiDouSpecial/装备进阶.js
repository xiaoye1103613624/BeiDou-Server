/*
 * ==================
 * 脚本类型: 装备进阶
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 按职业群（战士/弓箭手/法师/飞侠/海盗）自动匹配进阶路线
 *   2. 每阶消耗多种材料（可配置数量）、金币、点卷、抵用券
 *   3. 进阶后获得下一阶段装备，属性与上一阶段所有属性叠加
 *   4. 剩余强化次数保留上一阶段的
 *   5. 属性通过 EquipAdvanceManager.safeAddStat() 安全叠加，
 *      自动钳制到 short 范围 [0, 32767]，防止溢出导致客户端闪退
 *   6. 管理员通过Web后台维护进阶配置
 * ==================
 */

var EquipAdvanceManager = Java.type('org.gms.config.EquipAdvanceManager');
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');

var selectedRoute = null;       // EquipAdvanceRouteDO
var selectedEquip = null;       // 玩家背包中的 Equip 对象
var selectedStage = null;       // 当前所处的阶段
var selectedSlot = -1;
var selectedInvType = null;     // InventoryType

var _stageMap = [];             // 临时存储阶段信息

// ===== 数据持久化 =====

var advanceData = {};

function loadAdvanceData() {
    var raw = cm.getCharacterExtendValue("equipAdvance");
    if (raw && raw !== "") {
        try { advanceData = JSON.parse(raw); } catch (e) { advanceData = {}; }
    } else {
        advanceData = {};
    }
}

function saveAdvanceData() {
    cm.saveOrUpdateCharacterExtendValue("equipAdvance", JSON.stringify(advanceData));
}

/** 判断物品ID是否为装备 */
function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

/**
 * 根据职业ID匹配进阶路线标识。
 * 细化到具体4转职业，使不同武器路线（英雄剑/龙骑枪/神射弓/箭神弩等）独立。
 * 未4转的职业向前匹配本职业群第一个路线作为兜底。
 * @param jobId 职业ID
 * @return 路线匹配的 job_group 字符串（如 '112'=英雄, '132'=龙骑士）
 */
function getRouteKey(jobId) {
    // 战士系
    if (jobId === 112 || (jobId >= 110 && jobId <= 111)) return "112"; // 英雄(剑)
    if (jobId === 122 || (jobId >= 120 && jobId <= 121)) return "122"; // 圣骑士(钝器)
    if (jobId === 132 || (jobId >= 130 && jobId <= 131)) return "132"; // 龙骑士(枪)
    if (jobId >= 100 && jobId < 110) return "112";   // 未转职战士 → 英雄路线

    // 弓箭手系
    if (jobId === 312 || (jobId >= 310 && jobId <= 311)) return "312"; // 神射手(弓)
    if (jobId === 322 || (jobId >= 320 && jobId <= 321)) return "322"; // 箭神(弩)
    if (jobId >= 300 && jobId < 310) return "312";   // 未转职弓箭手 → 神射手路线

    // 法师系
    if (jobId === 212 || (jobId >= 210 && jobId <= 211)) return "212"; // 火毒(长杖)
    if (jobId === 222 || (jobId >= 220 && jobId <= 221)) return "222"; // 冰雷(短杖)
    if (jobId === 232 || (jobId >= 230 && jobId <= 231)) return "232"; // 主教(长杖)
    if (jobId >= 200 && jobId < 210) return "212";   // 未转职法师 → 火毒路线

    // 飞侠系
    if (jobId === 412 || (jobId >= 410 && jobId <= 411)) return "412"; // 隐士(拳套)
    if (jobId === 422 || (jobId >= 420 && jobId <= 421)) return "422"; // 侠盗(短刀)
    if (jobId >= 400 && jobId < 410) return "412";   // 未转职飞侠 → 隐士路线

    // 海盗系
    if (jobId === 512 || (jobId >= 510 && jobId <= 511)) return "512"; // 冲锋队长(指节)
    if (jobId === 522 || (jobId >= 520 && jobId <= 521)) return "522"; // 船长(手枪)
    if (jobId >= 500 && jobId < 510) return "512";   // 未转职海盗 → 冲锋队长路线

    // 初心者兜底
    return "112";
}

/**
 * 获取指定装备在路线中的阶段索引
 * @param itemId 装备物品ID
 * @param route 路线DO
 * @returns 阶段索引（-1表示未找到）
 */
function getStageIndex(itemId, route) {
    var stages = EquipAdvanceManager.getStages(route.id);
    for (var i = 0; i < stages.size(); i++) {
        if (stages.get(i).targetItemId == itemId) {
            return i;
        }
    }
    return -1;
}

/** 获取某路线的所有阶段物品ID列表（用于匹配背包中的装备） */
function getStageItemIds(route) {
    var ids = [];
    var stages = EquipAdvanceManager.getStages(route.id);
    for (var i = 0; i < stages.size(); i++) {
        ids.push(stages.get(i).targetItemId);
    }
    return ids;
}

/**
 * ★ 安全应用累计属性到新装备 ★
 * 使用 EquipAdvanceManager.getCumulativeStats() 获取累计属性，
 * 再逐项通过 safeAddStat() 安全叠加到新装备上。
 */
function applyCumulativeStatsSafe(equip, routeId, upToStageOrder) {
    var cumStats = EquipAdvanceManager.getCumulativeStats(routeId, upToStageOrder);
    if (cumStats.get("strAdd") > 0)   equip.setStr(EquipAdvanceManager.safeAddStat(equip.getStr(), cumStats.get("strAdd")));
    if (cumStats.get("dexAdd") > 0)   equip.setDex(EquipAdvanceManager.safeAddStat(equip.getDex(), cumStats.get("dexAdd")));
    if (cumStats.get("intAdd") > 0)   equip.setInt(EquipAdvanceManager.safeAddStat(equip.getInt(), cumStats.get("intAdd")));
    if (cumStats.get("lukAdd") > 0)   equip.setLuk(EquipAdvanceManager.safeAddStat(equip.getLuk(), cumStats.get("lukAdd")));
    if (cumStats.get("hpAdd") > 0)    equip.setHp(EquipAdvanceManager.safeAddStat(equip.getHp(), cumStats.get("hpAdd")));
    if (cumStats.get("mpAdd") > 0)    equip.setMp(EquipAdvanceManager.safeAddStat(equip.getMp(), cumStats.get("mpAdd")));
    if (cumStats.get("watkAdd") > 0)  equip.setWatk(EquipAdvanceManager.safeAddStat(equip.getWatk(), cumStats.get("watkAdd")));
    if (cumStats.get("matkAdd") > 0)  equip.setMatk(EquipAdvanceManager.safeAddStat(equip.getMatk(), cumStats.get("matkAdd")));
    if (cumStats.get("wdefAdd") > 0)  equip.setWdef(EquipAdvanceManager.safeAddStat(equip.getWdef(), cumStats.get("wdefAdd")));
    if (cumStats.get("mdefAdd") > 0)  equip.setMdef(EquipAdvanceManager.safeAddStat(equip.getMdef(), cumStats.get("mdefAdd")));
    if (cumStats.get("accAdd") > 0)   equip.setAcc(EquipAdvanceManager.safeAddStat(equip.getAcc(), cumStats.get("accAdd")));
    if (cumStats.get("avoidAdd") > 0) equip.setAvoid(EquipAdvanceManager.safeAddStat(equip.getAvoid(), cumStats.get("avoidAdd")));
    if (cumStats.get("speedAdd") > 0) equip.setSpeed(EquipAdvanceManager.safeAddStat(equip.getSpeed(), cumStats.get("speedAdd")));
    if (cumStats.get("jumpAdd") > 0)  equip.setJump(EquipAdvanceManager.safeAddStat(equip.getJump(), cumStats.get("jumpAdd")));
}

// ===== 入口 =====

function start() {
    levelStart();
}

function action(mode, type, selection) {
    cm.dispose();
}

function levelStart() {
    loadAdvanceData();

    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getRouteKey(jobId);

    // 始终从数据库刷新缓存，确保拿到最新配置
    EquipAdvanceManager.reload();
    var routeMap = EquipAdvanceManager.getRouteMap();

    selectedRoute = EquipAdvanceManager.getRoute(jobGroup);
    if (!selectedRoute || selectedRoute.id == null) {
        var msg = "当前职业群 #b" + getRouteDisplayName(jobGroup) + "#k 暂未配置装备进阶路线。\r\n\r\n";
        msg += "当前已配置的职业群:\r\n";
        var routeCount = routeMap.size();
        if (routeCount === 0) {
            msg += "  #r无（请通过Web管理后台 → 游戏管理 → 装备进阶 添加路线配置）#k\r\n";
        } else {
            var keySet = routeMap.keySet().toArray();
            for (var k = 0; k < keySet.length; k++) {
                var r = routeMap.get(keySet[k]);
                msg += "  #b" + getRouteDisplayName(keySet[k]) + "#k - " + r.routeName + "\r\n";
            }
            msg += "\r\n#r你的职业群 (" + getRouteDisplayName(jobGroup) + ") 不在上述列表中#k";
        }
        cm.sendOk(msg);
        cm.dispose();
        return;
    }

    showEquipList();
}

/** 展示背包中匹配进阶路线的装备 */
function showEquipList() {
    var stageItemIds = getStageItemIds(selectedRoute);
    var text = "#e#b=== " + selectedRoute.routeName + " 装备进阶 ===#k#n\r\n";
    text += "#r将装备进化为更高阶形态，属性叠加！剩余强化次数保留！#k\r\n\r\n";
    text += "请选择要进阶的装备：\r\n";

    _stageMap = [];
    var idx = 0;
    var invTypes = [InventoryType.EQUIPPED, InventoryType.EQUIP];

    for (var t = 0; t < invTypes.length; t++) {
        var inv = cm.getPlayer().getInventory(invTypes[t]);
        if (!inv) continue;
        var items = inv.list();
        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            if (!item || !isEquipment(item.getItemId())) continue;
            // 检查是否匹配进阶路线中的任意阶段
            var stageIdx = getStageIndex(item.getItemId(), selectedRoute);
            if (stageIdx < 0) continue;
            var slot = item.getPosition();
            var stages = EquipAdvanceManager.getStages(selectedRoute.id);
            var currentStage = stages.get(stageIdx);
            var nextStage = (stageIdx + 1 < stages.size()) ? stages.get(stageIdx + 1) : null;

            text += "#L" + idx + "##b#i" + currentStage.targetItemId + "# " + currentStage.targetItemName + "#k";
            text += " (槽位:" + slot + ")";
            text += " #d[" + getStageLabel(currentStage.stageOrder) + "]#k";
            if (nextStage) {
                text += " → #r可进阶至 " + nextStage.targetItemName + "#k";
            } else {
                text += " #r[满阶]#k";
            }
            text += "#l\r\n";

            _stageMap.push({
                slot: slot,
                stageIdx: stageIdx,
                invType: invTypes[t],
                item: item
            });
            idx++;
        }
    }

    if (idx === 0) {
        text += "#r背包和穿戴栏中没有可进阶的装备。#k\r\n";
        text += "该路线初始装备: #b";
        var allStages = EquipAdvanceManager.getStages(selectedRoute.id);
        if (allStages.size() > 0) {
            text += "#i" + allStages.get(0).targetItemId + "# " + allStages.get(0).targetItemName;
        }
        text += "#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    cm.sendNextSelectLevel("EquipSelect", text);
}

function levelEquipSelect(selection) {
    if (selection < 0 || selection >= _stageMap.length) {
        cm.dispose();
        return;
    }

    var info = _stageMap[selection];
    selectedSlot = info.slot;
    selectedInvType = info.invType;
    selectedEquip = info.item;

    var stages = EquipAdvanceManager.getStages(selectedRoute.id);
    selectedStage = stages.get(info.stageIdx);

    // 检查是否还有下一阶段
    var nextStageIdx = info.stageIdx + 1;
    if (nextStageIdx >= stages.size()) {
        cm.sendOk("#b" + selectedStage.targetItemName + "#k 已达最高阶 #r[满阶]#k！");
        cm.dispose();
        return;
    }

    var nextStage = stages.get(nextStageIdx);
    showAdvanceConfirm(nextStage);
}

/** 展示进阶确认界面 */
function showAdvanceConfirm(nextStage) {
    var costs = EquipAdvanceManager.getCosts(nextStage.id);
    var cumulativeStats = EquipAdvanceManager.getCumulativeStats(selectedRoute.id, nextStage.stageOrder);

    var text = "#e#b【装备进阶确认】#k#n\r\n\r\n";
    text += "当前装备: #b#i" + selectedStage.targetItemId + "# " + selectedStage.targetItemName + "#k";
    text += " #d[" + getStageLabel(selectedStage.stageOrder) + "]#k\r\n";
    text += "进阶目标: #r#i" + nextStage.targetItemId + "# " + nextStage.targetItemName + "#k";
    text += " #d[" + getStageLabel(nextStage.stageOrder) + "]#k\r\n";
    text += "剩余强化次数: #b" + selectedEquip.getUpgradeSlots() + "#k (保留)\r\n\r\n";

    // 消耗清单
    text += "#d========== 消耗明细 ==========#k\r\n";
    if (nextStage.mesoCost > 0) {
        text += "金币: #b" + nextStage.mesoCost.toLocaleString() + " 金币#k\r\n";
    }
    if (nextStage.cashCost > 0) {
        text += "点卷: #b" + nextStage.cashCost.toLocaleString() + " 点#k\r\n";
    }
    if (nextStage.creditCost > 0) {
        text += "抵用券: #b" + nextStage.creditCost.toLocaleString() + " 抵用#k\r\n";
    }
    for (var c = 0; c < costs.size(); c++) {
        var co = costs.get(c);
        text += "材料: #i" + co.itemId + "# ×" + co.count + "\r\n";
    }

    // 本次进阶属性预览
    text += "\r\n#d========== 本次进阶属性加成 ==========#k\r\n";
    text += formatStatText(nextStage);

    // 累计属性预览
    text += "\r\n\r\n#d========== 累计属性加成（含之前所有阶段） ==========#k\r\n";
    text += formatStatText(cumulativeStats);

    text += "\r\n\r\n#L0##r确认进阶#k#l\r\n#L1#取消#l";
    cm.sendNextSelectLevel("AdvanceResult", text);
}

function levelAdvanceResult(selection) {
    if (selection !== 0) {
        cm.dispose();
        return;
    }

    var stages = EquipAdvanceManager.getStages(selectedRoute.id);
    var currentStageIdx = getStageIndex(selectedEquip.getItemId(), selectedRoute);
    if (currentStageIdx < 0 || currentStageIdx + 1 >= stages.size()) {
        cm.sendOk("进阶数据异常，请重试。");
        cm.dispose();
        return;
    }

    var nextStage = stages.get(currentStageIdx + 1);
    var costs = EquipAdvanceManager.getCosts(nextStage.id);

    // === 检查金币 ===
    if (nextStage.mesoCost > 0 && cm.getPlayer().getMeso() < nextStage.mesoCost) {
        cm.sendOk("金币不足！需要 #r" + nextStage.mesoCost.toLocaleString() + " 金币#k。");
        cm.dispose();
        return;
    }

    // === 检查点卷 ===
    if (nextStage.cashCost > 0 && cm.getPlayer().getCashShop().getCash(1) < nextStage.cashCost) {
        cm.sendOk("点卷不足！需要 #r" + nextStage.cashCost.toLocaleString() + " 点#k。");
        cm.dispose();
        return;
    }

    // === 检查抵用券 ===
    if (nextStage.creditCost > 0 && cm.getPlayer().getCashShop().getCash(2) < nextStage.creditCost) {
        cm.sendOk("抵用券不足！需要 #r" + nextStage.creditCost.toLocaleString() + " 抵用#k。");
        cm.dispose();
        return;
    }

    // === 检查材料 ===
    for (var c = 0; c < costs.size(); c++) {
        var co = costs.get(c);
        if (!cm.haveItem(co.itemId, co.count)) {
            cm.sendOk("材料不足！#i" + co.itemId + "# 需要 ×" + co.count);
            cm.dispose();
            return;
        }
    }

    // === 扣除金币 ===
    if (nextStage.mesoCost > 0) {
        cm.gainMeso(-nextStage.mesoCost); // 修复：gainMeso无3参数重载，改用单参数版本
    }

    // === 扣除点卷 ===
    if (nextStage.cashCost > 0) {
        cm.getPlayer().getCashShop().gainCash(1, -nextStage.cashCost);
    }

    // === 扣除抵用券 ===
    if (nextStage.creditCost > 0) {
        cm.getPlayer().getCashShop().gainCash(2, -nextStage.creditCost);
    }

    // === 扣除材料 ===
    for (var cc = 0; cc < costs.size(); cc++) {
        var cco = costs.get(cc);
        cm.gainItem(cco.itemId, -cco.count);
    }

    // === 执行进阶：用新装备替换旧装备 ===
    var oldSlots = selectedEquip.getUpgradeSlots() & 0xFF;

    // 从WZ数据创建新装备（基础属性）
    var ii = ItemInformationProvider.getInstance();
    var newEquip = ii.getEquipById(nextStage.targetItemId);

    // 保留强化次数
    newEquip.setUpgradeSlots(oldSlots);

    // ★ 核心改动：使用 safeAddStat 安全叠加累计属性 ★
    applyCumulativeStatsSafe(newEquip, selectedRoute.id, nextStage.stageOrder);

    // 使用 InventoryManipulator 移除旧装备并添加新装备
    var player = cm.getPlayer();
    var c = player.getClient();
    InventoryManipulator.removeFromSlot(c, selectedInvType, selectedSlot, 1, false);

    var success = InventoryManipulator.addFromDrop(c, newEquip, true);
    if (!success) {
        cm.sendOk("背包已满，进阶失败！材料已消耗。\r\n请清理背包后联系管理员恢复。");
        cm.dispose();
        return;
    }

    // 保存进阶数据
    advanceData[String(nextStage.targetItemId)] = {
        stageOrder: nextStage.stageOrder,
        routeId: selectedRoute.id
    };
    saveAdvanceData();

    var cumulativeStats = EquipAdvanceManager.getCumulativeStats(selectedRoute.id, nextStage.stageOrder);

    var text = "#e#b进阶成功！#k#n\r\n\r\n";
    text += "获得: #r#i" + nextStage.targetItemId + "# " + nextStage.targetItemName + "#k\r\n";
    text += "强化次数: #b" + oldSlots + " (已保留)#k\r\n\r\n";
    text += "累计加成:\r\n";
    text += formatStatText(cumulativeStats);
    cm.sendOk(text);
    cm.dispose();
}

/**
 * 格式化属性文本（支持 DO 对象和 Map 对象）
 */
function formatStatText(stats) {
    var parts = [];
    var str = 0, dex = 0, int_ = 0, luk = 0, hp = 0, mp = 0;
    var watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, speed = 0, jump = 0;

    // DO对象直接读字段
    if (stats.strAdd !== undefined) {
        str = stats.strAdd; dex = stats.dexAdd; int_ = stats.intAdd; luk = stats.lukAdd;
        hp = stats.hpAdd; mp = stats.mpAdd; watk = stats.watkAdd; matk = stats.matkAdd;
        wdef = stats.wdefAdd; mdef = stats.mdefAdd; acc = stats.accAdd; avoid = stats.avoidAdd;
        speed = stats.speedAdd; jump = stats.jumpAdd;
    } else {
        // Map对象
        try { str = stats.get("strAdd"); dex = stats.get("dexAdd"); int_ = stats.get("intAdd"); luk = stats.get("lukAdd");
              hp = stats.get("hpAdd"); mp = stats.get("mpAdd"); watk = stats.get("watkAdd"); matk = stats.get("matkAdd");
              wdef = stats.get("wdefAdd"); mdef = stats.get("mdefAdd"); acc = stats.get("accAdd"); avoid = stats.get("avoidAdd");
              speed = stats.get("speedAdd"); jump = stats.get("jumpAdd"); } catch(e) {}
    }

    if (str > 0) parts.push("力量+" + str);
    if (dex > 0) parts.push("敏捷+" + dex);
    if (int_ > 0) parts.push("智力+" + int_);
    if (luk > 0) parts.push("运气+" + luk);
    if (hp > 0) parts.push("HP+" + hp);
    if (mp > 0) parts.push("MP+" + mp);
    if (watk > 0) parts.push("物攻+" + watk);
    if (matk > 0) parts.push("魔攻+" + matk);
    if (wdef > 0) parts.push("物防+" + wdef);
    if (mdef > 0) parts.push("魔防+" + mdef);
    if (acc > 0) parts.push("命中+" + acc);
    if (avoid > 0) parts.push("回避+" + avoid);
    if (speed > 0) parts.push("速度+" + speed);
    if (jump > 0) parts.push("跳跃+" + jump);
    return parts.length > 0 ? parts.join(", ") : "无属性加成";
}

/** 获取路线中文名称（基于职业ID） */
function getRouteDisplayName(routeKey) {
    switch (routeKey) {
        // 战士系
        case "112": return "英雄(剑/斧)";
        case "122": return "圣骑士(钝器)";
        case "132": return "龙骑士(枪/矛)";
        // 弓箭手系
        case "312": return "神射手(弓)";
        case "322": return "箭神(弩)";
        // 法师系
        case "212": return "火毒法师(长杖)";
        case "222": return "冰雷法师(短杖)";
        case "232": return "主教(长杖)";
        // 飞侠系
        case "412": return "隐士(拳套)";
        case "422": return "侠盗(短刀)";
        // 海盗系
        case "512": return "冲锋队长(指节)";
        case "522": return "船长(手枪)";
        default:    return routeKey;
    }
}

/** 获取阶段标签 */
function getStageLabel(stageOrder) {
    if (stageOrder === 0) return "初始";
    return stageOrder + "阶";
}
