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
		cm.sendNext("这么快就到达第二个关卡了，我就长话短说了，必须消灭第二座塔楼里的#r看守波尔#k，第二个封印才会解开。");
		break;
	case 1:
		cm.sendNextPrev("看守波尔......名字的意思好像是野猪吧？");
		break;
	case 2:
		cm.sendPrev("没错，就像名字一样，他是个像野猪一样凶残、可怕的怪物。找到之前的那个锁匠，他就会为你制作第二座塔楼的钥匙#v4032833:#，请你快去找他吧。");
		break;
	case 3:
		cm.forceStartQuest(3140);
		cm.dispose();
	}
}