/*
	名字:	航海士
	地图:	1510006 - 航海士
	功能:	起航前往列娜海峡
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.sendOk("等你准备好了再来找我。");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0) {
	cm.sendYesNo("航海士，现在要起航吗？");
    } else if (status == 1) {
	var portal = parseInt((cm.getMapId() - 141000000) / 10000) + 1;
	cm.warp(141060000, portal);
	cm.forceStartQuest();
	cm.dispose();
    }
}
