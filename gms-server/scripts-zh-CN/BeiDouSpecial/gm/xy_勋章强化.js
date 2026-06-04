/*
 * ==================
 * 脚本类型: 勋章强化
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 对玩家装备栏中的任意勋章进行强化
 *   2. 强化进度按玩家维度存储（更换勋章继续从上次等级开始）
 *   3. 强化数据从数据库 medal_enhance_config/_level/_cost 表读取
 *   4. 强化属性直接写入勋章装备
 *   5. 管理员通过Web后台维护强化配置
 * ==================
 */

var MedalEnhanceManager = Java.type('org.gms.config.MedalEnhanceManager');
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

var selectedConfig = null;
var selectedItem = null;
var selectedSlot = -1;
var selectedInvType = null;
var currentLevel = 0;

var _medalList = [];

// ===== 数据持久化 =====

function loadEnhanceLevel() {
    var raw = cm.getCharacterExtendValue("medalEnhance");
    if (raw && raw !== "") {
        try {
            currentLevel = parseInt(JSON.parse(raw).level) || 0;
        } catch (e) {
            currentLevel = 0;
        }
    } else {
        currentLevel = 0;
    }
}

function saveEnhanceLevel(level) {
    cm.saveOrUpdateCharacterExtendValue("medalEnhance", JSON.stringify({level: level}));
}

// ===== 入口 =====

function start() {
    levelStart();
}

function levelStart() {
    loadEnhanceLevel();

    selectedConfig = MedalEnhanceManager.getFirstConfig();
    if (!selectedConfig || selectedConfig.enabled != 1) {
        cm.sendOk("勋章强化系统暂未开放，请联系管理员配置。");
        cm.dispose();
        return;
    }

    if (currentLevel >= selectedConfig.maxEnhance) {
        cm.sendOk("你的勋章已达到最高强化等级 #r★" + currentLevel + "#k！\r\n无法继续强化。");
        cm.dispose();
        return;
    }

    var text = "#e#b=== 勋章强化系统 ===#k#n\r\n";
    text += "#r强化属性直接写入勋章，永久有效#k\r\n";
    text += "当前强化等级: #r★" + currentLevel + "#k (最高★" + selectedConfig.maxEnhance + ")\r\n\r\n";
    text += "请选择要强化的勋章：\r\n";

    _medalList = [];
    var idx = 0;

    // 1. 已装备的勋章（槽位 -49）
    var equippedInv = cm.getPlayer().getInventory(InventoryType.EQUIPPED);
    if (equippedInv) {
        var items = equippedInv.list();
        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            if (isMedal(item.getItemId())) {
                var slot = item.getPosition();
                text += "#L" + idx + "##b" + cm.getItemName(item.getItemId()) + "#k";
                text += " #d[已装备]#k";
                text += "\r\n#l";
                _medalList.push({slot: slot, invType: InventoryType.EQUIPPED, itemId: item.getItemId()});
                idx++;
            }
        }
    }

    // 2. 背包中的勋章
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    if (equipInv) {
        var equipItems = equipInv.list();
        for (var j = 0; j < equipItems.size(); j++) {
            var eqItem = equipItems.get(j);
            if (isMedal(eqItem.getItemId())) {
                var eqSlot = eqItem.getPosition();
                text += "#L" + idx + "##b" + cm.getItemName(eqItem.getItemId()) + "#k";
                text += " #d[背包]#k";
                text += "\r\n#l";
                _medalList.push({slot: eqSlot, invType: InventoryType.EQUIP, itemId: eqItem.getItemId()});
                idx++;
            }
        }
    }

    if (idx === 0) {
        text += "\r\n#r你的背包和装备栏中没有勋章。#k";
        text += "\r\n可通过游戏内活动或商城获得勋章。";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    cm.sendNextSelectLevel("MedalSelect", text);
}

