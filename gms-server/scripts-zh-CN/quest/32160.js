var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            qm.sendOk("你太忙了吗？");
            qm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            qm.sendNext("被冰川覆盖的里恩的环境正在发生变化。看来一定是发生了某种不寻常之事。\r\n\r\n#b（※ #r列娜海峡#b是特殊主题副本。提供#r59级#b以下和勇士等级对应的怪物和任务。）");
        } else if (status == 1) {
            qm.sendNextPrev("…………");
        } else if (status == 2) {
            qm.sendYesNo("接下来的话不是明摆着呢嘛，我想要将此次事件的解决交给你，你能不能现在来这里？\r\n\r\n#b#e(接受时自动前往里恩。)#n#k");
        } else if (status == 3) {
            qm.sendOk("我会在里恩等你。");
            qm.warp(140000000, 0);
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
