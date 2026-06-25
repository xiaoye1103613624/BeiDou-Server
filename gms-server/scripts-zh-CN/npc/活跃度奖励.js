/**
 * @author: 萧曵
 * @npc: 活跃度奖励
 * @description:
 *   每日活跃度积分=当日"每日活跃"13项任务完成次数之和(见 org.gms.config.DailyActiveManager)。
 *   每日4个积分阶梯(1/5/10/20点)各对应一次奖励，领取后该阶梯的点数阈值会叠加进月度累计；
 *   月度3个积分阶梯(200/300/500点)各对应一次奖励，达标且当月未领取即可领取。
 *   逻辑见 org.gms.config.ActivePointManager(Java侧门面) + org.gms.service.ActivePointService(懒重置)。
 *   货币奖励为CashShop点券(NX_CREDIT)/抵用券(MAPLE_POINT)，通过 cm.getPlayer().getCashShop().gainCash(类型,数量) 发放，
 *   与现金商店.js的扣款写法对称(正数为发放)。
 */

var ActivePointManager = Java.type("org.gms.config.ActivePointManager");

var CASH_NX_CREDIT = 1;   // 点券
var CASH_MAPLE_POINT = 2; // 抵用券

var characterId;
var overview = null;

/** 每日阶梯文案：奖励名称 */
var dailyRewardNames = ["100抵用券", "200抵用券", "300抵用券", "600点券"];
/** 月度阶梯文案：奖励名称 */
var monthlyRewardNames = ["2000点券", "6000点券", "10000点券"];

function start() {
    characterId = cm.getPlayer().getId();
    levelMain();
}

function levelMain() {
    overview = ActivePointManager.getOverview(characterId);

    var todayPoints = overview.get("todayPoints");
    var dailyThresholds = overview.get("dailyThresholds");
    var dailyClaimed = overview.get("dailyClaimed");
    var monthlyPoints = overview.get("monthlyPoints");
    var monthlyThresholds = overview.get("monthlyThresholds");
    var monthlyClaimed = overview.get("monthlyClaimed");

    var text = "#e活跃度奖励#n\r\n";
    text += "#d完成每日活跃任务即可累计积分，每日0点(自然日切换)懒重置#k\r\n\r\n";

    text += "#b今日活跃度：#k" + todayPoints + "\r\n";
    for (var i = 0; i < dailyThresholds.length; i++) {
        text += "  [" + dailyThresholds[i] + "点] " + dailyRewardNames[i];
        if (dailyClaimed[i]) {
            text += " #g(已领取)#k";
        } else if (todayPoints >= dailyThresholds[i]) {
            text += " #r(可领取)#k #L" + i + "##b[领取]#k#l";
        } else {
            text += " #9(未达标)#k";
        }
        text += "\r\n";
    }

    text += "\r\n#b本月累计活跃度：#k" + monthlyPoints + "\r\n";
    text += "#d(只有领取每日阶梯奖励才会把对应点数计入月度累计)#k\r\n";
    for (var j = 0; j < monthlyThresholds.length; j++) {
        var optionIndex = dailyThresholds.length + j;
        text += "  [" + monthlyThresholds[j] + "点] " + monthlyRewardNames[j];
        if (monthlyClaimed[j]) {
            text += " #g(已领取)#k";
        } else if (monthlyPoints >= monthlyThresholds[j]) {
            text += " #r(可领取)#k #L" + optionIndex + "##b[领取]#k#l";
        } else {
            text += " #9(未达标)#k";
        }
        text += "\r\n";
    }

    cm.sendSimple(text);
}

/** 玩家点击#L选项#后触发：选项下标0~3=每日阶梯1~4，4~6=月度阶梯1~3 */
function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }

    var dailyCount = 4;
    var result;
    if (selection < dailyCount) {
        result = ActivePointManager.claimDailyTier(characterId, selection);
    } else {
        result = ActivePointManager.claimMonthlyTier(characterId, selection - dailyCount);
    }

    if (!result.get("success")) {
        cm.sendOk("领取失败：" + result.get("message"));
        cm.dispose();
        return;
    }

    var cashType = result.get("cashType");
    var amount = result.get("amount");
    cm.getPlayer().getCashShop().gainCash(cashType, amount);

    var 货币名 = cashType == CASH_NX_CREDIT ? "点券" : "抵用券";
    cm.sendOk("领取成功！获得 #r" + amount + "#k " + 货币名);
    cm.dispose();
}
