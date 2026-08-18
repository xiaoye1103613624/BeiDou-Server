

var status = -1;
var sel;
var mod;
function start() {
    //cm.sendOk("暂时没有补偿。");
    //cm.dispose();
    //return;
    cm.sendSimple("我是转生管理员 \r\n\r\n" +
            "需要转生道具：\r\n" +
            "#i4005004#x1\r\n" +
            "需要1亿金币\r\n" +
            "#b#L0#我要转生#l#k\r\n\r\n" +
            "");
}

function action(mode, type, selection) {
    if (mode == 0) {
        cm.dispose();
        return;
    } else {
        status++;
    }
    if (status == 0) {
        sel = selection;
        if (sel == 0) {
            if (cm.getPlayer().getLevel() < 200) {
                cm.sendOk("您的等级不足200级。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4005004)) {
                cm.sendOk("所需道具不足。");
                cm.dispose();
                return;
            }


            if (cm.getMeso() < 100000000) {
                cm.sendOk("所需金币不足。");
                cm.dispose();
                return;
            }
            if (!cm.canHoldByType(2, 1)) {
                cm.sendOk("请确认背包是否已经满了。");
                cm.dispose();
                return;
            }

            cm.gainItem(4005004, -1);//减需要道具数量

            cm.gainMeso(-100000000);//需要冒险币数量

            cm.changeJob(0);
            cm.StatsZs();
            cm.getPlayer().setExp(0);

            cm.sendOk("你已成功转生。");
            cm.dispose();
            return;
        }
        cm.dispose();
    }
}
