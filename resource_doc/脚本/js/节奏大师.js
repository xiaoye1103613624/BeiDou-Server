var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE); //获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);

var status = -1;
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";

function start() {
    if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
		cm.sendOk("请保证背包所有栏位至少保留3个空格！！！");
        cm.dispose();
        return;
    }
/*    if (cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3) {
		cm.sendOk("活动只能在1.2.3频道进行！！！");
        cm.dispose();
        return;
    }
	*/
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            var text = "#b节奏大师，活动时间每天#r8点30分-22点30分#n#d\r\n";
            text += "每连击1次获得道具#v3994720#\r\n每连击10次获得1点节奏点,获得额外物品#v4001165#*99，#v3605006#*1\r\n\r\n";
            text += "               今日获得: #r" + cm.getBossLog("活动节奏点") + " 节奏点#b\r\n";
            text += "               #L1#" + 正方箭头 + " 开 始 游 戏#l\r\n\r\n";
            text += "               #L2#" + 正方箭头 + " 兑 换 节 奏#l\r\n\r\n\r\n";
            text += "       #b<注意>:每天0点#r清空节奏点#b，请及时兑换\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 1) {
            //    if (hour == 22 && minute >= 0 && minute <= 20) {     //设置时间
				 // 8:30 - 22:30 区间判断
                if ((hour == 8 && minute >= 30) || hour >= 8 && hour < 22 || (hour == 22 && minute <= 30)) {
                    cm.openNpc(9900004, "节奏大师开始");
                } else {
					cm.sendOk("当前服务器时间:" + hour + "点" + minute + "分\r\n活动时间：\r\n节奏大师每天#r8点30分 - 22点30分#k才可进行，请不要错过！");
                    cm.dispose();
                }
            } else if (selection == 2) {
                cm.openNpc(9900004, "节奏大师兑换");
            }
        }
    }
}