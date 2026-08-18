var status = -1;

function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.dispose();
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
            qm.sendNext("木材全部搜集到了吗？");
        } else if (status == 1) {
            qm.sendOk("谢谢你，勇士。看样子你对乘船有点天赋，我这下放心了。\r\n\r\n#b(和弗坦对话，开始执行正式的任务。）#k");
            qm.forceCompleteQuest(32163);
            qm.forceCompleteQuest(32164);
            qm.forceCompleteQuest(32165);
            qm.forceCompleteQuest(32166);
            qm.gainExp(52270);
            qm.gainItem(4030022, -3);
            qm.dispose();
        }
    }
}
