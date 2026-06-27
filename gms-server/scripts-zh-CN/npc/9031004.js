/**
 * @description 宝石系统：1.宝石合成 2.宝石镶嵌
 * 1. 宝石合成：消耗2个N级宝石合成1个N+1级宝石，1~16级，逐级翻倍（与"母矿合成"思路一致）。
 *    宝石等级/物品ID/合成金币消耗统一存储在 xy_gem_level 配置表（默认 enabled=0，数据确认后改为1）。
 * 2. 宝石镶嵌：消耗对应等级宝石 + 属性水晶 + 金币，为身上已穿戴的装备永久叠加属性
 *    （直接写入装备的力量/敏捷/智力/运气/物攻/魔攻字段，随 inventoryequipment 表持久化）。
 *    叠加值通过 EquipEnhanceManager.safeAddStat 钳制到 [0,32767]，防止 short 溢出。
 *    消耗配置统一存储在 xy_gem_socket_level 配置表（默认 enabled=0，数据确认后改为1）。
 * 3. 建议先完成装备强化再来镶嵌：本脚本不限制顺序，仅做提示，具体规则待后续确认。
 */

var GemManager = Java.type("org.gms.config.GemManager");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");

var synthesisCache = [];
var socketCache = [];
var pendingSynthesis = null;
var pendingSocket = null; // {equip, slot, gemLevelCfg, socketCfg}

// ==================== 入口 ====================

function start() {
    levelMain();
}

function levelMain() {
    var text = "#e宝石系统#n\r\n\r\n"
        + "#L0#宝石合成（2个低级宝石合成1个高级宝石）#l\r\n"
        + "#L1#宝石镶嵌（消耗宝石+属性水晶为装备永久加属性）#l\r\n"
        + "#L9#离开#l";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection === 0) {
        levelSynthesisMenu();
    } else if (selection === 1) {
        levelSocketEquipMenu();
    } else {
        cm.dispose();
    }
}

// ==================== 宝石合成 ====================

function levelSynthesisMenu() {
    synthesisCache = [];
    var levels = GemManager.queryEnabledGemLevels();
    var text = "#e宝石合成#n\r\n（消耗2个同等级宝石，合成1个高一级宝石）\r\n\r\n";

    for (var i = 0; i < levels.size(); i++) {
        var cur = levels.get(i);
        var next = GemManager.queryGemLevel(cur.getGemLevel() + 1);
        if (next == null) continue; // 已是最高级或下一级未启用，不可合成

        var have = cm.getPlayer().getItemQuantity(cur.getItemId(), false);
        var meso = cur.getSynthesisMesoCost();
        var idx = synthesisCache.length;
        text += "#L" + idx + "##i" + cur.getItemId() + "##t" + cur.getItemId() + "##k x2 → #i" + next.getItemId() + "##t" + next.getItemId() + "##k x1";
        if (meso > 0) text += "（金币:" + meso + "）";
        text += "　当前持有:" + have + "#l\r\n";
        synthesisCache.push({ from: cur, to: next });
    }

    if (synthesisCache.length === 0) {
        text += "暂无可合成的宝石（数据待定，敬请期待）。\r\n\r\n";
    }
    text += "\r\n#L9#返回#l";
    cm.sendNextSelectLevel("HandleSynthesis", text);
}

function levelHandleSynthesis(selection) {
    if (selection < 0 || selection >= synthesisCache.length) {
        levelMain();
        return;
    }
    pendingSynthesis = synthesisCache[selection];
    var from = pendingSynthesis.from;
    var to = pendingSynthesis.to;

    var text = "是否消耗 #b#i" + from.getItemId() + "##t" + from.getItemId() + "##k x2 + 金币" + from.getSynthesisMesoCost()
        + " 合成 #b#i" + to.getItemId() + "##t" + to.getItemId() + "##k x1？";
    cm.sendYesNoLevel("SynthesisMenu", "DoSynthesis", text);
}

function levelDoSynthesis() {
    var pair = pendingSynthesis;
    pendingSynthesis = null;
    if (pair == null) {
        cm.dispose();
        return;
    }
    var from = pair.from;
    var to = pair.to;

    if (!cm.haveItem(from.getItemId(), 2)) {
        cm.sendOkLevel("Main", "宝石数量不足，无法合成。");
        return;
    }
    if (cm.getMeso() < from.getSynthesisMesoCost()) {
        cm.sendOkLevel("Main", "金币不足，无法合成。");
        return;
    }
    if (!cm.canHold(to.getItemId(), 1)) {
        cm.sendOkLevel("Main", "背包空间不足，无法合成。");
        return;
    }

    cm.gainItem(from.getItemId(), -2);
    if (from.getSynthesisMesoCost() > 0) {
        cm.gainMeso(-from.getSynthesisMesoCost());
    }
    cm.gainItem(to.getItemId(), 1);

    cm.sendOkLevel("Main", "合成成功！获得 #i" + to.getItemId() + "##t" + to.getItemId() + "# x1");
}

// ==================== 宝石镶嵌 ====================

