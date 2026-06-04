/* ========= NPC 9900002 一键扫荡每日答题 ========= */
var status = 0;
var sweepKey  = "扫荡节奏大师";     // 扫荡记录键
var totalKey  = "活动节奏点";         // 答题总数键

var ex30Key0   = "节奏大师进入";       // 30题档兑换键
var ex30Key1   = "Gf节奏兑换1";       // 30题档兑换键
var ex30Key2   = "Gf节奏兑换2";       // 30题档兑换键
var ex30Key3   = "Gf节奏兑换3";       // 30题档兑换键
var ex30Key4   = "Gf节奏兑换4";       // 30题档兑换键
var ex30Key5   = "Gf节奏兑换5";       // 30题档兑换键
var ex30Key6   = "Gf节奏兑换6";       // 30题档兑换键
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
            cm.sendOk("今日「扫荡节奏大师」奖励已领取完毕（每日 1 次），请明日再来！");
            cm.dispose();
            return;
        }

        /* 2. 背包空格检查（消耗栏 3 格）*/
        if (cm.getSpace(2) < 3) {
            cm.sendOk("请保证消耗栏至少 3 个空格！");
            cm.dispose();
            return;
        }
		if (cm.getSpace(4) < 3) {
            cm.sendOk("请保证其他栏至少 3 个空格！");
            cm.dispose();
            return;
        }

        /* 3. 写入 30 题记录 */
    //    cm.getPlayer().setBossLog(totalKey, 0, 105);   // 节奏点 = 105

        /* 4. 直接发 30 题档奖励 */
        cm.gainItem(2022309, 5);
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得奖励[点券抵用置换卡*5]");
		
		cm.gainItem(4000463, 10);
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得奖励[国庆纪念币*10]");
		
		cm.gainItem(4310020, 150);		
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得奖励[怪物公园纪念币*150]");
		
		cm.gainItem(3605006, 200);
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得奖励[女神的赐福*200]");
		
		cm.gainItem(2022509, 25);
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得奖励[元宝宝箱*25]");
		
		cm.gainItem(2022524, 1);
        cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得奖励[绿水灵的礼物*1]");
        /* 5. 记录扫荡次数 */
        cm.getPlayer().setBossLog(sweepKey);
		
		cm.getPlayer().setBossLog(ex30Key0, 0, 2);           // 进入
        cm.getPlayer().setBossLog(ex30Key1);           // 写入 30 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key2);           // 写入 25 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key3);           // 写入 20 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key4);           // 写入 10 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key5);           // 写入 20 题档兑换记录
		cm.getPlayer().setBossLog(ex30Key6);           // 写入 10 题档兑换记录
		
	//	cm.喇叭(3, "恭喜[" + cm.getName() + "]一键扫荡【节奏大师】获得所有奖励");
    //    cm.sendOk("一键扫荡节奏大师完成！");
		cm.getPlayer().dropMessage(5, "一键扫荡节奏大师完成！！！");   //显示在聊天框 的红色个人提示

        cm.dispose();
    }
}