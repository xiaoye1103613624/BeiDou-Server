var tknow = 0;
var typed = 0;
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE); //获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒


function start() {
    if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
        cm.getPlayer().dropMessage(6,"#b#e请保证背包所有栏位至少保留3个空格.");
        cm.dispose();
        return;
    }
/*    if (cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3) {
        cm.getPlayer().dropMessage(6,"活动只能在1-3频道进行！");
        cm.dispose();
        return;
    }
	*/
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            var text = "截至目前为止，你已获得:#r" + cm.getBossLog("活动节奏点") + "节奏点#k\r\n";
			
			text += "#b#L1#" + 正方箭头 + "#v1003249#  5节奏点  #r兑换#b  #v2022309##z2022309# * 5#l\r\n\r\n";
			text += "#b#L2#" + 正方箭头 + "#v1003250# 10节奏点  #r兑换#b  #v4000463##z4000463# * 10#l\r\n\r\n";
			text += "#b#L3#" + 正方箭头 + "#v1003251# 15节奏点  #r兑换#b  #v4310020##z4310020# * 150#l\r\n\r\n";
			text += "#b#L4#" + 正方箭头 + "#v1003252# 20节奏点  #r兑换#b  #v3605006##z3605006# * 200#l\r\n\r\n";
			text += "#b#L5#" + 正方箭头 + "#v1003249# 25节奏点  #r兑换#b  #v2022509##z2022509# * 25#l\r\n\r\n";
			text += "#b#L6#" + 正方箭头 + "#v1003250# 30节奏点  #r兑换#b  #v2022524##z2022524# * 1#l\r\n\r\n";
			

            text += "\r\n";

            text += "#e#r注意：兑换奖励会扣掉相应的节奏点！每个奖项每天只能领取一次！\r\n";

            cm.sendSimple(text);

        } else if (status == 1) {

            if (selection == 1) {
                if (cm.getBossLog("Gf节奏兑换1") < 1) {
                    if (cm.getBossLog("活动节奏点") < 5) {
						cm.sendOk("抱歉，你尚未拥有5点节奏点，请继续努力！！！！");
                        cm.dispose();
                        return;
                    }
                    if (cm.getInventory(2).isFull(2)) {
                        cm.getPlayer().dropMessage(6,"请保证消耗栏与其他栏有3空格");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().setBossLog("活动节奏点", 0, -5);
                    cm.setBossLog("Gf节奏兑换1");
                    cm.gainItem(2022309, 5);
					cm.sendOk("恭喜，您使用5点节奏点兑换了#v2022309#x5..");
                    cm.喇叭(3, "恭喜[" + cm.getName() + "]在节奏大师使用节奏点兑换了[点券抵用置换卡*5]");
					cm.dispose();
                    return;
                } else {
                    cm.sendOk("抱歉，每日只能兑换一次.");
                    cm.dispose();
                    return;
                }
            } else if (selection == 2) {
                if (cm.getBossLog("Gf节奏兑换2") < 1) {
                    if (cm.getBossLog("活动节奏点") < 10) {
                        cm.sendOk("抱歉，你尚未拥有10点节奏点，请继续努力！！！！");
                        cm.dispose();
                        return;
                    }
                    if (cm.getInventory(2).isFull(2)) {
                        cm.getPlayer().dropMessage(6,"请保证消耗栏与其他栏有3空格");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().setBossLog("活动节奏点", 0, -10);
                    cm.setBossLog("Gf节奏兑换2");
                    cm.gainItem(4000463, 10);
					cm.sendOk("恭喜，您使用10点节奏点兑换了#v 4000463#x10.");
                    cm.喇叭(3, "恭喜[" + cm.getName() + "]在节奏大师使用节奏点兑换了[国庆纪念币*10]");
					cm.dispose();
					return;
                } else {
                    cm.sendOk("抱歉，每日只能兑换一次.");
                    cm.dispose();
                    return;
                }
			} else if (selection == 3) {
                if (cm.getBossLog("Gf节奏兑换3") < 1) {
                    if (cm.getBossLog("活动节奏点") < 15) {
                        cm.sendOk("抱歉，你尚未拥有15点节奏点，请继续努力！！！！");
                        cm.dispose();
                        return;
                    }
                    if (cm.getInventory(2).isFull(2)) {
                        cm.getPlayer().dropMessage(6,"请保证消耗栏与其他栏有3空格");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().setBossLog("活动节奏点", 0, -15);
                    cm.setBossLog("Gf节奏兑换3");
                    cm.gainItem(4310020, 150);
					cm.sendOk("恭喜，您使用15点节奏点兑换了#v4310020#x150.");
                    cm.喇叭(3, "恭喜[" + cm.getName() + "]在节奏大师使用节奏点兑换了怪物[公园纪念币*150]");
					cm.dispose();
					return;
                } else {
					cm.sendOk("抱歉，每日只能兑换一次.");
                    cm.dispose();
                    return;
                }
			} else if (selection == 4) {
                if (cm.getBossLog("Gf节奏兑换4") < 1) {
                    if (cm.getBossLog("活动节奏点") < 20) {
                        cm.sendOk("抱歉，你尚未拥有20点节奏点，请继续努力！！！！");
                        cm.dispose();
                        return;
                    }
                    if (cm.getInventory(2).isFull(2)) {
                        cm.getPlayer().dropMessage(6,"请保证消耗栏与其他栏有3空格");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().setBossLog("活动节奏点", 0, -20);
                    cm.setBossLog("Gf节奏兑换4");
                    cm.gainItem(3605006, 200);
					cm.sendOk("恭喜，您使用20点节奏点兑换了#v3605006#x200.");
                    cm.喇叭(3, "恭喜[" + cm.getName() + "]在节奏大师使用节奏点兑换了[女神的赐福*200]");
					cm.dispose();
					return;
                } else {
					cm.sendOk("抱歉，每日只能兑换一次.");
                    cm.dispose();
                    return;
                }
			} else if (selection == 5) {
                if (cm.getBossLog("Gf节奏兑换5") < 1) {
                    if (cm.getBossLog("活动节奏点") < 25) {
                        cm.sendOk("抱歉，你尚未拥有25点节奏点，请继续努力！！！！");
                        cm.dispose();
                        return;
                    }
                    if (cm.getInventory(2).isFull(2)) {
                        cm.getPlayer().dropMessage(6,"请保证消耗栏与其他栏有3空格");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().setBossLog("活动节奏点", 0, -25);
                    cm.setBossLog("Gf节奏兑换5");
                    cm.gainItem(2022509, 25);
					cm.sendOk("恭喜，您使用25点节奏点兑换了#v2022509#x25.");
                    cm.喇叭(3, "恭喜[" + cm.getName() + "]在节奏大师使用节奏点兑换了[元宝宝箱*25]");
					cm.dispose();
					return;
                } else {
					cm.sendOk("抱歉，每日只能兑换一次.");
                    cm.dispose();
                    return;
                }
			} else if (selection == 6) {
                if (cm.getBossLog("Gf节奏兑换6") < 1) {
                    if (cm.getBossLog("活动节奏点") < 30) {
                        cm.sendOk("抱歉，你尚未拥有30点节奏点，请继续努力！！！！");
                        cm.dispose();
                        return;
                    }
                    if (cm.getInventory(2).isFull(2)) {
                        cm.getPlayer().dropMessage(6,"请保证消耗栏与其他栏有3空格");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().setBossLog("活动节奏点", 0, -30);
                    cm.setBossLog("Gf节奏兑换6");
                    cm.gainItem(2022524, 1);
					cm.sendOk("恭喜，您使用30点节奏点兑换了#v2022524#x1.");
                    cm.喇叭(3, "恭喜[" + cm.getName() + "]在节奏大师使用节奏点兑换了[绿水灵的礼物*1]");
					cm.dispose();
					return;
                } else {
					cm.sendOk("抱歉，每日只能兑换一次.");
                    cm.dispose();
                    return;
                }
            }
        }
    }

}
