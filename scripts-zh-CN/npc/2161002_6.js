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
		status--;
		break;
	case 1:
		status++;
		break;
	}
	switch (status) {
	case 0:
		if (cm.getInfoQuest(3141).equals("clear")) { // 检查玩家是否已经完成相关任务
			cm.sendNext("你真的……把第三个封印也解开了吗？经过漫长的等待，通往忠诚的誓言的路终于打开了。");
		} else {
			cm.sendOk("要想穿过这扇门，必须拿到#v4032834:#，把第三座塔的#r看守莱诺#k全部消灭掉。");
			cm.dispose();
		}
		break;
	case 1:
		cm.sendNextPrev("#b忠诚的誓言……你是说狮子王的事情吗？");
		break;
	case 2:
		cm.sendNextPrev("那我就在第三个封印那边等着你，请一定要注意安全……");
		break;
	case 3:
		cm.sendPrev("我在第四座塔中，现在已经没有封印阻挡你了，请过来找我，小心路上的怪物，希望能尽快亲眼见到你……");
		break;
	case 4:
		cm.forceCompleteQuest(3141); // 完成任务
		cm.gainItem(4032834, -1); // 消耗钥匙
		cm.dispose();
	}
}