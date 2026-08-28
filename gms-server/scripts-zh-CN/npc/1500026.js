/*
	名字:	地鼠王
	地图:	妖精学院艾利涅
	描述:	1500026 - 地鼠王过场剧情
	GMS083 改写版 — 去掉 inGameDirection 动画层，保留对话骨架
*/

var status = 0;

function start() {
	cm.sendOk("#g太可怕了……呜呜，呜呜。我们只是想排练演出罢了……#k\r\n#b别担心，乌尼，会没事的……会有人来救我们的。#k\r\n#r呵呵呵，竟敢侵犯我地鼠王的领地，你们这些妖精，个头不大，可胆子倒挺大啊。#k\r\n#b请放了我们吧。我们不会再踏入这里半步。#k\r\n#r那怎么行。现在，这里马上就要成为我的王国了！并且，你们会成为我的新娘。\r\n什么，是谁！竟敢！\r\n不能继续在这黑暗的地底待下去了。我把曼德拉草释放到外面的世界也只不过是个开始。这就是地下世界的宣战！不管哪个妖精都阻止不了我，嘿嘿！#k\r\n#g呜，谁来帮帮我！……#k\r\n\r\n（回到#b萝卜田#k去找库迪）");
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
		cm.sendNextPrev("别担心，乌尼，会没事的……会有人来救我们的。");
	} else if (status == 2) {
		cm.sendNextPrev("呵呵呵，竟敢侵犯我地鼠王的领地，你们这些妖精，个头不大，可胆子倒挺大啊。");
	} else if (status == 3) {
		cm.sendNextPrev("请放了我们吧。我们不会再踏入这里半步。");
	} else if (status == 4) {
		cm.sendNextPrev("那怎么行。现在，这里马上就要成为我的王国了！并且，你们会成为我的新娘。");
	} else if (status == 5) {
		cm.sendNextPrev("什么，是谁！竟敢！");
	} else if (status == 6) {
		cm.sendNextPrev("不能继续在这黑暗的地底待下去了。我把曼德拉草释放到外面的世界也只不过是个开始。这就是地下世界的宣战！不管哪个妖精都阻止不了我，嘿嘿！");
	} else if (status == 7) {
		cm.sendNextPrev("呜，谁来帮帮我！……");
	} else if (status == 8) {
		cm.warp(101073100, 0);
		cm.dispose();
	} else {
		cm.dispose();
	}
}