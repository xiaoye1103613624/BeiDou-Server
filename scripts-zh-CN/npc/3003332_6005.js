

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
		var selStr = "#d【 #r" + cm.getServerName() + "#d 】-副本一条龙[中级]\r\n";
        selStr += "#r难度：★★\r\n";
		selStr += "#b副本要求：废弃副本-通关["+cm.getBossLog("废弃副本")+"/5]次#n\r\n"
		selStr += "          玩具副本-通关["+cm.getBossLog("玩具副本")+"/3]次#n\r\n"
		selStr += "          天空副本-通关["+cm.getBossLog("天空副本")+"/3]次#n\r\n"    
		selStr += "          毒物副本-通关["+cm.getBossLog("毒物副本")+"/2]次#n\r\n"       
		selStr += "任务奖励：#r#v2340000# x2  #v4001084# x1  #v4000175# x1 \r\n";	
		selStr += "#b任务奖励：#r元宝奖励 x2 \r\n";
		selStr += "#b任务奖励：黄金枫叶*100  枫叶*2000 混沌卷轴*2\r\n";
		selStr += "#b任务奖励：国庆币*5 淡蓝色矿石*3 点卷奖励：50000\r\n";
        selStr += "#L0#领取[中级]奖励(请保持背包足够)\r\n";
		cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            if (cm.getBossLog("废弃副本") < 5){
				cm.sendOk("废弃副本-通关["+cm.getBossLog("废弃副本")+"/5]次");
				cm.dispose();
			}else if (cm.getBossLog("玩具副本") < 3){
				cm.sendOk("玩具副本-通关["+cm.getBossLog("玩具副本")+"/3]次");
				cm.dispose();
			}else if (cm.getBossLog("天空副本") < 3){
				cm.sendOk("天空副本-通关["+cm.getBossLog("天空副本")+"/3]次");
				cm.dispose();
			}
			else if (cm.getBossLog("毒物副本") < 2){
				cm.sendOk("毒物副本-通关["+cm.getBossLog("毒物副本")+"/2]次");
				cm.dispose();
			}else if (cm.getBossLog("副本一条龙[中级]奖励") > 0){
				cm.sendOk("今日你已经领取过奖励了。");
				cm.dispose();
			}else if (cm.canHold(3992010,300) == false){
				cm.sendOk("请保证有足够的背包空间。");
				cm.dispose();
			}else{
				cm.setBossLog("副本一条龙[中级]奖励");
				cm.gainItem(4000313,100);//进阶币
				cm.gainItem(4000463,5);//国庆币
				cm.gainItem(4001197,3);//淡蓝色矿石
				cm.gainItem(4001126,2000);//枫叶
				cm.setmoneyb(2);//奖励元宝
				cm.gainItem(2049117,2);//混沌
				cm.gainItem(2340000,2);//祝福卷轴
				cm.gainItem(4000175,1);//皮亚奴斯模型
				cm.gainItem(4001084,1);//帕普拉图斯的象征
				cm.getPlayer().modifyCSPoints(1,50000, true);//给点卷20000
				cm.sendOk("领取成功~")
				cm.worldMessage(2, "[副本一条龙] : 恭喜 " + cm.getName() + " ，领取副本一条龙[中级]奖励。");
				cm.dispose();
            break;
		}
    }
}}
