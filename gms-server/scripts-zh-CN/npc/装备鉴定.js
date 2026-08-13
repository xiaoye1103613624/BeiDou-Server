/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：装备鉴定系统
 *   1. 可鉴定装备类型：帽子、披风、套装上下衣、盾牌、手套、鞋子（其余装备不可鉴定）
 *   2. 未鉴定过的装备需先"鉴定"才能获得随机词条加成
 *   3. 已鉴定过的装备可反复"重铸鉴定"，每次重新随机词条，直到满意为止
 *   4. 鉴定/重铸鉴定均消耗：祝福卷轴 x1 + 金币 100w
 *   5. 词条加成基于装备基础属性叠加（重铸时会先还原为基础属性再重新随机，不会无限累加）
 *   6. 鉴定状态通过 flag 位 0x20（未被其他系统占用）标记，避免新增数据库字段
 *   7. 词条等级：凡品 / 灵品 / 圣 / 仙 / 神，对应概率与加成见 GRADE_TABLE
 * ==================
 */

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

// ===== 消耗配置 =====
var COST_ITEM_ID = 2340000;   // 祝福卷轴
var COST_ITEM_QTY = 1;        // 每次鉴定消耗数量
var COST_MESO = 1000000;      // 每次鉴定消耗金币（100w）

// ===== 已鉴定标记位（Item.flag 的第 0x20 位，未被其他系统占用） =====
var IDENTIFIED_FLAG = 0x20;

// ===== 可鉴定的装备槛位（负值，参考 EquipSlot） =====
// -1帽子 -5上衣/套装 -6裤子 -7鞋子 -8手套 -9披风 -10盾牌
var IDENTIFIABLE_SLOTS = [-1, -5, -6, -7, -8, -9, -10];

// ===== 鉴定词条等级表：weight 权重越大越容易出（总权重100） =====
// stat: 四维(STR/DEX/INT/LUK)各加成；hpmp: HP/MP各加成；watkRange/matkRange: 攻击力加成区间[min,max]，空则不加成
var GRADE_TABLE = [
    { name: "凡品", weight: 40, stat: 1, hpmp: 10, watkRange: null, matkRange: null },
    { name: "灵品", weight: 30, stat: 1, hpmp: 15, watkRange: null, matkRange: null },
    { name: "圣", weight: 15, stat: 2, hpmp: 20, watkRange: [1, 1], matkRange: null },
    { name: "仙", weight: 10, stat: 2, hpmp: 20, watkRange: null, matkRange: [2, 2] },
    { name: "神", weight: 5, stat: 3, hpmp: 30, watkRange: [1, 2], matkRange: [1, 2] }
];

var status = -1;
var selectedEquip = null; // 选中的装备对象
var equipListCache = null; // 上一次列出的可鉴定装备列表（按显示顺序缓存，供选择时取回）

