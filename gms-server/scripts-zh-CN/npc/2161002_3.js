/*
	名字:	卢顿
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
		if (status >= 0) {
			cm.dispose();
			return;
		}
		status--;
		break;
	case 1:
		status++;
		break;
	}
	switch (status) {
	case 0:
		if (cm.getInfoQuest(3140).equals("clear")) { // 如果玩家已经完成任务
			cm.sendNext("看守波尔也消灭掉了，要想解开最后的封印，还需要克服更危险的难关，但是我相信你一定可以做到。");
		} else {
			cm.sendOk("要想穿过这扇门，需要拿到第二座塔的钥匙#v4032833:#，然后把第二座塔的#r看守波尔#k全部消灭掉。");
			cm.dispose();
		}
		break;
	case 1:
		cm.sendNextPrev("#b是的，我马上就去找你，请你等着我。");
		break;
	case 2:
		cm.sendPrev("那我就在第三个封印那边等着你，请一定要注意安全......");
		break;
	case 3:
		cm.forceCompleteQuest(3140); // 完成任务
		cm.gainItem(4032833, -1); // 消耗第二座塔楼的钥匙
		cm.dispose();
	}
}