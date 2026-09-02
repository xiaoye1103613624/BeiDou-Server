/*
	名字:	亚凯斯特的水晶
	地图:	冰原雪域市集
	描述:	211000100
*/

var status = -1;

function start(mode, type, selection) {
	switch (mode) {
	case 0:
		if (status == 3) {
		qm.sendOk("又不是什么困难的事，你这人一点人情味都没有…啧啧…");
		qm.dispose();
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
		qm.sendNext("#b#p2161004##k的信件我已经交给他的家人了。有件事，希望你答应我的请求…");
		break;
	case 1:
		qm.sendNextPrev("目前#p2161004#的魂魄受到寒气的痛苦，是因为狮子王对#p2161004#施展的诅咒。若要解除这诅咒，必须切断狮子王与#p2161004#之间的诅咒力量…");
		break;
	case 2:
		qm.sendNextPrev("请把这水晶带去，这水晶中有我魔法的力量。若把水晶在#p2161004#所在地使用，就可以解除狮子王对#p2161004#施展的诅咒。");
		break;
	case 3:
		qm.sendAcceptDecline("第一次我把水晶免费送给你，如果遗失的话，重新制作就需要#r1000万枫币#k。好了，现在我把你送到狮子王城入口处，你快去找莫特吧。");
		break;
	case 4:
		qm.forceStartQuest();
		qm.forceCompleteQuest(3182);
		qm.gainItem(2430159, 1);
		qm.warp(211060000, 0);
		qm.dispose();
}
}

function end(mode, type, selection) {
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
		qm.sendNext("是你…我的信件已经交给亚凯斯特了吗？");
		break;
	case 1:
		qm.sendNextPrev("什么？我的诅咒解开了？怎么可能…");
		break;
	case 2:
		qm.sendNextPrev("不…原来是真的…我不会再冷了！也不疼了！还可能自由行动！哈哈哈…谢谢你…");
		break;
	case 3:
		qm.sendNextPrev("我欠了亚凯斯特一个人情…当然还有你…如果你把#b#t4310009##k或#b#t4310010##k拿给我，我就帮你兑换从这城内找到的#b装备#k或#b武器#k。");
		break;
	case 4:
		qm.sendPrev("什么？你说就免费送给你？世界是哪有这么好的事？\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#   \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 12000 exp");
		break;
	case 5:
		qm.forceCompleteQuest();
		qm.gainExp(12000);
		qm.dispose();
}
}