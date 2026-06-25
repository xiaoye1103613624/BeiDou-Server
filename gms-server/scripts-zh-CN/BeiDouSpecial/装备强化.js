/*
 * ==================
 * 脚本类型: 装备强化 (status状态机模式)
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 通过NPC对指定装备进行多等级强化
 *   2. 强化数据从数据库读取，属性通过 safeAddStat() 安全叠加
 *   3. 支持唯一限制、成功率、失败销毁
 *   4. 星级文本与装备鉴定.js的词条文本共用 owner 字段做Tooltip展示（同一个自由文本行），
 *      写入前会先用 stripOldStarToken() 只摘掉旧的星级token，保留真实绑定名/词条文本，
 *      避免两个系统互相覆盖对方写进 owner 字段的内容
 * ==================
 */

/**
 * 从 owner 文本里只摘掉旧的星级token（"*" ~ "*****" 或 "N*"），
 * 真实绑定名/装备鉴定.js写的词条文本原样保留
 */
function stripOldStarToken(ownerText) {
    if (!ownerText) {
        return "";
    }
    var parts = ownerText.split(/\s+/).filter(function (p) { return p.length > 0; });
    var rest = [];
    for (var i = 0; i < parts.length; i++) {
        if (/^\d*\*$/.test(parts[i])) {
            continue;
        }
        rest.push(parts[i]);
    }
    return rest.join(" ");
}

var status = -1;
var EquipEnhanceManager, InventoryType, ItemInformationProvider;

// 选中数据
var selectedConfig = null;
var selectedItem = null;
var selectedSlot = -1;
var selectedInvType = null;
var currentStar = 0;
// 列表缓存
var _configList = [];
var _slotMap = [];

// ===== 入口 =====

function start() {
    status = -1;
    try {
        EquipEnhanceManager = Java.type('org.gms.config.EquipEnhanceManager');
        InventoryType = Java.type('org.gms.client.inventory.InventoryType');
        ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
    } catch (e) {
        cm.sendOk("装备强化系统初始化失败，请联系管理员。");
        cm.dispose();
        return;
    }
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    try {
        if (status === 0) {
            showEnhanceTypeMenu();
        } else if (status === 1) {
            handleTypeSelect(selection);
        } else if (status === 2) {
            handleEquipSelect(selection);
        } else if (status === 3) {
            handleEnhanceConfirm(selection);
        } else {
            cm.dispose();
        }
    } catch (e) {
        cm.sendOk("装备强化系统异常，请联系管理员。\r\n错误: " + e);
        cm.dispose();
    }
}

// ===== 数据持久化 =====

// 强化等级直接存在装备的 enhanceLevel 字段上，随 inventoryequipment 表持久化，无需额外 JSON 追踪

function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

// ===== 菜单：选择强化类型 =====

function showEnhanceTypeMenu() {
    _configList = [];
    // 直连数据库查询，绕过 GraalVM ClassLoader 隔离的 static 缓存问题
    var configs = EquipEnhanceManager.queryEnabledConfigs();
    for (var i = 0; i < configs.size(); i++) {
        _configList.push(configs.get(i));
    }

    if (_configList.length === 0) {
        cm.sendOk("当前没有可强化的装备配置。\r\n请通过Web管理后台 → 游戏管理 → 装备强化 添加配置。");
        cm.dispose();
        return;
    }

    var text = "#e#b=== 装备强化系统 ===#k#n\r\n";
    text += "#r强化属性直接写入装备，永久有效#k\r\n\r\n";
    text += "请选择要强化的装备类型：\r\n";

    for (var j = 0; j < _configList.length; j++) {
        var c = _configList[j];
        text += "#L" + j + "##b#i" + c.getItemId() + "# " + c.getItemName() + "#k";
        if (c.getUniquePerChar() != null && c.getUniquePerChar() == 1) {
            text += " #r[唯一]#k";
        }
        text += " (最高★" + c.getMaxEnhance() + ")#l\r\n";
    }

    cm.sendSimple(text);
}

function handleTypeSelect(selection) {
    if (selection < 0 || selection >= _configList.length) {
        cm.dispose();
        return;
    }
    selectedConfig = _configList[selection];

    // 唯一限制检查
    if (selectedConfig.getUniquePerChar() != null && selectedConfig.getUniquePerChar() == 1) {
        if (hasEnhancedItem(selectedConfig.getItemId())) {
            cm.sendOk("该装备已有一件强化版本，无法强化第二件。\r\n(唯一限制已开启)");
            cm.dispose();
            return;
        }
    }

    showEquipList();
}

