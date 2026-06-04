/**
*	脚本名称:白嫖
*	2020年2月22日22:34:18
*	作者: 岛霸 Q279934747
*	
*/
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
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
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


var dungeonid = 100020101; // 地下城起始地图ID
var dungeons = 5; // 地下城地图的数量

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
            text += "\r\n\t\t  "+挑战中心+"\r\n"+群粉心+"";
        //    text += "  #k满足条件即可进入：[练级区不限次数][打宝区每日一次]#n\r\n";
			text += "   #k#L0#"+花花1+"VIP①挂机区"+花花1+"#l\t\t#k#L1#"+花花3+"VIP③挂机区"+花花3+"#l \r\n\r\n"+群粉心+""
			text += "   #b#L2#"+花花5+"VIP⑤挂机区"+花花5+"#l\t\t#b#L3#"+花花7+"VIP⑦挂机区"+花花7+"#l \r\n\r\n"+群粉心+""
			text += "   #r#L4#"+花花8+"VIP⑨挂机区"+花花8+"#l\t\t#r#L5#"+花花9+"VIP⑩挂机区"+花花9+"#l \r\n\r\n"+群粉心+""
			text += "\t\t\t#r#L6##v3010507#王者VIP挂机区#v3010507##l \r\n\r\n\r\n"+群粉心+"\r\n"

            cm.sendSimple(text);
        } else if (status == 1){
        // 第一部分代码
		if(selection == 0){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('福利练级区') > 0){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 100){
				cm.sendOk("你还没达到VIP1");
				cm.dispose();
			}else if(cm.getPlayerCount(410000101) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//cm.setmoneyb(-20);
				//cm.spawnMobOnMap(9302033,10,408,-37,951000100,100000); // npc实现血量	
				//怪ID2400256
				//cm.setBossLog('福利练级区');
				cm.warpParty(410000101);
			//	cm.喇叭(2, "[" + cm.getPlayer().getName() + "]进入了VIP1挂机房,好羡慕TA呀!");
			//	cm.getPlayer().startMapTimeLimitTask(3600, cm.getChannelServer().getMapFactory().getMap(910000000));       //这个给个人地图记时
				cm.dispose();
			}
		} else if(selection == 1){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP3练级区') > 1){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 500){
				cm.sendOk("你还没达到VIP3");
				cm.dispose();
			}else if(cm.getPlayerCount(410000102) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400256+怪ID2400257
				//cm.setBossLog('VIP3练级区');
				cm.warpParty(410000102);
			//	cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP3练级区地图,好羡慕TA呀!");
			//	cm.getPlayer().startMapTimeLimitTask(10800, cm.getChannelServer().getMapFactory().getMap(910000000));       //这个给个人地图记时
				cm.dispose();
			}
		} else if(selection == 2){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP5练级区') > 1){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 2000){
				cm.sendOk("你还没达到VIP5");
				cm.dispose();
			}else if(cm.getPlayerCount(410000103) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400257
				//cm.setBossLog('VIP5练级区');
				cm.warpParty(410000103);
			//	cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP5练级区地图,好羡慕TA呀!");
			//	cm.getPlayer().startMapTimeLimitTask(18000, cm.getChannelServer().getMapFactory().getMap(910000000));       //这个给个人地图记时
				cm.dispose();
			}
		} else if(selection == 3){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP7练级区') > 1){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 5000){
				cm.sendOk("你还没达到VIP7");
				cm.dispose();
			}else if(cm.getPlayerCount(410000121) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400258   //BOSS怪ID2400259
			//	cm.setBossLog('VIP7练级区');
				cm.warpParty(410000121);
			//	cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP7练级区地图,好羡慕TA呀!");
			//	cm.getPlayer().startMapTimeLimitTask(25200, cm.getChannelServer().getMapFactory().getMap(910000000));       //这个给个人地图记时
				cm.dispose();
			}
		} else if(selection == 4){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP9练级区') > 10){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 10000){
				cm.sendOk("你还没达到VIP9");
				cm.dispose();
			}else if(cm.getPlayerCount(410000122) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
			} else {
				//怪ID2400258 //怪ID2400265  //BOSS怪ID2400259
			//	cm.setBossLog('VIP9练级区');
				cm.warpParty(410000122);
			//	cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP9练级区地图,好羡慕TA呀!");
			//	cm.getPlayer().startMapTimeLimitTask(32400, cm.getChannelServer().getMapFactory().getMap(910000000));       //这个给个人地图记时
				cm.dispose();
			}
		} else if(selection == 5){
				if(cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
			}else if(cm.getBossLog('VIP10练级区') > 1){
				cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
				cm.dispose();
			}else if(cm.getPlayer().getlpjf() < 15000){
				cm.sendOk("你还没达到VIP10");
				cm.dispose();
			}else if(cm.getPlayerCount(410000123) > 0){
				cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
				cm.dispose();
				
			}else if (cm.getPlayer().getClient().getChannel() == 8) {
		//	}else if(cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3 && cm.getPlayer().getClient().getChannel() != 4) {
				cm.sendOk("为了防止玩家拥挤，或影响公平性\r\n\r\n不能在此频道进入VIP10房间哦，请更换其他频道！\r\n\r\n此地图怪物爆率已调整为跟双倍频道爆率一样，不用担心！");
				cm.dispose();
			} else {
				////怪ID2400265
			//	cm.setBossLog('VIP10练级区');
				cm.warpParty(410000123);
			//	cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP10练级区地图,好羡慕TA呀!");
				cm.dispose();
			}
		} else if(selection == 6){
			
				enterDungeon();
				
				
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

function enterDungeon() {
    for (var i = 0; i < dungeons; i++) {
        if (cm.getPlayerCount(dungeonid + i) == 0) { // 检查当前地下城是否为空
        //    if (cm.getPlayer().getLevel() <= 2) { // 检查玩家等级是否不超过2级
			if (cm.getPlayer().getlpjf() > 19999){ // 检查玩家累计赞助是否超过20000
                if (cm.haveItem(5680324)) { // 检查玩家是否持有入场券
                    cm.warp(dungeonid + i, 0); // 将玩家传送到空的地下城地图
                    cm.dispose(); // 结束对话
                    return;
                } else {
                    cm.sendOk("你没有持有五倍爆率卡，无法进入该地图。");
                    cm.dispose(); // 提示后结束对话
                    return;
                }
            } else {
                cm.sendOk("你没有2W累计赞助，无法进入该地图。");
                cm.dispose(); // 提示后结束对话
                return;
            }
        }
    }
    cm.sendOk("目前所有迷你地下城都有人，请稍后再尝试。");
    cm.dispose(); // 提示后结束对话
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


