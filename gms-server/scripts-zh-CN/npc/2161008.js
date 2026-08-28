/*
	名字:	凡雷恩
	地图:	阴郁的见面室
	描述:	921140001
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
		cm.sendNext("你…是怎么知道这条路的？这条路只有王族、我和依菲雅知道的啊…难道你真的见过依菲雅吗？");
		break;
	case 1:
		cm.sendNextPrev("我真的见到依菲雅了，而且这次我带着依菲雅一起来了，你亲自和依菲雅说说吧。");
		break;
	case 2:
		cm.sendNextPrev("凡雷恩，你看不见我吗？你听不见我说话吗？");
		break;
	case 3:
		cm.sendNextPrev("你在胡说什么……依菲雅在哪里啊？你要耍我吗？");
		break;
	case 4:
		cm.sendNextPrev("你听不见依菲雅的声音吗？为什么…？为什么她的声音无法传递给你？");
		break;
	case 5:
		cm.sendNextPrev("说得好像真的一样，不…也许你说的是真的，说不定依菲雅真的在这里，还和我说话，但那又有什么用呢？我的手已经不再干净了…");
		break;
	case 6:
		cm.sendNextPrev("…为什么要说这么悲伤的话语…");
		break;
	case 7:
		cm.sendNextPrev("啊啊…也许是因为那件事，是因为我把我的灵魂出卖给了黑魔法师…由于我杀了太多的人，才听不见她的声音…这就是我所犯下罪孽的代价吗…");
		break;
	case 8:
		cm.sendNextPrev("认识依菲雅的人啊，请收下这个。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v4032839#  #t4032839#");
		break;
	case 9:
		if (!cm.canHold(4032839, 1)) {
			cm.sendOk("无法收纳#v4032839:#，请检查一下你的背包是否留有空位。");
			cm.dispose();
			return;
			}
		if (!cm.haveItem(4032839)) {
			cm.gainItem(4032839, 1);
			}
		cm.sendNextPrev("这个吊坠里装有很久以前宫廷画家画的依菲雅的画像…我时常看着它回忆依菲雅，但现在，这个已经不适合我了。");
		break;
	case 10:
		cm.sendNextPrev("出卖灵魂以满足复仇之心…最后什么都没有剩下，这样的我没有资格回忆她。");
		break;
	case 11:
		cm.sendNextPrev("如果能再回到当时，我会不会再做这样的决定？想过数万遍，但还是不知道答案，愤怒和虚无…选择哪一方，最终也不会有改变。");
		break;
	case 12:
		cm.sendPrev("你还是回去吧，现在我不想打架…");
		break;
	case 13:
		cm.warp(211061001, 1);
		cm.dispose();
}
}