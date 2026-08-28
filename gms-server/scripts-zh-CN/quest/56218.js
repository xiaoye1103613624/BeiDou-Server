
var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                qm.dispose();
                return;
            } else if (status == 1) {
                qm.sendOk("真的没人来救我了吗？呜呜……");
                qm.dispose();
            }
            status--;
        }
        if (status == 0) {
            qm.sendNext("现在就要去关闭外星人开通的传送口！快去关闭传送口！");
        } else if (status == 1) {
            qm.sendYesNo("把你手上的装置放在你最开始到这的传送口就可以了！");
        } else if (status == 2) {
			qm.forceCompleteQuest();
            qm.completeQuest(56218);
            //qm.forceCompleteQuest(56211);
            //qm.forceCompleteQuest(56213);
            //qm.warp(703100010, 0); //703100010
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
