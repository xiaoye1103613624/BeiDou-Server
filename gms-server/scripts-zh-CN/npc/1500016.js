/*
	名字:	学生
	地图:	妖精学院艾利涅相关
	描述:	1500016 - 地鼠王事件任务奖励（任务32128）
	GMS083 改写版
*/

var status = -1;

function start() {
			// ── 任务门控：32127未开始 或 32128已完成 则直接传走 ──
	if (cm.isQuestCompleted(32128)) {
		cm.sendNextPrev("我这辈子不会忘记你的大恩大德！");
		cm.dispose();
		return;
	}else{
	status = -1;
	action(1, 0, 0);
	}
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

	status++;

	if (status == 0) {
		cm.sendNext("你来救我们……真是太感谢了。");
	} else if (status == 1) {
		cm.sendNextPrev("我这辈子不会忘记你的大恩大德！");
	} else if (status == 2) {
			
			cm.forceCompleteQuest(32128);
			cm.warp(101073200);
			cm.gainExp(6000);
			cm.dispose();

	} else {
		cm.dispose();
	}
}