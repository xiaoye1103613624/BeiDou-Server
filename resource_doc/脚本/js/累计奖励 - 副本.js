
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
var 累计积分 = "#fEffect/CharacterEff1.img/QQ1408745/2/5#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  

var 一星勋章 = 1143175;
var 二星勋章 = 1142948;
var 三星勋章 = 1142947;
var 四星勋章 = 1142946;
var 五星勋章 = 1142945;
var 六星勋章 = 1142944;
var 七星勋章 = 1142943;
var 八星勋章 = 1142802;
var 九星勋章 = 1142803;
var 十星勋章 = 1142742;

var 情景 = 5390000;//情景喇叭
var 祝福 = 3605014;//祝福
//var 放大镜 = 2460005;//放大镜
//var 会员币 = 4000487;//会员币
//var 白嫖门票 = 5252001;//藏宝城门票
var 必成 = 2022615;//
var 抽奖币 = 4310154;
var 防爆 = 2531000;
var 高等五彩 = 4251202;
var 红武 = 2022355;//
var x11 = 2049345;//
var x12 = 2049346;//
var x13 = 2049347;//
var x14 = 2049348;//
var x15 = 2049349;
var 神秘 = 2022564;//
var 传说枫叶戒指 = 1112444;//
var 白银VIP戒指 = 1112787;//点装
var 黄金VIP戒指 = 1112786;//点装

