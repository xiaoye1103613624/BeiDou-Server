var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";

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
            var text = "#b物品分解列表#k\r\n";  // 标题改为“物品分解列表”更贴合主题
            // 按要求修改为“分解可获得”描述
            text += "#L0#" + 蓝色角点 + "#i1114203#分解可获得#i1114215##z1114215#（属性288额外30%）x4#l\r\n\r\n"; // 1114203分解→1114215
            text += "#L1#" + 蓝色角点 + "#i1114216#分解可获得#i1114215##z1114215#（属性288额外30%）x2#l\r\n\r\n"; // 1114216分解→1114215
            text += "#L2#" + 蓝色角点 + "#i1116042#分解可获得#i1116038##z1116038#（属性288额外30%）x4#l\r\n\r\n"; // 1116042分解→4个1116038
            text += "#L3#" + 蓝色角点 + "#i1116050#分解可获得#i1116038##z1116038#（属性288额外30%）x2#l\r\n\r\n"; // 1116050分解→2个1116038
            text += "#L4#" + 蓝色角点 + "#i1114215#分解可获得#i1112541##z1112541#（属性288额外30%）x1#l\r\n\r\n"; // 1114215分解→1112541
            text += "#L5#" + 蓝色角点 + "#i1116038#分解可获得#i1112541##z1112541#（属性288额外30%）x1#l\r\n\r\n"; // 1116038分解→1112541
            cm.sendSimple(text);

        } else if (status == 1) {
            switch (selection) {
                case 0: // 1.蓝龙(1114203)→国庆(1114215)
                    if (!cm.haveItem(1114203, 1)) {
                        cm.sendOk("所需#i1114203##z1114203#不足（需1个）。");
                        cm.dispose();
                        return;
                    }
                    cm.gainItem(1114203, -1); // 消耗蓝龙
                    cm.gainItem(1114215, 1); // 获得国庆
					cm.gainItem(1114215, 1);
					cm.gainItem(1114215, 1);
					cm.gainItem(1114215, 1);
                    cm.sendOk("兑换成功，获得#i1114215##z1114215#x1！");
                    cm.dispose();
                    break;

                case 1: // 2.中秋(1114216)→国庆(1114215)
                    if (!cm.haveItem(1114216, 1)) {
                        cm.sendOk("所需#i1114216##z1114216#不足（需1个）。");
                        cm.dispose();
                        return;
                    }
                    cm.gainItem(1114216, -1); // 消耗中秋
                    cm.gainItem(1114215, 1); // 获得国庆
					cm.gainItem(1114215, 1);
                    cm.sendOk("兑换成功，获得#i1114215##z1114215#x1！");
                    cm.dispose();
                    break;

                case 2: // 3.怒海狂涛(1116042)→4个风流倜傥(1116038)
                    if (!cm.haveItem(1116042, 1)) {
                        cm.sendOk("所需#i1116042##z1116042#不足（需1个）。");
                        cm.dispose();
                        return;
                    }
                    cm.gainItem(1116042, -1); // 消耗怒海狂涛
                    cm.gainItem(1116038, 1); // 获得4个风流倜傥
					cm.gainItem(1116038, 1);
					cm.gainItem(1116038, 1);
					cm.gainItem(1116038, 1);
                    cm.sendOk("兑换成功，获得#i1116038##z1116038#x4！");
                    cm.dispose();
                    break;

                case 3: // 4.摘星揽月(1116050)→2个风流倜傥(1116038)
                    if (!cm.haveItem(1116050, 1)) {
                        cm.sendOk("所需#i1116050##z1116050#不足（需1个）。");
                        cm.dispose();
                        return;
                    }
                    cm.gainItem(1116050, -1); // 消耗摘星揽月
                    cm.gainItem(1116038, 1); // 获得2个风流倜傥
					cm.gainItem(1116038, 1);
                    cm.sendOk("兑换成功，获得#i1116038##z1116038#x2！");
                    cm.dispose();
                    break;

                case 4: // 5.国庆(1114215)→青铜(1112541，属性288)
                    if (!cm.haveItem(1114215, 1)) {
                        cm.sendOk("所需#i1114215##z1114215#不足（需1个）。");
                        cm.dispose();
                        return;
                    }
                    cm.gainItem(1114215, -1); // 消耗国庆
					cm.gainItem(1112541,0,0,288,288,288,288,0,0,288,288,0,0,0,0,0,0);
                    cm.sendOk("兑换成功，获得#i1112541##z1112541#（属性288）x1！");
                    cm.dispose();
                    break;

                case 5: // 6.风流倜傥(1116038)→青铜(1112541，属性288)
                    if (!cm.haveItem(1116038, 1)) {
                        cm.sendOk("所需#i1116038##z1116038#不足（需1个）。");
                        cm.dispose();
                        return;
                    }
                    cm.gainItem(1116038, -1); // 消耗风流倜傥
                    cm.gainItem(1112541,0,0,288,288,288,288,0,0,288,288,0,0,0,0,0,0);
                    cm.sendOk("兑换成功，获得#i1112541##z1112541#（属性288）x1！");
                    cm.dispose();
                    break;
            }
        }
    }
}