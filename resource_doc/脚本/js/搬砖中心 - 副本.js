var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 传送中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/6#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 经验 = "#fUI/UIWindow.img/Family/RightIcon/2#";  
var 经验1 = "#fUI/UIWindow.img/Family/RightIcon/3#";  
var 经验2 = "#fUI/UIWindow.img/Family/RightIcon/4#";  
var daobaMS = 0;
/*
* 普通怪物池  
*/
var 物品item = new Array(
);

var 打宝门票 = 5252001;//藏宝城门票
var 每日打宝 = 951000100;
var 挑战次数 = 1;
var 挑战时间 = 300;

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

            cm.sendOk("那好吧,切记不断修炼,提升战斗力...平时别偷懒哦");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {//
            // 展示代码
            var text = "";
            text += ""+dd+"\r\n\t\t\t"+传送中心+"\r\n"+群粉心+"";
            text += "\t#k#v4001126##v4000313##v4000038#搬砖中心每个频道有六个地图\r\n";
            text += "\t#k怪物经验初始经验为 #r5万  #k每次进入需消耗 #r10 元宝#k\r\n\r\n";
			text += "\t\t\t#r#e#L0#"+花花1+"进入圣殿搬砖"+花花1+"#l#n\r\n\r\n\r\n"
            text += "\t#k#v4021009##v4011007##v4251202#搬砖中心每个频道有四个地图\r\n";
            text += "\t#k怪物经验初始经验为 #r5万  #k每次进入需消耗 #r20 元宝#k\r\n\r\n";
			text += "\t\t\t#r#e#L1#"+花花5+"进入峡谷搬砖"+花花5+"#l\r\n\r\n\r\n"

            cm.sendSimple(text);
        } else if (status == 1){
        // 第一部分代码
		if(selection == 0){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			/*}else if(cm.getBossLog('进入圣殿搬砖') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 10){
				cm.sendOk("你还没达到VIP1");
				cm.dispose();*/
			}else if (cm.getmoneyb() < 10){
				cm.sendOk("#d#b您没有足够的元宝无法进入...#k");
				cm.dispose();
			} else {
				cm.setmoneyb(-10);
				//cm.spawnMobOnMap(9302033,10,408,-37,951000100,100000); // npc实现血量	
				//怪ID2400256
				//cm.setBossLog('进入圣殿搬砖');
				cm.warpParty(271030500);
				cm.喇叭(2, "[" + cm.getPlayer().getName() + "]进入了圣殿搬砖,好羡慕TA呀!");
			}
		} else if(selection == 1){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			/*}else if(cm.getBossLog('峡谷搬砖') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 50){
				cm.sendOk("你还没达到VIP2");
				cm.dispose();
			}else if(cm.getPlayerCount(410000102) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();*/
			}else if (cm.getmoneyb() < 20){
				cm.sendOk("#d#b您没有足够的元宝无法进入...#k");
				cm.dispose();
			} else {
				//怪ID2400256+怪ID2400257
				//cm.setBossLog('峡谷搬砖');
				cm.setmoneyb(-20);
				cm.warpParty(273030000);
				cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了峡谷搬砖地图,好羡慕TA呀!");
			}
		} else if(selection == 2){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP4练功房') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 500){
				cm.sendOk("你还没达到VIP4");
				cm.dispose();
			}else if(cm.getPlayerCount(410000103) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400257
				cm.setBossLog('VIP4练功房');
				cm.warpParty(410000103);
				cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP4练功房地图,好羡慕TA呀!");
			}
		} else if(selection == 3){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP6练功房') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 2000){
				cm.sendOk("你还没达到VIP6");
				cm.dispose();
			}else if(cm.getPlayerCount(410000121) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400258   //BOSS怪ID2400259
				cm.setBossLog('VIP6练功房');
				cm.warpParty(410000121);
				cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP6练功房地图,好羡慕TA呀!");
			}
		} else if(selection == 4){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP8练功房') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 6000){
				cm.sendOk("你还没达到VIP8");
				cm.dispose();
			}else if(cm.getPlayerCount(410000122) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400258 //怪ID2400265  //BOSS怪ID2400259
				cm.setBossLog('VIP8练功房');
				cm.warpParty(410000122);
				cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP8练功房地图,好羡慕TA呀!");
			}
		} else if(selection == 5){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP10练功房') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 10000){
				cm.sendOk("你还没达到VIP10");
				cm.dispose();
			}else if(cm.getPlayerCount(410000123) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				////怪ID2400265
				cm.setBossLog('VIP10练功房');
				cm.warpParty(410000123);
				cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP10练功房地图,好羡慕TA呀!");
			}
		} else if(selection == 13){
			cm.刷新指定地图(每日打宝);//该方法重启服务器后刷新
		}
        // ------------------------------beauty部分-------------------------------------- //
        } else if (status == 2){       
        // 第二部分代码
			if(daobaMS == 1){
				cm.openNpc(9900004,"聚合功能");
				cm.dispose();
			}
        }
    }
}

/*
* js睡眠方法
*/
function sleep(numberMillis) { 
	var now = new Date(); 
	var exitTime = now.getTime() + numberMillis; 
	while (true) { 
		now = new Date(); 
	if (now.getTime() > exitTime) 
	return; 
	} 
}

/*
* 退场NPC说的内容
*/
function ExitMapSendOk(){
	daobaMS = 1;
	var ppd = "";
	ppd += "#b挑战失败#k...你太弱了...\r\n靓仔,不服吗????\r\n";
	ppd += ""+任务简介+"\r\n"+蓝色小兔子+"是否要打开#r我要变强#k功提升自己的#d战斗力#k???"+蓝色小兔子+"";
	return ppd;
}

/*
* cm.定时切换地图(910000000,30);
*/
function ExitMapSendOk(mapId,num,mapKey){
	var key1;
	if(key > 0){
		cm.cancelTaskWarp(key);//关闭计时切换地图的线程
		key1 = ExitMapSendOk(mapId,num);
		return key1;
	}
	key1 = cm.定时切换地图(mapId,num);
	return key1;
}


