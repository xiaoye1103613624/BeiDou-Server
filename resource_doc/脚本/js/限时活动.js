var 美化1 = "#fUI/ChatBalloon.img/120/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/120/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/120/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/120/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/120/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/120/s#";//选择道具
//var 美化7 = "#fUI/ChatBalloon.img/118/head#";//选择道具
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var Dabaims = "#fUI/GuildMark.img/Mark/Animal/00002015/16#";
var Dabaims1 = "#fUI/GuildMark.img/Mark/Animal/00002015/15#";
var Dabaims2 = "#fUI/GuildMark.img/Mark/Animal/00002015/14#";
var ca = java.util.Calendar.getInstance();
var 年 = ca.get(java.util.Calendar.YEAR);
var 月 = ca.get(java.util.Calendar.MONTH);
var 日 = ca.get(java.util.Calendar.DAY_OF_MONTH);
var 时 = ca.get(java.util.Calendar.HOUR_OF_DAY);
var 分钟 = ca.get(java.util.Calendar.MINUTE);
var 秒钟 = ca.get(java.util.Calendar.SECOND);
var 星期 = ca.get(java.util.Calendar.DAY_OF_WEEK)-1;
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
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var text = "#r"+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"#r即时活动#n"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
			
			text += "#r#L0# <今晚吃鸡全民pk> 活动正在进行 已挑战次数:<"+cm.getBossLog("吃鸡战场")+"次/1>#l\r\n";
			text += "#d#L0# <今晚吃鸡全民pk> 活动暂未开启 开启方式:活动管理#l\r\n\r\n";
			if (星期==1 || 星期==3 || 星期==5) {
			text += "#r#L1# < 天启试炼之路 > 活动正在进行 已挑战次数:<"+cm.getBossLog("天启试炼之路")+"次/1>#k#l\r\n\r\n";
            } else{
			text += "#d#L10# < 天启试炼之路 > 活动暂未开启 开启方式:周1,3,5#k#l\r\n\r\n";
            }
            if (cm.getMap(993185100).getMonsterById(9500319)!=null) {
            text += "\r\n #d#e#L77677887#全民拼图 召唤雪人 波斯传送门#k.#r火热 进行中 #k#l#n\r\n";
			}else{
            text += "\r\n";
			}
			if (星期==2 || 星期==4 || 星期==6) {
			text += "#r#L4# < 保护希纳斯 > 活动正在进行 已挑战次数:<"+cm.getBossLog("保护冒险岛村民")+"次/3>#k#l\r\n\r\n";
            } else{
			text += "#d#L11# < 保护希纳斯 > 活动正在进行 开启方式:周2,4,6#k#l\r\n\r\n";
            }
			if (星期==2 || 星期==4 || 星期==6) {
			text += "#r#L2# < 嘉年华试炼本 > 活动正在进行 已挑战次数:<"+cm.getBossLog("嘉年华试炼本")+"次/5>#k#l\r\n\r\n";
            } else{
			text += "#d#L12# < 嘉年华试炼本 > 活动正在进行 开启方式:周2,4,6#k#l\r\n\r\n";
            }
			 if (星期==0) {
			text += "#r#L3# < 周多人团队本 > 活动正在进行 已挑战次数:<"+cm.getBossLog("周多人团队本")+"次/1>#k#l\r\n\r\n";
            } else{
			text += "#d#L13# < 周多人团队本 > 活动正在进行 开启方式:周7#k#l\r\n\r\n";
            }
			if (星期==1 || 星期==3 || 星期==5) {
			text += "#r#L4# < 饥寒边塞 > 活动正在进行 已挑战次数:<"+cm.getBossLog("饥寒边塞")+"次/1>#k#l\r\n\r\n";
            } else{
			text += "#d#L14# < 饥寒边塞 > 活动暂未开启 开启方式:周1,3,5#k#l\r\n\r\n";
            }
			text += "\r\n#r"+美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+"#l#k\r\n\r\n";
			cm.sendSimpleS(text,2);
		} else if (status == 1) {
			if (selection == 0) {
            cm.dispose();
			cm.openNpc(9900004,"吃鸡123456789");
			} else if (selection == 10) {
            //cm.sendOkS("当前点击未开启活动\r\n< 饥寒边塞 > 开启时间段为:周1,3,5\r\n < 保护希纳斯 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6",2);
            cm.dispose();
			} else if (selection == 11) {
         //   cm.sendOkS("当前点击未开启活动\r\n< 饥寒边塞 > 开启时间段为:周1,3,5\r\n < 保护希纳斯 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6",2);
            cm.dispose();
			} else if (selection == 12) {
       //     cm.sendOkS("当前点击未开启活动\r\n< 饥寒边塞 > 开启时间段为:周1,3,5\r\n < 保护希纳斯 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6",2);
            cm.dispose();
			} else if (selection == 13) {
       //     cm.sendOkS("当前点击未开启活动\r\n< 饥寒边塞 > 开启时间段为:周1,3,5\r\n < 保护希纳斯 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6\r\n< 嘉年华试炼本 > 开启时间段为:周2,4,6",2);
            cm.dispose();
			} else if (selection == 1) {
            cm.dispose();
			//cm.openNpc(9030100);
			} else if (selection == 2) {
			cm.warp(980000000);
            cm.dispose();
  } else if (selection == 77677887){//7898989 74875 7654321 77677886
            cm.warp(993185100,0);
            cm.喇叭(2,"钓鱼拼图：["+cm.getName()+"]从 1X 点击拍卖 => 即时活动 => 钓鱼拼图 已进入地图");
            cm.dispose();
			} else if (selection == 3) {
            cm.dispose();
			//cm.openNpc(1401004,"随身金仓");
			} else if (selection == 4) {
            if (星期==0 || 星期==1 || 星期==3|| 星期==5) {// 246
			   cm.sendOk("只可以在 每周2 ，4 ，6 进行挑战");
			   cm.dispose();
        } else{
		       cm.dispose();
		       cm.openNpc(9310059,"保护冒险岛村民");
}
		}
		}
	}
}

