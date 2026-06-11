/* ========= NPC 9900002 一键扫荡【无尽洞穴】 ========= */
var status = 0;
var sweepKey = "扫荡无尽洞穴1";   // 本脚本自己的扫荡锁
var caveKey  = "无尽洞穴";      // 与入口脚本共用，读剩余次数
var dailyMax = 1;               // 本脚本每天只能点 1 次扫荡

/* 单次奖励配置 */
var rewardList = [
    { id: 4310100,  count: 3 },   // 积分
    { id: 3994978,  count: 3000 } // 时装图腾
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
        /* 1. 本脚本今天是否已扫荡过 */
        if (cm.getPlayer().getBossLog(sweepKey) >= dailyMax) {
            cm.sendOk("今日「扫荡无尽洞穴」奖励已领取完毕，请明日再来！");
            cm.dispose();
            return;
        }

        /* 2. 计算入口脚本剩余次数（3 次上限）*/
        var leftTimes = 3 - cm.getPlayer().getBossLog(caveKey, 0); // 返回 0~3
        if (leftTimes <= 0) {
            cm.sendOk("你今天已经没有可扫荡的【无尽洞穴】次数了！");
            cm.dispose();
            return;
        }

        /* 3. 背包检查：消耗栏至少 3 格 */
        if (cm.getSpace(4) < 3) {
            cm.sendOk("请保证其他栏至少 3 个空格！");
            cm.dispose();
            return;
        }
		if (cm.getSpace(3) < 3) {
            cm.sendOk("请保证设置栏至少 3 个空格！");
            cm.dispose();
            return;
        }

        /* 4. 发放 leftTimes 倍奖励 */
        var outItems = [];
        for each(var rw in rewardList) {
            var total = rw.count * leftTimes;
            cm.gainItem(rw.id, total);
            outItems.push("#i" + rw.id + "# #t" + rw.id + "# x " + total);
        }

        /* 5. 把入口次数全部用掉（写到 3 次）*/
        cm.getPlayer().setBossLog(caveKey, 0, leftTimes);

        /* 6. 标记本脚本已扫荡 */
        cm.getPlayer().setBossLog(sweepKey);

        /* 7. 提示 & 喇叭 */
        cm.sendOk("一键扫荡完成！\r\n\r\n" +
                  "本次按剩余次数 x " + leftTimes + " 发放：\r\n" +
                  outItems.join("\r\n") + "\r\n\r\n" +
                  "#b今日无尽洞穴扫荡完成！");
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【无尽洞穴】×" + leftTimes + " 次，获得全部奖励！");
        cm.dispose();
    }
}