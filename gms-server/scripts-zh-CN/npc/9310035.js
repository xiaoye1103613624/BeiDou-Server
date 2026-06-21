var bl = 200; // 多少点卷兑换物品
var wpid = 3994048; // 点卷兑换的物品的id
var wpsx = 720; // 小时
var mrjlid = 2050004; // 每日奖励的id
var mrjlbl = 100; // 每日奖励的倍率
var bosslog = '倍数兑换'; // bosslog的状态值


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
            text += "              【我就是你要拜访的人】\r\n\r\n";
           
            text += "#L1#我要完成拜访\r\n\r\n";
            cm.sendSimple(text);
        }
        else if (status == 1) {
            if (selection == 1) {
               if (cm.getPlayer().getBossLog('师门随机任务3') ==1&& cm.getPlayer().getBossLog("师门任务拜访")==4){
                cm.sendOk("#r#e你已经成功今日拜访.");
                cm.getPlayer().setBossLog("师门任务拜访"); //给一天次数记录
                cm.dispose();		
                }else{ 
                cm.sendOk("#b你已经完成拜访请拜访下一阶段.或者你今天没有拜访任务");
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
				 
                cm.getPlayer().setBossLog("师门任务拜访"); //给一天次数记录
				
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