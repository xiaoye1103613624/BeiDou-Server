/* ==================
 脚本类型: NPC	    
 脚本作者：一线海-维多   
 联系方式：297870163
 =====================
 */
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
var 礼包物品 = "#v1302000#";
var 美化1 = "#fUI/ChatBalloon.img/pet/127/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/127/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/127/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/127/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/127/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/127/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 礼包物品 = "#v1302000#";
var add = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 粉爱心 = "#fItem/Etc/0427/04270005/Icon8/1#";  //
var 菊花 = "#fUI/PredictHarmony/card/19#";//卡片效果菊花
var 笑 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/0#";//笑脸
var 金枫叶 ="#fMap/MapHelper/weather/maple/2#";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 巫女 ="#fMap/MapHelper/weather/witch/0#";//巫女
var 气球 ="#fMap/MapHelper/weather/balloon/4#";//气球
var 射箭 ="#fMap/MapHelper/weather/LoveEffect2/4/0#";//射箭
var 玫瑰 ="#fMap/MapHelper/weather/rose/0#";//玫瑰花
var 烟花 ="#fMap/MapHelper/weather/squib/squib1/3#";//烟花
var 爱心1 ="#fEffect/CharacterEff.img/1112946/0/0#";
var 爱心2 ="#fEffect/CharacterEff.img/1112905/0/1#";
var 爱心3 ="#fEffect/CharacterEff.img/1112946/1/0#";
var 爱心4 ="#fEffect/CharacterEff.img/1112946/1/1#";
var 爱心5 ="#fEffect/CharacterEff.img/1112946/2/0#";
var 爱心6 ="#fEffect/CharacterEff.img/1112946/2/1#";
var 爱心7 ="#fEffect/CharacterEff.img/1112946/3/0#";
var 爱心8 ="#fEffect/CharacterEff.img/1112946/3/1#";
var 爱心9 ="#fEffect/CharacterEff.img/1112906/0/1#";
var 爱心10 ="#fEffect/CharacterEff.img/1112903/1/0#";
var a1 ="#fEffect/CharacterEff.img/1112900/0/0#";
var a2 ="#fEffect/CharacterEff.img/1112900/2/0#";
var a3 ="#fEffect/CharacterEff.img/1112900/3/0#";
var a4 ="#fEffect/CharacterEff.img/1082229/0/0#";
var a5 ="#fEffect/CharacterEff.img/1102355/2/0#";
var a6 ="#fEffect/CharacterEff.img/1112902/0/1#";
var z1 ="#fEffect/CharacterEff.img/1112955/5/0#";
var z2 ="#fEffect/CharacterEff.img/1112955/2/0#";
var z3 ="#fEffect/CharacterEff.img/1112955/1/0#";
var x1 ="#fEffect/CharacterEff.img/1112949/1/0#";
var b0 ="#fEffect/CharacterEff.img/1112949/0/0#";
var b1 ="#fEffect/CharacterEff.img/1112949/1/0#";
var b2 ="#fEffect/CharacterEff.img/1112949/2/0#";
var b3 ="#fEffect/CharacterEff.img/1112949/3/0#";
var b4 ="#fEffect/CharacterEff.img/1112949/4/0#";
var m1 = "#fUI/ChatBalloon.img/118/nw#";//选择道具
var m3 = "#fUI/ChatBalloon.img/118/ne#";//选择道具
var m2 = "#fUI/ChatBalloon.img/118/n#";//选择道具
var m4 = "#fUI/ChatBalloon.img/118/sw#";//选择道具
var m5 = "#fUI/ChatBalloon.img/118/se#";//选择道具
var m6 = "#fUI/ChatBalloon.img/118/s#";//选择道具
var 大粉红爱心 = "#fItem/Etc/0427/04270001/Icon8/4#";  //
var 小粉红爱心 = "#fItem/Etc/0427/04270001/Icon8/5#";  //
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //
var 大黄星 = "#fItem/Etc/0427/04270001/Icon9/1#";  //
var 小水滴 = "#fItem/Etc/0427/04270001/Icon10/5#";  //
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#";  //
var tz = "#fEffect/CharacterEff/1082565/4/0#";  //粉兔子
var tz1 = "#fEffect/CharacterEff/1082565/0/0#";  //橙兔子
var tz2 = "#fEffect/CharacterEff/1082565/2/0#";  //蓝兔子
var 邪恶小兔 = "#fEffect/CharacterEff/1112960/3/0#";  //邪恶小兔 【小】
var 邪恶小兔2 = "#fEffect/CharacterEff/1112960/3/1#";  //邪恶小兔 【大】
var 花草 ="#fEffect/SetEff/208/effect/walk2/4#";
var 花草1 ="#fEffect/SetEff/208/effect/walk2/3#";
var 小花 ="#fMap/MapHelper/weather/birthday/2#";
var 桃花 ="#fMap/MapHelper/weather/rose/4#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 星星 ="#fMap/MapHelper/weather/witch/3#";
function start() {
    status = -1;

    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var tex2 = "";
            var text = "";
			text += ""+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"『矿石合成』"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
	        text += " #L1##d"+爱心1+"#v4021009# 星石合成 "+爱心1+"#l      #L2##d"+爱心3+"#v4011007# 月石合成 "+爱心3+"#l \r\n\r\n";//   
	        text += " #L3##d"+爱心1+"#v4005000# 力量水晶 "+爱心1+"#l      #L4##d"+爱心3+"#v4005001# 智慧水晶 "+爱心3+"#l\r\n\r\n";//  
	        text += " #L5##d"+爱心1+"#v4005002# 敏捷水晶 "+爱心1+"#l      #L6##d"+爱心3+"#v4005003# 幸运水晶 "+爱心3+"#l\r\n\r\n";//  
	        text += " #L7##d"+爱心1+"#v4005004# 黑暗水晶 "+爱心1+"#l     \r\n\r\n";//  
			// text += " \r\n\r\n\r\n"
			text += "#r" +美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+"#k#k\r\n"; 
			 cm.sendOk(text); 
        } else if (selection == 1) {
			cm.dispose();
            cm.openNpc(9310074,"星石合成"); 
        } else if (selection == 2) {
			cm.dispose();
            cm.openNpc(9310074,"月石合成" ); 	
		} else if (selection == 3) {
			cm.dispose();
			cm.openNpc(9310074, "合成力量水晶"); 
            // cm.openNpc(2020000,0); 
		} else if (selection == 4) {
			cm.dispose();
            cm.openNpc(9310074, "合成智慧水晶");
		} else if (selection == 5) {
			cm.dispose();
            cm.openNpc(9310074, "合成敏捷水晶");
		} else if (selection == 6) {
			cm.dispose();
		    cm.openNpc(9310074, "合成幸运水晶");
		} else if (selection == 7) {
			cm.dispose();
		    cm.openNpc(9310074, "合成黑暗水晶");
		} else if (selection == 8) {
			cm.dispose();
		    cm.openNpc(9310074, "合成黑暗水晶");
		} else if (selection == 9) {
			cm.dispose();
		    cm.openNpc(9310074, "混沌兑换");
			// cm.openNpc(9050001,0);
		} else if (selection == 10) {
			cm.dispose();
		    cm.openNpc(1061014, "蝙蝠怪箱子合成");
		} else if (selection == 11) {
			cm.dispose();
		    cm.openNpc(9310074,"星石合成"); //9900004, 606
		} else if (selection == 12) {
			cm.dispose();
		    cm.openNpc(9900004, 612); 
		} else if (selection == 13) {
			cm.dispose();
		    cm.openNpc(9900004, 613); 			
        } else if (selection == 14) {
			cm.dispose();
		    cm.openNpc(9900000,0); 			
        } else if (selection == 15) {
			cm.dispose();
		    cm.openNpc(9900004, "枫球兑换"); 		
        } else if (selection == 16) {
			cm.dispose();
		    cm.openNpc(9900004, "生肖兑换"); 	
        } else if (selection == 17) {
			cm.dispose();
		    cm.openNpc(9330040); 			
        } else if (selection == 18) {
			cm.dispose();
		    cm.openNpc(9900000); 		
        } else if (selection == 20) {
			cm.dispose();
		    cm.openNpc(9310075,"滑板收集"); 			
        }
    }
}