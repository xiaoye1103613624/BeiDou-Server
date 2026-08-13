// 匠人街 · 洗炼鉴定子脚本（通过 9031012 装备强化大师菜单进入）
// ①~⑤级16种词条鉴定/全洗/单行重洗/锁定
// Java层 ReforgeService + DB xy_reforge_affix

var ReforgeService = Java.type("org.gms.reforge.ReforgeService");
var ReforgeAffixMapper = Java.type("org.gms.dao.mapper.ReforgeAffixMapper");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ModifyInventory = Java.type("org.gms.client.inventory.ModifyInventory");
var PacketCreator = Java.type("org.gms.util.PacketCreator");

// 从Spring获取mapper（脚本通过全局变量注入或直接用静态方法）
var affixes = null; // 延迟加载

var status = -1;
var actionType = 0; // 0主菜单 1首次鉴定 2全洗 3单洗 4锁定 5查看
var selectedSlot = -1;
var equipObj = null;

function start() {
    status = -1;
    actionType = 0;
    selectedSlot = -1;
    equipObj = null;
    // 加载词条配置
    loadAffixes();
    action(1, 0, 0);
}

function loadAffixes() {
    if (affixes != null) return;
    try {
        // 通过Spring获取mapper（脚本上下文注入）
        var ctx = Java.type("org.gms.GMSApplication").getApplicationContext();
        if (ctx != null) {
            var mapper = ctx.getBean(ReforgeAffixMapper.class);
            var query = Java.type("com.mybatisflex.core.query.QueryWrapper").create().where("enabled = 1").orderBy("code", true);
            affixes = mapper.selectListByQuery(query);
        }
    } catch (e) {
        // Spring不可用时fallback：使用静态方法
    }
    if (affixes == null || affixes.isEmpty()) {
        affixes = Java.type("java.util.ArrayList")();
    }
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (actionType === 0) {
        handleMain(selection);
    } else if (actionType === 1) {
        handleAppraise(selection);
    } else if (actionType === 2) {
        handleRerollAll(selection);
    } else if (actionType === 3) {
        handleRerollLine(selection);
    } else if (actionType === 4) {
        handleLock(selection);
    } else if (actionType === 5) {
        handleView(selection);
    }
}

// ==================== 主菜单 ====================

function handleMain(selection) {
    if (status === 0) {
        if (affixes.isEmpty()) {
            cm.sendOk("洗炼词条配置未加载，请联系管理员。");
            cm.dispose();
            return;
        }
        var t = "#e#b<洗炼鉴定>#k#n\r\n\r\n";
        t += "为装备附加随机属性词条，从①~⑤级递增。\r\n";
        t += "16种词条类型：血/防/战/法/弓/侠/全/攻/魔/勇/慧/迅/刺/圣/仙/神\r\n";
        t += "血/防始终为①级（满阶段），其余①~⑤级。\r\n\r\n";
        t += "消耗：洗炼石 ×1 + 金币 50W\r\n\r\n";
        t += "#L1#首次鉴定（给装备附加1~3条词条）#l\r\n";
        t += "#L2#整类重洗（重新随机所有词条）#l\r\n";
        t += "#L3#单行重洗（仅重洗指定词条行）#l\r\n";
        t += "#L4#词条锁定/解锁#l\r\n";
        t += "#L5#查看装备洗炼详情#l\r\n\r\n";
        t += "#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { cm.dispose(); return; }
        actionType = selection;
        status = -1;
        if (selection <= 4) {
            // 需要选装备
            selectEquip();
        } else {
            action(1, 0, 0);
        }
    }
}

// ==================== 选装备 ====================

function selectEquip() {
    var t = "#e#b选择装备#k#n\r\n\r\n";
    t += "仅显示背包装备栏中的装备（请先将装备放入背包）。\r\n\r\n";
    var equips = listEquipInventory();
    if (equips.length === 0) {
        t += "#r背包装备栏为空。#k\r\n";
    } else {
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            // 显示洗炼状态
            var lines = ReforgeService.decodeLines(e.obj, affixes);
            var hasReforge = false;
            for (var j = 0; j < lines.size(); j++) {
                if (lines.get(j) != null) { hasReforge = true; break; }
            }
            if (hasReforge) t += " #b[已洗炼]#k";
            t += "#l\r\n";
        }
    }
    t += "\r\n#L9000##g返回#k#l";
    cm.sendSimple(t);
}

function onEquipSelected(selection) {
    if (selection === 9000) { backMain(); return; }
    selectedSlot = selection;
    equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
    if (equipObj == null) {
        cm.sendOk("未找到该装备。");
        cm.dispose();
        return;
    }
}

