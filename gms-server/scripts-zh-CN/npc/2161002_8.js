var status = -1;

function start() {
action(1, 0, 0);
}

function action(mode, type, selection) {
if (mode == 1) {
status++;
} else {
if (status == 1) {
cm.sendNext("如果你想体验什么是飞侠，再来找我.");
cm.dispose();
return;
}
status--;
}
if (status == 0) {
if (cm.getQuestStatus(3143) == 1) { // Check if quest 3143 is accepted
cm.sendYesNo("比斯特委托的对狮子王城调查完成了.回去向他报告吧!");
} else {
cm.sendOk("你好像没有接受比斯特的任务呢.");
cm.dispose();
}
} else if (status == 1) {
cm.forceCompleteQuest(3143); // Force complete quest 3143
cm.dispose(); // 关闭 NPC 对话窗口
}
}