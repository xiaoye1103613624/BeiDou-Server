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
            qm.sendNext("这里除了这个巨大的岩壁之外，好像没有其他路了。绝路啊… 我们还是先沿着岩壁上去看看吧。");
        } else if (status == 1) {
            qm.sendYesNo("#ho#，也是同样的想法吗？那，我们上去看看吧。");
        } else if (status == 2) {
			qm.forceStartQuest(34108);
			qm.forceCompleteQuest(34108);
			//qm.getQuestStatus(56222) == 2;
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
