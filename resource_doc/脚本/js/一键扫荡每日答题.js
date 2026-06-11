/* ========= NPC 9900002 一键扫荡每日答题 ========= */
var status = 0;
var sweepKey  = "扫荡每日答题";     // 扫荡记录键
var totalKey  = "答题总数";         // 答题总数键
var rightKey  = "答题正确";         // 答对题数键
var ex30Key1   = "答题30兑换";       // 30题档兑换键
var ex30Key2   = "答题25兑换";       // 30题档兑换键
var ex30Key3   = "答题20兑换";       // 30题档兑换键
var ex30Key4   = "答题10兑换";       // 30题档兑换键
var dailyMax  = 1;                  // 每日扫荡上限

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
        /* 1. 判断是否已扫荡 */
        var used = cm.getPlayer().getBossLog(sweepKey);
        if (used >= dailyMax) {
            cm.sendOk("今日「一键扫荡答题」奖励已领取完毕（每日 1 次），请明日再来！");
            cm.dispose();
            return;
        }

        /* 2. 背包空格检查（消耗栏 3 格）*/
        if (cm.getSpace(2) < 3) {
            cm.sendOk("请保证消耗栏至少 3 个空格！");
            cm.dispose();
            return;
        }

        /* 3. 写入 30 题记录 */
        cm.getPlayer().setBossLog(totalKey, 0, 30);   // 答题总数 = 30
        cm.getPlayer().setBossLog(rightKey, 0, 30);   // 答对题数 = 30
        cm.getPlayer().setBossLog(ex30Key1);           // 写入 30 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key2);           // 写入 25 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key3);           // 写入 20 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key4);           // 写入 10 题档兑换记录

        /* 4. 直接发 30 题档奖励 */
        cm.gainItem(2022509, 10);   // 元宝宝箱 x10
        cm.gainItem(2614000, 10);    // 伤害突破秘术 x6
		cm.gainItem(4000487, 10);    // 暗影币
        cm.gainItem(4310143, 20);	//BOSS币20个
		cm.gainItem(4000463, 20);	//国庆纪念币20个
		cm.gainMeso(20000000);   //金币2000W
		cm.gainItem(3994742, 888);	//彩虹鱼

        /* 5. 记录扫荡次数 */
        cm.getPlayer().setBossLog(sweepKey);

        cm.sendOk("一键扫荡完成！\r\n\r\n" +
                  "#b答题总数：30 / 30\r\n" +
                  "#r答对题数：30 / 30\r\n\r\n" +
                  "#k获得奖励：\r\n" +
                  "#i2022509# 元宝宝箱 x10\r\n" +
                  "#i2614000# 伤害突破秘术 x10\r\n" +
				  "#i4000487# 暗影币 x10\r\n" +
				  "#i4310143# BOSS币 x20\r\n" +
				  "#i4000463# 国庆纪念币 x10\r\n" +
				  "#i4031138# 金币 x2000万\r\n" +
                  "#i3994742# 彩虹鱼 x888\r\n\r\n" +
                  "#b今日扫荡完成");
		
		cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【每日答题】 获得 所有奖励");
        cm.dispose();
    }
}