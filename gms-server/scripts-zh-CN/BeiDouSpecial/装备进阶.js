/*
 * ==================
 * 脚本类型: 装备进阶
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 按职业群（战士/弓箭手/法师/飞侠/海盗）自动匹配进阶路线
 *   2. 每阶消耗多种材料（可配置数量）、金币、点卷、抵用券
 *   3. 进阶后获得下一阶段装备，属性与上一阶段所有属性叠加
 *   4. 剩余强化次数保留上一阶段的
 *   5. 管理员通过Web后台维护进阶配置
 * ==================
 */

var EquipAdvanceManager = Java.type('org.gms.config.EquipAdvanceManager');
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');

var selectedRoute = null;       // EquipAdvanceRouteDO
var selectedEquip = null;       // 玩家背包中的Equip对象
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

function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

/**
 * 根据职业ID判断职业群
 * @param jobId 职业ID
 * @return 职业群字符串（warrior/archer/mage/thief/pirate），不匹配返回null
 */
function getJobGroup(jobId) {
    if (jobId >= 100 && jobId <= 132) return "warrior";
    if (jobId >= 200 && jobId <= 232) return "archer";
    if (jobId >= 300 && jobId <= 332) return "mage";
    if (jobId >= 400 && jobId <= 432) return "thief";
    if (jobId >= 500 && jobId <= 532) return "pirate";
    // 初心者用战士路线兜底
    return "warrior";
}

/**
 * 获取当前进阶阶段（在路线中的位置）
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

/**
 * 获取某路线的所有阶段物品ID列表（用于匹配背包中的装备）
 */
function getStageItemIds(route) {
    var ids = [];
    var stages = EquipAdvanceManager.getStages(route.id);
    for (var i = 0; i < stages.size(); i++) {
        ids.push(stages.get(i).targetItemId);
    }
    return ids;
}

/**
 * 累计属性叠加（从阶段0到指定阶段的所有属性之和）
 * @param routeId 路线ID
 * @param upToStageOrder 累计到哪个阶段（含）
 * @returns 累计属性对象
 */
function getCumulativeStats(routeId, upToStageOrder) {
    var stats = {
        strAdd: 0, dexAdd: 0, intAdd: 0, lukAdd: 0,
        hpAdd: 0, mpAdd: 0, watkAdd: 0, matkAdd: 0,
        wdefAdd: 0, mdefAdd: 0, accAdd: 0, avoidAdd: 0,
        speedAdd: 0, jumpAdd: 0
    };
    var stages = EquipAdvanceManager.getStages(routeId);
    for (var i = 0; i < stages.size(); i++) {
        var s = stages.get(i);
        if (s.stageOrder > upToStageOrder) break;
        // 第0阶段（初始装备）不叠加属性，从第1阶段开始叠加
        if (s.stageOrder == 0) continue;
        stats.strAdd += s.strAdd;
        stats.dexAdd += s.dexAdd;
        stats.intAdd += s.intAdd;
        stats.lukAdd += s.lukAdd;
        stats.hpAdd += s.hpAdd;
        stats.mpAdd += s.mpAdd;
        stats.watkAdd += s.watkAdd;
        stats.matkAdd += s.matkAdd;
        stats.wdefAdd += s.wdefAdd;
        stats.mdefAdd += s.mdefAdd;
        stats.accAdd += s.accAdd;
        stats.avoidAdd += s.avoidAdd;
        stats.speedAdd += s.speedAdd;
        stats.jumpAdd += s.jumpAdd;
    }
    return stats;
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
    var jobGroup = getJobGroup(jobId);

    // 始终从数据库刷新缓存，确保拿到最新配置
    EquipAdvanceManager.reload();
    var routeMap = EquipAdvanceManager.getRouteMap();

    // 诊断信息：显示缓存中有多少条路线
    var routeCount = routeMap.size();
    cm.getPlayer().dropMessage(5, "[装备进阶] 缓存路线数: " + routeCount + ", 当前职业群: " + jobGroup + "(" + getJobGroupName(jobGroup) + ")");

    selectedRoute = EquipAdvanceManager.getRoute(jobGroup);
    if (!selectedRoute || selectedRoute.id == null) {
        var msg = "当前职业群 #b" + getJobGroupName(jobGroup) + "#k 暂未配置装备进阶路线。\r\n\r\n";
        msg += "当前已配置的职业群:\r\n";
        if (routeCount === 0) {
            msg += "  #r无（缓存为空，请检查 server 日志是否有报错）#k\r\n";
            msg += "\r\n#d提示：请通过Web管理后台 → 游戏管理 → 装备进阶 添加路线配置。#k";
        } else {
            var keySet = routeMap.keySet().toArray();
            for (var k = 0; k < keySet.length; k++) {
                var r = routeMap.get(keySet[k]);
                msg += "  #b" + getJobGroupName(keySet[k]) + "#k - " + r.routeName + "\r\n";
            }
            msg += "\r\n#r你的职业群 (" + getJobGroupName(jobGroup) + ") 不在上述列表中#k";
        }
        cm.sendOk(msg);
        cm.dispose();
        return;
    }

    // 展示背包中匹配的装备
    showEquipList();
}

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

