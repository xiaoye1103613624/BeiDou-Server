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
            qm.sendAcceptDecline("你知道魔法密林的妖精艾温和妖精罗雯吗？其实，他们是艾利涅的毕业生。要不要向他们寻求帮助？说不定他们更了解孩子们的心理。\r\n\r\n#e（接受的话，自动移动到魔法密林。）");
        } else if (status == 1) {
            qm.sendOk("拜托了。请返回魔法密林，去找妖精艾温。");
            qm.warp(101000000, 0);
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
            qm.sendNext("你有什么事？我忙得很……");
        } else if (status == 1) {
            qm.sendNextPrev("（把发生的事情告诉了妖精艾温。）");
        } else if (status == 2) {
            qm.sendOk("艾利涅的孩子失踪了？居然会有这样的事……");
            qm.gainExp(1900);
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}