function start() {
    status = -1;
    selectedEquip = null;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    status++;

    if (status == 0) {
        showEquipList();
    } else if (status == 1) {
        handleEquipSelection(selection);
    } else if (status == 2) {
        if (type == 1) {
            doIdentify();
        } else {
            cm.sendOk("已取消鉴定。");
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

/**
 * 判断装备是否为可鉴定槛位
 */
function isIdentifiableSlot(slot) {
    for (var i = 0; i < IDENTIFIABLE_SLOTS.length; i++) {
        if (IDENTIFIABLE_SLOTS[i] == slot) {
            return true;
        }
    }
    return false;
}

/**
 * 按权重随机抽取一个鉴定等级
 */
function rollGrade() {
    var totalWeight = 0;
    for (var i = 0; i < GRADE_TABLE.length; i++) {
        totalWeight += GRADE_TABLE[i].weight;
    }
    var roll = Math.random() * totalWeight;
    var acc = 0;
    for (var j = 0; j < GRADE_TABLE.length; j++) {
        acc += GRADE_TABLE[j].weight;
        if (roll < acc) {
            return GRADE_TABLE[j];
        }
    }
    return GRADE_TABLE[GRADE_TABLE.length - 1];
}

/**
 * 区间内取随机整数（包含上下限）
 */
function randomInRange(range) {
    if (range == null) {
        return 0;
    }
    return range[0] + Math.floor(Math.random() * (range[1] - range[0] + 1));
}

/**
 * 展示可鉴定装备列表（仅显示背包/已穿戴中可鉴定槛位的装备）
 */
function showEquipList() {
    var player = cm.getPlayer();
    var allItems = [];
    var equipInv = player.getInventory(InventoryType.EQUIP);
    var equippedInv = player.getInventory(InventoryType.EQUIPPED);
    var list1 = equipInv.list().toArray();
    var list2 = equippedInv.list().toArray();
    var i;
    for (i = 0; i < list1.length; i++) {
        allItems.push(list1[i]);
    }
    for (i = 0; i < list2.length; i++) {
        allItems.push(list2[i]);
    }

    var text = "#e装备鉴定#n\r\n\r\n";
    text += "#d可鉴定：帽子、披风、套装上下衣、盾牌、手套、鞋子#k\r\n";
    text += "每次消耗：#i" + COST_ITEM_ID + "# #t" + COST_ITEM_ID + "# x" + COST_ITEM_QTY;
    text += " + #b" + COST_MESO + "#k 金币\r\n";
    text += "\r\n#b请选择要鉴定的装备：#k\r\n\r\n";

    var hasEquip = false;
    for (i = 0; i < allItems.length; i++) {
        var equip = allItems[i];
        if (equip == null) {
            continue;
        }
        if (!isIdentifiableSlot(equip.getPosition())) {
            continue;
        }
        var identified = (equip.getFlag() & IDENTIFIED_FLAG) == IDENTIFIED_FLAG;
        text += "#L" + (i + 1) + "##i" + equip.getItemId() + "# #t" + equip.getItemId() + "# ";
        text += identified ? "[#b已鉴定#k]" : "[#r未鉴定#k]";
        text += "#l\r\n";
        hasEquip = true;
    }

    if (!hasEquip) {
        cm.sendOk("没有找到可鉴定的装备（帽子/披风/套装上下衣/盾牌/手套/鞋子）。");
        cm.dispose();
        return;
    }

    text += "\r\n#L0#取消#l\r\n";
    cm.sendSimple(text);

    // 缓存列表，供下一步按下标取回（用 selection - 1 对应 allItems 下标）
    equipListCache = allItems;
}

function handleEquipSelection(selection) {
    if (selection == 0) {
        cm.dispose();
        return;
    }

    var allItems = equipListCache;
    var idx = selection - 1;
    if (allItems == null || idx < 0 || idx >= allItems.length) {
        cm.sendOk("#r选择的装备不存在，请重新进入。#k");
        cm.dispose();
        return;
    }

    var equip = allItems[idx];
    if (equip == null || !isIdentifiableSlot(equip.getPosition())) {
        cm.sendOk("#r该装备不可鉴定。#k");
        cm.dispose();
        return;
    }
    selectedEquip = equip;

    var identified = (selectedEquip.getFlag() & IDENTIFIED_FLAG) == IDENTIFIED_FLAG;
    var actionName = identified ? "重铸鉴定" : "鉴定";

    var text = "确认对以下装备执行【" + actionName + "】？\r\n\r\n";
    text += "#i" + selectedEquip.getItemId() + "# #t" + selectedEquip.getItemId() + "#\r\n";
    text += "当前属性：STR+" + selectedEquip.getStr() + " DEX+" + selectedEquip.getDex();
    text += " INT+" + selectedEquip.getInt() + " LUK+" + selectedEquip.getLuk();
    text += " HP+" + selectedEquip.getHp() + " MP+" + selectedEquip.getMp();
    text += " 物攻+" + selectedEquip.getWatk() + " 魔攻+" + selectedEquip.getMatk() + "\r\n\r\n";
    text += "消耗：#i" + COST_ITEM_ID + "# #t" + COST_ITEM_ID + "# x" + COST_ITEM_QTY;
    text += " + #b" + COST_MESO + "#k 金币\r\n\r\n";
    if (identified) {
        text += "#r重铸将覆盖当前词条，结果随机，无法撤销！#k";
    } else {
        text += "#r鉴定结果随机，无法撤销！#k";
    }
    cm.sendYesNo(text);
}

function doIdentify() {
    if (selectedEquip == null) {
        cm.sendOk("数据异常，请重试。");
        cm.dispose();
        return;
    }

    // 二次校验材料与金币是否充足
    if (!cm.haveItem(COST_ITEM_ID, COST_ITEM_QTY)) {
        cm.sendOk("#r祝福卷轴不足！#k\r\n需要：#i" + COST_ITEM_ID + "# x" + COST_ITEM_QTY);
        cm.dispose();
        return;
    }
    if (cm.getMeso() < COST_MESO) {
        cm.sendOk("#r金币不足！#k\r\n需要：" + COST_MESO + " 金币");
        cm.dispose();
        return;
    }

    // 扣除消耗
    cm.gainItem(COST_ITEM_ID, -COST_ITEM_QTY);
    cm.gainMeso(-COST_MESO);

    // 取该装备的基础模板属性，重铸时先还原为基础值，再叠加新词条，避免重复累加
    var ii = ItemInformationProvider.getInstance();
    var base = ii.getEquipById(selectedEquip.getItemId());

    var grade = rollGrade();
    var watkBonus = randomInRange(grade.watkRange);
    var matkBonus = randomInRange(grade.matkRange);

    selectedEquip.setStr(base.getStr() + grade.stat);
    selectedEquip.setDex(base.getDex() + grade.stat);
    selectedEquip.setInt(base.getInt() + grade.stat);
    selectedEquip.setLuk(base.getLuk() + grade.stat);
    selectedEquip.setHp(base.getHp() + grade.hpmp);
    selectedEquip.setMp(base.getMp() + grade.hpmp);
    selectedEquip.setWatk(base.getWatk() + watkBonus);
    selectedEquip.setMatk(base.getMatk() + matkBonus);
    selectedEquip.setFlag(selectedEquip.getFlag() | IDENTIFIED_FLAG);

    cm.getPlayer().forceUpdateItem(selectedEquip);

    var text = "鉴定成功！获得【#r" + grade.name + "#k】词条！\r\n\r\n";
    text += "#i" + selectedEquip.getItemId() + "# #t" + selectedEquip.getItemId() + "#\r\n";
    text += "新属性：STR+" + selectedEquip.getStr() + " DEX+" + selectedEquip.getDex();
    text += " INT+" + selectedEquip.getInt() + " LUK+" + selectedEquip.getLuk();
    text += " HP+" + selectedEquip.getHp() + " MP+" + selectedEquip.getMp();
    text += " 物攻+" + selectedEquip.getWatk() + " 魔攻+" + selectedEquip.getMatk();
    cm.sendOk(text);
    cm.dispose();
}