function levelMedalSelect(selection) {
    if (selection < 0 || selection >= _medalList.length) {
        cm.dispose();
        return;
    }

    var info = _medalList[selection];
    selectedSlot = info.slot;
    selectedInvType = info.invType;

    // 定位装备对象
    var inv = cm.getPlayer().getInventory(selectedInvType);
    var items = inv.list();
    selectedItem = null;
    for (var i = 0; i < items.size(); i++) {
        var it = items.get(i);
        if (it.getPosition() == selectedSlot) {
            selectedItem = it;
            break;
        }
    }

    if (!selectedItem || !isMedal(selectedItem.getItemId())) {
        cm.sendOk("勋章数据异常，请重试。");
        cm.dispose();
        return;
    }

    var nextLevel = currentLevel + 1;
    var levelCfg = MedalEnhanceManager.getLevel(selectedConfig.id, nextLevel);
    if (!levelCfg) {
        cm.sendOk("强化配置异常：未找到等级 " + nextLevel + " 的配置。");
        cm.dispose();
        return;
    }

    var costs = MedalEnhanceManager.getCosts(levelCfg.id);

    var text = "【勋章强化确认】\r\n";
    text += "勋章: #b" + cm.getItemName(selectedItem.getItemId()) + "#k\r\n";
    text += "当前强化等级: " + (currentLevel > 0 ? "★" + currentLevel : "未强化") + "\r\n";
    text += "目标等级: #r★" + nextLevel + "#k\r\n";
    text += "成功率: #b" + levelCfg.successRate + "%#k";

    if (levelCfg.destroyOnFail == 1) {
        text += " #r[失败销毁勋章]#k";
    }
    text += "\r\n";

    if (levelCfg.mesoCost > 0) {
        text += "金币消耗: #b" + levelCfg.mesoCost.toLocaleString() + " 金币#k\r\n";
    }

    for (var c = 0; c < costs.size(); c++) {
        var co = costs.get(c);
        text += "消耗: #i" + co.itemId + "# ×" + co.count + "\r\n";
    }

    text += "\r\n属性加成预览(#r仅本次强化#k):\r\n";
    var stats = [];
    if (levelCfg.strAdd > 0) stats.push("力量+" + levelCfg.strAdd);
    if (levelCfg.dexAdd > 0) stats.push("敏捷+" + levelCfg.dexAdd);
    if (levelCfg.intAdd > 0) stats.push("智力+" + levelCfg.intAdd);
    if (levelCfg.lukAdd > 0) stats.push("运气+" + levelCfg.lukAdd);
    if (levelCfg.hpAdd > 0) stats.push("HP+" + levelCfg.hpAdd);
    if (levelCfg.mpAdd > 0) stats.push("MP+" + levelCfg.mpAdd);
    if (levelCfg.watkAdd > 0) stats.push("物攻+" + levelCfg.watkAdd);
    if (levelCfg.matkAdd > 0) stats.push("魔攻+" + levelCfg.matkAdd);
    if (levelCfg.wdefAdd > 0) stats.push("物防+" + levelCfg.wdefAdd);
    if (levelCfg.mdefAdd > 0) stats.push("魔防+" + levelCfg.mdefAdd);
    if (levelCfg.accAdd > 0) stats.push("命中+" + levelCfg.accAdd);
    if (levelCfg.avoidAdd > 0) stats.push("回避+" + levelCfg.avoidAdd);
    if (levelCfg.speedAdd > 0) stats.push("速度+" + levelCfg.speedAdd);
    if (levelCfg.jumpAdd > 0) stats.push("跳跃+" + levelCfg.jumpAdd);
    text += stats.length > 0 ? stats.join(", ") : "无属性加成";

    text += "\r\n\r\n#L0#确认强化#l\r\n#L1#取消#l";
    cm.sendNextSelectLevel("EnhanceResult", text);
}

function levelEnhanceResult(selection) {
    if (selection !== 0) {
        cm.dispose();
        return;
    }

    var nextLevel = currentLevel + 1;
    var levelCfg = MedalEnhanceManager.getLevel(selectedConfig.id, nextLevel);
    if (!levelCfg) {
        cm.dispose();
        return;
    }
    var costs = MedalEnhanceManager.getCosts(levelCfg.id);

    // 检查金币
    if (levelCfg.mesoCost > 0 && cm.getPlayer().getMeso() < levelCfg.mesoCost) {
        cm.sendOk("金币不足！需要 #r" + levelCfg.mesoCost.toLocaleString() + " 金币#k。");
        cm.dispose();
        return;
    }

    // 检查道具
    for (var c = 0; c < costs.size(); c++) {
        var co = costs.get(c);
        if (!cm.haveItem(co.itemId, co.count)) {
            cm.sendOk("材料不足！#i" + co.itemId + "# 需要 ×" + co.count);
            cm.dispose();
            return;
        }
    }

    // 扣除金币
    if (levelCfg.mesoCost > 0) {
        cm.getPlayer().gainMeso(-levelCfg.mesoCost, true, false);
    }

    // 扣除道具
    for (var cc = 0; cc < costs.size(); cc++) {
        var cco = costs.get(cc);
        cm.gainItem(cco.itemId, -cco.count);
    }

    // 成功判定
    var roll = Math.floor(Math.random() * 100);
    if (roll < levelCfg.successRate) {
        applyStats(selectedItem, levelCfg);
        currentLevel = nextLevel;
        saveEnhanceLevel(currentLevel);

        var totalStats = getTotalStats(currentLevel);
        cm.sendOk("强化成功！\r\n" + cm.getItemName(selectedItem.getItemId())
            + " 强化等级 → #r★" + currentLevel + "#k\r\n"
            + "强化属性已写入勋章！\r\n\r\n当前累计加成: " + totalStats);
    } else {
        if (levelCfg.destroyOnFail == 1) {
            cm.getPlayer().getInventory(selectedInvType).removeSlot(selectedSlot);
            cm.sendOk("强化失败！勋章已销毁。\r\n材料已消耗。");
        } else {
            cm.sendOk("强化失败！勋章保留，材料已消耗。\r\n可再次尝试强化。");
        }
    }
    cm.dispose();
}

