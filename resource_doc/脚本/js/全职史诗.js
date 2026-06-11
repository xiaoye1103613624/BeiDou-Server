var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var 幸运草 = "#fUI/GuildMark/Mark/Plant/00003006/15#";
var 彩虹1 = "#fUI/ChatBalloon/122/n#";
var 彩虹上1 = "#fUI/ChatBalloon/122/ne#";
var 彩虹上2 = "#fUI/ChatBalloon/122/nw#";
var 强化中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/1#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 彩1 = "#fUI/ChatBalloon/122/e#";
var 彩2 = "#fUI/ChatBalloon/122/w#";
var 大箭头 = "#fUI/Basic/icon/arrow#";
var 彩虹下 = "#fUI/ChatBalloon/122/s#";
var 彩虹下1 = "#fUI/ChatBalloon/122/se#";
var 彩虹下2 = "#fUI/ChatBalloon/122/sw#";
var 彩虹中 = "#fUI/ChatBalloon/122/head#";
var 梅花 = "#fUI/GuildMark/Mark/Animal/00002008/14#";
var 梅花 = "#fUI/GuildMark/Mark/Animal/00002020/14#";
var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 退出 = "#fUI/CN_Chat/ChattingRoom/BtExit/0/mouseOver/0#";
var 奖励 = "#fUI/UIWindow/Quest/reward#";
var 购买 = "#fUI/UIWindow/PersonalShop/BtBuy/mouseOver/0#";

function start() {
    status = -1;

    action(1, 0, 0);
}

