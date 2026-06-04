/* 
 * 脚本类型: cm
 * 脚本用途: 点卷中介
 * 脚本作者: 故事丶
 * 制作时间: 2014/12/18
 */

var status = -1;
var mydate=new Date();
var myddy=mydate.getDay();
var weekday=["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
var 星星 ="#fMap/MapHelper/weather/witch/3#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 中条猫 ="#fUI/ChatBalloon/37/n#";
var 闪星 = "#fEffect/CharacterEff/1114000/2/0#";
var ItemID  = 4000000;
var num = 100;
var ItemID1 = 4000001;
var num1 = 100;
var ItemID2 = 4000002;
var num2 = 100;
var ItemID3 = 4000003;
var num3 = 100;
var ItemID4 = 4000004;
var num4 = 100;
var ItemID5 = 4000005;
var num5 = 100;
var ItemID6 = 4000006;
var num6 = 100;
function start() {
	if (cm.getPlayer().getWeekBossLog("每周签到") > 0) {
		cm.sendOk("每日任务已完成，无法再次领取");
		cm.dispose();
	}else{
    action(1, 0, 0);
}}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                cm.sendNext("如果需要点卷中介服务在来找我吧。");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {
             if (weekday[myddy] == "星期一") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#e#r每日签到 \t"+彩虹+"#k#n#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID+"# * "+num+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L0##e提交任务";
				 cm.sendSimple(text);
             } else if (weekday[myddy] == "星期二") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#r每日签到 \t"+彩虹+"#k#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID1+"# * "+num1+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L1##e提交任务";
				 cm.sendSimple(text);
             } else if (weekday[myddy] == "星期三") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#r每日签到 \t"+彩虹+"#k#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID2+"# * "+num2+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L2##e提交任务";
				 cm.sendSimple(text);
             } else if (weekday[myddy] == "星期四") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#r每日签到 \t"+彩虹+"#k#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID3+"# * "+num3+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L3##e提交任务";
				 cm.sendSimple(text);
             } else if (weekday[myddy] == "星期五") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#r每日签到 \t"+彩虹+"#k#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID4+"# * "+num4+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L4##e提交任务";
				 cm.sendSimple(text);
             } else if (weekday[myddy] == "星期六") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#r每日签到 \t"+彩虹+"#k#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID5+"# * "+num5+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L5##e提交任务";
				 cm.sendSimple(text);
             } else if (weekday[myddy] == "星期日") {
				 var text ="#b冒险岛一条龙服务#k★#r定制各版单机+商业端#k★#d免费收徒教技术\r\n";
				     text += "\t\t\t"+彩虹+"#e#r每日签到 \t"+彩虹+"#k#n\r\n";
					 text +=""+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+中条猫+"\r\n\r\n";
					 text +="   今天签到需要提交 #v"+ItemID6+"# * "+num6+" \r\n\r\n   #r奖励如下\r\n\r\n   #v4001126#x388 #v4032398#x1 #v3605006#x1 #v4000313#x10 #v4000038#x10\r\n\r\n#L6##e提交任务";
				 cm.sendSimple(text);
				} 
				
        } else if (status == 1) {
            if (selection == 0) {
                if (cm.haveItem(4000000,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000000,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}

            
            } else if (selection == 1) {
              if (cm.haveItem(4000001,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000001,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}
					
            } else if (selection == 2) {
              if (cm.haveItem(4000002,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000002,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}
					
            } else if (selection == 3) {
              if (cm.haveItem(4000003,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000003,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}
					
            } else if (selection == 4) {
              if (cm.haveItem(4000004,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000004,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}
					
            } else if (selection == 5) {
              if (cm.haveItem(4000005,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000005,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}
					
            }  else if (selection == 6) {
               if (cm.haveItem(4000006,100) == false) {
                    cm.sendNext("道具不足。");
                    status = -1;
                } else {
					cm.gainItem(4001126,388);
					cm.gainItem(3605006,1);
					cm.gainItem(4032398,1);
					cm.gainItem(4000313,10);
					cm.gainItem(4000038,10);
					//cm.gainExp(20000)
					//cm.gainExpR(cm.getLevel()*cm.getLevel()*20);
                    cm.gainItem(4000006,-100);
					cm.getPlayer().setWeekBossLog("每周签到");
					cm.worldMessage(6,"玩家：["+cm.getName()+"]完成了草帽海贼岛每日签到！");
					cm.dispose();
					}
			
        }
    }
}
}