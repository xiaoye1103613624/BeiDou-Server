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
		cm.sendNext("终于到了最后一关，守护第三座塔楼的#r看守莱诺#k是比在城内徘徊的其他怪物更凶残的家伙。");
		break;
	case 1:
		cm.sendNextPrev("#b把他们全部消灭掉，就能解开封印吗？");
		break;
	case 2:
		cm.sendNextPrev("是的，虽然你之前一直做得很好，但这次绝对不能放松警惕。");
		break;
	case 3:
		cm.sendPrev("别担心，快去锁匠杰恩那里拿到钥匙#v4032834:#，解开第三个封印。");
		break;
	case 4:
		cm.forceStartQuest(3141); // 开始任务
		cm.dispose();
	}
}