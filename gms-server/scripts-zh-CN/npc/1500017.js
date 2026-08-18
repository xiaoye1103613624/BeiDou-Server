/*
    名字: 托希
    地图: 妖精学院艾利涅 - 地鼠王巢穴
    描述: 2540005 - PQ解救完成剧情 / 任务32123完结
    GMS083 改写版 — 去掉 inGameDirection 动画层，保留对话骨架
*/

var status = 0;

function start() {
		// ── 任务门控：32122 且 32123，否则直接传走 ──
	if (!cm.isQuestCompleted(32122) || !cm.isQuestStarted(32123) || cm.isQuestCompleted(32123)) {
		cm.warp(101073000, 0);
		cm.dispose();
		return;
	}
    cm.sendNext("呜呜，呜呜……真是吓死我了。");
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
        cm.sendNextPrev("我正和哥哥姐姐们一起排练演出时遭到了曼德拉草的袭击。由于我非常害怕，所以一直紧闭着双眼，等缓过神来后，发现自己在这个地方了……呜呜，呜呜。");
    } else if (status == 2) {
        cm.sendNextPrev("#b（能找到一个孩子也算是万幸了。把这个孩子送回艾利涅吧。）");
    } else if (status == 3) {
        cm.forceCompleteQuest(32123);
        cm.gainExp(3600);
        cm.warp(101073000, 0);
        cm.dispose();
    } else {
        cm.dispose();
    }
}