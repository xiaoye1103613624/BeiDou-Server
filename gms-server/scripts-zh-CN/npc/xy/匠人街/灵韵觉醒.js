// 匠人街 · 铁砧 · 灵韵觉醒（完整版）
var SpiritAwakenService = Java.type('org.gms.spirit.SpiritAwakenService');
var SpiritAwakenConfig = Java.type('org.gms.spirit.SpiritAwakenConfig');
var ItemConstants = Java.type('org.gms.constants.inventory.ItemConstants');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

var status = -1;
var weaponSlots = [];
var selectedSlot = -1;
var modeAction = 0; // 0=觉醒 1=重置

function start() {
    status = -1;
    modeAction = 0;
    selectedSlot = -1;
    weaponSlots = [];
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || (mode === 0 && status <= 0)) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    if (status === 0) {
        var text = "#e#b<灵韵觉醒 · 铁砧>#k#n\r\n";
        text += "将灵韵注入武器，随机附加强力技能。\r\n";
        text += "觉醒消耗：#v" + SpiritAwakenConfig.COST_ITEM_ID + "# ×" + SpiritAwakenConfig.COST_ITEM_QTY;
        text += " + " + (SpiritAwakenConfig.COST_MESO / 10000) + "万金币\r\n";
        text += "成功率：#b" + SpiritAwakenConfig.SUCCESS_RATE + "%#k（失败不清除已有灵韵）\r\n";
        text += "仅限装备栏中 reqLevel≥" + SpiritAwakenConfig.MIN_WEAPON_REQ_LEVEL + " 的武器。\r\n";
        text += "#r不含影分身。交易获得的带灵韵武器会清空灵韵。#k\r\n\r\n";
        text += "#L0#开始觉醒#l\r\n";
        text += "#L1#查看背包武器灵韵#l\r\n";
        text += "#L2#清除灵韵（重置）#l\r\n";
        text += "#L3#概率与规则说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 3) {
            cm.sendOk(buildHelpText());
            cm.dispose();
            return;
        }
        if (selection === 1) {
            cm.sendOk(buildWeaponListText(false));
            cm.dispose();
            return;
        }
        modeAction = (selection === 2) ? 1 : 0;
        weaponSlots = collectWeaponSlots(modeAction === 1);
        if (weaponSlots.length === 0) {
            if (modeAction === 1) {
                cm.sendOk("装备栏中没有已附灵韵的武器。");
            } else {
                cm.sendOk("装备栏中没有可觉醒的武器。\r\n需要 reqLevel≥" + SpiritAwakenConfig.MIN_WEAPON_REQ_LEVEL + " 的非现金武器。");
            }
            cm.dispose();
            return;
        }
        var title = modeAction === 1 ? "请选择要清除灵韵的武器：\r\n" : "请选择要觉醒的武器：\r\n";
        cm.sendSimple(title + buildWeaponListText(true));
    } else if (status === 2) {
        if (selection < 0 || selection >= weaponSlots.length) {
            cm.dispose();
            return;
        }
        selectedSlot = weaponSlots[selection];
        var eq = cm.getInventory(1).getItem(selectedSlot);
        if (eq == null) {
            cm.sendOk("装备已变更，请重试。");
            cm.dispose();
            return;
        }
        var tip;
        if (modeAction === 1) {
            tip = "确认清除以下武器的灵韵？\r\n";
            tip += "#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
            tip += SpiritAwakenService.describeEquip(eq) + "\r\n\r\n";
            tip += "消耗：#v" + SpiritAwakenConfig.COST_ITEM_ID + "# ×" + SpiritAwakenConfig.RESET_COST_ITEM_QTY;
            tip += " + " + (SpiritAwakenConfig.RESET_COST_MESO / 10000) + "万金币";
        } else {
            tip = "确认对以下武器进行灵韵觉醒？\r\n";
            tip += "#v" + eq.getItemId() + "# #t" + eq.getItemId() + "#\r\n";
            tip += SpiritAwakenService.describeEquip(eq) + "\r\n\r\n";
            tip += "消耗：#v" + SpiritAwakenConfig.COST_ITEM_ID + "# ×" + SpiritAwakenConfig.COST_ITEM_QTY;
            tip += " + " + (SpiritAwakenConfig.COST_MESO / 10000) + "万金币\r\n";
            tip += "成功率 " + SpiritAwakenConfig.SUCCESS_RATE + "%";
        }
        cm.sendYesNo(tip);
    } else if (status === 3) {
        var result = modeAction === 1
            ? SpiritAwakenService.reset(cm.getPlayer(), selectedSlot)
            : SpiritAwakenService.awaken(cm.getPlayer(), selectedSlot);
        cm.sendOk(result.message());
        cm.dispose();
    } else {
        cm.dispose();
    }
}

