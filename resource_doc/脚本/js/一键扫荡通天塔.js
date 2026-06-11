/* ========= NPC 9900003 一键扫荡【通天塔】 ========= */
var status = 0;
var sweepKey   = "扫荡通天塔";        // 本脚本扫荡锁
var layerKey   = "通天塔层数1";      // 已挑战层数（0~53）
var timesKey   = "通天塔次数";       // 已用进入次数（0~3）
var dailyMax   = 1;                  // 本脚本每日 1 次

/* 单层奖励（按剩余层数一次性 × N） */
var perLayerReward = [
    { id: 2614006, count: 1 },   // 突破30
    { id: 3605006, count: 3 },   // 女神
	{ id: 4170016, count: 1 },   // 彩蛋
	{ id: 3994732, count: 1 }    // 百万金币
];

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }

    if (status === 0) {
        /* 1. 本脚本今日是否已扫荡 */
        if (cm.getPlayer().getBossLog(sweepKey) >= dailyMax) {
            cm.sendOk("今日「扫荡通天塔」奖励已领取完毕，请明日再来！");
            cm.dispose();
            return;
        }

        /* 2. 必须还有进入次数才能扫荡 */
        var usedTimes = cm.getPlayer().getBossLog(timesKey, 0);
        var leftTimes = 3 - usedTimes;
        if (leftTimes <= 0) {
            cm.sendOk("你今天已经没有可扫荡的【通天塔】进入次数了！");
            cm.dispose();
            return;
        }

        /* 3. 背包空格检查（消耗栏 3 格）*/
        if (cm.getSpace(4) < 3) {
            cm.sendOk("请保证消耗栏至少 3 个空格！");
            cm.dispose();
            return;
        }

        /* 4. 计算剩余层数 = 53 - 已打层数 */
        var doneLayer = cm.getPlayer().getBossLog(layerKey, 0); // 0~53
        var leftLayer = 53 - doneLayer;                         // 剩余可爬层数
        if (leftLayer <= 0) {
            cm.sendOk("你已到达 53 层顶点，今日无需扫荡！");
            cm.dispose();
            return;
        }

        /* 5. 按剩余层数一次性发放奖励 */
        var outItems = [];
        for each(var rw in perLayerReward) {
            var total = rw.count * leftLayer;
            cm.gainItem(rw.id, total);
            outItems.push("#i" + rw.id + "# #t" + rw.id + "# x " + total);
        }
		cm.gainItem(2460005, 4); // 超级正向混沌卷轴
		cm.gainItem(4310038, 6); // 君主币
		cm.gainItem(4001245, 6); // 金蛋
		cm.gainItem(2022531, 2); // 四叶草花语
		cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + 30); // 累计赞助
		cm.getPlayer().dropMessage(5, "扫荡通天塔53层，全部通关：累计赞助 + 30"); // 红字提示
        cm.getPlayer().setBossLog(timesKey, 0, leftTimes);	//6. 把进入次数写满（禁止再进）
		
		cm.getPlayer().setBossLog(layerKey, 0, +leftLayer); // 0~53
        /* 7. 标记本脚本已扫荡 */
        cm.getPlayer().setBossLog(sweepKey);

        /* 8. 提示 & 喇叭 */
        cm.sendOk("一键扫荡完成！\r\n\r\n" + "按剩余 " + leftLayer + " 层，未挑战层数发放：\r\n" + outItems.join("\r\n") + "\r\n#i 2460005 ##t 2460005 # x 4\r\n#i 4310038 ##t 4310038 # x 6\r\n#i 4001245 ##t 4001245 # x 6\r\n#i 2022531 ##t 2022531 # x 2\r\n累计赞助 + 30\r\n");
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【通天塔】剩余 " + leftLayer + " 层，获得全部层数奖励！");
        cm.dispose();
    }
}