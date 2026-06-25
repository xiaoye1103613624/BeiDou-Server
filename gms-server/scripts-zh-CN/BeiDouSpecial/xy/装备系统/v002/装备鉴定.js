/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：装备鉴定系统
 *   1. 可鉴定装备类型：帽子、披风、套装上下衣、盾牌、手套、鞋子（其余装备不可鉴定）
 *   2. 未鉴定过的装备需先"鉴定"才能获得随机词条加成
 *   3. 已鉴定过的装备可反复"重铸鉴定"，每次重新随机词条，直到满意为止
 *   4. 首次鉴定消耗：祝福卷轴 x1 + 金币 100w
 *   5. 已鉴定装备的"洗炼"（重新随机词条）消耗：金币 100w + 点卷(NX点) 100
 *   6. 词条加成基于装备基础属性叠加（洗炼时会先还原为基础属性再重新随机，不会无限累加）
 *   7. 鉴定状态通过 flag 位 0x20（未被其他系统占用）标记，避免新增数据库字段
 *   8. 鉴定/洗炼成功可获得1~3组独立词条，词条种类见 WORD_POOL，每条词条带1~9阶前缀，
 *      阶数越大概率越低；最终数值 = 词条基础值(阶1) × 阶数；"血/防"为固定阶(无前缀加成，注释同满阶)
 *   9. 阶数前缀展示用"N阶"代替①~⑨这类圆圈数字，因v83客户端GBK字体子集里没有对应字形，
 *      直接塞中文显示会变成乱码占位符（参考装备强化.js里★改*的同类问题）
 *   10. 词条文本额外写进装备的 owner 字段做展示：v83客户端原生协议里这是个自由文本行，
 *       只要非空就会在Tooltip里多显示一行（类似"固有装备物品"，和现金道具"命名标签"5060000
 *       纯靠 setOwner() 实现挂名展示的原理一样），不需要任何客户端Hook
 *   11. 词条文本与真实的固有所有者绑定共存：写回前先用 extractRealOwnerPrefix() 把已有
 *       owner 文本里的"N阶X"词条片段摘掉，剩下的真实玩家名前缀会原样保留，鉴定/洗炼不会
 *       冲掉道具原本的绑定信息
 *   12. owner 文本同时也是词条的存储格式，未来其他系统只要遵循同样的"N阶X"规则读写
 *       owner 字段（见 parseWordsFromOwnerText/extractRealOwnerPrefix），就能给同一件装备
 *       追加/移除词条来源，互不冲突；当前鉴定/洗炼是整体重roll，不依赖已有词条
 * ==================
 */

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
var CashShop = Java.type('org.gms.server.CashShop');

// ===== 首次鉴定消耗配置 =====
var COST_ITEM_ID = 2340000;   // 祝福卷轴
var COST_ITEM_QTY = 1;        // 每次鉴定消耗数量
var COST_MESO = 1000000;      // 每次鉴定消耗金币（100w）

// ===== 洗炼（已鉴定装备重新随机词条）消耗配置 =====
var REFINE_COST_MESO = 1000000; // 洗炼消耗金币（100w）
var REFINE_COST_NX = 100;       // 洗炼消耗点卷（NX点）

// ===== 已鉴定标记位（Item.flag 的第 0x20 位，未被其他系统占用） =====
var IDENTIFIED_FLAG = 0x20;

// ===== 可鉴定的装备槛位（负值，参考 EquipSlot） =====
// -1帽子 -5上衣/套装 -6裤子 -7鞋子 -8手套 -9披风 -10盾牌
var IDENTIFIABLE_SLOTS = [-1, -5, -6, -7, -8, -9, -10];

// ===== 词条池：base 为 1 阶时的基础加成，实际加成 = base × 阶数（fixedTier 词条恒为1阶） =====
var WORD_POOL = [
    { code: "血", fixedTier: true, base: { hp: 100, mp: 100 } },
    { code: "防", fixedTier: true, base: { wdef: 50, mdef: 50 } },
    { code: "战", base: { str: 2, dex: 2 } },
    { code: "弓", base: { dex: 2, str: 2 } },
    { code: "法", base: { int_: 2, luk: 2 } },
    { code: "侠", base: { dex: 1, str: 2 } },
    { code: "全", base: { str: 1, dex: 1, int_: 1, luk: 1 } },
    { code: "攻", base: { watk: 1 } },
    { code: "魔", base: { matk: 1 } },
    { code: "勇", base: { str: 2, dex: 1, watk: 1 } },
    { code: "慧", base: { int_: 2, luk: 1, matk: 1 } },
    { code: "迅", base: { str: 1, dex: 2, matk: 1 } },
    { code: "圣", base: { str: 1, dex: 1, int_: 1, luk: 1, hp: 20, mp: 20, watk: 1 } },
    { code: "仙", base: { str: 2, dex: 2, int_: 2, luk: 2, hp: 20, mp: 20, matk: 1 } },
    { code: "神", base: { str: 3, dex: 3, int_: 3, luk: 3, hp: 20, mp: 20, watk: 1, matk: 1 } }
];

