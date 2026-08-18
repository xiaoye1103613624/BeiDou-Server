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
            qm.sendNext("来啦。你手上拿着的那个是……？");
        } else if (status == 1) {
            qm.sendNextPrev("#i4030030##b#t4030030##k\r\n\r\n这是冰川之核碎片……冰川之核是一种具有极强冷却效果的神秘物质。他们居然大量搜集这种物质，难道是想用来启动某个巨大的引擎装置？");
        } else if (status == 2) {
            qm.sendOk("这事很可疑。敌人的目的到底是什么？");
            qm.gainExp(107676);
            qm.forceStartQuest();
            qm.gainItem(4030030, -30);
            qm.forceCompleteQuest(32189);
            qm.warp(141050400, 0);
            qm.dispose();
        }
    }
}
