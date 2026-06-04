var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
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
var 金枫叶 ="#fMap/MapHelper/weather/maple/2#";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 星星 ="#fMap/MapHelper/weather/witch/3#";
//var tz = "#fEffect/CharacterEff/1082565/4/0#";  //兔子粉
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
        var selStr = "\r\t                    #r"+ 邪恶小兔 +"五转技能"+ 邪恶小兔 +"\r\n#l";
		selStr += "                    #L111#技能介绍#l     \r\n\r\n";
		selStr += "         学习5转技能需要#v4460000#*20个，元宝*100    \r\n\r\n";
		selStr += "                      （转生2次）     \r\n\r\n";
        selStr += "#k     #L1#英雄技能#l    #L2#圣骑技能#l    #L3#黑骑技能#l\r\n\r\n";
		selStr += "#b     #L4#火毒技能#l    #L5#冰雷技能#l    #L6#主教技能#l\r\n\r\n";
		selStr += "#k     #L7#弓箭技能#l    #L8#弩箭技能#l\r\n\r\n";
		selStr += "#b     #L9#隐士技能#l    #L10#侠盗技能#l\r\n\r\n";
		selStr += "#k     #L11#队长技能#l    #L12#船长技能#l \r\n\r\n";
		selStr += "#b     #L13#战神技能#l\r\n  ";
		//selStr += "                    #L14#五转技能#l\r\n  ";
		//selStr += "      #r#L22##v1402196#FFN武器传承#l   #r#L8##v1052669#毕业衣服传承#l\r\n    ";#L6#主教技能#l #L12#船长技能#l #L8#弩箭技能#l #L8#弩箭技能#l  
		cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
		case 999:
            cm.dispose();
			cm.openNpc(9900004,1472594);//兑换进阶技能书
            break;	
			
		case 1:
			if(cm.getJob()!=112){	
			cm.sendOk("对不起，您貌似不是英雄职业哦");
			cm.dispose();
			return;
			}
			cm.dispose();
			cm.openNpc(9900004,1472581);//英雄
            break;
        case 2:
		    if(cm.getJob()!=122){	
			cm.sendOk("对不起，您貌似不是圣骑士职业哦");
			cm.dispose();
			return;
			}
		    cm.dispose();
			cm.openNpc(9900004,1472582);//圣骑
            break;
        case 3:
		    if(cm.getJob()!=132){	
			cm.sendOk("对不起，您貌似不是黑骑士职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472583);//黑骑
            break;
        case 4:
		    if(cm.getJob()!=212){	
			cm.sendOk("对不起，您貌似不是火毒职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472584);//火毒
            break;
		case 5:
		    if(cm.getJob()!=222){	
			cm.sendOk("对不起，您貌似不是冰雷职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472585);//冰雷
            break;
		case 6:
		    if(cm.getJob()!=232){	
			cm.sendOk("对不起，您貌似不是主教职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472586);//主教
            break;
		case 7:
		    if(cm.getJob()!=312){	
			cm.sendOk("对不起，您貌似不是神射手职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472587);//弓箭
            break;
		case 8:
		    if(cm.getJob()!=322){	
			cm.sendOk("对不起，您貌似不是箭神职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472588);//弩箭
            break;
		case 9:
		    if(cm.getJob()!=412){	
			cm.sendOk("对不起，您貌似不是隐士职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472589);//标飞
            break;
		case 10:
		    if(cm.getJob()!=422){	
			cm.sendOk("对不起，您貌似不是侠盗职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472590);//侠盗
            break;
		case 11:
		    if(cm.getJob()!=512){	
			cm.sendOk("对不起，您貌似不是队长职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472591);//队长
            break;
		case 12:
		    if(cm.getJob()!=522){	
			cm.sendOk("对不起，您貌似不是船长职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,1472592);//海盗
            break;
		case 13:
		    if(cm.getJob()!=2112){	
			cm.sendOk("对不起，您貌似不是战神职业哦");
			cm.dispose();
			return;
			}
            cm.dispose();
			cm.openNpc(9900004,21112);//战神
            break;
			case 111:   
			 var text = "";
				//text += "\t             #v2022075##r终极技能介绍#v2022075#\r\n";
				text += "\t                  "+ 邪恶小兔 +"#b技能详解"+ 邪恶小兔 +"\r\n\r\n";
				
				
				text += "  #b英雄技能   #s11111006#  <剑影分身>数量8，段数8，伤害500% #l\r\n";
				
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k圣骑技能   #s15111007#  <威力神锤>数量8，段数8，伤害500% #l\r\n";		//被动武器单手双手 #s11121065#   #s11121058#
				text += "#k———————————————————————————\r\n";
				
				text += "  #b黑骑技能   #s14001002#  <枪舞旋风>数量8，段数8，伤害500%#l \r\n";  //被动武器枪 #s11121072#   #s11121056#
				text += "#k———————————————————————————\r\n";
				//法师
				text += "  #k火毒技能   #s12101006#  <魔力漩涡>数量8，段数8，伤害500%#l\r\n";  //更新  #s12111004#
				text += "#k———————————————————————————\r\n";

				text += "  #b冰雷技能   #s12001003#  <黑暗灵气>数量8，段数8，伤害500%#l\r\n";  //  #s12101002#
				text += "#k———————————————————————————\r\n";
				
				text += "  #k牧师技能   #s12111006#  <星座法阵>数量8，段数8，伤害500% #l\r\n";   //#s12101004#   #s12111003#
				text += "#k———————————————————————————\r\n";
				
				text += "  #b神射技能   #s13111002#  <释魂射击>数量1，段数8，伤害500%#l\r\n";  		//#s11121061#   #s13101005#			
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k箭神技能   #s13111001#  <箭 扫 射>数量1，段数8，伤害500%#l\r\n";   //#s11121060#   #s13111000#
				text += "#k———————————————————————————\r\n";
			
				text += "  #b隐士技能   #s14111002#  <速射>数量5，段数8，伤害500%  #l\r\n"; 
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k侠客技能   #s11001003#  <利刃风暴>数量8，段数8，伤害500%#l\r\n"; 
				text += "#k———————————————————————————\r\n";				
				//
				text += "  #b船长技能   #s15001001#  <子弹盛宴>数量8，段数8，伤害500%#l\r\n";  			//#s15001001#   #s13001004#		
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k队长技能   #s15111003#  <元气弹>数量10，段数8，伤害500% #l\r\n";  //#s15111007#   #s15101003#
				text += "#k———————————————————————————\r\n";					
				//				
				text += "  #b战神技能   #s11001002#  <虎鹤双击>数量10，段数8，伤害300% #l\r\n";  //#s11121054#   #s11121059#

				text += "#k———————————————————————————\r\n";								
				//				
				cm.sendSimple(text)
				cm.dispose();
            break;
								case 222:
            cm.dispose();
            cm.openNpc(9310074,55);
            break;
							case 22:
            cm.dispose();
			cm.openNpc(2110000,0);
            //cm.openNpc(9201123,0);
            break;
							case 23:
            cm.dispose();
            cm.openNpc(9270025,0);
            break;
							case 24:
            cm.dispose();
            cm.openNpc(2080000,0);
            break;
							case 26:
            cm.dispose();
            cm.openNpc(9120054,10);
            break;
							case 27:
            cm.dispose();
            cm.openNpc(1093000,1);
            break;
							case 28:
            cm.dispose();
            cm.openNpc(9310074,0);
            break;
							case 29:
            cm.dispose();
            cm.openNpc(9120006,0);
            break;
							case 30:
            cm.dispose();
            cm.openNpc(9050010,0);
            break;
							case 31:
            cm.dispose();
            cm.openNpc(9120054,0);
            break;
							case 32:
            cm.dispose();
            cm.openNpc(9120054,1000);
            break;
		case 14:

            cm.dispose();
			cm.openNpc(9900004,45676);//队长
            break;	
			
}
	}
    }
}
