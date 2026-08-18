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
            qm.sendNext("从纸条上获得有用的信息了吗？");
        } else if (status == 1) {
            qm.sendNextPrev("秘密物品……那个到底是什么？最好我们亲自找找看。");
        } else if (status == 2) {
            qm.sendAcceptDecline("按照纸条上所写的话，应该是藏在宿舍的某个地方。男生的宿舍应该在2楼的两侧。你去调查一下那里，看看会发现什么。");
        } else if (status == 3) {
            qm.sendOk("我会在这里等着你的，#h0#。\r\n#b（去查看一下位于2楼两侧的宿舍吧。）");
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
