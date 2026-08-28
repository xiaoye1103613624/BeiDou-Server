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
            qm.sendNext("这消亡旅途比我们之前经过的任何地方都要危险。");
        } else if (status == 1) {
			qm.sendNext("无论如何，我们都不能放松警惕。如果碰到这里的火焰，那肉体将会永远消失。为了平安无事地通过这条路…");
		} else if (status == 2) {
			qm.sendNext("在那之前，请你们先答应我。绝不再做鲁莽之事，一定要按照我的指示来走，你们能答应我吗？");
		} else if (status == 3) {
			qm.forceCompleteQuest(34109);
            qm.sendYesNo("好的，请相信我。接下来我会带着#ho#你和卡奥，安全通过这里。准备好之后，请重新和我对话。");
        } else if (status == 5) {
			//qm.forceStartQuest(34108);
			//qm.forceCompleteQuest(34109);
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