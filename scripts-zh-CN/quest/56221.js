
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
            qm.sendNext("再去找找别的居民吧？");
        } else if (status == 1) {
            qm.sendYesNo("居民们送了礼物，以表示他们的谢意！");
        } else if (status == 2) {
			qm.forceCompleteQuest();
			qm.completeQuest(56221);
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


