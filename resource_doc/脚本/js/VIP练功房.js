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

// 新增：王者VIP地图配置
var 王者VIP地图ID = 100020101;
var 王者VIP累计赞助要求 = 20000; // 2W累计赞助
var status = 0; // 补充全局status变量，避免未定义报错

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
            // 展示代码 - 新增王者VIP选项
            var text = "";
            text += "\r\n\t\t  "+挑战中心+"\r\n"+群粉心+"";
            text += "   #k#L0#"+花花1+"VIP①挂机区"+花花1+"#l\t\t#k#L1#"+花花3+"VIP③挂机区"+花花3+"#l \r\n\r\n"+群粉心+"";
            text += "   #b#L2#"+花花5+"VIP⑤挂机区"+花花5+"#l\t\t#b#L3#"+花花7+"VIP⑦挂机区"+花花7+"#l \r\n\r\n"+群粉心+"";
            text += "   #r#L4#"+花花8+"VIP⑨挂机区"+花花8+"#l\t\t#r#L5#"+花花9+"VIP⑩挂机区"+花花9+"#l \r\n\r\n"+群粉心+"\r\n";
            // 新增：王者VIP挂机区选项（selection=6）
            text += "\t\t\t#r#L6##v3010507#王者VIP挂机区#v3010507##l \r\n\r\n\r\n"+群粉心+"\r\n";

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
                    cm.setBossLog('福利练级区');
                    cm.warpParty(410000101);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "]进入了VIP1挂机房,好羡慕TA呀!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            } else if(selection == 1){
                if(cm.getPlayer().getParty()!= null){
                    cm.sendOk("只能一个人进入，请先退出组队");
                    cm.dispose();
                }else if(cm.getBossLog('VIP3练级区') > 0){
                    cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
                    cm.dispose();
                }else if(cm.getPlayer().getlpjf() < 500){
                    cm.sendOk("你还没达到VIP3");
                    cm.dispose();
                }else if(cm.getPlayerCount(410000102) > 0){
                    cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
                    cm.dispose();
                } else {
                    cm.setBossLog('VIP3练级区');
                    cm.warpParty(410000102);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP3练级区地图,好羡慕TA呀!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            } else if(selection == 2){
                if(cm.getPlayer().getParty()!= null){
                    cm.sendOk("只能一个人进入，请先退出组队");
                    cm.dispose();
                }else if(cm.getBossLog('VIP5练级区') > 0){
                    cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
                    cm.dispose();
                }else if(cm.getPlayer().getlpjf() < 2000){
                    cm.sendOk("你还没达到VIP5");
                    cm.dispose();
                }else if(cm.getPlayerCount(410000103) > 0){
                    cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
                    cm.dispose();
                } else {
                    cm.setBossLog('VIP5练级区');
                    cm.warpParty(410000103);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP5练级区地图,好羡慕TA呀!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            } else if(selection == 3){
                if(cm.getPlayer().getParty()!= null){
                    cm.sendOk("只能一个人进入，请先退出组队");
                    cm.dispose();
                }else if(cm.getBossLog('VIP7练级区') > 0){
                    cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
                    cm.dispose();
                }else if(cm.getPlayer().getlpjf() < 5000){
                    cm.sendOk("你还没达到VIP7");
                    cm.dispose();
                }else if(cm.getPlayerCount(410000121) > 0){
                    cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
                    cm.dispose();
                } else {
                    cm.setBossLog('VIP7练级区');
                    cm.warpParty(410000121);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP7练级区地图,好羡慕TA呀!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            } else if(selection == 4){
                if(cm.getPlayer().getParty()!= null){
                    cm.sendOk("只能一个人进入，请先退出组队");
                    cm.dispose();
                }else if(cm.getBossLog('VIP9练级区') > 0){
                    cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
                    cm.dispose();
                }else if(cm.getPlayer().getlpjf() < 10000){
                    cm.sendOk("你还没达到VIP9");
                    cm.dispose();
                }else if(cm.getPlayerCount(410000122) > 0){
                    cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
                    cm.dispose();
                } else {
                    cm.setBossLog('VIP9练级区');
                    cm.warpParty(410000122);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP9练级区地图,好羡慕TA呀!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            } else if(selection == 5){
                if(cm.getPlayer().getParty()!= null){
                    cm.sendOk("只能一个人进入，请先退出组队");
                    cm.dispose();
                }else if(cm.getBossLog('VIP10练级区') > 0){
                    cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
                    cm.dispose();
                }else if(cm.getPlayer().getlpjf() < 15000){
                    cm.sendOk("你还没达到VIP10");
                    cm.dispose();
                }else if(cm.getPlayerCount(410000123) > 0){
                    cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
                    cm.dispose();
                } else {
                    cm.setBossLog('VIP10练级区');
                    cm.warpParty(410000123);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了VIP10练级区地图,好羡慕TA呀!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            // 新增：王者VIP挂机区逻辑（selection=6）
            } else if(selection == 6){
                if(cm.getPlayer().getParty()!= null){
                    cm.sendOk("只能一个人进入，请先退出组队");
                    cm.dispose();
                }else if(cm.getBossLog('王者VIP练级区') > 0){
                    cm.sendOk("每天每个账号最多可挑战#b 1 #k次#k");
                    cm.dispose();
                }else if(cm.getPlayer().getlpjf() < 王者VIP累计赞助要求){
                    cm.sendOk("你还没达到王者VIP（累计赞助需≥20000）");
                    cm.dispose();
                }else if(cm.getPlayerCount(王者VIP地图ID) > 0){
                    cm.sendOk("#d当前频道已有人挑战,请切换频道...#k");
                    cm.dispose();
                } else {
                    cm.setBossLog('王者VIP练级区');
                    cm.warpParty(王者VIP地图ID);
                    cm.喇叭(2, "[" + cm.getPlayer().getName() + "] 进入了王者VIP挂机区地图,太壕了吧!");
                    cm.getPlayer().startMapTimeLimitTask(300, cm.getChannelServer().getMapFactory().getMap(910000000));
                    cm.dispose();
                }
            } else if(selection == 13){
                cm.刷新指定地图(每日打宝);
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