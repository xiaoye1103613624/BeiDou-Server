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
qm.sendAcceptDecline("我的名字叫樱花，是现任城主的独生女。啊，不需要这么多礼。");
} else if (status == 1) {
qm.sendNext("为了这次的事件，给各位外来人士添了许多麻烦，深感过意不去。不过…希望千万別误会了。……现在，在城里的父亲…大人其实不是本尊。");
} else if (status == 2) {
qm.sendNextPrev("或许你们已经听说了…现在，在城里的大人，其实是#b巨大的妖怪#k所化身的假扮者。我真正的父亲…为了救我和部下们，不知道被那个妖怪引到什么地方去了，已经行踪不明了。");
} else if (status == 3) {
qm.sendYesNo("你手上拿的东西…是#p9110108#委托收集来的东西吧？　我已经听#p9110108#说过了。收集这些东西，很辛苦吧…。能够收集到那些东西，各位一定具备绝佳的技艺，希望一定要接受我的委托。能否帮我打败城里的#b巨大妖怪#k？。");
//qm.forceCompleteQuest(8166);
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
qm.sendNext("喔！这么快，已经收集到啦？...一...二...三。咳咳咳...确实有100个，我就收下了。那么...我也该给你报酬。");
} else if (status == 1) {
qm.sendNextPrev("对了对了...这次的事...如果泄漏给其他人知道...下场会怎样你自己知道了吧？咳咳咳...。好了，拿了就赶紧走吧。");
} else if (status == 2) {
qm.sendNextPrev("而且...不知道是不是因为湖水的影响，附近的人每天在逐渐失去记忆。");
} else if (status == 3) {
//qm.warp(101070000, 0);
qm.forceCompleteQuest();
qm.dispose();
}
}翻译成简体