// ===== 阶数(1~9)权重表：阶数越大概率越低，下标0对应1阶 =====
var TIER_WEIGHTS = [9, 8, 7, 6, 5, 4, 3, 2, 1];

// ===== 词条组数(1~3)权重表：下标0对应1组 =====
var WORD_COUNT_WEIGHTS = [60, 30, 10];

/**
 * 按权重表随机抽取下标(0-based)，返回值为 下标+1
 */
function rollWeightedTier(weights) {
    var total = 0, i;
    for (i = 0; i < weights.length; i++) {
        total += weights[i];
    }
    var roll = Math.random() * total;
    var acc = 0;
    for (i = 0; i < weights.length; i++) {
        acc += weights[i];
        if (roll < acc) {
            return i + 1;
        }
    }
    return weights.length;
}

/**
 * 随机抽取一组独立词条(1~3个)，已排除同名词条重复
 */
function rollWordSet() {
    var count = rollWeightedTier(WORD_COUNT_WEIGHTS);
    var picked = [];
    var usedCodes = {};
    var guard = 0;
    while (picked.length < count && guard < 50) {
        guard++;
        var entry = WORD_POOL[Math.floor(Math.random() * WORD_POOL.length)];
        if (usedCodes[entry.code]) {
            continue;
        }
        usedCodes[entry.code] = true;
        var tier = entry.fixedTier ? 1 : rollWeightedTier(TIER_WEIGHTS);
        picked.push({ code: entry.code, tier: tier, base: entry.base });
    }
    return picked;
}

/**
 * 将一组词条的加成按阶数倍率叠加汇总
 */
function sumWordSet(wordSet) {
    var total = { str: 0, dex: 0, int_: 0, luk: 0, hp: 0, mp: 0, watk: 0, matk: 0, wdef: 0, mdef: 0 };
    for (var i = 0; i < wordSet.length; i++) {
        var w = wordSet[i];
        for (var key in w.base) {
            total[key] += w.base[key] * w.tier;
        }
    }
    return total;
}

/**
 * 描述一组词条，用于结果提示文本，阶数用"N阶"代替①~⑨圆圈数字(客户端字体子集没有对应字形)
 */
function describeWordSet(wordSet) {
    var parts = [];
    for (var i = 0; i < wordSet.length; i++) {
        var w = wordSet[i];
        var prefix = w.tier + "阶";
        parts.push(prefix + w.code);
    }
    return parts.join(" + ");
}

/**
 * 按词条编码(code)反查 WORD_POOL，找不到返回 null
 */
function findWordPoolEntry(code) {
    for (var i = 0; i < WORD_POOL.length; i++) {
        if (WORD_POOL[i].code == code) {
            return WORD_POOL[i];
        }
    }
    return null;
}

// ===== 词条复用 Equip.owner 字段做"固有装备物品"式展示，无需任何客户端Hook =====
// 原理：v83客户端原生协议里 equip.owner 是个自由文本行，只要非空就会在Tooltip里多显示一行
// （现金道具"命名标签"5060000就是纯靠 setOwner(player.getName()) 实现挂名展示，证明该机制不依赖任何flag位）。
// 词条描述文本固定格式是"N阶X"（如"2阶战"），与真实玩家名不会重叠，所以可以用正则直接从
// owner 文本里把词条部分摘出来，剩下的就是真实绑定的玩家名前缀——这样两种用途可以共存在同一个字段里，
// 不会因为鉴定/洗炼而冲掉道具原本的"固有所有者"绑定。
// 这套编码同时也是"多来源词条"的存储格式：未来其他系统（不只是这个鉴定NPC）只要遵循同样的
// "N阶X"文本规则读写 owner 字段，就能往同一件装备上追加/移除词条，互不冲突。
var WORD_TEXT_PATTERN = /([1-9])阶([一-龥])/g;

/**
 * 从 owner 文本里解析出已有词条列表（用于未来"追加词条"场景；当前鉴定/洗炼是整体重roll，不依赖这个结果）
 */
function parseWordsFromOwnerText(ownerText) {
    var words = [];
    if (!ownerText) {
        return words;
    }
    var re = new RegExp(WORD_TEXT_PATTERN.source, "g");
    var m;
    while ((m = re.exec(ownerText)) !== null) {
        var tier = parseInt(m[1], 10);
        var code = m[2];
        var entry = findWordPoolEntry(code);
        if (entry != null) {
            words.push({ code: code, tier: entry.fixedTier ? 1 : tier, base: entry.base });
        }
    }
    return words;
}

/**
 * 从 owner 文本里摘掉"N阶X"词条片段及连接符，剩下的就是真实玩家绑定名前缀（可能为空字符串）
 */
function extractRealOwnerPrefix(ownerText) {
    if (!ownerText) {
        return "";
    }
    var re = new RegExp(WORD_TEXT_PATTERN.source, "g");
    return ownerText.replace(re, "").replace(/\s*\+\s*/g, " ").trim();
}

/**
 * 拼出写回 owner 字段的最终文本：真实绑定名前缀（若有） + 词条描述
 */
