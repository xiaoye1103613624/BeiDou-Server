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
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "#b这里可以合成极品装备,每个戒指只能合成一次,额且不可重复佩戴#k\r\n"
            text += "#L0#" + 蓝色角点 + "兑换#z1114218##i1114218#（合成属性为2288额外伤害120%）,需要#z1114215##i1114215#x8#l\r\n\r\n\r\n";
            text += "#L1#" + 蓝色角点 + "兑换#i1116051##z1116051#（合成属性为2288额外伤害120%）,需要#i1116038##z1116038#x8#l\r\n\r\n\r\n";
            text += "#L2#" + 蓝色角点 + "兑换#i1112535##z1112535#（合成属性为2288额外伤害120%）,需要#i1112541##z1112541#x8#l\r\n\r\n\r\n";
			text += "#L3#" + 蓝色角点 + "兑换#i1092034##z1092034#（合成属性为2288额外伤害120%）,需要#i1114215##z1114215#x4,#i1116038##z1116038#x4#l\r\n\r\n\r\n";
			text += "#L4#" + 蓝色角点 + "兑换#i1112540##z1112540#（合成属性为888额外伤害50%）,需要#i1112541##z1112541#x2#l\r\n\r\n\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                // 检查是否已兑换过（带参数的日志判断）
                if (cm.getPlayer().getOneTimeLog('终极戒指合成1')) {
                    cm.sendOk("你已经兑换过该物品，无法重复兑换！");
                    cm.dispose();
                    return;
                }
                // 检查道具数量
                if (!cm.haveItem(1114215, 8)) {
                    cm.sendOk("所需道具不足。");
                    cm.dispose();
                    return;
                }
                // 执行兑换并记录日志
                cm.gainItem(1114215, -8);
				cm.gainItem(1114218, 1);
                cm.getPlayer().setOneTimeLog('终极戒指合成1'); // 记录对应日志
                cm.sendOk("兑换成功。");
                cm.dispose();
            } else if (selection == 1) {
                // 检查是否已兑换过
                if (cm.getPlayer().getOneTimeLog('终极戒指合成2')) {
                    cm.sendOk("你已经兑换过该物品，无法重复兑换！");
                    cm.dispose();
                    return;
                }
                // 检查道具数量
                if (!cm.haveItem(1116038, 8)) {
                    cm.sendOk("所需道具不足。");
                    cm.dispose();
                    return;
                }
                // 执行兑换并记录日志
                cm.gainItem(1116038, -8);
                cm.gainItem(1116051, 1);
                cm.getPlayer().setOneTimeLog('终极戒指合成2');
                cm.sendOk("兑换成功。");
                cm.dispose();
            } else if (selection == 2) {
                // 检查是否已兑换过
                if (cm.getPlayer().getOneTimeLog('终极戒指合成3')) {
                    cm.sendOk("你已经兑换过该物品，无法重复兑换！");
                    cm.dispose();
                    return;
                }
                // 检查道具数量
                if (!cm.haveItem(1112541, 8)) {
                    cm.sendOk("所需道具不足。");
                    cm.dispose();
                    return;
                }
                // 执行兑换并记录日志
                cm.gainItem(1112541, -8);
                cm.gainItem(1112764, -1);
                cm.gainItem(1112535,0,0,2288,2288,2288,2288,0,0,2288,2288,0,0,0,0,0,0);
                cm.getPlayer().setOneTimeLog('终极戒指合成3');
                cm.sendOk("兑换成功。");
                cm.dispose();
            } else if (selection == 3) {
                // 检查是否已兑换过
                if (cm.getPlayer().getOneTimeLog('终极戒指合成4')) {
                    cm.sendOk("你已经兑换过该物品，无法重复兑换！");
                    cm.dispose();
                    return;
                }
                // 检查道具数量
                if (!cm.haveItem(1114215, 4) || !cm.haveItem(1116038, 4)) {
                    cm.sendOk("所需道具不足。");
                    cm.dispose();
                    return;
                }
                // 执行兑换并记录日志
                cm.gainItem(1114215, -4);
                cm.gainItem(1116038, -4);
                cm.gainItem(1092034,0,0,2288,2288,2288,2288,0,0,2288,2288,0,0,0,0,0,0);
                cm.getPlayer().setOneTimeLog('终极戒指合成4');
                cm.sendOk("兑换成功。");
                cm.dispose();
			} else if (selection == 4) {
                // 检查是否已兑换过
                if (cm.getPlayer().getOneTimeLog('终极戒指合成5')) {
                    cm.sendOk("你已经兑换过该物品，无法重复兑换！");
                    cm.dispose();
                    return;
                }
                // 检查道具数量
                if (!cm.haveItem(1112541, 2)) {
                    cm.sendOk("所需道具不足。");
                    cm.dispose();
                    return;
                }
                // 执行兑换并记录日志
                cm.gainItem(1112541, -2);
                cm.gainItem(1112540,0,0,888,888,888,888,0,0,888,888,0,0,0,0,0,0);
                cm.getPlayer().setOneTimeLog('终极戒指合成5');
                cm.sendOk("兑换成功。");
                cm.dispose();
            }
        }
    }
}