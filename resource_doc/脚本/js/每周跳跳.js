var status;
var 星期几开始 = 6;
var 开始小时 = 20;
var 开始分钟 = 54;
var 结束小时 = 20;
var 结束分钟 = 59;
var 传送地图 = 910000077;
var dt = new Date();
var nowh = dt.getHours(); //获取时
var nowm = dt.getMinutes(); //获取分
var cal = java.util.Calendar.getInstance();
var todayweek = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 1 && mode == 0) {
        cm.dispose();
        return;
    } else if (status >= 2 && mode == 0) {
        cm.sendNext("。");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) { // 2 30  -  3 30    4.00    4.40
	    if ( nowh >= 开始小时 && nowh <= 结束小时 && (nowm >= 开始分钟 && nowm < 结束分钟) && todayweek == 星期几开始) {
            var selStr = "#e#r#L1#参加开心繁华赛活动。#b#l\r\n\r\n";
		} else {	
            selStr = "活动暂未开放 ，请留意下次开启时间\r\n";
            selStr += "下次开启时间: 周 #r#e" + 星期几开始 + "  " + 开始小时 + "时" + 开始分钟 + "-" + 结束小时 + "时" + 结束分钟;
            selStr += "\r\n#n#k不要错过\r\n\r\n";

        }
		
			//selStr = "#e#r#L1#参加开心繁华赛活动。#b#l\r\n\r\n";
        selStr += "#k#n#L2#我想要回去了#l";
        cm.sendSimple(selStr);

    } else if (selection == 1) {

        cm.warp(传送地图, 0);

        cm.dispose();

    } else if (selection == 2) {
        cm.warp(910000000);
    }

}
