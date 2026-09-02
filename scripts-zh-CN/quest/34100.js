/* 黎明之幕
[主题副本] 埃利涅尔仙子学院
黑暗领主
作者：Daenerys
*/
var status = -1;

function start(mode, type, selection) {
if (mode == 1)
status++;
else
status--;
if (status == 0) {
qm.sendAcceptDecline("…… 要怎么做才能抚慰他们的心灵呢？\r\n\r\n#b（必须完成<另一种力量，神秘力量>任务，获得神秘徽章。）");
} else if (status == 1) {
qm.sendNext("我要先说一件事。你有没有看见这个村子外围辽阔的湖水啊？村子里的人称之为忘却之湖。如果掉进去的话，就会失去所有记忆。");
} else if (status == 2) {
qm.sendNextPrev("而且…… 不知道是不是因为湖水的影响，附近的人每天在逐渐失去记忆。");
} else if (status == 3) {
qm.sendYesNo("所以，他们创造出了这记忆之树。他们把珍贵的记忆挂在树上，每天去看下，直到记忆风化消失…");
} else if (status == 4) {
	qm.sendYesNo("从村子那里的人听说树的事情后，我的心砰砰直跳。不知道树上会不会有关于我的记忆。我决定立刻去调查下那棵树。但是…");
} else if (status == 5) {
	qm.sendYesNo("村民们陷入茫然之中，连每天重复做的事情都停了下来。就连在忘却之湖上面往返的船只都停止运作了。");
} else if (status == 6) {
qm.forceCompleteQuest(34100);
//qm.warp(101020000, 0);
qm.forceStartQuest();
qm.dispose();
}
}

function end(mode, type, selection) {
if (mode == 0 && type == 0) {
status--;
} else if (mode == -1) {
qm.dispose();
return;
} else {
status++;
}
if (status == 0) {
qm.sendNext("记忆…村民们珍贵的记忆… 就纷纷散落了。");
} else if (status == 1) {
qm.sendNextPrev("不知什么原因，我的手刚触碰到那棵树…");
} else if (status == 2) {
qm.sendNextPrev("而且…… 不知道是不是因为湖水的影响，附近的人每天在逐渐失去记忆。");
} else if (status == 3) {
//qm.warp(101070000, 0);
qm.forceCompleteQuest();
qm.dispose();
}
}