/*
 * ==================
 * 脚本类型: 装备强化
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 通过NPC对指定装备进行强化（可配置多件装备）
 *   2. 强化数据从数据库 equip_enhance_config / _level / _cost 表读取
 *   3. 支持唯一限制（背包/仓库存在已强化装备时不可强化第二件）
 *   4. 强化属性直接写入装备，持久有效
 *   5. 管理员通过Web后台维护强化配置
 * ==================
 */

var EquipEnhanceManager = Java.type('org.gms.config.EquipEnhanceManager');
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

var selectedConfig = null;      // EquipEnhanceConfigDO
var selectedItem = null;        // 玩家背包中的Equip对象
var selectedSlot = -1;
var selectedInvType = null;     // InventoryType
var currentStar = 0;
var enhanceData = {};

// 临时存储（跨函数传递列表）
var _configList = [];
var _slotMap = [];

// ===== 数据持久化 =====

function loadEnhanceData() {
    var raw = cm.getCharacterExtendValue("equipEnhance");
    if (raw && raw !== "") {
        try { enhanceData = JSON.parse(raw); } catch (e) { enhanceData = {}; }
    } else {
        enhanceData = {};
    }
}

function saveEnhanceData() {
    cm.saveOrUpdateCharacterExtendValue("equipEnhance", JSON.stringify(enhanceData));
}

function hasEnhancedItem(itemId) {
    for (var key in enhanceData) {
        if (enhanceData[key].itemId === itemId && enhanceData[key].level > 0) {
            return true;
        }
    }
    return false;
}

function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

// ===== 入口 =====

function start() {
    levelStart();
}

function levelStart() {
    loadEnhanceData();

    var text = "#e#b=== 装备强化系统 ===#k#n\r\n";
    text += "#r强化属性直接写入装备，永久有效#k\r\n\r\n";
    text += "请选择要强化的装备类型：\r\n";

    _configList = [];
    var configMap = EquipEnhanceManager.getConfigMap();
    // 如果缓存为空，尝试从数据库重新加载（自愈机制）
    if (configMap.isEmpty()) {
        EquipEnhanceManager.reload();
        configMap = EquipEnhanceManager.getConfigMap();
    }
    var itemIds = configMap.keySet().toArray();
    for (var i = 0; i < itemIds.length; i++) {
        var cfg = configMap.get(itemIds[i]);
        if (cfg && cfg.enabled == 1) {
            text += "#L" + _configList.length + "##b#i" + cfg.itemId + "# " + cfg.itemName + "#k";
            if (cfg.uniquePerChar == 1) {
                text += " #r[唯一]#k";
            }
            text += " (最高★" + cfg.maxEnhance + ")#l\r\n";
            _configList.push(cfg);
        }
    }

    if (_configList.length === 0) {
        cm.sendOk("当前没有可强化的装备配置，请联系管理员添加。");
        cm.dispose();
        return;
    }

    cm.sendNextSelectLevel("ConfigSelect", text);
}

function levelConfigSelect(selection) {
    if (selection < 0 || selection >= _configList.length) {
        cm.dispose();
        return;
    }
    selectedConfig = _configList[selection];

    // 唯一限制检查
    if (selectedConfig.uniquePerChar == 1 && hasEnhancedItem(selectedConfig.itemId)) {
        cm.sendOk("该装备已有一件强化版本，无法强化第二件。\r\n(唯一限制已开启)");
        cm.dispose();
        return;
    }

    showEquipSelection();
}

