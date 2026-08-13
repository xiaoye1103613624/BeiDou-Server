// 匠人街 · 装备强化子脚本（通过 9031012 装备强化大师菜单进入）
// 星之力 / 潜能 / 附加潜能 / 灵魂宝珠 / 星岩 / 白金锤
// 各子系统Java后端已实现，本脚本负责UI交互+消耗校验+调用Java API

var PotentialHyperService = Java.type("org.gms.potential.PotentialHyperService");
var PotentialHyperConfig = Java.type("org.gms.potential.PotentialHyperConfig");
var HyperEnhanceTable = Java.type("org.gms.potential.HyperEnhanceTable");
var PotentialRules095 = Java.type("org.gms.potential.PotentialRules095");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ModifyInventory = Java.type("org.gms.client.inventory.ModifyInventory");
var PacketCreator = Java.type("org.gms.util.PacketCreator");

var status = -1;
var mode = 0;          // 0主菜单 1星之力 2潜能 3附加潜能 4灵魂 5星岩
var selectedSlot = -1; // 选中的背包槽位
var equipObj = null;   // Equip对象

function start() {
    status = -1;
    mode = 0;
    selectedSlot = -1;
    equipObj = null;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (this.mode === 0) {
        handleMain(selection);
    } else if (this.mode === 1) {
        handleStarForce(selection);
    } else if (this.mode === 2) {
        handlePotential(selection);
    } else if (this.mode === 3) {
        handleBonusPotential(selection);
    } else if (this.mode === 4) {
        handleSoul(selection);
    } else if (this.mode === 5) {
        handleSocket(selection);
    } else {
        cm.dispose();
    }
}

// ==================== 主菜单 ====================

function handleMain(selection) {
    if (status === 0) {
        var t = "#e#b<装备强化中心>#k#n\r\n\r\n";
        t += "欢迎！这里可以全方位强化你的装备。\r\n";
        t += "强化材料可在 #b材料商人#k 和 #b副本挑战#k 中获取。\r\n\r\n";
        t += "#L1##b⭐ 星之力强化#k - 消耗强化卷轴提升装备星级(★1~10)#l\r\n";
        t += "#L2##b🔮 潜能管理#k - 附加/鉴定/重随主潜能#l\r\n";
        t += "#L3##b✨ 附加潜能#k - 附加额外潜能属性#l\r\n";
        t += "#L4##b💎 灵魂宝珠#k - 给武器注入灵魂之力#l\r\n";
        t += "#L5##b🔩 星岩镶嵌#k - 镶嵌星岩提升属性#l\r\n\r\n";
        t += "#L0##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            return;
        }
        mode = selection;
        status = -1;
        action(1, 0, 0);
    }
}

// ==================== 星之力强化 ====================

