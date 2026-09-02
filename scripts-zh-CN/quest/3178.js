/*
名字: 会有希望吗
地图: 第五座塔楼
描述: 211061001
*/

var status = -1;

function start(mode, type, selection) {
switch (mode) {
case 0:
if (status <= 3) {
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
qm.sendSimple("请问你有见到知道这城的过往详情的人吗？什么？是#p2161002#？他曾是王的近卫骑士团长。原来他还在…嗯，那#p2161002#都有说什么吗？你有听到凡雷恩为什么变成现在这样吗？\r\n#L0##b(说明从#p2161002#听来的故事。)#l\n#k");
break;
case 1:
qm.sendSimple("对…没错，就是这样。就是黑云遮盖天空的那天，原来是如此平凡的士兵突然开始攻击我们。城墙倒塌屋顶燃烧…我，我在塔中被烟雾…没错事情就是这样。根据\r\n#L0##b(#p2161001#的反应#p2161002#说的话应该是事实。)#l\n#k");
break;
case 2:
qm.sendSimple("原来是因为这样才会让凡雷恩变节。我只是一味的埋怨着凡雷恩，但是没想到这段时间，他却在极度的悲伤与愤怒中让憎恶茁壮。…我不会让他这样继续下去了。\r\n#L0##b请问你有什么方法吗？#l\n#k");
break;
case 3:
qm.sendAcceptDecline("我要亲自去找他。之前因为拥有黑暗气息的怪物，让我无法前往见面室…但是若有你相助应该就可以了。请和我一起去见面室吧！我拜托你噜！");
break;
case 4:
if (qm.getPlayerCount(921140000) == 0) {
qm.forceStartQuest();
qm.resetMap(921140000); //地图刷新
qm.warp(921140000,1);
qm.getPlayer().startMapTimeLimitTask(1200, qm.getPlayer().getMap().getReturnMap());
} else {
qm.sendOk("任务正在执行中，请尝试其他频道。");
}
qm.dispose();
}
}