
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
var 蓝色小喇叭 = "#fUI/CN_Chat.img/ChattingRoom/BtVolUp/0/mouseOver/0#";  
var 热点推荐 = "#fUI/CashShop.img/CSChar/BtCoordination/normal/0#";
var 铅笔 = "#fUI/GuildBBS.img/GuildBBS/BtReply/mouseOver/0#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#";  
// 会员信息
var vipLevel = -1;//vip等级
var 一星费用 = 100;//直接办理会员价格
var 二星费用 = 200;//直接办理会员价格
var 三星费用 = 300;//直接办理会员价格
var 四星费用 = 400;//直接办理会员价格
var 五星费用 = 500;//直接办理会员价格
var 六星费用 = 1000*0.9;//直接办理会员价格
var 七星费用 = 2500*0.85;//直接办理会员价格
var 八星费用 = 5000*0.7;//直接办理会员价格

var vip1T2 = 100;//会员升级价格
var vip2T3 = 100;//会员升级价格
var vip3T4 = 100;//会员升级价格
var vip4T5 = 100;//会员升级价格
var vip5T6 = 500;//会员升级价格
var vip6T7 = 1500;//会员升级价格
var vip7T8 = 2500;//会员升级价格

// 会员升级
var 会员价格表 = [0,100,200,300,400,500,1000,2500,5000];
//var 旧会员price表 = [0,100,200,300,400,500,1000,2500];
//var 新会员price表 = [0,0,200,300,400,500,1000,2500,5000];
var 会员星级 = ["未办理","①星会员","②星会员","③星会员","④星会员","⑤星会员","⑥星会员","⑦星会员","⑧星会员"];
var 差价ArrayList = new java.util.ArrayList();
差价ArrayList.add(1);

// 赠送材料
var 必成 = 2022615;


var 精灵吊坠 = 1122017;



