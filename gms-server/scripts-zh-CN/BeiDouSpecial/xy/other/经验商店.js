/*
 * 经验商店脚本
 * 功能：消耗角色当前经验值兑换物品
 * 每次兑换打印后台日志
 */

// ==================== 兑换配置 ====================
// [物品ID, 物品名称, 单个所需经验值]
var expItems = [
    [4032181, "逆奥银币", 500000],
];

var status = 0;
var selectedItemIdx = -1;

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
        // 显示物品列表
        var text = "\t\t#r#e< 经验商店 >#k#n\r\n\r\n";
        text += "\t\t#b消耗角色当前经验值可以兑换道具#k\r\n\r\n";
        text += "\t\t#b当前经验值：#r" + formatNumber(cm.getPlayer().getExp()) + "#k\r\n\r\n";
        for (var i = 0; i < expItems.length; i++) {
            var item = expItems[i];
            text += "#L" + i + "##i" + item[0] + "# #b" + item[1] + "#k  需要：#r" + formatNumber(item[2]) + "点经验值#k#l\r\n";
        }
        cm.sendSimple(text);

    } else if (status == 1) {
        // 输入兑换数量
        selectedItemIdx = selection;
        var item = expItems[selectedItemIdx];
        var maxByExp = Math.floor(cm.getPlayer().getExp() / item[2]);
        if (maxByExp <= 0) {
            cm.sendOk("经验值不足！兑换一个 #b" + item[1] + "#k 需要 #r" + formatNumber(item[2]) + "点经验值#k。");
            cm.dispose();
            return;
        }
        var maxQty = Math.min(maxByExp, 999);
        cm.sendGetNumber("#i" + item[0] + "# #b" + item[1] + "#k\r\n"
            + "单个需要：#r" + formatNumber(item[2]) + "点经验值#k\r\n"
            + "当前经验值：#r" + formatNumber(cm.getPlayer().getExp()) + "#k\r\n\r\n"
            + "请输入兑换数量：", 1, 1, maxQty);

    } else if (status == 2) {
        // 执行兑换
        var qty = selection;
        var item = expItems[selectedItemIdx];
        var totalCost = item[2] * qty;

        if (cm.getPlayer().getExp() < totalCost) {
            cm.sendOk("经验值不足！需要 #r" + formatNumber(totalCost) + "点经验值#k。");
            cm.dispose();
            return;
        }
        if (!cm.canHold(item[0], qty)) {
            cm.sendOk("背包空间不足，无法存放 #b" + qty + "个 " + item[1] + "#k。");
            cm.dispose();
            return;
        }

        // 扣除经验值
        cm.getPlayer().loseExp(totalCost, true, true);
        // 给予物品
        cm.gainItem(item[0], qty);

        // 后台日志
        logExchange(item, qty, totalCost);

        cm.sendOk("#b兑换成功！#k\r\n\r\n"
            + "获得：#i" + item[0] + "# #b" + item[1] + " ×" + qty + "#k\r\n"
            + "消耗：#r" + formatNumber(totalCost) + "点经验值#k\r\n"
            + "剩余经验值：#r" + formatNumber(cm.getPlayer().getExp()) + "#k");
        cm.dispose();
    }
}

/**
 * 打印后台日志
 * @param {Array}  item 物品信息 [id, name, expCost]
 * @param {number} qty  数量
 * @param {number} cost 消耗经验值
 */
function logExchange(item, qty, cost) {
    try {
        var Logger = Java.type('org.slf4j.LoggerFactory');
        var logger = Logger.getLogger('经验商店');
        logger.info("========== 经验商店兑换 ==========");
        logger.info("玩家：{} (ID:{})", cm.getPlayer().getName(), cm.getPlayer().getId());
        logger.info("物品：{} (ID:{})", item[1], item[0]);
        logger.info("数量：{}", qty);
        logger.info("消耗经验值：{}", cost);
        logger.info("剩余经验值：{}", cm.getPlayer().getExp());
        logger.info("==================================");
    } catch (e) {
        // 日志失败不影响主流程
        print("[经验商店] 日志打印异常：" + e);
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