function handleStarForce(selection) {
    if (status === 0) {
        var t = "#e#b<星之力强化>#k#n\r\n\r\n";
        t += "使用强化卷轴提升装备星级(★1~★10)。\r\n";
        t += "每星增加四维和攻击力，星级越高属性越多。\r\n";
        t += "#r注意：失败可能损坏装备！请使用保护符。#k\r\n\r\n";
        t += "请选择要强化的装备（背包装备栏）：\r\n";
        var equips = listEquipInventory();
        if (equips.length === 0) {
            t += "#r背包装备栏为空。#k\r\n";
        } else {
            for (var i = 0; i < equips.length; i++) {
                var e = equips[i];
                t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
                if (e.enhance > 0) t += " #b★" + e.enhance + "#k";
                t += "#l\r\n";
            }
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        selectedSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
        if (equipObj == null || !(equipObj instanceof Java.type("org.gms.client.inventory.Equip"))) {
            cm.sendOk("未找到该装备。");
            cm.dispose();
            return;
        }
        showStarForceDetail();
    } else if (status === 2) {
        if (selection === 0) {
            doStarForce();
        } else {
            cm.dispose();
        }
    }
}

function showStarForceDetail() {
    var eq = equipObj;
    var curStar = eq.getEnhance() & 0xFF;
    var maxStar = PotentialHyperConfig.MAX_ENHANCE;

    var t = "#e#b<星之力强化>#k#n\r\n\r\n";
    t += "装备：#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
    t += "当前星级：#b★" + curStar + "#k / 最高★" + maxStar + "\r\n";

    if (curStar >= maxStar) {
        t += "\r\n#r已达最高星级！#k\r\n";
        cm.sendOk(t);
        cm.dispose();
        return;
    }

    // 显示当前属性加成
    var allStat = HyperEnhanceTable.cumulativeAllStat(curStar);
    var atk = HyperEnhanceTable.cumulativeAtk(curStar, eq.getItemId());
    t += "当前加成：四维各+#b" + allStat + "#k";
    if (atk > 0) t += "，攻击/魔攻+#b" + atk + "#k";
    t += "\r\n\r\n";

    // 下星属性预览
    var nextAllStat = HyperEnhanceTable.allStatOnStar(curStar + 1);
    var nextAtk = HyperEnhanceTable.atkOnStar(curStar + 1, isWeapon(eq.getItemId()));
    t += "升★" + (curStar + 1) + "后额外增加：四维各+#b" + nextAllStat + "#k";
    if (nextAtk > 0) t += "，攻击/魔攻+#b" + nextAtk + "#k";
    t += "\r\n\r\n";

    // 成功率
    var rate = PotentialHyperConfig.getHyperSuccessRate(2049300, curStar); // T5卷
    t += "成功率：#b" + rate + "%#k\r\n";
    t += "消耗：#b2049300 强化卷轴×1 + 金币50W#k\r\n";
    if (PotentialHyperConfig.HYPER_DESTROY_ON_FAIL) {
        t += "#r⚠ 失败可能损坏装备！#k\r\n";
    }

    t += "\r\n#L0#确认强化#l\r\n#L1#返回#l";
    cm.sendSimple(t);
}

function doStarForce() {
    var eq = equipObj;
    var curStar = eq.getEnhance() & 0xFF;

    // 检查升级次数是否用完
    if (eq.getUpgradeSlots() > 0) {
        cm.sendOk("请先用完可升级次数（剩余" + eq.getUpgradeSlots() + "次），再进行星之力强化。\r\n提示：使用普通卷轴砸完次数后再来。");
        cm.dispose();
        return;
    }

    // 检查卷轴
    if (!cm.haveItem(2049300, 1)) {
        cm.sendOk("需要 #b#t2049300# ×1#k！\r\n请在材料商人处购买或通过副本获取。");
        cm.dispose();
        return;
    }

    // 检查金币
    if (cm.getMeso() < 500000) {
        cm.sendOk("需要金币 #b500,000#k！");
        cm.dispose();
        return;
    }

    // 扣材料
    cm.gainItem(2049300, -1);
    cm.gainMeso(-500000);

    // 调用Java强化
    var result = PotentialHyperService.applyHyperScroll(cm.getPlayer(), eq, 2049300, false);

    // 刷新装备
    refreshEquip(eq);

    if (result.toString() === "SUCCESS") {
        var newStar = eq.getEnhance() & 0xFF;
        cm.getPlayer().dropMessage(5, "【星之力】强化成功！当前 ★" + newStar);
        cm.sendOk("#b⭐ 强化成功！#k\r\n装备升至 #b★" + newStar + "#k\r\n" + formatStarBonus(eq));
    } else if (result.toString() === "FAIL") {
        cm.sendOk("#r强化失败……#k\r\n卷轴和金币已消耗，装备未损坏。");
    } else if (result.toString() === "CURSE") {
        cm.sendOk("#r💥 强化失败，装备已损坏！#k\r\n建议使用保护符后再尝试。");
    }
    cm.dispose();
}

// ==================== 潜能管理 ====================

function handlePotential(selection) {
    if (status === 0) {
        var t = "#e#b<潜能管理>#k#n\r\n\r\n";
        t += "主潜能系统：给装备附加随机属性词条。\r\n";
        t += "品阶：普通→稀有→史诗→独特→传说\r\n\r\n";
        t += "#L1#附加潜能卷(2049402)#l\r\n";
        t += "#L2#鉴定潜能(放大镜)#l\r\n";
        t += "#L3#神奇魔方重随(5062000)#l\r\n";
        t += "#L4#查看装备潜能#l\r\n\r\n";
        t += "#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        if (selection === 4) {
            listPotentialEquips();
            return;
        }
        selectedAction = selection;
        // 选装备
        var t = "请选择背包装备栏中的装备：\r\n";
        var equips = listEquipInventory();
        if (equips.length === 0) {
            t += "#r背包装备栏为空。#k\r\n";
        }
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.grade > 0) t += " [潜能" + gradeName(e.grade) + "]";
            t += "#l\r\n";
        }
        cm.sendSimple(t);
    } else if (status === 2) {
        selectedSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
        if (equipObj == null) { cm.dispose(); return; }

        if (selectedAction === 1) applyPotentialScroll();
        else if (selectedAction === 2) applyMagnify();
        else if (selectedAction === 3) applyMainCube();
        else cm.dispose();
    } else if (status === 3) {
        // 魔方类型选择（applyMainCube → sendSimple）
        if (selectedAction === 3) {
            doCube(selection);
        } else {
            cm.dispose();
        }
    }
}