/**
 * 检查指定物品ID是否已有强化过的装备（扫描背包和身上）
 */
function hasEnhancedItem(itemId) {
    var invTypes = [InventoryType.EQUIP, InventoryType.EQUIPPED];
    for (var t = 0; t < invTypes.length; t++) {
        var inv = cm.getPlayer().getInventory(invTypes[t]);
        if (inv == null) continue;
        var iter = inv.list().iterator();
        while (iter.hasNext()) {
            var it = iter.next();
            if (it != null && it.getItemId() == itemId && it.getEnhanceLevel() > 0) {
                return true;
            }
        }
    }
    return false;
}

// ===== 菜单：选择具体装备 =====

function showEquipList() {
    _slotMap = [];
    var idx = 0;
    var targetId = selectedConfig.getItemId();
    var maxLevel = selectedConfig.getMaxEnhance();
    var invTypes = [InventoryType.EQUIP];

    var text = "选择要强化的 #b" + selectedConfig.getItemName() + "#k：\r\n";
    text += "(仅背包中的装备，穿在身上的请先脱下来再强化)\r\n\r\n";

    // 先收集所有符合条件的装备
    var candidates = [];
    for (var t = 0; t < invTypes.length; t++) {
        var inv = cm.getPlayer().getInventory(invTypes[t]);
        if (inv == null) continue;
        var itemsIter = inv.list().iterator();
        while (itemsIter.hasNext()) {
            var item = itemsIter.next();
            if (item != null && isEquipment(item.getItemId()) && item.getItemId() == targetId) {
                // 直接从装备属性读取强化等级（随装备持久化到 inventoryequipment 表）
                var star = item.getEnhanceLevel();
                if (star >= maxLevel) continue;
                candidates.push({slot: item.getPosition(), star: star, invType: invTypes[t], equip: item});
            }
        }
    }

    if (candidates.length === 0) {
        cm.sendOk("背包中没有可强化的 #b" + selectedConfig.getItemName() + "#k。\r\n(已达最大强化等级 #r★" + maxLevel + "#k 或无该装备)");
        cm.dispose();
        return;
    }

    // 按槽位升序排列（越靠前的格子越先显示）
    candidates.sort(function(a, b) { return a.slot - b.slot; });

    for (var j = 0; j < candidates.length; j++) {
        var c = candidates[j];
        text += "#L" + j + "#" + ItemInformationProvider.getInstance().getName(targetId);
        text += " (槽位:" + c.slot + ")";
        if (c.star > 0) text += " #r★" + c.star + "#k";
        text += "#l\r\n";
        _slotMap.push(c);
    }

    cm.sendSimple(text);
}

