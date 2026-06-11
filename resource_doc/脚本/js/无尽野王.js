var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var 幸运草 = "#fUI/GuildMark/Mark/Plant/00003006/15#";
var 香水 = "#fUI/GuildMark/Mark/Pattern/00004008/15#";
var 中条白 = "#fUI/Basic/HScr7/disabled/base#";
var 中条蓝 = "#fUI/ChatBalloon/tutorial/w#";

var 梦幻1 = "#fUI/ChatBalloon/122/n#";
var 梦幻上1 = "#fUI/ChatBalloon/122/ne#";
var 梦幻上2 = "#fUI/ChatBalloon/122/nw#";
var 彩1 = "#fUI/ChatBalloon/122/e#";
var 彩2 = "#fUI/ChatBalloon/122/w#";

var 梦幻下 = "#fUI/ChatBalloon/122/s#";
var 梦幻下1 = "#fUI/ChatBalloon/122/se#";
var 梦幻下2 = "#fUI/ChatBalloon/122/sw#";
var 梦幻中 = "#fUI/ChatBalloon/122/head#";
var 梅花 = "#fUI/GuildMark/Mark/Animal/00002008/14#";
var 蝴蝶 = "#fUI/GuildMark/Mark/Animal/00002020/14#";
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒


function start() {
    if (cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3 && cm.getPlayer().getClient().getChannel() != 4 && cm.getPlayer().getClient().getChannel() != 8) {
        cm.sendOk( "该副本只能在1-4或8线进行挑战");
        cm.dispose();
        return;
    }
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    // 获取当前时间
    var ca = java.util.Calendar.getInstance();
    var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); // 24小时制的小时数

    // 检查是否在20:00至22:00之间
    if (hour < 1 || hour >= 22) {
        cm.sendOk("当前副本暂未开放，开启时间为#r每天1:00至22:00#k。\r\n服务器当前时间：#r" + hour +":" + minute + ":" + second + "#n#k");
        cm.dispose();
        return;
    }	
	
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
			var currentTimes = cm.getBossLog('野王试炼'); // 获取当前挑战次数
            var maxTimes = 3; // 最大挑战次数
            var selStr = "                #e#k" + 皇冠白 + " #r无尽野王挑战#n#k " + 皇冠白 + "\r\n\r\n";
            selStr += "   " + 梦幻上2 + "" + 梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 +梦幻1 +梦幻1 + 梦幻1 + 梦幻中 + 梦幻1 + 梦幻1 +梦幻1 +梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 + 梦幻1 + "" + 梦幻上1 + "\r\n";
            selStr += "    1.#b共[85关卡]每关难度增加,刷新所有野外BOSS#k\r\n";
            selStr += "    2.#b通关后,额外奖励：[快乐百宝卷100张]！#k\r\n";
            selStr += "    3.#b入场条件：需持有月卡方可进入#k\r\n";
            selStr += "    4.#b入场要求：需300万破功即可挑战！#k\r\n";
			selStr += "    5.#b时间限制[2小时],每天可进#r" + currentTimes + "#b/" + maxTimes + "次！#k\r\n";
            selStr += "    6.#b需在1.-.4或.8频道进入。#k\r\n";
			selStr += "    7.#r限时开放时间：1：00-22：00#k\r\n";
            selStr += "   " + 梦幻下2 + "" + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 +梦幻下 +梦幻下 +梦幻下 +梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + 梦幻下 + "" + 梦幻下1 + "\r\n";
            selStr += "                #L0##r" + 蝴蝶 + " #k进入挑战#k " + 蝴蝶 + "#l\r\n";
            cm.sendSimple(selStr);
        } else if (status == 1) {
            if (selection == 0) {
                // 单人挑战逻辑
                if (cm.getBossLog('野王试炼') >= 3) {
                    cm.sendOk("你已经挑战了 3 次了！");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getLevel() < 200 || cm.getPlayer().getLevel() > 255) {
                    cm.sendOk("你的等级不符合要求（200-250级）！");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getDamage() < 3000000) {
                    cm.sendOk("你的破功不足300万！");
                    cm.dispose();
                    return;
                }
				// 检查是否持有月卡道具
                if (!cm.haveItem(5010019)) {
                    cm.sendOk("你没有持有月卡，无法进入！");
                    cm.dispose();
                    return;
                }
				// 检查目标地图是否有人
				var targetMapId = 910210000; // 目标地图ID
				var targetMap = cm.getMap(targetMapId);
				if (targetMap == null || targetMap.getCharactersSize() > 0) {
				    cm.sendOk("目标地图中已经有人，请更换其他频道！");
				    cm.dispose();
				    return;
				}

				// 刷新目标地图
				targetMap.resetFully(); // 重置地图，清除所有怪物和事件

				// 传送玩家到目标地图
				cm.warp(targetMapId, 0);

				// 为个人地图设置时间限制
				cm.getPlayer().startMapTimeLimitTask(7200, cm.getChannelServer().getMapFactory().getMap(910000000)); // 限制时间为 7200 秒（2小时），超时后传送到地图 910000000

				// 记录挑战次数
				cm.setBossLog('野王试炼');

				// 发送喇叭消息
				cm.喇叭(3, "玩家：" + cm.getPlayer().getName() + "进入开始挑战[无尽野王挑战]");

				// 发送全服漂浮喇叭消息
				cm.全服漂浮喇叭("【无尽野王】：[" + cm.getPlayer().getName() + "] 开始 [无尽野王挑战]", 5121000);

				// 结束脚本
				cm.dispose();
            }
        }
    }
}