var 赞助中心 = "#fEffect/CharacterEff1.img/QQ1408745/1/9#";
var 一亿金币 = 4031250;
var 永久三倍经验卡 = 5211060;
var 精灵吊坠 = 1122017;
var 防爆 = 2531000;
var 放大镜 = 2460005;
var 自选黄金武器 = 2022503;
var 红武自选 = 2022355;
var 神秘自选 = 2022564;
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
var x11 = 2049345;//
var x12 = 2049346;//
var x13 = 2049347;//
var x14 = 2049348;//
var x15 = 2049349;
var 必成箱子 = 2022428;
var 祝福 = 2340000;//祝福
function start() {
    status = -1;
    action(1, 0, 0);
}
 
function action(mode, type, selection){
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("今天是个好日子");
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
	
		if (cm.getMeso() < 1000) {
		cm.sendOk("防止领取失败判断:\r\n#b装备栏至少需要#k#r4个格子#k");
		cm.dispose();
		}else if (cm.getMeso() < 1000) {
		cm.sendOk("防止领取失败判断:\r\n#b消耗栏至少需要#k#r4个格子#k");
		cm.dispose();

		}
			var dbMs = "\t\t\t"+赞助中心+"#n\r\n";
            dbMs += "	亲爱的	#d"+cm.getChar().getName()+"#k,欢迎来到冒险岛礼包中心\r\n";
			dbMs += "	今日累计充值:#r"+ cm.getBossLog("每日累计充值")+"#k#l\n\r\n";
			dbMs += "#L0##d今日累计充值28元: #v"+精灵吊坠+"##b[1天权]#k #d金币x5000万\r\n";
			dbMs += "#L1##d今日累计充值68元: #v"+x11+"#*1 #v"+一亿金币+"#x1 各星卷*1\r\n";
			dbMs += "#L2##d今日累计充值128元: #v"+放大镜+"#*2 #v"+祝福+"#*2 #v"+一亿金币+"#x2 #v"+防爆+"#*1 #v"+精灵吊坠+"#*1 #v"+永久三倍经验卡+"#*1 各星卷*2\r\n";
			dbMs += "#L3##d今日累计充值588元: #v"+放大镜+"#*20 #v"+祝福+"#*20 #v"+一亿金币+"#x10 #v"+防爆+"#*30#v"+红武自选+"#*1 各星卷*15\r\n";
			dbMs += "#L6##d今日累计充值888元: #v"+放大镜+"#*30 #v"+祝福+"#*30 #v"+一亿金币+"#x15 #v"+防爆+"#*40 各星卷*20 #v"+神秘自选+"##r\r\n";
			dbMs += "#L7##d今日累计充值2000元: #v"+放大镜+"#*80 #v"+祝福+"#*80 #v"+一亿金币+"#x40 #v"+防爆+"#*50 各星卷*50 #v"+必成箱子+"#*50\r\n";
			dbMs += "#L4##d今日累计充值5000元: #v"+放大镜+"#*200 #v"+祝福+"#*200 #v"+一亿金币+"#x60 #v"+防爆+"#*300 各星卷*200 #v"+自选黄金武器+"##r若购买8星会员可再获得一件,两件可进行属性转移,强上加强!#k\r\n";
			//dbMs += "#L5#测试增加累计充值\r\n";
            cm.sendSimple(dbMs);//这个是选项
        }else if (status == 1){
            // ...
			if(selection == 0){
				if(cm.getBossLog('每日累计充值') <= 27){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日28元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日28元礼包');
					cm.gainItem(精灵吊坠, 10, 10, 10, 10, 10, 10, 10, 10, 0, 0, 0, 0, 0, 0, 24);
					cm.gainMeso(50000000);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}
				
			} else if(selection == 1){
				if(cm.getBossLog('每日累计充值') <= 67){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日68元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日68元礼包');
					cm.gainItem(防爆,1);
					cm.gainItem(一亿金币,1);
					cm.gainItem(x11,1);
					cm.gainItem(x12,1);
					cm.gainItem(x13,1);
					cm.gainItem(x14,1);
					cm.gainItem(x15,1);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}	
			} else if(selection == 2){
				if(cm.getBossLog('每日累计充值') <= 127){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日128元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日128元礼包');
					cm.gainItem(放大镜,2);
					cm.gainItem(祝福,2);
					cm.gainItem(一亿金币,2);
					cm.gainItem(防爆,1);
					cm.gainItem(永久三倍经验卡,1);
					cm.gainItem(精灵吊坠, 10, 10, 10, 10, 10, 10, 10, 10, 0, 0, 0, 0, 0, 0);
					cm.gainItem(x11,2);
					cm.gainItem(x12,2);
					cm.gainItem(x13,2);
					cm.gainItem(x14,2);
					cm.gainItem(x15,2);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}	
			} else if(selection == 3){
				if(cm.getBossLog('每日累计充值') <= 587){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日588元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日588元礼包');
					cm.gainItem(放大镜,20);
					cm.gainItem(祝福,20);
					cm.gainItem(x11,15);
					cm.gainItem(x12,15);
					cm.gainItem(x13,15);
					cm.gainItem(x14,15);
					cm.gainItem(x15,15);
					cm.gainItem(一亿金币,10);
					cm.gainItem(防爆,30);
					cm.gainItem(红武自选,1);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}	
			} else if(selection == 4){
				if(cm.getBossLog('每日累计充值') <= 4999){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日4999元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日4999元礼包');
					cm.gainItem(放大镜,200);
					cm.gainItem(一亿金币,60);
					cm.gainItem(祝福,200);
					cm.gainItem(x11,200);
					cm.gainItem(x12,200);
					cm.gainItem(x13,200);
					cm.gainItem(x14,200);
					cm.gainItem(x15,200);
					cm.gainItem(防爆,300);
					cm.gainItem(自选黄金武器,1);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}	
			}else if(selection == 6){//累计888奖励
				if(cm.getBossLog('每日累计充值') <= 887){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日888元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日888元礼包');
					cm.gainItem(放大镜,30);
					cm.gainItem(祝福,30);
					cm.gainItem(x11,20);
					cm.gainItem(x12,20);
					cm.gainItem(x13,20);
					cm.gainItem(x14,20);
					cm.gainItem(x15,20);
					cm.gainItem(一亿金币,15);
					cm.gainItem(防爆,40);
					cm.gainItem(神秘自选,1);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}	
			}else if(selection==7){//累计2000
				if(cm.getBossLog('每日累计充值') <= 1999){
					cm.sendOk("累计充值额度不足...");
					cm.dispose();
				} else if (cm.getBossLog('每日2000元礼包') >= 1){
					cm.sendOk("#r重复领取#k,,,\r\n#d今日已领取该礼包,请明日再来#k...");
					cm.dispose();
				} else {
					cm.setBossLog('每日2000元礼包');
					cm.gainItem(放大镜,80);
					cm.gainItem(祝福,80);
					cm.gainItem(x11,50);
					cm.gainItem(x12,50);
					cm.gainItem(x13,50);
					cm.gainItem(x14,50);
					cm.gainItem(x15,50);
					cm.gainItem(一亿金币,40);
					cm.gainItem(防爆,50);
					cm.gainItem(必成箱子,50);
					cm.sendOk(""+成功了+"\r\n领取成功");
					cm.dispose();
				}	
				
			}else if(selection == 5){
				cm.getChar().setBossLog('每日累计充值',0,8888);//每次自动累加
			}
        }
    }
}

























