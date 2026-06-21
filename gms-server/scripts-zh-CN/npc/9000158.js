var 礼包物品 = "#v1302000#";
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var add = "#fEffect/CharacterEff/1022223/4/0#";
var ttt = "#fUI/UIWindow.img/Quest/icon9/0#";
var xxx = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var add = "#fEffect/CharacterEff/1112905/0/1#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
//var add = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 琴符 = "#fEffect/CharacterEff/1003252/0/0#";
var 音符 = "#fEffect/CharacterEff/1032063/0/0#";
var 花 = "#fUI/UIWindow.img/AriantMatch/characterIcon/0#";
var s = "#fUI/StatusBar/BtClaim/normal/0#";
var h = "#fUI/CashShop/CSEffect/effect/1#";
var 小雪花 = "#fEffect/CharacterEff/1003393/0/0#";
var 翅膀 = "#fUI/CashShop/Base/Tab/Enable/2#";//翅膀
var 爱心4 = "#fEffect/CharacterEff/1042176/1/1#"; // 实体深红爱心【小型】
var 爱心2 = "#fEffect/CharacterEff/1022223/3/0#"; // 虚体深色粉红爱心
var 爱心1 = "#fEffect/CharacterEff/1003271/0/0#"; // 实体粉红爱心
var 表情大笑 ="#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";//表情大笑/1哭/0微笑 

