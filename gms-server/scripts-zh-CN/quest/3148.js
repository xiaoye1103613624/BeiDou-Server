/*
	名称:	卢顿
	地图:	第四座塔
	描述:	211060800
*/

var status;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	switch (mode) {
		case 0:
			status--;
			break;
		case 1:
			status++;
			break;
	}
	switch (status) {
		case 0:
			if (cm.getQuestStatus(3168) == 0) {
				cm.forceStartQuest(3168);
				cm.dispose();
			} else {
				cm.sendNext("哦，好久没有人能进到城里来了……冒险家，这里非常危险，你还是快离开吧。");
			}
			break;
		case 1:
			cm.sendNextPrev("#b……谁……？！是鬼魂吗？？？");
			break;
		case 2:
			cm.sendNextPrev("抱歉吓到你了，我是守护城堡的骑士#b卢顿#k，很久以前就死了，但是却变成了幽灵，在城里游荡。");
			break;
		case 3:
			cm.sendNextPrev("#b为什么变成了幽灵还留在城里呢？有什么必须守护的东西吗？");
			break;
		case 4:
			cm.sendPrev("详细的情况我们见面之后再说，首先，你想穿过这扇门，就必须消灭守护第一座塔的邪恶的#r红色鳄鱼兵#k，解开封印。我曾经在周围见到过一位优秀的锁匠，请你让他帮你制作第一座塔楼的钥匙。");
			break;
		case 5:
			cm.forceStartQuest(3139);
			cm.dispose();
			break;
	}
}