var selectedAction = 0;
var pendingCubeId = 0;

function applyPotentialScroll() {
    if (!cm.haveItem(2049402, 1)) {
        cm.sendOk("需要 #b#t2049402# ×1#k！");
        cm.dispose();
        return;
    }
    var result = PotentialHyperService.applyPotentialScroll(cm.getPlayer(), equipObj, 2049402, false);
    cm.gainItem(2049402, -1);
    refreshEquip(equipObj);
    if (result.toString() === "SUCCESS") {
        cm.sendOk("#b潜能附加成功！#k\r\n装备已进入隐藏潜能状态，请使用放大镜鉴定。\r\n\r\n提示：可在材料商人处购买放大镜。");
    } else if (result.toString() === "FAIL") {
        cm.sendOk("#r潜能附加失败……#k");
    } else if (result.toString() === "CURSE") {
        cm.sendOk("#r💥 装备已损坏！#k");
    } else {
        cm.sendOk("#r该装备可能已有潜能。#k");
    }
    cm.dispose();
}

function applyMagnify() {
    // 自动选择合适等级的放大镜
    var reqLv = Java.type("org.gms.server.ItemInformationProvider").getInstance().getEquipLevelReq(equipObj.getItemId());
    var glassId = 2460003; // 默认特级
    if (PotentialHyperConfig.magnifyFitsEquipLevel(2460000, reqLv)) glassId = 2460000;
    else if (PotentialHyperConfig.magnifyFitsEquipLevel(2460001, reqLv)) glassId = 2460001;
    else if (PotentialHyperConfig.magnifyFitsEquipLevel(2460002, reqLv)) glassId = 2460002;

    if (!cm.haveItem(glassId, 1)) {
        cm.sendOk("需要放大镜 #b#t" + glassId + "##k！\r\n可在材料商人处购买。");
        cm.dispose();
        return;
    }
    cm.gainItem(glassId, -1);
    var result = PotentialHyperService.applyMagnify(cm.getPlayer(), equipObj, glassId);
    refreshEquip(equipObj);
    if (result.toString() === "SUCCESS") {
        var stats = PotentialHyperService.computeBonus(equipObj, cm.getPlayer().getLevel());
        cm.sendOk("#b鉴定成功！#k\r\n" + formatPotentialStats(stats));
    } else {
        cm.sendOk("#r鉴定失败。#k\r\n请确认装备有未鉴定的潜能。");
    }
    cm.dispose();
}

function applyMainCube() {
    // 选择魔方类型；下一轮 status===3 → doCube(selection)
    var t = "#e#b选择魔方类型#k#n\r\n\r\n";
    t += "#L0#神奇魔方(5062000) - 稀有~史诗，不可独特#l\r\n";
    t += "#L1#高级神奇魔方(5062001) - 可独特，不可传说#l\r\n";
    t += "#L2#超级神奇魔方(5062002) - 全品阶可用#l\r\n";
    cm.sendSimple(t);
}

function doCube(selection) {
    var cubeId = [5062000, 5062001, 5062002][selection];
    if (!cm.haveItem(cubeId, 1)) {
        cm.sendOk("需要 #b#t" + cubeId + "##k！");
        cm.dispose();
        return;
    }
    cm.gainItem(cubeId, -1);
    var result;
    if (selection === 0) result = PotentialHyperService.applyMainCube(cm.getPlayer(), equipObj, cubeId, false);
    else if (selection === 1) result = PotentialHyperService.applyPremiumCube(cm.getPlayer(), equipObj, cubeId, false);
    else result = PotentialHyperService.applySuperCube(cm.getPlayer(), equipObj, cubeId, false);
    refreshEquip(equipObj);
    if (result.toString() === "SUCCESS") {
        cm.sendOk("#b重随成功！#k\r\n" + PotentialHyperService.describe(equipObj));
    } else {
        cm.sendOk("#r重随失败。#k\r\n" + result);
    }
    cm.dispose();
}

