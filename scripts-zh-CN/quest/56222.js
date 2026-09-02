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
            qm.sendNext("喂！我是金博士！听得见吗？！听见请回答！你之前破坏的那个钻机是假的！真的钻机还在工作！喂！听得见我说话吗？！");
        } else if (status == 1) {
            qm.sendYesNo("十万火急！原来真的钻机在别的地方！还好我能利用外星人的能量，把你送到真的钻机所在的地方！你快去准备把！");
        } else if (status == 2) {
			qm.warp(703020000);
			qm.startQuest(56222);
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