function handleEquipSelect(selection) {
    if (selection < 0 || selection >= _slotMap.length) {
        cm.dispose();
        return;
    }

    var info = _slotMap[selection];
    selectedSlot = info.slot;
    selectedInvType = info.invType;
    currentStar = info.star;
    selectedItem = info.equip;

    if (selectedItem == null || !isEquipment(selectedItem.getItemId())) {
        cm.sendOk("装备数据异常，请重试。");
        cm.dispose();
        return;
    }

    var maxLevel = selectedConfig.getMaxEnhance();
    if (currentStar >= maxLevel) {
        cm.sendOk("已到达最终强化等级 #r★" + maxLevel + "#k！");
        cm.dispose();
        return;
    }

    var nextStar = currentStar + 1;
    var levelCfg = EquipEnhanceManager.queryLevel(selectedConfig.getId(), nextStar);
    if (levelCfg == null) {
        cm.sendOk("已到达最终强化等级 #r★" + currentStar + "#k！");
        cm.dispose();
        return;
    }

    var costs = EquipEnhanceManager.queryCosts(levelCfg.getId());

    // 构建确认文本
    var text = "【强化确认】\r\n";
    text += "装备: #b" + ItemInformationProvider.getInstance().getName(selectedConfig.getItemId()) + "#k\r\n";
    text += "当前: " + (currentStar > 0 ? "★" + currentStar : "未强化") + "\r\n";
    text += "目标: #r★" + nextStar + "#k\r\n";
    text += "成功率: #b" + levelCfg.getSuccessRate() + "%#k";

    if (levelCfg.getDestroyOnFail() != null && levelCfg.getDestroyOnFail() == 1) {
        text += " #r[失败销毁]#k";
    }
    text += "\r\n";

    if (levelCfg.getMesoCost() > 0) {
        text += "金币消耗: #b" + levelCfg.getMesoCost().toLocaleString() + " 金币#k\r\n";
    }

    for (var c = 0; c < costs.size(); c++) {
        var co = costs.get(c);
        text += "消耗: #i" + co.getItemId() + "# ×" + co.getCount() + "\r\n";
    }

    // 属性加成预览
    text += "\r\n属性加成预览(#r仅本次强化#k):\r\n";
    var stats = [];
    if (levelCfg.getStrAdd() > 0) stats.push("力量+" + levelCfg.getStrAdd());
    if (levelCfg.getDexAdd() > 0) stats.push("敏捷+" + levelCfg.getDexAdd());
    if (levelCfg.getIntAdd() > 0) stats.push("智力+" + levelCfg.getIntAdd());
    if (levelCfg.getLukAdd() > 0) stats.push("运气+" + levelCfg.getLukAdd());
    if (levelCfg.getHpAdd() > 0) stats.push("HP+" + levelCfg.getHpAdd());
    if (levelCfg.getMpAdd() > 0) stats.push("MP+" + levelCfg.getMpAdd());
    if (levelCfg.getWatkAdd() > 0) stats.push("物攻+" + levelCfg.getWatkAdd());
    if (levelCfg.getMatkAdd() > 0) stats.push("魔攻+" + levelCfg.getMatkAdd());
    if (levelCfg.getWdefAdd() > 0) stats.push("物防+" + levelCfg.getWdefAdd());
    if (levelCfg.getMdefAdd() > 0) stats.push("魔防+" + levelCfg.getMdefAdd());
    if (levelCfg.getAccAdd() > 0) stats.push("命中+" + levelCfg.getAccAdd());
    if (levelCfg.getAvoidAdd() > 0) stats.push("回避+" + levelCfg.getAvoidAdd());
    if (levelCfg.getSpeedAdd() > 0) stats.push("速度+" + levelCfg.getSpeedAdd());
    if (levelCfg.getJumpAdd() > 0) stats.push("跳跃+" + levelCfg.getJumpAdd());
    text += stats.length > 0 ? stats.join(", ") : "无属性加成";

    text += "\r\n\r\n#L0#确认强化#l\r\n#L1#取消#l";
    cm.sendSimple(text);
}

