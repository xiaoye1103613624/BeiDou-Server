/*
 * 
 * @枫之梦
 * 神器进阶系统 - 魔武双修
 */
load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);
var 技能中心 = "#fEffect/CharacterEff1.img/QQ1408745/2/2#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 小兔 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 中猫条 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 小兔 = "#fEffect/CharacterEff/1112960/3/0#";
var status = 0;
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
            var text = "";//
            for (i = 0; i < 1; i++) {
                text += ""+dd+"\r\n\t\t\t"+ 技能中心+"\r\n"+群粉心+"";
            }
           text+= "\t\t\t\t\t#b您的累计积分为: #r" + cm.getPlayer().getlpjf() + "\r\n" 
		   if (cm.getPlayer().getlpjf()>=0) {
		   text+=  "#b温馨提示:\r\n#r领取技能后会提示技能将出现在0--9键盘位\r\n需要换线后技能才会显示出来\r\n技能领取后可随意移动，但切勿覆盖重叠！\r\n#b只有累计积分达到后技能才会出现#n\r\n\r\n";}
           if (cm.getPlayer().getlpjf()>=10) {
               text+=   "#d#L0##r领取#s4111006#二段跳(10累计积分)\r\n\r\n";}
           if (cm.getPlayer().getlpjf()>=50) {	   
               text+=   "#d#L1##r领取#s3221002#火眼晶晶(50累计积分)\r\n\r\n";}
	       if (cm.getPlayer().getlpjf()>=200) {
               text+=   "#d#L2##r领取#s2311003#主教神圣祈祷(200累计积分)\r\n\r\n";}			   
	       if (cm.getPlayer().getlpjf()>=500) {	   
               text+=   "#d#L3##r领取#s9001007#缩地大法(500累计积分)\r\n\r\n";}
           if (cm.getPlayer().getlpjf()>=1000) {	   
               text+=   "#d#L4##r领取#s1121002#稳如泰山(1000累计积分)\r\n\r\n";}
           if (cm.getPlayer().getlpjf()>=2000) {	   
               text+=   "#d#L5##r领取#s4111002#影分身(2000累计积分)\r\n\r\n";}
	       if (cm.getPlayer().getlpjf()>=4000) {  
               text+=  "#d#L6##r领取#s5121003#超级变身(4000累计积分)\r\n\r\n";}

            cm.sendSimple(text);
        } else if (status == 1) {

			if (selection == 0) {
                if (cm.getPlayer().getlpjf()>=10){ //
				
                    cm.teachSkill(4111006,30);
                    cm.getPlayer().changeKeybinding(2,1,4111006);
					cm.sendOk("二段跳#s4111006#领取成功，#b请换【频道】后查看【键码1】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】二段跳技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！");
                    cm.dispose();
                }

            } else if (selection == 1) {
                if (cm.getPlayer().getlpjf()>=50){ //
				
                    cm.teachSkill(3221002,30);
                    cm.getPlayer().changeKeybinding(3,1,3221002);
					cm.sendOk("火眼晶晶#s3221002#领取成功，#b请换【频道】后查看【键码2】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】火眼晶晶技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！");
                    cm.dispose();
                }
				
            } else if (selection == 2) {
                if (cm.getPlayer().getlpjf()>=200) {
					
                    cm.teachSkill(2311003,30);//技能和等级
                    cm.getPlayer().changeKeybinding(4,1,2311003);//技能键盘位子 
					cm.sendOk("主教花#s2311003#领取成功，#b请换【频道】后查看【键码3】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】主教花技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！");
                    cm.dispose();
                }

            } else if (selection == 3) {
                if (cm.getPlayer().getlpjf()>=500) {
					
                    cm.teachSkill(9001007,1);
                    cm.getPlayer().changeKeybinding(5,1,9001007);
					cm.sendOk("大佬的缩地大法#s9001007#领取成功，#b请换【频道】后查看【键码4】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的缩地大法技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                }               

            } else if (selection == 4) {
                if (cm.getPlayer().getlpjf()>=1000){ //要
				
                    cm.teachSkill(1121002,30);
                    cm.getPlayer().changeKeybinding(6,1,1121002);
					cm.sendOk("稳如泰山#s1121002#领取成功，#b请换【频道】后查看【键码5】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】稳如泰山技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！");
                    cm.dispose();
                }

            } else if (selection == 5) {
                if (cm.getPlayer().getlpjf()>=2000){ //要
				
                    cm.teachSkill(4111002,30);
                    cm.getPlayer().changeKeybinding(7,1,4111002);
					cm.sendOk("影分身#s4111002#领取成功，#b请换【频道】后查看【键码6】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】影分身技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！");
                    cm.dispose();
                }

			} else if (selection == 6) {
                if (cm.getPlayer().getlpjf()>=4000) {
					
                    cm.teachSkill(5121003,20);
                    cm.getPlayer().changeKeybinding(8,1,5121003);
					cm.sendOk("超级变身#s5121003#领取成功，#b请换【频道】后查看【键码7】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】超级变身技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 


            } 
        }
    }
}	


/*

            } else if (selection == 8) {
                if (cm.getPlayer().getlpjf()>=1) {
                    cm.teachSkill(1111008,30);
                    cm.getPlayer().changeKeybinding(9,1,1111008);
					cm.sendOk("虎咆哮#s1111008#领取成功，#b请换【频道】后查看【键码8】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】虎咆哮技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！");
                    cm.dispose();
                }


			} else if (selection == 12) {
                if (cm.getPlayer().getlpjf()>=1) {
                    cm.teachSkill(9001006,30);
                    cm.getPlayer().changeKeybinding(4,1,9001006);
					cm.sendOk("大佬的超级狂龙斩#s9001006#领取成功，#b请换【频道】后查看【键码3】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的超级狂龙斩技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                }

            } else if (selection == 14) {
                if (cm.getPlayer().getlpjf()>=1) {

                    cm.teachSkill(9001008,30);
                    cm.getPlayer().changeKeybinding(2,1,9001008);
					cm.sendOk("大佬的神圣之火#s9001008#领取成功，#b请换【频道】后查看【键码1】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的神圣之火技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 
				} else if (selection == 15) {
                if (cm.getPlayer().getlpjf()>=1) {

                    cm.teachSkill(5111005,30);
                    cm.getPlayer().changeKeybinding(2,1,5111005);
					cm.sendOk("大佬的赛亚人#s5111005#领取成功，#b请换【频道】后查看【键码1】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的赛亚人技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 

            } else if (selection == 16) {
                if (cm.getPlayer().getlpjf()>=1) {

                    cm.teachSkill(5121003,30);
                    cm.getPlayer().changeKeybinding(3,1,5121003);
					cm.sendOk("大佬的超级赛亚人#s5121003#领取成功，#b请换【频道】后查看【键码2】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的超级赛亚人技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 

            } else if (selection == 2 ) {
                if (cm.getPlayer().getlpjf()>= 0 ) {

                    cm.teachSkill(14101006,20);
                    cm.getPlayer().changeKeybinding(2,1,14101006);
					cm.sendOk("标飞全屏技#s14101006#领取成功，#b请换【频道】后查看【键码2】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】标飞全屏技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 

            } else if (selection == 1 ) {
                if (cm.getPlayer().getlpjf()>= 0 ) {

                    cm.teachSkill(1005,1);
                    cm.getPlayer().changeKeybinding(2,0,0001005);
					cm.sendOk("英雄之回声#s0001005#领取成功，#b请换【频道】后查看【键码1】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】英雄之回声技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 


            } else if (selection == 17) {
                if (cm.getPlayer().getlpjf()>=1) {

                    cm.teachSkill(13111000,20);
                    cm.getPlayer().changeKeybinding(4,1,13111000);
					cm.sendOk("大佬的全屏箭雨#s13111000#领取成功，#b请换【频道】后查看【键码3】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的全屏箭雨技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 

            } else if (selection == 18) {
                if (cm.getPlayer().getlpjf()>=1) {

                    cm.teachSkill(13111001,20);
                    cm.getPlayer().changeKeybinding(5,1,13111001);
					cm.sendOk("大佬的满段扫射#s13111001#领取成功，#b请换【频道】后查看【键码4】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的满段扫射技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 

            } else if (selection == 19) {
                if (cm.getPlayer().getlpjf()>=1) {

                    cm.teachSkill(13111002,20);
                    cm.getPlayer().changeKeybinding(6,1,13111002);
					cm.sendOk("大佬的满段暴风#s13111002#领取成功，#b请换【频道】后查看【键码5】");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】大佬的满段暴风技能领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } 

            } else if (selection == 20) {
                if (cm.getPlayer().getlpjf()>=1) {
                    cm.gainItem(4320002,1);
                    cm.sendOk("领了转职卷轴找【拍卖】【-重新转职】！");
					cm.喇叭(2, "玩家【" + cm.getPlayer().getName() + "】转职卷轴领取成功！");
				    cm.dispose();
                } else {
                    cm.sendOk("条件不足！无法领取！"); 
                    cm.dispose();
                } */