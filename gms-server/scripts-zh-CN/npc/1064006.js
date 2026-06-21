var tknow = 0;
var typed = 0;
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE); //获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒


function start() {
    if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
        cm.getPlayer().dropMessage(6,"#b#e请保证背包所有栏位至少保留3个空格.");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3) {
        cm.sendOk("活动只能在1.2.3频道进行！");
        cm.dispose();
        return;
    }
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
			
			
			
			
			
			
			
			
			
			
			
            var text = "#b节奏大师，活动时间每晚#r19点00分-19点10分#n#d\r\n";
            text += "每连击1次获得道具#v4001126#                                                 每连击10次获得1点节奏点,获得额外物品#v2614000#*5，#v4032398#*2，#v4031065#*1\r\n\r\n";
            text += "           今日获得: #r" + cm.getBossLog("活动节奏点") + " 节奏点#b\r\n";

            text += "           #L1#" + 正方箭头 + " 开 始 游 戏#l\r\n\r\n";
            text += "           #L2#" + 正方箭头 + " 兑 换 节 奏#l\r\n\r\n\r\n";
            text += "       #b<注意>:每天0点#r清空节奏点#b，请及时兑换\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {

            if (selection == 1) {
                if (hour == 19 && (minute >= 0 && minute <= 10)) {
                    cm.openNpc(9900004, 25005);
                } else {
					
					
                    cm.getPlayer().dropMessage(6,"#b当前服务器时间:" + hour + "点" + minute + "分\r\n时间还没到哦.#r节奏大师每晚的19点00分-10分才可进行，活动开始时候前，系统会公告，请不要错过。");
                    cm.dispose();
                }

            } else if (selection == 2) {
				
				
				 if (cm.getPlayer().isGM()) {
				
				cm.getPlayer().setBossLog('筑基期',1);
				 cm.getPlayer().setBossLog('结丹任务',1)
				 cm.getPlayer().setBossLog("元婴期之力",1);
				 cm.getPlayer().setBossLog("化神之力", 1)
				 cm.getPlayer().setBossLog("合体期",1);
				cm.getPlayer().setBossLog("大乘期",1);	
				cm.getPlayer().setBossLog("炼虚期",1);	
				cm.getPlayer().setBossLog("真仙境",1,1);
				cm.getPlayer().setBossLog("飞仙境",1,1);
				cm.getPlayer().setBossLog("天仙境",1,1);
				cm.getPlayer().setBossLog("准圣境",1,1);
				cm.getPlayer().setBossLog("九转圣境",1,1);
				cm.getPlayer().setBossLog("神谕使者",1,1);
				cm.getPlayer().setBossLog("上位主神",1);
				cm.getPlayer().setBossLog("至高神",1,1);
				cm.getPlayer().setBossLog("天至尊",1,1);
				cm.getPlayer().setBossLog("主宰",1);
				cm.getPlayer().setBossLog("大主宰",1,1);
				cm.getPlayer().setBossLog("转生",1,500);				
               	   

           
            }
				
			

                cm.openNpc(9900004, 25006);
				
            }
        }
    }
}
