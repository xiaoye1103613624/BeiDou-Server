var 日期 = new Date().getDate();        //获取当前日(1-31)
//var 日期 = 7;
var 签到所需材料 = 4000000;
var 签到所需数量 = 60;
var 签到材料 = new Array(
4000000,4000016,4000008,4000015,4000006,4000007,4000013,4000003,4000004,4000041,4000029,
4000043,4000106,4000107,4000095,4000020,4000024,4000032,4000012,4000009,4000005,4000001,
4170009,4170002,4170005,4170014,3994620
);
var itemList = new Array(
//[2020014],//超级药水
[2000005],//清晨之露
[2022000],//矿泉水 
[2000004]//特殊药水
);
var 签到随机物品 = itemList[Math.floor(Math.random() * itemList.length)];
var 签到随机物品数量 = Math.floor(Math.random()*(5 - 0) + 5);
var 连续签到 = 0;

var 积分 = new Array(1,2,3,4,1,2,3,1,2,2,5,1,3);
var 随机积分 = 积分[Math.floor(Math.random() * 积分.length)];

var 蓝加 = "#fUI/Basic.img/BtMax/mouseOver/0#";
var 小彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";

var LWJRZ = 0;
var xmml1 = 0;
var xmml2 = 0;
var 记录签到日期;


var now = new Date();
var hour = now.getHours();//得到小时