// ==================== 1. 首次鉴定 ====================

var pendingAppraiseSlot = -1;

function handleAppraise(selection) {
    if (status === 0) {
        selectEquip();
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        pendingAppraiseSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selection);
        if (equipObj == null) { cm.dispose(); return; }

        // 检查是否已有洗炼
        if (equipObj.getReforge1() > 0 || equipObj.getReforge2() > 0 || equipObj.getReforge3() > 0) {
            cm.sendOk("该装备已有洗炼词条！请使用整类重洗或单行重洗。\r\n如需重新鉴定请先清除。");
            cm.dispose();
            return;
        }

        // 成本确认
        var t = "#e#b首次鉴定确认#k#n\r\n\r\n";
        t += "装备：#v" + equipObj.getItemId() + "# #t" + equipObj.getItemId() + "#\r\n\r\n";
        t += "消耗：洗炼石 ×1 + 金币 500,000\r\n";
        t += "将随机获得 #b1~3条#k 词条（75%概率2条，15%概率3条，10%概率1条）\r\n\r\n";
        t += "词条类型与等级完全随机。\r\n";
        t += "#r血/防词条始终为①级（满阶段），不升至②~⑤。#k\r\n\r\n";
        t += "#L0#确认鉴定#l\r\n";
        cm.sendSimple(t);
    } else if (status === 2) {
        doFirstAppraise();
    }
}

function doFirstAppraise() {
    // 扣材料（洗炼石 = 4032171 装备之石 复用）
    if (!cm.haveItem(4032171, 1)) {
        cm.sendOk("需要 #b#t4032171# ×1#k（装备之石/洗炼石）！\r\n可在材料商人处购买。");
        cm.dispose();
        return;
    }
    if (cm.getMeso() < 500000) {
        cm.sendOk("需要金币 #b500,000#k！");
        cm.dispose();
        return;
    }
    cm.gainItem(4032171, -1);
    cm.gainMeso(-500000);

    ReforgeService.rollFirstTime(equipObj, affixes);
    refreshEquip(equipObj);

    var desc = ReforgeService.describe(equipObj, affixes);
    cm.getPlayer().dropMessage(5, "【洗炼】鉴定完成: " + desc);
    cm.sendOk("#b✨ 鉴定完成！#k\r\n\r\n" + formatLines(equipObj));
    cm.dispose();
}

// ==================== 2. 整类重洗 ====================

function handleRerollAll(selection) {
    if (status === 0) {
        selectEquip();
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selection);
        if (equipObj == null) { cm.dispose(); return; }

        if (equipObj.getReforge1() <= 0 && equipObj.getReforge2() <= 0 && equipObj.getReforge3() <= 0) {
            cm.sendOk("该装备尚未洗炼！请先进行首次鉴定。");
            cm.dispose();
            return;
        }

        var t = "#e#b整类重洗确认#k#n\r\n\r\n";
        t += "装备：#v" + equipObj.getItemId() + "# #t" + equipObj.getItemId() + "#\r\n";
        t += "当前词条：\r\n" + formatLines(equipObj) + "\r\n\r\n";
        t += "消耗：洗炼石 ×1 + 金币 500,000\r\n";
        t += "#r注意：锁定的词条不会被重洗。#k\r\n\r\n";
        t += "#L0#确认重洗#l\r\n";
        cm.sendSimple(t);
    } else if (status === 2) {
        if (!cm.haveItem(4032171, 1)) { cm.sendOk("需要 #b#t4032171# ×1#k！"); cm.dispose(); return; }
        if (cm.getMeso() < 500000) { cm.sendOk("需要金币 #b500,000#k！"); cm.dispose(); return; }
        cm.gainItem(4032171, -1);
        cm.gainMeso(-500000);

        ReforgeService.rerollAll(equipObj, affixes);
        refreshEquip(equipObj);

        var desc = ReforgeService.describe(equipObj, affixes);
        cm.getPlayer().dropMessage(5, "【洗炼】重洗完成: " + desc);
        cm.sendOk("#b重洗完成！#k\r\n\r\n" + formatLines(equipObj));
        cm.dispose();
    }
}

// ==================== 3. 单行重洗 ====================

