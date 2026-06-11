var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 转生系统 = "#fEffect/CharacterEff1.img/QQ1408745/0/8#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
function start() {
         //if (cm.getPlayer().getOneTimeLogcs("转生") == 0){
           //  cm.setBossRank("渡劫",1,0);
             //cm.setBossRank("转生",1,0);
           //  cm.getPlayer().setOneTimeLogcs("转生",0);
            // cm.sendOkS("哇尊敬的冒险家你来啦,本功能为转生功能专区",2);
            // cm.openNpc(9330067,0);
             status = -1;
         	action(1, 0, 0);

	   // } else {
        //     status = -1;
        // 	action(1, 0, 0);
       // }
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

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
            text += ""+dd+""
			text += "#k_____________________________________________________\r\n"
			text += "\t\t\t\t#b#e本职业转生消耗#n#b\r\n"
			text += "\t#i4021009##z4021009# #r*10 #b个 #k+#b #i4011007##z4011007# #r*10 #b个\r\n"
			text += "\t#b#i3605009##z3605009# #r*5 #b个 #k+#b 金币消耗 #r*5 #b亿\r\n"
			text += "\t\t\t\t#b#e本职业转生奖励#n#b\r\n"
			text += "\t#b#i3605007##z3605007# #r* 1 #b个 #k+#b #i3605011##z3605011# #r* 1 #b个\r\n"
			text += "\t能力点#r+ 30 #b点 #k+#b #b伤害上限突破：#r+ 50000 #b点\r\n"
			text += "#k_____________________________________________________\r\n"

			text += "\t\t\t\t#r#e全职业转生消耗#n#b\r\n"
			text += "\t#i4021009##z4021009# #r*10#b个 #k+#b #i4011007##z4011007# #r*10#b个\r\n"
			text += "\t#b#n#i3605009##z3605009# #r*5#b个 #k+#b #i3605011##z3605011# #r*5#b个\r\n"
			text += "\t金币消耗 #r*10 #b亿 #k+#b 元宝消耗 #r*100 #b点\r\n"
			text += "\t\t\t\t#r#e全职业转生奖励#n#b\r\n"
			text += "\t#n#b#i2022518##z2022518##r * 1 #b个 #k+#b #i3605007##z3605007# #r* 2 #b个\r\n"
			text += "\t#b能力点 #r+ 50 #b点 #k+#b #b伤害上限突破：#r+ 100000 #b点\r\n"
			text += "#k_____________________________________________________\r\n"
			text += "#k当前拥有：#b元宝: #r"+cm.getmoneyb()+"#b \t金币: #r"+cm.getMeso()+"\r\n"

        text +="#e#r\t#L4#"+红色箭头+"本职业转生"+正方形+"#l\t#L2#"+红色箭头+"全职业转生"+正方形+"#l\r\n\r\n\t\t\t#L6#"+红色箭头+"领取转生奖励"+正方形+"#l\r\n"//3	
/*        if(cm.getPlayer().getOneTimeLogcs("转生")<=0) {
			
        text +="#L4##e#r我要转生#k#n#l\r\n\r\n"//3		
		
        } else if(cm.getPlayer().getOneTimeLogcs("转生")<=0) {

		text +="#L6##r我要转生、当前元宝:#r[ "+cm.getmoneyb() +" ]#k#n#l\r\n\r\n"//3	
		
        }*/
			
			cm.sendSimple(text);

           } else if(selection == 11){

			if(cm.getPlayer().getOneTimeLogcs("转生")<=87){
				cm.sendOk("你未完成88次转生");
				cm.dispose();
 
			}else{
            cm.gainItem(1142493,100,100,100,100,100,100,100,100,100,100,0,0,0,0)//    cm.gainItem(2531000,5);
 				cm.sendOk("领取成功");
				cm.dispose();
			}
           } else if(selection == 22){
			if(cm.getPlayer().getOneTimeLogcs("转生")<=187){
				cm.sendOk("你未完成188次转生");
				cm.dispose();
 
			}else{
            cm.gainItem(1142494,200,200,200,200,200,200,200,200,200,200,0,0,0,0)//    cm.gainItem(2531000,5);
 				cm.sendOk("领取成功");
				cm.dispose();
			}
			} else if (selection == 6) {
 				cm.openNpc(9900001,"转生奖励");
				//cm.dispose();
          //cm.dispose();cm.openNpc(9900004,999);

			} else if (selection == 2) {
           var c = cm.getChar();
			 var oStr = c.getStr();
			 var oDex = c.getDex();
			 var oInt = c.getInt();
			 var oLuk = c.getLuk();
			 var 总属性 = oStr + oDex + oInt + oLuk;
          
			if(cm.getPlayer().getOneTimeLogcs("转生")>=100){
				cm.sendOk("你已经完成了100次转生");
				cm.dispose();
			}else if(cm.getLevel() < 250){
				cm.sendOk("转生需要达到250级才可以哦");
				 cm.dispose();
			}else if(cm.getMeso() <1000000000){
				cm.sendOk("你没有10E金币");
				 cm.dispose();
			}else if(cm.getmoneyb() < 100){
				cm.sendOk("你没有100元宝");
				 cm.dispose();
			}else if(c.getRemainingAp() > 0){
				 //剩余能力值需要为0 还有没加的需要加完才能转生
				cm.sendOk("请加完所有的能力值之后再来转生");
			 cm.dispose();
			}else if (!cm.haveItem(3605009, 5) ) {
				cm.sendOk("#v3605009##z3605009#不足");
				cm.dispose();
			}else if (!cm.haveItem(3605011, 5) ) {
				cm.sendOk("#v3605011##z3605011#不足");
				cm.dispose();
			}else if (!cm.haveItem(4021009, 10) ) {
				cm.sendOk("#v4021009##z4021009#不足");
				cm.dispose();
			}else if (!cm.haveItem(4011007, 10) ) {
				cm.sendOk("#v4011007##z4011007#不足");
				cm.dispose();
			 }else if(oStr >= 32767 && oDex >= 32767 && oInt >= 32767 && oLuk >= 32767){
				 cm.sendOk("系统判断您使用非法软件\r\n\r\n后台已记录，请及时私信GM，不然会封号封IP");
			     cm.dispose();
			 }else {
				 var stat = new java.util.ArrayList();
				 c.resetStats(4,4,4,4);
			     cm.gainAp(-200);
                 //cm.gainAp(50);
				 c.setLevel(200);
				 cm.changeJob(0);
				 cm.gainMeso(-1000000000);
				 cm.setmoneyb(-100);
				 cm.gainItem(3605009, -5);
				 cm.gainItem(3605011, -5);
				 cm.gainItem(4021009, -10);
				 cm.gainItem(4011007, -10);
				 cm.gainItem(2022518, 1);
				 cm.gainItem(3605007, 2);
				 cm.gainDamage(+100000);
				 //cm.gainItem(3605006, 5)
				 //cm.gainItem(1112184,66,66,66,66,66,66,66,66,66,66,0,0,0,0)
				// cm.teachSkill(8,1,1);
				 //cm.teachSkill(1005,1,1);
				 //cm.teachSkill(20000012,1,20);
                 cm.getPlayer().gainOneTimeLogcs("转生",+1)
				 cm.刷新();
				 cm.喇叭(1,"玩家 [" + cm.getPlayer().getName() + "] 成功完成第 "+(cm.getPlayer().getOneTimeLogcs("转生"))+" 次转生!");
				 cm.dispose();
			}
			} else if (selection == 4) {
           var c = cm.getChar();
			 var oStr = c.getStr();
			 var oDex = c.getDex();
			 var oInt = c.getInt();
			 var oLuk = c.getLuk();
			 var 总属性 = oStr + oDex + oInt + oLuk;
          
			if(cm.getPlayer().getOneTimeLogcs("转生")>=100){
				cm.sendOk("你已经完成了100次转生");
				cm.dispose();
			}else if(cm.getLevel() < 250){
				cm.sendOk("转生需要达到250级才可以哦");
				 cm.dispose();
			}else if(cm.getMeso() <500000000){
				cm.sendOk("你没有5E金币");
				 cm.dispose();
		//	}else if(c.getRemainingAp() > 0){
				 //剩余能力值需要为0 还有没加的需要加完才能转生
			//	cm.sendOk("请加完所有的能力值之后再来转生");
			/// cm.dispose();
			}else if (!cm.haveItem(3605009, 5) ) {
				cm.sendOk("#v3605009##z3605009#不足");
				cm.dispose();
			/*}else if (!cm.haveItem(3605011, 5) ) {
				cm.sendOk("#v3605011##z3605011#不足");
				cm.dispose();*/
			}else if (!cm.haveItem(4021009, 10) ) {
				cm.sendOk("#v4021009##z4021009#不足");
				cm.dispose();
			}else if (!cm.haveItem(4011007, 10) ) {
				cm.sendOk("#v4011007##z4011007#不足");
				cm.dispose();
			 }else if(oStr >= 32767 && oDex >= 32767 && oInt >= 32767 && oLuk >= 32767){
				 cm.sendOk("系统判断您使用非法软件\r\n\r\n后台已记录，请及时私信GM，不然会封号封IP");
			     cm.dispose();
			 }else {
				 var stat = new java.util.ArrayList();
				 c.resetStats(4,4,4,4);
			     cm.gainAp(-220);
                 //cm.gainAp(30);
				 c.setLevel(200);
				 cm.gainMeso(-500000000);
				 //cm.setmoneyb(-100);
				 cm.gainItem(3605009, -5);
				 //cm.gainItem(3605011, -5)
				 cm.gainItem(4021009, -10);
				 cm.gainItem(4011007, -10);
				 cm.gainItem(3605007, 1);
				 cm.gainItem(3605011, 1);
				 cm.gainDamage(+50000);
				 //cm.gainItem(1112184,66,66,66,66,66,66,66,66,66,66,0,0,0,0)
				// cm.teachSkill(8,1,1);
				 //cm.teachSkill(1005,1,1);
				 //cm.teachSkill(20000012,1,20);
                 cm.getPlayer().gainOneTimeLogcs("转生",+1)
				 cm.刷新();
				 cm.喇叭(1,"玩家 [" + cm.getPlayer().getName() + "] 成功完成第 "+(cm.getPlayer().getOneTimeLogcs("转生"))+" 次转生!");
				 cm.dispose();
			 }
        }
    }
}
