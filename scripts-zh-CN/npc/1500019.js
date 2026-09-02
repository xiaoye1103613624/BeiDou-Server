/*
	名字:	耶波尼
	地图:	妖精学院艾利涅 - 地鼠王巢穴2
	描述:	1500019 - PQ解救2完成剧情 / 任务32126完结
	GMS083 改写版 — 去掉 inGameDirection 动画层，保留对话骨架
*/

var status = 0;

function start() {
	// ── 任务门控：32125已完成 且 32126已开始但未完成，否则直接传走 ──
	if (!cm.isQuestCompleted(32125) || !cm.isQuestStarted(32126) || cm.isQuestCompleted(32126)) {
		cm.warp(101073100, 0);
		cm.dispose();
		return;
	}
	cm.sendNext("万岁！得救啦…之前我被怪物围住，所以一动都不敢动。");
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
	if (status == 1) {
		cm.sendNextPrev("你是谁？难道你是来拯救我们的英雄吗？");
	} else if (status == 2) {
		cm.sendNextPrev("#b（失踪的孩子一共有五名。那其他孩子在哪里呢？）");
	} else if (status == 3) {
		cm.sendNextPrev("啊，对了，请救救乌尼和特勒西吧！乌尼和特勒西都是女孩子……有个骑着战车的奇怪影子嗖的一下把她们劫走了！");
	} else if (status == 4) {
		cm.sendNextPrev("#b奇怪的影子……？");
	} else if (status == 5) {
		cm.gainExp(3600);
		cm.warp(101073201, 0);
		cm.forceCompleteQuest(32126);
		cm.dispose();
	} else {
		cm.dispose();
	}
}