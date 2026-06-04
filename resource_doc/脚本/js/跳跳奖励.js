var bl = 200; // 多少点卷兑换物品
var wpid = 3994048; // 点卷兑换的物品的id
var wpsx = 720; // 小时
var mrjlid = 2050004; // 每日奖励的id
var mrjlbl = 100; // 每日奖励的倍率
var BossLog1 = '倍数兑换'; // BossLog1的状态值


function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var text = "";
            text += "#r#e每日跳跳说明:#n\r\n\r\n";
            text += "#b通过奖励:#r#v4001126#*999;#v4000313#*99;#v2614012#*5;#v2022509#*5\r\n\r\n";
        //    text += "#b中途放弃安慰奖:#r#i4001126#*199;#v4000313#*19;#v2614012#*1;#v2022509#*1\r\n\r\n";
			if (cm.getPlayer().getBossLog("每天跳跳",0) > 0) {
            text += "您已完成今天的每日跳跳任务\r\n";

			} else {
            text += "#r#e#L1#领取通过奖励#n\r\n\r\n";
			}
            cm.sendSimple(text);
        }
        else if (status == 1) {
            if (selection == 1) {
               if (cm.getPlayer().getBossLog("每天跳跳",0) ==0){
                cm.gainItem(4001126, +999);
				cm.gainItem(4000313, +99);
				cm.gainItem(2614012, +5);
				cm.gainItem(2022509, +5);
                cm.getPlayer().setBossLog("每天跳跳",0); //给一天次数记录
				cm.warp(910000000,0);
				cm.worldMessage(6,"玩家["+cm.getPlayer().getName()+"]成功完成今日跳跳任务，领到了今天的奖励");
				//cm.喇叭(4,"玩家["+cm.getPlayer().getName()+"]因为自己是手残党，放弃跳跳任务领到了今天的安慰奖");
                cm.sendOk("#r#e您已完成了今天的每日跳跳");
                cm.dispose();		
                }else{ 
                cm.sendOk("#b您已完成了今天的每日跳跳");
                cm.dispose();	 
                  }
            }else if (selection == 2) {
                if (cm.getPlayer().getBossLog("师门任务拜访")>0) {
                    cm.sendOk("你已经完成本次拜访!");
                    cm.dispose();
                    return ;
                }
           
                cm.gainItem(2460005, +10);
				cm.gainItem(261400, +10);
				cm.gainItem(2531000, +10);
                cm.getPlayer().setBossLog1("师门任务拜访"); //给一天次数记录
                cm.sendOk("今日奖励领取成功");
				cm.喇叭(4,"玩家["+cm.getPlayer().getName()+"]完成今日跳跳任务");
                cm.dispose();
            }
            else {
                cm.dispose();
            }
        }
        else {
            cm.dispose();
        }
    }
}