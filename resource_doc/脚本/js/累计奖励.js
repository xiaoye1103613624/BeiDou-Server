
var status = 0;

var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 奖励物品 = "#v1302000#";
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 累计积分 = "#fEffect/CharacterEff1.img/QQ1408745/2/5#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  

var 一星勋章 = 1143066;
var 二星勋章 = 1143067;
var 三星勋章 = 1143068;
var 四星勋章 = 1143069;
var 五星勋章 = 1143070;
var 六星勋章 = 1143071;
var 七星勋章 = 1143072;
var 八星勋章 = 1143073;
var 九星勋章 = 1143074;
var 十星勋章 = 1143075;
var 十一星勋章 = 1143076;

function start() {
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
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            if (cm.getInventory(1).isFull(14)) {
                cm.sendOk("防止领取失败#b装备栏至少需要#k#r15个格子#k");
                cm.dispose();
            } else if (cm.getInventory(2).isFull(9)) {
                cm.sendOk("防止领取失败#b消耗栏至少需要#k#r10个格子#k");
                cm.dispose();
            } else if (cm.getInventory(3).isFull(4)) {
                cm.sendOk("防止领取失败#b设置栏至少需要#k#r5个格子#k");
                cm.dispose();
            } else if (cm.getInventory(4).isFull(9)) {
                cm.sendOk("防止领取失败#b其他栏至少需要#k#r10个格子#k");
                cm.dispose();
            } else if (cm.getInventory(5).isFull(4)) {
                cm.sendOk("防止领取失败#b特殊栏至少需要#k#r5个格子#k");
                cm.dispose();
            } else {
                var textz = "" + dd + "\r\n\t\t\t" + 累计积分 + "\r\n" + 群粉心 + "注：#b累计积分达到对应要求后请按顺序等级的领取！\r\n#r(升级装备需先把待升级装备放背包)#b祝大家游戏愉快！\t\t\t\t#b当前累计积分:#r" + cm.getPlayer().getlpjf() + "#k#l\r\n" + 群粉心 + "";

                // VIP1
                textz += "     #d#L0##e#r【VIP1】#n#b(100累计积分奖励)" + 美化new + getVIPStatus("VIP1") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143066#[#r可戴2个勋章/可升级/#k全属性100]#r(+5%伤害)#k\r\n" + xxx + "#v2022532#*1 #v2614000#*5 #v2022699#*2 #v4321010#*888 #v2711003#*20 #v2022618#*1\r\n\r\n" + 群粉心 + "";

                // VIP2
                textz += "     #d#L1##e#r【VIP2】#n#b(300累计积分奖励)" + 美化new + getVIPStatus("VIP2") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143067#[#rVIP1勋章放背包升级#k全属性200]#r(+10%伤害)#k\r\n" + xxx + "#v1902024##r[全属性20]#k " + xxx + "#v1912017##r[全属性20]#k \r\n" + xxx + "#v2614001#*1 #v2022699#*5 #v4321010#*1888 #v2711003#*50 #v4321012#*20 #v2022689#*1\r\n\r\n" + 群粉心 + "";

                // VIP3
                textz += "     #d#L2##e#r【VIP3】#n#b(500累计积分奖励)" + 美化new + getVIPStatus("VIP3") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143068#[#rVIP2勋章放背包升级#k全属性300]#r(+15%伤害)#k\r\n" + xxx + "#v1902403##r[全属性40]#k " + xxx + "#v1912403##r[全属性40]#k \r\n" + xxx + "#v2614001#*2 #v2022699#*10 #v3994731#*18 #v2711003#*100 #v4321012#*50 #v2022614*1 \r\n\r\n" + 群粉心 + "";

                // VIP4
                textz += "     #d#L3##e#r【VIP4】#n#b(1000累计积分奖励)" + 美化new + getVIPStatus("VIP4") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143069#[#rVIP3勋章放背包升级#k全属性400]#r(+20%伤害)#k\r\n" + xxx + "#v1902406##r[全属性60]#k " + xxx + "#v1912406##r[全属性60]#k " + xxx + "#v1122017##r[永久]#k\r\n" + xxx + "#v2614001#*4 #v2022699#*15 #v3994731#*28 #v2711003#*200 #v4321012#*100\r\n\r\n" + 群粉心 + "";

                // VIP5
                textz += "     #d#L4##e#r【VIP5】#n#b(2000累计积分奖励)" + 美化new + getVIPStatus("VIP5") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143070#[#rVIP4勋章放背包升级#k全属性500]#r(+25%伤害)#k\r\n" + xxx + "#v1902401##r[全属性80]#k " + xxx + "#v1912401##r[全属性80]#k " + xxx + "#v5210003##r[永久]#k\r\n" + xxx + "#v2614001#*6 #v2022699#*20 #v2711003#*300 #v4321012#*200 #v4321013#*100\r\n" + xxx + "#v2022515#*28 #v3994731#*38 #v4000487#*88 #v2022524#*3 #v2022690:#*1\r\n\r\n" + 群粉心 + "";

                // VIP6
                textz += "     #d#L5##e#r【VIP6】#n#b(3000累计积分奖励)" + 美化new + getVIPStatus("VIP6") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143071#[#rVIP5勋章放背包升级#k全属性600]#r(+30%伤害)#k\r\n" + xxx + "#v1902415##r[全属性100]#k " + xxx + "#v1912415##r[全属性100]#k " + xxx + "#v5211060##r[永久]#k\r\n" + xxx + "#v2614001#*8 #v2022699#*30 #v2711003#*400 #v4321012#*300 #v4321013#*200\r\n" + xxx + "#v2022515#*38 #v3994731#*48 #v4000487#*188 #v2022524#*6 #v2022691:#*1\r\n\r\n" + 群粉心 + "";

                // VIP7
                textz += "    #d#L6##e#r【VIP7】#n#b(5000累计积分奖励)" + 美化new + getVIPStatus("VIP7") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143072#[#rVIP6勋章放背包升级#k全属性700]#r(+35%伤害)#k\r\n" + xxx + "#v1902413##r[全属性120]#k " + xxx + "#v1912413##r[全属性120]#k " + xxx + "#v5360016##r[永久]#k\r\n" + xxx + "#v2614002#*1 #v2022699#*50 #v2711003#*500 #v4321012#*400 #v4321013#*300\r\n" + xxx + "#v2022515#*48 #v3994731#*68 #v4000487#*288 #v2022524#*10 #v2022504#*1\r\n\r\n" + 群粉心 + "";

                // VIP8
                textz += "    #d#L7##e#r【VIP8】#n#b(7000元累计积分奖励)" + 美化new + getVIPStatus("VIP8") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143073#[#rVIP7勋章放背包升级#k全属性800]#r(+40%伤害)#k\r\n" + xxx + "#v1902422##r[全属性150]" + xxx + "#v1912422##r[全属性150]#k " + xxx + "#v5680422##r[永久]#k\r\n" + xxx + "#v2614002#*2 #v2022699#*100 #v2711003#*600 #v4321012#*500 #v4321013#*400\r\n" + xxx + "#v2022515#*58 #v3994731#*88 #v4000487#*388 #v2022524#*15\r\n\r\n" + 群粉心 + "";

                // VIP9
                textz += "    #d#L8##e#r【VIP9】#n#b(10000元累计积分奖励)" + 美化new + getVIPStatus("VIP9") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143074#[#rVIP8勋章放背包升级#k全属性900]#r(+45%伤害)#k\r\n" + xxx + "#v1902336##r[全属性200]" + xxx + "#v1912336##r[全属性200]#k " + xxx + "#v5680323##r[永久]#k\r\n" + xxx + "#v2614002#*4 #v2022699#*150 #v2711003#*700 #v4321012#*600 #v4321013#*500\r\n" + xxx + "#v2022515#*68 #v3994731#*108 #v4000487#*388 #v2022524#*20\r\n\r\n" + 群粉心 + "";

                // VIP10
                textz += "   #d#L9##e#r【VIP10】#n#b(15000元累计积分奖励):" + 美化new + getVIPStatus("VIP10") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143075#[#rVIP9勋章放背包升级#k全属性1000]#r(+50%伤害)#k\r\n" + xxx + "#v1902339:#" + xxx + "#v1912339:##k[全属性500][#r套装：额外伤害+100%#k]\r\n" + xxx + " #v5220002##z5220002##r[永久]#k\r\n" + xxx + " #v2614003#*1 #v2022699#*300 #v2711003#*1000 #v4321012#*800 #v4321013#*600\r\n" + xxx + "#v2022515#*88 #v3994731#*188 #v4000487#*888#v2022524#*30\r\n\r\n" + 群粉心 + "";
				
				// VIP10
                textz += " #d#L10##e#r【王者VIP】#n#b(20000元累计积分奖励):" + 美化new + getVIPStatus("VIP11") + "#l#n#k\r\n\r\n";
                textz += "" + xxx + "#v1143076#[#rVIP10勋章放背包升级#k全属性1500]#r(+60%伤害)#k\r\n" + xxx + "#v1902347:#" + xxx + "#v1912347:##k[全属性888][#r套装：额外伤害+120%#k]\r\n" + xxx + " #v5680324##z5680324##r[永久]#k\r\n" + xxx + " #v2614003#*5 #v2460005#*200 #v2711003#*1888 #v4321012#*1600 #v4321013#*1200\r\n" + xxx + "#v2022515#*188 #v3994731#*388 #v4000487#*1888#v2022524#*50 #v2022516#*1\r\n\r\n" + 群粉心 + "";
				
			//	textz += "           #d#L99##e#r" + 小黄星 + "累计积分兑换物品" + 小黄星 + "#l#n#k\r\n\r\n";

                var 当前累计 = cm.getPlayer().getlpjf();
                if (当前累计 > 20000) {
                    textz += "           #d#L99##e#r" + 小黄星 + "累计积分兑换物品" + 小黄星 + "#l#n#k\r\n\r\n";
                }
				
                cm.sendSimple(textz);	
	}
	    	
	}else if (status == 1) {

	if (selection == 0) {
		if(cm.getPlayer().getlpjf() >= 100 && cm.getPlayer().getOneTimeLog("VIP1") == 0 ){
			
			cm.gainItem(一星勋章,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//V1勋章
			cm.gainItem(2022532,1);//T5箱子
			cm.gainItem(2614000,5);//一万破攻
			cm.gainItem(2022699,2);//高级正向
			cm.gainItem(4321010,888);//时装星星
			cm.gainItem(2711003,20);//贱人魔方
			cm.gainItem(2022618,1);
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP1累计积分奖励！！！");
            cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.getPlayer().setOneTimeLog("VIP1");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.或者你已经领取过了");
			cm.dispose();
		}	
				
	}else if (selection == 1) {
		if(cm.getPlayer().getlpjf() >= 300 && cm.getPlayer().getOneTimeLog("VIP2") == 0 && cm.haveItem(一星勋章)){
			cm.gainItem(一星勋章,-1);//扣除勋章
			cm.gainItem(二星勋章,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//V1勋章
			cm.gainItem(1902024,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//坐骑
			cm.gainItem(1912017,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//坐骑
		    cm.gainItem(2614001,1);//破功
			cm.gainItem(2022699,5);//高级混沌
			cm.gainItem(4321010,1888);//时装星星
			cm.gainItem(2711003,50);//魔方
			cm.gainItem(4321012,20);//A级锁
			cm.gainItem(2022689,1);
			cm.getPlayer().setOneTimeLog("VIP2");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP2累计积分奖励！！！");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的 #v" + 一星勋章 + "#没有放入背包\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}	 
	}else if (selection == 2) {
		if(cm.getPlayer().getlpjf() >= 500 && cm.getPlayer().getOneTimeLog("VIP3") == 0 && cm.haveItem(二星勋章) && cm.haveItem(1902024) && cm.haveItem(1912017)){
			cm.gainItem(二星勋章,-1);//KOUV2
			cm.gainItem(1902024,-1);//扣除坐骑
			cm.gainItem(1912017,-1);//扣除坐骑鞍子
			cm.gainItem(三星勋章,300,300,300,300,300,300,300,300,300,300,0,0,0,0);//V1勋章
			cm.gainItem(1902403,40,40,40,40,40,40,40,40,40,40,0,0,0,0);//坐骑
			cm.gainItem(1912403,40,40,40,40,40,40,40,40,40,40,0,0,0,0);//坐骑
		    cm.gainItem(2614001,2);//破功
			cm.gainItem(2022699,10);//高级混沌
			cm.gainItem(3994731,18);//一亿金币
			cm.gainItem(2711003,100);//魔方
			cm.gainItem(4321012,50);//A锁	
			cm.gainItem(2022614,1);		
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP3累计积分奖励！！！");
			cm.getPlayer().setOneTimeLog("VIP3");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 二星勋章 + "# #v1902024# #v1912017#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}	 
	}else if (selection == 3){
	if(cm.getPlayer().getlpjf() >= 1000 && cm.getPlayer().getOneTimeLog("VIP4") == 0 && cm.haveItem(三星勋章) && cm.haveItem(1902403) && cm.haveItem(1912403)){
			cm.gainItem(三星勋章,-1);//KOUV3
			cm.gainItem(1902403,-1);//扣除坐骑
			cm.gainItem(1912403,-1);//扣除坐骑鞍子
			cm.gainItem(四星勋章,400,400,400,400,400,400,400,400,400,400,0,0,0,0);//V1勋章
			cm.gainItem(1902406,60,60,60,60,60,60,60,60,60,60,0,0,0,0);//YANJING
			cm.gainItem(1912406,60,60,60,60,60,60,60,60,60,60,0,0,0,0);//YANJING
			cm.gainItem(1122017,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//精灵吊坠
			cm.gainItem(2614001,4);//十万破攻
			cm.gainItem(2022699,15);//高级正向
			cm.gainItem(3994731,28);//一亿金币
			cm.gainItem(2711003,200);//魔方
			cm.gainItem(4321012,100);//A锁
			cm.getPlayer().setOneTimeLog("VIP4");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP4累计积分奖励！！！");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 三星勋章 + "# #v1902403# #v1912403#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}	
	}else if (selection == 4){
	
	if(cm.getPlayer().getlpjf() >= 2000 && cm.getPlayer().getOneTimeLog("VIP5") == 0 && cm.haveItem(四星勋章) && cm.haveItem(1902406) && cm.haveItem(1912406)){
			cm.gainItem(四星勋章,-1);//KOUV4
			cm.gainItem(1902406,-1);//扣除坐骑
			cm.gainItem(1912406,-1);//扣除坐骑鞍子
			cm.gainItem(五星勋章,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//V5
			cm.gainItem(1902401,80,80,80,80,80,80,80,80,80,80,0,0,0,0);//坐骑
			cm.gainItem(1912401,80,80,80,80,80,80,80,80,80,80,0,0,0,0);//坐骑
		    cm.gainItem(5210003,1);//双倍经验卡
			cm.gainItem(2614001,6);//十万破攻
			cm.gainItem(2022699,20);//高级正向
			cm.gainItem(2711003,300);//魔方
			cm.gainItem(4321012,200);//A锁
			cm.gainItem(4321013,100);//S锁
			cm.gainItem(2022515,28);//点券币
			cm.gainItem(3994731,38);//一亿金币
			cm.gainItem(4000487,88);//暗影币
			cm.gainItem(2022524,3);//绿水灵
			cm.gainItem(2022690,1);//一键BUFF
			cm.getPlayer().setOneTimeLog("VIP5");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP5累计积分奖励！！！");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 四星勋章 + "# #v1902406# #v1912406#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}	
	}else if (selection == 5){
	if(cm.getPlayer().getlpjf() >= 3000 && cm.getPlayer().getOneTimeLog("VIP6") == 0 && cm.haveItem(五星勋章) && cm.haveItem(1902401) && cm.haveItem(1912401) && cm.haveItem(5210003)){
			cm.gainItem(五星勋章,-1);//KOUV5
			cm.gainItem(1902401,-1);//扣除坐骑
			cm.gainItem(1912401,-1);//扣除坐骑鞍子
			cm.gainItem(5210003,-1);//扣除双倍经验卡
			cm.gainItem(六星勋章,600,600,600,600,600,600,600,600,600,600,0,0,0,0);//V6
			cm.gainItem(1902415,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//坐骑
			cm.gainItem(1912415,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//坐骑
			cm.gainItem(5211060,1);//三倍经验卡
			cm.gainItem(2614001,8);//十万破攻
			cm.gainItem(2022699,30);//高级正向
			cm.gainItem(2711003,400);//魔方
			cm.gainItem(4321012,300);//A锁
			cm.gainItem(4321013,200);//S锁
			cm.gainItem(2022515,38);//点券币
			cm.gainItem(3994731,48);//一亿金币
			cm.gainItem(4000487,188);//暗影币
			cm.gainItem(2022524,6);//绿水灵
			cm.gainItem(2022691,1);//终极无限技能书
		    cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP6累计积分奖励！！！");
			cm.getPlayer().setOneTimeLog("VIP6");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 五星勋章 + "# #v1912401# #v5210003#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}	
	}else if (selection == 6){
	
	if(cm.getPlayer().getlpjf() >= 5000  && cm.getPlayer().getOneTimeLog("VIP7") == 0 && cm.haveItem(六星勋章) && cm.haveItem(1902415) && cm.haveItem(1912415)){
			cm.gainItem(六星勋章,-1);//KOUV6
			cm.gainItem(1902415,-1);//扣除坐骑
			cm.gainItem(1912415,-1);//扣除坐骑鞍子
			cm.gainItem(七星勋章,700,700,700,700,700,700,700,700,700,700,0,0,0,0);//V7
			cm.gainItem(1902413,120,120,120,120,120,120,120,120,120,120,0,0,0,0);//坐骑
			cm.gainItem(1912413,120,120,120,120,120,120,120,120,120,120,0,0,0,0);//坐骑
			cm.gainItem(5360016,1);//双倍爆率卡
			cm.gainItem(2614002,1);//百万破攻
			cm.gainItem(2022699,50);//高级正向
			cm.gainItem(2711003,500);//魔方
			cm.gainItem(4321012,400);//A锁
			cm.gainItem(4321013,300);//S锁
			cm.gainItem(2022515,48);//点券币
			cm.gainItem(3994731,68);//一亿金币
			cm.gainItem(4000487,288);//暗影币
			cm.gainItem(2022524,10);//绿水灵
			cm.gainItem(2022504,1);
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP7累计积分奖励！！！");
			cm.getPlayer().setOneTimeLog("VIP7");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 六星勋章 + "# #v1902415# #v1912415#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}	
	}else if (selection == 7){
	
	if(cm.getPlayer().getlpjf() >= 7000  && cm.getPlayer().getOneTimeLog("VIP8") == 0 && cm.haveItem(七星勋章) && cm.haveItem(1902413) && cm.haveItem(1912413) && cm.haveItem(5360016)){
			cm.gainItem(七星勋章,-1);//KOUV7
			cm.gainItem(1902413,-1);//扣除坐骑
			cm.gainItem(1912413,-1);//扣除坐骑鞍子
			cm.gainItem(5360016,-1);//扣除双倍爆率卡
			cm.gainItem(八星勋章,800,800,800,800,800,800,800,800,800,800,0,0,0,0);//V8
			cm.gainItem(1902422,150,150,150,150,150,150,150,150,150,150,0,0,0,0);//坐骑
			cm.gainItem(1912422,150,150,150,150,150,150,150,150,150,150,0,0,0,0);//坐骑
			cm.gainItem(5680422,1);//三倍爆率卡
			cm.gainItem(2614002,2);//百万破攻
			cm.gainItem(2022699,100);//高级正向
			cm.gainItem(2711003,600);//魔方
			cm.gainItem(4321012,500);//A锁
			cm.gainItem(4321013,400);//S锁
			cm.gainItem(2022515,58);//点券币
			cm.gainItem(3994731,88);//一亿金币
			cm.gainItem(4000487,388);//暗影币
			cm.gainItem(2022524,15);//绿水灵
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP8累计积分奖励！！！");
			cm.getPlayer().setOneTimeLog("VIP8");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 七星勋章 + "# #v1902413# #v1912413# #v5360016#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}
	}else if (selection == 8){
	
	if(cm.getPlayer().getlpjf() >= 10000  && cm.getPlayer().getOneTimeLog("VIP9") == 0 && cm.haveItem(八星勋章) && cm.haveItem(1902422) && cm.haveItem(1912422) && cm.haveItem(5680422)){
			cm.gainItem(八星勋章,-1);//KOUV7
			cm.gainItem(1902422,-1);//扣除凤凰
			cm.gainItem(1912422,-1);//扣除凤凰
			cm.gainItem(5680422,-1);//扣除三倍爆率卡
			cm.gainItem(九星勋章,900,900,900,900,900,900,900,900,900,900,0,0,0,0);//V9
			cm.gainItem(1902336,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//坐骑
			cm.gainItem(1912336,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//坐骑
			cm.gainItem(5680323,1);//四倍爆率卡
			cm.gainItem(2614002,4);//百万破攻
			cm.gainItem(2022699,150);//高级正向
			cm.gainItem(2711003,700);//魔方
			cm.gainItem(4321012,600);//A锁
			cm.gainItem(4321013,500);//S锁
			cm.gainItem(2022515,68);//点券币
			cm.gainItem(3994731,108);//一亿金币
			cm.gainItem(4000487,388);//暗影币
			cm.gainItem(2022524,20);//绿水灵
			cm.getPlayer().setOneTimeLog("VIP9");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP9累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 八星勋章 + "# #v1902422# #v1912422# #v5680422#没有放入背包\r\n\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
		}
		
	}else if (selection == 9){
	if(cm.getPlayer().getlpjf() >= 15000  && cm.getPlayer().getOneTimeLog("VIP10") == 0 && cm.haveItem(九星勋章) && cm.haveItem(1902336) && cm.haveItem(1912336)){
			cm.gainItem(九星勋章,-1);//KOUV7
			cm.gainItem(1902336,-1);//凤凰
			cm.gainItem(1912336,-1);//凤凰
			cm.gainItem(十星勋章,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//V10
			cm.gainItem(5220002,1);//双倍频道卡
			cm.gainItem(1902339,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//坐骑
			cm.gainItem(1912339,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//坐骑
			cm.gainItem(2614003,1);//百万破攻
			cm.gainItem(2022699,300);//高级正向
			cm.gainItem(2711003,1000);//魔方
			cm.gainItem(4321012,800);//A锁
			cm.gainItem(4321013,600);//S锁
			cm.gainItem(2022515,88);//点券币
			cm.gainItem(3994731,188);//一亿金币
			cm.gainItem(4000487,888);//暗影币
			cm.gainItem(2022524,30);//绿水灵
			cm.getPlayer().setOneTimeLog("VIP10");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP10累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 九星勋章 + "# #v1902336# #v1912336#没有放入背包\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
			}
			
	}else if (selection == 10){
	if(cm.getPlayer().getlpjf() >= 20000  && cm.getPlayer().getOneTimeLog("VIP11") == 0 && cm.haveItem(十星勋章) && cm.haveItem(1902339) && cm.haveItem(1912339) && cm.haveItem(5680323)){
			cm.gainItem(十星勋章,-1);//KOUV7
			cm.gainItem(1902339,-1);//地球坐骑
			cm.gainItem(1912339,-1);//地球鞍子
			cm.gainItem(5680323,-1);//扣除四倍爆率卡
			cm.gainItem(十一星勋章,1500,1500,1500,1500,1500,1500,1500,1500,1500,1500,0,0,0,0);//V11
			cm.gainItem(5680324,1);//四倍频道卡
			cm.gainItem(1902347,888,888,888,888,888,888,888,888,888,888,0,0,0,0);//坐骑
			cm.gainItem(1912347,888,888,888,888,888,888,888,888,888,888,0,0,0,0);//坐骑
			cm.gainItem(2614003,5);//千万破攻
			cm.gainItem(2460005,200);//高级正向
			cm.gainItem(2711003,1888);//魔方
			cm.gainItem(4321012,1600);//A锁
			cm.gainItem(4321013,1200);//S锁
			cm.gainItem(2022515,188);//点券币
			cm.gainItem(3994731,388);//一亿金币
			cm.gainItem(4000487,1888);//暗影币
			cm.gainItem(2022524,50);//绿水灵
			cm.gainItem(2022516,1);
			cm.getPlayer().setOneTimeLog("VIP11");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP11累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级的#v" + 十星勋章 + "# #v1902339# #v1912339# #v5680323#没有放入背包\r\n\r\n#d3.或者你已经领取过了");
			cm.dispose();
			}
		
		
		
		
		}else if (selection == 99){
			if(cm.getPlayer().getlpjf() >= 20000){  
				cm.dispose();
				cm.openNpc(9000444,"累计积分兑换物品");
				return; // ? 正确
				}else {
				cm.sendOk("累计积分要大于2W才行");
				cm.dispose();
				}
			}
		}
	}
}
	

// 获取VIP状态的函数
function getVIPStatus(vipLevel) {
    var player = cm.getPlayer();
    var lpjf = player.getlpjf(); // 累计积分
    var oneTimeLog = player.getOneTimeLog(vipLevel); // 是否已领取

    if (oneTimeLog == 1) {
        return " - #b已领取";
    } else if (lpjf >= getVIPRequirement(vipLevel)) {
        return " - #g可领取";
    } else {
        return " - #r未领取";
    }
}

// 获取VIP所需积分的函数
function getVIPRequirement(vipLevel) {
    switch (vipLevel) {
        case "VIP1": return 100;
        case "VIP2": return 300;
        case "VIP3": return 500;
        case "VIP4": return 1000;
        case "VIP5": return 2000;
        case "VIP6": return 3000;
        case "VIP7": return 5000;
        case "VIP8": return 7000;
        case "VIP9": return 10000;
        case "VIP10": return 15000;
		case "VIP11": return 20000;
        default: return 0;
    }
}