function showEquipSelection() {
    var text = "选择要强化的 #b" + selectedConfig.itemName + "#k：\r\n\r\n";
    _slotMap = [];
    var idx = 0;

    var invTypes = [InventoryType.EQUIPPED, InventoryType.EQUIP];

    for (var t = 0; t < invTypes.length; t++) {
        var inv = cm.getPlayer().getInventory(invTypes[t]);
        if (!inv) continue;
        var items = inv.list();
        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            if (item && isEquipment(item.getItemId()) && item.getItemId() == selectedConfig.itemId) {
                var slot = item.getPosition();
                var star = (enhanceData[String(slot)] && enhanceData[String(slot)].level) || 0;

                text += "#L" + idx + "#" + cm.getItemName(item.getItemId());
                text += " (槽位:" + slot + ")";
                if (star > 0) text += " #r★" + star + "#k";
                text += "#l\r\n";

                _slotMap.push({slot: slot, star: star, invType: invTypes[t]});
                idx++;
            }
        }
    }

    if (idx === 0) {
        cm.sendOk("你的背包和穿戴栏中没有 #b" + selectedConfig.itemName + "#k。");
        cm.dispose();
        return;
    }

    cm.sendNextSelectLevel("EquipSelect", text);
}

function levelEquipSelect(selection) {
    if (selection < 0 || selection >= _slotMap.length) {
        cm.dispose();
        return;
    }

    var info = _slotMap[selection];
    selectedSlot = info.slot;
    selectedInvType = info.invType;
    currentStar = info.star;

    // 在背包中定位装备对象
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

    if (!selectedItem || !isEquipment(selectedItem.getItemId())) {
        cm.sendOk("装备数据异常，请重试。");
        cm.dispose();
        return;
    }

    var maxLevel = selectedConfig.maxEnhance;
    if (currentStar >= maxLevel) {
        cm.sendOk("该装备已达最高强化等级 #r★" + maxLevel + "#k！");
        cm.dispose();
        return;
    }

    var nextStar = currentStar + 1;
    var levelCfg = EquipEnhanceManager.getLevel(selectedConfig.id, nextStar);
    if (!levelCfg) {
        cm.sendOk("强化配置异常：未找到等级 " + nextStar + " 的配置。");
        cm.dispose();
        return;
    }

    var costs = EquipEnhanceManager.getCosts(levelCfg.id);

    var text = "【强化确认】\r\n";
    text += "装备: #b" + cm.getItemName(selectedConfig.itemId) + "#k\r\n";
    text += "当前: " + (currentStar > 0 ? "★" + currentStar : "未强化") + "\r\n";
    text += "目标: #r★" + nextStar + "#k\r\n";
    text += "成功率: #b" + levelCfg.successRate + "%#k";

    if (levelCfg.destroyOnFail == 1) {
        text += " #r[失败销毁]#k";
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

    var nextStar = currentStar + 1;
    var levelCfg = EquipEnhanceManager.getLevel(selectedConfig.id, nextStar);
    if (!levelCfg) {
        cm.dispose();
        return;
    }
    var costs = EquipEnhanceManager.getCosts(levelCfg.id);

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

        var key = String(selectedSlot);
        if (!enhanceData[key]) {
            enhanceData[key] = {itemId: selectedConfig.itemId, level: 0};
        }
        enhanceData[key].level = nextStar;
        saveEnhanceData();

        var totalStats = getTotalStats(nextStar);
        cm.sendOk("强化成功！\r\n" + cm.getItemName(selectedConfig.itemId) + " → #r★" + nextStar + "#k\r\n"
            + "强化属性已写入装备！\r\n\r\n当前累计加成: " + totalStats);
    } else {
        if (levelCfg.destroyOnFail == 1) {
            cm.getPlayer().getInventory(selectedInvType).removeSlot(selectedSlot);
            var dkey = String(selectedSlot);
            if (enhanceData[dkey]) {
                delete enhanceData[dkey];
                saveEnhanceData();
            }
            cm.sendOk("强化失败！装备已销毁。\r\n材料已消耗。");
        } else {
            cm.sendOk("强化失败！装备保留，材料已消耗。\r\n可再次尝试强化。");
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
    // 逐项累计
    var acc = {strAdd:0, dexAdd:0, intAdd:0, lukAdd:0, watkAdd:0, matkAdd:0};
    for (var lv = 1; lv <= star; lv++) {
        var lvlCfg = EquipEnhanceManager.getLevel(selectedConfig.id, lv);
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