// 升级会员 - 办理会员奖励
var 升级材料数组 = [
	[0],[1],
	// 二星会员奖励
	[
	
		[必成,7]
		
	],
	// 三星会员奖励
	[
		
		[必成,7]
	],
	// 四星会员奖励
	[
	
		[必成,7]
	
	],
	// 五星会员奖励
	[
		[精灵吊坠,1],
		[必成,7]
	],
	// 六星会员奖励
	[
		
		[必成,7]
	],
	// 七星会员奖励
	[
		
		[必成,7]
	],
	// 八星会员奖励
	[
		
		[必成,21],
		[4000463,50]
	]
];

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
		var dbMs = "";
			dbMs += ""+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+蘑菇+"\r\n\r\n";
			dbMs += "尊敬的#r"+cm.getPlayer().getName()+"#k,您好!以下是我们的副职\r\n"
		   //vipLevel = cm.getChar().getVip();
		   
			//dbMs += "您当前的VIP等级是  #b["+会员星级[cm.getChar().getVip()]+"] #k\r\n"
              //dbMs += "#L0#给积分";
              //dbMs += "#L1#给礼包状态";
		
            dbMs += "#L10#" +蓝色小喇叭+"炼丹师      #L11#"+蓝色小喇叭+"御灵师      #L12#"+蓝色小喇叭+"溶术师\r\n\r\n";
			
			dbMs += "#L13#" +蓝色小喇叭+"我要炼丹    #L14#"+蓝色小喇叭+"我要御灵    #L15#"+蓝色小喇叭+"我要溶术\r\n\r\n";
			
			//dbMs += "#L13##r" +蓝色小喇叭+"进阶炼丹师  #L14#"+蓝色小喇叭+"进阶御灵师  #L15#"+蓝色小喇叭+"进阶溶术师\r\n\r\n";
            

			  
            cm.sendOk(dbMs); 
            cm.sendSimple(dbMs);//这个是选项
        }else if (status == 1){
			
			if (cm.getPlayer().getCSPoints(1) <0) {
				cm.sendOk("防止领取失败判断:\r\n#b装备栏至少需要#k#r3个格子#k");
				cm.dispose();
			}else if (cm.getPlayer().getCSPoints(1) <0) {
				cm.sendOk("防止领取失败判断:\r\n#b消耗栏至少需要#k#r7个格子#k");
				cm.dispose();

			}else {
				var 元宝余额 = cm.getzb();
				if(selection == 0){
					cm.gainjf(10);
				} else if (selection == 1){
					cm.设置礼包领取状态(1);//累加属性
				} else if (selection == 2){
					if(cm.getChar().getVip() >= 1){
						cm.sendOk("您已经办理了该级别会员,无法重复办理");
						cm.dispose();
					}else if(元宝余额 < 一星费用){
						cm.sendOk("余额不足...开通①星会员需要"+一星费用+"元宝");
						cm.dispose();
					}else {
						cm.sendYesNo("是否确认办理?");
						beauty = 8;
					}	
				// 下面是直接办理会员
				}else if (selection == 10){ 
					if(cm.getPlayer().getCSPoints(2) < 500000){
						cm.sendOk("你没有50万抵用券,不能选择副职。\r\n成为炼丹师:可以制作#v4440300##v4441300##v4442300##v4443300##v2022529##v2022511#");
						cm.dispose();
						 }else if(cm.getLevel() < 220){
				cm.sendOk("转生需要达到220级才可以哦");
				cm.dispose();
					}else if(cm.getPlayer().getOneTimeLog('副职') >= 1){
						cm.sendOk("您已经选择过副职无法继续");
						cm.dispose();
					}else {
						cm.sendYesNo("成为炼丹师:可以制作#v4440300##v4441300##v4442300##v4443300##v2022529##v2022511#");
						beauty = 1;
					}	
				} else if (selection == 11){
					if(cm.getPlayer().getCSPoints(2) < 500000){
						cm.sendOk("你没有50万抵用券,不能选择副职。\r\n成为御灵师:可以制作#v2049345##v2049346##v2049347##v2049348##v2049349##v2531000##v2460005#");
						cm.dispose();
						 }else if(cm.getLevel() < 220){
				cm.sendOk("转生需要达到220级才可以哦");
				cm.dispose();
					}else if(cm.getPlayer().getOneTimeLog('副职') >= 1){
						cm.sendOk("您已经选择过副职无法继续");
						cm.dispose();
					
					}else {
						cm.sendYesNo("成为御灵师:可以制作#v2049345##v2049346##v2049347##v2049348##v2049349##v2531000##v2460005#");
						beauty = 2;
					}	
				} else if (selection == 12){
					if(cm.getPlayer().getCSPoints(2) < 500000){
						cm.sendOk("你没有50万抵用券,不能选择副职。\r\n成为溶术师:可以制作#v2290448##v2290450##v2290451##v2290452##v2290453##v2290454##v2290455##v2290456##v2290457##v2290458##v2290459##v2290460##v2022530#");
						cm.dispose();
						 }else if(cm.getLevel() < 220){
				cm.sendOk("转生需要达到220级才可以哦");
				cm.dispose();
					}else if(cm.getPlayer().getOneTimeLog('副职') >= 1){
						cm.sendOk("您已经选择过副职无法继续");
						cm.dispose();
					}else {
						cm.sendYesNo("成为溶术师:可以制作#v2290448##v2290450##v2290451##v2290452##v2290453##v2290454##v2290455##v2290456##v2290457##v2290458##v2290459##v2290460##v2022530#");
						beauty = 3;
					}	
				} else if (selection == 13){
					 if(cm.getPlayer().getOneTimeLog('炼丹师') != 1){
						cm.sendOk("您还不是一阶段炼丹师:消耗8000万可以获取一套#v4440300##v4441300##v4442300##v4443300##v2022529##v2022511#");
						cm.dispose();
					}else if(cm.getMeso() < 80000000){
						cm.sendOk("您没有8000万金币不能炼丹:消耗8000万可以获取一套#v4440300##v4441300##v4442300##v4443300##v2022529##v2022511#");
						cm.dispose();	
					}else if(cm.getPlayer().getBossLog("今日炼丹")>=5){
						cm.sendOk("您今日炼丹次数已经达到5次");
						cm.dispose();
					
					}else {
						cm.sendYesNo("炼丹师:消耗8000万可以获取一套#v4440300##v4441300##v4442300##v4443300##v2022529##v2022511#");
						beauty = 4;
					}	
				} else if (selection == 14){
					if(cm.getPlayer().getOneTimeLog('御灵师') != 1){
						cm.sendOk("您还不是一阶段御灵师:2E:可以#v2049345##v2049346##v2049347##v2049348##v2049349##v2531000##v2460005#");
						cm.dispose();
					}else if(cm.getMeso() < 200000000 ){
						cm.sendOk("您没有2E不能御灵:消耗6万抵用可以#v2049345##v2049346##v2049347##v2049348##v2049349##v2531000##v2460005#");
						cm.dispose();	
					}else if(cm.getPlayer().getBossLog("今日炼丹")>=3){
						cm.sendOk("您今日炼丹次数已经达到3次");
						cm.dispose();
					
					}else {
						cm.sendYesNo("御灵师:消耗2E可以#v2049345##v2049346##v2049347##v2049348##v2049349##v2531000##v2460005#");
						beauty = 5;
					}	
				} else if (selection == 15){
					if(cm.getPlayer().getOneTimeLog('溶术师') != 1){
						cm.sendOk("您还不是一阶段溶术师:消耗10万抵用:随机获取#v2290448##v2290450##v2290451##v2290452##v2290453##v2290454##v2290455##v2290456##v2290457##v2290458##v2290459##v2290460##v2022530#");
						cm.dispose();
					}else if(cm.getPlayer().getCSPoints(2) < 100000 ){
						cm.sendOk("您没有10万抵用不能溶术:消耗10万抵用:随机获取#v2290448##v2290450##v2290451##v2290452##v2290453##v2290454##v2290455##v2290456##v2290457##v2290458##v2290459##v2290460##v2022530#");
						cm.dispose();	
					}else if(cm.getPlayer().getBossLog("今日炼丹")>=5){
						cm.sendOk("您今日炼丹次数已经达到5次");
						cm.dispose();
					
					}else {
						cm.sendYesNo("溶术师:消耗10万抵用可以随机获取#v2290448##v2290450##v2290451##v2290452##v2290453##v2290454##v2290455##v2290456##v2290457##v2290458##v2290459##v2290460##v2022530#");
						beauty = 6;
					}	
				} else if (selection == 16){
					if(cm.getChar().getVip() >= 7){
						cm.sendOk("您已经办理了该级别会员,无法重复办理");
						cm.dispose();
					}else if(元宝余额 < 七星费用){
						cm.sendOk("余额不足...开通星会员需要"+七星费用+"元宝");
						cm.dispose();
					}else {
						cm.sendYesNo("是否确认办理?");
						beauty = 7;	
					}	
				} else if (selection == 17){//升级会员
					if(cm.getChar().getVip() < 1 || cm.getChar().getVip() >= 8){
						cm.sendOk("请先办理会员\r\n或您的会员等级已经满级");
						cm.dispose();
					}else {
						var pdd = "";
						pdd += "一 #d会员价格#k:\r\n";
						pdd += ""+蓝色小喇叭+"①/②/③/④/⑤星会员\r\n"+蓝色小喇叭+"100/200/300/400/500元宝\r\n";
						pdd += ""+蓝色小喇叭+"#b⑥星会员#k #r1000元宝#k[#d开服活动9折#k]\r\n"
						pdd += ""+蓝色小喇叭+"#b⑦星会员#k #r2500元宝#k[#d开服活动8.5折#k]\r\n"
						pdd += ""+蓝色小喇叭+"#b⑧星会员#k #r5000元宝#k[#d开服活动7折#k]\r\n"
						pdd += ""+铅笔+"请输入你想要升级至的会员等级,输入数字[2-8]";
						//pdd += ""+铅笔+"#r注意:#k输入数字确认后如果元宝足额会自动办理\r\n";
						beauty = 9;
						cm.sendGetNumber(pdd, 1, 2, 8);
					}
				}
			}
				
			
        }else if(status == 2){
			if(beauty == 1){
				
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了炼丹师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了炼丹师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了炼丹师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了炼丹师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了炼丹师,霸气无比,大家恭喜TA吧!!!");
				cm.getPlayer().setOneTimeLog('炼丹师');
				cm.getPlayer().setOneTimeLog('副职');
				cm.getPlayer().modifyCSPoints(2, -500000);
				
					cm.dispose();
			}else if(beauty == 2){
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了御灵师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了御灵师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了御灵师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了御灵师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了御灵师,霸气无比,大家恭喜TA吧!!!");
				cm.getPlayer().setOneTimeLog('御灵师');
				cm.getPlayer().setOneTimeLog('副职');
				cm.getPlayer().modifyCSPoints(2, -500000);
				
					cm.dispose();
			}else if(beauty == 3){
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了溶术师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了溶术师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了溶术师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了溶术师,霸气无比,大家恭喜TA吧!!!");
				cm.喇叭(4,"["+cm.getPlayer().getName()+"]副职选择了溶术师,霸气无比,大家恭喜TA吧!!!");
				cm.getPlayer().setOneTimeLog('溶术师');
				cm.getPlayer().setOneTimeLog('副职');
				cm.getPlayer().modifyCSPoints(2, -500000);
				
					cm.dispose();
			}else if(beauty == 4){
				
						cm.getPlayer().setBossLog("今日炼丹");
						 cm.gainMeso(-80000000);
						 cm.gainItem(4440300,1);
						 cm.gainItem(4441300,1);
						 cm.gainItem(4442300,1);
						 cm.gainItem(4443300,1);
						 cm.gainItem(2022529,1);
						 cm.gainItem(2022511,1);
						 cm.喇叭(4,"["+cm.getPlayer().getName()+"]炼丹师练就了一套属性宝石,大家去抢购吧!!!");
						cm.dispose();
			}else if(beauty == 5){ 
				cm.getPlayer().setBossLog("今日炼丹");
						 cm.gainMeso(-200000000);
						 cm.gainItem(2049345,1);
						 cm.gainItem(2049346,1);
						 cm.gainItem(2049347,1);
						 cm.gainItem(2049348,1);
						 cm.gainItem(2049349,1);
						 cm.gainItem(2460005,1);
						 cm.gainItem(2531000,1);
						 cm.喇叭(4,"["+cm.getPlayer().getName()+"]御灵师练就了一套星卷加防爆,大家去抢购吧!!!");
						cm.dispose();
			}else if(beauty == 6){
				
				var chance = Math.ceil(Math.random()* 12);
				if(chance == 1){
					cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
						cm.gainItem(2290448,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就英雄终极技能书,外加迎春花语!!!");
						cm.dispose();
						
						} else if (chance == 2) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290449,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就圣骑士终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 3) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290450,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就黑骑士终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 4) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290451,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就火毒终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 5) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290452,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就冰雷终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 6) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290453,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就主教终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 7) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290454,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就神射手终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 8) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290455,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就箭神终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 9) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290456,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就侠盗终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 10) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290457,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就隐士终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 11) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290459,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就冲锋队长终极技能书,外加迎春花语!!!");
						cm.dispose();
						} else if (chance == 12) {
							cm.getPlayer().modifyCSPoints(2, -100000);
					cm.getPlayer().setBossLog("今日炼丹");
					cm.gainItem(2290460,1);
						cm.gainItem(2022530,1);
						cm.喇叭(3, "[" + cm.getPlayer().getName() + "]溶术师练就船长终极技能书,外加迎春花语!!!");
						cm.dispose();
						}
			}else if(beauty == 7){
				cm.setzb(-七星费用);
					cm.getChar().setVip(7);//set就是设置固定 gain是累加
					cm.sendOk(""+成功了+"恭喜您,业务办理成功!!!");
					cm.喇叭(4,"["+cm.getPlayer().getName()+"]开通了本服⑦星会员,霸气无比,大家恭喜TA吧!!!");
					cm.喇叭(4,"["+cm.getPlayer().getName()+"]开通了本服⑦星会员,霸气无比,大家恭喜TA吧!!!");
					cm.喇叭(4,"["+cm.getPlayer().getName()+"]开通了本服⑦星会员,霸气无比,大家恭喜TA吧!!!");
					// 1-2星
				
					cm.gainItem(必成,14);
					
					// 三星
				
					cm.gainItem(必成,7);
					
					// 四星
					
					cm.gainItem(必成,7);
					
					// 五星
					cm.gainItem(精灵吊坠,1);
					cm.gainItem(必成,7);
				
					// 六星
		
					cm.gainItem(必成,7);
					
					// 七星
					
					cm.gainItem(必成,7);
				
					cm.dispose();
			}else if(beauty == 8){
				cm.setzb(-一星费用);
					cm.getChar().setVip(1);//给死固定值
					cm.sendOk(""+成功了+"恭喜您,业务办理成功!!!");
					cm.喇叭(4,"["+cm.getPlayer().getName()+"]开通了本服①星会员,霸气无比,大家恭喜TA吧!!!");
					cm.喇叭(4,"["+cm.getPlayer().getName()+"]开通了本服①星会员,霸气无比,大家恭喜TA吧!!!");
					cm.喇叭(4,"["+cm.getPlayer().getName()+"]开通了本服①星会员,霸气无比,大家恭喜TA吧!!!");
					
					cm.gainItem(必成,7);
			
					cm.dispose();
			}else if (beauty == 9){//计算差价界面
			    var 目标会员的等级 = selection;
				var 旧会员等级 = cm.getChar().getVip();
				if(目标会员的等级<=旧会员等级){
					cm.sendOk("输入的等级需大于当前等级["+旧会员等级+"],请修改后再试!");
					cm.dispose();
					return;
				}
				
				// 当前会员价格
				var 旧会员price = 会员价格表[旧会员等级];
				// 新会员价格
				var 新会员price = 会员价格表[目标会员的等级];
				var 差价 = 新会员price - 旧会员price;
				差价ArrayList.add(差价);
				差价ArrayList.add(目标会员的等级);//新会员等级
				var pdd = "";
				pdd += ""+蓝色小喇叭+"当前会员等级:[#r"+会员星级[cm.getChar().getVip()]+"#k]\r\n";
				pdd += ""+蓝色小喇叭+"目标会员等级:[#r"+会员星级[目标会员的等级]+"#k]\r\n";
				pdd += ""+蓝色小喇叭+"差价:[#b"+差价+"#k]元宝\r\n";
				pdd += ""+铅笔+"是否确认办理?\r\n";
				beauty = 91;
				cm.sendYesNo(pdd);
			}
		}else if(status == 3){//执行会员升级
			//升级会员扣款
			if(beauty = 91){
				var 旧会员等级 = cm.getChar().getVip();
				var 差价 = 差价ArrayList.get(1);
				var 新会员等级 = 差价ArrayList.get(2);
				//cm.sendOk("差价:"+差价ArrayList.get(1)+"\r\n旧会员等级:"+旧会员等级+"\r\n新会员等级:"+新会员等级+"");//差价
				if(cm.getzb() < 差价){
					cm.sendOk("元宝不足");
					cm.dispose();
				}else {
					cm.setzb(差价);//扣元宝
					cm.getChar().setVip(新会员等级);//给等级
					// 给材料
					var 等级差 = 新会员等级 - 旧会员等级;
					var startLvl = 旧会员等级+1;
					for (var i = startLvl; i <= 新会员等级; i++) {
						// 
						for(var j = 0;j < 升级材料数组[i].length;j++){
							cm.gainItem(升级材料数组[i][j][0], 升级材料数组[i][j][1]);
						}
					}
					cm.喇叭(4,"["+会员星级[旧会员等级]+"]["+cm.getPlayer().getName()+"]会员等级升级至["+会员星级[cm.getChar().getVip()]+"],大家恭喜TA吧!!!");
				}
			}
		}
    }
}
