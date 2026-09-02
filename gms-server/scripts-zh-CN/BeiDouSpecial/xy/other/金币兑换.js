/*
 * 金币兑换脚本
 * 功能：金币兑换物品（5%手续费）、物品兑换金币（上限检查）
 * 每次兑换广播全地图公告，并打印后台日志
 */

// ==================== 兑换配置 ====================
// [物品ID, 物品名称, 金币单价]
// 金币→物品：实际花费 = 单价 × 1.05（含5%手续费）
// 物品→金币：实得 = 单价
var exchangeItems = [
    [4032485, "大型钱币模型", 10*10000],
    [4031039, "休咪的金币", 100*10000],
];

var FEE_RATE = 0.05;        // 金币兑换物品的手续费率 5%

var status = 0;
var exchangeDir = -1;       // 兑换方向：0=金币→物品, 1=物品→金币
var selectedItemIdx = -1;   // 选中的物品索引

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
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    if (status == 0) {
        // 选择兑换方向
        var text = "\t\t#r#e< 金币兑换中心 >#k#n\r\n\r\n";
        text += "\t\t#b当前金币：#r" + formatNumber(cm.getMeso()) + "#k\r\n\r\n";
        text += "#L0##b金币 → 物品#k（收取#r5%#k手续费）#l\r\n\r\n";
        text += "#L1##b物品 → 金币#k（#r需检查金币上限#k）#l\r\n";
        cm.sendSimple(text);

    } else if (status == 1) {
        // 显示物品列表
        exchangeDir = selection;
        if (exchangeDir == 0) {
            // 金币→物品：显示单价及含手续费的实际花费
            var text = "\t\t#r#e< 金币 → 物品 >#k#n\r\n\r\n";
            text += "#d手续费 #r5%#k，实际花费 = 单价 × 1.05#k\r\n";
            text += "\t\t#b当前金币：#r" + formatNumber(cm.getMeso()) + "#k\r\n\r\n";
            for (var i = 0; i < exchangeItems.length; i++) {
                var item = exchangeItems[i];
                var actualCost = Math.floor(item[2] * (1 + FEE_RATE));
                text += "#L" + i + "##i" + item[0] + "# #b" + item[1] + "#k  #r" + formatNumber(actualCost) + "金币/个#k#l\r\n";
            }
        } else {
            // 物品→金币：显示背包数量及可兑换金币
            var text = "\t\t#r#e< 物品 → 金币 >#k#n\r\n\r\n";
            text += "#d将物品兑换为金币#k\r\n";
            text += "\t\t#b当前金币：#r" + formatNumber(cm.getMeso()) + "#k\r\n";
            text += "\t\t#b金币上限：#r无固定上限#k（服务端 BIGINT / mesouncap）\r\n\r\n";
            for (var i = 0; i < exchangeItems.length; i++) {
                var item = exchangeItems[i];
                var owned = cm.getItemQuantity(item[0]);
                text += "#L" + i + "##i" + item[0] + "# #b" + item[1] + "#k  拥有：#r" + owned + "个#k  单价：#r" + formatNumber(item[2]) + "金币#k#l\r\n";
            }
        }
        cm.sendSimple(text);

    } else if (status == 2) {
        // 输入兑换数量
        selectedItemIdx = selection;
        var item = exchangeItems[selectedItemIdx];

        if (exchangeDir == 0) {
            // 金币→物品：计算最大可购买数量
            var actualCost = Math.floor(item[2] * (1 + FEE_RATE));
            var maxByMeso = Math.floor(cm.getMeso() / actualCost);
            if (maxByMeso <= 0) {
                cm.sendOk("金币不足！购买一个 #b" + item[1] + "#k 需要 #r" + formatNumber(actualCost) + "金币#k。");
                cm.dispose();
                return;
            }
            // 限制最大输入999
            var maxQty = Math.min(maxByMeso, 999);
            cm.sendGetNumber("#i" + item[0] + "# #b" + item[1] + "#k\r\n"
                + "单价：#r" + formatNumber(item[2]) + "金币#k（含手续费后 #r" + formatNumber(actualCost) + "金币#k）\r\n"
                + "当前金币：#r" + formatNumber(cm.getMeso()) + "#k\r\n\r\n"
                + "请输入购买数量：", 1, 1, maxQty);
        } else {
            // 物品→金币：计算最大可兑换数量
            var owned = cm.getItemQuantity(item[0]);
            if (owned <= 0) {
                cm.sendOk("背包中没有 #b" + item[1] + "#k ！");
                cm.dispose();
                return;
            }
            var maxQty = Math.min(owned, 999);
            if (maxQty <= 0) {
                cm.sendOk("背包中没有可兑换的 #b" + item[1] + "#k 。");
                cm.dispose();
                return;
            }
            cm.sendGetNumber("#i" + item[0] + "# #b" + item[1] + "#k\r\n"
                + "拥有数量：#r" + owned + "个#k  单价：#r" + formatNumber(item[2]) + "金币#k\r\n"
                + "金币上限：无固定上限  当前金币：" + formatNumber(cm.getMeso()) + "\r\n\r\n"
                + "请输入兑换数量：", 1, 1, maxQty);
        }
    } else if (status == 3) {
        // 执行兑换
        var qty = selection; // sendGetNumber返回的数量
        var item = exchangeItems[selectedItemIdx];

        if (exchangeDir == 0) {
            // ========== 金币 → 物品 ==========
            var unitCost = Math.floor(item[2] * (1 + FEE_RATE)); // 含手续费单价
            var totalCost = unitCost * qty;
            var fee = totalCost - item[2] * qty; // 实际手续费

            // 金币不足检查
            if (cm.getMeso() < totalCost) {
                cm.sendOk("金币不足！需要 #r" + formatNumber(totalCost) + "金币#k。");
                cm.dispose();
                return;
            }
            // 背包空间检查
            if (!cm.canHold(item[0], qty)) {
                cm.sendOk("背包空间不足，无法存放 #b" + qty + "个 " + item[1] + "#k。");
                cm.dispose();
                return;
            }

            // 扣除金币
            cm.gainMeso(-totalCost);
            // 给予物品
            cm.gainItem(item[0], qty);

            // 地图广播
            cm.mapMessage(6, "[" + cm.getPlayer().getName() + "玩家] 花费 " + formatNumber(totalCost) + " 金币购买了 " + qty + " 个 " + item[1]);
            // 后台日志
            logExchange("金币→物品", item, qty, totalCost, fee);
            // 成功提示
            cm.sendOk("#b兑换成功！#k\r\n\r\n"
                + "获得：#i" + item[0] + "# #b" + item[1] + " ×" + qty + "#k\r\n"
                + "花费：#r" + formatNumber(totalCost) + "金币#k（含手续费 #r" + formatNumber(fee) + "金币#k）\r\n"
                + "剩余金币：#r" + formatNumber(cm.getMeso()) + "#k");
            cm.dispose();

        } else {
            // ========== 物品 → 金币 ==========
            var totalGold = item[2] * qty;

            // 物品数量检查
            var owned = cm.getItemQuantity(item[0]);
            if (owned < qty) {
                cm.sendOk("物品不足！拥有 #r" + owned + "个#k，需要 #r" + qty + "个#k。");
                cm.dispose();
                return;
            }

            // 扣除物品
            cm.gainItem(item[0], -qty);
            // 给予金币
            cm.gainMeso(totalGold);

            // 地图广播
            cm.mapMessage(6, "[" + cm.getPlayer().getName() + "玩家] 用 " + qty + " 个 " + item[1] + " 兑换了 " + formatNumber(totalGold) + " 金币");
            // 后台日志
            logExchange("物品→金币", item, qty, totalGold, 0);
            // 成功提示
            cm.sendOk("#b兑换成功！#k\r\n\r\n"
                + "消耗：#i" + item[0] + "# #b" + item[1] + " ×" + qty + "#k\r\n"
                + "获得：#r" + formatNumber(totalGold) + "金币#k\r\n"
                + "当前金币：#r" + formatNumber(cm.getMeso()) + "#k");
            cm.dispose();
        }
    }
}