function handleRerollLine(selection) {
    if (status === 0) {
        selectEquip();
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selection);
        if (equipObj == null) { cm.dispose(); return; }

        // 显示当前词条，选择要重洗的行
        var t = "#e#b选择要重洗的词条行#k#n\r\n\r\n";
        t += "当前词条：\r\n" + formatLines(equipObj) + "\r\n\r\n";
        t += "消耗：洗炼石 ×1 + 金币 500,000\r\n";
        t += "#r锁定的行不可重洗。#k\r\n\r\n";

        for (var i = 0; i < 3; i++) {
            var locked = ReforgeService.isLineLocked(equipObj, i);
            if (locked) {
                t += "#L" + i + "#行" + (i + 1) + "：🔒 已锁定（不可选）#l\r\n";
            } else {
                t += "#L" + i + "#行" + (i + 1) + "：重洗此行#l\r\n";
            }
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 2) {
        if (selection === 9000) { backMain(); return; }
        if (!cm.haveItem(4032171, 1)) { cm.sendOk("需要 #b#t4032171# ×1#k！"); cm.dispose(); return; }
        if (cm.getMeso() < 500000) { cm.sendOk("需要金币 #b500,000#k！"); cm.dispose(); return; }
        cm.gainItem(4032171, -1);
        cm.gainMeso(-500000);

        ReforgeService.rerollLine(equipObj, selection, affixes);
        refreshEquip(equipObj);
        cm.sendOk("#b单行重洗完成！#k\r\n\r\n" + formatLines(equipObj));
        cm.dispose();
    }
}

// ==================== 4. 锁定/解锁 ====================

function handleLock(selection) {
    if (status === 0) {
        selectEquip();
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selection);
        if (equipObj == null) { cm.dispose(); return; }

        var t = "#e#b词条锁定/解锁#k#n\r\n\r\n";
        t += "锁定后，该行在整类重洗时不会被改变。\r\n";
        t += "当前词条：\r\n" + formatLines(equipObj) + "\r\n\r\n";

        for (var i = 0; i < 3; i++) {
            var locked = ReforgeService.isLineLocked(equipObj, i);
            t += "#L" + i + "#行" + (i + 1) + "：" + (locked ? "🔒 已锁定 → #r解锁#k" : "🔓 未锁定 → #b锁定#k") + "#l\r\n";
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 2) {
        if (selection === 9000) { backMain(); return; }
        var currentLock = ReforgeService.isLineLocked(equipObj, selection);
        ReforgeService.setLineLock(equipObj, selection, !currentLock);
        refreshEquip(equipObj);
        cm.sendOk((!currentLock ? "#b已锁定#k" : "#r已解锁#k") + " 行" + (selection + 1) + "。");
        cm.dispose();
    }
}

// ==================== 5. 查看详情 ====================

function handleView(selection) {
    var t = "#e#b穿戴装备洗炼详情#k#n\r\n\r\n";
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIPPED);
    var found = false;
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip")) {
            var desc = ReforgeService.describe(item, affixes);
            if (desc != null && desc.length() > 0) {
                t += "【#t" + item.getItemId() + "#】\r\n" + formatLines(item) + "\r\n\r\n";
                found = true;
            }
        }
    }
    if (!found) t += "暂无洗炼装备。\r\n";
    cm.sendOk(t);
    cm.dispose();
}

// ==================== 工具函数 ====================

function listEquipInventory() {
    var result = [];
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip")) {
            result.push({
                slot: item.getPosition(),
                id: item.getItemId(),
                obj: item
            });
        }
    }
    return result;
}

function formatLines(equip) {
    var lines = ReforgeService.decodeLines(equip, affixes);
    var t = "";
    for (var i = 0; i < lines.size(); i++) {
        var line = lines.get(i);
        if (line == null) {
            t += "行" + (i + 1) + "：空\r\n";
            continue;
        }
        var locked = line.get("locked");
        t += "行" + (i + 1) + "：" + (locked ? "🔒 " : "") + line.get("displayName") + "\r\n";
        var stats = line.get("stats");
        if (stats != null) {
            t += "  ";
            var first = true;
            for (var iter = stats.entrySet().iterator(); iter.hasNext();) {
                var e = iter.next();
                if (!first) t += "，";
                t += e.getKey() + "+" + e.getValue();
                first = false;
            }
            t += "\r\n";
        }
    }
    return t;
}

function refreshEquip(eq) {
    var mods = new java.util.ArrayList();
    mods.add(new ModifyInventory(3, eq));
    mods.add(new ModifyInventory(0, eq));
    cm.getClient().sendPacket(PacketCreator.modifyInventory(true, mods));
    if (eq.getPosition() < 0) {
        cm.getPlayer().equipChanged();
    }
}

function backMain() {
    actionType = 0;
    selectedSlot = -1;
    equipObj = null;
    status = -1;
    action(1, 0, 0);
}
