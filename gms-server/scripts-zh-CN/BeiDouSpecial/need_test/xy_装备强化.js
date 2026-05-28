// =============== xy_装备强化.js ===============
// 模仿高版本"星之力"强化系统，通过消耗材料将装备兑换为强化版
// 数据通过 characterExtendValue 持久化，记录每件装备的强化等级

var status = -1;
var ENHANCE_KEY = "equipEnhance";           // 角色扩展数据 key
var enhancedItem = null;                     // 当前选中的待强化装备
var selectedSlot = -1;
var enhanceData = {};                        // {slotId: {itemId, level, ...}}
var slotMapGlobal = [];                      // 强化选择列表（跨action调用）

// =============== 强化配置 ===============
// 每级强化：所需的材料和成功率
// rate: 成功率(0~100), costItem: 消耗材料ID, costCount: 材料数量
var ENHANCE_CONFIG = [
    {rate: 100, costItem: 4030000, costCount: 1},   // 0→1星
    {rate: 95,  costItem: 4030000, costCount: 2},   // 1→2星
    {rate: 90,  costItem: 4030000, costCount: 3},   // 2→3星
    {rate: 85,  costItem: 4030000, costCount: 4},   // 3→4星
    {rate: 80,  costItem: 4030000, costCount: 5},   // 4→5星
    {rate: 75,  costItem: 4030000, costCount: 6},   // 5→6星
    {rate: 70,  costItem: 4030000, costCount: 7},   // 6→7星
    {rate: 65,  costItem: 4030000, costCount: 8},   // 7→8星
    {rate: 60,  costItem: 4030000, costCount: 9},   // 8→9星
    {rate: 55,  costItem: 4030000, costCount: 10},  // 9→10星
];
var MAX_STAR = ENHANCE_CONFIG.length;

// 高版本武器攻击力表：每星增加的攻击力(对应武器基础攻击段位)
// 低星(+0~5): 每星+1攻, 中星(+6~10): 每星+2攻, 高星(+11~15): 每星+3攻
// 注：实际属性加成需配合Java端 Equip 修改；此处仅记录星数，由服务端统一结算
function getAtkBonus(star) {
    if (star <= 0) return 0;
    if (star <= 5) return star;           // 前5星各+1
    if (star <= 10) return 5 + (star - 5) * 2;  // 6~10星各+2
    return 15 + (star - 10) * 3;          // 11+星各+3
}
function getStatBonus(star) {
    if (star <= 0) return 0;
    return Math.floor(star / 2);          // 每2星全属性+1
}

// =============== 数据存取 ===============
function loadEnhanceData() {
    var raw = cm.getCharacterExtendValue(ENHANCE_KEY);
    if (raw && raw !== "") {
        try { enhanceData = JSON.parse(raw); } catch (e) { enhanceData = {}; }
    } else {
        enhanceData = {};
    }
}
function saveEnhanceData() {
    cm.saveOrUpdateCharacterExtendValue(ENHANCE_KEY, JSON.stringify(enhanceData));
}

// =============== 辅助函数 ===============
function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

// 获取装备所属的槽位名称
function getSlotName(slot) {
    var names = {
        "-1":"帽子", "-2":"脸饰", "-3":"眼饰", "-4":"耳环",
        "-5":"上衣", "-6":"裤子", "-7":"鞋子", "-8":"手套",
        "-9":"披风", "-10":"盾牌", "-11":"武器",
        "-12":"戒指1", "-13":"戒指2", "-14":"戒指3", "-15":"戒指4",
        "-17":"项链", "-18":"驯养", "-19":"鞍具",
        "-49":"勋章", "-50":"腰带"
    };
    return names[String(slot)] || ("槽位" + slot);
}

// 格式化当前强化数据为可读文本
function formatEnhanceList() {
    var lines = [];
    var count = 0;
    for (var key in enhanceData) {
        var d = enhanceData[key];
        if (d.level > 0) {
            count++;
            lines.push(" #b" + getSlotName(parseInt(key)) + "#k: "
                + cm.getItemName(d.itemId) + " #r★" + d.level + "#k");
        }
    }
    if (count === 0) return "暂无强化记录";
    return "已强化装备(" + count + "件):\r\n" + lines.join("\r\n");
}

// =============== 入口 ===============
function start() { status = -1; action(1, 0, 0); }