function start() {
	// cm.sendOk("");
    // cm.dispose();
    // return;
	
	/*连续签到 = cm.getPlayer().getBossLog("连续签到",1,"账号");
	记录签到日期 = cm.getPlayer().getBossLog("记录日期_签到", 1,"账号");
    if (记录签到日期 > 日期 ) {
        cm.getPlayer().setBossLog("月初清空_签到",0,1,"账号");
        cm.getPlayer().setBossLog("每日签到",1,-cm.getPlayer().getBossLog("每日签到",1,"账号"),"账号");
        cm.getPlayer().setBossLog("连续签到", 1, -连续签到,"账号");
        cm.getPlayer().setBossLog("记录日期_签到", 1, -cm.getPlayer().getBossLog("记录日期_签到", 1,"账号"));
        cm.getPlayer().setBossLog("7日奖励", 1, -cm.getPlayer().getBossLog("7日奖励", 1,"账号"),"账号");
        cm.getPlayer().setBossLog("15日奖励", 1, -cm.getPlayer().getBossLog("15日奖励", 1,"账号"),"账号");
        cm.getPlayer().setBossLog("28日奖励", 1, -cm.getPlayer().getBossLog("28日奖励", 1,"账号"),"账号");
        cm.sendOk("已经清空上月所有记录！");
        status = -1;*/
   // } else {
        status = -1;
        action(1, 0, 0);
 //   }
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            // cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
			cm.dispose();
            return;
        }
        if (status == 0) {
			连续签到 = cm.getPlayer().getBossLog("连续签到",1);

		//	签到所需材料 = cm.getPlayer().getBossLog("每日签到_今日材料",1,"账号");
			//连续签到 = cm.getPlayer().getBossLog("连续签到",1);


			
		//	人气度增益 = cm.getPlayer().getFame();
		//	签到所需数量2 = 签到所需数量+(连续签到*1);
			
			
			
			// 奖励点卷 = ((连续签到+1)*100)+人气度增益+开店奖励;
			// 奖励金币 = (((连续签到+1)*100)+开店奖励)*1000;
			
		//	奖励点卷 = ((连续签到+1)*100);
		//	奖励金币 = ((连续签到+1)*100)*1000;
		//	if (奖励点卷 >= 700){
		//		奖励点卷 = 700;			
		//	}
		//	奖励点卷 = 1000;
			
			
            var text = "\r\n";
            text += "          "+小彩虹+"#r#e签 到 系 统#n#k"+小彩虹+"\r\n\r\n";
 
			//text += "      #e#r注意 23:00 ~ 1:00 禁止签到[后果自负]#n#k\r\n";
            text += "签到后随机获得#b下方任意奖励#k,还有概率触发#b隐藏赞助奖励#k\r\n";
			
            text += "#i2022506##i2022507##i2022509##i2022517##i2022518##i2022519##i2022521##i3605014##i4011007##i4251202##i4021009##i2048403##i2616300#";//13
            text += "#i2022505##i4001126##i4000313##i4000038##i3994731##i4310108##i4032398##i4170016##i4170007##i4310174##i4310088##i4000464#\r\n\r\n";
            //text += "         今日签到状态:"+今日签到状态+" 您已累计签到：#r"+连续签到+"#k 天 \r\n";

			if (cm.getPlayer().getBossLog("每日签到",0) > 0) {
            text += "                您已累计签到：#r"+连续签到+"#k 天 \r\n";

			} else {
            text += "                #L0#"+蓝加+"#e#r开始签到"+蓝加+"#n#k#l\r\n\r\n";
			}
            
			text += "#L999##b累计签到#r "+连续签到+"/7#b日奖励:#i4011007#*5 #i 2022517#*5 #l\r\n\r\n";

			text += "#L1##b累计签到#r "+连续签到+"/14#b日奖励:#i4021009#*5 #i 3994731#*5 元宝*100#l\r\n\r\n";

            text += "#L2##b累计签到#r "+连续签到+"/21#b日奖励:#i2022505#*1 #i4011007#*5 累计*100#l\r\n\r\n";
			if(cm.haveItem(2022506) < 1){
            text += "#L3##b累计签到#r "+连续签到+"/30#b日奖励:#i2022506#*1 #i 4021009#*5 元宝*300#l\r\n\r\n";
			}else{
            text += "#L33##b累计签到#r "+连续签到+"/30#b日奖励:#i2022507#*1 #i 4021009#*5 元宝*300#l\r\n\r\n";
			}
            cm.sendSimple(text);

        } else if (status == 1) {
			xmml1 = selection;
            随机数 = Math.floor(Math.random() * 18+1)
            if (selection == 0) {
				if (cm.getPlayer().getBossLog("每日签到",0) > 0) {
                    cm.sendOk("今日已进行签到，请明日再来。");
                    cm.dispose();
					return;
                } else {
                        
                // var 随机数 = 1
                   if(随机数 == 1){
                    物品 =4310108
					数量 =1
            } else if(随机数 == 2){
                    物品 =3994731
					数量 =1
            } else if(随机数 == 3){
                    物品 =4310088
					数量 =1
            } else if(随机数 == 4){
                    物品 =4310174
					数量 =1
            } else if(随机数 == 5){
                    物品 =4170007
					数量 =1
            } else if(随机数 == 6){
                    物品 =4170016
					数量 =1
            } else if(随机数 == 7){
                    物品 =4032398
					数量 =1
            } else if(随机数 == 8){
                    物品 =4000038
					数量 =1
            } else if(随机数 == 9){
                    物品 =4000313
					数量 =1
            } else if(随机数 == 10){
                    物品 =4001126
					数量 =1
            } else if(随机数 == 11){
                    物品 =4000464
					数量 =1
            } else if(随机数 == 12){
                    物品 =2616300
					数量 =1
            } else if(随机数 == 13){
                    物品 =2048403
					数量 =1
            } else if(随机数 == 14){           
                    物品 =2022521
					数量 =1
            } else if(随机数 == 15){
                    物品 =3605014
					数量 =1
            } else if(随机数 == 16){
                    物品 =4011007
					数量 =1
            } else if(随机数 == 17){
                    物品 =4251202
					数量 =1
            } else if(随机数 == 18){
                    物品 =4021009
					数量 =1
            } else if(随机数 == 19){
                    物品 =2022517
					数量 =1
             } else{
                    物品 =2022509//19
					数量 =1

                    }



                    cm.gainItem(物品, 数量);
                    cm.getPlayer().setBossLog("每日签到");//连续签到
				    cm.getPlayer().setBossLog("连续签到",1,1);
                    cm.sendOk("签到成功，您已累计签到 #r" +cm.getPlayer().getBossLog("每日签到") + "#k 天\r\n\r\n今日随机签到奖励获得:#i"+物品+"##r * "+数量+" #k个");
				    cm.worldMessage(6,"【每日签到】["+cm.getName()+"]签到了，获得丰厚的奖励 累计签到["+cm.getPlayer().getBossLog("每日签到")+"]天"); 
                    影藏福利()
					cm.dispose();
					return;
                }
				
				
            } else if (selection == 999) {
                if (连续签到 < 7) {
                    cm.sendOk("连续签到时间不足，无法领取奖励 当前"+连续签到+" 天");
                    status = -1;
                } else if (cm.getPlayer().getBossLog("7日奖励", 1) > 0) {
                    cm.sendOk("此奖励角色已领取");
                    status = -1;
                } else {
 
					cm.gainItem(2022517, 5);//紫钻强化卷
					cm.gainItem(4011007, 5);//装备强化卷
                    cm.getPlayer().setBossLog("7日奖励", 1,1);
					cm.getPlayer().setBossLog("14日奖励", 1,-1);
                    cm.sendOk("领取成功");
					cm.worldMessage(12, cm.getC().getChannel(),"【每日签到】" + " : " + " [" + cm.getPlayer().getName() + "]已经连续签到7天获得非常棒的奖励！" );
                    status = -1;
                }

            } else if (selection == 1) {
                if (连续签到 < 14) {
                    cm.sendOk("连续签到时间不足，无法领取奖励 当前"+连续签到+" 天");
                    status = -1;
                } else if (cm.getPlayer().getBossLog("14日奖励", 1) > 0) {
                    cm.sendOk("此奖励角色已领取");
                    status = -1;
                } else {
                    cm.setmoneyb(+100);//元宝
					//cm.getPlayer().setwzcz(cm.getPlayer().getwzcz()+100);
					cm.playerMessage(5, "获得：100 元宝");
					cm.gainItem(4021009, 5);//绯红     
					cm.gainItem(3994731, 5);//点装防爆卷
                    cm.getPlayer().setBossLog("14日奖励", 1,1);
					cm.getPlayer().setBossLog("21日奖励", 1,-1);
                    cm.sendOk("领取成功");
					cm.worldMessage(12, cm.getC().getChannel(),"【每日签到】" + " : " + " [" + cm.getPlayer().getName() + "]已经连续签到14天获得非常棒的奖励！" );
                    status = -1;
                }

            } else if (selection == 2) {
                if (连续签到 < 21) {
                    cm.sendOk("连续签到时间不足，无法领取奖励 当前"+连续签到+" 天");
                    status = -1;
                } else if (cm.getPlayer().getBossLog("21日奖励", 1) > 0) {
                    cm.sendOk("此奖励角色已领取");
                    status = -1;
                } else {
                    
					 //cm.getPlayer().setwzcz(cm.getPlayer().getwzcz()+100);
					cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+100);
					cm.playerMessage(5, "获得：100 累计");
					cm.gainItem(2022505, 1);//重生卷
					cm.gainItem(4011007, 5);//元宝
                    cm.getPlayer().setBossLog("21日奖励", 1,1);
					cm.getPlayer().setBossLog("30日奖励", 1,-1);
                    cm.sendOk("领取成功");
					cm.worldMessage(12, cm.getC().getChannel(),"【每日签到】" + " : " + " [" + cm.getPlayer().getName() + "]已经连续签到21天获得非常棒的奖励！" );
                    status = -1;
                }
             } else if (selection == 3) {
                if (连续签到 < 30) {
                    cm.sendOk("连续签到时间不足，无法领取奖励 当前"+连续签到+" 天");
                    status = -1;
                } else if (cm.getPlayer().getBossLog("30日奖励", 1) > 0) {
                    cm.sendOk("此奖励角色已领取");
                    status = -1;
                } else {
                    
					// cm.gainHyPay(50);
					cm.setmoneyb(+300);//元宝
					 //cm.getPlayer().setwzcz(cm.getPlayer().getwzcz()+100);
					cm.playerMessage(5, "获得：300 元宝");
					cm.gainItem(2022506, 1);//分身
					cm.gainItem(4021009, 5);//元宝
                    cm.getPlayer().setBossLog("30日奖励", 1,1);
					cm.getPlayer().setBossLog("连续签到",1,-30);
					cm.getPlayer().setBossLog("7日奖励", 1,-1);
                    cm.sendOk("领取成功");
					cm.worldMessage(12, cm.getC().getChannel(),"【每日签到】" + " : " + " [" + cm.getPlayer().getName() + "]已经连续签到30天获得非常棒的奖励！" );
                    status = -1;
                }
             } else if (selection == 33) {
                if (连续签到 < 30) {
                    cm.sendOk("连续签到时间不足，无法领取奖励 当前"+连续签到+" 天");
                    status = -1;
                } else if (cm.getPlayer().getBossLog("30日奖励", 1) > 0) {
                    cm.sendOk("此奖励角色已领取");
                    status = -1;
                } else {
                    
					// cm.gainHyPay(50);
					cm.setmoneyb(+300);//元宝
					 //cm.getPlayer().setwzcz(cm.getPlayer().getwzcz()+100);
					cm.playerMessage(5, "获得：300 元宝");
					cm.gainItem(2022507, 1);//分身
					cm.gainItem(4021009, 5);//元宝
                    cm.getPlayer().setBossLog("30日奖励", 1,1);
					cm.getPlayer().setBossLog("连续签到",1,-30);
					cm.getPlayer().setBossLog("7日奖励", 1,-1);
                    cm.sendOk("领取成功");
					cm.worldMessage(12, cm.getC().getChannel(),"【每日签到】" + " : " + " [" + cm.getPlayer().getName() + "]已经连续签到30天获得非常棒的奖励！" );
                    status = -1;
                }


 } else if (selection == 4) {
				var text = "";
				text += "当前领取28天奖励："+cm.getPlayer().getAccYjLog("每日签到_满签")+" 次 \r\n"
				text += "连续获得3次满签将会获得：100充值奖励哦(可领累积礼包)\r\n"
				text += "#r#e注意：要每个月都领取28天奖励才有效哦！#k#n\r\n"
				text += "选择‘是’可领取奖励！\r\n"
				cm.sendYesNo(text);
			} else if (selection == 5) {
				var text = "";
				text += "已下是每日签到所需要的材料哦！每天需要的不一样..\r\n"
				for (var i = 0; i < 签到材料.length; i++) {
					text += "#v"+签到材料[i]+"#"
					 
				}
				cm.sendOk(text);
				status = -1;
            } else if (selection == 6) {
				cm.openNpc(9900004,"每日签到记录查询");
			}
        } else if (status == 2) {
			if (xmml1 == 0) {
				
			} else if (xmml1 == 4) {	
				if (cm.getPlayer().getAccYjLog("每日签到_满签") >= 3) {
					cm.getPlayer().gainAccYjLog("每日签到_满签",-cm.getPlayer().getAccYjLog("每日签到_满签"));
					cm.gainHyPay(100);
					cm.playerMessage(5, "获得：100 充值");
					cm.sendOk("哇！超级粉丝，这是给你的奖励！加油哦！希望下次你还能领取！");
					cm.worldMessage(12, cm.getC().getChannel(),"【签到超级粉丝】" + " : " + " [" + cm.getPlayer().getName() + "] 连续3次满签！获得超级粉丝福利：100充值 ", true);
					cm.worldMessage(12, cm.getC().getChannel(),"【签到超级粉丝】" + " : " + " [" + cm.getPlayer().getName() + "] 连续3次满签！获得超级粉丝福利：100充值 ", true);
					cm.worldMessage(12, cm.getC().getChannel(),"【签到超级粉丝】" + " : " + " [" + cm.getPlayer().getName() + "] 连续3次满签！获得超级粉丝福利：100充值 ", true);
				} else {
					cm.sendOk("你没有领取资格哦！请继续努力！");
				}
				cm.dispose();
			}
		}
    }
}


