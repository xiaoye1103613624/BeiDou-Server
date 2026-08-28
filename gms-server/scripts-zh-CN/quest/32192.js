var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 0 && type > 0) {
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
            qm.sendYesNo("看来咱们得到魔女芭芭拉的家去一趟。嗯……希望勇士你也可以一起去。\r\n\r\n#b#e(接受时，将自动移动。)#n#k");
        } else if (status == 1) {
            qm.warp(141040001, 0);
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
