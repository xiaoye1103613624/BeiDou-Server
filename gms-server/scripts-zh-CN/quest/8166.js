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
qm.sendAcceptDecline("...那个谁呀，有空吗？有件好的事...要不要听听看哪？");
} else if (status == 1) {
qm.sendNext("咳咳咳...你还真懂事呢！街坊的人有人到处说我坏话。什么，听我的请求的话，可别去做坏事哟。");
} else if (status == 2) {
qm.sendNextPrev("在枫叶古城里头，不是有#b#o9400401##k吗？我想你去收集他所持有的#b#t04000338##k100个。");
} else if (status == 3) {
qm.sendYesNo("#b#t04000338##k里所渗入的毒可是好东西呢...咳咳咳。啊...我可不能再多说了。总之，帮我取来#b#t04000338##k100个。当然，会重重答谢你的啦。");
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