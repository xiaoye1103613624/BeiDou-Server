/*
	名字:	传送猫
	地图:	妖精学院艾利涅相关
	描述:	1500010
	功能:	妖精学院主题副本传送NPC
*/

function start() {
	var player = cm.getPlayer();
	var d = cm.getMapId();

	// 前置任务检查：三个任务均未开始时拒绝进入
	if (player.getQuestStatus(32100) == 0 && player.getQuestStatus(32101) == 0 && player.getQuestStatus(32156) == 0) {
		cm.sendOk("这里好像通往不欢迎人类的妖精们居住的地方。没有得到许可的话，还是不要乱闯了。");
		cm.dispose();
		return;
	}

	// 根据当前所在地图发送传送确认
	if (d == 101020000) {
		cm.sendYesNo("你要进入#e#b[主题副本:妖精学院艾利涅]#k#n吗，喵？");
	} else if (d == 101070000 || d == 101070001 || d == 101070010) {
		cm.sendYesNo("你要回到魔法密林北部吗，喵？");
	} else {
		cm.dispose();
	}
}

function action(mode, type, selection) {
	if (mode > 0) {
		var player = cm.getPlayer();
		var d = cm.getMapId();

		if (d == 101020000) {
			// 若任务32101未完成则强制开始
			if (player.getQuestStatus(32101) != 2) {
				cm.forceStartQuest(32101);
			}
			cm.warp(101070000, 0);
		} else if (d == 101070000 || d == 101070001 || d == 101070010) {
			cm.warp(101020000, 1);
		}
	}
	cm.dispose();
}
