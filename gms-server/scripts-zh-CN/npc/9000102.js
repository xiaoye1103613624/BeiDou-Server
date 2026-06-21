var 奖励 = "#fUI/UIWindow/Quest/reward#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var warp = -1
    var status = 0;
function start() {
    chr = cm.getPlayer();
    妖塔 = chr.getBossLog("通天塔层数1", 1);
    永久 = chr.getBossLog("通天塔永久记录", 1);

    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    if (cm.getInventory(4).isFull(0)) {
        cm.getPlayer().dropMessage(6, "#b其他栏空间不足1格.");
        cm.dispose();
        return;
    }
    if (status = 1) {
        if (cm.getMapId() >= 744000021 && cm.getMapId() <= 744000040) { //高校
			cm.openNpc(9000429,2);
        } else {
            cm.warp(253000009);
            cm.getMap(253000008).resetFully();
            // cm.getPlayer().dropMessage(6,""+奖励+"\r\n\r\n"+圆形+" #v4031250#*5,#v4310144#*5,#v4310034#*5,#v4000487#*x5,#v4310044#*x1\r\n");
            chr.setBossLog("通天塔层数1", 0, +1);
            cm.gainItem(2614006, 1); //突破30%
            cm.gainItem(3605006, 3); //女神
            cm.gainItem(4170016, 1); //彩蛋
            cm.gainItem(3994732, 1); //百万金币
            if (妖塔 >= 永久) {
                chr.setBossLog("通天塔永久记录", 1, +1);
            }
			    // 新增判断条件
        if (chr.getBossLog("通天塔永久记录", 1) == 53) {
				// 检查是否已经领取过奖励
            if (!chr.getBossLog("通天塔53层奖励已领取")) {
                var hasMembership = cm.haveItem(5010019); // 检查是否持有会员凭证
                var multiplier = hasMembership ? 2 : 1; // 如果持有会员凭证，奖励翻倍

				// 发放奖励
				cm.gainItem(2460005, 2 * multiplier); // 超级正向混沌卷轴
				cm.gainItem(4310038, 3 * multiplier); // 君主币
				cm.gainItem(4001245, 3 * multiplier); // 金蛋
				cm.gainItem(2022531, 1 * multiplier); // 四叶草花语

				// 增加累计赞助点数
				var lpjfReward = 15 * multiplier;
				cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + lpjfReward); // 累计赞助

				// 提示玩家
				cm.getPlayer().dropMessage(5, "通天塔53层全部通关：累计赞助+" + lpjfReward); // 红字提示
				cm.喇叭(2, "恭喜[" + cm.getName() + "] 成功通关[通天塔53层]获得" + lpjfReward + "累计赞助与大量丰厚奖励！！！");

				// 标记奖励已领取
				chr.setBossLog("通天塔53层奖励已领取");

				// 将玩家传送到地图 910000000
				cm.warp(910000000);
			} else {
				// 如果已经领取过奖励，提示玩家
				// 将玩家传送到地图 910000000
				cm.warp(910000000);
				cm.getPlayer().dropMessage(5, "您已经领取过通天塔53层的奖励了。");
				}
			}
            cm.dispose();
        }
    }
}