var 饰品最小值 = 50;
var 饰品最大值 = 200;
var 武器最小值 = 600;
var 武器最大值 = 1000;

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
                for (i = 0; i < 10; i++) {
                    text += "";
                }
                text += ""+dd+"\r\n\t\t\t"+强化中心+"\r\n"+群粉心+""

                text += "#e#r#L1##v4170002#【副本蛋】#b进阶#r【全职史诗饰品】" + 星星 + "#l\r\n\r\n"
                text += "#n#k属性说明：四属性和双攻随机 + (#r" + 饰品最小值 + " - " + 饰品最大值 + " #k点)\r\n#n#k饰品说明：(随机合成)\r\n#i1112978# #i1022276# #i1012339# #i1032266# #i1122363#\r\n"//#i1112738# #i1122197# #i1132152#
				
                text += "#e#r#L2##v1472275#【神器武器】#b进阶#r【全职史诗武器】" + 星星 + "#l\r\n\r\n"
                text += "#n#k属性说明：四属性和双攻随机 + (#r" + 武器最小值 + " - " + 武器最大值 + " #k点)\r\n#n#k武器说明：(随机合成)\r\n#i1402332# #i1432306# #i1382267# #i1452307# #i1462245# #i1332281# #i1472267# #i1482204# #i1492237#\r\n"//
				
                text += "#e#r#L3##v1003172#【神器套装】#b进阶#r【全职史诗套装】" + 星星 + "#l\r\n\r\n"
                text += "#n#k属性说明：四属性和双攻随机 + (#r" + 武器最小值 + " - " + 武器最大值 + " #k点)\r\n#n#k套装说明：(随机合成)\r\n#i1003624# #i1052532# #i1132187# #i1102948# #i1072695# #i1082490#\r\n"//
				
				/*text += "  #L4#" + 皇冠白 + " 进阶#r#v1132161#【风暴套装】#k 随机" + 饰品最小值 + "-" + 饰品最大值 + "属性 " + 星星 + "#l\r\n\r\n     随机产出系列:\r\n #i1902034##i1912027##i1902035##i1912028##i1902019##i1912012##i1902061##i1912054##i1902031##i1912024##i1902021##i1912014##i1902045##i1912038##i1902028##i1912021#\r\n"
				text += "  #L5#" + 皇冠白 + " 进阶#r#v1332289#【点装武器】#k 随机90-800属性 " + 星星 + "#l\r\n\r\n     随机产出系列:\r\n#i1702675# #i1702330# #i1702334# #i1702631# #i1702636# #i1702634# #i1702682# #i1702660#\r\n"
				text += "  #L6#" + 皇冠白 + " 进阶#r #v1042142#【属性时装】#k 随机150-800属性 " + 星星 + "#l\r\n\r\n     随机产出系列:\r\n #i1042142#  #i1062054#  #i1002186#  #i1072153#  #i1082102#  #i1022048#  #i1032024#  #i1102039#\r\n" */  
			   //text += "   " + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + 爱心 + "\r\n"
                cm.sendSimpleS(text,2);
            } else if (selection == 1) {
                warp = 1;
                cm.sendYesNo("" + 圆形 + " 进阶#r史诗饰品#k需要：\r\n\r\n" + 感叹号 + " [#v 4170002##r#c 4170002##k/10 ] [ #v 4170005##r#c 4170005##k/10 ] [ #v 4170001##r#c 4170001##k/10 ]\r\n" + 感叹号 + " [#v 4170006##r#c 4170006##k/10 ] [ #v 4170009##r#c 4170009##k/10 ] [ #v 4251202##r#c 4251202##k/1 ] \r\n");

            } else if (selection == 2) {
                warp = 2;
                cm.sendYesNo("" + 圆形 + " 进阶#r史诗武器#k需要：\r\n\r\n" + 感叹号 + " [#v 1402268##r#c 1402268##k/1 ] [ #v 1432227##r#c 1432227##k/1 ] [ #v 1382273##r#c 1382273##k/1 ]\r\n" + 感叹号 + " [#v 1332289##r#c 1332289##k/1 ] [ #v 1452266##r#c 1452266##k/1 ] [ #v 1462252##r#c 1462252##k/1 ] \r\n" + 感叹号 + " [#v 1472275##r#c 1472275##k/1 ] [ #v 1482232##r#c 1482232##k/1 ] [ #v 1492245##r#c 1492245##k/1 ] \r\n" + 感叹号 + " [ #v 4251202##r#c 4251202##k/10 ] [ #v 3605014##r#c 3605014##k/10 ] [ #v 2048403##r#c 2048403##k/10 ] \r\n");

            } else if (selection == 3) {
                warp = 3;
                cm.sendYesNo("" + 圆形 + " 进阶#r史诗武器#k需要：\r\n\r\n" + 感叹号 + " [#v 1003172##r#c 1003172##k/1 ] [ #v 1052314##r#c 1052314##k/1 ] [ #v 1072485##r#c 1072485##k/1 ]\r\n" + 感叹号 + " [#v 1082295##r#c 1082295##k/1 ] [ #v 1102275##r#c 1102275##k/1 ] [ #v 1132143##r#c 1132143##k/1 ] \r\n" + 感叹号 + " [ #v 4251202##r#c 4251202##k/10 ] [ #v 3605014##r#c 3605014##k/10 ] [ #v 2048403##r#c 2048403##k/10 ] \r\n");
				
			/*} else if (selection == 4) {
                warp = 4;
                cm.sendYesNo("" + 圆形 + " 进阶#r风暴套装#k需要：\r\n\r\n" + 感叹号 + " [#v 1132161##r#c 1132161##k/1 ] [ #v 1102467##r#c 1102467##k/1 ] [ #v 1082438##r#c 1082438##k/1 ]\r\n" + 感叹号 + " [#v 1072672##r#c 1072672##k/1 ] [ #v 1052467##r#c 1052467##k/1 ] [ #v 1003561##r#c 1003561##k/1 ] \r\n" + 感叹号 + " [ #v 4251202##r#c 4251202##k/1 ] \r\n");
			
			} else if (selection == 5) {
                warp = 5;
                cm.sendYesNo("" + 圆形 + " 进阶#r点装武器#k需要：\r\n\r\n" + 感叹号 + " [#v 1332289##r#c 1332289##k/1 ] [ #v 1382273##r#c 1382273##k/1 ] [ #v 1402267##r#c 1402267##k/1 ]\r\n" + 感叹号 + " [#v 1422196##r#c 1422196##k/1 ] [ #v 1432226##r#c 1432226##k/1 ] [ #v 1442284##r#c 1442284##k/1 ] \r\n" + 感叹号 + " [#v 1452265##r#c 1452265##k/1 ] [ #v 1462251##r#c 1462251##k/1 ] [ #v 1472275##r#c 1472275##k/1 ] \r\n" + 感叹号 + " [#v 1482232##r#c 1482232##k/1 ] [ #v 1492245##r#c 1492245##k/1 ] [ #v 4251202##r#c 4251202##k/10 ] \r\n");
				
			} else if (selection == 6) {
                warp = 6;
                cm.sendYesNo("" + 圆形 + " 进阶#r属性时装#k需要：\r\n\r\n" + 感叹号 + " [#v 1432118##r#c 1432118##k/1 ] [ #v 1402130##r#c 1402130##k/1 ] [ #v 1422090##r#c 1422090##k/1 ]\r\n" + 感叹号 + " [#v 1382144##r#c 1382144##k/1 ] [ #v 1452148##r#c 1452148##k/1 ] [ #v 1462138##r#c 1462138##k/1 ] \r\n" + 感叹号 + " [#v 1472160##r#c 1472160##k/1 ] [ #v 1332237##r#c 1332237##k/1 ] [ #v 1492121##r#c 1492121##k/1 ] \r\n" + 感叹号 + " [#v 1482121##r#c 1482121##k/1 ] [ #v 1302107##r#c 1302107##k/1 ] [ #v 4251202##r#c 4251202##k/20 ] \r\n");*/
				
            } else if (status = 1) {
				var 随机装 = Math.floor(Math.random() * 9)+1;
                if (warp == 1) {
                    if (cm.getSpace(1) < 1 ) {
                    cm.sendOk("请把装备栏空出1个格子出来");
                    cm.dispose();
                    return;
                    } else if (!cm.haveItem(4170002, 10)) {
                        cm.sendOk("请将#v 4170002##b#z 4170002##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4170005, 10)) {
                        cm.sendOk("请将#v 4170005##b#z 4170005##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4170001, 10)) {
                        cm.sendOk("请将#v 4170001##b#z 4170001##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4170006, 10)) {
                        cm.sendOk("请将#v 4170006##b#z 4170006##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4170009, 10)) {
                        cm.sendOk("请将#v 4170009##b#z 4170009##k放入背包中");
                        cm.dispose();       
                    } else if (!cm.haveItem(4251202, 1)) {
                        cm.sendOk("请将#v 4251202##b#z 4251202##k放入背包中");
                        cm.dispose();
                    } else {
                        cm.gainItem(4170002, -10); //1
                        cm.gainItem(4170005, -10); //2
                        cm.gainItem(4170001, -10); //3
                        cm.gainItem(4170006, -10); //4
                        cm.gainItem(4170009, -10); //5
                        cm.gainItem(4251202, -1); //8
						if (随机装 < 2){
                        cm.gainItem(1112978, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 3 ){
                        cm.gainItem(1022276, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 4 ){
                        cm.gainItem(1012339, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 5 ){
                        cm.gainItem(1032266, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else {
                        cm.gainItem(1122363, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						/*} else if (随机装 == 7 ){
                        cm.gainItem(1112738, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 8 ){
                        cm.gainItem(1122197, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else {
                        cm.gainItem(1132152, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1*/
						}
                        cm.sendOk("合成成功");
                        cm.喇叭(5, " 玩家:<" + cm.getName() + ">使用副本蛋进阶出随机" + 饰品最小值 + "-" + 饰品最大值 + "全属性史诗饰品恭喜他~!");
                        cm.dispose();
                    }

                } else if (warp == 2) {
                    if (cm.getSpace(1) < 1 ) {
                    cm.sendOk("请把装备栏空出1个格子出来");
                    cm.dispose();
                    return;
                    } else if (!cm.haveItem(1402268, 1)) {
                        cm.sendOk("请将#v 1402268##b#z 1402268##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1432227, 1)) {
                        cm.sendOk("请将#v 1432227##b#z 1432227##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1382273, 1)) {
                        cm.sendOk("请将#v 1382273##b#z 1382273##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1332289, 1)) {
                        cm.sendOk("请将#v 1332289##b#z 1332289##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1452266, 1)) {
                        cm.sendOk("请将#v 1452266##b#z 1452266##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1462252, 1)) {
                        cm.sendOk("请将#v 1462252##b#z 1462252##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1472275, 1)) {
                        cm.sendOk("请将#v 1472275##b#z 1472275##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1482232, 1)) {
                        cm.sendOk("请将#v 1482232##b#z 1482232##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1492245, 1)) {
                        cm.sendOk("请将#v 1492245##b#z 1492245##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4251202, 10)) {
                        cm.sendOk("请将#v 4251202##b#z 4251202##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(3605014, 10)) {
                        cm.sendOk("请将#v 3605014##b#z 3605014##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(2048403, 10)) {
                        cm.sendOk("请将#v 2048403##b#z 2048403##k放入背包中");
                        cm.dispose();
                    } else {
                        cm.gainItem(1402268, -1); //1
                        cm.gainItem(1432227, -1); //2
                        cm.gainItem(1382273, -1); //3
                        cm.gainItem(1332289, -1); //4
                        cm.gainItem(1452266, -1); //5
                        cm.gainItem(1462252, -1); //6
                        cm.gainItem(1472275, -1); //8
                        cm.gainItem(1482232, -1); //3
                        cm.gainItem(1492245, -1); //4
                        cm.gainItem(4251202, -10); //5
                        cm.gainItem(3605014, -10); //6
                        cm.gainItem(2048403, -10); //8
						if (随机装 < 2){
                        cm.gainItem(1382267, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 3 ){
                        cm.gainItem(1402332, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 4 ){
                        cm.gainItem(1432306, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 5 ){
                        cm.gainItem(1452307, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 6 ){
                        cm.gainItem(1462245, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 7 ){
                        cm.gainItem(1332281, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 8 ){
                        cm.gainItem(1472267, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 9 ){
                        cm.gainItem(1482204, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else {
                        cm.gainItem(1492237, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						}
                        cm.sendOk("合成成功");
                        cm.喇叭(5, " 玩家:<" + cm.getName() + ">使用神器进阶出随机" + 武器最小值 + "-" + 武器最大值 + "全属性史诗武器一件恭喜他~!");
                        cm.dispose();
                    }
					
				} else if (warp == 3) {
                    if (cm.getSpace(1) < 1 ) {
                    cm.sendOk("请把装备栏空出1个格子出来");
                    cm.dispose();
                    return;
                    } else if (!cm.haveItem(1003172, 1)) {
                        cm.sendOk("请将#v 1003172##b#z 1003172##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1052314, 1)) {
                        cm.sendOk("请将#v 1052314##b#z 1052314##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1072485, 1)) {
                        cm.sendOk("请将#v 1072485##b#z 1072485##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1082295, 1)) {
                        cm.sendOk("请将#v 1082295##b#z 1082295##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1102275, 1)) {
                        cm.sendOk("请将#v 1102275##b#z 1102275##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1132143, 1)) {
                        cm.sendOk("请将#v 1132143##b#z 1132143##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4251202, 10)) {
                        cm.sendOk("请将#v 4251202##b#z 4251202##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(3605014, 10)) {
                        cm.sendOk("请将#v 3605014##b#z 3605014##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(2048403, 10)) {
                        cm.sendOk("请将#v 2048403##b#z 2048403##k放入背包中");
                        cm.dispose();
                    } else {
                        cm.gainItem(1003172, -1); //1
                        cm.gainItem(1052314, -1); //2
                        cm.gainItem(1072485, -1); //3
                        cm.gainItem(1082295, -1); //4
                        cm.gainItem(1102275, -1); //5
                        cm.gainItem(1132143, -1); //6
                        cm.gainItem(4251202, -10); //8
                        cm.gainItem(3605014, -10); //6
                        cm.gainItem(2048403, -10); //8
						if (随机装 < 2){
                        cm.gainItem(1003624, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 3 ){
                        cm.gainItem(1052532, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 4 ){
                        cm.gainItem(1132187, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 5 ){
                        cm.gainItem(1102948, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 6 ){
                        cm.gainItem(1072695, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						/*} else if (随机装 == 7 ){
                        cm.gainItem(1115005, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 8 ){
                        cm.gainItem(1112192, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 9 ){
                        cm.gainItem(1115105, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1*/
						} else {
                        cm.gainItem(1082490, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, Math.floor(Math.random() * (武器最大值 - 武器最小值))+武器最小值, 0, 0, 0, 0, 0, 0); //1
						}
                        cm.sendOk("合成成功");
                        cm.喇叭(5, " 玩家:<" + cm.getName() + ">使用神器套装成功进阶出随机" + 武器最小值 + "-" + 武器最大值 + "史诗套装恭喜他~!");
                        cm.dispose();
                    }
					
				} else if (warp == 4) {
                    if (cm.getSpace(1) < 1 ) {
                    cm.sendOk("请把装备栏空出1个格子出来");
                    cm.dispose();
                    return;
                    } else if (!cm.haveItem(1132161, 1)) { 
                        cm.sendOk("请将#v 1132161##b#z 1132161##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1102467, 1)) {
                        cm.sendOk("请将#v 1102467##b#z 1102467##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1082438, 1)) {
                        cm.sendOk("请将#v 1082438##b#z 1082438##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1072672, 1)) {
                        cm.sendOk("请将#v 1072672##b#z 1072672##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1052467, 1)) {
                        cm.sendOk("请将#v 1052467##b#z 1052467##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1003561, 1)) {
                        cm.sendOk("请将#v 1003561##b#z 1003561##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(4251202, 1)) {
                        cm.sendOk("请将#v 4251202##b#z 4251202##k放入背包中");
                        cm.dispose();
                    } else {
                        cm.gainItem(1132161, -1); //1
                        cm.gainItem(1102467, -1); //2
                        cm.gainItem(1082438, -1); //3
                        cm.gainItem(1072672, -1); //4
                        cm.gainItem(1052467, -1); //5
                        cm.gainItem(1003561, -1); //6
                        cm.gainItem(4251202, -1); //8
						if (随机装 < 2){
                        cm.gainItem(1902034,  Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 3 ){
                        cm.gainItem(1912027, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 4 ){
                        cm.gainItem(1902035, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 5 ){
                        cm.gainItem(1912028, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 6 ){
                        cm.gainItem(1902019, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 7 ){
                        cm.gainItem(1912012, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 8 ){
                       // cm.gainItem(1902059, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						//} else if (随机装 == 9 ){
                       // cm.gainItem(1912052, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						//} else if (随机装 == 10 ){
						cm.gainItem(1902061, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 11 ){
						cm.gainItem(1912054, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 12 ){
						cm.gainItem(1902031, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 13 ){
						cm.gainItem(1912024, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 14 ){
						cm.gainItem(1902021, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 15 ){
						cm.gainItem(1912014, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 16 ){
						cm.gainItem(1902045, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 17 ){
						cm.gainItem(1912038, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 18 ){
						cm.gainItem(1902028, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						} else {
						cm.gainItem(1912021, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, Math.floor(Math.random() * (饰品最大值 - 饰品最小值))+饰品最小值, 0, 0, 0, 0, 0, 0); //1
						}

						
                        cm.sendOk("合成成功");
                        cm.喇叭(5, " 玩家:<" + cm.getName() + ">成功进阶出了随机" + 饰品最小值 + "-" + 饰品最大值 + "星球坐骑一个恭喜他~!");
                        cm.dispose();
                    }
					
					
				} else if (warp == 5) {
                    if (cm.getSpace(1) < 1 ) {
                    cm.sendOk("请把装备栏空出1个格子出来");
                    cm.dispose();
                    return;
                    } else if (!cm.haveItem(1332289, 1)) {
                        cm.sendOk("请将#v 1332289##b#z 1332289##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1382273, 1)) {
                        cm.sendOk("请将#v 1382273##b#z 1382273##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1402267, 1)) {
                        cm.sendOk("请将#v 1402267##b#z 1402267##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1422196, 1)) {
                        cm.sendOk("请将#v 1422196##b#z 1422196##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1432226, 1)) {
                        cm.sendOk("请将#v 1432226##b#z 1432226##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1442284, 1)) {
                        cm.sendOk("请将#v 1442284##b#z 1442284##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1452265, 1)) {
                        cm.sendOk("请将#v 1452265##b#z 1452265##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1462251, 1)) {
                        cm.sendOk("请将#v 1462251##b#z 1462251##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1472275, 1)) {
                        cm.sendOk("请将#v 1472275##b#z 1472275##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1482232, 1)) {
                        cm.sendOk("请将#v 1482232##b#z 1482232##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1492245, 1)) {
                        cm.sendOk("请将#v 1492245##b#z 1492245##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(4251202, 10)) {
                        cm.sendOk("请将#v 4251202##b#z 4251202##k放入背包中");
                        cm.dispose();
                    } else {
                        cm.gainItem(1332289, -1); //1
                        cm.gainItem(1382273, -1); //2
                        cm.gainItem(1402267, -1); //3
                        cm.gainItem(1422196, -1); //4
                        cm.gainItem(1432226, -1); //5
                        cm.gainItem(1442284, -1); //6
						cm.gainItem(1462251, -1); //7
                        cm.gainItem(1452265, -1); //8
						cm.gainItem(1472275, -1); //8
						cm.gainItem(1482232, -1); //8
						cm.gainItem(1492245, -1); //8
						cm.gainItem(4251202, -10); //8 
						if (随机装 < 2){
                        cm.gainItem(1702675, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 3 ){
                        cm.gainItem(1702330, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 4 ){
                        cm.gainItem(1702334, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 5 ){
                        cm.gainItem(1702631, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 6 ){
                        cm.gainItem(1702636, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 7 ){
                        cm.gainItem(1702634, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 8 ){
                        cm.gainItem(1702682, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						} else {
                        cm.gainItem(1702660, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, Math.floor(Math.random() * 710)+90, Math.floor(Math.random() * 710)+90, 0, 0, 0, 0, 0, 0); //1
						}
                        cm.sendOk("合成成功");
                        cm.喇叭(5, " 玩家:<" + cm.getName() + ">成功进阶出了随机90-800点装武器一个恭喜他~!");
                        cm.dispose();
					}				
				} else if (warp == 6) {
                    if (!cm.haveItem(1302107, 1)) {
                        cm.sendOk("请将#v 1302107##b#z 1302107##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1332237, 1)) {
                        cm.sendOk("请将#v 1332237##b#z 1332237##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1402130, 1)) {
                        cm.sendOk("请将#v 1402130##b#z 1402130##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1422090, 1)) {
                        cm.sendOk("请将#v 1422090##b#z 1422090##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1432118, 1)) {
                        cm.sendOk("请将#v 1432118##b#z 1432118##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1382144, 1)) {
                        cm.sendOk("请将#v 1382144##b#z 1382144##k放入背包中");
                        cm.dispose();
                    } else if (!cm.haveItem(1492121, 1)) {
                        cm.sendOk("请将#v 1492121##b#z 1492121##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1452148, 1)) {
                        cm.sendOk("请将#v 1452148##b#z 1452148##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1482121, 1)) {
                        cm.sendOk("请将#v 1482121##b#z 1482121##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1472160, 1)) {
                        cm.sendOk("请将#v 1472160##b#z 1472160##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(1462138, 1)) {
                        cm.sendOk("请将#v 1462138##b#z 1462138##k放入背包中");
                        cm.dispose();
					} else if (!cm.haveItem(4251202, 20)) {
                        cm.sendOk("请将#v 4251202##b#z 4251202##k放入背包中");
                        cm.dispose();
                    } else {
                        cm.gainItem(1302107, -1); //1
                        cm.gainItem(1332237, -1); //2
                        cm.gainItem(1402130, -1); //3
                        cm.gainItem(1422090, -1); //4
                        cm.gainItem(1432118, -1); //5
                        cm.gainItem(1382144, -1); //6
						cm.gainItem(1492121, -1); //7
                        cm.gainItem(1452148, -1); //8
						cm.gainItem(1482121, -1); //8
						cm.gainItem(1472160, -1); //8
						cm.gainItem(1462138, -1); //8
						cm.gainItem(4251202, -20); //8 
						if (随机装 < 2){
                        cm.gainItem(1022048, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 3 ){
                        cm.gainItem(1102039, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 4 ){
                        cm.gainItem(1042142, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 5 ){
                        cm.gainItem(1062054, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 6 ){
                        cm.gainItem(1002186, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 7 ){
                        cm.gainItem(1072153, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else if (随机装 == 8 ){
                        cm.gainItem(1032024, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						} else {
                        cm.gainItem(1082102, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, Math.floor(Math.random() * 650)+150, Math.floor(Math.random() * 650)+150, 0, 0, 0, 0, 0, 0); //1
						}
                        cm.sendOk("合成成功");
                        cm.喇叭(5, " 玩家:<" + cm.getName() + ">成功进阶出了随机150-800属性时装一个恭喜他~!");
                        cm.dispose();
					}
                }
            }
        }
    }