function action(mode, type, selection) {
    if (mode === -1) { cm.dispose(); return; }
    if (mode === 1) status++; else status--;

    if (status === 0) {
        loadEnhanceData();
        var menu = "#b装备强化系统#k\r\n";
        menu += "#L0#开始强化装备#l\r\n";
        menu += "#L1#查看强化列表#l\r\n";
        menu += "#L2#查看强化规则#l";
        cm.sendSimple(menu);
    } else if (status === 1) {
        if (selection === 0) {
            // 列出背包中所有已装备的装备供选择
            var equipped = cm.getPlayer().getInventory(
                Java.type("org.gms.client.inventory.InventoryType").EQUIPPED
            );
            if (!equipped) {
                cm.sendOk("无法获取装备信息。");
                cm.dispose();
                return;
            }
            var list = equipped.list();
            var menu = "选择要强化的装备：\r\n";
            var idx = 0;
            var slotMap = [];
            for (var i = 0; i < list.size(); i++) {
                var item = list.get(i);
                if (item && isEquipment(item.getItemId())) {
                    var slot = item.getPosition();
                    var star = (enhanceData[String(slot)] && enhanceData[String(slot)].level) || 0;
                    menu += "#L" + idx + "#" + cm.getItemName(item.getItemId())
                        + " (" + getSlotName(slot) + ")";
                    if (star > 0) menu += " #r★" + star + "#k";
                    menu += "#l\r\n";
                    slotMap.push({slot: slot, itemId: item.getItemId(), star: star});
                    idx++;
                }
            }
            if (idx === 0) {
                cm.sendOk("你没有已装备的可强化装备。\r\n请先穿戴需要强化的装备。");
                cm.dispose();
                return;
            }
            cm.sendSimple(menu);
            slotMapGlobal = slotMap;
        } else if (selection === 1) {
            cm.sendOk(formatEnhanceList());
            cm.dispose();
        } else if (selection === 2) {
            var msg = "#b强化规则：#k\r\n";
            msg += "1. 强化只对#r已穿戴的装备#k生效\r\n";
            msg += "2. 最高强化等级: #r" + MAX_STAR + "星#k\r\n";
            msg += "3. 强化消耗特殊材料，每级递增\r\n";
            msg += "4. 强化有成功概率，详情见下方\r\n\r\n";
            msg += "#b各星成功率：#k\r\n";
            for (var i = 0; i < ENHANCE_CONFIG.length; i++) {
                msg += "★" + (i + 1) + ": " + ENHANCE_CONFIG[i].rate + "%";
                if (i < ENHANCE_CONFIG.length - 1) msg += "  ";
                if ((i + 1) % 5 === 0) msg += "\r\n";
            }
            msg += "\r\n\r\n#b攻击加成：#k前5星每星+1攻, 6~10星每星+2攻\r\n";
            msg += "#b属性加成：#k每2星全属性+1";
            cm.sendOk(msg);
            cm.dispose();
        }
    } else if (status === 2 && selection >= 0) {
        // 确认强化
        var slotMap = slotMapGlobal;
        if (!slotMap || selection >= slotMap.length) { cm.dispose(); return; }
        var info = slotMap[selection];
        var slot = info.slot;
        var itemId = info.itemId;
        var currentStar = info.star;

        if (currentStar >= MAX_STAR) {
            cm.sendOk("该装备已达最高强化等级 #r★" + MAX_STAR + "#k！");
            cm.dispose();
            return;
        }

        var nextStar = currentStar + 1;
        var cfg = ENHANCE_CONFIG[currentStar];
        enhancedItem = {slot: slot, itemId: itemId, nextStar: nextStar};
        selectedSlot = slot;

        var msg = "【强化确认】\r\n";
        msg += "装备: #b" + cm.getItemName(itemId) + "#k\r\n";
        msg += "当前: " + (currentStar > 0 ? "★" + currentStar : "未强化") + "\r\n";
        msg += "目标: #r★" + nextStar + "#k\r\n";
        msg += "成功率: #b" + cfg.rate + "%#k\r\n";
        msg += "消耗: " + cm.getItemName(cfg.costItem) + " ×" + cfg.costCount + "\r\n";
        msg += "\r\n属性预览:\r\n";
        msg += "  攻击力: " + getAtkBonus(currentStar) + " → #r" + getAtkBonus(nextStar) + "#k (+" + (getAtkBonus(nextStar) - getAtkBonus(currentStar)) + ")\r\n";
        msg += "  全属性: " + getStatBonus(currentStar) + " → #r" + getStatBonus(nextStar) + "#k\r\n";
        msg += "\r\n#L0#确认强化#l\r\n#L1#取消#l";
        cm.sendSimple(msg);
    } else if (status === 3 && selection === 0) {
        // 执行强化
        var info = enhancedItem;
        var cfg = ENHANCE_CONFIG[info.nextStar - 1];

        // 检查材料
        if (!cm.haveItem(cfg.costItem, cfg.costCount)) {
            cm.sendOk("材料不足！需要 " + cm.getItemName(cfg.costItem) + " ×" + cfg.costCount + "。");
            cm.dispose();
            return;
        }

        // 扣除材料
        cm.gainItem(cfg.costItem, -cfg.costCount);

        // 成功判定
        var roll = Math.floor(Math.random() * 100);
        if (roll < cfg.rate) {
            // 成功
            var key = String(selectedSlot);
            if (!enhanceData[key]) {
                enhanceData[key] = {itemId: info.itemId, level: 0};
            }
            enhanceData[key].level = info.nextStar;
            saveEnhanceData();
            var newAtk = getAtkBonus(enhanceData[key].level);
            var newStat = getStatBonus(enhanceData[key].level);
            cm.sendOk("强化成功！\r\n" + cm.getItemName(info.itemId) + " → #r★" + info.nextStar + "#k\r\n"
                + "当前加成: 攻击力+" + newAtk + ", 全属性+" + newStat + "\r\n"
                + "(属性已记录，请联系GM或通过专用接口应用属性)");
        } else {
            cm.sendOk("强化失败！材料已消耗。\r\n装备未受损，可再次尝试。");
        }
        cm.dispose();
    } else if (status === 3 && selection === 1) {
        cm.sendOk("已取消强化。");
        cm.dispose();
    }
}