function 影藏福利() {
if (Math.floor(Math.random() * 5) <= 1) {//   
		奖励 = Math.floor(Math.random() * 3+1);// 5+1 5的范围 取 1个数字 随机 
        //cm.getPlayer().setwzcz(cm.getPlayer().getwzcz()+奖励);
		cm.setmoneyb(+奖励);
		//cm.gainmoney(奖励);
		cm.playerMessage("签到隐藏奖励:"+奖励+"元 赞助奖励！");	
		cm.worldMessage(12, cm.getC().getChannel(),"【签到隐藏福利】" + " : " + " [" + cm.getPlayer().getName() + "] 签到时欧皇附体，额外获得了"+奖励+"元赞助奖励！");
	}
}

function insertzonghelog() {
	var 日期 = new Date().getDate();
	var conn = cm.getConnection(); 
	var sql = "insert into xmzonghelog (time,type,acid,cs) values (CURRENT_TIMESTAMP(),?,?,?);";          
    var psu = conn.prepareStatement(sql);
	psu.setString(1,"每日签到");
	psu.setInt(2,cm.getPlayer().getAccountID());
	psu.setInt(3,cm.getMapId());
    psu.executeUpdate();	
	psu.close();	
	
}

function intodancilog(log) {
	var 日期 = new Date().getDate();
	var conn = cm.getConnection(); 
	var sql = "insert into xmzonghelog (time,type,acid,cs) values (CURRENT_TIMESTAMP(),?,?,?);";          
    var psu = conn.prepareStatement(sql);
	psu.setString(1,log);
	psu.setInt(2,cm.getPlayer().getAccountID());
	psu.setInt(3,cm.getMapId());
    psu.executeUpdate();	
	psu.close();	
	
}
