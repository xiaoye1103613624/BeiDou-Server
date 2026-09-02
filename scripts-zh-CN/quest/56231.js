
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
            qm.sendNext("好，我们行动吧。手机500个外星人的能量，就能装满这哥能量储存器了。能量盒装满以后，就会来找我。");
        } else if (status == 1) {
            qm.sendYesNo("回村子找我吧！");
        } else if (status == 2) {
			qm.forceCompleteQuest();
            qm.completeQuest(56231);
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
