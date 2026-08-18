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
				
                qm.sendOk("可以随时来找我说话");
                qm.dispose();
            }
            status--;
        }
        if (status == 0) {
            qm.sendNext("女神的宠物竟然变成了如此邪恶凶暴的怪物.....？");
        } else if (status == 1) {
			qm.sendNext("呵呵呵……这只是开始。以后的事情更值得期待。");
        } else if (status == 2) {
            qm.sendYesNo("#b(可疑的人的声音顺着风消失在了远方……)#k");
        } else if (status == 3) {
			qm.forceStartQuest();
			qm.gainExp(6419211);
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