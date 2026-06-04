function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
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
            cm.sendYesNo("欢迎来到" + cm.开服名称() + "成长基金购买,售价为:#r 200 #k余额,\r\n购买后每个仙级可领取相应的#b点券、抵用、元宝与特殊道具#k奖励哦~\r\n#r每个账号只可以购买一次哦~#k\r\n是否购买？");
        } else if (status == 1) {
            if (cm.getPlayer().getmoney() < 200) {
                cm.sendOk("余额不足无法兑换。");
                cm.dispose();
            } else if (cm.getPlayer().getOneTimeLog("仙级奖励1") >= 1) {
                cm.sendOk("每个账号只可购买一次成长基金。");
                cm.dispose();
            } else {
                cm.getPlayer().setmoney(cm.getPlayer().getmoney() - 200);
                cm.getPlayer().setOneTimeLog("仙级奖励1");
                cm.喇叭(2, "[" + cm.getPlayer().getName() + "],成功购买成长基金！");
                cm.sendOk("恭喜兑换成功。");
                cm.dispose();
            }
        }
    }
}