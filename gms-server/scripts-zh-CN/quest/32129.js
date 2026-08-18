var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.sendOk("在你决定是否要进行这个任务之后再跟我说。如果你决定不做，你不会错过任何机会。");
        qm.dispose();
    } else {
        if (mode == 0 && type > 0 || selection == 1) {
            qm.sendOk("在你决定是否要进行这个任务之后再跟我说。如果你决定不做，你不会错过任何机会。");
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            qm.sendAcceptDecline("#h0#，我真的很佩服你的实力啊。你终于成功救出了我们全部的孩子！现在只要返回艾利涅，并向校长先生做报告就行了。现在我们一起回去吧。\r\n#b（接受后，移动到艾利涅。）");
        } else if (status == 1) {
            qm.sendOk("好吧，孩子们已经全部回来了吧？");
            qm.warp(101072001, 0);
            qm.gainExp(10190);
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            qm.sendOk("你现在已经成为#b妖精学院艾利涅#k的名誉教授，这里随时欢迎你。祝你的人生幸福美满……呵呵呵。");
            qm.gainExp(6814);
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}