function collectWeaponSlots(onlyWithSpirit) {
    var ii = ItemInformationProvider.getInstance();
    var inv = cm.getInventory(1);
    var slots = [];
    var limit = inv.getSlotLimit();
    for (var i = 1; i <= limit; i++) {
        var it = inv.getItem(i);
        if (it == null) {
            continue;
        }
        var id = it.getItemId();
        if (!ItemConstants.isWeapon(id) || ii.isCash(id)) {
            continue;
        }
        if (onlyWithSpirit) {
            if (it.getEquipSkillId() <= 0 || it.getEquipSkillLevel() <= 0) {
                continue;
            }
        } else {
            var req = ii.getEquipLevelReq(id);
            if (req == null) {
                req = 0;
            }
            if (req < SpiritAwakenConfig.MIN_WEAPON_REQ_LEVEL) {
                continue;
            }
        }
        slots.push(i);
    }
    return slots;
}

function buildWeaponListText(asMenu) {
    var slots = asMenu ? weaponSlots : collectWeaponSlots(false);
    if (slots.length === 0) {
        return "装备栏中暂无可显示的武器。";
    }
    var text = "";
    for (var i = 0; i < slots.length; i++) {
        var eq = cm.getInventory(1).getItem(slots[i]);
        if (eq == null) {
            continue;
        }
        var line = "#v" + eq.getItemId() + "# #t" + eq.getItemId() + "# @栏位" + slots[i] + "\r\n";
        line += "  " + SpiritAwakenService.describeEquip(eq) + "\r\n";
        if (asMenu) {
            text += "#L" + i + "#" + line + "#l";
        } else {
            text += line + "\r\n";
        }
    }
    return text;
}

function buildHelpText() {
    var t = "#e规则说明#n\r\n";
    t += "1. 成功 " + SpiritAwakenConfig.SUCCESS_RATE + "% / 失败 "
        + (100 - SpiritAwakenConfig.SUCCESS_RATE) + "%（失败不掉已有灵韵）\r\n";
    t += "2. 成功后先选池：通用 " + SpiritAwakenConfig.COMMON_POOL_CHANCE
        + "% / 本职攻击 " + SpiritAwakenConfig.JOB_POOL_CHANCE + "%\r\n";
    t += "3. 同技能则等级+1，异技能则覆盖为 Lv.1\r\n";
    t += "4. 普通技能上限 Lv." + SpiritAwakenConfig.NORMAL_MAX_LEVEL
        + "；火眼/稳如泰山(T0) 上限 Lv." + SpiritAwakenConfig.T0_MAX_LEVEL + "\r\n";
    t += "5. 通用池内火眼、稳如泰山各约 "
        + (SpiritAwakenConfig.COMMON_T0_RATE * 100) + "%\r\n";
    t += "6. #r影分身永不出现#k\r\n";
    t += "7. 穿戴武器后灵韵技能生效，卸下失效；装备 tip 可显示灵韵\r\n";
    t += "8. 交易获得的带灵韵武器会清空灵韵\r\n";
    t += "9. 清除灵韵消耗：#v4021017# ×" + SpiritAwakenConfig.RESET_COST_ITEM_QTY
        + " + " + (SpiritAwakenConfig.RESET_COST_MESO / 10000) + "万\r\n";
    t += "10. 材料可在匠人街材料商人处购买（灵韵结晶 #v4021017#）";
    return t;
}
