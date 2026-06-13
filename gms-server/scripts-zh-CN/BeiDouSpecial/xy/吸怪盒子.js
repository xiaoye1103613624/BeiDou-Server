var 小雪花 = "#fEffect/CharacterEff/1003393/0/0#";
var  a1 = "#fUI/ChatBalloon.img/28/w#";//右上
var  a2 = "#fUI/ChatBalloon.img/28/e#";//上中
var 笑脸1 ="#fUI/GuildMark/Mark/Etc/00009025/1#";
var 笑脸3 ="#fUI/GuildMark/Mark/Etc/00009025/3#";
var 笑脸5 ="#fUI/GuildMark/Mark/Etc/00009025/5#";
var 笑脸7 ="#fUI/GuildMark/Mark/Etc/00009025/7#";
var 笑脸9 ="#fUI/GuildMark/Mark/Etc/00009025/9#";
var 笑脸11 ="#fUI/GuildMark/Mark/Etc/00009025/11#";
var 笑脸13 ="#fUI/GuildMark/Mark/Etc/00009025/13#";
var 笑脸16 ="#fUI/GuildMark/Mark/Etc/00009025/16#";
var 禁止2 ="#fUI/GuildMark/Mark/Pattern/00004004/2#";
var 禁止3 ="#fUI/GuildMark/Mark/Pattern/00004004/3#";
var 禁止4 ="#fUI/GuildMark/Mark/Pattern/00004004/4#";
var 吸怪系统 = "#fEffect/CharacterEff1.img/QQ1408745/2/9#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
function start() {
    status = -1;
    action(1, 0, 0);
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
        }else {
            status--;
        }
        if (status == 0) {
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			text += ""+dd+"\r\n"+群粉心+"";
            //text += "         #v1003861#    #r欢迎使用.吸怪功能    #v1003861##k#n\r\n" 
			//text += "#r#e----------------------------------------------#k#n\r\n";
		    //text += "\t\t\t\t#b当前坐标：[X:#r"+cm.getPlayer().getPosition().x +"  #bY:#r"+cm.getPlayer().getPosition().y +"#b]\r\n";
 if (cm.getPlayer().getMapId() == 910000000//自由 
    ||cm.getPlayer().getMapId() == 910000010//自由洞10
	||cm.getPlayer().getMapId() == 910000011//自由洞11
	||cm.getPlayer().getMapId() == 910000012//自由洞12
    ||cm.getPlayer().getMapId() == 104000000//明珠港
	||cm.getPlayer().getMapId() == 100000000//射手村
	||cm.getPlayer().getMapId() == 101000000//魔法林
	||cm.getPlayer().getMapId() == 102000000//勇士部落
	||cm.getPlayer().getMapId() == 103000000//废弃
   ||cm.getPlayer().getMapId() == 230040420//鱼王
   ||cm.getPlayer().getMapId() == 701010323//蜈蚣
   ||cm.getPlayer().getMapId() == 220080001//闹钟
   ||cm.getPlayer().getMapId() == 240020401//喷火龙
   ||cm.getPlayer().getMapId() == 240020101 //天鹰
   ||cm.getPlayer().getMapId() == 541020800 //树精  
   ||cm.getPlayer().getMapId() == 551030200//暴力熊 
   ||cm.getPlayer().getMapId() == 702060000//武僧    
   ||cm.getPlayer().getMapId() == 240060200//黑龙
   ||cm.getPlayer().getMapId() == 280030000//扎昆
   ||cm.getPlayer().getMapId() == 270050100//品克斌   

   ||cm.getPlayer().getMapId() == 105200210//高级 
   ||cm.getPlayer().getMapId() == 910141030//高级    
   ||cm.getPlayer().getMapId() == 913010000//高级 
   ||cm.getPlayer().getMapId() == 401060200//高级
   ||cm.getPlayer().getMapId() == 927020070//高级 
   ||cm.getPlayer().getMapId() == 940001100//高级
   
   ||cm.getPlayer().getMapId() == 861000050//五兄弟
   ||cm.getPlayer().getMapId() == 910150100//女神
   ||cm.getPlayer().getMapId() == 101050010//女神
   ||cm.getPlayer().getMapId() == 350060160//女神
   ||cm.getPlayer().getMapId() == 101071301//萌新
   
   
   ){
			text += "\r\n\t#k我问：#b魔镜~魔镜~谁是这个世界上最帅最有钱的人?\r\n\r\n\t#k魔镜:#r#e[" + cm.getChar().getName() + "]#b#n是这个世界上最帅最有钱的人！\r\n\r\n\t#k魔镜:#b但是你再帅再有钱~在这里也不能用#r[吸怪系统]\r\n"
			cm.dispose();
} else {
			text += "\t\t #L1##r#e"+笑脸1+"开启吸怪"+笑脸1+"#l\t#L2##k"+禁止2+"关闭吸怪"+禁止2+"#l\r\n\r\n"
			text += "\t\t#L3##r#e"+笑脸3+"开启轮回"+笑脸3+"#l\t#L4##k"+禁止3+"关闭轮回"+禁止3+"#l\r\n\r\n"

       if(cm.haveItem(5010019,1)){	
			text += "\r\n#n#b----------------------≮月卡功能≯--------------------#n\r\n";	
			text += "#L5##r#e"+笑脸5+"自动杀怪升级"+笑脸5+"#n#k------#b[点击开启]#n#l\r\n\r\n\t#r说明#k：练级地图自动击杀所有怪物。#d(#g换地图自动关闭#d)\r\n\r\n";	
//  #L55##k[关闭按钮]#n#k#l
			text += "#L6##r#e"+笑脸7+"物品掉落脚下"+笑脸7+"#n#k------#b[点击开启]#n#l\r\n\r\n\t#r说明#k：所有掉落物品被吸到玩家脚下。#d(#g换地图自动关闭#d)\r\n\r\n";		

			text += "#L7##r#e"+笑脸9+"自动贩卖装备"+笑脸9+"#n#k------#b[点击开启]#n#l\r\n\r\n\t#r说明#k：拾取的所有装备自动变成金币。#d(#g换地图自动关闭#d)\r\n\r\n";		

			text += "#L8##r#e"+笑脸11+"自动存放金币"+笑脸11+"#n#k------#b[点击开启]#n#l\r\n\r\n\t#r说明#k：身上金币超10亿自动兑换存放。#d(#g换地图自动关闭#d)\r\n\r\n";	

			text += "#L9##r#e"+笑脸13+"@超级复活术"+笑脸13+"#n#b  每天限使用:#r 5  #k次   [@复活术]#n#l\r\n\r\n\t#r说明#k：玩家挂了时打出:[#r@复活术#k]即可复活自己。\r\n\r\n";				
        } else {
			text += "\r\n#n#b----------------------≮月卡功能≯--------------------\r\n\r\n"
			text += "\t#r"+笑脸5+"自动杀怪升级"+笑脸7+"物品掉落脚下"+笑脸9+"自动贩卖装备"+笑脸11+"\r\n\r\n\t\t\t"+笑脸11+"自动存放金币"+笑脸13+"超级复活术"+笑脸9+"\r\n"
}
}
			
            cm.sendSimpleS(text,2);
        } else if (status == 1) {
            if (selection == 1) {
				cm.getPlayer().starXg();
				cm.dispose();
            }else if (selection == 2) {
				cm.getPlayer().cancelXg();
				cm.dispose();
            
            }else if(selection ==3){
				cm.getMap().setDbg(cm.getPlayer())
				cm.dispose()
            }else if(selection ==4){
				cm.getMap().closeDbg(cm.getPlayer())
				cm.dispose()
            }else if(selection ==5){
               
			   
			   
			   
			   cm.getPlayer().startzdqg()

		       cm.dispose();
            }else if(selection ==55){
               
			   cm.getPlayer().cancelzdqg()
			   
			   // cm.processCommand("@全图开心秒怪");
		        cm.dispose();
			}else if(selection ==6){
                cm.processCommand("@物品开心掉落脚下");
		        cm.dispose();
            }else if(selection ==7){
                cm.processCommand("@开心自动贩卖");
		        cm.dispose();
            }else if(selection ==8){
                cm.processCommand("@金币开心变物品");
		        cm.dispose();
            }else if(selection ==9){
                cm.sendOk("当自己不小心嘎了时在对话框打出：\r\n#r@复活术\r\n#k你会感受到海王唐三的爱\r\n每次会失去一个复活术（谨慎使用）");
		        cm.dispose();
            }else if(selection ==19){
			if (cm.getPlayer().getBossLog("月卡免费功能") >=1) {
				cm.sendOk("今天使用过月卡免费特权的次数了");
				cm.dispose();
			}else{
             //   cm.getPlayer().setBossLog("月卡免费功能");//cm.getPlayer().modifyCSPoints(1,-10000, true);
				cm.getMap().setDbg(2,120)
                cm.getPlayer().dropMessage(1,"当前地图\r\n1.开启2倍怪 持续120分钟\r\n2.刷新速度提高 持续120分钟");
                cm.getPlayer().setBossLog("月卡免费功能");
//cm.全服漂浮喇叭("〖月卡吸怪〗 恭喜 ["+cm.getName()+"] 运气爆棚获得了[ "+cm.getItemName(itemId)+" ] "+quantity+"个", 5120026);
				//cm.sendOk("当前地图开启2倍怪，持续60分钟")
				cm.dispose()
}
			}
       }
    }
}
