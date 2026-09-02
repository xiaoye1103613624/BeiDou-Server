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
            qm.sendAcceptDecline("总觉得有个地方非常可疑。要去调查下吗？");
        } else if (status == 1) {
            qm.sendNext("#i4033829# \r\n\r\n找到了一堆衣服。看上去不像是平时穿的衣服……要展开看看吗？");
        } else if (status == 2) {
            qm.sendOk("#i1052196##i1050168##i1052495#\r\n\r\n果真如此……这些衣服是看上去就是为了演出而特别制作的。把衣服拿给库迪，看看他怎么说。");
            qm.gainItem(4033829, 1);
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
            qm.sendNext("女孩子们偷偷制作的东西原来是衣服啊！孩子们一直在为有关封印黑魔法师的五位英雄们的演出做准备。并且，做衣服也是非常隐秘地在进行。这意味着什么呢？");
        } else if (status == 1) {
            if (qm.haveItem(4033829)) {
                qm.gainItem(4033829, -1);
            }
            qm.gainExp(12360);
            qm.forceCompleteQuest(32114);
            qm.forceCompleteQuest();
            qm.sendOk("我们能掌握的就只有这些。返回1楼，和大家谈谈吧。\r\n\r\n（#b前往艾利涅1楼。）");
            qm.dispose();
        }
    }
}
