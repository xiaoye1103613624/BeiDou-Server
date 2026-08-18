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
			if (cm.getInfoQuest(3139).equals("clear")) {
				cm.sendNext("你解开了第一个封印，好像比我想象的更强，但是后面还需要解开两个这样的封印，才能到达我所在的地方。现在回头还来得及，怎么样？");
			} else {
				cm.sendOk("要想穿过这扇门，必须拿到第一座塔的钥匙#v4032832:#，进去把怪物的全部消灭掉，才能解开封印。");
				cm.dispose();
			}
			break;
		case 1:
			cm.sendNextPrev("#b听你这么一说，我反而更有斗志了，你等着，我马上过去。");
			break;
		case 2:
			cm.sendPrev("那我就祝你能够获胜，希望你能打败那帮邪恶的家伙。");
			break;
		case 3:
			cm.forceCompleteQuest(3139);
			cm.gainItem(4032832, -1);
			cm.dispose();
			break;
	}
}