var status = -1;
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 黄条上 = "#fUI/ChatBalloon.img/pet/25/head#";
var 黄条下 = "#fUI/ChatBalloon.img/pet/25/s#";
var 黄条下左 = "#fUI/ChatBalloon.img/pet/25/sw#";
var 黄条下右 = "#fUI/ChatBalloon.img/pet/25/se#";
var 黄条左 = "#fUI/ChatBalloon.img/pet/25/nw#";
var 黄条右 = "#fUI/ChatBalloon.img/pet/25/ne#";
var 五子棋 = "#fUI/ChatBalloon.img/miniroom/Omok#";
var 斜金币 = "#fUI/ChatBalloon.img/miniroom/PersonalShop#";
var 熊猫 = "#fUI/ChatBalloon.img/pet/1/nw#";
var 毛球 = "#fUI/ChatBalloon.img/pet/12/nw#";
var 金冠 = "#fUI/UIWindow.img/UserInfo/bossPetCrown#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#"
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 开心冒险岛 = "#fEffect/CharacterEff1.img/QQ1408745/1/12#";

var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var shx1 ="#fUI/ChatBalloon/dead/n#";
var ygw1 ="#fUI/ChatBalloon/dead/ne#";
var zgw1 ="#fUI/ChatBalloon/dead/nw#";

var xhx2 ="#fUI/ChatBalloon/dead/s#";
var ygw2 ="#fUI/ChatBalloon/dead/se#";
var zgw2 ="#fUI/ChatBalloon/dead/sw#";
var 空格 = "#fUI/Basic/CheckBox/0#";

function start() {
    action(1, 0, 0)
}

function action(mode, type, selection) {

    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
		 var selStr = "\r\n";	

			//selStr +=  ""+dd+"\r\n\t\t\t"+开心冒险岛+"\r\n"+群粉心+""
			selStr += "\t#r#e   	  "+ 红星 + ""+ 大红星 + ""+ 红点 + "" + cm.开服名称() + "排名系统"+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k \r\n";

			selStr +=""+zgw1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+shx1+ygw1+"\r\n"	
		 //cm.getPlayer().gainOneTimeLogcs("成就",+1);
			//selStr += "                 #r欢迎来到综合排名中心#k\r\n";
			//selStr += ""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+"\r\n\r\n";
			//selStr += "\t\t\t\t #L0##d返回拍卖界面#k#l\r\n\r\n";
			selStr += "#r#L1##v4031569#战 力 排 名#v4031569##l #r#L3##v1142499#仙 级 排 名#v1142499##l#k\r\n\r\n\r\n";//#b#L2#"+小烟花+"飞升排行"+小烟花+"#l#k
			selStr += "#b#L6##v2022546#公 会 排 名#v2022546##l#k #b#L4##v5010073#人 气 排 名#v5010074##l#k\r\n\r\n";
			//selStr += "      #b#L6#"+空格+"公会排名#l#k
			selStr +=""+zgw2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+xhx2+ygw2+"\r\n"				
			
			
			//selStr += "    #r#L5#"+小烟花+"人气排行"+小烟花+"#l#k         \r\n\r\n";
			cm.sendSimple(selStr);
        //cm.sendSimple("#b你好 #k#h  ##e  #b我是排名系统。#k\r\n#L5##r战力排行\n\#l\r\n#L1##d玩家排名#l\r\n#L2##b金币排名#l#l\r\n#L3##r职业排名#l\r\n#L4##d人气排行#l \r\n#L0##d公会排名#l");
    } else if (status == 1) {
        if (selection == 0) {
			cm.dispose();
            cm.openNpc(9900004);
                       
        } else if (selection == 1) {
            cm.dispose();
			cm.openNpc(9010000,"最强战力排行榜");
			return;
        } 
		else if (selection == 2) {
            cm.dispose();
			cm.openNpc(9010000,"飞升排行榜");
			return;
        } else if (selection == 3) {
		//	cm.displayLevelRanks();
			//cm.showlvl();//等级排名
			cm.openNpc(9010000,"仙级排行榜");
            cm.dispose();
			
            
        } else if (selection == 4) {
		//	cm.displayMesoRanks();  //金币排名
			//cm.showmeso();//金币排名
			cm.人气排行榜();
            cm.dispose();
			
            
	   } else if (selection == 5) {
		   cm.showfame();//人气排行
            cm.dispose();

			return;
	   } else if (selection == 6) {
			cm.showAllGuiGP();
           // cm.displayGuildRanks();//公会排名
            cm.dispose();
	   }
    } else if (status == 2) {
        cm.sendNext(cm.ShowJobRank(selection));
        cm.dispose();
    } else {
        cm.dispose();
    }
}

/*
//cm.sendSimple("#L9##r综合#k排名\r\n#L1##b战士#k排名\r\n#L2##b法师#k排名\r\n#L3##b弓箭手#k排名\r\n#L4##b飞侠#k排名\r\n#L5##b海盜#k排名\r\n#L7##d骑士团#k排名\r\n#L6##d战神#k排名\r\n#L8##d龙神#k排名\r\n"); //职业排名
*/