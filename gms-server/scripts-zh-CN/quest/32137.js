/* 黎明之幕
[主题副本] 艾琳妮尔仙女学院
格伦德尔，那个非常老的人
由丹妮莉丝制作
*/
var status = -1;

function start(mode, type, selection) {
if (mode == 1)
status++;
else
status--;
if (status == 0) {
qm.askAcceptDecline("你看起来状况不错。要不要接受另一个任务？我收到了#b艾琳妮尔仙女学院#k的紧急请求。");
} else if (status == 1) {
qm.sendNext("一个年轻的人类闯进了#b艾琳妮尔仙女学院#k，引起了很大的骚动。");
} else if (status == 2) {
qm.sendNextPrev("我不知道所有的细节，但我知道我们与仙女们的关系已经足够紧张了。你愿意去艾琳妮尔附近的北方森林与#b范茜#k见面吗？");
} else if (status == 3) {
qm.sendOk("范茜会带你进入仙女的领地。如果你愿意，我可以直接把你送到他那里。'");
} else if (status == 4) {
qm.warp(101020000, 0);
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
qm.sendNext("你是我邀请来帮助艾琳妮尔仙女学院骚动的人吗？");
} else if (status == 1) {
qm.sendNextPrev("嗯，当然是我吗？");
} else if (status == 2) {
qm.sendNextPrev("你看起来没有我期望的那么强大。但是，你很有名，所以我会把任务交给你。");
} else if (status == 3) {
qm.forceCompleteQuest();
qm.dispose();
}
}