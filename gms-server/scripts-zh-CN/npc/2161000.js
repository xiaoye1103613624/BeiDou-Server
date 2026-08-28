/*
	名字:	凡雷恩
	地图:	见面室
	描述:	211070100
*/

function start() {
	cm.sendAcceptDecline("你们是来打败我的勇士吗，或者你是反黑法师联盟的？不管你是谁，如果我们确定彼此的目的，就没有必要闲聊。\r\n快点，你们这些傻瓜！");
}

function action(mode, type, selection) {
	if (mode == 1 && cm.getMap().getAllMonstersThreadsafe().size() == 0) {
		cm.removeNpc(cm.getMapId(), 2161000);
		cm.spawnMob(8840010, 0, -181);
	if (!cm.getPlayer().isGM()) {
		cm.getMap().startSpeedRun();
		}
		}
	cm.dispose();
}