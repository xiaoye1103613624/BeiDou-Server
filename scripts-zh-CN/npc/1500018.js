/*
	名字:	学生
	地图:	妖精学院艾利涅相关
	描述:	1500018
	功能:	地鼠王事件任务奖励NPC（任务32128）
*/

var status = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	if (status == 0) {
		var c = parseInt(em.getProperty("state"));
		if (c > 1) {
			// 事件已完成，进入感谢对话流程
			cm.sendNext("你来救我们……真是太感谢了。");
		} else {
			// 事件未完成，提示先消灭地鼠王
			cm.sendOk("请消灭那个凶恶的土地鼠！\r\n#b（消灭地鼠王后，重新进行对话。）");
			cm.dispose();
		}
	} else if (status == 1) {
		cm.sendNext("我这辈子不会忘记你的大恩大德！");
	} else if (status == 2) {
		cm.forceCompleteQuest(32128);
		cm.warp(101073200, 0);
		cm.gainExp(6000);
		cm.dispose();
	} else {
		cm.dispose();
	}
}
