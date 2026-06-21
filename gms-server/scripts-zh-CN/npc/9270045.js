function start() {
	cm.sendYesNo("你想离开这个地方吗？");
}

function action(mode, type, selection) {
    if (mode == 1) {
		cm.warp(541020700,6);
		cm.setBossLog("克雷塞尔进场");
	}
	cm.dispose();
}