/**
 * 打印后台日志
 * @param {string} direction 兑换方向描述
 * @param {Array}  item      物品信息 [id, name, price]
 * @param {number} qty       数量
 * @param {number} gold      金币变化量
 * @param {number} fee       手续费
 */
function logExchange(direction, item, qty, gold, fee) {
    try {
        var Logger = Java.type('org.slf4j.LoggerFactory');
        var logger = Logger.getLogger('金币兑换');
        logger.info("========== 金币兑换 ==========");
        logger.info("方向：{}", direction);
        logger.info("玩家：{} (ID:{})", cm.getPlayer().getName(), cm.getPlayer().getId());
        logger.info("物品：{} (ID:{})", item[1], item[0]);
        logger.info("数量：{}", qty);
        logger.info("金币：{}（手续费：{}）", gold, fee);
        logger.info("兑换后金币：{}", cm.getMeso());
        logger.info("==============================");
    } catch (e) {
        // 日志失败不影响主流程
        print("[金币兑换] 日志打印异常：" + e);
    }
}

/**
 * 数字千位分隔格式化
 * @param {number} num 数字
 * @returns {string} 格式化后的字符串
 */
function formatNumber(num) {
    if (num >= 100000000) {
        return (num / 100000000).toFixed(2) + "亿";
    }
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