var 童趣卷自选 = 123;//

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
	
	if (cm.getInventory(1).isFull(4)) {
		cm.sendOk("防止领取失败#b装备栏至少需要#k#r5个格子#k");
		cm.dispose();
	}else if (cm.getInventory(2).isFull(4)) {
		cm.sendOk("防止领取失败#b消耗栏至少需要#k#r5个格子#k");
		cm.dispose();

	}else {
		var textz = ""+dd+"\r\n\t\t\t"+累计积分+"\r\n"+群粉心+"注：#b累计积分达到对应要求后请按顺序等级的领取！\r\n#r(升级装备需先把待升级装备放背包)#b祝大家游戏愉快！\t\t\t\t#b当前累计积分:#r"+cm.getPlayer().getlpjf()+"#k#l\r\n"+群粉心+"";
		//\r\n\t\t\t#L100##r#v3010507#VIP技能中心#v3010507##l#n\r\n\r\n
        
        //textz += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
		
		//if(cm.getPlayer().getOneTimeLog("VIP1") == 0 ){
	    textz += "\t\t#d#L0##r#e【VIP1】#n#b(10累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";//VIP1
		
        textz += ""+xxx+"#v1143175#[#r可戴2个勋章/可升级/#k全属性100]#r(+10%爆率)#k\r\n"+xxx+"#v1003946# #v1102612# #v1082540# #v1052647# #v1132242# #v1072853##r(全属性20)\r\n"+xxx+"#v3605014#*5\r\n"+群粉心+"";//VIP1 
		//}else {"+xxx+"#s4111006#(技能中心领取)
        //textz += "";//VIP1
			//}
		//textz += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
		
		//if(cm.getPlayer().getOneTimeLog("VIP2") == 0 ){
		textz += "\t\t#d#L1##e#r【VIP2】#n#b(50累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";//300档2
		
        textz += ""+xxx+"#v1142948#[#rVIP1勋章放背包升级#k全属性200]#r(+20%爆率)#k\r\n"+xxx+"#v1002186#[全属性100]"+xxx+"#v3605014#*10"+xxx+"#v3605006#*10\r\n"+群粉心+"";//VIP2

		textz += "\t\t#d#L2##e#r【VIP3】#n#b(200累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		//500档3"+xxx+"#s3221002#(技能中心领取)\r\n
        textz += ""+xxx+"#v1142947#[#rVIP2勋章放背包升级#k全属性300]#r(+30%爆率)#k\r\n"+xxx+"#v1012289#[全属性200]"+xxx+"#v1112906#[#r+20%额外经验#k全属性200]\r\n"+xxx+"#v3605014#*20"+xxx+"#v2022511#*100\r\n"+群粉心+"";//VIP3

		textz += "\t\t#d#L3##e#r【VIP4】#n#b(500累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		//1000档4"+xxx+"#s2311003#(在技能中心领取)
		textz += ""+xxx+"#v1142946#[#rVIP3勋章放背包升级#k全属性400]#r(+40%爆率)#k\r\n"+xxx+"#v1022048#[全属性300]"+xxx+"#v3605014#*30"+xxx+"#v3605006#*20\r\n"+群粉心+"";//VIP4

		textz += "\t\t#d#L4##e#r【VIP5】#n#b(1000累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		//2500档5"+xxx+"#s9001007#(在技能中心领取)\r\n
		textz += ""+xxx+"#v1142945#[#rVIP4勋章放背包升级#k全属性500]#r(+50%爆率)#k\r\n"+xxx+"#v1082102#[全属性400]"+xxx+"#v1902403#+#v1912403#[全属性100]#r(共200)#k\r\n"+xxx+"#v3605014#*40"+xxx+"#v2022511#*200\r\n"+群粉心+"";//VIP5"+xxx+"#s1121002#(在技能中心领取)

		textz += "\t\t#d#L5##e#r【VIP6】#n#b(2000累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		//5000档6
		textz += ""+xxx+"#v1142944#[#rVIP5勋章放背包升级#k全属性600]#r(+60%爆率)#k\r\n"+xxx+"#v1112905#[#r20%戒指放背包升级#k全属性300](#r+30%额外经验#k)\r\n"+xxx+"#v1032024#[全属性500]"+xxx+"#v3605014#*50"+xxx+"#v3605006#*40"+xxx+"#v2022511#*400\r\n"+群粉心+"";//VIP6"+xxx+"#s4111002#(在技能中心领取)\r\n

		textz += "\t\t#d#L6##e#r【VIP7】#n#b(4000累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		
		textz += ""+xxx+"#v1142943#[#rVIP6勋章放背包升级#k全属性700]#r(+70%爆率)#k\r\n"+xxx+"#v1902404#+#v1912404#[#rVIP5坐骑放背包升级#k全属性200]#r(共400)#k\r\n"+xxx+"#v1102039#[全属性600]"+xxx+"#v3605014#*60"+xxx+"#v3605006#*60"+xxx+"#v2022511#*600\r\n"+群粉心+"";//VIP7//7000档7"+xxx+"#s5121003#(在技能中心领取)

		textz += "\t\t#d#L7##e#r【VIP8】#n#b(6000元累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		//9000档8
		textz += ""+xxx+"#v1142802#[#rVIP7勋章放背包升级#k全属性800]#r(+80%爆率)#k\r\n"+xxx+"#v1072153#[全属性700]"+xxx+"#v3605014#*70"+xxx+"#v3605006#*80"+xxx+"#v2022511#*800\r\n"+群粉心+"";//VIP8"+xxx+"#v2022506##r(召唤身外分身+1)#k\r\n

		textz += "\t\t#d#L8##e#r【VIP9】#n#b(8000元累计积分奖励)"+美化new+"#l#n#k\r\n\r\n";
		//12000档9
		textz += ""+xxx+"#v1142803#[#rVIP8勋章放背包升级#k全属性900]#r(+90%爆率)#k\r\n"+xxx+"#v1902402#+#v1912402#[#rVIP7坐骑放背包升级#k全属性500]#r(共1000)#k\r\n"+xxx+"#v1112947#[#r30%戒指放背包升级#k全属性500](#r+50%额外经验#k)\r\n"+xxx+"#v1702224#[全属性800]"+xxx+"#v3605014#*80\r\n"+xxx+"#v3605006#*110"+xxx+"#v2022511#*1100\r\n"+群粉心+"";//VIP9

		textz += "\t\t#d#L9##e#r【VIP10】#n#b(10000元累计积分奖励):"+美化new+"#l#n#k\r\n\r\n";
		//15000档10
		textz += ""+xxx+"#v1142742#[#rVIP9勋章放背包升级#k全属性1000]#r(+100%爆率)#k\r\n"+xxx+"#i1003624# #i1052532# #i1132187# #i1102948# #i1072695# #i1082490##r(全属性1000)#k\r\n"+xxx+"#v1050603#[全属性900]"+xxx+"#v3605014#*90\r\n"+xxx+"#v3605006#*150"+xxx+"#v2022511#*1500\r\n"+群粉心+"";//VIP10

			
		//textz += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
		//textz += "#d#L8##r20000元累计积分奖励#k\r\n#v1112905#[全属性100]#v1112928#[全属性100]#v1142796#[全属性+100]#v1802100#[全属性50]*3#v3605014#*100#v2049124#*50#v4310088#*1888#v2022511 #*200\r\n";
		//textz += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"

                cm.sendSimple (textz);  	
	}
	    	
	}else if (status == 1) {

	if (selection == 0) {
		if(cm.getPlayer().getlpjf() >= 10 && cm.getPlayer().getOneTimeLog("VIP1") == 0 ){
			
			cm.gainItem(一星勋章,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//V1勋章
			//cm.gainItem(2022511,5);//破功
			cm.gainItem(1003946,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//T4
			cm.gainItem(1102612,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//T4
			cm.gainItem(1082540,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//T4
			cm.gainItem(1052647,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//T4
			cm.gainItem(1132242,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//T4
			cm.gainItem(1072853,20,20,20,20,20,20,20,20,20,20,0,0,0,0);//T4
			cm.gainItem(3605014,5);//祝福
			//cm.gainItem(2022510,7);
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP1累计积分奖励！！！");
            cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.getPlayer().setOneTimeLog("VIP1");
			//cm.gainItem(4031332,-1);
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	
			} else if (selection == 100) {
			    cm.dispose();
            	cm.openNpc(9900004, "累计技能");
				
}else if (selection == 1) {
		if(cm.getPlayer().getlpjf() >= 50 && cm.getPlayer().getOneTimeLog("VIP2") == 0 && cm.haveItem(一星勋章)){
			cm.gainItem(一星勋章,-1);//KOUV1
			cm.gainItem(二星勋章,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//V1勋章
			cm.gainItem(1002186,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//MAOZI
		    //cm.gainItem(2022511,10);//破功
			cm.gainItem(3605014,10);//祝福
			cm.gainItem(3605006,10);//恶魔
			cm.getPlayer().setOneTimeLog("VIP2");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP2累计积分奖励！！！");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	 
}else if (selection == 2) {
	if(cm.getPlayer().getlpjf() >= 200 && cm.getPlayer().getOneTimeLog("VIP3") == 0 && cm.haveItem(二星勋章)){
			cm.gainItem(二星勋章,-1);//KOUV2
			cm.gainItem(三星勋章,300,300,300,300,300,300,300,300,300,300,0,0,0,0);//V1勋章
			cm.gainItem(1012289,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//MIANJU
			cm.gainItem(1112906,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//20%经验
		    //cm.gainItem(2022511,10);//破功
			cm.gainItem(3605014,20);//祝福
			cm.gainItem(2022511,100);//ZHENGXIANG
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP3累计积分奖励！！！");
			cm.getPlayer().setOneTimeLog("VIP3");
			
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	 
}else if (selection == 3){
	if(cm.getPlayer().getlpjf() >= 500 && cm.getPlayer().getOneTimeLog("VIP4") == 0 && cm.haveItem(三星勋章)){
			cm.gainItem(三星勋章,-1);//KOUV3
			cm.gainItem(四星勋章,400,400,400,400,400,400,400,400,400,400,0,0,0,0);//V1勋章
			cm.gainItem(1022048,300,300,300,300,300,300,300,300,300,300,0,0,0,0);//YANJING
		    //cm.gainItem(2022511,20);//破功
			cm.gainItem(3605014,30);//祝福
			cm.gainItem(3605006,20);//恶魔卷轴
			//cm.gainItem(2022506,7);
			cm.getPlayer().setOneTimeLog("VIP4");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP4累计积分奖励！！！");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	
}else if (selection == 4){
	
	if(cm.getPlayer().getlpjf() >= 1000 && cm.getPlayer().getOneTimeLog("VIP5") == 0 && cm.haveItem(四星勋章)){
			cm.gainItem(四星勋章,-1);//KOUV4
			cm.gainItem(五星勋章,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//V5
			cm.gainItem(1902403,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//XUNLU
			cm.gainItem(1912403,100,100,100,100,100,100,100,100,100,100,0,0,0,0);//XUNLU
			cm.gainItem(1082102,400,400,400,400,400,400,400,400,400,400,0,0,0,0);//SHOUTAO
		    //cm.gainItem(2022511,30);//破功
			cm.gainItem(3605014,40);//祝福
			cm.gainItem(2022511,200);//正向
			//cm.gainItem(2022513,7);
			cm.getPlayer().setOneTimeLog("VIP5");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP5累计积分奖励！！！");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	
}else if (selection == 5){
	if(cm.getPlayer().getlpjf() >= 2000 && cm.getPlayer().getOneTimeLog("VIP6") == 0 && cm.haveItem(1112906) && cm.haveItem(五星勋章)){
			cm.gainItem(五星勋章,-1);//KOUV5
			cm.gainItem(1112906,-1);//20经验戒指
			cm.gainItem(六星勋章,600,600,600,600,600,600,600,600,600,600,0,0,0,0);//V6
			cm.gainItem(1032024,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//ERHUAN
			cm.gainItem(1112905,300,300,300,300,300,300,300,300,300,300,0,0,0,0);//30经验戒指
			//cm.gainItem(1112245,100,100,100,100,1,1,100,100,1,1,1,1,5,5);//JIEZI
			//cm.gainItem(1112138,100,100,100,100,1,1,100,100,1,1,1,1,5,5);//JIEZI
		    //cm.gainItem(2022511,50);//破功
			cm.gainItem(3605014,50);//祝福
			cm.gainItem(2022511,400);//正向;
			cm.gainItem(3605006,40);//恶魔卷轴;
			//cm.gainItem(2022514,7);
			cm.getPlayer().setOneTimeLog("VIP6");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
		    cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP6累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	
}else if (selection == 6){
	
	if(cm.getPlayer().getlpjf() >= 4000  && cm.getPlayer().getOneTimeLog("VIP7") == 0 && cm.haveItem(六星勋章) && cm.haveItem(1902403) && cm.haveItem(1912403)){
			cm.gainItem(六星勋章,-1);//KOUV6
			cm.gainItem(1902403,-1);//扣驯鹿
			cm.gainItem(1912403,-1);//扣驯鹿
			cm.gainItem(七星勋章,700,700,700,700,700,700,700,700,700,700,0,0,0,0);//V7
			cm.gainItem(1102039,600,600,600,600,600,600,600,600,600,600,0,0,0,0);//披风
			cm.gainItem(1902404,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//凤凰
			cm.gainItem(1912404,200,200,200,200,200,200,200,200,200,200,0,0,0,0);//凤凰
			//cm.gainItem(1802100,50,50,50,50,1,1,50,50,1,1,1,1,5,5);//
		    //cm.gainItem(2022511,70);//破功
			cm.gainItem(3605014,60);//祝福
			cm.gainItem(2022511,600);//正向;
			cm.gainItem(3605006,60);//恶魔卷轴;
			cm.getPlayer().setOneTimeLog("VIP7");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP7累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}	
}else if (selection == 7){
	
	if(cm.getPlayer().getlpjf() >= 6000  && cm.getPlayer().getOneTimeLog("VIP8") == 0 && cm.haveItem(七星勋章)){
			cm.gainItem(七星勋章,-1);//KOUV7
			cm.gainItem(八星勋章,800,800,800,800,800,800,800,800,800,800,0,0,0,0);//V8
			cm.gainItem(1072153,700,700,700,700,700,700,700,700,700,700,0,0,0,0);//鞋子
			//cm.gainItem(1112904,100,100,100,100,100,100,100,100,1,1,1,1,5,5);//星星戒指
			//cm.gainItem(1112901,100,100,100,100,100,100,100,100,1,1,1,1,5,5);//星星戒指
		    //cm.gainItem(2022511,100);//破功
			cm.gainItem(3605014,70);//祝福
			cm.gainItem(2022511,800);//正向;
			cm.gainItem(3605006,80);//恶魔卷轴;
			cm.gainItem(2022506,1);//分身;
			cm.getPlayer().setOneTimeLog("VIP8");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP8累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}
		}else if (selection == 8){
	
	if(cm.getPlayer().getlpjf() >= 8000  && cm.getPlayer().getOneTimeLog("VIP9") == 0 && cm.haveItem(八星勋章) && cm.haveItem(1902404) && cm.haveItem(1912404) && cm.haveItem(1112905)){
			cm.gainItem(八星勋章,-1);//KOUV7
			cm.gainItem(1112905,-1);//30经验戒指
			cm.gainItem(1902404,-1);//凤凰
			cm.gainItem(1912404,-1);//凤凰
			cm.gainItem(九星勋章,900,900,900,900,900,900,900,900,900,900,0,0,0,0);//V9
			cm.gainItem(1702224,800,800,800,800,800,800,800,800,800,800,0,0,0,0);//武器
			cm.gainItem(1912402,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//麒麟
			cm.gainItem(1902402,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//麒麟
			cm.gainItem(1112947,500,500,500,500,500,500,500,500,500,500,0,0,0,0);//50经验戒指
		    //cm.gainItem(2022511,100);//破功
			cm.gainItem(3605014,80);//祝福
			cm.gainItem(2022511,1100);//正向;
			cm.gainItem(3605006,110);//恶魔卷轴;
			//cm.gainItem(5010019,1);//V箱;
			cm.getPlayer().setOneTimeLog("VIP9");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP9累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}
		}else if (selection == 9){
	
	if(cm.getPlayer().getlpjf() >= 10000  && cm.getPlayer().getOneTimeLog("VIP10") == 0 && cm.haveItem(九星勋章)){
			cm.gainItem(九星勋章,-1);//KOUV7
			cm.gainItem(十星勋章,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//V10
			cm.gainItem(1050603,900,900,900,900,900,900,900,900,900,900,0,0,0,0);//衣服
			//cm.gainItem(1112905,100,100,100,100,100,100,100,100,1,1,1,1,5,5);//星星戒指
			cm.gainItem(1003624,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//史诗套装
			cm.gainItem(1052532,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//史诗套装
			cm.gainItem(1132187,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//史诗套装
			cm.gainItem(1102948,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//史诗套装
			cm.gainItem(1072695,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//史诗套装
			cm.gainItem(1082490,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0,0,0,0);//史诗套装
		    //cm.gainItem(2022511,100);//破功
			cm.gainItem(3605014,90);//祝福
			cm.gainItem(2022511,1500);//正向;
			cm.gainItem(3605006,150);//恶魔卷轴;
			//cm.gainItem(2022511,1);//神器;
			cm.getPlayer().setOneTimeLog("VIP10");
			cm.sendOk(""+成功了+"\r\n领取成功!!!");
			cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功领取了VIP10累计积分奖励！！！");
			cm.dispose();
		}else {
			cm.sendOk("领取失败请核查原因：\r\n\r\n#b1.累计积分不足\r\n\r\n#r2.需要升级 的装备没有放入背包");
			cm.dispose();
		}
	}
	}
		}
	}