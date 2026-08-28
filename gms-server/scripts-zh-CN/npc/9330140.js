
var status = 0;
var zones = 0;
var selectedMap = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendYesNo("你想要回市场了？？");
    } 
	else if (status == 1) 
	{
		if(cm.判断当前地图怪物数量 != 0)
		{
			var mapid = cm.当前地图ID();
			cm.清怪();
			cm.清除地图物品(mapid);
			cm.warp(910000000,0);
			cm.dispose();
		}
		else
		{
			cm.warp(910000000,0);
			cm.dispose();
		}
    }
}	