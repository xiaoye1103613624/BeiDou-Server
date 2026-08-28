/*
	名字:	里恩渡口船夫
	地图:	1511001 - 船夫
	功能:	前往里恩（免费）
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.sendOk("恩... 我猜你还有想在这做的事？");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0) {
	cm.sendYesNo("你想离开这里回去#b里恩#k吗？。");
    } else if (status == 1) {
	cm.sendNext("刚好我们马上就要开船了。上来吧！");
    } else if (status == 2) {
	cm.warp(141000200);
	cm.dispose();
    }
}