function handleEnhanceConfirm(selection) {
    if (selection !== 0) {
        cm.dispose();
        return;
    }

    var nextStar = currentStar + 1;
    var levelCfg = EquipEnhanceManager.queryLevel(selectedConfig.getId(), nextStar);
    if (levelCfg == null) {
        cm.dispose();
        return;
    }
    var costs = EquipEnhanceManager.queryCosts(levelCfg.getId());

    // 检查金币
    if (levelCfg.getMesoCost() > 0 && cm.getPlayer().getMeso() < levelCfg.getMesoCost()) {
        cm.sendOk("金币不足！需要 #r" + levelCfg.getMesoCost().toLocaleString() + " 金币#k。");
        cm.dispose();
        return;
    }

    // 检查道具
    for (var c = 0; c < costs.size(); c++) {
        var co = costs.get(c);
        if (!cm.haveItem(co.getItemId(), co.getCount())) {
            cm.sendOk("材料不足！#i" + co.getItemId() + "# 需要 ×" + co.getCount());
            cm.dispose();
            return;
        }
    }

    // 扣除金币
    if (levelCfg.getMesoCost() > 0) {
        cm.gainMeso(-levelCfg.getMesoCost());
    }

    // 扣除道具
    for (var cc = 0; cc < costs.size(); cc++) {
        var cco = costs.get(cc);
        cm.gainItem(cco.getItemId(), -cco.getCount());
    }

    // 成功判定
    var roll = Math.floor(Math.random() * 100);
    if (roll < levelCfg.getSuccessRate()) {
        // 安全叠加属性
        applyStatsSafe(selectedItem, levelCfg);
        // 强化等级直接写入装备对象（随 inventoryequipment 表持久化）
        selectedItem.setEnhanceLevel(nextStar);
        // owner 纯展示：显示星星，超5星改为"数字*"格式防止超长
        // 注意：用ASCII的"*"而不是Unicode的"★"，因为v83客户端字体子集里没有★的字形，
        // 显示会变成占位乱码（即使服务端GBK编码本身是对的）
        var starStr;
        if (nextStar <= 0) {
            starStr = "";
        } else if (nextStar <= 5) {
            starStr = "";
            for (var s = 0; s < nextStar; s++) {
                starStr += "*";
            }
        } else {
            starStr = nextStar + "*";
        }
        // 只替换星级token，保留 owner 字段里可能已有的真实绑定名/装备鉴定.js写的词条文本
        var restOwnerText = stripOldStarToken(selectedItem.getOwner());
        selectedItem.setOwner(starStr ? (restOwnerText ? starStr + " " + restOwnerText : starStr) : restOwnerText);
        // 通知客户端刷新装备属性显示
        cm.getPlayer().forceUpdateItem(selectedItem);

        cm.sendOk("强化成功！\r\n" + ItemInformationProvider.getInstance().getName(selectedConfig.getItemId()) + " → #r★" + nextStar + "#k\r\n强化属性已写入装备！");
    } else {
        if (levelCfg.getDestroyOnFail() != null && levelCfg.getDestroyOnFail() == 1) {
            cm.getPlayer().getInventory(selectedInvType).removeSlot(selectedSlot);
            cm.sendOk("强化失败！装备已销毁。\r\n材料已消耗。");
        } else {
            cm.sendOk("强化失败！装备保留，材料已消耗。\r\n可再次尝试强化。");
        }
    }
    cm.dispose();
}

// ===== 安全属性叠加（使用 safeAddStat 防止溢出） =====

function applyStatsSafe(equip, cfg) {
    if (cfg.getStrAdd() > 0) equip.setStr(EquipEnhanceManager.safeAddStat(equip.getStr(), cfg.getStrAdd()));
    if (cfg.getDexAdd() > 0) equip.setDex(EquipEnhanceManager.safeAddStat(equip.getDex(), cfg.getDexAdd()));
    if (cfg.getIntAdd() > 0) equip.setInt(EquipEnhanceManager.safeAddStat(equip.getInt(), cfg.getIntAdd()));
    if (cfg.getLukAdd() > 0) equip.setLuk(EquipEnhanceManager.safeAddStat(equip.getLuk(), cfg.getLukAdd()));
    if (cfg.getHpAdd() > 0) equip.setHp(EquipEnhanceManager.safeAddStat(equip.getHp(), cfg.getHpAdd()));
    if (cfg.getMpAdd() > 0) equip.setMp(EquipEnhanceManager.safeAddStat(equip.getMp(), cfg.getMpAdd()));
    if (cfg.getWatkAdd() > 0) equip.setWatk(EquipEnhanceManager.safeAddStat(equip.getWatk(), cfg.getWatkAdd()));
    if (cfg.getMatkAdd() > 0) equip.setMatk(EquipEnhanceManager.safeAddStat(equip.getMatk(), cfg.getMatkAdd()));
    if (cfg.getWdefAdd() > 0) equip.setWdef(EquipEnhanceManager.safeAddStat(equip.getWdef(), cfg.getWdefAdd()));
    if (cfg.getMdefAdd() > 0) equip.setMdef(EquipEnhanceManager.safeAddStat(equip.getMdef(), cfg.getMdefAdd()));
    if (cfg.getAccAdd() > 0) equip.setAcc(EquipEnhanceManager.safeAddStat(equip.getAcc(), cfg.getAccAdd()));
    if (cfg.getAvoidAdd() > 0) equip.setAvoid(EquipEnhanceManager.safeAddStat(equip.getAvoid(), cfg.getAvoidAdd()));
    if (cfg.getSpeedAdd() > 0) equip.setSpeed(EquipEnhanceManager.safeAddStat(equip.getSpeed(), cfg.getSpeedAdd()));
    if (cfg.getJumpAdd() > 0) equip.setJump(EquipEnhanceManager.safeAddStat(equip.getJump(), cfg.getJumpAdd()));
}
