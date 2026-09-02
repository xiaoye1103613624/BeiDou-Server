
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
            qm.sendNext("好，我要给你下一个任务了！准备好了么？");
        } else if (status == 1) {
            qm.sendYesNo("我会用能量存储器里装着的外星人能量，强化你的能力。现在你要做的就是，去破坏位于地下的外星人钻机！如果放任外星人继续开采矿石，他们就会越来越强大。到外星人的基地去破坏他们的钻机吧！");
        } else if (status == 2) {
			qm.forceCompleteQuest();
            //qm.completeQuest(56231);
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