function levelSocketEquipMenu() {
    socketCache = [];
    var text = "#e宝石镶嵌#n\r\n#r建议先完成装备强化再来镶嵌#k\r\n\r\n请选择身上已穿戴的装备：\r\n\r\n";

    var inv = cm.getPlayer().getInventory(InventoryType.EQUIPPED);
    var iter = inv.list().iterator();
    var idx = 0;
    while (iter.hasNext()) {
        var item = iter.next();
        if (item == null) continue;
        var name = ItemInformationProvider.getInstance().getName(item.getItemId());
        text += "#L" + idx + "#" + name + "#l\r\n";
        socketCache.push({ equip: item, slot: item.getPosition() });
        idx++;
    }

    if (socketCache.length === 0) {
        text += "身上没有已穿戴的装备。\r\n\r\n";
    }
    text += "\r\n#L9#返回#l";
    cm.sendNextSelectLevel("HandleSocketEquip", text);
}

function levelHandleSocketEquip(selection) {
    if (selection < 0 || selection >= socketCache.length) {
        levelMain();
        return;
    }
    pendingSocket = { equip: socketCache[selection].equip, slot: socketCache[selection].slot };
    levelSocketGemMenu();
}

function levelSocketGemMenu() {
    var levels = GemManager.queryEnabledGemLevels();
    var text = "#e选择用于镶嵌的宝石#n\r\n\r\n";
    var idx = 0;
    var cache = [];

    for (var i = 0; i < levels.size(); i++) {
        var lv = levels.get(i);
        var socketCfg = GemManager.querySocketLevel(lv.getGemLevel());
        if (socketCfg == null) continue; // 该等级宝石未配置镶嵌效果

        var have = cm.getPlayer().getItemQuantity(lv.getItemId(), false);
        text += "#L" + idx + "##i" + lv.getItemId() + "##t" + lv.getItemId() + "##k（持有:" + have + "）#l\r\n";
        cache.push({ gemLevel: lv, socketCfg: socketCfg });
        idx++;
    }

    if (cache.length === 0) {
        text += "暂无可用于镶嵌的宝石（数据待定，敬请期待）。\r\n\r\n";
    }
    text += "\r\n#L9#返回#l";
    socketCache = cache; // 复用变量名存放本级缓存（与装备选择列表互斥使用）
    cm.sendNextSelectLevel("HandleSocketGem", text);
}

function levelHandleSocketGem(selection) {
    if (selection < 0 || selection >= socketCache.length) {
        levelSocketEquipMenu();
        return;
    }
    var pick = socketCache[selection];
    pendingSocket.gemLevelCfg = pick.gemLevel;
    pendingSocket.socketCfg = pick.socketCfg;
    levelConfirmSocket();
}

function levelConfirmSocket() {
    var cfg = pendingSocket.socketCfg;
    var text = "本次镶嵌将消耗：\r\n"
        + "#i" + pendingSocket.gemLevelCfg.getItemId() + "##t" + pendingSocket.gemLevelCfg.getItemId() + "# x1\r\n"
        + "#i" + cfg.getCrystalItemId() + "##t" + cfg.getCrystalItemId() + "# x" + cfg.getCrystalCount() + "\r\n"
        + "金币 x" + cfg.getMesoCost() + "\r\n\r\n"
        + "本次提升的属性：\r\n";

    var stats = [];
    if (cfg.getStrAdd() > 0) stats.push("力量+" + cfg.getStrAdd());
    if (cfg.getDexAdd() > 0) stats.push("敏捷+" + cfg.getDexAdd());
    if (cfg.getIntAdd() > 0) stats.push("智力+" + cfg.getIntAdd());
    if (cfg.getLukAdd() > 0) stats.push("运气+" + cfg.getLukAdd());
    if (cfg.getWatkAdd() > 0) stats.push("物攻+" + cfg.getWatkAdd());
    if (cfg.getMatkAdd() > 0) stats.push("魔攻+" + cfg.getMatkAdd());
    text += (stats.length > 0 ? stats.join(", ") : "无") + "\r\n\r\n是否继续提升？";

    cm.sendYesNoLevel("SocketEquipMenu", "DoSocket", text);
}

function levelDoSocket() {
    var equip = pendingSocket.equip;
    var gemLevelCfg = pendingSocket.gemLevelCfg;
    var cfg = pendingSocket.socketCfg;
    pendingSocket = null;

    if (!cm.haveItem(gemLevelCfg.getItemId(), 1)) {
        cm.sendOkLevel("Main", "宝石不足，无法镶嵌。");
        return;
    }
    if (!cm.haveItem(cfg.getCrystalItemId(), cfg.getCrystalCount())) {
        cm.sendOkLevel("Main", "属性水晶不足，无法镶嵌。");
        return;
    }
    if (cm.getMeso() < cfg.getMesoCost()) {
        cm.sendOkLevel("Main", "金币不足，无法镶嵌。");
        return;
    }

    cm.gainItem(gemLevelCfg.getItemId(), -1);
    cm.gainItem(cfg.getCrystalItemId(), -cfg.getCrystalCount());
    if (cfg.getMesoCost() > 0) {
        cm.gainMeso(-cfg.getMesoCost());
    }

    GemManager.applySocketStats(equip, cfg);
    cm.getPlayer().forceUpdateItem(equip);

    cm.sendOkLevel("Main", "镶嵌成功！属性已永久写入装备。");
}
