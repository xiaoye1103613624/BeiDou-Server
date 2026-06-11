
var status = 0;
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 礼包物品 = "#v1302000#";
var ttt = "#fUI/UIWindow.img/Quest/icon9/0#";
var xxx = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var  a1 = "#fUI/ChatBalloon.img/28/w#";//右上
var  a2 = "#fUI/ChatBalloon.img/28/e#";//上中
var  a3 = "#fUI/ChatBalloon.img/28/n#";//右上
var  a4 = "#fUI/ChatBalloon.img/28/s#";//右上
var  a5 = "#fUI/ChatBalloon.img/28/nw#";//右上
var  a6 = "#fUI/ChatBalloon.img/28/ne#";//右上
var  a7 = "#fUI/ChatBalloon.img/155/nw#";//右上
var  a8 = "#fUI/ChatBalloon.img/19/nw#";//右上
var  a9 = "#fUI/ChatBalloon.img/19/ne#";//右上
var  a10 = "#fUI/ChatBalloon.img/28/sw#";//右上
var  a11 = "#fUI/ChatBalloon.img/28/se#";//右上
var  a12 = "#fUI/ChatBalloon.img/28/s#";//右上
var b1 = "#fUI/ChatBalloon.img/pet/339/nw#";
var b2 = "#fUI/ChatBalloon.img/pet/339/ne#";
var b3 = "#fUI/ChatBalloon.img/pet/99/n#";
var b4 = "#fUI/ChatBalloon.img/pet/99/s#";
var b5 = "#fUI/ChatBalloon.img/pet/339/sw#";
var b6 = "#fUI/ChatBalloon.img/pet/339/se#";
var chr = null;
var say = "";
var em = null;
var 粉星 = "#fEffect/CharacterEff/1112926/0/1#";
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
            if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
                cm.sendOk("请保证背包所有栏位至少保留3个空格！");
                cm.dispose();
                return;
            }
			if (cm.getPlayer().getlpjf() < 9) { 
			    cm.sendOk("需要累计赞助大于9！");
                cm.dispose();
                return;
            }
            var textz = _getTitle("每日选购");

			textz += "    #r#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～～╮#n\r\n";//您的赞助余额为: #r" + cm.getPlayer().getmoney() + "
			textz += "			     #d您的赞助余额为: [ #r" + cm.getPlayer().getmoney() + " #k]\r\n"
			textz += "   #r#e╰ ～～～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
            if (cm.getPlayer().getBossLog("每日金币") == 0) { 
			textz += "   #d#L0##d每日金币(#r8R，限购1次#k):#l\r\n\r\n  \t\t#v3994731##r×10  #v5210002##r #b24小时 #k\r\n\r\n";
            } else {
			textz += "       #d#d每日金币:（#r已购买#d）\r\n\r\n\t\t#v3994731##r×10  #v5210002##r #b24小时 #k\r\n\r\n";
			}
			if (cm.getPlayer().getBossLog("每日点券") == 0) { 
			textz += "   #d#L1##d每日点券(#r8R，限购1次#k)#l:\r\n\r\n  \t\t#v2022309##r×30  #v5360015# #b12小时  #k\r\n\r\n";
            } else {
			textz += "       #d#d每日点券:（#r已购买#d）\r\n\r\n  \t\t#v2022309##r×30  #v5360015# #b12小时 #k\r\n\r\n";
			}
			if (cm.getPlayer().getBossLog("每日吊坠") == 0) { 
			textz += "   #d#L2##d每日积分(#r8R，限购1次#k)#l:\r\n\r\n  \t\t#r#v4310144#×5W #v1122017# #b24小时 #k\r\n\r\n";//#v2290285##r×5 #v2340000#×5 #v5220002#(限时5小时) 
            } else {
			textz += "       #d#d每日积分:（#r已购买#d）\r\n\r\n  \t\t#r#v4310144#×5W #v1122017# #b24小时 #k\r\n\r\n";//#v5220002#(限时5小时) 
			}

            cm.sendSimpleS(textz,2);
		} else if (status == 1) {

			if (selection == 0) {
				if (cm.getPlayer().getBossLog("每日金币") >= 1) {
					cm.sendOk("感谢支持！您已经购买过了，请明天再来吧！");
					cm.dispose();
					return;
				}
				if (cm.getPlayer().getmoney()>=8){ //
				    cm.getPlayer().setmoney(cm.getPlayer().getmoney()-8);
                    cm.gainItem(3994731, 10);
					cm.gainItem(5210002, 1, 1);   //这个是1天 -双倍经验
                    cm.getPlayer().setBossLog("每日金币");
					cm.sendOk("感谢支持！祝您游戏愉快!");
					cm.喇叭(3, "【每日赞助】[" + cm.getName() + "]购买了每日 金币 + 双倍经验卡 礼包！感谢大佬支持！");
				    cm.dispose();
                } else {
                    cm.sendOk("亲，您的赞助点不足8！\r\n可在#r赞助中心 - 自助充值#k中充值后，再来购买吧！");
                    cm.dispose();
                }

			} else if (selection == 1) {
				if (cm.getPlayer().getBossLog("每日点券") >= 1) {
					cm.sendOk("感谢支持！您已经购买过了，请明天再来吧！");
					cm.dispose();
					return;
				}
				if (cm.getPlayer().getmoney()>=8){ //
				    cm.getPlayer().setmoney(cm.getPlayer().getmoney()-8);
                    cm.gainItem(2022309, 30);
				//	cm.gainItem(5360015, 1, 1);   //这个是1天  -双倍暴率
					cm.gainItem(5360015, 1, 12,true);//这个是12小时  -双倍暴率
                    cm.getPlayer().setBossLog("每日点券");
					cm.sendOk("感谢支持！祝您游戏愉快!");
					cm.喇叭(3, "【每日赞助】[" + cm.getName() + "]购买了每日 点券 + 双倍暴率卡 礼包！感谢大佬支持！");
				    cm.dispose();
                } else {
                    cm.sendOk("亲，您的赞助点不足8！\r\n可在#r赞助中心 - 自助充值#k中充值后，再来购买吧！");
                    cm.dispose();
                }

			} else if (selection == 2) {// // im.gainItem(5220002, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5);
				if (cm.getPlayer().getBossLog("每日吊坠") >= 1) {
					cm.sendOk("感谢支持！您已经购买过了，请明天再来吧！");
					cm.dispose();
					return;
				}
				if (cm.getPlayer().getmoney()>=8){ //
				    cm.getPlayer().setmoney(cm.getPlayer().getmoney()-8);
					//cm.gainItem(5220002, 1, 5,true);
					cm.gainItem(4310144, 30000);
					cm.gainItem(4310144, 20000);
					cm.gainItem(1122017, 1, 1);   //这个是1天  -双倍暴率
                    cm.getPlayer().setBossLog("每日吊坠");
					cm.sendOk("感谢支持！祝您游戏愉快!");
					cm.喇叭(3, "【每日赞助】[" + cm.getName() + "]购买了 一万积分 + 精灵项链 礼包！感谢大佬支持！");
				    cm.dispose();
                } else {
                    cm.sendOk("亲，您的赞助点不足8！\r\n可在#r赞助中心 - 自助充值#k中充值后，再来购买吧！");
                    cm.dispose();
                }
				
				
				
				

			}
		}
	}
}
var ul_cloud = "#fItem/Etc/0403/04031309/info/iconRaw#"; //
function _getTitle(t) {
    return " " + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "#r#e『" + t + "』#k#n" + ul_cloud + ul_cloud + ul_cloud + ul_cloud + "\r\n\r\n";
}