function buildOwnerDisplayText(realOwnerPrefix, wordSet) {
    var wordsText = describeWordSet(wordSet);
    if (realOwnerPrefix) {
        return realOwnerPrefix + " " + wordsText;
    }
    return wordsText;
}

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
    var actionName = identified ? "洗炼" : "鉴定";

    var text = "确认对以下装备执行【" + actionName + "】？\r\n\r\n";
    text += "#i" + selectedEquip.getItemId() + "# #t" + selectedEquip.getItemId() + "#\r\n";
    text += "当前属性：STR+" + selectedEquip.getStr() + " DEX+" + selectedEquip.getDex();
    text += " INT+" + selectedEquip.getInt() + " LUK+" + selectedEquip.getLuk();
    text += " HP+" + selectedEquip.getHp() + " MP+" + selectedEquip.getMp();
    text += " 物攻+" + selectedEquip.getWatk() + " 魔攻+" + selectedEquip.getMatk() + "\r\n\r\n";
    if (identified) {
        text += "消耗：#b" + REFINE_COST_MESO + "#k 金币 + #b" + REFINE_COST_NX + "#k 点卷\r\n\r\n";
        text += "#r洗炼将覆盖当前词条，结果随机，无法撤销！#k";
    } else {
        text += "消耗：#i" + COST_ITEM_ID + "# #t" + COST_ITEM_ID + "# x" + COST_ITEM_QTY;
        text += " + #b" + COST_MESO + "#k 金币\r\n\r\n";
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

    var identified = (selectedEquip.getFlag() & IDENTIFIED_FLAG) == IDENTIFIED_FLAG;

    // 二次校验材料/金币/点卷是否充足（首次鉴定 与 洗炼 消耗不同）
    if (identified) {
        if (cm.getMeso() < REFINE_COST_MESO) {
            cm.sendOk("#r金币不足！#k\r\n需要：" + REFINE_COST_MESO + " 金币");
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getCashShop().getCash(CashShop.NX_CREDIT) < REFINE_COST_NX) {
            cm.sendOk("#r点卷不足！#k\r\n需要：" + REFINE_COST_NX + " 点卷");
            cm.dispose();
            return;
        }
        cm.gainMeso(-REFINE_COST_MESO);
        cm.getPlayer().getCashShop().gainCash(CashShop.NX_CREDIT, -REFINE_COST_NX);
    } else {
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
        cm.gainItem(COST_ITEM_ID, -COST_ITEM_QTY);
        cm.gainMeso(-COST_MESO);
    }

    // 取该装备的基础模板属性，洗炼时先还原为基础值，再叠加新词条，避免重复累加
    var ii = ItemInformationProvider.getInstance();
    var base = ii.getEquipById(selectedEquip.getItemId());

    // 保留真实的固有所有者绑定（如现金道具命名标签写入的玩家名），词条文本只是追加在后面，
    // 不会因为鉴定/洗炼把道具原本的绑定信息冲掉
    var realOwnerPrefix = extractRealOwnerPrefix(selectedEquip.getOwner());

    var wordSet = rollWordSet();
    var bonus = sumWordSet(wordSet);

    selectedEquip.setStr(base.getStr() + bonus.str);
    selectedEquip.setDex(base.getDex() + bonus.dex);
    selectedEquip.setInt(base.getInt() + bonus.int_);
    selectedEquip.setLuk(base.getLuk() + bonus.luk);
    selectedEquip.setHp(base.getHp() + bonus.hp);
    selectedEquip.setMp(base.getMp() + bonus.mp);
    selectedEquip.setWatk(base.getWatk() + bonus.watk);
    selectedEquip.setMatk(base.getMatk() + bonus.matk);
    selectedEquip.setWdef(base.getWdef() + bonus.wdef);
    selectedEquip.setMdef(base.getMdef() + bonus.mdef);
    selectedEquip.setFlag(selectedEquip.getFlag() | IDENTIFIED_FLAG);
    // 词条文本写进 owner 字段：v83客户端原生协议里这是个自由文本行，非空就会在Tooltip里多显示一行
    // （和现金道具"命名标签"5060000的展示原理一样），不需要任何客户端Hook
    selectedEquip.setOwner(buildOwnerDisplayText(realOwnerPrefix, wordSet));

    cm.getPlayer().forceUpdateItem(selectedEquip);

    var text = (identified ? "洗炼成功！" : "鉴定成功！") + "获得词条【#r" + describeWordSet(wordSet) + "#k】！\r\n\r\n";
    text += "#i" + selectedEquip.getItemId() + "# #t" + selectedEquip.getItemId() + "#\r\n";
    text += "新属性：STR+" + selectedEquip.getStr() + " DEX+" + selectedEquip.getDex();
    text += " INT+" + selectedEquip.getInt() + " LUK+" + selectedEquip.getLuk();
    text += " HP+" + selectedEquip.getHp() + " MP+" + selectedEquip.getMp();
    text += " 物攻+" + selectedEquip.getWatk() + " 魔攻+" + selectedEquip.getMatk();
    text += " 物防+" + selectedEquip.getWdef() + " 魔防+" + selectedEquip.getMdef();
    cm.sendOk(text);
    cm.dispose();
}
