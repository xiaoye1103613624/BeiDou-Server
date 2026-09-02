/* Author: aaroncsn(MapleSea Like)(Incomplete)
 NPC Name: 		Pam
 Map(s): 		Leafre: Pam's House(240000006)
 Description: 		Unknown
 */


function start() {
    var Editing = false //false 开始
    if (Editing) {
        cm.sendOk("维修中");
        cm.dispose();
        return;
    }
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
   } else {
        status--;
}
    if (status == 0) {
        cm.sendSimple("你想获得浓缩浓缩离乳食吗？\r\n" +
                "#L0##d第一阶段浓缩离乳食\r\n" +
                "#L1##d第二阶段浓缩离乳食\r\n" +
                "#L2##d第三阶段浓缩离乳食\r\n");

    } else if (status == 1) {
        if (selection == 0) {
            if (cm.haveItem(4000236, 30) && cm.haveItem(4000237, 30) && cm.haveItem(4000238, 30) && cm.getMeso() > 2000000) {
                cm.gainItem(4000236, -30);
                cm.gainItem(4000237, -30);
                cm.gainItem(4000238, -30);
                cm.gainMeso(-2000000);
                cm.gainItem(4032196, 1);
                cm.sendOk("兑换成功。");
                cm.dispose();
                return;
            } else {
                cm.sendOk("#d请确认背包中是否有：\r\n#z4000236#x30\r\n#z4000237#x30\r\n#z4000236#x30\r\n200万金币。");
                cm.dispose();
                return;
            }
        } else if (selection == 1) {
            if (cm.haveItem(4000239, 30) && cm.haveItem(4000241, 30) && cm.haveItem(4000242, 30) && cm.getMeso() > 3000000) {
                cm.gainItem(4000239, -30);
                cm.gainItem(4000241, -30);
                cm.gainItem(4000242, -30);
                cm.gainMeso(-3000000);
                cm.gainItem(4032197, 1);
                cm.sendOk("兑换成功。");
                cm.dispose();
                return;
            } else {
                cm.sendOk("#d请确认背包中是否有：\r\n#z4000239#x30\r\n#z4000241#x30\r\n#z4000242#x30\r\n300万金币。");
                cm.dispose();
                return;
            }
        } else if (selection == 2) {
            if (cm.haveItem(4000262, 30) && cm.haveItem(4000263, 30) && cm.haveItem(4000265, 30) && cm.getMeso() > 4000000) {
                cm.gainItem(4000262, -30);
                cm.gainItem(4000263, -30);
                cm.gainItem(4000265, -30);
                cm.gainMeso(-4000000);
                cm.gainItem(4032198, 1);
                cm.sendOk("兑换成功。");
                cm.dispose();
                return;
            } else {
                cm.sendOk("#d请确认背包中是否有：\r\n#z4000262#x30\r\n#z4000263#x30\r\n#z4000265#x30\r\n400万金币。");
                cm.dispose();
                return;
            }
        }
    }
}
