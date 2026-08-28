/*
	名字:	簡的弟弟
	地圖:	危險的第一座塔樓
	描述:	921140100
*/

function start() {
	if (cm.getMap().getAllMonstersThreadsafe().size() <= 0) {//判断地图有没有怪物
		cm.sendNext("抽泣……我，我想回家。");
	} else {
		cm.sendOk("请消灭所有怪物在来解救我。");
		cm.dispose();
}
}

function action(mode, type, selection) {
	if (mode == 1) {
		if (!cm.canHold(4032831, 1)) {
			cm.sendOk("无法收纳物品，请检查一下你的背包是否留有空位。");
			cm.dispose();
			return;
			}
		cm.gainItem(4032831, 1);
		cm.removeAll(4032858);
		cm.warp(211060200, 3);
		}
		cm.dispose();
}