function showAdvanceConfirm(nextStage) {
    var costs = EquipAdvanceManager.getCosts(nextStage.id);
    var cumulativeStats = getCumulativeStats(selectedRoute.id, nextStage.stageOrder);

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

    // 属性预览（本次进阶加成）
    text += "\r\n#d========== 本次进阶属性加成 ==========#k\r\n";
    var thisStats = [];
    if (nextStage.strAdd > 0) thisStats.push("力量+" + nextStage.strAdd);
    if (nextStage.dexAdd > 0) thisStats.push("敏捷+" + nextStage.dexAdd);
    if (nextStage.intAdd > 0) thisStats.push("智力+" + nextStage.intAdd);
    if (nextStage.lukAdd > 0) thisStats.push("运气+" + nextStage.lukAdd);
    if (nextStage.hpAdd > 0) thisStats.push("HP+" + nextStage.hpAdd);
    if (nextStage.mpAdd > 0) thisStats.push("MP+" + nextStage.mpAdd);
    if (nextStage.watkAdd > 0) thisStats.push("物攻+" + nextStage.watkAdd);
    if (nextStage.matkAdd > 0) thisStats.push("魔攻+" + nextStage.matkAdd);
    if (nextStage.wdefAdd > 0) thisStats.push("物防+" + nextStage.wdefAdd);
    if (nextStage.mdefAdd > 0) thisStats.push("魔防+" + nextStage.mdefAdd);
    if (nextStage.accAdd > 0) thisStats.push("命中+" + nextStage.accAdd);
    if (nextStage.avoidAdd > 0) thisStats.push("回避+" + nextStage.avoidAdd);
    if (nextStage.speedAdd > 0) thisStats.push("速度+" + nextStage.speedAdd);
    if (nextStage.jumpAdd > 0) thisStats.push("跳跃+" + nextStage.jumpAdd);
    text += thisStats.length > 0 ? thisStats.join(", ") : "无属性加成";

    // 累计属性预览
    text += "\r\n\r\n#d========== 累计属性加成（含之前所有阶段） ==========#k\r\n";
    var cumStats = [];
    if (cumulativeStats.strAdd > 0) cumStats.push("力量+" + cumulativeStats.strAdd);
    if (cumulativeStats.dexAdd > 0) cumStats.push("敏捷+" + cumulativeStats.dexAdd);
    if (cumulativeStats.intAdd > 0) cumStats.push("智力+" + cumulativeStats.intAdd);
    if (cumulativeStats.lukAdd > 0) cumStats.push("运气+" + cumulativeStats.lukAdd);
    if (cumulativeStats.hpAdd > 0) cumStats.push("HP+" + cumulativeStats.hpAdd);
    if (cumulativeStats.mpAdd > 0) cumStats.push("MP+" + cumulativeStats.mpAdd);
    if (cumulativeStats.watkAdd > 0) cumStats.push("物攻+" + cumulativeStats.watkAdd);
    if (cumulativeStats.matkAdd > 0) cumStats.push("魔攻+" + cumulativeStats.matkAdd);
    if (cumulativeStats.wdefAdd > 0) cumStats.push("物防+" + cumulativeStats.wdefAdd);
    if (cumulativeStats.mdefAdd > 0) cumStats.push("魔防+" + cumulativeStats.mdefAdd);
    if (cumulativeStats.accAdd > 0) cumStats.push("命中+" + cumulativeStats.accAdd);
    if (cumulativeStats.avoidAdd > 0) cumStats.push("回避+" + cumulativeStats.avoidAdd);
    if (cumulativeStats.speedAdd > 0) cumStats.push("速度+" + cumulativeStats.speedAdd);
    if (cumulativeStats.jumpAdd > 0) cumStats.push("跳跃+" + cumulativeStats.jumpAdd);
    text += cumStats.length > 0 ? cumStats.join(", ") : "无属性加成";

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
        cm.getPlayer().gainMeso(-nextStage.mesoCost, true, false);
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
    // 保存旧装备的剩余强化次数（Java byte → JS int）
    var oldSlots = selectedEquip.getUpgradeSlots() & 0xFF;

    // 创建新装备（从WZ数据加载基础属性）
    var ii = ItemInformationProvider.getInstance();
    var newEquip = ii.getEquipById(nextStage.targetItemId);

    // 复制剩余强化次数
    newEquip.setUpgradeSlots(oldSlots);

    // 叠加所有阶段的属性（从第1阶段到当前进阶阶段）
    var cumulativeStats = getCumulativeStats(selectedRoute.id, nextStage.stageOrder);
    newEquip.setStr(newEquip.getStr() + cumulativeStats.strAdd);
    newEquip.setDex(newEquip.getDex() + cumulativeStats.dexAdd);
    newEquip.setInt(newEquip.getInt() + cumulativeStats.intAdd);
    newEquip.setLuk(newEquip.getLuk() + cumulativeStats.lukAdd);
    newEquip.setHp(newEquip.getHp() + cumulativeStats.hpAdd);
    newEquip.setMp(newEquip.getMp() + cumulativeStats.mpAdd);
    newEquip.setWatk(newEquip.getWatk() + cumulativeStats.watkAdd);
    newEquip.setMatk(newEquip.getMatk() + cumulativeStats.matkAdd);
    newEquip.setWdef(newEquip.getWdef() + cumulativeStats.wdefAdd);
    newEquip.setMdef(newEquip.getMdef() + cumulativeStats.mdefAdd);
    newEquip.setAcc(newEquip.getAcc() + cumulativeStats.accAdd);
    newEquip.setAvoid(newEquip.getAvoid() + cumulativeStats.avoidAdd);
    newEquip.setSpeed(newEquip.getSpeed() + cumulativeStats.speedAdd);
    newEquip.setJump(newEquip.getJump() + cumulativeStats.jumpAdd);

    // 使用 InventoryManipulator 移除旧装备并添加新装备
    var player = cm.getPlayer();
    var c = player.getClient();
    InventoryManipulator.removeFromSlot(c, selectedInvType, selectedSlot, 1, false);

    // 添加新装备到背包
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

    var text = "#e#b进阶成功！#k#n\r\n\r\n";
    text += "获得: #r#i" + nextStage.targetItemId + "# " + nextStage.targetItemName + "#k\r\n";
    text += "强化次数: #b" + oldSlots + " (已保留)#k\r\n\r\n";
    text += "累计加成:\r\n";
    var cumStats = [];
    if (cumulativeStats.strAdd > 0) cumStats.push("力量+" + cumulativeStats.strAdd);
    if (cumulativeStats.dexAdd > 0) cumStats.push("敏捷+" + cumulativeStats.dexAdd);
    if (cumulativeStats.intAdd > 0) cumStats.push("智力+" + cumulativeStats.intAdd);
    if (cumulativeStats.lukAdd > 0) cumStats.push("运气+" + cumulativeStats.lukAdd);
    if (cumulativeStats.watkAdd > 0) cumStats.push("物攻+" + cumulativeStats.watkAdd);
    if (cumulativeStats.matkAdd > 0) cumStats.push("魔攻+" + cumulativeStats.matkAdd);
    text += cumStats.length > 0 ? cumStats.join(", ") : "";
    cm.sendOk(text);
    cm.dispose();
}

/**
 * 获取职业群中文名称
 */
function getJobGroupName(jobGroup) {
    switch (jobGroup) {
        case "warrior": return "战士";
        case "archer":  return "弓箭手";
        case "mage":    return "法师";
        case "thief":   return "飞侠";
        case "pirate":  return "海盗";
        default:        return jobGroup;
    }
}

/**
 * 获取阶段标签
 */
function getStageLabel(stageOrder) {
    if (stageOrder === 0) return "初始";
    return stageOrder + "阶";
}
