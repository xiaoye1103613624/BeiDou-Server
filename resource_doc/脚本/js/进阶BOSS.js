var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 音符 = "#fEffect/CharacterEff/1032063/0/0#";
var 红箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 圆点 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#"; 
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#"; 
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#"; 
var 蓝兔 = "#fEffect/CharacterEff.img/1112960/3/1#"; 
var 橙条 = "#fUI/UIWindow.img/Minigame/Common/barTeamA#"; 
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#"; 
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";  
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";  
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#";  
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#";  
var 热点推荐 = "#fUI/CashShop.img/CSChar/BtCoordination/normal/0#";
var 铅笔 = "#fUI/GuildBBS.img/GuildBBS/BtReply/mouseOver/0#"; 
var 入场 = "#fUI/CN_Chat.img/roomList/BtEnter/mouseOver/0#";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n"; 
///////////////////////////////////////////
//////////////////		boss挑战相关	////////////////////////
var 进阶入场 = 3994742;

var 地图1 = 240093310;
var BOSS1 = 9410184;
var BOSS11 = 9410184;
var BOSS111 = 9410184;
var BOSS1血量 = 1000000;

var 地图2 = 240093310;
var 地图3 = 272020210;
var 地图4 = 105200210;
var 地图5 = 105200310;
var 地图6 = 105200410;
var 地图7 = 105200510;
var 地图8 = 703200810;
var 地图9 = 401060300;
//BOSS ID
var 高级入场券 = 3994589;
var 半半 = 8910000;
var flag = "#fUI/CN_Chat.img/roomList/BtEnter/mouseOver/0#";
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
           
            text += ""+dd+"\r\n\t\t\t"+挑战中心+"\r\n"
 /*           text += "\t\t\t "+音符+"#e#r 进 阶 BOSS "+音符+"\r\n\r\n"
            text += "#gBOSS介绍:\r\n#n#k\t挑战资格为#r120级以上#k且拥有足够的#r[彩虹鱼]\r\n\t#k只能单人进入挑战，期间别人可消耗#r[彩虹鱼]#k进入\r\n\tBOSS血量为#k(简单*1倍)#d(普通*3倍)#b(困难*9倍)\r\n\t#kBOSS爆率为#k(简单*1倍)#d(普通*2倍)#b(困难*4倍)\r\n\t#kBOSS挑战时间为#r600秒#k，玩家#r可用普攻攻击#k其他玩家\r\n"
			text += "#e#g主要产物:#i2022467#"+圆点+"#i2022465#"+圆点+"#i4170016#"+圆点+"#i4170007#"+圆点+"#i4021009#"+圆点+"#i4011007#"+圆点+"\r\n"
			text += "#i4251202#"+圆点+"#i4310156#"+圆点+"#i4000313#"+圆点+"#i4310143#"+圆点+"#i4310097#"+圆点+"#i4310108#"+圆点+"#i4000464#"+圆点+"\r\n#n"+群粉心+""
			*/
				text += "#e#r"+红箭头+"T4进阶BOSS【#o"+BOSS1+"#】#n #k每次挑战需要#r 10只#i"+进阶入场+"#\r\n"+蓝箭头+"#k基础血量#r("+BOSS1血量+")#k； 每日可#r(" + cm.getBossLog(BOSS1) + "/3)#k次任选模式挑战\r\n"
				text += "#e#k#L1#"+圆点+"简单模式"+圆点+"#l  #e#d#L11#"+圆点+"普通模式"+圆点+"#l  #e#b#L111#"+圆点+"困难模式"+圆点+"#l\r\n\r\n"+群粉心+""

				text += "\r\n#e#r"+红箭头+蓝箭头+"#o8880004#"+任务提示+"#n#k每日限制挑战 #r（" + cm.getPlayerCount(地图1) + "/3）#k次\r\n#rBOSS血量：#k简单#r5千万  #k普通#r1.5亿  #k困难#r4.5亿\r\n"
				text += "#e#k#L2#"+圆点+"简单模式"+圆点+"#l  #e#d#L22#"+圆点+"普通模式"+圆点+"#l  #e#b#L222#"+圆点+"困难模式"+圆点+"#l\r\n\r\n"+群粉心+""
				
				text += "\r\n#e#r"+红箭头+蓝箭头+"#o8880004#"+任务提示+"#n#k每日限制挑战 #r（" + cm.getPlayerCount(地图1) + "/3）#k次\r\n#rBOSS血量：#k简单#r5千万  #k普通#r1.5亿  #k困难#r4.5亿\r\n"
				text += "#e#k#L3#"+圆点+"简单模式"+圆点+"#l  #e#d#L33#"+圆点+"普通模式"+圆点+"#l  #e#b#L333#"+圆点+"困难模式"+圆点+"#l\r\n\r\n"+群粉心+""
				
				text += "\r\n#e#r"+红箭头+蓝箭头+"#o8880004#"+任务提示+"#n#k每日限制挑战 #r（" + cm.getPlayerCount(地图1) + "/3）#k次\r\n#rBOSS血量：#k简单#r5千万  #k普通#r1.5亿  #k困难#r4.5亿\r\n"
				text += "#e#k#L4#"+圆点+"简单模式"+圆点+"#l  #e#d#L44#"+圆点+"普通模式"+圆点+"#l  #e#b#L444#"+圆点+"困难模式"+圆点+"#l\r\n\r\n"+群粉心+""
				

            cm.sendSimple(text);