function listPotentialEquips() {
    var t = "#e#b装备潜能一览#k#n\r\n\r\n";
    var found = false;
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIPPED);
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip")) {
            var d = PotentialHyperService.describe(item);
            if (d && d.length > 0) {
                t += "【穿戴】#t" + item.getItemId() + "# " + d + "\r\n";
                found = true;
            }
        }
    }
    if (!found) t += "暂无潜能装备。\r\n";
    t += "\r\n#L0#确定#l";
    cm.sendOk(t);
    cm.dispose();
}

// ==================== 附加潜能 ====================

function handleBonusPotential(selection) {
    if (status === 0) {
        var t = "#e#b<附加潜能>#k#n\r\n\r\n";
        t += "附加潜能：在已有主潜能的装备上额外附加1~3条属性。\r\n";
        t += "需要：主潜能已鉴定 + 附加潜能卷(2049902)\r\n\r\n";
        t += "请选择装备：\r\n";
        var equips = listEquipInventory();
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.bonusGrade > 0) t += " [附加" + gradeName(e.bonusGrade) + "]";
            t += "#l\r\n";
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        selectedSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
        if (!cm.haveItem(2049902, 1)) {
            cm.sendOk("需要 #b#t2049902# ×1#k！");
            cm.dispose();
            return;
        }
        cm.gainItem(2049902, -1);
        var result = PotentialHyperService.applyBonusPotentialScroll(cm.getPlayer(), equipObj, 2049902, false);
        refreshEquip(equipObj);
        if (result.toString() === "SUCCESS") {
            cm.sendOk("#b附加潜能成功！#k\r\n" + PotentialHyperService.describe(equipObj));
        } else {
            cm.sendOk("#r附加失败。#k\r\n请确认装备已有主潜能且已鉴定。");
        }
        cm.dispose();
    }
}

// ==================== 灵魂宝珠 ====================

function handleSoul(selection) {
    if (status === 0) {
        var t = "#e#b<灵魂宝珠>#k#n\r\n\r\n";
        t += "给武器注入灵魂之力，获得额外属性和华丽特效！\r\n\r\n";
        t += "#L1#灵魂附魔(2049914) - 给武器开槽/镶珠#l\r\n";
        t += "#L2#灵魂清除(2049915) - 清除武器灵魂#l\r\n";
        t += "#L3#查看当前灵魂#l\r\n\r\n";
        t += "#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        if (selection === 3) {
            listSoulEquips();
            return;
        }
        soulAction = selection;
        // 选武器
        var t = "请选择武器：\r\n";
        var equips = listEquipInventory();
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.soulId > 0) t += " [灵魂]";
            t += "#l\r\n";
        }
        cm.sendSimple(t);
    } else if (status === 2) {
        selectedSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
        if (soulAction === 1) {
            if (!cm.haveItem(2049914, 1)) { cm.sendOk("需要 #b#t2049914# ×1#k！"); cm.dispose(); return; }
            cm.gainItem(2049914, -1);
            var result = PotentialHyperService.applySoulScroll(cm.getPlayer(), equipObj, 2049914, false);
            refreshEquip(equipObj);
            if (result.toString() === "SUCCESS") {
                cm.sendOk("#b灵魂注入成功！#k\r\n" + PotentialHyperService.describe(equipObj));
            } else {
                cm.sendOk("#r注入失败。#k");
            }
            cm.dispose();
        } else if (soulAction === 2) {
            if (!cm.haveItem(2049915, 1)) { cm.sendOk("需要 #b#t2049915# ×1#k！"); cm.dispose(); return; }
            cm.gainItem(2049915, -1);
            PotentialHyperService.clearSoul(equipObj);
            refreshEquip(equipObj);
            cm.sendOk("#b灵魂已清除。#k");
            cm.dispose();
        }
    }
}

var soulAction = 0;

function listSoulEquips() {
    var t = "#e#b灵魂宝珠一览#k#n\r\n\r\n";
    var found = false;
    var inv = cm.getPlayer().getInventory(InventoryType.EQUIPPED);
    for (var i = 0; i < inv.list().size(); i++) {
        var item = inv.list().get(i);
        if (item instanceof Java.type("org.gms.client.inventory.Equip") && item.getSoulId() > 0) {
            t += "【" + (item.getPosition() < 0 ? "穿戴" : "背包") + "】#t" + item.getItemId() + "#\r\n";
            found = true;
        }
    }
    if (!found) t += "暂无灵魂装备。\r\n";
    cm.sendOk(t);
    cm.dispose();
}

// ==================== 星岩镶嵌 ====================

