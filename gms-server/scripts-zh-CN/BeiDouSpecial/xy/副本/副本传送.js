/*
    副本传送脚本 — 传送到冒险岛各个副本门口
    传送前检查金币是否足够，不足则提示
    ========== 修改区（所有可配置项集中于此） ==========
**/
var status = 0;
var selectedIdx = -1;

// ==================== 费用配置 ====================
// 按索引依次匹配，超出最后一个的索引统一使用默认费用
// 修改方法：直接改数值，单位是金币（1W = 10000）
var FEE_TIERS = [10000, 20000, 50000];  // 索引0→1W, 1→2W, 2→5W
var FEE_DEFAULT  = 100000;               // 索引3及之后→10W

// ==================== 副本列表 ====================
// 每个副本：name=显示名称, mapId=地图ID, desc=简短描述
// 增删改副本只需编辑此数组即可
// 注：地图ID为079版本常用值，如与实际服不一致请直接修改mapId
var dungeons = [
    // --- 组队副本（079六大PQ） ---
    { name: "月妙副本",      mapId: 910010000,  desc: "射手村 → 月妙副本入口"           },
    { name: "废弃副本",      mapId: 103000800,  desc: "废弃都市 → 下水道副本入口"        },
    { name: "天空之城副本",  mapId: 920010000,  desc: "天空之城 → 女神之塔入口"          },
    { name: "玩具塔副本",    mapId: 922010000,  desc: "玩具城 → 时间裂缝入口"            },
    { name: "毒物副本",      mapId: 2610000210, desc: "玛加提亚 → 罗密欧与朱丽叶入口"    },
    { name: "男女副本",      mapId: 680000000,  desc: "结婚礼堂 → 爱情副本入口"          },
    // --- BOSS讨伐 ---
    { name: "扎昆祭坛",      mapId: 211042300,  desc: "废弃矿坑 → 扎昆祭坛入口"          },
    { name: "闹钟大厅",      mapId: 220080001,  desc: "玩具城 → 时间漩涡"                },
    { name: "鱼王海域",      mapId: 230040420,  desc: "水下世界 → 鱼王出没海域"           },
];

// ==================== UI 文案（选填） ====================
var TITLE_TEXT   = "#e#d副本传送服务#n#k";
var CANCEL_TEXT  = "有需要再来找我！";
var CONFIRM_TEXT = "#e副本传送确认#n";
var SUCCESS_TEXT = "传送成功！";
var NO_MONEY_TEXT = "金币不足！";

// ==================== 以下为逻辑代码，一般无需修改 ====================

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.sendOk(CANCEL_TEXT);
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        // 构建副本列表
        var meso = cm.getPlayer().getMeso();
        var text = TITLE_TEXT + "\r\n\r\n";
        text += "#b传送费用规则：#k\r\n";
        text += buildFeeRuleText() + "\r\n\r\n";
        text += "#e选择你要去的副本：#n\r\n\r\n";

        for (var i = 0; i < dungeons.length; i++) {
            var fee = getFee(i);
            text += "#L" + i + "#";
            text += "#b" + (i + 1) + ".#k " + dungeons[i].name;
            text += "   #r" + formatMeso(fee) + "#k";
            text += "   #d(" + dungeons[i].desc + ")#k";
            text += "#l\r\n";
        }
        text += "\r\n#e#r当前拥有金币：#b" + meso + "#k";
        cm.sendSimple(text);

    } else if (status == 1) {
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= dungeons.length) {
            cm.sendOk("无效的选择。");
            cm.dispose();
            return;
        }
        var fee = getFee(selectedIdx);
        var d = dungeons[selectedIdx];
        cm.sendYesNo(
            CONFIRM_TEXT + "\r\n\r\n" +
            "目的地：#b" + d.name + "#k\r\n" +
            "描述：#d" + d.desc + "#k\r\n" +
            "费用：#r" + formatMeso(fee) + "#k\r\n\r\n" +
            "确认传送吗？"
        );

    } else if (status == 2) {
        if (selectedIdx < 0 || selectedIdx >= dungeons.length) {
            cm.dispose();
            return;
        }
        var fee = getFee(selectedIdx);
        var d = dungeons[selectedIdx];

        if (cm.getPlayer().getMeso() < fee) {
            cm.sendOk(NO_MONEY_TEXT + "\r\n\r\n需要 #r" + formatMeso(fee) + "#k，你当前只有 #b" + cm.getPlayer().getMeso() + " 金币#k。");
            cm.dispose();
            return;
        }

        cm.gainMeso(-fee);
        cm.warp(d.mapId, 0);
        cm.sendOk(SUCCESS_TEXT + " 已扣除 #r" + formatMeso(fee) + "#k。\r\n\r\n欢迎来到 #b" + d.name + "#k！");
        cm.dispose();
    }
}

// ==================== 工具函数 ====================

/** 根据索引获取费用：按 FEE_TIERS 依次匹配，超出用 FEE_DEFAULT */
function getFee(index) {
    if (index < FEE_TIERS.length) {
        return FEE_TIERS[index];
    }
    return FEE_DEFAULT;
}

/** 金币格式化：>=10000 显示为 X.W（万），否则显示原值 */
function formatMeso(amount) {
    if (amount >= 10000 && amount % 10000 == 0) {
        return (amount / 10000) + "W金币";
    }
    if (amount >= 10000) {
        return (amount / 10000).toFixed(1) + "W金币";
    }
    return amount + "金币";
}

/** 构建费用规则描述文本 */
function buildFeeRuleText() {
    var parts = [];
    for (var i = 0; i < FEE_TIERS.length; i++) {
        parts.push("第" + (i + 1) + "个 #r" + formatMeso(FEE_TIERS[i]) + "#k");
    }
    parts.push("之后 #r" + formatMeso(FEE_DEFAULT) + "/次#k");
    return "  " + parts.join(" | ");
}
