function start() {
        cm.喇叭(1, "【开心冒险岛】给您想要的任意玩法！(私信GM提出建议或想法即可)");
        cm.dispose();
}
/*var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
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
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
///////////////////////////////////////////
//////////////////		boss挑战相关	////////////////////////
var 普通入场券 = 3994389;
var 中级入场券 = 3994587;
var 高级入场券 = 3994589;

var 双头蛇地图 = 745010300;
var 三头犬地图 = 510103700;
var 幻龙地图 = 240080300;
var 御龙魔地图 = 240080801;
var 黑魔女地图 = 924010000;
var 蜘蛛女王地图 = 240093310;
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
           
            text += "\t\t\t  #e#b	[冒险岛希纳斯BOSS] #k#n\r\n"
			
			text += ""+表情+表情2+表情+表情2+表情+表情2+表情+表情2+表情+表情2+表情+表情2+表情+表情2+表情+表情2+表情+表情2+"\r\n"
			
            //text += ""+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"\r\n"
			
            text += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
			
			//text += ""+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"#e[普通BOSS]#k"+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"\r\n"
			
            //text += "#L0##b" + 蓝色小兔子 + "测试地图是否有人 \r\n"
			 text += "#e#k进入条件:#v3994731#x20个,破功299万 250级[今日次数"+cm.getBossLog("希纳斯")+"/3]\r\n\r\n#e#kboss暴率:#v4000464##v2531000##v1482216##v2531000##v2460005##v1432227##v1402268#\r\n\r\n"
            text += "#L1##b挑战希纳斯boss[2000E]   #d状态: " + cm.getPlayerCount(271040100) + " #l\r\n"

            //text += "#L6##b" + 蓝色小兔子 + "挑战黄龙[500E]    #d状态: " + cm.getPlayerCount(40000) + " #l\r\n"
            //text += "#L7##b" + 蓝色小兔子 + "挑战赤虎[500E]    #d状态: " + cm.getPlayerCount(40000) + " #l\r\n\r\n"
       
        
			//text += ""+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"#e[中级BOSS]#k"+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"#l\r\n"
			//text += "#L8##b" + 蓝色小兔子 + "挑战蜘蛛女王[500E]  #d状态: " + cm.getPlayerCount(40000) + " #l\r\n\r\n"

            cm.sendSimple(text);
        } else if (status == 1){
		 if (selection == 1) { //挑战双头蛇  3994389
				cm.sendYesNo(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[希纳斯]#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('希纳斯')+"/3 #k次\r\n3.请确保背包拥有#v3994731##d(每次挑战消耗10个)#k\r\n4.#v3994731#可在#b中介兑换获取#k\r\n\r\n "+蓝色小兔子+"#e#d您确定现在进行挑战吗?#k#l");
				beauty = 1;
        
        } 
				
		// ------------------------------beauty部分-------------------------------------- //
		} else if (status == 2){
			 if (beauty == 1){ // 双头蛇
			 if (cm.getPlayer().getParty() == null){
				cm.sendOk(""+ 任务简介 +"\r\n首先您必须拥有一个队伍#k");
				cm.dispose();
			}else if (!cm.isLeader()){
				cm.sendOk(""+ 任务简介 +"\r\n您不是队长,请让队长与我对话#k");
				cm.dispose();
				}else if (cm.getPlayer().getDamage()<279){
				cm.sendOk(""+ 任务简介 +"\r\n您的破功不足299万#k");
				cm.dispose();
			}else if(!cm.haveItem(3994731,20)){
				cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[希纳斯#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('希纳斯')+" #k次 #d[每日每个账号可挑战3次]#k\r\n3.请确保背包拥有#v3994731##d(每次挑战消耗10张)#k\r\n\r\n");
				cm.dispose();
				}else if(cm.getPlayer().getLevel() <= 200 ){
				cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[希纳斯#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('希纳斯')+" #k次 #d[每日每个账号可挑战3次]#k\r\n3.请确保背包拥有#v3994731##d(每次挑战消耗10张)#k\r\n\r\n");
				cm.dispose();
				}else if(cm.getBossLog('希纳斯') >= 3){
				cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[希纳斯#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('希纳斯')+" #k次 #d[每日每个账号可挑战3次]#k\r\n3.请确保背包拥有#v3994731##d(每次挑战消耗10张)#k\r\n\r\n");
				cm.dispose();
			}else if (cm.getPartyBossLog('希纳斯',3)== false){
				cm.sendOk(""+ 任务简介 +"\r\n您的组队有人挑战过3次了#k");
				cm.dispose();
			
			}else if(cm.getPlayerCount(271040100) >= 1){
				cm.sendOk(""+ 任务提示 +"\r\n当前频道BOSS地图可能已有人挑战,请切换频道");
				cm.dispose();
			}else{
				cm.warpParty(271040100,0); // 传送到BOSS地图
				cm.gainItem(3994731,-20);
				cm.刷新地图();
				// 召唤怪物 怪物id 地图id ,坐标
				// cm.spawnMob_map(9601015,745010300,1116,337);//xml实现血量
				// cm.spawnMob_map(怪物ID,数量,x坐标,y坐标,地图id,血量); 
				cm.spawnMobOnMap(8850011,1,-152,115,271040100,200000000000); // npc实现血量	
				cm.givePartyBossLog('希纳斯');
				cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
				cm.dispose();
			}		
		    } else if (beauty == 2){ // 三头犬
				if(cm.haveItem(普通入场券) && cm.getPlayer().getLevel() > 159 && cm.getBossLog('三头犬') < 1){
					cm.warpParty(三头犬地图,0);
					cm.gainItem(普通入场券,-1);
					cm.刷新地图();
					// 召唤怪物 怪物id 地图id ,坐标
					// cm.spawnMob_map(9601015,745010300,1116,337);//xml实现血量
					// cm.spawnMob_map(怪物ID,数量,x坐标,y坐标,地图id,血量); 
					cm.spawnMobOnMap(9400897,1,450,33,三头犬地图,5000000000); // npc实现血量	
					cm.setBossLog('三头犬');
					cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
					cm.dispose();
				}else {
					cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[三头犬]#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('三头犬')+" #k次 #d[每日每个账号可挑战一次]#k\r\n3.请确保背包拥有#v"+普通入场券+"##d(每次挑战消耗1张)#k\r\n4.#v"+普通入场券+"#可在#b中介兑换获取#k\r\n\r\n "+蓝色小兔子+"#e#d您确定现在进行挑战吗?#k#l");
					cm.dispose();
				}
			} else if (beauty == 3){ // 幻龙
				if(cm.haveItem(普通入场券) && cm.getPlayer().getLevel() > 159 && cm.getBossLog('幻龙') < 1){
					cm.warpParty(幻龙地图,0);
					cm.gainItem(普通入场券,-1);
					cm.刷新地图();
					// 召唤怪物 怪物id 地图id ,坐标
					// cm.spawnMob_map(9601015,745010300,1116,337);//xml实现血量
					// cm.spawnMob_map(怪物ID,数量,x坐标,y坐标,地图id,血量); 
					cm.spawnMobOnMap(8300006,1,450,33,幻龙地图,7000000000); // npc实现血量	
					cm.setBossLog('幻龙');
					cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
					cm.dispose();
				}else {
					cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[幻龙]#k 血量 #r[70E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('幻龙')+" #k次 #d[每日每个账号可挑战一次]#k\r\n3.请确保背包拥有#v"+普通入场券+"##d(每次挑战消耗1张)#k\r\n4.#v"+普通入场券+"#可在#b中介兑换获取#k\r\n\r\n "+蓝色小兔子+"#e#d您确定现在进行挑战吗?#k#l");
					cm.dispose();
				}
				
			} else if (beauty == 4){ // 御龙魔	
				if(cm.haveItem(普通入场券) && cm.getPlayer().getLevel() > 159 && cm.getBossLog('御龙魔') < 1){
					cm.warpParty(御龙魔地图,0);
					cm.gainItem(普通入场券,-1);
					cm.刷新地图();
					// 召唤怪物 怪物id 地图id ,坐标
					// cm.spawnMob_map(9601015,745010300,1116,337);//xml实现血量
					// cm.spawnMob_map(怪物ID,数量,x坐标,y坐标,地图id,血量); 
					cm.spawnMobOnMap(8300007,1,36,-11,御龙魔地图,9000000000); // npc实现血量	
					cm.setBossLog('御龙魔');
					cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
					cm.dispose();
				}else {
					cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[御龙魔]#k 血量 #r[90E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('御龙魔')+" #k次 #d[每日每个账号可挑战一次]#k\r\n3.请确保背包拥有#v"+普通入场券+"##d(每次挑战消耗1张)#k\r\n4.#v"+普通入场券+"#可在#b中介兑换获取#k\r\n\r\n "+蓝色小兔子+"#e#d您确定现在进行挑战吗?#k#l");
					cm.dispose();
				}
			} else if (beauty == 5){ // 黑魔女	
				if(cm.haveItem(普通入场券) && cm.getPlayer().getLevel() > 159 && cm.getBossLog('黑魔女') < 1){
					cm.warpParty(黑魔女地图,0);
					cm.gainItem(普通入场券,-1);
					cm.刷新地图();
					// 召唤怪物 怪物id 地图id ,坐标
					// cm.spawnMob_map(9601015,745010300,1116,337);//xml实现血量
					// cm.spawnMob_map(怪物ID,数量,x坐标,y坐标,地图id,血量); 
					cm.spawnMobOnMap(9001010,1,188,110,黑魔女地图,10000000000); // npc实现血量	
					cm.setBossLog('黑魔女');
					cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
					cm.dispose();
				}else {
					cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[黑魔女]#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('黑魔女')+" #k次 #d[每日每个账号可挑战一次]#k\r\n3.请确保背包拥有#v"+普通入场券+"##d(每次挑战消耗1张)#k\r\n4.#v"+普通入场券+"#可在#b中介兑换获取#k\r\n\r\n "+蓝色小兔子+"#e#d您确定现在进行挑战吗?#k#l");
					cm.dispose();
				}
			} else if (beauty == 8){ // 蜘蛛女王
				if(cm.haveItem(中级入场券) && cm.getPlayer().getLevel() > 159 && cm.getBossLog('蜘蛛女王') < 1){
					cm.warpParty(蜘蛛女王地图,0);
					cm.gainItem(中级入场券,-1);
					cm.刷新地图();
					// 召唤怪物 怪物id 地图id ,坐标
					// cm.spawnMob_map(9601015,745010300,1116,337);//xml实现血量
					// cm.spawnMob_map(怪物ID,数量,x坐标,y坐标,地图id,血量); 
					cm.spawnMobOnMap(8800400,1,456,80,蜘蛛女王地图,20000000000); // npc实现血量	
					cm.setBossLog('蜘蛛女王');
					cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
					cm.dispose();
				}else {
					cm.sendOk(""+ 任务简介 +"\r\n1.当前挑战的BOSS为#d[蜘蛛女王]#k 血量 #r[2000E]#k\r\n2.当前挑战次数 #b"+cm.getPlayer().getBossLog('蜘蛛女王')+" #k次 #d[每日每个账号可挑战一次]#k\r\n3.请确保背包拥有#v"+中级入场券+"##d(每次挑战消耗1张)#k\r\n4.#v"+中级入场券+"#可在#b中介兑换获取#k\r\n\r\n "+蓝色小兔子+"#e#d您确定现在进行挑战吗?#k#l");
					cm.dispose();
				}
			 }
		}
    }
}

*/