function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            cm.getPlayer().dropMessage(6,"感谢你的光临！");
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
            var fame = cm.getPlayer().getFame();
            var mesoCost1 = 100000000;
            var mesoCost10 = 10;
            var nxCost1 = 20000;
            var nxCost10 = 200000;

            if (fame > 2000) {
                mesoCost1 *= 12;
                mesoCost10 *= 12;
                nxCost1 *= 12;
                nxCost10 *= 12;
			} else if (fame > 1900) {
                mesoCost1 *= 11;
                mesoCost10 *= 11;
                nxCost1 *= 11;
                nxCost10 *= 11;
			} else if (fame > 1800) {
                mesoCost1 *= 10;
                mesoCost10 *= 10;
                nxCost1 *= 10;
                nxCost10 *= 10;
			} else if (fame > 1700) {
                mesoCost1 *= 9;
                mesoCost10 *= 9;
                nxCost1 *= 9;
                nxCost10 *= 9;
			} else if (fame > 1600) {
                mesoCost1 *= 8;
                mesoCost10 *= 8;
                nxCost1 *= 8;
                nxCost10 *= 8;
			} else if (fame > 1500) {
                mesoCost1 *= 7;
                mesoCost10 *= 7;
                nxCost1 *= 7;
                nxCost10 *= 7;
			} else if (fame > 1400) {
                mesoCost1 *= 6;
                mesoCost10 *= 6;
                nxCost1 *= 6;
                nxCost10 *= 6;
            } else if (fame > 1300) {
                mesoCost1 *= 5;
                mesoCost10 *= 5;
                nxCost1 *= 5;
                nxCost10 *= 5;
			} else if (fame > 1200) {
                mesoCost1 *= 4;
                mesoCost10 *= 4;
                nxCost1 *= 4;
                nxCost10 *= 4;
			} else if (fame > 1100) {
                mesoCost1 *= 3;
                mesoCost10 *= 3;
                nxCost1 *= 3;
                nxCost10 *= 3;
            } else if (fame > 1000) {
                mesoCost1 *= 2;
                mesoCost10 *= 2;
                nxCost1 *= 2;
                nxCost10 *= 2;
            }
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "           #r"+小雪花+"欢迎来到开心冒险岛人气系统"+小雪花+"#k#n\r\n"
            text += "#r#e----------------------------------------------#k#n\r\n";
            text += "     			  目前拥有人气值：#r[" + cm.getPlayer().getFame() + "]           #k#n\r\n"
            text += "当前金币拥有：#r[" + cm.getPlayer().getMeso() + "]#k    目前点券拥有：#r["+cm.getPlayer().getCSPoints(1)+"]#k#n\r\n"
            text += "#r#e----------------------------------------------#k#n\r\n";
            text += "     		#r#e人气越多怪物公园通关奖励越高           #k#n\r\n";
			text += "#L1##k" + aaa + "#d " + (mesoCost1 / 100000000) + "亿金币#r购买[ 1人气]#l #L3#" + zzz + "#d " + (nxCost1 / 10000) + "万点券#r购买[ 1人气]#l\r\n\r\n"
			text += "#L2##k" + aaa + "#d" + (mesoCost10 / 1) + "亿金币#r购买[10人气]#l #L4#" + zzz + "#d" + (nxCost10 / 10000) + "万点券#r购买[10人气]#l\r\n\r\n"
			text += "#r#e----------------------------------------------#k#n\r\n";
			text += "#b#e特殊说明：#k#n\r\n";
			text += "    #b人气大于1000则双倍收费；人气大于1100则三倍收费！#k#n\r\n";
			text += "    #b人气大于1200则四倍收费；人气大于1300则五倍收费！#k#n\r\n";
			text += "    #b人气大于1400则六倍收费；人气大于1500则七倍收费！#k#n\r\n";
			text += "    #b人气大于1600则八倍收费；人气大于1700则九倍收费！#k#n\r\n";
			text += "    #b人气大于1800则十倍收费；人气大于1900则11倍收费！#k#n\r\n";
			text += "    #b人气大于2000则12倍收费！公园奖励：2000人气封顶！#k#n\r\n";
		//	text += "    #b怪物公园奖励：2000人气封顶！#k#n\r\n";
			text += "#r#e----------------------------------------------#k#n\r\n";

            cm.sendSimple(text);
        } else if (status == 1) {
            var fame = cm.getPlayer().getFame();
            var mesoCost1 = 100000000;
            var mesoCost10 = 10;
            var nxCost1 = 20000;
            var nxCost10 = 200000;

            if (fame > 2000) {
                mesoCost1 *= 12;
                mesoCost10 *= 12;
                nxCost1 *= 12;
                nxCost10 *= 12;
			} else if (fame > 1900) {
                mesoCost1 *= 11;
                mesoCost10 *= 11;
                nxCost1 *= 11;
                nxCost10 *= 11;
			} else if (fame > 1800) {
                mesoCost1 *= 10;
                mesoCost10 *= 10;
                nxCost1 *= 10;
                nxCost10 *= 10;
			} else if (fame > 1700) {
                mesoCost1 *= 9;
                mesoCost10 *= 9;
                nxCost1 *= 9;
                nxCost10 *= 9;
			} else if (fame > 1600) {
                mesoCost1 *= 8;
                mesoCost10 *= 8;
                nxCost1 *= 8;
                nxCost10 *= 8;
			} else if (fame > 1500) {
                mesoCost1 *= 7;
                mesoCost10 *= 7;
                nxCost1 *= 7;
                nxCost10 *= 7;
			} else if (fame > 1400) {
                mesoCost1 *= 6;
                mesoCost10 *= 6;
                nxCost1 *= 6;
                nxCost10 *= 6;
            } else if (fame > 1300) {
                mesoCost1 *= 5;
                mesoCost10 *= 5;
                nxCost1 *= 5;
                nxCost10 *= 5;
			} else if (fame > 1200) {
                mesoCost1 *= 4;
                mesoCost10 *= 4;
                nxCost1 *= 4;
                nxCost10 *= 4;
			} else if (fame > 1100) {
                mesoCost1 *= 3;
                mesoCost10 *= 3;
                nxCost1 *= 3;
                nxCost10 *= 3;
            } else if (fame > 1000) {
                mesoCost1 *= 2;
                mesoCost10 *= 2;
                nxCost1 *= 2;
                nxCost10 *= 2;
            }

            if (selection == 1) {
                if (cm.getPlayer().getMeso() < mesoCost1) {
                    cm.sendOk("所需金币不足" + (mesoCost1 / 100000000) + "亿金币，无法兑换");
                    cm.dispose();
                } else {
                    cm.gainMeso(-mesoCost1);
                    cm.getPlayer().setFame(cm.getPlayer().getFame() + 1);
                    cm.getPlayer().dropMessage(6,"购买成功");
                    cm.喇叭(2,"人气：["+cm.getName()+"]一次性购买了1点人气值！全场嗨翻！");
                    cm.dispose();
                }
            } else if (selection == 2) {
                if (cm.haveItem(3994731,mesoCost10)==false){
                    cm.sendOk("所需#v3994731#不足"+mesoCost10+"个.");
                    cm.dispose();
                } else {
                    cm.gainItem(3994731,-mesoCost10);
                    cm.getPlayer().setFame(cm.getPlayer().getFame() + 10);
                    cm.getPlayer().dropMessage(6,"购买成功");
                    cm.喇叭(2,"人气：["+cm.getName()+"]一次性购买了10点人气值！全场嗨翻！");
                    cm.dispose();
                }
            } else if (selection == 3) {
                if (cm.getPlayer().getCSPoints(1) < nxCost1) {
                    cm.sendOk("所需点券不足"+nxCost1+"，无法兑换");
                    cm.dispose();
                } else {
                    cm.gainNX(-nxCost1);
                    cm.getPlayer().setFame(cm.getPlayer().getFame() + 1);
                    cm.getPlayer().dropMessage(6,"购买成功");
                    cm.喇叭(2,"人气：["+cm.getName()+"]一次性购买了1点人气值！全场嗨翻！");
                    cm.dispose();
                }
            } else if (selection == 4) {
                if (cm.getPlayer().getCSPoints(1) < nxCost10) {
                    cm.sendOk("所需点券不足"+nxCost10+"，无法兑换");
                    cm.dispose();
                } else {
                    cm.gainNX(-nxCost10);
                    cm.getPlayer().setFame(cm.getPlayer().getFame() + 10);
                    cm.getPlayer().dropMessage(6,"购买成功");
                    cm.喇叭(2,"人气：["+cm.getName()+"]一次性购买了10点人气值！最亮的仔！");
                    cm.dispose();
                }
            }
        }
    }
}