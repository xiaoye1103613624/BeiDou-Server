var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var 大心 = "#fEffect/CharacterEff/1051295/0/0#";
var 琴符 = "#fUI/UIWindow/Quest/icon0#";
var 小雪花 = "#fEffect/CharacterEff/1003393/0/0#";
var 音符 = "#fEffect/CharacterEff/1032063/0/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 爱心1 = "#fEffect/CharacterEff/1032063/0/0#";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";

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
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			text += "\t\t\t  \r\n                 #r#v3994066##v3994084##v3994068##k  \r\n\r\n\r\n";
	//		text += "                 #r主线任务完成25个获得\r\n       超强坐骑#v1902001##v1912000#（每件四维108，攻魔78）\r\n  #L100#主线任务完成10个获得#s9001006#超级龙咆哮技能#s9001006##l\r\n\r\n"
			
			if(cm.getBossLog("主线",1) == 0 && cm.getLevel() > 119){
					text += "               #L1#"+小烟花+"#b开始任务1#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 0 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务1完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务1(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 1 && cm.getLevel() > 119){
					text += "               #L2#"+小烟花+"#b开始任务2#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 1 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务2完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务2(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 2 && cm.getLevel() > 119){
					text += "               #L3#"+小烟花+"#b开始任务3#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 2 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务3完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务3(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 3 && cm.getLevel() > 119){
					text += "               #L4#"+小烟花+"#b开始任务4#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 3 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务4完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务4(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 4 && cm.getLevel() > 119){
					text += "               #L5#"+小烟花+"#b开始任务5#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 4 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务5完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务5(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 5 && cm.getLevel() > 119){
					text += "               #L6#"+小烟花+"#b开始任务6#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 5 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务6完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务6(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			
			if(cm.getBossLog("主线",1) == 6 && cm.getLevel() > 119){
					text += "               #L7#"+小烟花+"#b开始任务7#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 6 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务7完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务7(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			if(cm.getBossLog("主线",1) == 7 && cm.getLevel() > 119){
					text += "               #L8#"+小烟花+"#b开始任务8#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 7 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务8完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务8(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			
			if(cm.getBossLog("主线",1) == 8 && cm.getLevel() > 119){
					text += "               #L9#"+小烟花+"#b开始任务9#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 8 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务9完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务9(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 9 && cm.getLevel() > 119){
					text += "               #L10#"+小烟花+"#b开始任务10#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 9 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务10完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "              "+小烟花+"#b任务10(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 10 && cm.getLevel() > 119){
					text += "               #L11#"+小烟花+"#b开始任务11#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 10 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务11完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "             "+小烟花+"#b任务11(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 11 && cm.getLevel() > 119){
					text += "               #L12#"+小烟花+"#b开始任务12#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 11 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务12完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "             "+小烟花+"#b任务12(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}
			
			if(cm.getBossLog("主线",1) == 12 && cm.getLevel() > 119){
					text += "               #L13#"+小烟花+"#b开始任务13#n"+小烟花+"#l\r\n\r\n"//3
				} else if(cm.getBossLog("主线",1) > 12 && cm.getLevel() > 119){
					text += "                  "+小烟花+"#b任务13完成#n"+小烟花+"#l#k\r\n"//3
				} else {
					text += "             "+小烟花+"#b任务13(#rlv.120#b)待命"+小烟花+"\r\n"//3
			}

            cm.sendSimple(text);
        } else if (selection == 1) {
			if (cm.haveItem(4000019,50) && cm.haveItem(4000000,50)){
				cm.gainItem(4000019, -50);//绿蜗牛壳
				cm.gainItem(4000000, -50);//蓝蜗牛壳

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务1，");
				cm.sendOk("#r恭喜你，完成了任务1，");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000019#*50，#v4000000#*50交给我\r\n\r\n\r\n");
				cm.dispose();
			}
        } else if (selection == 2) {
			if (cm.haveItem(4000002,50) && cm.haveItem(4000017,50)){
				cm.gainItem(4000002, -50);//蝴蝶结
				cm.gainItem(4000017, -50);//猪头

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务2");
				cm.sendOk("#r恭喜你，完成了任务2");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000002#*50，#v4000017#*50交给我\r\n\r\n\r\n");
				cm.dispose();
			}
        } else if (selection == 3) {
			if (cm.haveItem(4000021,50) && cm.haveItem(4003004,50)){
				cm.gainItem(4000021,-50);//动物皮
				cm.gainItem(4003004,-50);//粗羽毛

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务3");
				cm.sendOk("#r恭喜你，完成了任务3");
				cm.dispose();	
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000021#*50，#v4003004#*50交给我\r\n\r\n\r\n");
				cm.dispose();		
			}
        } else if (selection == 4) {			
			if (cm.haveItem(4000015,50) && cm.haveItem(4000008,50)){
				cm.gainItem(4000015, -50);//刺蘑菇盖
				cm.gainItem(4000008, -50);//道符

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务4");
				cm.sendOk("#r恭喜你，完成了任务4");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000015#*50，#v4000008#*50交给我\r\n\r\n\r\n");
				cm.dispose();
			}
        } else if (selection == 5) {			
			if (cm.haveItem(4001006,20)){
				cm.gainItem(4001006, -20);//火焰羽毛

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务5");
				cm.sendOk("#r恭喜你，完成了任务5");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4001006#*20交给我\r\n");
				cm.dispose();
			}
        } else if (selection == 6) {
			if (cm.haveItem(4000006 ,50) && cm.haveItem(4000177 ,50)){
				cm.gainItem(4000006, -50);//三眼章鱼触角
				cm.gainItem(4000177, -50);//混种石块

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务6");
				cm.sendOk("#r恭喜你，完成了任务6");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000006#*50，#v4000177#*50交给我\r\n");
				cm.dispose();
			}
        } else if (selection == 7) {
			if (cm.haveItem(4031227, 5)){
				cm.gainItem(4031227, -5);//赤珠

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务7");
				cm.sendOk("#r恭喜你，完成了任务7");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4031227#*5交给我\r\n");
				cm.dispose();
			}
        } else if (selection == 8) {
			if (cm.haveItem(4000034 ,50) && cm.haveItem(4000082 ,10)){
				cm.gainItem(4000034, -50);//蛇皮
				cm.gainItem(4000082, -10);//僵尸丢失的金齿

                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务8");
				cm.sendOk("#r恭喜你，完成了任务8，");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000034#*50，#v4000082#*10交给我\r\n");
				cm.dispose();
			}
		} else if (selection == 9) {
			if (cm.haveItem(4000128, 25) && cm.haveItem(4000129, 25)){
				cm.gainItem(4000128, -25);//黄小丑的帽子
				cm.gainItem(4000129, -25);//红小丑的珠子

				cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务9");
				cm.sendOk("#r恭喜你，完成了任务9");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000128#*25，#v4000129#*25交给我\r\n");
				cm.dispose();
			}
		} else if (selection == 10) {
			if (cm.haveItem(2210006, 5) && cm.haveItem(4000040, 5)){
				cm.gainItem(2210006, -5);//TT色蜗牛壳儿
				cm.gainItem(4000040, -5);//蘑菇王芽孢

				cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务10");
				cm.sendOk("#r恭喜你，完成了任务10，");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v2210006#*5，#v4000040#*5交给我\r\n");
				cm.dispose();
			}	
		} else if (selection == 11) {
			if (cm.haveItem(4000176, 5) && cm.haveItem(4000094, 5)){
				cm.gainItem(4000176, -5);//毒菇
				cm.gainItem(4000094, -5);//老板的名牌


                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务11");
				cm.sendOk("#r恭喜你，完成了任务11，");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请收集#v4000176#*5，#v4000094#*5交给我\r\n\r\n\r\n");
				cm.dispose();
			}
		} else if (selection == 12) {
			if (cm.haveItem(4000232 ,50) && cm.haveItem(4000233 ,50) && cm.haveItem(4000234 ,50) ){

				cm.gainItem(4000232, -50);//半人马的火花
				cm.gainItem(4000233, -50);//半人马的净水
				cm.gainItem(4000234, -50);//半人马的骨头


                cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了任务12，");
				cm.sendOk("#r恭喜你，完成了任务12，！");
				cm.dispose();
			}else{
				cm.sendOk("#r请收集#v4000232#*50，#v4000233#*50，#v4000234#*50交给我\r\n");
				cm.dispose();
			}
		} else if (selection == 13) {
			if (cm.haveItem(1112024 ,1) ){

				cm.gainItem(1112024, -1);//时间鬼王的冰块


				cm.gainItem(4033334,1);//混沌卷轴
				cm.即时存档();
				cm.setBossLog1("主线",1);
				cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]完成了魂之终极任务，给予终极水晶");
				cm.sendOk("#r恭喜你，完成了魂之终极任务，获得终极水晶");
				cm.dispose();
			}else{
				cm.sendOk("\r\n\r\n#r请把最终魂戒给我#v1112024#*1交给我\r\n");
				cm.dispose();
			}

		} 
    }
}