function handleSocket(selection) {
    if (status === 0) {
        var t = "#e#b<星岩镶嵌>#k#n\r\n\r\n";
        t += "给装备镶嵌星岩，获得额外属性。最多3孔。\r\n\r\n";
        t += "#L1#镶嵌星岩(2049913)#l\r\n";
        t += "#L2#清除星岩#l\r\n\r\n";
        t += "#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        socketAction = selection;
        var t = "请选择装备：\r\n";
        var equips = listEquipInventory();
        for (var i = 0; i < equips.length; i++) {
            var e = equips[i];
            t += "#L" + e.slot + "##v" + e.id + "# #t" + e.id + "#";
            if (e.socket1 > 0 || e.socket2 > 0 || e.socket3 > 0) t += " [星岩]";
            t += "#l\r\n";
        }
        cm.sendSimple(t);
    } else if (status === 2) {
        selectedSlot = selection;
        equipObj = cm.getPlayer().getInventory(InventoryType.EQUIP).getItem(selectedSlot);
        if (socketAction === 1) {
            if (!cm.haveItem(2049913, 1)) { cm.sendOk("需要 #b#t2049913# ×1#k！"); cm.dispose(); return; }
            cm.gainItem(2049913, -1);
            var result = PotentialHyperService.applySocketScroll(cm.getPlayer(), equipObj, 2049913, false);
            refreshEquip(equipObj);
            if (result.toString() === "SUCCESS") {
                cm.sendOk("#b星岩镶嵌成功！#k\r\n" + PotentialHyperService.describe(equipObj));
            } else {
                cm.sendOk("#r镶嵌失败。#k\r\n可能槽位已满或失败。");
            }
            cm.dispose();
        } else if (socketAction === 2) {
            PotentialHyperService.clearSocket(equipObj);
            refreshEquip(equipObj);
            cm.sendOk("#b星岩已清除。#k");
            cm.dispose();
        }
    }
}

var socketAction = 0;

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
                enhance: item.getEnhance() & 0xFF,
                grade: item.getPotentialGrade() & 0xFF,
                bonusGrade: item.getBonusPotentialGrade() & 0xFF,
                soulId: item.getSoulId(),
                socket1: item.getSocket1(),
                socket2: item.getSocket2(),
                socket3: item.getSocket3()
            });
        }
    }
    return result;
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

function isWeapon(itemId) {
    var ItemConstants = Java.type("org.gms.constants.inventory.ItemConstants");
    return ItemConstants.isWeapon(itemId);
}

function gradeName(g) {
    if (g === 1) return "普通";
    if (g === 2) return "稀有";
    if (g === 3) return "史诗";
    if (g === 4) return "独特";
    if (g === 5) return "传说";
    return "无";
}

function formatStarBonus(eq) {
    var s = eq.getEnhance() & 0xFF;
    var all = HyperEnhanceTable.cumulativeAllStat(s);
    var atk = HyperEnhanceTable.cumulativeAtk(s, eq.getItemId());
    return "累计加成：四维各+" + all + (atk > 0 ? "，攻击/魔攻+" + atk : "");
}

function formatPotentialStats(stats) {
    var parts = [];
    if (stats.str > 0) parts.push("力量+" + stats.str);
    if (stats.dex > 0) parts.push("敏捷+" + stats.dex);
    if (stats.inte > 0) parts.push("智力+" + stats.inte);
    if (stats.luk > 0) parts.push("运气+" + stats.luk);
    if (stats.watk > 0) parts.push("攻击+" + stats.watk);
    if (stats.matk > 0) parts.push("魔攻+" + stats.matk);
    if (stats.hp > 0) parts.push("HP+" + stats.hp);
    if (stats.strR > 0) parts.push("力量+" + stats.strR + "%");
    if (stats.dexR > 0) parts.push("敏捷+" + stats.dexR + "%");
    if (stats.intR > 0) parts.push("智力+" + stats.intR + "%");
    if (stats.lukR > 0) parts.push("运气+" + stats.lukR + "%");
    if (stats.bossDamR > 0) parts.push("Boss伤+" + stats.bossDamR + "%");
    if (stats.damR > 0) parts.push("伤害+" + stats.damR + "%");
    if (stats.ignoreDef > 0) parts.push("无视防御+" + stats.ignoreDef + "%");
    return parts.join("\r\n");
}

function backMain() {
    mode = 0;
    selectedSlot = -1;
    equipObj = null;
    status = -1;
    action(1, 0, 0);
}