// 应用属性到装备
function applyStats(equip, cfg) {
    if (cfg.strAdd > 0) equip.setStr(equip.getStr() + cfg.strAdd);
    if (cfg.dexAdd > 0) equip.setDex(equip.getDex() + cfg.dexAdd);
    if (cfg.intAdd > 0) equip.setInt(equip.getInt() + cfg.intAdd);
    if (cfg.lukAdd > 0) equip.setLuk(equip.getLuk() + cfg.lukAdd);
    if (cfg.hpAdd > 0) equip.setHp(equip.getHp() + cfg.hpAdd);
    if (cfg.mpAdd > 0) equip.setMp(equip.getMp() + cfg.mpAdd);
    if (cfg.watkAdd > 0) equip.setWatk(equip.getWatk() + cfg.watkAdd);
    if (cfg.matkAdd > 0) equip.setMatk(equip.getMatk() + cfg.matkAdd);
    if (cfg.wdefAdd > 0) equip.setWdef(equip.getWdef() + cfg.wdefAdd);
    if (cfg.mdefAdd > 0) equip.setMdef(equip.getMdef() + cfg.mdefAdd);
    if (cfg.accAdd > 0) equip.setAcc(equip.getAcc() + cfg.accAdd);
    if (cfg.avoidAdd > 0) equip.setAvoid(equip.getAvoid() + cfg.avoidAdd);
    if (cfg.speedAdd > 0) equip.setSpeed(equip.getSpeed() + cfg.speedAdd);
    if (cfg.jumpAdd > 0) equip.setJump(equip.getJump() + cfg.jumpAdd);
}

// 获取累计属性文本
function getTotalStats(star) {
    var parts = [];
    var acc = {strAdd:0, dexAdd:0, intAdd:0, lukAdd:0, watkAdd:0, matkAdd:0};
    for (var lv = 1; lv <= star; lv++) {
        var lvlCfg = MedalEnhanceManager.getLevel(selectedConfig.id, lv);
        if (lvlCfg) {
            if (lvlCfg.strAdd > 0) acc.strAdd += lvlCfg.strAdd;
            if (lvlCfg.dexAdd > 0) acc.dexAdd += lvlCfg.dexAdd;
            if (lvlCfg.intAdd > 0) acc.intAdd += lvlCfg.intAdd;
            if (lvlCfg.lukAdd > 0) acc.lukAdd += lvlCfg.lukAdd;
            if (lvlCfg.watkAdd > 0) acc.watkAdd += lvlCfg.watkAdd;
            if (lvlCfg.matkAdd > 0) acc.matkAdd += lvlCfg.matkAdd;
        }
    }
    if (acc.strAdd > 0) parts.push("力量+" + acc.strAdd);
    if (acc.dexAdd > 0) parts.push("敏捷+" + acc.dexAdd);
    if (acc.intAdd > 0) parts.push("智力+" + acc.intAdd);
    if (acc.lukAdd > 0) parts.push("运气+" + acc.lukAdd);
    if (acc.watkAdd > 0) parts.push("物攻+" + acc.watkAdd);
    if (acc.matkAdd > 0) parts.push("魔攻+" + acc.matkAdd);
    return parts.length > 0 ? parts.join(", ") : "";
}

// 判断是否为勋章（itemId在勋章ID范围内）
function isMedal(itemId) {
    // 勋章物品ID范围: 1142000~1142999 (GMS v83勋章)
    return itemId >= 1142000 && itemId <= 1143000;
}