/*        } else if (status == 1){
			//var count = cm.getChar().getVip() == 0 ? 1:cm.getChar().getVip()+1;
			var pdd = "";
			pdd += ""+ 任务简介 +"\r\n";
			switch(selection){
				case 1:
					pdd += "1.当前挑战的BOSS为#d【#o"+BOSS1+"#】#k 血量 #r[100E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('BOSS1')+" #k次\r\n";
					pdd += "3.请确保背包拥有#v"+进阶BOSS入场+"##d(每次挑战消耗10只)#k\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b自由市场钓鱼获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 1;
					break;
				case 2:
					pdd += "1.当前挑战的BOSS为#d[阿卡伊勒]#k 血量 #r[400E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('阿卡伊勒')+"/1 #k次\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b中介兑换获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 2;
					break;
				case 3:
					pdd += "1.当前挑战的BOSS为#d[半半]#k 血量 #r[600E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('半半')+"/1 #k次\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b中介兑换获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 3;
					break;
				case 4:
					pdd += "1.当前挑战的BOSS为#d[希纳斯]#k 血量 #r[200E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('希纳斯')+"/1 #k次\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b中介兑换获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 4;
					break;
				case 5:
					pdd += "1.当前挑战的BOSS为#d[麦格纳斯]#k 血量 #r[300E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('麦格纳斯')+"/1 #k次\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b中介兑换获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 5;
					break;
				case 6:
					pdd += "1.当前挑战的BOSS为#d[血腥女王]#k 血量 #r[1800E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('血腥女王')+"/1 #k次\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b中介兑换获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 6;
					break;
				case 7:
					pdd += "1.当前挑战的BOSS为#d[赤虎]#k 血量 #r[100E]#k\r\n";
					pdd += "2.当前挑战次数 #b"+cm.getPlayer().getBossLog('赤虎')+"/1 #k次\r\n";
					pdd += "4.#v"+进阶BOSS入场+"#可在#b中介兑换获取#k\r\n\r\n";
					pdd += ""+蓝兔+"#e#d您确定现在进行挑战吗?#k#l\r\n";
					cm.sendYesNo(pdd);
					beauty = 7;
					break;
				case 8:
					cm.sendOk("暂未开启");
					break;
			}
			*/
		}else if(status == 1){
			//var count = cm.getChar().getVip() == 0 ? 1:cm.getChar().getVip()+1;
			if (cm.getPlayer().getParty() == null){
				cm.sendOk(""+ 任务简介 +"\r\n首先您必须拥有一个队伍#k");
				cm.dispose();
			}else if (!cm.isLeader()){
				cm.sendOk(""+ 任务简介 +"\r\n您不是队长,请让队长与我对话#k");
				cm.dispose();
			}else if(cm.getLevel() <= 159){
				cm.sendOk(""+任务提示+"\r\n1.挑战进阶BOSS等级需要#b160级#k以上,请确认...");
				cm.dispose();	
			}else if(!cm.haveItem(进阶BOSS入场 , 10)){
				cm.sendOk(""+任务提示+"\r\n1.挑战进阶BOSS需要10只#v"+进阶BOSS入场+"#\r\n");
				cm.dispose();	
			}else {
				if(beauty == 1){
					if(cm.getBossLog('蜘蛛女王')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						cm.setBossLog('蜘蛛女王');
					//	cm.刷新指定地图(地图2);
						cm.warpParty(地图2,0); // 传送到BOSS地图
						cm.gainItem(进阶BOSS入场,-1);
						cm.spawnMobOnMap(8800400,1,121,97,地图2); // npc实现血量	
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 蜘蛛女王[100亿HP]!!!");
						cm.dispose();
					}
				}else if(beauty == 2){
					if(cm.getBossLog('阿卡伊勒')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						cm.setBossLog('阿卡伊勒');
						//cm.刷新指定地图(地图3);
						cm.warpParty(地图3,0); // 传送到BOSS地图
						cm.gainItem(进阶BOSS入场,-1);
						cm.spawnMobOnMap(8860000,1,-2,-181,地图3,40000000000); // npc实现血量		
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 阿卡伊勒[400亿HP]!!!");
						cm.dispose();
				}
				}else if(beauty == 3){
					if(cm.getBossLog('半半')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						cm.setBossLog('半半');
						//cm.刷新指定地图(地图7);
						cm.warpParty(地图7,0); // 传送到BOSS地图
						cm.gainItem(进阶BOSS入场,-1);
						cm.spawnMobOnMap(8910000,1,-27,455,地图7,60000000000); // npc实现血量		
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 半半[600亿HP]!!!可真是个狠人啊~!");
						cm.dispose();
				}
				}else if(beauty == 4){
					if(cm.getBossLog('希纳斯')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						//cm.刷新指定地图(地图8);
						cm.gainItem(进阶BOSS入场,-1);
						cm.setBossLog('希纳斯');
						cm.warpParty(地图8,0); // 传送到BOSS地图
						cm.spawnMobOnMap(9300742,1,-183,115,地图8); // npc实现血量	
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 希纳斯[200亿HP]!!!可真是个狠人啊~!");
						cm.dispose();
				}
				}else if(beauty == 5){
					if(cm.getBossLog('麦格纳斯')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						//cm.刷新指定地图(地图9);
						cm.gainItem(进阶BOSS入场,-1);
						cm.setBossLog('麦格纳斯');
						cm.warpParty(地图9,0); // 传送到BOSS地图    
						cm.spawnMobOnMap(8880000,1,1519,-1348,地图9); // npc实现血量
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 麦格纳斯[300亿HP]!!!可真是个狠人啊~!");
						cm.dispose();
				}
				}else if(beauty == 6){
					if(cm.getBossLog('血腥女王')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						//cm.刷新指定地图(地图5);
						cm.gainItem(进阶BOSS入场,-1);
						cm.setBossLog('血腥女王');
						cm.warpParty(地图5,0); // 传送到BOSS地图
						cm.spawnMobOnMap(8920100,1,66,7,地图5,180000000000); // npc实现血量
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 血腥女王[1800亿HP]!!!可真是个狠人啊~!");
						cm.dispose();
				}
				}else if(beauty == 7){
					if(cm.getBossLog('赤虎')> 1){
						cm.sendOk("您今日次数已用尽,请确认\r\n可以让其他玩家当队长带您进入地图");
						cm.dispose();
					}else {
						//cm.刷新指定地图(赤虎地图);
						cm.gainItem(进阶BOSS入场,-1);
						cm.setBossLog('赤虎');
						cm.warpParty(赤虎地图,0); // 传送到BOSS地图
						cm.spawnMobOnMap(9601014,1,66,7,赤虎地图,10000000000); // npc实现血量
						cm.showInstruction("#r[BOSS挑战说明]#k\r\n您已经进入BOSS地图,快去消灭BOSS吧!!!\r\n", 240, 60);
						cm.喇叭(4, "[" + cm.getPlayer().getName() + "]带领TA的队伍前往挑战进阶BOSS - 赤虎[100亿HP]!!!可真是个狠人啊~!");
						cm.dispose();
				}
				}
			}
			
			}//status2结束
			
		}
}



